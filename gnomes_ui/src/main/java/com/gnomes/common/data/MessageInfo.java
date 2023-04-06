package com.gnomes.common.data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/**
 *
 * メッセージ情報
 *
 */
public class MessageInfo {

    /** 画面ID */
    private String screenId;
    /** 画面名 */
    private String screenName;
    /** 発生拠点コード */
    private String siteCode;
    /** 発生コンピュータ名 */
    private String occurHost;
    /** 発生コンピュータID */
    private String occurHostId;
    /** 発生IPアドレス */
    private String originIpAddress;
    /** 発生者ID */
    private String occrUserId;
    /** 発生者名 */
    private String occrUserName;
    /** 発生日時 */
    private LocalDateTime occurDate;
    /** コマンドID */
    private String commandId;
    /** コマンド名 */
    private String commandName;
    /** ボタンID */
    private String buttonId;
    /** 発生元ボタン操作内容 */
    private String buttonOperationContent;
    /** リソースID */
    private String resourceId;
    /** パラメータ情報（リスト） */
    private List<String> msgParamList;
    /** 発生元ソース  メッセージの発生元ソース（クラス名#メソッド名)  */
    private String sourceInfo;
    /** メッセージno */
    private String messageNo;
    /** 種別 */
    private Integer category;
    /** メッセージ重要度 */
    private Integer msgLevel;
    /** メッセージ履歴記録可否 */
    private Integer isMsgHistoryRec;
    /** ログ有無 */
    private Integer isLogging;
    /** プッシュ通知フラグ */
    private Integer isNoticePush;
    /** メッセージボタンモード */
    private Integer msgBtnMode;
    /** メッセージデフォルトボタンモード */
    private Integer defaultBtn;
    /** メール送信先グループID */
    private String sendMailGroupid;
    /** メール送信抑制上限数 */
    private Integer sendMailRestrainLimit;
    /** メール送信抑制期間（時間） */
    private Integer sendMailRestrainLimitTime;
    /** Talend呼出ジョブ名 */
    private String talendJobName;
    /** Talendコンテキストパラメータ */
    private String talendContextParam;
    /** メッセージタイトルリソースID */
    private String msgTitleResourceid;
    /** メッセージ本文リソースID */
    private String msgTextResourceid;
    /** ガイダンスメッセージ */
    private String guidanceMsg;
    /** リンク情報 */
    private String linkInfo;
    /** リンク名 */
    private String linkName;
    /** ユーザーロケール */
    private Locale userLocale;
    /** 画面表示フラグ */
    private boolean isDispFlg;
    /** 親フラグ */
    private boolean isOwner;
    /** OK メッセージコマンド */
    private String messageCommand;
    /** キャンセルボタンOnclick */
    private String messageCancelOnClick;
    /** OKボタンOnclick */
    private String messageOkOnClick;
    /** DB領域区分 */
    private Integer dbAreaDiv;
    /** 監視機能用Key */
    private String watcherSearchKey;

	/**
     * MessageInfo コンストラクタ
     */
    public MessageInfo() {
    }

    /**
     * 画面IDを取得
     * @return 画面ID
     */
    public String getScreenId() {
        return screenId;
    }

