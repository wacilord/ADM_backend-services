package com.icrown.backendapi.dtos;

import io.lettuce.core.protocol.CommandHandler;

import java.util.List;

/**
 * @author dennis
 */
public class JackpotPlayerReportResponse {
    private List<JackpotPlayerRecord> jackpotRecord;

    public List<JackpotPlayerRecord> getJackpotRecord() {
        return jackpotRecord;
    }

    public void setJackpotRecord(List<JackpotPlayerRecord> jackpotRecord) {
        this.jackpotRecord = jackpotRecord;
    }
}
