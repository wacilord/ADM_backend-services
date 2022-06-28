package com.icrown.backendapi.services;

import com.icrown.backendapi.dtos.*;
import com.icrown.common.services.AgentCommonService;
import com.icrown.common.services.MerchantCommonService;
import com.icrown.gameapi.commons.responses.ResponseCode;
import com.icrown.gameapi.commons.utils.APIException;
import com.icrown.gameapi.commons.utils.DateUtil;
import com.icrown.gameapi.daos.AgentDAO;
import com.icrown.gameapi.daos.SubAccountDAO;
import com.icrown.gameapi.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Frank
 */
@Service
public class AgentService {
    @Autowired
    SessionService sessionService;
    @Autowired
    SubAccountService subAccountService;
    @Autowired
    AgentCommonService agentCommonService;
    @Autowired
    AgentDAO agentDAO;
    @Autowired
    SubAccountDAO subAccountDAO;
    @Autowired
    MerchantCommonService merchantCommonService;
    @Autowired
    private PlatformTransactionManager platformTransactionManager;
    @Autowired
    DateUtil dateUtil;

    public GetAgentInfoResponse getAgentInfo(String token) {
        SessionModel sessionData = sessionService.getSessionByToken(token);
        SubAccountModel subAccountData = subAccountService.getSubAccountByGuid(sessionData.getSAT_GUID());
        AgentModel agentData = agentCommonService.getAgentInfoByAgentGUID(subAccountData.getAGT_GUID());
        AGT_MerchantModel merchantData = merchantCommonService.getMerchant(agentData.getMCT_GUID());

        GetAgentInfoResponse response = new GetAgentInfoResponse();
        response.setSessionModel(sessionData);
        response.setSubAccountModel(subAccountData);
        response.setAgentModel(agentData);
        response.setMerchantModel(merchantData);
        return response;
    }

    public Map<String, String> getAgentTree(String accountID, String agentGuid, int level) {
        switch (level) {
            case 1:
                Map<String, String> map = new HashMap<>();
                map.put("Agent1", accountID);
                return map;
            case 2:
                return getAgentTreeByAgent2(agentGuid);
            case 3:
                return getAgentTreeByAgent3(agentGuid);
            default:
                return Collections.emptyMap();
        }
    }

    public Map<String, String> getAgentTreeByAgent3(String agentGuid3) {
        return agentDAO.getAgentTreeByAgent3(agentGuid3);
    }

    public List<OrgTree> getAgentTreeListByAgent3(String mctGuid) {
        SqlRowSet rowSet = agentDAO.getAgentTreeListByAgent3(mctGuid);
        List<OrgTree> orgTreeList = new ArrayList<>();
        while (rowSet.next()) {
            OrgTree orgTree = new OrgTree();
            orgTree.setAgent3(rowSet.getString("AGT_Guid"));

            Map<String, String> result = new HashMap<>();
            result.put("Agent1", rowSet.getString("Agent1"));
            result.put("Agent2", rowSet.getString("Agent2"));
            result.put("Agent3", rowSet.getString("Agent3"));
            orgTree.setTree(result);
            orgTreeList.add(orgTree);
        }

        return orgTreeList;
    }

    public Map<String, String> getAgentTreeByAgent2(String agentGuid2) {
        return agentDAO.getAgentTreeByAgent2(agentGuid2);
    }

    public List<AgentModel> getAllAgentsByMctGUID(String mctGuid) {
        return agentDAO.getAllAgentsByMctGUID(mctGuid);
    }

    public List<AgentModel> getAllAgent3ByMctGUID(String mctGuid) {
        return agentDAO.getAllAgent3ByMctGUID(mctGuid);
    }

