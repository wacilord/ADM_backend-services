package com.icrown.backendapi.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AddSubAccountRequest {



    @JsonCreator
    public AddSubAccountRequest(@JsonProperty(value = "accountID", required = true) String accountID,
                           @JsonProperty(value = "password", required = true) String password,
                           @JsonProperty(value = "funCodes", required = true) List<String> funCodes) {
        this.accountID = accountID;
        this.password = password;
        this.funCodes = funCodes;
    }




    private String accountID;
    private String password;
    private String nickName;
    private String memo;
    private List<String> funCodes;

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

    public List<String> getFunCodes() {
        return funCodes;
    }

    public void setFunCodes(List<String> funCodes) {
        this.funCodes = funCodes;
    }
}
