package com.frozenironsoftware.flynnstatus.data.model.cloudflare;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

public class DnsRecord {
    @Nullable private String id;
    @Nullable private String type;
    @Nullable private String name;
    @Nullable private String content;
    private boolean proxiable;
    private boolean proxied;
    private long ttl;
    private boolean locked;
    @SerializedName("zone_id")
    @Nullable private String zoneId;
    @SerializedName("zone_name")
    @Nullable private String zoneName;
    @SerializedName("created_on")
    @Nullable private String createdOn;
    @SerializedName("modified_on")
    @Nullable private String modifiedOn;
    @Nullable private DnsRecordMeta meta;

    @Nullable
    public String getId() {
        return id;
    }

    public void setId(@Nullable String id) {
        this.id = id;
    }

    @Nullable
    public String getType() {
        return type;
    }

    public void setType(@Nullable String type) {
        this.type = type;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Nullable
    public String getContent() {
        return content;
    }

    public void setContent(@Nullable String content) {
        this.content = content;
    }

    public boolean isProxiable() {
        return proxiable;
    }

    public void setProxiable(boolean proxiable) {
        this.proxiable = proxiable;
    }

    public boolean isProxied() {
        return proxied;
    }

    public void setProxied(boolean proxied) {
        this.proxied = proxied;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Nullable
    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(@Nullable String zoneId) {
        this.zoneId = zoneId;
    }

    @Nullable
    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(@Nullable String zoneName) {
        this.zoneName = zoneName;
    }

    @Nullable
    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(@Nullable String createdOn) {
        this.createdOn = createdOn;
    }

    @Nullable
    public String getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(@Nullable String modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    @Nullable
    public DnsRecordMeta getMeta() {
        return meta;
    }

    public void setMeta(@Nullable DnsRecordMeta meta) {
        this.meta = meta;
    }
}
