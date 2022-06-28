package com.icrown.backendapi.services;

import com.icrown.backendapi.dtos.AddAgentRequest;
import com.icrown.common.services.AgentCommonService;
import com.icrown.common.services.MerchantCommonService;
import com.icrown.gameapi.commons.responses.ResponseCode;
import com.icrown.gameapi.commons.utils.APIException;
import com.icrown.gameapi.daos.AgentDAO;
import com.icrown.gameapi.daos.SubAccountDAO;
import com.icrown.backendapi.dtos.AgentDetailResponse;
import com.icrown.gameapi.models.AGT_MerchantModel;
import com.icrown.gameapi.models.AgentModel;
import com.icrown.gameapi.models.SessionModel;
import com.icrown.gameapi.models.SubAccountModel;
import org.apiguardian.api.API;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;

import java.util.*;

import org.mockito.Mockito;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ratpack.http.Response;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.fail;

@SpringBootTest(classes = AgentServiceTest.class)
@ComponentScan(basePackages = "com.icrown")
@EnableAutoConfiguration
public class AgentServiceTest {
    @Autowired
    AgentService agentService;

    @MockBean
    AgentDAO agentDAO;
    @MockBean
    SessionService sessionService;
    @MockBean
    SubAccountService subAccountService;
    @MockBean
    AgentCommonService agentCommonService;
    @MockBean
    MerchantCommonService merchantCommonService;
    @MockBean
    SubAccountDAO subAccountDAO;


    @Test
    public void getAgentInfo() {
        SessionModel sessionData = new SessionModel();
        sessionData.setSAT_GUID("satGuid");
        SubAccountModel subAccountData = new SubAccountModel();
        subAccountData.setAGT_GUID("agtGuid");
        AgentModel agentData = new AgentModel();
        agentData.setMCT_GUID("mctGuid");
        AGT_MerchantModel merchantData = new AGT_MerchantModel();
        merchantData.setMCT_Domain("domain");
        String token = "token";
        when(sessionService.getSessionByToken(token)).thenReturn(sessionData);
        when(subAccountService.getSubAccountByGuid(sessionData.getSAT_GUID())).thenReturn(subAccountData);
        when(agentCommonService.getAgentInfoByAgentGUID(subAccountData.getAGT_GUID())).thenReturn(agentData);
        when(merchantCommonService.getMerchant(agentData.getMCT_GUID())).thenReturn(merchantData);

        var response = agentService.getAgentInfo(token);
        assertTrue(response.getSessionModel().getSAT_GUID().equals(sessionData.getSAT_GUID()));
        assertTrue(response.getSubAccountModel().getAGT_GUID().equals(subAccountData.getAGT_GUID()));
        assertTrue(response.getAgentModel().getMCT_GUID().equals(agentData.getMCT_GUID()));
        assertTrue(response.getMerchantModel().getMCT_Domain().equals(merchantData.getMCT_Domain()));
    }

    @Test
    public void getAgentTreeByAgent3() {
        String agentGuid3 = "agentGuid3";
        Map<String, String> map = new HashMap<>();
        map.put("Agent1", "agent1");
        map.put("Agent2", "agent2");
        map.put("Agent3", "agent3");
        when(agentDAO.getAgentTreeByAgent3(agentGuid3)).thenReturn(map);
        Map<String, String> returnMap = agentService.getAgentTreeByAgent3(agentGuid3);
        assertTrue(map.get("Agent1").equals(returnMap.get("Agent1")));
        assertTrue(map.get("Agent2").equals(returnMap.get("Agent2")));
        assertTrue(map.get("Agent3").equals(returnMap.get("Agent3")));
    }

    @Test
    public void getAgentTreeByAgent2() {
        String agentGuid2 = "agentGuid2";
        Map<String, String> map = new HashMap<>();
        map.put("Agent1", "Agent1");
        map.put("Agent2", "Agent2");
        when(agentDAO.getAgentTreeByAgent2(agentGuid2)).thenReturn(map);
        Map<String, String> returnMap = agentService.getAgentTreeByAgent2(agentGuid2);
        assertTrue(map.get("Agent1").equals(returnMap.get("Agent1")));
        assertTrue(map.get("Agent2").equals(returnMap.get("Agent2")));
    }

    @Test
    public void getLoginInfo() {
        Map<String, String> map = new HashMap<>();
        map.put("Agent1", "agent1");
        map.put("Agent2", "agent2");
        map.put("Agent3", "agent3");
        when(agentDAO.getAgentTreeByAgent3(Mockito.anyString())).thenReturn(map);
        String agentGuid = "agentGuid";
        int level = 3;
        String domain = "domain";
        String accountID = "accountID";
        Date createDateTime = new Date();
        var response = agentService.getLoginInfo(agentGuid, level, domain, accountID, createDateTime);
        assertTrue(response.getOrgTree().size() == 3);
    }

