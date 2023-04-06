package com.gnomes.common.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.TooManyListenersException;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums.WeighIsResponse;
import com.gnomes.common.dto.TransmissionResultDto;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.resource.GnomesMessagesConstants;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

/**
 * RS-232C通信クラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/06/14 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class Rs232c implements SerialPortEventListener {

    /** シリアルポート. */
    protected SerialPort serialPort;

    /** 秤量器ID. */
    protected String weighMachineId;

    /** 通信回線受信ストリーム. */
    protected InputStream inStream;

    /** 受信完了フラグ. */
    protected boolean receiveCompleteFlag;

    /** 通信結果. */
    protected TransmissionResultDto result;

    /** 改行コード */
    protected static final String LINE_SEPARATOR = "\r\n";

    /**
     * コンストラクタ.
     */
    public Rs232c() {

        this.serialPort = null;
        this.weighMachineId = null;
        this.inStream = null;
        this.receiveCompleteFlag = false;
        this.result = null;

    }

    /**
     * 通信回線を開く.
     * <pre>
     * RS-232C通信回線を開く。
     * </pre>
     *
     * @param userId ユーザID
     * @param weighMachineId 秤量器ID
     * @param port ポート番号
     * @param dataBit データビット長
     * @param stopBit ストップビット長
     * @param parity パリティ
     * @param baudrate ボーレート
     * @param timeout タイムアウト
     * @param isResponse 応答有無
     * @return 通信結果
     */
    public TransmissionResultDto open(String userId,
            String weighMachineId, String port, int dataBit,
            int stopBit, int parity, int baudrate, int timeout, int isResponse) {

        TransmissionResultDto openResult = new TransmissionResultDto();

        try {
            // 秤量器IDを変数に設定
            this.weighMachineId = weighMachineId;

            // ポート識別子の取得
            CommPortIdentifier portId =
                    CommPortIdentifier.getPortIdentifier(port);

            // 通信回線を開く
            this.serialPort = (SerialPort)portId.open(
                    weighMachineId, timeout);

            // 通信条件を設定
            this.setCommunicationProtocol(
                    baudrate, dataBit, stopBit, parity, timeout);

            // 受信イベント・リスナの登録
            this.serialPort.addEventListener(this);
            this.serialPort.notifyOnDataAvailable(true);

            // 通信回線受信ストリームの生成
            this.inStream = this.serialPort.getInputStream();

            // 受信完了フラグ初期化
            if (WeighIsResponse.TRUE.equals(WeighIsResponse.getEnum(isResponse))) {
                this.receiveCompleteFlag = false;
            } else {
                this.receiveCompleteFlag = true;
                this.result = new TransmissionResultDto();
            }

        } catch (NoSuchPortException e) {
            // メッセージ：ポート識別子の取得に失敗しました。（秤量器ID：パラメータ.秤量器ID）
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            openResult.setGnomesAppException(ef.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0060, weighMachineId));

        } catch (PortInUseException e) {
            // メッセージ：通信回線の接続に失敗しました。（タイムアウト）（秤量器ID：パラメータ.秤量器ID）
         // WeighMachineにてメッセージNoでタイムアウトであることを識別している
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            openResult.setGnomesAppException(ef.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0061, weighMachineId));

        } catch (UnsupportedCommOperationException e) {
            // メッセージ：通信条件の設定に失敗しました。（秤量器ID：変数.秤量器ID）
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            openResult.setGnomesAppException(ef.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0062, weighMachineId));

        } catch (TooManyListenersException e) {
            // メッセージ：イベント・リスナーの登録に失敗しました。（秤量器ID：パラメータ.秤量器ID）
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            openResult.setGnomesAppException(ef.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0063, weighMachineId));

        } catch (IOException e) {
            // メッセージ：受信ストリームの生成に失敗しました。（秤量器ID：パラメータ.秤量器ID）
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            openResult.setGnomesAppException(ef.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0064, weighMachineId));

        }

        return openResult;

    }

    /**
     * データ送信を行う.
     * <pre>
     * RS-232C通信回線にデータの送信を行う。
     * </pre>
     *
     * @param sendData 送信データ
     * @return 通信結果
     */
    public TransmissionResultDto send(String sendData) {

        TransmissionResultDto sendResult = new TransmissionResultDto();

        try {
            // 通信回線の状態確認
            if (this.serialPort == null) {
                // メッセージ：通信回線がオープンされていないため、送信できません。（秤量器ID：変数.秤量器ID）
                GnomesExceptionFactory ef = new GnomesExceptionFactory();
                sendResult.setGnomesAppException(ef.createGnomesAppException(null,
                        GnomesMessagesConstants.ME01_0065, this.weighMachineId));

                return sendResult;

            }

            // 通信回線送信ストリームを生成
            PrintWriter writer = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(
                            this.serialPort.getOutputStream(), "ASCII")));
            // 送信
            writer.println(sendData);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            // メッセージ：データの送信に失敗しました。（秤量器ID：変数.秤量器ID）
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            sendResult.setGnomesAppException(ef.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0066, this.weighMachineId));

        }
        return sendResult;

    }

    /**
     * データ受信を行う.
     * <pre>
     * RS-232C通信回線からデータの受信を行う。
     * </pre>
     *
     * @return 通信結果
     */
    public TransmissionResultDto receive() {

        TransmissionResultDto receiveResult = new TransmissionResultDto();

        if (this.serialPort == null) {

            // メッセージ：通信回線がオープンされていないため、受信できません。（秤量器ID：変数.秤量器ID）
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            receiveResult.setGnomesAppException(ef.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0067, this.weighMachineId));

            return receiveResult;

        }

        while (true) {
            // 受信完了フラグが true の場合
            if (this.receiveCompleteFlag) {
                return this.result;

            }

            try {
                Thread.sleep(CommonConstants.DEFAULT_SLEEP_TIME);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return receiveResult;
            }

        }

    }

    /**
     * 通信回線を閉じる.
     * <pre>
     * RS-232C通信回線を閉じる。
     * </pre>
     */
    public void close() {

        if (this.serialPort != null) {
            this.serialPort.close();
            this.serialPort = null;
        }

    }

    /**
     * シリアルポートで発生するイベントの受信処理.
     * <pre>
     * シリアルポートで発生するイベントの受信処理を行う。
     * </pre>
     *
     * @param paramSerialPortEvent シリアルポートイベント
     *
     */
    @Override
    public void serialEvent(SerialPortEvent paramSerialPortEvent) {

        if (paramSerialPortEvent.getEventType()
                == SerialPortEvent.DATA_AVAILABLE) {

            this.result = new TransmissionResultDto();

            try {

                StringBuilder recvData = new StringBuilder();

                while (true) {

                    byte[] buffer = new byte[1024];
                    int numRead = this.inStream.read(buffer);

                    if (numRead == -1) {
                        break;
                    } else if (numRead == 0) {

                        // メッセージ：通信でタイムアウトが発生しました。（秤量器ID：変数.秤量器ID）
                        // WeighMachineにてメッセージNoでタイムアウトであることを識別している
                        GnomesExceptionFactory ef =
                                new GnomesExceptionFactory();
                        this.result.setGnomesAppException(
                                ef.createGnomesAppException(null,
                                GnomesMessagesConstants.ME01_0069,
                                this.weighMachineId));

                        break;

                    } else {
                        // 受信データ読み出し
                        recvData.append(new String(buffer, 0, numRead));
                        // 受信データに改行コードが含まれる場合、処理終了
                        if (recvData.toString().indexOf(LINE_SEPARATOR) != -1) {
                            break;
                        }

                    }

                }

                if (recvData.length() > 0) {
                    this.result.setRecvData(recvData.toString());
                }

            } catch (IOException e) {
                // メッセージ：データの受信に失敗しました。（秤量器ID：変数.秤量器ID）
                GnomesExceptionFactory ef = new GnomesExceptionFactory();
                this.result.setGnomesAppException(ef.createGnomesAppException(
                        null, GnomesMessagesConstants.ME01_0068,
                        this.weighMachineId));

            } finally {
                this.receiveCompleteFlag = true;
            }

        }

    }

    /**
     * 通信条件設定.
     * <pre>
     * 通信条件の設定を行う。
     * </pre>
     *
     * @param baudrate ボーレート
     * @param dataBit データビット
     * @param stopBit ストップビット
     * @param parity パリティ
     * @param timeout タイムアウト
     * @throws UnsupportedCommOperationException
     */
    protected void setCommunicationProtocol(
            int baudrate, int dataBit, int stopBit,
            int parity, int timeout) throws UnsupportedCommOperationException {

        // シリアルポートの設定
        this.serialPort.setSerialPortParams(
                baudrate,    // ボーレート
                dataBit,     // データビット
                stopBit,     // ストップビット
                parity);     // パリティ

        // DTR制御の設定
        this.serialPort.setDTR(true);
        // RTS制御の設定
        this.serialPort.setRTS(true);

        // フロー制御の設定
        this.serialPort.setFlowControlMode(
                SerialPort.FLOWCONTROL_RTSCTS_IN
                | SerialPort.FLOWCONTROL_RTSCTS_OUT);

        // 受信イベントのタイムアウト設定
        this.serialPort.enableReceiveTimeout(timeout);

    }

}
