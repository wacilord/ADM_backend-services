package com.icrown.backendapi.services;

import com.icrown.backendapi.dtos.*;
import com.icrown.common.dtos.GameListResponse;
import com.icrown.common.services.*;
import com.icrown.gameapi.commons.configs.GameTypeData;
import com.icrown.gameapi.commons.responses.ResponseCode;
import com.icrown.gameapi.commons.utils.APIException;
import com.icrown.gameapi.commons.utils.DateUtil;
import com.icrown.gameapi.commons.utils.PageUtil;
import com.icrown.gameapi.commons.utils.RtpUtil;
import com.icrown.gameapi.daos.*;
import com.icrown.gameapi.models.AgentModel;
import com.icrown.gameapi.models.GameTypeModel;
import com.icrown.gameapi.models.SubAccountModel;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Frank
 */
@Service
public class GameRecordService {
    @Autowired
    DailyTradeDAO dailyTradeDAO;
    @Autowired
    GameReportDAO gameReportDAO;
    @Autowired
    GameReportDayDAO gameReportDayDAO;
    @Autowired
    GameTypeCommonService gameTypeCommonService;
    @Autowired
    GameCommonService gameCommonService;
    @Autowired
    AgentGameRelationCommonService agentGameRelationCommonService;
    @Autowired
    PageUtil pageUtil;
    @Autowired
    AgentService agentService;
    @Autowired
    AgentCommonService agentCommonService;
    @Autowired
    PlayerCommonService playerCommonService;
    @Autowired
    AgentDAO agentDAO;
    @Autowired
    RtpUtil rtpUtil;
    @Autowired
    TranslateCommonService translateService;
    @Autowired
    CampaignAwardDAO campaignDAO;
    @Autowired
    DateUtil dateUtil;
    @Autowired
    SubAccountDAO subAccountDAO;

    /**
     * 不需要計算RTP的遊戲類別
     */
    private static final List<Integer> NO_RTP_GAME_TYPE_LIST = List.of(5);

    /**
     * 不需要計算RTP的遊戲代碼
     */
    private static final List<Integer> NO_RTP_GAME_CODE_LIST = List.of(4002);

    /**
     * 第三層代理
     */
    private static final int AGENT_LEVEL = 3;

    public GameDetailResponse getGameDetail(GameDetailRequest request, String agentGuid, int level) {
        SqlRowSet rowSet = dailyTradeDAO.getRecordBySeqAndLevel(agentGuid, request.getSeq(), level);
        GameDetailResponse response = new GameDetailResponse();
        List<GameDetailRecordResponse> list = new ArrayList<>();
        while (rowSet.next()) {
            if (rowSet.getInt("workType") == 2) {
                GameDetailRecordResponse item = new GameDetailRecordResponse();
                item.setSeq(rowSet.getLong("seq"));
                item.setAccountID(rowSet.getString("accountID"));
                item.setGameType(rowSet.getInt("gameType"));
                item.setGameCode(rowSet.getString("gameCode"));
                item.setCurrency(rowSet.getString("currency"));
                item.setBets(rowSet.getBigDecimal("bets"));
                item.setValidBets(rowSet.getBigDecimal("validBets"));
                item.setNetWin(rowSet.getBigDecimal("netWin"));
                item.setStartTime(rowSet.getDate("startTime"));
                list.add(item);
            }
        }
        if (list.isEmpty()) {
            throw new APIException(ResponseCode.BACKENDAPI_FORM_NO_NOT_EXIST);
        }
        response.setRecords(list);
        return response;
    }

    public BigDecimal getHistorySumBets(String playerGuid, Date startDate, Date endDate) {
        return gameReportDAO.getHistorySumBets(playerGuid, startDate, endDate);
    }

    public BigDecimal getHistorySumResult(String playerGuid, Date startDate, Date endDate) {
        return gameReportDAO.getHistorySumNetWin(playerGuid, startDate, endDate);
    }

    public SqlRowSet getResultGroupByGameType(String playerGuid, Date startDate, Date endDate) {
        return gameReportDAO.getNetWinGroupByGameType(playerGuid, startDate, endDate);
    }

    public SqlRowSet getWinGroupByDate(String playerGuid, Date startDate, Date endDate) {
        return gameReportDayDAO.getNetWinGroupByDate(playerGuid, startDate, endDate);
    }

    public SqlRowSet getBetsGroupByGame(String playerGuid, Date startDate, Date endDate) {
        return gameReportDAO.getBetsGroupByGame(playerGuid, startDate, endDate);
    }

    /**
     * TODO 增加GameType需修改GameResponse
     */
    public GameReportResponse getGameReportInfo(GameReportRequest request, String agentGuid, int level, String mctGuid, String domain) throws IOException {
        gameTypeCommonService.checkGameTypeListValid(request.getGameType());
        agentGameRelationCommonService.checkGameTypeListEnable(mctGuid, agentGuid, level, request.getGameType());

        String playerAccount = request.getAccountID();
        SqlRowSet rowSet;
        if (StringUtil.isNullOrEmpty(playerAccount)) {
            rowSet = gameReportDAO.getGameReportInfoByGameCode(request.getGameType(), request.getGameCode(), request.getStartDate(),
                    request.getEndDate(), agentGuid, level);
        } else {
            if (request.getAccountType() == 0) {
                AgentModel agentModel = agentDAO.getAgentByAccountAndDomain(request.getAccountID(), domain).orElseThrow(() -> {
                    throw new APIException(ResponseCode.BACKENDAPI_AGENT_NOT_FOUND, ResponseCode.BACKENDAPI_AGENT_NOT_FOUND.getErrorMessage());
                });

                if (agentModel.getAGT_Level() != AGENT_LEVEL) {
                    throw new APIException(ResponseCode.BACKENDAPI_AGENT_NOT_FOUND, ResponseCode.BACKENDAPI_AGENT_NOT_FOUND.getErrorMessage());
                }

                if (level == 1 && !agentModel.getAGT_Agent1().equals(agentGuid)) {
                    throw new APIException(ResponseCode.BACKENDAPI_AGENT_NOT_FOUND, ResponseCode.BACKENDAPI_AGENT_NOT_FOUND.getErrorMessage());
                }

                if (level == 2 && !agentModel.getAGT_Agent2().equals(agentGuid)) {
                    throw new APIException(ResponseCode.BACKENDAPI_AGENT_NOT_FOUND, ResponseCode.BACKENDAPI_AGENT_NOT_FOUND.getErrorMessage());
                }

                rowSet = gameReportDAO.getGameReportInfoByAgent3(request.getGameType(), request.getGameCode(), request.getStartDate(),
                        request.getEndDate(), agentModel.getAGT_Agent3());

            } else {
                playerCommonService.checkUserExistByAgent(agentGuid, request.getAccountID(), level);
                rowSet = gameReportDAO.getGameReportInfoByPlayer(request.getAccountID(), request.getGameType(), request.getGameCode(), request.getStartDate(),
                        request.getEndDate(), agentGuid, level);
            }
        }

        var allList = getGameReportAllGameResponseFromRowSet(rowSet);
        var slotGameRecords = getSlotGamePart(allList);
        var fishingGameRecords = getFishingGamePart(allList);
        var cardGameRecords = getCardGamePart(allList);
        var miniGameRecords = getMiniGamePart(allList);
        var roomGameRecords = getRoomGamePart(allList);

        GameRecord slotGameTypeRecord = new GameRecord();
        slotGameTypeRecord.setGameType(GameTypeData.SLOT_GAME.getGameTypeValue());
        slotGameTypeRecord.setGameTypeRecordList(slotGameRecords);

        GameRecord fishingGameTypeRecord = new GameRecord();
        fishingGameTypeRecord.setGameType(GameTypeData.FISHING_GAME.getGameTypeValue());
        fishingGameTypeRecord.setGameTypeRecordList(fishingGameRecords);

        GameRecord cardGameTypeRecord = new GameRecord();
        cardGameTypeRecord.setGameType(GameTypeData.CARD_GAME.getGameTypeValue());
        cardGameTypeRecord.setGameTypeRecordList(cardGameRecords);

        GameRecord miniGameTypeRecord = new GameRecord();
        miniGameTypeRecord.setGameType(GameTypeData.MINI_GAME.getGameTypeValue());
        miniGameTypeRecord.setGameTypeRecordList(miniGameRecords);

        GameRecord roomGameTypeRecord = new GameRecord();
        roomGameTypeRecord.setGameType(GameTypeData.ROOM_GAME.getGameTypeValue());
        roomGameTypeRecord.setGameTypeRecordList(roomGameRecords);

        GameReportResponse gameReportResponse = new GameReportResponse();
        gameReportResponse.setGameRecordList(List.of(slotGameTypeRecord, fishingGameTypeRecord, cardGameTypeRecord, miniGameTypeRecord, roomGameTypeRecord));

        return gameReportResponse;
    }

