package com.gnomes.common.data;

/**
 * メッセージデータ
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/04/06 YJP/K.Fujiwara             初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class MessageData {

    /** メッセージNo */
    private String messageNo;

    /** パラメータ */
    private Object[] params;

    /** OK メッセージコマンド */
    private String messageCommand;

    /** キャンセルボタンOnclick */
    private String messageCancelOnClick;

    /** OKボタンOnclick */
    private String messageOkOnClick;


    /**
     * コンストラクタ
     * @param messageNo メッセージNo
     * @param params パラメータ
     */
    public MessageData(String messageNo, Object[] params) {
        this.messageNo = messageNo;
        this.params = params;
    }


    /**
     * メッセージNoを取得
     *
     * @return messageNo メッセージNo
     */
    public String getMessageNo() {
        return messageNo;
    }

    /**
     * メッセージNoを設定
     *
     * @param messageNo セットする messageNo
     */
    public void setMessageNo(String messageNo) {
        this.messageNo = messageNo;
    }

    /**
     * パラメータを取得
     * @return params パラメータ
     */
    public Object[] getParams() {
        return params;
    }

    /**
     * パラメータを設定
     *
     * @param params セットする params
     */
    public void setParams(Object[] params) {
        this.params = params;
    }


    /**
     * @return messageCommand
     */
    public String getMessageCommand() {
        return messageCommand;
    }


    /**
     * @param messageCommand セットする messageCommand
     */
    public void setMessageCommand(String messageCommand) {
        this.messageCommand = messageCommand;
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



}
