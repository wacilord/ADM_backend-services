package com.icrown.backendapi.dtos;

import java.util.List;

/**
 * @author adi
 */
public class PlayerListResponse {

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

    public List<Player> getRecords() {
        return records;
    }

    public void setRecords(List<Player> records) {
        this.records = records;
    }

    private int pageIndex;

    private int pageSize;
    private int pageCount;
    private int itemCount;
    private List<Player> records;
}
