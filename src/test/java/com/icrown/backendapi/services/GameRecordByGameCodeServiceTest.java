package com.icrown.backendapi.services;

import com.icrown.backendapi.dtos.*;
import com.icrown.common.dtos.GameListResponse;
import com.icrown.common.services.AgentGameRelationCommonService;
import com.icrown.common.services.GameCommonService;
import com.icrown.common.services.GameTypeCommonService;
import com.icrown.common.services.PlayerCommonService;
import com.icrown.gameapi.commons.utils.DateUtil;
import com.icrown.gameapi.daos.DailyTradeDAO;
import com.icrown.gameapi.daos.GameReportDAO;
import com.icrown.gameapi.daos.GameReportDayDAO;
import com.icrown.gameapi.models.GameTypeModel;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = GameRecordByGameCodeServiceTest.class)
@ComponentScan(basePackages = "com.icrown")
@EnableAutoConfiguration
public class GameRecordByGameCodeServiceTest {
    @Autowired
    GameRecordService gameRecordService;
    //@MockBean(name="forMock")
    //GameRecordService partialGameRecordService;
    @MockBean
    DailyTradeDAO dailyTradeDAO;
    @MockBean
    GameReportDAO gameReportDAO;
    @MockBean
    GameReportDayDAO gameReportDayDAO;
    @Autowired
    DateUtil dateUtil;
    @MockBean
    AgentService agentService;
    @MockBean
    GameTypeCommonService gameTypeCommonService;
    @MockBean
    AgentGameRelationCommonService agentGameRelationCommonService;
    @MockBean
    GameCommonService gameCommonService;
    @MockBean
    PlayerCommonService playerCommonService;
    @MockBean
    TranslateService translateService;;


    private void initGameType() throws IOException {
        Mockito.doNothing().when(gameTypeCommonService).checkGameType(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString());
        Mockito.doNothing().when(agentGameRelationCommonService).checkGameTypeListEnable(Mockito.anyString(), Mockito.anyString(), anyInt(), Mockito.any());

    }

    @Test
    public void getGameDetail() {
        String agentGuid = "agentGuid";
        long seq = 10000;
        int level = 3;
        var resultSetMock = Mockito.mock(SqlRowSet.class);
        Mockito.when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        when(dailyTradeDAO.getRecordBySeqAndLevel(agentGuid, seq, level)).thenReturn(resultSetMock);
        when(resultSetMock.getInt("workType")).thenReturn(2);
        GameDetailRequest request = new GameDetailRequest(seq);
        var response = gameRecordService.getGameDetail(request, agentGuid, level);
        assertTrue(response.getRecords().size() == 1);
    }

    @Test
    public void getHistorySumBets() {
        String playerGuid = "playerGuid";
        Date startDate = new Date();
        Date endDate = new Date();
        BigDecimal result = new BigDecimal(100);
        when(gameReportDAO.getHistorySumBets(playerGuid, startDate, endDate)).thenReturn(result);
        BigDecimal returnResult = gameRecordService.getHistorySumBets(playerGuid, startDate, endDate);
        assertTrue(returnResult.compareTo(result) == 0);

    }

    @Test
    public void getHistorySumBetsResult() {
        String playerGuid = "playerGuid";
        Date startDate = new Date();
        Date endDate = new Date();
        BigDecimal result = new BigDecimal(100);
        when(gameReportDAO.getHistorySumNetWin(playerGuid, startDate, endDate)).thenReturn(result);
        BigDecimal returnResult = gameRecordService.getHistorySumResult(playerGuid, startDate, endDate);
        assertTrue(returnResult.compareTo(result) == 0);
    }

    @Test
    public void getBetsResultGroupByGameType() {
        String playerGuid = "playerGuid";
        Date startDate = new Date();
        Date endDate = new Date();
        var resultSetMock = Mockito.mock(SqlRowSet.class);
        Mockito.when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        when(gameReportDAO.getNetWinGroupByGameType(playerGuid, startDate, endDate)).thenReturn(resultSetMock);
        SqlRowSet rowSet = gameRecordService.getResultGroupByGameType(playerGuid, startDate, endDate);
        assertTrue(rowSet.next() == true);
    }

