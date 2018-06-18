package com.frozenironsoftware.flynnstatus.data.model.cloudflare;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DnsRecordList {
    private boolean success;
    @Nullable private List<String> errors;
    @Nullable private List<String> messages;
    @Nullable private List<DnsRecord> result;
    @SerializedName("result_info")
    @Nullable private ResultInfo resultInfo;

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
    public List<DnsRecord> getResult() {
        return result;
    }

    public void setResult(@Nullable List<DnsRecord> result) {
        this.result = result;
    }

    @Nullable
    public ResultInfo getResultInfo() {
        return resultInfo;
    }

    public void setResultInfo(@Nullable ResultInfo resultInfo) {
        this.resultInfo = resultInfo;
    }
}
