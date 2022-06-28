package com.icrown.backendapi.services;

import com.icrown.backendapi.dtos.JackpotAgentReportData;
import com.icrown.backendapi.dtos.JackpotAgentReportResponse;
import com.icrown.backendapi.dtos.JackpotPlayerRecord;
import com.icrown.backendapi.dtos.JackpotPlayerReportResponse;
import com.icrown.backendapi.dtos.JackpotReportRequest;
import com.icrown.common.dtos.GameListResponse;
import com.icrown.common.services.GameCommonService;
import com.icrown.gameapi.commons.responses.ResponseCode;
import com.icrown.gameapi.commons.utils.APIException;
import com.icrown.gameapi.daos.AgentDAO;
import com.icrown.gameapi.models.AgentModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import com.icrown.gameapi.daos.JackpotReportDAO;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import java.util.*;

/**
 * @author dennis
 */
@Service
public class JackpotReportService {
    @Autowired
    AgentDAO agentDAO;
    @Autowired
    JackpotReportDAO jackpotReportDAO;
    @Autowired
    AgentService agentService;
    @Autowired
    GameCommonService gameCommonService;

    public JackpotAgentReportResponse getJackpotAgentReport(String agtAccountID, String selfAccountID, String selfAguGuid, String domain, int selflevel, Date startDate, Date endTime) {
        String agtGuid = selfAguGuid;
        AgentModel agentModel = null;
        List<AgentModel> agentModelList = agentDAO.getAgentByDomain(domain).orElseThrow(() -> new APIException(ResponseCode.BACKENDAPI_AGENT_NOT_FOUND, ResponseCode.BACKENDAPI_AGENT_NOT_FOUND.getErrorMessage()));
        //account有值為查詢下層代理，無值為查詢自己
        if (!StringUtils.isEmpty(agtAccountID)) {
            agentModel = agentModelList.stream().filter(ag -> ag.getAGT_AccountID().equals(agtAccountID)).findFirst().orElseThrow(() -> {
                throw new APIException(ResponseCode.BACKENDAPI_AGENT_NOT_FOUND, ResponseCode.BACKENDAPI_AGENT_NOT_FOUND.getErrorMessage());
            });
            agtGuid = agentModel.getAGT_GUID();
        }

        JackpotAgentReportResponse response = new JackpotAgentReportResponse();
        SqlRowSet rowSet = null;
        //前三個判斷為查詢自己
        //後三個判斷為查詢下層代理
        if (agtGuid == selfAguGuid && selflevel == 1) {
            rowSet = jackpotReportDAO.getJackpotAgent1Report(agtGuid, startDate, endTime);
            response.setAgentLevel(1);
        }

        if (agtGuid == selfAguGuid && selflevel == 2) {
            rowSet = jackpotReportDAO.getJackpotAgent2Report(agtGuid, startDate, endTime);
            response.setAgentLevel(2);
        }

        if (agtGuid == selfAguGuid && selflevel == 3) {
            rowSet = jackpotReportDAO.getJackpotAgent3Report(agtGuid, startDate, endTime);
            response.setAgentLevel(3);
        }

        if (agentModel != null && agentModel.getAGT_Level() == 1) {
            rowSet = jackpotReportDAO.getJackpotAgent2ReportByAgent1(agtGuid, startDate, endTime);
            response.setAgentLevel(2);
        }

        if (agentModel != null && agentModel.getAGT_Level() == 2) {
            rowSet = jackpotReportDAO.getJackpotAgent3ReportByAgent2(agtGuid, startDate, endTime);
            response.setAgentLevel(3);
        }

        if (agentModel != null && agentModel.getAGT_Level() == 3) {
            throw new APIException(ResponseCode.BACKENDAPI_NO_OPERATE_PERMISSION, ResponseCode.BACKENDAPI_NO_OPERATE_PERMISSION.getErrorMessage());
        }

        List<JackpotAgentReportData> jackpotReportList = new ArrayList<>();
        while (rowSet.next()) {
            JackpotAgentReportData jackpotReportData = new JackpotAgentReportData();
            jackpotReportData.setAccountID(rowSet.getString("AGT_AccountID"));
            if (agtGuid == selfAguGuid) {
                jackpotReportData.setAccountID(selfAccountID);
            }

            String agent2 = rowSet.getString("AGT_Agent2");
            if(agentModel != null && agentModel.getAGT_Level() == 1) {
                AgentModel agent2Model = agentModelList.stream().filter(ag -> ag.getAGT_GUID().equals(agent2)).findFirst().orElseThrow(() -> {
                            throw new APIException(ResponseCode.BACKENDAPI_AGENT_NOT_FOUND, ResponseCode.BACKENDAPI_AGENT_NOT_FOUND.getErrorMessage());});
                jackpotReportData.setAccountID(agent2Model.getAGT_AccountID());
            }

            String agent3 = rowSet.getString("AGT_Agent3");
            if(agentModel != null && agentModel.getAGT_Level() == 2) {
                AgentModel agent3Model = agentModelList.stream().filter(ag -> ag.getAGT_GUID().equals(agent3)).findFirst().orElseThrow(() -> {
                    throw new APIException(ResponseCode.BACKENDAPI_AGENT_NOT_FOUND, ResponseCode.BACKENDAPI_AGENT_NOT_FOUND.getErrorMessage());});
                jackpotReportData.setAccountID(agent3Model.getAGT_AccountID());
            }

            jackpotReportData.setCurrency(rowSet.getString("DT_Currency"));

            if (rowSet.getString("GM_JackpotPoolType").equals("Mini")) {
                jackpotReportData.setMini(rowSet.getBigDecimal("GM_Jackpot"));
            }

            if (rowSet.getString("GM_JackpotPoolType").equals("Minor")) {
                jackpotReportData.setMinor(rowSet.getBigDecimal("GM_Jackpot"));
            }

            if (rowSet.getString("GM_JackpotPoolType").equals("Major")) {
                jackpotReportData.setMajor(rowSet.getBigDecimal("GM_Jackpot"));
            }

            if (rowSet.getString("GM_JackpotPoolType").equals("Grand")) {
                jackpotReportData.setGrand(rowSet.getBigDecimal("GM_Jackpot"));
            }
            jackpotReportList.add(jackpotReportData);
        }
        List<JackpotAgentReportData> reportList = new ArrayList<>();

        for (JackpotAgentReportData jp : jackpotReportList) {
            Optional<JackpotAgentReportData> jpReportOp = reportList.stream().filter(report -> report.getAccountID().equals(jp.getAccountID())).findFirst();
            JackpotAgentReportData jackpotReportData;
            if (jpReportOp.isEmpty()) {
                jackpotReportData = new JackpotAgentReportData();
                jackpotReportData.setAccountID(jp.getAccountID());
                jackpotReportData.setCurrency(jp.getCurrency());
                jackpotReportData.setMini(jp.getMini());
                jackpotReportData.setMinor(jp.getMinor());
                jackpotReportData.setMajor(jp.getMajor());
                jackpotReportData.setGrand(jp.getGrand());
                reportList.add(jackpotReportData);
            } else {
                jackpotReportData = jpReportOp.get();
                jackpotReportData.setMini(jackpotReportData.getMini().add(jp.getMini()));
                jackpotReportData.setMinor(jackpotReportData.getMinor().add(jp.getMinor()));
                jackpotReportData.setMajor(jackpotReportData.getMajor().add(jp.getMajor()));
                jackpotReportData.setGrand(jackpotReportData.getGrand().add(jp.getGrand()));
            }
        }

        response.setJackpotRecord(reportList);
        return response;
    }

