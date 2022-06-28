package com.icrown.backendapi.dtos;

import java.util.List;

public class LoginResponse {
    private String token;
    private boolean isFirstLogin;
    private String accountID;
    private int type;
    private int agentLevel;
    private List<Integer> gameTypes;
    private String language;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public boolean isFirstLogin() {
        return isFirstLogin;
    }

    public void setFirstLogin(boolean firstLogin) {
        isFirstLogin = firstLogin;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getAgentLevel() {
        return agentLevel;
    }

    public void setAgentLevel(int agentLevel) {
        this.agentLevel = agentLevel;
    }

    public List<Integer> getGameTypes() {
        return gameTypes;
    }

    public void setGameTypes(List<Integer> gameTypes) {
        this.gameTypes = gameTypes;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='" + token + '\'' +
                ", isFirstLogin=" + isFirstLogin +
                ", accountID='" + accountID + '\'' +
                ", type=" + type +
                ", agentLevel=" + agentLevel +
                ", gameTypes=" + gameTypes +
                ", language='" + language + '\'' +
                '}';
    }
}
