package com.gnomes.system.logic;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums;
import com.gnomes.common.constants.CommonEnums.PrintoutStatus;
import com.gnomes.common.constants.CommonEnums.ReprintFlag;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.GnomesTransactional;
import com.gnomes.common.persistence.GnomesEntityManager;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.CurrentTimeStamp;
import com.gnomes.system.data.printout.PrintOutCallbackInfo;
import com.gnomes.system.entity.HistoryPrintout;
import com.gnomes.system.entity.HistoryPrintoutParameter;
import com.gnomes.system.entity.TmpQueuePrintout;
import com.gnomes.system.entity.TmpQueuePrintoutParameter;

import biz.grandsight.ex.rs.CReportGen;

/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2020/04/01 11:03 YJP/S.Hamamoto           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
*/
/**
 * キュー処理専用クラス。K01APrintOutよりコールされる。
 *
 * @author 03501213
 *
 */
@Dependent
public class K01A01PrintOutQueueProc extends PrintOutLogic {

    /** バッチエンティティマネージャー */
    @Inject
    protected GnomesEntityManager em;

    private static final String className = "K01A01PrintOutQueueProc";

    /** 電子ファイル作成区分（電子保存） */
    private static final int IS_FILE_CREATE_TYPE = 3;

    /** 空文字（エラー時reportIdがnullになりエラーメッセージでExceptionにならないよう代替値） */
    private static final String EMPTY_STRING = "";

    /**
     * 帳票印字のキュー取得・印字実行処理
     * ※この範囲でトランザクションが制御される
     * @param localeId          ロケールＩＤ
     * @return
     */
    @ErrorHandling
    @GnomesTransactional
    public boolean printOutQueueProc(String localeId) {
        final String methodName = "printOutQueueProc";

        /** キューの1件分のDTO */
        TmpQueuePrintout tmpQueuePrintout = null;

        /** キューの印刷パラメータ */
        List<TmpQueuePrintoutParameter> tmpQueuePrintoutParameters = null;

        /** 印字キー情報 */
        HashMap<String, String> phvalue = null;

        /** 印字履歴に書ける状態かどうか（デフォルトfalse) */
        boolean historyOut = false;

        // 処理成功フラグ
        boolean successFlag = false;

        try {

            //印字キュー取得
            tmpQueuePrintout = getTmpQueuePrintoutFromQueue(em.getEntityManager());

            // 該当データが存在しない場合
            // （すべてのキューを処理し終えたか、最初からキューがないか）
            if (Objects.isNull(tmpQueuePrintout)) {
                //キューがないので親の繰り返しチェック制御を終了する
                return false;
            }

            //----------------------------------------------------
            //キューのパラメータ取得
            //----------------------------------------------------
            tmpQueuePrintoutParameters = getTmpQueuePrintParameterFromEntity(tmpQueuePrintout, em.getEntityManager());

            // 印字キー情報取得
            phvalue = this.getPhvalue(tmpQueuePrintoutParameters);

            // 印字履歴に書ける状態を示す。
            historyOut = true;

            //----------------------------------------------------
            //印字処理メイン
            //----------------------------------------------------

            int printer_type = tmpQueuePrintout.getPrinter_type();
            // 帳票印字（ラベル）
            if (printer_type == CommonEnums.PrintType.Label.getValue()) {
                successFlag = this.printRequestMain(tmpQueuePrintout, phvalue, em.getEntityManager(), localeId);
                // 帳票印字（一覧）
            } else if (printer_type == CommonEnums.PrintType.List.getValue()) {
                successFlag = this.printRequestMain(tmpQueuePrintout, phvalue, em.getEntityManager(), localeId);
                // 帳票印字（多重）
            } else if (printer_type == CommonEnums.PrintType.Multiple.getValue()) {
                successFlag = this.printRequestMainMultiple(tmpQueuePrintout, phvalue, em.getEntityManager(), localeId);
                // 帳票印字（多段）
            } else if (printer_type == CommonEnums.PrintType.MultiStage.getValue()) {
                successFlag = this.printRequestMainMultiStage(tmpQueuePrintout, phvalue, em.getEntityManager(), localeId);
                // 帳票印字（多段キー変更改ページ無し）
            } else if (printer_type == CommonEnums.PrintType.MultiStageNoNewPage.getValue()) {
                successFlag = this.printRequestMainMultiStageNoNewPage(tmpQueuePrintout, phvalue, em.getEntityManager(), localeId);
                // 帳票印字（多重多段）
            } else if (printer_type == CommonEnums.PrintType.MultipleMultiStage.getValue()) {
                successFlag = this.printRequestMainMultipleMultiStage(tmpQueuePrintout, phvalue, em.getEntityManager(), localeId);
                // 帳票印字（多重棚卸一覧）
            } else if (printer_type == CommonEnums.PrintType.MultipleInventory.getValue()) {
                successFlag = this.printRequestMainMultipleInventory(tmpQueuePrintout, phvalue, em.getEntityManager(), localeId);
            }
        } catch (Exception e) {

            //ログに記録する
            this.logHelper.severe(logger, className, methodName, e.getMessage() + makeQueueInfo(tmpQueuePrintout), e);

            if (historyOut) {
                //Exceptionになったので履歴に書く
                InsertPrintHistory(e.getMessage(), tmpQueuePrintout, null, em.getEntityManager(), phvalue);
                this.InsertMessageForReport(e.getMessage(), tmpQueuePrintout, null, phvalue);
            }
            //この処理失敗を意味する。コミットはする。ループはやめる
            return false;
        } finally {
            //----------------------------------------------------
            // 印字キュー削除(結果がどうであろうと）
            //----------------------------------------------------
            if (!Objects.isNull(tmpQueuePrintout)) {
                this.tmpQueuePrintoutDao.delete(tmpQueuePrintout, em.getEntityManager());
            }

            //----------------------------------------------------
            // 印字キーパラメータ削除
            //----------------------------------------------------
            if (!Objects.isNull(tmpQueuePrintoutParameters)) {
                this.tmpQueuePrintoutParameterDao.delete(tmpQueuePrintoutParameters, em.getEntityManager());
            }
        }

        //ループは継続する
        return true;

    }

