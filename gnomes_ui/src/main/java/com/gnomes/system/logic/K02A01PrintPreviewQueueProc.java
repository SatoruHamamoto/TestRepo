package com.gnomes.system.logic;

import java.io.File;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.picketbox.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

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
import com.gnomes.common.util.FileUtils;
import com.gnomes.system.dao.HistoryPrintoutFileDao;
import com.gnomes.system.dao.MstrSystemDefineDao;
import com.gnomes.system.data.printout.PrintOutCallbackInfo;
import com.gnomes.system.data.printout.PrintPreviewCallbackInfo;
import com.gnomes.system.entity.HistoryPrintout;
import com.gnomes.system.entity.TmpQueuePrintPreview;
import com.gnomes.system.entity.TmpQueuePrintPreviewParameter;

import biz.grandsight.ex.rs.CReportGen;
import biz.grandsight.ex.rs.CReportGenException;

/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2021/12/02 YJP-D/Jixin.Sun           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
*/
/**
 * キュー処理専用クラス。K02APrintPreviewよりコールされる。
 *
 * @author Jixin.Sun
 *
 */
@Dependent
public class K02A01PrintPreviewQueueProc extends PrintPreviewLogic
{

    /** バッチエンティティマネージャー */
    @Inject
    protected GnomesEntityManager    em;

    private static final String      CLASS_NAME      = "K01A01PrintPreviewQueueProc";

    private static final String      PDF_FOLDER_PATH = "./preview/";

    /** システム定義 Dao */
    @Inject
    protected MstrSystemDefineDao    mstrSystemDefineDao;

    /** 帳票ラベル印刷履歴ファイル Dao */
    @Inject
    protected HistoryPrintoutFileDao historyPrintoutFileDao;

    /** Excelファイル拡張子 */
    private static final String      EXCEL_EXTENSION = ".xlsx";

    /**
     * 日付からタイムゾーン付日付型に変換
     * 
     * @param value  変換元文字列
     * @param format フォーマット
     * @return 変換結果
     */
    public static ZonedDateTime dateToUtc(final Date date)
    {
        if (date == null) {
            return null;
        }

        ZonedDateTime result = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        // UTC(協定世界時)
        if (ZoneId.systemDefault().getId().equals(CommonConstants.ZONEID_UTC)) {
            result = result.withZoneSameLocal(ZoneId.of(CommonConstants.ZONEID_GMT)).withZoneSameInstant(
                    ZoneId.systemDefault());
        }
        // デフォルトタイムゾーン
        else {
            result = result.withZoneSameLocal(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of(
                    CommonConstants.ZONEID_UTC));
        }
        return result;
    }

    /**
     * 印刷プレビューの期限切れ日時取得
     * @return 印刷プレビューの期限切れ日時
     */
    protected Date getExpiredDate()
    {
        Date nowDate = new Date();
        ZonedDateTime utcNowDate = dateToUtc(nowDate);
        ZonedDateTime utcExpiredDate = utcNowDate.minusHours(this.getPeriodHour());

        return new Date(Timestamp.valueOf(utcExpiredDate.toLocalDateTime()).getTime());
    }