    @Test
    public void getBetsResultGroupByDate() {
        String playerGuid = "playerGuid";
        Date startDate = new Date();
        Date endDate = new Date();
        var resultSetMock = Mockito.mock(SqlRowSet.class);
        Mockito.when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        when(gameReportDayDAO.getNetWinGroupByDate(playerGuid, startDate, endDate)).thenReturn(resultSetMock);
        SqlRowSet rowSet = gameRecordService.getWinGroupByDate(playerGuid, startDate, endDate);
        assertTrue(rowSet.next() == true);
    }

    @Test
    public void getBetsGroupByGame() {
        String playerGuid = "playerGuid";
        Date startDate = new Date();
        Date endDate = new Date();
        var resultSetMock = Mockito.mock(SqlRowSet.class);
        Mockito.when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        when(gameReportDAO.getBetsGroupByGame(playerGuid, startDate, endDate)).thenReturn(resultSetMock);
        SqlRowSet rowSet = gameRecordService.getBetsGroupByGame(playerGuid, startDate, endDate);
        assertTrue(rowSet.next() == true);
    }

    @Test
    public void getGameReportInfo() throws IOException {
        initGameType();
        List<Integer> gameType = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> gameCode = Arrays.asList(1001, 1002, 2001, 3001, 4001, 5001);
        Date startDate = new Date();
        Date endDate = new Date();
        String agentGuid = "agentGuid";
        int level = 3;
        String mctGuid = "mctGuid";
        GameReportRequest request = new GameReportRequest(gameType, gameCode, "", startDate, endDate, 0);
        var resultSetMock = mock(SqlRowSet.class);
        when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        when(resultSetMock.getInt("gameType")).thenReturn(1);
        when(resultSetMock.getString("gameCode")).thenReturn("1001");
        when(resultSetMock.getString("currency")).thenReturn("RMB");
        when(resultSetMock.getBigDecimal("sumBets")).thenReturn(new BigDecimal(0));
        when(resultSetMock.getBigDecimal("sumValidBets")).thenReturn(new BigDecimal(0));
        when(resultSetMock.getBigDecimal("sumBetsResult")).thenReturn(new BigDecimal(0));
        when(resultSetMock.getBigDecimal("sumJackpotContribute")).thenReturn(new BigDecimal(0));
        when(resultSetMock.getBigDecimal("sumJackpot")).thenReturn(new BigDecimal(0));
        when(resultSetMock.getBigDecimal("sumJackpot2")).thenReturn(new BigDecimal(0));
        when(resultSetMock.getBigDecimal("sumNetWin")).thenReturn(new BigDecimal(0));
        when(resultSetMock.getBigDecimal("sumPureNetWin")).thenReturn(new BigDecimal(0));
        when(resultSetMock.getInt("items")).thenReturn(1);
        when(gameReportDAO.getGameReportInfoByGameCode(gameType, gameCode, startDate, endDate, agentGuid, level)).thenReturn(resultSetMock);

        var response = gameRecordService.getGameReportInfo(request, agentGuid, level, mctGuid, "domain");
        assertNotNull(response);

    }

