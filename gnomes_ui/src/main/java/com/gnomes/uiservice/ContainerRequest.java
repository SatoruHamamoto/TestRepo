package com.gnomes.uiservice;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceUnit;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.picketbox.util.StringUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums;
import com.gnomes.common.constants.CommonEnums.IsLogging;
import com.gnomes.common.constants.CommonEnums.MailNoticeStatus;
import com.gnomes.common.constants.CommonEnums.MessageCategory;
import com.gnomes.common.constants.CommonEnums.MessageHistoryRec;
import com.gnomes.common.constants.CommonEnums.TalendJobKind;
import com.gnomes.common.constants.CommonEnums.TalendParameterName;
import com.gnomes.common.constants.GnomesQueryConstants;
import com.gnomes.common.data.AuditTrailInfo;
import com.gnomes.common.data.GnomesEjbBean;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.data.GnomesSystemBean;
import com.gnomes.common.data.MessageData;
import com.gnomes.common.data.MessageInfo;
import com.gnomes.common.data.TalendJobInfoBean;
import com.gnomes.common.dto.CountDto;
import com.gnomes.common.entity.BaseEntity;
import com.gnomes.common.entity.IContainerRequest;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.ptlamp.data.PatlampData;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.search.data.SearchSetting;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.CurrentTimeStamp;
import com.gnomes.common.util.StringUtils;
import com.gnomes.common.util.TalendJobRun;
import com.gnomes.common.view.SystemFormBean;
import com.gnomes.system.dao.MstrLinkDao;
import com.gnomes.system.dao.MstrMessageDefineDao;
import com.gnomes.system.dao.MstrPatlampDao;
import com.gnomes.system.dao.MstrScreenButtonDao;
import com.gnomes.system.entity.Message;
import com.gnomes.system.entity.MstrLink;
import com.gnomes.system.entity.MstrMessageDefine;
import com.gnomes.system.entity.MstrPatlamp;
import com.gnomes.system.entity.MstrScreenButton;
import com.gnomes.system.entity.TmpQueueMailNotice;

