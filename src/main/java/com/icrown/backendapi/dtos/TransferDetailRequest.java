package com.icrown.backendapi.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Frank
 */
public class TransferDetailRequest {
    private String transferID;

    @JsonCreator
    public TransferDetailRequest(
            @JsonProperty(value = "transferID", required = true) String transferID
    ) {
        this.transferID = transferID;
    }

    public String getTransferID() {
        return transferID;
    }

    public void setTransferID(String transferID) {
        this.transferID = transferID;
    }
}