    @Test
    public void getAgentTree() {
        Map<String, String> map1 = new HashMap<>();
        map1.put("Agent1", "Agent1");
        map1.put("Agent2", "Agent2");
        Map<String, String> map2 = new HashMap<>();
        map2.put("Agent1", "Agent1");
        map2.put("Agent2", "Agent2");
        map2.put("Agent3", "Agent3");
        when(agentDAO.getAgentTreeByAgent2(Mockito.anyString())).thenReturn(map1);
        when(agentDAO.getAgentTreeByAgent3(Mockito.anyString())).thenReturn(map2);
        String accountID = "accountID";
        String agentGuid = "agentGuid";
        int level = 2;
        Map<String, String> returnMap = agentService.getAgentTree(accountID, agentGuid, level);
        assertTrue(returnMap.get("Agent1").equals(map1.get("Agent1")));
        assertTrue(returnMap.get("Agent2").equals(map1.get("Agent2")));
    }

    @Test
    public void getAgentList() {
        String mctGuid = "mctGuid";
        String currency = "currency";
        String agtParent = "agtParent";
        String agtAccountID = "agtAccountID";
        int status = 0;
        int pageIndex = 1;
        int pageSize = 10;
        when(agentDAO.getAgentListCount(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt())).thenReturn(1);
        List<AgentModel> agentModels = new ArrayList<>();
        when(agentDAO.getAllAgentsByMctGUID(Mockito.anyString())).thenReturn(agentModels);
        var resultSetMock = Mockito.mock(SqlRowSet.class);
        Mockito.when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        when(agentDAO.getAgentList(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(resultSetMock);
        var response = agentService.getAgentList(mctGuid, currency, agtParent, agtAccountID, status, pageSize, pageIndex);
        assertTrue(response.getRecords().size() == 1);
    }

    @Test
    public void createAgent() {
        AgentModel agentModel = new AgentModel();
        agentModel.setAGT_Level(1);

        AddAgentRequest request = new AddAgentRequest("accountID", "password");
        request.setMemo("memo");

        when(agentDAO.findAgentByAccountID(Mockito.anyString(), Mockito.anyString())).thenReturn(0);
        when(agentCommonService.getAgentInfoByAgentGUID(Mockito.anyString())).thenReturn(agentModel);
        when(subAccountService.encryptPassword(anyString())).thenReturn("password");
        agentService.createAgent("domainName", "parentGuid", request);

        verify(agentDAO).add(any(AgentModel.class));
        verify(subAccountDAO).add(anyString(), anyString(), anyInt(), anyString(), anyString(), anyString(), any(), anyString());
    }

    @Test
    public void createAgentAccNotPresent() {
        AddAgentRequest request = new AddAgentRequest("accountID", "password");
        request.setMemo("memo");

        when(agentDAO.findAgentByAccountID(Mockito.anyString(), Mockito.anyString())).thenReturn(1);
        when(agentDAO.findSubAccountByAccountID(Mockito.anyString(), Mockito.anyString())).thenReturn(0);
        Exception exception = assertThrows(APIException.class, () -> {
            agentService.createAgent("domainName", "parentGuid", request);
        });

        assertEquals("Account Already Exists", exception.getMessage());
    }

    @Test
    public void createAgentSubAccNotPresent() {
        AddAgentRequest request = new AddAgentRequest("accountID", "password");
        request.setMemo("memo");

        when(agentDAO.findAgentByAccountID(anyString(), anyString())).thenReturn(0);
        when(agentDAO.findSubAccountByAccountID(Mockito.anyString(), Mockito.anyString())).thenReturn(1);
        Exception exception = assertThrows(APIException.class, () -> {
            agentService.createAgent("domainName", "parentGuid", request);
        });

        assertEquals("Account Already Exists", exception.getMessage());
    }

    @Test
    public void createAgentFatherIsLevel3() {
        AddAgentRequest request = new AddAgentRequest("accountID", "password");
        request.setMemo("memo");

        AgentModel father = new AgentModel();
        father.setAGT_Level(3);

        when(agentDAO.findAgentByAccountID(anyString(), anyString())).thenReturn(0);
        when(agentCommonService.getAgentInfoByAgentGUID(anyString())).thenReturn(father);
        Exception exception = assertThrows(APIException.class, () -> {
            agentService.createAgent("domainName", "parentGuid", request);
        });

        assertEquals("You have No permission to add an agent", exception.getMessage());
    }

    @Test
    public void updateAgent() {
        SubAccountModel subAccountModel = new SubAccountModel();
        subAccountModel.setAGT_GUID("agentGuid");
        Optional<SubAccountModel> opp = Optional.of(subAccountModel);

        AgentModel agentData = new AgentModel();
        agentData.setAGT_Agent3("parent");

        when(subAccountDAO.getSubAccountByGuid(anyString())).thenReturn(opp);
        when(agentCommonService.getAgentInfoByAgentGUID("agentGuid")).thenReturn(agentData);
        doNothing().when(subAccountDAO).updateAgentMemo(anyString(), anyString());
        agentService.updateAgent("agentGuid", "memo", "parent", 3);

        verify(subAccountDAO).updateAgentMemo(anyString(), anyString());
    }

    @Test
    public void updateAgentSatGuidNotPresent() {
        Optional<SubAccountModel> opt = Optional.empty();

        when(subAccountDAO.getSubAccountByGuid(anyString())).thenReturn(opt);
        Exception exception = assertThrows(APIException.class, () -> {
            agentService.updateAgent("satGuid", "memo", "parentGuid", 3);
        });

        assertEquals("Account Not Exists", exception.getMessage());
    }

    @Test
    public void updateAgentIsSupervisor() {
        SubAccountModel model = new SubAccountModel();
        model.setAGT_GUID("agentGuid");
        Optional<SubAccountModel> opt = Optional.of(model);

        AgentModel agentModel = new AgentModel();
        agentModel.setAGT_Agent2("parentGuid");

        when(subAccountDAO.getSubAccountByGuid(any())).thenReturn(opt);
        when(agentCommonService.getAgentInfoByAgentGUID(anyString())).thenReturn(agentModel);
        agentService.updateAgent("satGuid", "memo", "parentGuid", 2);

        verify(subAccountDAO).updateAgentMemo(anyString(), anyString());
    }

    @Test
    public void updateAgentNotSupervisor() {
        SubAccountModel model = new SubAccountModel();
        model.setAGT_GUID("agentGuid");
        Optional<SubAccountModel> opt = Optional.of(model);

        AgentModel agentModel = new AgentModel();
        agentModel.setAGT_Agent1("falseSupervisor");

        when(subAccountDAO.getSubAccountByGuid(any())).thenReturn(opt);
        when(agentCommonService.getAgentInfoByAgentGUID(anyString())).thenReturn(agentModel);

        Exception exception = assertThrows(APIException.class, () -> {
            agentService.updateAgent("satGuid", "memo", "parentGuid", 1);
        });

        assertEquals("No Permission To Run API", exception.getMessage());
    }

    @Test
    public void findParentGuidByAgentGuid() {
        AgentModel model = new AgentModel();
        model.setAGT_Parent("parent");
        model.setAGT_Agent2("parent");

        when(agentCommonService.getAgentInfoByAgentGUID(Mockito.anyString())).thenReturn(model);
        String agtParent = agentService.findParentGuidByAgentGuid("agentGuid", 2);

        assertEquals("parent", agtParent);
    }

    @Test
    public void lockAgent() {
        AgentModel model = new AgentModel();
        model.setAGT_Parent("parent");
        model.setAGT_GUID("guid");
        model.setAGT_Level(2);

        List<String> list = new ArrayList<>();
        list.add("guid");

        when(agentCommonService.getAgentInfoByAgentGUID(Mockito.anyString())).thenReturn(model);
        when(agentDAO.getAllAgentByParent(Mockito.anyString(), Mockito.anyInt())).thenReturn(list);
        when(agentDAO.getAllMemberByParent(Mockito.anyString(), Mockito.anyInt())).thenReturn(list);
        agentService.lockAgent("parent", "guid");

        verify(agentDAO).lockAgent(anyString(), ArgumentMatchers.<String>anyList());
        verify(agentDAO).lockPlayerByAgent(anyString(), ArgumentMatchers.<String>anyList());
    }

    @Test
    public void lockAgnetLockerNotSupervisor() {
        AgentModel model = new AgentModel();
        model.setAGT_Parent("falseParent");

        when(agentCommonService.getAgentInfoByAgentGUID(anyString())).thenReturn(model);
        Exception exception = assertThrows(APIException.class, () -> {
            agentService.lockAgent("lockerGuid", "guid");
        });

        assertEquals("No Operate Permission", exception.getMessage());
    }

    @Test
    public void lockAgentIsLock() {
        List<String> agentGuidList = new ArrayList<>();

        AgentModel model = new AgentModel();
        model.setAGT_Parent("lockerGuid");
        model.setAGT_Level(2);

        when(agentCommonService.getAgentInfoByAgentGUID(anyString())).thenReturn(model);
        when(agentDAO.getAllAgentByParent(anyString(), anyInt())).thenReturn(agentGuidList);
        Exception exception = assertThrows(APIException.class, () -> {
            agentService.lockAgent("lockerGuid", "guid");
        });

        assertEquals("Account Is Already Lock", exception.getMessage());
    }

    @Test
    public void unlockAgent() {
        AgentModel model = new AgentModel();
        model.setAGT_Parent("parent");
        model.setAGT_GUID("guid");
        model.setAGT_Level(2);

        List<String> list = new ArrayList<>();
        list.add("guid");

        when(agentCommonService.getAgentInfoByAgentGUID(anyString())).thenReturn(model);
        when(agentDAO.getAllAgentByLocker(anyString(), anyString(), anyInt())).thenReturn(list);
        when(agentDAO.getAllMemberByLocker(anyString(), anyString(), anyInt())).thenReturn(list);
        agentService.unlockAgent("parent", "guid");

        verify(agentDAO).unlockAgent(ArgumentMatchers.<String>anyList());
        verify(agentDAO).unlockPlayer(ArgumentMatchers.<String>anyList());
    }

    @Test
    public void unlockAgentLockerNotSupervisor() {
        AgentModel model = new AgentModel();
        model.setAGT_Parent("flaseParent");

        when(agentCommonService.getAgentInfoByAgentGUID(anyString())).thenReturn(model);

        Exception exception = assertThrows(APIException.class, () -> {
            agentService.unlockAgent("lockerGuid", "guid");
        });

        assertEquals("No Operate Permission", exception.getMessage());
    }

    @Test
    public void unlockAgentIsNotLock() {
        AgentModel model = new AgentModel();
        model.setAGT_Parent("unlockerGuid");
        model.setAGT_Level(2);

        List<String> agentGuidList = new ArrayList<>();

        when(agentCommonService.getAgentInfoByAgentGUID(anyString())).thenReturn(model);
        when(agentDAO.getAllAgentByParent(anyString(), anyInt())).thenReturn(agentGuidList);
        Exception exception = assertThrows(APIException.class, () -> {
            agentService.unlockAgent("unlockerGuid", "guid");
        });

        assertEquals("Account Is Not Lock", exception.getMessage());
    }

    @Test
    public void getAgentDetail() {
        String agentGuid = "agentGuid";
        String accountID = "accountID";
        String parentGuid = "parentGuid";
        String domain = "domain";
        int level = 1;
        String memo = "memo";
        String nickName = "nickName";
        java.sql.Date createDateTime = new java.sql.Date(new Date().getTime());

        var rowSet = Mockito.mock(SqlRowSet.class);
        when(agentDAO.getAgentDetail(Mockito.anyString())).thenReturn(rowSet);
        when(rowSet.next()).thenReturn(false).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);

        assertThrows(APIException.class, () -> agentService.getAgentDetail(agentGuid, level, parentGuid));
        when(rowSet.getString("accountID")).thenReturn(accountID);
        when(rowSet.getString("domain")).thenReturn(domain);
        when(rowSet.getInt("level")).thenReturn(level);
        when(rowSet.getString("memo")).thenReturn(memo);
        when(rowSet.getString("nickName")).thenReturn(nickName);
        when(rowSet.getDate("createDateTime")).thenReturn(createDateTime);

        Map<String, String> map = new HashMap<>();
        map.put("Agent1", accountID);
        map.put("Agent2", accountID);
        map.put("Agent3", accountID);
        AgentModel agentData = new AgentModel();
        agentData.setAGT_Agent1(parentGuid);
        when(agentCommonService.getAgentInfoByAgentGUID(agentGuid)).thenReturn(agentData);
        when(agentDAO.getAgentTreeByAgent2(agentGuid)).thenReturn(map);
        when(agentDAO.getAgentTreeByAgent3(agentGuid)).thenReturn(map);
        assertTrue(agentService.getAgentDetail(agentGuid, level, parentGuid).getLevel() == 1);
        when(rowSet.getInt("level")).thenReturn(2);
        assertTrue(agentService.getAgentDetail(agentGuid, level, parentGuid).getLevel() == 2);
        when(rowSet.getInt("level")).thenReturn(3);
        assertTrue(agentService.getAgentDetail(agentGuid, level, parentGuid).getLevel() == 3);
    }

}