/**
 * コンテナリクエスト
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/07/15 YJP/K.Gotanda              初版
 * R0.01.02 2018/08/08 YJP/S.Hosokawa             イベント発生日時追加
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class ContainerRequest implements IContainerRequest, Serializable
{
    /** トレースモニター 日時フォーマット */
    private static final String                TRACE_MONITOR_DATE_FORMAT           = "yyyy-MM-dd HH:mm:ss,SSS";

    /** 検索条件マップのリクエスト名 */
    public static final String                 REQUEST_NAME_SEARCH_SETTING_MAP     = "searchSettingMap";

    /** ウィンドウIDのリクエスト名 */
    public static final String                 REQUEST_NAME_WINDOW_ID              = "windowId";

    /** 動的画面IDのリクエスト名 */
    public static final String                 REQUEST_NAME_DYNAMIC_SCREEN_ID      = "dynamicScreenId";

    /** 動的画面名IDのリクエスト名 */
    public static final String                 REQUEST_NAME_DYNAMIC_SCREEN_NAME_ID = "dynamicScreenNameId";

    /** 動的タイトルIDのリクエスト名 */
    public static final String                 REQUEST_NAME_DYNAMIC_TITLE_ID       = "dynamicTitleId";

    /** 例外時のメッセージ文字が取れないときのメッセージ*/
    public static final String                 SEVIRE_STR_ILLIGAL_MSG_GET          = "An application error has occurred. Check log for details";

    /** 発生元ソース  メッセージの発生元ソース（クラス名#メソッド名) */
    public static final String                 FORMAT_MSG_SOURCE                   = "%s#%s";

	/** コンテンツパラメータ（IPアドレス） */
	private static final String                CONTENT_IP_ADDRESS                  = "ip_address=";

    /** ロガー */
    @Inject
    transient Logger                           logger;

    /** ログヘルパー */
    @Inject
    protected LogHelper                        logHelper;

    /** メッセージ定義マスタDao */
    @Inject
    protected MstrMessageDefineDao             mstrMessageDefineDao;

    /** リンク情報Dao */
    @Inject
    protected MstrLinkDao                      mstrLinkDao;

	/** パトランプマスタDao */
	@Inject
	protected MstrPatlampDao                   mstrPatlampDao;

    // スタートの URI >>> ログに利用
    /** URI */
    private String                             uri                                 = null;

    /** リクエスト */
    private transient ContainerRequest.REQUEST request                             = null;

    /** トレースモニター */
    private transient List<Object>             traceMonitorList                    = null;

    /** フォワードコマンド */
    private String                             forwardCommand                      = null;

    /** フォワードパラメータ */
    private transient Map<String, Object>      forwardParameters;

    /** セッション Baen */
    @Inject
    protected GnomesSessionBean                gnomesSessionBean;

    /** セッション Baen(Ejb使用時) */
    protected GnomesSessionBean                gnomesSessionBeanEjb                = null;

    /** アプリケーション Baen */
    @Inject
    protected GnomesSystemBean                 gnomesSystemBean;

    /** 画面共通項目用 フォームビーン */
    @Inject
    protected SystemFormBean                   systemFormBean;

    /** 画面ボタンマスタ Dao */
    @Inject
    protected MstrScreenButtonDao              mstrScreenButtonDao;

    /** Talendジョブ情報Bean */
    @Inject
    protected TalendJobInfoBean                talendJobInfoBean;

    /** 非同期処理 */
    @Inject
    protected AsynchronousProcess              asynchronousProcess;

    /** GnomesExceptionファクトリ */
    @Inject
    protected GnomesExceptionFactory           exceptionFactory;

    /**
     *  EJBバッチ用ビーン
     */
    @Inject
    protected GnomesEjbBean                    ejbBean;

    /** ユーザKey */
    private String                             userKey;

    /** ユーザID */
    private String                             userId;

    /** ユーザ名 */
    private String                             userName;

    /** ユーザロケール */
    private Locale                             userLocale;

    /** 言語 */
    private String                             language;

    /** IPアドレス */
    private String                             ipAddress;

    /** コンピュータ名 */
    private String                             computerName;

    /** エリアID */
    private String                             areaId;

    /** エリア名 */
    private String                             areaName;

    /** 拠点コード */
    private String                             siteCode;

    /** 拠点名 */
    private String                             siteName;

    /** システムロケールID */
    private Locale                             systemLocale                        = Locale.getDefault();

    /** コマンドID */
    private String                             commandId;

    /** 操作内容リソースキー */
    private String                             operationResourceKey;

    /** 画面ID */
    private String                             screenId;

    /** オリジナル（定義上）画面ID */
    private String                             orgScreenId;

    /** 画面名 */
    private String                             screenName;

    /** イベントID */
    private String                             eventId                             = UUID.randomUUID().toString();

    /** イベント発生日時 */
    private Date                               eventOccurDate                      = new Date();

    /** コマンドが実行するBLクラス名 */
    private String                             businessClassName;

    /** 監視機能用Key */
    private String                             watcherSearchKey;

    /** メッセージ情報リスト */
    private transient List<MessageInfo>        messageInfoList                     = new ArrayList<>();

    /** リンク情報 */
    private String[]                           linkInfo;

    /** Exceptionがスローされたか否か */
    private Boolean                            isThrowException                    = false;

    /** 監査証跡情報リスト */
    private transient List<AuditTrailInfo>     auditTrailInfoList                  = new ArrayList<>();

    /** トレースクラス名 */
    private List<String>                       traceClazzNameList                  = new ArrayList<>();

    /** トレースメソッド名 */
    private List<String>                       traceMethodNameList                 = new ArrayList<>();

    /** ログ出力用（通常領域固定） */
    @PersistenceUnit(unitName = CommonConstants.PERSISTENCE_UNIT_NAME_NON_JTA)
    private transient EntityManagerFactory     emf;

    /** ラッパーリクエスト */
    private transient HttpServletRequest       wrapperRequest;

    /**
     * サービスのリクエスト
     * MultipartFormDataInput または、BaseServiceRequest
     */
    private transient Object                   serviceRequest;

    /** リクエスト時のパラメータ変換エラー情報 */
    private Map<String, String[]>              requestParamMapErr                  = new HashMap<String, String[]>();

    /** ドメイン参照時のバリデーションチェックエラー情報 */
    private Map<String, Map<String, String[]>> validationDomainError               = new LinkedHashMap<String, Map<String, String[]>>();

    /** コマンド処理準備時のエラー情報 */
    private String commandValidErrorInfo = null;

    /**
     * ドメイン参照時のバリデーションチェックエラー情報を取得
     * @return validationDomainError
     */
    public Map<String, Map<String, String[]>> getValidationDomainError()
    {
        return validationDomainError;
    }


    /**
     * ドメイン参照時のバリデーションチェックエラー情報を設定
     * @param validationDomainError
     */
    public void setValidationDomainError(Map<String, Map<String, String[]>> validationDomainError)
    {
        this.validationDomainError = validationDomainError;
    }
    /**
     * ドメイン参照時のバリデーションチェックエラー情報を追加
     * @param key
     * @param value
     */
    public void addValidationDomainError(String key, Map<String, String[]> value)
    {
        this.validationDomainError.put(key, value);
    }

    /**
     * @return commandValidErrorInfo
     */
    public String getCommandValidErrorInfo()
    {
        return commandValidErrorInfo;
    }


    /**
     * @param commandValidErrorInfo セットする commandValidErrorInfo
     */
    public void setCommandValidErrorInfo(String commandValidErrorInfo)
    {
        this.commandValidErrorInfo = commandValidErrorInfo;
    }


    /**
     * ユーザKeyを取得
     * @return userKey
     */
    public String getUserKey()
    {
        return userKey;
    }

    /**
     * ユーザKeyを設定
     * @param userKey ユーザKey
     */
    public void setUserKey(String userKey)
    {
        this.userKey = userKey;
    }

    /**
     * ユーザIDを取得
     * @return userId
     */
    public String getUserId()
    {
        return userId;
    }

    /**
     * ユーザIDを設定
     * @param userId ユーザID
     */
    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    /**
     * ユーザ名を取得
     * @return userName
     */
    public String getUserName()
    {
        return userName;
    }

    /**
     * ユーザ名を設定
     * @param userName ユーザ名
     */
    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    /**
     * ユーザロケールを取得
     * @return locale
     */
    public Locale getUserLocale()
    {

        if (this.userLocale != null) {
            return this.userLocale;
        }
        else {
            return Locale.getDefault();
        }
    }

    /**
     * ユーザロケールを設定
     * @param userLocale ユーザロケール
     */
    public void setUserLocale(Locale userLocale)
    {
        this.userLocale = userLocale;
    }

    /**
     * 言語を取得
     * @return language
     */
    public String getLanguage()
    {
        if (language == null || language.isEmpty()) {
            return Locale.getDefault().toString();
        }
        else {
            return language;
        }
    }

    /**
     * 言語を設定
     * @param language 言語
     */
    public void setLanguage(String language)
    {
        this.language = language;
    }

    /**
     * IPアドレスを取得
     * @return ipAddress
     */
    public String getIpAddress()
    {
        return ipAddress;
    }

    /**
     * IPアドレスを設定
     * @param ipAddress IPアドレス
     */
    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    /**
     * コンピュータ名を取得
     * @return computerName
     */
    public String getComputerName()
    {
        return computerName;
    }

    /**
     * コンピュータ名を設定
     * @param computerName コンピュータ名
     */
    public void setComputerName(String computerName)
    {
        this.computerName = computerName;
    }

    /**
     * エリアIDを取得
     * @return areaId
     */
    public String getAreaId()
    {
        return areaId;
    }

    /**
     * エリアIDを設定
     * @param areaId エリアID
     */
    public void setAreaId(String areaId)
    {
        this.areaId = areaId;
    }

    /**
     * エリア名を取得
     * @return areaName
     */
    public String getAreaName()
    {
        return areaName;
    }

    /**
     * エリア名を設定
     * @param areaName エリア名
     */
    public void setAreaName(String areaName)
    {
        this.areaName = areaName;
    }

    /**
     * 拠点コードを取得
     * @return siteCode
     */
    public String getSiteCode()
    {
        return siteCode;
    }

    /**
     * 拠点コードを設定
     * @param siteCode 拠点コード
     */
    public void setSiteCode(String siteCode)
    {
        this.siteCode = siteCode;
    }

    /**
     * 拠点名を取得
     * @return siteName
     */
    public String getSiteName()
    {
        return siteName;
    }

    /**
     * 拠点名を設定
     * @param siteName 拠点名
     */
    public void setSiteName(String siteName)
    {
        this.siteName = siteName;
    }

    /**
     * システムロケールを設定
     * @return systemLocale
     */
    public Locale getLocale()
    {
        return systemLocale;
    }

    /**
     * コマンドIDを取得
     * @return commandId
     */
    public String getCommandId()
    {
        return commandId;
    }

    /**
     * コマンドIDを設定
     * @param commandId コマンドID
     */
    public void setCommandId(String commandId)
    {
        this.commandId = commandId;
    }

    /**
     * 操作内容リソースキーを取得
     * @return operationName
     */
    public String getOperationResourceKey()
    {
        return operationResourceKey;
    }

    /**
     * 操作内容リソースキーを設定
     * @param operationName 操作内容リソースキー
     */
    public void setOperationResourceKey(String operationResourceKey)
    {
        this.operationResourceKey = operationResourceKey;
    }

    /**
     * 画面IDを取得
     * @return screenId
     */
    public String getScreenId()
    {
        return screenId;
    }

    /**
     * 画面IDを設定
     * @param screenId
     */
    public void setScreenId(String screenId)
    {
        this.screenId = screenId;
    }

    /**
     * オリジナル（定義上)画面IDを取得
     * @return orgScreenId
     */
    public String getOrgScreenId()
    {
        return orgScreenId;
    }

    /**
     * オリジナル（定義上)画面IDを設定
     * @param orgScreenId
     */
    public void setOrgScreenId(String orgScreenId)
    {
        this.orgScreenId = orgScreenId;
    }

    /**
     * 画面名を取得
     * @return screenName
     */
    public String getScreenName()
    {
        return screenName;
    }

    /**
     * 画面名を設定
     * @param screenName
     */
    public void setScreenName(String screenName)
    {
        this.screenName = screenName;
    }

    /**
     * イベントIDを取得
     * @return イベントID
     */
    public String getEventId()
    {
        return eventId;
    }

    /**
     * イベントIDを設定
     * @param eventId イベントID
     */
    public void setEventId(String eventId)
    {
        this.eventId = eventId;
    }

    /**
     * イベント発生日時を取得
     * @return イベント発生日時
     */
    public Date getEventOccurDate()
    {
        return eventOccurDate;
    }

    /**
     * イベント発生日時を設定
     * @param eventId イベント発生日時
     */
    public void setEventOccurDate(Date eventOccurDate)
    {
        this.eventOccurDate = eventOccurDate;
    }

    /**
     * コマンドが実行するBLクラス名を取得
     * @return businessClassName コマンドが実行するBLクラス名
     */
    public String getBusinessClassName()
    {
        return businessClassName;
    }

    /**
     * コマンドが実行するBLクラス名を設定
     * @param businessClassName コマンドが実行するBLクラス名
     */
    public void setBusinessClassName(String businessClassName)
    {
        this.businessClassName = businessClassName;
    }

    /**
     * 監視機能用Keyを取得
     * @return watcherSearchKey 監視機能用Key
     */
    public String getWatcherSearchKey()
    {
        return watcherSearchKey;
    }

    /**
     * 監視機能用Keyを設定
     * @param watcherSearchKey 監視機能用Key
     */
    public void setWatcherSearchKey(String watcherSearchKey)
    {
        this.watcherSearchKey = watcherSearchKey;
    }

    /**
     * メッセージ情報リストを取得
     * @return messageInfoList
     */
    public List<MessageInfo> getMessageInfoList()
    {
        return messageInfoList;
    }

    /**
     * リンク情報を取得
     * @return linkInfo
     */
    public String[] getLinkInfo()
    {
        return linkInfo;
    }

    /**
     * Exceptionがスローされたか否かを取得
     * @return isThrowException
     */
    public Boolean getIsThrowException()
    {
        return isThrowException;
    }

    /**
     * Exceptionがスローされたか否か
     * @param screenName
     */
    public void setIsThrowException(Boolean isThrowException)
    {
        this.isThrowException = isThrowException;
    }

    /**
     * トレースクラス名を取得
     * @return トレースクラス名
     */
    public List<String> getTraceClazzNameList()
    {
        return traceClazzNameList;
    }

    /**
     * トレースクラス名を設定
     * @param traceClazzNameList トレースクラス名
     */
    public void setTraceClazzNameList(List<String> traceClazzNameList)
    {
        this.traceClazzNameList = traceClazzNameList;
    }

    /**
     * トレースメソッド名を取得
     * @return トレースメソッド名
     */
    public List<String> getTraceMethodNameList()
    {
        return traceMethodNameList;
    }

    /**
     * トレースメソッド名を設定
     * @param traceMethodNameList トレースメソッド名
     */
    public void setTraceMethodNameList(List<String> traceMethodNameList)
    {
        this.traceMethodNameList = traceMethodNameList;
    }

    /**
     * リクエスト時のパラメータ変換エラー情報を取得
     * @return リクエスト時のパラメータ変換エラー情報
     */
    public Map<String, String[]> getRequestParamMapErr()
    {
        return requestParamMapErr;
    }

    /**
     * リクエスト時のパラメータ変換エラー情報を設定
     * @param requestParamMapErr リクエスト時のパラメータ変換エラー情報
     */
    public void setRequestParamMapErr(Map<String, String[]> requestParamMapErr)
    {
        this.requestParamMapErr = requestParamMapErr;
    }

    /**
     * コンストラクタ
     */
    public ContainerRequest()
    {

    }

    /**
     * URIを取得
     * @return URI
     */
    public String getUri()
    {
        return uri;
    }

    /**
     * URIを設定
     * @param uri
     */
    public void setUri(String uri)
    {
        this.uri = uri;
    }

    /**
     * リクエストを取得
     * @return リクエスト
     */
    public ContainerRequest.REQUEST getRequest()
    {
        return request;
    }

    /**
     * リクエストを設定
     * @param request リクエスト
     */
    public void setRequest(ContainerRequest.REQUEST request)
    {
        this.uri = request.getBaseUri();
        this.traceMonitorList = request.getTraceMonitorList();
        this.request = request;
    }

    /**
     * フォワードコマンド、フォワードパラメータを設定
     * @param command フォワードコマンド
     * @param forwardParameters フォワードパラメータ
     */
    public void setAttributeCommand(String command, Map<String, Object> forwardParameters)
    {
        this.forwardCommand = command;
        this.forwardParameters = forwardParameters;
    }

    /**
     * リダイレクトであるかを判定
     */
    public boolean isRedirect()
    {
        return (request != null && request.isRedirect());//this.redirect;
    }

    /**
     * フォワードコマンドが設定されているか否か
     */
    public boolean isForwardAttribute()
    {
        return (forwardCommand != null);
    }

    /**
     * フォワードコマンドを取得
     * @return フォワードコマンド
     */
    public String getAttributeCommand()
    {
        //        return ((request != null) ? request.getRedirectCommand() : null);
        return forwardCommand;
    }

    /**
     * リクエストを取得
     * @return
     */
    public String getCommand()
    {
        return ((request != null) ? request.getRedirectCommand() : null);
    }

    /**
     * リダイレクトで引き渡すパラメーターを取得
     * @param name パラメータ名
     * @return
     */
    public Object getRedirectParameter(String name)
    {
        //return this.redirectParameters.get(name);
        return ((request != null) ? request.getRedirectParameters().get(name) : null);
    }

    /**
     * フォワードパラメータを取得
     * @param name パラメータ名
     * @return
     */
    public Object getAttributeParameter(String name)
    {
        //return this.redirectParameters.get(name);
        return ((forwardParameters != null) ? forwardParameters.get(name) : null);
    }

    /**
     * トレースモニターがあるかどうか
     * @return
     */
    public boolean existTraceMonitor()
    {
        return (!traceMonitorList.isEmpty());
    }

    /**
     * トレースモニターを取得
     * @return トレースモニター
     */
    public List<Object> getTraceMonitor()
    {
        return traceMonitorList;
    }

    /**
     * ボタンIDを取得する
     * @return nullの場合を考慮する
     */
    public String getButtonId()
    {
        return this.systemFormBean.getButtonId();
    }

    /**
     * アプリケーション Bean、セッション Bean に保持したユーザ情報を保持
     */
    public void setSessionInfo()
    {

        // ユーザロケールの設定
        if (gnomesSessionBean != null) {
            String localId = gnomesSessionBean.getLocaleId();
            if (localId != null) {
                String[] localeParams = localId.split(CommonConstants.SPLIT_CHAR);

                if (localeParams.length >= 2) {
                    systemLocale = new Locale(localeParams[0], localeParams[1]);
                }
            }

            // ユーザ情報をセッション Bean から取得する
            setAreaId(gnomesSessionBean.getAreaId()); // エリアID
            setAreaName(gnomesSessionBean.getAreaName()); // エリア名
            setComputerName(gnomesSessionBean.getComputerName()); // コンピュータ名
            setIpAddress(gnomesSessionBean.getIpAddress()); // IPアドレス
            setLanguage(gnomesSessionBean.getLanguage()); // 言語
            setUserLocale(gnomesSessionBean.getUserLocale()); // ユーザロケール
            setSiteCode(gnomesSessionBean.getSiteCode()); // 拠点コード
            setSiteName(gnomesSessionBean.getSiteName()); // 拠点名
            setUserId(gnomesSessionBean.getUserId()); // ユーザID
            setUserName(gnomesSessionBean.getUserName()); // ユーザ名
            setUserKey(gnomesSessionBean.getUserKey()); // ユーザKey
        }

    }

    /**
     * セッションビーン(EJB使用時)の設定
     * @return gnomesSessionBean セッションビーン(EJB使用時)
     */
    public void setGnomesSessionBeanEjb(GnomesSessionBean gnomesSessionBeanEjb)
    {
        this.gnomesSessionBeanEjb = gnomesSessionBeanEjb;
    }

    /**
     * セッションビーンの取得
     * セッションビーン(EJB使用時)が設定されている場合はEJB側を取得
     * @return gnomesSessionBean セッションビーン
     */
    public GnomesSessionBean getGnomesSessionBean()
    {
        if (gnomesSessionBeanEjb != null) {
            return gnomesSessionBeanEjb;
        }
        return gnomesSessionBean;
    }

    /**
     * トレースモニター開始の追加
     * @param prefix 接頭辞
     * @param clazzName クラス名
     * @param methodName メソッド名
     * @param args 引数文字列
     */
    public void addTraceMonitorStart(String prefix, String clazzName, String methodName, String args)
    {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(TRACE_MONITOR_DATE_FORMAT);
        String strDate = ZonedDateTime.now().format(dtf);

        // スレッド名
        String threadName = "(" + Thread.currentThread().getName() + ")";

        this.traceMonitorList.add(String.format("%s %s %s %s#%s(%s)", strDate, prefix, threadName, clazzName,
                methodName, args));

        if (CommonEnums.TraceMonitorWrite.AnyTime.getValue()) {
            StringBuilder trace = new StringBuilder(128);
            trace.append("[TRACE MESSAGE] ");
            trace.append(String.format("%s#%s(%s)", clazzName, methodName, args));
            this.logHelper.fine(this.logger, null, null, trace.toString());
            this.traceMonitorList.clear();

        }

        // トレースクラス名
        this.traceClazzNameList.add(clazzName);
        // トレースメソッド名
        this.traceMethodNameList.add(methodName);

    }

    /**
     * トレースモニター終了の追加
     * @param prefix 接頭辞
     * @param clazzName クラス名
     * @param methodName メソッド名
     * @param args 引数文字列
     * @param ext その他文字列
     */
    public void addTraceMonitorFinally(String prefix, String clazzName, String methodName, String args, String ext)
    {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(TRACE_MONITOR_DATE_FORMAT);
        String strDate = ZonedDateTime.now().format(dtf);

        // スレッド名
        String threadName = "(" + Thread.currentThread().getName() + ")";

        this.traceMonitorList.add(String.format("%s %s %s %s#%s(%s) %s", strDate, prefix, threadName, clazzName,
                methodName, args, ext));

        if (CommonEnums.TraceMonitorWrite.AnyTime.getValue()) {
            StringBuilder trace = new StringBuilder(128);
            trace.append("[TRACE MESSAGE] ");
            trace.append(String.format("%s#%s(%s) %s", clazzName, methodName, args, ext));
            this.logHelper.fine(this.logger, null, null, trace.toString());
            this.traceMonitorList.clear();

        }
        this.traceClazzNameList.remove(this.traceClazzNameList.size() - 1);
        this.traceMethodNameList.remove(this.traceMethodNameList.size() - 1);

    }

    /**
     * メッセージ情報を追加
     * @param messageNo メッセージNo
     * @throws GnomesAppException
     */
    public void addMessageInfo(String messageNo)
    {

        this.addMessageInfo(messageNo, (Object[]) null);
    }

    /**
     * メッセージ情報を追加
     * @param messageNo メッセージNo
     * @param arguments リソース引数
     * @throws GnomesAppException
     */
    public void addMessageInfo(String messageNo, Object... arguments)
    {

        // パラメータ情報（リスト）の作成
        List<String> params = new ArrayList<>();
        if (arguments != null) {
            for (int i = 0; i < arguments.length; i++) {
                if (arguments[i] instanceof List) {
                    for (Object l : (List<?>) arguments[i]) {
                        params.add(l.toString());
                    }
                }
                else {
                    params.add(arguments[i].toString());
                }
            }
        }
        this.addMessageInfo(messageNo, params);
    }

    /**
     * メッセージ追加
     * @param mesOwner 親
     * @param mesChilds 子
     */
    public void addMessageInfo(MessageData mesOwner, MessageData[] mesChilds)
    {

        try {
            MessageInfo message = this.getMessageInfo(mesOwner, true);

            // ログ出力
            this.isLogging(message, mesChilds);

            // メッセージ情報リストに追加
            this.messageInfoList.add(message);

            // 監視機能用Key
           	message.setWatcherSearchKey(this.watcherSearchKey);

            // Exceptionスロー判定
            if (this.getIsThrowException()) {
                // Exceptionがスローされている場合、同期でメッセージ登録
                this.insertMessage(message, mesChilds);
            }
            else {
                // Exceptionがスローされていない場合、非同期でメッセージ登録
                this.asynchronousProcess.insertMessage(message, mesChilds, this);
            }

            if (mesChilds != null) {
                for (int i = 0; i < mesChilds.length; i++) {
                    message = this.getMessageInfo(mesChilds[i], false);

                    // ログ出力
                    this.isLogging(message);

                    // メッセージ情報リストに追加
                    this.messageInfoList.add(message);
                }
            }

            this.setIsThrowException(false);

        }
        catch (Exception e) {
            GnomesException ex = this.exceptionFactory.createGnomesException(e, GnomesMessagesConstants.ME01_0001);
            // ログ出力
            this.logHelper.severe(this.logger, null, null, MessagesHandler.getExceptionMessage(this, ex), e);
        }
    }

    /**
     * パラメータ取得
     * @param arguments パラメータ
     * @return パラメータ
     */
    private List<String> getParamListString(Object[] arguments)
    {
        // パラメータ情報（リスト）の作成
        List<String> params = new ArrayList<>();
        if (arguments != null) {
            for (int i = 0; i < arguments.length; i++) {
                if (arguments[i] instanceof List) {
                    for (Object l : (List<?>) arguments[i]) {
                        params.add(l.toString());
                    }
                }
                else {
                    params.add(arguments[i].toString());
                }
            }
        }
        return params;
    }

    /**
     * メッセージ情報の共通項目編集
     * @return メッセージ情報
     * @throws GnomesAppException
     */
    private MessageInfo editMessageInfoCommon() throws GnomesAppException
    {
        MessageInfo message = new MessageInfo();

        // メッセージ情報を設定
        // 画面ID
        message.setScreenId(this.screenId);
        // 画面名
        message.setScreenName(this.screenName);
        // 発生拠点ID
        message.setSiteCode(this.siteCode);
        // 発生コンピュータ名
        message.setOccurHost(this.computerName);
        // 発生コンピュータID
        try {
            if(ejbBean.isEjbBatch()){
                message.setOccurHostId(CommonConstants.COMPUTERID_LOCALHOST_SF);
            }
            else {
                message.setOccurHostId(gnomesSessionBean.getComputerId());
            }
        }
        catch (ContextNotActiveException e) {
            //定周期処理用 nop
        }
        // 発生元IPアドレス クライアント
        //バッチの場合はLocalHost固定
        if(this.ejbBean.isEjbBatch()){
            message.setOriginIpAddress(CommonConstants.IPADDRESS_LOCALHOST);
        }
        //バッチでない（画面）の場合はSessionBeanから
        else {
            message.setOriginIpAddress(this.gnomesSessionBean.getIpAddress());
        }

        // 発生者ID
        message.setOccrUserId(this.userId);
        // 発生者名
        message.setOccrUserName(this.userName);

        //Web.xmlのタイムゾーンがUTCの場合,
        if (CommonConstants.ZONEID_UTC.equals(TimeZone.getDefault().getID())) {

            String defaultTimeZoneName = TimeZone.getDefault().getID();
            TimeZone.setDefault(gnomesSessionBean.getUserTimeZone());
            // 発生日時
            message.setOccurDate(ConverterUtils.dateToLocalDateTime(CurrentTimeStamp.getSystemCurrentTimeStamp()));
            TimeZone timeZone = TimeZone.getTimeZone(defaultTimeZoneName);
            TimeZone.setDefault(timeZone);
        }
        else {
            // 発生日時
            message.setOccurDate(ConverterUtils.dateToLocalDateTime(CurrentTimeStamp.getSystemCurrentTimeStamp()));
        }

        // コマンドID
        message.setCommandId(this.commandId);
        // コマンド名
        if (!StringUtil.isNullOrEmpty(this.commandId)) {
            message.setCommandName(ResourcesHandler.getString(this.operationResourceKey));
        }

        // ボタンID
        if (this.getWrapperRequest() != null) {
            message.setButtonId(systemFormBean.getButtonId());
            if (!StringUtil.isNullOrEmpty(this.screenId) && !StringUtil.isNullOrEmpty(systemFormBean.getButtonId())) {

                // 画面ボタンマスタの取得
                MstrScreenButton dataMstrScreenButton = mstrScreenButtonDao.getMstrScreenButton(this.screenId,
                        systemFormBean.getButtonId());

                if (dataMstrScreenButton != null) {
                    // ボタン操作内容
                    message.setButtonOperationContent(dataMstrScreenButton.getOperation_content());
                }
            }
        }
        // 領域区分(1:通常領域 2:保管領域
        if(ejbBean.isEjbBatch()){
        	message.setDbAreaDiv(Integer.parseInt(CommonEnums.RegionType.NORMAL.getValue()));
        } else {
        	String regionType = this.gnomesSessionBean.getRegionType();
        	if( StringUtil.isNotNull(regionType)){
        		if (regionType.equals(CommonEnums.RegionType.NORMAL.getValue())
        				|| regionType.equals(CommonEnums.RegionType.STORAGE.getValue())) {

        			message.setDbAreaDiv(Integer.parseInt(regionType));
        		}
        	}

        }

        // ユーザーロケール
        message.setUserLocale(this.getUserLocale());

        return message;
    }

    /**
     * メッセージ情報の共通項目編集（バッチ用）
     * @return メッセージ情報
     * @throws GnomesAppException
     */
    private MessageInfo editMessageInfoCommonBatch()
    {
        MessageInfo message = new MessageInfo();

        // メッセージ情報を設定
        // 画面ID
        message.setScreenId(this.screenId);
        // 画面名
        message.setScreenName(this.screenName);
        // 発生拠点ID
        message.setSiteCode(this.siteCode);
        // 発生コンピュータ名
        message.setOccurHost(this.computerName);
        // 発生コンピュータID
        if(this.ejbBean.isEjbBatch()){
            message.setOccurHostId(CommonConstants.COMPUTERID_LOCALHOST_SF);
        }
        else {
            message.setOccurHostId(gnomesSessionBean.getComputerId());
        }
        // 発生者ID
        message.setOccrUserId(this.userId);
        // 発生者名
        message.setOccrUserName(this.userName);
        // 発生日時
        message.setOccurDate(ConverterUtils.dateToLocalDateTime(CurrentTimeStamp.getSystemCurrentTimeStamp()));

        // コマンドID
        message.setCommandId(this.commandId);
        // コマンド名
        if (!StringUtil.isNullOrEmpty(this.commandId)) {
            message.setCommandName(ResourcesHandler.getString(this.operationResourceKey));
        }

        // ユーザーロケール
        message.setUserLocale(this.getUserLocale());

        return message;
    }

    /**
     * メッセージ定義マスタからメッセージ情報を設定
     * @param message メッセージ情報
     * @param mstrMsgDef メッセージ定義マスタ
     */
    private void setMessageDef2Info(MessageInfo message, MstrMessageDefine mstrMsgDef)
    {

        // メッセージno
        message.setMessageNo(mstrMsgDef.getMessage_no());
        // 種別
        message.setCategory(mstrMsgDef.getCategory());
        // メッセージ重要度
        message.setMsgLevel(mstrMsgDef.getMessage_level());
        // メッセージ履歴記録可否
        message.setIsMsgHistoryRec(mstrMsgDef.getIs_message_history_rec());
        // ログ有無
        message.setIsLogging(mstrMsgDef.getIs_logging());
        // プッシュ通知フラグ
        message.setIsNoticePush(mstrMsgDef.getIs_notice_push());
        // メッセージボタンモード
        message.setMsgBtnMode(mstrMsgDef.getMessage_btn_mode());
        // メッセージデフォルトボタンモード
        message.setDefaultBtn(mstrMsgDef.getMessage_default_btn_mode());
        // メール送信先グループID
        message.setSendMailGroupid(mstrMsgDef.getSend_mail_group_id());
        // メール送信抑制上限数
        message.setSendMailRestrainLimit(mstrMsgDef.getSend_mail_restrain_limit());
        // メール送信抑制期間（時間）
        message.setSendMailRestrainLimitTime(mstrMsgDef.getSend_mail_restrain_limit_time());
        // Talend呼出ジョブ名
        message.setTalendJobName(mstrMsgDef.getTalend_job_name());
        // Talendコンテキストパラメータ
        message.setTalendContextParam(mstrMsgDef.getTalend_context_param());
        // メッセージタイトルリソースID
        message.setMsgTitleResourceid(mstrMsgDef.getMessage_title_resource_id());
        // メッセージ本文リソースID
        message.setMsgTextResourceid(mstrMsgDef.getMessage_text_resource_id());

        // リンク情報を取得
        MstrLink mstrLinkInfo = this.getLinkInfo(mstrMsgDef.getMessage_define_key());
        if (mstrLinkInfo != null) {
            // ガイダンスメッセージ
            message.setGuidanceMsg(MessagesHandler.getString(mstrLinkInfo.getGuidance_resource_id(),
                    this.getUserLocale()));
            // リンク情報
            message.setLinkInfo(mstrLinkInfo.getLink_info());
            // リンク名
            message.setLinkName(ResourcesHandler.getString(mstrLinkInfo.getLink_name_resource_id(),
                    this.getUserLocale()));

        }
    }

    /**
     * メッセージ情報取得
     * @param mesData メッセージデータ
     * @param isOwner 親フラグ
     * @return メッセージ情報
     * @throws GnomesAppException
     */
    private MessageInfo getMessageInfo(MessageData mesData, boolean isOwner) throws GnomesAppException
    {

        MessageInfo message = editMessageInfoCommon();
        MstrMessageDefine mstrMsgDef = null;

        // メッセージ定義を取得
        mstrMsgDef = mstrMessageDefineDao.getMstrMessageDefine(mesData.getMessageNo());

        if (!StringUtil.isNullOrEmpty(mstrMsgDef.getResource_id())) {
            // リソースID
            message.setResourceId(mstrMsgDef.getResource_id());
        }
        // パラメータ情報（リスト）
        message.setMsgParamList(getParamListString(mesData.getParams()));
        // 画面表示フラグ=true
        message.setIsDispFlg(true);

        // 親
        message.setOwner(isOwner);

        if (!traceClazzNameList.isEmpty()) {
            // 発生元ソース  メッセージの発生元ソース（クラス名#メソッド名)
            message.setSourceInfo(String.format(FORMAT_MSG_SOURCE, this.traceClazzNameList.get(
                    this.traceClazzNameList.size() - 1), this.traceMethodNameList.get(
                            this.traceMethodNameList.size() - 1)));
        }

        // 共通項目の設定
        setMessageDef2Info(message, mstrMsgDef);

        message.setMessageCommand(mesData.getMessageCommand());
        message.setMessageCancelOnClick(mesData.getMessageCancelOnClick());
        message.setMessageOkOnClick(mesData.getMessageOkOnClick());

        return message;
    }

    /**
     * メッセージ情報を追加(ログ出力用)
     * @param userName
     * @param msgParamList パラメータ情報（リスト）
     * @param logoutFlag
     */
    public void addMessageInfoForStartLogging(String userName, List<String> msgParamList, Boolean logoutFlag)
    {

        String resourceId;
        if (!StringUtil.isNullOrEmpty(userName)) {
            if (msgParamList.size() == 5) {
            	if (userName.equals(CommonConstants.USERNAME_BATCH)) {
            		// {0}さんのリクエストにより{1}({2})、{3}({4})処理を開始しました。
	                resourceId = GnomesMessagesConstants.MV01_0038;
            	} else {
	                // {0}さんのリクエストにより{1}({2})、{3}({4})処理を開始しました。
	                resourceId = GnomesMessagesConstants.MV01_0013;
            	}
            }
            else {
                // {0}さんのリクエストにより{1}({2})、{3}({4})より{5}({6})処理を開始しました。
                resourceId = GnomesMessagesConstants.MV01_0028;
            }
        }
        else {
            if (msgParamList.size() == 4) {
                // {0}({1})、{2}({3})処理を開始しました。
                resourceId = GnomesMessagesConstants.MV01_0034;
            }
            else {
                // {0}({1})、{2}({3})より{4}({5})処理を開始しました。
                resourceId = GnomesMessagesConstants.MV01_0036;
            }
        }
        addMessageInfoForLogging(resourceId, msgParamList, logoutFlag);
    }

    /**
     * メッセージ情報を追加(ログ出力用)
     * @param messageNo メッセージNo
     * @param msgParamList パラメータ情報（リスト）
     * @param logoutFlag
     */
    public void addMessageInfoForLogging(String resourceId, List<String> msgParamList, Boolean logoutFlag)
    {

        try {
            MessageInfo message = editMessageInfoCommon();
            // message setting for logout transition
            if (logoutFlag) {
                message.setScreenName(msgParamList.get(1)); // 画面Id
                message.setScreenId(msgParamList.get(2)); // 画面名
                message.setCommandName(msgParamList.get(3)); // コマンド名
                message.setCommandId(msgParamList.get(4));// コマンドID
            }

            MstrMessageDefine mstrMsgDef = null;

            // メッセージ定義を取得
            try {
                mstrMsgDef = mstrMessageDefineDao.getMstrMessageDefine(resourceId);

            }
            catch (GnomesAppException e) {
                // エラーハンドリング処理でエラー処理を行うため、ここでは処理なし
            }

            if (Objects.isNull(mstrMsgDef)) {
                return;
            }

            if (!StringUtil.isNullOrEmpty(mstrMsgDef.getResource_id())) {
                // リソースID
                message.setResourceId(mstrMsgDef.getResource_id());
            }

            // パラメータ情報（リスト）
            message.setMsgParamList(msgParamList);
            // 画面表示フラグ=false
            message.setIsDispFlg(false);

            // 親設定
            message.setOwner(true);

            if (!traceClazzNameList.isEmpty()) {
                // 発生元ソース  メッセージの発生元ソース（クラス名#メソッド名)
                message.setSourceInfo(String.format(FORMAT_MSG_SOURCE, this.traceClazzNameList.get(
                        this.traceClazzNameList.size() - 1), this.traceMethodNameList.get(
                                this.traceMethodNameList.size() - 1)));
            }

            // 共通項目の設定
            setMessageDef2Info(message, mstrMsgDef);

            // ログ出力判定
            this.isLogging(message);

            // 監視機能用Key
            message.setWatcherSearchKey(this.watcherSearchKey);

            // メッセージ情報リストに追加
            this.messageInfoList.add(message);

            // Exceptionスロー判定
            if (this.getIsThrowException()) {
                this.setIsThrowException(false);
                // Exceptionがスローされている場合、同期でメッセージ登録
                this.insertMessage(message);
            }
            else {
                // Exceptionがスローされていない場合、非同期でメッセージ登録
                this.asynchronousProcess.insertMessage(message, this);
            }

        }
        catch (Exception e) {
            GnomesException ex = this.exceptionFactory.createGnomesException(e, GnomesMessagesConstants.ME01_0001);
            // ログ出力
            this.logHelper.severe(this.logger, null, null, MessagesHandler.getExceptionMessage(this, ex), e);
        }

    }

    /**
     * メッセージ情報を追加(バッチログ出力用)
     * @param messageNo メッセージNo
     * @param msgParamList パラメータ情報（リスト）
     */
    public void addMessageInfoForLoggingBatch(String resourceId, List<String> msgParamList)
    {

        try {
            MessageInfo message = editMessageInfoCommonBatch();
            MstrMessageDefine mstrMsgDef = null;

            // メッセージ定義を取得
            try {
                mstrMsgDef = mstrMessageDefineDao.getMstrMessageDefine(resourceId);

            }
            catch (GnomesAppException e) {
                // エラーハンドリング処理でエラー処理を行うため、ここでは処理なし
            }

            if (Objects.isNull(mstrMsgDef)) {
                return;
            }

            if (!StringUtil.isNullOrEmpty(mstrMsgDef.getResource_id())) {
                // リソースID
                message.setResourceId(mstrMsgDef.getResource_id());
            }

            // パラメータ情報（リスト）
            message.setMsgParamList(msgParamList);
            // 画面表示フラグ=false
            message.setIsDispFlg(false);

            // 親設定
            message.setOwner(true);

            if (!traceClazzNameList.isEmpty()) {
                // 発生元ソース  メッセージの発生元ソース（クラス名#メソッド名)
                message.setSourceInfo(String.format(FORMAT_MSG_SOURCE, this.traceClazzNameList.get(
                        this.traceClazzNameList.size() - 1), this.traceMethodNameList.get(
                                this.traceMethodNameList.size() - 1)));
            }

            // 共通項目の設定
            setMessageDef2Info(message, mstrMsgDef);

            // 監視機能用Key
            message.setWatcherSearchKey(this.watcherSearchKey);

            // ログ出力判定
            this.isLogging(message);

            // メッセージ情報リストに追加
            this.messageInfoList.add(message);

            // Exceptionスロー判定
            if (this.getIsThrowException()) {
                this.setIsThrowException(false);
                // Exceptionがスローされている場合、同期でメッセージ登録
                this.insertMessage(message);
            }
            else {
                // Exceptionがスローされていない場合、非同期でメッセージ登録
                this.asynchronousProcess.insertMessage(message, this);
            }

        }
        catch (Exception e) {
            GnomesException ex = this.exceptionFactory.createGnomesException(e, GnomesMessagesConstants.ME01_0001);
            // ログ出力
            this.logHelper.severe(this.logger, null, null, MessagesHandler.getExceptionMessage(this, ex), e);
        }

    }

    /**
     * メッセージ情報を追加
     * @param messageNo メッセージNo
     * @param msgParamList パラメータ情報（リスト）
     * @throws GnomesAppException
     */
    public void addMessageInfo(String messageNo, List<String> msgParamList)
    {

        try {
            MessageInfo message = editMessageInfoCommon();
            MstrMessageDefine mstrMsgDef = null;

            try {
                // メッセージ定義を取得
                mstrMsgDef = mstrMessageDefineDao.getMstrMessageDefine(messageNo);

            }
            catch (Exception e) {
                //DBに接続できなかったら無限ループを起こすので、メッセージIDが取れなかったら
                //何もせずリターンする
                GnomesException ex = this.exceptionFactory.createGnomesException(e, SEVIRE_STR_ILLIGAL_MSG_GET);
                // ログ出力
                this.logHelper.severe(this.logger, null, null, SEVIRE_STR_ILLIGAL_MSG_GET);
                // ログ出力
                this.logHelper.severe(this.logger, null, null, MessagesHandler.getExceptionMessage(this, ex), e);
                throw ex;
            }

            if (Objects.isNull(mstrMsgDef)) {
                return;
            }

            if (!StringUtil.isNullOrEmpty(mstrMsgDef.getResource_id())) {
                // リソースID
                message.setResourceId(mstrMsgDef.getResource_id());
            }
            // パラメータ情報（リスト）
            message.setMsgParamList(msgParamList);
            // 画面表示フラグ=true
            message.setIsDispFlg(true);

            // 親
            message.setOwner(true);

            if (!traceClazzNameList.isEmpty()) {
                // 発生元ソース  メッセージの発生元ソース（クラス名#メソッド名)
                message.setSourceInfo(String.format(FORMAT_MSG_SOURCE, this.traceClazzNameList.get(
                        this.traceClazzNameList.size() - 1), this.traceMethodNameList.get(
                                this.traceMethodNameList.size() - 1)));
            }

            // 共通項目の設定
            setMessageDef2Info(message, mstrMsgDef);

            // ログ出力判定
            this.isLogging(message);

            // 監視機能用Key
            message.setWatcherSearchKey(this.watcherSearchKey);

            // メッセージ情報リストに追加
            this.messageInfoList.add(message);

            // Exceptionスロー判定
            if (this.getIsThrowException()) {
                this.setIsThrowException(false);
                // Exceptionがスローされている場合、同期でメッセージ登録
                this.insertMessage(message);
            }
            else {
                // Exceptionがスローされていない場合、非同期でメッセージ登録
                this.asynchronousProcess.insertMessage(message, this);

            }
        }
        catch (Exception e) {
            GnomesException ex = this.exceptionFactory.createGnomesException(e, GnomesMessagesConstants.ME01_0001);
            // ログ出力
            this.logHelper.severe(this.logger, null, null, MessagesHandler.getExceptionMessage(this, ex), e);
        }
    }

    /**
     * メッセージビーンの設定
     * @param messageBean メッセージビーン
     */
    @ErrorHandling
    public void setMessageBean(MessageBean messageBean) throws Exception
    {
        setMessageBean(messageBean, false);
    }

    /**
     * メッセージビーンの設定
     * @param messageBean メッセージビーン
     * @param talendFlag
     */
    @ErrorHandling
    public void setMessageBean(MessageBean messageBean, boolean talendFlag) throws Exception
    {

        int ownerIndex = -1;

        // Talendによるパトライト処理
        if (talendFlag) {
            for (int i = messageInfoList.size() - 1; i >= 0; i--) {
                MessageInfo messageInfo = messageInfoList.get(i);
                if (messageInfo.getIsDispFlg() && messageInfo.isOwner()) {
                    ownerIndex = i;
                    break;
                }
            }
            if (ownerIndex >= 0) {

                MessageInfo mesInfo = messageInfoList.get(ownerIndex);

                // Talend呼出ジョブ名が設定されている場合
                if (!StringUtil.isNullOrEmpty(mesInfo.getTalendJobName())) {

                	String talendContextParam = mesInfo.getTalendContextParam();
                	String[] contextParam;
                	//Talendパラメータが存在する場合
                	if(talendContextParam != null && !talendContextParam.isEmpty()){

                		//JSONをMAPに変換
                		//PatlampParamData p = ConverterUtils.readJson(talendContextParam, PatlampParamData.class);
                		//System.out.println(p.toString());
                		Map<String, String> talendMap = ConverterUtils.readJson(talendContextParam, new TypeReference<Map<String, Object>>(){});

                		//種類を取得
                		String kind = talendMap.get(TalendParameterName.KIND.getValue());

                		//種類がパトランプだった場合の処理
                		if(kind.equals(TalendJobKind.PATLAMP.getValue())){

                			String patlampID = talendMap.get(TalendParameterName.PATLAMP_ID.getValue());
                			PatlampData patlapmData;

                			//パトランプIDが指定されていた場合、パトランプマスタを取得する
                			if(patlampID != null && !patlampID.isEmpty()){
        						List<String> paramList = new ArrayList<String>();
                				patlapmData = this.getPatliteData(patlampID);

                				paramList.add(CommonConstants.TALEND_CONTEXT_PARAM_HEADER + CONTENT_IP_ADDRESS + patlapmData.getIp_address());
        						for (String str : patlapmData.getLightSoundParameterStringList()) {
        							paramList.add(str);
        						}

        			            // Talendコンテキストパラメータから引数に設定するコンテキストパラメータを取得
        			            if (paramList != null && paramList.size() != 0) {
        			                // Talendコンテキストパラメータをカンマ区切りで配列に変換
        			            	contextParam = paramList.toArray(new String[paramList.size()]);
        			            } else {
        			            	contextParam = new String[]{};
        			            }

                			}
                			//パトランプIDが指定されていない場合、パラメータをMAPから作成する
                			else {
                				contextParam = this.getPatliteDataFromJSON(talendMap);
                			}

                		}
                		//種類が特定できない場合
                		else{
                			contextParam = new String[]{};
                		}
                	}
                	//パラメータが存在しない
                	else {
                		contextParam = new String[]{};
                	}
                    // Talend処理実行
                    this.runTalendJob(mesInfo.getTalendJobName(), contextParam);
                }
            }
        }
        // 後ろから親を検索

        // 表示先頭を取得
        for (int i = messageInfoList.size() - 1; i >= 0; i--) {
            MessageInfo messageInfo = messageInfoList.get(i);
            if (messageInfo.getIsDispFlg() && messageInfo.isOwner()) {
                ownerIndex = i;
                break;
            }
        }

        if (ownerIndex >= 0) {

            MessageInfo mesInfo = messageInfoList.get(ownerIndex);

            // リソースID
            messageBean.setResourceid(mesInfo.getResourceId());

            // メッセージボタンモード
            messageBean.setMsgBtnMode(mesInfo.getMsgBtnMode());

            // メッセージデフォルトボタンモード
            // defaultbtn
            messageBean.setDefaultBtn(mesInfo.getDefaultBtn());

            // 日付フォーマット
            String datePattern = ResourcesHandler.getString(CommonConstants.RES_OCCURDATE_FORMAT, getUserLocale());

            // 発生日時
            messageBean.setOccurDate(ConverterUtils.dateTimeToString(mesInfo.getOccurDate(), getUserLocale(),
                    TimeZone.getDefault(), datePattern));

            // 発生者名
            messageBean.setOccrUserName(mesInfo.getOccrUserName());

            // 発生元コンピュータ名
            messageBean.setOccrHost(mesInfo.getOccurHost());

            // 種別
            messageBean.setCategory(mesInfo.getCategory());

            //保管領域
            //mesInfoはint(1/2)なので、これをリソースに変換する
            Integer dbAreaDiv = mesInfo.getDbAreaDiv();

            //保管領域のみ[保管領域]に差し替える
            if(Objects.nonNull(dbAreaDiv) && dbAreaDiv.equals(CommonEnums.RegionType.STORAGE.getIntValue())){
                messageBean.setDbAreaDiv(ResourcesHandler.getString(GnomesResourcesConstants.DI01_0116, getUserLocale()));
            }
            else {
                //デフォルトで実行領域
                messageBean.setDbAreaDiv(ResourcesHandler.getString(GnomesResourcesConstants.DI01_0115, getUserLocale()));
            }

            // 種別(名称)
            CommonEnums.MessageCategory msgCategory = CommonEnums.MessageCategory.getEnum(mesInfo.getCategory());
            // リソースよりユーザ言語で取得
            messageBean.setCategoryName(ResourcesHandler.getString(msgCategory.toString(), getUserLocale()));

            // メッセージ設定
            String resourceId = mesInfo.getResourceId();

            Object[] params = null;
            if (mesInfo.getMsgParamList() != null) {
                params = mesInfo.getMsgParamList().toArray(new Object[0]);
            }

            String mes = MessagesHandler.getString(resourceId, getUserLocale(), params);

            String mesLineEscapeHtml = StringUtils.getStringEscapeHtml(mes, false);
            // メッセージ
            messageBean.setMessage(mesLineEscapeHtml);

            // メッセージ詳細
            StringBuilder mesDetail = new StringBuilder();

            // 子メッセージ分
            for (int i = ownerIndex + 1; i < messageInfoList.size(); i++) {
                MessageInfo messageInfoSub = messageInfoList.get(i);

                //エラーメッセージのみを対象とする
                if (messageInfoSub.getIsDispFlg()) {
                    if (mesDetail.length() > 0) {
                        mesDetail.append(StringUtils.LIEN_SEPARATOR_RESOURCE);
                    }

                    Object[] subParams = null;
                    if (messageInfoSub.getMsgParamList() != null) {
                        subParams = messageInfoSub.getMsgParamList().toArray(new Object[0]);
                    }

                    mesDetail.append(MessagesHandler.getString(messageInfoSub.getResourceId(), getUserLocale(),
                            subParams));

                }
            }

            String mesDetailLineEscapeHtml = StringUtils.getStringEscapeHtml(mesDetail.toString());
            messageBean.setMessageDetail(mesDetailLineEscapeHtml);

            // メッセージ重要度
            CommonEnums.MessageLevel msgLevel = CommonEnums.MessageLevel.getEnum(mesInfo.getMsgLevel());

            // メッセージアイコン名
            messageBean.setIconName(ResourcesHandler.getString(MessagesHandler.getMessageIconResKey(msgCategory,
                    msgLevel), getUserLocale()));

            if (!StringUtil.isNullOrEmpty(mesInfo.getGuidanceMsg())) {

                this.linkInfo = new String[]{mesInfo.getGuidanceMsg(), mesInfo.getLinkInfo(), mesInfo.getLinkName()};
                messageBean.setLinkInfo(this.linkInfo);
            }

            messageBean.setCommand(mesInfo.getMessageCommand());
            messageBean.setMessageCancelOnClick(mesInfo.getMessageCancelOnClick());
            messageBean.setMessageOkOnClick(mesInfo.getMessageOkOnClick());
            messageBean.setButtonId(mesInfo.getButtonId());
        }
    }

    /**
     * リンク情報取得
     * @param messageKey メッセージキー
     * @return リンク情報
     */
    public MstrLink getLinkInfo(String messageKey)
    {

        MstrLink mstrLinkInfo = null;

        try {
            if (!StringUtil.isNullOrEmpty(messageKey)) {
                // リンク情報を取得
                mstrLinkInfo = this.mstrLinkDao.getMstrLinkInfo(messageKey);

            }

        }
        catch (GnomesAppException e) {
            // エラーハンドリング処理でエラー処理を行うため、ここでは処理なし
            return null;
        }

        return mstrLinkInfo;

    }

    /**
     * 例外メッセージを取得
     * @param ex 例外
     * @return String
     */
    public String getExceptionMessage(GnomesAppException ex)
    {
        String ret = null;

        if (ex.getMessageNo() == null) {
            return ex.getMessage();
        }
        else {
            // メッセージ定義を取得
            MstrMessageDefine mstrMsgDef = null;
            try {
                mstrMsgDef = mstrMessageDefineDao.getMstrMessageDefine(ex.getMessageNo());
            }
            catch (GnomesAppException e) {
                // エラーハンドリング処理でエラー処理を行うため、ここでは処理なし
            }
            if (!Objects.isNull(mstrMsgDef)) {
                if (!StringUtil.isNullOrEmpty(mstrMsgDef.getResource_id())) {
                    ret = MessagesHandler.getString(mstrMsgDef.getResource_id(), ex.getMessageParams());
                }
            }
            return ret;
        }
    }

    /**
     * 例外メッセージを取得
     * @param ex 例外
     * @return String
     */
    public String getExceptionMessage(GnomesException ex)
    {
        String ret = null;

        if (ex.getMessageNo() == null) {
            return ex.getMessage();
        }
        else {
            // メッセージ定義を取得
            MstrMessageDefine mstrMsgDef = null;
            try {
                mstrMsgDef = mstrMessageDefineDao.getMstrMessageDefine(ex.getMessageNo());
            }
            catch (GnomesAppException e) {
                // エラーハンドリング処理でエラー処理を行うため、ここでは処理なし
            }
            if (!Objects.isNull(mstrMsgDef) && !StringUtil.isNullOrEmpty(mstrMsgDef.getResource_id())) {
                ret = MessagesHandler.getString(mstrMsgDef.getResource_id(), ex.getMessageParams());
            }

            return ret;
        }
    }

    /**
     * 監査証跡情報を追加
     * @param BaseEntity エンティティ
     * @param contentResourceId 変更内容リソースID
     */
    public void addAuditTrailInfo(BaseEntity entity, String contentResourceId)
    {

        // ContainerRequest に保持した情報をもとに監査証跡情報の各項目を設定
        AuditTrailInfo auditTrail = new AuditTrailInfo();

        // アクセス（操作）日時
        auditTrail.setAccessDateTime(LocalDateTime.now());

        if (StringUtil.isNullOrEmpty(this.computerName)) {
            // 操作PCコンピュータ名
            auditTrail.setAcesssComputerNamer(this.computerName);
        }
        else {
            // IPアドレス
            auditTrail.setAcesssComputerNamer(this.ipAddress);
        }
        // 操作ユーザID
        auditTrail.setOccrUserId(this.userId);
        // 操作ユーザ名
        auditTrail.setOccrUserName(this.userName);
        // 操作内容（画面ID）
        auditTrail.setAccessScreenId(this.screenId);
        // 操作内容（画面名）
        auditTrail.setAccessScreenName(this.screenName);

        if (!traceClazzNameList.isEmpty()) {
            // 操作内容（クラス名）
            auditTrail.setAccessClassName(this.traceClazzNameList.get(this.traceClazzNameList.size() - 1));
            // 操作内容（メソッド名）
            auditTrail.setAccessMethodName(this.traceMethodNameList.get(this.traceMethodNameList.size() - 1));
        }
        // 操作内容（機能（コマンドID)）
        auditTrail.setAccessCommandId(this.commandId);

        // 変更データ項目（リスト）
        String[] sccessDataItemList = {entity.getClass().getSimpleName()};
        auditTrail.setAccessDataItemList(sccessDataItemList);
        // 変更データ内容（リスト）
        String[] accessDataContentList = {contentResourceId};
        auditTrail.setAccessDataContentList(accessDataContentList);

        // メッセージ情報リストに追加
        this.auditTrailInfoList.add(auditTrail);

    }

    /**
     * 蓄積したトレース情報を出力する
     *
     * @return トレース情報
     */
    public String dumpTraceMonitor()
    {
        String delimiter = System.getProperty("line.separator");

        List<String> traceMonitor = new ArrayList<>();

        for (Object obj : traceMonitorList) {
            if (obj instanceof String) {
                traceMonitor.add((String) obj);
            }
            if (obj instanceof InvocationContext) {
                InvocationContext context = (InvocationContext) obj;

                StringBuilder trace = new StringBuilder(128);
                String traceClassName = context.getTarget().getClass().getSuperclass().getName();
                String traceMethodName = context.getMethod().getName();
                //trace.append("\t");
                trace.append(traceClassName);
                trace.append("#");
                trace.append(traceMethodName);
                trace.append(" ");
                trace.append((String) context.getContextData().get("message"));
                traceMonitor.add("\t" + trace.toString());

                //パラメーター
                Object[] params = context.getParameters();
                if (params != null && params.length > 0) {
                    traceMonitor.add("\t\tPARAMETER:");
                    int i = 0;
                    for (Object param : params) {
                        StringBuilder s = new StringBuilder(128);
                        s.append("\t\t\targ");
                        s.append(++i);
                        s.append("[");
                        if (param == null) {
                            s.append("null");
                        }
                        else {
                            s.append(param.toString());
                        }
                        s.append("]");
                        traceMonitor.add(s.toString());
                    }
                }

                //フィールド
                Field[] fields = context.getTarget().getClass().getFields();
                if (fields != null && fields.length > 0) {
                    traceMonitor.add("\t\tFIELD:");
                    for (Field field : fields) {
                        StringBuilder s = new StringBuilder(128);
                        s.append("\t\t\t");
                        s.append(field.getName());
                        s.append("[");
                        s.append("]");
                        traceMonitor.add(s.toString());
                    }
                }
            }
        }

        Stream<String> dumpStream = Arrays.stream((String[]) traceMonitor.toArray(new String[traceMonitor.size()]));
        String traceDump = dumpStream.collect(Collectors.joining(delimiter));
        dumpStream.close();

        return traceDump;
    }

    /**
     * メッセージ情報登録.
     * @param messageInfo メッセージ情報
     */
    public void insertMessage(MessageInfo messageInfo)
    {

        EntityManager em = this.emf.createEntityManager();
        EntityTransaction tran = null;

        try {
            // メッセージ履歴記録可否判定
            if (MessageHistoryRec.TRUE.getValue() == messageInfo.getIsMsgHistoryRec()) {
                tran = em.getTransaction();
                tran.begin();

                insertMessageInternal(em, messageInfo);

                tran.commit();
            }
        }
        catch (Exception ex) {
            //トランザクションがアクティブならばロールバックする
            if ((!Objects.isNull(tran)) && tran.isActive()) {
                tran.rollback();
            }
            ex.printStackTrace();
        }
        finally {
            if ((!Objects.isNull(em)) && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     *
     * トランザクションを挟んでメッセージをインサートする
     *
     * @param em            オープン済エンティティマネージャー
     * @param messageInfo   登録するメッセージの情報
     */
    private void insertMessageInternal(EntityManager em, MessageInfo messageInfo)
    {
        // メッセージ登録
        Message mes = this.editMessage(messageInfo);
        em.persist(mes);
        // メール通知キュー登録
        insertTmpQueueMailNotice(messageInfo, mes, em);
        em.flush();

        return;
    }

    /**
     * メッセージ情報登録
     *
     * @param messageInfo メッセージ情報
     * @param mesChilds 子メッセージ
     */
    public void insertMessage(MessageInfo messageInfo, MessageData[] mesChilds)
    {
        EntityManager em = this.emf.createEntityManager();
        EntityTransaction tran = null;

        try {

            // メッセージ履歴記録可否判定
            if (MessageHistoryRec.TRUE.getValue() == messageInfo.getIsMsgHistoryRec()) {

                tran = em.getTransaction();
                tran.begin();

                insertMessageInternal(em, messageInfo, mesChilds);

                tran.commit();
            }
        }
        catch (Exception ex) {
            //トランザクションがアクティブならばロールバックする
            if ((!Objects.isNull(tran)) && tran.isActive()) {
                tran.rollback();
            }
            ex.printStackTrace();
        }
        finally {
            if ((!Objects.isNull(em)) && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     *
     * トランザクションを挟んでメッセージをインサートする
     *
     * @param em            オープン済エンティティマネージャー
     * @param messageInfo   登録するメッセージの情報
     * @param mesChilds     子メッセージ
     *
     * @throws GnomesAppException
     */
    private void insertMessageInternal(EntityManager em, MessageInfo messageInfo, MessageData[] mesChilds)
            throws GnomesAppException
    {
        // メッセージ登録
        Message mes = this.editMessage(messageInfo, mesChilds);
        em.persist(mes);
        // メール通知キュー登録
        insertTmpQueueMailNotice(messageInfo, mes, em);
        em.flush();

        return;
    }

    /**
     * Talendジョブ実行
     * @param talendJobName ジョブ名
     * @param talendContextParam コンテキストパラメータ
     * @throws Exception
     */
    public void runTalendJob(String talendJobName, String[] talendContextParam) throws Exception
    {

        /** 確認用ソース
        messageInfo.setTalendJobName("gnomes_project.movetofilejob_0_1.MoveToFileJob");
        messageInfo.setTalendContextParam("MoveFromFolderName=C:/送信temp/,MoveToFolderName=C:/送信先/,SendRecvFileName=test.txt,IsFileCheck=false");
        */

        // Talend呼出ジョブ名が設定されている場合
        if (!StringUtil.isNullOrEmpty(talendJobName)) {

            //System.out.println(talendContextParam);
            TalendJobRun.runJob(talendJobName, talendContextParam, false);
            // エラーが出力された場合
            if (!StringUtil.isNullOrEmpty(this.talendJobInfoBean.getErrorJobName())) {
                throw exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0146,
                        talendJobInfoBean.getErrorJobName(), talendJobInfoBean.getErrorComponentName());
            }

        }

    }

    /**
     * 共通的な項目のメッセージ情報を編集
     * @param messageInfo メッセージ情報
     * @return メッセージ情報
     */
    private Message editMessageCommon(MessageInfo messageInfo)
    {
        // メッセージ情報編集
        Message mes = new Message();

        // メッセージキー
        mes.setMessage_key(UUID.randomUUID().toString());
        // 発生日時
        ZonedDateTime date = messageInfo.getOccurDate().atZone(ZoneId.systemDefault());
        mes.setOccur_date(ConverterUtils.utcToTimestamp(date));
        // 発生者id
        mes.setOccur_user_id(messageInfo.getOccrUserId());
        // 発生者名
        mes.setOccur_user_name(messageInfo.getOccrUserName());
        // 発生元コンピュータ名
        mes.setOrigin_computer_name(messageInfo.getOccurHost());
        // 発生元コンピュータID
        mes.setOrigin_computer_id(messageInfo.getOccurHostId());
        // 発生元IPアドレス クライアント
        mes.setOrigin_ip_address(messageInfo.getOriginIpAddress());
        // 画面ID
        mes.setOrigin_screen_id(messageInfo.getScreenId());
        // 画面名
        mes.setOrigin_screen_name(messageInfo.getScreenName());
        // コマンドID
        if (messageInfo.getCommandId() == null) {
            mes.setOrigin_command_id(null);
        } else {
            // コマンドIDは
            // this.getClass().getSimpleName() で取得する場合があるが、取得した文字列は
            // A01001S001Proc$Proxy$_$$_WeldSubclass のように
            // 余計な文字列が付く場合があるので、最初の$マークまでの文字列を切り出す
            mes.setOrigin_command_id(messageInfo.getCommandId().split("\\$")[0]);
        }
        // コマンド名
        mes.setOrigin_command_name(messageInfo.getCommandName());
        // ボタンID
        mes.setOrigin_button_id(messageInfo.getButtonId());
        // ボタン操作内容
        mes.setOrigin_button_operation_content(messageInfo.getButtonOperationContent());

        // 発生拠点
        mes.setOccur_site_code(messageInfo.getSiteCode());
        // メッセージno.
        mes.setMessage_no(messageInfo.getMessageNo());
        // リソースid
        mes.setResource_id(messageInfo.getResourceId());
        // 種別
        mes.setCategory(messageInfo.getCategory());
        // メッセージ重要度
        mes.setMessage_level(messageInfo.getMsgLevel());

        //DB領域区分
        mes.setDb_area_div(messageInfo.getDbAreaDiv());
        //監視機能用Key
        mes.setWatcher_search_key(messageInfo.getWatcherSearchKey());

        // 発生元ソース
        mes.setMessage_origin_source(messageInfo.getSourceInfo());
        // Talendジョブ名
        mes.setTalend_job_name(messageInfo.getTalendJobName());
        // Talendコンテキストパラメータ
        mes.setTalend_context_param(messageInfo.getTalendContextParam());
        // メッセージタイトルリソースID
        mes.setMessage_title_resource_id(messageInfo.getMsgTitleResourceid());
        // メッセージ本文リソースID
        mes.setMessage_text_resource_id(messageInfo.getMsgTextResourceid());
        // ガイダンスメッセージ文字列
        mes.setGuidance_message(messageInfo.getGuidanceMsg());
        // リンク情報
        mes.setLink_info(messageInfo.getLinkInfo());
        // リンク名
        mes.setLink_name(messageInfo.getLinkName());
        // 登録イベントID
        mes.setFirst_regist_event_id(this.eventId);
        // 登録従業員No
        mes.setFirst_regist_user_number(messageInfo.getOccrUserId());
        // 登録従業員名
        mes.setFirst_regist_user_name(messageInfo.getOccrUserName());
        // 登録日時
        mes.setFirst_regist_datetime(ConverterUtils.utcToTimestamp(date));
        // 更新イベントID
        mes.setLast_regist_event_id(this.eventId);
        // 更新従業員No
        mes.setLast_regist_user_number(messageInfo.getOccrUserId());
        // 更新従業員名
        mes.setLast_regist_user_name(messageInfo.getOccrUserName());
        // 更新日時
        mes.setLast_regist_datetime(ConverterUtils.utcToTimestamp(date));

        return mes;
    }

    /**
     * メッセージ編集.
     * @param messageInfo メッセージ情報
     * @return メッセージエンティティ
     */
    private Message editMessage(MessageInfo messageInfo)
    {

        int msgparamCount = 0;

        // メッセージ情報編集
        Message mes = editMessageCommon(messageInfo);

        // メッセージ取得
        String msgValue = this.getMessage(messageInfo.getResourceId(), messageInfo.getMsgParamList(),
                this.getUserLocale());

        if (messageInfo.getMessageNo().equals(GnomesMessagesConstants.ME01_0001)) {

            int s = msgValue.indexOf(StringUtils.LIEN_SEPARATOR_RESOURCE);
            if (s >= 0) {
                //最初の改行が入った文字を改行文字毎mes1に入れる
                String mes1 = msgValue.substring(0, s + 1);
                // メッセージ補足情報１
                mes.setMessage_param1(mes1);
                msgparamCount = 1;

                //最初の改行の次の文字から最後までを得る
                msgValue = msgValue.substring(s + 1, msgValue.length());
            }
        }

        StringBuilder regex = new StringBuilder();
        regex.append("[\\s\\S]{1,").append(CommonConstants.MESSAGE_PARAMETER_MAX_VALUE).append("}");

        Matcher m = Pattern.compile(regex.toString()).matcher(msgValue);

        while (m.find()) {

            switch (msgparamCount) {
                case 0 :
                    // メッセージ補足情報１
                    mes.setMessage_param1(m.group());
                    break;
                case 1 :
                    // メッセージ補足情報２
                    mes.setMessage_param2(m.group());
                    break;
                case 2 :
                    // メッセージ補足情報３
                    mes.setMessage_param3(m.group());
                    break;
                case 3 :
                    // メッセージ補足情報４
                    mes.setMessage_param4(m.group());
                    break;
                case 4 :
                    // メッセージ補足情報５
                    mes.setMessage_param5(m.group());
                    break;
                case 5 :
                    // メッセージ補足情報６
                    mes.setMessage_param6(m.group());
                    break;
                case 6 :
                    // メッセージ補足情報７
                    mes.setMessage_param7(m.group());
                    break;
                case 7 :
                    // メッセージ補足情報８
                    mes.setMessage_param8(m.group());
                    break;
                case 8 :
                    // メッセージ補足情報９
                    mes.setMessage_param9(m.group());
                    break;
                case 9 :
                    // メッセージ補足情報１０
                    mes.setMessage_param10(m.group());
                    break;
                default :
                    break;
            }

            msgparamCount++;

            if (msgparamCount == CommonConstants.MESSAGE_PARAMETER_MAX_COLUMN) {
                break;
            }

        }

        return mes;

    }

    /**
     * メッセージ編集.
     * @param messageInfo メッセージ情報
     * @return メッセージエンティティ
     * @throws GnomesAppException
     */
    private Message editMessage(MessageInfo messageInfo, MessageData[] mesChilds) throws GnomesAppException
    {

        int msgparamCount = 0;

        // メッセージ情報編集
        Message mes = editMessageCommon(messageInfo);

        // メッセージ取得
        String msgValue = this.getMessage(messageInfo.getResourceId(), messageInfo.getMsgParamList(),
                this.getUserLocale());

        if (messageInfo.getMessageNo().equals(GnomesMessagesConstants.ME01_0001)) {

            int s = msgValue.indexOf(StringUtils.LIEN_SEPARATOR_RESOURCE);
            if (s >= 0) {
                String mes1 = msgValue.substring(0, s + 1);
                // メッセージ補足情報１
                mes.setMessage_param1(mes1);
                msgparamCount = 1;

              //最初の改行の次の文字から最後までを得る
                msgValue = msgValue.substring(s + 1, msgValue.length());
            }
        }

        StringBuilder msgBuild = new StringBuilder(128);
        msgBuild.append(msgValue);

        if (mesChilds != null) {

            // 子のメッセージをセット
            for (int i = 0; i < mesChilds.length; i++) {
                MessageData messageData = mesChilds[i];
                if (msgBuild.length() > 0) {
                    msgBuild.append(StringUtils.LIEN_SEPARATOR_RESOURCE);
                }

                // メッセージ定義を取得
                MstrMessageDefine mstrMsgDef = mstrMessageDefineDao.getMstrMessageDefine(messageData.getMessageNo());

                msgValue = MessagesHandler.getString(mstrMsgDef.getResource_id(), getUserLocale(),
                        messageData.getParams());

                msgBuild.append(msgValue);
            }
        }

        StringBuilder regex = new StringBuilder();
        regex.append("[\\s\\S]{1,").append(CommonConstants.MESSAGE_PARAMETER_MAX_VALUE).append("}");

        Matcher m = Pattern.compile(regex.toString()).matcher(msgBuild.toString());

        while (m.find()) {

            switch (msgparamCount) {
                case 0 :
                    // メッセージ補足情報１
                    mes.setMessage_param1(m.group());
                    break;
                case 1 :
                    // メッセージ補足情報２
                    mes.setMessage_param2(m.group());
                    break;
                case 2 :
                    // メッセージ補足情報３
                    mes.setMessage_param3(m.group());
                    break;
                case 3 :
                    // メッセージ補足情報４
                    mes.setMessage_param4(m.group());
                    break;
                case 4 :
                    // メッセージ補足情報５
                    mes.setMessage_param5(m.group());
                    break;
                case 5 :
                    // メッセージ補足情報６
                    mes.setMessage_param6(m.group());
                    break;
                case 6 :
                    // メッセージ補足情報７
                    mes.setMessage_param7(m.group());
                    break;
                case 7 :
                    // メッセージ補足情報８
                    mes.setMessage_param8(m.group());
                    break;
                case 8 :
                    // メッセージ補足情報９
                    mes.setMessage_param9(m.group());
                    break;
                case 9 :
                    // メッセージ補足情報１０
                    mes.setMessage_param10(m.group());
                    break;
                default :
                    break;
            }

            msgparamCount++;

            if (msgparamCount == CommonConstants.MESSAGE_PARAMETER_MAX_COLUMN) {
                break;
            }

        }

        return mes;

    }

    /**
     * メール通知キュー登録.
     * <pre>
     * メール通知キューの登録を行う。
     * 条件：メール送信先グループIDが設定かつ、メール送信抑制上限数以下
     * ただし、メール送信抑制上限数が<code>0</code>の場合はチェック対象外
     * </pre>
     * @param messageInfo メッセージ情報
     * @param message メッセージエンティティ
     * @param em エンティティマネージャ
     * @throws GnomesAppException
     */
    private void insertTmpQueueMailNotice(MessageInfo messageInfo, Message message, EntityManager em)
    {

        if (!StringUtil.isNullOrEmpty(messageInfo.getSendMailGroupid())) {

            boolean insertFlag = true;

            if (messageInfo.getSendMailRestrainLimit() != null && messageInfo.getSendMailRestrainLimit() > 0) {
                // メール通知件数取得
                TypedQuery<CountDto> query = em.createNamedQuery(GnomesQueryConstants.QUERY_NAME_GET_SEND_MAIL_COUNT,
                        CountDto.class);

                query.setParameter(TmpQueueMailNotice.COLUMN_NAME_MESSAGE_NO, messageInfo.getMessageNo());
                query.setParameter(TmpQueueMailNotice.COLUMN_NAME_OCCUR_DATE, this.getOccurdate(message.getOccur_date(),
                        messageInfo.getSendMailRestrainLimitTime()));

                int count = query.getSingleResult().getCnt().intValueExact();

                if (count > messageInfo.getSendMailRestrainLimit()) {
                    insertFlag = false;
                }

            }

            if (insertFlag) {
                // メール通知キュー登録
                em.persist(setTmpQueueMailNotice(message));
            }

        }

    }

    /**
     * 発生日時抽出条件取得.
     * <pre>
     * 発生日時からメール送信抑制期間（時間）を減算した値を返却する。
     * </pre>
     * @param occurdate 発生日時
     * @param sendMailRestrainLimitTime メール送信抑制期間（時間）
     * @return
     */
    private Timestamp getOccurdate(Date occurdate, int sendMailRestrainLimitTime)
    {

        Calendar cal = Calendar.getInstance();
        cal.setTime(occurdate);
        cal.add(Calendar.HOUR, -sendMailRestrainLimitTime);

        Timestamp result = new Timestamp(cal.getTimeInMillis());
        long micro = TimeUnit.NANOSECONDS.toMicros(((Timestamp) occurdate).getNanos());
        result.setNanos((int) micro * 1000);

        return result;

    }

    /**
     * メッセージ通知キュー編集.
     * <pre>
     * メッセージ通知キュー登録値を編集する。
     * </pre>
     * @param message メッセージエンティティ
     * @return メッセージ通知キューエンティティ
     */
    private TmpQueueMailNotice setTmpQueueMailNotice(Message message)
    {

        TmpQueueMailNotice tmpQueueMailNotice = new TmpQueueMailNotice();
        // メール通知キューキー
        tmpQueueMailNotice.setTmp_queue_mail_notice_key(UUID.randomUUID().toString());
        // メッセージキー
        tmpQueueMailNotice.setMessage_key(message.getMessage_key());
        // 発生日時
        tmpQueueMailNotice.setOccur_date(message.getOccur_date());
        // メッセージNo
        tmpQueueMailNotice.setMessage_no(message.getMessage_no());
        // メール通知状況
        tmpQueueMailNotice.setMail_notice_status(MailNoticeStatus.NotNotice.getValue());

        // 登録イベントID
        tmpQueueMailNotice.setFirst_regist_event_id(message.getFirst_regist_event_id());
        // 登録従業員No
        tmpQueueMailNotice.setFirst_regist_user_number(message.getFirst_regist_user_number());
        // 登録従業員名
        tmpQueueMailNotice.setFirst_regist_user_name(message.getFirst_regist_user_name());
        // 登録日時
        tmpQueueMailNotice.setFirst_regist_datetime(message.getFirst_regist_datetime());
        // 更新イベントID
        tmpQueueMailNotice.setLast_regist_event_id(message.getLast_regist_event_id());
        // 更新従業員No
        tmpQueueMailNotice.setLast_regist_user_number(message.getLast_regist_user_number());
        // 更新従業員名
        tmpQueueMailNotice.setLast_regist_user_name(message.getLast_regist_user_name());
        // 更新日時
        tmpQueueMailNotice.setLast_regist_datetime(message.getLast_regist_datetime());

        return tmpQueueMailNotice;

    }

    /**
     * メッセージ取得.
     * @param resourceid リソースID
     * @param msgParamList パラメータ情報リスト
     * @param locale ロケール
     * @return メッセージ
     */
    private String getMessage(String resourceid, List<String> msgParamList, Locale locale)
    {

        // メッセージ取得
        String msgValue = MessagesHandler.getString(resourceid, locale);

        List<String> mesLineParams = new ArrayList<>();
        List<String> mesDetailparams = new ArrayList<>();

        for (int i = 0; i < msgParamList.size(); i++) {

            if (msgValue.indexOf("{" + i + "}") != -1) {
                // メッセージのパラメータ
                mesLineParams.add(msgParamList.get(i));

            }
            else if (!StringUtil.isNullOrEmpty(msgParamList.get(i))) {
                // スタックトレースなどの付属情報
                mesDetailparams.add(msgParamList.get(i));
            }
        }

        // メッセージのフォーマット
        MessageFormat messageFormat = new MessageFormat(msgValue);
        String mes = messageFormat.format(mesLineParams.toArray());

        // <br>を除去
        mes = Pattern.compile("<br>", Pattern.CASE_INSENSITIVE).matcher(mes).replaceAll("");

        // スタックトレースなどの付属情報の追加
        if (!mesDetailparams.isEmpty()) {

            mes = mes + StringUtils.LIEN_SEPARATOR_RESOURCE;

            for (int i = 0; i < mesDetailparams.size(); i++) {
                mes = mes + mesDetailparams.get(i);
            }

        }

        return mes;

    }

    /**
     * @return serviceRequest
     */
    public Object getServiceRequest()
    {
        return serviceRequest;
    }

    /**
     * @param serviceRequest セットする serviceRequest
     */
    public void setServiceRequest(Object serviceRequest)
    {
        this.serviceRequest = serviceRequest;
    }

    /**
     * サービスのリクエストからアップロードファイル取得
     * @return アップロードファイル
     */
    public List<InputPart> getServiceRequestFile()
    {
        MultipartFormDataInput multipartFormDataInput = (MultipartFormDataInput) serviceRequest;
        Map<String, List<InputPart>> inputPartMap = multipartFormDataInput.getFormDataMap();
        List<InputPart> inputParts = inputPartMap.get("file");
        return inputParts;
    }

    /**
     * サービスのリクエストからパラメータ値取得
     * @param name パラメータ名
     * @return パラメータ値
     * @throws IOException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws Exception
     */
    public String getServiceRequestParam(String name) throws IOException, IllegalArgumentException,
            IllegalAccessException
    {
        String result = null;

        if (serviceRequest instanceof MultipartFormDataInput) {
            MultipartFormDataInput multipartFormDataInput = (MultipartFormDataInput) serviceRequest;
            Map<String, List<InputPart>> inputPartMap = multipartFormDataInput.getFormDataMap();

            // ファイル取得
            List<InputPart> inputParts = inputPartMap.get(name);
            if (inputParts == null || inputParts.size() != 1) {
                return null;
            }

            InputStream inputStream = inputParts.get(0).getBody(InputStream.class, null);
            byte[] bytes = IOUtils.toByteArray(inputStream);

            result = new String(bytes, "UTF-8");
        }
        else {
            Field fld;
            try {
                fld = serviceRequest.getClass().getDeclaredField(name);
            }
            catch (NoSuchFieldException e) {
                return null;
            }
            catch (SecurityException e) {
                return null;
            }
            fld.setAccessible(true);
            result = (String) fld.get(serviceRequest);
        }
        return result;
    }

    /**
     * サービスのリクエストからパラメータ値取得
     * @param name パラメータ名
     * @return パラメータ値
     * @throws IOException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws Exception
     */
    public Object getServiceRequestParamObject(String name) throws IllegalArgumentException, IllegalAccessException
    {

        try {
            Field fld = serviceRequest.getClass().getDeclaredField(name);
            fld.setAccessible(true);
            return fld.get(serviceRequest);

        }
        catch (NoSuchFieldException e) {
            return null;
        }
        catch (SecurityException e) {
            return null;
        }

    }

    /**
     * リクエストからパラメータ値取得
     * @param requestParamName パラメータ名
     * @return 取得値
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws IOException
     */
    public Object getRequestParamValue(String requestParamName) throws IllegalAccessException, IOException
    {

        if (this.getServiceRequest() != null) {
            return this.getServiceRequestParam(requestParamName);
        }
        else {
            // リクエストパラメータ取得
            if (ServletContainer.USE_ATTRIBUTE) {
                // Forwardの時は、Forward時にセットされたforwardParameters優先
                // なければ、リクエストから取得
                if (this.isForwardAttribute()) {
                    return this.getAttributeParameter(requestParamName);
                }
                // リダイレクト
                else if (this.isRedirect()) {
                    return this.getRedirectParameter(requestParamName);
                }
            }
            else {
                if (this.isRedirect()) {
                    return this.getRedirectParameter(requestParamName);
                }
            }
        }
        return null;
    }

    /**
     * リクエストからパラメータ値取得
     * @param requestParamName パラメータ名
     * @return 取得値
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws IOException
     */
    public Object getRequestParamValueWithWapper(String requestParamName) throws IllegalAccessException, IOException
    {

        Object requestParamValue = getRequestParamValue(requestParamName);

        if (requestParamValue == null && this.getWrapperRequest() != null) {
            //            requestParamValue = this.getWrapperRequest().getParameter(requestParamName);

            // サニタイズ処理なし
            if (this.getWrapperRequest() instanceof SanitizeHttpServletRequestWrapper) {
                SanitizeHttpServletRequestWrapper wrapper = (SanitizeHttpServletRequestWrapper) this.getWrapperRequest();

                requestParamValue = wrapper.getRequest().getParameter(requestParamName);
            }
        }

        return requestParamValue;
    }

    /**
     * リクエストからパラメータ値取得（配列）
     * @param requestParamName パラメータ名
     * @return 取得値
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws IOException
     */
    public Object getRequestParamValues(String requestParamName) throws IllegalAccessException, IOException
    {

        if (this.getServiceRequest() != null) {
            return this.getServiceRequestParam(requestParamName);
        }
        else {
            // リクエストパラメータ取得
            if (ServletContainer.USE_ATTRIBUTE) {
                // Forwardの時は、Forward時にセットされたforwardParameters優先
                // なければ、リクエストから取得
                if (this.isForwardAttribute()) {
                    return this.getAttributeParameter(requestParamName);
                }
                // リダイレクト
                else if (this.isRedirect()) {
                    return this.getRedirectParameter(requestParamName);
                }
            }
            else {
                if (this.isRedirect()) {
                    return this.getRedirectParameter(requestParamName);
                }
            }
        }
        return null;
    }

    /**
     * リクエストからパラメータ値取得(配列)
     * @param requestParamName パラメータ名
     * @return 取得値
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws IOException
     */
    public Object getRequestParamValuesWithWapper(String requestParamName) throws IllegalAccessException, IOException
    {

        Object requestParamValue = getRequestParamValues(requestParamName);

        if (requestParamValue == null && this.getWrapperRequest() != null) {
            //            requestParamValue = this.getWrapperRequest().getParameterValues(requestParamName);
            // サニタイズ処理なし
            if (this.getWrapperRequest() instanceof SanitizeHttpServletRequestWrapper) {
                SanitizeHttpServletRequestWrapper wrapper = (SanitizeHttpServletRequestWrapper) this.getWrapperRequest();

                requestParamValue = wrapper.getRequest().getParameterValues(requestParamName);
            }
        }

        return requestParamValue;
    }

    /**
     * リクエストからパラメータ値取得（JSON)
     * @param requestParamName パラメータ名
     * @return 取得値（JSON 文字列)
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws IOException
     */
    public Object getRequestParamValueJson(String requestParamName) throws IllegalAccessException, IOException
    {

        Object requestParamValue = this.getRequestParamValue(requestParamName);

        if (requestParamValue == null) {
            // JSONはサニタイズ処理なし
            if (this.getWrapperRequest() instanceof SanitizeHttpServletRequestWrapper) {
                SanitizeHttpServletRequestWrapper wrapper = (SanitizeHttpServletRequestWrapper) this.getWrapperRequest();

                requestParamValue = wrapper.getRequest().getParameter(requestParamName);
            }
            else {
                if (this.getWrapperRequest() != null) {
                    requestParamValue = this.getWrapperRequest().getParameter(requestParamName);
                }
            }
        }

        return requestParamValue;
    }

    /**
     * リクエストから検索情報のパラメータ値取得)
     * @param searchTableId テーブルID
     * @return 検索条件
     * @throws Exception
     */
    public SearchSetting getRequestSearchSetting(String tableId) throws Exception
    {
        // リクエスト情報より取得する
        String requestParamValue = (String) this.getRequestParamValueJson(REQUEST_NAME_SEARCH_SETTING_MAP);
        Map<String, SearchSetting> serachMap = ConverterUtils.readJson(requestParamValue,
                new TypeReference<Map<String, SearchSetting>>()
                {
                });
        return serachMap.get(tableId);
    }

    /**
     * 初期処理
     */
    @PostConstruct
    private void init()
    {
        // トレースモニター
        traceMonitorList = new ArrayList<>();
    }

    /**
     * 後処理
     * @throws ParseException
     */
    @PreDestroy
    public void destroy()
    {

        if (!CommonEnums.TraceMonitorWrite.None.getValue() && CommonEnums.TraceMonitorWrite.End.getValue()) {

            if (!this.isThrowException) {

                // トレースモニター情報ログ
                if (this.existTraceMonitor()) {
                    StringBuilder trace = new StringBuilder(128);
                    trace.append(System.getProperty("line.separator"));
                    trace.append("---- TRACE MESSAGE (START) ----");
                    trace.append(System.getProperty("line.separator"));
                    trace.append(this.dumpTraceMonitor());
                    trace.append(System.getProperty("line.separator"));
                    trace.append("---- TRACE MESSAGE (END) ----");
                    trace.append(System.getProperty("line.separator"));
                    this.logHelper.fine(this.logger, null, null, trace.toString());
                }

            }
        }

    }

    /**
     * エンティティを監査証跡対象に加える
     *
     * @param 監査対象とするエンティティ
     */
    public void addAuditTrailList(BaseEntity entity)
    {
        entity.setReq(this);
    }

    /**
     * エンティティを監査証跡対象に加える
     *
     * @param entityList 監査対象とするエンティティ（リスト）
     */
    public <T> void addAuditTrailList(List<T> entityList)
    {
        for (int i = 0; i < entityList.size(); i++) {
            if (entityList.get(i) instanceof BaseEntity) {
                ((BaseEntity) (entityList.get(i))).setReq(this);
            }
        }

    }

    /**
     * ラッパーリクエストを取得する。
     * @return wrapperRequest
     */
    public HttpServletRequest getWrapperRequest()
    {
        return wrapperRequest;
    }

    /**
     * ラッパーリクエストを設定する。
     * @param wrapperRequest
     */
    public void setWrapperRequest(HttpServletRequest wrapperRequest)
    {
        this.wrapperRequest = wrapperRequest;
    }

    /**
     *
     * 画面遷移時に引き継ぎするリクエスト情報
     *
     */
    public static class REQUEST
    {

        //リダイレクトされたリクエストかどうか
        private boolean             redirect           = false;

        //最初の URI
        private String              baseUri            = null;

        //リダイレクトで実行するコマンド名称
        private String              redirectCommand    = null;

        //リダイレクトで引き渡すパラメーター
        private Map<String, Object> redirectParameters = null;

        //トレースモニター
        private List<Object>        traceMonitorList   = null;

        public REQUEST()
        {
        }

        public boolean isRedirect()
        {
            return redirect;
        }

        public void setRedirect(boolean redirect)
        {
            this.redirect = redirect;
        }

        public String getBaseUri()
        {
            return baseUri;
        }

        public void setBaseUri(String baseUri)
        {
            this.baseUri = baseUri;
        }

        public String getRedirectCommand()
        {
            return redirectCommand;
        }

        public void setRedirectCommand(String redirectCommand)
        {
            this.redirectCommand = redirectCommand;
        }

        public Map<String, Object> getRedirectParameters()
        {
            if (redirectParameters == null) {
                return new HashMap<>();
            }
            return redirectParameters;
        }

        public void setRedirectParameters(Map<String, Object> redirectParameters)
        {
            this.redirectParameters = redirectParameters;
        }

        public List<Object> getTraceMonitorList()
        {
            return traceMonitorList;
        }

        public void setTraceMonitorList(List<Object> traceMonitorList)
        {
            this.traceMonitorList = traceMonitorList;
        }

    }

    /**
     * ログ有無判定.
     * <pre>
     * ログ有無の判定を行い、「ログあり」が設定されている場合、ログの出力を行う。
     * </pre>
     * @param messageInfo メッセージ情報
     */
    private void isLogging(MessageInfo messageInfo)
    {

        if (IsLogging.TRUE.getValue() == messageInfo.getIsLogging()) {

            String message = this.getMessage(messageInfo.getResourceId(), messageInfo.getMsgParamList(),
                    this.getUserLocale());

            //ログがINFOかSEVEREかを判定
            // カテゴリがエラーの場合はSEVIREにする
            if(messageInfo.getCategory() == MessageCategory.MessageCategory_Error.getValue()){
                this.logHelper.severe(this.logger, null, null, message);
            }
            else {
                this.logHelper.info(this.logger, null, null, message);
            }

        }

    }

    /**
     * ログ出力
     * @param messageInfo メッセージ情報
     * @param childMessageDatas 子メッセージデータ
     */
    private void isLogging(MessageInfo messageInfo, MessageData[] childMessageDatas)
    {

        if (IsLogging.TRUE.getValue() == messageInfo.getIsLogging()) {

            StringBuilder msgBuild = new StringBuilder(128);

            String message = this.getMessage(messageInfo.getResourceId(), messageInfo.getMsgParamList(),
                    this.getUserLocale());

            msgBuild.append(message);

            if (childMessageDatas != null) {

                // 子のメッセージをセット
                for (MessageData messageData : childMessageDatas) {
                    if (msgBuild.length() > 0) {
                        msgBuild.append(StringUtils.LIEN_SEPARATOR_RESOURCE);
                    }
                    message = MessagesHandler.getString(messageData.getMessageNo(), getUserLocale(),
                            messageData.getParams());

                    msgBuild.append(message);
                }
            }

            //ログがINFOかSEVEREかを判定
            // カテゴリがエラーの場合はSEVIREにする
            if(messageInfo.getCategory() == MessageCategory.MessageCategory_Error.getValue()){
                this.logHelper.severe(this.logger, null, null, msgBuild.toString(), messageInfo.getMessageNo());
            }
            else {
                this.logHelper.info(this.logger, null, null, msgBuild.toString());
            }

        }

    }

	/**
	 * パトランプIDからIPアドレス、点灯パラメータを取得
	 * @param patliteId パトランプID
	 * @return パトライトデータ（IPアドレス、点灯パラメータ）
	 * @throws GnomesAppException
	 */
	private PatlampData getPatliteData(String patliteId) throws GnomesAppException {

		PatlampData data = new PatlampData();

		// パトランプマスタ取得
		MstrPatlamp mstrPtlamp = mstrPatlampDao.getMstrPatlamp(patliteId);

        if (Objects.isNull(mstrPtlamp)) {
            // ME01.0185:「該当するパトランプ機種の登録がありません。\nパトランプ機種の登録を確認してください。」
            throw this.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0185);
		}

		// IPアドレスを設定
		data.setIp_address(mstrPtlamp.getIp_address());

		//点灯鳴動パラメーター文字列を取得
		List<String> LightSoundParameterStringList = new ArrayList<String>();
		//点灯鳴動パラメーター文字列01
		if(mstrPtlamp.getLight_sound_parameter_string_01() != null && !mstrPtlamp.getLight_sound_parameter_string_01().isEmpty()){
			LightSoundParameterStringList.add(CommonConstants.TALEND_CONTEXT_PARAM_HEADER + mstrPtlamp.getLight_sound_parameter_string_01());
		}
		//点灯鳴動パラメーター文字列02
		if(mstrPtlamp.getLight_sound_parameter_string_02() != null && !mstrPtlamp.getLight_sound_parameter_string_02().isEmpty()){
			LightSoundParameterStringList.add(CommonConstants.TALEND_CONTEXT_PARAM_HEADER + mstrPtlamp.getLight_sound_parameter_string_02());
		}
		//点灯鳴動パラメーター文字列03
		if(mstrPtlamp.getLight_sound_parameter_string_03() != null && !mstrPtlamp.getLight_sound_parameter_string_03().isEmpty()){
			LightSoundParameterStringList.add(CommonConstants.TALEND_CONTEXT_PARAM_HEADER + mstrPtlamp.getLight_sound_parameter_string_03());
		}
		//点灯鳴動パラメーター文字列04
		if(mstrPtlamp.getLight_sound_parameter_string_04() != null && !mstrPtlamp.getLight_sound_parameter_string_04().isEmpty()){
			LightSoundParameterStringList.add(CommonConstants.TALEND_CONTEXT_PARAM_HEADER + mstrPtlamp.getLight_sound_parameter_string_04());
		}
		//点灯鳴動パラメーター文字列05
		if(mstrPtlamp.getLight_sound_parameter_string_05() != null && !mstrPtlamp.getLight_sound_parameter_string_05().isEmpty()){
			LightSoundParameterStringList.add(CommonConstants.TALEND_CONTEXT_PARAM_HEADER + mstrPtlamp.getLight_sound_parameter_string_05());
		}
		data.setLightSoundParameterStringList(LightSoundParameterStringList);

		return data;
	}

	/**
	 * JSONからIPアドレス、talendジョブ名を取得
	 * @param patliteId パトランプID
	 * @return Talendパラメータ
	 * @throws GnomesAppException
	 * @throws JsonProcessingException
	 */
	private String[] getPatliteDataFromJSON(Map<String, String> paramMap) throws GnomesAppException, JsonProcessingException {

		PatlampData data = new PatlampData();

		// パトランプマスタ取得
		//MstrPatlamp mstrPtlamp = mstrPatlampDao.getMstrPatlamp(patliteId);

    	// コンテンツパラメータ作成
    	List<String> contextParamList = new ArrayList<String>();

		// IPアドレス
		String ipAddress = CommonConstants.TALEND_CONTEXT_PARAM_HEADER + TalendParameterName.IP_ADDRESS.getValue() + "=" + paramMap.get(TalendParameterName.IP_ADDRESS.getValue());
    	contextParamList.add(ipAddress);

    	// サウンドパターン
    	String soundData = CommonConstants.TALEND_CONTEXT_PARAM_HEADER + TalendParameterName.SOUND_DATA.getValue() + "=" + paramMap.get(TalendParameterName.SOUND_DATA.getValue());
    	contextParamList.add(soundData);

    	// パトライトパラメータ名(点灯消灯)
    	String commandType = CommonConstants.TALEND_CONTEXT_PARAM_HEADER + TalendParameterName.COMMAND_TYPE.getValue() + "=" + paramMap.get(TalendParameterName.COMMAND_TYPE.getValue());
    	contextParamList.add(commandType);

		// 点灯状態
    	String lightData = CommonConstants.TALEND_CONTEXT_PARAM_HEADER + TalendParameterName.LIGHTS_DATA.getValue() + "=" + ConverterUtils.getJson(paramMap.get(TalendParameterName.LIGHTS_DATA.getValue()));
    	contextParamList.add(lightData);

		String[] contextParamArray = contextParamList.toArray(new String[contextParamList.size()]);

		return contextParamArray;
	}


}
