package com.frozenironsoftware.flynnstatus.data.model;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

public class Monitor {
    @SerializedName("zone_id")
    @Nullable private String zoneId;
    @SerializedName("dns_record_name")
    @Nullable private String dnsRecordName;
    @SerializedName("dns_record_ip")
    @Nullable private String dnsRecordIp;
    @SerializedName("flynn_status_key")
    @Nullable private String flynnStatusKey;
    @SerializedName("cloudflare_api_key")
    @Nullable private String cloudflareApiKey;
    @SerializedName("cloudflare_api_email")
    @Nullable private String cloudflareApiEmail;

    /**
     * Checks that all expected json field have been populated
     * @throws InvalidDataException if any field is missing
     */
    public void validateData() throws InvalidDataException {
        if (nullOrEmpty(getZoneId()) || nullOrEmpty(getDnsRecordName()) || nullOrEmpty(getDnsRecordIp()) ||
                nullOrEmpty(getFlynnStatusKey()) || nullOrEmpty(getCloudflareApiKey()) ||
                nullOrEmpty(getCloudflareApiEmail()))
            throw new InvalidDataException();
    }

    /**
     * Check if a string is null or empty
     * @param string string to check
     * @return true if null or empty
     */
    private boolean nullOrEmpty(@Nullable String string) {
        return string == null || string.isEmpty();
    }

    public class InvalidDataException extends Exception {}

    // Getters/Setters

    @Nullable
    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(@Nullable String zoneId) {
        this.zoneId = zoneId;
    }

    @Nullable
    public String getDnsRecordName() {
        return dnsRecordName;
    }

    public void setDnsRecordName(@Nullable String dnsRecordName) {
        this.dnsRecordName = dnsRecordName;
    }

    @Nullable
    public String getDnsRecordIp() {
        return dnsRecordIp;
    }

    public void setDnsRecordIp(@Nullable String dnsRecordIp) {
        this.dnsRecordIp = dnsRecordIp;
    }

    @Nullable
    public String getFlynnStatusKey() {
        return flynnStatusKey;
    }

    public void setFlynnStatusKey(@Nullable String flynnStatusKey) {
        this.flynnStatusKey = flynnStatusKey;
    }

    @Nullable
    public String getCloudflareApiKey() {
        return cloudflareApiKey;
    }

    public void setCloudflareApiKey(@Nullable String cloudflareApiKey) {
        this.cloudflareApiKey = cloudflareApiKey;
    }

    @Nullable
    public String getCloudflareApiEmail() {
        return cloudflareApiEmail;
    }

    public void setCloudflareApiEmail(@Nullable String cloudflareApiEmail) {
        this.cloudflareApiEmail = cloudflareApiEmail;
    }
}
