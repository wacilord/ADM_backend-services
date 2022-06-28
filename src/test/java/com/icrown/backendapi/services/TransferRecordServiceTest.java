package com.icrown.backendapi.services;

import com.icrown.backendapi.dtos.DailyTradeListResponse;
import com.icrown.backendapi.dtos.OrgTree;
import com.icrown.backendapi.dtos.TransferDetailRequest;
import com.icrown.backendapi.dtos.TransferListRequest;
import com.icrown.common.services.AgentCommonService;
import com.icrown.gameapi.daos.AgentDAO;
import com.icrown.gameapi.daos.TransferRecordDAO;
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

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.mockito.Mockito;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.fail;

@SpringBootTest(classes = TransferRecordServiceTest.class)
@ComponentScan(basePackages = "com.icrown")
@EnableAutoConfiguration
public class TransferRecordServiceTest {
    @Autowired
    TransferRecordService transferRecordService;
    @MockBean
    AgentService agentService;
    @MockBean
    TransferRecordDAO transferRecordDAO;

    @Test
    public void getTransferList() {
        String agentGuid = "agentGuid";
        String code = "3101";
        Date startDate = new Date();
        Date endDate = new Date();
        String plyGuid = "";
        int pageIndex = 1;
        int pageSize = 10;
        int itemCount = 100;
        var resultSetMock = Mockito.mock(SqlRowSet.class);
        int level = 3;
        String mctGuid = "mctGuid";
        Mockito.when(resultSetMock.next()).thenReturn(true).thenReturn(false);

        TransferListRequest request = new TransferListRequest(code, startDate, endDate, pageIndex, pageSize);

        when(transferRecordDAO.getTradeRecordByQueryCount(agentGuid, code, startDate, endDate, plyGuid)).thenReturn(itemCount);
        when(transferRecordDAO.getTradeRecordByQuery(agentGuid, code, startDate, endDate, plyGuid, pageIndex, pageSize)).thenReturn(resultSetMock);

        var response = transferRecordService.getTransferList(request, agentGuid, level, mctGuid);
        assertTrue(response.getPageIndex() == pageIndex);
        assertTrue(response.getPageSize() == pageSize);
    }

    @Test
    public void getTransferDetail() {
        String agentGuid = "agentGuid";
        String transferID = "transferID";
        String mctGuid = "mctGuid";
        Map<String, String> map = new HashMap<>();
        map.put("agent3", "1234");
        int level = 1;
        TransferDetailRequest request = new TransferDetailRequest(transferID);
        var resultSetMock = Mockito.mock(SqlRowSet.class);
        Mockito.when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        when(transferRecordDAO.getTradeRecordByTransferID(agentGuid, level, transferID)).thenReturn(resultSetMock);
        when(resultSetMock.getString("agent")).thenReturn(agentGuid);

        OrgTree orgTree = new OrgTree();
        orgTree.setTree(map);
        orgTree.setAgent3(agentGuid);
        when(agentService.getAgentTreeListByAgent3(mctGuid)).thenReturn(Arrays.asList(orgTree));

        var response = transferRecordService.getTransferDetail(request, agentGuid, level, mctGuid);
        assertTrue(response.getRecords().size() == 1);
    }
}
