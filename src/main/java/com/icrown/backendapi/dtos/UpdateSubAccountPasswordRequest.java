package com.icrown.backendapi.dtos;

public class UpdateSubAccountPasswordRequest {
    private String satGuid;
    private String password;

    public String getSatGuid() {
        return satGuid;
    }

    public void setSatGuid(String satGuid) {
        this.satGuid = satGuid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
