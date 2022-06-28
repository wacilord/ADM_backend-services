package com.icrown.backendapi.dtos;

import java.math.BigDecimal;

/**
 * @author dennis
 */
public class SubTotalSummaryReportResponse {
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getSubTotalBets() {
        return subTotalBets;
    }

    public void setSubTotalBets(BigDecimal subTotalBets) {
        this.subTotalBets = subTotalBets;
    }

    public BigDecimal getSubTotalValidBets() {
        return subTotalValidBets;
    }

    public void setSubTotalValidBets(BigDecimal subTotalValidBets) {
        this.subTotalValidBets = subTotalValidBets;
    }

    public BigDecimal getSubTotalWin() {
        return subTotalWin;
    }

    public void setSubTotalWin(BigDecimal subTotalWin) {
        this.subTotalWin = subTotalWin;
    }

    public BigDecimal getSubTotalJackpotContribute() {
        return subTotalJackpotContribute;
    }

    public void setSubTotalJackpotContribute(BigDecimal subTotalJackpotContribute) {
        this.subTotalJackpotContribute = subTotalJackpotContribute;
    }

    public BigDecimal getSubTotalJackpot() {
        return subTotalJackpot;
    }

    public void setSubTotalJackpot(BigDecimal subTotalJackpot) {
        this.subTotalJackpot = subTotalJackpot;
    }

    public BigDecimal getSubTotalJackpot2() {
        return subTotalJackpot2;
    }

    public void setSubTotalJackpot2(BigDecimal subTotalJackpot2) {
        this.subTotalJackpot2 = subTotalJackpot2;
    }

    public BigDecimal getSubTotalJackpot3() {
        return subTotalJackpot3;
    }

    public void setSubTotalJackpot3(BigDecimal subTotalJackpot3) {
        this.subTotalJackpot3 = subTotalJackpot3;
    }

    public BigDecimal getSubCommission() {
        return subCommission;
    }

    public void setSubCommission(BigDecimal subCommission) {
        this.subCommission = subCommission;
    }

    public BigDecimal getSubTotalNetWin() {
        return subTotalNetWin;
    }

    public void setSubTotalNetWin(BigDecimal subTotalNetWin) {
        this.subTotalNetWin = subTotalNetWin;
    }

    public BigDecimal getSubTotalPureNetWin() {
        return subTotalPureNetWin;
    }

    public void setSubTotalPureNetWin(BigDecimal subTotalPureNetWin) {
        this.subTotalPureNetWin = subTotalPureNetWin;
    }

    public int getItems() {
        return items;
    }

    public void setItems(int items) {
        this.items = items;
    }

    private String currency = "";
    private BigDecimal subTotalBets = BigDecimal.valueOf(0);
    private BigDecimal subTotalValidBets = BigDecimal.valueOf(0);
    private BigDecimal subTotalWin = BigDecimal.valueOf(0);
    private BigDecimal subTotalJackpotContribute = BigDecimal.valueOf(0);
    private BigDecimal subTotalJackpot = BigDecimal.valueOf(0);
    private BigDecimal subTotalJackpot2 = BigDecimal.valueOf(0);
    private BigDecimal subTotalJackpot3 = BigDecimal.valueOf(0);
    private BigDecimal subCommission = BigDecimal.valueOf(0);
    private BigDecimal subTotalNetWin = BigDecimal.valueOf(0);
    private BigDecimal subTotalPureNetWin = BigDecimal.valueOf(0);
    private int items = 0;
}
