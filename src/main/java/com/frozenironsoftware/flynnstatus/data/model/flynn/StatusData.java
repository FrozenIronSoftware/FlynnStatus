package com.frozenironsoftware.flynnstatus.data.model.flynn;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class StatusData {
    @Nullable private String status;
    @Nullable private Map<String, StatusDetail> detail;
    @Nullable private String version;

    @Nullable
    public String getStatus() {
        return status;
    }

    public void setStatus(@Nullable String status) {
        this.status = status;
    }

    @Nullable
    public Map<String, StatusDetail> getDetail() {
        return detail;
    }

    public void setDetail(@Nullable Map<String, StatusDetail> detail) {
        this.detail = detail;
    }

    @Nullable
    public String getVersion() {
        return version;
    }

    public void setVersion(@Nullable String version) {
        this.version = version;
    }
}
