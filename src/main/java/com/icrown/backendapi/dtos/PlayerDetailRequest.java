package com.icrown.backendapi.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Frank
 */
public class PlayerDetailRequest {
    private String accountID;
    private String playerGuid;

    @JsonCreator
    public PlayerDetailRequest(
            @JsonProperty(value = "accountID", required = true) String accountID,
            @JsonProperty(value = "playerGuid", required = true) String playerGuid
                              ) {
        this.accountID = accountID;
        this.playerGuid = playerGuid;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getPlayerGuid() {
        return playerGuid;
    }

    public void setPlayerGuid(String playerGuid) {
        this.playerGuid = playerGuid;
    }
}
