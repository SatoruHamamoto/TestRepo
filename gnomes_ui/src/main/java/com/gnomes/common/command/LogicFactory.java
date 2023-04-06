package com.gnomes.common.command;

import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums.DisplayType;
import com.gnomes.common.constants.CommonEnums.PrivilegeDisplayFinishFlag;
import com.gnomes.common.data.CommandData;
import com.gnomes.common.data.FileDownLoadData;
import com.gnomes.common.data.GnomesEjbBean;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.data.GnomesSystemBean;
import com.gnomes.common.data.MessageInfo;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.exportdata.ExportDataTable;
import com.gnomes.common.exportdata.ExportDataTableDef;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.view.CommandIdConstants;
import com.gnomes.common.view.IScreenFormBean;
import com.gnomes.common.view.IServiceFormBean;
import com.gnomes.common.view.SystemFormBean;
import com.gnomes.system.dao.MstrScreenButtonDao;
import com.gnomes.system.dao.MstrSiteDao;
import com.gnomes.system.dao.MstrSystemDefineDao;
import com.gnomes.system.data.IScreenPrivilegeBean;
import com.gnomes.system.data.PartsPrivilegeResultInfo;
import com.gnomes.system.entity.MstrScreenButton;
import com.gnomes.system.logic.BLSecurity;
import com.gnomes.uiservice.ContainerRequest;
import com.gnomes.uiservice.ContainerResponse;
import com.gnomes.uiservice.ServletContainer;

