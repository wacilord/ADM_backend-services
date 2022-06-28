package com.icrown.backendapi.services;

import com.icrown.backendapi.dtos.*;
import com.icrown.common.services.AgentCommonService;
import com.icrown.common.services.AgentGameRelationCommonService;
import com.icrown.common.services.TranslateCommonService;
import com.icrown.gameapi.commons.responses.ResponseCode;
import com.icrown.gameapi.commons.utils.APIException;
import com.icrown.gameapi.commons.utils.DateUtil;
import com.icrown.gameapi.daos.*;
import com.icrown.gameapi.models.AgentModel;
import com.icrown.gameapi.models.SubAccountModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;

import java.util.*;

import org.mockito.Mockito;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.util.AssertionErrors.fail;

@SpringBootTest(classes = SubAccountServiceTest.class)
@ComponentScan(basePackages = "com.icrown")
@EnableAutoConfiguration
public class SubAccountServiceTest {
    @Autowired
    SubAccountService subAccountService;
    @Autowired
    DateUtil dateUtil;
    @MockBean
    SubAccountDAO subAccountDAO;
    @MockBean
    FunctionRelationDAO functionRelationDAO;
    @MockBean
    AgentGameRelationCommonService agentGameRelationCommonService;
    @MockBean
    AgentCommonService agentCommonService;
    @MockBean
    LogGameTicketDAO logGameTicketDAO;
    @MockBean
    AgentService agentService;
    @MockBean
    GameReportDayDAO gameReportDayDAO;
    @MockBean
    AgentDAO agentDAO;
    @MockBean
    TranslateCommonService translateService;

    @Test
    public void getSubAccountByGuid() {
        String satGuid = "satGuid";
        SubAccountModel model = new SubAccountModel();
        model.setSAT_GUID(satGuid);
        Optional<SubAccountModel> optModel = Optional.of(model);
        when(subAccountDAO.getSubAccountByGuid(satGuid)).thenReturn(optModel);

        var returnModel = subAccountService.getSubAccountByGuid(satGuid);
        assertTrue(returnModel.getSAT_GUID().equals(model.getSAT_GUID()));
    }

