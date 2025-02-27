package com.pos.increff.model.data;

import java.util.List;

public class PaginatedData<T> {
    private List<T> data;
    private int currentPage;
    private int pageSize;
    private boolean hasNextPage;

    public PaginatedData(List<T> data, int currentPage, int pageSize, boolean hasNextPage) {
        this.data = data;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.hasNextPage = hasNextPage;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }
} 