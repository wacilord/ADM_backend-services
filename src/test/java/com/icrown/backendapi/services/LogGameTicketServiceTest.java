package com.icrown.backendapi.services;

import com.icrown.common.services.AgentCommonService;
import com.icrown.gameapi.daos.AgentDAO;
import com.icrown.gameapi.daos.LogGameTicketDAO;
import com.icrown.gameapi.models.AGT_MerchantModel;
import com.icrown.gameapi.models.AgentModel;
import com.icrown.gameapi.models.SessionModel;
import com.icrown.gameapi.models.SubAccountModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.fail;
@SpringBootTest(classes = LogGameTicketServiceTest.class)
@ComponentScan(basePackages = "com.icrown")
@EnableAutoConfiguration
public class LogGameTicketServiceTest {
    @Autowired
    LogGameTicketService logGameTicketService;
    @MockBean
    LogGameTicketDAO logGameTicketDAO;

    @Test
    public void getLastOnlineTime(){
        String plyGuid = "plyGuid";
        Optional<Date> opt = Optional.of(new Date());
        when(logGameTicketDAO.lastOnlineTime(plyGuid)).thenReturn(opt);
        var returnOpt = logGameTicketService.getLastOnlineTime(plyGuid);
        assertTrue(returnOpt.isEmpty() == false);

    }

    @Test
    public void getPlayerIsOnline(){
        String plyGuid = "plyGuid";
        boolean result = true;
        when(logGameTicketDAO.playerIsOnline(plyGuid)).thenReturn(result);
        var returnResult = logGameTicketService.getPlayerIsOnline(plyGuid);
        assertTrue(returnResult == result);
    }

}
