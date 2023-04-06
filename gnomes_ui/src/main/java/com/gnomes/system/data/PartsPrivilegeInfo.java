package com.gnomes.system.data;

/**
*
* パーツ権限情報
* <!-- TYPE DESCRIPTION --><pre>
* </pre>
*/
/* ========================== MODIFICATION HISTORY ==========================
* Release  Date       ID/Name                   Comment
* --------------------------------------------------------------------------
* R0.01.01 2017/01/23 KCC/K.Fujiwara            初版
* [END OF MODIFICATION HISTORY]
* ==========================================================================
*/
public class PartsPrivilegeInfo {

    /** パーツID(画面項目ID):String */
    private String tagId;

    /** 画面ID:String */
    private String screenId;

    /** ボタンID:String */
    private String buttonId;

    /**
     * コンストラクター
     * @param tagId パーツID
     * @param screenId 画面ID
     * @param buttonId ボタンID
     */
    public PartsPrivilegeInfo(String tagId, String screenId, String buttonId) {
        super();
        this.tagId = tagId;
        this.screenId = screenId;
        this.buttonId = buttonId;
    }

    /**
     * パーツIDを取得
     * @return tagId パーツID
     */
    public String getTagId() {
        return tagId;
    }

    /**
     * パーツIDを設定
     * @param tagId パーツID
     */
    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    /**
     * 画面IDを取得
     * @return screenId 画面ID
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
     * ボタンIDを取得
     * @return buttonId ボタンID
     */
    public String getButtonId() {
        return buttonId;
    }

    /**
     * ボタンIDを設定
     * @param buttonId ボタンID
     */
    public void setButtonId(String buttonId) {
        this.buttonId = buttonId;
    }

}
