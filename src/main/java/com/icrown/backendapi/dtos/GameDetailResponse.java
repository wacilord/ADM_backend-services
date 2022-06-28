package com.icrown.backendapi.dtos;

import java.util.List;

public class GameDetailResponse {
    private List<GameDetailRecordResponse> records;

    public List<GameDetailRecordResponse> getRecords() {
        return records;
    }

    public void setRecords(List<GameDetailRecordResponse> records) {
        this.records = records;
    }
}