    public JackpotPlayerReportResponse getJackpotPlayerReport(JackpotReportRequest request, String domain) {
        AgentModel agentModel = agentDAO.getAgentByAccountAndDomain(request.getAccountID(), domain).orElseThrow(() -> {
            throw new APIException(ResponseCode.BACKENDAPI_AGENT_NOT_FOUND
                    , ResponseCode.BACKENDAPI_AGENT_NOT_FOUND.getErrorMessage());
        });
        JackpotPlayerReportResponse response = new JackpotPlayerReportResponse();
        SqlRowSet sqlRowSet = jackpotReportDAO.getJackpotPlayerRecord(request.getStartDate(), request.getEndDate(), agentModel.getAGT_GUID());

        Map<String, String> org = agentService.getAgentTreeByAgent3(agentModel.getAGT_GUID());
        List<String> orgTree = Arrays.asList(org.get("Agent1"), org.get("Agent2"), org.get("Agent3"));
        List<JackpotPlayerRecord> recordList = new ArrayList<>();
        while (sqlRowSet.next()) {
            JackpotPlayerRecord record = new JackpotPlayerRecord();
            record.setGameTurn(sqlRowSet.getString("gameTurn"));
            record.setGameTime(sqlRowSet.getDate("gameTime"));
            record.setCurrency(sqlRowSet.getString("currency"));
            record.setAgentTree(orgTree);
            record.setAccountID(sqlRowSet.getString("accountID"));
            Optional<GameListResponse> gameModel = gameCommonService.getAllGameList().stream()
                    .filter(model -> String.valueOf(model.getGameCode()).equals(sqlRowSet.getString("gameCode"))).findFirst();
            gameModel.ifPresent(gameListResponse -> record.setGameName(gameListResponse.getGameName()));
            record.setGameCode(sqlRowSet.getString("gameCode"));
            record.setJackpotPoolType(sqlRowSet.getString("jackpotPoolType"));
            record.setJackpot2(sqlRowSet.getBigDecimal("jackpot2"));
            recordList.add(record);
        }
        response.setJackpotRecord(recordList);

        return response;
    }
}
