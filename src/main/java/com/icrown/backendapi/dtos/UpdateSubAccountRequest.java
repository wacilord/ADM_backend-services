package com.icrown.backendapi.dtos;

import java.util.List;

public class UpdateSubAccountRequest {

    private String satGuid;
    private String nickName;
    private String memo;
    private List<String> funCodes;

    public String getSatGuid() {
        return satGuid;
    }

    public void setSatGuid(String satGuid) {
        this.satGuid = satGuid;
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
}
