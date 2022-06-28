package com.icrown.backendapi.dtos;

import java.util.List;

public class LoginLogResponse {
    private List<LoginLogRecordResponse> records;
    private int pageIndex;
    private int pageSize;
    private int pageCount;
    private int itemCount;

    public List<LoginLogRecordResponse> getRecords() {
        return records;
    }

    public void setRecords(List<LoginLogRecordResponse> records) {
        this.records = records;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
}
