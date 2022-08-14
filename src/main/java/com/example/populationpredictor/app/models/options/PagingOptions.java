package com.example.populationpredictor.app.models.options;

public final class PagingOptions {
    private final long page;
    private final long pageSize;

    public PagingOptions(long page, long pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

    public long getPage() {
        return page;
    }

    public long getPageSize() {
        return pageSize;
    }
}