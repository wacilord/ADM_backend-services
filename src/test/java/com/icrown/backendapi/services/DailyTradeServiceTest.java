package com.icrown.backendapi.services;

import com.icrown.backendapi.dtos.DailyTradeListRequest;
import com.icrown.backendapi.dtos.DailyTradeListResponse;
import com.icrown.backendapi.dtos.OrgTree;
import com.icrown.common.configs.UserConfig;
import com.icrown.common.dtos.AccountingCodeData;
import com.icrown.common.dtos.GameListResponse;
import com.icrown.common.services.*;
import com.icrown.gameapi.commons.utils.APIException;
import com.icrown.gameapi.commons.utils.DateUtil;
import com.icrown.gameapi.commons.utils.RedisUtil;
import com.icrown.gameapi.daos.DailyTradeDAO;
import com.icrown.gameapi.models.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.mockito.Mockito;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = DailyTradeServiceTest.class)
@ComponentScan(basePackages = "com.icrown")
@EnableAutoConfiguration
public class DailyTradeServiceTest {
    @Autowired
    DailyTradeService dailyTradeService;
    @Autowired
    DateUtil dateUtil;
    @MockBean
    private UserConfig userConfig;
    @MockBean
    RedisPrefixService redisPrefixService;
    @MockBean
    PlayerCommonService playerCommonService;
    @MockBean
    DailyTradeDAO dailyTradeDAO;
    @MockBean
    GameCommonService gameCommonService;
    @MockBean
    RedisUtil redisUtil;
    @MockBean
    AgentService agentService;
    @MockBean
    AccountCodeCommonService accountCodeCommonService;
    @MockBean
    TranslateCommonService translateService;

