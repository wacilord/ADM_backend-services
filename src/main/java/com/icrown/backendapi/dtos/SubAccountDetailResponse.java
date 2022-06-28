package com.icrown.backendapi.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SubAccountDetailResponse {
    public String satGuid;
    public String accountID;
    public String nickName;
    public String memo;
    public List<String> funCodes;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDatetime;

    public SubAccountDetailResponse()
    {
        funCodes = new ArrayList<String>();
    }

    public String getSatGuid() {
        return satGuid;
    }

    public void setSatGuid(String satGuid) {
        this.satGuid = satGuid;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public List<String> getFunCodes() {
        return funCodes;
    }

    public void setFunCodes(List<String> funCodes) {
        this.funCodes = funCodes;
    }

    public Date getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(Date createDatetime) {
        this.createDatetime = createDatetime;
    }
}
