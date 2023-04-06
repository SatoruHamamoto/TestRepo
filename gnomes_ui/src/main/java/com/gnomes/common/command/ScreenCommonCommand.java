package com.gnomes.common.command;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.annotation.Priority;
import javax.enterprise.context.BusyConversationException;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums.DoingClassMethodDiv;
import com.gnomes.common.constants.CommonEnums.TableSearchSettingType;
import com.gnomes.common.constants.SystemDefConstants;
import com.gnomes.common.data.BaseFunctionBean;
import com.gnomes.common.data.CommandData;
import com.gnomes.common.data.CommandScreenData;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.GnomesTransactional;
import com.gnomes.common.interceptor.PopupMessageHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.search.SearchInfoController.ConditionDateType;
import com.gnomes.common.search.SearchInfoController.ConditionParamSaveType;
import com.gnomes.common.search.SearchInfoController.ConditionType;
import com.gnomes.common.search.data.ConditionInfo;
import com.gnomes.common.search.data.MstCondition;
import com.gnomes.common.search.data.MstSearchInfo;
import com.gnomes.common.search.data.SearchSetting;
import com.gnomes.common.search.data.SearchSetting.DispType;
import com.gnomes.common.search.query.SearchCondition;
import com.gnomes.common.tags.GnomesCTagDictionary;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.GnomesDateUtil;
import com.gnomes.common.view.BaseFormBean;
import com.gnomes.common.view.IFormBean;
import com.gnomes.common.view.IScreenFormBean;
import com.gnomes.system.dao.BookmarkDao;
import com.gnomes.system.dao.MstrScreenTableTagDao;
import com.gnomes.system.dao.TableSearchSettingDao;
import com.gnomes.system.entity.Bookmark;
import com.gnomes.system.entity.MstrScreenTableTag;
import com.gnomes.system.entity.MstrSystemDefine;
import com.gnomes.system.entity.TableSearchSetting;
import com.gnomes.uiservice.ContainerRequest;

/**
*
* 画面共通コマンド
* <!-- TYPE DESCRIPTION --><pre>
* </pre>
*/
/* ========================== MODIFICATION HISTORY ==========================
* Release  Date       ID/Name                   Comment
* --------------------------------------------------------------------------
* R0.01.01 2018/01/19 YJP/K.Fujiwara            初版
* R0.01.02 2018/08/29 YJP/S.Hamamoto			エラーログの補足を追加
* R0.01.03 2019/05/29 YJP/S.Hamamoto            検索ソート構造変更によるアクセス手段変更
* [END OF MODIFICATION HISTORY]
* ==========================================================================
*/
@RequestScoped
@Alternative
@Priority(CommonConstants.GNOMESINTERCEPTOR_PLATFORM)
@ICommandQualifier(CommonConstants.SCREEN_COMMON_COMMAND)
public class ScreenCommonCommand extends ScreenBaseCommand
{

    @Inject
    protected CommonCommand         commonCommand;

    protected BaseFormBean          baseFormBean;

    protected BaseFunctionBean      baseFunctionBean;

    protected CommandData           commandData;

    protected CommandScreenData     commandScreenData;

    // 初期処理で発生した例外
    protected GnomesAppException    postConstructException;

    @Inject
    protected BookmarkDao           bookmarkDao;

    @Inject
    protected MstrScreenTableTagDao mstrScreenTableTagDao;

    @Inject
    protected GnomesCTagDictionary  gnomesCTagDictionary;

    @Inject
    transient Logger                logger;

    @Inject
    ContainerRequest                requestContext;

    @Inject
    protected GnomesSessionBean     gnomesSessionBean;

    @Inject
    protected ContainerRequest      containerRequest;

    /** 新規windowsオープン */
    @Inject
    @RequestParamMap
    private String                  gnomesNewWindowOpen;

    /** 画面遷移タイプ 0:フォワード  */
    public static final String      TRANSITION_TYPE_FORWARD       = "0";

    /** 画面遷移タイプ 1:リダイレクト */
    public static final String      TRANSITION_TYPE_REDIRECT      = "1";

    /** 画面遷移タイプ 2:FunctionBeanより決定 */
    private static final String     TRANSITION_TYPE_FROM_FUNCTION = "2";

    /** テーブルタグIDの区切り文字 */
    private static final String     TABLE_TAG_ID_SEPARATOR        = ".";

    /** テーブルタグIDの区切り文字 */
    private static final String     TABLE_TAG_ID_SEPARATOR_REGEX  = "\\.";

    /**
     * デフォルト・コンストラクタ
     */
    public ScreenCommonCommand()
    {
    }

    /** 初期処理 */
    //    @PostConstruct
    @Override
    public void setupCommand()
    {
        //    	if (!FacesContext.getCurrentInstance().isPostback()) {
        //    	}
        try {
            commandData = commonCommand.getCommandData(requestContext.getCommandId());

            if (commandData == null) {
                // jspTest時のみ
                return;
            }

            // 共通画面情報リストより１．で取得した共通コマンド情報.画面IDに該当する、共通画面情報を取得する。
            commandScreenData = commonCommand.getCommandScreenData(commandData.getScreenId());
            if (commandScreenData == null) {
                // 該当なしの場合、例外
                // (ME01.0151) 共通画面情報に存在しません。対象画面ID：{0}
                throw new GnomesAppException(null, GnomesMessagesConstants.ME01_0151, commandData.getScreenId());
            }

            String formBeanName = commandScreenData.getCommonFormBeanName();
            String functionBeanName = commandScreenData.getCommonFunctionBeanName();

            // 共通FormBeanを取得する。
            baseFormBean = commonCommand.getCDIInstance(formBeanName);

            if (commandData.isEditFlag()) {
                baseFormBean.setInputChangeFlag("0");
            }
            // 新規Windowオープン時 */
            if (!StringUtil.isNullOrEmpty(gnomesNewWindowOpen)) {
                // windowIdを作成
                String windowId = this.makeWindowId();
                baseFormBean.setWindowId(windowId);
            }

            // 共通画面情報.FunctionBeanクラス名が未設定でない場合
            if (functionBeanName != null) {
                try {
                    // 共通FunctionBeanを取得
                    baseFunctionBean = commonCommand.getCDIInstance(functionBeanName);
                    // 遷移タイプなし（遷移しない）に設定
                    baseFunctionBean.setScreenTransitionType(null);

                }
                catch (BusyConversationException e) {
                    //ただ今処理中につき、OKボタンを押してから再度操作してください。
                    requestContext.setCommandValidErrorInfo(MessagesHandler.getString(
                            GnomesMessagesConstants.MV01_0039));
                }
            }

        }
        catch (GnomesAppException e) {
            if (commandData != null) {
                logHelper.severe(this.logger, null, null,
                        "Error Info: " + " commandId = " + commandData.getCommandId() + " ScreenId = " + commandData.getScreenId() + " DefaultForward = " + commandData.getDefaultForward());
            }
            postConstructException = e;
        }
    }

