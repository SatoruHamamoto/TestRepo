package com.gnomes.uiservice;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.bind.JAXB;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.data.CommandData;
import com.gnomes.common.data.CommandDatas;
import com.gnomes.common.data.CommandScreenData;
import com.gnomes.common.data.GnomesInputDomain;
import com.gnomes.common.data.GnomesSystemBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.tags.GnomesCTagDictionary;
import com.gnomes.common.util.KeyStoreUtilities;
import com.gnomes.system.constants.BLSecurityConstants;
import com.gnomes.system.entity.MstrSystemDefine;
import com.gnomes.system.model.PartsPrivilegeInfoRead;

import biz.grandsight.ex.rs.CGenReportMeta;
import biz.grandsight.ex.rs.CReportGenException;

/**
 * ServletContextListener の拡張 (システムロケールとシステムエンティティの設定）
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/12/20 KCC/H.Gojo                  初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesServletContextListener implements ServletContextListener {

    private static final String LOCALE_PARAM_NAME = "LOCALE";

    private static final String TIMEZONE_PARAM_NAME = "TZ";

    private static final String DB_TIMEZONE_PARAM_NAME = "DB-TZ";

    /** 帳票印字処理で使用する定義ファイル名 */
    protected static final String REPORT_DEFINITION_XML_FILE_NAME = "ReportDefinitionXMLFileName";

    /** 帳票印字処理で使用する DBサーバ名 */
    protected static final String REPORT_DB_SERVER_NAME = "ReportDBServerName";

    /** 帳票印字処理で使用する DB接続ポートNo. */
    protected static final String REPORT_DB_PORT_NO = "ReportDBPortNo";

    /** 帳票印字処理で使用する DBユーザユーザID */
    protected static final String REPORT_DB_USER_NAME = "ReportDBUserName";

    /** 帳票印字処理で使用する DBユーザパスワード */
    protected static final String REPORT_DB_PASSWORD = "ReportDBPassword";

    /** 帳票印字処理で使用する DB名 */
    protected static final String REPORT_DB_NAME = "ReportDBName";

    /** 帳票印字処理で使用する DB名(保管領域) */
    protected static final String REPORT_DB_NAME_OF_STORAGE = "ReportDBNameOfStorage";

    /** ConversionScopeのオブジェクトのタイムアウト値 */
    protected static final String CONVERSION_TIME_OUT = "Conversion_Time_Out";

    /** DB種類 */
    protected static final String REPORT_DB_TYPE = "ReportDBType";

    /** 秤量器IFクローズまでのタイムアウト時間 */
    protected static final String WEIGH_CLOSE_TIMEOUT = "WeighCloseTimeout";

    /** 秤量インジケータの表示周期(ms) */
    protected static final String CYCLIC_WEIGH_INTERVAL_MILISECOND = "CyclicWeighIntervalMiliSecond";

    /** 同期モード */
    protected static final String IS_CYCLIC_WEIGH_SYNC_ACCESS_MODE = "isCyclicWeighSyncAccessMode";

    /** カスタムタグファイルパス */
    protected static final String TAG_DICT_XML_PATH = "/WEB-INF/taglibs/";

    /** 共通コマンドファイルパス */
    private static final String COMMON_COMMAND_PATH = "/WEB-INF/common-command/";

    /** 共通コマンドファイルフィルター */
    private static final String[] COMMAND_FILTERS = { "GnomesCommands_", "GnomesCommandsContents_", "GnomesCommandsContentsJob_" };

    /** カスタムタグファイル名接頭語 */
    private static final String TAG_DICT_FILE_HEAD = "gnomes_";

    /** カスタムタグファイル名接尾語 */
    private static final String TAG_DICT_FILE_TAIL = "_tag_dict.xml";

    /** 定数定義クラス共通パッケージ名 */
    private static final String COMMON_CONSTANTS_CLASS_PACKAGE_NAME = "com.gnomes.common.view.";

    /** コマンドID 定数定義クラス名リスト */
    private static final String[] COMMAND_ID_CONSTANTS_CLASSNAME_LIST =
        {
                "CommandIdConstantsContentsJob",
                "CommandIdConstantsContents",
                "CommandIdConstants"
        };

    /** 画面ID 定数定義クラス名リスト */
    private static final String[] SCREEN_ID_CONSTANTS_CLASSNAME_LIST =
        {
                "ScreenIdConstantsContentsJob",
                "ScreenIdConstantsContents",
                "ScreenIdConstants"
        };

    /** リソース定数クラスパッケージ名 */
    private static final String RESOURCE_CONSTANTS_CLASS_PACKAGE_NAME = "com.gnomes.common.resource.";

    /** リソース 定数定義クラス名リスト */
    private static final String[] RESOURCE_CONSTANTS_CLASSNAME_LIST =
        {
                "GnomesResources_Contents_JobConstants",
                "GnomesResources_ContentsConstants",
                "GnomesResourcesConstants"
        };

    /** ドメイン定義ファイルパス */
    protected static final String DOMAIN_XML_PATH = "/WEB-INF/src/";

    /** 入力項目ドメイン定義ファイル名 */
    private static final String DOMAIN_CONSTANTS_FILE_NAME = "GnomesDomainConstants.xml";

    /** アプリケーションBean */
    @Inject
    protected GnomesSystemBean gnomesSystemBean;

    /** セキュリティ関連 */
    @Inject
    protected KeyStoreUtilities keyStoreUtilities;

    /** カスタムタグ辞書 */
    @Inject
    protected GnomesCTagDictionary gnomesCTagDictionary;

    @Override
    public void contextDestroyed(ServletContextEvent event) { /* empty. */ }

    /**
     * コンテキスト初期化後にコールされるイベント処理
     *
     * @param イベントハンドラ
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {

        /*
         * システムのデフォルトロケールおよびタイムゾーンを設定する。
         */
        ServletContext sc = event.getServletContext();
        // ConfigProperties

        System.out.println("GnomesServletContextAttributeListener activated.");

        // セキュリティ関連の初期化
        keyStoreUtilities.Wakeup();


        java.util.Enumeration<String> setout = sc.getInitParameterNames();

        String reportDefinitionXMLFileName = null;
        String reportDBServerName = null;
        String reportDBPortNo = null;
        String reportDBUserName = null;
        String reportDBPassword = null;
        String reportDBName = null;
        String reportDBNameOfStorage = null;
        String reportDBType = null;
        String conversionTimeOut = null;
        String weighCloseTimeout = null;
        String cyclicWeighIntervalMiliSecond = null;
        String coordinatedUniversalTime = null;
        String isCyclicWeighSyncAccessMode = null;

        while (setout.hasMoreElements()) {
            String paramName = setout.nextElement();
            System.out.println("Param: "+ paramName + " " + sc.getInitParameter(paramName));
            if (paramName.equals(LOCALE_PARAM_NAME)) {
                String[] params = sc.getInitParameter(paramName).split(CommonConstants.SPLIT_CHAR);
                if (params.length >= 2) {
                    Locale.setDefault(new Locale(params[0], params[1]));
                    System.out.println("Default Locale set to " + Locale.getDefault().toString());
                }
            }
            if (paramName.equals(TIMEZONE_PARAM_NAME)) {
                if (!sc.getInitParameter(paramName).isEmpty()) {
                    try {
                        TimeZone timeZone = TimeZone.getTimeZone(sc.getInitParameter(paramName));    // パラメータには "Asia/Tokyo" のようなタイムゾーンID が設定されている想定
                        TimeZone.setDefault(timeZone);
                    }
                    catch(Exception e){
                        // 何もしない
                    }
                }
                System.out.println("Default TimeZone set to " + TimeZone.getDefault().getDisplayName());
            }

            // 帳票印字処理で使用する定義ファイル名
            if (REPORT_DEFINITION_XML_FILE_NAME.equals(paramName)) {
                reportDefinitionXMLFileName = sc.getInitParameter(paramName);
                this.gnomesSystemBean.setReportDefinitionXMLFileName(reportDefinitionXMLFileName);
            }
            // 帳票印字処理で使用する DBサーバ名
            if (REPORT_DB_SERVER_NAME.equals(paramName)) {
                reportDBServerName = sc.getInitParameter(paramName);
            }
            // 帳票印字処理で使用する DB接続ポートNo.
            if (REPORT_DB_PORT_NO.equals(paramName)) {
                reportDBPortNo = sc.getInitParameter(paramName);
            }
            // 帳票印字処理で使用する DBユーザユーザID
            if (REPORT_DB_USER_NAME.equals(paramName)) {
                reportDBUserName = sc.getInitParameter(paramName);
            }
            // 帳票印字処理で使用する DBユーザパスワード
            if (REPORT_DB_PASSWORD.equals(paramName)) {
                reportDBPassword = sc.getInitParameter(paramName);
            }
            // 帳票印字処理で使用する DB名
            if (REPORT_DB_NAME.equals(paramName)) {
                reportDBName = sc.getInitParameter(paramName);
            }
            // 帳票印字処理で使用する DB名
            if (REPORT_DB_NAME_OF_STORAGE.equals(paramName)) {
                reportDBNameOfStorage = sc.getInitParameter(paramName);
            }
            // DB種類
            if (REPORT_DB_TYPE.equals(paramName)) {
                reportDBType = sc.getInitParameter(paramName);
            }
            // ConversionScopeのオブジェクトのタイムアウト値
            if (CONVERSION_TIME_OUT.equals(paramName)) {
                conversionTimeOut = sc.getInitParameter(paramName);
            }
            // 秤量器IFクローズまでのタイムアウト時間
            if (WEIGH_CLOSE_TIMEOUT.equals(paramName)) {
                weighCloseTimeout = sc.getInitParameter(paramName);
                if(weighCloseTimeout != null && !weighCloseTimeout.isEmpty()){
                    this.gnomesSystemBean.setWeighCloseTimeout(Long.parseLong(weighCloseTimeout));
                }
            }
            // 秤量インジケータの更新周期(ms)
            if (CYCLIC_WEIGH_INTERVAL_MILISECOND.equals(paramName)) {
                cyclicWeighIntervalMiliSecond = sc.getInitParameter(paramName);
                if(cyclicWeighIntervalMiliSecond != null && !cyclicWeighIntervalMiliSecond.isEmpty()){
                    this.gnomesSystemBean.setCyclicWeighIntervalMiliSecond(Long.parseLong(cyclicWeighIntervalMiliSecond));
                }
                else {
                    this.gnomesSystemBean.setCyclicWeighIntervalMiliSecond(CommonConstants.CYCLIC_WEIGH_INTERVAL_MILISECOND);
                }
            }

            // 同期モード
            if (IS_CYCLIC_WEIGH_SYNC_ACCESS_MODE.equals(paramName)) {
                isCyclicWeighSyncAccessMode = sc.getInitParameter(paramName);
                if (isCyclicWeighSyncAccessMode != null && !isCyclicWeighSyncAccessMode.isEmpty()) {
                    this.gnomesSystemBean
                            .setIsCyclicWeighSyncAccessMode(Integer.parseInt(isCyclicWeighSyncAccessMode));
                }
            }
        }

        // 帳票印刷情報
        try {

            //帳票で使用するDBのパスワードを取得
            //2020/02/05以降、 WEB.xmlから取得を廃止しKeyStoreから取得するようにした
            reportDBPassword = keyStoreUtilities.getSecretKeyValue(REPORT_DB_PASSWORD);


            if (!(StringUtil.isNullOrEmpty(reportDefinitionXMLFileName)
                    || StringUtil.isNullOrEmpty(reportDBServerName)
                    || StringUtil.isNullOrEmpty(reportDBPortNo)
                    || StringUtil.isNullOrEmpty(reportDBUserName)
                    || StringUtil.isNullOrEmpty(reportDBPassword)
                    || StringUtil.isNullOrEmpty(reportDBName)
                    || StringUtil.isNullOrEmpty(reportDBNameOfStorage)
                    || StringUtil.isNullOrEmpty(reportDBType))) {

                //-------------------------------------------------------------------------
                //  通常領域用
                //-------------------------------------------------------------------------
                this.gnomesSystemBean.setcGenReportMeta(new CGenReportMeta());
                this.gnomesSystemBean.getcGenReportMeta().setConnectionString(
                        reportDBServerName, reportDBPortNo, reportDBName,
                        reportDBUserName, reportDBPassword, Integer.valueOf(reportDBType));

                this.gnomesSystemBean.getcGenReportMeta().setXMLSetting(reportDefinitionXMLFileName);

                // 多重
                this.gnomesSystemBean.setcGenReportMultipleMeta(new biz.grandsight.ex.rs_multiple.CGenReportMeta());
                this.gnomesSystemBean.getcGenReportMultipleMeta().setConnectionString(
                        reportDBServerName, reportDBPortNo, reportDBName,
                        reportDBUserName, reportDBPassword, Integer.valueOf(reportDBType));

                this.gnomesSystemBean.getcGenReportMultipleMeta().setXMLSetting(reportDefinitionXMLFileName);

                // 多重(棚卸一覧)
                this.gnomesSystemBean.setcGenReportMultipleInventoryMeta(new biz.grandsight.ex.rs_multiple21.CGenReportMeta());
                this.gnomesSystemBean.getcGenReportMultipleInventoryMeta().setConnectionString(
                        reportDBServerName, reportDBPortNo, reportDBName,
                        reportDBUserName, reportDBPassword, Integer.valueOf(reportDBType));

                this.gnomesSystemBean.getcGenReportMultipleInventoryMeta().setXMLSetting(reportDefinitionXMLFileName);

                // 多段
                this.gnomesSystemBean.setcGenReportMultiStageMeta(new biz.grandsight.ex.rs_multistage.CGenReportMeta());
                this.gnomesSystemBean.getcGenReportMultiStageMeta().setConnectionString(
                        reportDBServerName, reportDBPortNo, reportDBName,
                        reportDBUserName, reportDBPassword, Integer.valueOf(reportDBType));

                this.gnomesSystemBean.getcGenReportMultiStageMeta().setXMLSetting(reportDefinitionXMLFileName);

                // 多段（改ページなし）
                this.gnomesSystemBean.setcGenReportMultiStageNoNewPageMeta(new biz.grandsight.ex.rs_multistage41.CGenReportMeta());
                this.gnomesSystemBean.getcGenReportMultiStageNoNewPageMeta().setConnectionString(
                        reportDBServerName, reportDBPortNo, reportDBName,
                        reportDBUserName, reportDBPassword, Integer.valueOf(reportDBType));

                this.gnomesSystemBean.getcGenReportMultiStageNoNewPageMeta().setXMLSetting(reportDefinitionXMLFileName);

                // 多重多段
                this.gnomesSystemBean.setcGenReportMultipleMultiStageMeta(new biz.grandsight.ex.rs_multiplemultistage.CGenReportMeta());
                this.gnomesSystemBean.getcGenReportMultipleMultiStageMeta().setConnectionString(
                        reportDBServerName, reportDBPortNo, reportDBName,
                        reportDBUserName, reportDBPassword, Integer.valueOf(reportDBType));

                this.gnomesSystemBean.getcGenReportMultipleMultiStageMeta().setXMLSetting(reportDefinitionXMLFileName);

                this.gnomesSystemBean.getcGenReportMeta().setConnectionString(
                        reportDBServerName, reportDBPortNo, reportDBName,
                        reportDBUserName, reportDBPassword, Integer.valueOf(reportDBType));

                this.gnomesSystemBean.getcGenReportMeta().setXMLSetting(reportDefinitionXMLFileName);

                //-------------------------------------------------------------------------
                //  保管領域用
                //-------------------------------------------------------------------------

                this.gnomesSystemBean.setcGenReportMetaStorage(new CGenReportMeta());
                this.gnomesSystemBean.getcGenReportMetaStorage().setConnectionString(
                        reportDBServerName, reportDBPortNo, reportDBNameOfStorage,
                        reportDBUserName, reportDBPassword, Integer.valueOf(reportDBType));

                this.gnomesSystemBean.getcGenReportMetaStorage().setXMLSetting(reportDefinitionXMLFileName);

                // 多重
                this.gnomesSystemBean.setcGenReportMultipleMetaStorage(new biz.grandsight.ex.rs_multiple.CGenReportMeta());
                this.gnomesSystemBean.getcGenReportMultipleMetaStorage().setConnectionString(
                        reportDBServerName, reportDBPortNo, reportDBNameOfStorage,
                        reportDBUserName, reportDBPassword, Integer.valueOf(reportDBType));

                this.gnomesSystemBean.getcGenReportMultipleMetaStorage().setXMLSetting(reportDefinitionXMLFileName);

                // 多重(棚卸一覧)
                this.gnomesSystemBean.setcGenReportMultipleInventoryMetaStorage(new biz.grandsight.ex.rs_multiple21.CGenReportMeta());
                this.gnomesSystemBean.getcGenReportMultipleInventoryMetaStorage().setConnectionString(
                        reportDBServerName, reportDBPortNo, reportDBNameOfStorage,
                        reportDBUserName, reportDBPassword, Integer.valueOf(reportDBType));

                this.gnomesSystemBean.getcGenReportMultipleInventoryMetaStorage().setXMLSetting(reportDefinitionXMLFileName);

                // 多段
                this.gnomesSystemBean.setcGenReportMultiStageMetaStorage(new biz.grandsight.ex.rs_multistage.CGenReportMeta());
                this.gnomesSystemBean.getcGenReportMultiStageMetaStorage().setConnectionString(
                        reportDBServerName, reportDBPortNo, reportDBNameOfStorage,
                        reportDBUserName, reportDBPassword, Integer.valueOf(reportDBType));

                this.gnomesSystemBean.getcGenReportMultiStageMetaStorage().setXMLSetting(reportDefinitionXMLFileName);

                // 多段（改ページなし）
                this.gnomesSystemBean.setcGenReportMultiStageNoNewPageMetaStorage(new biz.grandsight.ex.rs_multistage41.CGenReportMeta());
                this.gnomesSystemBean.getcGenReportMultiStageNoNewPageMetaStorage().setConnectionString(
                        reportDBServerName, reportDBPortNo, reportDBNameOfStorage,
                        reportDBUserName, reportDBPassword, Integer.valueOf(reportDBType));

                this.gnomesSystemBean.getcGenReportMultiStageNoNewPageMetaStorage().setXMLSetting(reportDefinitionXMLFileName);

                // 多重多段
                this.gnomesSystemBean.setcGenReportMultipleMultiStageMetaStorage(new biz.grandsight.ex.rs_multiplemultistage.CGenReportMeta());
                this.gnomesSystemBean.getcGenReportMultipleMultiStageMetaStorage().setConnectionString(
                        reportDBServerName, reportDBPortNo, reportDBNameOfStorage,
                        reportDBUserName, reportDBPassword, Integer.valueOf(reportDBType));

                this.gnomesSystemBean.getcGenReportMultipleMultiStageMetaStorage().setXMLSetting(reportDefinitionXMLFileName);



            }

            if(!(StringUtil.isNullOrEmpty(conversionTimeOut))){
                this.gnomesSystemBean.setConversionTimeOut(Long.parseLong(conversionTimeOut));
            }

        } catch (GnomesAppException e) {
            System.out.println(e);
        } catch (CReportGenException e) {
            System.out.println(e);
        } catch (NumberFormatException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            // 全マスタ定義情報読込み
            this.gnomesSystemBean.getGnomesSystemModel().readAllMstrEntity();
            // 初期化処理
            this.gnomesSystemBean.getGnomesSystemModel().initializeProc();
        } catch (GnomesAppException e) {
            System.out.println(e);
        }

        //ファイルリスト作成
        String[] files = getDictFileList(sc);


        //カスタムタグファイルを全て読み込み
        for (int i = 0; i < files.length; i++) {
            String file = files[i];

            // カスタムタグ読み込み
            gnomesCTagDictionary.readDict(sc.getRealPath(TAG_DICT_XML_PATH + file));
            //権限情報読み込み
            gnomesSystemBean.addPartsPrivilegeInfo(
                    PartsPrivilegeInfoRead.readXml(sc.getRealPath(TAG_DICT_XML_PATH + file)));

        }

        /*
         * standaloneから取得したModuleOptionを設定する。
         */
        gnomesSystemBean.setModuleOptionInfo(getModuleOption());

        try {
            for(MstrSystemDefine dataMstrSystemConstant: gnomesSystemBean.getGnomesSystemModel().getMstrSystemDefineList()){
                // A101:システム定義.ログイン認証用モジュールの種類（JDBC, LDAP)を設定
                if(dataMstrSystemConstant.getChar1() != null &&
                        !dataMstrSystemConstant.getChar1().equals("")){
                    if(dataMstrSystemConstant.getChar1().equals(CommonConstants.LDAP) ||
                            dataMstrSystemConstant.getChar1().equals(CommonConstants.JDBC)){
                        gnomesSystemBean.setLoginModuleType(dataMstrSystemConstant.getChar1());
                        break;
                    }
                }
            }
        } catch (GnomesAppException e) {
            System.out.println(e);
        }

        /*
         * 共通コマンドを読込
         */
        gnomesSystemBean.setCommandDatas(getCommandDatas(sc));

        /*
         * 入力項目ドメイン定義を読込
         */
        gnomesSystemBean.setGnomesInputDomain(getGnomesInputDomain(sc));
    }

    /**
     *  カスタムタグ定義ファイルのリストを取得する。
     *
     * @param Entity クラス名
     */
    protected String[] getDictFileList(ServletContext sc){
        /* フィルタ作成 */
        FilenameFilter filter = new FilenameFilter() {
            /* ここに条件を書く。trueの場合、そのファイルは選択される */
            public boolean accept(File dir, String name) {
                if (name.startsWith(TAG_DICT_FILE_HEAD) && name.endsWith(TAG_DICT_FILE_TAIL)) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        File dir = new File(sc.getRealPath(TAG_DICT_XML_PATH));
        String[] files = dir.list(filter);

        return files;

    }

    /**
     * 共通コマンド読込
     * @param path 共通コマンドxmlパス
     * @return 共通コマンド
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    private CommandDatas getCommandDatas(ServletContext sc) {

        List<CommandData> commandDataList = new ArrayList<>();
        List<CommandScreenData> screenDataList = new ArrayList<>();


        for (String fileFilter : COMMAND_FILTERS) {
            /* フィルタ作成 */
            FilenameFilter filter = new FilenameFilter() {
                /* ここに条件を書く。trueの場合、そのファイルは選択される */
                public boolean accept(File dir, String name) {
                    if (name.startsWith(fileFilter)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            };

            System.out.println("command read file filter:" + fileFilter);

            File dir = new File(sc.getRealPath(COMMON_COMMAND_PATH));
            String[] files = dir.list(filter);
            if (files == null) {
                continue;
            }
            for (String filename : files) {

                System.out.println("command read file:" + filename);

                File file = new File(sc.getRealPath(COMMON_COMMAND_PATH + filename));
                CommandDatas datas = JAXB.unmarshal(file, CommandDatas.class);

                // 共通コマンド
                for (CommandData data : datas.getCommandDataList()) {

                    List<CommandData> lstRemoveData = commandDataList.stream()
                            .filter(item -> item.getCommandId().equals(data.getCommandId()))
                            .collect(Collectors.toList());

                    // 既存は削除
                    commandDataList.removeAll(lstRemoveData);

                    commandDataList.add(data);
                }

                // 共通画面
                for (CommandScreenData data : datas.getScreenDataList()) {

                    List<CommandScreenData> lstRemoveData = screenDataList.stream()
                            .filter(item -> item.getScreenId().equals(data.getScreenId()))
                            .collect(Collectors.toList());

                    // 既存は削除
                    screenDataList.removeAll(lstRemoveData);

                    screenDataList.add(data);
                }
            }
        }

        try {
            // リソース展開 コマンド定義
            for (CommandData data : commandDataList) {
                // コマンドID
                String commandId;
                commandId = getConstants(COMMAND_ID_CONSTANTS_CLASSNAME_LIST, data.getCommandId());
                data.setCommandId(commandId);

                // 画面ID
                if (data.getScreenId() != null) {
                    String screenId =  getConstants(SCREEN_ID_CONSTANTS_CLASSNAME_LIST, data.getScreenId());
                    data.setScreenId(screenId);
                }

                // 検証項目
                if (data.getCheckList() != null) {
                    List<String> resIds = new ArrayList<>();
                    for (String res : data.getCheckList()) {
                        if (res.contains(CommonConstants.COMMAND_INPUT_CHECK_LIST_FIELDNAME_SEPARATOR)) {
                            // Listのフィールド名を指すのでそのまま
                            resIds.add(res);
                        } else {
                            resIds.add(getGnomesResourcesConstants(res));
                        }
                    }
                    data.setCheckList(resIds);
                }

                // フォワード先コマンドID
                if (data.getForwardCommandId() != null) {
                    data.setForwardCommandId(
                            getConstants(COMMAND_ID_CONSTANTS_CLASSNAME_LIST, data.getForwardCommandId()));
                }

            }

            // リソース展開 画面ID定義 画面情報
            for (CommandScreenData data : screenDataList) {
                String screenId = getConstants(SCREEN_ID_CONSTANTS_CLASSNAME_LIST, data.getScreenId());
                data.setScreenId(screenId);
            }

        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();

            throw new RuntimeException(e);
        }

        CommandDatas commandDatas = new CommandDatas();
        commandDatas.setCommandDataList(commandDataList);
        commandDatas.setScreenDataList(screenDataList);

        return commandDatas;
    }

    /**
     * 入力項目ドメイン定義読込
     * @param sc サーブレット
     * @return 入力項目ドメイン定義
     */
    private GnomesInputDomain getGnomesInputDomain(ServletContext sc) {

        // 入力項目ドメイン定義ファイル読込
        File file = new File(sc.getRealPath(DOMAIN_XML_PATH + DOMAIN_CONSTANTS_FILE_NAME));
        GnomesInputDomain gnomesInputDomain = JAXB.unmarshal(file, GnomesInputDomain.class);

        return gnomesInputDomain;
    }

    /**
     * リソース定数取得
     * @param resourceKey
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public String getGnomesResourcesConstants(String resourceKey)
            throws IllegalArgumentException, IllegalAccessException {

        String result = null;

        for (int i = 0; i < RESOURCE_CONSTANTS_CLASSNAME_LIST.length; i++) {

            String className = RESOURCE_CONSTANTS_CLASS_PACKAGE_NAME
                    + RESOURCE_CONSTANTS_CLASSNAME_LIST[i];

            try {
                Class<?> clazz = Class.forName(className);

                return getConstants(clazz, resourceKey);

            } catch (ClassNotFoundException e) {
                // 定数クラスが存在しない場合、次の定数クラスを読み込む
            } catch (NoSuchFieldException | SecurityException e) {
                // 対象のフィールドが存在しない場合、次の定数クラスを読み込む
            }

        }

        return result;
    }

    /**
     * コンスタント値取得
     * @param constantClass 対象クラス
     * @param fieldName 対象フィールド名
     * @return コンスタント値
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public String getConstants(Class<?> constantClass, String fieldName)
            throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        String result = null;

        Field targetField = constantClass.getDeclaredField(fieldName);
        result = (String) targetField.get(null);

        return result;
    }

    /**
     * コンスタント値取得
     * @param constantsClassNameList コンスタントクラス名リスト
     * @param commandId 対象コマンドID
     * @return コマンドID コンスタント値
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public String getConstants(String[] constantsClassNameList, String commandId)
            throws SecurityException, IllegalArgumentException, IllegalAccessException {

        for (int i = 0; i < constantsClassNameList.length; i++) {

            String className = COMMON_CONSTANTS_CLASS_PACKAGE_NAME
                    + constantsClassNameList[i];

            try {
                Class<?> clazz = Class.forName(className);

                return getConstants(clazz, commandId);

            } catch (ClassNotFoundException e) {
                // 定数クラスが存在しない場合、次の定数クラスを読み込む
            } catch (NoSuchFieldException e) {
                // 対象のフィールドが存在しない場合、次の定数クラスを読み込む
            }

        }

        return null;

    }

    /**
     * standaloneからModuleOptionを取得する。
     * @return
     */
    public Hashtable<String, String> getModuleOption(){

        Hashtable<String, String> env = new Hashtable<String, String>();
        String OTHER = "other";

        Configuration config = java.security.AccessController.doPrivileged
            (new java.security.PrivilegedAction<Configuration>() {
            public Configuration run() {
                return Configuration.getConfiguration();
            }
        });

        String name = BLSecurityConstants.GNOMES_WEB_POLICY;

        // get the LoginModules configured for this application
        AppConfigurationEntry[] entries = config.getAppConfigurationEntry(name);
        if (entries == null) {
            entries = config.getAppConfigurationEntry(OTHER);
            if (entries == null) {
                System.out.println("(form.format(source));");
            }
        }

        if (entries != null) {
            for (int i = 0; i < entries.length; i++) {

                // 接続先URL
                Object providerURL = entries[i].getOptions().get(BLSecurityConstants.PROVIDER_URL);
                if(providerURL != null){
                    env.put(BLSecurityConstants.PROVIDER_URL, providerURL.toString());
                }

                // ドメイン名接尾部
                Object principalDNSuffix =  entries[i].getOptions().get(BLSecurityConstants.PRINCIPAL_DN_SUFFIX);
                if(principalDNSuffix != null){
                    env.put(BLSecurityConstants.PRINCIPAL_DN_SUFFIX, principalDNSuffix.toString());
                }

            }
        }

        return env;
    }

}