    public AgentListResponse getAgentList(String mctGuid, String currency, String agtParent, String agtAccountID, int status, int pageSize, int pageIndex) {
        AgentListResponse response = new AgentListResponse();
        int itemCount = agentDAO.getAgentListCount(agtParent, agtAccountID, status);

        List<Agent> agents = new ArrayList<>();

        if (itemCount > 0) {
            List<AgentModel> agentModelList = agentDAO.getAllAgentsByMctGUID(mctGuid);
            SqlRowSet rs = agentDAO.getAgentList(agtParent, agtAccountID, status, pageSize, pageIndex);
            while (rs.next()) {
                Agent agent = new Agent();
                agent.setAgtGuid(rs.getString("AGT_GUID"));
                agent.setLevel(rs.getInt("AGT_Level"));
                agent.setAccountID(rs.getString("AGT_AccountID"));
                agent.setLock(rs.getBoolean("AGT_Lock"));
                agent.setCurrency(currency);

                if (agent.isLock()) {
                    String locker = rs.getString("AGT_Locker");
                    AgentModel agentModel = agentModelList.stream()
                            .filter(o -> o.getAGT_GUID().equals(locker)).collect(Collectors.toList()).get(0);
                    agent.setLocker(agentModel.getAGT_AccountID());
                }


                agents.add(agent);
            }

            response.setPageCount((int) Math.ceil((double) itemCount / pageSize));
        }
        else {
            response.setPageCount(1);
        }

        response.setItemCount(itemCount);
        response.setPageIndex(pageIndex);
        response.setPageSize(pageSize);
        response.setRecords(agents);

        return response;
    }

    public GetManagerListReponse getManagerList(String mctGuid, String currency, String agtParent, String agtAccountID, int status) {
        GetManagerListReponse response = new GetManagerListReponse();
        int itemCount = agentDAO.getManagerListCount(agtParent, agtAccountID, status);

        List<Agent> agents = new ArrayList<>();

        if (itemCount > 0) {
            List<AgentModel> agentModelList = agentDAO.getAllAgentsByMctGUID(mctGuid);
            SqlRowSet rs = agentDAO.getManagerList(agtParent, agtAccountID, status);
            while (rs.next()) {
                Agent agent = new Agent();
                agent.setAgtGuid(rs.getString("AGT_GUID"));
                agent.setLevel(rs.getInt("AGT_Level"));
                agent.setAccountID(rs.getString("AGT_AccountID"));
                agent.setLock(rs.getBoolean("AGT_Lock"));
                agent.setCurrency(currency);

                if (agent.isLock()) {
                    String locker = rs.getString("AGT_Locker");
                    AgentModel agentModel = agentModelList.stream()
                            .filter(o -> o.getAGT_GUID().equals(locker)).collect(Collectors.toList()).get(0);
                    agent.setLocker(agentModel.getAGT_AccountID());
                }
                agents.add(agent);
            }
        }

        response.setRecords(agents);
        return response;
    }

    @Transactional(rollbackFor = Exception.class, timeout = 10)
    public void createAgent(String domainName, String parentGuid, AddAgentRequest model) {

        // 判斷是否已經存在,包含檢查子帳號是否有重複的名稱
        if (agentDAO.findAgentByAccountID(model.getAccountID(), domainName) > 0 || agentDAO.findSubAccountByAccountID(model.getAccountID(), domainName) > 0) {
            throw new APIException(ResponseCode.BACKENDAPI_ACCOUNT_ALREADY_EXISTS);
        }

        // 組合所需要的欄位
        AgentModel father = agentCommonService.getAgentInfoByAgentGUID(parentGuid);


        // 第三層不可以增加下一層代理
        if (father.getAGT_Level() >= 3) {
            throw new APIException(ResponseCode.BACKENDAPI_NO_PERMISSION, "You have No permission to add an agent");
        }

        String guid = UUID.randomUUID().toString();
        AgentModel agentModel = new AgentModel();
        agentModel.setAGT_GUID(guid);
        agentModel.setMCT_GUID(father.getMCT_GUID());
        agentModel.setAGT_Parent(father.getAGT_GUID());
        int level = father.getAGT_Level() + 1;
        agentModel.setAGT_Level(level);
        switch (level) {
            case 3:
                agentModel.setAGT_Agent3(guid);
                agentModel.setAGT_Agent2(father.getAGT_GUID());
                agentModel.setAGT_Agent1(father.getAGT_Agent1());
                break;
            case 2:
                agentModel.setAGT_Agent2(guid);
                agentModel.setAGT_Agent1(father.getAGT_GUID());
                break;
            default:
                break;
        }
        agentModel.setMCT_Domain(father.getMCT_Domain());
        agentModel.setAGT_AccountID(model.getAccountID());
        agentModel.setAGT_CreateDatetime(dateUtil.getDateWithFormat(dateUtil.getDate(), "yyyy-MM-dd HH:mm:ss"));
        // 正式寫入
        agentDAO.add(agentModel);
        //主帳號subAccountGuid=agentGuid,subAccount就不相同
        subAccountDAO.add(guid, guid, 1, domainName, model.getAccountID()
                , subAccountService.encryptPassword(model.getPassword()), model.getNickName(), model.getMemo());
    }

