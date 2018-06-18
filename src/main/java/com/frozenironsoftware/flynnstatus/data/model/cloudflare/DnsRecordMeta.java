package com.frozenironsoftware.flynnstatus.data.model.cloudflare;

import com.google.gson.annotations.SerializedName;

public class DnsRecordMeta {
    @SerializedName("auto_added")
    private boolean autoAdded;
    @SerializedName("managed_by_apps")
    private boolean managedByApps;
    @SerializedName("managed_by_argo_tunnel")
    private boolean managedByArgoTunnel;

    public boolean isAutoAdded() {
        return autoAdded;
    }

    public void setAutoAdded(boolean autoAdded) {
        this.autoAdded = autoAdded;
    }

    public boolean isManagedByApps() {
        return managedByApps;
    }

    public void setManagedByApps(boolean managedByApps) {
        this.managedByApps = managedByApps;
    }

    public boolean isManagedByArgoTunnel() {
        return managedByArgoTunnel;
    }

    public void setManagedByArgoTunnel(boolean managedByArgoTunnel) {
        this.managedByArgoTunnel = managedByArgoTunnel;
    }
}
