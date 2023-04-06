package com.gnomes.external.logic;

import java.math.BigDecimal;
import java.util.Objects;

import com.gnomes.common.constants.CommonEnums.WeighResponseType;
import com.gnomes.common.dto.TransmissionResultDto;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.external.data.ResponseFormatInfo;
import com.gnomes.external.data.WeighIfParam;
import com.gnomes.external.data.WeighResult;

/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/12/06 19:57 YJP/S.Hamamoto           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
*/

/**
 * 秤量器通信ロジックの共通親クラス
 * 　使われる箇所がEtherNetタイプやEdgeタイプなどプロトコル制御が
 * 　異なるのはサブクラスで、ルールや判定ロジックなどは共通処理で行う
 * @author 03501213
 *
 */
public class WeighMachineCommon
{

    /** 応答文字列 マイナス. */
    protected static final String STR_MINUS = "-";

    /**
     * データ判断結果ステータスの定義
     * @author 03501213
     *
     */
    protected enum WeighResultJudgeStatus
    {
        /**
         * 正常終了し値も格納されました。
         */
        NormalResult,

        /**
         * 不安定値として返されました。秤量値は獲得していません
         */
        UnstableResult,

        /**
         * コマンドが正しく動きました。（成功）
         * (値を返さない系のコマンドの正常応答)
         */
        OKResult,

        /**
         * コマンドの応答が失敗と正しく応答を返しました。
         */
        NGResult,

        /**
         * 判定情報が存在しませんでした。
         */
        JudgeRuleNotFound,

        /**
         * 想定しえない文字が受信されました
         */
        UnknownResult,

        /**
         * 何か問題があり、値は取れませんでした。
         */
        ErrorResult,
    }

    /**
     * 秤量値の応答データが安定値か、不安定値か、異常だったのかを判定し
     *   安定値だったら値を取得して返す
     *
     * @param weighIfParam  秤量器設定
     * @param result        生の値情報(weighResultの一部）
     * @param weighResult   秤量値オブジェクト
     * @return 判定結果
     */
    protected WeighResultJudgeStatus JudgeForResultData(WeighIfParam weighIfParam, TransmissionResultDto result,
            WeighResult weighResult)
    {

        try {
            //------------------------------------------------------------------------------------
            // 応答データ判定情報が無いならば何もせずリターンする
            //------------------------------------------------------------------------------------
            if (Objects.isNull(weighIfParam.getResponseFormatInfoList())) {
                return WeighResultJudgeStatus.JudgeRuleNotFound;
            }

            // 応答フォーマットリストの数を順番に確認する
            // 安定値不安定値の判断をする
            for (ResponseFormatInfo formatInfo : weighIfParam.getResponseFormatInfoList()) {

                //受信した秤量結果文字を回す
                for (String recvData : result.getRecvDataList()) {

                    // 識別文字列取得
                    String identify = this.editRecvData(recvData, weighIfParam.getBeginIndexIdentify(),
                            weighIfParam.getLengthIdentify());

                    // 判定区分確認
                    if (!identify.equals(formatInfo.getResponseDivString())) {
                        // 次の判定区分確認をする
                        continue;
                    }

                    // 応答区分が安定の場合
                    if (WeighResponseType.STABLE.equals(WeighResponseType.getEnum(formatInfo.getResponseType()))) {

                        System.out.println("Weigh Result is Stable [" + recvData + "]");

                        //数値が取れなかったらUnknownResultが返る
                        return setWeighResultForStable(weighResult, weighIfParam, recvData);

                    }
                    // 応答区分が不安定の場合
                    if (WeighResponseType.UNSTABLE.equals(WeighResponseType.getEnum(formatInfo.getResponseType()))) {

                        System.out.println("Weigh Result is UnStable [" + recvData + "]");

                        //不安定値と判断したと返す。数値が取れなかったらUnknownResultが返る
                        return setWeighResultForUnstable(weighResult, weighIfParam, recvData);
                    }
                    if (WeighResponseType.SUCCESS.equals(WeighResponseType.getEnum(formatInfo.getResponseType()))) {
                        System.out.println("Weigh Result is Success [" + recvData + "]");

                        //不安定値と判断したと返す。数値が取れなかったらUnknownResultが返る
                        return setWeighResultForSuccess(weighResult, weighIfParam, recvData);
                    }
                    if (WeighResponseType.FAILED.equals(WeighResponseType.getEnum(formatInfo.getResponseType()))) {
                        System.out.println("Weigh Result is Failed [" + recvData + "]");

                        //不安定値と判断したと返す。数値が取れなかったらUnknownResultが返る
                        return setWeighResultForFailed(weighResult, weighIfParam, recvData);
                    }
                }

            }
            // 応答区分がDBの定義と違う場合エラー
            System.out.println("Weigh Result is Unrecognized data");

            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            weighResult.setGnomesAppException(ef.createGnomesAppException(null, GnomesMessagesConstants.ME01_0213,
                    result.getRecvData()));

            return WeighResultJudgeStatus.UnknownResult;
        }
        catch (Exception e) {
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            weighResult.setGnomesAppException(ef.createGnomesAppException(null, GnomesMessagesConstants.ME01_0001,
                    e.getMessage()));

            return WeighResultJudgeStatus.ErrorResult;
        }
    }

