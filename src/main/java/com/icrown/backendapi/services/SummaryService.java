package com.icrown.backendapi.services;

import com.icrown.backendapi.dtos.Bets;
import com.icrown.backendapi.dtos.SummaryResponse;
import com.icrown.gameapi.commons.utils.DateUtil;
import com.icrown.gameapi.daos.GameReportDAO;
import com.icrown.gameapi.daos.LogGameTicketDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Adi
 */
@Service
public class SummaryService {

    @Autowired
    GameReportDAO gameReportDAO;

    @Autowired
    LogGameTicketDAO logGameTicketDAO;

    @Autowired
    DateUtil dateUtil;

    public SummaryResponse summary(String agtGuid, int level, Date startTime, Date endTime, Date now) {

        BigDecimal betsTotal = BigDecimal.valueOf(0);
        BigDecimal resultTotal = BigDecimal.valueOf(0);
        int onlineCount = logGameTicketDAO.onlineCount(agtGuid, level);


        List<Bets> betsList = new ArrayList<>(48);
        List<Bets> resultList = new ArrayList<>(24);

        for (int i = 0; i < 48; i++) {
            Bets bet = new Bets();
            bet.setDate(dateUtil.addTime(startTime, Calendar.HOUR, i));
            //只到目前時間為止有預設值
            if(bet.getDate().compareTo(now) <= 0) {
                bet.setPoint(BigDecimal.valueOf(0));
            }
            betsList.add(bet);

            if (i < 24) {
                continue;
            }
            Bets result = new Bets();
            result.setDate(dateUtil.addTime(startTime, Calendar.HOUR, i));
            //只到目前時間為止有預設值
            if(result.getDate().compareTo(now) <= 0) {
                result.setPoint(BigDecimal.valueOf(0));
            }
            resultList.add(result);
        }

        SqlRowSet rs = gameReportDAO.getTodayBetsListByHour(agtGuid, level, startTime, endTime);
        while (rs.next()) {
            Date aggTime = rs.getDate("AGG_Time");

            Bets bet = betsList.stream().filter(o -> o.getDate().getTime() == aggTime.getTime()).findFirst().get();
            bet.setPoint(rs.getBigDecimal("sumbets"));

            if (resultList.stream().filter(o -> o.getDate().getTime() == aggTime.getTime()).count() == 0) {
                continue;
            }
            Bets result = resultList.stream().filter(o -> o.getDate().getTime() == aggTime.getTime()).collect(Collectors.toList()).get(0);
            result.setPoint(rs.getBigDecimal("sumNetWin"));
            resultTotal = resultTotal.add(rs.getBigDecimal("sumNetWin"));
            betsTotal = betsTotal.add(rs.getBigDecimal("sumbets"));
        }
        SummaryResponse response = new SummaryResponse();
        response.setTotalBets(betsTotal);
        response.setTotalNetWin(resultTotal);
        response.setBetsList1(betsList.subList(0, 24));
        response.setBetsList2(betsList.subList(24, 48));
        response.setResultList(resultList);
        response.setOnlineCount(onlineCount);
        return response;
    }

}
