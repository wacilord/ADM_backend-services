package com.icrown.backendapi.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;
/**
 * @author Frank
 */
public class GameSummaryReportRequest {
    private List<Integer> gameType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date endDate;

    @JsonCreator
    public GameSummaryReportRequest(
            @JsonProperty(value = "gameType", required = true) List<Integer> gameType,
            @JsonProperty(value = "startDate", required = true) Date startDate,
            @JsonProperty(value = "endDate",required = true) Date endDate
    ) {
        this.gameType = gameType;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public List<Integer> getGameType() {
        return gameType;
    }

    public void setGameType(List<Integer> gameType) {
        this.gameType = gameType;
    }


    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
