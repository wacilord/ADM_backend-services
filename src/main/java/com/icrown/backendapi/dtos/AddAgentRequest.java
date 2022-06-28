package com.icrown.backendapi.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AddAgentRequest {


    @JsonCreator
    public AddAgentRequest(@JsonProperty(value = "accountID", required = true) String accountID,
                           @JsonProperty(value = "password", required = true) String password) {
        this.accountID = accountID;
        this.password = password;
    }

    private String accountID;
    private String password;
    private String nickName;
    private String memo;

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
