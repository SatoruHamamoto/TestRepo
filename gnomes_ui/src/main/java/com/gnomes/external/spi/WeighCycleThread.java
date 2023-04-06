package com.gnomes.external.spi;

import java.math.BigDecimal;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.external.data.WeighIfParam;
import com.gnomes.external.dto.WeighResultDto;
import com.gnomes.external.logic.WeighCycle;
import com.gnomes.external.logic.WeighInfoControl;

/**
 * 定周期秤量処理スレッドクラス
 *
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/01/18 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Stateless
public abstract class WeighCycleThread {

    /** クラス名 */
    private static final String className = "WeighCycleThread";

    // ロガー
    @Inject
    transient Logger logger;

    /** ログヘルパー */
    protected LogHelper logHelper = new LogHelper();

    static final String TraceMessageCycleStoped = "Weigh Cycle Stopped WeighId=";
    static final String ClientIsNoneRetry = "Client is Nothing. retry count=";

    /**
     * 定周期秤量処理開始.
     * @param weighCycle 定周期秤量処理
     * @param weighIfParam 連携パラメータ
     */
    @ErrorHandling
    @Asynchronous
    public void weighCycleStart(WeighCycle weighCycle, WeighIfParam weighIfParam) {

        try {
            final String methodName = "weighCycleStart";
            this.logHelper.fine(this.logger, className, methodName, "定周期スレッドが生成され開始されました。");

            // 接続待機回数
            int count = 0;

            // 秤量結果
            WeighResultDto weighResultDto = new WeighResultDto();

            //定周期収集セット
            weighIfParam.setIsCycleCollect(true);

            // --------------------------------------------------------------------------------
            // 定周期実行ループ
            // --------------------------------------------------------------------------------
            while (true) {

                this.logHelper.fine(this.logger, className, methodName, "定周期無限ループ続行中");

                //エラーを一度リセット
                weighResultDto.setGnomesAppException(null);

                // 定周期処理スレッド強制停止要求状態確認
                if (weighCycle.getThreadStopRequestState(weighIfParam.getWeighApparatusId())) {
                    this.logHelper.fine(this.logger, className, methodName, "スレッド停止要求が来ましたが、すぐには停止しません。続行します");

                    //すぐに停止せず、待機する
                    weighCycle.setConnectionState(weighIfParam.getWeighApparatusId(), false);
                }

                try {
                    // 秤量処理要求
                    //weighResultDto.setWeighedValue(this.contents.processWeigh(weighIfParam));
                    weighResultDto.setWeighedValue(this.processWeigh(weighIfParam));

                } catch (GnomesAppException e) {

                    this.logHelper.fine(this.logger, className, methodName, "秤量が収集されがExceptionが来たのでExceptionを設定");
                    weighResultDto.setGnomesAppException(e);

                } catch (GnomesException e) {

                    this.logHelper.fine(this.logger, className, methodName, "秤量が収集されがExceptionが来たのでExceptionを設定");
                    GnomesExceptionFactory gnomesExceptionFactory = new GnomesExceptionFactory();
                    weighResultDto.setGnomesAppException(
                            gnomesExceptionFactory.createGnomesAppException(null, e.getMessageNo(), e.getMessageParams()));

                }

                // WebSocket接続状態確認
                if (weighCycle.getConnectionState(weighIfParam.getWeighApparatusId())) {
                    this.logHelper.fine(this.logger, className, methodName,
                            "WebSocket接続状態確認がtrue(接続中)なので、秤量値[" + weighResultDto.getWeighedValue().toString() + "]を設定");

                    // 接続待機回数リセット
                    count = 0;
                    // 秤量結果(定周期)設定
                    weighCycle.setWeighResultMap(weighIfParam.getWeighApparatusId(), weighResultDto);

                    //                if (!Objects.isNull(weighResultDto.getGnomesAppException())) {
                    //                    count = weighIfParam.getWeighCycleRetryNum() + 1;
                    //                }

                } else {
                    this.logHelper.fine(this.logger, className, methodName, "WebSocket接続状態確認がfalse(切断中)なので、秤接続待機カウント＆切断待ち");

                    // 接続待機回数カウントアップ
                    count++;
                    logHelper.info(this.logger, className, methodName, ClientIsNoneRetry + count);
                    // 定周期監視リトライ回数を超えた場合、処理終了
                    if (weighIfParam.getWeighCycleRetryNum() < count) {
                        this.logHelper.fine(this.logger, className, methodName, "状態確認がfalse(切断)が続いたので切断。スレッドも終わり");

                        logHelper.info(this.logger, className, methodName,
                                TraceMessageCycleStoped + weighIfParam.getWeighApparatusId());
                        break;
                    }

                }

                try {
                    this.logHelper.fine(this.logger, className, methodName, weighIfParam.getWeighCycleRetryInterval() + "ms の間待機して、また無限ループへ");
                    // 定周期秤量間隔の時間だけ待機
                    Thread.sleep(weighIfParam.getWeighCycleRetryInterval());

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;

                }

            }
        }
        finally {
            // 定周期処理スレッド破棄
            WeighInfoControl.removeWeighCycleThread(weighIfParam.getWeighApparatusId());
        }
    }

    /**
     * 秤量処理
     * @param weighIfParam 連携パラメータ
     * @return 秤量結果
     * @throws GnomesAppException
     */
    public BigDecimal processWeigh(WeighIfParam weighIfParam) throws GnomesAppException {

        // コンテンツ側で処理を実装
        return null;

    }

}
