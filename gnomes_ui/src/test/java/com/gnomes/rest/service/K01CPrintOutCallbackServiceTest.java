package com.gnomes.rest.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.gnomes.common.constants.SystemDefConstants;
import com.gnomes.common.data.GnomesSystemBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.system.dao.HistoryPrintoutDao;
import com.gnomes.system.dao.MstrSystemDefineDao;
import com.gnomes.system.data.printout.PrintOutCallbackInfo;
import com.gnomes.system.data.printout.PrintOutInfo;
import com.gnomes.system.entity.HistoryPrintout;
import com.gnomes.system.entity.MstrSystemDefine;
import com.gnomes.uiservice.ContainerRequest;

class K01CPrintOutCallbackServiceTest {

    private final static String REPORT_ID_XLSX = "ut-gnomes-ui_K01CPrintOutCallbackServiceTest.xlsx";
    private final static String REPORT_ID_PDF = "ut-gnomes-ui_K01CPrintOutCallbackServiceTest.pdf";

    @Spy
    @InjectMocks
    K01CPrintOutCallbackService service;

    @Mock
    EntityManagerFactory factory;

    @Mock
    EntityManager manager;

    @Mock
    Session session;

    @Mock
    Transaction transaction;

    @Mock
    ContainerRequest req;

    @Mock
    HistoryPrintoutDao historyPrintoutDao;

    @Mock
    MstrSystemDefineDao mstrSystemDefineDao;

    @Mock
    GnomesSystemBean gnomesSystemBean;

    @Mock
    GnomesExceptionFactory exceptionFactory;

    @Mock
    LogHelper logHelper;

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
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    @Disabled
    // ここでは一連の動作を流すのみとし、詳細なファイル操作の検証はFileUtilTest.javaにて実施
    @DisplayName("帳票印刷コールバック処理：一連のファイル操作を確認")
    void test_process() throws GnomesAppException, IOException {
        Path reportXlsx = tempDir.resolve(REPORT_ID_XLSX);
        Path reportPdf = tempDir.resolve(REPORT_ID_PDF);
        Files.createFile(reportXlsx);
        Files.createFile(reportPdf);
        Path targetDir = tempDir.resolve("target-directory");
        Path templateDir = tempDir.resolve("template-directory");
        Path templateReportId = templateDir.resolve(REPORT_ID_XLSX.replace(".xlsx", ""));
        Path targetDelReportId = targetDir.resolve(REPORT_ID_XLSX.split("_")[1]);
        Files.createDirectories(templateReportId);
        Files.createDirectories(targetDir);
        Files.createFile(targetDelReportId);

        PrintOutCallbackInfo callBackInfo = getPrintOutCallbackInfo();

        doReturn(manager).when(factory).createEntityManager();
        doReturn(session).when(manager).unwrap(Session.class);
        doReturn(transaction).when(session).beginTransaction();
        doReturn(new HistoryPrintout()).when(historyPrintoutDao).getConditionReportId(callBackInfo.getReportId(), manager);
        doReturn(new MstrSystemDefine()).when(mstrSystemDefineDao).getMstrSystemDefine(SystemDefConstants.TYPE_HISTORY_PRINTOUT_FILE, SystemDefConstants.CODE_HISTORY_PRINTOUT_FILE_INSERT_FLAG);
        doReturn("gnomes-temp-def.xml").when(gnomesSystemBean).getReportDefinitionXMLFileName();
        doReturn(templateDir.toString()).when(service).getPathName("target-directory");
        doReturn(targetDir.toString()).when(service).getPathName("template-directory");

        StringBuilder directoryXlsxPathTo = new StringBuilder();
        directoryXlsxPathTo.append(callBackInfo.getPathName());
        directoryXlsxPathTo.append(File.separator);
        directoryXlsxPathTo.append(callBackInfo.getReportId().replace(".xlsx", ""));
        directoryXlsxPathTo.append(File.separator);
        directoryXlsxPathTo.append(REPORT_ID_XLSX);
        StringBuilder directoryPdfPathTo = new StringBuilder();
        directoryPdfPathTo.append(callBackInfo.getPathName());
        directoryPdfPathTo.append(File.separator);
        directoryPdfPathTo.append(callBackInfo.getReportId().replace(".xlsx", ""));
        directoryPdfPathTo.append(File.separator);
        directoryPdfPathTo.append(REPORT_ID_XLSX);
        Path xlsxPathTo = Paths.get(directoryXlsxPathTo.toString());
        Path pdfPathTo = Paths.get(directoryPdfPathTo.toString());
        Path parentTo = xlsxPathTo.getParent();

        // =======================================================================
        // ファイル操作前の存在確認チェック
        // =======================================================================
        if (!Files.exists(reportXlsx) || !Files.exists(reportPdf)) {
            fail();
        }

        if (Files.exists(xlsxPathTo) || Files.exists(pdfPathTo)) {
            fail();
        }

        if (Files.exists(parentTo)) {
            fail();
        }

        if (!Files.exists(templateReportId) || !Files.exists(targetDelReportId)) {
            fail();
        }

        // 帳票印刷コールバック処理
        service.process(callBackInfo);

        // =======================================================================
        // ファイル操作後の存在確認チェック
        // =======================================================================
        assertTrue(!Files.exists(reportXlsx));
        assertTrue(!Files.exists(reportPdf));
        assertTrue(!Files.exists(templateReportId));
        assertTrue(!Files.exists(targetDelReportId));
        assertTrue(!Files.exists(parentTo));
    }

