package com.gnomes.common.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import javax.inject.Inject;

import com.gnomes.common.constants.CommonEnums.CyclicWeighSyncAccessMode;
import com.gnomes.common.constants.CommonEnums.StreamMode;
import com.gnomes.common.dto.TransmissionResultDto;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesMessagesConstants;

/**
 * Ethernet通信クラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/06/13 KCC/H.Yamada              初版
 * R0.01.02 2019/05/13 YJP/S.Hosokawa            複数行読込、接続確認完了処理の追加
 * R0.01.03 2021/12/17 YJP-D/Jixin.Sun           設備 IF 連携機能のストリーミング方式対応
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */

public class Ethernet {

    /** ソケット. */
    private Socket socket;

    /** 秤量器ID. */
    protected String weighMachineId;

    // ロガー
    @Inject
    transient Logger logger = Logger.getLogger(this.getClass().getName());

    /** ログヘルパー */
    @Inject
    protected LogHelper logHelper = new LogHelper();

    /** クラス名 */
    private static final String className ="Ethernet";

	/**
	 * アクセスされていない累積時間を貯めておく
	 */
	protected int sleepingTimes;

    /** 行セパレーター */
    protected String                    lineSeparator;

    /** 同期モード */
    protected CyclicWeighSyncAccessMode cyclicWeighSyncAccessMode;

    /** 通信方式 */
    protected StreamMode                streamMode;

    /** 秤量値取得スレッド */
    protected ReceiveThread             receiveThread;

    /**
	 * @return weighMachineId
	 */
	public String getWeighMachineId() {
		return weighMachineId;
	}


	/**
	 * @param weighMachineId
	 */
	public void setWeighMachineId(String weighMachineId) {
		this.weighMachineId = weighMachineId;
	}


	/**
	 * @return sleepingTimes
	 */
	public int getSleepingTimes() {
		return sleepingTimes;
	}


	/**
	 * @param sleepingTimes
	 */
	public void setSleepingTimes(int sleepingTimes) {
		this.sleepingTimes = sleepingTimes;
	}

    /**
     * 同期モードを取得
     * @return 同期モード
     */
    public CyclicWeighSyncAccessMode getCyclicWeighSyncAccessMode()
    {
        return cyclicWeighSyncAccessMode;
    }

    /**
     * 同期モードを設定
     * @param cyclicWeighSyncAccessMode 同期モード
     */
    public void setCyclicWeighSyncAccessMode(CyclicWeighSyncAccessMode cyclicWeighSyncAccessMode)
    {
        this.cyclicWeighSyncAccessMode = cyclicWeighSyncAccessMode;
    }

    /**
     * 通信方式を取得
     * @return 通信方式
     */
    public StreamMode getStreamMode()
    {
        return streamMode;
    }

    /**
     * 通信方式を設定
     * @param streamMode 通信方式
     */
    public void setStreamMode(StreamMode streamMode)
    {
        this.streamMode = streamMode;
    }

    /**
     * コンストラクタ.
     * @param lineSeparator 行セパレーター
     */
    public Ethernet(String lineSeparator)
    {
        this.socket = new Socket();
        this.weighMachineId = null;
        this.sleepingTimes = 0;

        this.lineSeparator = lineSeparator;

        this.cyclicWeighSyncAccessMode = CyclicWeighSyncAccessMode.ON;
        this.streamMode = StreamMode.COMMAND;
        this.receiveThread = null;
    }

