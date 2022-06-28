package com.icrown.backendapi.services;

import com.icrown.common.services.AgentCommonService;
import com.icrown.gameapi.commons.utils.APIException;
import com.icrown.gameapi.daos.AgentDAO;
import com.icrown.gameapi.daos.SessionDAO;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.fail;
@SpringBootTest(classes = SessionServiceTest.class)
@ComponentScan(basePackages = "com.icrown")
@EnableAutoConfiguration
public class SessionServiceTest {
    @Autowired
    SessionService sessionService;
    @MockBean
    SessionDAO sessionDAO;
    @Test
    public void getSessionByToken(){
        String token = "token";
        SessionModel model = new SessionModel();
        model.setSAT_GUID("satGuid");
        when(sessionDAO.getSessionByToken(token)).thenReturn(Optional.of(model));

        var returnData = sessionService.getSessionByToken(token);
        assertTrue(returnData.getSAT_GUID().equals(model.getSAT_GUID()));

        when(sessionDAO.getSessionByToken(token)).thenReturn(Optional.empty());
        assertThrows(APIException.class, ()->{sessionService.getSessionByToken(token);});

    }

    @Test
    public void expiredSessionByToken(){
        String token = "token";
        Date date = new Date();
        when(sessionDAO.expiredSessionByToken(token, date)).thenReturn(false);
        assertThrows(APIException.class, () -> sessionService.expiredSessionByToken(token, date));
    }
}
