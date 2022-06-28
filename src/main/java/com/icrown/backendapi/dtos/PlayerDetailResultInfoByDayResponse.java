package com.icrown.backendapi.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Frank
 */
public class PlayerDetailResultInfoByDayResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;
    private BigDecimal netWin;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getNetWin() {
        return netWin;
    }

    public void setNetWin(BigDecimal netWin) {
        this.netWin = netWin;
    }
}
