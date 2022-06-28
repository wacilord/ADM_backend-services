package com.icrown.backendapi.dtos;

public class Agent {


    public String getAgtGuid() {
        return agtGuid;
    }

    public void setAgtGuid(String agtGuid) {
        this.agtGuid = agtGuid;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }


    public void setLocker(String locker) {
        this.locker = locker;
    }


    public String getLocker() {
        return locker;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }


    private String agtGuid;
    private String currency;
    private String nickName;
    private int level;
    private boolean isLock;
    private String accountID;
    private String locker;
}
