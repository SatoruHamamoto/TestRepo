package com.gnomes.system.logic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.camel.cdi.Mock;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.data.GnomesSystemBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.persistence.GnomesEntityManager;
import com.gnomes.system.dao.HistoryPrintoutDao;
import com.gnomes.system.dao.HistoryPrintoutFileDao;
import com.gnomes.system.entity.HistoryPrintout;
import com.gnomes.system.entity.HistoryPrintoutFile;
import com.gnomes.uiservice.ContainerRequest;

import biz.grandsight.ex.rs.CGenReportMeta;
import biz.grandsight.ex.rs.CReportGen;
import biz.grandsight.ex.rs.CReportGenException;

class K01BReprintTest {

    private final static String METHOD_PRINT = "print";
    private final static String METHOD_REPRINT = "rePrint";

    /** XML定義パス */
    private Path xmlPath;

    @Spy
    @InjectMocks
    K01BReprint reprint;

    @Mock
    HistoryPrintoutFileDao historyPrintoutFileDao;

    @Mock
    HistoryPrintoutDao historyPrintoutDao;

    @Mock
    GnomesEntityManager em;

    @Mock
    GnomesSessionBean gnomesSessionBean;

    @Mock
    GnomesSystemBean gnomesSystemBean;

    @Mock
    ContainerRequest req;

    @Mock
    GnomesExceptionFactory exceptionFactory;

    @Mock
    CReportGen cReportGen;

    @Mock
    biz.grandsight.ex.rs_multiple.CReportGen cReportGenMultiple;

    @Mock
    biz.grandsight.ex.rs_multiple21.CReportGen cReportGenMultipleInventoryList;

    @Mock
    biz.grandsight.ex.rs_multistage.CReportGen cReportGenMultiStage;

    @Mock
    biz.grandsight.ex.rs_multistage41.CReportGen cReportGenMultiStageNoNewPage;

    @Mock
    biz.grandsight.ex.rs_multiplemultistage.CReportGen cReportGenMultipleMultiStage;

    @TempDir
    Path tempDir;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        xmlPath = tempDir.resolve("gnomes-temp-def.xml");
        if (!Files.exists(xmlPath)) {
            Files.createFile(xmlPath);
            createXmlDefinitionFile();
        }
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    @Disabled
    @DisplayName("帳票再印刷依頼：電子ファイル作成区分が「3：電子保存」のとき再印字時プリンター出力フラグがfalseになる確認")
    void test_printRequest_type3_printerOutputFlag_false() throws Exception, GnomesAppException {
        // 電子ファイル作成区分「3：電子保存」
        int isFileCreateType = 3;

        // モック差替え
        initMocksTestPrinterOutputFlag();

        // 帳票再印刷の実行パラメータ再印字時プリンター出力フラグがfalseになることを確認
        boolean printerOutputFlag = testRun(METHOD_PRINT, isFileCreateType);
        assertTrue(!printerOutputFlag);
    }

    @Test
    @Disabled
    @DisplayName("帳票再印刷依頼：電子ファイル作成区分が「1：印刷」のとき再印字時プリンター出力フラグがtrueになる確認")
    void test_printRequest_type1_printerOutputFlag_true() throws Exception, GnomesAppException {
        // 電子ファイル作成区分「1：印刷」
        int isFileCreateType = 1;

        // モック差替え
        initMocksTestPrinterOutputFlag();

        // 帳票再印刷の実行パラメータ再印字時プリンター出力フラグがtrueになることを確認
        boolean printerOutputFlag = testRun(METHOD_PRINT, isFileCreateType);
        assertTrue(printerOutputFlag);
    }

    @Test
    @Disabled
    @DisplayName("帳票再印刷依頼：電子ファイル作成区分が「2：印刷と電子保存」のとき再印字時プリンター出力フラグがtrueになる確認")
    void test_printRequest_type2_printerOutputFlag_true() throws Exception, GnomesAppException {
        // 電子ファイル作成区分「2：印刷と電子保存」
        int isFileCreateType = 2;

        // モック差替え
        initMocksTestPrinterOutputFlag();

        // 帳票再印刷の実行パラメータ再印字時プリンター出力フラグがtrueになることを確認
        boolean printerOutputFlag = testRun(METHOD_PRINT, isFileCreateType);
        assertTrue(printerOutputFlag);
    }

