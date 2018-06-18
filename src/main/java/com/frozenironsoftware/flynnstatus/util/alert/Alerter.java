package com.frozenironsoftware.flynnstatus.util.alert;

import com.frozenironsoftware.flynnstatus.util.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class Alerter {
    private static List<Alerter> alerters = new ArrayList<>();

    /**
     * Sends a message to all alerters that the cluster with the specifed dns record name is indicating it is unhealthy
     * @param dnsRecordName cluster dns record name
     */
    public static void sendClusterUnhealthyMessage(String dnsRecordName) {
        sendAll(String.format("Cluster \"%s\" has reported an unhealthy status.", dnsRecordName),
                "Cluster Unhealthy");
    }

    /**
     * Send all alerters a message
     * @param message message to send
     */
    private static void sendAll(String message, @Nullable String shortMessage) {
        for (Alerter alerter : alerters) {
            alerter.send(message, shortMessage);
        }
    }

    /**
     * Checks the environment variables for alerter service configurations and attempts to enable them
     */
    public static void initialize() {
        String mailgunApiKey = System.getenv("MAILGUN_API_KEY");
        if (mailgunApiKey != null && !mailgunApiKey.isEmpty()) {
            String from = System.getenv("MAILGUN_FROM_EMAIL");
            String to = System.getenv("MAILGUN_TO_EMAIL");
            if (from != null && !from.isEmpty() && to != null && !to.isEmpty()) {
                Logger.info("Adding Mailgun alerter");
                alerters.add(new MailgunAlerter(mailgunApiKey, from, to));
            }
        }
    }

    /**
     * Send a message to all alerters indicating that a request to CloudFlare failed
     * @param cloudflareApiEmail email used for cloudflare request
     * @param dnsRecordName dns record name
     */
    public static void sendCloudflareApiFail(String cloudflareApiEmail, String dnsRecordName) {
        sendAll(String.format("CloudFlare request failed for account with email %s: Record name: %s",
                    cloudflareApiEmail, dnsRecordName),
                "CloudFlare API Failure");
    }

    /**
     * Send a message to all alerters indicating a DNS record has been removed
     * @param dnsRecordName record name
     * @param dnsRecordIp record IP
     */
    public static void sendDnsRecordRemovedAlert(String dnsRecordName, String dnsRecordIp) {
        sendAll(String.format("Removed DNS record: Name: %s, IP: %s", dnsRecordName, dnsRecordIp),
                "DNS Record Removed");
    }

    public static void sendDnsRecordAdddedAlert(String dnsRecordName, String dnsRecordIp) {
        sendAll(String.format("Added DNS record: Name: %s, IP: %s", dnsRecordName, dnsRecordIp),
                "DNS Record Added");
    }

    /**
     * Implementations should handle the message according to the service they define.
     * @param message message to be handled
     * @param shortMessage short message that some implementations may user
     */
    abstract void send(String message, @Nullable String shortMessage);
}
