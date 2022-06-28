package com.icrown.backendapi.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Frank
 */
public class PlayerDetailUserInfoResponse {
    private String accountID;
    private boolean isOnline;
    private String[] orgTree;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastOnlineTime;
    private BigDecimal point;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date pointUpdateTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDateTime;
    private int createTotalDay;
    private BigDecimal historyBets;
    private BigDecimal historyNetWin;

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String[] getOrgTree() {
        return orgTree;
    }

    public void setOrgTree(String[] orgTree) {
        this.orgTree = orgTree;
    }

    public Date getLastOnlineTime() {
        return lastOnlineTime;
    }

    public void setLastOnlineTime(Date lastOnlineTime) {
        this.lastOnlineTime = lastOnlineTime;
    }

    public BigDecimal getPoint() {
        return point;
    }

    public void setPoint(BigDecimal point) {
        this.point = point;
    }

    public Date getPointUpdateTime() {
        return pointUpdateTime;
    }

    public void setPointUpdateTime(Date pointUpdateTime) {
        this.pointUpdateTime = pointUpdateTime;
    }

    public Date getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(Date createDateTime) {
        this.createDateTime = createDateTime;
    }

    public int getCreateTotalDay() {
        return createTotalDay;
    }

    public void setCreateTotalDay(int createTotalDay) {
        this.createTotalDay = createTotalDay;
    }

    public BigDecimal getHistoryBets() {
        return historyBets;
    }

    public void setHistoryBets(BigDecimal historyBets) {
        this.historyBets = historyBets;
    }

    public BigDecimal getHistoryNetWin() {
        return historyNetWin;
    }

    public void setHistoryNetWin(BigDecimal historyNetWin) {
        this.historyNetWin = historyNetWin;
    }
}
