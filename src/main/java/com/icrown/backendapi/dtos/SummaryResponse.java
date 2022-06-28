package com.icrown.backendapi.dtos;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Adi
 */
public class SummaryResponse {


    public BigDecimal getTotalBets() {
        return totalBets;
    }

    public void setTotalBets(BigDecimal totalBets) {
        this.totalBets = totalBets;
    }

    public BigDecimal getTotalNetWin() {
        return totalNetWin;
    }

    public void setTotalNetWin(BigDecimal totalNetWin) {
        this.totalNetWin = totalNetWin;
    }

    public int getOnlineCount() {
        return onlineCount;
    }

    public void setOnlineCount(int onlineCount) {
        this.onlineCount = onlineCount;
    }


    public List<Bets> getBetsList1() {
        return betsList1;
    }

    public void setBetsList1(List<Bets> betsList1) {
        this.betsList1 = betsList1;
    }

    public List<Bets> getResultList() {
        return resultList;
    }

    public void setResultList(List<Bets> resultList) {
        this.resultList = resultList;
    }

    public List<Bets> getBetsList2() {
        return betsList2;
    }

    public void setBetsList2(List<Bets> betsList2) {
        this.betsList2 = betsList2;
    }


    private List<Bets> betsList1;
    private List<Bets> betsList2;
    private List<Bets> resultList;

    private BigDecimal totalBets;
    private BigDecimal totalNetWin;
    private int onlineCount;

}