    /**
     * 帳票印刷プレビューのキュー取得・印刷実行処理
     * ※この範囲でトランザクションが制御される
     * @param localeId          ロケールＩＤ
     * @return
     */
    @ErrorHandling
    @GnomesTransactional
    public boolean printPreviewQueueProc(String localeId)
    {
        final String methodName = "printPreviewQueueProc";

        /** キューの1件分のDTO */
        TmpQueuePrintPreview tmpQueuePrintPreview = null;

        /** キューの印刷パラメータ */
        List<TmpQueuePrintPreviewParameter> tmpQueuePrintPreviewParameters = null;

        /** 印刷プレビューキー情報 */
        HashMap<String, String> phvalue = null;

        // 処理成功フラグ
        boolean successFlag = false;

        try {
            // 期限切れ日時取得
            Date expiredDate = this.getExpiredDate();

            // 印刷プレビューキュー取得
            tmpQueuePrintPreview = getTmpQueuePrintPreviewFromQueue(expiredDate, em.getEntityManager());

            // 該当データが存在しない場合
            // （すべてのキューを処理し終えたか、最初からキューがないか）
            if (Objects.isNull(tmpQueuePrintPreview)) {
                // キューがないので親の繰り返しチェック制御を終了する
                return false;
            }

            // ----------------------------------------------------
            // キューのパラメータ取得
            // ----------------------------------------------------
            tmpQueuePrintPreviewParameters = getTmpQueuePrintPreviewParameterFromEntity(tmpQueuePrintPreview,
                    em.getEntityManager());

            // 印刷プレビューキー情報取得
            phvalue = this.getPhvalue(tmpQueuePrintPreviewParameters);

            int previewStatus = tmpQueuePrintPreview.getPrint_preview_status();
            if (previewStatus == CommonEnums.PrintPreviewStatus.PREVIEW_UNTREATED.getValue()) {
                // 印刷プレビューキューの帳票印刷プレビュー状態を「プレビュー処理中」に更新
                if (tmpQueuePrintPreview != null) {
                    tmpQueuePrintPreview.setPrint_preview_status(
                            CommonEnums.PrintPreviewStatus.PREVIEW_PROCESSING.getValue());
                    tmpQueuePrintPreview.setPreview_folder_path(PDF_FOLDER_PATH);
                    this.tmpQueuePrintPreviewDao.update(tmpQueuePrintPreview, em.getEntityManager());
                }
                // ----------------------------------------------------
                // 印刷プレビュー処理メイン
                // ----------------------------------------------------

                int printer_type = tmpQueuePrintPreview.getPrinter_type();
                // 帳票印刷プレビュー（ラベル）
                if (printer_type == CommonEnums.PrintType.Label.getValue()) {
                    successFlag = this.previewRequestMain(tmpQueuePrintPreview, phvalue, localeId);
                    // 帳票印刷プレビュー（一覧）
                }
                else if (printer_type == CommonEnums.PrintType.List.getValue()) {
                    successFlag = this.previewRequestMain(tmpQueuePrintPreview, phvalue, localeId);
                    // 帳票印刷プレビュー（多重）
                }
                else if (printer_type == CommonEnums.PrintType.Multiple.getValue()) {
                    successFlag = this.previewRequestMainMultiple(tmpQueuePrintPreview, phvalue, localeId);
                    // 帳票印刷プレビュー（多段）
                }
                else if (printer_type == CommonEnums.PrintType.MultiStage.getValue()) {
                    successFlag = this.previewRequestMainMultiStage(tmpQueuePrintPreview, phvalue, localeId);
                    // 帳票印刷プレビュー（多段キー変更改ページ無し）
                }
                else if (printer_type == CommonEnums.PrintType.MultiStageNoNewPage.getValue()) {
                    successFlag = this.previewRequestMainMultiStageNoNewPage(tmpQueuePrintPreview, phvalue, localeId);
                    // 帳票印刷プレビュー（多重多段）
                }
                else if (printer_type == CommonEnums.PrintType.MultipleMultiStage.getValue()) {
                    successFlag = this.previewRequestMainMultipleMultiStage(tmpQueuePrintPreview, phvalue, localeId);
                    // 帳票印刷プレビュー（多重棚卸一覧）
                }
                else if (printer_type == CommonEnums.PrintType.MultipleInventory.getValue()) {
                    successFlag = this.previewRequestMainMultipleInventory(tmpQueuePrintPreview, phvalue, localeId);
                }
            }
            else if (previewStatus == CommonEnums.PrintPreviewStatus.PRINT_UNTREATED.getValue()) {
                // 印刷プレビューキューの帳票印刷プレビュー状態を「印刷処理中」に更新
                if (tmpQueuePrintPreview != null) {
                    tmpQueuePrintPreview.setPrint_preview_status(
                            CommonEnums.PrintPreviewStatus.PRINT_PROCESSING.getValue());
                    this.tmpQueuePrintPreviewDao.update(tmpQueuePrintPreview, em.getEntityManager());
                }
                // 処理成功フラグ
                this.print(tmpQueuePrintPreview, tmpQueuePrintPreview.getReport_id(),
                        tmpQueuePrintPreview.getPrint_reason_code(), tmpQueuePrintPreview.getPrint_reason_name(),
                        tmpQueuePrintPreview.getPrint_reason_comment(), tmpQueuePrintPreview.getPrint_times(),
                        tmpQueuePrintPreview.getEvent_id(), tmpQueuePrintPreview.getRequest_seq(), true);

            }
            else {

                // 印刷プレビューキューのプレビュー画面表示状態が「画面が閉じられる」であるか、または、印刷プレビューキュー登録日時が期限切れであるかをチェック
                int displayStatus = tmpQueuePrintPreview.getPreview_display_status();
                if (((displayStatus == CommonEnums.PreviewDisplayStatus.CLOSED.getValue()) && ((previewStatus != CommonEnums.PrintPreviewStatus.PREVIEW_PROCESSING.getValue()) && (previewStatus != CommonEnums.PrintPreviewStatus.PRINT_PROCESSING.getValue()))) || (tmpQueuePrintPreview.getFirst_regist_datetime().getTime() < expiredDate.getTime())) {
                    // 印刷プレビューキューとプレビューファイルを削除
                    this.deletePrintPreviewQueue(tmpQueuePrintPreview, em.getEntityManager(),
                            tmpQueuePrintPreviewParameters);
                }
            }

        }
        catch (Exception e) {

            // ログに記録する
            this.logHelper.severe(logger, CLASS_NAME, methodName, e.getMessage() + makeQueueInfo(tmpQueuePrintPreview),
                    e);

            // この処理失敗を意味する。コミットはする。ループはやめる
            return false;
        }

        // ループは継続する
        return true;

    }

    /**
     * 帳票印刷.
     * @param TmpQueuePrintPreview 印刷プレビューキュー
     * @param reportId ReportId
     * @param printReasonCode 印刷理由コード
     * @param printReasonName 印刷理由名
     * @param printReasonComment 印刷理由コメント
     * @param printoutCopies 印刷枚数
     * @param eventId nk要求イベントID
     * @param requestSeq nk要求内連番
     * @param printerOutputFlag 再印刷時プリンター出力フラグ
     *
     * @throws GnomesAppException
     */
    protected boolean print(TmpQueuePrintPreview tmpQueuePrintPreview, String reportId, String printReasonCode,
            String printReasonName, String printReasonComment, int printoutCopies, String eventId, int requestSeq,
            boolean printerOutputFlag) throws GnomesAppException
    {

        // 処理成功フラグ
        boolean successFlag = true;
        // 印刷対象電子ファイル名
        String fileName = null;

        try {

            //  帳票印刷設定情報チェック
            if (Objects.isNull(super.gnomesSystemBean.getcGenReportMeta())) {
                // 帳票印刷設定情報が Web.xml に正しく設定されていません。
                throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0161);

            }

            // 印刷対象電子ファイル名
            fileName = UUID.randomUUID().toString() + EXCEL_EXTENSION;

            try {

                StringBuilder directoryPathTo = new StringBuilder();
                directoryPathTo.append(this.getPathName("preview-directory"));
                directoryPathTo.append(File.separator);
                directoryPathTo.append(tmpQueuePrintPreview.getReport_id().replace(".xlsx", ""));

                // 印刷対象電子ファイル作成
                this.createRePrintExcelFile(fileName, FileUtils.readFileToByte(this.getFilePath(
                        directoryPathTo.toString(), tmpQueuePrintPreview.getReport_id())));

            }
            catch (GnomesAppException e) {

                String errorInfo = MessagesHandler.getExceptionMessage(super.req, e);

                // 印刷対象のファイル作成に失敗しました。 (エラー内容: {0}）
                throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0188,
                        errorInfo);

            }

            // 印刷依頼処理
            // CReportGen初期化
            CReportGen cReportGen = super.initCReportGen(tmpQueuePrintPreview.getDb_area_div());
            // CReportGen（多重）初期化
            biz.grandsight.ex.rs_multiple.CReportGen cReportGenMultiple = super.initCReportGenMultiple(
                    tmpQueuePrintPreview.getDb_area_div());
            // CReportGen（多重棚卸一覧）初期化
            biz.grandsight.ex.rs_multiple21.CReportGen cReportGenMultipleInventoryList = super.initCReportGenMultipleInventoryList(
                    tmpQueuePrintPreview.getDb_area_div());
            // CReportGen（多段）初期化
            biz.grandsight.ex.rs_multistage.CReportGen cReportGenMultiStage = super.initCReportGenMultiStage(
                    tmpQueuePrintPreview.getDb_area_div());
            // CReportGen（多段キー変更改ページ無し）初期化
            biz.grandsight.ex.rs_multistage41.CReportGen cReportGenMultiStageNoNewPage = super.initCReportGenMultiStageNoNewPage(
                    tmpQueuePrintPreview.getDb_area_div());
            // CReportGen（多重多段）初期化
            biz.grandsight.ex.rs_multiplemultistage.CReportGen cReportGenMultipleMultiStage = super.initCReportGenMultipleMultiStage(
                    tmpQueuePrintPreview.getDb_area_div());