    /**
     * TODO 不需計算RTP的遊戲類別需排除(ex:對戰類遊戲)
     */
    public String getGameReportDownload(GameReportRequest request, String agentGuid, int level, String mctGuid, String domain, String language) throws IOException {
        gameTypeCommonService.checkGameTypeListValid(request.getGameType());
        agentGameRelationCommonService.checkGameTypeListEnable(mctGuid, agentGuid, level, request.getGameType());
        List<GameTypeModel> gameTypeList = gameTypeCommonService.getAllGameTypeByLanguage(language);
        List<GameListResponse> gameList = gameCommonService.getAllGameList();
        Map<String, String> trasMap = translateService.getTranslationMap(language);

        String playerAccount = request.getAccountID();
        SqlRowSet rowSet;
        if (StringUtil.isNullOrEmpty(playerAccount)) {
            rowSet = gameReportDAO.getGameReportInfoByGameCode(request.getGameType(), request.getGameCode(), request.getStartDate(),
                    request.getEndDate(), agentGuid, level);
        } else {
            if (request.getAccountType() == 0) {
                AgentModel agentModel = agentDAO.getAgentByAccountAndDomain(request.getAccountID(), domain).orElseThrow(() -> {
                    throw new APIException(ResponseCode.BACKENDAPI_AGENT_NOT_FOUND, ResponseCode.BACKENDAPI_AGENT_NOT_FOUND.getErrorMessage());
                });
                if (agentModel.getAGT_Level() != 3) {
                    throw new APIException(ResponseCode.BACKENDAPI_AGENT_NOT_FOUND, ResponseCode.BACKENDAPI_AGENT_NOT_FOUND.getErrorMessage());
                }

                rowSet = gameReportDAO.getGameReportInfoByAgent3(request.getGameType(), request.getGameCode(), request.getStartDate(),
                        request.getEndDate(), agentModel.getAGT_Agent3());

            } else {
                rowSet = gameReportDAO.getGameReportInfoByPlayer(request.getAccountID(), request.getGameType(), request.getGameCode(), request.getStartDate(),
                        request.getEndDate(), agentGuid, level);
            }
        }

        DecimalFormat decimalFormat = new DecimalFormat("0.0000");
        StringBuilder stringBuilder = new StringBuilder();
        String header = "游戏类型,游戏名称,币别,总投注额,有效投注额,赢分,天九福彩,天九红包,彩金贡献,天九福彩(新),佣金,盈利,純盈利,交易笔数,庄家优势";
        //翻譯header,language是zh_CN不會翻譯
        header = translateService.translateCsvHeaderByLanguage(language, header);
        stringBuilder.append(header);
        while (rowSet.next()) {
            stringBuilder.append(System.getProperty("line.separator"));
            int gameType = rowSet.getInt("gameType");
            String gameTypeName = gameTypeList.stream().filter(g -> g.getGM_TypeSerial() == gameType).map(GameTypeModel::getGM_GameTypeName).findFirst().orElse("");

            int gameCode = rowSet.getInt("gameCode");
            String gameName = gameList.stream().filter(g -> g.getGameCode() == gameCode).map(GameListResponse::getGameName).findFirst().orElse("");
            gameName = trasMap.get(gameName) == null ? gameName : trasMap.get(gameName);
            BigDecimal sumBets = rowSet.getBigDecimal("sumBets");
            BigDecimal sumWin = rowSet.getBigDecimal("sumWin");
            BigDecimal sumJackpot2 = rowSet.getBigDecimal("sumJackpot2");
            BigDecimal bankerAdvantage = BigDecimal.ZERO;
            if (sumBets.compareTo(BigDecimal.ZERO) != 0 && !NO_RTP_GAME_TYPE_LIST.contains(gameType) && !NO_RTP_GAME_CODE_LIST.contains(gameCode)) {
                var rtp = rtpUtil.calculateRtpWithoutJackpot(sumWin, sumBets, sumJackpot2);
                bankerAdvantage = BigDecimal.valueOf(100.0000).subtract(rtp);
            }

            String line = gameTypeName + "," +
                    gameName + "," +
                    rowSet.getString("currency") + "," +
                    decimalFormat.format(sumBets) + "," +
                    decimalFormat.format(rowSet.getBigDecimal("sumValidBets")) + "," +
                    decimalFormat.format(rowSet.getBigDecimal("sumWin")) + "," +
                    decimalFormat.format(rowSet.getBigDecimal("sumJackpot")) + "," +
                    decimalFormat.format(rowSet.getBigDecimal("sumJackpot3")) + "," +
                    decimalFormat.format(rowSet.getBigDecimal("sumJackpotContribute")) + "," +
                    decimalFormat.format(rowSet.getBigDecimal("sumJackpot2")) + "," +
                    decimalFormat.format(rowSet.getBigDecimal("commission")) + "," +
                    decimalFormat.format(rowSet.getBigDecimal("sumNetWin")) + "," +
                    decimalFormat.format(rowSet.getBigDecimal("sumNetWin").subtract(rowSet.getBigDecimal("sumJackpot2")).add(rowSet.getBigDecimal("sumJackpotContribute"))) + "," +
                    rowSet.getInt("items") + "," +
                    decimalFormat.format(bankerAdvantage);
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    /**
     * TODO 不需計算RTP的遊戲類別需排除(ex:對戰類遊戲)
     */
    private List<GameReportAllGameResponse> getGameReportAllGameResponseFromRowSet(SqlRowSet rowSet) {
        List<GameReportAllGameResponse> allList = new ArrayList<>();
        while (rowSet.next()) {
            var item = new GameReportAllGameResponse();
            item.setGameType(rowSet.getInt("gameType"));
            item.setGameCode(rowSet.getString("gameCode"));
            item.setCurrency(rowSet.getString("currency"));
            item.setSumBets(rowSet.getBigDecimal("sumBets"));
            item.setSumValidBets(rowSet.getBigDecimal("sumValidBets"));
            item.setSumWin(rowSet.getBigDecimal("sumWin"));
            item.setSumJackpotContribute(rowSet.getBigDecimal("sumJackpotContribute"));
            item.setSumJackpot(rowSet.getBigDecimal("sumJackpot"));
            item.setSumJackpot2(rowSet.getBigDecimal("sumJackpot2"));
            item.setSumJackpot3(rowSet.getBigDecimal("sumJackpot3"));
            item.setSumNetWin(rowSet.getBigDecimal("sumNetWin"));
            item.setSumPureNetWin(rowSet.getBigDecimal("sumNetWin").subtract(rowSet.getBigDecimal("sumJackpot2")).add(rowSet.getBigDecimal("sumJackpotContribute")));
            item.setCommission(rowSet.getBigDecimal("commission"));
            item.setItems(rowSet.getInt("items"));
            BigDecimal sumBetsResult = item.getSumWin();
            BigDecimal sumBets = item.getSumBets();
            BigDecimal sumJackpot2 = item.getSumJackpot2();
            BigDecimal bankerAdvantage = BigDecimal.ZERO;
            if (item.getSumBets().compareTo(BigDecimal.ZERO) != 0 && !NO_RTP_GAME_TYPE_LIST.contains(item.getGameType()) && !NO_RTP_GAME_CODE_LIST.contains(Integer.valueOf(item.getGameCode()))) {
                var rtp = rtpUtil.calculateRtpWithoutJackpot(sumBetsResult, sumBets, sumJackpot2);
                bankerAdvantage = BigDecimal.valueOf(100.0000).subtract(rtp);
            }
            item.setBankerAdvantage(bankerAdvantage);
            allList.add(item);
        }
        return allList;
    }

    private List<GameRecordByGameCode> getSlotGamePart(List<GameReportAllGameResponse> allGAmeList) {
        List<GameRecordByGameCode> list = new ArrayList<>();
        var slotGameList = allGAmeList.stream().filter(f -> f.getGameType() == GameTypeData.SLOT_GAME.getGameTypeValue())
                .collect(Collectors.toList());
        slotGameList.forEach(ele -> {
            var item = new GameRecordByGameCode();
            item.setGameCode(ele.getGameCode());
            item.setCurrency(ele.getCurrency());
            item.setSumBets(ele.getSumBets());
            item.setSumValidBets(ele.getSumValidBets());
            item.setSumWin(ele.getSumWin());
            item.setSumJackpotContribute(ele.getSumJackpotContribute());
            item.setSumJackpot(ele.getSumJackpot());
            item.setSumJackpot2(ele.getSumJackpot2());
            item.setSumJackpot3(ele.getSumJackpot3());
            item.setSumNetWin(ele.getSumNetWin());
            item.setSumPureNetWin(ele.getSumNetWin().subtract(ele.getSumJackpot2()).add(ele.getSumJackpotContribute()));
            item.setSumCommission(ele.getCommission());
            item.setBankerAdvantage(ele.getBankerAdvantage());
            item.setItems(ele.getItems());
            list.add(item);
        });
        return list;
    }

    private List<GameRecordByGameCode> getFishingGamePart(List<GameReportAllGameResponse> allGAmeList) {
        List<GameRecordByGameCode> list = new ArrayList<>();
        var fishingGameList = allGAmeList.stream().filter(f -> f.getGameType() == GameTypeData.FISHING_GAME.getGameTypeValue())
                .collect(Collectors.toList());
        fishingGameList.forEach(ele -> {
            var item = new GameRecordByGameCode();
            item.setGameCode(ele.getGameCode());
            item.setCurrency(ele.getCurrency());
            item.setSumBets(ele.getSumBets());
            item.setSumValidBets(ele.getSumValidBets());
            item.setSumWin(ele.getSumWin());
            item.setSumNetWin(ele.getSumNetWin());
            item.setSumPureNetWin(ele.getSumNetWin().subtract(ele.getSumJackpot2()).add(ele.getSumJackpotContribute()));
            item.setSumJackpot(ele.getSumJackpot());
            item.setSumJackpot2(ele.getSumJackpot2());
            item.setSumJackpot3(ele.getSumJackpot3());
            item.setSumJackpotContribute(ele.getSumJackpotContribute());
            item.setSumCommission(ele.getCommission());
            item.setBankerAdvantage(ele.getBankerAdvantage());
            item.setItems(ele.getItems());
            list.add(item);
        });
        return list;
    }

    private List<GameRecordByGameCode> getCardGamePart(List<GameReportAllGameResponse> allGAmeList) {
        List<GameRecordByGameCode> list = new ArrayList<>();
        var chessGameList = allGAmeList.stream().filter(f -> f.getGameType() == GameTypeData.CARD_GAME.getGameTypeValue())
                .collect(Collectors.toList());
        chessGameList.forEach(ele -> {
            var item = new GameRecordByGameCode();
            item.setGameCode(ele.getGameCode());
            item.setCurrency(ele.getCurrency());
            item.setSumBets(ele.getSumBets());
            item.setSumValidBets(ele.getSumValidBets());
            item.setSumWin(ele.getSumWin());
            item.setSumJackpot(ele.getSumJackpot());
            item.setSumJackpot2(ele.getSumJackpot2());
            item.setSumJackpot3(ele.getSumJackpot3());
            item.setSumJackpotContribute(ele.getSumJackpotContribute());
            item.setSumNetWin(ele.getSumNetWin());
            item.setSumPureNetWin(ele.getSumNetWin().subtract(ele.getSumJackpot2()).add(ele.getSumJackpotContribute()));
            item.setBankerAdvantage(ele.getBankerAdvantage());
            item.setSumCommission(ele.getCommission());
            item.setItems(ele.getItems());
            list.add(item);
        });
        return list;
    }

    private List<GameRecordByGameCode> getMiniGamePart(List<GameReportAllGameResponse> allGAmeList) {
        List<GameRecordByGameCode> list = new ArrayList<>();
        var arcadeGameList = allGAmeList.stream().filter(f -> f.getGameType() == GameTypeData.MINI_GAME.getGameTypeValue())
                .collect(Collectors.toList());
        arcadeGameList.forEach(ele -> {
            var item = new GameRecordByGameCode();
            item.setGameCode(ele.getGameCode());
            item.setCurrency(ele.getCurrency());
            item.setSumBets(ele.getSumBets());
            item.setSumValidBets(ele.getSumValidBets());
            item.setSumWin(ele.getSumWin());
            item.setSumJackpot(ele.getSumJackpot());
            item.setSumJackpot2(ele.getSumJackpot2());
            item.setSumJackpot3(ele.getSumJackpot3());
            item.setSumJackpotContribute(ele.getSumJackpotContribute());
            item.setSumCommission(ele.getCommission());
            item.setSumNetWin(ele.getSumNetWin());
            item.setSumPureNetWin(ele.getSumNetWin().subtract(ele.getSumJackpot2()).add(ele.getSumJackpotContribute()));
            //對戰類遊戲沒有莊家優勢
            if (NO_RTP_GAME_CODE_LIST.contains(Integer.valueOf(ele.getGameCode()))) {
                item.setBankerAdvantage(BigDecimal.ZERO);
            } else {
                item.setBankerAdvantage(ele.getBankerAdvantage());
            }
            item.setItems(ele.getItems());
            list.add(item);
        });
        return list;
    }

    private List<GameRecordByGameCode> getRoomGamePart(List<GameReportAllGameResponse> allGAmeList) {
        List<GameRecordByGameCode> list = new ArrayList<>();
        var roomGameList = allGAmeList.stream().filter(f -> f.getGameType() == GameTypeData.ROOM_GAME.getGameTypeValue())
                .collect(Collectors.toList());
        roomGameList.forEach(ele -> {
            var item = new GameRecordByGameCode();
            item.setGameCode(ele.getGameCode());
            item.setCurrency(ele.getCurrency());
            item.setSumBets(ele.getSumBets());
            item.setSumValidBets(ele.getSumValidBets());
            item.setSumWin(ele.getSumWin());
            item.setSumJackpot(ele.getSumJackpot());
            item.setSumJackpot2(ele.getSumJackpot2());
            item.setSumJackpot3(ele.getSumJackpot3());
            item.setSumJackpotContribute(ele.getSumJackpotContribute());
            item.setSumCommission(ele.getCommission());
            item.setSumNetWin(ele.getSumNetWin());
            item.setSumPureNetWin(ele.getSumNetWin().subtract(ele.getSumJackpotContribute().subtract(ele.getSumJackpot2())));
            //對戰類遊戲沒有莊家優勢
            item.setBankerAdvantage(BigDecimal.ZERO);
            item.setItems(ele.getItems());
            list.add(item);
        });
        return list;
    }

    public SummaryReportResponse getGameAndPlayerSummaryReport(SummaryReportRequest request, String agentGuid, int level, String mctGuid, String currency) throws IOException {
        GameSummaryReportRequest gameRequest = new GameSummaryReportRequest(request.getGameType(), request.getStartDate(), request.getEndDate());
        var gameSummaryReport = getGameSummaryReport(gameRequest, agentGuid, level, mctGuid, currency);
        var playerSummaryReport = getPlayerSummaryReport(request, agentGuid, level, mctGuid, currency);
        TotalSummaryReportResponse total = getTotalSummaryReport(request.getGameType(), request.getStartDate(), request.getEndDate(), agentGuid, level, currency);
        gameSummaryReport.setTotal(total);
        playerSummaryReport.setTotal(total);
        SummaryReportResponse summaryReportResponse = new SummaryReportResponse();
        summaryReportResponse.setGameRecord(gameSummaryReport);
        summaryReportResponse.setPlayerRecord(playerSummaryReport);
        return summaryReportResponse;
    }

    public Object getCamReport(SummaryCamReportRequest request, int level, String agtGuid, String currency, String domain) {
        if (!StringUtils.isEmpty(request.getAccountID())) {
            Optional<AgentModel> agentModelOp = agentDAO.getAgentByAccountAndDomain(request.getAccountID(), domain);
            if (!agentModelOp.isEmpty()) {
                AgentModel agentModel = agentModelOp.get();
                if (agentModel.getAGT_Level() == level && !agentModel.getAGT_GUID().equals(agtGuid)) {
                    throw new APIException(ResponseCode.BACKENDAPI_NO_OPERATE_PERMISSION);
                }
                if (agentModel.getAGT_Level() < level) {
                    throw new APIException(ResponseCode.BACKENDAPI_NO_OPERATE_PERMISSION);
                }

                if (agentModel.getAGT_Level() == 2 && !agentModel.getAGT_GUID().equals(agtGuid) && !agentModel.getAGT_Agent1().equals(agtGuid)) {
                    throw new APIException(ResponseCode.BACKENDAPI_NO_OPERATE_PERMISSION);
                }

                if (agentModel.getAGT_Level() == 3 && !agentModel.getAGT_GUID().equals(agtGuid) && !agentModel.getAGT_Agent2().equals(agtGuid) && !agentModel.getAGT_Agent1().equals(agtGuid)) {
                    throw new APIException(ResponseCode.BACKENDAPI_NO_OPERATE_PERMISSION);
                }

                level = agentModel.getAGT_Level();
                agtGuid = agentModel.getAGT_GUID();
            }
        }

        if (level == 3) {
            return getPlayerCamReport(request, agtGuid, currency);
        }

        return getAgentCamReprot(request, level, agtGuid, currency, domain);
    }

    public Object getAgentCamReprot(SummaryCamReportRequest request, int level, String agtGuid, String currency, String domain) {
        SqlRowSet rowSet = null;
        SqlRowSet sumRowSet = null;
        int itemCount;
        if (StringUtils.isEmpty(request.getAccountID())) {
            itemCount = 1;
            rowSet = campaignDAO.getCampaignAwardSelfByTimeAndAgtGuid(request.getStartDate(), request.getEndDate(), level, agtGuid, request.getPageIndex(), request.getPageSize());
            sumRowSet = campaignDAO.getCampaignAwardTotalSelfByTimeAndAgtGuid(request.getStartDate(), request.getEndDate(), level, agtGuid);

        } else {
            itemCount = campaignDAO.getCampaignAwardCountByTimeAndAgtGuid(request.getStartDate(), request.getEndDate(), level, agtGuid);
            if (itemCount > 0) {
                rowSet = campaignDAO.getCampaignAwardByTimeAndAgtGuid(request.getStartDate(), request.getEndDate(), level, agtGuid, request.getPageIndex(), request.getPageSize());
                sumRowSet = campaignDAO.getCampaignAwardTotalByTimeAndAgtGuid(request.getStartDate(), request.getEndDate(), level, agtGuid);
            }
        }

        SummaryCamReportResponse response = new SummaryCamReportResponse();
        List<AgentModel> agentModelList = agentDAO.getAgentByDomain(domain).orElseThrow(() -> new APIException(ResponseCode.BACKENDAPI_AGENT_NOT_FOUND, ResponseCode.BACKENDAPI_AGENT_NOT_FOUND.getErrorMessage()));

        while (rowSet != null && rowSet.next()) {
            SqlRowSet finalRowSet = rowSet;
            var currencyAgent = agentModelList.stream().filter(f -> f.getAGT_GUID().equals(finalRowSet.getString("agentGuid"))).findFirst();
            if (currencyAgent.isEmpty()) {
                continue;
            }
            response.addRecord(response.newRecordBuilder()
                    .accountID(currencyAgent.get().getAGT_AccountID())
                    .currency(currency)
                    .items(rowSet.getInt("items"))
                    .sumCamWin(rowSet.getBigDecimal("sumCamWin")).build());
        }

        response.setTotal(response.newCamTotalBuilder().build());
        if (sumRowSet != null && sumRowSet.first()) {
            response.setTotal(response.newCamTotalBuilder().totalCamWin(sumRowSet.getBigDecimal("sumCamWin"))
                    .currency(currency)
                    .items(sumRowSet.getInt("items")).build());
        }

        response.setItemCount(itemCount);
        response.setPageCount(1);
        if (itemCount > 0) {
            response.setPageCount(pageUtil.getPageCount(itemCount, request.getPageSize()));
        }

        response.setPageIndex(request.getPageIndex());
        response.setPageSize(request.getPageSize());

        response.setSubTotal(response.newCamTotalBuilder().totalCamWin(response.getRecordsSumCamWin())
                .currency(currency)
                .items(response.getRecords().size()).build());
        response.setAgentLevel(level);

        return response;
    }

    public Object getPlayerCamReport(SummaryCamReportRequest request, String agtGuid, String currency) {
        PlayerSummaryCamReportResponse response = new PlayerSummaryCamReportResponse();
        Map<String, String> org = agentService.getAgentTreeByAgent3(agtGuid);
        List<String> orgTree = Arrays.asList(org.get("Agent1"), org.get("Agent2"), org.get("Agent3"));
        int itemCount = campaignDAO.getPlayerCampaignAwardCountByTimeAndAgent3(request.getStartDate(), request.getEndDate(), agtGuid);
        response.setTotal(response.newCamTotalBuilder().build());
        if (itemCount > 0) {
            SqlRowSet rowSet = campaignDAO.getPlayerCampaignAwardByTimeAndAgent3(request.getStartDate(), request.getEndDate(), agtGuid, request.getPageIndex(), request.getPageSize());
            SqlRowSet totalRowSet = campaignDAO.getPlayerCampaignAwarTotalByTimeAndAgent3(request.getStartDate(), request.getEndDate(), agtGuid);
            while (rowSet.next()) {
                response.addRecord(response.newRecordBuilder().accountID(rowSet.getString("PLY_AccountID"))
                        .gameTurn(rowSet.getLong("DT_SEQ"))
                        .camAwardTypeID(rowSet.getInt("CAM_AwardTypeID"))
                        .camAwardTypeName(rowSet.getString("CAM_AwardTypeName"))
                        .camWin(rowSet.getBigDecimal("CAM_Win"))
                        .currency(rowSet.getString("DT_Currency"))
                        .camAwardCreateDateTime(rowSet.getDate("CAM_AwardCreateDateTime")).build());
            }
            while (totalRowSet.next()) {
                response.setTotal(response.newCamTotalBuilder().currency(currency)
                        .totalCamWin(totalRowSet.getBigDecimal("sumCamWin"))
                        .items(totalRowSet.getInt("items")).build());
            }
        }

        response.setItemCount(itemCount);
        response.setPageCount(1);
        if (itemCount > 0) {
            response.setPageCount(pageUtil.getPageCount(itemCount, request.getPageSize()));
        }

        response.setPageIndex(request.getPageIndex());
        response.setPageSize(request.getPageSize());

        response.setSubTotal(response.newCamTotalBuilder().totalCamWin(response.getRecordsSumCamWin())
                .currency(currency)
                .items(response.getRecords().size()).build());
        response.setAgentTree(orgTree);
        return response;
    }


    public String getCamReportDownload(SummaryReportCamDownloadRequest request, int level, String agtGuid, String currency, String domain, String language) {
        List<AgentModel> agentModelList = agentDAO.getAgentByDomain(domain).orElseThrow(() -> new APIException(ResponseCode.BACKENDAPI_AGENT_NOT_FOUND, ResponseCode.BACKENDAPI_AGENT_NOT_FOUND.getErrorMessage()));
        if (!StringUtils.isEmpty(request.getAccountID())) {
            Optional<AgentModel> agentModelOp = agentModelList.stream().filter(ag -> ag.getAGT_AccountID().equals(request.getAccountID())).findFirst();
            if (!agentModelOp.isEmpty()) {
                AgentModel agentModel = agentModelOp.get();
                level = agentModel.getAGT_Level();
                agtGuid = agentModel.getAGT_GUID();
            }
        }

        if (level == 3) {
            return getPlayerCamReportDownload(request, agtGuid, currency, language);
        }

        return getAgentCamReprotDownload(request, level, agtGuid, currency, agentModelList, language);
    }

    public String getAgentCamReprotDownload(SummaryReportCamDownloadRequest request, int level, String agtGuid, String currency, List<AgentModel> agentModelList, String language) {
        SqlRowSet rowSet;
        if (StringUtils.isEmpty(request.getAccountID())) {
            rowSet = campaignDAO.getCampaignAwardSelfByTimeAndAgtGuidForDownload((request.getStartDate()), request.getEndDate(), level, agtGuid);
        } else {
            rowSet = campaignDAO.getCampaignAwardByTimeAndAgtGuidDownload((request.getStartDate()), request.getEndDate(), level, agtGuid);
        }

        SummaryCamReportResponse response = new SummaryCamReportResponse();
        while (rowSet.next()) {
            var currencyAgent = agentModelList.stream().filter(f -> f.getAGT_GUID().equals(rowSet.getString("agentGuid"))).findFirst();
            if (currencyAgent.isEmpty()) {
                continue;
            }
            response.addRecord(response.newRecordBuilder()
                    .accountID(currencyAgent.get().getAGT_AccountID())
                    .currency(currency)
                    .items(rowSet.getInt("items"))
                    .sumCamWin(rowSet.getBigDecimal("sumCamWin")).build());
        }

        response.setTotal(response.newCamTotalBuilder().totalCamWin(response.getRecordsSumCamWin())
                .currency(currency)
                .items(response.getRecords().size()).build());
        response.setItemCount(response.getRecords().size());

        StringBuilder stringBuilder = new StringBuilder();
        DecimalFormat decimalFormat = new DecimalFormat("0.0000");
        String header = "帐号,赢分,币别,交易笔数";
        header = translateService.translateCsvHeaderByLanguage(language, header);
        stringBuilder.append(header);
        response.getRecords().forEach(record -> {
            stringBuilder.append(System.getProperty("line.separator"));
            stringBuilder.append(record.getAccountID()).append(",");
            stringBuilder.append(decimalFormat.format(record.getSumCamWin())).append(",");
            stringBuilder.append(record.getCurrency()).append(",");
            stringBuilder.append(record.getItems());
        });

        return stringBuilder.toString();
    }

    public String getPlayerCamReportDownload(SummaryReportCamDownloadRequest request, String agtGuid, String currency, String language) {
        PlayerSummaryCamReportResponse response = new PlayerSummaryCamReportResponse();
        Map<String, String> org = agentService.getAgentTreeByAgent3(agtGuid);
        List<String> orgTree = Arrays.asList(org.get("Agent1"), org.get("Agent2"), org.get("Agent3"));
        String agentTree = String.join("->", orgTree);
        SqlRowSet rowSet = campaignDAO.getPlayerCampaignAwardByTimeAndAgent3ForDownload(request.getStartDate(), request.getEndDate(), agtGuid);
        while (rowSet.next()) {
            response.addRecord(response.newRecordBuilder().accountID(rowSet.getString("PLY_AccountID"))
                    .gameTurn(rowSet.getLong("DT_SEQ"))
                    .camAwardTypeID(rowSet.getInt("CAM_AwardTypeID"))
                    .camAwardTypeName(rowSet.getString("CAM_AwardTypeName"))
                    .camWin(rowSet.getBigDecimal("CAM_Win"))
                    .currency(rowSet.getString("DT_Currency"))
                    .camAwardCreateDateTime(rowSet.getDate("CAM_AwardCreateDateTime")).build());
        }
        response.setTotal(response.newCamTotalBuilder().currency(currency)
                .totalCamWin(response.getRecordsSumCamWin())
                .items(response.getRecords().size()).build());

        response.setSubTotal(response.newCamTotalBuilder().totalCamWin(response.getRecordsSumCamWin())
                .currency(currency)
                .items(response.getRecords().size()).build());
        response.setItemCount(response.getRecords().size());

        StringBuilder stringBuilder = new StringBuilder();
        DecimalFormat decimalFormat = new DecimalFormat("0.0000");
        String header = "单号,中奖时间,帐号,阶层,赢分,币别";
        header = translateService.translateCsvHeaderByLanguage(language, header);
        stringBuilder.append(header);
        response.getRecords().forEach(record -> {
            stringBuilder.append(System.getProperty("line.separator"));
            stringBuilder.append("=\"").append(record.getGameTurn()).append("\"").append(",");
            stringBuilder.append(dateUtil.getDateFormat(record.getCamAwardCreateDateTime(), "yyyy-MM-dd HH:mm:ss")).append(",");
            stringBuilder.append(record.getAccountID()).append(",");
            stringBuilder.append(agentTree).append(",");
            stringBuilder.append(decimalFormat.format(record.getCamWin())).append(",");
            stringBuilder.append(record.getCurrency()).append(",");
        });

        return stringBuilder.toString();
    }

    public GameSummaryReportResponse getGameSummaryReport(GameSummaryReportRequest request, String agentGuid, int level, String mctGuid, String currency) throws IOException {
        gameTypeCommonService.checkGameTypeListValid(request.getGameType());
        agentGameRelationCommonService.checkGameTypeListEnable(mctGuid, agentGuid, level, request.getGameType());
        SqlRowSet rowSet = gameReportDAO.getGameSummaryReport(request.getGameType(), request.getStartDate(), request.getEndDate(), agentGuid, level);
        GameSummaryReportResponse response = new GameSummaryReportResponse();
        SubTotalSummaryReportResponse subTotal = new SubTotalSummaryReportResponse();
        List<GameSummaryReportDetailResponse> list = new ArrayList<>();
        while (rowSet.next()) {
            GameSummaryReportDetailResponse item = new GameSummaryReportDetailResponse();
            item.setCurrency(rowSet.getString("currency"));
            item.setGameType(rowSet.getInt("gameType"));
            item.setSumBets(rowSet.getBigDecimal("sumBets"));
            item.setSumValidBets(rowSet.getBigDecimal("sumValidBets"));
            item.setSumWin(rowSet.getBigDecimal("sumWin"));
            item.setSumJackpotContribute(rowSet.getBigDecimal("sumJackpotContribute"));
            item.setSumJackpot(rowSet.getBigDecimal("sumJackpot"));
            item.setSumJackpot2(rowSet.getBigDecimal("sumJackpot2"));
            item.setSumJackpot3(rowSet.getBigDecimal("sumJackpot3"));
            item.setCommission(rowSet.getBigDecimal("commission"));
            item.setSumNetWin(rowSet.getBigDecimal("sumNetWin"));
            item.setSumPureNetWin(rowSet.getBigDecimal("sumNetWin").subtract(rowSet.getBigDecimal("sumJackpot2")).add(rowSet.getBigDecimal("sumJackpotContribute")));
            item.setItems(rowSet.getInt("items"));
            list.add(item);

            //單頁小計
            subTotal.setSubTotalBets(subTotal.getSubTotalBets().add(item.getSumBets()));
            subTotal.setSubTotalValidBets(subTotal.getSubTotalValidBets().add(item.getSumValidBets()));
            subTotal.setSubTotalWin(subTotal.getSubTotalWin().add(item.getSumWin()));
            subTotal.setSubTotalJackpotContribute(subTotal.getSubTotalJackpotContribute().add(item.getSumJackpotContribute()));
            subTotal.setSubTotalJackpot(subTotal.getSubTotalJackpot().add(item.getSumJackpot()));
            subTotal.setSubTotalJackpot2(subTotal.getSubTotalJackpot2().add(item.getSumJackpot2()));
            subTotal.setSubTotalJackpot3(subTotal.getSubTotalJackpot3().add(item.getSumJackpot3()));
            subTotal.setSubCommission(subTotal.getSubCommission().add(item.getCommission()));
            subTotal.setSubTotalNetWin(subTotal.getSubTotalNetWin().add(item.getSumNetWin()));
            subTotal.setSubTotalPureNetWin(subTotal.getSubTotalNetWin().subtract(subTotal.getSubTotalJackpot2()).add(subTotal.getSubTotalJackpotContribute()));
            subTotal.setItems(subTotal.getItems() + item.getItems());

        }
        subTotal.setCurrency(currency);
        response.setRecords(list);
        response.setSubTotal(subTotal);

        return response;
    }


    public String getGameSummaryReportDownload(GameSummaryReportRequest request, String agentGuid, int level, String mctGuid, String language) throws IOException {
        gameTypeCommonService.checkGameTypeListValid(request.getGameType());
        agentGameRelationCommonService.checkGameTypeListEnable(mctGuid, agentGuid, level, request.getGameType());
        SqlRowSet rowSet = gameReportDAO.getGameSummaryReport(request.getGameType(), request.getStartDate(), request.getEndDate(), agentGuid, level);

        List<GameTypeModel> gameTypeList = gameTypeCommonService.getAllGameTypeByLanguage(language);

        DecimalFormat decimalFormat = new DecimalFormat("0.0000");
        StringBuilder stringBuilder = new StringBuilder();
        String header = "游戏类型,币别,总投注额,有效投注额,赢分,天九福彩,天九红包,彩金贡献,天九福彩(新),佣金,盈利,純盈利,交易笔数";
        //翻譯header,language是zh_CN不會翻譯
        header = translateService.translateCsvHeaderByLanguage(language, header);
        stringBuilder.append(header);
        while (rowSet.next()) {
            stringBuilder.append(System.getProperty("line.separator"));
            int gameType = rowSet.getInt("gameType");
            String gameTypeName = gameTypeList.stream().filter(g -> g.getGM_TypeSerial() == gameType).map(GameTypeModel::getGM_GameTypeName).findFirst().orElse("");

            String line = gameTypeName + "," +
                    rowSet.getString("currency") + "," +
                    decimalFormat.format(rowSet.getBigDecimal("sumBets")) + "," +
                    decimalFormat.format(rowSet.getBigDecimal("sumValidBets")) + "," +
                    decimalFormat.format(rowSet.getBigDecimal("sumWin")) + "," +
                    decimalFormat.format(rowSet.getBigDecimal("sumJackpot")) + "," +
                    decimalFormat.format(rowSet.getBigDecimal("sumJackpot3")) + "," +
                    decimalFormat.format(rowSet.getBigDecimal("sumJackpotContribute")) + "," +
                    decimalFormat.format(rowSet.getBigDecimal("sumJackpot2")) + "," +
                    decimalFormat.format(rowSet.getBigDecimal("commission")) + "," +
                    decimalFormat.format(rowSet.getBigDecimal("sumNetWin")) + "," +
                    decimalFormat.format(rowSet.getBigDecimal("sumNetWin").subtract(rowSet.getBigDecimal("sumJackpot2")).add(rowSet.getBigDecimal("sumJackpotContribute"))) + "," +
                    rowSet.getInt("items");
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }


    public PlayerSummaryReportResponse getPlayerSummaryReport(SummaryReportRequest request, String agentGuid, int level, String mctGuid, String currency) throws IOException {
        //如果有傳agent就帶入查詢
        if (request.getAgent() != null && request.getAgent().length() > 0) {
            AgentModel agentModel = agentCommonService.getAgentByAccountAndDomain(request.getAgent(), request.getDomain());
            if (agentModel.getAGT_Level() <= level) {
                throw new APIException(ResponseCode.BACKENDAPI_NO_OPERATE_PERMISSION);
            }

            if (agentModel.getAGT_Level() == 2 && !agentModel.getAGT_Agent1().equals(agentGuid)) {
                throw new APIException(ResponseCode.BACKENDAPI_NO_OPERATE_PERMISSION);
            }

            if (agentModel.getAGT_Level() == 3 && !agentModel.getAGT_Agent1().equals(agentGuid) && !agentModel.getAGT_Agent2().equals(agentGuid)) {
                throw new APIException(ResponseCode.BACKENDAPI_NO_OPERATE_PERMISSION);
            }

            agentGuid = agentModel.getAGT_GUID();
            level = agentModel.getAGT_Level();
        }

        gameTypeCommonService.checkGameTypeListValid(request.getGameType());
        agentGameRelationCommonService.checkGameTypeListEnable(mctGuid, agentGuid, level, request.getGameType());
        if (level == AGENT_LEVEL) {
            return getSummaryReportForPlayer(request, agentGuid, currency);
        }
        return getSummaryReportForAgent(request, agentGuid, level, mctGuid, currency);
    }


    public String getPlayerSummaryReportDownload(PlayerSummaryReportRequest request, SummaryReportRequest summaryReportRequest, String agentGuid, int level, String mctGuid, String language) throws IOException {

        //如果有傳agent就帶入查詢
        if (summaryReportRequest.getAgent() != null && summaryReportRequest.getAgent().length() > 0) {
            AgentModel agentModel = agentCommonService.getAgentByAccountAndDomain(summaryReportRequest.getAgent(), summaryReportRequest.getDomain());
            if (agentModel.getAGT_Level() <= level) {
                throw new APIException(ResponseCode.BACKENDAPI_NO_OPERATE_PERMISSION);
            }

            if (agentModel.getAGT_Level() == 2 && !agentModel.getAGT_Agent1().equals(agentGuid)) {
                throw new APIException(ResponseCode.BACKENDAPI_NO_OPERATE_PERMISSION);
            }

            if (agentModel.getAGT_Level() == 3 && !agentModel.getAGT_Agent1().equals(agentGuid) && !agentModel.getAGT_Agent2().equals(agentGuid)) {
                throw new APIException(ResponseCode.BACKENDAPI_NO_OPERATE_PERMISSION);
            }

            agentGuid = agentModel.getAGT_GUID();
            level = agentModel.getAGT_Level();
        }


        gameTypeCommonService.checkGameTypeListValid(request.getGameType());
        agentGameRelationCommonService.checkGameTypeListEnable(mctGuid, agentGuid, level, request.getGameType());
        if (level == AGENT_LEVEL) {
            return getSummaryReportForPlayerDownload(request, agentGuid, language);
        }
        return getSummaryReportForAgentDownload(request, agentGuid, level, mctGuid, language);
    }

    private PlayerSummaryReportResponse getSummaryReportForPlayer(SummaryReportRequest request, String agentGuid, String currency) {
        PlayerSummaryReportResponse response = new PlayerSummaryReportResponse();
        int itemCount = gameReportDAO.getPlayerSummaryReportCount(request.getGameType(),
                request.getStartDate(), request.getEndDate(), agentGuid);
        List<PlayerSummaryReportDetailResponse> list = new ArrayList<>();
        SubTotalSummaryReportResponse subTotal = new SubTotalSummaryReportResponse();
        if (itemCount > 0) {
            SqlRowSet rowSet = gameReportDAO.getPlayerSummaryReport(request.getGameType(), request.getStartDate(), request.getEndDate(),
                    agentGuid, request.getPageIndex(), request.getPageSize());
            while (rowSet.next()) {
                PlayerSummaryReportDetailResponse item = new PlayerSummaryReportDetailResponse();

                item.setAccountID(rowSet.getString("accountID"));
                item.setCurrency(rowSet.getString("currency"));
                item.setSumBets(rowSet.getBigDecimal("sumBets"));
                item.setSumValidBets(rowSet.getBigDecimal("sumValidBets"));
                item.setSumWin(rowSet.getBigDecimal("sumWin"));
                item.setSumJackpotContribute(rowSet.getBigDecimal("sumJackpotContribute"));
                item.setSumJackpot(rowSet.getBigDecimal("sumJackpot"));
                item.setSumJackpot2(rowSet.getBigDecimal("sumJackpot2"));
                item.setSumJackpot3(rowSet.getBigDecimal("sumJackpot3"));
                item.setCommission(rowSet.getBigDecimal("commission"));
                item.setSumNetWin(rowSet.getBigDecimal("sumNetWin"));
                item.setSumPureNetWin(rowSet.getBigDecimal("sumNetWin").subtract(rowSet.getBigDecimal("sumJackpot2")).add(rowSet.getBigDecimal("sumJackpotContribute")));
                item.setItems(rowSet.getInt("items"));
                list.add(item);

                //單頁小計
                subTotal = getSubTotal(subTotal, item);
            }
            response.setPageCount(pageUtil.getPageCount(itemCount, request.getPageSize()));
        } else {
            response.setPageCount(1);
        }
        subTotal.setCurrency(currency);

        response.setRecords(list);
        response.setPageIndex(request.getPageIndex());
        response.setPageSize(request.getPageSize());
        response.setItemCount(itemCount);
        response.setSubTotal(subTotal);
        response.setTotal(getTotalSummaryReport(request.getGameType(), request.getStartDate(), request.getEndDate(), agentGuid, 3, currency));

        return response;
    }

    private SubTotalSummaryReportResponse getSubTotal(SubTotalSummaryReportResponse subTotal, PlayerSummaryReportDetailResponse report) {
        subTotal.setSubTotalBets(subTotal.getSubTotalBets().add(report.getSumBets()));
        subTotal.setSubTotalValidBets(subTotal.getSubTotalValidBets().add(report.getSumValidBets()));
        subTotal.setSubTotalWin(subTotal.getSubTotalWin().add(report.getSumWin()));
        subTotal.setSubTotalJackpotContribute(subTotal.getSubTotalJackpotContribute().add(report.getSumJackpotContribute()));
        subTotal.setSubTotalJackpot(subTotal.getSubTotalJackpot().add(report.getSumJackpot()));
        subTotal.setSubTotalJackpot2(subTotal.getSubTotalJackpot2().add(report.getSumJackpot2()));
        subTotal.setSubTotalJackpot3(subTotal.getSubTotalJackpot3().add(report.getSumJackpot3()));
        subTotal.setSubCommission(subTotal.getSubCommission().add(report.getCommission()));
        subTotal.setSubTotalNetWin(subTotal.getSubTotalNetWin().add(report.getSumNetWin()));
        subTotal.setSubTotalPureNetWin(subTotal.getSubTotalNetWin().subtract(subTotal.getSubTotalJackpot2()).add(subTotal.getSubTotalJackpotContribute()));
        subTotal.setItems(subTotal.getItems() + report.getItems());

        return subTotal;
    }

    private String getSummaryReportForPlayerDownload(PlayerSummaryReportRequest request, String agentGuid, String language) {
        StringBuilder stringBuilder = new StringBuilder();
        String header = "会员帐号,币别,总投注额,有效投注额,赢分,天九福彩,天九红包,彩金贡献,天九福彩(新),佣金,盈利,純盈利,交易笔数";
        //翻譯header,language是zh_CN不會翻譯
        header = translateService.translateCsvHeaderByLanguage(language, header);
        stringBuilder.append(header);

        int itemCount = gameReportDAO.getPlayerSummaryReportCount(request.getGameType(),
                request.getStartDate(), request.getEndDate(), agentGuid);
        if (itemCount > 0) {
            SqlRowSet rowSet = gameReportDAO.getPlayerSummaryReport(request.getGameType(), request.getStartDate(), request.getEndDate(),
                    agentGuid, 1, itemCount);

            DecimalFormat decimalFormat = new DecimalFormat("0.0000");

            while (rowSet.next()) {
                stringBuilder.append(System.getProperty("line.separator"));

                String line = rowSet.getString("accountID") + "," +
                        rowSet.getString("currency") + "," +
                        decimalFormat.format(rowSet.getBigDecimal("sumBets")) + "," +
                        decimalFormat.format(rowSet.getBigDecimal("sumValidBets")) + "," +
                        decimalFormat.format(rowSet.getBigDecimal("sumWin")) + "," +
                        decimalFormat.format(rowSet.getBigDecimal("sumJackpot")) + "," +
                        decimalFormat.format(rowSet.getBigDecimal("sumJackpot3")) + "," +
                        decimalFormat.format(rowSet.getBigDecimal("sumJackpotContribute")) + "," +
                        decimalFormat.format(rowSet.getBigDecimal("sumJackpot2")) + "," +
                        decimalFormat.format(rowSet.getBigDecimal("commission")) + "," +
                        decimalFormat.format(rowSet.getBigDecimal("sumNetWin")) + "," +
                        decimalFormat.format(rowSet.getBigDecimal("sumNetWin").subtract(rowSet.getBigDecimal("sumJackpot2")).add(rowSet.getBigDecimal("sumJackpotContribute"))) + "," +
                        rowSet.getInt("items");
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }

    private PlayerSummaryReportResponse getSummaryReportForAgent(SummaryReportRequest request, String agentGuid, int level, String mctGuid, String currency) {
        PlayerSummaryReportResponse response = new PlayerSummaryReportResponse();
        int itemCount = gameReportDAO.getAgentSummaryReportCount(request.getGameType(),
                request.getStartDate(), request.getEndDate(), agentGuid, level);
        List<PlayerSummaryReportDetailResponse> list = new ArrayList<>();
        SubTotalSummaryReportResponse subTotal = new SubTotalSummaryReportResponse();
        if (itemCount > 0) {
            SqlRowSet rowSet = gameReportDAO.getAgentSummaryReport(request.getGameType(), request.getStartDate(), request.getEndDate(),
                    agentGuid, level, request.getPageIndex(), request.getPageSize());
            var agentList = agentService.getAllAgentsByMctGUID(mctGuid);

            while (rowSet.next()) {
                PlayerSummaryReportDetailResponse item = new PlayerSummaryReportDetailResponse();
                String selectAgentGuid = rowSet.getString("agentGuid");
                var currencyAgents = agentList.stream().filter(f -> f.getAGT_GUID().equals(selectAgentGuid)).collect(Collectors.toList());
                if (currencyAgents.isEmpty()) {
                    continue;
                }
                item.setAccountID(currencyAgents.get(0).getAGT_AccountID());
                item.setCurrency(rowSet.getString("currency"));
                item.setSumBets(rowSet.getBigDecimal("sumBets"));
                item.setSumValidBets(rowSet.getBigDecimal("sumValidBets"));
                item.setSumWin(rowSet.getBigDecimal("sumWin"));
                item.setSumJackpotContribute(rowSet.getBigDecimal("sumJackpotContribute"));
                item.setSumJackpot(rowSet.getBigDecimal("sumJackpot"));
                item.setSumJackpot2(rowSet.getBigDecimal("sumJackpot2"));
                item.setSumJackpot3(rowSet.getBigDecimal("sumJackpot3"));
                item.setCommission(rowSet.getBigDecimal("commission"));
                item.setSumNetWin(rowSet.getBigDecimal("sumNetWin"));
                item.setSumPureNetWin(rowSet.getBigDecimal("sumNetWin").subtract(rowSet.getBigDecimal("sumJackpot2")).add(rowSet.getBigDecimal("sumJackpotContribute")));
                item.setItems(rowSet.getInt("items"));
                list.add(item);

                //單頁小計
                subTotal = getSubTotal(subTotal, item);
            }

            response.setPageCount(pageUtil.getPageCount(itemCount, request.getPageSize()));
        } else {
            response.setPageCount(1);
        }
        subTotal.setCurrency(currency);

        response.setRecords(list);
        response.setPageIndex(request.getPageIndex());
        response.setPageSize(request.getPageSize());
        response.setItemCount(itemCount);
        response.setSubTotal(subTotal);
        response.setTotal(getTotalSummaryReport(request.getGameType(), request.getStartDate(), request.getEndDate(), agentGuid, level, currency));

        return response;
    }

    private TotalSummaryReportResponse getTotalSummaryReport(List<Integer> gameType, Date startDate, Date endDate, String agentGuid, int level, String currency) {
        SqlRowSet rowSet = gameReportDAO.getTotalSummaryReport(gameType, startDate, endDate, agentGuid, level);
        TotalSummaryReportResponse item = new TotalSummaryReportResponse();
        while (rowSet.next()) {
            item.setTotalBets(rowSet.getBigDecimal("sumBets"));
            item.setTotalValidBets(rowSet.getBigDecimal("sumValidBets"));
            item.setTotalWin(rowSet.getBigDecimal("sumWin"));
            item.setTotalJackpotContribute(rowSet.getBigDecimal("sumJackpotContribute"));
            item.setTotalJackpot(rowSet.getBigDecimal("sumJackpot"));
            item.setTotalJackpot2(rowSet.getBigDecimal("sumJackpot2"));
            item.setTotalJackpot3(rowSet.getBigDecimal("sumJackpot3"));
            item.setTotalCommission(rowSet.getBigDecimal("commission"));
            item.setTotalNetWin(rowSet.getBigDecimal("sumNetWin"));
            item.setTotalPureNetWin(item.getTotalNetWin().subtract(item.getTotalJackpot2()).add(item.getTotalJackpotContribute()));
            item.setItems(rowSet.getInt("items"));
        }
        item.setCurrency(currency);
        return item;
    }

    private String getSummaryReportForAgentDownload(PlayerSummaryReportRequest request, String agentGuid, int level, String mctGuid, String language) {
        StringBuilder stringBuilder = new StringBuilder();
        String header = "会员帐号,币别,总投注额,有效投注额,赢分,天九福彩,天九红包,彩金贡献,天九福彩(新),佣金,盈利,純盈利,交易笔数";
        //翻譯header,language是zh_CN不會翻譯
        header = translateService.translateCsvHeaderByLanguage(language, header);
        stringBuilder.append(header);

        int itemCount = gameReportDAO.getAgentSummaryReportCount(request.getGameType(),
                request.getStartDate(), request.getEndDate(), agentGuid, level);
        if (itemCount > 0) {
            SqlRowSet rowSet = gameReportDAO.getAgentSummaryReport(request.getGameType(), request.getStartDate(), request.getEndDate(),
                    agentGuid, level, 1, itemCount);
            var agentList = agentService.getAllAgentsByMctGUID(mctGuid);

            DecimalFormat decimalFormat = new DecimalFormat("0.0000");

            while (rowSet.next()) {
                stringBuilder.append(System.getProperty("line.separator"));

                String selectAgentGuid = rowSet.getString("agentGuid");
                var currencyAgents = agentList.stream().filter(f -> f.getAGT_GUID().equals(selectAgentGuid)).collect(Collectors.toList());
                if (currencyAgents.isEmpty()) {
                    continue;
                }
                String line = currencyAgents.get(0).getAGT_AccountID() + "," +
                        rowSet.getString("currency") + "," +
                        decimalFormat.format(rowSet.getBigDecimal("sumBets")) + "," +
                        decimalFormat.format(rowSet.getBigDecimal("sumValidBets")) + "," +
                        decimalFormat.format(rowSet.getBigDecimal("sumWin")) + "," +
                        decimalFormat.format(rowSet.getBigDecimal("sumJackpot")) + "," +
                        decimalFormat.format(rowSet.getBigDecimal("sumJackpot3")) + "," +
                        decimalFormat.format(rowSet.getBigDecimal("sumJackpotContribute")) + "," +
                        decimalFormat.format(rowSet.getBigDecimal("sumJackpot2")) + "," +
                        decimalFormat.format(rowSet.getBigDecimal("commission")) + "," +
                        decimalFormat.format(rowSet.getBigDecimal("sumNetWin")) + "," +
                        decimalFormat.format(rowSet.getBigDecimal("sumNetWin").subtract(rowSet.getBigDecimal("sumJackpot2")).add(rowSet.getBigDecimal("sumJackpotContribute"))) + "," +
                        rowSet.getInt("items");
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }

    public SinglePlayerSummaryReportResponse getSinglePlayerSummaryReport(SinglePlayerSummaryReportRequest request, String agentGuid, int level, String mctGuid) {
        playerCommonService.checkUserExistByAgent(agentGuid, request.getPlayerID(), level);
        SinglePlayerSummaryReportResponse reportResponse = new SinglePlayerSummaryReportResponse();
        List<SinglePlayerSummaryReportDetailResponse> list = new ArrayList<>();
        SqlRowSet rowSet = gameReportDAO.getSinglePlayerSummaryReport(agentGuid, level, request.getGameType(), request.getStartDate(), request.getEndDate(),
                request.getPlayerID());
        List<OrgTree> rawOrgTreeList = agentService.getAgentTreeListByAgent3(mctGuid);

        while (rowSet.next()) {
            SinglePlayerSummaryReportDetailResponse item = new SinglePlayerSummaryReportDetailResponse();

            List<String> orgTreeList = agentService.getAgentTreeByAgent3AndLevel(rawOrgTreeList, level, rowSet.getString("agent"));

            item.setAgentTree(orgTreeList);
            item.setGameType(rowSet.getString("gameType"));
            item.setAccountID(rowSet.getString("accountID"));
            item.setCurrency(rowSet.getString("currency"));
            item.setSumBets(rowSet.getBigDecimal("sumBets"));
            item.setSumValidBets(rowSet.getBigDecimal("sumValidBets"));
            item.setSumWin(rowSet.getBigDecimal("sumWin"));
            item.setSumJackpotContribute(rowSet.getBigDecimal("sumJackpotContribute"));
            item.setSumJackpot(rowSet.getBigDecimal("sumJackpot"));
            item.setSumJackpot2(rowSet.getBigDecimal("sumJackpot2"));
            item.setSumJackpot3(rowSet.getBigDecimal("sumJackpot3"));
            item.setCommission(rowSet.getBigDecimal("commission"));
            item.setSumNetWin(rowSet.getBigDecimal("sumNetWin"));
            item.setItems(rowSet.getInt("items"));
            list.add(item);
        }
        reportResponse.setRecords(list);

        return reportResponse;
    }

    public DailyReportResponse getDailyReport(DailyReportRequest request, String agentGuid, int level, String mctGuid, String currency) throws IOException {
        gameTypeCommonService.checkGameTypeListValid(request.getGameType());
        agentGameRelationCommonService.checkGameTypeListEnable(mctGuid, agentGuid, level, request.getGameType());
        SqlRowSet rowSet = gameReportDayDAO.getDailyReportByDate(request.getGameType(), request.getStartDate(), request.getEndDate(), agentGuid, level);
        DailyReportResponse response = new DailyReportResponse();
        List<DailyReportDetailResponse> list = new ArrayList<>();
        TotalSummaryReportResponse total = new TotalSummaryReportResponse();
        while (rowSet.next()) {
            DailyReportDetailResponse item = new DailyReportDetailResponse();
            item.setAccountDate(rowSet.getDate("accountDate"));
            item.setCurrency(rowSet.getString("currency"));
            item.setSumBets(rowSet.getBigDecimal("sumBets"));
            item.setSumValidBets(rowSet.getBigDecimal("sumValidBets"));
            item.setSumWin(rowSet.getBigDecimal("sumWin"));
            item.setSumJackpotContribute(rowSet.getBigDecimal("sumJackpotContribute"));
            item.setSumJackpot(rowSet.getBigDecimal("sumJackpot"));
            item.setSumJackpot2(rowSet.getBigDecimal("sumJackpot2"));
            item.setSumJackpot3(rowSet.getBigDecimal("sumJackpot3"));
            item.setCommission(rowSet.getBigDecimal("commission"));
            item.setSumNetWin(rowSet.getBigDecimal("sumNetWin"));
            item.setSumPureNetWin(rowSet.getBigDecimal("sumNetWin").subtract(rowSet.getBigDecimal("sumJackpot2")).add(rowSet.getBigDecimal("sumJackpotContribute")));
            item.setItems(rowSet.getInt("items"));
            list.add(item);

            total.setCurrency(currency);
            total.setTotalBets(total.getTotalBets().add(item.getSumBets()));
            total.setTotalValidBets(total.getTotalValidBets().add(item.getSumValidBets()));
            total.setTotalWin(total.getTotalWin().add(item.getSumWin()));
            total.setTotalJackpotContribute(total.getTotalJackpotContribute().add(item.getSumJackpotContribute()));
            total.setTotalJackpot(total.getTotalJackpot().add(item.getSumJackpot()));
            total.setTotalJackpot2(total.getTotalJackpot2().add(item.getSumJackpot2()));
            total.setTotalJackpot3(total.getTotalJackpot3().add(item.getSumJackpot3()));
            total.setTotalCommission(total.getTotalCommission().add(item.getCommission()));
            total.setTotalNetWin(total.getTotalNetWin().add(item.getSumNetWin()));
            total.setTotalPureNetWin(total.getTotalNetWin().subtract(total.getTotalJackpot2()).add(total.getTotalJackpotContribute()));
            total.setItems(total.getItems() + item.getItems());
        }
        response.setRecords(list);
        response.setTotal(total);

        return response;
    }

    public String getDailyReportDownload(DailyReportRequest request, String agentGuid, int level, String mctGuid, String language) throws IOException {
        gameTypeCommonService.checkGameTypeListValid(request.getGameType());
        agentGameRelationCommonService.checkGameTypeListEnable(mctGuid, agentGuid, level, request.getGameType());
        SqlRowSet rowSet = gameReportDayDAO.getDailyReportByDate(request.getGameType(), request.getStartDate(), request.getEndDate(), agentGuid, level);

        DecimalFormat decimalFormat = new DecimalFormat("0.0000");
        StringBuilder stringBuilder = new StringBuilder();
        String header = "帐务日期,币别,总投注额,有效投注额,赢分,天九福彩,天九红包,彩金贡献,天九福彩(新),佣金,盈利,純盈利,交易笔数";
        //翻譯header,language是zh_CN不會翻譯
        header = translateService.translateCsvHeaderByLanguage(language, header);
        stringBuilder.append(header);
        while (rowSet.next()) {
            stringBuilder.append(System.getProperty("line.separator"));
            String line = rowSet.getDate("accountDate") + "," +
                    rowSet.getString("currency") + "," +
                    decimalFormat.format(rowSet.getBigDecimal("sumBets")) + "," +
                    decimalFormat.format(rowSet.getBigDecimal("sumValidBets")) + "," +
                    decimalFormat.format(rowSet.getBigDecimal("sumWin")) + "," +
                    decimalFormat.format(rowSet.getBigDecimal("sumJackpot")) + "," +
                    decimalFormat.format(rowSet.getBigDecimal("sumJackpot3")) + "," +
                    decimalFormat.format(rowSet.getBigDecimal("sumJackpotContribute")) + "," +
                    decimalFormat.format(rowSet.getBigDecimal("sumJackpot2")) + "," +
                    decimalFormat.format(rowSet.getBigDecimal("commission")) + "," +
                    decimalFormat.format(rowSet.getBigDecimal("sumNetWin")) + "," +
                    decimalFormat.format(rowSet.getBigDecimal("sumNetWin").subtract(rowSet.getBigDecimal("sumJackpot2")).add(rowSet.getBigDecimal("sumJackpotContribute"))) + "," +
                    rowSet.getInt("items");
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }
}
