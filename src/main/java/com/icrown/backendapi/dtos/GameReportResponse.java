package com.icrown.backendapi.dtos;

import java.util.List;

/**
 * @author Frank
 */
public class GameReportResponse {
    private List<GameRecord> gameRecordList;

    public List<GameRecord> getGameRecordList() {
        return gameRecordList;
    }

    public void setGameRecordList(List<GameRecord> gameRecordList) {
        this.gameRecordList = gameRecordList;
    }
}
