package com.icrown.backendapi.dtos;

import java.math.BigDecimal;

/**
 * @author dennis
 */
public class TotalSummaryReportResponse {
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getTotalBets() {
        return totalBets;
    }

    public void setTotalBets(BigDecimal totalBets) {
        this.totalBets = totalBets;
    }

    public BigDecimal getTotalValidBets() {
        return totalValidBets;
    }

    public void setTotalValidBets(BigDecimal totalValidBets) {
        this.totalValidBets = totalValidBets;
    }

    public BigDecimal getTotalWin() {
        return totalWin;
    }

    public void setTotalWin(BigDecimal totalWin) {
        this.totalWin = totalWin;
    }

    public BigDecimal getTotalJackpotContribute() {
        return totalJackpotContribute;
    }

    public void setTotalJackpotContribute(BigDecimal totalJackpotContribute) {
        this.totalJackpotContribute = totalJackpotContribute;
    }

    public BigDecimal getTotalJackpot() {
        return totalJackpot;
    }

    public void setTotalJackpot(BigDecimal totalJackpot) {
        this.totalJackpot = totalJackpot;
    }

    public BigDecimal getTotalJackpot2() {
        return totalJackpot2;
    }

    public void setTotalJackpot2(BigDecimal totalJackpot2) {
        this.totalJackpot2 = totalJackpot2;
    }

    public BigDecimal getTotalJackpot3() {
        return totalJackpot3;
    }

    public void setTotalJackpot3(BigDecimal totalJackpot3) {
        this.totalJackpot3 = totalJackpot3;
    }

    public BigDecimal getTotalCommission() {
        return totalCommission;
    }

    public void setTotalCommission(BigDecimal totalCommission) {
        this.totalCommission = totalCommission;
    }

    public BigDecimal getTotalNetWin() {
        return totalNetWin;
    }

    public void setTotalNetWin(BigDecimal totalNetWin) {
        this.totalNetWin = totalNetWin;
    }

    public BigDecimal getTotalPureNetWin() {
        return totalPureNetWin;
    }

    public void setTotalPureNetWin(BigDecimal totalPureNetWin) {
        this.totalPureNetWin = totalPureNetWin;
    }

    public int getItems() {
        return items;
    }

    public void setItems(int items) {
        this.items = items;
    }

    private String currency = "";
    private BigDecimal totalBets = BigDecimal.valueOf(0);
    private BigDecimal totalValidBets = BigDecimal.valueOf(0);
    private BigDecimal totalWin = BigDecimal.valueOf(0);
    private BigDecimal totalJackpotContribute = BigDecimal.valueOf(0);
    private BigDecimal totalJackpot = BigDecimal.valueOf(0);
    private BigDecimal totalJackpot2 = BigDecimal.valueOf(0);
    private BigDecimal totalJackpot3 = BigDecimal.valueOf(0);
    private BigDecimal totalCommission = BigDecimal.valueOf(0);
    private BigDecimal totalNetWin = BigDecimal.valueOf(0);
    private BigDecimal totalPureNetWin = BigDecimal.valueOf(0);
    private int items = 0;
}
