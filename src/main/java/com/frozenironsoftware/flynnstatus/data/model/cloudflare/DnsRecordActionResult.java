package com.frozenironsoftware.flynnstatus.data.model.cloudflare;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DnsRecordActionResult {
    private boolean success;
    @Nullable private List<String> errors;
    @Nullable private List<String> messages;
    @Nullable private DnsRecord result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Nullable
    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(@Nullable List<String> errors) {
        this.errors = errors;
    }

    @Nullable
    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(@Nullable List<String> messages) {
        this.messages = messages;
    }

    @Nullable
    public DnsRecord getResult() {
        return result;
    }

    public void setResult(@Nullable DnsRecord result) {
        this.result = result;
    }
}
