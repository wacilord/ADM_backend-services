package com.icrown.backendapi.dtos;

import java.util.List;

/**
 * @author Frank
 */
public class PlayerSummaryReportResponse {
    private List<PlayerSummaryReportDetailResponse> records;
    private TotalSummaryReportResponse total;
    private SubTotalSummaryReportResponse subTotal;
    private int pageIndex;
    private int pageSize;
    private int pageCount;
    private int itemCount;

    public List<PlayerSummaryReportDetailResponse> getRecords() {
        return records;
    }

    public void setRecords(List<PlayerSummaryReportDetailResponse> records) {
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

    public TotalSummaryReportResponse getTotal() {
        return total;
    }

    public void setTotal(TotalSummaryReportResponse total) {
        this.total = total;
    }

    public SubTotalSummaryReportResponse getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(SubTotalSummaryReportResponse subTotal) {
        this.subTotal = subTotal;
    }
}
