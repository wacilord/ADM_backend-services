package com.icrown.backendapi.services;

import com.icrown.backendapi.dtos.*;
import com.icrown.common.services.AgentGameRelationCommonService;
import com.icrown.common.services.PlayerCommonService;
import com.icrown.common.services.RedisPrefixService;
import com.icrown.gameapi.commons.configs.GameTypeData;
import com.icrown.gameapi.commons.responses.ResponseCode;
import com.icrown.gameapi.commons.utils.APIException;
import com.icrown.gameapi.commons.utils.DateUtil;
import com.icrown.gameapi.daos.AgentDAO;
import com.icrown.gameapi.daos.GameReportDAO;
import com.icrown.gameapi.daos.MemPlayerDAO;
import com.icrown.gameapi.models.AgentModel;
import com.icrown.gameapi.models.MemberPlayerModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Frank
 */
@Service
public class PlayerService {
    private static final int DAY_SECONDS = 60 * 60 * 24;
    @Autowired
    PlayerCommonService playerCommonService;
    @Autowired
    LogGameTicketService logGameTicketService;
    @Autowired
    AgentService agentService;
    @Autowired
    DateUtil dateUtil;
    @Autowired
    GameRecordService gameRecordService;
    @Autowired
    RedisPrefixService redisPrefixService;
    @Autowired
    AgentGameRelationCommonService agentGameRelationCommonService;
    @Autowired
    MemPlayerDAO memPlayerDAO;
    @Autowired
    AgentDAO agentDAO;
    @Autowired
    GameReportDAO gameReportDAO;

    public PlayerDetailResponse getPlayerDetail(PlayerDetailRequest request, String agentGuid, String mctGuid, int level) {
        PlayerDetailResponse response = new PlayerDetailResponse();
        var player = playerCommonService.getPlayerInfoByAgtGuidAndLevelAndPlayerGuid(agentGuid, request.getAccountID(), level, request.getPlayerGuid());
        Date now = new Date();
        Date startDate6Month = dateUtil.addTime(dateUtil.getDatePart(now), Calendar.MONTH, -6);
        var userInfo = getUserInfo(player.get(0), startDate6Month, now);
        response.setUserInfo(userInfo);
        var list1 = getResultInfoByGame(mctGuid, player.get(0).getPLY_GUID(), startDate6Month, now);
        response.setResultInfoByGame(list1);
        var list2 = getResultGroupByDate(player.get(0).getPLY_GUID(), startDate6Month, now);
        response.setResultInfoByDay(list2);
        var list3 = getBetsGroupByGame(mctGuid, player.get(0).getPLY_GUID(), startDate6Month, now);
        response.setBetsInfo(list3);
        return response;
    }

    /**
     * 取得玩定資料
     *
     * @param player
     * @param startDate
     * @param endDate
     * @return
     */
    private PlayerDetailUserInfoResponse getUserInfo(MemberPlayerModel player, Date startDate, Date endDate) {
        PlayerDetailUserInfoResponse userInfo = new PlayerDetailUserInfoResponse();
        userInfo.setAccountID(player.getPLY_AccountID());
        boolean isOnline = logGameTicketService.getPlayerIsOnline(player.getPLY_GUID());
        userInfo.setOnline(isOnline);
        String[] orgTree = null;

        Map<String, String> orgs = agentService.getAgentTreeByAgent3(player.getAGT_Agent3());
        orgTree = Arrays.asList(orgs.get("Agent1"), orgs.get("Agent2"), orgs.get("Agent3"), player.getPLY_AccountID()).toArray(new String[4]);

        userInfo.setOrgTree(orgTree);
        Optional<Date> lastOnlineTime = logGameTicketService.getLastOnlineTime(player.getPLY_GUID());
        userInfo.setLastOnlineTime(lastOnlineTime.isEmpty() ? null : lastOnlineTime.get());
        userInfo.setPoint(player.getPLY_Point());
        userInfo.setPointUpdateTime(player.getPLY_PointUpdatetime());
        userInfo.setCreateDateTime(player.getPLY_CreateDatetime());
        long seconds = dateUtil.diffSeconds(player.getPLY_CreateDatetime(), new Date());
        long days = seconds / DAY_SECONDS;
        userInfo.setCreateTotalDay((int) days);
        SqlRowSet rowSet = gameReportDAO.getHistorySumBetsAndSumNetWin(player.getPLY_GUID(), startDate, endDate);
        if(rowSet.next()) {
            userInfo.setHistoryBets(rowSet.getBigDecimal("SumBets"));
            userInfo.setHistoryNetWin(rowSet.getBigDecimal("SumNetWin"));
        }
        return userInfo;
    }