    /**
     * キューテーブルから1レコード分を取得
     */
    /**
     * @param em    エンティティマネージャー
     * @return
     */
    private TmpQueuePrintout getTmpQueuePrintoutFromQueue(EntityManager em) {
        // 印字キュー取得
        TmpQueuePrintout tmpQueuePrintout = this.tmpQueuePrintoutDao.getQueue(em);

        return tmpQueuePrintout;
    }
    /**
     * キューの中に一致した印字キューのパラメータを抜き出す
     * @param tmpQueuePrintout  キューエンティティ
     * @param em    エンティティマネージャー
     * @return
     */
    private List<TmpQueuePrintoutParameter> getTmpQueuePrintParameterFromEntity(TmpQueuePrintout tmpQueuePrintout,
            EntityManager em) {
        // 印字キューパラメータ取得
        List<TmpQueuePrintoutParameter> tmpQueuePrintoutParameters = tmpQueuePrintoutParameterDao.getParameter(em,
                tmpQueuePrintout.getQueue_printout_key());

        return tmpQueuePrintoutParameters;
    }

    /**
     * 印字キー情報取得(Json読込み)
     * @param params 印字キューパラメータ情報
     * @return 印字キー情報
     * @throws GnomesAppException
     */
    private HashMap<String, String> getPhvalue(List<TmpQueuePrintoutParameter> params) {

        HashMap<String, String> phvalue = new HashMap<>();

        for (TmpQueuePrintoutParameter param : params) {
            phvalue.put(param.getParameter_name(), param.getParameter_value());
        }
        return phvalue;

    }

    /**
    *
    * 印字履歴を記録する。印刷が正常、エラーにかかわらずコールされる
    *
    * @param printMessage      履歴に残す任意の文字列
    * @param tmpQueuePrintout  キューのエンティティオブジェクト
    * @param reportId          帳票ID
    * @param em                エンティティマネージャー
    * @param phvalue           印字キー情報
    */
    private void InsertPrintHistory(String printMessage, TmpQueuePrintout tmpQueuePrintout, String reportId,
            EntityManager em, HashMap<String, String> phvalue) {

        // 印字履歴記録
        HistoryPrintout historyPrintout = this.editInsHistoryPrintout(tmpQueuePrintout, reportId, printMessage);
        this.historyPrintoutDao.insert(historyPrintout, em);

        // 帳票ラベル印字履歴パラメータ
        List<HistoryPrintoutParameter> hpp = this.editInsHistoryPrintoutParameter(historyPrintout, phvalue);
        this.historyPrintoutParameterDao.insert(hpp, em);

    }
    /**
     * 帳票ラベル印字履歴パラメータ編集
     * @param historyPrintout 帳票ラベル印字履歴
     * @param phvalue パラメータ
     * @return 帳票ラベル印字履歴パラメータ
     */
    private List<HistoryPrintoutParameter> editInsHistoryPrintoutParameter(HistoryPrintout historyPrintout,
            HashMap<String, String> phvalue) {

        List<HistoryPrintoutParameter> result = new ArrayList<HistoryPrintoutParameter>();

        for (Map.Entry<String, String> item : phvalue.entrySet()) {
            HistoryPrintoutParameter ins = new HistoryPrintoutParameter();

            // 帳票ラベル印字履歴パラメータキー
            ins.setHistory_printout_parameter_key(UUID.randomUUID().toString());
            // nk帳票ラベル印字履歴キー (FK)
            ins.setHistory_printout_key(historyPrintout.getHistory_printout_key());
            // nk要求イベントID
            ins.setEvent_id(historyPrintout.getEvent_id());
            // nk要求内連番
            ins.setRequest_seq(historyPrintout.getRequest_seq());
            // nkパラメータ名
            ins.setParameter_name(item.getKey());
            //パラメータ値
            ins.setParameter_value(item.getValue());
            result.add(ins);
        }

        return result;
    }

