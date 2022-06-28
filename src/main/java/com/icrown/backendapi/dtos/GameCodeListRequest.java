package com.icrown.backendapi.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GameCodeListRequest {
    private int[] gameType;

    @JsonCreator
    public GameCodeListRequest(
            @JsonProperty(value = "gameType", required = true) int[] gameType
                              ) {
        this.gameType = gameType;
    }

    public int[] getGameType() {
        return gameType;
    }

    public void setGameType(int[] gameType) {
        this.gameType = gameType;
    }
}