    public void updateAgent(String satGuid, String memo, String parentGuid, int level) {
        Optional<SubAccountModel> opp = subAccountDAO.getSubAccountByGuid(satGuid);
        SubAccountModel model = opp.orElseThrow(() -> {
            throw new APIException(ResponseCode.BACKENDAPI_ACCOUNT_NOT_EXISTS, ResponseCode.BACKENDAPI_ACCOUNT_NOT_EXISTS.getErrorMessage());
        });
        //只能改自己的下線或自己
        if (!this.findParentGuidByAgentGuid(model.getAGT_GUID(), level).equals(parentGuid)) {
            throw new APIException(ResponseCode.BACKENDAPI_NO_PERMISSION);
        }
        subAccountDAO.updateAgentMemo(satGuid, memo);
    }


    public AgentDetailResponse getAgentDetail(String agentGuid, int level, String parentGuid) {
        SqlRowSet rowSet = agentDAO.getAgentDetail(agentGuid);
        if (!rowSet.next()) {
            throw new APIException(ResponseCode.BACKENDAPI_AGENT_NOT_FOUND);
        }

        //只能取得自己的下線或自己
        if (!this.findParentGuidByAgentGuid(agentGuid, level).equals(parentGuid)) {
            throw new APIException(ResponseCode.BACKENDAPI_NO_PERMISSION);
        }

        AgentDetailResponse model = new AgentDetailResponse();
        model.setAccountID(rowSet.getString("accountID"));
        model.setDomain(rowSet.getString("domain"));
        model.setLevel(rowSet.getInt("level"));
        model.setAgentGuid(rowSet.getString("agentGuid"));
        model.setMemo(rowSet.getString("memo"));
        model.setNickName(rowSet.getString("nickName"));
        model.setCreateDateTime(rowSet.getDate("createDateTime"));

        var orgTree = getAgentTree(model.getAccountID(), model.getAgentGuid(), model.getLevel());
        model.orgTreeAddItem(orgTree.get("Agent1"));
        if (model.getLevel() >= 2) {
            model.orgTreeAddItem(orgTree.get("Agent2"));
        }
        if (model.getLevel() >= 3) {
            model.orgTreeAddItem(orgTree.get("Agent3"));
        }
        return model;
    }


    public LoginInfoResponse getLoginInfo(String agentGuid, int level, String domain, String accountID, Date createDateTime) {

        LoginInfoResponse response = new LoginInfoResponse();
        var orgTree = getAgentTree(accountID, agentGuid, level);
        List<String> orgTreeList = new ArrayList<>();
        if (orgTree.containsKey("Agent1")) {
            orgTreeList.add(orgTree.get("Agent1"));
        }
        if (orgTree.containsKey("Agent2")) {
            orgTreeList.add(orgTree.get("Agent2"));
        }
        if (orgTree.containsKey("Agent3")) {
            orgTreeList.add(orgTree.get("Agent3"));
        }
        response.setDomain(domain);
        response.setOrgTree(orgTreeList);
        response.setAccountID(accountID);
        response.setCreateDateTime(createDateTime);
        return response;
    }

