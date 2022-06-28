# backend-services

ver1.3.0
* fix dailytradeListDownload 6001 not show

ver1.2.9
* dailytradeList add 6001

ver1.2.8
* refactor camReport

ver1.2.7
* queryGameDetail url add language

ver1.2.6
* header get language

ver1.2.5
* download report i18n

ver1.2.4
* add campaign report

ver1.2.3
* fix summary agent3 pure net win subtract brackets

ver1.2.2
* fix summary agent3 pure net win

ver1.2.1
* fix dailyreaport pure net win

ver1.2.0
* fix getGameSummaryReportDownload pure net win

ver1.1.9
* getGameCodeList change to filter by 2 month record

ver1.1.8
* fix fix pure net win

ver1.1.7
* gameReport,dailyReport,summaryReport add sumPureNetWin 

ver1.1.6
* gameDetail url move to common-service

ver1.1.5
* query game detail add version

ver1.1.4
* JackpotContribute decimal to 4
* modify GameRecordService gameType5 for Jackpot2&3
* add GameType5
* modify GameReportResponse For FrontendWeb

ver1.1.3
* JackpotReportService add getJackpotAgentReport
* add new API JackpotPlayerReport
* fixed getAgent3ListByAgentGuidAndLevel empty

ver1.1.2
* enable gameTypes from gameReportDay

ver1.1.1
* GameRecordService add jackpotType3

ver1.1.0
* add SubAccountService'login add Transactional
* modify rtp calculate without jackpot2

ver1.0.9
* fix enable games redis map isEmpty
* fix player detail getNetWinGroupByDate

ver1.0.8
* add jackpot2 column
* open jackpot2

ver1.0.7
* getUserInfo 優化歷史bets及歷史netWin語法並合併成單一個sql

ver1.0.6
* modify 4002 bankerAdvantage

ver1.0.4
* summaryReportPlayerDownload modify pageSize

ver1.0.3
* change GameRecordService use GameReportDayDAO

ver1.0.2
* add getGameReportByAgent3
* add getGameReportByAccountType
* modify getDailiTradeList and DownLoad,get list for 7days 

ver1.0.1
* gamereport增加可以查代理
* modify dailytrade list add request param current day and add response dayList
* modify DailyTradeService for Download 7days
* modify playerDetail for multi player
* agentDetail add permition condition
* modify get agent tree
* modify SubAccountService'updatePasswordForHandler for AgentLevel
* dailytradelist add orgTree
* modify dailytradelist to multiple playerid
* modify addAgentBymerchant to addAgentBymanagerid
* add agentListByManagerID
* add addagentBymerchant, managerList
* modify PlayerService'getPlayerList for agentLevel
* add_GameCodeList request&response
* modify_gameRecordService'getGameReportInfo for playerAccount&GameCode
* modify_gameRecordService'getGameReportDownload for playerAccount&GameCode
* get transferlist,playerdetail,gamedetail,dailytradelist by agent level
* modify login ip check condition
* add SinglePlayerSummaryReportDetailResponse
* add SinglePlayerSummaryReportResponse
* add SinglePlayerSummaryReportRequest
* add BackendSinglePlayerSummaryReportHandler
* add GameRecordService getSinglePlayerSummaryReport
* modify TransferRecordService add orgTree

ver1.0.0
* first release