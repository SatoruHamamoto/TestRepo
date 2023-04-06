package com.gnomes.system.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;

import org.picketbox.util.StringUtil;

import com.gnomes.common.dao.BaseDao;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.system.entity.MstrMessageDefine;

/**
 * メッセージ定義 Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/04/28 KCC/T.Kamizuru            初版
 * R0.01.02 2018/10/31 YJP/S.Hamamoto			 メッセージがマスターになければ例外を吐く
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class MstrMessageDefineDao extends BaseDao implements Serializable {

    /**
     * コンストラクタ
     */
    public MstrMessageDefineDao() {
    }

    private static final String ERRMES_NOT_NO = "The specified message no does not exist in the master message define table. MessageNo= ";

    private static final String ERRMES_CATCH_EXCEPTION = "Exception occurred while getting message from master message definition. MessageNo = [%s] Exception Message = [%s]";

    private static final String className = "MstrMessageDefineDao";

    /**
     * メッセージ定義 取得
     * @return メッセージ定義
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrMessageDefine> getMstrMessageDefine()
            throws GnomesAppException {

        List<MstrMessageDefine> datas = gnomesSystemModel
                .getMstrMessageDefineList();

        return datas;
    }

    /**
     * メッセージ定義 取得
     * @param messageNo メッセージNo
     * @return メッセージ定義
     */
    @TraceMonitor
    @ErrorHandling
    public MstrMessageDefine getMstrMessageDefine(String messageNo)
            throws GnomesAppException {

        final String methodName = "getMstrMessageDefine";
        MstrMessageDefine data = null;

        try {
            // パラメータチェック
            if (StringUtil.isNullOrEmpty(messageNo)) {
                // ME02.0009:「パラメータが不正です。({0})」
                GnomesAppException ex = exceptionFactory
                        .createGnomesAppException(null,
                                GnomesMessagesConstants.ME01_0050,
                                new Object[]{super.createMessageParamsRequired(
                                        MstrMessageDefine.COLUMN_NAME_MESSAGE_NO,
                                        messageNo)});
                throw ex;

            }

            //メッセージIDを検索
            List<MstrMessageDefine> result = gnomesSystemModel
                    .getMstrMessageDefineList().stream()
                    .filter(item -> item.getMessage_no().equals(messageNo))
                    .collect(Collectors.toList());

            //データが取得出来なかったら例外を吐く
            if (result.isEmpty()) {

                this.logHelper.severe(this.logger, methodName, methodName,
                        ERRMES_NOT_NO + messageNo);

                //無限ループを起こすため、messageNoがME01.0001については、例外をスローせずデータを特別に入れる
                if (messageNo.equals(GnomesMessagesConstants.ME01_0001)) {
                    data = new MstrMessageDefine();
                    data.setMessage_no(GnomesMessagesConstants.ME01_0001);
                    data.setResource_id(GnomesMessagesConstants.ME01_0001);
                    data.setCategory(2);
                    data.setMessage_level(10);
                    data.setIs_message_history_rec(1);
                    data.setIs_logging(0);
                    data.setIs_notice_push(1);
                    data.setMessage_btn_mode(1);
                    data.setMessage_default_btn_mode(1);
                    data.setMessage_title_resource_id(
                            GnomesMessagesConstants.YY01_0055);
                    data.setMessage_text_resource_id(
                            GnomesMessagesConstants.YY01_0056);

                    return data;
                }
                //(Rev1) どのメッセージIDでも取れなかったら固定で入れる
                else {
                    data = new MstrMessageDefine();
                    data.setMessage_no(messageNo); //引数を使用
                    data.setResource_id(messageNo); //引数を使用
                    data.setCategory(2); //警告メッセージ
                    data.setMessage_level(10); //操作履歴レベル
                    data.setIs_message_history_rec(1); //履歴には保存する
                    data.setIs_logging(0); //ロギングしない
                    data.setIs_notice_push(1); //push通知する
                    data.setMessage_btn_mode(1); //ダイアログはOKのみ表示
                    data.setMessage_default_btn_mode(1); //デフォルトはOKボタン
                    data.setMessage_title_resource_id(null); //メールは吐かない
                    data.setMessage_text_resource_id(null); //メールは吐かない

                    return data;
                }
            } else {
                data = result.get(0);
            }
        }
        /**
         * どの状態でもスローしない
         */
        catch (Exception ex) {

            this.logHelper.severe(this.logger, methodName, methodName,
                    String.format(ERRMES_CATCH_EXCEPTION, ex.getMessage(),
                            messageNo));

            data = new MstrMessageDefine();

            //messageNoが取れない場合は「アプリケーション例外」
            if (StringUtil.isNullOrEmpty(messageNo)) {
                data.setMessage_no(GnomesMessagesConstants.ME01_0001);
                data.setResource_id(GnomesMessagesConstants.ME01_0001);
            } else {
                data.setMessage_no(messageNo); //引数を使用
                data.setResource_id(messageNo); //引数を使用
            }
            data.setCategory(2); //警告メッセージ
            data.setMessage_level(10); //操作履歴レベル
            data.setIs_message_history_rec(1); //履歴には保存する
            data.setIs_logging(0); //ロギングしない
            data.setIs_notice_push(1); //push通知する
            data.setMessage_btn_mode(1); //ダイアログはOKのみ表示
            data.setMessage_default_btn_mode(1); //デフォルトはOKボタン
            data.setMessage_title_resource_id(null); //メールは吐かない
            data.setMessage_text_resource_id(null); //メールは吐かない

            return data;
        }
        return data;
    }

    /**
     * メッセージ定義 取得
     * @param messageNo メッセージNo
     * @return メッセージ定義
     */
    @TraceMonitor
    @ErrorHandling
    public MstrMessageDefine getMstrMessageDefineForMessageNo(String messageNo)
            throws GnomesAppException {

        // パラメータチェック
        if (StringUtil.isNullOrEmpty(messageNo)) {

            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            MstrMessageDefine.COLUMN_NAME_MESSAGE_NO,
                            messageNo)});
        }

        List<MstrMessageDefine> result = super.gnomesSystemModel
                .getMstrMessageDefineList().stream()
                .filter(item -> item.getMessage_no().equals(messageNo))
                .collect(Collectors.toList());

        if (!result.isEmpty()) {
            return result.get(0);
        }

        return null;

    }

}
