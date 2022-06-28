package com.icrown.backendapi.services;

import com.icrown.backendapi.dtos.*;
import com.icrown.common.services.AgentCommonService;
import com.icrown.common.services.AgentGameRelationCommonService;
import com.icrown.gameapi.commons.responses.ResponseCode;
import com.icrown.gameapi.commons.utils.APIException;
import com.icrown.gameapi.commons.utils.DateUtil;
import com.icrown.gameapi.commons.utils.Md5Util;
import com.icrown.gameapi.daos.*;
import com.icrown.gameapi.models.FunctionRelationModel;
import com.icrown.gameapi.models.SubAccountModel;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author adi
 */
@Service
public class SubAccountService {

    @Autowired
    SubAccountDAO subAccountDAO;
    @Autowired
    FunctionRelationDAO functionRelationDAO;
    @Autowired
    DateUtil dateUtil;
    @Autowired
    Md5Util md5Util;
    @Autowired
    AgtFunctionDAO agtFunctionDAO;
    @Autowired
    private PlatformTransactionManager platformTransactionManager;
    @Autowired
    AgentService agentService;
    @Autowired
    AgentGameRelationCommonService agentGameRelationCommonService;
    @Autowired
    LogGameTicketDAO logGameTicketDAO;
    @Autowired
    AgentCommonService agentCommonService;
    @Autowired
    AgentDAO agentDAO;
    @Autowired
    GameReportDayDAO gameReportDayDAO;

    /**
     * 加密密碼字串
     */
    public String encryptPassword(String originalPassword) {
        return md5Util.generate(originalPassword);
    }

