package com.icrown.backendapi.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class QueryGameDetailRequest {

    private long gameTurn;

    @JsonCreator
    public QueryGameDetailRequest(@JsonProperty(value = "gameTurn", required = true) long gameTurn) {
        this.gameTurn = gameTurn;
    }

    public long getGameTurn() {
        return gameTurn;
    }

    public void setGameTurn(long gameTurn) {
        this.gameTurn = gameTurn;
    }
}