    /**
     * 通信回線を開く.
     * <pre>
     * Ethernet通信回線を開く。
     * </pre>
     *
     * @param userId ユーザID
     * @param weighMachineId 秤量器ID
     * @param hostName ホスト名
     * @param port ポート番号
     * @param timeout タイムアウト
     * @return 通信結果
     */
    public TransmissionResultDto open(String userId,
            String weighMachineId, String hostName, int port, int timeout) {

        TransmissionResultDto result = new TransmissionResultDto();

        try {
            synchronized (this) {
                // 通信回線が開いているかのチェックを行う
                if ((this.socket == null) || (this.socket.isClosed())) {
                    // 通信回線が開いていない場合、ソケットを新規作成
                    this.socket = new Socket();
                }

                // 秤量器IDを変数に設定
                this.weighMachineId = weighMachineId;
                // ソケットアドレス作成
                InetSocketAddress endpoint = new InetSocketAddress(hostName, port);
                // 通信回線を開く
                this.socket.connect(endpoint, timeout);
                // 受信タイムアウトの設定
                this.socket.setSoTimeout(timeout);

                // 通信方式がストリーミング方式であるかをチェック
                if (this.isStreamingMode()) {
                    // 秤量値取得スレッドが作成されたかをチェック
                    if (this.receiveThread == null) {
                        // 秤量値取得スレッド作成
                        this.receiveThread = new ReceiveThread(this, userId, weighMachineId, hostName, port, timeout,
                                this.lineSeparator);
                    }
                }
            }

        }
        catch (SocketTimeoutException e) {
            // メッセージ：通信回線の接続に失敗しました。（タイムアウト）（秤量器ID：パラメータ.秤量器ID）
            // WeighMachineにてメッセージNoでタイムアウトであることを識別している
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            result.setGnomesAppException(ef.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0061, weighMachineId));

            this.closeSocket();

        }
        catch (SocketException e) {
            // メッセージ：通信条件の設定に失敗しました。（秤量器ID：パラメータ.秤量器ID）
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            result.setGnomesAppException(ef.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0062, weighMachineId));

