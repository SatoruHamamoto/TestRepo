package com.gnomes.external.data;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gnomes.common.constants.CommonEnums;

/**
 * 秤量器I/Fパラメータ
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/01/17 YJP/H.Yamada              初版
 * R0.01.02 2018/12/13 YJP/S.Kohno               パラメータ見直し
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeighIfParam {

    /** ユーザID */
    private String userId;

    /** 通信種別 */
    private Integer transmissionType;

    /** 秤量器ID */
    private String weighApparatusId;

    /** ポート番号 */
    private String portNo;

    /** データビット長 */
    private Integer dataBitLen;

    /** ストップビット長 */
    private Integer stopBitLen;

    /** パリティ */
    private Integer parity;

    /** ボーレート */
    private Integer baudrate;

    /** ホスト名 */
    private String hostName;

    /** タイムアウト値 */
    private Integer timeout;

    /** 通信タイムアウトリトライ回数 **/
    private Integer timeoutRetryNum = 0;

	/**不安定リトライ間隔 */
    private Integer unstableRetryInterval;

    /** 不安定時リトライ回数 */
    private Integer unstableRetryNum;

    /** 定周期秤量間隔 */
    private Integer weighCycleRetryInterval;

    /** 定周期監視リトライ回数 */
    private Integer weighCycleRetryNum;

    /** 応答待機時間
     * 定周期収集の収集値Mapが空だった場合のリトライ待機時間 */
    private Integer responseInterval;

    /** 接続開始完了判定実行フラグ */
    private Integer openFinishJudgeFlag;

    /** 接続開始完了判定タイムアウト時間（ミリ秒） */
    private Integer openFinishJudgeTimeoutLimitMiliSecond;

    /** 接続開始完了判定が完了となる連続タイムアウト回数 */
    private Integer openFinishJudgeTimeoutLimitCount;

    /** 接続先 サービスURI */
    private String remoteServiceUri;

    /** 送信コマンド文字列 */
    private String sendCommand;

    /** 応答有無 */
    private Integer isResponse;

    /** 応答フォーマット情報リスト */
    private List<ResponseFormatInfo> responseFormatInfoList;

    /** データ開始位置（識別文字列） */
    private Integer beginIndexIdentify;

    /** 文字列長（識別文字列） */
    private Integer lengthIdentify;

    /** データ開始位置（符号） */
    private Integer beginIndexSign;

    /** 文字列長（符号） */
    private Integer lengthSign;

    /** データ開始位置（秤量値） */
    private Integer beginIndexWeigh;

    /** 文字列長（秤量値） */
    private Integer lengthWeigh;

    /** データ開始位置（単位） */
    private Integer beginIndexUnit;

    /** 文字列長（単位） */
    private Integer lengthUnit;

    /** 秤量値小数点以下桁数丸め実施フラグ */
    private Boolean roundCalcFlag = false;

    /** 丸め実施後小数点以下桁数 */
    private Integer roundDecimalPlace;

    /** 丸め演算方法区分 */
    private CommonEnums.RoundCalculateDiv roundCalculateDiv;

    /** 定周期収集かを示すフラグ。DBにはなく、定周期収集の処理でON(TRUE)にする*/
    private Boolean isCycleCollect = false;

    /** 風袋値（秤量インジケータ用) 単位換算済*/
    private BigDecimal tareWeightAmount;

    /** 同期モード */
    private Integer cyclicWeighSyncAccessMode;

    /** 通信方式 */
    private Integer streamMode;

    /**
     * ユーザIDを取得
     * @return ユーザID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * ユーザIDを設定
     * @param userId ユーザID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 通信種別を取得
     * @return 通信種別
     */
    public Integer getTransmissionType() {
        return transmissionType;
    }

    /**
     * 通信種別を設定
     * @param transmissionType 通信種別
     */
    public void setTransmissionType(Integer transmissionType) {
        this.transmissionType = transmissionType;
    }

    /**
     * 秤量器ID（ID + "-" + 通信種別）を取得
     * @return 秤量器ID（ID + "-" + 通信種別）
     */
    public String getWeighApparatusId() {
        return weighApparatusId;
    }

    /**
     * 秤量器ID（ID + "-" + 通信種別）を設定
     * @param weighApparatusId 秤量器ID（ID + "-" + 通信種別）
     */
    public void setWeighApparatusId(String weighApparatusId) {
        this.weighApparatusId = weighApparatusId;
    }

    /**
     * ポート番号を取得
     * @return ポート番号
     */
    public String getPortNo() {
        return portNo;
    }

    /**
     * ポート番号を設定
     * @param portNo ポート番号
     */
    public void setPortNo(String portNo) {
        this.portNo = portNo;
    }

    /**
     * データビット長を取得
     * @return データビット長
     */
    public Integer getDataBitLen() {
        return dataBitLen;
    }

    /**
     * データビット長を設定
     * @param dataBitLen データビット長
     */
    public void setDataBitLen(Integer dataBitLen) {
        this.dataBitLen =dataBitLen;
    }

    /**
     * ストップビット長を取得
     * @return ストップビット長
     */
    public Integer getStopBitLen() {
        return stopBitLen;
    }

    /**
     * ストップビット長を設定
     * @param stopBitLen ストップビット長
     */
    public void setStopBitLen(Integer stopBitLen) {
        this.stopBitLen = stopBitLen;
    }

    /**
     * パリティを取得
     * @return パリティ
     */
    public Integer getParity() {
        return parity;
    }

    /**
     * パリティを設定
     * @param parity パリティ
     */
    public void setParity(Integer parity) {
        this.parity = parity;
    }

    /**
     * ボーレートを取得
     * @return ボーレート
     */
    public Integer getBaudrate() {
        return baudrate;
    }

    /**
     * ボーレートを設定
     * @param baudrate ボーレート
     */
    public void setBaudrate(Integer baudrate) {
        this.baudrate = baudrate;
    }

    /**
     * ホスト名を取得
     * @return ホスト名
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * ホスト名を設定
     * @param hostName ホスト名
     */
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    /**
     * タイムアウト値を取得
     * @return タイムアウト値
     */
    public Integer getTimeout() {
        return timeout;
    }

    /**
     * タイムアウト値を設定
     * @param timeout タイムアウト値
     */
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    /**
     * 通信タイムアウトリトライ回数を取得
     * @return
     */
    public Integer getTimeoutRetryNum() {
		return timeoutRetryNum;
	}

    /**
     * 通信エラーリトライ回数を設定
     * @param timeoutRetryNum
     */
	public void setTimeoutRetryNum(Integer timeoutRetryNum) {
		this.timeoutRetryNum = timeoutRetryNum;
	}

    /**
     * 不安定時リトライ間隔を取得
     * @return 不安定時リトライ間隔
     */
    public Integer getUnstableRetryInterval() {
        return unstableRetryInterval;
    }

    /**
     * 不安定時リトライ間隔を設定
     * @param unstableRetryInterval 不安定時リトライ間隔
     */
    public void setUnstableRetryInterval(Integer unstableRetryInterval) {
        this.unstableRetryInterval = unstableRetryInterval;
    }

    /**
     * 不安定時リトライ回数を取得
     * @return 不安定時リトライ回数
     */
    public Integer getUnstableRetryNum() {
        return unstableRetryNum;
    }

    /**
     * 不安定時リトライ回数を設定
     * @param unstableRetryNum 不安定時リトライ回数
     */
    public void setUnstableRetryNum(Integer unstableRetryNum) {
        this.unstableRetryNum = unstableRetryNum;
    }

    /**
     * 定周期秤量間隔を取得
     * @return 定周期秤量間隔を設定
     */
    public Integer getWeighCycleRetryInterval() {
        return weighCycleRetryInterval;
    }

    /**
     * 定周期秤量間隔を取得
     * @param weighCycleRetryInterval 定周期秤量間隔
     */
    public void setWeighCycleRetryInterval(Integer weighCycleRetryInterval) {
        this.weighCycleRetryInterval = weighCycleRetryInterval;
    }

    /**
     * 定周期監視リトライ回数を取得
     * @return 定周期監視リトライ回数
     */
    public Integer getWeighCycleRetryNum() {
        return weighCycleRetryNum;
    }

    /**
     * 定周期監視リトライ回数を設定
     * @param weighCycleRetryNum 定周期監視リトライ回数
     */
    public void setWeighCycleRetryNum(Integer weighCycleRetryNum) {
        this.weighCycleRetryNum = weighCycleRetryNum;
    }

    /**
     * 応答待機時間を取得
     * @return 応答待機時間
     */
    public Integer getResponseInterval() {
        return responseInterval;
    }

    /**
     * 応答待機時間を設定
     * @param responseInterval 応答待機時間
     */
    public void setResponseInterval(Integer responseInterval) {
        this.responseInterval = responseInterval;
    }

    /**
     * 接続開始完了判定実行フラグを取得
     * @return 接続開始完了判定実行フラグ
     */
    public Integer getOpenFinishJudgeFlag() {
        return openFinishJudgeFlag;
    }

    /**
     * 接続開始完了判定実行フラグを設定
     * @param openFinishJudgeFlag 接続開始完了判定実行フラグ
     */
    public void setOpenFinishJudgeFlag(Integer openFinishJudgeFlag) {
        this.openFinishJudgeFlag = openFinishJudgeFlag;
    }

    /**
     * 接続開始完了判定タイムアウト時間（ミリ秒）を取得
     * @return 接続開始完了判定タイムアウト時間（ミリ秒）
     */
    public Integer getOpenFinishJudgeTimeoutLimitMiliSecond() {
        return openFinishJudgeTimeoutLimitMiliSecond;
    }

    /**
     * 接続開始完了判定タイムアウト時間（ミリ秒）を設定
     * @param openFinishJudgeTimeoutLimitMiliSecond 接続開始完了判定タイムアウト時間（ミリ秒）
     */
    public void setOpenFinishJudgeTimeoutLimitMiliSecond(Integer openFinishJudgeTimeoutLimitMiliSecond) {
        this.openFinishJudgeTimeoutLimitMiliSecond = openFinishJudgeTimeoutLimitMiliSecond;
    }

    /**
     * 接続開始完了判定が完了となる連続タイムアウト回数を取得
     * @return 接続開始完了判定が完了となる連続タイムアウト回数
     */
    public Integer getOpenFinishJudgeTimeoutLimitCount() {
        return openFinishJudgeTimeoutLimitCount;
    }

    /**
     * 接続開始完了判定が完了となる連続タイムアウト回数を設定
     * @param openFinishJudgeTimeoutLimitCount 接続開始完了判定が完了となる連続タイムアウト回数
     */
    public void setOpenFinishJudgeTimeoutLimitCount(Integer openFinishJudgeTimeoutLimitCount) {
        this.openFinishJudgeTimeoutLimitCount = openFinishJudgeTimeoutLimitCount;
    }

    /**
     * 接続先 サービスURIを取得
     * @return 接続先 サービスURI
     */
    public String getRemoteServiceUri() {
        return remoteServiceUri;
    }

    /**
     * 接続先 サービスURIを設定
     * @param remoteServiceUri 接続先 サービスURI
     */
    public void setRemoteServiceUri(String remoteServiceUri) {
        this.remoteServiceUri = remoteServiceUri;
    }

    /**
     * 送信コマンド文字列を取得
     * @return 送信コマンド文字列
     */
    public String getSendCommand() {
        return sendCommand;
    }

    /**
     * 送信コマンド文字列を設定
     * @param sendCommand 送信コマンド文字列
     */
    public void setSendCommand(String sendCommand) {
        this.sendCommand = sendCommand;
    }

    /**
     * 応答有無を取得
     * @return 応答有無
     */
    public Integer getIsResponse() {
        return isResponse;
    }

    /**
     * 応答有無を設定
     * @param isResponse 応答有無
     */
    public void setIsResponse(Integer isResponse) {
        this.isResponse = isResponse;
    }
    /**
     * データ開始位置（識別文字列）を取得
     * @return データ開始位置（識別文字列）
     */
    public Integer getBeginIndexIdentify() {
        return beginIndexIdentify;
    }

    /**
     * データ開始位置（識別文字列）を設定
     * @param beginIndexIdentify データ開始位置（識別文字列）
     */
    public void setBeginIndexIdentify(Integer beginIndexIdentify) {
        this.beginIndexIdentify = beginIndexIdentify;
    }

    /**
     * 文字列長（識別文字列）を取得
     * @return 文字列長（識別文字列）
     */
    public Integer getLengthIdentify() {
        return lengthIdentify;
    }

    /**
     * 文字列長（識別文字列）を設定
     * @param lengthIdentify 文字列長（識別文字列）
     */
    public void setLengthIdentify(Integer lengthIdentify) {
        this.lengthIdentify = lengthIdentify;
    }

    /**
     * データ開始位置（符号）を取得
     * @return データ開始位置（符号）
     */
    public Integer getBeginIndexSign() {
        return beginIndexSign;
    }

    /**
     * データ開始位置（符号）を設定
     * @param beginIndexSign データ開始位置（符号）
     */
    public void setBeginIndexSign(Integer beginIndexSign) {
        this.beginIndexSign = beginIndexSign;
    }

    /**
     * 文字列長（符号）を取得
     * @return 文字列長（符号）
     */
    public Integer getLengthSign() {
        return lengthSign;
    }

    /**
     * 文字列長（符号）を設定
     * @param lengthSign 文字列長（符号）
     */
    public void setLengthSign(Integer lengthSign) {
        this.lengthSign = lengthSign;
    }

    /**
     * データ開始位置（秤量値）を取得
     * @return データ開始位置（秤量値）
     */
    public Integer getBeginIndexWeigh() {
        return beginIndexWeigh;
    }

    /**
     * データ開始位置（秤量値）を設定
     * @param beginIndexWeigh データ開始位置（秤量値）
     */
    public void setBeginIndexWeigh(Integer beginIndexWeigh) {
        this.beginIndexWeigh = beginIndexWeigh;
    }

    /**
     * 文字列長（秤量値）を取得
     * @return 文字列長（秤量値）
     */
    public Integer getLengthWeigh() {
        return lengthWeigh;
    }

    /**
     * 文字列長（秤量値）を設定
     * @param lengthWeigh 文字列長（秤量値）
     */
    public void setLengthWeigh(Integer lengthWeigh) {
        this.lengthWeigh = lengthWeigh;
    }

    /**
     * データ開始位置（単位）を取得
     * @return データ開始位置（単位）
     */
    public Integer getBeginIndexUnit() {
        return beginIndexUnit;
    }

    /**
     * データ開始位置（単位）を設定
     * @param beginIndexUnit データ開始位置（単位）
     */
    public void setBeginIndexUnit(Integer beginIndexUnit) {
        this.beginIndexUnit = beginIndexUnit;
    }

    /**
     * 文字列長（単位）を取得
     * @return 文字列長（単位）
     */
    public Integer getLengthUnit() {
        return lengthUnit;
    }

    /**
     * 文字列長（単位）を設定
     * @param lengthUnit 文字列長（単位）
     */
    public void setLengthUnit(Integer lengthUnit) {
        this.lengthUnit = lengthUnit;
    }

    /**
     * 応答フォーマット情報リストを取得
     * @return 応答フォーマット情報リスト
     */
    public List<ResponseFormatInfo> getResponseFormatInfoList() {
        return responseFormatInfoList;
    }

    /**
     * 応答フォーマット情報リストを設定
     * @param responseFormatInfoList 応答フォーマット情報リスト
     */
    public void setResponseFormatInfoList(List<ResponseFormatInfo> responseFormatInfoList) {
        this.responseFormatInfoList = responseFormatInfoList;
    }


    /**
     * 秤量値小数点以下桁数丸め実施フラグを取得
     * @return roundCalcFlag
     */
    public Boolean getRoundCalcFlag() {
        return roundCalcFlag;
    }

    /**
     * 秤量値小数点以下桁数丸め実施フラグを設定
     * @param roundCalcFlag 秤量値小数点以下桁数丸め実施フラグ
     */
    public void setRoundCalcFlag(Boolean roundCalcFlag) {
        this.roundCalcFlag = roundCalcFlag;
    }

    /**
     * 丸め実施後小数点以下桁数を取得
     * @return roundDecimalPlace
     */
    public Integer getRoundDecimalPlace() {
        return roundDecimalPlace;
    }

    /**
     * 丸め実施後小数点以下桁数を設定
     * @param roundDecimalPlace 丸め実施後小数点以下桁数
     */
    public void setRoundDecimalPlace(Integer roundDecimalPlace) {
        this.roundDecimalPlace = roundDecimalPlace;
    }


    /**
     * 丸め演算方法区分 を取得
     * @return 丸め演算方法区分
     */
    public CommonEnums.RoundCalculateDiv getRoundCalculateDiv() {
        return roundCalculateDiv;
    }

    /**
     * 丸め演算方法区分 を設定
     * @param roundCalculateDiv 丸め演算方法区分
     */
    public void setRoundCalculateDiv(CommonEnums.RoundCalculateDiv roundCalculateDiv) {
        this.roundCalculateDiv = roundCalculateDiv;
    }

    /**
     * 定周期収集をするかどうかを取得する
     * @return true:定周期収集 false:その他（ワンショット）
     */
    public Boolean getIsCycleCollect()
    {
        return isCycleCollect;
    }

    /**
     * 風袋値（秤量インジケータ用)
     * @return tareWeightAmount
     */
    public BigDecimal getTareWeightAmount()
    {
        return tareWeightAmount;
    }

    /**
     * 風袋値（秤量インジケータ用)
     * @param tareWeightAmount セットする tareWeightAmount
     */
    public void setTareWeightAmount(BigDecimal tareWeightAmount)
    {
        this.tareWeightAmount = tareWeightAmount;
    }

    /**
     * 周期収集をするかどうかを設定する
     * @param isCycleCollect true:定周期収集 false:その他（ワンショット）
     */
    public void setIsCycleCollect(Boolean isCycleCollect)
    {
        this.isCycleCollect = isCycleCollect;
    }

    /**
     * 同期モードを取得
     * @return 同期モード
     */
    public Integer getCyclicWeighSyncAccessMode() {
        return cyclicWeighSyncAccessMode;
    }

    /**
     * 同期モードを設定
     * @param cyclicWeighSyncAccessMode 同期モード
     */
    public void setCyclicWeighSyncAccessMode(Integer cyclicWeighSyncAccessMode) {
        this.cyclicWeighSyncAccessMode = cyclicWeighSyncAccessMode;
    }

    /**
     * 通信方式を取得
     * @return 通信方式
     */
    public Integer getStreamMode() {
        return streamMode;
    }

    /**
     * 通信方式を設定
     * @param streamMode 通信方式
     */
    public void setStreamMode(Integer streamMode) {
        this.streamMode = streamMode;
    }

    /**
     * このオブジェクトの内容をデバッグ用にダンプする
     *
     * @return デバッグ文字化されたパラメータの内容
     */
    public String toDebugString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(" userId:" + userId);
        sb.append(" transmissionType:" + transmissionType);
        sb.append(" weighApparatusId:" + weighApparatusId);
        sb.append(" hostName:" + hostName);
        sb.append(" portNo:" + portNo);
        sb.append(" sendCommand:" + sendCommand);
        sb.append(" isResponse:" + isResponse);
        sb.append(" timeout:" + timeout);
        sb.append(" responseInterval:" + responseInterval);
        sb.append(" remoteServiceUri:" + remoteServiceUri);

        return sb.toString();
    }


}
