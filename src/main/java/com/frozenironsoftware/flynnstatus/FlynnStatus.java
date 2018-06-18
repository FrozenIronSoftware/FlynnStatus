package com.frozenironsoftware.flynnstatus;

import com.frozenironsoftware.flynnstatus.data.Constants;
import com.frozenironsoftware.flynnstatus.data.model.Monitor;
import com.frozenironsoftware.flynnstatus.util.CloudflareApi;
import com.frozenironsoftware.flynnstatus.util.HealthChecker;
import com.frozenironsoftware.flynnstatus.util.Logger;
import com.frozenironsoftware.flynnstatus.util.alert.Alerter;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class FlynnStatus {
    public static void main(String[] args) {
        // Parse log level
        String logLevel = System.getenv().getOrDefault("LOG_LEVEL", "INFO");
        Logger.setLevel(Level.parse(logLevel));
        Logger.info("Starting %s version %s", Constants.NAME, Constants.VERSION);
        // Parse domains/ips
        String monitorJson = System.getenv("MONITOR");
        if (monitorJson == null || monitorJson.isEmpty()) {
            Logger.warn("Missing env var MONITOR");
            System.exit(1);
        }
        Gson gson = new Gson();
        List<Monitor> monitors = null;
        try {
            monitors = gson.fromJson(monitorJson, new TypeToken<List<Monitor>>() {}.getType());
        }
        catch (JsonSyntaxException e) {
            Logger.warn("Failed to parse monitor json");
            Logger.exception(e);
            System.exit(1);
        }
        for (Monitor monitor : monitors) {
            try {
                monitor.validateData();
            }
            catch (Monitor.InvalidDataException e) {
                Logger.warn("Monitor json is missing data");
                Logger.exception(e);
                System.exit(1);
            }
        }
        // Dry run
        boolean dryRun = Boolean.parseBoolean(System.getenv().getOrDefault("DRY_RUN", "true"));
        if (dryRun)
            Logger.warn("Dry run mode is enabled. No changes will be made to Cloudflare DNS records.");
        else
            Logger.warn("Dry run mode is disabled. Changes will be made to Cloudfalre DNS records.");

        // Check
        HealthChecker healthChecker = new HealthChecker();
        CloudflareApi cloudflareApi = new CloudflareApi(dryRun);
        Alerter.initialize();
        //noinspection InfiniteLoopStatement
        while (true) {
            for (Monitor monitor : monitors) {
                Logger.verbose("Checking health: Name %s, IP %s", monitor.getDnsRecordName(),
                        monitor.getDnsRecordIp());
                if (!healthChecker.isNodeAlive(monitor)) {
                    Logger.verbose("Attempting to remove DNS record for: Name: %s, IP: %s",
                            monitor.getDnsRecordName(), monitor.getDnsRecordIp());
                    cloudflareApi.removeIfNotPresent(monitor);
                }
                else {
                    Logger.verbose("Attempting to add DNS record for: Name: %s, IP: %s",
                            monitor.getDnsRecordName(), monitor.getDnsRecordIp());
                    cloudflareApi.addIfNotPresent(monitor);
                }
            }
            try {
                Thread.sleep(60000);
            }
            catch (InterruptedException e) {
                System.exit(0);
            }
        }
    }

    /**
     * Parse passed CLI args
     * @param args args
     */
    private static void parseArgs(String[] args) {
        String logLevel = "INFO";
        for (String arg : args) {
            if (arg.equalsIgnoreCase("--log")) {
                int logIndex = Arrays.asList(args).indexOf(arg);
                if (logIndex + 1 < args.length)
                    logLevel = args[logIndex + 1];
            }
        }
        Logger.setLevel(Level.parse(logLevel));
    }
}
