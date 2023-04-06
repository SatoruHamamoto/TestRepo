package com.gnomes.system.logic;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.picketbox.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.gnomes.common.constants.CommonEnums;
import com.gnomes.common.data.MessageData;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.logic.BaseLogic;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.system.dao.HistoryPrintoutDao;
import com.gnomes.system.dao.HistoryPrintoutParameterDao;
import com.gnomes.system.dao.TmpQueuePrintPreviewDao;
import com.gnomes.system.dao.TmpQueuePrintPreviewParameterDao;
import com.gnomes.system.data.printout.PrintPreviewCallbackInfo;
import com.gnomes.system.entity.TmpQueuePrintout;
import com.gnomes.uiservice.ContainerRequest;

import biz.grandsight.ex.rs.CGenReportMeta;
import biz.grandsight.ex.rs.CReportGen;
import biz.grandsight.ex.rs.CReportGenException;

/**
 * 帳票印刷処理ロジック 基底クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2021/12/02 YJP-D/Jixin.Sun           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class PrintPreviewLogic extends BaseLogic
{

    /** エラー発生時、ReportID */
    protected static final String              ERROR_REPORT_ID                     = "0";

    /** 印刷プレビューキュー Dao */
    @Inject
    protected TmpQueuePrintPreviewDao          tmpQueuePrintPreviewDao;

    /** 印刷プレビューキューパラメータ Dao */
    @Inject
    protected TmpQueuePrintPreviewParameterDao tmpQueuePrintPreviewParameterDao;

    /** 帳票ラベル印刷履歴 Dao */
    @Inject
    protected HistoryPrintoutDao               historyPrintoutDao;

    /**
     * コンテナリクエストコンテクスト
     */
    @Inject
    protected ContainerRequest                 requestContex;

    /** 帳票ラベル印刷履歴パラメータ Dao */
    @Inject
    protected HistoryPrintoutParameterDao      historyPrintoutParameterDao;

    /** Web.xmlタグ名：帳票種類（多重） */
    protected static final String              REPORT_TYPE_MULTIPLE                = "ReportTypeMultiple";

    /** Web.xmlタグ名：帳票種類（多重棚卸一覧） */
    protected static final String              REPORT_TYPE_MULTIPLE_INVENTORY      = "ReportTypeMultipleInventory";

    /** Web.xmlタグ名：帳票種類（多段） */
    protected static final String              REPORT_TYPE_MULTI_STAGE             = "ReportTypeMultiStage";

    /** Web.xmlタグ名：帳票種類（多段キー変更改ページ無し） */
    protected static final String              REPORT_TYPE_MULTI_STAGE_NO_NEW_PAGE = "ReportTypeMultiStageNoNewPage";

    /** Web.xmlタグ名：帳票種類（多重多段） */
    protected static final String              REPORT_TYPE_MULTIPLE_MULTI_STAGE    = "ReportTypeMultipleMultiStage";

    /**
     * CReportGen初期化
     * @return CReportGen
     * @throws GnomesAppException
     */
    protected CReportGen initCReportGen(int dbAreaDiv) throws GnomesAppException
    {

        try {
            CReportGen cReportGen = new CReportGen();

            // キューの中にある領域（通常領域、保管領域）毎に渡すMetaにより初期処理を区別する
            // DB接続先が異なる
            CGenReportMeta initMeta = null;

            // 通常領域
            if (dbAreaDiv == CommonEnums.RegionType.NORMAL.getIntValue()) {
                initMeta = super.gnomesSystemBean.getcGenReportMeta();
            }
            // 保管領域
            else if (dbAreaDiv == CommonEnums.RegionType.STORAGE.getIntValue()) {
                initMeta = super.gnomesSystemBean.getcGenReportMetaStorage();
            }
            else {
                // 通常領域も保管領域でも指定ない場合はデフォルトで通常領域を指定
                initMeta = super.gnomesSystemBean.getcGenReportMeta();
            }

            cReportGen.GenReportInit(initMeta);

            return cReportGen;

        }
        catch (CReportGenException e) {
            // アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0001, e);

        }

    }

    /**
     * 印刷エラーメッセージ編集
     * <pre>
     * 印刷エラーメッセージが2000文字を超える場合、
     * 2000文字目までを返却する。
     * </pre>
     *
     * @param message 印刷エラーメッセージ
     * @return 変種後印刷エラーメッセージ
     */
    protected String editPrintoutErrorMessage(String message)
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
     * CReportGen（多重）初期化
     * @return CReportGen
     * @throws GnomesAppException
     */
    protected biz.grandsight.ex.rs_multiple.CReportGen initCReportGenMultiple(int dbAreaDiv) throws GnomesAppException
    {

        try {
            biz.grandsight.ex.rs_multiple.CReportGen cReportGen = new biz.grandsight.ex.rs_multiple.CReportGen();

            // キューの中にある領域（通常領域、保管領域）毎に渡すMetaにより初期処理を区別する
            // DB接続先が異なる
            biz.grandsight.ex.rs_multiple.CGenReportMeta initMeta = null;

            // 通常領域
            if (dbAreaDiv == CommonEnums.RegionType.NORMAL.getIntValue()) {
                initMeta = super.gnomesSystemBean.getcGenReportMultipleMeta();
            }
            // 保管領域
            else if (dbAreaDiv == CommonEnums.RegionType.STORAGE.getIntValue()) {
                initMeta = super.gnomesSystemBean.getcGenReportMultipleMetaStorage();
            }
            else {
                // 通常領域も保管領域でも指定ない場合はデフォルトで通常領域を指定
                initMeta = super.gnomesSystemBean.getcGenReportMultipleMeta();
            }

            cReportGen.GenReportInit(initMeta);

            return cReportGen;

        }
        catch (biz.grandsight.ex.rs_multiple.CReportGenException e) {
            // アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0001, e);

        }

    }

    /**
     * CReportGen（多重棚卸一覧）初期化
     * @return CReportGen
     * @throws GnomesAppException
     */
    protected biz.grandsight.ex.rs_multiple21.CReportGen initCReportGenMultipleInventoryList(int dbAreaDiv)
            throws GnomesAppException
    {

        try {
            biz.grandsight.ex.rs_multiple21.CReportGen cReportGen = new biz.grandsight.ex.rs_multiple21.CReportGen();

            // キューの中にある領域（通常領域、保管領域）毎に渡すMetaにより初期処理を区別する
            // DB接続先が異なる
            biz.grandsight.ex.rs_multiple21.CGenReportMeta initMeta = null;

            // 通常領域
            if (dbAreaDiv == CommonEnums.RegionType.NORMAL.getIntValue()) {
                initMeta = super.gnomesSystemBean.getcGenReportMultipleInventoryMeta();
            }
            // 保管領域
            else if (dbAreaDiv == CommonEnums.RegionType.STORAGE.getIntValue()) {
                initMeta = super.gnomesSystemBean.getcGenReportMultipleInventoryMetaStorage();
            }
            else {
                // 通常領域も保管領域でも指定ない場合はデフォルトで通常領域を指定
                initMeta = super.gnomesSystemBean.getcGenReportMultipleInventoryMeta();
            }

            cReportGen.GenReportInit(initMeta);

            return cReportGen;

        }
        catch (biz.grandsight.ex.rs_multiple21.CReportGenException e) {
            // アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0001, e);

        }

    }

    /**
     * CReportGen（多段）初期化
     * @return CReportGen
     * @throws GnomesAppException
     */
    protected biz.grandsight.ex.rs_multistage.CReportGen initCReportGenMultiStage(int dbAreaDiv)
            throws GnomesAppException
    {

        try {
            biz.grandsight.ex.rs_multistage.CReportGen cReportGen = new biz.grandsight.ex.rs_multistage.CReportGen();

            // キューの中にある領域（通常領域、保管領域）毎に渡すMetaにより初期処理を区別する
            // DB接続先が異なる
            biz.grandsight.ex.rs_multistage.CGenReportMeta initMeta = null;

            // 通常領域
            if (dbAreaDiv == CommonEnums.RegionType.NORMAL.getIntValue()) {
                initMeta = super.gnomesSystemBean.getcGenReportMultiStageMeta();
            }
            // 保管領域
            else if (dbAreaDiv == CommonEnums.RegionType.STORAGE.getIntValue()) {
                initMeta = super.gnomesSystemBean.getcGenReportMultiStageMetaStorage();
            }
            else {
                // 通常領域も保管領域でも指定ない場合はデフォルトで通常領域を指定
                initMeta = super.gnomesSystemBean.getcGenReportMultiStageMeta();
            }

            cReportGen.GenReportInit(initMeta);

            return cReportGen;

        }
        catch (biz.grandsight.ex.rs_multistage.CReportGenException e) {
            // アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0001, e);

        }

    }

    /**
     * CReportGen（多段キー変更改ページ無し）初期化
     * @return CReportGen
     * @throws GnomesAppException
     */
    protected biz.grandsight.ex.rs_multistage41.CReportGen initCReportGenMultiStageNoNewPage(int dbAreaDiv)
            throws GnomesAppException
    {

        try {
            biz.grandsight.ex.rs_multistage41.CReportGen cReportGen = new biz.grandsight.ex.rs_multistage41.CReportGen();

            // キューの中にある領域（通常領域、保管領域）毎に渡すMetaにより初期処理を区別する
            // DB接続先が異なる
            biz.grandsight.ex.rs_multistage41.CGenReportMeta initMeta = null;

            // 通常領域
            if (dbAreaDiv == CommonEnums.RegionType.NORMAL.getIntValue()) {
                initMeta = super.gnomesSystemBean.getcGenReportMultiStageNoNewPageMeta();
            }
            // 保管領域
            else if (dbAreaDiv == CommonEnums.RegionType.STORAGE.getIntValue()) {
                initMeta = super.gnomesSystemBean.getcGenReportMultiStageNoNewPageMetaStorage();
            }
            else {
                // 通常領域も保管領域でも指定ない場合はデフォルトで通常領域を指定
                initMeta = super.gnomesSystemBean.getcGenReportMultiStageNoNewPageMeta();
            }

            cReportGen.GenReportInit(initMeta);

            return cReportGen;

        }
        catch (biz.grandsight.ex.rs_multistage41.CReportGenException e) {
            // アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0001, e);

        }

    }

    /**
     * CReportGen（多重多段）初期化
     * @return CReportGen
     * @throws GnomesAppException
     */
    protected biz.grandsight.ex.rs_multiplemultistage.CReportGen initCReportGenMultipleMultiStage(int dbAreaDiv)
            throws GnomesAppException
    {

        try {
            biz.grandsight.ex.rs_multiplemultistage.CReportGen cReportGen = new biz.grandsight.ex.rs_multiplemultistage.CReportGen();

            // キューの中にある領域（通常領域、保管領域）毎に渡すMetaにより初期処理を区別する
            // DB接続先が異なる
            biz.grandsight.ex.rs_multiplemultistage.CGenReportMeta initMeta = null;

            // 通常領域
            if (dbAreaDiv == CommonEnums.RegionType.NORMAL.getIntValue()) {
                initMeta = super.gnomesSystemBean.getcGenReportMultipleMultiStageMeta();
            }
            // 保管領域
            else if (dbAreaDiv == CommonEnums.RegionType.STORAGE.getIntValue()) {
                initMeta = super.gnomesSystemBean.getcGenReportMultipleMultiStageMetaStorage();
            }
            else {
                // 通常領域も保管領域でも指定ない場合はデフォルトで通常領域を指定
                initMeta = super.gnomesSystemBean.getcGenReportMultipleMultiStageMeta();
            }

            cReportGen.GenReportInit(initMeta);

            return cReportGen;

        }
        catch (biz.grandsight.ex.rs_multiplemultistage.CReportGenException e) {
            // アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
            throw super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0001, e);

        }

    }

    /**
     * エラー発生時のメッセージ取得.
     * @param reportId ReportID
     * @return メッセージ
     */
    protected String getMessage(String reportId)
    {

        if (ERROR_REPORT_ID.equals(reportId)) {
            // 帳票印刷依頼処理でエラーが発生しました。 GRANSIGHT-EX 帳票処理のログを確認してください。
            return MessagesHandler.getExceptionMessage(super.req, super.exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0165));

        }

        return null;

    }

    /**
     * 帳票印刷コールバック情報設定
     * @param pdfFileName PDFファイル名
     * @param printType 印刷種別
     * @param rePrintFileName 再印刷対象ファイル名
     * @param printoutCopies 印刷枚数
     * @param printRequestCount 印刷依頼回数
     * @param printRequestCount nk要求イベントID
     * @param printRequestCount nk要求内連番
     *
     * @return
     */
    protected PrintPreviewCallbackInfo setCallbackInfo(String pdfFileName, String printType, String rePrintFileName,
            int printoutCopies, int printRequestCount, String requestEventId, int requestSeq,
            String queuePringPreviewKey)
    {

        PrintPreviewCallbackInfo callbackInfo = new PrintPreviewCallbackInfo();
        callbackInfo.setPathName(this.getPathName());
        callbackInfo.setPdfFileName(pdfFileName);
        callbackInfo.setPrintType(printType);
        callbackInfo.setRePrintFileName(rePrintFileName);
        callbackInfo.setPrintoutCopies(printoutCopies);
        callbackInfo.setPrintRequestCount(printRequestCount);
        callbackInfo.setRequest(super.req);
        callbackInfo.setRequestEventId(requestEventId);
        callbackInfo.setRequestSeq(requestSeq);
        callbackInfo.setQueuePrintPreviewKey(queuePringPreviewKey);

        return callbackInfo;

    }

    /**
     * パス名取得
     * @return パス名
     */
    protected String getPathName()
    {

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            // XML解析で外部エンティティへのアクセスを無効にする
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            DocumentBuilder configXml = dbf.newDocumentBuilder();
            Document xMLSetting = configXml.parse(super.gnomesSystemBean.getReportDefinitionXMLFileName());
            NodeList template = xMLSetting.getElementsByTagName("target-directory").item(0).getChildNodes();
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
     * 印刷プレビューファイル生成のタイムアウト時間取得
     * @return タイムアウト時間
     */
    protected int getPeriodHour()
    {

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            // XML解析で外部エンティティへのアクセスを無効にする
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            DocumentBuilder configXml = dbf.newDocumentBuilder();
            Document xMLSetting = configXml.parse(super.gnomesSystemBean.getReportDefinitionXMLFileName());
            NodeList template = xMLSetting.getElementsByTagName("preview-period").item(0).getChildNodes();
            int hour = 0;
            if (!"".equals(template.item(1).getTextContent())) {
                hour = Integer.parseInt(template.item(1).getTextContent());
            }
            return hour;

        }
        catch (Exception e) {

            String message = MessagesHandler.getExceptionMessage(super.req,
                    super.exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0001,
                            new Object[10]));

            this.logHelper.severe(super.logger, null, null, message, e);

        }

        return 0;

    }

    /**
     * 印刷エラーメッセージ編集
     * <pre>
     * 印刷エラーメッセージが2000文字を超える場合、
     * 2000文字目までを返却する。
     * </pre>
     *
     * @param message 印刷エラーメッセージ
     * @return 変種後印刷エラーメッセージ
     */
    protected String editPrintPreviewErrorMessage(String message)
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
     * メッセージ履歴に出力する
     *
     * @param message           任意のメッセージ文字列。リソースの他Exceptionのメッセージも
     * @param tmpQueuePrintout  キューのエンティティの内容
     * @param reportId          帳票ID
     * @param phvalue           帳票パラメータの内容
     */
    protected void insertMessageForReport(String message, TmpQueuePrintout tmpQueuePrintout, String reportId,
            HashMap<String, String> phvalue)
    {

        String messageStr = message;

        if (StringUtil.isNullOrEmpty(messageStr)) {
            messageStr = "";
        }
        // メインメッセージの出力
        MessageData mesOwner = new MessageData(GnomesMessagesConstants.ME01_0168, new Object[]{reportId, messageStr});

        // 子メッセージの出力
        MessageData[] mesChilds = new MessageData[phvalue.size()];

        int i = 0;
        for (Map.Entry<String, String> entry : phvalue.entrySet()) {
            String strMessage = entry.getKey() + " = " + entry.getValue();
            mesChilds[i] = new MessageData(GnomesMessagesConstants.ME01_0238, new Object[]{strMessage});
            i++;
        }

        // メッセージ履歴を出力する
        MessagesHandler.setMessage(requestContex, mesOwner, mesChilds);

        return;
    }

}
