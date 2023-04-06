package com.gnomes.uiservice;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * メッセージビーン
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/01/13 KCC/Y.Oota                  初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Named
@RequestScoped
public class MessageBean {

    /** リソースID */
    private String resourceid;
    /** 発生日時 */
    private String occurDate;
    /** 発生者名 */
    private String occrUserName;
    /** 発生元コンピュータ名 */
    private String occrHost;
    /** 種別 */
    private Integer category;
    /** 種別(名称) */
    private String categoryName;
    /** メッセージ */
    private String message;
    /** メッセージ詳細 */
    private String messageDetail;
    /** メッセージアイコン名 */
    private String iconName;
    /** メッセージボタンモード */
    private int msgBtnMode;
    /** 保管領域 */
    private String dbAreaDiv;

    /** メッセージデフォルトボタンモード */
    private int defaultBtn;

    /** 実行コマンド */
    private String command;

    /** キャンセルボタンOnclick */
    private String messageCancelOnClick;

    /** OKボタンOnclick */
    private String messageOkOnClick;

    /** リンク情報 */
    private String[] linkInfo;

    /** ボタンID */
    private String buttonId;

    /**
     * リソースIDを取得
     * @return リソースID
     */
    public String getResourceid() {
        return resourceid;
    }

    /**
     * リソースIDを設定
     * @param resourceid リソースID
     */
    public void setResourceid(String resourceid) {
        this.resourceid = resourceid;
    }

    /**
     * @return occurdate
     */
    public String getOccurDate() {
        return occurDate;
    }

    /**
     * @param occurdate セットする occurdate
     */
    public void setOccurDate(String occurDate) {
        this.occurDate = occurDate;
    }

    /**
     * @return occrusername
     */
    public String getOccrUserName() {
        return occrUserName;
    }

    /**
     * @param occrusername セットする occrusername
     */
    public void setOccrUserName(String occrUserName) {
        this.occrUserName = occrUserName;
    }

    /**
     * @return ccrhost
     */
    public String getOccrHost() {
        return occrHost;
    }

    /**
     * @param ccrhost セットする ccrhost
     */
    public void setOccrHost(String occrHost) {
        this.occrHost = occrHost;
    }

    /**
     * @return category
     */
    public Integer getCategory() {
        return category;
    }

    /**
     * @param category セットする category
     */
    public void setCategory(Integer category) {
        this.category = category;
    }

    /**
     * @return category_name
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * @param category_name セットする category_name
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message セットする message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return message_detail
     */
    public String getMessageDetail() {
        return messageDetail;
    }

    /**
     * @param message_detail セットする message_detail
     */
    public void setMessageDetail(String messageDetail) {
        this.messageDetail = messageDetail;
    }

    /**
     * @return iconName
     */
    public String getIconName() {
        return iconName;
    }

    /**
     * @param iconName セットする iconName
     */
    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    /**
     * @return msgBtnMode
     */
    public int getMsgBtnMode() {
        return msgBtnMode;
    }

    /**
     * @param msgBtnMode セットする msgBtnMode
     */
    public void setMsgBtnMode(int msgBtnMode) {
        this.msgBtnMode = msgBtnMode;
    }

    /**
     * 領域区分を取得
     * @return 1:実行領域, 2:保管領域
     */
    public String getDbAreaDiv() {
        return dbAreaDiv;
    }

    /**
     * 領域区分を設定
     * @param dbAreaDiv 1:実行領域, 2:保管領域
     */
    public void setDbAreaDiv(String dbAreaDiv) {
        this.dbAreaDiv = dbAreaDiv;
    }

    /**
     * @return defaultBtn
     */
    public int getDefaultBtn() {
        return defaultBtn;
    }

    /**
     * @param defaultBtn セットする defaultBtn
     */
    public void setDefaultBtn(int defaultBtn) {
        this.defaultBtn = defaultBtn;
    }

    /**
     * @return command
     */
    public String getCommand() {
        return command;
    }

    /**
     * @param command セットする command
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * @return linkInfo
     */
    public String[] getLinkInfo() {
        return linkInfo;
    }

    /**
     * @param linkInfo セットする linkInfo
     */
    public void setLinkInfo(String[] linkInfo) {
        this.linkInfo = linkInfo;
    }

    /**
     * @return messageCancelOnClick
     */
    public String getMessageCancelOnClick() {
        return messageCancelOnClick;
    }

    /**
     * @param messageCancelOnClick セットする messageCancelOnClick
     */
    public void setMessageCancelOnClick(String messageCancelOnClick) {
        this.messageCancelOnClick = messageCancelOnClick;
    }

    /**
     * @return messageOkOnClick
     */
    public String getMessageOkOnClick() {
        return messageOkOnClick;
    }

    /**
     * @param messageOkOnClick セットする messageOkOnClick
     */
    public void setMessageOkOnClick(String messageOkOnClick) {
        this.messageOkOnClick = messageOkOnClick;
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
}
