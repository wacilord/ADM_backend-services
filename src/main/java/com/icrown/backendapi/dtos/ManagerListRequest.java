package com.icrown.backendapi.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ManagerListRequest {
    private String accountID;
    private Integer status;

    public ManagerListRequest(
            @JsonProperty(value = "accountID", required = true) String accountID,
            @JsonProperty(value = "status", required = true) Integer status) {
        this.accountID = accountID;
        this.status = status;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