    @Transactional(rollbackFor = Exception.class, timeout = 10)
    public LoginResponse login(String accountID, String password, String domain, String ipAddress, String clientType, String language) {

        Map<String, Object> result = subAccountDAO.login(accountID, domain);

        if (result.isEmpty()) {
            subAccountDAO.loginLog(accountID, domain, ipAddress, "", clientType, ResponseCode.BACKENDAPI_ACCOUNT_NOT_EXISTS.getStatusCode(), "account not exists");
            throw new APIException(ResponseCode.BACKENDAPI_ACCOUNT_NOT_EXISTS, "account not exists");
        }

        if (!(boolean) result.get("MCT_Enable")) {
            throw new APIException(ResponseCode.BACKENDAPI_MERCHANT_IS_CLOSE, ResponseCode.BACKENDAPI_MERCHANT_IS_CLOSE.getErrorMessage());
        }

        String agtGuid = result.get("AGT_GUID").toString();

        //檢查ip
        String allowIP = result.get("MCT_BackendAllowIP").toString();
        boolean allowed = false;

        //留白代表都可以
        if (StringUtil.isNullOrEmpty(allowIP)) {
            allowed = true;
        }
        else {
            Stream<String> ips = Arrays.stream(allowIP.trim().split(","));
            allowed = ips.anyMatch(ip -> ip.trim().equalsIgnoreCase(ipAddress.trim()));
        }

        if (!allowed) {
            subAccountDAO.loginLog(accountID, domain, ipAddress, agtGuid, clientType, ResponseCode.BACKENDAPI_IP_NOT_ALLOW.getStatusCode(), "agent ip error" + ipAddress);
            throw new APIException(ResponseCode.BACKENDAPI_IP_NOT_ALLOW, ipAddress);
        }

        //檢查代理鎖定
        boolean agentLock = (boolean) result.get("AGT_Lock");
        if (agentLock) {
            subAccountDAO.loginLog(accountID, domain, ipAddress, agtGuid, clientType, ResponseCode.BACKENDAPI_AGENT_FROZEN.getStatusCode(), "agent frozen");
            throw new APIException(ResponseCode.BACKENDAPI_AGENT_FROZEN);
        }
        //檢查自己鎖定
        boolean subaccountEnable = (boolean) result.get("SAT_Enable");
        if (!subaccountEnable) {
            subAccountDAO.loginLog(accountID, domain, ipAddress, agtGuid, clientType, ResponseCode.BACKENDAPI_ACCOUNT_ALREADY_LOCK.getStatusCode(), "account already lock");
            throw new APIException(ResponseCode.BACKENDAPI_ACCOUNT_ALREADY_LOCK);
        }

        //檢查regaintime 是否超過
        Date regainTime = (Date) result.get("SAT_RegainDatetime");
        if (regainTime == null) {
            regainTime = new Date();
        }
        Date now = new Date();
        long diffS = (now.getTime() - regainTime.getTime()) / 1000;
        if (diffS < 0) {
            subAccountDAO.loginLog(accountID, domain, ipAddress, agtGuid, clientType, ResponseCode.BACKENDAPI_AGENT_REGAIN_TIME.getStatusCode(), "agent regain time ," + diffS);
            throw new APIException(ResponseCode.BACKENDAPI_AGENT_REGAIN_TIME, diffS + "");
        }
        //檢查密碼
        String dbPassword = result.get("SAT_Password").toString();
        if (!dbPassword.trim().equals(encryptPassword(password))) {
            regainTime = now;
            int errorCount = (int) result.get("SAT_ErrorCount") + 1;

            //超過3次後都會需要重試時間
            if (errorCount >= 3) {
                regainTime = dateUtil.addTime(now, Calendar.SECOND, 120);
            }
            subAccountDAO.loginError(accountID, domain, ipAddress, new Date(), regainTime);
            subAccountDAO.loginLog(accountID, domain, ipAddress, agtGuid, clientType, ResponseCode.BACKENDAPI_AGENT_PASSWORD_ERROR.getStatusCode(), "agent password error,errorCount:" + errorCount);
            throw new APIException(ResponseCode.BACKENDAPI_AGENT_PASSWORD_ERROR, "agent password error,errorCount:" + errorCount);
        }

        //成功log 200
        String satGuid = (String) result.get("SAT_GUID");
        String token = UUID.randomUUID().toString().replace("-", "");
        Date logintime = new Date();
        Date expiretime = dateUtil.addTime(logintime, Calendar.MINUTE, 30);

        subAccountDAO.loginOk(satGuid, token, expiretime, accountID, domain, ipAddress, logintime);
        subAccountDAO.loginLog(accountID, domain, ipAddress, agtGuid, clientType, 200, "");

        int agtLevel = (int) result.get("AGT_Level");
        int satType = (int) result.get("SAT_Type");
        LoginResponse response = new LoginResponse();

        boolean isFirst = (boolean) result.get("SAT_IsFirstLogin");

        SqlRowSet agentListRowSet = agentDAO.getAgent3ListByAgentGuidAndLevel(agtGuid, agtLevel);
        List<String> agent3List = new ArrayList<>();
        agent3List.add("");
        while (agentListRowSet.next()) {
            agent3List.add(agentListRowSet.getString("AGT_Agent3"));
        }

        List<Integer> gameTypes = agentGameRelationCommonService.getEnableGameTypeByMctGuid(result.get("MCT_Guid").toString());

        List<Integer> reportGameTypes = gameReportDayDAO.get2MonthGameTypeByAgent3List(agent3List);

        List<Integer> allGameTypes = new ArrayList<>();
        allGameTypes.addAll(gameTypes);
        allGameTypes.removeAll(reportGameTypes);
        allGameTypes.addAll(reportGameTypes);

        response.setToken(token);
        response.setFirstLogin(isFirst);
        response.setAccountID(accountID);
        response.setType(satType);
        response.setAgentLevel(agtLevel);
        response.setGameTypes(allGameTypes);
        language = language == null? result.get("LAG_Code").toString() : language;
        response.setLanguage(language);

        return response;
    }


    public void logout(String token) {
        subAccountDAO.logout(token);
    }


    public SubAccountModel getSubAccountByGuid(String satGuid) {

        var optData = subAccountDAO.getSubAccountByGuid(satGuid);
        return optData.orElseThrow(() -> {
            throw new APIException(ResponseCode.BACKENDAPI_SUB_ACCOUNT_NOT_EXIST);
        });
    }

