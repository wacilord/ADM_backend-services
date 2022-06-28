package com.icrown.backendapi.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Frank
 */
public class GameDetailRecordResponse {
    private long seq;
    private String accountID;
    private int gameType;
    private String gameCode;
    private String currency;
    private BigDecimal bets;
    private BigDecimal validBets;
    private BigDecimal netWin;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public int getGameType() {
        return gameType;
    }

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getBets() {
        return bets;
    }

    public void setBets(BigDecimal bets) {
        this.bets = bets;
    }

    public BigDecimal getValidBets() {
        return validBets;
    }

    public void setValidBets(BigDecimal validBets) {
        this.validBets = validBets;
    }

    public BigDecimal getNetWin() {
        return netWin;
    }

    public void setNetWin(BigDecimal netWin) {
        this.netWin = netWin;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
}
