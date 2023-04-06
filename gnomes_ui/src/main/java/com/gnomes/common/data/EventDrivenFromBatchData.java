package com.gnomes.common.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.RequestScoped;

/**
 * バッチイベント駆動化データ
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2021/07/08 YJP/K.Nakanishi            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class EventDrivenFromBatchData
{
    /** コマンド処理後バッチ処理パラメータ */
    private Map<String, List<String>>          commandAfterBatchParameter          = new HashMap<>();

    /** 外部IF送信処理パラメータ */
    private List<String>                       sendProcParameter                   = new ArrayList<String>();


    /**
     * コマンド処理後バッチ処理パラメータを取得
     * @return コマンド処理後バッチ処理パラメータ
     */
    public Map<String, List<String>> getCommandAfterBatchParameter()
    {
        return commandAfterBatchParameter;
    }

    /**
     * コマンド処理後バッチ処理パラメータを設定
     * @param commandAfterBatchParameter コマンド処理後バッチ処理パラメータ
     */
    public void setCommandAfterBatchParameter(Map<String, List<String>> commandAfterBatchParameter)
    {
        this.commandAfterBatchParameter = commandAfterBatchParameter;
    }

    /**
     * コマンド処理後バッチ処理コマンドを設定（パラメータなし）
     * @param commandId コマンド処理後バッチ処理コマンド
     * @param batchCommnadParameter コマンド処理後バッチ処理コマンドパラメーター
     */
    public void setCommandAfterBatchCommandId(String commandId)
    {
        setCommandAfterBatchCommandId(commandId, "");
    }

    /**
     * コマンド処理後バッチ処理コマンドを設定
     * @param commandId コマンド処理後バッチ処理コマンド
     * @param batchCommnadParameter コマンド処理後バッチ処理コマンドパラメーター
     */
    public void setCommandAfterBatchCommandId(String commandId, String batchCommnadParameter)
    {
        if (getCommandAfterBatchParameter().containsKey(commandId)) {
            getCommandAfterBatchParameter().get(commandId).add(batchCommnadParameter);
        } else {
            List<String> batchParameters = new ArrayList<>(Arrays.asList(batchCommnadParameter));
            this.commandAfterBatchParameter.put(commandId, batchParameters);
        }
    }

    /**
     * 外部IF送信処理パラメータを取得
     * @return 外部IF送信処理パラメータ
     */
    public List<String> getSendProcParameter() {
        return sendProcParameter;
    }

    /**
     * 外部IF送信処理パラメータを設定
     * @param sendProcParameter 外部IF送信処理パラメータ
     */
    public void setSendProcParameter(List<String> sendProcParameter) {
        this.sendProcParameter = sendProcParameter;
    }

}
