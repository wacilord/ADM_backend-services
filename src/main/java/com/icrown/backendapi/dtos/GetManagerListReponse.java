package com.icrown.backendapi.dtos;

import java.util.List;

public class GetManagerListReponse {

    private List<Agent> records;;

    public List<Agent> getRecords() {
        return records;
    }

    public void setRecords(List<Agent> records) {
        this.records = records;
    }
}