    /**
     * ログやエラーメッセージ用にキューの内容をダンプする
     *
     * @param tmpQueuePrintout
     * @return
     */
    private String makeQueueInfo(TmpQueuePrintout tmpQueuePrintout) {
        StringBuilder sb = new StringBuilder();
        sb.append(" Queue Info : ");
        sb.append(" queue_printout_key = " + tmpQueuePrintout.getQueue_printout_key());
        sb.append(" print_command_no = " + tmpQueuePrintout.getPrint_command_no());
        sb.append(" report_name = " + tmpQueuePrintout.getReport_name());

        return sb.toString();
    }

    /**
     * 帳票ラベル印字履歴編集
     * @param tmpQueuePrintout 印字キュー
     * @param reportId リポートID
     * @param message エラーメッセージ
     * @return 帳票ラベル印字履歴
     */
    private HistoryPrintout editInsHistoryPrintout(TmpQueuePrintout tmpQueuePrintout, String reportId, String message) {

        HistoryPrintout historyPrintout = new HistoryPrintout();

        Timestamp date = ConverterUtils.utcToTimestamp(ConverterUtils
                .dateToLocalDateTime(CurrentTimeStamp.getSystemCurrentTimeStamp()).atZone(ZoneId.systemDefault()));

        // 帳票ラベル印字履歴キー
        historyPrintout.setHistory_printout_key(UUID.randomUUID().toString());
        // 要求イベントID
        historyPrintout.setEvent_id(tmpQueuePrintout.getEvent_id());
        // 要求内連番
        historyPrintout.setRequest_seq(tmpQueuePrintout.getRequest_seq());
        // 発生日時
        historyPrintout.setOccur_date(date);
        // ReportID
        if (StringUtil.isNullOrEmpty(reportId) || ERROR_REPORT_ID.equals(reportId)) {
            historyPrintout.setReport_id(UUID.randomUUID().toString());
        } else {
            historyPrintout.setReport_id(reportId);
        }
        // 再印字フラグ
        historyPrintout.setReprint_flag(ReprintFlag.OFF.getValue());
        // 再印字回数
        historyPrintout.setPrintout_num(0);
        // 端末ID
        historyPrintout.setComputer_id(tmpQueuePrintout.getComputer_id());
        // 発生コンピュータ名
        historyPrintout.setComputer_name(tmpQueuePrintout.getComputer_name());
        // 帳票様式番号
        historyPrintout.setPrint_command_no(tmpQueuePrintout.getPrint_command_no());
        // 帳票印字日時
        historyPrintout.setPrintout_date(tmpQueuePrintout.getPrintout_date());
        // 帳票種類
        historyPrintout.setPrinter_type(tmpQueuePrintout.getPrinter_type());
        // 印字枚数
        historyPrintout.setPrintout_copies(tmpQueuePrintout.getPrint_times());
        // 帳票名
        historyPrintout.setReport_name(tmpQueuePrintout.getReport_name());
        // 領域区分
        historyPrintout.setDb_area_div(tmpQueuePrintout.getDb_area_div());
        // プリンタID
        historyPrintout.setPrinter_id(tmpQueuePrintout.getPrinter_id());
        // プリンタ名
        historyPrintout.setPrinter_name(tmpQueuePrintout.getPrinter_name());
        // ユーザID
        historyPrintout.setUser_id(tmpQueuePrintout.getUser_id());
        // ユーザ名
        historyPrintout.setUser_name(tmpQueuePrintout.getUser_name());
        // 画面表示用Key情報
        historyPrintout.setDisplay_key_text(tmpQueuePrintout.getDisplay_key_text());
        // 印字理由コード
        historyPrintout.setPrint_reason_code(tmpQueuePrintout.getPrint_reason_code());
        // 印字理由名
        historyPrintout.setPrint_reason_name(tmpQueuePrintout.getPrint_reason_name());
        // 印字理由コメント
        historyPrintout.setPrint_reason_comment(tmpQueuePrintout.getPrint_reason_comment());
        // 再印刷マーク出力有無
        historyPrintout.setIs_re_print_mark(tmpQueuePrintout.getIs_re_print_mark());
        // 再印刷マーク出力位置
        historyPrintout.setPosition_re_print_mark(tmpQueuePrintout.getPosition_re_print_mark());
        // 電子ファイル作成区分
        historyPrintout.setIs_file_create_type(tmpQueuePrintout.getIs_file_create_type());
        // PDF電子ファイル名
        historyPrintout.setPdf_file_name(tmpQueuePrintout.getPdf_file_name());
        // EXCEL電子ファイル名
        historyPrintout.setExcel_file_name(null);

        if (StringUtil.isNullOrEmpty(reportId) || ERROR_REPORT_ID.equals(reportId)) {
            // 帳票印字結果状態
            historyPrintout.setPrintout_status(PrintoutStatus.ERROR.getValue());
            // 印字エラーメッセージ
            historyPrintout.setPrintout_error_message(super.editPrintoutErrorMessage(message));
        } else {
            historyPrintout.setPrintout_status(PrintoutStatus.PROCESSING.getValue());
        }
        // 再印字ソース要求イベントID
        historyPrintout.setReprint_source_event_id(tmpQueuePrintout.getEvent_id());
        // 再印字ソース要求内連番
        historyPrintout.setReprint_source_request_seq(tmpQueuePrintout.getRequest_seq());

        // 帳票印字様式マスターの帳票印字機能区分
        historyPrintout.setPrintFunctionDiv(tmpQueuePrintout.getPrintFunctionDiv());
        // 帳票印字様式マスターの帳票定義単位区分
        historyPrintout.setPrintDefineScopeDiv(tmpQueuePrintout.getPrintDefineScopeDiv());
        // 帳票印字様式マスターの指図工程コード
        historyPrintout.setProcessCode(tmpQueuePrintout.getProcessCode());
        // 帳票印字様式マスターの試験目的区分
        historyPrintout.setInspectionPurposeDiv(tmpQueuePrintout.getInspectionPurposeDiv());
        // 帳票印字様式マスターの品目タイプコード
        historyPrintout.setItemTypeCode(tmpQueuePrintout.getItemCode());
        // 帳票印字様式マスターの品目コード
        historyPrintout.setItemCode(tmpQueuePrintout.getItemCode());
        // 帳票印字様式マスターのレシピID
        historyPrintout.setRecipeID(tmpQueuePrintout.getRecipeID());
        // 帳票印字様式マスターのレシピVER
        historyPrintout.setRecipeVersion(tmpQueuePrintout.getRecipeVersion());
        // 帳票印字様式マスターのレシピREV
        historyPrintout.setRecipeRevision(tmpQueuePrintout.getRecipeRevision());

        return historyPrintout;

    }
    /**
    *
    * 印刷要求の実施を行う
    *
    * @param tmpQueuePrintout  印字キューのエンティティ1件分
    * @param printerNo         リクエストにあったプリンタ番号
    * @param phvalue           印字キー情報
    * @param em                エンティティマネージャー
    * @param localeId          ロケールＩＤ
    * @return
    * @throws GnomesAppException
    */
    private boolean printRequestMain(TmpQueuePrintout tmpQueuePrintout, HashMap<String, String> phvalue,
            EntityManager em, String localeId) throws GnomesAppException {

        final String methodName = "printRequestMain";

        //プリンタのIDを一時取得
        String printerNo = tmpQueuePrintout.getPrinter_id();

        // 印字依頼処理

        String reportId = null;

        // CReportGen初期化
        CReportGen cReportGen = super.initCReportGen(tmpQueuePrintout.getDb_area_div());

        try {

            // コールバック関数の登録
            PrintOutCallbackInfo callbackInfo = super.setCallbackInfo(tmpQueuePrintout.getPdf_file_name(),
                    PrintOutCallbackInfo.PRINT, null, tmpQueuePrintout.getPrint_times(), 1,
                    tmpQueuePrintout.getEvent_id(), tmpQueuePrintout.getRequest_seq());

            // コールバック関数
            K01CPrintOutCallbackProcess callbackProc = new K01CPrintOutCallbackProcess(callbackInfo, cReportGen);

            // プリンターから印字するかしないか
            // 電子ファイル作成区分 1：印刷、2：印刷と電子保存、3：電子保存
            // 3：電子保存のみ物理プリンタから印刷しない
            int isfileCreateType = tmpQueuePrintout.getIs_file_create_type();
            boolean printOutputFlg = true;
            if (isfileCreateType == IS_FILE_CREATE_TYPE){
                printOutputFlg = false;
            }

            // ロケール切替
            String printCommandNumber = tmpQueuePrintout.getPrint_command_no();
            if (tmpQueuePrintout.getPrint_command_no().contains(CommonConstants.JOIN_LOCALE)) {
                String selectLocale[] = localeId.split(CommonConstants.SPLIT_CHAR);
                printCommandNumber = tmpQueuePrintout.getPrint_command_no().replace(CommonConstants.REPLACE_LOCALE, selectLocale[0]);
            }

            //----------------------------------------------------------------
            // 印字依頼
            //----------------------------------------------------------------
            reportId = cReportGen.Print(printCommandNumber, // 帳票様式番号
                    phvalue, // 印字キー情報
                    printerNo, // プリンタID
                    1, // 1:PDF
                    callbackProc, // コールバック関数
                    tmpQueuePrintout.getPrint_times(), // 印刷部数
                    printOutputFlg // プリンターから印字するかしないか
            );

            // 正常として印字履歴登録
            this.InsertPrintHistory(super.getMessage(reportId), tmpQueuePrintout, reportId, em, phvalue);

        } catch (Exception e) {

            this.logHelper.severe(logger, className, methodName, e.getMessage(), e);

            Object[] params = new Object[10];
            params[0] = e.getMessage();

            // 履歴に残す任意のメッセージを作成
            String message = MessagesHandler.getExceptionMessage(super.req,
                    super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0001, params));

            // エラーとして印字履歴記録
            if (StringUtil.isNullOrEmpty(reportId)){
                reportId = EMPTY_STRING;
            }
            this.InsertPrintHistory(message, tmpQueuePrintout, reportId, em, phvalue);
            this.InsertMessageForReport(message, tmpQueuePrintout, reportId, phvalue);
        }
        return true;
    }

    /**
    *
    * 印刷要求の実施を行う（多重）
    *
    * @param tmpQueuePrintout  印字キューのエンティティ1件分
    * @param printerNo         リクエストにあったプリンタ番号
    * @param phvalue           印字キー情報
    * @param em                エンティティマネージャー
    * @param localeId          ロケールＩＤ
    * @return
    * @throws GnomesAppException
    */
    private boolean printRequestMainMultiple(TmpQueuePrintout tmpQueuePrintout, HashMap<String, String> phvalue,
            EntityManager em, String localeId) throws GnomesAppException {
        final String methodName = "printRequestMainMultiple";

        //プリンタのIDを一時取得
        String printerNo = tmpQueuePrintout.getPrinter_id();

        // 印字依頼処理

        String reportId = null;

        // CReportGen（多重）初期化
        biz.grandsight.ex.rs_multiple.CReportGen cReportGenMultiple = super.initCReportGenMultiple(tmpQueuePrintout.getDb_area_div());

        try {

            // コールバック関数の登録
            PrintOutCallbackInfo callbackInfo = super.setCallbackInfo(tmpQueuePrintout.getPdf_file_name(),
                    PrintOutCallbackInfo.PRINT, null, tmpQueuePrintout.getPrint_times(), 1,
                    tmpQueuePrintout.getEvent_id(), tmpQueuePrintout.getRequest_seq());

            // コールバック関数
            K01DPrintOutCallbackProcessMultiple callbackProc = new K01DPrintOutCallbackProcessMultiple(callbackInfo,
                    cReportGenMultiple);

            // プリンターから印字するかしないか
            // 電子ファイル作成区分 1：印刷、2：印刷と電子保存、3：電子保存
            // 3：電子保存のみ物理プリンタから印刷しない
            int isfileCreateType = tmpQueuePrintout.getIs_file_create_type();
            boolean printOutputFlg = true;
            if (isfileCreateType == IS_FILE_CREATE_TYPE){
                printOutputFlg = false;
            }

            // ロケール切替
            String printCommandNumber = tmpQueuePrintout.getPrint_command_no();
            if (tmpQueuePrintout.getPrint_command_no().contains(CommonConstants.JOIN_LOCALE)) {
                String selectLocale[] = localeId.split(CommonConstants.SPLIT_CHAR);
                printCommandNumber = tmpQueuePrintout.getPrint_command_no().replace(CommonConstants.REPLACE_LOCALE, selectLocale[0]);
            }

            //----------------------------------------------------------------
            // 印字依頼
            //----------------------------------------------------------------
            reportId = cReportGenMultiple.Print(printCommandNumber, // 帳票様式番号
                    phvalue, // 印字キー情報
                    printerNo, // プリンタID
                    1, // 1:PDF
                    callbackProc, // コールバック関数
                    tmpQueuePrintout.getPrint_times(), // 印刷部数
                    printOutputFlg // プリンターから印字するかしないか
            );

            // 正常として印字履歴登録
            this.InsertPrintHistory(super.getMessage(reportId), tmpQueuePrintout, reportId, em, phvalue);

        } catch (biz.grandsight.ex.rs_multiple.CReportGenException e) {

            this.logHelper.severe(logger, className, methodName, e.getMessage(), e);

            Object[] params = new Object[10];
            params[0] = e.getMessage();

            // 履歴に残す任意のメッセージを作成
            String message = MessagesHandler.getExceptionMessage(super.req,
                    super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0001, params));

            // エラーとして印字履歴記録
            if (StringUtil.isNullOrEmpty(reportId)){
                reportId = EMPTY_STRING;
            }
            this.InsertPrintHistory(message, tmpQueuePrintout, reportId, em, phvalue);
            this.InsertMessageForReport(message, tmpQueuePrintout, reportId, phvalue);

        }
        return true;
    }

    /**
    *
    * 印刷要求の実施を行う（多重棚卸一覧）
    *
    * @param tmpQueuePrintout  印字キューのエンティティ1件分
    * @param printerNo         リクエストにあったプリンタ番号
    * @param phvalue           印字キー情報
    * @param em                エンティティマネージャー
    * @param localeId          ロケールＩＤ
    * @return
    * @throws GnomesAppException
    */
    private boolean printRequestMainMultipleInventory(TmpQueuePrintout tmpQueuePrintout,
            HashMap<String, String> phvalue, EntityManager em, String localeId) throws GnomesAppException {

        final String methodName = "printRequestMainMultipleInventory";

        //プリンタのIDを一時取得
        String printerNo = tmpQueuePrintout.getPrinter_id();

        // 印字依頼処理

        String reportId = null;

        // CReportGen（多重棚卸一覧）初期化
        biz.grandsight.ex.rs_multiple21.CReportGen cReportGenMultipleInventoryList = super.initCReportGenMultipleInventoryList(tmpQueuePrintout.getDb_area_div());

        try {

            // コールバック関数の登録
            PrintOutCallbackInfo callbackInfo = super.setCallbackInfo(tmpQueuePrintout.getPdf_file_name(),
                    PrintOutCallbackInfo.PRINT, null, tmpQueuePrintout.getPrint_times(), 1,
                    tmpQueuePrintout.getEvent_id(), tmpQueuePrintout.getRequest_seq());

            // コールバック関数
            K01HPrintOutCallbackProcessMultipleInventory callbackProc = new K01HPrintOutCallbackProcessMultipleInventory(
                    callbackInfo, cReportGenMultipleInventoryList);

            // プリンターから印字するかしないか
            // 電子ファイル作成区分 1：印刷、2：印刷と電子保存、3：電子保存
            // 3：電子保存のみ物理プリンタから印刷しない
            int isfileCreateType = tmpQueuePrintout.getIs_file_create_type();
            boolean printOutputFlg = true;
            if (isfileCreateType == IS_FILE_CREATE_TYPE){
                printOutputFlg = false;
            }

            // ロケール切替
            String printCommandNumber = tmpQueuePrintout.getPrint_command_no();
            if (tmpQueuePrintout.getPrint_command_no().contains(CommonConstants.JOIN_LOCALE)) {
                String selectLocale[] = localeId.split(CommonConstants.SPLIT_CHAR);
                printCommandNumber = tmpQueuePrintout.getPrint_command_no().replace(CommonConstants.REPLACE_LOCALE, selectLocale[0]);
            }

            //----------------------------------------------------------------
            // 印字依頼
            //----------------------------------------------------------------
            reportId = cReportGenMultipleInventoryList.Print(printCommandNumber, // 帳票様式番号
                    phvalue, // 印字キー情報
                    printerNo, // プリンタID
                    1, // 1:PDF
                    callbackProc, // コールバック関数
                    tmpQueuePrintout.getPrint_times(), // 印刷部数
                    printOutputFlg // プリンターから印字するかしないか
            );

            // 正常として印字履歴登録
            this.InsertPrintHistory(super.getMessage(reportId), tmpQueuePrintout, reportId, em, phvalue);

        } catch (biz.grandsight.ex.rs_multiple21.CReportGenException e) {

            this.logHelper.severe(logger, className, methodName, e.getMessage(), e);

            Object[] params = new Object[10];
            params[0] = e.getMessage();

            // 履歴に残す任意のメッセージを作成
            String message = MessagesHandler.getExceptionMessage(super.req,
                    super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0001, params));

            // エラーとして印字履歴記録
            if (StringUtil.isNullOrEmpty(reportId)){
                reportId = EMPTY_STRING;
            }
            this.InsertPrintHistory(message, tmpQueuePrintout, reportId, em, phvalue);
            this.InsertMessageForReport(message, tmpQueuePrintout, reportId, phvalue);

        }
        return true;
    }

    /**
    *
    * 印刷要求の実施を行う（多段）
    *
    * @param tmpQueuePrintout  印字キューのエンティティ1件分
    * @param printerNo         リクエストにあったプリンタ番号
    * @param phvalue           印字キー情報
    * @param em                エンティティマネージャー
    * @param localeId          ロケールＩＤ
    * @return
    * @throws GnomesAppException
    */
    private boolean printRequestMainMultiStage(TmpQueuePrintout tmpQueuePrintout, HashMap<String, String> phvalue,
            EntityManager em, String localeId) throws GnomesAppException {
        final String methodName = "printRequestMainMultiStage";

        //プリンタのIDを一時取得
        String printerNo = tmpQueuePrintout.getPrinter_id();

        // 印字依頼処理

        String reportId = null;

        // CReportGen（多段）初期化
        biz.grandsight.ex.rs_multistage.CReportGen cReportGenMultiStage = super.initCReportGenMultiStage(tmpQueuePrintout.getDb_area_div());

        try {

            // コールバック関数の登録
            PrintOutCallbackInfo callbackInfo = super.setCallbackInfo(tmpQueuePrintout.getPdf_file_name(),
                    PrintOutCallbackInfo.PRINT, null, tmpQueuePrintout.getPrint_times(), 1,
                    tmpQueuePrintout.getEvent_id(), tmpQueuePrintout.getRequest_seq());

            // コールバック関数
            K01EPrintOutCallbackProcessMultiStage callbackProc = new K01EPrintOutCallbackProcessMultiStage(callbackInfo,
                    cReportGenMultiStage);

            // プリンターから印字するかしないか
            // 電子ファイル作成区分 1：印刷、2：印刷と電子保存、3：電子保存
            // 3：電子保存のみ物理プリンタから印刷しない
            int isfileCreateType = tmpQueuePrintout.getIs_file_create_type();
            boolean printOutputFlg = true;
            if (isfileCreateType == IS_FILE_CREATE_TYPE){
                printOutputFlg = false;
            }

            // ロケール切替
            String printCommandNumber = tmpQueuePrintout.getPrint_command_no();
            if (tmpQueuePrintout.getPrint_command_no().contains(CommonConstants.JOIN_LOCALE)) {
                String selectLocale[] = localeId.split(CommonConstants.SPLIT_CHAR);
                printCommandNumber = tmpQueuePrintout.getPrint_command_no().replace(CommonConstants.REPLACE_LOCALE, selectLocale[0]);
            }

            //----------------------------------------------------------------
            // 印字依頼
            //----------------------------------------------------------------
            reportId = cReportGenMultiStage.Print(printCommandNumber, // 帳票様式番号
                    phvalue, // 印字キー情報
                    printerNo, // プリンタID
                    1, // 1:PDF
                    callbackProc, // コールバック関数
                    tmpQueuePrintout.getPrint_times(), // 印刷部数
                    printOutputFlg // プリンターから印字するかしないか
            );

            // 正常として印字履歴登録
            this.InsertPrintHistory(super.getMessage(reportId), tmpQueuePrintout, reportId, em, phvalue);

        } catch (biz.grandsight.ex.rs_multistage.CReportGenException e) {

            this.logHelper.severe(logger, className, methodName, e.getMessage(), e);

            Object[] params = new Object[10];
            params[0] = e.getMessage();

            // 履歴に残す任意のメッセージを作成
            String message = MessagesHandler.getExceptionMessage(super.req,
                    super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0001, params));

            // エラーとして印字履歴記録
            if (StringUtil.isNullOrEmpty(reportId)){
                reportId = EMPTY_STRING;
            }
            this.InsertPrintHistory(message, tmpQueuePrintout, reportId, em, phvalue);
            this.InsertMessageForReport(message, tmpQueuePrintout, reportId, phvalue);
        }
        return true;
    }

    /**
    *
    * 印刷要求の実施を行う（多段キー変更改ページ無し）
    *
    * @param tmpQueuePrintout  印字キューのエンティティ1件分
    * @param printerNo         リクエストにあったプリンタ番号
    * @param phvalue           印字キー情報
    * @param localeId          ロケールＩＤ
    * @param em                エンティティマネージャー
    * @return
    * @throws GnomesAppException
    */
    private boolean printRequestMainMultiStageNoNewPage(TmpQueuePrintout tmpQueuePrintout,
            HashMap<String, String> phvalue, EntityManager em, String localeId) throws GnomesAppException {

        final String methodName = "printRequestMainMultiStageNoNewPage";

        //プリンタのIDを一時取得
        String printerNo = tmpQueuePrintout.getPrinter_id();

        // 印字依頼処理

        String reportId = null;

        // CReportGen（多段キー変更改ページ無し）初期化
        biz.grandsight.ex.rs_multistage41.CReportGen cReportGenMultiStageNoNewPage = super.initCReportGenMultiStageNoNewPage(tmpQueuePrintout.getDb_area_div());

        try {

            // コールバック関数の登録
            PrintOutCallbackInfo callbackInfo = super.setCallbackInfo(tmpQueuePrintout.getPdf_file_name(),
                    PrintOutCallbackInfo.PRINT, null, tmpQueuePrintout.getPrint_times(), 1,
                    tmpQueuePrintout.getEvent_id(), tmpQueuePrintout.getRequest_seq());

            // コールバック関数
            K01FPrintOutCallbackProcessMultiStageNoNewPage callbackProc = new K01FPrintOutCallbackProcessMultiStageNoNewPage(
                    callbackInfo, cReportGenMultiStageNoNewPage);

            // プリンターから印字するかしないか
            // 電子ファイル作成区分 1：印刷、2：印刷と電子保存、3：電子保存
            // 3：電子保存のみ物理プリンタから印刷しない
            int isfileCreateType = tmpQueuePrintout.getIs_file_create_type();
            boolean printOutputFlg = true;
            if (isfileCreateType == IS_FILE_CREATE_TYPE){
                printOutputFlg = false;
            }

            // ロケール切替
            String printCommandNumber = tmpQueuePrintout.getPrint_command_no();
            if (tmpQueuePrintout.getPrint_command_no().contains(CommonConstants.JOIN_LOCALE)) {
                String selectLocale[] = localeId.split(CommonConstants.SPLIT_CHAR);
                printCommandNumber = tmpQueuePrintout.getPrint_command_no().replace(CommonConstants.REPLACE_LOCALE, selectLocale[0]);
            }

            //----------------------------------------------------------------
            // 印字依頼
            //----------------------------------------------------------------
            reportId = cReportGenMultiStageNoNewPage.Print(printCommandNumber, // 帳票様式番号
                    phvalue, // 印字キー情報
                    printerNo, // プリンタID
                    1, // 1:PDF
                    callbackProc, // コールバック関数
                    tmpQueuePrintout.getPrint_times(), // 印刷部数
                    printOutputFlg // プリンターから印字するかしないか
            );

            // 正常として印字履歴登録
            this.InsertPrintHistory(super.getMessage(reportId), tmpQueuePrintout, reportId, em, phvalue);

        } catch (biz.grandsight.ex.rs_multistage41.CReportGenException e) {

            this.logHelper.severe(logger, className, methodName, e.getMessage(), e);

            Object[] params = new Object[10];
            params[0] = e.getMessage();

            // 履歴に残す任意のメッセージを作成
            String message = MessagesHandler.getExceptionMessage(super.req,
                    super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0001, params));

            // エラーとして印字履歴記録
            if (StringUtil.isNullOrEmpty(reportId)){
                reportId = EMPTY_STRING;
            }
            this.InsertPrintHistory(message, tmpQueuePrintout, reportId, em, phvalue);
            this.InsertMessageForReport(message, tmpQueuePrintout, reportId, phvalue);
        }
        return true;
    }

    /**
    *
    * 印刷要求の実施を行う（多重多段）
    *
    * @param tmpQueuePrintout  印字キューのエンティティ1件分
    * @param printerNo         リクエストにあったプリンタ番号
    * @param phvalue           印字キー情報
    * @param em                エンティティマネージャー
    * @param localeId          ロケールＩＤ
    * @return
    * @throws GnomesAppException
    */
    private boolean printRequestMainMultipleMultiStage(TmpQueuePrintout tmpQueuePrintout,
            HashMap<String, String> phvalue, EntityManager em, String localeId) throws GnomesAppException {
        final String methodName = "printRequestMainMultipleMultiStage";

        //プリンタのIDを一時取得
        String printerNo = tmpQueuePrintout.getPrinter_id();

        // 印字依頼処理

        String reportId = null;

        // CReportGen（多重多段）初期化
        biz.grandsight.ex.rs_multiplemultistage.CReportGen cReportGenMultipleMultiStage = super.initCReportGenMultipleMultiStage(tmpQueuePrintout.getDb_area_div());

        try {

            // コールバック関数の登録
            PrintOutCallbackInfo callbackInfo = super.setCallbackInfo(tmpQueuePrintout.getPdf_file_name(),
                    PrintOutCallbackInfo.PRINT, null, tmpQueuePrintout.getPrint_times(), 1,
                    tmpQueuePrintout.getEvent_id(), tmpQueuePrintout.getRequest_seq());

            // コールバック関数
            K01GPrintOutCallbackProcessMultipleMultiStage callbackProc = new K01GPrintOutCallbackProcessMultipleMultiStage(
                    callbackInfo, cReportGenMultipleMultiStage);

            // プリンターから印字するかしないか
            // 電子ファイル作成区分 1：印刷、2：印刷と電子保存、3：電子保存
            // 3：電子保存のみ物理プリンタから印刷しない
            int isfileCreateType = tmpQueuePrintout.getIs_file_create_type();
            boolean printOutputFlg = true;
            if (isfileCreateType == IS_FILE_CREATE_TYPE){
                printOutputFlg = false;
            }

            // ロケール切替
            String printCommandNumber = tmpQueuePrintout.getPrint_command_no();
            if (tmpQueuePrintout.getPrint_command_no().contains(CommonConstants.JOIN_LOCALE)) {
                String selectLocale[] = localeId.split(CommonConstants.SPLIT_CHAR);
                printCommandNumber = tmpQueuePrintout.getPrint_command_no().replace(CommonConstants.REPLACE_LOCALE, selectLocale[0]);
            }

            //----------------------------------------------------------------
            // 印字依頼
            //----------------------------------------------------------------
            reportId = cReportGenMultipleMultiStage.Print(printCommandNumber, // 帳票様式番号
                    phvalue, // 印字キー情報
                    printerNo, // プリンタID
                    1, // 1:PDF
                    callbackProc, // コールバック関数
                    tmpQueuePrintout.getPrint_times(), // 印刷部数
                    printOutputFlg // プリンターから印字するかしないか
            );

            // 正常として印字履歴登録
            this.InsertPrintHistory(super.getMessage(reportId), tmpQueuePrintout, reportId, em, phvalue);

        } catch (biz.grandsight.ex.rs_multiplemultistage.CReportGenException e) {

            this.logHelper.severe(logger, className, methodName, e.getMessage(), e);

            Object[] params = new Object[10];
            params[0] = e.getMessage();

            // 履歴に残す任意のメッセージを作成
            String message = MessagesHandler.getExceptionMessage(super.req,
                    super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0001, params));

            // エラーとして印字履歴記録
            if (StringUtil.isNullOrEmpty(reportId)){
                reportId = EMPTY_STRING;
            }
            this.InsertPrintHistory(message, tmpQueuePrintout, reportId, em, phvalue);
            this.InsertMessageForReport(message, tmpQueuePrintout, reportId, phvalue);

        }
        return true;
    }

}