    public String findParentGuidByAgentGuid(String agentGuid, int level) {
        AgentModel agentData = agentCommonService.getAgentInfoByAgentGUID(agentGuid);
        String parentId = "";
        switch (level) {
            case 1:
                parentId = agentData.getAGT_Agent1();
                break;
            case 2:
                parentId = agentData.getAGT_Agent2();
                break;
            case 3:
                parentId = agentData.getAGT_Agent3();
                break;
            default:
                break;
        }
        return parentId;
    }

    public void lockAgent(String lockerGuid, String guid) {
        AgentModel agent = agentCommonService.getAgentInfoByAgentGUID(guid);

        if (agent.getAGT_Parent().compareTo(lockerGuid) == 0) {
            List<String> agentGuidList = agentDAO.getAllAgentByParent(guid, agent.getAGT_Level());
            if (agentGuidList.isEmpty()) {
                throw new APIException(ResponseCode.BACKENDAPI_ACCOUNT_ALREADY_LOCK);
            }
            agentDAO.lockAgent(lockerGuid, agentGuidList);

            List<String> playerGuidList = agentDAO.getAllMemberByParent(guid, agent.getAGT_Level());
            agentDAO.lockPlayerByAgent(lockerGuid, playerGuidList);

        }
        else {
            throw new APIException(ResponseCode.BACKENDAPI_NO_OPERATE_PERMISSION);
        }
    }

    public void unlockAgent(String lockerGuid, String guid) {
        AgentModel agent = agentCommonService.getAgentInfoByAgentGUID(guid);

        if (agent.getAGT_Parent().compareTo(lockerGuid) == 0) {
            List<String> agentGuidList = agentDAO.getAllAgentByLocker(lockerGuid, guid, agent.getAGT_Level());
            if (agentGuidList.isEmpty()) {
                throw new APIException(ResponseCode.BACKENDAPI_ACCOUNT_NOT_LOCK);
            }
            agentDAO.unlockAgent(agentGuidList);

            List<String> playerGuidList = agentDAO.getAllMemberByLocker(lockerGuid, guid, agent.getAGT_Level());
            agentDAO.unlockPlayer(playerGuidList);
        }
        else {
            throw new APIException(ResponseCode.BACKENDAPI_NO_OPERATE_PERMISSION);
        }
    }

    public AgentDetailResponse getAgentByAccountIDAndParentGuid(String parantGUid, String agtAccountID) {

        List<AgentModel> models = agentDAO.getAgentByAccountIDAndParentGuid(parantGUid, agtAccountID, 0);

        if (models.isEmpty()) {
            throw new APIException(ResponseCode.BACKENDAPI_ACCOUNT_NOT_EXISTS, ResponseCode.BACKENDAPI_ACCOUNT_NOT_EXISTS.getErrorMessage());
        }

        AgentDetailResponse response = new AgentDetailResponse();
        AgentModel model = models.get(0);
        response.setAccountID(model.getAGT_AccountID());
        response.setAgentGuid(model.getAGT_GUID());
        response.setCreateDateTime(model.getAGT_CreateDatetime());
        response.setDomain(model.getMCT_Domain());
        response.setLevel(model.getAGT_Level());
        return response;
    }

    public List<String> getAgentTreeByAgent3AndLevel(List<OrgTree> orgTreeList, int level, String agent3) {
        var orgTree = orgTreeList.stream().filter(o -> o.getAgent3().equals(agent3)).findFirst().get();

        List<String> orgTreeList2 = new ArrayList<>();
        switch (level) {
            case 1:
                orgTreeList2.add(orgTree.getTree().get("Agent1"));
                orgTreeList2.add(orgTree.getTree().get("Agent2"));
                orgTreeList2.add(orgTree.getTree().get("Agent3"));
                break;
            case 2:
                orgTreeList2.add(orgTree.getTree().get("Agent2"));
                orgTreeList2.add(orgTree.getTree().get("Agent3"));
                break;
            case 3:
                orgTreeList2.add(orgTree.getTree().get("Agent3"));
                break;
            default:
                orgTreeList2.add("");
                break;
        }

        return orgTreeList2;
    }
}
