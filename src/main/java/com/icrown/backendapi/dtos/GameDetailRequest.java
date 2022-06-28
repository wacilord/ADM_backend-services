package com.icrown.backendapi.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Frank
 */
public class GameDetailRequest {
    private long seq;
    @JsonCreator
    public GameDetailRequest(
            @JsonProperty(value = "seq", required = true) long seq
    ) {
        this.seq = seq;
    }
    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }
}