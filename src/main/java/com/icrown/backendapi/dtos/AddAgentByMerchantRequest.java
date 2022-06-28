package com.icrown.backendapi.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AddAgentByMerchantRequest {

    private String managerAccountID;
    private String accountID;
    private String password;
    private String nickName;
    private String memo;

    @JsonCreator
    public AddAgentByMerchantRequest(
            @JsonProperty(value = "managerAccountID", required = true) String managerAccountID,
            @JsonProperty(value = "accountID", required = true) String accountID,
            @JsonProperty(value = "password", required = true) String password) {
        this.managerAccountID = managerAccountID;
        this.accountID = accountID;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
