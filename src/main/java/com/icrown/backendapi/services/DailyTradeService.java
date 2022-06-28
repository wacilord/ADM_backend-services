package com.icrown.backendapi.services;

import com.icrown.backendapi.dtos.DailyTradeListDetailResponse;
import com.icrown.backendapi.dtos.DailyTradeListRequest;
import com.icrown.backendapi.dtos.DailyTradeListResponse;
import com.icrown.backendapi.dtos.OrgTree;
import com.icrown.common.configs.UserConfig;
import com.icrown.common.dtos.AccountingCodeData;
import com.icrown.common.dtos.GameListResponse;
import com.icrown.common.services.*;
import com.icrown.gameapi.commons.configs.RedisKeyData;
import com.icrown.gameapi.commons.responses.ResponseCode;
import com.icrown.gameapi.commons.utils.APIException;
import com.icrown.gameapi.commons.utils.DateUtil;
import com.icrown.gameapi.commons.utils.PageUtil;
import com.icrown.gameapi.commons.utils.RedisUtil;
import com.icrown.gameapi.daos.DailyTradeDAO;
import com.icrown.gameapi.models.MemberPlayerModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Frank
 */
@Service
public class DailyTradeService {
    @Autowired
    DailyTradeDAO dailyTradeDAO;
    @Autowired
    PlayerCommonService playerCommonService;
    @Autowired
    PageUtil pageUtil;
    @Autowired
    DateUtil dateUtil;
    @Autowired
    GameCommonService gameCommonService;
    @Autowired
    AccountCodeCommonService accountCodeCommonService;
    @Autowired
    RedisPrefixService redisPrefixService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    private UserConfig userConfig;
    @Autowired
    AgentService agentService;
    @Autowired
    GameRecordCommonService gameRecordCommonService;
    @Autowired
    TranslateCommonService translateService;


    public DailyTradeListResponse getDailyTradeList(DailyTradeListRequest request, String agentGuid, int level, String mctGuid) {

        Date startDateDayPart = dateUtil.getDatePart(request.getStartDate());
        Date endDateDayPart = dateUtil.getDatePart(request.getEndDate());
        Date currentDay = request.getCurrentDate();

        //currentDate若為中間區段，以下兩個if都成立

        //currentDate和startDate不天同,startDate設為currentDate
        if (!currentDay.equals(startDateDayPart)) {
            request.setStartDate(currentDay);
        }

        //currentDate和endDate不同天,endDate設為currentDate
        if (!currentDay.equals(endDateDayPart)) {
            request.setEndDate(dateUtil.addTime(currentDay, Calendar.DATE, 1));
        }

        List<MemberPlayerModel> player = playerCommonService.getPlayerInfoByAgtGuidAndLevel(agentGuid, request.getAccountID(), level);

        Date partitionStart = dateUtil.getDateWithFormat(request.getStartDate(), "yyyy-MM-dd");
        Date partitionEnd = dateUtil.addTime(partitionStart, Calendar.DATE, 2);

        //取得所有同名playerID的plyGuid
        List<String> plyGuidList = player.stream().map(MemberPlayerModel::getPLY_GUID).collect(Collectors.toList());
        int itemCount = dailyTradeDAO.getDailyTradeListCount2(plyGuidList, request.getStartDate(), request.getEndDate(), partitionStart, partitionEnd, request.getQueryType());
        DailyTradeListResponse response = new DailyTradeListResponse();
        response.setRecords(Collections.emptyList());
        //pageCount初始值
        response.setPageCount(1);
        if (itemCount > 0) {
            List<DailyTradeListDetailResponse> list = new ArrayList<>(request.getPageSize());
            SqlRowSet rowSet = dailyTradeDAO.getDailyTradeListWithAccCode(plyGuidList, request.getStartDate(),
                    request.getEndDate(), partitionStart, partitionEnd, request.getPageIndex(), request.getPageSize(), request.getQueryType());
            List<OrgTree> orgTreeList = agentService.getAgentTreeListByAgent3(mctGuid);

            while (rowSet.next()) {
                DailyTradeListDetailResponse item = new DailyTradeListDetailResponse();

                List<String> orgTreeList2 = agentService.getAgentTreeByAgent3AndLevel(orgTreeList, level, rowSet.getString("AGT_Agent3"));

                item.setSeq(rowSet.getLong("seq"));
                item.setAccountID(rowSet.getString("PLY_AccountID"));
                item.setAgentTree(orgTreeList2);
                item.setTransferID(rowSet.getString("transferID"));
                item.setUpdateDate(rowSet.getDate("updateDate"));
                item.setCode(rowSet.getInt("code"));
                item.setGameType(rowSet.getInt("gameType"));
                item.setGameCode(rowSet.getString("gameCode"));
                item.setCurrency(rowSet.getString("currency"));
                item.setTradePoint(rowSet.getBigDecimal("tradePoint"));
                item.setAfterBalance(rowSet.getBigDecimal("afterBalance"));

                list.add(item);
            }
            response.setRecords(list);
            response.setPageCount(pageUtil.getPageCount(itemCount, request.getPageSize()));
        }

        response.setPageIndex(request.getPageIndex());
        response.setPageSize(request.getPageSize());
        response.setItemCount(itemCount);
        response.setCurrentDate(currentDay);
        return response;
    }


