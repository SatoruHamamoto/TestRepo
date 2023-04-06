package com.gnomes.system.data;

import com.gnomes.system.dto.PopupMessageListDto;

/**
 * JSON データ用のクラス定義(ポップアップメッセージ情報)
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/01/12 KCC/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */

/**
 * JSON データ用のクラス定義（ポップアップメッセージ情報）
 */
public class PopupMessageInfo implements IMessageInfoBase {

    /** 発生日時 */
    private String occurDate;
    /** メッセージno. */
    private String messageNo;
    /** 発生者id */
    private String occrUserId;
    /** 発生者名 */
    private String occrUserName;
    /** 発生元コンピュータ名 */
    private String occrHost;
    /** 種別 */
    private Integer category;
    /** 種別(名称) */
    private String categoryName;
    /** メッセージ重要度 */
    private Integer msgLevel;
    /** メッセージ重要度(名称) */
    private String msgLevelName;
    /** メッセージ */
    private String message;
    /** メッセージ詳細 */
    private String messageDetail;
    /** メッセージアイコン名 */
    private String iconName;
    /** ガイダンスメッセージ */
    private String guidanceMessage;
    /** リンクURL */
    private String linkURL;
    /** リンク名 */
    private String linkName;
    /** 領域区分(文字) **/
    private String dbAreaDiv;

    /**
     * 発生日時を取得
     * @return 発生日時
     */
    public String getOccurDate() {
        return occurDate;
    }

    /**
     * 発生日時を設定
     * @param occurDate 発生日時
     */
    public void setOccurDate(String occurDate) {
        this.occurDate = occurDate;
    }

    /**
     * メッセージno.を取得
     * @return メッセージno
     */
    public String getMessageNo() {
        return messageNo;
    }

    /**
     * メッセージnoを設定
     * @param messageNo メッセージno
     */
    public void setMessageNo(String messageNo) {
        this.messageNo = messageNo;
    }

    /**
     * 発生者idを取得
     * @return 発生者id
     */
    public String getOccrUserId() {
        return occrUserId;
    }

    /**
     * 発生者idを設定
     * @param occrUserId 発生者id
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
     * 発生元コンピュータ名を取得
     * @return 発生元コンピュータ名
     */
    public String getOccrHost() {
        return occrHost;
    }

    /**
     * 発生元コンピュータ名を設定
     * @param occrHost 発生元コンピュータ名
     */
    public void setOccrHost(String occrHost) {
        this.occrHost = occrHost;
    }

    /**
     * 種別を取得
     * @return 種別
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
     * 種別(名称)を取得
     * @return 種別(名称)
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * 種別(名称)を設定
     * @param categoryName 種別(名称)
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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
     * メッセージ重要度(名称)を取得
     * @return メッセージ重要度(名称)
     */
    public String getMsgLevelName() {
        return msgLevelName;
    }

    /**
     * メッセージ重要度(名称)を設定
     * @param msgLevelName メッセージ重要度(名称)
     */
    public void setMsgLevelName(String msgLevelName) {
        this.msgLevelName = msgLevelName;
    }

    /**
     * メッセージを取得
     * @return メッセージ
     */
    public String getMessage() {
        return message;
    }

    /**
     * メッセージを設定
     * @param message メッセージ
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * メッセージ詳細を取得
     * @return メッセージ詳細
     */
    public String getMessageDetail() {
        return messageDetail;
    }

    /**
     * メッセージ詳細を設定
     * @param messageDetail メッセージ詳細
     */
    public void setMessageDetail(String messageDetail) {
        this.messageDetail = messageDetail;
    }

    /**
     * メッセージアイコン名を取得
     * @return メッセージアイコン名
     */
    public String getIconName() {
        return iconName;
    }

    /**
     * メッセージアイコン名を設定
     * @param iconName メッセージアイコン名
     */
    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    /**
     * ガイダンスメッセージを取得
     * @return ガイダンスメッセージ
     */
    public String getGuidanceMessage() {
        return guidanceMessage;
    }

    /**
     * ガイダンスメッセージを設定
     * @param guidanceMessage ガイダンスメッセージ
     */
    public void setGuidanceMessage(String guidanceMessage) {
        this.guidanceMessage = guidanceMessage;
    }

    /**
     * リンクURLを取得
     * @return リンクURL
     */
    public String getLinkURL() {
        return linkURL;
    }
    /**
     * リンクURLを設定
     * @param linkURL リンクURL
     */
    public void setLinkURL(String linkURL) {
        this.linkURL = linkURL;
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
     * 領域区分を取得
     * @return 領域区分のリソース化文字列
     */
    public String getDbAreaDiv()
    {
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
     * その他のデータを設定
     * @param data データ
     */
    @Override
    public void setInfoOther(Object data) {
        PopupMessageListDto dto = (PopupMessageListDto) data;

        // メッセージNo
        this.setMessageNo(dto.getMessage_no());

        // 発生者ID
        this.setOccrUserId(dto.getOccur_user_id());

        // 発生者名
        this.setOccrUserName(dto.getOccur_user_name());

        // 発生元コンピュータ名
        this.setOccrHost(dto.getOrigin_computer_name());

        // 種別
        this.setCategory(dto.getCategory());

        // メッセージ重要度
        this.setMsgLevel(dto.getMessage_level());

        // 領域
        this.setDbAreaDiv(String.valueOf(dto.getDb_area_div()));

    }


}
