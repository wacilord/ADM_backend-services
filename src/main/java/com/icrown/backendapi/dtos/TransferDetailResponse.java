package com.icrown.backendapi.dtos;

import java.util.List;

public class TransferDetailResponse {
    private List<TransferListDetailResponse> records;

    public List<TransferListDetailResponse> getRecords() {
        return records;
    }

    public void setRecords(List<TransferListDetailResponse> records) {
        this.records = records;
    }
}