    /**
     * 安定値データ編集
     *
     * @param weighResult    設定する対象の秤量結果データ
     * @param formatInfo    処理する応答フォーマット情報
     * @param weighIfParam    秤量パラメータ
     */
    @ErrorHandling
    protected WeighResultJudgeStatus setWeighResultForStable(WeighResult weighResult, WeighIfParam weighIfParam,
            String recvData)
    {

        // 符号取得
        String sign = this.editRecvData(recvData, weighIfParam.getBeginIndexSign(), weighIfParam.getLengthSign());

        // 秤量値取得
        String strWeighValue = this.editRecvData(recvData, weighIfParam.getBeginIndexWeigh(),
                weighIfParam.getLengthWeigh()).trim();

        // 単位取得
        String unit = this.editRecvData(recvData, weighIfParam.getBeginIndexUnit(),
                weighIfParam.getLengthUnit()).trim();

        BigDecimal weighValue;
        try {
            weighValue = new BigDecimal(strWeighValue);
        }
        catch (NumberFormatException ex) {
            //電源OFF等で正しい数値文字が帰ってこなくなる場合は例外的にエラーにする
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            weighResult.setGnomesAppException(ef.createGnomesAppException(null, GnomesMessagesConstants.ME01_0068,
                    weighIfParam.getWeighApparatusId()));

            return WeighResultJudgeStatus.UnknownResult;
        }

        if (weighIfParam.getRoundCalcFlag() == true) {
            // 丸め演算実施フラグONの場合小数点以下桁数丸め
            weighValue = ConverterUtils.roundCalculate(weighValue, weighIfParam.getRoundDecimalPlace(),
                    weighIfParam.getRoundCalculateDiv());
        }

        if (STR_MINUS.equals(sign)) {
            weighValue = weighValue.multiply(new BigDecimal(-1));
        }

        // 秤量結果設定
        weighResult.setWeighValue(weighValue); // 秤量値
        weighResult.setUnit(unit); // 単位
        weighResult.setStableValue(true); // 安定値フラグ

        return WeighResultJudgeStatus.NormalResult;
    }

    /**
     *
     * 不安定値データ編集
     *
     * @param weighResult
     *            設定する対象の秤量結果データ
     * @param formatInfo
     *            処理する応答フォーマット情報
     * @param rs232c
     *            RS232Cオブジェクト
     * @param weighIfParam
     *            秤量パラメータ
     */
    protected WeighResultJudgeStatus setWeighResultForUnstable(WeighResult weighResult, WeighIfParam weighIfParam,
            String recvData)
    {

        // 符号取得
        String sign = this.editRecvData(recvData, weighIfParam.getBeginIndexSign(), weighIfParam.getLengthSign());

        // 秤量値取得
        String strWeighValue = this.editRecvData(recvData, weighIfParam.getBeginIndexWeigh(),
                weighIfParam.getLengthWeigh()).trim();

        // 単位取得
        String unit = this.editRecvData(recvData, weighIfParam.getBeginIndexUnit(),
                weighIfParam.getLengthUnit()).trim();

        BigDecimal weighValue;

        try {
            weighValue = new BigDecimal(strWeighValue);
        }
        catch (NumberFormatException ex) {
            //電源OFF等で正しい数値文字が帰ってこなくなる場合は例外的にエラーにする
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            weighResult.setGnomesAppException(ef.createGnomesAppException(null, GnomesMessagesConstants.ME01_0068,
                    weighIfParam.getWeighApparatusId()));

            return WeighResultJudgeStatus.UnknownResult;
        }

        if (weighIfParam.getRoundCalcFlag() == true) {
            // 丸め演算実施フラグONの場合小数点以下桁数丸め
            weighValue = ConverterUtils.roundCalculate(weighValue, weighIfParam.getRoundDecimalPlace(),
                    weighIfParam.getRoundCalculateDiv());
        }

        if (STR_MINUS.equals(sign)) {
            weighValue = weighValue.multiply(new BigDecimal(-1));
        }

        // 秤量結果設定
        weighResult.setWeighValue(weighValue); // 秤量値
        weighResult.setUnit(unit); // 単位
        weighResult.setStableValue(false); // 安定値フラグ

        return WeighResultJudgeStatus.UnstableResult;
    }

