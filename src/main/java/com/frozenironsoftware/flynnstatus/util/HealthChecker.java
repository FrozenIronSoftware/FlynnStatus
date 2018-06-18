package com.frozenironsoftware.flynnstatus.util;

import com.frozenironsoftware.flynnstatus.data.Constants;
import com.frozenironsoftware.flynnstatus.data.model.Monitor;
import com.frozenironsoftware.flynnstatus.data.model.flynn.Status;
import com.frozenironsoftware.flynnstatus.data.model.flynn.StatusData;
import com.frozenironsoftware.flynnstatus.util.alert.Alerter;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import sun.security.ssl.SSLSocketImpl;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class HealthChecker {
    private Gson gson = new Gson();

    /**
     * Attempt to connect to the status endpoint for a cluster node
     * The status of the cluster will be checked and if it is not healthy, an alert event will be fired.
     * @param monitor cluster node
     * @return true if the cluster node responds with valid status json. The return value will not reflect the status of
     * the cluster.
     */
    public boolean isNodeAlive(Monitor monitor) {
        try {
            Status status = getStatus(monitor);
            StatusData statusData = status.getData();
            if (statusData == null || statusData.getStatus() == null)
                return false;
            boolean clusterIsHealthy = statusData.getStatus().equalsIgnoreCase("healthy");
            if (!clusterIsHealthy)
                Alerter.sendClusterUnhealthyMessage(monitor.getDnsRecordName());
            return true;
        }
        catch (HealthCheckException e) {
            Logger.exception(e);
            return false;
        }
    }

    /**
     * Request JSON status from a Flynn cluster node.
     * @param monitor cluster node
     * @return the status of the Flynn cluster
     * @throws HealthCheckException Errors if there is an error communicating with the server or the status data is not
     * valid. This will check the cluster status health.
     */
    private Status getStatus(Monitor monitor) throws HealthCheckException {
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        final HealthCheckException[] healthCheckException = {null};
        final Status[] status = {null};
        Thread readThread = new Thread(() -> {
            try (SSLSocket socket = (SSLSocket) factory.createSocket(monitor.getDnsRecordIp(), 443)) {
                String host = String.format("status.%s", monitor.getDnsRecordName());
                if (socket instanceof SSLSocketImpl)
                    ((SSLSocketImpl) socket).setHost(host);
                String request = String.format("GET /%s HTTP/1.1\r\nHost: %s\r\nUser-Agent: %s\r\n" +
                                "Accept: application/json\r\n\r\n",
                        String.format("?key=%s", monitor.getFlynnStatusKey()),
                        host,
                        String.format("%s/%s (Java/%s)", Constants.NAME, Constants.VERSION,
                                System.getProperty("java.version"))
                );
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                DataInputStream in = new DataInputStream(socket.getInputStream());
                out.writeBytes(request);
                out.flush();

                StringBuilder responseHeader = new StringBuilder();
                StringBuilder response = new StringBuilder();
                Integer contentSize = null;
                boolean reachedEndOfHeader = false;
                while (socket.isConnected() && (contentSize == null || contentSize > 0)) {
                    AtomicInteger readBytes = new AtomicInteger();
                    byte[] finalBytes = new byte[1048576];
                    readBytes.set(in.read(finalBytes));
                    byte[] bytes = Arrays.copyOfRange(finalBytes, 0, readBytes.get());
                    if (readBytes.get() < 0) {
                        healthCheckException[0] = new HealthCheckException("Reached unexpected end of stream.");
                        return;
                    }
                    if (!reachedEndOfHeader) {
                        responseHeader.append(new String(bytes));
                        // Check for content size
                        if (contentSize == null) {
                            String[] responseSplit = responseHeader.toString().split("\r\n");
                            for (String line : responseSplit) {
                                if (line.toUpperCase().startsWith("CONTENT-LENGTH")) {
                                    try {
                                        contentSize = Integer.parseInt(line.toUpperCase()
                                                .replace("CONTENT-LENGTH", "")
                                                .replace(":", "").trim());
                                    } catch (NumberFormatException e) {
                                        healthCheckException[0] =
                                                new HealthCheckException("Failed to parse content-length header.");
                                        return;
                                    }
                                }
                            }
                        }
                        // Check end of header
                        String[] headerSplit = responseHeader.toString().split("\r\n\r\n");
                        if (headerSplit.length > 1) {
                            reachedEndOfHeader = true;
                            responseHeader = new StringBuilder(headerSplit[0]).append("\r\n\r\n");
                            for (int splitIndex = 1; splitIndex < headerSplit.length; splitIndex++) {
                                if (splitIndex != 1)
                                    response.append("\r\n\r\n");
                                response.append(headerSplit[splitIndex]);
                            }
                            if (contentSize != null)
                                contentSize -= response.toString().getBytes().length;
                        }
                    }
                    else {
                        if (contentSize == null) {
                            healthCheckException[0] =
                                    new HealthCheckException("Received data larger than specified content-length header");
                            return;
                        }
                        contentSize -= readBytes.get();
                        response.append(new String(bytes));
                    }
                }
                try {
                    Status flynnStatus = gson.fromJson(response.toString(), Status.class);
                    StatusData statusData = flynnStatus.getData();
                    if (statusData == null || statusData.getStatus() == null || statusData.getStatus().isEmpty()) {
                        healthCheckException[0] = new HealthCheckException("Invalid status json received: No data");
                        return;
                    }
                    status[0] = flynnStatus;
                }
                catch (JsonSyntaxException e) {
                    Logger.exception(e);
                    healthCheckException[0] = new HealthCheckException("Invalid status json received: Failed to parse");
                }
            }
            catch (IOException e) {
                Logger.exception(e);
                healthCheckException[0] = new HealthCheckException(e.getMessage());
            }
        });
        readThread.setName("Health Checker Read Thread");
        readThread.setDaemon(true);
        readThread.start();
        long startTime = System.currentTimeMillis();
        while (readThread.isAlive()) {
            if (System.currentTimeMillis() - startTime >= 30000) {
                readThread.interrupt();
                throw new HealthCheckException("Timed out reading data.");
            }
        }
        if (healthCheckException[0] != null)
            throw healthCheckException[0];
        return status[0];
    }


    public class HealthCheckException extends Exception {
        HealthCheckException(String message) {
            super(message);
        }
    }
}