    @Test
    public void updateSelfPassword() {
        SubAccountModel model = new SubAccountModel();
        String passwordEry = subAccountService.encryptPassword("1234");
        model.setSAT_Password(passwordEry);
        model.setAGT_GUID("X1001");
        model.setSAT_AccountID("Test001");
        Optional<SubAccountModel> optSubAccount = Optional.of(model);
        when(subAccountDAO.updatePassword(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        when(subAccountDAO.getSubAccountByGuid(Mockito.anyString())).thenReturn(optSubAccount);
        subAccountService.updateSelfPassword("X1001", "1234", "123456Ab");
        assertTrue(true);
    }

    @Test
    public void login() {
        Date now = new Date();
        Map<String, Object> map = new HashMap<>();
        map.put("AGT_GUID", "agtGuid");
        map.put("MCT_BackendAllowIP", "127.0.0.1");
        map.put("AGT_Lock", true);
        map.put("SAT_Enable", false);
        map.put("SAT_RegainDatetime", dateUtil.addTime(now, Calendar.SECOND, -1));
        map.put("SAT_Password", subAccountService.encryptPassword("1111111"));
        map.put("AGT_Level", 3);
        map.put("SAT_GUID", "satGuid");
        map.put("SAT_Type", 1);
        map.put("SAT_IsFirstLogin", false);
        map.put("SAT_ErrorCount", 2);
        map.put("MCT_Guid", "mctGuid");
        map.put("MCT_Enable", false);

        Mockito.doNothing().when(subAccountDAO).loginLog("accountID", "domain", "ipAddress", "agtGuid", "clientType", ResponseCode.BACKENDAPI_ACCOUNT_NOT_EXISTS.getStatusCode(), "");
        //Mockito.doNothing().when(subAccountDAO).loginError("accountID", "domain", "ipAddress",new Date(), dateUtil.addTime(now, Calendar.SECOND, 120));

        //查詢帳號為空
        when(subAccountDAO.login(Mockito.anyString(), Mockito.anyString())).thenReturn(new HashMap<String, Object>());
        assertThrows(APIException.class, () -> {
            subAccountService.login("accountID", "123456", "domain", "127.0.0.1", "clientType", "zh-CN");
        }, "account not exists");
        when(subAccountDAO.login(Mockito.anyString(), Mockito.anyString())).thenReturn(map);

        //"MCT_Enable"為false
        assertThrows(APIException.class, () -> {
            subAccountService.login("accountID", "123456", "domain", "127.0.0.1", "clientType", "zh-CN");
        }, ResponseCode.BACKENDAPI_MERCHANT_IS_CLOSE.getErrorMessage());
        map.put("MCT_Enable", true);

        //MCT_BackendAllowIP有值但不符
        map.put("MCT_BackendAllowIP", "111,111,111,111");
        assertThrows(APIException.class, () -> {
            subAccountService.login("accountID", "123456", "domain", "127.0.0.1", "clientType", "zh-CN");
        }, "127.0.0.1");
        map.put("MCT_BackendAllowIP", "");

        assertThrows(APIException.class, () -> {
            subAccountService.login("accountID", "123456", "domain", "127.0.0.1", "clientType", "zh-CN");
        });
        map.put("AGT_Lock", false);

        assertThrows(APIException.class, () -> {
            subAccountService.login("accountID", "123456", "domain", "127.0.0.1", "clientType", "zh-CN");
        });
        map.put("SAT_Enable", true);

        assertThrows(APIException.class, () -> {
            subAccountService.login("accountID", "123456", "domain", "127.0.0.1", "clientType", "zh-CN");
        });
        map.put("SAT_RegainDatetime", now);

        assertThrows(APIException.class, () -> {
            subAccountService.login("accountID", "123456", "domain", "127.0.0.1", "clientType", "zh-CN");
        });
        map.put("SAT_ErrorCount", 0);
        map.put("SAT_Password", subAccountService.encryptPassword("123456"));
        map.put("LAG_Code", "LAG_Code");

        when(agentGameRelationCommonService.getEnableGameTypeByMctGuid(Mockito.anyString())).thenReturn(Arrays.asList(1, 2, 3));
        Mockito.doNothing().when(subAccountDAO).loginOk("satGuid", "token", new Date(), "accountID", "domain", "ipaddress", new Date());
        Mockito.doNothing().when(subAccountDAO).loginLog("accountID", "domain", "ipAddress", "agtGuid", "clientType", 200, "");

        AgentModel agentModel = new AgentModel();
        agentModel.setAGT_Agent1("agent1");
        when(agentCommonService.getAgentInfoByAgentGUID(Mockito.anyString())).thenReturn(agentModel);
        List<Integer> gameType1 = List.of(3);
        when(logGameTicketDAO.getGameTypeByTimeRangeAndAgent1(Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(gameType1);

        var resultSetMock = Mockito.mock(SqlRowSet.class);
        Mockito.when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        when(resultSetMock.getString("AGT_Agent3")).thenReturn("abc");
        when(agentDAO.getAgent3ListByAgentGuidAndLevel(anyString(), anyInt())).thenReturn(resultSetMock);
        when(gameReportDayDAO.get2MonthGameTypeByAgent3List(Arrays.asList("abc"))).thenReturn(gameType1);
        Map tmpMap = new HashMap();
        when(resultSetMock.getString("FUN_Name")).thenReturn("funName");
        when(translateService.getTranslationMap(anyString())).thenReturn(tmpMap);
        //when(translateService.fuzzyTranslationByTranlationMap(tmpMap, anyString())).thenReturn("funName");
        var response = subAccountService.login("accountID", "123456", "domain", "127.0.0.1", "clientType", "zh-CN");
        assertTrue(!response.getToken().isEmpty());
        assertTrue(response.getGameTypes().containsAll(List.of(1, 2, 3)));
    }

    @Test
    public void logout() {
        Mockito.doNothing().when(subAccountDAO).logout(Mockito.anyString());
        subAccountService.logout("token");
    }

    @Test
    public void getSubAccountDetailByGuid() {
        SubAccountModel model = new SubAccountModel();
        model.setAGT_GUID("agtGuid");
        model.setSAT_GUID("satGuid");
        model.setSAT_AccountID("accountID");
        model.setSAT_NickName("nickName");
        model.setSAT_Memo("memo");
        Optional<SubAccountModel> optModel = Optional.of(model);
        when(subAccountDAO.getSubAccountByGuid("satGuid")).thenReturn(optModel);
        var rowSet = Mockito.mock(SqlRowSet.class);
        Mockito.when(rowSet.next()).thenReturn(false);
        Mockito.when(rowSet.getString(Mockito.anyString())).thenReturn("code1");
        var response = subAccountService.getSubAccountDetailByGuid("satGuid", "agtGuid");

        assertTrue(response.getSatGuid().equals(model.getSAT_GUID()));
        assertTrue(response.getAccountID().equals(model.getSAT_AccountID()));
        assertTrue(response.getMemo().equals(model.getSAT_Memo()));

    }

    @Test
    public void createSubAccount() {
        when(subAccountDAO.findSubAccountGuidByAccountID(Mockito.anyString(), Mockito.anyString())).thenReturn(0);
        when(subAccountDAO.add(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString()
                , Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.doNothing().when(functionRelationDAO).batchInsert(Mockito.anyString(), Mockito.any());
        AddSubAccountRequest request = new AddSubAccountRequest("accountID", "password", Arrays.asList("1", "2", "3"));
        request.setMemo("memo");
        request.setNickName("nickName");

        subAccountService.createSubAccount("agentGuid", "belongDomain", request);
    }

    @Test
    public void updateSubAccount() {
        when(subAccountDAO.findSubAccountGuidByGuid(Mockito.anyString())).thenReturn(1);
        Mockito.doNothing().when(subAccountDAO).update(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.doNothing().when(functionRelationDAO).deleteAllBySubAccount(Mockito.anyString());
        Mockito.doNothing().when(functionRelationDAO).batchInsert(Mockito.anyString(), Mockito.any());
        UpdateSubAccountRequest request = new UpdateSubAccountRequest();
        request.setSatGuid("satGuid");
        request.setNickName("nickName");
        request.setMemo("memo");
        request.setFunCodes(Arrays.asList("1", "2", "3"));
        SubAccountModel subModel = new SubAccountModel();
        subModel.setAGT_GUID("agtGuid");
        when(subAccountDAO.getSubAccountByGuid("satGuid")).thenReturn(Optional.of(subModel));

        subAccountService.updateSubAccount(request, "agtGuid");

    }

    @Test
    public void lockSubAccount() {
        Mockito.doNothing().when(subAccountDAO).lock(Mockito.anyString(), Mockito.anyString());
        subAccountService.lockSubAccount("agtGuid", "subAccountGuid");
    }

    @Test
    public void unLockSubAccount() {
        Mockito.doNothing().when(subAccountDAO).unlock(Mockito.anyString(), Mockito.anyString());
        subAccountService.unLockSubAccount("agtGuid", "subAccountGuid");
    }

    @Test
    public void updatePasswordForHandler() {
        String parentGuid = "parentGuid";
        String agentGuid = "agentGuid";
        String password = "password";
        when(agentService.findParentGuidByAgentGuid(agentGuid, 2)).thenReturn("123").thenReturn(parentGuid);
        assertThrows(APIException.class, () -> {
            subAccountService.updateManagerAndAgentPassword(parentGuid, 2, agentGuid, password);
        });

        Optional<SubAccountModel> op = Optional.empty();
        when(subAccountDAO.getSubAccountByAgentGuid(agentGuid)).thenReturn(op);
        assertThrows(APIException.class, () -> {
            subAccountService.updateManagerAndAgentPassword(parentGuid, 2, agentGuid, password);
        });
    }

    @Test
    public void updatePassword() {
        SubAccountModel model = new SubAccountModel();
        model.setSAT_AccountID("satAccountID");
        model.setSAT_GUID("satGuid");
        Optional<SubAccountModel> optModel = Optional.of(model);
        when(subAccountDAO.getSubAccountByGuid(Mockito.anyString())).thenReturn(optModel);
        when(subAccountDAO.updatePassword(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        subAccountService.updatePassword("subAccountGuid", "password");
    }

    @Test
    void getSubAccountListbyAgentGuid() {
        var resultSetMock = Mockito.mock(SqlRowSet.class);
        when(subAccountDAO.findSubAccountListByAGT_GUID(anyString())).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        when(resultSetMock.getString("satGuid")).thenReturn(anyString());
        when(resultSetMock.getString("accountID")).thenReturn(anyString());
        when(resultSetMock.getString("nickName")).thenReturn(anyString());
        when(resultSetMock.getBoolean("enable")).thenReturn(anyBoolean());
        when(resultSetMock.getDate("lastLoginDateTime")).thenReturn(any());
        when(resultSetMock.getString("lastIP")).thenReturn(anyString());
        when(resultSetMock.getDate("createDateTime")).thenReturn(any());

        List<SubAccountListResponse> list1 = subAccountService.getSubAccountListbyAgentGuid("agentGuid");
        List<SubAccountListResponse> list2 = subAccountService.getSubAccountListbyAgentGuid("agentGuid");
        assertTrue(!list1.isEmpty());
        assertTrue(list2.isEmpty());

    }

    @Test
    void getAndCheckSubAcount(){
        SubAccountModel model = new SubAccountModel();
        model.setAGT_GUID("agtGuid");
        model.setSAT_GUID("satGuid");
        model.setSAT_AccountID("accountID");
        model.setSAT_NickName("nickName");
        model.setSAT_Memo("memo");
        Optional<SubAccountModel> optModel = Optional.of(model);
        when(subAccountDAO.getSubAccountByGuid("satGuid")).thenReturn(optModel);
        assertTrue("satGuid".equals(subAccountService.getAndCheckSubAcount("satGuid", "agtGuid").getSAT_GUID()));
    }
}
