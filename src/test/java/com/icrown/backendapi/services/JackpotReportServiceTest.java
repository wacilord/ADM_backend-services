package com.icrown.backendapi.services;

import com.icrown.common.services.GameCommonService;
import com.icrown.gameapi.commons.utils.APIException;
import com.icrown.gameapi.daos.AgentDAO;
import com.icrown.gameapi.daos.JackpotReportDAO;
import com.icrown.gameapi.models.AgentModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = JackpotReportServiceTest.class)
@ComponentScan(basePackages = "com.icrown")
@EnableAutoConfiguration
class JackpotReportServiceTest {

    @Autowired
    JackpotReportService jackpotReportService;
    @MockBean
    JackpotReportDAO jackpotReportDAO;
    @MockBean
    AgentService agentService;
    @MockBean
    GameCommonService gameCommonService;
    @MockBean
    AgentDAO agentDAO;

    @Test
    void getJackpotAgentReport() {
        String agtAccountID = "agtAccountID";
        String selfAccountID = "selfAccountID";
        String selfAguGuid = "selfAguGuid";
        String domain = "domain";
        int selflevel = 1;
        Date startDate = new Date();
        Date endTime = new Date();
        BigDecimal jp = BigDecimal.valueOf(10L);
        AgentModel agentModel = new AgentModel();
        agentModel.setAGT_Level(3);
        agentModel.setAGT_GUID("agtGuid");
        agentModel.setAGT_AccountID(agtAccountID);
        SqlRowSet rowSet = mock(SqlRowSet.class);
        when(rowSet.getString("AGT_Agent3")).thenReturn("agtGuid");
        when(jackpotReportDAO.getJackpotAgent1Report(selfAguGuid, startDate, endTime)).thenReturn(rowSet);
        when(jackpotReportDAO.getJackpotAgent2Report(selfAguGuid, startDate, endTime)).thenReturn(rowSet);
        when(jackpotReportDAO.getJackpotAgent3Report(selfAguGuid, startDate, endTime)).thenReturn(rowSet);
        when(jackpotReportDAO.getJackpotAgent2ReportByAgent1(selfAguGuid, startDate, endTime)).thenReturn(rowSet);
        when(jackpotReportDAO.getJackpotAgent3ReportByAgent2(selfAguGuid, startDate, endTime)).thenReturn(rowSet);

        when(agentDAO.getAgentByDomain(domain)).thenReturn(Optional.empty());
        String finalAgtAccountID = agtAccountID;
        int finalSelflevel = selflevel;
        assertThrows(APIException.class, () -> jackpotReportService.getJackpotAgentReport(finalAgtAccountID, selfAccountID, selfAguGuid, domain, finalSelflevel, startDate, endTime));
        when(agentDAO.getAgentByDomain(domain)).thenReturn(Optional.of(Arrays.asList(agentModel)));
        assertThrows(APIException.class, () -> jackpotReportService.getJackpotAgentReport(finalAgtAccountID, selfAccountID, selfAguGuid, domain, finalSelflevel, startDate, endTime));

        agtAccountID = "";
        assertTrue(jackpotReportService.getJackpotAgentReport(agtAccountID, selfAccountID, selfAguGuid, domain, selflevel, startDate, endTime).getAgentLevel()
                == 1);
        selflevel = 2;
        assertTrue(jackpotReportService.getJackpotAgentReport(agtAccountID, selfAccountID, selfAguGuid, domain, selflevel, startDate, endTime).getAgentLevel()
                == 2);
        selflevel = 3;
        assertTrue(jackpotReportService.getJackpotAgentReport(agtAccountID, selfAccountID, selfAguGuid, domain, selflevel, startDate, endTime).getAgentLevel()
                == 3);

        when(jackpotReportDAO.getJackpotAgent1Report(agentModel.getAGT_GUID(), startDate, endTime)).thenReturn(rowSet);
        when(jackpotReportDAO.getJackpotAgent2Report(agentModel.getAGT_GUID(), startDate, endTime)).thenReturn(rowSet);
        when(jackpotReportDAO.getJackpotAgent3Report(agentModel.getAGT_GUID(), startDate, endTime)).thenReturn(rowSet);
        when(jackpotReportDAO.getJackpotAgent2ReportByAgent1(agentModel.getAGT_GUID(), startDate, endTime)).thenReturn(rowSet);
        when(jackpotReportDAO.getJackpotAgent3ReportByAgent2(agentModel.getAGT_GUID(), startDate, endTime)).thenReturn(rowSet);
        agtAccountID = "agtAccountID";
        when(rowSet.next()).thenReturn(false);
        agentModel.setAGT_Level(1);
        assertTrue(jackpotReportService.getJackpotAgentReport(agtAccountID, selfAccountID, selfAguGuid, domain, selflevel, startDate, endTime).getAgentLevel()
                == 2);
        agentModel.setAGT_Level(2);
        assertTrue(jackpotReportService.getJackpotAgentReport(agtAccountID, selfAccountID, selfAguGuid, domain, selflevel, startDate, endTime).getAgentLevel()
                == 3);
        var resultList =  jackpotReportService.getJackpotAgentReport(agtAccountID, selfAccountID, selfAguGuid, domain, selflevel, startDate, endTime).getJackpotRecord();
        var resultOp = resultList.stream().findFirst();
        assertTrue(resultOp.isEmpty());

        when(rowSet.next()).thenReturn(true).thenReturn(false);
        when(rowSet.getString("AGT_AccountID")).thenReturn(agtAccountID);
        when(rowSet.getString("DT_Currency")).thenReturn("currency");
        when(rowSet.getBigDecimal("GM_Jackpot")).thenReturn(jp);
        when(rowSet.getString("GM_JackpotPoolType")).thenReturn("Mini");

         resultList =  jackpotReportService.getJackpotAgentReport(agtAccountID, selfAccountID, selfAguGuid, domain, selflevel, startDate, endTime).getJackpotRecord();
         resultOp = resultList.stream().findFirst();
        assertTrue(resultOp.isPresent());
        var result = resultOp.get();
        assertTrue(result.getMini().compareTo(jp) == 0);
        assertFalse(result.getMinor().compareTo(jp) == 0);

        when(rowSet.next()).thenReturn(true).thenReturn(false);
        when(rowSet.getString("GM_JackpotPoolType")).thenReturn("Grand");
        resultOp = jackpotReportService.getJackpotAgentReport(agtAccountID, selfAccountID, selfAguGuid, domain, selflevel, startDate, endTime).getJackpotRecord()
                .stream().findFirst();
        assertTrue(resultOp.isPresent());
        result = resultOp.get();
        assertTrue(result.getGrand().compareTo(jp) == 0);
        assertFalse(result.getMini().compareTo(jp) == 0);


        agtAccountID = "";
        selflevel = 1;
        when(jackpotReportDAO.getJackpotAgent1Report(selfAguGuid, startDate, endTime)).thenReturn(rowSet);
        when(rowSet.getString("GM_JackpotPoolType")).thenReturn("Grand");
        when(rowSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(rowSet.getBigDecimal("GM_Jackpot")).thenReturn(BigDecimal.valueOf(10L)).thenReturn(BigDecimal.valueOf(10L));
        resultList =  jackpotReportService.getJackpotAgentReport(agtAccountID, selfAccountID, selfAguGuid, domain, selflevel, startDate, endTime).getJackpotRecord();
                assertTrue(resultOp.isPresent());
        var result1 = resultList.get(0);
        assertTrue(result1.getGrand().compareTo(jp.add(jp)) == 0);
        assertTrue(resultList.size() == 1);
        assertFalse(resultList.size() == 2);
    }

}