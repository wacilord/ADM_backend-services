package com.icrown.backendapi.services;

import com.icrown.backendapi.dtos.LoginLogRecordResponse;
import com.icrown.backendapi.dtos.LoginLogResponse;
import com.icrown.gameapi.commons.utils.PageUtil;
import com.icrown.gameapi.daos.LoginLogDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dennis
 */
@Service
public class LoginLogService {
    @Autowired
    LoginLogDAO loginLogDAO;
    @Autowired
    PageUtil pageUtil;

    /**
     * 取得LoginLog
     *
     * @param guid
     */
    public LoginLogResponse getLoginLog(String guid, int pageIndex, int pageSize) {
        LoginLogResponse response = new LoginLogResponse();
        int totalCount = loginLogDAO.getTotalCount(guid);
        if(totalCount == 0){
            return response;
        }

        List<LoginLogRecordResponse> list = new ArrayList<>(pageSize);
        SqlRowSet rowSet = loginLogDAO.getLoginLogList(guid, pageIndex, pageSize);
        while (rowSet.next()) {
            LoginLogRecordResponse item = new LoginLogRecordResponse();
            item.setCreateDateTime(rowSet.getDate("createDateTime"));
            item.setIp(rowSet.getString("ip"));
            item.setLoginID(rowSet.getString("loginID"));
            item.setErrorCode(rowSet.getInt("errorCode"));
            item.setErrorMessage(rowSet.getString("errorMessage"));
            list.add(item);
        }
        response.setRecords(list);
        response.setPageIndex(pageIndex);
        response.setPageSize(pageSize);
        response.setPageCount(pageUtil.getPageCount(totalCount, pageSize));
        response.setItemCount(totalCount);
        return response;
    }
}
