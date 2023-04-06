package com.gnomes.uiservice;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gnomes.common.command.RequestParamMap;
import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.data.FileDownLoadData;
import com.gnomes.common.data.FilePDFData;
import com.gnomes.common.data.MessageData;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;

/**
 * コンテナレスポンス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/07/15 YJP/K.Gotanda              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class ContainerResponse
{

    //    private static final boolean USE_ATTRIBUTE = false;

    // 警告
    public static final String  MES_TITLE_ID_MA          = "MA";

    // 選択
    public static final String  MES_TITLE_ID_MC          = "MC";

    // 情報
    public static final String  MES_TITLE_ID_MG          = "MG";

    // エラー
    public static final String  MES_TITLE_ID_ME          = "ME";

    /** フォワードコマンド アトレビュート名 */
    public static final String  ATTR_FORWARD_CMD_NAME    = "forwardCommand";

    /** フォワードパラメータ アトレビュート名 */
    public static final String  ATTR_FORWARD_PARAMS_NAME = "forwardParameters";

    //ロガー
    @Inject
    transient Logger            logger;

    /** ログヘルパー */
    @Inject
    protected LogHelper         logHelper;

    @Inject
    ContainerRequest            requestContext;

    private boolean             redirect                 = false;

    @Inject
    ContainerSession            sessionContext;

    private String              uri;

    // リダイレクトで実行するコマンド名称
    private String              redirectCommand;

    // リダイレクトで引き渡すパラメーター
    private Map<String, Object> redirectParameters;

    /** メッセージタイトル */
    private String              messageTitle;

    /** メッセージ */
    private String              message;

    /** メッセージコマンド */
    private String              messageCommand;

    /** フォワードコマンド */
    private String              forwardCommand           = null;

    /** フォワード */
    private Map<String, Object> forwardParameters;

    // executeLogicのエラー有無
    private boolean             isLogicError;

    /**
     * キャンセルボタンOnclick
     */
    private String              messageCancelOnClick;

    /**
     * OKボタンOnclick
     */
    private String              messageOkOnClick;

    /**
     * 親メッセージ
     */
    private MessageData         tmpOwnerMessageData;

    /**
     * 子メッセージ
     */
    private List<MessageData>   tmpChildMessageDatas;


    /** ダウンロードファイル */
    private List<FileDownLoadData> fileDownLoadDatas = null;

    /** PDFファイル */
    private FilePDFData            filePDFData       = null;

    /** 画面表示したいかどうか（PDFは強制画面表示）*/
    private boolean                isDisplayFile     = false;

    /** ダウンロードするドキュメントの拡張子
     * isDisplayFile=trueとセットになる
     * "JPG" "XLS" "PNG" "TXT"など
     */
    private String                 documentSuffixString;

    /** ドキュメントコンテクスト識別子 */
    private String                 documentContext;

    /** サーブレットレスポンス応答 **/
    private int                    httpStatus       = HttpServletResponse.SC_OK;

    /**
     * コンストラクタ
     */
    public ContainerResponse()
    {
    }

    /**
     * フォワード設定
     * @param command コマンド
     * @param forwardParameters コマンドパラメータ
     */
    public void setForwardAttributeCommand(String command,
            Map<String, Object> forwardParameters)
    {
        this.forwardCommand = command;
        this.forwardParameters = forwardParameters;
    }

    /**
     * フォワード
     * @param request リクエスト情報
     * @param response レスポンス情報
     * @throws Exception 例外
     */
    public void forward(HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {

        //URL直接入力で無効なリクエストが処理されら場合は
        //何もせずリターンする
        if(response.getStatus() == HttpServletResponse.SC_NO_CONTENT){
            return;
        }

        if (redirect) {

            sessionContext.setRedirect(requestContext.getUri(), redirectCommand,
                    redirectParameters, requestContext.getTraceMonitor());

            response.sendRedirect(response.encodeRedirectURL(uri));
            return;
        }

        if (forwardCommand != null) {
            this.logHelper.fine(this.logger, null, null,
                    "ContainerResponse.forward(): redirect");

            request.setAttribute(ATTR_FORWARD_CMD_NAME, forwardCommand);
            request.setAttribute(ATTR_FORWARD_PARAMS_NAME, forwardParameters);

            // エラー時の無限ループ対応
            forwardCommand = null;
            uri = null;

            RequestDispatcher dispatcher = request
                    .getRequestDispatcher("/gnomes");
            dispatcher.forward(request, response);

            //                ServletContext servletContext = CDI.current()
            //                        .select(ServletContext.class).get();
            //                servletContext.getRequestDispatcher("/GNOMES_PROTOTYPE/servlet").forward(request, response);
        }
        else {
            //URLが設定されていないかチェック
            if (uri == null || uri.length() == 0) {
                this.logHelper.fine(this.logger, null, null,
                        "forward uri is null or not set.");
                throw new GnomesException(
                        "uri is not set. Have you forgotten the default forwarding destination of the bean definition?");
            }

            this.logHelper.fine(this.logger, null, null,
                    "ContainerResponse.forward(): forward");
            //requestContext.clear();
            ServletContext servletContext = CDI.current()
                    .select(ServletContext.class).get();

            servletContext.getRequestDispatcher(uri).forward(request, response);
        }

    }

    /**
     * リダイレクト設定
     * @param uri URI
     * @param redirectCommand コマンド
     */
    public void setRedirect(String uri, String redirectCommand)
    {
        this.redirect = true;
        this.uri = uri;
        this.redirectCommand = redirectCommand;
    }

    /**
     * リダイレクト設定
     * @param uri URI
     * @param redirectCommand コマンド
     * @param redirectParameters パラメータ
     */
    public void setRedirect(String uri, String redirectCommand,
            Map<String, Object> redirectParameters)
    {
        this.redirect = true;
        this.uri = uri;
        this.redirectCommand = redirectCommand;
        this.redirectParameters = redirectParameters;
    }

    /**
     * フォワード設定
     * @param uri URI
     */
    public void setForward(String uri)
    {
        this.redirect = false;
        this.uri = uri;
        this.forwardCommand = null;
    }

    /**
     * リダイレクト判定
     * @return 判定結果
     */
    public boolean isRedirect()
    {
        return this.redirect;
    }

    /**
     * URI取得
     * @return URI
     */
    public String getUri()
    {
        return this.uri;
    }

    /**
     * コマンド取得
     * @return リダイレクトコマンド
     */
    public String getCommand()
    {
        return redirectCommand;
    }

    /**
     * リダイレクトパラメータ取得
     * @return リダイレクトパラメータ
     */
    public Map<String, Object> getRedirectParameters()
    {
        return this.redirectParameters;
    }

    /**
     * メッセージタイトル取得（使用しないこと）
     * @return メッセージタイトル
     */
    public String getMessageTitle()
    {
        return messageTitle;
    }

    /**
     * メッセージタイトル設定（使用しないこと）
     * @param messageTitle メッセージタイトル
     */
    public void setMessageTitle(String messageTitle)
    {
        this.messageTitle = messageTitle;
    }

    /**
     * メッセージ取得（使用しないこと）
     * @return メッセージ
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * メッセージ設定（使用しないこと）
     * @param message メッセージ
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * メッセージコマンド取得
     * @return メッセージコマンド
     */
    public String getMessageCommand()
    {
        return messageCommand;
    }

    /**
     * メッセージコマンド設定
     * @param messageCommand メッセージコマンド
     */
    public void setMessageCommand(String messageCommand)
    {
        this.messageCommand = messageCommand;
    }

    /**
     * @return messageCancelOnClick
     */
    public String getMessageCancelOnClick()
    {
        return messageCancelOnClick;
    }

    /**
     * @param messageCancelOnClick セットする messageCancelOnClick
     */
    public void setMessageCancelOnClick(String messageCancelOnClick)
    {
        this.messageCancelOnClick = messageCancelOnClick;
    }

    /**
     * @return messageOkOnClick
     */
    public String getMessageOkOnClick()
    {
        return messageOkOnClick;
    }

    /**
     * @param messageOkOnClick セットする messageOkOnClick
     */
    public void setMessageOkOnClick(String messageOkOnClick)
    {
        this.messageOkOnClick = messageOkOnClick;
    }

    /**
     * メッセージ設定（使用しない）
     * @param id メッセージID
     * @param params 置き換えパラメータ
     */
    public void setMessageResouce(String id, Object... params)
    {
        String mes = MessagesHandler.getString(id, params);
        this.message = mes;

        setMessageTitleFromId(id);
    }

    /**
     * メッセージ設定
     * @param messageNo メッセージNo
     */
    public void setMessageInfo(String messageNo)
    {
        MessagesHandler.setMessageNo(this.requestContext, messageNo);
    }

    /**
     * メッセージ設定
     * @param messageNo メッセージNo
     * @param params 置き換えパラメータ
     */
    public void setMessageInfo(String messageNo, Object... params)
    {
        MessagesHandler.setMessageNo(this.requestContext, messageNo, params);
    }

    /**
     * メッセージ設定
     * @param mesOwner 親メッセージ
     * @param mesChilds 子メッセージ
     */
    public void setMessage(MessageData mesOwner, MessageData[] mesChilds)
    {
        MessagesHandler.setMessage(this.requestContext, mesOwner, mesChilds);
    }

    /**
     * メッセージ設定
     * @param mesOwner 親メッセージ
     */
    public void setMessage(MessageData mesOwner)
    {
        tmpOwnerMessageData = mesOwner;
    }

    /**
     * 子メッセージ追加
     * @param mesChild
     */
    public void addMessageChild(MessageData mesChild)
    {
        if (tmpChildMessageDatas == null) {
            tmpChildMessageDatas = new ArrayList<>();
        }
        tmpChildMessageDatas.add(mesChild);
    }

    /**
     * メッセージ書き込み
     */
    public void writeMessage()
    {

        MessageData[] childs = null;
        if (tmpChildMessageDatas != null) {
            childs = (MessageData[]) tmpChildMessageDatas
                    .toArray(new MessageData[0]);
        }

        tmpOwnerMessageData.setMessageCommand(this.messageCommand);
        tmpOwnerMessageData.setMessageCancelOnClick(this.messageCancelOnClick);
        tmpOwnerMessageData.setMessageOkOnClick(this.messageOkOnClick);

        setMessage(tmpOwnerMessageData, childs);

        // 書き込み後はクリア
        clearMessage();
    }

    /**
     * メッセージクリア
     */
    public void clearMessage()
    {

        tmpOwnerMessageData = null;

        if (tmpChildMessageDatas != null) {
            tmpChildMessageDatas.clear();
        }

        messageCancelOnClick = null;
        messageCommand = null;
        messageOkOnClick = null;
    }

    /**
     * メッセージタイトル設定（使用しないこと)
     * @param id メッセージID
     */
    public void setMessageTitleFromId(String id)
    {
        switch (id.substring(0, 2)) {
            case MES_TITLE_ID_MA :
                this.messageTitle = ResourcesHandler.getString("YY01.0004"); //"警告";
                break;
            case MES_TITLE_ID_MC :
                this.messageTitle = ResourcesHandler.getString("YY01.0005"); //"選択";
                break;
            case MES_TITLE_ID_MG :
                this.messageTitle = ResourcesHandler.getString("YY01.0006"); //"情報";
                break;
            case MES_TITLE_ID_ME :
                this.messageTitle = ResourcesHandler.getString("YY01.0007"); //"エラー";
                break;
        }
    }



    /**
     * @return fileDownLoadDatas
     */
    public List<FileDownLoadData> getFileDownLoadDatas()
    {
        return fileDownLoadDatas;
    }

    /**
     * @param fileDownLoadDatas セットする fileDownLoadDatas
     */
    public void setFileDownLoadDatas(List<FileDownLoadData> fileDownLoadDatas)
    {
        this.fileDownLoadDatas = fileDownLoadDatas;
    }

    /**
     * @return filePDFData
     */
    public FilePDFData getFilePDFData()
    {
        return filePDFData;
    }

    /**
     * @param filePDFData セットする filePDFData
     */
    public void setFilePDFData(FilePDFData filePDFData)
    {
        this.filePDFData = filePDFData;
    }

    /**
     * ダウンロード対象は表示したいものかどうかを取得
     * @return false:表示しない true:表示する
     */
    public boolean getIsDisplayFile()
    {
        return this.isDisplayFile;
    }

    /**
     * ダウンロード対象は表示したいものかどうかを設定する
     * @param isDisplayFile false:表示しない true:表示する
     */
    public void setIsDisplayFile(boolean isDisplayFile )
    {
        this.isDisplayFile = isDisplayFile;
    }

    /**
     * ドキュメントの拡張子文字を取得
     *
     * @return documentSuffixString
     */
    public String getDocumentSuffixString()
    {
        return documentSuffixString;
    }

    /**
     * ドキュメントの拡張子文字をセットする
     *
     * @param documentSuffixString セットする documentSuffixString
     */
    public void setDocumentSuffixString(String documentSuffixString)
    {
        this.documentSuffixString = documentSuffixString;
    }

    /**
     * ドキュメントコンテクストの文字を取得する
     * @return documentContext
     */
    public String getDocumentContext()
    {
        return documentContext;
    }

    /**
     * ドキュメントコンテクストの文字を設定する
     * @param documentContext セットする documentContext
     */
    public void setDocumentContext(String documentContext)
    {
        this.documentContext = documentContext;
    }

    /**
     * @return isLogicError
     */
    public boolean isLogicError()
    {
        return isLogicError;
    }

    /**
     * @param isLogicError セットする isLogicError
     */
    public void setLogicError(boolean isLogicError)
    {
        this.isLogicError = isLogicError;
    }

    /**
     * HTTPレスポンスステータスを取得する
     * @return
     */
    public int getHttpStatus() {
        return httpStatus;
    }

    /**
     * HTTPレスポンスステータスを設定する
     * @param httpStatus    ステータス値 デフォルト（HttpServletResponse.SC_OK)
     */
    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }


    /**
     * レスポンスデータを取得
     *
     * @param bean 取得元ビーン
     * @param feildName 取得フィールド名
     * @return Object (Stringの場合はリクエスト情報)
     *
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws IOException
     * @throws GnomesAppException
     */
    public Object getResponseFormBean(Object bean, String feildName,
            String requestParamName) throws GnomesAppException,
            IllegalArgumentException, IllegalAccessException, IOException
    {

        Object value = null;

        Field f = null;
        for (Class<?> clazz = bean
                .getClass(); clazz != Object.class; clazz = clazz
                        .getSuperclass()) {
            try {
                f = clazz.getDeclaredField(feildName);
                f.setAccessible(true);
                break;
            }
            catch (NoSuchFieldException | SecurityException ex) {
                // 継承元も検索するため
            }
        }
        if (f == null) {
            // ME01.0154：「ビーンプロパティ取得でエラーが発生しました。詳細についてはログを確認してください。取得クラス：{0} 、取得プロパティ：{1}」
            GnomesAppException gae = new GnomesAppException(null,
                    GnomesMessagesConstants.ME01_0154,
                    bean.getClass().getName(), feildName);
            throw gae;

        }

        // 処理が正常終了しなかった場合（入力チェックエラーや業務エラーなどあり）
        if (this.isLogicError) {
            /*
            // RequestParamMapがあるフィールドの場合、リクエスト情報を取得する
            RequestParamMap map = f.getAnnotation(RequestParamMap.class);
            if (map != null) {
                // リクエストから取得
                String requestParamName = map.value();
                if (requestParamName.length() == 0) {
                    requestParamName = feildName;
                }
                value = requestContext.getRequestParamValueWithWapper(requestParamName);
            }
            */
            if (requestParamName != null && requestParamName.length() > 0) {
                value = requestContext
                        .getRequestParamValueWithWapper(requestParamName);
            }
        }

        // リクエスト情報に存在しない場合
        // または処理が正常終了（入力チェックエラーや業務エラーなどない場合）
        if (this.isLogicError == false || value == null) {

            // FormBeanから取得
            value = f.get(bean);
        }
        return value;
    }

    /**
     * レスポンスデータ配列を取得
     *
     * @param bean 取得元ビーン
     * @param feildName 取得フィールド名
     * @return Object (Stringの場合はリクエスト情報)
     *
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws IOException
     * @throws GnomesAppException
     */
    public Object getResponsesFormBean(Object bean, String feildName,
            String requestParamName) throws GnomesAppException,
            IllegalArgumentException, IllegalAccessException, IOException
    {

        Object value = null;

        Field f = null;
        for (Class<?> clazz = bean
                .getClass(); clazz != Object.class; clazz = clazz
                        .getSuperclass()) {
            try {
                f = clazz.getDeclaredField(feildName);
                f.setAccessible(true);
                break;
            }
            catch (NoSuchFieldException | SecurityException ex) {
                // 継承元も検索するため
            }
        }
        if (f == null) {
            // ME01.0154：「ビーンプロパティ取得でエラーが発生しました。詳細についてはログを確認してください。取得クラス：{0} 、取得プロパティ：{1}」
            GnomesAppException gae = new GnomesAppException(null,
                    GnomesMessagesConstants.ME01_0154,
                    bean.getClass().getName(), feildName);
            throw gae;

        }

        // 処理が正常終了しなかった場合（入力チェックエラーや業務エラーなどあり）
        if (this.isLogicError) {
            if (requestParamName != null && requestParamName.length() > 0) {
                // リクエスト情報を取得する
                value = (String[]) requestContext
                        .getRequestParamValuesWithWapper(requestParamName);
            }
        }

        // リクエスト情報に存在しない場合
        // または処理が正常終了（入力チェックエラーや業務エラーなどない場合）
        if (this.isLogicError == false || value == null) {

            // FormBeanから取得
            value = f.get(bean);
        }
        return value;
    }

    /**
     * レスポンスデータを取得
     *
     * @param bean 取得元ビーン
     * @param feildName 取得フィールド名
     * @return Object (Stringの場合はリクエスト情報)
     *
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws IOException
     * @throws GnomesAppException
     */
    public Object getResponseFormBean(Object bean, String feildName)
            throws GnomesAppException, IllegalArgumentException,
            IllegalAccessException, IOException
    {

        Object value = null;

        Field f = null;
        for (Class<?> clazz = bean
                .getClass(); clazz != Object.class; clazz = clazz
                        .getSuperclass()) {
            try {
                f = clazz.getDeclaredField(feildName);
                f.setAccessible(true);
                break;
            }
            catch (NoSuchFieldException | SecurityException ex) {
                // 継承元も検索するため
            }
        }
        if (f == null) {
            // ME01.0154：「ビーンプロパティ取得でエラーが発生しました。詳細についてはログを確認してください。取得クラス：{0} 、取得プロパティ：{1}」
            GnomesAppException gae = new GnomesAppException(null,
                    GnomesMessagesConstants.ME01_0154,
                    bean.getClass().getName(), feildName);
            throw gae;

        }

        // 処理が正常終了しなかった場合（入力チェックエラーや業務エラーなどあり）
        if (this.isLogicError) {
            // RequestParamMapがあるフィールドの場合、リクエスト情報を取得する
            RequestParamMap map = f.getAnnotation(RequestParamMap.class);
            if (map != null) {
                // リクエストから取得
                String requestParamName = map.value();
                if (requestParamName.length() == 0) {
                    requestParamName = feildName;
                }
                value = requestContext
                        .getRequestParamValueWithWapper(requestParamName);
            }
        }

        // リクエスト情報に存在しない場合
        // または処理が正常終了（入力チェックエラーや業務エラーなどない場合）
        if (this.isLogicError == false || value == null) {

            // FormBeanから取得
            value = f.get(bean);
        }
        return value;
    }

    /**
     * レスポンスデータを取得
     *
     * @param bean 取得元ビーン
     * @param feildName 取得フィールド名
     * @param rowIndex 行Index
     * @param requestParamName リクエストパラメータ名
     * @return Object (Stringの場合はリクエスト情報)
     *
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws IOException
     * @throws GnomesAppException
     */
    public Object getResponseFormBean(Object bean, String feildName,
            int rowIndex, String requestParamName) throws GnomesAppException,
            IllegalArgumentException, IllegalAccessException, IOException
    {

        Object[] value = null;
        Object result = null;

        Field f = null;
        for (Class<?> clazz = bean
                .getClass(); clazz != Object.class; clazz = clazz
                        .getSuperclass()) {
            try {
                f = clazz.getDeclaredField(feildName);
                f.setAccessible(true);
                break;
            }
            catch (NoSuchFieldException | SecurityException ex) {
                // 継承元も検索するため
            }
        }
        if (f == null) {
            // ME01.0154：「ビーンプロパティ取得でエラーが発生しました。詳細についてはログを確認してください。取得クラス：{0} 、取得プロパティ：{1}」
            GnomesAppException gae = new GnomesAppException(null,
                    GnomesMessagesConstants.ME01_0154,
                    bean.getClass().getName(), feildName);
            throw gae;

        }

        // 処理が正常終了しなかった場合（入力チェックエラーや業務エラーなどあり）
        if (this.isLogicError) {
            // リクエスト情報を取得する
            value = (Object[]) requestContext
                    .getRequestParamValuesWithWapper(requestParamName);
            if (value != null && value.length > rowIndex) {
                result = value[rowIndex];
            }
        }

        // リクエスト情報に存在しない場合
        // または処理が正常終了（入力チェックエラーや業務エラーなどない場合）
        if (this.isLogicError == false || result == null) {

            // FormBeanから取得
            result = f.get(bean);
        }
        return result;
    }

    /**
     * レスポンスデータを取得
     *
     * @param bean 取得元ビーン
     * @param feildName 取得フィールド名
     * @param rowIndex 行Index
     * @param requestParamName リクエストパラメータ名
     * @return Object (Stringの場合はリクエスト情報)
     *
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws IOException
     * @throws GnomesAppException
     */
    public Object getResponseCheckBoxFormBean(Object bean, String feildName,
            int rowIndex, String requestParamName) throws GnomesAppException,
            IllegalArgumentException, IllegalAccessException, IOException
    {

        String[] value = null;
        Object result = null;

        Field f = null;
        for (Class<?> clazz = bean
                .getClass(); clazz != Object.class; clazz = clazz
                        .getSuperclass()) {
            try {
                f = clazz.getDeclaredField(feildName);
                f.setAccessible(true);
                break;
            }
            catch (NoSuchFieldException | SecurityException ex) {
                // 継承元も検索するため
            }
        }
        if (f == null) {
            // ME01.0154：「ビーンプロパティ取得でエラーが発生しました。詳細についてはログを確認してください。取得クラス：{0} 、取得プロパティ：{1}」
            GnomesAppException gae = new GnomesAppException(null,
                    GnomesMessagesConstants.ME01_0154,
                    bean.getClass().getName(), feildName);
            throw gae;

        }

        // 処理が正常終了しなかった場合（入力チェックエラーや業務エラーなどあり）
        if (this.isLogicError) {
            // リクエスト情報を取得する
            value = (String[]) requestContext
                    .getRequestParamValuesWithWapper(requestParamName);
            if (value != null) {
                String strRowIndex = String.valueOf(rowIndex);
                for (int i = 0; i < value.length; i++) {
                    if (strRowIndex.equals(value[i])) {
                        return CommonConstants.DTO_CHECK_BOX_CHECKED_VALUE;
                    }
                }
                return CommonConstants.DTO_CHECK_BOX_NO_CHECKED_VALUE;
            }
        }

        // リクエスト情報に存在しない場合
        // または処理が正常終了（入力チェックエラーや業務エラーなどない場合）
        if (this.isLogicError == false || value == null) {

            // FormBeanから取得
            result = f.get(bean);
        }
        return result;
    }
}
