package com.gnomes.system.logic;

import java.io.File;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.Objects;
import java.util.UUID;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonEnums;
import com.gnomes.common.constants.CommonEnums.PrintoutStatus;
import com.gnomes.common.constants.CommonEnums.ReprintFlag;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.GnomesTransactional;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.persistence.GnomesEntityManager;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.CurrentTimeStamp;
import com.gnomes.common.util.FileUtils;
import com.gnomes.system.dao.HistoryPrintoutFileDao;
import com.gnomes.system.data.printout.PrintOutCallbackInfo;
import com.gnomes.system.entity.HistoryPrintout;
import com.gnomes.system.entity.HistoryPrintoutFile;

import biz.grandsight.ex.rs.CReportGen;
import biz.grandsight.ex.rs.CReportGenException;

/**
 * 帳票再印刷処理
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/03/19 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class K01BReprint extends PrintOutLogic {

    /** Excelファイル拡張子 */
    private static final String EXCEL_EXTENSION = ".xlsx";

    /** 電子ファイル作成区分（電子保存） */
    private static final int IS_FILE_CREATE_TYPE = 3;

    /** 帳票ラベル印字履歴ファイル Dao */
    @Inject
    protected HistoryPrintoutFileDao historyPrintoutFileDao;

    @Inject
    protected GnomesEntityManager em;

    /**
     * 帳票再印刷依頼（再印字時必ずプリンター出力）.
     * @param reportId ReportId
     * @param printReasonCode 印字理由コード
     * @param printReasonName 印字理由名
     * @param printReasonComment 印字理由コメント
     * @param printoutCopies 印字枚数
     * @param eventId nk要求イベントID
     * @param requestSeq nk要求内連番
     *
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    @GnomesTransactional
    public void print(String reportId,
            String printReasonCode, String printReasonName, String printReasonComment,
            int printoutCopies, String eventId, int requestSeq) throws GnomesAppException {

        boolean successFlag = false;
        // 再印字時プリンター出力フラグ
        boolean printerOutputFlag = true;

        try {
            // 処理成功フラグ
            successFlag = rePrint(reportId,
                    printReasonCode, printReasonName, printReasonComment,
                    printoutCopies, eventId, requestSeq, printerOutputFlag);
        } catch (Exception e) {
            try {
                throw e;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } finally {

        }
    }

    /**
     * 帳票再印刷依頼（再印字時プリンター出力を再印字時プリンター出力フラグで制御）.
     * @param reportId ReportId
     * @param printReasonCode 印字理由コード
     * @param printReasonName 印字理由名
     * @param printReasonComment 印字理由コメント
     * @param printoutCopies 印字枚数
     * @param eventId nk要求イベントID
     * @param requestSeq nk要求内連番
     * @param isFileCreateType 電子ファイル作成区分
     *
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    @GnomesTransactional
    public void print(String reportId,
            String printReasonCode, String printReasonName, String printReasonComment,
            int printoutCopies, String eventId, int requestSeq, int isFileCreateType) throws GnomesAppException {

        boolean successFlag = false;
        boolean printerOutputFlag = true;

        // プリンター出力制御
        // 電子ファイル作成区分 1：印刷、2：印刷と電子保存、3：電子保存
        // 3：電子保存のみ物理プリンタから印刷しない
        if (isFileCreateType == IS_FILE_CREATE_TYPE) {
            printerOutputFlag = false;
        }
        try {
            // 処理成功フラグ
            successFlag = rePrint(reportId,
                    printReasonCode, printReasonName, printReasonComment,
                    printoutCopies, eventId, requestSeq, printerOutputFlag);
        } catch (Exception e) {
            try {
                throw e;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } finally {

        }
    }

     /**
     * 帳票再印刷.
     * @param reportId ReportId
     * @param printReasonCode 印字理由コード
     * @param printReasonName 印字理由名
     * @param printReasonComment 印字理由コメント
     * @param printoutCopies 印字枚数
     * @param eventId nk要求イベントID
     * @param requestSeq nk要求内連番
     * @param printerOutputFlag 再印字時プリンター出力フラグ
     *
     * @throws GnomesAppException
     */
    protected boolean rePrint(String reportId,
            String printReasonCode, String printReasonName, String printReasonComment,
            int printoutCopies, String eventId, int requestSeq, boolean printerOutputFlag) throws GnomesAppException {

        // 処理成功フラグ
        boolean successFlag = true;
        // 再印字対象電子ファイル名
        String fileName = null;

        try {
            // 印字ラベル印字履歴情報取得
            HistoryPrintout historyPrintout = this.historyPrintoutDao.getConditionReportId(reportId, em.getEntityManager());
            // データが存在しない場合
            if (Objects.isNull(historyPrintout)) {
                // 再印字対象の帳票ラベル印字履歴が取得できません。 (ReportID: {0})
                throw super.exceptionFactory.createGnomesAppException(
                        null, GnomesMessagesConstants.ME01_0166, reportId);

            }
            //  帳票印字設定情報チェック
            if (Objects.isNull(super.gnomesSystemBean.getcGenReportMeta())) {
                // 帳票印字設定情報が Web.xml に正しく設定されていません。
                throw super.exceptionFactory.createGnomesAppException(
                        null, GnomesMessagesConstants.ME01_0161);

            }

            // 帳票ラベル印字履歴情報取得
            HistoryPrintoutFile historyPrintoutFile =
                    this.historyPrintoutFileDao.getHistoryPrintoutFile(
                            historyPrintout.getReprint_source_event_id(),
                            historyPrintout.getReprint_source_request_seq(), em.getEntityManager());

            if (Objects.isNull(historyPrintoutFile)) {
                // 再印字対象の帳票ラベル印字履歴ファイルが取得できません。 (ReportID: {0}、帳票ラベル印字履歴キー：{1})
                throw super.exceptionFactory.createGnomesAppException(
                        null, GnomesMessagesConstants.ME01_0187,
                        new Object[]{reportId, historyPrintout.getHistory_printout_key()});
            }

            // 再印字対象電子ファイル名
            fileName = UUID.randomUUID().toString() + EXCEL_EXTENSION;

            try {
                // 再印字対象電子ファイル作成
                this.createRePrintExcelFile(fileName, historyPrintoutFile.getExcel_report_file());

            } catch (GnomesAppException e) {

                String errorInfo = MessagesHandler.getExceptionMessage(super.req, e);

                // 再印字対象のファイル作成に失敗しました。 (エラー内容: {0}）
                throw super.exceptionFactory.createGnomesAppException(
                        null, GnomesMessagesConstants.ME01_0188, errorInfo);

            }

            // 印字依頼処理
            // CReportGen初期化
            CReportGen cReportGen = super.initCReportGen(historyPrintout.getDb_area_div());
            // CReportGen（多重）初期化
            biz.grandsight.ex.rs_multiple.CReportGen cReportGenMultiple = super.initCReportGenMultiple(historyPrintout.getDb_area_div());
            // CReportGen（多重棚卸一覧）初期化
            biz.grandsight.ex.rs_multiple21.CReportGen cReportGenMultipleInventoryList = super.initCReportGenMultipleInventoryList(historyPrintout.getDb_area_div());
            // CReportGen（多段）初期化
            biz.grandsight.ex.rs_multistage.CReportGen cReportGenMultiStage = super.initCReportGenMultiStage(historyPrintout.getDb_area_div());
            // CReportGen（多段キー変更改ページ無し）初期化
            biz.grandsight.ex.rs_multistage41.CReportGen cReportGenMultiStageNoNewPage = super.initCReportGenMultiStageNoNewPage(historyPrintout.getDb_area_div());
            // CReportGen（多重多段）初期化
            biz.grandsight.ex.rs_multiplemultistage.CReportGen cReportGenMultipleMultiStage = super.initCReportGenMultipleMultiStage(historyPrintout.getDb_area_div());

            // 再印刷画像位置取得
            String pos = historyPrintout.getPosition_re_print_mark();
            if (StringUtils.isEmpty(pos)){
                pos = "";
            }

            try {

                PrintOutCallbackInfo callbackInfo = super.setCallbackInfo(
                        historyPrintout.getPdf_file_name(),
                        PrintOutCallbackInfo.REPRINT,
                        fileName,
                        printoutCopies,
                        1,
                        eventId,
                        requestSeq);

                // コールバック関数
                K01CPrintOutCallbackProcess callbackProc =
                        new K01CPrintOutCallbackProcess(callbackInfo, cReportGen);

                String reReportId = null;

                int reportType = historyPrintout.getPrinter_type();
                // 再印字依頼
                // 帳票印字（ラベル）
                if (reportType == CommonEnums.PrintType.Label.getValue()) {
                    reReportId = cReportGen.Reprint(
                            fileName,                                 // 帳票様式番号(再印字対象電子ファイル名)
                            historyPrintout.getPrinter_id(),          // プリンタID
                            1,                                        // 1:PDF
                            pos,                                      // 再印刷マーク出力位置
                            callbackProc,                             // コールバック関数
                            printoutCopies,                           // 印刷部数
                            printerOutputFlag                         // 再印字時プリンター出力フラグ
                            );
                // 帳票印字（一覧）
                } else if (reportType == CommonEnums.PrintType.List.getValue()) {
                    reReportId = cReportGen.Reprint(
                            fileName,                                 // 帳票様式番号(再印字対象電子ファイル名)
                            historyPrintout.getPrinter_id(),          // プリンタID
                            1,                                        // 1:PDF
                            pos,                                      // 再印刷マーク出力位置
                            callbackProc,                             // コールバック関数
                            printoutCopies,                           // 印刷部数
                            printerOutputFlag                         // 再印字時プリンター出力フラグ
                            );
                // 帳票印字（多重）
                } else if (reportType == CommonEnums.PrintType.Multiple.getValue()) {
                    // コールバック関数
                    K01DPrintOutCallbackProcessMultiple callbackProcMultiple =
                            new K01DPrintOutCallbackProcessMultiple(callbackInfo, cReportGenMultiple);
                    reReportId = cReportGenMultiple.Reprint(
                            fileName,                                 // 帳票様式番号(再印字対象電子ファイル名)
                            historyPrintout.getPrinter_id(),          // プリンタID
                            1,                                        // 1:PDF
                            pos,                                      // 再印刷マーク出力位置
                            callbackProcMultiple,                     // コールバック関数
                            printoutCopies,                           // 印刷部数
                            printerOutputFlag                         // 再印字時プリンター出力フラグ
                            );
                // 帳票印字（多段）
                } else if (reportType == CommonEnums.PrintType.MultiStage.getValue()) {
                    // コールバック関数
                    K01EPrintOutCallbackProcessMultiStage callbackProcMultiStage =
                            new K01EPrintOutCallbackProcessMultiStage(callbackInfo, cReportGenMultiStage);
                    reReportId = cReportGenMultiStage.Reprint(
                            fileName,                                 // 帳票様式番号(再印字対象電子ファイル名)
                            historyPrintout.getPrinter_id(),          // プリンタID
                            1,                                        // 1:PDF
                            pos,                                      // 再印刷マーク出力位置
                            callbackProcMultiStage,                   // コールバック関数
                            printoutCopies,                           // 印刷部数
                            printerOutputFlag                         // 再印字時プリンター出力フラグ
                            );
                // 帳票印字（多段キー変更改ページ無し）
                } else if (reportType == CommonEnums.PrintType.MultiStageNoNewPage.getValue()) {
                    // コールバック関数
                    K01FPrintOutCallbackProcessMultiStageNoNewPage callbackProcMultiStageNoNewPage =
                            new K01FPrintOutCallbackProcessMultiStageNoNewPage(callbackInfo, cReportGenMultiStageNoNewPage);
                    reReportId = cReportGenMultiStageNoNewPage.Reprint(
                            fileName,                                 // 帳票様式番号(再印字対象電子ファイル名)
                            historyPrintout.getPrinter_id(),          // プリンタID
                            1,                                        // 1:PDF
                            pos,                                      // 再印刷マーク出力位置
                            callbackProcMultiStageNoNewPage,          // コールバック関数
                            printoutCopies,                           // 印刷部数
                            printerOutputFlag                         // 再印字時プリンター出力フラグ
                            );
                // 帳票印字（多重多段）
                } else if (reportType == CommonEnums.PrintType.MultipleMultiStage.getValue()) {
                    // コールバック関数
                    K01GPrintOutCallbackProcessMultipleMultiStage callbackProcMultipleMultiStage =
                            new K01GPrintOutCallbackProcessMultipleMultiStage(callbackInfo, cReportGenMultipleMultiStage);
                    reReportId = cReportGenMultipleMultiStage.Reprint(
                            fileName,                                 // 帳票様式番号(再印字対象電子ファイル名)
                            historyPrintout.getPrinter_id(),          // プリンタID
                            1,                                        // 1:PDF
                            pos,                                      // 再印刷マーク出力位置
                            callbackProcMultipleMultiStage,           // コールバック関数
                            printoutCopies,                           // 印刷部数
                            printerOutputFlag                         // 再印字時プリンター出力フラグ
                            );
                // 帳票印字（多重棚卸一覧）
                } else if (reportType == CommonEnums.PrintType.MultipleInventory.getValue()) {
                    // コールバック関数
                    K01HPrintOutCallbackProcessMultipleInventory callbackProcMultipleInventoryList =
                            new K01HPrintOutCallbackProcessMultipleInventory(callbackInfo, cReportGenMultipleInventoryList);
                    reReportId = cReportGenMultipleInventoryList.Reprint(
                            fileName,                                 // 帳票様式番号(再印字対象電子ファイル名)
                            historyPrintout.getPrinter_id(),          // プリンタID
                            1,                                        // 1:PDF
                            pos,                                      // 再印刷マーク出力位置
                            callbackProcMultipleInventoryList,        // コールバック関数
                            printoutCopies,                           // 印刷部数
                            printerOutputFlag                         // 再印字時プリンター出力フラグ
                            );
                }

                // 帳票ラベル印字履歴登録
                this.insertHistoryPrintout(
                        historyPrintout, reReportId,
                        printReasonCode, printReasonName, printReasonComment, printoutCopies,
                        super.getMessage(reReportId), eventId, requestSeq, em.getEntityManager());

            } catch (CReportGenException e) {
                // 印字履歴記録
                Object[] params = new Object[10];
                params[0] = e.getMessage();

                String message = MessagesHandler.getExceptionMessage(super.req,
                        super.exceptionFactory.createGnomesAppException(
                        null, GnomesMessagesConstants.ME01_0001, params));

                super.logHelper.severe(super.logger, null, null, message);

                // 帳票ラベル印字履歴登録
                this.insertHistoryPrintout(historyPrintout, null,
                        printReasonCode, printReasonName, printReasonComment, printoutCopies,
                        message, eventId, requestSeq, em.getEntityManager());
            }
        } catch (Exception e) {
            successFlag = false;
            try {
                throw e;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return successFlag;
    }

    /**
     * 再印字対象電子ファイル作成
     * @param fileName ファイル名
     * @param fileData ファイル情報
     * @throws GnomesAppException
     */
    private void createRePrintExcelFile(String fileName, byte[] fileData) throws GnomesAppException {

        String path = super.getPathName();

        if (!StringUtil.isNullOrEmpty(path)) {
            StringBuilder directoryPath = new StringBuilder();
            directoryPath.append(path);
            directoryPath.append(File.separator);
            directoryPath.append(fileName);

            FileUtils.writeFileFromByte(directoryPath.toString(), fileData);

        }

    }

    /**
     * 帳票ラベル印字履歴登録
     * @param tmpQueuePrintout 印字キュー
     * @param reportId リポートID
     * @param printReasonCode 印字理由コード
     * @param printReasonName 印字理由名
     * @param printReasonComment 印字理由コメント
     * @param printoutCopies 印字枚数
     * @param message エラーメッセージ
     * @param em エンティティマネージャー
     * @return 帳票ラベル印字履歴
     * @throws GnomesAppException
     */
    private void insertHistoryPrintout(HistoryPrintout historyPrintout,
            String reportId, String printReasonCode, String printReasonName, String printReasonComment,
            int printoutCopies, String message, String eventID, int requestSeq, EntityManager em) throws GnomesAppException {

        HistoryPrintout reHistoryPrintout = new HistoryPrintout();

        Timestamp date = ConverterUtils.utcToTimestamp(
                ConverterUtils.dateToLocalDateTime(
                        CurrentTimeStamp.getSystemCurrentTimeStamp()).atZone(ZoneId.systemDefault()));

        // 印字ラベル印字履歴情報再印字回数取得
        Integer printoutNum = this.historyPrintoutDao.getPrintoutNum(
                historyPrintout.getReprint_source_event_id(),
                historyPrintout.getReprint_source_request_seq(), em);

        // 帳票ラベル印字履歴キー
        reHistoryPrintout.setHistory_printout_key(UUID.randomUUID().toString());
        // 要求イベントID
        reHistoryPrintout.setEvent_id(eventID);
        // 要求内連番
        reHistoryPrintout.setRequest_seq(requestSeq);
        // 発生日時
        reHistoryPrintout.setOccur_date(date);
        // ReportID
        if (StringUtil.isNullOrEmpty(reportId) || ERROR_REPORT_ID.equals(reportId)) {
            reHistoryPrintout.setReport_id(UUID.randomUUID().toString());
        } else {
            reHistoryPrintout.setReport_id(reportId);
        }
        // 再印字フラグ
        reHistoryPrintout.setReprint_flag(ReprintFlag.ON.getValue());
        // 再印字回数
        reHistoryPrintout.setPrintout_num(printoutNum);
        // 端末ID
        reHistoryPrintout.setComputer_id(super.gnomesSessionBean.getComputerId());
        // 端末名
        reHistoryPrintout.setComputer_name(super.gnomesSessionBean.getComputerName());
        // 帳票様式番号
        reHistoryPrintout.setPrint_command_no(historyPrintout.getPrint_command_no());
        // 帳票印字日時
        reHistoryPrintout.setPrintout_date(historyPrintout.getPrintout_date());
        // 要求内連番
        reHistoryPrintout.setRequest_seq(historyPrintout.getRequest_seq());
        // 帳票種類
        reHistoryPrintout.setPrinter_type(historyPrintout.getPrinter_type());
        // 印字枚数
        reHistoryPrintout.setPrintout_copies(printoutCopies);
        // 帳票名
        reHistoryPrintout.setReport_name(historyPrintout.getReport_name());
        // DB領域区分
        reHistoryPrintout.setDb_area_div(historyPrintout.getDb_area_div());
        // プリンタID
        reHistoryPrintout.setPrinter_id(historyPrintout.getPrinter_id());
        // プリンタ名
        reHistoryPrintout.setPrinter_name(historyPrintout.getPrinter_name());
        // ユーザID
        reHistoryPrintout.setUser_id(super.gnomesSessionBean.getUserId());
        // ユーザ名
        reHistoryPrintout.setUser_name(super.gnomesSessionBean.getUserName());
        // 画面表示用Key情報
        reHistoryPrintout.setDisplay_key_text(historyPrintout.getDisplay_key_text());
        // 印字理由コード
        reHistoryPrintout.setPrint_reason_code(printReasonCode);
        // 印字理由名
        reHistoryPrintout.setPrint_reason_name(printReasonName);
        // 印字理由コメント
        reHistoryPrintout.setPrint_reason_comment(printReasonComment);
        // 再印刷マーク出力有無
        reHistoryPrintout.setIs_re_print_mark(CommonEnums.RePrintMark.REPRINTMARK.getValue());
        // 再印刷マーク出力位置
        reHistoryPrintout.setPosition_re_print_mark(historyPrintout.getPosition_re_print_mark());
        // 電子ファイル作成区分
        reHistoryPrintout.setIs_file_create_type(historyPrintout.getIs_file_create_type());
        // PDF電子ファイル名
        reHistoryPrintout.setPdf_file_name(historyPrintout.getPdf_file_name());
        // EXCEL電子ファイル名
        reHistoryPrintout.setExcel_file_name(historyPrintout.getExcel_file_name());

        if (StringUtil.isNullOrEmpty(reportId) || ERROR_REPORT_ID.equals(reportId)) {
            // 帳票印字結果状態
            reHistoryPrintout.setPrintout_status(PrintoutStatus.ERROR.getValue());
            // 印字エラーメッセージ
            reHistoryPrintout.setPrintout_error_message(super.editPrintoutErrorMessage(message));
        } else {
            // 帳票印字結果状態
            reHistoryPrintout.setPrintout_status(PrintoutStatus.PROCESSING.getValue());
        }

        // 再印字ソース要求イベントID
        reHistoryPrintout.setReprint_source_event_id(historyPrintout.getReprint_source_event_id());
        // 再印字ソース要求内連番
        reHistoryPrintout.setReprint_source_request_seq(historyPrintout.getReprint_source_request_seq());

        // 帳票印字様式マスターの帳票印字機能区分
        reHistoryPrintout.setPrintFunctionDiv(historyPrintout.getPrintFunctionDiv());
        // 帳票印字様式マスターの帳票定義単位区分
        reHistoryPrintout.setPrintDefineScopeDiv(historyPrintout.getPrintDefineScopeDiv());
        // 帳票印字様式マスターの指図工程コード
        reHistoryPrintout.setProcessCode(historyPrintout.getProcessCode());
        // 帳票印字様式マスターの試験目的区分
        reHistoryPrintout.setInspectionPurposeDiv(historyPrintout.getInspectionPurposeDiv());
        // 帳票印字様式マスターの品目タイプコード
        reHistoryPrintout.setItemTypeCode(historyPrintout.getItemCode());
        // 帳票印字様式マスターの品目コード
        reHistoryPrintout.setItemCode(historyPrintout.getItemCode());
        // 帳票印字様式マスターのレシピID
        reHistoryPrintout.setRecipeID(historyPrintout.getRecipeID());
        // 帳票印字様式マスターのレシピVER
        reHistoryPrintout.setRecipeVersion(historyPrintout.getRecipeVersion());
        // 帳票印字様式マスターのレシピREV
        reHistoryPrintout.setRecipeRevision(historyPrintout.getRecipeRevision());

        this.historyPrintoutDao.insert(reHistoryPrintout, em);
    }

}
