package com.icrown.backendapi.dtos;

public class UpdateChildAgentPasswordRequest {
    private String agentGuid;
    private String password;

    public String getAgentGuid() {
        return agentGuid;
    }

    public void setAgentGuid(String agentGuid) {
        this.agentGuid = agentGuid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
