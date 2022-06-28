package com.icrown.backendapi.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

public class SummaryReportRequest {
    private List<Integer> gameType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH")
    private Date startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH")
    private Date endDate;
    private int pageIndex;
    private int pageSize;
    private String agent;
    private String domain;

    @JsonCreator
    public SummaryReportRequest(
            @JsonProperty(value = "gameType", required = true) List<Integer> gameType,
            @JsonProperty(value = "startDate", required = true) Date startDate,
            @JsonProperty(value = "endDate", required = true) Date endDate,
            @JsonProperty(value = "pageIndex", required = true) int pageIndex,
            @JsonProperty(value = "pageSize", required = true) int pageSize
    ) {
        this.gameType = gameType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public List<Integer> getGameType() {
        return gameType;
    }

    public void setGameType(List<Integer> gameType) {
        this.gameType = gameType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
