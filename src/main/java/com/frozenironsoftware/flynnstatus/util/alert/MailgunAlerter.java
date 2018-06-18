package com.frozenironsoftware.flynnstatus.util.alert;

import com.frozenironsoftware.flynnstatus.util.Logger;
import com.frozenironsoftware.flynnstatus.util.WebbUtil;
import com.goebl.david.Webb;
import com.goebl.david.WebbException;
import org.jetbrains.annotations.Nullable;

import java.util.Base64;

class MailgunAlerter extends Alerter {
    private static final String API = "https://api.mailgun.net/v3";
    private final String apiKey;
    private final String from;
    private final String to;
    private final String domain;

    MailgunAlerter(String apiKey, String from, String to) {
        this.apiKey = apiKey;
        this.from = from;
        this.to = to;
        this.domain = from.replaceAll("[<|>]", "").split("@")[1].trim();
    }

    @Override
    void send(String message, @Nullable String shortMessage) {
        Webb webb = getWebb();
        try {
            webb.post("/messages")
                    .param("from", from)
                    .param("to", to)
                    .param("subject",
                            String.format("Alert Fired%s", shortMessage == null ? "" : ": " + shortMessage))
                    .param("text", message)
                    .param("o:require-tls", "True")
                    .ensureSuccess()
                    .asVoid();
        }
        catch (WebbException e) {
            Logger.warn("MailgunAlerter: Failed to send a message");
            Logger.exception(e);
        }
    }

    /**
     * Get a web instance configured for use with mailgun
     * @return authorized webb instance
     */
    private Webb getWebb() {
        Webb webb = WebbUtil.getWebb();
        // Auth
        String auth = String.format("api:%s", apiKey);
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        webb.setDefaultHeader("Authorization", String.format("Basic %s", encodedAuth));
        // Domain
        webb.setBaseUri(String.format("%s/%s", API, domain));
        return webb;
    }
}