    /**
     * 画面IDを設定
     * @param screenId 画面ID
     */
    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }

    /**
     * 画面名を取得
     * @return 画面名
     */
    public String getScreenName() {
        return screenName;
    }

    /**
     * 画面名を設定
     * @param screenName 画面名
     */
    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    /**
     * 発生エリアIDを取得
     * @return 発生エリアID
     */
    public String getSiteCode() {
        return siteCode;
    }

    /**
     * 発生エリアIDを設定
     * @param siteCode 発生エリアID
     */
    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    /**
     * 発生コンピュータ名を取得
     * @return 発生コンピュータ名
     */
    public String getOccurHost() {
        return occurHost;
    }

    /**
     * 発生コンピュータ名を設定
     * @param occurHost 発生コンピュータ名
     */
    public void setOccurHost(String occurHost) {
        this.occurHost = occurHost;
    }




    /**
     * 発生コンピュータIDを取得
	 * @return occurHostId
	 */
	public String getOccurHostId() {
		return occurHostId;
	}

	/**
	 * 発生コンピュータIDを設定
	 * @param occurHostId セットする 発生コンピュータId
	 */
	public void setOccurHostId(String occurHostId) {
		this.occurHostId = occurHostId;
	}

	/**
	 * 発生元IPアドレス クライアントの取得
	 * @return 発生元IPアドレス クライアント
	 */
    public String getOriginIpAddress()
    {
        return originIpAddress;
    }

    /**
     * 発生元IPアドレス クライアントの設定
     * @param originIpAddress 発生元IPアドレス クライアント
     */
    public void setOriginIpAddress(String originIpAddress)
    {
        this.originIpAddress = originIpAddress;
    }

	/**
     * 発生者IDを取得
     * @return 発生者ID
     */
    public String getOccrUserId() {
        return occrUserId;
    }

    /**
     * 発生者ID を設定
     * @param occrUserId 発生者ID
     */
    public void setOccrUserId(String occrUserId) {
        this.occrUserId = occrUserId;
    }

    /**
     * 発生者名を取得
     * @return 発生者名
     */
    public String getOccrUserName() {
        return occrUserName;
    }

    /**
     * 発生者名を設定
     * @param occrUserName 発生者名
     */
    public void setOccrUserName(String occrUserName) {
        this.occrUserName = occrUserName;
    }

    /**
     * 発生日時を取得
     * @return 発生日時
     */
    public LocalDateTime getOccurDate() {
        return occurDate;
    }

    /**
     * 発生日時を設定
     * @param occurDate 発生日時
     */
    public void setOccurDate(LocalDateTime occurDate) {
        this.occurDate = occurDate;
    }

    /**
     * リソースIDを取得
     * @return リソースID
     */
    public String getResourceId() {
        return resourceId;
    }

    /**
     * リソースIDを設定
     * @param resourceId リソースID
     */
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    /**
     * パラメータ情報（リスト）を取得
     * @return パラメータ情報（リスト）
     */
    public List<String> getMsgParamList() {
        return msgParamList;
    }

    /**
     * パラメータ情報（リスト）を設定
     * @param msgParamList パラメータ情報（リスト）
     */
    public void setMsgParamList(List<String> msgParamList) {
        this.msgParamList = msgParamList;
    }

    /**
     * 発生元ソース  メッセージの発生元ソース（クラス名#メソッド名) を取得
     * @return 発生元ソース  メッセージの発生元ソース（クラス名#メソッド名)
     */
    public String getSourceInfo() {
        return sourceInfo;
    }

    /**
     * 発生元ソース  メッセージの発生元ソース（クラス名#メソッド名)を設定
     * @param sourceInfo 発生元ソース  メッセージの発生元ソース（クラス名#メソッド名)
     */
    public void setSourceInfo(String sourceInfo) {
        this.sourceInfo = sourceInfo;
    }

    /**
     * メッセージNo を取得
     * @return メッセージNo
     */
    public String getMessageNo() {
        return messageNo;
    }

    /**
     * メッセージNoを設定
     * @param messageNo メッセージNo
     */
    public void setMessageNo(String messageNo) {
        this.messageNo = messageNo;
    }

    /**
     *  種別を取得
     * @return  種別
     */
    public Integer getCategory() {
        return category;
    }

    /**
     * 種別を設定
     * @param category 種別
     */
    public void setCategory(Integer category) {
        this.category = category;
    }

    /**
     * メッセージ重要度を取得
     * @return メッセージ重要度
     */
    public Integer getMsgLevel() {
        return msgLevel;
    }

    /**
     * メッセージ重要度を設定
     * @param msgLevel メッセージ重要度
     */
    public void setMsgLevel(Integer msgLevel) {
        this.msgLevel = msgLevel;
    }

    /**
     * メッセージ履歴記録可否を取得
     * @return メッセージ履歴記録可否
     */
    public Integer getIsMsgHistoryRec() {
        return this.isMsgHistoryRec;
    }

    /**
     * メッセージ履歴記録可否を設定
     * @param isMsgHistoryRec メッセージ履歴記録可否
     */
    public void setIsMsgHistoryRec(Integer isMsgHistoryRec) {
        this.isMsgHistoryRec = isMsgHistoryRec;
    }

    /**
     * ログ有無を取得
     * @return ログ有無
     */
    public Integer getIsLogging() {
        return isLogging;
    }

    /**
     * ログ有無を設定
     * @param isLogging ログ有無
     */
    public void setIsLogging(Integer isLogging) {
        this.isLogging = isLogging;
    }

    /**
     * プッシュ通知フラグを取得
     * @return プッシュ通知フラグ
     */
    public Integer getIsNoticePush() {
        return this.isNoticePush;
    }

    /**
     * プッシュ通知フラグを設定
     * @param isNoticePush プッシュ通知フラグ
     */
    public void setIsNoticePush(Integer isNoticePush) {
        this.isNoticePush = isNoticePush;
    }

    /**
     * 画面表示フラグを取得
     * @return 画面表示フラグ
     */
    public boolean getIsDispFlg() {
        return isDispFlg;
    }

    /**
     * 画面表示フラグを設定
     * @param isDispFlg 画面表示フラグ
     */
    public void setIsDispFlg(boolean isDispFlg) {
        this.isDispFlg = isDispFlg;
    }

    /**
     * メッセージボタンモードを取得
     * @return メッセージボタンモード
     */
    public Integer getMsgBtnMode() {
        return msgBtnMode;
    }

    /**
     * メッセージボタンモードを設定
     * @param msgBtnMode メッセージボタンモード
     */
    public void setMsgBtnMode(Integer msgBtnMode) {
        this.msgBtnMode = msgBtnMode;
    }

    /**
     * メッセージデフォルトボタンモードを取得
     * @return メッセージデフォルトボタンモード
     */
    public Integer getDefaultBtn() {
        return defaultBtn;
    }

    /**
     * メッセージデフォルトボタンモードを設定
     * @param defaultBtn メッセージデフォルトボタンモード
     */
    public void setDefaultBtn(Integer defaultBtn) {
        this.defaultBtn = defaultBtn;
    }

    /**
     * メール送信先グループIDを取得
     * @return メール送信先グループID
     */
    public String getSendMailGroupid() {
        return this.sendMailGroupid;
    }

    /**
     * メール送信先グループIDを設定
     * @param send_mail_groupid メール送信先グループID
     */
    public void setSendMailGroupid(String sendMailGroupid) {
        this.sendMailGroupid = sendMailGroupid;
    }

    /**
     * メール送信抑制上限数を取得
     * @return メール送信抑制上限数
     */
    public Integer getSendMailRestrainLimit() {
        return this.sendMailRestrainLimit;
    }

    /**
     * メール送信抑制上限数を設定
     * @param sendMailRestrainLimit メール送信抑制上限数
     */
    public void setSendMailRestrainLimit(Integer sendMailRestrainLimit) {
        this.sendMailRestrainLimit = sendMailRestrainLimit;
    }

    /**
     * メール送信抑制期間（時間）を取得
     * @return メール送信抑制期間（時間）
     */
    public Integer getSendMailRestrainLimitTime() {
        return this.sendMailRestrainLimitTime;
    }

    /**
     * メール送信抑制期間（時間）を設定
     * @param sendMailRestrainLimitTime メール送信抑制期間（時間）
     */
    public void setSendMailRestrainLimitTime(Integer sendMailRestrainLimitTime) {
        this.sendMailRestrainLimitTime = sendMailRestrainLimitTime;
    }

    /**
     * Talend呼出ジョブ名 を取得
     * @return Talend呼出ジョブ名
     */
    public String getTalendJobName() {
        return talendJobName;
    }

    /**
     * Talend呼出ジョブ名 を設定
     * @param talendJobName Talend呼出ジョブ名
     */
    public void setTalendJobName(String talendJobName) {
        this.talendJobName = talendJobName;
    }

    /**
     * Talendコンテキストパラメータ を取得
     * @return Talendコンテキストパラメータ
     */
    public String getTalendContextParam() {
        return talendContextParam;
    }

    /**
     * Talendコンテキストパラメータ を設定
     * @param talendContextParam Talendコンテキストパラメータ
     */
    public void setTalendContextParam(String talendContextParam) {
        this.talendContextParam = talendContextParam;
    }

    /**
     * メッセージタイトルリソースIDを取得
     * @return メッセージタイトルリソースID
     */
    public String getMsgTitleResourceid() {
        return this.msgTitleResourceid;
    }

    /**
     * メッセージタイトルリソースIDを設定
     * @param msgTitleResourceid メッセージタイトルリソースID
     */
    public void setMsgTitleResourceid(String msgTitleResourceid) {
        this.msgTitleResourceid = msgTitleResourceid;
    }

    /**
     * メッセージ本文リソースIDを取得
     * @return メッセージ本文リソースID
     */
    public String getMsgTextResourceid() {
        return this.msgTextResourceid;
    }

    /**
     * メッセージ本文リソースIDを設定
     * @param msgTextResourceid メッセージ本文リソースID
     */
    public void setMsgTextResourceid(String msgTextResourceid) {
        this.msgTextResourceid = msgTextResourceid;
    }

    /**
     * ガイダンスメッセージを取得
     * @return ガイダンスメッセージ
     */
    public String getGuidanceMsg() {
        return guidanceMsg;
    }

    /**
     * ガイダンスメッセージを設定
     * @param guidanceMsg ガイダンスメッセージ
     */
    public void setGuidanceMsg(String guidanceMsg) {
        this.guidanceMsg = guidanceMsg;
    }

    /**
     * リンク情報を取得
     * @return リンク情報
     */
    public String getLinkInfo() {
        return linkInfo;
    }

    /**
     * リンク情報を設定
     * @param linkInfo リンク情報
     */
    public void setLinkInfo(String linkInfo) {
        this.linkInfo = linkInfo;
    }

    /**
     * リンク名を取得
     * @return リンク名
     */
    public String getLinkName() {
        return linkName;
    }

    /**
     * リンク名を設定
     * @param linkName リンク名
     */
    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    /**
     * ユーザーロケールを取得
     * @return ユーザーロケール
     */
    public Locale getUserLocale() {
        return userLocale;
    }

    /**
     * ユーザーロケールを設定
     * @param userLocale ユーザーロケール
     */
    public void setUserLocale(Locale userLocale) {
        this.userLocale = userLocale;
    }

    /**
     * 親フラグを取得
     * @return 親フラグ
     */
    public boolean isOwner() {
        return isOwner;
    }

    /**
     * 親フラグを設定
     * @param isOwner 親フラグ
     */
    public void setOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }

    /**
     * OKメッセージコマンドを取得
     * @return OKメッセージコマンド
     */
    public String getMessageCommand() {
        return messageCommand;
    }

    /**
     * OKメッセージコマンドを設定
     * @param messageCommand OKメッセージコマンド
     */
    public void setMessageCommand(String messageCommand) {
        this.messageCommand = messageCommand;
    }

    /**
     * キャンセルボタンOnclickを取得
     * @return キャンセルボタンOnclick
     */
    public String getMessageCancelOnClick() {
        return messageCancelOnClick;
    }

    /**
     * キャンセルボタンOnclickを設定
     * @param messageCancelOnClick キャンセルボタンOnclick
     */
    public void setMessageCancelOnClick(String messageCancelOnClick) {
        this.messageCancelOnClick = messageCancelOnClick;
    }

    /**
     * OKボタンOnclickを取得
     * @return OKボタンOnclick
     */
    public String getMessageOkOnClick() {
        return messageOkOnClick;
    }

    /**
     * OKボタンOnclickを設定
     * @param messageOkOnClick OKボタンOnclick
     */
    public void setMessageOkOnClick(String messageOkOnClick) {
        this.messageOkOnClick = messageOkOnClick;
    }

	/**
	 * コマンドIDを取得
	 * @return commandId コマンドID
	 */
	public String getCommandId() {
		return commandId;
	}

	/**
	 * コマンドIDを設定
	 * @param commandId セットする コマンドID
	 */
	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}

	/**
	 * コマンド名を取得
	 * @return commandName コマンド
	 */
	public String getCommandName() {
		return commandName;
	}

	/**
	 * コマンド名を設定
	 * @param commandName セットする コマンド名
	 */
	public void setCommandName(String commandName) {
		this.commandName = commandName;
	}

	/**
	 * ボタンIDを取得
	 * @return buttonId ボタンID
	 */
	public String getButtonId() {
		return buttonId;
	}

	/**
	 * ボタンIDを設定
	 * @param buttonId セットする ボタンID
	 */
	public void setButtonId(String buttonId) {
		this.buttonId = buttonId;
	}

	/**
	 * ボタン操作内容を取得
	 * @return buttonOperationContent
	 */
	public String getButtonOperationContent() {
		return buttonOperationContent;
	}

	/**
	 * ボタン操作内容を設定
	 * @param buttonOperationContent セットする ボタン操作内容
	 */
	public void setButtonOperationContent(String buttonOperationContent) {
		this.buttonOperationContent = buttonOperationContent;
	}

	/**
	 * 領域区分を取得
	 * @return 1:実行領域, 2:保管領域
	 */
    public Integer getDbAreaDiv() {
		return dbAreaDiv;
	}

    /**
     * 領域区分を設定
     * @param dbAreaDiv 1:実行領域, 2:保管領域
     */
	public void setDbAreaDiv(Integer dbAreaDiv) {
		this.dbAreaDiv = dbAreaDiv;
	}

    /**
     * 監視機能用Keyを取得
     * @return 監視機能用Key
     */
    public String getWatcherSearchKey() {
        return watcherSearchKey;
    }

    /**
     * 監視機能用Keyを設定
     * @param messageOkOnClick 監視機能用Key
     */
    public void setWatcherSearchKey(String watcherSearchKey) {
        this.watcherSearchKey = watcherSearchKey;
    }

}