    /**
     * 後処理
     */
    @PreDestroy
    private void destroy()
    {
        this.logHelper.fine(this.logger, null, null, "@PreDestroy: " + this);
    }

    /**
     * トランザクション管理有無
     * @return
     */
    @Override
    public boolean isTransactional()
    {
        return commandData.isTransactional();
    }

    /**
     * 共通メイン処理（トランザクションあり）
     */
    @TraceMonitor
    @ErrorHandling
    @PopupMessageHandling
    @Override
    @GnomesTransactional
    public void mainExecute() throws Exception
    {
        this.mainFunction();
    }

    /**
     * 共通メイン処理（トランザクションなし）
     */
    @TraceMonitor
    @ErrorHandling
    @PopupMessageHandling
    @Override
    public void mainExecuteNoTransactional() throws Exception
    {
        this.mainFunction();
    }

    /**
     * 共通メイン処理
     * @throws Exception
     */
    @TraceMonitor
    @ErrorHandling
    public void mainFunction() throws Exception
    {

        // FunctionBeanの初期化
        // 定義情報より、FunctinBeanのbegin実行判定
        doFunctionBeanBegin();

        // 一覧情報の初期化
        // 初期化情報が定義情報にある場合に、処理を行う
        doInit();

        // FormBean(入力情報)をFunctionBeanに保存
        setFunctionBeanFromForm();

        // モデル実行（クラス名.メソッド名）
        doModel();

        // ビジネスロジック実行（クラス名.メソッド名）
        doBusinessLogic();

        // 画面表示データ取得実行
        doGetDispData();

        // 取得した画面表示データをFormBeanにマッピング
        setFormBeanFromFunction();

        // 画面遷移
        doScreenTransition();
    }

    /**
     * FunctionBean開始処理
     * @throws GnomesAppException
     */
    public void doFunctionBeanBegin() throws GnomesAppException
    {

        // 定義情報より、FunctinBeanのbegin実行判定
        if (commandData.isFunctionBeanBegin().equals("true")) {
            this.beginFunctionBean(baseFormBean, baseFunctionBean);
        }
        else if (commandData.isFunctionBeanBegin().equals("clear")) {
            baseFunctionBean.clear();
        }
    }

    /**
     * 初期設定
     * @throws Exception
     */
    public void doInit() throws Exception
    {

        String screenId = requestContext.getScreenId();
        if (commandData.isInitCommandFlag()) {

            gnomesSessionBean.pushScreeenStack(baseFormBean.getWindowId(), screenId);

            List<String> bookmarked = gnomesSessionBean.getBookMarkedScreenIdList();

            Integer regionType = Integer.parseInt(gnomesSessionBean.getRegionType());

            // 自画面を削除
            bookmarked.remove(screenId);
            
            List<Bookmark> bookmarks = bookmarkDao.getBookmarks(requestContext.getUserId(), screenId, regionType);
            
            // ブックマーク有無判定
            if (bookmarks != null) {
            	// ブックマーク有無判定(画面遷移モードの値突合せ)
            	for (Bookmark marks : bookmarks) {
            		if (marks.getbookmark_parameter().equals(baseFormBean.getScreenTransitionMode())) {
                        bookmarked.add(screenId);
            		}
                }
            }
            gnomesSessionBean.setBookMarkedScreenIdList(bookmarked);

            doInitTable();
        }
        else {
            gnomesSessionBean.returnScreeenStack(baseFormBean.getWindowId(), screenId);
        }
    }

    @Inject
    SearchCondition searchCondition;

