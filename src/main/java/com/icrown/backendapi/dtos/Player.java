package com.icrown.backendapi.dtos;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author adi
 */
public class Player {

    public String getPlayerGuid() {
        return playerGuid;
    }

    public void setPlayerGuid(String playerGuid) {
        this.playerGuid = playerGuid;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public BigDecimal getPoint() {
        return point;
    }

    public void setPoint(BigDecimal point) {
        this.point = point;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }


    public String getLocker() {
        return locker;
    }

    public void setLocker(String locker) {
        this.locker = locker;
    }

    public String getLockerAccountID() {
        return lockerAccountID;
    }

    public void setLockerAccountID(String lockerAccountID) {
        this.lockerAccountID = lockerAccountID;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
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

    private String playerGuid;
    private String accountID;
    private BigDecimal point;
    private String nickName;
    private boolean isLock;
    private String locker;
    private String lockerAccountID;
    private String currency;
    private List<String> agentTree;
}
