package com.icrown.backendapi.dtos;

import java.math.BigDecimal;

/**
 * @author Cliff
 */
public class GameRecordByGameCode {
    private String gameCode;
    private String currency;
    private BigDecimal sumBets;
    private BigDecimal sumValidBets;
    private BigDecimal sumWin;
    private BigDecimal sumJackpotContribute;
    private BigDecimal sumJackpot;
    private BigDecimal sumJackpot2;
    private BigDecimal sumJackpot3;
    private BigDecimal sumNetWin;
    private BigDecimal sumPureNetWin;
    private BigDecimal sumCommission;
    private int items;
    private BigDecimal bankerAdvantage;

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

    public BigDecimal getSumBets() {
        return sumBets;
    }

    public void setSumBets(BigDecimal sumBets) {
        this.sumBets = sumBets;
    }

    public BigDecimal getSumValidBets() {
        return sumValidBets;
    }

    public void setSumValidBets(BigDecimal sumValidBets) {
        this.sumValidBets = sumValidBets;
    }

    public BigDecimal getSumWin() {
        return sumWin;
    }

    public void setSumWin(BigDecimal sumWin) {
        this.sumWin = sumWin;
    }

    public BigDecimal getSumCommission() {
        return sumCommission;
    }

    public void setSumCommission(BigDecimal sumCommission) {
        this.sumCommission = sumCommission;
    }

    public BigDecimal getSumJackpotContribute() {
        return sumJackpotContribute;
    }

    public void setSumJackpotContribute(BigDecimal sumJackpotContribute) {
        this.sumJackpotContribute = sumJackpotContribute;
    }

    public BigDecimal getSumJackpot() {
        return sumJackpot;
    }

    public void setSumJackpot(BigDecimal sumJackpot) {
        this.sumJackpot = sumJackpot;
    }

    public BigDecimal getSumJackpot2() {
        return sumJackpot2;
    }

    public void setSumJackpot2(BigDecimal sumJackpot2) {
        this.sumJackpot2 = sumJackpot2;
    }

    public BigDecimal getSumJackpot3() {
        return sumJackpot3;
    }

    public void setSumJackpot3(BigDecimal sumJackpot3) {
        this.sumJackpot3 = sumJackpot3;
    }

    public BigDecimal getSumNetWin() {
        return sumNetWin;
    }

    public void setSumNetWin(BigDecimal sumNetWin) {
        this.sumNetWin = sumNetWin;
    }

    public BigDecimal getSumPureNetWin() {
        return sumPureNetWin;
    }

    public void setSumPureNetWin(BigDecimal sumPureNetWin) {
        this.sumPureNetWin = sumPureNetWin;
    }

    public int getItems() {
        return items;
    }

    public void setItems(int items) {
        this.items = items;
    }

    public BigDecimal getBankerAdvantage() {
        return bankerAdvantage;
    }

    public void setBankerAdvantage(BigDecimal bankerAdvantage) {
        this.bankerAdvantage = bankerAdvantage;
    }
}
