package com.icrown.backendapi.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

/**
 * @author Frank
 */
public class DailyTradeListRequest {
    private String accountID;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private Date startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private Date endDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date currentDate;
    private int pageIndex;
    private int pageSize;
    private List<Integer> queryType;

    @JsonCreator
    public DailyTradeListRequest(
            @JsonProperty(value = "accountID", required = true) String accountID,
            @JsonProperty(value = "startDate", required = true) Date startDate,
            @JsonProperty(value = "endDate", required = true) Date endDate,
            @JsonProperty(value = "currentDate", required = true, defaultValue = "") Date currentDate,
            @JsonProperty(value = "pageIndex", required = true) int pageIndex,
            @JsonProperty(value = "pageSize", required = true) int pageSize,
            @JsonProperty(value = "queryType", required = true) List<Integer> queryType
                                ) {
        this.accountID = accountID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.currentDate = currentDate;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.queryType = queryType;
        this.currentDate = currentDate;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
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

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
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

    public List<Integer> getQueryType() {
        return queryType;
    }

    public void setQueryType(List<Integer> queryType) {
        this.queryType = queryType;
    }
}
