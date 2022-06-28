package com.icrown.backendapi.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AgentGuidRequest {

    @JsonCreator
    public AgentGuidRequest(@JsonProperty(value = "agentGuid", required = true) String agentGuid) {
        this.agentGuid=agentGuid;
    }

    private String agentGuid;

    public String getAgentGuid() {
        return agentGuid;
    }

    public void setAgentGuid(String agentGuid) {
        this.agentGuid = agentGuid;
    }
}