    /**
     * Validate処理実行
     * @throws Exception
     */
    @Override
    public boolean commonValidate(IScreenFormBean formBean)
    {

        // 共通入力チェック
        if (super.commonValidate(formBean) == true) {
            return true;
        }

        // 検索必須チェック
        if (commandData.getTableIdList() != null) {
            // 対象テーブル
            for (String tableId : commandData.getTableIdList()) {
                // 検索ダイアログの入力チェック
                Map<String, String[]> requestParamErr = searchCondition.checkInput(tableId,
                        baseFunctionBean.getMstSearchInfoMap().get(tableId), baseFormBean.getSearchSettingMap().get(
                                tableId));
                // エラー情報の設定
                requestErr.putAll(requestParamErr);

                // 検索ダイアログ・ソートダイアログで入力エラーの場合
                if (!requestParamErr.isEmpty()) {
                    // formBeanの検索ソート条件はfunctionBeanより再生する
                    // カスタムタグの検索ダイアログ、ソートダイアログは
                    // 入力エラーの場合、リクエスト情報（入力エラーのまま）よりダイアログを作成する
                    baseFormBean.getSearchSettingMap().replace(tableId, baseFunctionBean.getSearchSettingMap().get(
                            tableId));
                }

            }

            if (requestErr.entrySet().size() > 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * 検索条件とテーブル表示設定の初期化
     * @throws Exception
     */
    @TraceMonitor
    @ErrorHandling
    public void doInitTable() throws Exception
    {

        /**
         * 検索条件マスター情報マップ（レスポンスのみ）
         *      key:    辞書ID
         *      value:  MstSearchInfo
         */
        Map<String, MstSearchInfo> mstSearchInfoMap = new HashMap<String, MstSearchInfo>();

        /**
         * リクエストとレスポンス 検索条件マップ
         *      key:    辞書ID
         *      value:  SearchSetting
         */
        Map<String, SearchSetting> searchSettingMap = new HashMap<String, SearchSetting>();

        /*
         * 自画面のテーブルカスタムを取得する
         */

        // オリジナル（定義上）の画面ID
        String screenId = requestContext.getOrgScreenId();
        // オリジナル（定義上）の画面IDがない場合
        if (StringUtil.isNullOrEmpty(screenId)) {
            // 表示上の画面IDを使う
            screenId = requestContext.getScreenId();
        }

        // 辞書からはオリジナルの画面IDから取得
        Map<String, Boolean> tableMap = gnomesCTagDictionary.getTableId(screenId);

        for (Map.Entry<String, Boolean> e : tableMap.entrySet()) {

            String tableId = e.getKey();
            DispType dispType = DispType.DispType_List;
            if (e.getValue() == true) {
                dispType = DispType.DispType_Paging;
            }

            // テーブル情報の初期設定
            initTableInfo(dispType, tableId, requestContext.getScreenId(), mstSearchInfoMap, searchSettingMap);
        }

        if (baseFunctionBean != null) {
            baseFunctionBean.getMstSearchInfoMap().putAll(mstSearchInfoMap);
            baseFunctionBean.getSearchSettingMap().putAll(searchSettingMap);
        }
        baseFormBean.setMstSearchInfoMap(mstSearchInfoMap);
        baseFormBean.setSearchSettingMap(searchSettingMap);

    }

    /**
     * FunctionBean設定（FormBean→FunctionBean)
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void setFunctionBeanFromForm() throws GnomesAppException
    {

        /* 検索条件をFunctionBeanに渡すのは、コマンドごとにマッピングで行うことになったので、不要
        // 検索条件を設定
        if (baseFunctionBean != null
                && baseFormBean.getSearchSettingMap() != null) {

            // 送信された検索条件のみをFunctionBeanに設定
            for (Map.Entry<String, SearchSetting> e : baseFormBean
                    .getSearchSettingMap().entrySet()) {

                SearchSetting setting = baseFunctionBean.getSearchSettingMap().get(e.getKey());

                // 検索情報入れ替え
                setting.setConditionInfos(e.getValue().getConditionInfos());
                if (e.getValue().getOrderingInfos() != null && e.getValue().getOrderingInfos().size() > 0) {
                    // 表示情報入れ替え
                    setting.setFixedColNum(e.getValue().getFixedColNum());
                    setting.setOrderingInfos(e.getValue().getOrderingInfos());
                }
                baseFunctionBean.getSearchSettingMap().replace(e.getKey(), setting);

            }
        }
        */

        // 検索情報(FormBean(入力情報)をFunctionBeanに保存)
        if (commandData.getTableIdList() != null) {
            for (String tableId : commandData.getTableIdList()) {

                // 1ページ表示件数
                baseFunctionBean.getSearchSettingMap().get(tableId).setOnePageDispCount(
                        baseFormBean.getSearchSettingMap().get(tableId).getOnePageDispCount());

                baseFunctionBean.getSearchSettingMap().get(tableId).setFixedColNum(
                        baseFormBean.getSearchSettingMap().get(tableId).getFixedColNum());

                baseFunctionBean.getSearchSettingMap().get(tableId).setConditionInfos(
                        baseFormBean.getSearchSettingMap().get(tableId).getConditionInfos());

                baseFunctionBean.getSearchSettingMap().get(tableId).setOrderingInfos(
                        baseFormBean.getSearchSettingMap().get(tableId).getOrderingInfos());

                baseFunctionBean.getSearchSettingMap().get(tableId).setPagingFlag(
                        baseFormBean.getSearchSettingMap().get(tableId).getPagingFlag());
            }
        }

        baseFunctionBean.setInputChangeFlag(baseFormBean.getInputChangeFlag());

        // 入力項目(FormBean(入力情報)をFunctionBeanに保存)

        Map<String, String> map = commandData.getMappingFunctionBeanFromForm();
        if (map != null) {
            commonCommand.mappingBean(baseFunctionBean, baseFormBean, map);
        }

    }

    /**
     * モデル実行
     * @throws GnomesAppException
     */
    public void doModel() throws GnomesAppException
    {
        if (commandData.getDoModelClassMethod() != null) {
            commonCommand.doClassMethod(commandData.getDoModelClassMethod(), DoingClassMethodDiv.MODEL,
                    containerRequest);
        }
    }

    /**
     * ビジネスロジック実行
     * @throws GnomesAppException
     */
    public void doBusinessLogic() throws GnomesAppException
    {
        if (commandData.getDoBusinessClassMethod() != null) {

            Object businessBean = null;

            // BusinessBean作成
            if (commandData.getBusinessBeanName() != null) {
                businessBean = commonCommand.getCDIInstance(commandData.getBusinessBeanName());
            }

            // BusinessBeanマッピング
            if (businessBean != null && commandData.getMappingBusinessBeanFromFunction() != null) {
                commonCommand.mappingBean(businessBean, baseFunctionBean,
                        commandData.getMappingBusinessBeanFromFunction());
            }

            // ビジネスロジック実行
            commonCommand.doClassMethod(commandData.getDoBusinessClassMethod(), DoingClassMethodDiv.BUSINESS_LOGIC,
                    containerRequest);

            // FunctionBeanマッピング
            if (businessBean != null && commandData.getMappingFunctionBeanFromBusiness() != null) {
                commonCommand.mappingBean(baseFunctionBean, businessBean,
                        commandData.getMappingFunctionBeanFromBusiness());
            }
        }
    }

    /**
     * 表示データ取得
     * @throws GnomesAppException
     */
    public void doGetDispData() throws GnomesAppException
    {
        if (commandData.getDoGetDispDataClassMethod() != null) {
            commonCommand.doClassMethod(commandData.getDoGetDispDataClassMethod(), DoingClassMethodDiv.GET_DISP_DATA,
                    containerRequest);
        }
    }

    /**
     * FormBean設定（FunctionBean→FormBean）
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void setFormBeanFromFunction() throws GnomesAppException
    {

        /* コマンドごとに、マッピングを行うので不要となった
        if (baseFunctionBean != null) {
            // ロジックで検索条件を設定することがあるので
            // 自画面の情報のみ
            Map<String, SearchSetting> mapPageSearchSetting = new HashMap<>();
            String screenId = requestContext.getScreenId();
            String prefix = screenId.concat(TABLE_TAG_ID_SEPARATOR);
            int sepCnt = screenId.split(TABLE_TAG_ID_SEPARATOR_REGEX).length + 1;

            for(Entry<String, SearchSetting> e : baseFunctionBean.getSearchSettingMap().entrySet()) {
                if (e.getKey().startsWith(prefix) &&
                        sepCnt == e.getKey().split(TABLE_TAG_ID_SEPARATOR_REGEX).length) {

                    mapPageSearchSetting.put(e.getKey(), e.getValue());
                }
            }
            baseFormBean.setSearchSettingMap(mapPageSearchSetting);
        }
        */

        // データ取得に伴い変更があるものは常にFormBeanに設定する
        if (baseFunctionBean != null) {
            // 自画面の情報のみ
            // オリジナル（定義上）の画面ID
            String screenId = requestContext.getOrgScreenId();
            // オリジナル（定義上）の画面IDがない場合
            if (StringUtil.isNullOrEmpty(screenId)) {
                // 表示上の画面IDを使う
                screenId = requestContext.getScreenId();
            }

            String prefix = screenId.concat(TABLE_TAG_ID_SEPARATOR);
            int sepCnt = screenId.split(TABLE_TAG_ID_SEPARATOR_REGEX).length + 1;

            for (Entry<String, SearchSetting> e : baseFunctionBean.getSearchSettingMap().entrySet()) {
                if (e.getKey().startsWith(prefix) && sepCnt == e.getKey().split(TABLE_TAG_ID_SEPARATOR_REGEX).length) {
                    // 総件数
                    baseFormBean.getSearchSettingMap().get(e.getKey()).setAllDataCount(e.getValue().getAllDataCount());
                    // 現在のページ
                    baseFormBean.getSearchSettingMap().get(e.getKey()).setNowPage(e.getValue().getNowPage());
                    // 1ページ表示件数
                    baseFormBean.getSearchSettingMap().get(e.getKey()).setOnePageDispCount(
                            e.getValue().getOnePageDispCount());
                    // 横固定位置
                    baseFormBean.getSearchSettingMap().get(e.getKey()).setFixedColNum(e.getValue().getFixedColNum());
                }
            }

            // 検索情報(FounctionBean→FormBean)
            if (commandData.getTableIdList() != null) {
                for (String tableId : commandData.getTableIdList()) {
                    // 検索項目のチェックがないものは、削除する
                    searchInfoController.clearNoCheckCondition(baseFunctionBean.getSearchSettingMap().get(tableId));

                    baseFormBean.getSearchSettingMap().put(tableId, baseFunctionBean.getSearchSettingMap().get(
                            tableId));
                }
            }

			// 表示用のターブルヘッダーを設定
			baseFormBean.setTableDispHeaders(baseFunctionBean.getTableDispHeaders());

            // Hiddenまたはnoneにするカスタムタグの辞書IDを設定
            baseFormBean.setTagHiddenKindMap(baseFunctionBean.getTagHiddenKindMap());
        }

        baseFormBean.setInputChangeFlag(baseFunctionBean.getInputChangeFlag());

        // FounctionBean→FormBeanのマッピング情報
        Map<String, String> map = commandData.getMappingBeanFromFunction();
        if (map != null) {
            commonCommand.mappingBean(baseFormBean, baseFunctionBean, map);
        }
    }

    /**
     * FunctionBean終了処理
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void doFunctionBeanEnd() throws GnomesAppException
    {

        // 定義情報より、FunctinBeanのbegin実行判定
        if (commandData.isFunctionBeanEnd().equals("true")) {
            this.endFunctionBean(baseFormBean, baseFunctionBean);

            // 画面遷移方法がフォワードの場合は、functionBeanのclearメソッドを実行
            String type = commandData.getScreenTransitionType();
            if (type.equals(TRANSITION_TYPE_FORWARD)) {
                baseFunctionBean.clear();
            }
        }
        else if (commandData.isFunctionBeanEnd().equals("clear")) {
            baseFunctionBean.clear();
        }
    }

    /**
     * 画面遷移設定
     * @return 遷移有無
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public boolean doScreenTransition() throws GnomesAppException
    {

        // 画面遷移
        String type = commandData.getScreenTransitionType();
        if (type == null) {
            // 遷移なし
            return false;
        }

        // 遷移先のコマンドID
        String commandId = commandData.getForwardCommandId();

        // 引継パラメータマッピング情報
        Map<String, String> map = null;

        // 画面遷移タイプ 2:FunctionBeanより決定の場合
        if (type.equals(TRANSITION_TYPE_FROM_FUNCTION)) {

            type = baseFunctionBean.getScreenTransitionType();

            // FunctionBeanの画面遷移タイプがnullの場合、遷移なし
            if (type == null) {
                // 遷移なし
                return false;
            }

            // コマンドIDもFunctionBeanから取得
            commandId = baseFunctionBean.getForwardCommandId();

            // フォワードの場合、FormBean引継パラメータマッピング情報を取得
            if (type.equals(TRANSITION_TYPE_FORWARD)) {
                map = baseFunctionBean.getTransitionParameterMap();
            }
        }

        // リクエストパラメータ
        Map<String, Object> param = new HashMap<String, Object>();
        // 動的画面IDの設定
        setDynamicScreenInfoParam(param);

        // FunctionBean終了処理
        doFunctionBeanEnd();

        // SystemFormBean初期化処理
        //systemFormBean.clear();

        // フォワード
        if (type.equals(TRANSITION_TYPE_FORWARD)) {

            // FunctionBeanから引継パラメータマッピング情報を取得していない場合、
            // コマンド定義から引継パラメータマッピング情報を取得
            if (map == null || map.size() == 0) {
                map = commandData.getMappingTransitionParameter();
            }

            if (map != null && map.size() > 0) {

                // 遷移先のコマンドIDからコマンド情報を取得
                CommandData toCommandData = commonCommand.getCommandData(commandId);

                // 遷移先のコマンド情報から画面情報を取得
                CommandScreenData toCommandScreenData = commonCommand.getCommandScreenData(toCommandData.getScreenId());
                if (toCommandScreenData == null) {
                    // 該当なしの場合、例外
                    // (ME01.0151) 共通画面情報に存在しません。対象画面ID：{0}
                    throw new GnomesAppException(null, GnomesMessagesConstants.ME01_0151, toCommandData.getScreenId());
                }

                // 遷移先の共通FormBeanを取得する。
                BaseFormBean toBaseFormBean = commonCommand.getCDIInstance(toCommandScreenData.getCommonFormBeanName());

                // BaseFormBean,引継パラメータに動的画面IDを設定
                setDynamicScreenInfoFormBeanMapping(param, map);

                commonCommand.mappingBean(toBaseFormBean, baseFormBean, map);
            }

            responseContext.setForwardAttributeCommand(commandId, param);

        }
        else if (type.equals(TRANSITION_TYPE_REDIRECT)) {
            setCommonTransitionParam(param);
            responseContext.setRedirect(CommonConstants.REDIRECT_URL, commandId, param);
        }
        else {
            String cid = (String) commonCommand.getBeanProperty(baseFormBean, type);
            setCommonTransitionParam(param);
            responseContext.setRedirect(CommonConstants.REDIRECT_URL + "?cid=" + cid, commandId, param);
        }

        // 定義情報
        //		・コマンドID
        //		・引継パラメータマッピング
        //			キー：引継パラメータ名
        //			値：FormBeanのパラメータ名
        //		・遷移方法
        //			0:フォワード
        //			1:リダイレクト（cidなし）
        //			FormBeanのプロパティ名:リダイレクト（cidあり cidは設定されているFormBeanのプロパティ名より取得）

        return true;
    }

    /**
     * 動的画面情報のパラメータ設定
     * @param param
     */
    private void setDynamicScreenInfoParam(Map<String, Object> param)
    {

        if (baseFunctionBean != null && !StringUtil.isNullOrEmpty(baseFunctionBean.getTransitionScreenID())) {
            // 画面ID
            param.put(ContainerRequest.REQUEST_NAME_DYNAMIC_SCREEN_ID, baseFunctionBean.getTransitionScreenID());

            if (!StringUtil.isNullOrEmpty(baseFunctionBean.getTransitionScreenNameId())) {
                // 画面名ID
                param.put(ContainerRequest.REQUEST_NAME_DYNAMIC_SCREEN_NAME_ID,
                        baseFunctionBean.getTransitionScreenNameId());
            }
            else {
                param.put(ContainerRequest.REQUEST_NAME_DYNAMIC_SCREEN_NAME_ID, "");
            }
            if (!StringUtil.isNullOrEmpty(baseFunctionBean.getTransitionTitleId())) {
                // タイトルID
                param.put(ContainerRequest.REQUEST_NAME_DYNAMIC_TITLE_ID, baseFunctionBean.getTransitionTitleId());
            }
            else {
                param.put(ContainerRequest.REQUEST_NAME_DYNAMIC_TITLE_ID, "");
            }
            // 遷移先の動的画面ID指定がない場合
        }
        else {
            // 空を設定する
            // 空を設定しないと、フォワード時に引き継がれてしまう。
            // 画面ID
            param.put(ContainerRequest.REQUEST_NAME_DYNAMIC_SCREEN_ID, "");
            // 画面名ID
            param.put(ContainerRequest.REQUEST_NAME_DYNAMIC_SCREEN_NAME_ID, "");
            // タイトルID
            param.put(ContainerRequest.REQUEST_NAME_DYNAMIC_TITLE_ID, "");
        }
        if (baseFunctionBean != null) {
            // 遷移処理後は、クリアする
            baseFunctionBean.clearTransitionScreen();
        }
    }

    /**
     * 動的画面情報をBaseFormBean,引継パラメータマッピング情報に設定
     * コマンド定義に引継パラメータの設定がある場合のみ実行する
     * @param param 動的画面情報
     * @param map 引継パラメータマッピング情報
     */
    private void setDynamicScreenInfoFormBeanMapping(Map<String, Object> param, Map<String, String> map)
    {

        // 動的画面ID
        if (param.get(ContainerRequest.REQUEST_NAME_DYNAMIC_SCREEN_ID) != null && param.get(
                ContainerRequest.REQUEST_NAME_DYNAMIC_SCREEN_ID) != "") {
            baseFormBean.setDynamicScreenId((String) param.get(ContainerRequest.REQUEST_NAME_DYNAMIC_SCREEN_ID));
            map.put(ContainerRequest.REQUEST_NAME_DYNAMIC_SCREEN_ID, ContainerRequest.REQUEST_NAME_DYNAMIC_SCREEN_ID);
        }
        // 動的画面名ID
        if (param.get(ContainerRequest.REQUEST_NAME_DYNAMIC_SCREEN_NAME_ID) != null && param.get(
                ContainerRequest.REQUEST_NAME_DYNAMIC_SCREEN_NAME_ID) != "") {
            baseFormBean.setDynamicScreenNameId((String) param.get(
                    ContainerRequest.REQUEST_NAME_DYNAMIC_SCREEN_NAME_ID));
            map.put(ContainerRequest.REQUEST_NAME_DYNAMIC_SCREEN_NAME_ID,
                    ContainerRequest.REQUEST_NAME_DYNAMIC_SCREEN_NAME_ID);
        }
        // 動的タイトルID
        if (param.get(ContainerRequest.REQUEST_NAME_DYNAMIC_TITLE_ID) != null && param.get(
                ContainerRequest.REQUEST_NAME_DYNAMIC_TITLE_ID) != "") {
            baseFormBean.setDynamicTitleId((String) param.get(ContainerRequest.REQUEST_NAME_DYNAMIC_TITLE_ID));
            map.put(ContainerRequest.REQUEST_NAME_DYNAMIC_TITLE_ID, ContainerRequest.REQUEST_NAME_DYNAMIC_TITLE_ID);
        }

    }

    /**
     * 画面遷移の共通パラメータ
     * @return 画面遷移の共通パラメータ
     */
    public void setCommonTransitionParam(Map<String, Object> param)
    {
        param.put(ContainerRequest.REQUEST_NAME_WINDOW_ID, baseFormBean.getWindowId());
    }

    @Override
    public void preExecute() throws Exception
    {
        // 処理なし
    }

    @Override
    public void postExecute() throws Exception
    {
        // 処理なし
    }

    /**
     * バリデーションチェック項目設定
     */
    @TraceMonitor
    @ErrorHandling
    @Override
    public void initCheckList() throws GnomesAppException, Exception
    {

        if (commandData.getCheckList() != null) {
            // 検証対象リストの設定
            Map<Integer, String> checkList = new HashMap<Integer, String>();
            int seq = 0;

            for (String resId : commandData.getCheckList()) {
                checkList.put(++seq, resId);
            }

            ((IFormBean) baseFormBean).setCheckList(checkList);
        }
    }

    @Override
    public void validate() throws GnomesAppException
    {
        // 処理なし
    }

    /**
     * FormBeanリストア
     */
    @TraceMonitor
    @ErrorHandling
    @Override
    public void setFormBeanForRestore() throws GnomesException
    {

        // マスター検索情報のリストア
        if (baseFunctionBean != null) {
            // 自画面の情報のみ
            Map<String, MstSearchInfo> mapPageMstSearchInfo = new HashMap<>();
            // オリジナル（定義上）の画面ID
            String screenId = requestContext.getOrgScreenId();
            // オリジナル（定義上）の画面IDがない場合
            if (StringUtil.isNullOrEmpty(screenId)) {
                // 表示上の画面IDを使う
                screenId = requestContext.getScreenId();
            }

            String prefix = screenId.concat(TABLE_TAG_ID_SEPARATOR);
            int sepCnt = screenId.split(TABLE_TAG_ID_SEPARATOR_REGEX).length + 1;
            Map<String, MstSearchInfo> mstSearchInfoMap = baseFunctionBean.getMstSearchInfoMap();
            if (mstSearchInfoMap != null && mstSearchInfoMap.size() > 0) {
                for (Entry<String, MstSearchInfo> e : mstSearchInfoMap.entrySet()) {
                    if (e.getKey().startsWith(prefix) && sepCnt == e.getKey().split(
                            TABLE_TAG_ID_SEPARATOR_REGEX).length) {

                        mapPageMstSearchInfo.put(e.getKey(), e.getValue());
                    }
                }
            }

            baseFormBean.setMstSearchInfoMap(mapPageMstSearchInfo);

            /* 検索条件は常に画面から送られるようになったので、不要となった
            Map<String, SearchSetting> mapPageSearchSetting = new HashMap<>();

            for(Entry<String, SearchSetting> e : baseFunctionBean.getSearchSettingMap().entrySet()) {
                if (e.getKey().startsWith(prefix) &&
                        sepCnt == e.getKey().split(TABLE_TAG_ID_SEPARATOR_REGEX).length) {

                        mapPageSearchSetting.put(e.getKey(), e.getValue());
                }
            }
            if (baseFormBean.getSearchSettingMap() != null) {
                // 画面からの情報を設定
                mapPageSearchSetting.putAll(baseFormBean.getSearchSettingMap());
            }
            baseFormBean.setSearchSettingMap(mapPageSearchSetting);
            */
            // 2018/11/16 ADD START -------------
            // 遷移先から復帰時
            // 遷移先から検索条件は送られていないので
            boolean isFound = false;
            if (baseFormBean.getSearchSettingMap() != null) {
                for (Entry<String, SearchSetting> e : baseFormBean.getSearchSettingMap().entrySet()) {
                    if (e.getKey().startsWith(prefix) && sepCnt == e.getKey().split(
                            TABLE_TAG_ID_SEPARATOR_REGEX).length) {
                        // 自画面の検索条件が送られてきている
                        isFound = true;
                        break;
                    }
                }
            }

            if (isFound == false) {
                // ここで、functionBeanよりformBeanに検索条件を復帰させる
                Map<String, SearchSetting> mapPageSearchSetting = new HashMap<>();
                Map<String, SearchSetting> searchSettingMap = baseFunctionBean.getSearchSettingMap();
                if (searchSettingMap != null && searchSettingMap.size() > 0) {
                    for (Entry<String, SearchSetting> e : searchSettingMap.entrySet()) {
                        if (e.getKey().startsWith(prefix) && sepCnt == e.getKey().split(
                                TABLE_TAG_ID_SEPARATOR_REGEX).length) {

                            mapPageSearchSetting.put(e.getKey(), e.getValue());
                        }
                    }
                }

                baseFormBean.setSearchSettingMap(mapPageSearchSetting);
            }

			// 表示用のターブルヘッダーを設定
			baseFormBean.setTableDispHeaders(baseFunctionBean.getTableDispHeaders());

            // 2018/11/16 ADD END --------------

            // Hiddenまたはnoneにするカスタムタグの辞書IDをリストア
            baseFormBean.setTagHiddenKindMap(baseFunctionBean.getTagHiddenKindMap());
        }

        // FounctionBean→FormBeanのマッピング情報
        Map<String, String> map = commandData.getMappingFormBeanRestore();
        if (map != null) {
            try {
                commonCommand.mappingBean(baseFormBean, baseFunctionBean, map);
            }
            catch (GnomesAppException e) {
                // 処理なし(GnomesExceptionのみとなる)
            }
        }

        // FormBeanリストアの後処理
        try {
            baseFormBean.setFormBeanForRestoreAfter();
        }
        catch (GnomesAppException e) {
            // 処理なし(GnomesExceptionのみとなる)
        }

    }

    /**
     * FormBean取得
     */
    @Override
    public IScreenFormBean getFormBean()
    {
        return (IScreenFormBean) baseFormBean;
    }

    /**
     * FunctionBean取得
     */
    public BaseFunctionBean getFunctionBean()
    {
        return baseFunctionBean;
    }

    /**
     * デフォルトフォワード設定
     */
    @Override
    public void setDefaultForward()
    {
        if (commandData.getDefaultForward().contains(CommonConstants.REPLACE_LOCALE)) {
            String selectLocale[] = gnomesSessionBean.getLocaleId().split(CommonConstants.SPLIT_CHAR);
            String defaultForward[] = commandData.getDefaultForward().split(CommonConstants.SPLIT_EXTENSION_LOCALE);
            String urlLocale = defaultForward[0].replace(CommonConstants.REPLACE_LOCALE, selectLocale[0]);
            responseContext.setForward(urlLocale + CommonConstants.PERIOD + defaultForward[1]);
        }
        else {
            responseContext.setForward(commandData.getDefaultForward());
        }
    }

    /**
    * ページトークンチェックフラグを取得
    * @return tokenCheckFlg
    */
    @Override
    public boolean getTokenCheckFlg()
    {

        return commandData.isTokenCheckFlg();
    }

    /**
    * ページトークン更新フラグを取得
    * @return tokenUpdateFlg
    */
    @Override
    public boolean getTokenUpdateFlg()
    {

        return commandData.isTokenUpdateFlg();
    }

    /**
     * @return postConstructException
     */
    public GnomesAppException getPostConstructException()
    {
        return postConstructException;
    }

    /**
     * @param postConstructException セットする postConstructException
     */
    public void setPostConstructException(GnomesAppException postConstructException)
    {
        this.postConstructException = postConstructException;
    }

    @Inject
    protected TableSearchSettingDao tableSearchSettingDao;

    /**
     * 保存されている検索条件を取得
     * @param tableId テーブルID
     * @param type テーブル検索条件管理の設定種類
     * @return 検索条件
     * @throws Exception
     */
    protected SearchSetting getSaveSearchSetting(String tableId, TableSearchSettingType type) throws Exception
    {

        SearchSetting result = null;

        TableSearchSetting setting = tableSearchSettingDao.getTableSearchSetting(requestContext.getUserId(), tableId,
                type.getValue());
        if (setting != null) {
            result = ConverterUtils.readJson(setting.getSetting(), SearchSetting.class);
        }

        return result;
    }

    /**
     * 検索情報の初期設定
     * @param tableTagName テーブルタグ辞書名
     * @param formBean フォームビーン
     * @throws Exception
     */
    @TraceMonitor
    @ErrorHandling
    protected void initTableInfo(DispType dispType, String tableTagName, String dispScreenId,
            Map<String, MstSearchInfo> mstSearchInfoMap, Map<String, SearchSetting> searchSettingMap) throws Exception
    {

        MstSearchInfo mst = null;
        try {
            // formBeanに検索マスター情報の作成
            mst = searchInfoController.getMstSearchInfo(tableTagName, ((IScreenFormBean) baseFormBean).getUserLocale());
            mstSearchInfoMap.put(tableTagName, mst);
        }
        catch (Exception ex) {
            //[2018/08/29 浜本記載]
            //クエリ定義のエラーで例外が出ることがあるので、補足として追加
            logHelper.severe(this.logger, null, null,
                    "Query Error Please check the query definition. For example, a Where phrase is defined in a pull-down query.tableTagName = " + tableTagName);
            throw new GnomesAppException(ex);
        }

        SearchSetting searchSetting = new SearchSetting();

        // テーブルタグ名は定義上の画面ID.XXXX
        int index = tableTagName.indexOf(GnomesCTagDictionary.ID_SEPARATOR);
        String tableTag = tableTagName.substring(index, tableTagName.length());

        // 保存されている検索条件のキーは
        // 表示上の画面ID.XXXX
        String saveTableTagName = dispScreenId + tableTag;

        // 記録済みの検索情報を取得
        SearchSetting loadSetting = getSaveSearchSetting(saveTableTagName, TableSearchSettingType.SEARCH);
        if (loadSetting != null) {
            // 保存あり
            // 変換
            List<ConditionInfo> saveConditions = getSaveConditionInfo(loadSetting.getConditionInfos(), mst);

            searchSetting.setConditionInfos(saveConditions);
            // 保存データ読込んだフラグを設定
            mst.setConditonSaveDataLoaded(true);
        }
        else {
            // 保存なし 初期設定値
            searchSetting.setConditionInfos(mst.getDefaultSearchSetting().getConditionInfos());
        }
        // 記録済みの表示情報を取得
        loadSetting = getSaveSearchSetting(saveTableTagName, TableSearchSettingType.DISPLAY);
        if (loadSetting != null) {
            // 保存あり

            // 固定col数
            searchSetting.setFixedColNum(loadSetting.getFixedColNum());

            // 表示情報
            searchSetting.setOrderingInfos(loadSetting.getOrderingInfos());
        }
        else {
            // 保存なし 初期設定値
            searchSetting.setFixedColNum(mst.getDefaultSearchSetting().getFixedColNum());

            searchSetting.setOrderingInfos(mst.getDefaultSearchSetting().getOrderingInfos());
        }

        searchSetting.setDispType(dispType);

        // 画面テーブル設定マスタの取得
        MstrScreenTableTag mstrScreenTableTag = mstrScreenTableTagDao.getMstrScreenTableTag(tableTagName);

        int maxDispCount;
        MstrSystemDefine mstrSystemDefine;

        // 画面テーブル設定マスタが存在しない場合、デフォルトの最大表示件数をシステム定数テーブルより取得
        if (Objects.isNull(mstrScreenTableTag)) {

            mstrSystemDefine = mstrSystemDefineDao.getMstrSystemDefine(SystemDefConstants.TYPE_CTAG_TABLE_DEFAULT,
                    SystemDefConstants.MAX_LIST_DISPLAY_COUNT);

            // デフォルトの最大表示件数を取得できない場合、エラー
            if (Objects.isNull(mstrSystemDefine)) {
                StringBuilder params = new StringBuilder();
                params.append(MstrSystemDefine.TABLE_NAME);
                params.append(CommonConstants.PERIOD).append(MstrSystemDefine.COLUMN_NAME_SYSTEM_DEFINE_TYPE);
                params.append(CommonConstants.COLON).append(SystemDefConstants.TYPE_CTAG_TABLE_DEFAULT);
                params.append(CommonConstants.COMMA).append(MstrSystemDefine.COLUMN_NAME_SYSTEM_DEFINE_CODE);
                params.append(CommonConstants.COLON).append(SystemDefConstants.MAX_LIST_DISPLAY_COUNT);

                // ME01.0026:「データがみつかりません。（{0}） 」
                GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                        GnomesMessagesConstants.ME01_0026, params.toString());
                throw ex;
            }
            maxDispCount = mstrSystemDefine.getNumeric1().intValue();

        }
        else {
            maxDispCount = mstrScreenTableTag.getMax_list_display_count();
        }

        searchSetting.setMaxDispCount(maxDispCount);

        // ページングの場合
        if (dispType == DispType.DispType_Paging) {

            int onePageDispCount;
            // ページは1
            searchSetting.setNowPage(1);

            // 1ページ表示件数
            // 画面テーブル設定マスタ.１ページの表示件数が存在しない場合、デフォルトの１ページの表示件数をシステム定数テーブルより取得
            if (Objects.isNull(mstrScreenTableTag) || Objects.isNull(
                    mstrScreenTableTag.getOne_page_list_display_count())) {

                mstrSystemDefine = mstrSystemDefineDao.getMstrSystemDefine(SystemDefConstants.TYPE_CTAG_TABLE_DEFAULT,
                        SystemDefConstants.ONE_PAGE_LIST_DISPLAY_COUNT);

                // デフォルトの１ページの表示件数を取得できない場合、エラー
                if (Objects.isNull(mstrSystemDefine)) {
                    StringBuilder params = new StringBuilder();
                    params.append(MstrSystemDefine.TABLE_NAME);
                    params.append(CommonConstants.PERIOD).append(MstrSystemDefine.COLUMN_NAME_SYSTEM_DEFINE_TYPE);
                    params.append(CommonConstants.COLON).append(SystemDefConstants.TYPE_CTAG_TABLE_DEFAULT);
                    params.append(CommonConstants.COMMA).append(MstrSystemDefine.COLUMN_NAME_SYSTEM_DEFINE_CODE);
                    params.append(CommonConstants.COLON).append(SystemDefConstants.ONE_PAGE_LIST_DISPLAY_COUNT);

                    // ME01.0026:「データがみつかりません。（{0}） 」
                    GnomesAppException ex = exceptionFactory.createGnomesAppException(null,
                            GnomesMessagesConstants.ME01_0026, params.toString());
                    throw ex;
                }
                onePageDispCount = mstrSystemDefine.getNumeric1().intValue();
            }
            else {
                onePageDispCount = mstrScreenTableTag.getOne_page_list_display_count();
            }

            searchSetting.setOnePageDispCount(onePageDispCount);
        }

        searchSettingMap.put(tableTagName, searchSetting);

    }

