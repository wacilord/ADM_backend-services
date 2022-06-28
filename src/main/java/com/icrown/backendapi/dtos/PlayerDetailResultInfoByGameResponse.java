package com.icrown.backendapi.dtos;

import java.math.BigDecimal;

/**
 * @author Frank
 */
public class PlayerDetailResultInfoByGameResponse {
    private int gameType;
    private BigDecimal netWin;

    public int getGameType() {
        return gameType;
    }

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    public BigDecimal getNetWin() {
        return netWin;
    }

    public void setNetWin(BigDecimal netWin) {
        this.netWin = netWin;
    }
}
