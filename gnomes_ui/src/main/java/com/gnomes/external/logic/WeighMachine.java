package com.gnomes.external.logic;

import java.util.Objects;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;

import org.apache.commons.text.StringEscapeUtils;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums;
import com.gnomes.common.constants.CommonEnums.CyclicWeighSyncAccessMode;
import com.gnomes.common.constants.CommonEnums.StreamMode;
import com.gnomes.common.constants.CommonEnums.WeighIsResponse;
import com.gnomes.common.constants.CommonEnums.WeighTransmissionType;
import com.gnomes.common.data.GnomesSystemBean;
import com.gnomes.common.dto.TransmissionResultDto;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.util.Ethernet;
import com.gnomes.common.util.EthernetStore;
import com.gnomes.external.data.ResponseFormatInfo;
import com.gnomes.external.data.WeighIfParam;
import com.gnomes.external.data.WeighResult;
import com.gnomes.system.logic.InsecureHostnameVerifier;
import com.gnomes.system.logic.InsecureTrustManager;

/**
 * 秤量器IF機能クラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/01/18 YJP/H.Yamada              初版
 * R0.01.02 2018/12/13 YJP/S.Kohno               タイムアウトのリトライを追加
 * R0.01.03 2019/05/13 YJP/S.Hosokawa            複数行読込、接続確認完了処理の追加
 * R0.01.04 2021/12/17 YJP-D/Jixin.Sun           設備 IF 連携機能のストリーミング方式対応
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class WeighMachine extends WeighMachineCommon
{

    /** クラス名 */
    private static final String className = "WeighMachine";

    // ロガー
    @Inject
    transient Logger            logger;

    /** ログヘルパー */
    protected LogHelper         logHelper = new LogHelper();

    @Inject
    private EthernetStore       ethernetStore;

    /** アプリケーションビーン */
    @Inject
    protected GnomesSystemBean  gnomesSystemBean;

    /**
     * 秤量処理.
     * <pre>
     * 秤量器IFパラメータをもとに秤量処理を行う。
     * </pre>
     * @param weighIfParam 秤量器IFパラメータ
     * @return 秤量結果
     */
    @ErrorHandling
    public WeighResult processWeigh(WeighIfParam weighIfParam)
    {
        //コマンドのUnescapeを実施
        weighIfParam.setSendCommand(StringEscapeUtils.unescapeJava(weighIfParam.getSendCommand()));

        //排他オブジェクトをWeighIfParamの秤量IDに格納されたweighIfParamMapから取得する。
        //先に作られていない可能性があり、その場合は排他の必要がないため
        //オブジェクトにthisを入れる
        Object syncObj = WeighInfoControl.weighCycleThreadMap.get(weighIfParam.getWeighApparatusId());

        if(Objects.isNull(syncObj)){
            syncObj = this;
        }

        synchronized(syncObj){

            //メソッド名
            final String methodName = "processWeigh";

            // 秤量結果
            WeighResult weighResult = new WeighResult();

            // RS-232C通信の場合
            if (WeighTransmissionType.RS232C.equals(WeighTransmissionType.getEnum(weighIfParam.getTransmissionType()))) {

                try {
                    // 接続先サービスURL
                    String servicePath = String.format("%s%s/%s/%s", weighIfParam.getRemoteServiceUri(), "restweigh",
                            "WeighMachineService", "executeWeigh");

                    //トレースログを吐く
                    logHelper.fine(this.logger, className, methodName,
                            "Edge Weigh Reeust path=" + servicePath + ": Weigh Param = " + weighIfParam.toDebugString());

                    weighResult = this.post(servicePath, Entity.json(weighIfParam), WeighResult.class);

                }
                catch (GnomesAppException e) {
                    // 秤量結果設定
                    weighResult.setWeighValue(null); // 秤量値
                    weighResult.setUnit(null); // 単位
                    weighResult.setStableValue(false); // 安定値フラグ
                    weighResult.setGnomesAppException(e); // エラー情報

                }

            }
            else if (WeighTransmissionType.ETHERNET.equals(WeighTransmissionType.getEnum(
                    weighIfParam.getTransmissionType()))) {
                // Ethernet通信の場合
                //トレースログを吐く
                logHelper.fine(this.logger, className, methodName, "EtherNet Weigh Param =" + weighIfParam.toDebugString());

                try {
                    ethernetStore.setWorking();

                    weighResult = this.executeWeigh(weighIfParam);

                }
                finally {

                    ethernetStore.resetWorking();
                }

            }

            //トレースログ
            if (!Objects.isNull(weighResult.getGnomesAppException())) {
                this.logHelper.severe(this.logger, className, methodName,
                        "Weigh result Exception Message = " + weighResult.getGnomesAppException().getMessage() + "MessageNo = (" + weighResult.getGnomesAppException().getMessageNo() + ")]");
            }
            if (!Objects.isNull(weighResult.getWeighValue())) {
                this.logHelper.severe(this.logger, className, methodName,
                        "Weigh result return value = [" + weighResult.getWeighValue().toString() + "]");
            }

            //値を風袋引きする
            //値が入っている場合　かつ、風袋引き数値が入っている場合
            //風袋引き数値をもとに値を引いて返す
            if((!Objects.isNull(weighResult.getWeighValue()) && !Objects.isNull(weighIfParam.getTareWeightAmount()))){
                weighResult.setWeighValue(weighResult.getWeighValue().subtract(weighIfParam.getTareWeightAmount()));
            }
            return weighResult;
        }

    }

    /**
     * 秤量処理.
     * <pre>
     * リモート呼び出し用に秤量処理を切り出し
     * </pre>
     * @param weighIfParam 秤量器IFパラメータ
     * @return 秤量結果
     */
    @ErrorHandling
    public WeighResult executeWeigh(WeighIfParam weighIfParam)
    {
        //メソッド名
        final String methodName = "executeWeigh";

        // 秤量結果
        WeighResult weighResult = new WeighResult();
        // 通信結果
        TransmissionResultDto resultDto = null;

        // リトライ回数初期化
        int unstableRetryCount = 0;
        int timeoutRetryCount = 0;

        //終端文字（改行コード）を取得
        String lineSeparator = makeLineSeparator(weighIfParam);

        while (true) {

            // Ethernet通信の場合
            // すでにオープンされたEthernetオブジェクトを得る
            Ethernet ethernet = ethernetStore.getEthernet(weighIfParam.getWeighApparatusId());
            boolean openFlag = false;
            boolean existFlag = false;

            if (ethernet != null) {

                // オープン済み、アクセス済みの場合
                // 通信結果情報を一度クリアする
                existFlag = true;
                openFlag = ethernet.openCheck();
            }
            else {
                ethernet = new Ethernet(lineSeparator);
            }

            //------------------------------------------------------------------------------------
            //  通信デバイスの状態を確認し、初めての時は初期処理を行う
            //------------------------------------------------------------------------------------
            if (!openFlag) {
                CreateInitialize(weighIfParam, resultDto, ethernet, existFlag, weighResult);
                //エラー情報が入っている場合、そのままリターンする
                if (!Objects.isNull(weighResult.getGnomesAppException())) {
                    return weighResult;
                }
            }

            //------------------------------------------------------------------------------------
            //  STEP1 パラメータに従い、RS232Cにコマンドを送る
            //------------------------------------------------------------------------------------
            if (resultDto == null || resultDto.getGnomesAppException() == null) {
                // データ送信
                resultDto = ethernet.send(weighIfParam.getSendCommand());
            }

            //------------------------------------------------------------------------------------
            //  STEP2 応答を確認する
            //  　ただし、秤量器IFパラメータ.応答有無が「応答なし」の場合、レシーブはスルーする
            //------------------------------------------------------------------------------------
            if (WeighIsResponse.TRUE.equals(WeighIsResponse.getEnum(weighIfParam.getIsResponse()))) {
                if (resultDto.getGnomesAppException() == null) {
                    // データ受信
                    resultDto = ethernet.receive(lineSeparator);
                }
            }
            //------------------------------------------------------------------------------------
            //  STEP3 応答を確認した結果、エラーがあった場合、エラー情報と確認してリターンする
            //------------------------------------------------------------------------------------
            // 通信結果にエラー情報が設定されている場合
            if (resultDto.getGnomesAppException() != null) {
                if (resultDto.getGnomesAppException().getMessageNo().equals(GnomesMessagesConstants.ME01_0061) || resultDto.getGnomesAppException().getMessageNo().equals(GnomesMessagesConstants.ME01_0069)) {
                    // タイムアウトの場合
                    timeoutRetryCount++;
                    if (timeoutRetryCount <= weighIfParam.getTimeoutRetryNum()) {
                        resultDto.setGnomesAppException(null);
                        // 上限以内の回数ならリトライ
                        continue;
                    }
                    else {
                        //上限越えの場合例外を格納しReturn
                        //トレースログ
                        this.logHelper.severe(this.logger, className, methodName, "Recieve TimeOut");

                        weighResult.setGnomesAppException(resultDto.getGnomesAppException());

                        return weighResult;
                    }
                }
                else {
                    // タイムアウト以外のエラーの場合例外を格納しReturn
                    weighResult.setGnomesAppException(resultDto.getGnomesAppException());

                    return weighResult;
                }
            }

            // 秤量器IFパラメータ.応答有無が「応答なし」の場合、そのまま返す
            if (!WeighIsResponse.TRUE.equals(WeighIsResponse.getEnum(weighIfParam.getIsResponse()))) {

                return weighResult;
            }


            //------------------------------------------------------------------------------------
            //  STEP4 応答の値のデータ判断処理
            //------------------------------------------------------------------------------------
            WeighResultJudgeStatus judgeStatus = JudgeForResultData(weighIfParam, resultDto, weighResult);

            // 応答ありでもゼロリセットのような OK,NG応答はそのままリターンする
            if (judgeStatus == WeighResultJudgeStatus.OKResult || judgeStatus == WeighResultJudgeStatus.NGResult){
                return weighResult;
            }

            //応答値が正しく入ったことを受けて値が加工されたのでそのままリターンする
            if (judgeStatus == WeighResultJudgeStatus.NormalResult) {
                weighResult.setGnomesAppException(null);
                return weighResult;
            }

            //定周期収集の場合は不安定値が返ってきたときでもそのまま値を返す
            if (judgeStatus == WeighResultJudgeStatus.UnstableResult && weighIfParam.getIsCycleCollect()) {
                weighResult.setGnomesAppException(null);
                return weighResult;
            }

            //認識外文字は再チャレンジする。また、不安定値も定周期収集の場合はリトライする
            if (judgeStatus == WeighResultJudgeStatus.UnstableResult || judgeStatus == WeighResultJudgeStatus.UnknownResult) {

                logHelper.fine(this.logger, className, methodName,
                        "Weigh Result is Unrecognized data Retry (" + unstableRetryCount + "/" + weighIfParam.getUnstableRetryNum());

                // リトライ回数カウントアップ
                unstableRetryCount++;

                // 不安定リトライ回数を、リトライ回数が超えた場合
                // 不安定値が連続して正常に取れなかったことを呼び出し元に返す
                if (weighIfParam.getUnstableRetryNum() < unstableRetryCount) {

                    logHelper.fine(this.logger, className, methodName, "Weigh Result Unrecognized data Retry Over");

                    GnomesExceptionFactory ef = new GnomesExceptionFactory();

                    //データ判断の種類に応じてメッセージを区別する
                    if (judgeStatus == WeighResultJudgeStatus.UnstableResult) {
                        weighResult.setGnomesAppException(ef.createGnomesAppException(null,
                                GnomesMessagesConstants.ME01_0226, resultDto.getRecvData()));
                    }
                    else if (judgeStatus == WeighResultJudgeStatus.UnknownResult) {
                        weighResult.setGnomesAppException(ef.createGnomesAppException(null,
                                GnomesMessagesConstants.ME01_0213, resultDto.getRecvData()));
                    }

                    return weighResult;
                }

            }

            //何らかエラーの場合
            // エラー情報がそのまま詰まっているのでそのままリターンする
            if (judgeStatus == WeighResultJudgeStatus.ErrorResult) {
                return weighResult;
            }

            //------------------------------------------------------------------------------------
            //  STEP5 応答の判断データが無い場合、または不安定値や知らない応答文字などが返ってたら
            //      正しい値になるまで繰り返す
            //------------------------------------------------------------------------------------

            try {
                // 不安定リトライ間隔待機
                //トレースログ
                this.logHelper.severe(this.logger, className, methodName,
                        "Weigh Retry Sleeping..." + weighIfParam.getUnstableRetryInterval() + "ms");
                Thread.sleep(weighIfParam.getUnstableRetryInterval());
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

        }

        return weighResult;

    }

    /**
     * 通信時の終端文字を得る
     *
     * @param weighIfParam 終端文字の情報が入っている
     * @return 終端文字 (CR or CR+LF)
     */
    private String makeLineSeparator(WeighIfParam weighIfParam)
    {
        //電文終端改行文字を応答情報から得るが、中がnullや空白でなかった場合のみ
        //設定して返す
        for(ResponseFormatInfo responceFormatInfo : weighIfParam.getResponseFormatInfoList()){
            String linefeedCode = responceFormatInfo.getLinefeedCode();
            if(linefeedCode != null && linefeedCode.length() > 0){
                return linefeedCode;
            }
        }

        //ここに来るときはCRかCR+LFかわからないので、nullを返す
        return null;
    }

    /**
     *
     * 通信回線のオープンと初期化
     * 読み飛ばし処理もある
     * エラーの時は weighResultのException情報に値が入るので呼び元はそれを見て状態を管理する
     *
     * @param weighIfParam  通信要求パラメータ
     * @param resultDto     通信結果データ
     * @param ethernet      イーサネットデバイスオブジェクト
     * @param existFlag     既にオブジェクトが作られている場合のフラグ
     * @param weighResult   秤量結果オブジェクト
     */
    @ErrorHandling
    private void CreateInitialize(WeighIfParam weighIfParam, TransmissionResultDto resultDto, Ethernet ethernet,
            boolean existFlag, WeighResult weighResult)
    {
        //メソッド名
        final String methodName = "CreateInitialize";

        //終端文字（改行コード）を取得
        String lineSeparator = makeLineSeparator(weighIfParam);


        // 通信回線を開く
        try {
            //トレースログを吐く
            this.logHelper.severe(this.logger, className, methodName,
                    "Ethernet Connect hostname=" + weighIfParam.getHostName());

            // 同期モードを取得
            Integer cyclicWeighSyncAccessModeValue = weighIfParam.getCyclicWeighSyncAccessMode();
            CyclicWeighSyncAccessMode cyclicWeighSyncAccessMode = CyclicWeighSyncAccessMode.getEnum(cyclicWeighSyncAccessModeValue);

            if ((cyclicWeighSyncAccessMode != CyclicWeighSyncAccessMode.ON) && (cyclicWeighSyncAccessMode != CyclicWeighSyncAccessMode.OFF)) {
                // 同期モードが設定されない場合、アプリケーションビーンの設定を参照
                cyclicWeighSyncAccessModeValue = this.gnomesSystemBean.getIsCyclicWeighSyncAccessMode();
                cyclicWeighSyncAccessMode = CyclicWeighSyncAccessMode.getEnum(cyclicWeighSyncAccessModeValue);
                if ((cyclicWeighSyncAccessMode == null) || (cyclicWeighSyncAccessMode.equals(CyclicWeighSyncAccessMode.NONE))) {
                    cyclicWeighSyncAccessMode = CyclicWeighSyncAccessMode.ON;
                }
            }

            // 同期モードを設定
            ethernet.setCyclicWeighSyncAccessMode(cyclicWeighSyncAccessMode);
            
            // 通信方式を取得
            Integer streamModeValue = weighIfParam.getStreamMode();
            StreamMode streamMode = StreamMode.getEnum(streamModeValue);

            // 通信方式を設定
            ethernet.setStreamMode(streamMode);

            resultDto = ethernet.open(weighIfParam.getUserId(), weighIfParam.getWeighApparatusId(),
                    weighIfParam.getHostName(), Integer.valueOf(weighIfParam.getPortNo()), weighIfParam.getTimeout());
        }
        finally {
            // エラー無くオープンできた場合はストアにオブジェクトを入れる
            if (resultDto.getGnomesAppException() == null && !existFlag) {
                ethernetStore.AddEthernet(weighIfParam.getWeighApparatusId(), ethernet);
            }
        }
        //読み飛ばし処理
        if (resultDto == null || resultDto.getGnomesAppException() == null) {
            if (weighIfParam.getOpenFinishJudgeFlag() != null && CommonEnums.OpenFinishJudgeFlag.ON.getValue() == weighIfParam.getOpenFinishJudgeFlag().intValue()) {

                int skipCount = 0;
                int skipLimit = 0;
                int skipTimeout = 0;
                //読み飛ばし用のパラメーターを取得
                //nullならエラーメッセージ
                if (weighIfParam.getOpenFinishJudgeTimeoutLimitCount() != null && weighIfParam.getOpenFinishJudgeTimeoutLimitMiliSecond() != null) {

                    skipLimit = weighIfParam.getOpenFinishJudgeTimeoutLimitCount();
                    skipTimeout = weighIfParam.getOpenFinishJudgeTimeoutLimitMiliSecond();

                }
                else {
                    GnomesExceptionFactory ef = new GnomesExceptionFactory();
                    weighResult.setGnomesAppException(ef.createGnomesAppException(null,
                            GnomesMessagesConstants.ME01_0214));
                    return;
                }

                if (resultDto.getGnomesAppException() == null) {
                    //読み飛ばし用のタイムアウト時間を設定
                    resultDto = ethernet.setTimeout(skipTimeout);
                    while (skipCount < skipLimit) {

                        // データ受信
                        resultDto = ethernet.receive(lineSeparator);

                        if (resultDto.getRecvDataList() == null || resultDto.getRecvDataList().isEmpty()) {
                            skipCount++;
                        }
                        else {
                            skipCount = 0;
                        }

                        //結果を初期化
                        resultDto.setGnomesAppException(null);
                        resultDto.setRecvData(null);
                        resultDto.setRecvDataList(null);

                    }
                }

                //タイムアウト時間を元の時間に設定
                if (resultDto == null || resultDto.getGnomesAppException() == null) {
                    resultDto = ethernet.setTimeout(weighIfParam.getTimeout());
                }
            }
        }
    }

    /**
     * サービス呼出処理(POSTメゾット).
     * @param servicePath サービスパス
     * @param paramEntity エンティティ
     * @param clazz 返却クラス
     * @return サービス呼び出し結果
     * @throws GnomesAppException
     * @see com.gnomes.common.logic.RsClient.post
     */
    @ErrorHandling
    private <T> T post(String servicePath, Entity<?> paramEntity, Class<T> clazz) throws GnomesAppException
    {
        final String methodName = "post";

        T resultValue = null;

        try {

            // RSClientオブジェクト
            Client rsClient = this.createClient();
            // 接続先サービスのURLを指定
            WebTarget target = rsClient.target(servicePath);
            // リクエストの生成
            Builder builder = target.request();
            // POSTメゾットの実行、送信パラメータと返却される型を指定
            resultValue = builder.post(paramEntity, clazz);

        }
        catch (GnomesAppException e) {
            throw e;
        }
        catch (GnomesException e) {
            throw e;
        }
        catch (NotFoundException e) { // HTTP 404 Not Found path設定ミス

            // Web サービス呼び出しに失敗しました。（接続先不正） ({0}) 詳細はエラーメッセージを確認してください。
            this.logHelper.severe(this.logger, className, methodName, null, GnomesMessagesConstants.ME01_0056);
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            throw ef.createGnomesAppException(null, GnomesMessagesConstants.ME01_0056, e);

        }
        catch (ProcessingException e) { // タイムアウト

            // Web サービス呼び出しに失敗しました。（タイムアウト） ({0}) 詳細はエラーメッセージを確認してください。
            this.logHelper.severe(this.logger, className, methodName, null, GnomesMessagesConstants.ME01_0057);
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            throw ef.createGnomesAppException(null, GnomesMessagesConstants.ME01_0057, e);

        }
        catch (Exception e) { // その他

            // Web サービス呼び出しに失敗しました。({0}) 詳細はエラーメッセージを確認してください。
            this.logHelper.severe(this.logger, className, methodName, null, GnomesMessagesConstants.ME01_0074);
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            throw ef.createGnomesAppException(null, GnomesMessagesConstants.ME01_0074, e);

        }

        return resultValue;

    }

    /**
     * RSClientオブジェクト生成.
     * @return RSClientオブジェクト
     * @throws GnomesAppException
     * @see com.gnomes.common.logic.RsClient.createClient
     */
    @ErrorHandling
    private Client createClient() throws GnomesAppException
    {

        Client rsClient = null;

        try {

            SSLContext sc = SSLContext.getInstance(CommonConstants.RSCLIENT_SECURE_SOCKET_PROTOCOL_TLS);//Java 8
            System.setProperty(CommonConstants.RSCLIENT_HTTP_PROTOCOLS,
                    CommonConstants.RSCLIENT_SECURE_SOCKET_PROTOCOL_TLS);//Java 8

            TrustManager[] trustAllCerts = {new InsecureTrustManager()};
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HostnameVerifier allHostsValid = new InsecureHostnameVerifier();

            rsClient = ClientBuilder.newBuilder().sslContext(sc).hostnameVerifier(allHostsValid).build();
            rsClient.property(CommonConstants.RSCLIENT_PROPERTY_TIMEOUT, 1);

        }
        catch (Exception e) {
            // Web サービス・クライアントの生成に失敗しました。 詳細はエラーメッセージを確認してください。
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            throw ef.createGnomesAppException(null, GnomesMessagesConstants.ME01_0073, e);

        }

        return rsClient;

    }

}
