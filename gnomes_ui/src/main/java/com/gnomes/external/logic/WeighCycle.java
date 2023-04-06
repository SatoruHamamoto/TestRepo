package com.gnomes.external.logic;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseLogic;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.external.data.WeighIfParam;
import com.gnomes.external.dto.WeighResultDto;
import com.gnomes.external.spi.WeighCycleThread;
import com.gnomes.system.dao.MstrMessageDefineDao;

/**
 * 定周期秤量処理クラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/01/18 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class WeighCycle extends BaseLogic
{
    static final String            className         = "WeighCycle";

    static final String            logMsg_cycleStart = "Weigh Cycle Start -- ";

    //定周期収集が開始しても値がまだ入っていないことがあるため
    //クライアントにマジックナンバーを返す文字
    static final String            RECV_MSG_NODATA   = "_NODATA_";

    //    /** 定周期秤量処理スレッド */
    //    @Inject
    //    protected WeighCycleThread weighCycleThread;

    /** メッセージ定義Dao. */
    @Inject
    protected MstrMessageDefineDao mstrMessageDefineDao;

    /** 秤量器IF機能クラス */
    @Inject
    public WeighMachine            weighMachine;

    /**
     * 定周期秤量処理.
     * <pre>
     * 定周期で秤量処理を行う。
     * </pre>
     * @param weighIfParam 秤量器IFパラメータ
     * @param weighCycleThread 定周期秤量処理スレッド
     */
    @TraceMonitor
    @ErrorHandling
    public void weighCycleStart(WeighIfParam weighIfParam, WeighCycleThread weighCycleThread)
    {
        final String methodName = "weighCycleStart";

        this.logHelper.fine(this.logger, className, methodName, "定周期処理を開始するメソッドが呼ばれた。今から定周期が動きます");

        //開始ログを出力
        logHelper.info(this.logger, className, methodName, logMsg_cycleStart + weighIfParam.toDebugString());
        //既にスレッド起動されているのであれば何もしない
        if (!isExistWeighCycleThread(weighIfParam.getWeighApparatusId())) {
            // 定周期実行
            weighCycleThread.weighCycleStart(this, weighIfParam);
            // 秤量器IFパラメータMapに秤量器IFパラメータを設定
            WeighInfoControl.weighIfParamMap.put(weighIfParam.getWeighApparatusId(), weighIfParam);
            // 定周期秤量処理スレッドMapに定周期秤量処理スレッドを設定
            WeighInfoControl.weighCycleThreadMap.put(weighIfParam.getWeighApparatusId(), weighCycleThread);
            this.logHelper.fine(this.logger, className, methodName, "今から定周期が動きます。新たに開始されました。");

        }
        else {
            this.logHelper.fine(this.logger, className, methodName, "定周期処理を開始するメソッドが呼ばれが、すでに開始されていた。");
        }

    }

    /**
     * 秤量結果（定周期）取得.
     * <pre>
     * 秤量結果（定周期）の取得を行う。
     * </pre>
     * @param weighApparatusId 秤量器ID
     * @return 秤量結果
     */
    public String getWeighCycleReceiveData(String weighApparatusId)
    {

        final String methodName = "getWeighCycleReceiveData";

        this.logHelper.fine(this.logger, className, methodName, "クライアントからデータを要求");

        try {
            // 秤量器I/Fパラメータ取得
            WeighIfParam weighIfParam = WeighInfoControl.weighIfParamMap.get(weighApparatusId);

            // 秤量結果取得
            WeighResultDto weighResult = this.getWeighResult(weighApparatusId);

            if (weighResult != null) {

                if (weighResult.getGnomesAppException() != null) {
                    this.logHelper.fine(this.logger, className, methodName, "秤量結果が異常と判断");

                    return this.getMessage(weighResult.getGnomesAppException().getMessageNo(),
                            weighResult.getGnomesAppException().getMessageParams());
                }
                this.logHelper.fine(this.logger, className, methodName,
                        "秤量結果が正常と判断 値は" + weighResult.getWeighedValue().toString());

                return weighResult.getWeighedValue().toString();

            }
            else {
                // 定周期処理スレッド存在チェック
                // A)スレッドが存在しなかった場合
                if (!isExistWeighCycleThread(weighApparatusId)) {
                    // 定周期処理が実施されていません。（秤量器ID：{0}）（ME01.0085）
                    this.logHelper.fine(this.logger, className, methodName, "秤量結果がNULLだった　「周期処理が実施されていません」と返答");
                    return this.getMessage(GnomesMessagesConstants.ME01_0085, weighApparatusId);

                }
                //B)スレッドが存在しても値が入っていない場合 "NO_DATA"を返す
                //  -> クライアントは描画せず、次の機会を待つ
                else {
                    return RECV_MSG_NODATA;
                }

            }
        }
        catch (Exception ex) {
            this.logHelper.severe(this.logger, className, methodName, ex.getMessage(), ex);
            return ex.getMessage();
        }

    }

    /**
     * 定周期秤量処理スレッド存在チェック.
     * <pre>
     * 定周期秤量処理スレッドの存在チェックを行う。
     * </pre>
     * @param externalDeviceCode 秤量器ID
     * @return 定周期秤量処理スレッド存在結果
     */
    public boolean isExistWeighCycleThread(String weighApparatusId)
    {
        return (WeighInfoControl.weighCycleThreadMap.containsKey(weighApparatusId));
    }

    /**
     * 定周期処理スレッド強制停止要求状態取得.
     * <pre>
     * 定周期処理スレッド強制停止要求状態の取得を行う。
     * </pre>
     * @param weighApparatusId 秤量器ID
     * @return 強制停止要求状態
     */
    public boolean getThreadStopRequestState(String weighApparatusId)
    {

        return (WeighInfoControl.threadStopStateMap.containsKey(weighApparatusId));

    }

    /**
     * 定周期処理スレッド強制停止要求.
     * <pre>
     * 定周期処理スレッド強制停止要求を行う。
     * </pre>
     * @param weighApparatusId 秤量器ID
     */
    public void setThreadStopRequest(String weighApparatusId)
    {
        final String methodName = "setThreadStopRequest";

        this.logHelper.fine(this.logger, className, methodName, "処理周期処理スレッド強制停止要求を行った");

        // 定周期処理スレッド取得
        if (isExistWeighCycleThread(weighApparatusId)) {
            WeighInfoControl.threadStopStateMap.put(weighApparatusId, true);
        }

    }
    /**
     * 定周期処理スレッド強制停止要求解除.
     * <pre>
     * 定周期処理スレッド強制停止要求解除を行う。
     * </pre>
     * @param weighApparatusId 秤量器ID
     */
    public void resetThreadStopRequest(String weighApparatusId)
    {
        final String methodName = "resetThreadStopRequest";

        this.logHelper.fine(this.logger, className, methodName, "処理周期処理スレッド強制停止!!解除!!を行った");

        // 定周期処理スレッド取得
        if (isExistWeighCycleThread(weighApparatusId)) {

            WeighInfoControl.threadStopStateMap.remove(weighApparatusId);

        }

    }

    /**
     * WebSocket接続状態取得.
     * <pre>
     * WebSocket接続状態の取得を行う。
     * </pre>
     * @param weighApparatusId 秤量器ID
     * @return WebSocket接続状態
     */
    public boolean getConnectionState(String weighApparatusId)
    {

        final String methodName = "getConnectionState";

        if (WeighInfoControl.connectionStateMap.containsKey(weighApparatusId)) {
            this.logHelper.fine(this.logger, className, methodName,
                    "通信状態を取得　connectionStateMapに見つかったので　値を返す " + WeighInfoControl.connectionStateMap.get(
                            weighApparatusId));

            return WeighInfoControl.connectionStateMap.get(weighApparatusId);
        }
        this.logHelper.fine(this.logger, className, methodName, "通信状態を取得　connectionStateMapに見つかったので　falseを返す ");

        return false;

    }

    /**
     * WebSocket接続状態設定.
     * <pre>
     * WebSocket接続状態の設定を行う。
     * </pre>
     * @param weighApparatusId 秤量器ID
     * @param connectionState WebSocket接続状態
     */
    public void setConnectionState(String weighApparatusId, boolean connectionState)
    {

        final String methodName = "setConnectionState";

        this.logHelper.fine(this.logger, className, methodName, "通信状態を設定 " + connectionState + "を設定");

        // 定周期処理実行確認
        if (isExistWeighCycleThread(weighApparatusId)) {
            WeighInfoControl.connectionStateMap.put(weighApparatusId, connectionState);
        }

    }

    /**
     * 秤量結果(定周期)取得.
     * <pre>
     * 秤量結果(定周期)の取得を行う。
     * </pre>
     * @param weighApparatusId 秤量器ID
     * @return 秤量結果(定周期)
     */
    protected WeighResultDto getWeighResult(String weighApparatusId)
    {

        return (WeighInfoControl.weighCycleResultMap.get(weighApparatusId));

    }

    /**
     * 秤量結果(定周期)設定.
     * <pre>
     * 秤量結果(定周期)の設定を行う。
     * </pre>
     * @param weighApparatusId 秤量器ID
     * @param weighResultDto 秤量結果(定周期)
     */
    public void setWeighResultMap(String weighApparatusId, WeighResultDto weighResultDto)
    {

        final String methodName = "setWeighResultMap";

        synchronized (WeighInfoControl.weighCycleResultMap) {
            //秤量値保存データを差し替える
            WeighInfoControl.weighCycleResultMap.put(weighApparatusId, weighResultDto);
            this.logHelper.fine(this.logger, className, methodName, "秤量結果(定周期)が設定された");
        }

    }

    /**
     * メッセージ取得.
     * @param messageKey メッセージキー
     * @param messageParams リリースの引数
     * @return メッセージ
     */
    public String getMessage(String messageKey, Object... messageParams)
    {

        String convertedMessage = "";

        try {

            //通常メッセージを取得する場合はリソースIDから取得する
            convertedMessage = MessagesHandler.getString(messageKey, messageParams);

        }
        catch (Exception e) {

            convertedMessage = MessagesHandler.getString(GnomesMessagesConstants.ME01_0001, e.getMessage());

        }

        return convertedMessage;

    }

}