    public String getDailyTradeListDownload(DailyTradeListRequest request, String agentGuid, int level, String mctGuid, String language) {

        StringBuilder stringBuilder = new StringBuilder();
        String header = "单号,帐号,阶层,交易时间,帐务类型,游戏,币别,变动分数";
        Map<String, String> transMap = translateService.getTranslationMap(language);
        boolean zhCNFlag = language.equals("zh-CN") || language.equals("zh_CN");
        //翻譯header,language是zh_CN不會翻譯
        if (!zhCNFlag) {
            header = translateService.translateCsvHeaderByTranlationMap(transMap, header);
        }

        stringBuilder.append(header);

        List<MemberPlayerModel> player = playerCommonService.getPlayerInfoByAgtGuidAndLevel(agentGuid, request.getAccountID(), level);
        //取得所有同名playerID的plyGuid
        List<String> plyGuidList = player.stream().map(MemberPlayerModel::getPLY_GUID).collect(Collectors.toList());

        List<Date> dateIntervalList = dateUtil.getDateIntervalList(request.getStartDate(), request.getEndDate());
        //日期降冪排序
        for (int i = dateIntervalList.size() - 1; i - 1 >= 0; i--) {

            Date startDate = dateIntervalList.get(i - 1);
            Date endDate = dateIntervalList.get(i);

            Date tempPartitionStart = dateUtil.getDatePart(startDate);
            Date tempPartitionEnd = dateUtil.getDatePart(dateUtil.addTime(endDate, Calendar.DATE, 2));

            int itemCount = dailyTradeDAO.getDailyTradeListCount2(plyGuidList, startDate, endDate, tempPartitionStart, tempPartitionEnd, request.getQueryType());

            if (itemCount > 0) {
                List<GameListResponse> gameList = gameCommonService.getAllGameList();
                List<AccountingCodeData> accountingCodeList = accountCodeCommonService.getAllAccountingCode();

                SqlRowSet rowSet = dailyTradeDAO.getDailyTradeListWithAccCode(plyGuidList, startDate, endDate, tempPartitionStart, tempPartitionEnd, 1, itemCount, request.getQueryType());
                List<OrgTree> orgTreeList = agentService.getAgentTreeListByAgent3(mctGuid);
                DecimalFormat decimalFormat = new DecimalFormat("0.00");

                while (rowSet.next()) {
                    MemberPlayerModel playerModel = player.stream().filter(pl -> pl.getPLY_GUID().equals(rowSet.getString("PLY_GUID"))).findFirst().get();

                    List<String> orgTreeList2 = agentService.getAgentTreeByAgent3AndLevel(orgTreeList, level, rowSet.getString("AGT_Agent3"));

                    stringBuilder.append(System.getProperty("line.separator"));
                    int accCode = rowSet.getInt("code");
                    String accName = accountingCodeList.stream().filter(a -> a.getAccCode() == accCode).map(AccountingCodeData::getAccName).findFirst().get();

                    int gameCode = rowSet.getInt("gameCode");
                    String gameName = (accCode != 2001) ? "" : gameList.stream().filter(g -> g.getGameCode() == gameCode).collect(Collectors.toList()).get(0).getGameName();

                    String seq = String.valueOf(rowSet.getLong("seq"));

                    String orgTreeString = "";
                    for (String ot : orgTreeList2) {
                        orgTreeString += ot + ">";
                    }
                    orgTreeString = orgTreeString.substring(0, orgTreeString.lastIndexOf(">"));
                    String plyAccountId = playerModel.getPLY_AccountID();
                    if (accCode != 2001 && accCode != 6001) {
                        seq = rowSet.getString("transferID");
                    }

                    accName = zhCNFlag ? accName : translateService.fuzzyTranslationByTranlationMap(transMap, accName);
                    gameName = zhCNFlag ? gameName : translateService.fuzzyTranslationByTranlationMap(transMap, gameName);

                    String line = "=\"" + seq + "\"," +
                            plyAccountId + "," +
                            orgTreeString + "," +
                            dateUtil.getDateFormat(rowSet.getDate("updateDate"), "yyyy-MM-dd HH:mm:ss") + "," +
                            accName + "," +
                            gameName + "," +
                            rowSet.getString("currency") + "," +
                            decimalFormat.format(rowSet.getBigDecimal("tradePoint"));

                    stringBuilder.append(line);
                }
            }
        }
        return stringBuilder.toString();
    }

    public String queryGameDetail(String agtGuid, long gameTurn, int level, String language) {
        SqlRowSet rowSet = dailyTradeDAO.getRecordBySeqAndLevel(agtGuid, gameTurn, level);

        if (!rowSet.next()) {
            throw new APIException(ResponseCode.BACKENDAPI_GAMETURN_NOT_FOUND, ResponseCode.BACKENDAPI_GAMETURN_NOT_FOUND.getErrorMessage());
        }
        String gameCode = rowSet.getString("gameCode");
        String plyGuid = rowSet.getString("PLY_GUID");

        String token = UUID.randomUUID().toString();
        String key = redisPrefixService.getKey(RedisKeyData.TOKEN, token);
        Map<String, String> map = new HashMap<>(4);
        map.put("gameTurn", String.valueOf(gameTurn));
        map.put("gameCode", gameCode);
        map.put("guid", plyGuid);
        map.put("memberType", "1");
        map.put("language", language);
        redisUtil.hPutAll(key, map);
        redisUtil.expire(key, userConfig.getTokenExpireSeconds(), TimeUnit.SECONDS);

        return gameRecordCommonService.getGameDetailUrlWithLanguage(token, language);
    }

}