    public SubAccountDetailResponse getSubAccountDetailByGuid(String satGuid, String agtGuid) {
        //取得並檢查子帳號是否合法
        SubAccountModel model = getAndCheckSubAcount(satGuid, agtGuid);

        List<FunctionRelationModel> functionRelationModels = functionRelationDAO.getFunctionRelationBySatGuid(satGuid);

        List<String> acls = functionRelationModels.stream().map(FunctionRelationModel::getFUN_Code).collect(Collectors.toList());

        SubAccountDetailResponse response = new SubAccountDetailResponse();
        response.setSatGuid(model.getSAT_GUID());
        response.setAccountID(model.getSAT_AccountID());
        response.setNickName(model.getSAT_NickName());
        response.setMemo(model.getSAT_Memo());
        response.setFunCodes(acls);
        response.setCreateDatetime(model.getSAT_CreateDatetime());

        return response;
    }

    public List<SubAccountListResponse> getSubAccountListbyAgentGuid(String agentGuid) {
        SqlRowSet rowSet = subAccountDAO.findSubAccountListByAGT_GUID(agentGuid);
        List<SubAccountListResponse> list = new ArrayList<>();
        while (rowSet.next()) {
            var item = new SubAccountListResponse();
            item.setSatGuid(rowSet.getString("satGuid"));
            item.setAccountID(rowSet.getString("accountID"));
            item.setNickName(rowSet.getString("nickName"));
            item.setEnable(rowSet.getBoolean("enable"));
            item.setLastLoginDateTime(rowSet.getDate("lastLoginDateTime"));
            item.setLastIP(rowSet.getString("lastIP"));
            item.setCreateDateTime(rowSet.getDate("createDateTime"));
            list.add(item);
        }
        return list;
    }

    /**
     * 建立子帳號
     **/
    @Transactional(rollbackFor = Exception.class, timeout = 10)
    public void createSubAccount(String agentGuid, String belongDomain, AddSubAccountRequest model) {

        // 判斷是否已經存在
        if (subAccountDAO.findSubAccountGuidByAccountID(model.getAccountID(), belongDomain) > 0) {
            throw new APIException(ResponseCode.BACKENDAPI_ACCOUNT_ALREADY_EXISTS);
        }

        // 正式寫入
        String guid = UUID.randomUUID().toString();
        subAccountDAO.add(guid, agentGuid, 0, belongDomain, model.getAccountID(), encryptPassword(model.getPassword()), model.getNickName(), model.getMemo());
        functionRelationDAO.batchInsert(guid, model.getFunCodes());
    }


    /**
     * 更新子帳號
     **/
    @Transactional(rollbackFor = Exception.class, timeout = 10)
    public void updateSubAccount(UpdateSubAccountRequest request, String agtGuid) {
        //檢查子帳號是否合法
        getAndCheckSubAcount(request.getSatGuid(), agtGuid);

        subAccountDAO.update(request.getSatGuid(), request.getNickName(), request.getMemo());
        // 更新權限表
        functionRelationDAO.deleteAllBySubAccount(request.getSatGuid());
        functionRelationDAO.batchInsert(request.getSatGuid(), request.getFunCodes());
    }


    public void lockSubAccount(String agtGuid, String satGuid) {
        subAccountDAO.lock(agtGuid, satGuid);
    }

    public void unLockSubAccount(String agtGuid, String satGuid) {
        subAccountDAO.unlock(agtGuid, satGuid);
    }

    public void updateManagerAndAgentPassword(String parentGuid, int level, String agentGuid, String password) {
        // 只能改自己的下線或自己密碼
        if (!agentService.findParentGuidByAgentGuid(agentGuid, level).equals(parentGuid)) {
            throw new APIException(ResponseCode.BACKENDAPI_NO_PERMISSION);
        }

        Optional<SubAccountModel> optAccount = subAccountDAO.getSubAccountByAgentGuid(agentGuid);
        optAccount.ifPresentOrElse(model -> updatePassword(model.getSAT_GUID(), password), () -> {
            throw new APIException(ResponseCode.BACKENDAPI_ACCOUNT_NOT_EXISTS);
        });
    }

