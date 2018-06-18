package com.frozenironsoftware.flynnstatus.data.model.flynn;

import org.jetbrains.annotations.Nullable;

public class StatusDetail {
    @Nullable private String status;
    @Nullable private String version;

    @Nullable
    public String getVersion() {
        return version;
    }

    public void setVersion(@Nullable String version) {
        this.version = version;
    }

    @Nullable
    public String getStatus() {
        return status;
    }

    public void setStatus(@Nullable String status) {
        this.status = status;
    }
}