    /**
     * 取得6個月淨輸贏統計 group by 遊戲類型
     *
     * @param mctGuid
     * @param playerGuid
     * @param startDate
     * @param endDate
     * @return
     */
    private List<PlayerDetailResultInfoByGameResponse> getResultInfoByGame(String mctGuid, String playerGuid, Date startDate, Date endDate) {
        final List<PlayerDetailResultInfoByGameResponse> list = new ArrayList<>(GameTypeData.values().length);
        var rowSet = gameRecordService.getResultGroupByGameType(playerGuid, startDate, endDate);
        while (rowSet.next()) {
            PlayerDetailResultInfoByGameResponse item = new PlayerDetailResultInfoByGameResponse();
            item.setGameType(rowSet.getInt("gameType"));
            item.setNetWin(rowSet.getBigDecimal("NetWin"));
            list.add(item);
        }
        //取得資料裡的所有遊戲類型
        List<Integer> dataList = list.stream().map(PlayerDetailResultInfoByGameResponse::getGameType)
                .collect(Collectors.toList());
        //取得所有啟用的遊戲類型
        List<Integer> gameEnableTypeList = agentGameRelationCommonService.getEnableGameTypeByMctGuid(mctGuid);
        //取得有啟用的遊戲類型但沒有資料
        List<Integer> noData = gameEnableTypeList.stream().filter(f -> !dataList.contains(f)).collect(Collectors.toList());
        //新增遊戲類型並將值設0
        noData.stream().forEach(g -> {
            PlayerDetailResultInfoByGameResponse item = new PlayerDetailResultInfoByGameResponse();
            item.setGameType(g);
            item.setNetWin(new BigDecimal(0));
            list.add(item);
        });

        //依遊戲類型做遞增排序結果
        return list.stream()
                .sorted(Comparator.comparing(PlayerDetailResultInfoByGameResponse::getGameType))
                .collect(Collectors.toList());
    }

    /**
     * 取得6個月每日輸贏統計 group by 日期
     *
     * @param playerGuid
     * @param startDate
     * @param endDate
     * @return
     */
    private List<PlayerDetailResultInfoByDayResponse> getResultGroupByDate(String playerGuid, Date startDate, Date endDate) {
        List<PlayerDetailResultInfoByDayResponse> list = new ArrayList<>();
        var rowSet = gameRecordService.getWinGroupByDate(playerGuid, startDate, endDate);
        //撈取資料庫資料
        while (rowSet.next()) {
            PlayerDetailResultInfoByDayResponse item = new PlayerDetailResultInfoByDayResponse();
            item.setDate(rowSet.getDate("date"));
            item.setNetWin(rowSet.getBigDecimal("netWin"));
            list.add(item);
        }
        //取得有資料所有的日期
        List<Date> dataList2 = list.stream().map(PlayerDetailResultInfoByDayResponse::getDate)
                .collect(Collectors.toList());
        //取得區間裡所有的日期
        List<Date> dates = new ArrayList<>();
        Date tmp = startDate;
        while (tmp.compareTo(endDate) <= 0) {
            dates.add(tmp);
            tmp = dateUtil.addTime(tmp, Calendar.DATE, 1);
        }
        //取得資料裡未包含的日期
        List<Date> noDatas = dates.stream().filter(f -> !dataList2.contains(f)).collect(Collectors.toList());
        //新增日期資料並將值設為0
        for (int index = 0; index < noDatas.size(); index++) {
            Date ele = noDatas.get(index);
            PlayerDetailResultInfoByDayResponse item = new PlayerDetailResultInfoByDayResponse();
            item.setDate(ele);
            item.setNetWin(new BigDecimal(0));
            list.add(item);
        }
        //依日期做遞增排序
        list = list.stream()
                .sorted(Comparator.comparing(PlayerDetailResultInfoByDayResponse::getDate))
                .collect(Collectors.toList());
        return list;
    }

