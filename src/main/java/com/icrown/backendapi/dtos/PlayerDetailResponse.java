package com.icrown.backendapi.dtos;

import java.util.List;

public class PlayerDetailResponse {
    private PlayerDetailUserInfoResponse userInfo;
    private List<PlayerDetailResultInfoByGameResponse> resultInfoByGame;
    private List<PlayerDetailResultInfoByDayResponse> resultInfoByDay;
    private List<PlayerDetailBetsInfoResponse> betsInfo;

    public PlayerDetailUserInfoResponse getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(PlayerDetailUserInfoResponse userInfo) {
        this.userInfo = userInfo;
    }

    public List<PlayerDetailResultInfoByGameResponse> getResultInfoByGame() {
        return resultInfoByGame;
    }

    public void setResultInfoByGame(List<PlayerDetailResultInfoByGameResponse> resultInfoByGame) {
        this.resultInfoByGame = resultInfoByGame;
    }

    public List<PlayerDetailResultInfoByDayResponse> getResultInfoByDay() {
        return resultInfoByDay;
    }

    public void setResultInfoByDay(List<PlayerDetailResultInfoByDayResponse> resultInfoByDay) {
        this.resultInfoByDay = resultInfoByDay;
    }

    public List<PlayerDetailBetsInfoResponse> getBetsInfo() {
        return betsInfo;
    }

    public void setBetsInfo(List<PlayerDetailBetsInfoResponse> betsInfo) {
        this.betsInfo = betsInfo;
    }
}
