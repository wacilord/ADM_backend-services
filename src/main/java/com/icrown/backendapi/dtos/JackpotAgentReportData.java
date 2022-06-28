package com.icrown.backendapi.dtos;

import java.math.BigDecimal;

/**
 * @author Tetsu
 */
public class JackpotAgentReportData {
    private String accountID;
    private String currency;
    private BigDecimal Mini = BigDecimal.ZERO;
    private BigDecimal Minor = BigDecimal.ZERO;
    private BigDecimal Major = BigDecimal.ZERO;
    private BigDecimal Grand = BigDecimal.ZERO;

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getMini() {
        return Mini;
    }

    public void setMini(BigDecimal mini) {
        Mini = mini;
    }

    public BigDecimal getMinor() {
        return Minor;
    }

    public void setMinor(BigDecimal minor) {
        Minor = minor;
    }

    public BigDecimal getMajor() {
        return Major;
    }

    public void setMajor(BigDecimal major) {
        Major = major;
    }

    public BigDecimal getGrand() {
        return Grand;
    }

    public void setGrand(BigDecimal grand) {
        Grand = grand;
    }

    @Override
    public String toString() {
        return "JackpotAgentReportData{" +
                "accountID='" + accountID + '\'' +
                ", currency='" + currency + '\'' +
                ", Mini=" + Mini +
                ", Minor=" + Minor +
                ", Major=" + Major +
                ", Grand=" + Grand +
                '}';
    }
}
