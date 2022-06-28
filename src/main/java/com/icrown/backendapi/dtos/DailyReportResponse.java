package com.icrown.backendapi.dtos;

import java.util.List;

/**
 * @author Frank
 */
public class DailyReportResponse {
    private List<DailyReportDetailResponse> records;
    private TotalSummaryReportResponse total;

    public List<DailyReportDetailResponse> getRecords() {
        return records;
    }

    public void setRecords(List<DailyReportDetailResponse> records) {
        this.records = records;
    }

    public TotalSummaryReportResponse getTotal() {
        return total;
    }

    public void setTotal(TotalSummaryReportResponse total) {
        this.total = total;
    }
}
