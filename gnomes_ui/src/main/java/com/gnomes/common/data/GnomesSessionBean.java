package com.gnomes.common.data;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.inject.Named;

import org.picketbox.util.StringUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exportdata.ExportDataTableDef;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.PageTokenGenerate;
import com.gnomes.common.util.StringUtils;
import com.gnomes.system.data.PopupMessageInfo;

/**
 * セッションビーン
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/11/24 YJP/Y.Oota                  初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Named
@SessionScoped
public class GnomesSessionBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Inject
    transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected transient LogHelper logHelper;

    @Inject
    protected BeanManager beanManager;

    @Inject
    protected GnomesEjbBean ejbBean;

    private int count = 0;

    /** ユーザKey */
    private String userKey;

    /** ユーザID */
    private String userId;

    /** ユーザ名 */
    private String userName;

    /** パスワード */
    private String password;

    /** ロケールID */
    private String localeId;

    /** 言語 */
    private String language;

    /** IPアドレス */
    private String ipAddress;

    /** 端末ID */
    private String computerId;

    /** 端末名 */
    private String computerName;

    /** 初期設定時端末名 */
    private String initComputerName;

    /** エリアID */
    private String areaId;

    /** エリア名 */
    private String areaName;

    /** 拠点コード */
    private String siteCode;

    /** 拠点名 */
    private String siteName;

    /** 領域区分 */
    private String regionType;

    /** 指図工程コード */
    private String orderProcessCode;

    /** 指図工程名 */
    private String orderProcessName;

    /** 作業工程コード */
    private String workProcessCode;

    /** 作業工程名 */
    private String workProcessName;

    /** 作業場所コード */
    private String workCellCode;

    /** 作業場所名 */
    private String workCellName;

    /** トップ画面ID */
    private String topScreenId;

    /** トップ画面名 */
    private String topScreenName;

    /** スクリーンロック中か否か */
    private Boolean isScreenLocked;

    /** スクリーンロック起動時間(分） */
    private Integer screenLockTimeoutTime;

    /** メッセージリスト */
    private List<PopupMessageInfo> messageList;

    /** メッセージ一覧画面最大表示件数 */
    private Integer maxListDisplayCount;

    /** ポップアップメッセージ表示件数 */
    private Integer popupDisplayCount;

    /** ポップアップメッセージ監視周期（分） */
    private Integer watchPeriodForPopup;

    /**
     * 延命措置用FunctionBeanストア
     */
    private Map<String,String> arrivalFunctionBeanMap = new HashMap<>();

    /** ページトークン
     * WindowsID単位で管理する
    */
    private Map<String, String> pageTokenMap = new HashMap<>();

    /**
     * JSONでシリアライズされたページトークン情報
     */
    private String pageToken;

    /** インポートエラーファイル */
    private FileUpLoadData requestImpErrFile;

    /** インポートエラー情報 */
    private Map<String, String[]> requestImpErr;

    /** 一覧エクスポート情報 */
    private ExportDataTableDef exportDataTableDef;

    /** cidマップリスト */
    private List<CidMap> cidMapList = new ArrayList<>();

    /** windowIdリスト */
    private List<String> windowIdList = new ArrayList<>();

    /** ブックマーク画面IDリスト */
    private List<String> bookMarkedScreenIdList = new ArrayList<>();

    /** ログイン画面遷移URL */
    private String loginUrl;

    /**
     * 遷移画面IDスタック
     * key    String             :windowId
     * value  List<String>   :遷移画面IDスタック
     */
    private Map<String, List<String>> screeenIdStack = new HashMap<String, List<String>>();

    /**
     * 遷移画面IDスタックに存在するか
     * @param windowId ウィンドウID
     * @param screenId 画面ID
     * @return true:存在する false:存在しない
     */
    public boolean existScreeenStack(String windowId, String screenId) {
        synchronized (screeenIdStack) {

            boolean exist = false;
            List<String> scIdList = screeenIdStack.get(windowId);

            if (scIdList != null) {
                for (String scId : scIdList) {
                    if (scId.equals(screenId)) {
                        exist = true;
                        break;
                    }
                }
            }
            //        dumpScreenIdList("existScreeenStack",windowId,screenId,exist);

            return exist;
        }
    }

    /**
     * 直前の戻る画面IDを取得
     * @param windowId ウィンドウID
     * @return 画面ID
     */
    public String getPreviousScreenId(String windowId) {
        synchronized (screeenIdStack) {

            String previousScreenId = null;
            List<String> scInfoList = screeenIdStack.get(windowId);
            if (scInfoList != null && scInfoList.size() > 1) {
                previousScreenId = scInfoList.get(scInfoList.size() - 2);
            }
            //        dumpScreenIdList("getPreviousScreenId",windowId,"---",previousScreenId);
            return previousScreenId;
        }
    }

    /**
     * 遷移画面IDスタックに画面IDを追加
     * @param windowId ウィンドウID
     * @param screenId 画面ID
     */
    public void pushScreeenStack(String windowId, String screenId) {
        synchronized (screeenIdStack) {

            List<String> scIdList = screeenIdStack.get(windowId);
            if (scIdList == null) {
                scIdList = new ArrayList<String>();
                scIdList.add(screenId);
                screeenIdStack.put(windowId, scIdList);
            } else {
                //過去に同一ScreenIdがあったら、その先を削除する
                int foundIndex = scIdList.lastIndexOf(screenId);
                int scIdListSize = scIdList.size();
                if (foundIndex >= 0) {
                    for (int i = foundIndex + 1; i < scIdListSize; i++) {
                        // foundIndex + 1以降の要素を削除する
                        scIdList.remove(foundIndex + 1);
                    }
                }
                //無かったらPushするだけ
                else {
                    scIdList.add(screenId);
                }
            }
            //        dumpScreenIdList("pushScreeenStack",windowId,screenId,"void");
        }
    }

    /**
     * 遷移画面IDスタックより削除
     * @param windowId ウィンドウID
     * @param screenId 画面ID
     */
    public void returnScreeenStack(String windowId, String screenId) {
        synchronized (screeenIdStack) {
            List<String> scIdList = screeenIdStack.get(windowId);
            if (scIdList != null) {
                //List<String> delList = new ArrayList<String>();
                // upd 20181105 strt ---- 初期表示、連発対応
                //            int index = scIdList.indexOf(screenId);
                int index = scIdList.lastIndexOf(screenId);
                // upd 20181105 end ----
                int scIdListSize = scIdList.size();
                if (index >= 0) {
                    for (int i = index + 1; i < scIdListSize; i++) {
                        //delList.add(scIdList.get(i));
                        // index + 1以降の要素を削除する
                        scIdList.remove(index + 1);
                    }
                } else {
                    dumpScreenIdList("returnScreeenStack", windowId, screenId, "void");
                    throw new IllegalArgumentException(
                            "not found windowId=[" + windowId + "] screenId=[" + screenId + "]  in screenIdStack");
                }

            } else {
                dumpScreenIdList("returnScreeenStack", windowId, screenId, "void");
                throw new IllegalArgumentException("not found windowId=[" + windowId + "] in screenIdStack");
            }
        }
        //dumpScreenIdList("returnScreeenStack",windowId,screenId,"void");
    }

    /*
     * [2019/09/17 浜本記載]
     *      デバッグのために残す
     */
    private void dumpScreenIdList(String methodName, String windowId, String screenId, Object retVal) {
        StringBuilder sb = new StringBuilder();
        sb.append("--------------------------------------\n");
        sb.append("---------Screen Stack Dump------------\n");
        sb.append("Method = " + methodName + " windowId = " + windowId + " screenId = " + screenId + " return = "
                + retVal.toString() + "\n");
        int i = 0;
        for (String scid : screeenIdStack.get(windowId)) {
            sb.append(i++);
            sb.append(": ");
            sb.append("[" + scid + "]");
            sb.append("\n");
            sb.append("--------------------------------------\n");
        }
        System.out.println(sb.toString());
    }

    /**
     * 遷移画面IDスタックをクリア
     * @param windowId ウィンドウID
     * @param screenId 画面ID
     */
    public void clearScreenStack(String windowId, String screenId) {
        synchronized (screeenIdStack) {

            screeenIdStack.remove(windowId);
            pushScreeenStack(windowId, screenId);
            //        dumpScreenIdList("clearScreenStack",windowId,screenId,"void");

        }
    }

    /**
     * ユーザKeyを取得
     * @return userKey
     */
    public String getUserKey() {
        return userKey;
    }

    /**
     * ユーザKeyを設定
     * @param userKey ユーザKey
     */
    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    /**
     * ユーザIDを取得
     * @return userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * ユーザIDを設定
     * @param userId ユーザID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * ユーザ名を取得
     * @return userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * ユーザ名を設定
     * @param userName ユーザ名
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * パスワードを取得
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * パスワードを設定
     * @param password パスワード
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * ロケールIDを取得
     * @return localeId
     */
    public String getLocaleId() {
        return localeId;
    }

    /**
     * ロケールIDを設定
     * @param localeId ロケールID
     */
    public void setLocaleId(String localeId) {
        this.localeId = localeId;
    }

    /**
     * ロケールIDよりロケールを取得
     * @return ロケール
     */
    public Locale getUserLocale() {
        if (this.localeId != null) {
            String[] params = this.localeId.split(CommonConstants.SPLIT_CHAR);
            if (params.length >= 2) {
                return new Locale(params[0], params[1]);
            } else {
                return Locale.getDefault();
            }
        } else {
            return Locale.getDefault();
        }
    }

    /**
     * ロケールIDよりタイムゾーンを取得
     * @return タイムゾーン
     */
    public TimeZone getUserTimeZone() {
        if (this.localeId != null) {
            String[] params = this.localeId.split(CommonConstants.SPLIT_CHAR);
            if (params.length >= 3) {
                String selectTimeZone = "";
                for(int i = 2; i < params.length; i++){
                    selectTimeZone = selectTimeZone + params[i] + CommonConstants.SPLIT_CHAR;
                }
                selectTimeZone = selectTimeZone.substring(0, selectTimeZone.length()-1);
                return TimeZone.getTimeZone(selectTimeZone);
            } else {
                return TimeZone.getDefault();
            }
        } else {
            return TimeZone.getDefault();
        }
    }

    /**
     * 言語を取得
     * @return language
     */
    public String getLanguage() {
        if (language == null || language.isEmpty()) {
            return Locale.getDefault().toString();
        } else {
            return language;
        }
    }

    /**
     * 言語を設定
     * @param language 言語
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * IPアドレスを取得
     * @return ipAddress
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * IPアドレスを設定
     * @param ipAddress IPアドレス
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * 端末IDを取得
     * @return computerId
     */
    public String getComputerId() {
        return computerId;
    }

    /**
     * 端末IDを設定
     * @param computerId 端末ID
     */
    public void setComputerId(String computerId) {
        this.computerId = computerId;
    }

    /**
     * 端末名を取得
     * @return computerName
     */
    public String getComputerName() {
        if (computerName != null && !computerName.equals("")) {
            return computerName;
        } else {
            return initComputerName;
        }
    }

    /**
     * 端末名を設定
     * @param computerName 端末名
     */
    public void setComputerName(String computerName) {
        this.computerName = computerName;
    }

    /**
     * 初期設定時端末名を設定
     * @param computerName 初期設定時端末名
     */
    public void setInitComputerName(String computerName) {
        this.initComputerName = computerName;
    }

    /**
     * エリアIDを取得
     * @return areaId
     */
    public String getAreaId() {
        return areaId;
    }

    /**
     * エリアIDを設定
     * @param areaId エリアID
     */
    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    /**
     * エリア名を取得
     * @return areaName
     */
    public String getAreaName() {
        return areaName;
    }

    /**
     * エリア名を設定
     * @param areaName エリア名
     */
    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    /**
     * 拠点コードを取得
     * @return siteCode
     */
    public String getSiteCode() {
        //もし、取得元がバッチ系だった場合、ejbBeanのサイトコードを入手する
        if (this.ejbBean.isEjbBatch()) {
            return ejbBean.getSiteCode();
        } else {
            return siteCode;
        }
    }

    /**
     * 拠点コードを設定
     * @param siteCode 拠点コード
     */
    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    /**
     * 拠点名を取得
     * @return siteName
     */
    public String getSiteName() {
        //もし、取得元がバッチ系だった場合、ejbBeanのサイト名称を入手する
        if (this.ejbBean.isEjbBatch()) {
            return ejbBean.getSiteName();
        } else {
            return siteName;
        }
    }

    /**
     * 拠点名を設定
     * @param siteName 拠点名
     */
    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    /**
     * 領域区分を取得
     * @return 領域区分
     */
    public String getRegionType() {
        //もし、取得元がバッチ系だった場合、ejbBeanの領域を入手する
        if (this.ejbBean.isEjbBatch()) {
            return this.ejbBean.getRegionType();
        } else {
            return regionType;
        }
    }

    /**
     * 領域区分を設定
     * @param regionType 領域区分
     */
    public void setRegionType(String regionType) {

        //        if ((!Objects.isNull(this.entityManager))
        //                && this.entityManager.isOpen()) {
        //            this.entityManager.close();
        //
        //        }
        this.regionType = regionType;

    }

    /**
     * 指図工程コードを取得
     * @return orderProcessCode
     */
    public String getOrderProcessCode() {
        return orderProcessCode;
    }

    /**
     * 指図工程コードを設定
     * @param orderProcessCode 指図工程コード
     */
    public void setOrderProcessCode(String orderProcessCode) {
        this.orderProcessCode = orderProcessCode;
    }

    /**
     * 指図工程名を取得
     * @return orderProcessName
     */
    public String getOrderProcessName() {
        return orderProcessName;
    }

    /**
     * 指図工程名を設定
     * @param orderProcessName 指図工程名
     */
    public void setOrderProcessName(String orderProcessName) {
        this.orderProcessName = orderProcessName;
    }

    /**
     * 作業工程コードを取得
     * @return workProcessCode
     */
    public String getWorkProcessCode() {
        return workProcessCode;
    }

    /**
     * 作業工程コードを設定
     * @param workProcessCode 作業工程コード
     */
    public void setWorkProcessCode(String workProcessCode) {
        this.workProcessCode = workProcessCode;
    }

    /**
     * 作業工程名を取得
     * @return workProcessName
     */
    public String getWorkProcessName() {
        return workProcessName;
    }

    /**
     * 作業工程名を設定
     * @param workProcessName 作業工程名
     */
    public void setWorkProcessName(String workProcessName) {
        this.workProcessName = workProcessName;
    }

    /**
     * 作業場所コードを取得
     * @return workCellCode
     */
    public String getWorkCellCode() {
        return workCellCode;
    }

    /**
     * 作業場所コードを設定
     * @param workCellCode 作業場所コード
     */
    public void setWorkCellCode(String workCellCode) {
        this.workCellCode = workCellCode;
    }

    /**
     * 作業場所名を取得
     * @return workCellName
     */
    public String getWorkCellName() {
        return workCellName;
    }

    /**
     * 作業場所名を設定
     * @param workCellName 作業場所名
     */
    public void setWorkCellName(String workCellName) {
        this.workCellName = workCellName;
    }

    /**
     * トップ画面IDを取得
     * @return topScreenId
     */
    public String getTopScreenId() {
        return topScreenId;
    }

    /**
     * トップ画面IDを設定
     * @param topScreenId トップ画面ID
     */
    public void setTopScreenId(String topScreenId) {
        this.topScreenId = topScreenId;
    }

    /**
     * トップ画面名を取得
     * @return topScreenName
     */
    public String getTopScreenName() {
        return topScreenName;
    }

    /**
     * トップ画面名を設定
     * @param topScreenName トップ画面名
     */
    public void setTopScreenName(String topScreenName) {
        this.topScreenName = topScreenName;
    }

    /**
     * スクリーンロック中か否かを取得
     * @return isScreenLocked
     */
    public Boolean getIsScreenLocked() {
        return isScreenLocked;
    }

    /**
     * スクリーンロック中か否かを設定
     * @param isScreenLocked スクリーンロック中か否か
     */
    public void setIsScreenLocked(Boolean isScreenLocked) {
        this.isScreenLocked = isScreenLocked;
    }

    /**
     * スクリーンロック起動時間(分）を取得
     * @return screenLockTimeoutTime
     */
    public Integer getScreenLockTimeoutTime() {
        return screenLockTimeoutTime;
    }

    /**
     * スクリーンロック起動時間(分）を設定
     * @param screenLockTimeoutTime スクリーンロック起動時間(分）
     */
    public void setScreenLockTimeoutTime(Integer screenLockTimeoutTime) {
        this.screenLockTimeoutTime = screenLockTimeoutTime;
    }

    /**
     * メッセージリストを取得
     * @return messageList
     */
    public List<PopupMessageInfo> getMessageList() {
        return messageList;
    }

    /**
     * メッセージリストを設定
     * @param messageList メッセージリスト
     */
    public void setMessageList(List<PopupMessageInfo> messageList) {
        this.messageList = messageList;
    }

    /**
     * メッセージ一覧画面最大表示件数を取得
     * @return maxListDisplayCount
     */
    public Integer getMaxListDisplayCount() {
        return maxListDisplayCount;
    }

    /**
     * メッセージ一覧画面最大表示件数を設定
     * @param maxListDisplayCount メッセージ一覧画面最大表示件数
     */
    public void setMaxListDisplayCount(Integer maxListDisplayCount) {
        this.maxListDisplayCount = maxListDisplayCount;
    }

    /**
     * ポップアップメッセージ表示件数を取得
     * @return popupDisplayCount
     */
    public Integer getPopupDisplayCount() {
        return popupDisplayCount;
    }

    /**
     * ポップアップメッセージ表示件数を設定
     * @param popupDisplayCount ポップアップメッセージ表示件数
     */
    public void setPopupDisplayCount(Integer popupDisplayCount) {
        this.popupDisplayCount = popupDisplayCount;
    }

    /**
     * ポップアップメッセージ監視周期（分）を取得
     * @return watchPeriodForPopup
     */
    public Integer getWatchPeriodForPopup() {
        return watchPeriodForPopup;
    }

    /**
     * ポップアップメッセージ監視周期（分）を設定
     * @param watchPeriodForPopup ポップアップメッセージ監視周期（分）
     */
    public void setWatchPeriodForPopup(Integer watchPeriodForPopup) {
        this.watchPeriodForPopup = watchPeriodForPopup;
    }

    /**
     *
     * ページトークンがMap管理化されたので既存の関数はMapをJSONシリアライズ
     * ＋サニタイズされた文字を取得する。
     * （現時点では呼ばれる箇所無し：将来用）
     *
     * @return pageTokenMapをシリアライズ、JSON化、エスケープした文字
     * @throws JsonProcessingException
     */
    public String getPageToken() throws JsonProcessingException {
        String jsonString = ConverterUtils.getJson(this.pageTokenMap);
        return StringUtils.getStringEscapeHtml(jsonString);
    }

    /**
     * エスケープ化されたJSONデータのPageTokenMapの文字をデシリアライズ
     * してメンバーに設定する
     * （現時点では呼ばれる箇所無し：将来用）
     *
     * @param pageToken エスケープ化されたJSONデータのPageTokenMapの文字
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void setPageTokenMap(String pageToken) throws Exception {
        this.pageTokenMap = ConverterUtils.readJson(pageToken, new TypeReference<Map<String, String>>() {
        });
    }

    /**
     * 指定したWindowIdをキーにPageTokenマップからページトークン値を入手する
     *
     * @param windowId  キーになるウインドウID（nullの場合はデフォルトIDになる）
     * @return 入手したページトークン文字列
     */
    public String getPageToken(String windowId) {

        //ログイン時などウインドウIDがnullの場合はセッションビーンとして共通にする
        String checkwindowId;
        if (StringUtil.isNullOrEmpty(windowId)) {
            checkwindowId = GnomesSessionBean.class.getName();
        } else {
            checkwindowId = windowId;
        }

        return this.pageTokenMap.get(checkwindowId);
    }

    /**
     * 指定したwindowIDにある内容をパラメータページトークンで設定（更新）する。
     * キーがない場合（新しい）追加される
     *
     * @oaram windowId      キーになるウインドウID
     * @param pageToken     設定したいページトークン
     */
    public void setPageToken(String windowId, String pageToken) {
        //ログイン時などウインドウIDがnullの場合はセッションビーンとして共通にする
        String checkwindowId;

        //ウインドウIDがnullならばデフォルト位置
        if (StringUtil.isNullOrEmpty(windowId)) {
            checkwindowId = GnomesSessionBean.class.getName();
        } else {
            checkwindowId = windowId;
        }

        //mapに存在しなければ追加で、存在すればリプレース
        if (this.pageTokenMap.containsKey(checkwindowId)) {
            this.pageTokenMap.replace(checkwindowId, pageToken);
        } else {
            this.pageTokenMap.put(checkwindowId, pageToken);
        }
    }

    /**
     * インポートエラーファイルを取得
     * @return インポートエラーファイル
     */
    public FileUpLoadData getRequestImpErrFile() {
        return requestImpErrFile;
    }

    /**
     * インポートエラーファイルを設定
     * @param requestImpErrFile
     */
    public void setRequestImpErrFile(FileUpLoadData requestImpErrFile) {
        this.requestImpErrFile = requestImpErrFile;
    }

    /**
     * インポートエラー情報を取得
     * @return インポートエラー情報
     */
    public Map<String, String[]> getRequestImpErr() {
        return requestImpErr;
    }

    /**
     * インポートエラー情報を設定
     * @param requestImpErr
     */
    public void setRequestImpErr(Map<String, String[]> requestImpErr) {
        this.requestImpErr = requestImpErr;
    }

    /**
     * @return exportDataTableDef
     */
    public ExportDataTableDef getExportDataTableDef() {
        return exportDataTableDef;
    }

    /**
     * @param exportDataTableDef セットする exportDataTableDef
     */
    public void setExportDataTableDef(ExportDataTableDef exportDataTableDef) {
        this.exportDataTableDef = exportDataTableDef;
    }

    /**
     * @return cidMapList
     */
    public List<CidMap> getCidMapList() {
        return cidMapList;
    }

    /**
     * @param cidMapList セットする cidMapList
     */
    public void setCidMapList(List<CidMap> cidMapList) {
        this.cidMapList = cidMapList;
    }

    /**
     * @return windowIdList
     */
    public List<String> getWindowIdList() {
        return windowIdList;
    }

    /**
     * @param windowIdList セットする windowIdList
     */
    public void setWindowIdList(List<String> windowIdList) {
        this.windowIdList = windowIdList;
    }

    /**
     * @return bookMarkedScreenIdList
     */
    public List<String> getBookMarkedScreenIdList() {
        return bookMarkedScreenIdList;
    }

    /**
     * @param bookMarkedScreenIdList セットする bookMarkedScreenIdList
     */
    public void setBookMarkedScreenIdList(List<String> bookMarkedScreenIdList) {
        this.bookMarkedScreenIdList = bookMarkedScreenIdList;
    }

    /**
     * ログイン画面遷移URLを取得
     * @return loginUrl
     */
    public String getLoginUrl() {
        return loginUrl;
    }

    /**
     * ログイン画面遷移URLを設定
     * @param loginUrl ログイン画面遷移URL
     */
    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    /**
     * 初期処理
     */
    @PostConstruct
    private void init() {
        this.logHelper.fine(this.logger, null, null, "@PostConstruct: " + this);
        System.out.println("[@SessionScoped] SessionBean post construct : " + this.getClass().getName());

    }

    /**
     * 後処理
     */
    @PreDestroy
    private void destroy() {
        this.logHelper.fine(this.logger, null, null, "@PreDestroy: " + this);
        System.out.println("[@SessionScoped] SessionBean pre destroy : " + this.getClass().getName());

    }

    public void clear() {
        this.userId = null;
        this.userName = null;
        this.password = null;
        this.localeId = null;
        this.language = null;
        this.ipAddress = null;
        this.computerName = null;
        this.areaId = null;
        this.areaName = null;
        this.siteCode = null;
        this.siteName = null;
        this.orderProcessCode = null;
        this.orderProcessName = null;
        this.workProcessCode = null;
        this.workProcessName = null;
        this.workCellCode = null;
        this.workCellName = null;
        this.siteName = null;
        this.regionType = null;
        //        this.entityManager = null;
        this.topScreenId = null;
        this.topScreenName = null;
        this.isScreenLocked = false;
        this.screenLockTimeoutTime = null;
        this.messageList = null;
        this.maxListDisplayCount = null;
        this.popupDisplayCount = null;
        this.watchPeriodForPopup = null;
        this.requestImpErrFile = null;
        this.requestImpErr = null;
        this.exportDataTableDef = null;
        this.loginUrl = null;

        this.cidMapList = new ArrayList<>();
        this.windowIdList = new ArrayList<>();
        this.bookMarkedScreenIdList = new ArrayList<>();
        this.screeenIdStack = new HashMap<String, List<String>>();
    }

    /**
     * ページトークンの再生成
     * @throws NoSuchAlgorithmException
     */
    public void pageTokenGenerate(String windowId) throws NoSuchAlgorithmException {
        //再生成
        String newPageToken = PageTokenGenerate.getCsrfToken();
        //同じキーだとリプレースされ、新規WindowIdなら生成される
        this.setPageToken(windowId, newPageToken);
    }

    /**
     * FunctionBeanが消去された際のCIDをリストから除外する
     * @param cid
     */
    public void deleteCidMapList(String cid) {
        for (CidMap cidmap : this.cidMapList) {
            if (cidmap.getCid().equals(cid)) {
                this.cidMapList.remove(cidmap);
                break;
            }
        }
    }

    /**
     * カウントアップ
     * @throws GnomesAppException
     */
    public void CountUp() throws GnomesAppException {
        // TODO 自動生成されたメソッド・スタブ
        count++;
        this.lifeExtensionMeasures();
    }

    public void DebugOutput(String where) throws GnomesAppException {
        synchronized(this){
            System.out.println("--------------------------------------------------------------------------------------");
            System.out.println("< Session Bean Dump >  " + where);
            System.out.println("|    ThreadId = " + Thread.currentThread().getName());
            System.out.println("|    instance = " + this.toString());
            System.out.println("|    hashCode = " + this.hashCode());
            System.out.println("|    count    = " + this.count);
            System.out.println("|    siteCode = " + this.siteCode);
            System.out.println("|    siteName = " + this.siteName);
            System.out.println("|    areaName = " + this.areaName);
            System.out.println("|    userId =   " + this.userId);
            System.out.println("|    userName = " + this.userName);
            System.out.println("|    initComputerName = " + this.initComputerName);
            System.out.println("|    isScreenLocked = " + this.isScreenLocked);
            System.out.println("--------------------------------------------------------------------------------------");

            for (String baseFunctionBean : this.arrivalFunctionBeanMap.values()) {
                BaseFunctionBean bean = getCDIInstance(baseFunctionBean);
                bean.DebugOutput();
            }
        }
    }

    /**
     * FunctionBeanが生成されたらそのクラス名を保持する
     *
     * @param baseFunctionBean FunctionBeanのクラス名
     */
    public void addFunctionBean(String fullClassName,String functionBeanName) {
        if(! this.arrivalFunctionBeanMap.containsKey(fullClassName)){
            this.arrivalFunctionBeanMap.put(fullClassName, functionBeanName);
        }
    }

    /**
     * FunctionBeanが破棄されたらそのクラス名を破棄する
     *
     * @param baseFunctionBean FunctionBeanのクラス名
     */
    public void removeFunctionBean(String fullClassName,String functionBeanName) {
        if(this.arrivalFunctionBeanMap.containsKey(fullClassName)){
            this.arrivalFunctionBeanMap.remove(fullClassName);
        }
    }

    /**
     * FunctionBeanの延命措置
     *
     * @throws GnomesAppException
     */
    public void lifeExtensionMeasures() throws GnomesAppException {
        List<String> baseFunctionBeanList = null;
        baseFunctionBeanList = new ArrayList<String>(new HashSet<>(this.arrivalFunctionBeanMap.values()));
        if(Objects.nonNull(baseFunctionBeanList)){
            for (String baseFunctionBean : baseFunctionBeanList) {
                BaseFunctionBean bean = getCDIInstance(baseFunctionBean);
                bean.countUp();
            }
        }
    }

    /**
     * FunctionBeanの延命措置専用のCDIインスタンス取得関数
     *
     * @param className FunctionBeanのクラス名
     * @return 取得されたFunctionBeanのオブジェクト
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    private <T> T getCDIInstance(String className) throws GnomesAppException {
        Instance<?> instance;

        try {
            CDI<Object> current = CDI.current();
            Class<?> clazz;
            clazz = Class.forName(className);
            instance = current.select(clazz);

        } catch (Exception e) {
            // ME01.0155：「CDIインスタンス取得でエラーが発生しました。詳細についてはログを確認してください。取得クラス：{0} 」
            GnomesAppException gae = new GnomesAppException(e);
            gae.setMessageNo(GnomesMessagesConstants.ME01_0155);

            Object[] errParam = {className};
            gae.setMessageParams(errParam);

            throw gae;
        }

        if (!instance.iterator().hasNext()) {
            // ME01.0156：「指定されたクラス：{0}のCDIインスタンスは見つかりません。」
            throw new GnomesAppException(null, GnomesMessagesConstants.ME01_0156, className);
        }

        return (T) instance.get();

    }
}