    /**
     * 取得30天押分比例 group by gameType
     *
     * @param mctGuid
     * @param playerGuid
     * @param startDate
     * @param endDate
     * @return
     */
    private List<PlayerDetailBetsInfoResponse> getBetsGroupByGame(String mctGuid, String playerGuid, Date startDate, Date endDate) {
        //取得所有啟用的遊戲類型
        List<Integer> gameEnableTypeList = agentGameRelationCommonService.getEnableGameTypeByMctGuid(mctGuid);
        List<PlayerDetailBetsInfoResponse> list = new ArrayList<>();
        var rowSet = gameRecordService.getBetsGroupByGame(playerGuid, startDate, endDate);

        BigDecimal sumBets = new BigDecimal(0);
        while (rowSet.next()) {
            PlayerDetailBetsInfoResponse item = new PlayerDetailBetsInfoResponse();
            item.setGameType(rowSet.getInt("gameType"));
            item.setBets(rowSet.getBigDecimal("bets"));
            list.add(item);
            sumBets = sumBets.add(item.getBets());
        }
        //取得資料裡的所有遊戲類型
        List<Integer> dataList = list.stream().map(PlayerDetailBetsInfoResponse::getGameType)
                .collect(Collectors.toList());
        //取得有啟用的遊戲類型但沒有資料
        List<Integer> noDatas = gameEnableTypeList.stream().filter(f -> !dataList.contains(f)).collect(Collectors.toList());
        //新增遊戲類型並將值設0
        for (int index = 0; index < noDatas.size(); index++) {
            Integer ele = noDatas.get(index);
            PlayerDetailBetsInfoResponse item = new PlayerDetailBetsInfoResponse();
            item.setGameType(ele);
            item.setBets(new BigDecimal(0));
            list.add(item);
        }
        for (int index = 0; index < list.size(); index++) {
            PlayerDetailBetsInfoResponse item = list.get(index);
            BigDecimal betsPercent = new BigDecimal(0);
            if (sumBets.compareTo(new BigDecimal(0)) > 0) {
                betsPercent = item.getBets().divide(sumBets, 4, RoundingMode.HALF_DOWN);
                betsPercent = betsPercent.multiply(new BigDecimal(100));
            }
            item.setBetsPercent(betsPercent);
        }
        //依遊戲類型做遞增排序結果
        list = list.stream()
                .sorted(Comparator.comparing(PlayerDetailBetsInfoResponse::getGameType))
                .collect(Collectors.toList());
        return list;
    }


    public PlayerListResponse getPlayerList(String mctGUID, String agtGUID, int level, String playerAccountID, String currency, int status, int pageSize, int pageIndex) {
        PlayerListResponse response = new PlayerListResponse();
        int count = memPlayerDAO.getPlayerListCount(agtGUID, level, playerAccountID, status);

        if (count == 0) {
            throw new APIException(ResponseCode.BACKENDAPI_ACCOUNT_NOT_EXISTS);
        }

        List<Player> players = new ArrayList<>(pageSize);

        List<AgentModel> agents = agentDAO.getAllAgentsByMctGUID(mctGUID);

        SqlRowSet rs = memPlayerDAO.getPlayerList(agtGUID, level, playerAccountID, status, pageSize, pageIndex);
        List<OrgTree> orgTreeList = agentService.getAgentTreeListByAgent3(mctGUID);
        while (rs.next()) {
            Player p = new Player();
            p.setAccountID(rs.getString("PLY_AccountID"));
            p.setLocker(rs.getString("PLY_Locker"));
            p.setNickName(rs.getString("PLY_NickName"));
            p.setPlayerGuid(rs.getString("PLY_GUID"));
            p.setPoint(rs.getBigDecimal("PLY_Point"));
            p.setLock(rs.getBoolean("PLY_Lock"));
            p.setCurrency(currency);
            if (p.isLock()) {
                AgentModel agentModel = agents.stream()
                        .filter(o -> o.getAGT_GUID().equals(p.getLocker())).findFirst().get();
                p.setLockerAccountID(agentModel.getAGT_AccountID());
            }
            else {
                p.setLockerAccountID("");
            }

            List<String> orgTreeList2 = agentService.getAgentTreeByAgent3AndLevel(orgTreeList, level, rs.getString("AGT_Agent3"));

            p.setAgentTree(orgTreeList2);

            players.add(p);
        }


        response.setRecords(players);
        response.setItemCount(count);
        response.setPageCount((int) Math.ceil((double) count / pageSize));
        response.setPageIndex(pageIndex);
        response.setPageSize(pageSize);
        return response;
    }

    public void lockPlayer(String lockerGuid, String plyGuid) {
        memPlayerDAO.lockPlayer(plyGuid, lockerGuid);
    }

    public void unLockPlayer(String lockerGuid, String plyGuid) {
        memPlayerDAO.unlockPlayer(plyGuid, lockerGuid);
    }
}