    @Test
    @Disabled
    @DisplayName("ファイル内容取得：FileUtils.readFileToByteで取得したバイト配列を電子帳票情報にセットする")
    void test_getFileInfo() throws GnomesAppException, NoSuchMethodException, SecurityException,
        IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
        Path reportXlsx = tempDir.resolve(REPORT_ID_XLSX);
        Path reportPdf = tempDir.resolve(REPORT_ID_PDF);
        Files.createFile(reportXlsx);
        Files.createFile(reportPdf);

        PrintOutCallbackInfo callbackInfo = getPrintOutCallbackInfo();
        PrintOutInfo info = new PrintOutInfo();
        info.setExcelFileName(callbackInfo.getReportId());
        info.setPdfFileNameFrom(callbackInfo.getReportId().replace(".xlsx", ".pdf"));
        info.setPdfFileName(REPORT_ID_PDF);
        info.setDirectoryPathTo(tempDir.toString());

        K01CPrintOutCallbackService service = new K01CPrintOutCallbackService();
        Method method = K01CPrintOutCallbackService.class.getDeclaredMethod("getFileInfo", PrintOutInfo.class);
        method.setAccessible(true);
        method.invoke(service, info);
        assertNotNull(info.getExcelFileInfo());
        assertNotNull(info.getPdfFileInfo());
    }

    @Test
    @Disabled
    @DisplayName("印字エラーメッセージ編集：2000文字を超えたら2000文字目までを返却する")
    void test_editPrintoutErrorMessage_2000Over() throws GnomesAppException, NoSuchMethodException, SecurityException,
        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String message = RandomStringUtils.randomAlphabetic(2100);
        K01CPrintOutCallbackService service = new K01CPrintOutCallbackService();
        Method method = K01CPrintOutCallbackService.class.getDeclaredMethod("editPrintoutErrorMessage", String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(service, message);
        assertTrue(result.length() == 2000);
    }

    @Test
    @Disabled
    @DisplayName("印字エラーメッセージ編集：2001文字のとき2000文字目までを返却する")
    void test_editPrintoutErrorMessage_2001() throws GnomesAppException, NoSuchMethodException, SecurityException,
        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String message = RandomStringUtils.randomAlphabetic(2001);
        K01CPrintOutCallbackService service = new K01CPrintOutCallbackService();
        Method method = K01CPrintOutCallbackService.class.getDeclaredMethod("editPrintoutErrorMessage", String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(service, message);
        assertTrue(result.length() == 2000);
    }

    @Test
    @Disabled
    @DisplayName("印字エラーメッセージ編集：1999文字のとき1999文字を返却する")
    void test_editPrintoutErrorMessage_1999() throws GnomesAppException, NoSuchMethodException, SecurityException,
        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String message = RandomStringUtils.randomAlphabetic(1999);
        K01CPrintOutCallbackService service = new K01CPrintOutCallbackService();
        Method method = K01CPrintOutCallbackService.class.getDeclaredMethod("editPrintoutErrorMessage", String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(service, message);
        assertTrue(result.length() == 1999);
    }

    private PrintOutCallbackInfo getPrintOutCallbackInfo() {
        PrintOutCallbackInfo callbackInfo = new PrintOutCallbackInfo();
        callbackInfo.setReportId(REPORT_ID_XLSX);
        callbackInfo.setPathName(tempDir.toString());
        callbackInfo.setPdfFileName("ut-gnomes-renameTo.pdf");
        callbackInfo.setPrintType("1");
        return callbackInfo;
    }

}
