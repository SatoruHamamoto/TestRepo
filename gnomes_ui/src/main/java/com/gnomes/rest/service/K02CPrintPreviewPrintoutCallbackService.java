package com.gnomes.rest.service;

import java.io.File;
import java.util.Objects;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.picketbox.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums;
import com.gnomes.common.constants.CommonEnums.PrintoutStatus;
import com.gnomes.common.constants.SystemDefConstants;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.util.FileUtils;
import com.gnomes.system.dao.HistoryPrintoutDao;
import com.gnomes.system.dao.HistoryPrintoutFileDao;
import com.gnomes.system.dao.MstrSystemDefineDao;
import com.gnomes.system.dao.TmpQueuePrintPreviewDao;
import com.gnomes.system.data.printout.PrintOutCallbackInfo;
import com.gnomes.system.data.printout.PrintOutInfo;
import com.gnomes.system.data.printout.PrintPreviewCallbackInfo;
import com.gnomes.system.entity.HistoryPrintout;
import com.gnomes.system.entity.HistoryPrintoutFile;
import com.gnomes.system.entity.MstrSystemDefine;
import com.gnomes.system.entity.TmpQueuePrintPreview;

/**
 * 帳票印刷プレビューコールバックサービス処理
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2021/12/02 YJP-D/Jixin.Sun           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Path("K02CPrintPreviewPrintoutCallbackService")
@RequestScoped
public class K02CPrintPreviewPrintoutCallbackService extends BaseService
{

    /** 帳票ラベル印字履歴ファイル登録フラグ */
    private static final String       HISTORY_PRINTOUT_FILE_INSERT_FLAG = "1";

    /** エンティティマネージャーファクトリ（通常領域） */
    @PersistenceUnit(unitName = CommonConstants.PERSISTENCE_UNIT_NAME_NON_JTA)
    private EntityManagerFactory      emf;

    /** 帳票ラベル印字履歴 Dao */
    @Inject
    protected HistoryPrintoutDao      historyPrintoutDao;

    /** 帳票ラベル印字履歴ファイル Dao */
    @Inject
    protected HistoryPrintoutFileDao  historyPrintoutFileDao;

    /** システム定義 Dao */
    @Inject
    protected MstrSystemDefineDao     mstrSystemDefineDao;

    /** 印刷プレビューキュー Dao */
    @Inject
    protected TmpQueuePrintPreviewDao tmpQueuePrintPreviewDao;

    /**
     * 帳票印刷プレビューの印刷コールバックサービス処理
     * @param callbackInfo 帳票印刷プレビューコールバック情報
     * @throws GnomesAppException
     */
    @Path("process")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void process(PrintPreviewCallbackInfo callbackInfo) throws GnomesAppException
    {

        EntityManager em = this.emf.createEntityManager();
        Session session = em.unwrap(Session.class);
        // トランザクション開始
        Transaction transaction = session.beginTransaction();

        // 電子帳票情報
        PrintOutInfo printOutInfo = new PrintOutInfo();

        try {
            // リクエスト情報設定
            this.setRequest(callbackInfo);

            if (!StringUtil.isNullOrEmpty(callbackInfo.getPrintErrorMsg())) {

                // 帳票ラベル印字処理においてエラーが発生しました。 (ReportID {0}、エラー内容：{1})
                String errMessage = MessagesHandler.getExceptionMessage(super.req,
                        super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0168,
                                new Object[]{callbackInfo.getReportId(), callbackInfo.getPrintErrorMsg()}));
                // ログ出力
                super.logHelper.severe(super.logger, null, null, errMessage);

                printOutInfo.setErrMessage(errMessage);

            }

            // 電子帳票情報の取得
            this.getPrintOutInfo(callbackInfo, printOutInfo);
            // 帳票ラベル印字履歴更新
            String historyPrintoutKey = this.updateHistoryPrintout(callbackInfo.getReportId(), printOutInfo, em);

            MstrSystemDefine mstrSystemDefine = this.mstrSystemDefineDao.getMstrSystemDefine(
                    SystemDefConstants.TYPE_HISTORY_PRINTOUT_FILE,
                    SystemDefConstants.CODE_HISTORY_PRINTOUT_FILE_INSERT_FLAG);

            boolean insertFlag = false;

            if ((!Objects.isNull(mstrSystemDefine)) && HISTORY_PRINTOUT_FILE_INSERT_FLAG.equals(
                    mstrSystemDefine.getChar1())) {
                insertFlag = true;
            }

            if (PrintOutCallbackInfo.PRINT.equals(callbackInfo.getPrintType()) || (PrintOutCallbackInfo.REPRINT.equals(
                    callbackInfo.getPrintType()) && insertFlag)) {

                // 帳票ラベル印字履歴ファイル登録
                if (!StringUtil.isNullOrEmpty(historyPrintoutKey)) {
                    this.insertHistoryPrintoutFile(historyPrintoutKey, printOutInfo, callbackInfo, em);
                }

            }

            // 印字プレビューキュー取得
            TmpQueuePrintPreview tmpUpdate = this.tmpQueuePrintPreviewDao.getQueueState(
                    callbackInfo.getQueuePrintPreviewKey(), em);
            tmpUpdate.setPrint_preview_status(CommonEnums.PrintPreviewStatus.PRINT_END.getValue());
            this.tmpQueuePrintPreviewDao.update(tmpUpdate, em);
            // コミット
            transaction.commit();

        }
        catch (Exception e) {
            // ロールバック
            transaction.rollback();
            // 帳票ラベル印字処理においてエラーが発生しました。 (ReportID {0}、エラー内容：{1})
            // ログ出力
            super.logHelper.severe(super.logger, null, null, e.getMessage(), e);

            throw e;

        }
        finally {

            if (!Objects.isNull(em) && em.isOpen()) {
                em.close();
            }

            // 印刷用一時ディレクトリを削除
            StringBuilder temporaryPath = new StringBuilder();
            temporaryPath.append(callbackInfo.getPathName());
            temporaryPath.append(File.separator);
            temporaryPath.append(callbackInfo.getRePrintFileName().replace(".xlsx", ""));
            FileUtils.makeDirectory(temporaryPath.toString());
            // 印刷用一時ディレクトリを移動
            FileUtils.move(callbackInfo.getPathName(), temporaryPath.toString(), callbackInfo.getRePrintFileName());

            File pathName = new File(temporaryPath.toString());
            try {
                org.apache.commons.io.FileUtils.deleteDirectory(pathName);
            }
            catch (Exception e) {
                // ログ出力
                String errMessage = MessagesHandler.getExceptionMessage(super.req,
                        super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0192,
                                new Object[]{temporaryPath.toString(), e.getMessage()}));

                super.logHelper.severe(super.logger, null, null, errMessage);

                if (StringUtil.isNullOrEmpty(printOutInfo.getErrMessage())) {
                    printOutInfo.setErrMessage(errMessage);
                }
            }
        }
    }

    /**
     * 電子帳票情報の取得
     * @param callbackInfo 帳票印刷コールバック情報
     * @param printOutInfo 電子帳票情報
     * @throws GnomesAppException
     */
    private void getPrintOutInfo(PrintPreviewCallbackInfo callbackInfo, PrintOutInfo printOutInfo)
    {

        // Excelファイル名
        printOutInfo.setExcelFileName(callbackInfo.getReportId());
        // PDFファイル名(リネーム前)
        printOutInfo.setPdfFileNameFrom(callbackInfo.getReportId().replace(".xlsx", ".pdf"));
        // PDFファイル名
        printOutInfo.setPdfFileName(callbackInfo.getPdfFileName());
        // ファイル移動
        boolean success = this.moveFile(callbackInfo, printOutInfo);

        if (success) {
            // ファイル内容取得
            this.getFileInfo(printOutInfo);
            // 作成ディレクトリ削除
            this.deleteDirectory(printOutInfo);

        }

    }

    /**
     * ファイル移動
     * @param callbackInfo 帳票印刷コールバック情報
     * @param printOutInfo 電子帳票情報
     */
    private boolean moveFile(PrintPreviewCallbackInfo callbackInfo, PrintOutInfo printOutInfo)
    {

        // 移動先ディレクトリパス
        StringBuilder directoryPathTo = new StringBuilder();
        directoryPathTo.append(callbackInfo.getPathName());
        directoryPathTo.append(File.separator);
        directoryPathTo.append(callbackInfo.getReportId().replace(".xlsx", ""));
        printOutInfo.setDirectoryPathTo(directoryPathTo.toString());

        try {
            // ディレクトリ作成
            FileUtils.makeDirectory(directoryPathTo.toString());
            // Excelファイル移動
            FileUtils.move(callbackInfo.getPathName(), directoryPathTo.toString(), printOutInfo.getExcelFileName());
            // PDFファイル移動
            FileUtils.move(callbackInfo.getPathName(), directoryPathTo.toString(), printOutInfo.getPdfFileNameFrom());
            // PDFファイル名変更
            FileUtils.rename(directoryPathTo.toString(), printOutInfo.getPdfFileNameFrom(),
                    printOutInfo.getPdfFileName());

        }
        catch (GnomesAppException e) {

            String errMessage = MessagesHandler.getExceptionMessage(super.req, e);
            super.logHelper.severe(super.logger, null, null, errMessage);

            if (StringUtil.isNullOrEmpty(printOutInfo.getErrMessage())) {
                printOutInfo.setErrMessage(errMessage);
            }
            return false;

        }

        return true;

    }

    /**
     * ファイル内容取得
     * @param printOutInfo 電子帳票情報
     */
    private void getFileInfo(PrintOutInfo printOutInfo)
    {

        try {
            // Excelファイル内容取得
            printOutInfo.setExcelFileInfo(FileUtils.readFileToByte(this.getFilePath(printOutInfo.getDirectoryPathTo(),
                    printOutInfo.getExcelFileName())));

        }
        catch (Exception e) {
            // 再印字対象の電子帳票の取得が失敗しました。 (ファイル名: {0}、エラー内容：{1})
            String errMessage = MessagesHandler.getExceptionMessage(super.req,
                    super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0167,
                            new Object[]{printOutInfo.getExcelFileName(), e.getMessage()}));

            // ログ出力
            super.logHelper.severe(super.logger, null, null, errMessage);

            printOutInfo.setErrMessage(errMessage);

        }

        try {
            // PDFファイル内容取得
            printOutInfo.setPdfFileInfo(FileUtils.readFileToByte(this.getFilePath(printOutInfo.getDirectoryPathTo(),
                    printOutInfo.getPdfFileName())));
        }
        catch (Exception e) {
            // 再印字対象の電子帳票の取得が失敗しました。 (ファイル名: {0}、エラー内容：{1})
            String errMessage = MessagesHandler.getExceptionMessage(super.req,
                    super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0167,
                            new Object[]{printOutInfo.getPdfFileName(), e.getMessage()}));

            // ログ出力
            super.logHelper.severe(super.logger, null, null, errMessage);

            if (StringUtil.isNullOrEmpty(printOutInfo.getErrMessage())) {
                printOutInfo.setErrMessage(errMessage);
            }

        }

    }

    /**
     * ディレクトリ削除
     * @param printOutInfo 電子帳票情報
     */
    private void deleteDirectory(PrintOutInfo printOutInfo)
    {

        try {
            FileUtils.delete(printOutInfo.getDirectoryPathTo());
        }
        catch (GnomesAppException e) {
            // ログ出力
            String errMessage = MessagesHandler.getExceptionMessage(super.req, e);
            super.logHelper.severe(super.logger, null, null, errMessage);

            if (StringUtil.isNullOrEmpty(printOutInfo.getErrMessage())) {
                printOutInfo.setErrMessage(errMessage);
            }

        }

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
     * 帳票ラベル印字履歴更新
     * @param reportId ReportID
     * @param printOutInfo 電子帳票情報
     * @param em エンティティマネージャー(通常領域固定)
     * @param 帳票ラベル印字履歴キー
     * @throws GnomesAppException
     */
    private String updateHistoryPrintout(String reportId, PrintOutInfo printOutInfo, EntityManager em)
            throws GnomesAppException
    {

        HistoryPrintout entity = this.historyPrintoutDao.getConditionReportId(reportId, em);

        // データが無い場合はアップデートしない
        if (entity == null) {
            return null;
        }

        if (StringUtil.isNullOrEmpty(printOutInfo.getErrMessage())) {
            // 帳票印字結果状態
            entity.setPrintout_status(PrintoutStatus.NORMAL.getValue());
            // EXCEL電子ファイル名
            entity.setExcel_file_name(printOutInfo.getExcelFileName());
        }
        else {
            // 帳票印字結果状態
            entity.setPrintout_status(PrintoutStatus.ERROR.getValue());
            // 印字エラーメッセージ
            entity.setPrintout_error_message(this.editPrintoutErrorMessage(printOutInfo.getErrMessage()));
        }

        this.historyPrintoutDao.update(entity, em);

        return entity.getHistory_printout_key();

    }

    /**
     * 帳票ラベル印字履歴ファイル登録
     * @param historyPrintoutKey 帳票ラベル印字履歴キー
     * @param printOutInfo 電子帳票情報
     * @param em エンティティマネージャー
     */
    private void insertHistoryPrintoutFile(String historyPrintoutKey, PrintOutInfo printOutInfo,
            PrintPreviewCallbackInfo callbackInfo, EntityManager em)
    {

        HistoryPrintoutFile historyPrintoutFile = new HistoryPrintoutFile();

        // 帳票ラベル印字履歴ファイルキー
        historyPrintoutFile.setHistory_printout_file_key(UUID.randomUUID().toString());
        // 帳票ラベル印字履歴キー (FK)
        historyPrintoutFile.setHistory_printout_key(historyPrintoutKey);
        // nk要求イベントID
        historyPrintoutFile.setEvent_id(callbackInfo.getRequestEventId());
        // nk要求内連番
        historyPrintoutFile.setRequest_seq(callbackInfo.getRequestSeq());
        // PDF電子ファイル
        historyPrintoutFile.setPdf_report_file(printOutInfo.getPdfFileInfo());
        // EXCEL電子ファイル
        historyPrintoutFile.setExcel_report_file(printOutInfo.getExcelFileInfo());

        this.historyPrintoutFileDao.insert(historyPrintoutFile, em);

    }

    /**
     * 印字エラーメッセージ編集
     * <pre>
     * 印字エラーメッセージが2000文字を超える場合、
     * 2000文字目までを返却する。
     * </pre>
     *
     * @param message 印字エラーメッセージ
     * @return 変種後印字エラーメッセージ
     */
    private String editPrintoutErrorMessage(String message)
    {

        if (StringUtil.isNullOrEmpty(message)) {
            return message;
        }

        if (message.length() > 2000) {
            return message.substring(0, 2000);
        }

        return message;
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

}
