package com.icrown.backendapi.dtos;

public class SummaryReportResponse {
    private GameSummaryReportResponse gameRecord;
    private PlayerSummaryReportResponse playerRecord;

    public GameSummaryReportResponse getGameRecord() {
        return gameRecord;
    }

    public void setGameRecord(GameSummaryReportResponse gameRecord) {
        this.gameRecord = gameRecord;
    }

    public PlayerSummaryReportResponse getPlayerRecord() {
        return playerRecord;
    }

    public void setPlayerRecord(PlayerSummaryReportResponse playerRecord) {
        this.playerRecord = playerRecord;
    }
}
