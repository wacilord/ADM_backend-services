package com.icrown.backendapi.dtos;

import java.math.BigDecimal;

/**
 * @author Frank
 */
public class PlayerDetailBetsInfoResponse {
    private int gameType;
    private BigDecimal betsPercent;
    private BigDecimal bets;

    public int getGameType() {
        return gameType;
    }

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    public BigDecimal getBetsPercent() {
        return betsPercent;
    }

    public void setBetsPercent(BigDecimal betsPercent) {
        this.betsPercent = betsPercent;
    }

    public BigDecimal getBets() {
        return bets;
    }

    public void setBets(BigDecimal bets) {
        this.bets = bets;
    }
}
