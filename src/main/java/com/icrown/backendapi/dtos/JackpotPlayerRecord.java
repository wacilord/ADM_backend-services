package com.icrown.backendapi.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author dennis
 */
public class JackpotPlayerRecord {
    private String gameTurn;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date gameTime;
    private String currency;
    private List<String> agentTree;
    private String accountID;
    private String gameName;
    private String gameCode;
    private String jackpotPoolType;
    private BigDecimal jackpot2;

    public String getGameTurn() {
        return gameTurn;
    }

    public void setGameTurn(String gameTurn) {
        this.gameTurn = gameTurn;
    }

    public Date getGameTime() {
        return gameTime;
    }

    public void setGameTime(Date gameTime) {
        this.gameTime = gameTime;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<String> getAgentTree() {
        return agentTree;
    }

    public void setAgentTree(List<String> agentTree) {
        this.agentTree = agentTree;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }

    public String getJackpotPoolType() {
        return jackpotPoolType;
    }

    public void setJackpotPoolType(String jackpotPoolType) {
        this.jackpotPoolType = jackpotPoolType;
    }

    public BigDecimal getJackpot2() {
        return jackpot2;
    }

    public void setJackpot2(BigDecimal jackpot2) {
        this.jackpot2 = jackpot2;
    }
}
