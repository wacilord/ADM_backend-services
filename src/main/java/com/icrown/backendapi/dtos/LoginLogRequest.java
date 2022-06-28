package com.icrown.backendapi.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginLogRequest {
    private int pageIndex;
    private int pageSize;

    @JsonCreator
    public LoginLogRequest(@JsonProperty(value = "pageIndex", required = true) int pageIndex,
    @JsonProperty(value = "pageSize", required = true) int pageSize){
        this.pageIndex = pageIndex;
        this.pageSize =pageSize;
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
}
