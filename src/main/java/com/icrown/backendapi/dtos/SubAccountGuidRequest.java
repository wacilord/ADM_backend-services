package com.icrown.backendapi.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SubAccountGuidRequest {
    @JsonCreator
    public SubAccountGuidRequest(@JsonProperty(value = "satGuid", required = true) String satGuid) {
        this.satGuid=satGuid;
    }

    private String satGuid;

    public String getSatGuid() {
        return satGuid;
    }

    public void setSatGuid(String satGuid) {
        this.satGuid = satGuid;
    }
}