    /**
     * 成功応答を編集する
     *  成功応答は値を返さないタイプの返却値なので、値の編集はない。
     *
     * @param weighResult   設定する対象の秤量結果データ
     * @param weighIfParam  処理する応答フォーマット情報
     * @param recvData      RS232Cオブジェクト
     * @return              秤量パラメータ
     */
    protected WeighResultJudgeStatus setWeighResultForSuccess(WeighResult weighResult, WeighIfParam weighIfParam,
            String recvData)
    {
        // 秤量結果設定
        weighResult.setWeighValue(null); // 秤量値
        weighResult.setUnit(null); // 単位
        weighResult.setStableValue(false); // 安定値フラグ

        return WeighResultJudgeStatus.OKResult;
    }

    /**
     * NG応答を編集
     *  応答は値を返さないタイプの返却値なので、値の編集はない。
     * @param weighResult   設定する対象の秤量結果データ
     * @param weighIfParam  処理する応答フォーマット情報
     * @param recvData      RS232Cオブジェクト
     * @return              秤量パラメータ
     */
    protected WeighResultJudgeStatus setWeighResultForFailed(WeighResult weighResult, WeighIfParam weighIfParam,
            String recvData)
    {
        // 秤量結果設定
        weighResult.setWeighValue(null); // 秤量値
        weighResult.setUnit(null); // 単位
        weighResult.setStableValue(false); // 安定値フラグ

        return WeighResultJudgeStatus.NGResult;
    }
    /**
     * 受信データ編集.
     *
     * <pre>
     * 受信データから指定された文字数を切り取り返却する。
     * </pre>
     *
     * @param value
     *            受信データ
     * @param beginIndex
     *            開始位置
     * @param length
     *            文字列長
     * @return 編集後受信データ ( 引数がNGの場合０文字）
     */
    protected String editRecvData(String recvData, int beginIndex, int length)
    {

        //検査したい文字の文字数を計算
        int endLength = beginIndex + length - 1;

        //処理できない変数値をチェックし、だめなら空文字を返す
        if (beginIndex <= 0 || endLength <= 0) {
            return "";
        }

        //編集用バッファにlength分の文字を作る
        //lengthに満たない場合はスペースで埋める
        String checkRecvData = copyArraysFull(beginIndex + length, recvData);

        while (true) {

            if (endLength <= checkRecvData.length()) {

                break;
            }

            endLength--;

        }

        return checkRecvData.substring(beginIndex - 1, endLength);

    }
    /**
     * 固定長のSPACE文字列を生成し、recvDataで中を埋める<br>
     * Arrays.fill(char[] a, char val)の中のロジックをコピー版
     *
     * @param length データ長
     * @return length分のSPACE文字列
     */
    public static String copyArraysFull(int length, String recvData)
    {

        char[] newChar = new char[length];
        char[] recvChar = recvData.toCharArray();

        //recvDataの内容はそのまま転記する
        //recvDataの内容を超えたところは空白で埋める
        for (int i = 0, len = newChar.length; i < len; i++) {
            if (i < recvChar.length) {
                newChar[i] = recvChar[i];
            }
            else {
                newChar[i] = ' ';
            }
        }

        return new String(newChar);

    }
}
