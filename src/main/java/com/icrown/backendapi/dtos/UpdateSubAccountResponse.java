package com.icrown.backendapi.dtos;

import java.util.List;

public class UpdateSubAccountResponse {
    private String nickName;
    private String memo;
    private List<String> acl;

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

    public List<String> getAcl() {
        return acl;
    }

    public void setAcl(List<String> acl) {
        this.acl = acl;
    }
}
