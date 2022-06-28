package com.icrown.backendapi.dtos;

import com.icrown.common.dtos.GameListResponse;
import com.icrown.gameapi.models.GameTypeModel;

import java.util.List;

public class GameCodeListResponse {
    private List<GameTypeModel>  gameType;
    private List<GameListResponse> list;

    public List<GameTypeModel>  getGameType() {
        return gameType;
    }

    public void setGameType(List<GameTypeModel>  gameType) {
        this.gameType = gameType;
    }

    public List<GameListResponse> getList() {
        return list;
    }

    public void setList(List<GameListResponse> list) {
        this.list = list;
    }
}
