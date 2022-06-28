package com.icrown.backendapi.dtos;

import java.util.List;

/**
 * @author Frank
 */
public class GameSummaryReportResponse {
    private List<GameSummaryReportDetailResponse> records;
    private TotalSummaryReportResponse total;
    private SubTotalSummaryReportResponse subTotal;

    public List<GameSummaryReportDetailResponse> getRecords() {
        return records;
    }

    public void setRecords(List<GameSummaryReportDetailResponse> records) {
        this.records = records;
    }

    public SubTotalSummaryReportResponse getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(SubTotalSummaryReportResponse subTotal) {
        this.subTotal = subTotal;
    }

    public TotalSummaryReportResponse getTotal() {
        return total;
    }

    public void setTotal(TotalSummaryReportResponse total) {
        this.total = total;
    }
}