            this.closeSocket();

        }
        catch (IOException e) {
            // メッセージ：通信回線の接続に失敗しました。（秤量器ID：パラメータ.秤量器ID）
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            result.setGnomesAppException(ef.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0070, weighMachineId));

            this.closeSocket();

        }
        return result;

    }

    /**
     * 通信回線が開いているかのチェックを行う。
     * <pre>
     * Ethernet通信回線が開いているかのチェックを行う。
     * </pre>
     *
     * @return 回線が開いているかどうか
     */
    public Boolean openCheck() {

        synchronized (this) {
            // 通信回線の状態を確認
            if (this.socket == null || this.socket.isClosed()) {
                return false;
            }
            else {
                return true;
            }
        }
    }

    /**
     * データ送信を行う.
     * <pre>
     * Ethernet通信回線にデータの送信を行う。
     * </pre>
     *
     * @param sendData 送信データ
     * @return 通信結果
     * @throws GnomesAppException
     */
    public TransmissionResultDto send(String sendData) {

        final String methodName = "send";

        TransmissionResultDto result = new TransmissionResultDto();

        try {
            synchronized (this) {
                // タイマーをリセット
                this.sleepingTimes = 0;

                // 通信方式がストリーミング方式であるかをチェックする
                if (this.isStreamingMode()) {
                    // 通信方式がストリーミング方式である場合、秤量値取得スレッドが起動されたかをチェックする
                    if ((this.receiveThread != null) && (!this.receiveThread.isAlive()) && (!this.receiveThread.isInterrupted())) {
                        // 秤量値取得スレッドを起動
                        this.receiveThread.start();
                    }
                }
                else {
                    // 通信方式がストリーミング方式ではない場合

                    // 通信回線の状態を確認
                    if (this.socket == null || this.socket.isClosed()) {
                        // メッセージ：通信回線がオープンされていないため、送信できません。（秤量器ID：変数.秤量器ID）
                        GnomesExceptionFactory ef = new GnomesExceptionFactory();
                        result.setGnomesAppException(ef.createGnomesAppException(null,
                                GnomesMessagesConstants.ME01_0065, this.weighMachineId));

                        return result;
                    }

                    // 通信回線送信ストリーム生成
                    PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                            this.socket.getOutputStream())));

                    // 送信データのトレースログを吐く
                    this.logHelper.fine(this.logger, className, methodName, "Ethernet Send Text=[" + sendData + "]");

                    // データ送信
                    writer.print(sendData);
                    writer.flush();
                }
            }

        }
        catch (IOException e) {
            // メッセージ：データの送信に失敗しました。（秤量器ID：変数.秤量器ID）
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            result.setGnomesAppException(ef.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0066, this.weighMachineId));

            this.closeSocket();

        }
        return result;

    }

    /**
     *  タイムアウト時間設定
     *  <pre>
     *  Ethernet通信のタイムアウト時間の設定を行う。
     *  </pre>
     *
     * @param timeout タイムアウト
     */
    public TransmissionResultDto setTimeout(int timeout) {
        TransmissionResultDto result = new TransmissionResultDto();

        try {
            synchronized (this) {
                // 受信タイムアウトの設定
                this.socket.setSoTimeout(timeout);
            }
        }
        catch (SocketException e) {
            // メッセージ：通信条件の設定に失敗しました。（秤量器ID：パラメータ.秤量器ID）
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            result.setGnomesAppException(ef.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0062, this.weighMachineId));

            this.closeSocket();
        }
        return result;

    }

    /**
     * データ受信を行う.
     * <pre>
     *  Ethernet通信回線からデータの受信を行う。
     * </pre>
     *
     * @return 通信結果
     */
    public TransmissionResultDto receive(String lineSeparator)
    {
        final String methodName = "recieve";
        final long sleepInterval = 100;

        TransmissionResultDto result = null;

        // タイマーをリセット
        synchronized (this) {
            this.sleepingTimes = 0;
        }
        try {
            // 通信方式がストリーミング方式であるかをチェックする
            if (this.isStreamingMode()) {
                if (this.receiveThread != null) {
                    if ((!this.receiveThread.isAlive()) && (!this.receiveThread.isInterrupted())) {
                        this.receiveThread.start();
                    }

                    while (Objects.isNull(result)) {
                        result = receiveThread.getTransmissionResultDto();
                        if (Objects.isNull(result)) {
                            Thread.sleep(sleepInterval);
                        }
                    }
                }
                else {
                    result = new TransmissionResultDto();
                    GnomesExceptionFactory ef = new GnomesExceptionFactory();
                    result.setGnomesAppException(ef.createGnomesAppException(null, GnomesMessagesConstants.ME01_0070,
                            this.weighMachineId));
                }
            }
            else {
                result = executeCommandRecieve(lineSeparator);
            }
        }
        catch (InterruptedException e) {
            this.logHelper.severe(this.logger, className, methodName, e.getMessage());

            Thread.currentThread().interrupt();
        }

        return result;
    }

    /**
     * 通信方式がストリーミング方式であるかをチェックする。
     * @return 通信方式がストリーミング方式であるか
     */
    private boolean isStreamingMode()
    {
        return ((this.cyclicWeighSyncAccessMode == CyclicWeighSyncAccessMode.OFF) && (this.streamMode == StreamMode.STREAMING));
    }

    /**
     *  コマンド方式のデータ受信を行う.
     *  <pre>
     *  Ethernet通信回線からデータの受信を行う。
     *  </pre>
     *
     * @return 通信結果
     */
    public TransmissionResultDto executeCommandRecieve(String lineSeparator)
    {

        final String methodName = "executeCommandRecieve";

        TransmissionResultDto result = new TransmissionResultDto();
        StringBuilder recvData = new StringBuilder();
        InputStream inStream = null;

        try {

            synchronized (this) {
                // 通信回線の状態を確認
                if (this.socket == null || this.socket.isClosed()) {
                    // メッセージ：通信回線がオープンされていないため、受信できません。（秤量器ID：変数.秤量器ID）
                    GnomesExceptionFactory ef = new GnomesExceptionFactory();
                    result.setGnomesAppException(ef.createGnomesAppException(null, GnomesMessagesConstants.ME01_0067,
                            this.weighMachineId));

                    return result;

                }

                // 通信回線受信ストリーム生成
                inStream = socket.getInputStream();
                // 受信データが読み込めるまで待機
                //while (inStream.available() == 0);
            }

            byte[] buffer = new byte[1024];
            String judgedLineSeparator = null;

            while (true) {
                // 受信データ読み込み
                int numRead = inStream.read(buffer);

                if (numRead == -1) {
                    break;
                }
                else {
                    // 受信データ読み出し
                    recvData.append(new String(buffer, 0, numRead));

                    judgedLineSeparator = JudgeLineSeparator(recvData.toString(), lineSeparator);

                    // 受信データの最後が改行コードである場合、処理終了
                    if (judgedLineSeparator != null && judgedLineSeparator.length() > 0) {
                        //トレースログを吐く
                        this.logHelper.fine(this.logger, className, methodName,
                                "Ethernet Revieve Text=[" + recvData.toString() + "]");
                        break;
                    }

                }

            }

            //判定改行コードが見つかっていない場合はもう一度確かめる
            //それでもだめならスローされる
            if (judgedLineSeparator == null || judgedLineSeparator.length() == 0) {
                judgedLineSeparator = JudgeLineSeparator(recvData.toString(), lineSeparator);
            }

            //改行コード毎に分割して受信データリストに追加
            List<String> recvDataList = Arrays.asList(recvData.toString().split(judgedLineSeparator));
            result.setRecvDataList(recvDataList);

        }
        catch (SocketTimeoutException e) {
            // メッセージ：通信でタイムアウトが発生しました。（秤量器ID：変数.秤量器ID）
            // WeighMachineにてメッセージNoでタイムアウトであることを識別している
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            result.setGnomesAppException(ef.createGnomesAppException(null, GnomesMessagesConstants.ME01_0069,
                    this.weighMachineId));

            //this.close();

        }
        catch (IOException e) {
            // メッセージ：データの受信に失敗しました。（秤量器ID：変数.秤量器ID）
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            result.setGnomesAppException(ef.createGnomesAppException(null, GnomesMessagesConstants.ME01_0068,
                    this.weighMachineId));

            //this.close();

        }
        return result;

    }

    /**
     *  ストリーミング方式のデータ受信を行う.
     *  <pre>
     *  Ethernet通信回線からデータの受信を行う。
     *  </pre>
     *
     * @return 通信結果
     */
    public TransmissionResultDto executeStreamingRecieve(String lineSeparator)
    {
        final String methodName = "executeStreamingRecieve";

        TransmissionResultDto result = new TransmissionResultDto();
        StringBuilder recvData = new StringBuilder();
        InputStream inStream = null;

        try {
            synchronized (this) {
                // 通信回線受信ストリーム生成
                inStream = socket.getInputStream();
            }

            byte[] buffer = new byte[1024];
            String judgedLineSeparator = null;

            while (true) {
                // 受信データ読み込み
                int numRead = inStream.read(buffer);

                if (numRead == -1) {
                    break;
                }
                else {
                    // 受信データ読み出し
                    recvData.append(new String(buffer, 0, numRead));

                    judgedLineSeparator = JudgeLineSeparator(recvData.toString(), lineSeparator);

                    // 受信データの最後が改行コードである場合、処理終了
                    if (judgedLineSeparator != null && judgedLineSeparator.length() > 0) {
                        //トレースログを吐く
                        this.logHelper.fine(this.logger, className, methodName,
                                "Ethernet Revieve Text=[" + recvData.toString() + "]");
                        break;
                    }
                }
            }

            //判定改行コードが見つかっていない場合はもう一度確かめる
            //それでもだめならスローされる
            if (judgedLineSeparator == null || judgedLineSeparator.length() == 0) {
                judgedLineSeparator = JudgeLineSeparator(recvData.toString(), lineSeparator);
            }

            //改行コード毎に分割して受信データリストに追加
            if (!"".equals(recvData.toString())) {
                List<String> recvDataList = Arrays.asList(recvData.toString().split(judgedLineSeparator));
                result.setRecvDataList(recvDataList);
            }
        }
        catch (SocketTimeoutException e) {
            // メッセージ：通信でタイムアウトが発生しました。（秤量器ID：変数.秤量器ID）
            // WeighMachineにてメッセージNoでタイムアウトであることを識別している
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            result.setGnomesAppException(ef.createGnomesAppException(null, GnomesMessagesConstants.ME01_0069,
                    this.weighMachineId));

            //            this.closeSocket();

        }
        catch (IOException e) {
            // メッセージ：データの受信に失敗しました。（秤量器ID：変数.秤量器ID）
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            result.setGnomesAppException(ef.createGnomesAppException(null, GnomesMessagesConstants.ME01_0068,
                    this.weighMachineId));

            this.closeSocket();
        }

        return result;
    }

    /**
     * 改行文字が含まれているかを見て、含まれていたらヒットした改行コードを返す
     * lineSeparator がnullの場合は、CR+LF , CRを順番に見て、ヒットしたらそのコードを返す
     * 何もヒットしなかったらnullを返す
     *
     * @param recvString 電文
     * @param lineSeparator 改行コード
     * @return ヒットしたときの改行コード
     */
    private String JudgeLineSeparator(String recvString,String lineSeparator){

        final String[] separators = { "\r\n","\r" };
        //lineSeparatorに制御コードがあれば、それを使って確認する
        if(lineSeparator != null && lineSeparator.length() > 0){
            if(recvString.endsWith(lineSeparator)){
                return lineSeparator;
            }
            else {
                //終端が違っていたらnullで返す
                return null;
            }
        }
        //lineSeparator がnullの場合は、CR-LF 、CRの順に確認する
        for(String separator : separators){
            if(recvString.endsWith(separator)){
                return separator;
            }
        }
        //自動判定でも取れない場合はnullを返す
        return null;
    }

    /**
     * 通信回線を閉じる.
     * <pre>
     * Socket通信回線を閉じる。
     * </pre>
     *
     * @return 通信結果
     */
    public TransmissionResultDto closeSocket()
    {
        TransmissionResultDto result = new TransmissionResultDto();

        try {
            synchronized (this) {
                if ((this.socket != null) && (!this.socket.isClosed())) {
                    this.socket.close();
                }
            }
        }
        catch (IOException e) {
            // メッセージ：通信回線のクローズに失敗しました。（秤量器ID：変数.秤量器ID）
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            result.setGnomesAppException(ef.createGnomesAppException(null, GnomesMessagesConstants.ME01_0071,
                    this.weighMachineId));
        }

        return result;
    }

    /**
     * 通信回線を閉じる.
     * <pre>
     * Ethernet通信回線を閉じる。
     * </pre>
     *
     * @return 通信結果
     */
    public TransmissionResultDto close()
    {
        TransmissionResultDto result = new TransmissionResultDto();

        final String methodName = "close";

        try {
            synchronized (this) {
                if (this.receiveThread != null) {
                    try {
                        // 秤量値取得スレッドを中断
                        this.receiveThread.interrupt();

                        // 秤量値取得スレッドが停止されたまで待つ
                        this.receiveThread.join();
                    }
                    catch (InterruptedException e) {
                        this.logHelper.fine(this.logger, className, methodName, e.getMessage());

                        this.receiveThread.interrupt();
                    }
                }

                if (this.socket != null) {
                    this.socket.close();
                    this.socket = null;
                }
            }
        }
        catch (IOException e) {
            // メッセージ：通信回線のクローズに失敗しました。（秤量器ID：変数.秤量器ID）
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            result.setGnomesAppException(ef.createGnomesAppException(null, GnomesMessagesConstants.ME01_0071,
                    this.weighMachineId));
        }

        return result;
    }

    /**
     *
     * 定周期で監視、一定期間無処理だったらtrueを返してEJBに消してもらう
     *
     * @return 一定期間処理が無かったらtrueを返す
     */
    public boolean monitorAndEliminateLeisure(long weighCloseTimeout)
    {
        synchronized (this) {
            this.sleepingTimes += 10000;

            if (this.sleepingTimes > weighCloseTimeout) {
                //一度あふれたら0リセットする
                this.sleepingTimes = 0;
                return true;
            }
            return false;
        }
    }

}