            // 印刷画像位置取得
            String pos = tmpQueuePrintPreview.getPosition_re_print_mark();
            if (StringUtils.isEmpty(pos)) {
                pos = "";
            }

            try {

                PrintPreviewCallbackInfo callbackInfo = super.setCallbackInfo(tmpQueuePrintPreview.getPdf_file_name(),
                        PrintOutCallbackInfo.PRINT, fileName, printoutCopies, 1, eventId, requestSeq,
                        tmpQueuePrintPreview.getQueue_print_preview_key());

                // コールバック関数
                K02CPrintPreviewPrintoutCallbackProcess callbackProc = new K02CPrintPreviewPrintoutCallbackProcess(
                        callbackInfo, cReportGen);

                String reReportId = null;

                int reportType = tmpQueuePrintPreview.getPrinter_type();
                // 再印刷依頼
                // 帳票印刷（ラベル）
                if (reportType == CommonEnums.PrintType.Label.getValue()) {
                    reReportId = cReportGen.Reprint(fileName, // 帳票様式番号(再印刷対象電子ファイル名)
                            tmpQueuePrintPreview.getPrinter_id(), // プリンタID
                            1, // 1:PDF
                            pos, // 再印刷マーク出力位置
                            callbackProc, // コールバック関数
                            printoutCopies, // 印刷部数
                            printerOutputFlag // 再印刷時プリンター出力フラグ
                    );
                    // 帳票印刷（一覧）
                }
                else if (reportType == CommonEnums.PrintType.List.getValue()) {
                    reReportId = cReportGen.Reprint(fileName, // 帳票様式番号(再印刷対象電子ファイル名)
                            tmpQueuePrintPreview.getPrinter_id(), // プリンタID
                            1, // 1:PDF
                            pos, // 再印刷マーク出力位置
                            callbackProc, // コールバック関数
                            printoutCopies, // 印刷部数
                            printerOutputFlag // 再印刷時プリンター出力フラグ
                    );
                    // 帳票印刷（多重）
                }
                else if (reportType == CommonEnums.PrintType.Multiple.getValue()) {
                    // コールバック関数
                    K02DPrintPreviewPrintoutCallbackProcessMultiple callbackProcMultiple = new K02DPrintPreviewPrintoutCallbackProcessMultiple(
                            callbackInfo, cReportGenMultiple);
                    reReportId = cReportGenMultiple.Reprint(fileName, // 帳票様式番号(再印刷対象電子ファイル名)
                            tmpQueuePrintPreview.getPrinter_id(), // プリンタID
                            1, // 1:PDF
                            pos, // 再印刷マーク出力位置
                            callbackProcMultiple, // コールバック関数
                            printoutCopies, // 印刷部数
                            printerOutputFlag // 再印刷時プリンター出力フラグ
                    );
                    // 帳票印刷（多段）
                }
                else if (reportType == CommonEnums.PrintType.MultiStage.getValue()) {
                    // コールバック関数
                    K02EPrintPreviewPrintoutCallbackProcessMultiStage callbackProcMultiStage = new K02EPrintPreviewPrintoutCallbackProcessMultiStage(
                            callbackInfo, cReportGenMultiStage);
                    reReportId = cReportGenMultiStage.Reprint(fileName, // 帳票様式番号(再印刷対象電子ファイル名)
                            tmpQueuePrintPreview.getPrinter_id(), // プリンタID
                            1, // 1:PDF
                            pos, // 再印刷マーク出力位置
                            callbackProcMultiStage, // コールバック関数
                            printoutCopies, // 印刷部数
                            printerOutputFlag // 再印刷時プリンター出力フラグ
                    );
                    // 帳票印刷（多段キー変更改ページ無し）
                }
                else if (reportType == CommonEnums.PrintType.MultiStageNoNewPage.getValue()) {
                    // コールバック関数
                    K02FPrintPreviewPrintoutCallbackProcessMultiStageNoNewPage callbackProcMultiStageNoNewPage = new K02FPrintPreviewPrintoutCallbackProcessMultiStageNoNewPage(
                            callbackInfo, cReportGenMultiStageNoNewPage);
                    reReportId = cReportGenMultiStageNoNewPage.Reprint(fileName, // 帳票様式番号(再印刷対象電子ファイル名)
                            tmpQueuePrintPreview.getPrinter_id(), // プリンタID
                            1, // 1:PDF
                            pos, // 再印刷マーク出力位置
                            callbackProcMultiStageNoNewPage, // コールバック関数
                            printoutCopies, // 印刷部数
                            printerOutputFlag // 再印刷時プリンター出力フラグ
                    );
                    // 帳票印刷（多重多段）
                }
                else if (reportType == CommonEnums.PrintType.MultipleMultiStage.getValue()) {
                    // コールバック関数
                    K02GPrintPreviewPrintoutCallbackProcessMultipleMultiStage callbackProcMultipleMultiStage = new K02GPrintPreviewPrintoutCallbackProcessMultipleMultiStage(
                            callbackInfo, cReportGenMultipleMultiStage);
                    reReportId = cReportGenMultipleMultiStage.Reprint(fileName, // 帳票様式番号(再印刷対象電子ファイル名)
                            tmpQueuePrintPreview.getPrinter_id(), // プリンタID
                            1, // 1:PDF
                            pos, // 再印刷マーク出力位置
                            callbackProcMultipleMultiStage, // コールバック関数
                            printoutCopies, // 印刷部数
                            printerOutputFlag // 再印刷時プリンター出力フラグ
                    );
                    // 帳票印刷（多重棚卸一覧）
                }
                else if (reportType == CommonEnums.PrintType.MultipleInventory.getValue()) {
                    // コールバック関数
                    K02HPrintPreviewPrintoutCallbackProcessMultipleInventory callbackProcMultipleInventoryList = new K02HPrintPreviewPrintoutCallbackProcessMultipleInventory(
                            callbackInfo, cReportGenMultipleInventoryList);
                    reReportId = cReportGenMultipleInventoryList.Reprint(fileName, // 帳票様式番号(再印刷対象電子ファイル名)
                            tmpQueuePrintPreview.getPrinter_id(), // プリンタID
                            1, // 1:PDF
                            pos, // 再印刷マーク出力位置
                            callbackProcMultipleInventoryList, // コールバック関数
                            printoutCopies, // 印刷部数
                            printerOutputFlag // 再印刷時プリンター出力フラグ
                    );
                }

                // 帳票ラベル印字履歴存在チェック
                if (!this.historyPrintoutDao.isHistoryPrintExisted(eventId, requestSeq, em.getEntityManager())) {
                    // 帳票ラベル印刷履歴登録
                    this.insertHistoryPrintout(tmpQueuePrintPreview, reReportId, printReasonCode, printReasonName,
                            printReasonComment, printoutCopies, super.getMessage(reReportId), eventId, requestSeq,
                            em.getEntityManager());
                }
            }
            catch (CReportGenException e) {
                // 印刷履歴記録
                Object[] params = new Object[10];
                params[0] = e.getMessage();

                String message = MessagesHandler.getExceptionMessage(super.req,
                        super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0001,
                                params));

