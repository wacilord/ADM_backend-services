package com.icrown.backendapi.services;

import com.icrown.backendapi.dtos.*;
import com.icrown.common.services.PlayerCommonService;
import com.icrown.gameapi.commons.responses.ResponseCode;
import com.icrown.gameapi.commons.utils.APIException;
import com.icrown.gameapi.commons.utils.PageUtil;
import com.icrown.gameapi.daos.TransferRecordDAO;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Frank
 */
@Service
public class TransferRecordService {
    @Autowired
    TransferRecordDAO transferRecordDAO;
    @Autowired
    PlayerCommonService playerCommonService;
    @Autowired
    AgentService agentService;
    @Autowired
    PageUtil pageUtil;

    public TransferListResponse getTransferList(TransferListRequest request, String agentGuid, int level, String mctGuid) {

        List<String> plyGuidList = new ArrayList<>();
        if (!StringUtil.isNullOrEmpty(request.getAccountID())) {
            plyGuidList.addAll(playerCommonService.getPlayerGuidAndLevel(agentGuid, request.getAccountID(), level));
        }

        int itemCount = transferRecordDAO.getTradeRecordByQueryCountAndLevel(agentGuid, request.getCode()
                , request.getStartDate(), request.getEndDate(), plyGuidList, level);

        List<TransferListDetailResponse> list = new ArrayList<>();
        if (itemCount > 0) {
            SqlRowSet rowSet = transferRecordDAO.getTradeRecordByQueryAndLevel(agentGuid, request.getCode(),
                                                                               request.getStartDate(), request.getEndDate(), plyGuidList, request.getPageIndex(), request.getPageSize(), level);
            List<OrgTree> rawOrgTreeList = agentService.getAgentTreeListByAgent3(mctGuid);

            while (rowSet.next()) {
                TransferListDetailResponse item = new TransferListDetailResponse();

                List<String> orgTreeList = agentService.getAgentTreeByAgent3AndLevel(rawOrgTreeList, level, rowSet.getString("agent"));

                item.setAgentTree(orgTreeList);
                item.setTransferID(rowSet.getString("transferID"));
                item.setCode(rowSet.getString("code"));
                item.setCurrency(rowSet.getString("currency"));
                item.setPoint(rowSet.getBigDecimal("point"));
                item.setStatus(rowSet.getInt("status"));
                item.setBeforeBalance(rowSet.getBigDecimal("beforeBalance"));
                item.setBalance(rowSet.getBigDecimal("balance"));
                item.setAfterBalance(rowSet.getBigDecimal("afterBalance"));
                item.setAccountID(rowSet.getString("accountID"));
                item.setCreateDateTime(rowSet.getDate("createDateTime"));
                list.add(item);
            }
        }

        TransferListResponse response = new TransferListResponse();
        response.setRecords(list);
        response.setPageIndex(request.getPageIndex());
        response.setPageSize(request.getPageSize());
        response.setPageCount(pageUtil.getPageCount(itemCount, request.getPageSize()));
        response.setItemCount(itemCount);
        return response;
    }

    public TransferDetailResponse getTransferDetail(TransferDetailRequest requests, String agentGuid, int level, String mctGuid) {
        List<TransferListDetailResponse> list = new ArrayList<>();
        SqlRowSet rowSet = transferRecordDAO.getTradeRecordByTransferID(agentGuid, level, requests.getTransferID());
        List<OrgTree> rawOrgTreeList = agentService.getAgentTreeListByAgent3(mctGuid);

        while (rowSet.next()) {
            List<String> orgTreeList = agentService.getAgentTreeByAgent3AndLevel(rawOrgTreeList, level, rowSet.getString("agent"));
            TransferListDetailResponse item = new TransferListDetailResponse();

            item.setAgentTree(orgTreeList);
            item.setTransferID(rowSet.getString("transferID"));
            item.setCode(rowSet.getString("code"));
            item.setCurrency(rowSet.getString("currency"));
            item.setPoint(rowSet.getBigDecimal("point"));
            item.setStatus(rowSet.getInt("status"));
            item.setBeforeBalance(rowSet.getBigDecimal("beforeBalance"));
            item.setBalance(rowSet.getBigDecimal("balance"));
            item.setAfterBalance(rowSet.getBigDecimal("afterBalance"));
            item.setAccountID(rowSet.getString("accountID"));
            item.setCreateDateTime(rowSet.getDate("createDateTime"));
            list.add(item);
        }
        if (list.isEmpty()) {
            throw new APIException(ResponseCode.BACKENDAPI_TRANSFERID_NOT_EXIST, ResponseCode.BACKENDAPI_TRANSFERID_NOT_EXIST.getErrorMessage());
        }
        TransferDetailResponse response = new TransferDetailResponse();
        response.setRecords(list);
        return response;
    }

}
