package com.icrown.backendapi.services;

import com.icrown.backendapi.dtos.OrgTree;
import com.icrown.backendapi.dtos.PlayerDetailRequest;
import com.icrown.common.services.AgentGameRelationCommonService;
import com.icrown.common.services.PlayerCommonService;
import com.icrown.gameapi.commons.utils.APIException;
import com.icrown.gameapi.daos.AgentDAO;
import com.icrown.gameapi.daos.MemPlayerDAO;
import com.icrown.gameapi.models.AgentModel;
import com.icrown.gameapi.models.MemberPlayerModel;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = PlayerServiceTest.class)
@ComponentScan(basePackages = "com.icrown")
@EnableAutoConfiguration
public class PlayerServiceTest {
    @Autowired
    PlayerService playerService;
    @MockBean
    PlayerCommonService playerCommonService;
    @MockBean
    LogGameTicketService logGameTicketService;
    @MockBean
    AgentService agentService;
    @MockBean
    GameRecordService gameRecordService;
    @MockBean
    AgentGameRelationCommonService agentGameRelationCommonService;
    @MockBean
    MemPlayerDAO memPlayerDAO;
    @MockBean
    AgentDAO agentDAO;

    @Test
    public void getPlayerDetail() {
        String agentGuid = "agentGuid";
        String accountID = "accountID";
        String playerGuid = "playerGuid";
        PlayerDetailRequest request = new PlayerDetailRequest(accountID, playerGuid);
        MemberPlayerModel player = new MemberPlayerModel();
        player.setPLY_GUID("plyGuid");
        player.setPLY_AccountID(accountID);
        player.setPLY_Point(new BigDecimal(100));
        player.setPLY_PointUpdatetime(new Date());
        player.setPLY_CreateDatetime(new Date());
        List<MemberPlayerModel> playerList = Arrays.asList(player);
        Map<String, String> map = new HashMap<>();
        map.put("agent1", "agent1");
        map.put("agent2", "agent2");
        map.put("agent3", "agent3");
        //Date startDate = new Date();
        //Date endDate = new Date();
        String mctGuid = "mctGuid";
        int level = 3;
        String agtAccountID = "accountID";
        List<Integer> enableGameTypes = new ArrayList<>();
        enableGameTypes.add(1);
        enableGameTypes.add(2);
        enableGameTypes.add(3);
        enableGameTypes.add(4);

        var betsResultGroupByGameTypeRowSet = Mockito.mock(SqlRowSet.class);
        Mockito.when(betsResultGroupByGameTypeRowSet.next()).thenReturn(false);

        var betsResultGroupByDate = Mockito.mock(SqlRowSet.class);
        Mockito.when(betsResultGroupByDate.next()).thenReturn(false);

        var betsGroupByGame = Mockito.mock(SqlRowSet.class);
        Mockito.when(betsGroupByGame.next()).thenReturn(false);

        when(playerCommonService.getPlayerInfoByAgtGuidAndLevelAndPlayerGuid(agentGuid, accountID, level, playerGuid)).thenReturn(playerList);

        when(logGameTicketService.getPlayerIsOnline(player.getPLY_GUID())).thenReturn(true);
        when(agentService.getAgentTreeByAgent3(agentGuid)).thenReturn(map);
        when(logGameTicketService.getLastOnlineTime(player.getPLY_GUID())).thenReturn(Optional.empty());
        when(gameRecordService.getHistorySumBets(Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(new BigDecimal(100));
        when(gameRecordService.getHistorySumResult(Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(new BigDecimal(100));

        when(agentGameRelationCommonService.getEnableGameTypeByMctGuid(mctGuid)).thenReturn(enableGameTypes);
        when(gameRecordService.getResultGroupByGameType(Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(betsResultGroupByGameTypeRowSet);

        when(gameRecordService.getWinGroupByDate(Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(betsResultGroupByDate);

        when(gameRecordService.getBetsGroupByGame(Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(betsGroupByGame);

        var response = playerService.getPlayerDetail(request, agentGuid, mctGuid, level);

        assertTrue(response.getBetsInfo().size() > 0);
        assertTrue(response.getResultInfoByDay().size() > 0);
        assertTrue(response.getResultInfoByGame().size() > 0);
        assertTrue(response.getUserInfo().getAccountID().equals(accountID));


    }

    @Test
    public void getPlayerList() {
        when(memPlayerDAO.getPlayerListCount(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt())).thenReturn(1);
        List<AgentModel> agentList = new ArrayList<>();
        AgentModel agentModel = new AgentModel();
        agentList.add(agentModel);
        when(agentDAO.getAllAgentsByMctGUID(Mockito.anyString())).thenReturn(agentList);
        var resultSetMock = Mockito.mock(SqlRowSet.class);
        Mockito.when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        Mockito.when(resultSetMock.getString("AGT_Agent3")).thenReturn("agent3");
        OrgTree o = new OrgTree();
        o.setAgent3("agent3");
        o.setTree(Map.of("agent1", "agent1"));
        Mockito.when(agentService.getAgentTreeListByAgent3("mctGuid")).thenReturn(List.of(o));
        when(memPlayerDAO.getPlayerList(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(resultSetMock);
        var response = playerService.getPlayerList("mctGuid", "agtGuid", 1, "playerAccountID", "currency", 0, 10, 1);
        assertTrue(response.getRecords().size() == 1);
    }

    //@Test
    public void getPlayerListWithZeroPlayer() {
        when(memPlayerDAO.getPlayerListCount(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt())).thenReturn(0);
        assertThrows(APIException.class, () -> {
            var response = playerService.getPlayerList("mctGuid", "agtGuid", 1, "playerAccountID", "currency", 0, 10, 1);
        });
        //assertTrue(response.getRecords().size() == 0);
    }

    @Test
    public void lockPlayer() {
        when(memPlayerDAO.lockPlayer(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        playerService.lockPlayer("lockerGuid", "plyGuid");
    }

    @Test
    public void unLockPlayer() {
        when(memPlayerDAO.unlockPlayer(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        playerService.unLockPlayer("lockerGuid", "plyGuid");
    }
}
