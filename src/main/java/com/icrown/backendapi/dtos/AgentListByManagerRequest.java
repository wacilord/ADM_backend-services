package com.icrown.backendapi.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AgentListByManagerRequest {
    private String managerAccountID;
    private String accountID;
    private int status;
    private int pageIndex;
    private int pageSize;

    @JsonCreator
    public AgentListByManagerRequest(
            @JsonProperty(value = "managerAccountID", required = true) String managerAccountID,
            @JsonProperty(value = "pageIndex", required = true) int pageIndex,
            @JsonProperty(value = "pageSize", required = true) int pageSize,
            @JsonProperty(value = "status", required = true) int status

    ) {
        this.managerAccountID = managerAccountID;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.status = status;
    }

    public String getManagerAccountID() {
        return managerAccountID;
    }

    public void setManagerAccountID(String managerAccountID) {
        this.managerAccountID = managerAccountID;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
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
}
