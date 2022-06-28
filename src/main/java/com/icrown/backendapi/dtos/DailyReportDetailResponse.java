package com.icrown.backendapi.dtos;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Frank
 */
public class DailyReportDetailResponse {
    private Date accountDate;
    private String currency;
    private BigDecimal sumBets;
    private BigDecimal sumValidBets;
    private BigDecimal sumWin;
    private BigDecimal sumJackpotContribute;
    private BigDecimal sumJackpot;
    private BigDecimal sumJackpot2;
    private BigDecimal sumJackpot3;
    private BigDecimal commission;
    private BigDecimal sumNetWin;
    private BigDecimal sumPureNetWin;
    private int items;

    public Date getAccountDate() {
        return accountDate;
    }

    public void setAccountDate(Date accountDate) {
        this.accountDate = accountDate;
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

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
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
}