    /**
     * 保存時の検索条件から変換取得
     * @param saveConditions 保存検索条件
     * @param mstSearchInfo マスター検索条件
     * @return 保存時の検索条件
     * @throws ParseException
     * @throws GnomesAppException
     */
    public List<ConditionInfo> getSaveConditionInfo(List<ConditionInfo> saveConditions, MstSearchInfo mstSearchInfo)
            throws ParseException, GnomesAppException
    {
        List<ConditionInfo> result = new ArrayList<>();

        for (ConditionInfo condition : saveConditions) {
            MstCondition m = mstSearchInfo.getMstCondition(condition.getColumnId());
            //保存された条件が古く、新しい検索マスターに存在しなくなった場合は
            //この設定を消す
            if (m == null) {
                continue;
            }
            if (m.getSaveParamTypes() == null || condition.getParameters() == null) {
                result.add(condition);
                continue;
            }

            ConditionInfo newCondition = new ConditionInfo();
            newCondition.setColumnId(condition.getColumnId());
            newCondition.setEnable(condition.isEnable());
            newCondition.setPatternKeys(condition.getPatternKeys());
            newCondition.setHiddenItem(condition.isHiddenItem());

            List<String> newParams = new ArrayList<>();
            // 拡張情報
            List<String> newParamsExt = new ArrayList<>();

            // 3個めからは拡張情報
            for (int i = 0; i < ((condition.getParameters().size() > 2) ? 2 : condition.getParameters().size()); i++) {

                ConditionParamSaveType saveType = m.getSaveParamTypes().get(i);
                if (saveType == null || saveType == ConditionParamSaveType.SAVE) {
                    newParams.add(condition.getParameters().get(i));
                    newParamsExt.add(null);
                }
                else if (saveType == ConditionParamSaveType.SYSTEM_DATE_SAVE) {

                    // 拡張情報の保持
                    newParamsExt.add(condition.getParameters().get(i + 2));

                    if (condition.getParameters().get(i) != null && condition.getParameters().get(i).length() > 0) {

                        ConditionDateType conditionDateType;
                        if (m.getType() == ConditionType.DATE || m.getType() == ConditionType.MULTIFORMAT_DATESTR) {
                            conditionDateType = ConditionDateType.DATE;
                        }
                        else if (m.getType() == ConditionType.DATE_TIME) {
                            conditionDateType = ConditionDateType.DATE_TIME;
                        }
                        else if (m.getType() == ConditionType.DATE_TIME_SS) {
                            conditionDateType = ConditionDateType.DATE_TIME_SS;
                        }
                        else if (m.getType() == ConditionType.DATE_TIME_MM00) {
                            conditionDateType = ConditionDateType.DATE_TIME_MM00;
                        }
                        else if (m.getType() == ConditionType.DATE_TIME_SS00) {
                            conditionDateType = ConditionDateType.DATE_TIME_SS00;
                        }
                        else {
                            conditionDateType = ConditionDateType.DATE_YM;
                        }

                        String localeDate = "";

                        //Web.xmlのタイムゾーンがUTCの場合,
                        if (CommonConstants.ZONEID_UTC.equals(TimeZone.getDefault().getID())) {
                            Long value = Long.parseLong(condition.getParameters().get(i));
                            // 経過ミリ秒から日付へ変換
                            Date date1 = new Date(value);

                            //UTCのDate型の値を任意のロケール(タイムゾーン)のString型に変更
                            localeDate = GnomesDateUtil.convertDateToLocaleString(date1, ResourcesHandler.getString(
                                    conditionDateType.getFormat(), gnomesSessionBean.getUserLocale()),
                                    gnomesSessionBean.getUserTimeZone(), gnomesSessionBean.getUserLocale());
                        }
                        else {
                            if (!condition.getParameters().get(i).equals("")) {
                                String[] params = condition.getParameters().get(i).split(CommonConstants.SPLIT_CHAR);

                                if (params.length >= 2) {
                                    // 文字列からdbに保存したフォーマットで日付型に変換
                                    Date date1 = ConverterUtils.stringToDateFormat(params[0], params[1]);

                                    //Web.xmlのタイムゾーンがUTCではない場合は変換を行わない
                                    //ログインのロケールによって、日付型から文字列に変換する
                                    localeDate = ConverterUtils.dateTimeToString(date1, ResourcesHandler.getString(
                                            conditionDateType.getFormat(), gnomesSessionBean.getUserLocale()));
                                }
                            }
                        }

                        //                        Date orgDate = ConverterUtils.stringToDateFormat(
                        //                        		localeDate,
                        //                                ResourcesHandler.getString(conditionDateType.getFormat(), gnomesSessionBean.getUserLocale()));

                        //                        // カレンダークラスのインスタンスを取得
                        //                        Calendar calOrg = Calendar.getInstance();
                        //                        calOrg.setTime(orgDate);
                        //
                        //
                        //                        // 現在
                        //                        Date dt = new Date();
                        //                        // 加算日数
                        //                        int addDate = Integer.valueOf(condition.getParameters().get(i + 2));
                        //
                        //                        // カレンダークラスのインスタンスを取得
                        //                        Calendar cal = Calendar.getInstance();
                        //
                        //                        // 現在時刻を設定
                        //                        cal.setTime(dt);
                        //
                        //                        // addDateを加算
                        //                        cal.add(Calendar.DATE, addDate);
                        //
                        //                        if (m.getType() != ConditionType.DATE ){
                        //                            cal.set(Calendar.HOUR_OF_DAY, calOrg.get(Calendar.HOUR_OF_DAY));   //時を指定する
                        //                            cal.set(Calendar.MINUTE, calOrg.get(Calendar.MINUTE));  //分を指定する
                        //                        }
                        //
                        //                        if (m.getType() == ConditionType.DATE_TIME_SS ){
                        //                            cal.set(Calendar.SECOND, calOrg.get(Calendar.SECOND));  //秒を指定する
                        //                        }

                        //                        String p = ConverterUtils.dateTimeToString(
                        //                        		orgDate,
                        //                                ResourcesHandler.getString(conditionDateType.getFormat(), gnomesSessionBean.getUserLocale()));

                        // newParams.add(p);
                        newParams.add(localeDate);

                    }
                    else {
                        newParams.add("");
                    }

                }
                else {
                    // 保存なし
                    ConditionInfo org = null;
                    for (ConditionInfo ci : mstSearchInfo.getDefaultSearchSetting().getConditionInfos()) {
                        if (ci.getColumnId().equals(condition.getColumnId())) {
                            org = ci;
                            break;
                        }
                    }
                    if (org == null) {
                        newParams.add("");
                        newParamsExt.add(null);
                    }
                    else {
                        newParams.add(org.getParameters().get(i));
                        newParamsExt.add(null);
                    }
                }
            }
            newParams.addAll(newParamsExt);
            newCondition.setParameters(newParams);
            result.add(newCondition);
        }

        return result;
    }
}