                super.logHelper.severe(super.logger, null, null, message);

                // 帳票ラベル印字履歴存在チェック
                if (!this.historyPrintoutDao.isHistoryPrintExisted(eventId, requestSeq, em.getEntityManager())) {
                    // 帳票ラベル印刷履歴登録
                    this.insertHistoryPrintout(tmpQueuePrintPreview, null, printReasonCode, printReasonName,
                            printReasonComment, printoutCopies, message, eventId, requestSeq, em.getEntityManager());
                }
            }
        }
        catch (Exception e) {
            successFlag = false;
            this.logHelper.severe(logger, CLASS_NAME, null, null, e);
        }
        return successFlag;
    }

    /**
     * 帳票ラベル印刷履歴登録
     * @param tmpQueuePrintout 印刷キュー
     * @param reportId リポートID
     * @param printReasonCode 印刷理由コード
     * @param printReasonName 印刷理由名
     * @param printReasonComment 印刷理由コメント
     * @param printoutCopies 印刷枚数
     * @param message エラーメッセージ
     * @param em エンティティマネージャー
     * @return 帳票ラベル印刷履歴
     * @throws GnomesAppException
     */
    private void insertHistoryPrintout(TmpQueuePrintPreview tmpQueuePrintPreview, String reportId,
            String printReasonCode, String printReasonName, String printReasonComment, int printoutCopies,
            String message, String eventID, int requestSeq, EntityManager em) throws GnomesAppException
    {

        HistoryPrintout reHistoryPrintout = new HistoryPrintout();

        Timestamp date = ConverterUtils.utcToTimestamp(ConverterUtils.dateToLocalDateTime(
                CurrentTimeStamp.getSystemCurrentTimeStamp()).atZone(ZoneId.systemDefault()));

        // 帳票ラベル印刷履歴キー
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
        }
        else {
            reHistoryPrintout.setReport_id(reportId);
        }
        // 再印刷フラグ
        reHistoryPrintout.setReprint_flag(ReprintFlag.ON.getValue());
        // 再印刷回数
        reHistoryPrintout.setPrintout_num(0);
        // 端末ID
        reHistoryPrintout.setComputer_id(tmpQueuePrintPreview.getComputer_id());
        // 端末名
        reHistoryPrintout.setComputer_name(tmpQueuePrintPreview.getComputer_name());
        // 帳票様式番号
        reHistoryPrintout.setPrint_command_no(tmpQueuePrintPreview.getPrint_command_no());
        // 帳票印刷日時
        reHistoryPrintout.setPrintout_date(tmpQueuePrintPreview.getPrint_preview_date());
        // 要求内連番
        reHistoryPrintout.setRequest_seq(tmpQueuePrintPreview.getRequest_seq());
        // 帳票種類
        reHistoryPrintout.setPrinter_type(tmpQueuePrintPreview.getPrinter_type());
        // 印刷枚数
        reHistoryPrintout.setPrintout_copies(printoutCopies);
        // 帳票名
        reHistoryPrintout.setReport_name(tmpQueuePrintPreview.getReport_name());
        // DB領域区分
        reHistoryPrintout.setDb_area_div(tmpQueuePrintPreview.getDb_area_div());
        // プリンタID
        reHistoryPrintout.setPrinter_id(tmpQueuePrintPreview.getPrinter_id());
        // プリンタ名
        reHistoryPrintout.setPrinter_name(tmpQueuePrintPreview.getPrinter_name());
        // ユーザID
        reHistoryPrintout.setUser_id(tmpQueuePrintPreview.getUser_id());
        // ユーザ名
        reHistoryPrintout.setUser_name(tmpQueuePrintPreview.getUser_name());
        // 画面表示用Key情報
        reHistoryPrintout.setDisplay_key_text(tmpQueuePrintPreview.getDisplay_key_text());
        // 印刷理由コード
        reHistoryPrintout.setPrint_reason_code(printReasonCode);
        // 印刷理由名
        reHistoryPrintout.setPrint_reason_name(printReasonName);
        // 印刷理由コメント
        reHistoryPrintout.setPrint_reason_comment(printReasonComment);
        // 再印刷マーク出力有無
        reHistoryPrintout.setIs_re_print_mark(CommonEnums.RePrintMark.REPRINTMARK.getValue());
        // 再印刷マーク出力位置
        reHistoryPrintout.setPosition_re_print_mark(tmpQueuePrintPreview.getPosition_re_print_mark());
        // 電子ファイル作成区分
        reHistoryPrintout.setIs_file_create_type(tmpQueuePrintPreview.getIs_file_create_type());
        // PDF電子ファイル名
        reHistoryPrintout.setPdf_file_name(tmpQueuePrintPreview.getPdf_file_name());
        // EXCEL電子ファイル名
        reHistoryPrintout.setExcel_file_name(null);

        if (StringUtil.isNullOrEmpty(reportId) || ERROR_REPORT_ID.equals(reportId)) {
            // 帳票印刷結果状態
            reHistoryPrintout.setPrintout_status(PrintoutStatus.ERROR.getValue());
            // 印刷エラーメッセージ
            reHistoryPrintout.setPrintout_error_message(super.editPrintoutErrorMessage(message));
        }
        else {
            // 帳票印刷結果状態
            reHistoryPrintout.setPrintout_status(PrintoutStatus.PROCESSING.getValue());
        }

        // 再印刷ソース要求イベントID
        reHistoryPrintout.setReprint_source_event_id(tmpQueuePrintPreview.getEvent_id());
        // 再印刷ソース要求内連番
        reHistoryPrintout.setReprint_source_request_seq(tmpQueuePrintPreview.getRequest_seq());

        // 帳票印刷様式マスターの帳票印刷機能区分
        reHistoryPrintout.setPrintFunctionDiv(tmpQueuePrintPreview.getPrintFunctionDiv());
        // 帳票印刷様式マスターの帳票定義単位区分
        reHistoryPrintout.setPrintDefineScopeDiv(tmpQueuePrintPreview.getPrintDefineScopeDiv());
        // 帳票印刷様式マスターの指図工程コード
        reHistoryPrintout.setProcessCode(tmpQueuePrintPreview.getProcessCode());
        // 帳票印刷様式マスターの試験目的区分
        reHistoryPrintout.setInspectionPurposeDiv(tmpQueuePrintPreview.getInspectionPurposeDiv());
        // 帳票印刷様式マスターの品目タイプコード
        reHistoryPrintout.setItemTypeCode(tmpQueuePrintPreview.getItemCode());
        // 帳票印刷様式マスターの品目コード
        reHistoryPrintout.setItemCode(tmpQueuePrintPreview.getItemCode());
        // 帳票印刷様式マスターのレシピID
        reHistoryPrintout.setRecipeID(tmpQueuePrintPreview.getRecipeID());
        // 帳票印刷様式マスターのレシピVER
        reHistoryPrintout.setRecipeVersion(tmpQueuePrintPreview.getRecipeVersion());
        // 帳票印刷様式マスターのレシピREV
        reHistoryPrintout.setRecipeRevision(tmpQueuePrintPreview.getRecipeRevision());

        this.historyPrintoutDao.insert(reHistoryPrintout, em);
    }

    /**
     * 印刷対象電子ファイル作成
     * @param fileName ファイル名
     * @param fileData ファイル情報
     * @throws GnomesAppException
     */
    private void createRePrintExcelFile(String fileName, byte[] fileData) throws GnomesAppException
    {

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
     *
     * 印刷プレビューキュー情報を削除
     *
     * @param tmpQueuePrintout キューのエンティティオブジェクト
     * @param reportId         帳票ID
     * @param em               エンティティマネージャー
     * @param phvalue          印刷キー情報
     */
    private void deletePrintPreviewQueue(TmpQueuePrintPreview tmpQueuePrintPreview, EntityManager em,
            List<TmpQueuePrintPreviewParameter> tmpQueuePrintPreviewParameters) throws GnomesAppException
    {

        //----------------------------------------------------
        // 印刷プレビューキュー削除(結果がどうであろうと）
        //----------------------------------------------------
        if (!Objects.isNull(tmpQueuePrintPreview)) {
            this.tmpQueuePrintPreviewDao.delete(tmpQueuePrintPreview, em);
        }

        //----------------------------------------------------
        // 印刷プレビューキーパラメータ削除
        //----------------------------------------------------
        if (!Objects.isNull(tmpQueuePrintPreviewParameters)) {
            this.tmpQueuePrintPreviewParameterDao.delete(tmpQueuePrintPreviewParameters, em);
        }

        //----------------------------------------------------
        // プレビューファイル削除
        //----------------------------------------------------
        if (!Objects.isNull(tmpQueuePrintPreviewParameters) && !Objects.isNull(tmpQueuePrintPreview) && !Objects.isNull(
                tmpQueuePrintPreview.getReport_id())) {
            StringBuilder directoryPath = new StringBuilder();
            directoryPath.append(this.getPathName("preview-directory"));
            directoryPath.append(File.separator);
            directoryPath.append(tmpQueuePrintPreview.getReport_id().replace(".xlsx", ""));
            File pathName = new File(directoryPath.toString());
            try {
                org.apache.commons.io.FileUtils.deleteDirectory(pathName);
            }
            catch (Exception e) {
                // ログ出力
                String errMessage = MessagesHandler.getExceptionMessage(super.req,
                        super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0192,
                                new Object[]{directoryPath.toString(), e.getMessage()}));

                super.logHelper.severe(super.logger, null, null, errMessage);
            }
        }

    }

    /**
     * パス名取得
     * @return パス名
     */
    protected String getPathName(String elementsByTagName)
    {

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            // XML解析で外部エンティティへのアクセスを無効にする
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            DocumentBuilder configXml = dbf.newDocumentBuilder();
            Document xMLSetting = configXml.parse(super.gnomesSystemBean.getReportDefinitionXMLFileName());
            NodeList template = xMLSetting.getElementsByTagName(elementsByTagName).item(0).getChildNodes();
            return template.item(1).getTextContent();

        }
        catch (Exception e) {

            String message = MessagesHandler.getExceptionMessage(super.req,
                    super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0001,
                            new Object[10]));

            this.logHelper.severe(super.logger, null, null, message, e);

        }

        return "";

    }

    /**
     * ファイルパス取得
     * @param directoryPath ディレクトリパス
     * @param fileName ファイル名
     * @return ファイルパス
     */
    private String getFilePath(String directoryPath, String fileName)
    {

        StringBuilder excelfilePath = new StringBuilder();
        excelfilePath.append(directoryPath);
        excelfilePath.append(File.separator);
        excelfilePath.append(fileName);

        return excelfilePath.toString();

    }

    /**
     * キューテーブルから1レコード分を取得
     */
    /**
     * @param em    エンティティマネージャー
     * @return
     */
    private TmpQueuePrintPreview getTmpQueuePrintPreviewFromQueue(Date expiredDate, EntityManager em)
    {
        // 印刷プレビューキュー取得
        TmpQueuePrintPreview tmpQueuePrintPreview = this.tmpQueuePrintPreviewDao.getQueue(expiredDate, em);

        return tmpQueuePrintPreview;
    }
    /**
     * キューの中に一致した印刷キューのパラメータを抜き出す
     * @param tmpQueuePrintPreview  キューエンティティ
     * @param em    エンティティマネージャー
     * @return
     */
    private List<TmpQueuePrintPreviewParameter> getTmpQueuePrintPreviewParameterFromEntity(
            TmpQueuePrintPreview tmpQueuePrintPreview, EntityManager em)
    {
        // 印刷キューパラメータ取得
        List<TmpQueuePrintPreviewParameter> tmpQueuePrintPreviewParameters = tmpQueuePrintPreviewParameterDao.getParameter(
                em, tmpQueuePrintPreview.getQueue_print_preview_key());

        return tmpQueuePrintPreviewParameters;
    }

    /**
     * 印刷キー情報取得(Json読込み)
     * @param params 印刷キューパラメータ情報
     * @return 印刷キー情報
     * @throws GnomesAppException
     */
    private HashMap<String, String> getPhvalue(List<TmpQueuePrintPreviewParameter> params)
    {

        HashMap<String, String> phvalue = new HashMap<>();

        for (TmpQueuePrintPreviewParameter param : params) {
            phvalue.put(param.getParameter_name(), param.getParameter_value());
        }
        return phvalue;

    }

    /**
     * ログやエラーメッセージ用にキューの内容をダンプする
     *
     * @param tmpQueuePrintPreview
     * @return
     */
    private String makeQueueInfo(TmpQueuePrintPreview tmpQueuePrintPreview)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(" Queue Info : ");

        if (!Objects.isNull(tmpQueuePrintPreview)) {
            sb.append(" queue_printPreview_key = " + tmpQueuePrintPreview.getQueue_print_preview_key());
            sb.append(" print_command_no = " + tmpQueuePrintPreview.getPrint_command_no());
            sb.append(" report_name = " + tmpQueuePrintPreview.getReport_name());
        }

        return sb.toString();
    }

    /**
    *
    * 印刷プレビュー要求の実施を行う
    *
    * @param tmpQueuePrintPreview  印刷プレビューキューのエンティティ1件分
    * @param printerNo         リクエストにあったプリンタ番号
    * @param phvalue           印刷プレビューキー情報
    * @param em                エンティティマネージャー
    * @param localeId          ロケールＩＤ
    * @return
    * @throws GnomesAppException
    */
    private boolean previewRequestMain(TmpQueuePrintPreview tmpQueuePrintPreview, HashMap<String, String> phvalue,
            String localeId) throws GnomesAppException
    {

        final String methodName = "printRequestMain";

        //プリンタのIDを一時取得
        String printerNo = tmpQueuePrintPreview.getPrinter_id();

        // CReportGen初期化
        CReportGen cReportGen = super.initCReportGen(tmpQueuePrintPreview.getDb_area_div());

        try {

            // コールバック関数の登録
            PrintPreviewCallbackInfo callbackInfo = super.setCallbackInfo(tmpQueuePrintPreview.getPdf_file_name(),
                    PrintOutCallbackInfo.PRINT, null, tmpQueuePrintPreview.getPrint_times(), 1,
                    tmpQueuePrintPreview.getEvent_id(), tmpQueuePrintPreview.getRequest_seq(),
                    tmpQueuePrintPreview.getQueue_print_preview_key());

            // コールバック関数
            K02CPrintPreviewCallbackProcess callbackProc = new K02CPrintPreviewCallbackProcess(callbackInfo,
                    cReportGen);

            // ロケール切替
            String printCommandNumber = tmpQueuePrintPreview.getPrint_command_no();
            if (tmpQueuePrintPreview.getPrint_command_no().contains(CommonConstants.JOIN_LOCALE)) {
                String[] selectLocale = localeId.split(CommonConstants.SPLIT_CHAR);
                printCommandNumber = tmpQueuePrintPreview.getPrint_command_no().replace(CommonConstants.REPLACE_LOCALE,
                        selectLocale[0]);
            }

            //----------------------------------------------------------------
            // 印刷プレビュー依頼
            //----------------------------------------------------------------
            cReportGen.Print(printCommandNumber, // 帳票様式番号
                    phvalue, // 印刷プレビューキー情報
                    printerNo, // プリンタID
                    1, // 1:PDF
                    callbackProc, // コールバック関数
                    tmpQueuePrintPreview.getPrint_times(), // 印刷部数
                    false // プリンターから印刷するかしないか
            );

        }
        catch (Exception e) {

            this.logHelper.severe(logger, CLASS_NAME, methodName, e.getMessage(), e);

        }
        return true;
    }

    /**
    *
    * 印刷プレビュー要求の実施を行う（多重）
    *
    * @param tmpQueuePrintPreview  印刷プレビューキューのエンティティ1件分
    * @param printerNo         リクエストにあったプリンタ番号
    * @param phvalue           印刷プレビューキー情報
    * @param em                エンティティマネージャー
    * @param localeId          ロケールＩＤ
    * @return
    * @throws GnomesAppException
    */
    private boolean previewRequestMainMultiple(TmpQueuePrintPreview tmpQueuePrintPreview,
            HashMap<String, String> phvalue, String localeId) throws GnomesAppException
    {
        final String methodName = "printRequestMainMultiple";

        // プリンタのIDを一時取得
        String printerNo = tmpQueuePrintPreview.getPrinter_id();

        // CReportGen（多重）初期化
        biz.grandsight.ex.rs_multiple.CReportGen cReportGenMultiple = super.initCReportGenMultiple(
                tmpQueuePrintPreview.getDb_area_div());

        try {

            // コールバック関数の登録
            PrintPreviewCallbackInfo callbackInfo = super.setCallbackInfo(tmpQueuePrintPreview.getPdf_file_name(),
                    PrintOutCallbackInfo.PRINT, null, tmpQueuePrintPreview.getPrint_times(), 1,
                    tmpQueuePrintPreview.getEvent_id(), tmpQueuePrintPreview.getRequest_seq(),
                    tmpQueuePrintPreview.getQueue_print_preview_key());

            // コールバック関数
            K02DPrintPreviewCallbackProcessMultiple callbackProc = new K02DPrintPreviewCallbackProcessMultiple(
                    callbackInfo, cReportGenMultiple);

            // ロケール切替
            String printCommandNumber = tmpQueuePrintPreview.getPrint_command_no();
            if (tmpQueuePrintPreview.getPrint_command_no().contains(CommonConstants.JOIN_LOCALE)) {
                String[] selectLocale = localeId.split(CommonConstants.SPLIT_CHAR);
                printCommandNumber = tmpQueuePrintPreview.getPrint_command_no().replace(CommonConstants.REPLACE_LOCALE,
                        selectLocale[0]);
            }

            //----------------------------------------------------------------
            // 印刷プレビュー依頼
            //----------------------------------------------------------------
            cReportGenMultiple.Print(printCommandNumber, // 帳票様式番号
                    phvalue, // 印刷プレビューキー情報
                    printerNo, // プリンタID
                    1, // 1:PDF
                    callbackProc, // コールバック関数
                    tmpQueuePrintPreview.getPrint_times(), // 印刷部数
                    false // プリンターから印刷するかしないか
            );

        }
        catch (biz.grandsight.ex.rs_multiple.CReportGenException e) {

            this.logHelper.severe(logger, CLASS_NAME, methodName, e.getMessage(), e);
        }
        return true;
    }

    /**
    *
    * 印刷プレビュー要求の実施を行う（多重棚卸一覧）
    *
    * @param tmpQueuePrintPreview  印刷プレビューキューのエンティティ1件分
    * @param printerNo         リクエストにあったプリンタ番号
    * @param phvalue           印刷プレビューキー情報
    * @param em                エンティティマネージャー
    * @param localeId          ロケールＩＤ
    * @return
    * @throws GnomesAppException
    */
    private boolean previewRequestMainMultipleInventory(TmpQueuePrintPreview tmpQueuePrintPreview,
            HashMap<String, String> phvalue, String localeId) throws GnomesAppException
    {

        final String methodName = "printRequestMainMultipleInventory";

        // プリンタのIDを一時取得
        String printerNo = tmpQueuePrintPreview.getPrinter_id();

        // CReportGen（多重棚卸一覧）初期化
        biz.grandsight.ex.rs_multiple21.CReportGen cReportGenMultipleInventoryList = super.initCReportGenMultipleInventoryList(
                tmpQueuePrintPreview.getDb_area_div());

        try {

            // コールバック関数の登録
            PrintPreviewCallbackInfo callbackInfo = super.setCallbackInfo(tmpQueuePrintPreview.getPdf_file_name(),
                    PrintOutCallbackInfo.PRINT, null, tmpQueuePrintPreview.getPrint_times(), 1,
                    tmpQueuePrintPreview.getEvent_id(), tmpQueuePrintPreview.getRequest_seq(),
                    tmpQueuePrintPreview.getQueue_print_preview_key());

            // コールバック関数
            K02HPrintPreviewCallbackProcessMultipleInventory callbackProc = new K02HPrintPreviewCallbackProcessMultipleInventory(
                    callbackInfo, cReportGenMultipleInventoryList);

            // ロケール切替
            String printCommandNumber = tmpQueuePrintPreview.getPrint_command_no();
            if (tmpQueuePrintPreview.getPrint_command_no().contains(CommonConstants.JOIN_LOCALE)) {
                String[] selectLocale = localeId.split(CommonConstants.SPLIT_CHAR);
                printCommandNumber = tmpQueuePrintPreview.getPrint_command_no().replace(CommonConstants.REPLACE_LOCALE,
                        selectLocale[0]);
            }

            //----------------------------------------------------------------
            // 印刷プレビュー依頼
            //----------------------------------------------------------------
            cReportGenMultipleInventoryList.Print(printCommandNumber, // 帳票様式番号
                    phvalue, // 印刷プレビューキー情報
                    printerNo, // プリンタID
                    1, // 1:PDF
                    callbackProc, // コールバック関数
                    tmpQueuePrintPreview.getPrint_times(), // 印刷部数
                    false // プリンターから印刷するかしないか
            );

        }
        catch (biz.grandsight.ex.rs_multiple21.CReportGenException e) {

            this.logHelper.severe(logger, CLASS_NAME, methodName, e.getMessage(), e);

        }
        return true;
    }

    /**
    *
    * 印刷プレビュー要求の実施を行う（多段）
    *
    * @param tmpQueuePrintPreview  印刷プレビューキューのエンティティ1件分
    * @param printerNo         リクエストにあったプリンタ番号
    * @param phvalue           印刷プレビューキー情報
    * @param em                エンティティマネージャー
    * @param localeId          ロケールＩＤ
    * @return
    * @throws GnomesAppException
    */
    private boolean previewRequestMainMultiStage(TmpQueuePrintPreview tmpQueuePrintPreview,
            HashMap<String, String> phvalue, String localeId) throws GnomesAppException
    {
        final String methodName = "printRequestMainMultiStage";

        // プリンタのIDを一時取得
        String printerNo = tmpQueuePrintPreview.getPrinter_id();

        // CReportGen（多段）初期化
        biz.grandsight.ex.rs_multistage.CReportGen cReportGenMultiStage = super.initCReportGenMultiStage(
                tmpQueuePrintPreview.getDb_area_div());

        try {

            // コールバック関数の登録
            PrintPreviewCallbackInfo callbackInfo = super.setCallbackInfo(tmpQueuePrintPreview.getPdf_file_name(),
                    PrintOutCallbackInfo.PRINT, null, tmpQueuePrintPreview.getPrint_times(), 1,
                    tmpQueuePrintPreview.getEvent_id(), tmpQueuePrintPreview.getRequest_seq(),
                    tmpQueuePrintPreview.getQueue_print_preview_key());

            // コールバック関数
            K02EPrintPreviewCallbackProcessMultiStage callbackProc = new K02EPrintPreviewCallbackProcessMultiStage(
                    callbackInfo, cReportGenMultiStage);

            // ロケール切替
            String printCommandNumber = tmpQueuePrintPreview.getPrint_command_no();
            if (tmpQueuePrintPreview.getPrint_command_no().contains(CommonConstants.JOIN_LOCALE)) {
                String[] selectLocale = localeId.split(CommonConstants.SPLIT_CHAR);
                printCommandNumber = tmpQueuePrintPreview.getPrint_command_no().replace(CommonConstants.REPLACE_LOCALE,
                        selectLocale[0]);
            }

            //----------------------------------------------------------------
            // 印刷プレビュー依頼
            //----------------------------------------------------------------
            cReportGenMultiStage.Print(printCommandNumber, // 帳票様式番号
                    phvalue, // 印刷プレビューキー情報
                    printerNo, // プリンタID
                    1, // 1:PDF
                    callbackProc, // コールバック関数
                    tmpQueuePrintPreview.getPrint_times(), // 印刷部数
                    false // プリンターから印刷するかしないか
            );

        }
        catch (biz.grandsight.ex.rs_multistage.CReportGenException e) {

            this.logHelper.severe(logger, CLASS_NAME, methodName, e.getMessage(), e);

        }
        return true;
    }

    /**
    *
    * 印刷プレビュー要求の実施を行う（多段キー変更改ページ無し）
    *
    * @param tmpQueuePrintPreview  印刷プレビューキューのエンティティ1件分
    * @param printerNo         リクエストにあったプリンタ番号
    * @param phvalue           印刷プレビューキー情報
    * @param localeId          ロケールＩＤ
    * @param em                エンティティマネージャー
    * @return
    * @throws GnomesAppException
    */
    private boolean previewRequestMainMultiStageNoNewPage(TmpQueuePrintPreview tmpQueuePrintPreview,
            HashMap<String, String> phvalue, String localeId) throws GnomesAppException
    {

        final String methodName = "printRequestMainMultiStageNoNewPage";

        // プリンタのIDを一時取得
        String printerNo = tmpQueuePrintPreview.getPrinter_id();

        // CReportGen（多段キー変更改ページ無し）初期化
        biz.grandsight.ex.rs_multistage41.CReportGen cReportGenMultiStageNoNewPage = super.initCReportGenMultiStageNoNewPage(
                tmpQueuePrintPreview.getDb_area_div());

        try {

            // コールバック関数の登録
            PrintPreviewCallbackInfo callbackInfo = super.setCallbackInfo(tmpQueuePrintPreview.getPdf_file_name(),
                    PrintOutCallbackInfo.PRINT, null, tmpQueuePrintPreview.getPrint_times(), 1,
                    tmpQueuePrintPreview.getEvent_id(), tmpQueuePrintPreview.getRequest_seq(),
                    tmpQueuePrintPreview.getQueue_print_preview_key());

            // コールバック関数
            K02FPrintPreviewCallbackProcessMultiStageNoNewPage callbackProc = new K02FPrintPreviewCallbackProcessMultiStageNoNewPage(
                    callbackInfo, cReportGenMultiStageNoNewPage);

            // ロケール切替
            String printCommandNumber = tmpQueuePrintPreview.getPrint_command_no();
            if (tmpQueuePrintPreview.getPrint_command_no().contains(CommonConstants.JOIN_LOCALE)) {
                String[] selectLocale = localeId.split(CommonConstants.SPLIT_CHAR);
                printCommandNumber = tmpQueuePrintPreview.getPrint_command_no().replace(CommonConstants.REPLACE_LOCALE,
                        selectLocale[0]);
            }

            //----------------------------------------------------------------
            // 印刷プレビュー依頼
            //----------------------------------------------------------------
            cReportGenMultiStageNoNewPage.Print(printCommandNumber, // 帳票様式番号
                    phvalue, // 印刷プレビューキー情報
                    printerNo, // プリンタID
                    1, // 1:PDF
                    callbackProc, // コールバック関数
                    tmpQueuePrintPreview.getPrint_times(), // 印刷部数
                    false // プリンターから印刷するかしないか
            );

        }
        catch (biz.grandsight.ex.rs_multistage41.CReportGenException e) {

            this.logHelper.severe(logger, CLASS_NAME, methodName, e.getMessage(), e);

        }
        return true;
    }

    /**
    *
    * 印刷プレビュー要求の実施を行う（多重多段）
    *
    * @param tmpQueuePrintPreview  印刷プレビューキューのエンティティ1件分
    * @param printerNo         リクエストにあったプリンタ番号
    * @param phvalue           印刷プレビューキー情報
    * @param em                エンティティマネージャー
    * @param localeId          ロケールＩＤ
    * @return
    * @throws GnomesAppException
    */
    private boolean previewRequestMainMultipleMultiStage(TmpQueuePrintPreview tmpQueuePrintPreview,
            HashMap<String, String> phvalue, String localeId) throws GnomesAppException
    {
        final String methodName = "printRequestMainMultipleMultiStage";

        // プリンタのIDを一時取得
        String printerNo = tmpQueuePrintPreview.getPrinter_id();

        // CReportGen（多重多段）初期化
        biz.grandsight.ex.rs_multiplemultistage.CReportGen cReportGenMultipleMultiStage = super.initCReportGenMultipleMultiStage(
                tmpQueuePrintPreview.getDb_area_div());

        try {

            // コールバック関数の登録
            PrintPreviewCallbackInfo callbackInfo = super.setCallbackInfo(tmpQueuePrintPreview.getPdf_file_name(),
                    PrintOutCallbackInfo.PRINT, null, tmpQueuePrintPreview.getPrint_times(), 1,
                    tmpQueuePrintPreview.getEvent_id(), tmpQueuePrintPreview.getRequest_seq(),
                    tmpQueuePrintPreview.getQueue_print_preview_key());

            // コールバック関数
            K02GPrintPreviewCallbackProcessMultipleMultiStage callbackProc = new K02GPrintPreviewCallbackProcessMultipleMultiStage(
                    callbackInfo, cReportGenMultipleMultiStage);

            // ロケール切替
            String printCommandNumber = tmpQueuePrintPreview.getPrint_command_no();
            if (tmpQueuePrintPreview.getPrint_command_no().contains(CommonConstants.JOIN_LOCALE)) {
                String[] selectLocale = localeId.split(CommonConstants.SPLIT_CHAR);
                printCommandNumber = tmpQueuePrintPreview.getPrint_command_no().replace(CommonConstants.REPLACE_LOCALE,
                        selectLocale[0]);
            }

            //----------------------------------------------------------------
            // 印刷プレビュー依頼
            //----------------------------------------------------------------
            cReportGenMultipleMultiStage.Print(printCommandNumber, // 帳票様式番号
                    phvalue, // 印刷プレビューキー情報
                    printerNo, // プリンタID
                    1, // 1:PDF
                    callbackProc, // コールバック関数
                    tmpQueuePrintPreview.getPrint_times(), // 印刷部数
                    false // プリンターから印刷するかしないか
            );

        }
        catch (biz.grandsight.ex.rs_multiplemultistage.CReportGenException e) {

            this.logHelper.severe(logger, CLASS_NAME, methodName, e.getMessage(), e);

        }
        return true;
    }
}