    @Test
    @Disabled
    @DisplayName("帳票再印刷依頼：印字ラベル印字履歴情報が存在しない")
    void test_reprint_historyPrintout_exists_false() throws Exception {
        boolean printerOutputFlag = true;

        Mockito.doReturn(null).when(historyPrintoutDao).getConditionReportId(Mockito.any(), Mockito.any());
        Mockito.doNothing().when(historyPrintoutDao).insert(Mockito.any(), Mockito.any());

        boolean result = (boolean) testRun(METHOD_REPRINT, printerOutputFlag);

        assertTrue(!result);
        verify(historyPrintoutDao, Mockito.times(0)).insert(Mockito.any(), Mockito.any());
    }

    @Test
    @Disabled
    @DisplayName("帳票再印刷依頼：帳票印字設定情報が存在しない")
    void test_reprint_cGenReportMeta_exists_false() throws Exception {
        boolean printerOutputFlag = true;

        Mockito.doReturn(initMockHistoryPrintout(10)).when(historyPrintoutDao).getConditionReportId(Mockito.any(), Mockito.any());
        Mockito.doReturn(null).when(gnomesSystemBean).getcGenReportMeta();
        Mockito.doNothing().when(historyPrintoutDao).insert(Mockito.any(), Mockito.any());

        boolean result = (boolean) testRun(METHOD_REPRINT, printerOutputFlag);

        assertTrue(!result);
        verify(historyPrintoutDao, Mockito.times(0)).insert(Mockito.any(), Mockito.any());
    }

