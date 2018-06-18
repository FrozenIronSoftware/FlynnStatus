package com.frozenironsoftware.flynnstatus.util;

import com.frozenironsoftware.flynnstatus.data.model.Monitor;
import com.frozenironsoftware.flynnstatus.data.model.cloudflare.DnsRecord;
import com.frozenironsoftware.flynnstatus.data.model.cloudflare.DnsRecordActionResult;
import com.frozenironsoftware.flynnstatus.data.model.cloudflare.DnsRecordList;
import com.frozenironsoftware.flynnstatus.util.alert.Alerter;
import com.goebl.david.Response;
import com.goebl.david.Webb;
import com.goebl.david.WebbException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class CloudflareApi {
    private static final String API = "https://api.cloudflare.com/client/v4";
    private final boolean dryRun;
    private final Gson gson;

    public CloudflareApi(boolean dryRun) {
        this.dryRun = dryRun;
        this.gson = new Gson();
    }

    /**
     * Remove an A record for a Flynn cluster node if it is present on CloudFlare
     * @param monitor Flynn cluster node
     */
    public void removeIfNotPresent(Monitor monitor) {
        try {
            DnsRecord dnsRecord = getRecord(monitor);
            if (dnsRecord != null) {
                Logger.verbose("Removing DNS record for: Name: %s, IP: %s",
                        monitor.getDnsRecordName(), monitor.getDnsRecordIp());
                if (!dryRun)
                    removeRecord(monitor, dnsRecord);
                Logger.verbose("Removed DNS record for: Name: %s, IP: %s",
                        monitor.getDnsRecordName(), monitor.getDnsRecordIp());
                Alerter.sendDnsRecordRemovedAlert(monitor.getDnsRecordName(), monitor.getDnsRecordIp());
            }
            else {
                Logger.verbose("Record does not exist. Not removing DNS record for: Name: %s, IP: %s",
                        monitor.getDnsRecordName(), monitor.getDnsRecordIp());
            }
        }
        catch (RecordFetchFailedException e) {
            Alerter.sendCloudflareApiFail(monitor.getCloudflareApiEmail(), monitor.getDnsRecordName());
            Logger.warn("Failed fetching DNS record info. Not removing.");
        }
    }

    /**
     * Remove an A record from CloudFlare
     * @param monitor flynn custer node
     * @param record cloudflare dns record
     */
    private void removeRecord(Monitor monitor, DnsRecord record) throws RecordFetchFailedException {
        Webb webb = getWebb(monitor);
        try {
            Response<String> response = webb.delete(String.format("/%s", record.getId())).ensureSuccess().asString();
            if (response.getBody() == null)
                throw new RecordFetchFailedException();
            DnsRecordActionResult result = gson.fromJson(response.getBody(), DnsRecordActionResult.class);
            if (!result.isSuccess())
                throw new RecordFetchFailedException();
        }
        catch (WebbException | JsonSyntaxException e) {
            Logger.exception(e);
            throw new RecordFetchFailedException();
        }
    }

    /**
     * Polls the CloudFlare API to check if a dns record is present
     * @param monitor Flynn cluster node
     * @return true if the record exists on CloudFlare
     */
    private boolean isRecordPresent(Monitor monitor) throws RecordFetchFailedException {
        return getRecord(monitor) != null;
    }

    /**
     * Polls the CloudFlare API to get a dns record
     * @param monitor Flynn cluster node
     * @return dns record
     * @throws RecordFetchFailedException the response is invalid or http status code is not 200
     */
    @Nullable
    private DnsRecord getRecord(Monitor monitor) throws RecordFetchFailedException {
        Webb webb = getWebb(monitor);
        try {
            Response<String> response = webb.get("")
                    .param("type", "A")
                    .param("name", monitor.getDnsRecordName())
                    .param("content", monitor.getDnsRecordIp())
                    .param("match", "all")
                    .ensureSuccess()
                    .asString();
            if (response.getBody() == null)
                throw new RecordFetchFailedException();
            DnsRecordList dnsRecordList = gson.fromJson(response.getBody(), DnsRecordList.class);
            List<DnsRecord> records = dnsRecordList.getResult();
            if (!dnsRecordList.isSuccess() || records == null)
                throw new RecordFetchFailedException();
            for (DnsRecord dnsRecord : records) {
                if (Objects.equals(dnsRecord.getName(), monitor.getDnsRecordName()) &&
                        Objects.equals(dnsRecord.getContent(), monitor.getDnsRecordIp()))
                    return dnsRecord;
            }
        }
        catch (WebbException | JsonSyntaxException e) {
            Logger.exception(e);
            throw new RecordFetchFailedException();
        }
        return null;
    }

    /**
     * Get webb instance ready for use with cloudflare DNS record listing and editing
     * @return webb instance
     * @param monitor Flynn cluster node
     */
    private Webb getWebb(Monitor monitor) {
        Webb webb = WebbUtil.getWebb();
        webb.setBaseUri(String.format("%s/zones/%s/dns_records", API, monitor.getZoneId()));
        webb.setDefaultHeader("X-Auth-Key", monitor.getCloudflareApiKey());
        webb.setDefaultHeader("X-Auth-Email", monitor.getCloudflareApiEmail());
        webb.setDefaultHeader("Content-Type", "application/json");
        webb.setDefaultHeader("Accept", "application/json");
        return webb;
    }

    /**
     * Add an A record for a Flynn cluster node if it is not present of CloudFlare
     * @param monitor Flynn cluster node
     */
    public void addIfNotPresent(Monitor monitor) {
        try {
            if (!isRecordPresent(monitor)) {
                Logger.verbose("Adding DNS record for: Name: %s, IP: %s",
                        monitor.getDnsRecordName(), monitor.getDnsRecordIp());
                if (!dryRun)
                    addRecord(monitor);
                Logger.verbose("Added DNS record for: Name: %s, IP: %s",
                        monitor.getDnsRecordName(), monitor.getDnsRecordIp());
                Alerter.sendDnsRecordAdddedAlert(monitor.getDnsRecordName(), monitor.getDnsRecordIp());
            }
            else {
                Logger.verbose("Record exists: Not adding DNS record for: Name: %s, IP: %s",
                        monitor.getDnsRecordName(), monitor.getDnsRecordIp());
            }
        } catch (RecordFetchFailedException e) {
            Alerter.sendCloudflareApiFail(monitor.getCloudflareApiEmail(), monitor.getDnsRecordName());
            Logger.warn("Failed fetching DNS record info. Not adding.");
        }
    }

    /**
     * Add a DNS record to CloudFlare
     * @param monitor flynn cluster node
     */
    private void addRecord(Monitor monitor) throws RecordFetchFailedException {
        Webb webb = getWebb(monitor);
        try {
            JSONObject payload = new JSONObject();
            payload.put("type", "A");
            payload.put("name", monitor.getDnsRecordName());
            payload.put("content", monitor.getDnsRecordIp());
            payload.put("ttl", 1);
            payload.put("proxied", false);
            Response<String> response = webb.post("").body(payload).ensureSuccess().asString();
            if (response.getBody() == null)
                throw new RecordFetchFailedException();
            DnsRecordActionResult result = gson.fromJson(response.getBody(), DnsRecordActionResult.class);
            if (!result.isSuccess())
                throw new RecordFetchFailedException();
        }
        catch (WebbException | JsonSyntaxException e) {
            Logger.exception(e);
            throw new RecordFetchFailedException();
        }
    }

    private class RecordFetchFailedException extends Exception {
    }
}
