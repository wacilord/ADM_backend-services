package com.icrown.backendapi.dtos;

import java.util.List;

/**
 * @author Tetsu
 */
public class JackpotAgentReportResponse {
    private List<JackpotAgentReportData> jackpotRecord;
    private int agentLevel;

    public List<JackpotAgentReportData> getJackpotRecord() {
        return jackpotRecord;
    }

    public void setJackpotRecord(List<JackpotAgentReportData> jackpotRecord) {
        this.jackpotRecord = jackpotRecord;
    }

    public int getAgentLevel() {
        return agentLevel;
    }

    public void setAgentLevel(int agentLevel) {
        this.agentLevel = agentLevel;
    }

    @Override
    public String toString() {
        return "JackpotAgentReportResponse{" +
                "jackpotRecord=" + jackpotRecord +
                ", agentLevel=" + agentLevel +
                '}';
    }
}
