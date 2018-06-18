package com.frozenironsoftware.flynnstatus.data.model.flynn;

import org.jetbrains.annotations.Nullable;

public class Status {
    @Nullable private StatusData data;

    @Nullable
    public StatusData getData() {
        return data;
    }

    public void setData(@Nullable StatusData data) {
        this.data = data;
    }
}