    @Test
    public void getGameAndPlayerSummaryReport() throws IOException {
        initGetGameSummaryReport();
        initGetPlayerSummaryReportForPlayer();
        List<Integer> gameType = Arrays.asList(1, 2, 3, 4);
        int gameHour = 0;
        Date startDate = dateUtil.getDatePart(new Date());
        Date endDate = dateUtil.addTime(startDate, Calendar.DATE, 10);
        int pageIndex = 1;
        int pageSize = 10;
        SummaryReportRequest summaryReportRequest = new SummaryReportRequest(gameType, startDate, endDate, pageIndex, pageSize);
        String agentGuid = "agentGuid";
        int level = 3;
        String mctGuid = "mctGuid";
        String currency = "currency";

        var resultSetMock = Mockito.mock(SqlRowSet.class);
        Mockito.when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        Mockito.when(resultSetMock.getBigDecimal("commission")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getBigDecimal("sumBets")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getBigDecimal("sumValidBets")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getBigDecimal("sumWin")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getBigDecimal("sumJackpotContribute")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getBigDecimal("sumJackpot")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getBigDecimal("sumJackpot2")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getBigDecimal("sumJackpot3")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getBigDecimal("sumNetWin")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getInt("items")).thenReturn(1);

        var resultSetMock2 = Mockito.mock(SqlRowSet.class);
        Mockito.when(resultSetMock2.next()).thenReturn(true).thenReturn(false);
        Mockito.when(resultSetMock2.getBigDecimal("commission")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock2.getBigDecimal("sumBets")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock2.getBigDecimal("sumValidBets")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock2.getBigDecimal("sumWin")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock2.getBigDecimal("sumJackpotContribute")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock2.getBigDecimal("sumJackpot")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock2.getBigDecimal("sumJackpot2")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock2.getBigDecimal("sumJackpot3")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock2.getBigDecimal("sumNetWin")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock2.getInt("items")).thenReturn(1);

        var resultSetMock3 = Mockito.mock(SqlRowSet.class);
        Mockito.when(resultSetMock3.next()).thenReturn(true).thenReturn(false);
        Mockito.when(resultSetMock3.getBigDecimal("commission")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock3.getBigDecimal("sumBets")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock3.getBigDecimal("sumValidBets")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock3.getBigDecimal("sumWin")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock3.getBigDecimal("sumJackpotContribute")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock3.getBigDecimal("sumJackpot")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock3.getBigDecimal("sumJackpot2")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock3.getBigDecimal("sumJackpot3")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock3.getBigDecimal("sumNetWin")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock3.getInt("items")).thenReturn(1);

        var resultSetMock4 = Mockito.mock(SqlRowSet.class);
        Mockito.when(resultSetMock4.next()).thenReturn(true).thenReturn(false);
        Mockito.when(resultSetMock4.getBigDecimal("commission")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock4.getBigDecimal("sumBets")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock4.getBigDecimal("sumValidBets")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock4.getBigDecimal("sumWin")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock4.getBigDecimal("sumJackpotContribute")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock4.getBigDecimal("sumJackpot")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock4.getBigDecimal("sumJackpot2")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock4.getBigDecimal("sumJackpot3")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock4.getBigDecimal("sumNetWin")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock4.getInt("items")).thenReturn(1);
        when(gameReportDAO.getGameSummaryReport(gameType, startDate, endDate, agentGuid, level)).thenReturn(resultSetMock);
        when(gameReportDAO.getTotalSummaryReport(gameType, startDate, endDate, agentGuid, level)).thenReturn(resultSetMock2);
        when(gameReportDAO.getPlayerSummaryReport(gameType, startDate, endDate, agentGuid, pageIndex, pageSize)).thenReturn(resultSetMock3);
        when(gameReportDAO.getTotalSummaryReport(gameType, startDate, endDate, agentGuid, level)).thenReturn(resultSetMock4);

        var res = gameRecordService.getGameAndPlayerSummaryReport(summaryReportRequest, agentGuid, level, mctGuid, currency);
        assertTrue(res.getGameRecord().getRecords().size() == 1);
        assertTrue(res.getPlayerRecord().getRecords().size() == 1);
    }

    private void initGetGameSummaryReport() {
        var resultSetMock = Mockito.mock(SqlRowSet.class);
        Mockito.when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        when(gameReportDAO.getGameSummaryReport(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyString(), Mockito.anyInt()))
                .thenReturn(resultSetMock);
    }

