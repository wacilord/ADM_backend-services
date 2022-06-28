package com.icrown.backendapi.services;

import com.icrown.common.services.AgentCommonService;
import com.icrown.gameapi.daos.AgentDAO;
import com.icrown.gameapi.daos.LoginLogDAO;
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
import org.mockito.Mockito;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.fail;
@SpringBootTest(classes = LoginLogServiceTest.class)
@ComponentScan(basePackages = "com.icrown")
@EnableAutoConfiguration
public class LoginLogServiceTest {
    @Autowired
    LoginLogService loginLogService;
    @MockBean
    LoginLogDAO loginLogDAO;
    @Test
    public void getLoginLog(){
        when(loginLogDAO.getTotalCount(Mockito.anyString())).thenReturn(1);
        var resultSetMock = Mockito.mock(SqlRowSet.class);
        Mockito.when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        when(loginLogDAO.getLoginLogList(Mockito.anyString(),Mockito.anyInt(),Mockito.anyInt())).thenReturn(resultSetMock);
        var response = loginLogService.getLoginLog("guid",1,10);
        assertTrue(response.getRecords().size() == 1);
    }

}
