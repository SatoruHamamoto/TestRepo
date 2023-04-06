package com.gnomes.system.data;

/**
*
* メッセージ情報 インターフェイス
* <!-- TYPE DESCRIPTION --><pre>
* </pre>
*/
/* ========================== MODIFICATION HISTORY ==========================
* Release  Date       ID/Name                   Comment
* --------------------------------------------------------------------------
* R0.01.01 2017/01/17 KCC/K.Fujiwara            初版
* [END OF MODIFICATION HISTORY]
* ==========================================================================
*/
public interface IMessageInfoBase {


    /**
     * 発生日を設定
     * @param occurDate 発生日
     */
    public void setOccurDate(String occurDate);

    /**
     * 種別(名称)を設定する
     * @param categoryName 種別(名称)
     */
    public void setCategoryName(String categoryName);

    /**
     * メッセージ重要度(名称)を設定する
     * @param msgLevelName メッセージ重要度(名称)
     */
    public void setMsgLevelName(String msgLevelName);

    /**
     * メッセージを設定する
     * @param message メッセージ
     */
    public void setMessage(String message);

    /**
     * メッセージ詳細を設定する
     * @param messageDetail メッセージ詳細
     */
    public void setMessageDetail(String messageDetail);

    /**
     * アイコン名を設定する
     * @param iconName アイコン名
     */
    public void setIconName(String iconName);

    /**
     * その他データを設定する
     * @param bundle リソースバンドル
     * @param data データ
     */
    public void setInfoOther(Object data);

}
