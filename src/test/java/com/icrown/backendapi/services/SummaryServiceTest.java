package com.icrown.backendapi.services;

import com.icrown.gameapi.commons.utils.DateUtil;
import com.icrown.gameapi.daos.GameReportDAO;
import com.icrown.gameapi.daos.LogGameTicketDAO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;

import java.util.*;

import org.mockito.Mockito;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.util.AssertionErrors.fail;
@SpringBootTest(classes = SummaryServiceTest.class)
@ComponentScan(basePackages = "com.icrown")
@EnableAutoConfiguration
public class SummaryServiceTest {
    @Autowired
    SummaryService summaryService;
    @MockBean
    LogGameTicketDAO logGameTicketDAO;
    @MockBean
    GameReportDAO gameReportDAO;
    @Autowired
    DateUtil dateUtil;

    @Test
    public void summary(){
        String agentGuid = "agentGuid";
        int level = 1;
        Date now = new Date();
        Date startTime = dateUtil.addTime(now, Calendar.DATE, -1);;
        startTime = dateUtil.getDateWithFormat(startTime, "yyyy-MM-dd 00:00:00");
        Date endTime = dateUtil.addTime(startTime, Calendar.DATE, 2);
        when(logGameTicketDAO.onlineCount(agentGuid, level)).thenReturn(1);
        var resultSetMock = Mockito.mock(SqlRowSet.class);
        Mockito.when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        when(resultSetMock.getDate("AGG_Time")).thenReturn(new java.sql.Date( startTime.getTime()));
        when(gameReportDAO.getTodayBetsListByHour(agentGuid, level, startTime, endTime)).thenReturn(resultSetMock);

        var response = summaryService.summary(agentGuid, level, startTime, endTime, now);
        assertTrue(response.getOnlineCount() == 1);
    }
}
