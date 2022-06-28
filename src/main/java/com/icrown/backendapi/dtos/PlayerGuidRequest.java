package com.icrown.backendapi.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayerGuidRequest {

    @JsonCreator
    public PlayerGuidRequest(@JsonProperty(value = "plyGuid", required = true) String plyGuid) {
        this.plyGuid=plyGuid;
    }

    public String getPlyGuid() {
        return plyGuid;
    }

    public void setPlyGuid(String plyGuid) {
        this.plyGuid = plyGuid;
    }

    private String plyGuid;

}
