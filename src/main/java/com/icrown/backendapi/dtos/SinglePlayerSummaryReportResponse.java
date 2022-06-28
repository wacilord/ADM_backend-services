package com.icrown.backendapi.dtos;

import java.util.List;

/**
 * @author dennis
 */
public class SinglePlayerSummaryReportResponse {
    private List<SinglePlayerSummaryReportDetailResponse> records;

    public List<SinglePlayerSummaryReportDetailResponse> getRecords() {
        return records;
    }

    public void setRecords(List<SinglePlayerSummaryReportDetailResponse> records) {
        this.records = records;
    }
}