    @Test
    public void getDailyTradeList() {
        String accountID = "accountID";
        Date startDate = new Date();
        Date endDate = new Date();

        Date currentDate = new Date();
        Date partitionStart = new Date();
        Date partitionEnd = new Date();
        int pageIndex = 1;
        int pageSize = 10;
        String agentGuid = "agentGuid";
        int level = 3;
        String mctGuid = "mctGuid";
        List<Integer> queryType = Arrays.asList(1, 2, 3, 4);

        DailyTradeListRequest request = new DailyTradeListRequest(accountID, startDate, endDate, currentDate, pageIndex, pageSize, queryType);
        int itemCount = 100;
        var resultSetMock = Mockito.mock(SqlRowSet.class);
        Mockito.when(resultSetMock.next()).thenReturn(true).thenReturn(false);

        MemberPlayerModel player = new MemberPlayerModel();
        player.setPLY_GUID("plyGuid");
        player.setAGT_Agent3(agentGuid);
        List<MemberPlayerModel> plays = Arrays.asList(player);
        Map<String, String> map = new HashMap<>();
        map.put("agent3", "1234");
        when(playerCommonService.getPlayerInfoByAgtGuidAndLevel(agentGuid, request.getAccountID(), level)).thenReturn(plays);
        when(dailyTradeDAO.getDailyTradeListCount2(Mockito.anyList(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(itemCount);
        when(dailyTradeDAO.getDailyTradeListWithAccCode(Mockito.anyList(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any())).thenReturn(resultSetMock);
        when(agentService.getAgentTreeByAgent3(player.getAGT_Agent3())).thenReturn(map);
        when(resultSetMock.getString("PLY_GUID")).thenReturn("plyGuid");
        when(resultSetMock.getString("AGT_Agent3")).thenReturn(agentGuid);

        OrgTree orgTree = new OrgTree();
        orgTree.setTree(map);
        orgTree.setAgent3(agentGuid);
        when(agentService.getAgentTreeListByAgent3(mctGuid)).thenReturn(Arrays.asList(orgTree));
        List<String> dateList = Arrays.asList(dateUtil.getDateFormat(currentDate, "yyyy-MM-dd"));

        DailyTradeListResponse response = dailyTradeService.getDailyTradeList(request, agentGuid, level, mctGuid);
        response.setDateList(dateList);
        assertTrue(response.getRecords().size() == 1);

    }

    @Test
    void getDailyTradeListDownload() throws ParseException {
        String agentGuid = "agentGuid";
        String accountID = "accountID";
        Date endDate = new Date();
        Date startDate = dateUtil.addTime(endDate, Calendar.DATE, -1);

        Date currentDate = new Date();
        int level = 3;
        String mctGuid = "mctGuid";

        DailyTradeListRequest dailyTradeListRequest = new DailyTradeListRequest(accountID, startDate, endDate, currentDate, 1, 10, Arrays.asList(3101, 3102, 2001));

        Date partitionEnd = dateUtil.getDatePart(dailyTradeListRequest.getEndDate());
        partitionEnd = dateUtil.addTime(partitionEnd, Calendar.DATE, 1);
        Date partitionStart = dateUtil.addTime(dailyTradeListRequest.getStartDate(), Calendar.DATE, -1);
        List<Integer> queryType = Arrays.asList(3101, 3102, 2001);
        MemberPlayerModel memberPlayerModel = new MemberPlayerModel();
        memberPlayerModel.setPLY_GUID("plyGuid");
        memberPlayerModel.setPLY_AccountID("plyAccountID");
        List<String> plyGuidList = Arrays.asList("plyGuid");
        List<MemberPlayerModel> players = Arrays.asList(memberPlayerModel);
        when(playerCommonService.getPlayerInfoByAgtGuidAndLevel("agentGuid", accountID, level)).thenReturn(players);
        when(dailyTradeDAO.getDailyTradeListCount2(any(), any(Date.class), any(Date.class), any(Date.class), any(Date.class), any())).thenReturn(10);
        GameListResponse gameListResponse = new GameListResponse();
        gameListResponse.setGameCode(1001);
        gameListResponse.setGameName("财神到");
        gameListResponse.setGameType(1);
        List<GameListResponse> gameList = new ArrayList<>();
        gameList.add(gameListResponse);
        when(gameCommonService.getAllGameList()).thenReturn(gameList);

        AccountingCodeData accountingCodeData = new AccountingCodeData();
        accountingCodeData.setAccCode(3102);
        accountingCodeData.setAccName("会员出金");
        AccountingCodeData accountingCodeData2 = new AccountingCodeData();
        accountingCodeData2.setAccCode(3101);
        accountingCodeData2.setAccName("会员入金");
        AccountingCodeData accountingCodeData3 = new AccountingCodeData();
        accountingCodeData3.setAccCode(2001);
        accountingCodeData3.setAccName("游戏纪录");
        List<AccountingCodeData> accountingCodeList = new ArrayList<>(3);
        accountingCodeList.add(accountingCodeData);
        accountingCodeList.add(accountingCodeData2);
        accountingCodeList.add(accountingCodeData3);
        when(accountCodeCommonService.getAllAccountingCode()).thenReturn(accountingCodeList);

        var resultSetMock = Mockito.mock(SqlRowSet.class);

        when(dailyTradeDAO.getDailyTradeListWithAccCode(any(), any(Date.class), any(Date.class), any(Date.class), any(Date.class), anyInt(), anyInt(), any())).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
        when(resultSetMock.getString("AGT_Agent3")).thenReturn(agentGuid);
        when(resultSetMock.getString("PLY_GUID")).thenReturn("plyGuid");
        when(resultSetMock.getInt("code")).thenReturn(3102).thenReturn(3101).thenReturn(2001);
        when(resultSetMock.getInt("gameCode")).thenReturn(1001);
        when(resultSetMock.getLong("seq")).thenReturn(1L).thenReturn(11L).thenReturn(111L);
        when(resultSetMock.getString("transferID")).thenReturn("transferId");
        when(resultSetMock.getDate("updateDate")).thenReturn(new java.sql.Date(new java.util.Date().getTime()));
        when(resultSetMock.getString("currency")).thenReturn("RMB");
        when(resultSetMock.getBigDecimal("tradePoint")).thenReturn(BigDecimal.valueOf(2L));

        Map<String, String> map = new HashMap<>();
        map.put("Agent3", "1234");
        OrgTree orgTree = new OrgTree();
        orgTree.setTree(map);
        orgTree.setAgent3(agentGuid);
        when(agentService.getAgentTreeListByAgent3(anyString())).thenReturn(List.of(orgTree));

        when(agentService.getAgentTreeByAgent3AndLevel(any(), anyInt(), anyString())).thenReturn(List.of("1234"));
        when(translateService.translateCsvHeaderByLanguage(anyString(), anyString())).thenReturn("单号,帐号,阶层,交易时间,帐务类型,游戏,币别,变动分数");
        when(translateService.getTranslationMap(anyString())).thenReturn(new HashMap());
        when(translateService.translateCsvHeaderByTranlationMap(any(), anyString())).thenReturn("单号,帐号,阶层,交易时间,帐务类型,游戏,币别,变动分数");
        when(translateService.fuzzyTranslationByTranlationMap(any(), anyString())).thenReturn("");
        String result = dailyTradeService.getDailyTradeListDownload(dailyTradeListRequest, agentGuid, level, mctGuid, "zh-CN");

        verify(dailyTradeDAO, times(2)).getDailyTradeListWithAccCode(any(), any(Date.class), any(Date.class), any(Date.class), any(Date.class), anyInt(), anyInt(), any());

        assertTrue(result.contains("1"));
        assertTrue(result.contains("11"));
        assertTrue(result.contains("111"));
    }

    @Test
    void queryGameDetail() {
        String agtGuid = "agtGuid";
        long gameTurn = 1000001L;
        var sqlRowSet = Mockito.mock(SqlRowSet.class);
        int level = 3;
        when(dailyTradeDAO.getRecordBySeqAndLevel(agtGuid, gameTurn, level)).thenReturn(sqlRowSet);
        when(sqlRowSet.next()).thenReturn(false).thenReturn(true);
        assertThrows(APIException.class, () -> {
            dailyTradeService.queryGameDetail(agtGuid, gameTurn, level, "zh-CN");
        });

        when(sqlRowSet.getString("gameCode")).thenReturn("1001");
        when(sqlRowSet.getString("PLY_GUID")).thenReturn("plyGuid");

        String key = "key";
        String reportUrl = "abc.com";

        when(redisPrefixService.getKey(any(), anyString())).thenReturn(key);
        when(userConfig.getTokenExpireSeconds()).thenReturn(1);
        when(userConfig.getReportUrl()).thenReturn(reportUrl);
        doNothing().when(redisUtil).hPutAll(anyString(), any(Map.class));
        String gameDetailUrl = reportUrl + "?token=";
        assertTrue(dailyTradeService.queryGameDetail(agtGuid, gameTurn, level, "zh-CN").contains(gameDetailUrl));
    }
}