    private void initGetPlayerSummaryReportForPlayer() {
        var resultSetMock = Mockito.mock(SqlRowSet.class);
        Mockito.when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        Mockito.when(resultSetMock.getString("currency")).thenReturn("RMB");
        Mockito.when(resultSetMock.getBigDecimal("commission")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getBigDecimal("sumBets")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getBigDecimal("sumValidBets")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getBigDecimal("sumWin")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getBigDecimal("sumJackpotContribute")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getBigDecimal("sumJackpot")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getBigDecimal("sumJackpot2")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getBigDecimal("sumJackpot3")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getBigDecimal("sumNetWin")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getInt("items")).thenReturn(1);

        when(gameReportDAO.getPlayerSummaryReportCount(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyString()))
                .thenReturn(1);
        when(gameReportDAO.getPlayerSummaryReport(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(resultSetMock);
    }

    @Test
    public void getGameSummaryReport() throws IOException {
        //initGameType();
        initGetGameSummaryReport();

        var gameType = Arrays.asList(1, 2, 3, 4);
        Date startDate = dateUtil.addTime(dateUtil.getDatePart(new Date()), Calendar.DATE, -1);
        Date endDate = dateUtil.getDatePart(new Date());

        GameSummaryReportRequest request = new GameSummaryReportRequest(gameType, startDate, endDate);
        String agentGuid = "agentGuid";
        int level = 3;
        String mctGuid = "mctGuid";
        String currency = "currency";
        var resultSetMock = Mockito.mock(SqlRowSet.class);
        Mockito.when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        Mockito.when(resultSetMock.getBigDecimal("sumBets")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getBigDecimal("sumValidBets")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getBigDecimal("sumWin")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getBigDecimal("sumJackpotContribute")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getBigDecimal("sumJackpot")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getBigDecimal("sumJackpot2")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getBigDecimal("sumJackpot3")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getBigDecimal("commission")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getBigDecimal("sumNetWin")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getInt("items")).thenReturn(1);

        var resultSetMock2 = Mockito.mock(SqlRowSet.class);
        Mockito.when(resultSetMock2.next()).thenReturn(true).thenReturn(false);
        Mockito.when(resultSetMock2.getBigDecimal("sumBets")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock2.getBigDecimal("sumValidBets")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock2.getBigDecimal("sumWin")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock2.getBigDecimal("sumJackpotContribute")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock2.getBigDecimal("sumJackpot")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock2.getBigDecimal("sumJackpot2")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock2.getBigDecimal("sumJackpot3")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock2.getBigDecimal("sumNetWin")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock2.getInt("items")).thenReturn(1);
        when(gameReportDAO.getGameSummaryReport(gameType, startDate, endDate, agentGuid, level)).thenReturn(resultSetMock);
        when(gameReportDAO.getTotalSummaryReport(gameType, startDate, endDate, agentGuid, level)).thenReturn(resultSetMock2);
        var response = gameRecordService.getGameSummaryReport(request, agentGuid, level, mctGuid, currency);
        assertEquals(response.getRecords().size(), 1);
    }

    @Test
    public void getPlayerSummaryReportForPlayer() throws IOException {
        //initGameType();
        initGetPlayerSummaryReportForPlayer();

        var gameType = Arrays.asList(1, 2, 3, 4);
        int timeHour = 0;
        Date startDate = dateUtil.addTime(dateUtil.getDatePart(new Date()), Calendar.DATE, -1);
        Date endDate = dateUtil.getDatePart(new Date());
        int pageIndex = 1;
        int pageSize = 10;

        SummaryReportRequest request = new SummaryReportRequest(gameType, startDate, endDate, pageIndex, pageSize);
        String agentGuid = "agentGuid";
        int level = 3;
        String mctGuid = "mctGuid";
        String currency = "currency";

        var resultSetMock = Mockito.mock(SqlRowSet.class);
        Mockito.when(resultSetMock.getBigDecimal("sumBets")).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(resultSetMock.getBigDecimal("sumValidBets")).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(resultSetMock.getBigDecimal("sumWin")).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(resultSetMock.getBigDecimal("sumJackpotContribute")).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(resultSetMock.getBigDecimal("sumJackpot")).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(resultSetMock.getBigDecimal("sumJackpot2")).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(resultSetMock.getBigDecimal("sumJackpot3")).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(resultSetMock.getBigDecimal("commission")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getBigDecimal("sumNetWin")).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(resultSetMock.getInt("items")).thenReturn(0);
        Mockito.when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        when(gameReportDAO.getTotalSummaryReport(gameType, startDate, endDate, agentGuid, level))
                .thenReturn(resultSetMock);

        var response = gameRecordService.getPlayerSummaryReport(request, agentGuid, level, mctGuid, currency);
        assertEquals(response.getItemCount(), 1);
    }

    @Test
    public void getPlayerSummaryReportForAgent() throws IOException {
        initGameType();

        var gameType = Arrays.asList(1, 2, 3, 4);
        int timeHour = 0;
        Date startDate = dateUtil.addTime(dateUtil.getDatePart(new Date()), Calendar.DATE, -1);
        Date endDate = dateUtil.getDatePart(new Date());
        int pageIndex = 1;
        int pageSize = 10;
        SummaryReportRequest request = new SummaryReportRequest(gameType, startDate, endDate, pageIndex, pageSize);
        String agentGuid = "agentGuid";
        int level = 2;
        String mctGuid = "mctGuid";
        String currency = "currency";

        var resultSetMock = Mockito.mock(SqlRowSet.class);
        Mockito.when(resultSetMock.getBigDecimal("sumBets")).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(resultSetMock.getBigDecimal("sumValidBets")).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(resultSetMock.getBigDecimal("sumWin")).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(resultSetMock.getBigDecimal("sumJackpotContribute")).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(resultSetMock.getBigDecimal("sumJackpot")).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(resultSetMock.getBigDecimal("sumJackpot2")).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(resultSetMock.getBigDecimal("commission")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getBigDecimal("sumNetWin")).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(resultSetMock.getInt("items")).thenReturn(0);
        Mockito.when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        when(gameReportDAO.getTotalSummaryReport(gameType, startDate, endDate, agentGuid, level))
                .thenReturn(resultSetMock);

        var response = gameRecordService.getPlayerSummaryReport(request, agentGuid, level, mctGuid, currency);
        assertTrue(response.getItemCount() == 0);
    }

    @Test
    public void getSinglePlayerSummaryReport() throws IOException {
        initGameType();

        var gameType = Arrays.asList(1, 2, 3, 4);
        Date startDate = dateUtil.addTime(dateUtil.getDatePart(new Date()), Calendar.DATE, -1);
        Date endDate = dateUtil.getDatePart(new Date());
        String playerID = "playerID";
        SinglePlayerSummaryReportRequest request = new SinglePlayerSummaryReportRequest(gameType, startDate, endDate, playerID);
        String agentGuid = "agentGuid";
        int level = 2;
        String mctGuid = "mctGuid";
        Map<String, String> map = new HashMap<>();
        map.put("agent3", "1234");

        Mockito.doNothing().when(playerCommonService).checkUserExistByAgent(agentGuid, playerID, level);
        var resultSetMock = Mockito.mock(SqlRowSet.class);
        Mockito.when(gameReportDAO.getSinglePlayerSummaryReport(agentGuid, level, gameType, startDate, endDate, playerID))
                .thenReturn(resultSetMock);

        Mockito.when(resultSetMock.getString("agent")).thenReturn(agentGuid);
        OrgTree orgTree = new OrgTree();
        orgTree.setTree(map);
        orgTree.setAgent3(agentGuid);
        Mockito.when(agentService.getAgentTreeListByAgent3(mctGuid)).thenReturn(Collections.singletonList(orgTree));
        Mockito.when(resultSetMock.getBigDecimal("sumBets")).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(resultSetMock.getBigDecimal("sumValidBets")).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(resultSetMock.getBigDecimal("sumWin")).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(resultSetMock.getBigDecimal("sumJackpotContribute")).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(resultSetMock.getBigDecimal("sumJackpot")).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(resultSetMock.getBigDecimal("commission")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getBigDecimal("sumNetWin")).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(resultSetMock.getInt("items")).thenReturn(0);
        Mockito.when(resultSetMock.next()).thenReturn(true).thenReturn(false);


        var response = gameRecordService.getSinglePlayerSummaryReport(request, agentGuid, level, mctGuid);
        assertTrue(response.getRecords().size() == 1);
    }

    @Test
    public void getDailyReport() throws IOException {
        initGameType();
        var resultSetMock = Mockito.mock(SqlRowSet.class);
        Mockito.when(resultSetMock.getBigDecimal("sumBets")).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(resultSetMock.getBigDecimal("sumValidBets")).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(resultSetMock.getBigDecimal("sumWin")).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(resultSetMock.getBigDecimal("sumJackpotContribute")).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(resultSetMock.getBigDecimal("sumJackpot")).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(resultSetMock.getBigDecimal("sumJackpot2")).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(resultSetMock.getBigDecimal("sumJackpot3")).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(resultSetMock.getBigDecimal("commission")).thenReturn(new BigDecimal(0));
        Mockito.when(resultSetMock.getBigDecimal("sumNetWin")).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(resultSetMock.getInt("items")).thenReturn(0);
        Mockito.when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        when(gameReportDayDAO.getDailyReportByDate(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyString(), Mockito.anyInt()))
                .thenReturn(resultSetMock);

        var gameType = Arrays.asList(1, 2, 3, 4);
        Date startDate = new Date();
        Date endDate = new Date();
        String agentGuid = "agentGuid";
        int level = 3;
        String mctGuid = "mctGuid";
        String currency = "currency";
        DailyReportRequest request = new DailyReportRequest(gameType, startDate, endDate);
        var response = gameRecordService.getDailyReport(request, agentGuid, level, mctGuid, currency);
        assertTrue(response.getRecords().size() == 1);

    }


    @Test
    void getGameReportDownload() throws IOException {
        String agentGuid = "agentGuid";
        int level = 3;
        String mctGuid = "mctGuid";
        Date startDate = new Date();
        Date endDate = dateUtil.addTime(startDate, Calendar.DATE, 1);
        GameReportRequest request = new GameReportRequest(Arrays.asList(1, 2), Arrays.asList(1001, 1002, 2001), "", startDate, endDate, 0);

        GameTypeModel gameTypeModel1 = new GameTypeModel();
        gameTypeModel1.setGM_TypeSerial(1);
        gameTypeModel1.setGM_GameType("SlotGame");
        gameTypeModel1.setGM_GameTypeName("老虎机游戏");
        gameTypeModel1.setGM_TableName("GR_SlotGame");
        GameTypeModel gameTypeModel2 = new GameTypeModel();
        gameTypeModel2.setGM_TypeSerial(2);
        gameTypeModel2.setGM_GameType("FisshingGame");
        gameTypeModel2.setGM_GameTypeName("捕鱼机游戏");
        gameTypeModel2.setGM_TableName("GR_FishingGame");
        List<GameTypeModel> gameTypeList = new ArrayList<>();
        gameTypeList.add(gameTypeModel1);
        gameTypeList.add(gameTypeModel2);

        GameListResponse gr1001 = new GameListResponse();
        gr1001.setGameType(1);
        gr1001.setGameCode(1001);
        gr1001.setGameName("财神到");
        GameListResponse gr1002 = new GameListResponse();
        gr1002.setGameType(1);
        gr1002.setGameCode(1002);
        gr1002.setGameName("盗墓笔记");
        GameListResponse gr2001 = new GameListResponse();
        gr2001.setGameType(2);
        gr2001.setGameCode(2001);
        gr2001.setGameName("捕鱼来了");
        List<GameListResponse> gameList = new ArrayList<>();
        gameList.add(gr1001);
        gameList.add(gr1002);
        gameList.add(gr2001);

        var rowSetMock = Mockito.mock(SqlRowSet.class);
        when(rowSetMock.next()).thenReturn(false).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
        when(rowSetMock.getInt("gameType")).thenReturn(1).thenReturn(1).thenReturn(2);
        when(rowSetMock.getInt("gameCode")).thenReturn(1001).thenReturn(1002).thenReturn(2001);
        when(rowSetMock.getBigDecimal("sumBets")).thenReturn(BigDecimal.valueOf(30L));
        when(rowSetMock.getBigDecimal("sumWin")).thenReturn(BigDecimal.valueOf(300L));
        when(rowSetMock.getBigDecimal("sumValidBets")).thenReturn(BigDecimal.valueOf(30L));
        when(rowSetMock.getBigDecimal("sumJackpotContribute")).thenReturn(BigDecimal.valueOf(0L));
        when(rowSetMock.getBigDecimal("sumJackpot")).thenReturn(BigDecimal.valueOf(0L));
        when(rowSetMock.getBigDecimal("sumJackpot2")).thenReturn(BigDecimal.valueOf(0L));
        when(rowSetMock.getBigDecimal("sumJackpot3")).thenReturn(BigDecimal.valueOf(0L));
        when(rowSetMock.getBigDecimal("commission")).thenReturn(BigDecimal.valueOf(0L));
        when(rowSetMock.getBigDecimal("sumNetWin")).thenReturn(BigDecimal.valueOf(300L));
        when(rowSetMock.getBigDecimal("sumPureNetWin")).thenReturn(BigDecimal.valueOf(300L));

        doNothing().when(gameTypeCommonService).checkGameTypeListValid(request.getGameType());
        doNothing().when(agentGameRelationCommonService).checkGameTypeListEnable(mctGuid, agentGuid, level, request.getGameType());
        when(gameTypeCommonService.getAllGameType()).thenReturn(gameTypeList);
        when(gameCommonService.getAllGameList()).thenReturn(gameList);
        when(gameReportDAO.getGameReportInfoByGameCode(request.getGameType(), request.getGameCode(), request.getStartDate(),
                                                       request.getEndDate(), agentGuid, level)).thenReturn(rowSetMock);
        when(translateService.translateCsvHeaderByLanguage(anyString(), anyString())).thenReturn("游戏类型,游戏名称,币别,总投注额,有效投注额,赢分,天九福彩,天九红包,彩金贡献,天九福彩(新),佣金,盈利,純盈利,交易笔数,庄家优势");
        assertFalse(gameRecordService.getGameReportDownload(request, agentGuid, level, mctGuid, "1.com", "zh-CN").contains("财神到"));
        when(translateService.getTranslationMap(anyString())).thenReturn(new HashMap<>());
        String result = gameRecordService.getGameReportDownload(request, agentGuid, level, mctGuid, "1.com", "zh-CN");

        System.out.println(result);
        assertTrue(result.contains("30.0000,30.0000,300.0000,0.0000,0.0000,0.0000,0.0000,0.0000,300.0000,300.0000,0,-900.0000"));
    }

    @Test
    void getDailyReportDownload() throws IOException {
        List<Integer> gameType = Arrays.asList(1, 2);
        Date startDate = new Date();
        Date endDate = dateUtil.addTime(startDate, Calendar.DATE, 1);
        String agentGuid = "agentGuid";
        int level = 1;
        String currency = "RMB";
        java.sql.Date date = new java.sql.Date(new Date().getTime());
        String mctGuid = "mctGuid";
        DailyReportRequest request = new DailyReportRequest(gameType, startDate, endDate);

        doNothing().when(gameTypeCommonService).checkGameTypeListValid(request.getGameType());
        doNothing().when(agentGameRelationCommonService).checkGameTypeListEnable(mctGuid, agentGuid, level, request.getGameType());
        var rowSet = mock(SqlRowSet.class);
        when(rowSet.next()).thenReturn(false).thenReturn(true).thenReturn(true).thenReturn(false);
        when(rowSet.getDate("accountDate")).thenReturn(date);
        when(rowSet.getString("currency")).thenReturn(currency);
        when(rowSet.getBigDecimal("sumBets")).thenReturn(BigDecimal.valueOf(10L));
        when(rowSet.getBigDecimal("sumValidBets")).thenReturn(BigDecimal.valueOf(10L));
        when(rowSet.getBigDecimal("sumWin")).thenReturn(BigDecimal.valueOf(10L));
        when(rowSet.getBigDecimal("sumJackpotContribute")).thenReturn(BigDecimal.ZERO);
        when(rowSet.getBigDecimal("sumJackpot")).thenReturn(BigDecimal.ZERO);
        when(rowSet.getBigDecimal("sumJackpot2")).thenReturn(BigDecimal.ZERO);
        when(rowSet.getBigDecimal("sumJackpot3")).thenReturn(BigDecimal.ZERO);
        when(rowSet.getBigDecimal("commission")).thenReturn(BigDecimal.ZERO);
        when(rowSet.getBigDecimal("sumNetWin")).thenReturn(BigDecimal.valueOf(100L));
        when(rowSet.getInt("items")).thenReturn(1);
        when(gameReportDayDAO.getDailyReportByDate(request.getGameType(), request.getStartDate(), request.getEndDate(), agentGuid, level)).thenReturn(rowSet);
        when(translateService.translateCsvHeaderByLanguage(anyString(), anyString())).thenReturn("帐务日期,币别,总投注额,有效投注额,赢分,天九福彩,天九红包,彩金贡献,天九福彩(新),佣金,盈利,純盈利,交易笔数");
        assertFalse(gameRecordService.getDailyReportDownload(request, agentGuid, level, mctGuid, "zh-CN").contains("100"));
        String result = (gameRecordService.getDailyReportDownload(request, agentGuid, level, mctGuid, "zh-CN"));
        assertTrue(result.contains("10"));
        assertTrue(result.contains("100"));
        assertTrue(result.contains("RMB"));
        assertTrue(result.contains("1"));
        assertTrue(result.contains(dateUtil.getDateFormat(date, "yyyy-MM-dd")));
    }


    @Test
    void getGameSummaryReportDownload() throws IOException {
        List<Integer> gameType = Arrays.asList(1, 2);
        Date startDate = new Date();
        Date endDate = dateUtil.addTime(startDate, Calendar.DATE, 1);
        String agentGuid = "agentGuid";
        String mctGuid = "mctGuid";
        int level = 1;
        var rowSet = mock(SqlRowSet.class);
        GameSummaryReportRequest request = new GameSummaryReportRequest(gameType, startDate, endDate);

        doNothing().when(gameTypeCommonService).checkGameTypeListValid(request.getGameType());

        doNothing().when(agentGameRelationCommonService).checkGameTypeListEnable(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.any());
        when(gameReportDAO.getGameSummaryReport(gameType, startDate, endDate, agentGuid, level)).thenReturn(rowSet);

        GameTypeModel gameTypeModel1 = new GameTypeModel();
        gameTypeModel1.setGM_TypeSerial(1);
        gameTypeModel1.setGM_GameType("SlotGame");
        gameTypeModel1.setGM_GameTypeName("老虎机游戏");
        gameTypeModel1.setGM_TableName("GR_SlotGame");
        GameTypeModel gameTypeModel2 = new GameTypeModel();
        gameTypeModel2.setGM_TypeSerial(2);
        gameTypeModel2.setGM_GameType("FisshingGame");
        gameTypeModel2.setGM_GameTypeName("捕鱼机游戏");
        gameTypeModel2.setGM_TableName("GR_FishingGame");
        List<GameTypeModel> gameTypeList = new ArrayList<>();
        gameTypeList.add(gameTypeModel1);
        gameTypeList.add(gameTypeModel2);
        when(gameTypeCommonService.getAllGameType()).thenReturn(gameTypeList);

        when(rowSet.next()).thenReturn(false);
        when(translateService.translateCsvHeaderByLanguage(anyString(), anyString())).thenReturn("游戏类型,币别,总投注额,有效投注额,赢分,天九福彩,天九红包,彩金贡献,天九福彩(新),佣金,盈利,純盈利,交易笔数");

        assertFalse(gameRecordService.getGameSummaryReportDownload(request, agentGuid, level, mctGuid, "zh-CN").contains("老虎机游戏"));

        when(rowSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(rowSet.getInt("gameType")).thenReturn(1).thenReturn(2);
        when(rowSet.getString("currency")).thenReturn("RMB");
        when(rowSet.getBigDecimal("sumBets")).thenReturn(BigDecimal.valueOf(10));
        when(rowSet.getBigDecimal("sumValidBets")).thenReturn(BigDecimal.valueOf(10));
        when(rowSet.getBigDecimal("sumWin")).thenReturn(BigDecimal.valueOf(20));
        when(rowSet.getBigDecimal("sumJackpotContribute")).thenReturn(BigDecimal.valueOf(0));
        when(rowSet.getBigDecimal("sumJackpot")).thenReturn(BigDecimal.valueOf(0));
        when(rowSet.getBigDecimal("sumJackpot2")).thenReturn(BigDecimal.valueOf(0));
        when(rowSet.getBigDecimal("sumJackpot3")).thenReturn(BigDecimal.valueOf(0));
        when(rowSet.getBigDecimal("commission")).thenReturn(BigDecimal.valueOf(0));
        when(rowSet.getBigDecimal("sumNetWin")).thenReturn(BigDecimal.valueOf(30));
        when(rowSet.getInt("items")).thenReturn(5);
        when(translateService.translateCsvHeaderByLanguage(anyString(), anyString())).thenReturn("游戏类型,币别,总投注额,有效投注额,赢分,天九福彩,天九红包,彩金贡献,天九福彩(新),佣金,盈利,純盈利,交易笔数");

        var result = gameRecordService.getGameSummaryReportDownload(request, agentGuid, level, mctGuid, "zh-CN");
        assertTrue(result.contains("10"));
        assertTrue(result.contains("20"));
        assertTrue(result.contains("30"));

    }

}
