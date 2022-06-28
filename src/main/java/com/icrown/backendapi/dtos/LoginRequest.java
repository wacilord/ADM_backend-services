package com.icrown.backendapi.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequest {

    @JsonCreator
    public LoginRequest(@JsonProperty(value = "accountID", required = true) String accountID,
                        @JsonProperty(value = "password", required = true) String password,
                        @JsonProperty(value = "domain", required = true) String domain) {
        this.accountID = accountID;
        this.password = password;
        this.domain = domain;

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


    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }


    private String accountID;
    private String password;
    private String domain;

}