    public void updatePassword(String satGuid, String password) {
        var optAccount = subAccountDAO.getSubAccountByGuid(satGuid);
        optAccount.ifPresentOrElse(model -> {
            if (model.getSAT_AccountID().equals(password)) {
                throw new APIException(ResponseCode.BACKENDAPI_PASSWORD_SAME_AS_ACCOUNT);
            }
            if (!StringUtil.isNullOrEmpty(model.getSAT_Password()) && model.getSAT_Password().equals(encryptPassword(password))) {
                throw new APIException(ResponseCode.BACKENDAPI_PASSWORD_SAME_AS_OLD_PASSWORD);
            }
            if (!subAccountDAO.updatePassword(model.getSAT_GUID(), encryptPassword(password))) {
                throw new APIException(ResponseCode.BACKENDAPI_SQL_NO_RECORD);
            }
        }, () -> {
            throw new APIException(ResponseCode.BACKENDAPI_ACCOUNT_NOT_EXISTS);
        });
    }

    public void updateSubAccountPassword(String satGuid, String password, String agtGuid) {

        //取得並檢查子帳號是否合法
        SubAccountModel model = getAndCheckSubAcount(satGuid, agtGuid);

        if (model.getSAT_AccountID().equals(password)) {
            throw new APIException(ResponseCode.BACKENDAPI_PASSWORD_SAME_AS_ACCOUNT);
        }
        if (!StringUtil.isNullOrEmpty(model.getSAT_Password()) && model.getSAT_Password().equals(encryptPassword(password))) {
            throw new APIException(ResponseCode.BACKENDAPI_PASSWORD_SAME_AS_OLD_PASSWORD);
        }
        if (!subAccountDAO.updatePassword(model.getSAT_GUID(), encryptPassword(password))) {
            throw new APIException(ResponseCode.BACKENDAPI_SQL_NO_RECORD);
        }
    }

    public void updateSelfPassword(String subAccountGuid, String oldPassword, String newPassword) {
        subAccountDAO.getSubAccountByGuid(subAccountGuid).ifPresentOrElse(model -> {
            String oldPasswordEncrypt = encryptPassword(oldPassword);

            if (!model.getSAT_Password().equals(oldPasswordEncrypt)) {
                throw new APIException(ResponseCode.BACKENDAPI_OLD_PASSWORD_ERROR);
            }

            if (model.getSAT_AccountID().equals(newPassword)) {
                throw new APIException(ResponseCode.BACKENDAPI_PASSWORD_SAME_AS_ACCOUNT);
            }

            if (!StringUtil.isNullOrEmpty(model.getSAT_Password()) && model.getSAT_Password().contentEquals(encryptPassword(newPassword))) {
                throw new APIException(ResponseCode.BACKENDAPI_PASSWORD_SAME_AS_OLD_PASSWORD);
            }
            subAccountDAO.updateSelfPassword(subAccountGuid, encryptPassword(newPassword));
        }, () -> {
            throw new APIException(ResponseCode.BACKENDAPI_ACCOUNT_NOT_EXISTS);
        });
    }

    public SubAccountModel getAndCheckSubAcount(String satGuid, String agtGuid) {
        SubAccountModel subAccountModel = subAccountDAO.getSubAccountByGuid(satGuid).orElseThrow(() -> {
            throw new APIException(ResponseCode.BACKENDAPI_SUB_ACCOUNT_NOT_EXIST);
        });

        if (!agtGuid.equals(subAccountModel.getAGT_GUID())) {
            throw new APIException(ResponseCode.BACKENDAPI_NO_PERMISSION, ResponseCode.BACKENDAPI_NO_PERMISSION.getErrorMessage());
        }

        return subAccountModel;
    }

}
