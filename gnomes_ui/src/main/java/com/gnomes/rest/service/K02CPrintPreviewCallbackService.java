package com.gnomes.rest.service;

import java.io.File;

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
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.util.FileUtils;
import com.gnomes.system.dao.HistoryPrintoutDao;
import com.gnomes.system.dao.HistoryPrintoutFileDao;
import com.gnomes.system.dao.MstrSystemDefineDao;
import com.gnomes.system.dao.TmpQueuePrintPreviewDao;
import com.gnomes.system.data.printout.PrintOutInfo;
import com.gnomes.system.data.printout.PrintPreviewCallbackInfo;
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
@Path("K02CPrintPreviewCallbackService")
@RequestScoped
public class K02CPrintPreviewCallbackService extends BaseService
{

    /** エンティティマネージャーファクトリ（通常領域） */
    @PersistenceUnit(unitName = CommonConstants.PERSISTENCE_UNIT_NAME_NON_JTA)
    private EntityManagerFactory      emf;

    /** 帳票ラベル印刷履歴 Dao */
    @Inject
    protected HistoryPrintoutDao      historyPrintoutDao;

    /** 帳票ラベル印刷履歴ファイル Dao */
    @Inject
    protected HistoryPrintoutFileDao  historyPrintoutFileDao;

    /** システム定義 Dao */
    @Inject
    protected MstrSystemDefineDao     mstrSystemDefineDao;

    /** 印刷プレビューキュー Dao */
    @Inject
    protected TmpQueuePrintPreviewDao tmpQueuePrintPreviewDao;

    /** Excelファイル拡張子 */
    private static final String       EXCEL_EXTENSION = ".xlsx";

    /** PDFファイル拡張子 */
    private static final String       PDF_EXTENSION   = ".pdf";

    /**
     * 帳票印刷プレビューコールバック処理.
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

                // 帳票ラベル印刷プレビュー処理においてエラーが発生しました。 (ReportID {0}、エラー内容：{1})
                String errMessage = MessagesHandler.getExceptionMessage(super.req,
                        super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0168,
                                new Object[]{callbackInfo.getReportId(), callbackInfo.getPrintErrorMsg()}));
                // ログ出力
                super.logHelper.severe(super.logger, null, null, errMessage);

                printOutInfo.setErrMessage(errMessage);

            }

            // 電子帳票情報の取得
            this.getPrintOutInfo(callbackInfo, printOutInfo);

            // テンプレートファイルを削除
            StringBuilder filePath = new StringBuilder();
            filePath.append(this.getPathName("template-directory"));
            filePath.append(File.separator);
            String[] prntCommandNo = printOutInfo.getExcelFileName().split("_");
            filePath.append(prntCommandNo[1]);
            File fileName = new File(filePath.toString());

            try {
                org.apache.commons.io.FileUtils.forceDelete(fileName);
            }
            catch (Exception e) {
                // ログ出力
                String errMessage = MessagesHandler.getExceptionMessage(super.req,
                        super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0192,
                                new Object[]{filePath.toString(), e.getMessage()}));

                super.logHelper.severe(super.logger, null, null, errMessage);

                if (StringUtil.isNullOrEmpty(printOutInfo.getErrMessage())) {
                    printOutInfo.setErrMessage(errMessage);
                }
            }

            // 印刷プレビューキュー取得
            TmpQueuePrintPreview tmpUpdate = this.tmpQueuePrintPreviewDao.getQueueState(
                    callbackInfo.getQueuePrintPreviewKey(), em);
            tmpUpdate.setPrint_preview_status(CommonEnums.PrintPreviewStatus.PREVIEW_END.getValue());
            tmpUpdate.setReport_id(callbackInfo.getReportId());
            this.tmpQueuePrintPreviewDao.update(tmpUpdate, em);

            // コミット
            transaction.commit();

        }
        catch (Exception e) {
            // ロールバック
            transaction.rollback();
            // 帳票ラベル印刷処理においてエラーが発生しました。 (ReportID {0}、エラー内容：{1})
            // ログ出力
            super.logHelper.severe(super.logger, null, null, e.getMessage(), e);

            throw e;

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
        printOutInfo.setPdfFileNameFrom(callbackInfo.getReportId().replace(EXCEL_EXTENSION, PDF_EXTENSION));
        // PDFファイル名
        printOutInfo.setPdfFileName(callbackInfo.getPdfFileName());
        // ファイル移動
        this.moveFile(callbackInfo, printOutInfo);

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
        directoryPathTo.append(this.getPathName("preview-directory"));
        directoryPathTo.append(File.separator);
        directoryPathTo.append(callbackInfo.getReportId().replace(EXCEL_EXTENSION, ""));
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
