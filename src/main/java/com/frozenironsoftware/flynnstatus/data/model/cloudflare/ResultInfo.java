package com.frozenironsoftware.flynnstatus.data.model.cloudflare;

import com.google.gson.annotations.SerializedName;

public class ResultInfo {
    private long page;
    @SerializedName("per_page")
    private long perPage;
    private long count;
    @SerializedName("total_count")
    private long totalCount;

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getPerPage() {
        return perPage;
    }

    public void setPerPage(long perPage) {
        this.perPage = perPage;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
}
