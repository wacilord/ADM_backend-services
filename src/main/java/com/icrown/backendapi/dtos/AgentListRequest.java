package com.icrown.backendapi.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AgentListRequest {


    @JsonCreator
    public AgentListRequest(
            @JsonProperty(value = "pageIndex", required = true) int pageIndex,
            @JsonProperty(value = "pageSize", required = true) int pageSize,
            @JsonProperty(value = "status", required = true) int status

    ) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.status = status;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    private int pageIndex;
    private int pageSize;
    private int status;
    private String accountID;
}
