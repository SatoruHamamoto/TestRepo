package com.gnomes.rest.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.gnomes.common.constants.CommonEnums.PrintoutStatus;
import com.gnomes.common.constants.SystemDefConstants;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.util.FileUtils;
import com.gnomes.system.dao.HistoryPrintoutDao;
import com.gnomes.system.dao.HistoryPrintoutFileDao;
import com.gnomes.system.dao.MstrSystemDefineDao;
import com.gnomes.system.data.printout.PrintOutCallbackInfo;
import com.gnomes.system.data.printout.PrintOutInfo;
import com.gnomes.system.entity.HistoryPrintout;
import com.gnomes.system.entity.HistoryPrintoutFile;
import com.gnomes.system.entity.MstrSystemDefine;

/**
 * 帳票印刷コールバックサービス処理
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/04/04 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Path("K01CPrintOutCallbackService")
@RequestScoped
public class K01CPrintOutCallbackService extends BaseService {

    /** 帳票ラベル印字履歴ファイル登録フラグ */
    private static final String HISTORY_PRINTOUT_FILE_INSERT_FLAG = "1";

    /** エンティティマネージャーファクトリ（通常領域） */
    @PersistenceUnit(unitName = CommonConstants.PERSISTENCE_UNIT_NAME_NON_JTA)
    private EntityManagerFactory emf;

    /** 帳票ラベル印字履歴 Dao */
    @Inject
    protected HistoryPrintoutDao historyPrintoutDao;

    /** 帳票ラベル印字履歴ファイル Dao */
    @Inject
    protected HistoryPrintoutFileDao historyPrintoutFileDao;

    /** システム定義 Dao */
    @Inject
    protected MstrSystemDefineDao mstrSystemDefineDao;

    /**
     * 帳票印刷コールバック処理.
     * @param callbackInfo 帳票印刷コールバック情報
     * @throws GnomesAppException
     */
    @Path("process")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void process(PrintOutCallbackInfo callbackInfo) throws GnomesAppException {

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
                      super.exceptionFactory.createGnomesAppException(
                              null, GnomesMessagesConstants.ME01_0168,
                              new Object[]{callbackInfo.getReportId(), callbackInfo.getPrintErrorMsg()}));
                // ログ出力
                super.logHelper.severe(super.logger, null, null, errMessage);

                printOutInfo.setErrMessage(errMessage);

            }

            // 電子帳票情報の取得
            this.getPrintOutInfo(callbackInfo, printOutInfo);
            // 帳票ラベル印字履歴更新
            String historyPrintoutKey = this.updateHistoryPrintout(
                    callbackInfo.getReportId(), printOutInfo, em);

            MstrSystemDefine mstrSystemDefine = this.mstrSystemDefineDao.getMstrSystemDefine(
                    SystemDefConstants.TYPE_HISTORY_PRINTOUT_FILE,
                    SystemDefConstants.CODE_HISTORY_PRINTOUT_FILE_INSERT_FLAG);

            boolean insertFlag = false;

            if ((!Objects.isNull(mstrSystemDefine))
                    && HISTORY_PRINTOUT_FILE_INSERT_FLAG.equals(mstrSystemDefine.getChar1())) {
                insertFlag = true;
            }

            if (PrintOutCallbackInfo.PRINT.equals(callbackInfo.getPrintType())
                    || (PrintOutCallbackInfo.REPRINT.equals(callbackInfo.getPrintType()) && insertFlag)) {

                // 帳票ラベル印字履歴ファイル登録
                if(! StringUtil.isNullOrEmpty(historyPrintoutKey)){
                    this.insertHistoryPrintoutFile(historyPrintoutKey, printOutInfo, callbackInfo, em);
                }

            }
            // コミット
            transaction.commit();

            // 印刷用一時ディレクトリを削除
            StringBuilder directoryPath = new StringBuilder();
            directoryPath.append(this.getPathName("target-directory"));
            directoryPath.append(File.separator);
            directoryPath.append(callbackInfo.getReportId().replace(".xlsx", ""));
            File pathName = new File(directoryPath.toString());
            try {
                org.apache.commons.io.FileUtils.deleteDirectory(pathName);
            } catch (Exception e) {
                // ログ出力
                String errMessage = MessagesHandler.getExceptionMessage(super.req,
                        super.exceptionFactory.createGnomesAppException(
                                null, GnomesMessagesConstants.ME01_0192,
                                new Object[]{directoryPath.toString(), e.getMessage()}));

                super.logHelper.severe(super.logger, null, null, errMessage);

                if (StringUtil.isNullOrEmpty(printOutInfo.getErrMessage())) {
                    printOutInfo.setErrMessage(errMessage);
                }
            }

            // テンプレートファイルを削除
            StringBuilder filePath = new StringBuilder();
            filePath.append(this.getPathName("template-directory"));
            filePath.append(File.separator);
            String[] prntCommandNo= printOutInfo.getExcelFileName().split("_");
            if (prntCommandNo[2].equals(callbackInfo.getLanguage())) {
                filePath.append(prntCommandNo[1] + CommonConstants.SPLIT_CHAR + prntCommandNo[2] + CommonConstants.SPLIT_CHAR + prntCommandNo[3]);
            } else {
                filePath.append(prntCommandNo[1]);
            }
            File fileName = new File(filePath.toString());

            try {
                org.apache.commons.io.FileUtils.forceDelete(fileName);
            } catch (Exception e) {
                // ログ出力
                String errMessage = MessagesHandler.getExceptionMessage(super.req,
                        super.exceptionFactory.createGnomesAppException(
                                null, GnomesMessagesConstants.ME01_0192,
                                new Object[]{filePath.toString(), e.getMessage()}));

                super.logHelper.severe(super.logger, null, null, errMessage);

                if (StringUtil.isNullOrEmpty(printOutInfo.getErrMessage())) {
                    printOutInfo.setErrMessage(errMessage);
                }
            }
        } catch (Exception e) {
            // ロールバック
            transaction.rollback();
            // 帳票ラベル印字処理においてエラーが発生しました。 (ReportID {0}、エラー内容：{1})
            // ログ出力
            super.logHelper.severe(super.logger, null, null, e.getMessage(), e);

            throw e;

        } finally {

            if (!Objects.isNull(em) && em.isOpen()) {
                em.close();
            }


        }

    }

    /**
     * 電子帳票情報の取得
     * @param callbackInfo 帳票印刷コールバック情報
     * @param printOutInfo 電子帳票情報
     * @throws GnomesAppException
     */
    private void getPrintOutInfo(PrintOutCallbackInfo callbackInfo, PrintOutInfo printOutInfo) throws GnomesAppException {

        // ファイル名の確認
        checkFileName(callbackInfo);

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
     * ファイル名のチェック
     * @param callbackInfo 帳票印刷コールバック情報
     * @throws GnomesAppException
     */
    private void checkFileName(PrintOutCallbackInfo callbackInfo) throws GnomesAppException {

        boolean reportIdMatched = false;

        // ReportIdに不必要な特殊文字（バックスラッシュもしくはスラッシュ）が入っていないか確認
        if (callbackInfo.getReportId().matches("(.*)\\\\(.*)") || callbackInfo.getReportId().matches("(.*)/(.*)")) {
            throw exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0001, callbackInfo.getReportId());
        }

        // PdfFileNameに不必要な特殊文字（バックスラッシュもしくはスラッシュ）が入っていないか確認
        if (callbackInfo.getPdfFileName().matches("(.*)\\\\(.*)") || callbackInfo.getPdfFileName().matches("(.*)/(.*)")) {
            throw exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0001, callbackInfo.getPdfFileName());
        }

        // プリンタIDリストの取得
        List<String> printersList = new ArrayList<String>();
        printersList = this.getPrinterslist("printers-list");

        // ReportIDにプリンタIDが含まれているか確認
        for(String printerRegex : printersList){
            Pattern reportIdPtn = Pattern.compile(printerRegex);
            Matcher reportIdMat = reportIdPtn.matcher(callbackInfo.getReportId());
            if (reportIdMat.find()) {
                reportIdMatched = true;
                break;
            }
        }

        // ReportIdが不正の場合、例外をスロー
        if (reportIdMatched == false) {
            throw exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0001, callbackInfo.getReportId());
        }

        // PDFファイル名の確認
        String[] reportIdRegex = callbackInfo.getReportId().split(CommonConstants.SPLIT_CHAR);
        Pattern reportIdPtn = Pattern.compile(reportIdRegex[1]);
        Matcher pdfFileNameMat = reportIdPtn.matcher(callbackInfo.getPdfFileName());

        // PDFファイル名にReportIdのイベントIDが含まれない場合、例外をスロー
        if (pdfFileNameMat.find() == false) {
            throw exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0001, callbackInfo.getPdfFileName());
        }
    }

    /**
     * ファイル移動
     * @param callbackInfo 帳票印刷コールバック情報
     * @param printOutInfo 電子帳票情報
     */
    private boolean moveFile(PrintOutCallbackInfo callbackInfo, PrintOutInfo printOutInfo) {

        // 移動先ディレクトリパス
        StringBuilder directoryPathTo = new StringBuilder();
        directoryPathTo.append(this.getPathName("target-directory"));
        directoryPathTo.append(File.separator);
        directoryPathTo.append(callbackInfo.getReportId().replace(".xlsx", ""));
        printOutInfo.setDirectoryPathTo(directoryPathTo.toString());

        try {
            // ディレクトリ作成
            FileUtils.makeDirectory(directoryPathTo.toString());
            // Excelファイル移動
            FileUtils.move(this.getPathName("target-directory"), directoryPathTo.toString(), printOutInfo.getExcelFileName());
            // PDFファイル移動
            FileUtils.move(this.getPathName("target-directory"), directoryPathTo.toString(), printOutInfo.getPdfFileNameFrom());
            // PDFファイル名変更
            FileUtils.rename(directoryPathTo.toString(), printOutInfo.getPdfFileNameFrom(), printOutInfo.getPdfFileName());

        } catch (GnomesAppException e) {

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
    private void getFileInfo(PrintOutInfo printOutInfo) {

        try {
            // Excelファイル内容取得
            printOutInfo.setExcelFileInfo(FileUtils.readFileToByte(
                    this.getFilePath(printOutInfo.getDirectoryPathTo(), printOutInfo.getExcelFileName())));

        } catch (Exception e) {
            // 再印字対象の電子帳票の取得が失敗しました。 (ファイル名: {0}、エラー内容：{1})
            String errMessage = MessagesHandler.getExceptionMessage(super.req,
                    super.exceptionFactory.createGnomesAppException(
                            null, GnomesMessagesConstants.ME01_0167,
                            new Object[]{printOutInfo.getExcelFileName(), e.getMessage()}));

            // ログ出力
            super.logHelper.severe(super.logger, null, null, errMessage);

            printOutInfo.setErrMessage(errMessage);

        }

        try {
            // PDFファイル内容取得
            printOutInfo.setPdfFileInfo(FileUtils.readFileToByte(
                    this.getFilePath(printOutInfo.getDirectoryPathTo(), printOutInfo.getPdfFileName())));
        } catch (Exception e) {
            // 再印字対象の電子帳票の取得が失敗しました。 (ファイル名: {0}、エラー内容：{1})
            String errMessage = MessagesHandler.getExceptionMessage(super.req,
                    super.exceptionFactory.createGnomesAppException(
                            null, GnomesMessagesConstants.ME01_0167,
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
    private void deleteDirectory(PrintOutInfo printOutInfo) {

        try {
            FileUtils.delete(printOutInfo.getDirectoryPathTo());
        } catch (GnomesAppException e) {
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
    private String getFilePath(String directoryPath, String fileName) {

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
    private String updateHistoryPrintout(String reportId, PrintOutInfo printOutInfo, EntityManager em) throws GnomesAppException {

        HistoryPrintout entity = this.historyPrintoutDao.getConditionReportId(reportId, em);

        //データが無い場合はアップデートしない
        if(entity == null){
            return null;
        }

        if (StringUtil.isNullOrEmpty(printOutInfo.getErrMessage())) {
            // 帳票印字結果状態
            entity.setPrintout_status(PrintoutStatus.NORMAL.getValue());
            // EXCEL電子ファイル名
            entity.setExcel_file_name(printOutInfo.getExcelFileName());
        } else {
            // 帳票印字結果状態
            entity.setPrintout_status(PrintoutStatus.ERROR.getValue());
            // 印字エラーメッセージ
            entity.setPrintout_error_message(
                    this.editPrintoutErrorMessage(printOutInfo.getErrMessage()));
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
    private void insertHistoryPrintoutFile(String historyPrintoutKey, PrintOutInfo printOutInfo, PrintOutCallbackInfo callbackInfo, EntityManager em) {

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
    private String editPrintoutErrorMessage(String message) {

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
     * @param elementsByTagName XMLエレメントタグ名
     * @return パス名
     */
    protected String getPathName(String elementsByTagName) {

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

        } catch (Exception e) {

            String message = MessagesHandler.getExceptionMessage(super.req,
                    super.exceptionFactory.createGnomesAppException(
                            null, GnomesMessagesConstants.ME01_0001,
                            new Object[10]));

            this.logHelper.severe(super.logger, null, null, message, e);

        }

        return "";

    }

    /**
     * プリンタリスト取得
     * @param elementsByTagName XMLエレメントタグ名
     * @return プリンタリスト
     */
    protected List<String> getPrinterslist(String elementsByTagName) {

        List<String> printersList = new ArrayList<String>();
        try {

            // RSのconfig.xmlからプリンタリストを取得
            DocumentBuilder configXml = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document xMLSetting = configXml.parse(super.gnomesSystemBean.getReportDefinitionXMLFileName());
            NodeList printers = xMLSetting.getElementsByTagName(elementsByTagName).item(0).getChildNodes();;
            for (int i = 0; i < printers.getLength(); ++i) {
                if (printers.item(i).getNodeName().equalsIgnoreCase("printer")) {
                    NodeList printer = printers.item(i).getChildNodes();
                    for (int j = 0; j < printer.getLength(); j++) {
                        if (printer.item(j).getNodeName().equalsIgnoreCase("id")) {
                            String printerId = printer.item(j).getTextContent();
                            printersList.add(printerId);
                        }
                    }
                }
            }

            return printersList;

        } catch (Exception e) {

            String message = MessagesHandler.getExceptionMessage(super.req,
                    super.exceptionFactory.createGnomesAppException(
                            null, GnomesMessagesConstants.ME01_0001,
                            new Object[10]));

            this.logHelper.severe(super.logger, null, null, message, e);

        }

        return printersList;

    }

}