    @Test
    @Disabled
    @DisplayName("帳票再印刷依頼：帳票ラベル印字履歴情報が存在しない")
    void test_reprint_historyPrintoutFile_exists_false() throws Exception {
        boolean printerOutputFlag = true;

        Mockito.doReturn(initMockHistoryPrintout(10)).when(historyPrintoutDao).getConditionReportId(Mockito.any(), Mockito.any());
        Mockito.doReturn(new CGenReportMeta()).when(gnomesSystemBean).getcGenReportMeta();
        Mockito.doReturn(null).when(historyPrintoutFileDao).getHistoryPrintoutFile(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doNothing().when(historyPrintoutDao).insert(Mockito.any(), Mockito.any());

        boolean result = (boolean) testRun(METHOD_REPRINT, printerOutputFlag);

        assertTrue(!result);
        verify(historyPrintoutDao, Mockito.times(0)).insert(Mockito.any(), Mockito.any());
    }

    @Test
    @Disabled
    @DisplayName("帳票再印刷依頼：Excelbyte配列がNull")
    void test_reprint_excelReportFile_null() throws Exception {
        boolean printerOutputFlag = true;

        HistoryPrintoutFile historyPrintoutFile = initMockHistoryPrintoutFile();
        historyPrintoutFile.setExcel_report_file(null);

        // GnomesSessionBeanクラスモック差替え
        setupDoReturnGnomesSessionBean();

        Mockito.doReturn(initMockHistoryPrintout(10)).when(historyPrintoutDao).getConditionReportId(Mockito.any(), Mockito.any());
        Mockito.doReturn(historyPrintoutFile).when(historyPrintoutFileDao).getHistoryPrintoutFile(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doReturn(new CGenReportMeta()).when(gnomesSystemBean).getcGenReportMeta();
        Mockito.doReturn(xmlPath.toString()).when(gnomesSystemBean).getReportDefinitionXMLFileName();
        Mockito.doNothing().when(historyPrintoutDao).insert(Mockito.any(), Mockito.any());

        boolean result = (boolean) testRun(METHOD_REPRINT, printerOutputFlag);

        assertTrue(!result);
        verify(historyPrintoutDao, Mockito.times(0)).insert(Mockito.any(), Mockito.any());
    }

    @Test
    @Disabled
    @DisplayName("帳票再印刷依頼：「帳票種類：ラベル」の場合帳票印字（ラベル）の再印刷依頼が実施される")
    void test_cReportGen_reprint_printType_label() throws Exception {
        boolean printerOutputFlag = true;

        // 10：ラベル
        initMocksTestCReportGenReprintPrintType(10);

        boolean result = (boolean) testRun(METHOD_REPRINT, printerOutputFlag);

        assertTrue(result);
        // 帳票印字（ラベル）の再印刷依頼
        verify(cReportGen, Mockito.times(1)).Reprint(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(),
            Mockito.anyString(), Mockito.any(K01CPrintOutCallbackProcess.class), Mockito.anyInt(),
            Mockito.anyBoolean());
    }

    @Test
    @Disabled
    @DisplayName("帳票再印刷依頼：「帳票種類：一覧」の場合帳票印字（一覧）の再印刷依頼が実施される")
    void test_cReportGen_reprint_printType_list() throws Exception {
        boolean printerOutputFlag = true;

        // 20：一覧
        initMocksTestCReportGenReprintPrintType(20);

        boolean result = (boolean) testRun(METHOD_REPRINT, printerOutputFlag);

        assertTrue(result);
        // 帳票印字（一覧）の再印刷依頼
        verify(cReportGen, Mockito.times(1)).Reprint(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(),
            Mockito.anyString(), Mockito.any(K01CPrintOutCallbackProcess.class), Mockito.anyInt(),
            Mockito.anyBoolean());
    }

    @Test
    @Disabled
    @DisplayName("帳票再印刷依頼：「帳票種類：多重」の場合帳票印字（多重）の再印刷依頼が実施される")
    void test_cReportGen_reprint_printType_multiple() throws Exception {
        boolean printerOutputFlag = true;

        // 21：多重
        initMocksTestCReportGenReprintPrintType(21);

        boolean result = (boolean) testRun(METHOD_REPRINT, printerOutputFlag);

        assertTrue(result);
        // 帳票印字（多重）
        verify(cReportGenMultiple, Mockito.times(1)).Reprint(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(),
            Mockito.anyString(), Mockito.any(K01DPrintOutCallbackProcessMultiple.class), Mockito.anyInt(),
            Mockito.anyBoolean());
    }

    @Test
    @Disabled
    @DisplayName("帳票再印刷依頼：「帳票種類：多段」の場合帳票印字（多段）の再印刷依頼が実施される")
    void test_cReportGen_reprint_printType_multiStage() throws Exception {
        boolean printerOutputFlag = true;

        // 22：多段
        initMocksTestCReportGenReprintPrintType(22);

        boolean result = (boolean) testRun(METHOD_REPRINT, printerOutputFlag);

        assertTrue(result);
        // 帳票印字（多段）
        verify(cReportGenMultiStage, Mockito.times(1)).Reprint(Mockito.anyString(), Mockito.anyString(),
            Mockito.anyInt(), Mockito.anyString(), Mockito.any(K01EPrintOutCallbackProcessMultiStage.class),
            Mockito.anyInt(), Mockito.anyBoolean());
    }

    @Test
    @DisplayName("帳票再印刷依頼：「帳票種類：多段（キー割れ時改頁無し）」の場合帳票印字（多段キー変更改ページ無し）の再印刷依頼が実施される")
    void test_cReportGen_reprint_printType_multiStageNoNewPage() throws Exception {
        boolean printerOutputFlag = true;

        // 23：多段（キー割れ時改頁無し）
        initMocksTestCReportGenReprintPrintType(23);

        boolean result = (boolean) testRun(METHOD_REPRINT, printerOutputFlag);

        assertTrue(result);
        // 帳票印字（多段キー変更改ページ無し）
        verify(cReportGenMultiStageNoNewPage, Mockito.times(1)).Reprint(Mockito.anyString(), Mockito.anyString(),
            Mockito.anyInt(), Mockito.anyString(), Mockito.any(K01FPrintOutCallbackProcessMultiStageNoNewPage.class),
            Mockito.anyInt(), Mockito.anyBoolean());
    }

    @Test
    @Disabled
    @DisplayName("帳票再印刷依頼：「帳票種類：多重多段」の場合帳票印字（多重多段）の再印刷依頼が実施される")
    void test_cReportGen_reprint_printType_multipleMultiStage() throws Exception {
        boolean printerOutputFlag = true;

        // 24：多重多段
        initMocksTestCReportGenReprintPrintType(24);

        boolean result = (boolean) testRun(METHOD_REPRINT, printerOutputFlag);

        assertTrue(result);
        // 帳票印字（多重多段）
        verify(cReportGenMultipleMultiStage, Mockito.times(1)).Reprint(Mockito.anyString(), Mockito.anyString(),
            Mockito.anyInt(), Mockito.anyString(), Mockito.any(K01GPrintOutCallbackProcessMultipleMultiStage.class),
            Mockito.anyInt(), Mockito.anyBoolean());
    }

    @Test
    @Disabled
    @DisplayName("帳票再印刷依頼：「帳票種類：集計行有り」の場合帳票印字（多重棚卸一覧）の再印刷依頼が実施される")
    void test_cReportGen_reprint_printType_multipleInventory() throws Exception {
        boolean printerOutputFlag = true;

        // 25：集計行有り
        initMocksTestCReportGenReprintPrintType(25);

        boolean result = (boolean) testRun(METHOD_REPRINT, printerOutputFlag);

        assertTrue(result);
        // 帳票印字（多重棚卸一覧）
        verify(cReportGenMultipleInventoryList, Mockito.times(1)).Reprint(Mockito.anyString(), Mockito.anyString(),
            Mockito.anyInt(), Mockito.anyString(), Mockito.any(K01HPrintOutCallbackProcessMultipleInventory.class),
            Mockito.anyInt(), Mockito.anyBoolean());
    }

    @Test
    @Disabled
    @DisplayName("帳票再印刷依頼：帳票種類がいずれも該当しない場合再印刷依頼が実施されない")
    void test_cReportGen_reprint_printType_none() throws Exception {
        boolean printerOutputFlag = true;

        // 99：該当しない
        initMocksTestCReportGenReprintPrintType(99);

        boolean result = (boolean) testRun(METHOD_REPRINT, printerOutputFlag);

        assertTrue(result);
        verify(cReportGen, Mockito.times(0)).Reprint(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(),
            Mockito.anyString(), Mockito.any(K01CPrintOutCallbackProcess.class), Mockito.anyInt(),
            Mockito.anyBoolean());
        verify(cReportGenMultiple, Mockito.times(0)).Reprint(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(),
            Mockito.anyString(), Mockito.any(K01DPrintOutCallbackProcessMultiple.class), Mockito.anyInt(),
            Mockito.anyBoolean());
        verify(cReportGenMultipleInventoryList, Mockito.times(0)).Reprint(Mockito.anyString(), Mockito.anyString(),
            Mockito.anyInt(), Mockito.anyString(), Mockito.any(K01HPrintOutCallbackProcessMultipleInventory.class),
            Mockito.anyInt(), Mockito.anyBoolean());
        verify(cReportGenMultiStage, Mockito.times(0)).Reprint(Mockito.anyString(), Mockito.anyString(),
            Mockito.anyInt(), Mockito.anyString(), Mockito.any(K01EPrintOutCallbackProcessMultiStage.class),
            Mockito.anyInt(), Mockito.anyBoolean());
        verify(cReportGenMultiStageNoNewPage, Mockito.times(0)).Reprint(Mockito.anyString(), Mockito.anyString(),
            Mockito.anyInt(), Mockito.anyString(), Mockito.any(K01FPrintOutCallbackProcessMultiStageNoNewPage.class),
            Mockito.anyInt(), Mockito.anyBoolean());
        verify(cReportGenMultipleMultiStage, Mockito.times(0)).Reprint(Mockito.anyString(), Mockito.anyString(),
            Mockito.anyInt(), Mockito.anyString(), Mockito.any(K01GPrintOutCallbackProcessMultipleMultiStage.class),
            Mockito.anyInt(), Mockito.anyBoolean());
    }

    /**
     * 帳票ラベル印字履歴初期化
     * @param printType 帳票種類
     * @return 帳票ラベル印字履歴
     */
    private HistoryPrintout initMockHistoryPrintout(int printType) {
        return new HistoryPrintout("test_history_printout_key",
            "test_event_id",
            99,
            new Date(),
            "test_report_id",
            1,
            "test_computer_id",
            "test_computer_name",
            new Date(),
            printType,
            1,
            "test_printer_id",
            "test_user_id",
            "test_user_name",
            1,
            1,
            "test_reprint_source_event_id",
            99,
            1);
    }

    /**
     * 帳票ラベル印字履歴ファイル初期化
     * @return 帳票ラベル印字履歴ファイル
     * @throws IOException
     */
    private HistoryPrintoutFile initMockHistoryPrintoutFile() throws IOException {
        return new HistoryPrintoutFile("test_history_printout_file_key",
            "test_history_printout_key",
            "event_id",
            99,
            getPdfByteArray(),
            getExcelByteArray(),
            "test_first_regist_event_id",
            "30038536",
            "test_first_user",
            new Date(),
            "test_last_regist_event_id",
            "99999999",
            "test_last_user",
            new Date(),
            1);
    }

    /**
     * PDFファイルバイト配列変換
     * @return バイト配列
     * @throws IOException
     */
    private byte[] getPdfByteArray() throws IOException {
        Path path = tempDir.resolve("test_K01BReprint.pdf");
        try (PDDocument document = new PDDocument();) {
            PDPage page = new PDPage();
            document.addPage(page);
            document.save(path.toFile());
        }
        return Files.readAllBytes(path);
    }

    /**
     * Excelファイルバイト配列変換
     * @return バイト配列
     * @throws IOException
     */
    private byte[] getExcelByteArray() throws IOException {
        Path path = tempDir.resolve("test_K01BReprint.xlsx");
        Files.createFile(path);
        try (XSSFWorkbook workbook = new XSSFWorkbook();
            FileOutputStream  stream = new FileOutputStream(path.toFile())) {
            workbook.createSheet("TEST_SHEET");
            workbook.write(stream);
        }
        return Files.readAllBytes(path);
    }

    /**
     * XML定義ファイル作成
     * @throws ParserConfigurationException
     * @throws TransformerException
     * @throws IOException
     */
    private void createXmlDefinitionFile() throws ParserConfigurationException, TransformerException, IOException {
        Path targetDirectoryPath = tempDir.resolve("target-directory");
        Files.createDirectory(targetDirectoryPath);

        Document document = createXmlDocument("target-directory");
        Element root = document.getDocumentElement();

        Element path = document.createElement("path");
        Text targetDirectory = document.createTextNode(targetDirectoryPath.toString());
        path.appendChild(targetDirectory);
        root.appendChild(path);

        List<String> list = new ArrayList<>();
        list.add(createXmlString(document));

        Files.write(xmlPath, list, Charset.forName("UTF-8"), StandardOpenOption.WRITE);
    }

    /**
     * ドキュメント生成
     * @param tagName タグ名
     * @return Documetオブジェクト
     * @throws ParserConfigurationException
     */
    private Document createXmlDocument(String tagName) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation dom = builder.getDOMImplementation();
        return dom.createDocument("", tagName, null);
    }

    /**
     * XML文字列生成
     * @param document ドキュメント
     * @return XML文字列
     * @throws TransformerException
     */
    private String createXmlString(Document document) throws TransformerException {
        StringWriter writer = new StringWriter();
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");

        transformer.transform(new DOMSource(document), new StreamResult(writer));
        return writer.toString();
    }

    /**
     * 再印字時プリンター出力フラグ確認用モック差替え
     * @throws IOException
     * @throws Exception
     */
    private void initMocksTestPrinterOutputFlag() throws IOException, Exception {
        setupDoReturnDao(10);
        setupDoReturnGnomesSessionBean();
        setupDoReturnGnomesSystemBean();
    }

    /**
     * 帳票印字確認用モック差替え
     * @param printType 帳票種類
     * @throws GnomesAppException
     * @throws Exception
     */
    private void initMocksTestCReportGenReprintPrintType(int printType) throws GnomesAppException, Exception {
        setupDoReturnDao(printType);
        setupDoReturnGnomesSessionBean();
        setupDoReturnGnomesSystemBean();
        setupDoReturnCReportGen();
    }

    /**
     * GnomesSessionBeanモック化
     */
    private void setupDoReturnGnomesSessionBean() {
        Mockito.doReturn("ut_test_session_computer_01").when(gnomesSessionBean).getComputerId();
        Mockito.doReturn("ut_test_session_computer").when(gnomesSessionBean).getComputerName();
        Mockito.doReturn("ut_test_session_user_01").when(gnomesSessionBean).getUserId();
        Mockito.doReturn("ut_test_session_user").when(gnomesSessionBean).getUserName();
    }

    /**
     * GnomesSystemBeanモック化
     * @throws CReportGenException
     */
    private void setupDoReturnGnomesSystemBean() throws CReportGenException,
        biz.grandsight.ex.rs_multiple.CReportGenException, biz.grandsight.ex.rs_multiple21.CReportGenException,
        biz.grandsight.ex.rs_multistage.CReportGenException, biz.grandsight.ex.rs_multistage41.CReportGenException,
        biz.grandsight.ex.rs_multiplemultistage.CReportGenException {
        Mockito.doReturn(new CGenReportMeta()).when(gnomesSystemBean).getcGenReportMeta();
        Mockito.doReturn(new biz.grandsight.ex.rs_multiple.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultipleMeta();
        Mockito.doReturn(new biz.grandsight.ex.rs_multiple21.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultipleInventoryMeta();
        Mockito.doReturn(new biz.grandsight.ex.rs_multistage.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultiStageMeta();
        Mockito.doReturn(new biz.grandsight.ex.rs_multistage41.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultiStageNoNewPageMeta();
        Mockito.doReturn(new biz.grandsight.ex.rs_multiplemultistage.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultipleMultiStageMeta();
        Mockito.doReturn(xmlPath.toString()).when(gnomesSystemBean).getReportDefinitionXMLFileName();
    }

    /**
     * Daoモック化
     * @param printType
     * @throws GnomesAppException
     * @throws IOException
     */
    private void setupDoReturnDao(int printType) throws GnomesAppException, IOException {
        Mockito.doReturn(initMockHistoryPrintout(printType)).when(historyPrintoutDao).getConditionReportId(Mockito.any(), Mockito.any());
        Mockito.doReturn(initMockHistoryPrintoutFile()).when(historyPrintoutFileDao).getHistoryPrintoutFile(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doNothing().when(historyPrintoutDao).insert(Mockito.any(), Mockito.any());
    }

    /**
     * CReportGenモック化
     * @throws GnomesAppException
     */
    private void setupDoReturnCReportGen() throws GnomesAppException {
        Mockito.doReturn(cReportGen).when(reprint).initCReportGen(anyInt());
        Mockito.doReturn(cReportGenMultiple).when(reprint).initCReportGenMultiple(anyInt());
        Mockito.doReturn(cReportGenMultipleInventoryList).when(reprint).initCReportGenMultipleInventoryList(anyInt());
        Mockito.doReturn(cReportGenMultiStage).when(reprint).initCReportGenMultiStage(anyInt());
        Mockito.doReturn(cReportGenMultiStageNoNewPage).when(reprint).initCReportGenMultiStageNoNewPage(anyInt());
        Mockito.doReturn(cReportGenMultipleMultiStage).when(reprint).initCReportGenMultipleMultiStage(anyInt());
    }

    /**
     * テスト対象メソッドの実行
     * @param methodName メソッド名
     * @param param パラメータ
     * @return 期待値
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws GnomesAppException
     */
    private boolean testRun(String methodName, Object param) throws NoSuchMethodException, SecurityException,
        IllegalAccessException, IllegalArgumentException, InvocationTargetException, GnomesAppException {
        Method method = null;

        if (methodName.equals(METHOD_PRINT)) {
            method = K01BReprint.class.getDeclaredMethod(METHOD_PRINT, String.class, String.class, String.class,
                String.class, int.class, String.class, int.class, int.class);

        } else {
            method = K01BReprint.class.getDeclaredMethod(METHOD_REPRINT, String.class, String.class, String.class,
                String.class, int.class, String.class, int.class, boolean.class);
        }
        method.setAccessible(true);

        String reportId = "test_report_id";
        String printReasonCode = "reasonCode";
        String printReasonName = "reasonName";
        String printReasonComment = "reasonComment";
        int printoutCopies = 1;
        String eventId = "eventId";
        int requestSeq = 1;

        if (methodName.equals(METHOD_PRINT)) {
            ArgumentCaptor<String> reportIdCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> commentCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<Integer> copiesCaptor = ArgumentCaptor.forClass(int.class);
            ArgumentCaptor<String> eventIdCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<Integer> seqCaptor = ArgumentCaptor.forClass(int.class);
            ArgumentCaptor<Boolean> flagCaptor = ArgumentCaptor.forClass(boolean.class);

            method.invoke(reprint, reportId, printReasonCode, printReasonName, printReasonComment, printoutCopies,
                eventId, requestSeq, (int) param);

            verify(reprint, times(1)).rePrint(reportIdCaptor.capture(), codeCaptor.capture(), nameCaptor.capture(),
                commentCaptor.capture(), copiesCaptor.capture(), eventIdCaptor.capture(), seqCaptor.capture(),
                flagCaptor.capture());

            return flagCaptor.getValue();

        } else {
            return (boolean) method.invoke(reprint, reportId, printReasonCode, printReasonName,
                printReasonComment, printoutCopies, eventId, requestSeq, (boolean) param);
        }

    }

}
