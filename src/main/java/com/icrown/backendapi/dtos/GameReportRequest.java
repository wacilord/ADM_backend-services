package com.icrown.backendapi.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

/**
 * @author Frank
 */
public class GameReportRequest {
    private List<Integer> gameType;
    private List<Integer> gameCode;
    private String accountID;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH")
    private Date startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH")
    private Date endDate;
    private int accountType;

    @JsonCreator
    public GameReportRequest(
            @JsonProperty(value = "gameType", required = true) List<Integer> gameType,
            @JsonProperty(value = "gameCode", required = true) List<Integer> gameCode,
            @JsonProperty(value = "accountID", required = true) String accountID,
            @JsonProperty(value = "startDate", required = true) Date startDate,
            @JsonProperty(value = "endDate", required = true) Date endDate,
            @JsonProperty(value = "accountType", required = true) int accountType) {
        this.gameType = gameType;
        this.gameCode = gameCode;
        this.accountID = accountID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.accountType = accountType;
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

    public List<Integer> getGameCode() {
        return gameCode;
    }

    public void setGameCode(List<Integer> gameCode) {
        this.gameCode = gameCode;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }
}
