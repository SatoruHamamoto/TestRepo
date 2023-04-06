package com.gnomes.common.util;

import java.util.logging.Logger;

import javax.inject.Inject;

import com.gnomes.common.dto.TransmissionResultDto;
import com.gnomes.common.logging.LogHelper;

/**
 * 秤量値取得スレッド
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2021/12/30 YJP-D/Jixin.Sun           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class ReceiveThread extends Thread
{
    /** クラス名 */
    private static final String     CLASS_NAME = "ReceiveThread";

    // ロガー
    @Inject
    transient Logger                logger     = Logger.getLogger(this.getClass().getName());

    /** ログヘルパー */
    @Inject
    protected LogHelper             logHelper  = new LogHelper();

    /** Ethernet通信 */
    protected Ethernet              ethernet;

    /** ユーザID */
    protected String                userId;

    /** 秤量器ID */
    protected String                weighMachineId;

    /** ホスト名 */
    protected String                hostName;

    /** ポート番号 */
    protected int                   port;

    /** タイムアウト */
    protected int                   timeout;

    /** 行セパレーター */
    private String                  lineSeparator;

    /** 秤量器通信結果 */
    protected TransmissionResultDto transmissionResultDto;

    /**
     * 秤量器通信結果を取得
     * @return 秤量器通信結果
     */
    public TransmissionResultDto getTransmissionResultDto()
    {
        return this.transmissionResultDto;
    }

    /**
     * 秤量器通信結果を設定
     * @param transmissionResultDto 秤量器通信結果
     */
    public void setTransmissionResultDto(TransmissionResultDto transmissionResultDto)
    {
        this.transmissionResultDto = transmissionResultDto;
    }

    /**
     * コンストラクタ
     * @param ethernet Ethernet通信
     * @param userId ユーザID
     * @param weighMachineId 秤量器ID
     * @param hostName ホスト名
     * @param port ポート番号
     * @param timeout タイムアウト
     * @param lineSeparator 行セパレーター
     */
    public ReceiveThread(Ethernet ethernet, String userId, String weighMachineId, String hostName, int port,
            int timeout, String lineSeparator)
    {
        this.ethernet = ethernet;

        this.userId = userId;
        this.weighMachineId = weighMachineId;
        this.hostName = hostName;
        this.port = port;
        this.timeout = timeout;
        this.lineSeparator = lineSeparator;

        this.transmissionResultDto = null;
    }

    @Override
    public void run()
    {
        final String methodName = "run";
        final long defaultInterval = 100;

        try {
            while (!this.isInterrupted()) {

                // 通信回線が開いているかをチェック
                if (!this.ethernet.openCheck()) {
                    // 通信回線が開いていない場合、通信回線を開く
                    ethernet.open(this.userId, this.weighMachineId, this.hostName, this.port, this.timeout);
                }

                if (this.isInterrupted()) {
                    break;
                }

                long interval = defaultInterval;

                // 通信回線が開いているかをチェック
                if (this.ethernet.openCheck()) {
                    // ストリーミング方式のデータ受信を行う
                    TransmissionResultDto result = this.ethernet.executeStreamingRecieve(lineSeparator);

                    if (this.transmissionResultDto == null) {
                        // 初回で、最新の通信結果を使う
                        this.transmissionResultDto = result;
                    }
                    else {
                        if (result.getGnomesAppException() == null) {
                            // 次回以降、正常の通信結果のみを使う
                            this.transmissionResultDto = result;

                            // 正常の通信結果を受けた場合、スリープを行わない
                            interval = 0;
                        }
                    }
                }

                Thread.sleep(interval);
            }
        }
        catch (InterruptedException e) {
            this.logHelper.severe(this.logger, CLASS_NAME, methodName, e.getMessage());
            ReceiveThread.currentThread().interrupt();
        }
    }

}