/**
 * ロジックファクトリークラス。
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/07/01 YJP/K.Gotanda             初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class LogicFactory {

    // ロガー
    @Inject
    transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected LogHelper logHelper;

    @Inject
    protected GnomesExceptionFactory exceptionFactory;

    @Inject
    HttpServletRequest request;

    @Inject
    ContainerRequest requestContext;

    @Inject
    ContainerResponse responseContext;

    @Inject
    GnomesSessionBean gnomesSessionBean;

    @Inject
    BLSecurity blSecurity;

    @Inject
    GnomesSystemBean gnomesSystemBean;

    @Inject
    ExportDataTable exportDataScreenTable;

    /*
    * システム定義 Dao
    */
   @Inject
   protected MstrSystemDefineDao mstrSystemDefineDao;

    @Inject
    protected GnomesEjbBean ejbBean;

    /**
     * サイトマスタDAO
     */
    @Inject
    protected MstrSiteDao mstrSiteDao;

    /** 画面共通項目用 フォームビーン */
    @Inject
    protected SystemFormBean systemFormBean;

    /** 画面ボタンマスタ Dao */
    @Inject
    protected MstrScreenButtonDao mstrScreenButtonDao;

    // リクエストパラメーター名称: コマンド
    transient static final String REQUEST_PARAM_COMMAND = "command";

    // リクエストパラメーター名称: ページトークン
    transient static final String REQUEST_PARAM_TOKEN = "pageToken";

    // リクエストパラメータ名称：ウインドウID
    transient static final String REQUEST_PARAM_WINDOWID = "windowId";

    public void executeLogic() throws Exception {

        //ロジック
        IScreenCommand logic = createLogic();

        //ロジックが取得できない（通常は例外に飛ぶが)
        //その場合は何もせずリターンする
        if(Objects.isNull(logic)){
            return;
        }

        // 画面ID、画面名の ContainerRequest への設定 （ScreenInfoInterceptorを起動するために必要）
        logic.setScreenInfo();

        IScreenFormBean formBean = logic.getFormBean();

        // ユーザ言語設定
        formBean.setUserLocale(gnomesSessionBean.getUserLocale());

        // コンピュータ名設定時
        if (formBean.getComputerName() != null && !formBean.getComputerName().equals("")) {
            // コンピュータ名設定
            gnomesSessionBean.setComputerName(formBean.getComputerName());
            requestContext.setSessionInfo();
        }
        formBean.setComputerName(gnomesSessionBean.getComputerName());

        // インポートファイルエラー情報クリア
        clearRequestImpErr();

        // 一覧エクスポートクリア
        clearExpDataTableDef();

        //操作開始メッセージ出力
        startOperationMessage();

        // 権限
        Class<?> clazz = formBean.getClass();
        Class<?> intrfc = IScreenPrivilegeBean.class;

        // インターフェースを実装したクラスであるかどうかをチェック
        if (!clazz.isInterface() && intrfc.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {

            // 権限設定処理
            blSecurity.setScreenPrivilege((IScreenPrivilegeBean) formBean);
        }

        //リストア実行
        logic.setFormBeanForRestore();

        //デフォルトフォワード先設定
        logic.setDefaultForward();

        //---------------------------------------------------------------------------------------
        // ページトークンに関する処理のセクション
        //---------------------------------------------------------------------------------------

        //リクエストした画面の中にあるname=windowIdの値を入手する
        String windowId = requestContext.getWrapperRequest().getParameter(REQUEST_PARAM_WINDOWID);

        //セッションビーンで管理されているページトークンをwindowidから引き出す
        String savedToken = this.gnomesSessionBean.getPageToken(windowId);

        //トークンチェック、再生成
        // FunctionBeanの保存したトークンと比較するため3番目の引数にFunctionBeanのトークンを指定
        //
        if (chkTokenRegenerate(logic.getTokenCheckFlg(), logic.getTokenUpdateFlg(), savedToken) == false) {
            return;
        }

        //---------------------------------------------------------------------------------------
        // バリデーションチェックに関するセクション
        //---------------------------------------------------------------------------------------
        // インターフェースを実装したクラスであるかどうかをチェック
        if (!clazz.isInterface() && intrfc.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {
            // 認証権限チェック
            if (logic.judgePersonsLicenseCheck((IScreenPrivilegeBean) formBean, formBean.getScreenId()) == false) {
                // 認証権限チェックエラー時は、以降処理を行わない
                return;
            }
        }

        // バリデーションチェック項目設定
        logic.initCheckList();

        // 共通バリデーション実行
        if (logic.commonValidate(formBean) == false) {
            // 業務バリデーション実行
            logic.validate();
        }
        // バリデーション結果確認
        if (logic.validateResultCheck(formBean) == false) {
            return;
        }

        // 前処理
        logic.preExecute();

        // メイン処理実行
        if (logic.isTransactional()) {
            logic.mainExecute();
        } else {
            logic.mainExecuteNoTransactional();
        }

        // 権限チェックやバリデーションでは、例外スローがないので、このフラグを追加して対応
        // 画面表示時に、リクエストのままを表示するかを判定するフラグ
        responseContext.setLogicError(false);

        // 後処理
        logic.postExecute();

        // エクスポート
        doExportTable(logic);

        //操作完了メッセージ出力
        completionOperationMessage(clazz, formBean);

    }

    /**
     * インポートエラークリア
     *
     * @param command コマンド
     */
    private void clearRequestImpErr() {

        // インポートエラーエクスポートコマンド以外の場合
        if (!requestContext.getCommandId().equals(CommandIdConstants.COMMAND_ID_Y99002C001)) {
            // インポートエラー情報クリア
            gnomesSessionBean.setRequestImpErr(null);
            // インポートエラーファイルクリア
            gnomesSessionBean.setRequestImpErrFile(null);
        }
    }

    /**
     * 一覧エクスポートクリア
     */
    private void clearExpDataTableDef() {

        // エクスポートコマンド以外の場合
        if (!requestContext.getCommandId().equals(CommandIdConstants.COMMAND_ID_Y99002C002)) {
            gnomesSessionBean.setExportDataTableDef(null);
        }
    }

    /**
     * 一覧テーブルエクスポート
     * @param logic コマンド
     * @throws GnomesAppException
     */
    private void doExportTable(IScreenCommand logic) throws GnomesAppException {

        Class<?> clazz = logic.getClass();
        Class<?> intrfc = IExportTableCommand.class;

        // インターフェースを実装したクラスであるかどうかをチェック
        if (!clazz.isInterface() && intrfc.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {

            // コマンドよりエクスポート定義情報を取得
            ExportDataTableDef expDef = ((IExportTableCommand) logic).getExportDataTableDefinition();

            // エクスポート処理
            List<FileDownLoadData> files = new ArrayList<FileDownLoadData>();
            FileDownLoadData file = exportDataScreenTable.doExport(expDef, gnomesSessionBean.getUserLocale());
            files.add(file);
            responseContext.setFileDownLoadDatas(files);
        }
    }

    /**
     * サービス用のロジック実行
     * @return Object 実行戻り値
     * @throws Exception 例外
     */
    public Object executeServiceLogic() throws Exception {

        // コマンド取得
        ServiceBaseCommand logic;
        String command = requestContext.getServiceRequestParam(REQUEST_PARAM_COMMAND);

        Instance<ServiceBaseCommand> instance = CDI.current().select(ServiceBaseCommand.class,
                new CommandQualifierImple(command));

        if (!instance.iterator().hasNext()) {

            logic = getCommonCommand(command, ServiceBaseCommand.class, CommonConstants.SERVICE_COMMON_COMMAND);

            logic.setupCommand();

            GnomesAppException e = ((ServiceCommonCommand) logic).getPostConstructException();
            if (e != null) {
                throw e;
            }

        } else {
            // コマンドID を ContainerRequest に設定する。
            requestContext.setCommandId(command);

            //ロジック
            logic = instance.get();
        }

        // 画面ID、画面名の ContainerRequest への設定 （ScreenInfoInterceptorを起動するために必要）
        logic.setScreenInfo();

        IServiceFormBean formBean = logic.getFormBean();
        // ユーザ言語設定
        formBean.setUserLocale(gnomesSessionBean.getUserLocale());

        // コンピュータ名設定時
        if (formBean.getComputerName() != null && !formBean.getComputerName().equals("")) {
            // コンピュータ名設定
            gnomesSessionBean.setComputerName(formBean.getComputerName());
            requestContext.setSessionInfo();
        }
        formBean.setComputerName(gnomesSessionBean.getComputerName());

        //操作開始メッセージ出力
        startOperationMessage();

        // 権限
        Class<?> clazz = formBean.getClass();
        Class<?> intrfc = IScreenPrivilegeBean.class;

        // インターフェースを実装したクラスであるかどうかをチェック
        if (!clazz.isInterface() && intrfc.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {
            // 認証権限チェック
            if (logic.judgePersonsLicenseCheck((IScreenPrivilegeBean) formBean, formBean.getScreenId()) == false) {
                // 認証権限チェックエラー時は、以降処理を行わない
                return logic.getCommandResponse();
            }
        }

        // バリデーションチェック項目設定
        logic.initCheckList();

        // 共通バリデーション実行
        if (logic.commonValidate(formBean) == false) {
            // 業務バリデーション実行
            logic.validate();
        }

        // バリデーション結果確認
        if (logic.validateResultCheck(formBean) == true) {

            // 前処理
            logic.preExecute();

            // メイン処理実行
            if (logic.isTransactional()) {
                logic.mainExecute();
            } else {
                logic.mainExecuteNoTransactional();
            }

            // 後処理
            logic.postExecute();
        }
        return logic.getCommandResponse();
    }

    /**
     * サービス用のロジック実行
     * @return Object 実行戻り値
     * @throws Exception 例外
     */
    public Object executeServiceLogicForSF() throws Exception {

        // コマンド取得
        ServiceBaseCommand logic;
        String command = requestContext.getServiceRequestParam(REQUEST_PARAM_COMMAND);

        Instance<ServiceBaseCommand> instance = CDI.current().select(ServiceBaseCommand.class,
                new CommandQualifierImple(command));

        if (!instance.iterator().hasNext()) {

            logic = getCommonCommand(command, ServiceBaseCommand.class, CommonConstants.SERVICE_COMMON_COMMAND);

            logic.setupCommand();

            GnomesAppException e = ((ServiceCommonCommand) logic).getPostConstructException();
            if (e != null) {
                throw e;
            }

        } else {
            // コマンドID を ContainerRequest に設定する。
            requestContext.setCommandId(command);

            //ロジック
            logic = instance.get();
        }

        // 画面ID、画面名の ContainerRequest への設定 （ScreenInfoInterceptorを起動するために必要）
        logic.setScreenInfo();

        IServiceFormBean formBean = logic.getFormBean();

        //操作開始メッセージ出力
        startOperationMessage();

        // 権限
        Class<?> clazz = formBean.getClass();
        Class<?> intrfc = IScreenPrivilegeBean.class;

        // インターフェースを実装したクラスであるかどうかをチェック
        if (!clazz.isInterface() && intrfc.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {
            // 認証権限チェック
            if (logic.judgePersonsLicenseCheck((IScreenPrivilegeBean) formBean, formBean.getScreenId()) == false) {
                // 認証権限チェックエラー時は、以降処理を行わない
                return logic.getCommandResponse();
            }
        }

        // バリデーションチェック項目設定
        logic.initCheckList();

        // 共通バリデーション実行
        if (logic.commonValidate(formBean) == false) {
            // 業務バリデーション実行
            logic.validate();
        }

        // バリデーション結果確認
        if (logic.validateResultCheck(formBean) == true) {

            // 前処理
            logic.preExecute();

            // メイン処理実行
            if (logic.isTransactional()) {
                logic.mainExecute();
            } else {
                logic.mainExecuteNoTransactional();
            }

            // 後処理
            logic.postExecute();
        }

        return logic.getCommandResponse();
    }

    /**
     * ロジッククラスを生成します。
     * <pre>
     * 生成条件:
     *   command
     * </pre>
     * @return Logic
     */
    public IScreenCommand createLogic() throws GnomesAppException {

        IScreenCommand result = null;

        String command = null;
        if (ServletContainer.USE_ATTRIBUTE) {
            if (requestContext.isForwardAttribute()) {
                command = requestContext.getAttributeCommand();
                systemFormBean.clear();

                // リダイレクト
            } else if (requestContext.isRedirect()) {
                command = requestContext.getRequest().getRedirectCommand();
            } else {
                command = requestContext.getWrapperRequest().getParameter(REQUEST_PARAM_COMMAND);
            }
        } else {
            command = requestContext.getWrapperRequest().getParameter(REQUEST_PARAM_COMMAND);
            if (command == null || command.equals("")) {
                command = requestContext.getCommand();
            }
        }

        //通常コマンドIDが空白やNULLで来ることはないが、念のため
        //もし空白NULLが来たら画面状態を維持するためステータスを指定して中断する
        if (command == null || command.equals("")) {
            responseContext.setHttpStatus(HttpServletResponse.SC_NO_CONTENT);
            return null;
            // (ME01.0002) 指定されたコマンド： {0} は見つかりません。
            //throw new GnomesAppException(null, GnomesMessagesConstants.ME01_0002, "--");
        }
        //----------------------------------------------------------------------------------
        //（特別）FunctionBeanの延命措置
        //  FunctionBeanのオブジェクト生成後のタイムアウトはweb.xmlに記載しているが
        //  これは画面が閉じられた後に誰も使わなくなったら消去されるタイマーであり
        //  画面操作しない間にもタイムアウトをカウントしていると意図しないタイミングで
        //  FunctionBeanのオブジェクトが破棄されエラーになる。そのためメッセージポップアップ時
        //  とロック画面の表示する間は定周期で特殊コマンドが呼ばれ、FunctionBeanを延命させる
        //----------------------------------------------------------------------------------
        if (command.equals(CommandIdConstants.COMMAND_ID_WATCHDOG_CONVERSION)){
            this.gnomesSessionBean.CountUp();
            //次のコメントはデバッグ用に残しておく
//            this.gnomesSessionBean.DebugOutput("画面延命措置サブミット");
            responseContext.setHttpStatus(HttpServletResponse.SC_NO_CONTENT);
            return null;
        }
        Instance<IScreenCommand> instance = CDI.current().select(IScreenCommand.class,
                new CommandQualifierImple(command));

        if (!instance.iterator().hasNext()) {

            result = getCommonCommand(command, IScreenCommand.class, CommonConstants.SCREEN_COMMON_COMMAND);

            result.setupCommand();

			//コマンド処理準備時のエラー情報が存在した場合何もせず処理終了する
            if(! StringUtil.isNullOrEmpty(requestContext.getCommandValidErrorInfo())){
                responseContext.setHttpStatus(HttpServletResponse.SC_NO_CONTENT);
                return null;
            }

            GnomesAppException e = ((ScreenCommonCommand) result).getPostConstructException();
            if (e != null) {
                throw e;
            }

        } else {
            // コマンドID を ContainerRequest に設定する。
            requestContext.setCommandId(command);
            result = instance.get();
        }

        // スクロール位置保持有無を設定
        CommandData commandData = this.getCommandData(command);
		if (commandData != null && commandData.isKeepScrollPosition()) {
			systemFormBean.setKeepScrollPosition("true");
		} else {
			systemFormBean.setKeepScrollPosition(null);
		}

        return result;
    }

    /**
     * 共通コマンド取得
     * @param command コマンド
     * @param selClass 基底クラス
     * @return 共通コマンドインスタンス
     * @throws GnomesAppException
     */
    private <T> T getCommonCommand(String command, Class<T> selClass, String baseCommandQualifier)
            throws GnomesAppException {

        T result = null;

        CommandData commandData = getCommandData(command);
        if (commandData == null) {
            // (ME01.0002) 指定されたコマンド： {0} は見つかりません。
            throw new GnomesAppException(null, GnomesMessagesConstants.ME01_0002, command);
        }

        String commonCommand = commandData.getCommandQualifier();
        if (commonCommand == null) {
            commonCommand = baseCommandQualifier;
        }

        Instance<T> instance = CDI.current().select(selClass, new CommandQualifierImple(commonCommand));
        if (!instance.iterator().hasNext()) {
            // (ME01.0002) 指定されたコマンド： {0} は見つかりません。
            throw new GnomesAppException(null, GnomesMessagesConstants.ME01_0002, command);
        }

        requestContext.setCommandId(command);
        result = instance.get();

        return result;
    }

    /**
     * 共通コマンド情報取得
     * @param commandId コマンドID
     * @return 共通コマンド情報
     * @throws GnomesAppException
     */
    private CommandData getCommandData(String commandId) throws GnomesAppException {

        CommandData commandData = null;

        try {
            List<CommandData> lstCommandData = gnomesSystemBean.getCommandDatas().getCommandDataList().stream()
                    .filter(item -> commandId.equals(item.getCommandId())).collect(Collectors.toList());

            if (lstCommandData.size() > 0) {
                commandData = lstCommandData.get(0);
            }
        } catch (NullPointerException ex) {
            // エラー
            //ME01.0178	コマンドIDが見つかりません。commandId={0} Bean定義シートと定数シートを確認してください
            throw exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0178, commandId);
        }
        return commandData;
    }

    /**
     * ログイン用コマンドか否かをチェックする
     */
    public boolean isLoginCommand() {
        String command = request.getParameter(REQUEST_PARAM_COMMAND);

        // コマンド名が未設定か否か
        if (StringUtil.isNullOrEmpty(command)) {
            // コマンド名未設定の場合はログイン画面用のコマンドを設定する。
            request.setAttribute(ContainerResponse.ATTR_FORWARD_CMD_NAME, CommandIdConstants.COMMAND_ID_LOGIN_CLIENT);
            return true;
        }

        // コマンド名がログイン画面表示用か否か
        if (command.equals(CommandIdConstants.COMMAND_ID_LOGIN_CLIENT)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * ログイン用コマンドか否かをチェックする
     */
    public boolean isLoginCommand(DisplayType displayType) {
        String command = request.getParameter(REQUEST_PARAM_COMMAND);

        // コマンド名が未設定か否か
        if (StringUtil.isNullOrEmpty(command)) {
            if (displayType.equals(DisplayType.CLIENT)) {
                // コマンド名未設定の場合はログイン画面（管理端末）表示用のコマンドを設定する。
                request.setAttribute(ContainerResponse.ATTR_FORWARD_CMD_NAME,
                        CommandIdConstants.COMMAND_ID_LOGIN_CLIENT);
                return true;
            } else if (displayType.equals(DisplayType.PANECON)) {
                // コマンド名未設定の場合はログイン画面（工程端末）表示用のコマンドを設定する。
                request.setAttribute(ContainerResponse.ATTR_FORWARD_CMD_NAME,
                        CommandIdConstants.COMMAND_ID_LOGIN_PANECON);
                return true;
            }

        }

        // コマンド名がログイン画面表示用か否か
        if (CommandIdConstants.COMMAND_ID_LOGIN_CLIENT.equals(command)
                || CommandIdConstants.COMMAND_ID_LOGIN_PANECON.equals(command)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 対象クラスのインスタンスをCDIより取得.
     * <pre>
     * 取得できなかった場合、nullを返却。
     * </pre>
     * @param className クラス名
     * @return CDIより取得した対象クラスのインスタンス
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public Object getCDIInstance(String className) throws ClassNotFoundException {

        CDI<Object> current = CDI.current();
        Class<?> clazz = Class.forName(className);

        Instance<?> instance = current.select(clazz);

        if (!instance.iterator().hasNext()) {
            return null;
        }
        return instance.get();

    }

    /**
     * 操作開始メッセージ出力
     * @throws ParseException
     * @throws GnomesAppException
     */
    private void startOperationMessage() throws ParseException, GnomesAppException {

        //メッセージ情報追加
        // パラメータ情報（リスト）の作成
        List<String> params = new ArrayList<String>();
        if (!StringUtil.isNullOrEmpty(requestContext.getUserName())) {
            params.add(requestContext.getUserName()); //ユーザ名
        }
        params.add(requestContext.getScreenName()); //画面名
        params.add(requestContext.getScreenId()); //画面ID

        if (!StringUtil.isNullOrEmpty(systemFormBean.getButtonId())) {
            // 画面ボタンマスタの取得
            MstrScreenButton dataMstrScreenButton = mstrScreenButtonDao
                    .getMstrScreenButton(requestContext.getScreenId(), systemFormBean.getButtonId(), true);
            if (dataMstrScreenButton != null) {
                // ボタン操作内容
                params.add(dataMstrScreenButton.getOperation_content());

                // ボタンID
                params.add(systemFormBean.getButtonId());
            }
        }

        params.add(ResourcesHandler.getString(requestContext.getOperationResourceKey(), gnomesSessionBean.getUserLocale())); //操作内容
        params.add(requestContext.getCommandId()); //コマンドID

        /**
         * YJP/浜本 Update 2021/02/10
         *      Bachからのログは出力しないように修正する
         */
        String userName = requestContext.getUserName();
        Boolean isLogging = true;
        if(! StringUtil.isNullOrEmpty(userName) && userName.equals(CommonConstants.USERNAME_BATCH)) {
            isLogging = false;
        }

        if(isLogging){

			requestContext.addMessageInfoForStartLogging(userName, params, false);

			//ログ出力
			String logStr;
			if (!StringUtil.isNullOrEmpty(userName)) {
				if (params.size() == 5) {
					if (userName.equals(CommonConstants.USERNAME_BATCH)) {
						logStr = "[Operation]" + MessagesHandler.getStringParamList(GnomesMessagesConstants.MV01_0038, params);
					} else {
						logStr = "[Operation]" + MessagesHandler.getStringParamList(GnomesMessagesConstants.MV01_0013, params);
					}
				} else {
					logStr = "[Operation]" + MessagesHandler.getStringParamList(GnomesMessagesConstants.MV01_0028, params);
				}
			} else {
				if (params.size() == 4) {
					logStr = "[Operation]" + MessagesHandler.getStringParamList(GnomesMessagesConstants.MV01_0034, params);
				} else {
					logStr = "[Operation]" + MessagesHandler.getStringParamList(GnomesMessagesConstants.MV01_0036, params);
				}
			}
			this.logHelper.info(this.logger, null, null, logStr);
		}
    }

    /**
     * 操作完了メッセージ出力
     * @throws ParseException
     * @throws GnomesAppException
     */
    private void completionOperationMessage(Class<?> clazz, IScreenFormBean formBean)
            throws ParseException, GnomesAppException {

        //メッセージ情報追加

        // ボタンID
        String buttonId = systemFormBean.getButtonId();

        // パラメータ情報（リスト）の作成
        List<String> params = new ArrayList<String>();
        if (!StringUtil.isNullOrEmpty(requestContext.getUserName())) {
            params.add(requestContext.getUserName()); //ユーザ名
        }
        params.add(requestContext.getScreenName()); //画面名
        params.add(requestContext.getScreenId()); //画面ID

        String completionMessageResourceId = "";
        if (!StringUtil.isNullOrEmpty(buttonId)) {

            // 画面ボタンマスタの取得
            MstrScreenButton dataMstrScreenButton = mstrScreenButtonDao
                    .getMstrScreenButton(requestContext.getScreenId(), systemFormBean.getButtonId());
            if (dataMstrScreenButton != null) {
                // ボタン操作内容
                params.add(dataMstrScreenButton.getOperation_content());

                // ボタンID
                params.add(systemFormBean.getButtonId());

                // 完了メッセージNo
                completionMessageResourceId = dataMstrScreenButton.getFinish_message_no();
            }
        }

        params.add(ResourcesHandler.getString(requestContext.getOperationResourceKey(), gnomesSessionBean.getUserLocale())); //操作内容
        params.add(requestContext.getCommandId());

		/**
         * YJP/浜本 Update 2021/02/10
         *      Bachからのログは出力しないように修正する
         */
        String userName = requestContext.getUserName();
        Boolean isLogging = true;
        if (!StringUtil.isNullOrEmpty(userName) && userName.equals(CommonConstants.USERNAME_BATCH)) {
            isLogging = false;
        }

        //ログ出力
        String logStr = null;

		if (isLogging) {

			if (!StringUtil.isNullOrEmpty(completionMessageResourceId)) {
				logStr = "[Operation]" + MessagesHandler.getString(completionMessageResourceId);
				params = new ArrayList<String>();
			} else {
				if (!StringUtil.isNullOrEmpty(userName)) {
					if (params.size() == 5) {
						// {0}さんのリクエストにより{1}({2})、{3}({4})処理を開始完了しました。
						completionMessageResourceId = GnomesMessagesConstants.MV01_0029;
					} else {
						// {0}さんのリクエストにより{1}({2})、{3}({4})より{5}({6})処理を開始完了しました。
						completionMessageResourceId = GnomesMessagesConstants.MV01_0030;
					}
				} else {
					if (params.size() == 4) {
						// {0}({1})、{2}({3})処理を開始完了しました。
						completionMessageResourceId = GnomesMessagesConstants.MV01_0035;
					} else {
						// {0}({1})、{2}({3})より{4}({5})処理を開始完了しました。
						completionMessageResourceId = GnomesMessagesConstants.MV01_0037;
					}
				}
				logStr = "[Operation]" + MessagesHandler.getStringParamList(completionMessageResourceId, params);
			}

			// 完了メッセージダイアログを画面表示するかどうか判定する
			Class<?> intrfc = IScreenPrivilegeBean.class;
			// インターフェースを実装したクラスであるかどうかをチェック
			if (!clazz.isInterface() && intrfc.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {

				// messageInfoが設定されている、かつボタンIDが設定されている場合
				if ((requestContext.getMessageInfoList() != null && requestContext.getMessageInfoList().size() > 0)
						&& !StringUtil.isNullOrEmpty(buttonId)) {

					// 画面表示フラグがTrueのメッセージが存在する場合、完了メッセージを設定しない。
					for (MessageInfo messageInfo : requestContext.getMessageInfoList()) {
						if (messageInfo.getIsDispFlg()) {
							return;
						}
					}

					// パーツ権限結果情報リスト
					List<PartsPrivilegeResultInfo> partsPrivilegeList = ((IScreenPrivilegeBean) formBean)
							.getPartsPrivilegeResultInfo();
					// ボタンの完了メッセージが設定されている場合
					if (!Objects.isNull(partsPrivilegeList)) {
						for (PartsPrivilegeResultInfo partsPrivilege : partsPrivilegeList) {
							if (buttonId.equals(partsPrivilege.getButtonId())) {
								// 押下されたボタンの完了ダイアログの表示有無が表示有りの場合
								if (partsPrivilege
										.getDisplayFinishFlag() == PrivilegeDisplayFinishFlag.PrivilegeDisplayFinishFlag_ON) {
									// 画面に出力するメッセージを設定
									requestContext.addMessageInfo(completionMessageResourceId, params);
									this.logHelper.fine(this.logger, null, null, logStr);
									return;
								}
								break;
							}
						}
					}
				}
			}
		}

        // 画面に出力しないメッセージを設定
        requestContext.addMessageInfoForLogging(completionMessageResourceId, params, false);

		if (isLogging && (!StringUtil.isNullOrEmpty(logStr))) {
			this.logHelper.info(this.logger, null, null, logStr);
		}
    }

    /**
     * ページトークンのチェック、再生成を行う
     *
     * @param tokenCheckFlg トークンチェックフラグ
     * @param tokenUpdateFlg 更新の設定（するしないはBean定義)
     * @param savedPageToken FunctionBeanに保存されたトークン文字
     * @return
     * @throws Exception
     */
    private boolean chkTokenRegenerate(boolean tokenCheckFlg, boolean tokenUpdateFlg, String savedPageToken)
            throws Exception {

        //チェックありの場合かつ、セッションにトークンが設定されている場合に、
        //チェック処理を行う。
        if (tokenCheckFlg == true && !StringUtil.isNullOrEmpty(savedPageToken)) {
            String pageTokenNow = requestContext.getWrapperRequest().getParameter(REQUEST_PARAM_TOKEN);

            //FunctionBeanの保持ページトークンとページに入っていたトークンが一致しない場合、エラー処理
            if (chkPageToken(pageTokenNow, savedPageToken) == false) {
                return false;
            }
        }

        if (tokenUpdateFlg) {
            //ページトークンの再生成を行う。
            String windowId = requestContext.getWrapperRequest().getParameter(REQUEST_PARAM_WINDOWID);
            gnomesSessionBean.pageTokenGenerate(windowId);
        }

        return true;
    }

    /**
     * ページトークンチェック
     * @param tokenCheckFlg トークンチェックフラグ
     * @param pageToken ページトークン
     */

    /**
     * ページトークンチェック
     * 	savedPageTokenと画面からリクエストで渡ったトークン文字をチェックする
     *
     * @param pageToken ページトークン
     * @param savedPageToken functionBeanに保存されたページトークン
     * @return boolean true:一致 false:不一致
     */
    private boolean chkPageToken(String pageToken, String savedPageToken) {

        //ページトークンが一致しない場合、エラー処理
        if (!savedPageToken.equals(pageToken)) {
            // (ME01.0002) 只今処理中につき、しばらくお待ちください...  （改行）
            //処理を中断する場合は、再表示または遷移元の画面に戻ってください。
            responseContext.setMessageInfo(GnomesMessagesConstants.MV01_0014);
            return false;
        }
        return true;
    }

    /**
     * CDI 限定子 実装クラス。
     */
    private class CommandQualifierImple extends AnnotationLiteral<ICommandQualifier> implements ICommandQualifier {

        private String value;

        public CommandQualifierImple(String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return this.value;
        }
    }

    /**
     * セッション情報を生成
     */
    public GnomesSessionBean createSessionBean() {
        gnomesSessionBean.clear();
        return gnomesSessionBean;
    }

    /**
     * セッション情報を取得
     */
    public GnomesSessionBean getSessionBean() {
        return gnomesSessionBean;
    }
    /**
     * 初期処理
     */
    @PostConstruct
    private void init() {
        this.logHelper.fine(this.logger, null, null, "@PostConstruct: " + this);
    }

    /**
     * 後処理
     */
    @PreDestroy
    private void destroy() {
        this.logHelper.fine(this.logger, null, null, "@PreDestroy: " + this);
    }

}
