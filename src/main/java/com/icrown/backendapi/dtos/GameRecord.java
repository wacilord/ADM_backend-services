package com.icrown.backendapi.dtos;

import java.util.List;

/**
 * @author Cliff
 */
public class GameRecord {
    private int gameType;
    private List<GameRecordByGameCode> gameTypeRecordList;

    public int getGameType() {
        return gameType;
    }

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    public List<GameRecordByGameCode> getGameTypeRecordList() {
        return gameTypeRecordList;
    }

    public void setGameTypeRecordList(List<GameRecordByGameCode> gameTypeRecordList) {
        this.gameTypeRecordList = gameTypeRecordList;
    }
}
