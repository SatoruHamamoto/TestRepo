package com.gnomes.system.logic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
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

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.gnomes.common.data.GnomesSystemBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.uiservice.ContainerRequest;

import biz.grandsight.ex.rs.CGenReportMeta;

class PrintOutLogicTest {

    private static final String METHOD_GET_PATHNAME = "getPathName";
    private static final String METHOD_INIT_CREPORT_GEN = "initCReportGen";
    private static final String METHOD_INIT_CREPORT_GEN_MULTIPLE = "initCReportGenMultiple";
    private static final String METHOD_INIT_CREPORT_GEN_MULTIPLE_INVENTORY_LIST = "initCReportGenMultipleInventoryList";
    private static final String METHOD_INIT_CREPORT_GEN_MULTI_STAGE = "initCReportGenMultiStage";
    private static final String METHOD_INIT_CREPORT_GEN_MULTI_STAGE_NO_NEW_PAGE = "initCReportGenMultiStageNoNewPage";
    private static final String METHOD_INIT_CREPORT_GEN_MULTIPLE_MULTI_STAGE = "initCReportGenMultipleMultiStage";
    private static final String METHOD_EDIT_ERROR_MESSAGE = "editPrintoutErrorMessage";

    @InjectMocks
    PrintOutLogic logic;

    @Mock
    GnomesSystemBean gnomesSystemBean;

    @Mock
    ContainerRequest req;

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
    @DisplayName("パス名取得：パスが取得できる")
    void test_getPathName() throws Exception {
        Method method = PrintOutLogic.class.getDeclaredMethod(METHOD_GET_PATHNAME);
        method.setAccessible(true);

        Path targetDirectoryPath = tempDir.resolve("target-directory");
        Path xmlPath = tempDir.resolve("gnomes-temp-def.xml");
        Files.createFile(xmlPath);
        createXmlDefinitionFile(xmlPath, "target-directory");

        Mockito.doReturn(xmlPath.toString()).when(gnomesSystemBean).getReportDefinitionXMLFileName();

        String pathName = (String) method.invoke(logic);

        assertTrue(!StringUtils.isEmpty(pathName));
        assertEquals(pathName, targetDirectoryPath.toString());
    }

    @Test
    @DisplayName("パス名取得：定義ファイルからtarget-directoryが参照できない")
    void test_getPathName_targetDirectory_exsits_false() throws Exception {
        Method method = PrintOutLogic.class.getDeclaredMethod(METHOD_GET_PATHNAME);
        method.setAccessible(true);

        Path xmlPath = tempDir.resolve("gnomes-temp-def.xml");
        Files.createFile(xmlPath);
        createXmlDefinitionFile(xmlPath, "template-directory");

        Mockito.doReturn(xmlPath.toString()).when(gnomesSystemBean).getReportDefinitionXMLFileName();
        Mockito.doReturn(new GnomesAppException(null, GnomesMessagesConstants.ME01_0001, new Object[10]))
            .when(exceptionFactory).createGnomesAppException(Mockito.anyString(),
                Mockito.anyString(), Mockito.any(Object[].class));

        String pathName = (String) method.invoke(logic);

        // Exceptionが発生したら空文字が返却される
        assertTrue(StringUtils.isEmpty(pathName));
    }

    @Test
    @DisplayName("パス名取得：定義ファイルのtarget-directoryタグから子要素が取得できない")
    void test_getPathName_targetDirectory_child_exsits_false() throws Exception {
        Method method = PrintOutLogic.class.getDeclaredMethod(METHOD_GET_PATHNAME);
        method.setAccessible(true);

        Path xmlPath = tempDir.resolve("gnomes-temp-def.xml");
        Files.createFile(xmlPath);
        createErrorXmlDefinitionFile(xmlPath, "target-directory");

        Mockito.doReturn(xmlPath.toString()).when(gnomesSystemBean).getReportDefinitionXMLFileName();
        Mockito.doReturn(new GnomesAppException(null, GnomesMessagesConstants.ME01_0001, new Object[10]))
            .when(exceptionFactory).createGnomesAppException(Mockito.anyString(),
                Mockito.anyString(), Mockito.any(Object[].class));

        String pathName = (String) method.invoke(logic);

        // Exceptionが発生したら空文字が返却される
        assertTrue(StringUtils.isEmpty(pathName));
    }

    @Test
    @DisplayName("パス名取得：定義ファイルが見つからない")
    void test_getPathName_xml_exsits_false() throws Exception {
        Method method = PrintOutLogic.class.getDeclaredMethod(METHOD_GET_PATHNAME);
        method.setAccessible(true);

        Path xmlPath = tempDir.resolve("gnomes-temp-def.xml");

        Mockito.doReturn(xmlPath.toString()).when(gnomesSystemBean).getReportDefinitionXMLFileName();

        String pathName = (String) method.invoke(logic);

        // Exceptionが発生したら空文字が返却される
        assertTrue(StringUtils.isEmpty(pathName));
    }

    @Test
    @DisplayName("CReportGen初期化：「1：通常領域」から帳票印刷情報を取得する")
    void test_initCReportGen_dbAreaDiv_nomal() throws Exception {
        int dbAreaDiv = 1;

        Method method = PrintOutLogic.class.getDeclaredMethod(METHOD_INIT_CREPORT_GEN, int.class);
        method.setAccessible(true);

        Mockito.doReturn(new CGenReportMeta()).when(gnomesSystemBean).getcGenReportMeta();
        Mockito.doReturn(new CGenReportMeta()).when(gnomesSystemBean).getcGenReportMetaStorage();

        method.invoke(logic, dbAreaDiv);

        verify(gnomesSystemBean, times(1)).getcGenReportMeta();
        verify(gnomesSystemBean, times(0)).getcGenReportMetaStorage();
    }

    @Test
    @DisplayName("CReportGen初期化：「2：保管領域」から帳票印刷情報を取得する")
    void test_initCReportGen_dbAreaDiv_storage() throws Exception {
        int dbAreaDiv = 2;

        Method method = PrintOutLogic.class.getDeclaredMethod(METHOD_INIT_CREPORT_GEN, int.class);
        method.setAccessible(true);

        Mockito.doReturn(new CGenReportMeta()).when(gnomesSystemBean).getcGenReportMeta();
        Mockito.doReturn(new CGenReportMeta()).when(gnomesSystemBean).getcGenReportMetaStorage();

        method.invoke(logic, dbAreaDiv);

        verify(gnomesSystemBean, times(0)).getcGenReportMeta();
        verify(gnomesSystemBean, times(1)).getcGenReportMetaStorage();
    }

    @Test
    @DisplayName("CReportGen初期化：通常領域も保管領域でも指定ない場合はデフォルトで通常領域から帳票印刷情報を取得する")
    void test_initCReportGen_dbAreaDiv_default() throws Exception {
        int dbAreaDiv = 3;

        Method method = PrintOutLogic.class.getDeclaredMethod(METHOD_INIT_CREPORT_GEN, int.class);
        method.setAccessible(true);

        Mockito.doReturn(new CGenReportMeta()).when(gnomesSystemBean).getcGenReportMeta();
        Mockito.doReturn(new CGenReportMeta()).when(gnomesSystemBean).getcGenReportMetaStorage();

        method.invoke(logic, dbAreaDiv);

        verify(gnomesSystemBean, times(1)).getcGenReportMeta();
        verify(gnomesSystemBean, times(0)).getcGenReportMetaStorage();
    }

    @Test
    @DisplayName("CReportGen（多重）初期化：「1：通常領域」から帳票印刷情報を取得する")
    void test_initCReportGenMultiple_dbAreaDiv_nomal() throws Exception {
        int dbAreaDiv = 1;

        Method method = PrintOutLogic.class.getDeclaredMethod(METHOD_INIT_CREPORT_GEN_MULTIPLE, int.class);
        method.setAccessible(true);

        Mockito.doReturn(new biz.grandsight.ex.rs_multiple.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultipleMeta();
        Mockito.doReturn(new biz.grandsight.ex.rs_multiple.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultipleMetaStorage();

        method.invoke(logic, dbAreaDiv);

        verify(gnomesSystemBean, times(1)).getcGenReportMultipleMeta();
        verify(gnomesSystemBean, times(0)).getcGenReportMultipleMetaStorage();
    }

    @Test
    @DisplayName("CReportGen（多重）初期化：「2：保管領域」から帳票印刷情報を取得する")
    void test_initCReportGenMultiple_dbAreaDiv_storage() throws Exception {
        int dbAreaDiv = 2;

        Method method = PrintOutLogic.class.getDeclaredMethod(METHOD_INIT_CREPORT_GEN_MULTIPLE, int.class);
        method.setAccessible(true);

        Mockito.doReturn(new biz.grandsight.ex.rs_multiple.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultipleMeta();
        Mockito.doReturn(new biz.grandsight.ex.rs_multiple.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultipleMetaStorage();

        method.invoke(logic, dbAreaDiv);

        verify(gnomesSystemBean, times(0)).getcGenReportMultipleMeta();
        verify(gnomesSystemBean, times(1)).getcGenReportMultipleMetaStorage();
    }

    @Test
    @DisplayName("CReportGen（多重）初期化：通常領域も保管領域でも指定ない場合はデフォルトで通常領域から帳票印刷情報を取得する")
    void test_initCReportGenMultiple_dbAreaDiv_default() throws Exception {
        int dbAreaDiv = 3;

        Method method = PrintOutLogic.class.getDeclaredMethod(METHOD_INIT_CREPORT_GEN_MULTIPLE, int.class);
        method.setAccessible(true);

        Mockito.doReturn(new biz.grandsight.ex.rs_multiple.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultipleMeta();
        Mockito.doReturn(new biz.grandsight.ex.rs_multiple.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultipleMetaStorage();

        method.invoke(logic, dbAreaDiv);

        verify(gnomesSystemBean, times(1)).getcGenReportMultipleMeta();
        verify(gnomesSystemBean, times(0)).getcGenReportMultipleMetaStorage();
    }

    @Test
    @DisplayName("CReportGen（多重棚卸一覧）初期化：「1：通常領域」から帳票印刷情報を取得する")
    void test_initCReportGenMultipleInventoryList_dbAreaDiv_nomal() throws Exception {
        int dbAreaDiv = 1;

        Method method = PrintOutLogic.class.getDeclaredMethod(METHOD_INIT_CREPORT_GEN_MULTIPLE_INVENTORY_LIST, int.class);
        method.setAccessible(true);

        Mockito.doReturn(new biz.grandsight.ex.rs_multiple21.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultipleInventoryMeta();
        Mockito.doReturn(new biz.grandsight.ex.rs_multiple21.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultipleInventoryMetaStorage();

        method.invoke(logic, dbAreaDiv);

        verify(gnomesSystemBean, times(1)).getcGenReportMultipleInventoryMeta();
        verify(gnomesSystemBean, times(0)).getcGenReportMultipleInventoryMetaStorage();
    }

    @Test
    @DisplayName("CReportGen（多重棚卸一覧）初期化：「2：保管領域」から帳票印刷情報を取得する")
    void test_initCReportGenMultipleInventoryList_dbAreaDiv_storage() throws Exception {
        int dbAreaDiv = 2;

        Method method = PrintOutLogic.class.getDeclaredMethod(METHOD_INIT_CREPORT_GEN_MULTIPLE_INVENTORY_LIST, int.class);
        method.setAccessible(true);

        Mockito.doReturn(new biz.grandsight.ex.rs_multiple21.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultipleInventoryMeta();
        Mockito.doReturn(new biz.grandsight.ex.rs_multiple21.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultipleInventoryMetaStorage();

        method.invoke(logic, dbAreaDiv);

        verify(gnomesSystemBean, times(0)).getcGenReportMultipleInventoryMeta();
        verify(gnomesSystemBean, times(1)).getcGenReportMultipleInventoryMetaStorage();
    }

    @Test
    @DisplayName("CReportGen（多重棚卸一覧）初期化：通常領域も保管領域でも指定ない場合はデフォルトで通常領域から帳票印刷情報を取得する")
    void test_initCReportGenMultipleInventoryList_dbAreaDiv_default() throws Exception {
        int dbAreaDiv = 3;

        Method method = PrintOutLogic.class.getDeclaredMethod(METHOD_INIT_CREPORT_GEN_MULTIPLE_INVENTORY_LIST, int.class);
        method.setAccessible(true);

        Mockito.doReturn(new biz.grandsight.ex.rs_multiple21.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultipleInventoryMeta();
        Mockito.doReturn(new biz.grandsight.ex.rs_multiple21.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultipleInventoryMetaStorage();

        method.invoke(logic, dbAreaDiv);

        verify(gnomesSystemBean, times(1)).getcGenReportMultipleInventoryMeta();
        verify(gnomesSystemBean, times(0)).getcGenReportMultipleInventoryMetaStorage();
    }

    @Test
    @DisplayName("CReportGen（多段）初期化：「1：通常領域」から帳票印刷情報を取得する")
    void test_initCReportGenMultiStage_dbAreaDiv_nomal() throws Exception {
        int dbAreaDiv = 1;

        Method method = PrintOutLogic.class.getDeclaredMethod(METHOD_INIT_CREPORT_GEN_MULTI_STAGE, int.class);
        method.setAccessible(true);

        Mockito.doReturn(new biz.grandsight.ex.rs_multistage.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultiStageMeta();
        Mockito.doReturn(new biz.grandsight.ex.rs_multistage.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultiStageMetaStorage();

        method.invoke(logic, dbAreaDiv);

        verify(gnomesSystemBean, times(1)).getcGenReportMultiStageMeta();
        verify(gnomesSystemBean, times(0)).getcGenReportMultiStageMetaStorage();
    }

    @Test
    @DisplayName("CReportGen（多段）初期化：「2：保管領域」から帳票印刷情報を取得する")
    void test_initCReportGenMultiStage_dbAreaDiv_storage() throws Exception {
        int dbAreaDiv = 2;

        Method method = PrintOutLogic.class.getDeclaredMethod(METHOD_INIT_CREPORT_GEN_MULTI_STAGE, int.class);
        method.setAccessible(true);

        Mockito.doReturn(new biz.grandsight.ex.rs_multistage.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultiStageMeta();
        Mockito.doReturn(new biz.grandsight.ex.rs_multistage.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultiStageMetaStorage();

        method.invoke(logic, dbAreaDiv);

        verify(gnomesSystemBean, times(0)).getcGenReportMultiStageMeta();
        verify(gnomesSystemBean, times(1)).getcGenReportMultiStageMetaStorage();
    }

    @Test
    @DisplayName("CReportGen（多段）初期化：通常領域も保管領域でも指定ない場合はデフォルトで通常領域から帳票印刷情報を取得する")
    void test_initCReportGenMultiStage_dbAreaDiv_default() throws Exception {
        int dbAreaDiv = 3;

        Method method = PrintOutLogic.class.getDeclaredMethod(METHOD_INIT_CREPORT_GEN_MULTI_STAGE, int.class);
        method.setAccessible(true);

        Mockito.doReturn(new biz.grandsight.ex.rs_multistage.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultiStageMeta();
        Mockito.doReturn(new biz.grandsight.ex.rs_multistage.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultiStageMetaStorage();

        method.invoke(logic, dbAreaDiv);

        verify(gnomesSystemBean, times(1)).getcGenReportMultiStageMeta();
        verify(gnomesSystemBean, times(0)).getcGenReportMultiStageMetaStorage();
    }

    @Test
    @DisplayName("CReportGen（多段キー変更改ページ無し）初期化：「1：通常領域」から帳票印刷情報を取得する")
    void test_initCReportGenMultiStageNoNewPage_dbAreaDiv_nomal() throws Exception {
        int dbAreaDiv = 1;

        Method method = PrintOutLogic.class.getDeclaredMethod(METHOD_INIT_CREPORT_GEN_MULTI_STAGE_NO_NEW_PAGE, int.class);
        method.setAccessible(true);

        Mockito.doReturn(new biz.grandsight.ex.rs_multistage41.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultiStageNoNewPageMeta();
        Mockito.doReturn(new biz.grandsight.ex.rs_multistage41.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultiStageNoNewPageMetaStorage();

        method.invoke(logic, dbAreaDiv);

        verify(gnomesSystemBean, times(1)).getcGenReportMultiStageNoNewPageMeta();
        verify(gnomesSystemBean, times(0)).getcGenReportMultiStageNoNewPageMetaStorage();
    }

    @Test
    @DisplayName("CReportGen（多段キー変更改ページ無し）初期化：「2：保管領域」から帳票印刷情報を取得する")
    void test_initCReportGenMultiStageNoNewPage_dbAreaDiv_storage() throws Exception {
        int dbAreaDiv = 2;

        Method method = PrintOutLogic.class.getDeclaredMethod(METHOD_INIT_CREPORT_GEN_MULTI_STAGE_NO_NEW_PAGE, int.class);
        method.setAccessible(true);

        Mockito.doReturn(new biz.grandsight.ex.rs_multistage41.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultiStageNoNewPageMeta();
        Mockito.doReturn(new biz.grandsight.ex.rs_multistage41.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultiStageNoNewPageMetaStorage();

        method.invoke(logic, dbAreaDiv);

        verify(gnomesSystemBean, times(0)).getcGenReportMultiStageNoNewPageMeta();
        verify(gnomesSystemBean, times(1)).getcGenReportMultiStageNoNewPageMetaStorage();
    }

    @Test
    @DisplayName("CReportGen（多段キー変更改ページ無し）初期化：通常領域も保管領域でも指定ない場合はデフォルトで通常領域から帳票印刷情報を取得する")
    void test_initCReportGenMultiStageNoNewPage_dbAreaDiv_default() throws Exception {
        int dbAreaDiv = 3;

        Method method = PrintOutLogic.class.getDeclaredMethod(METHOD_INIT_CREPORT_GEN_MULTI_STAGE_NO_NEW_PAGE, int.class);
        method.setAccessible(true);

        Mockito.doReturn(new biz.grandsight.ex.rs_multistage41.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultiStageNoNewPageMeta();
        Mockito.doReturn(new biz.grandsight.ex.rs_multistage41.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultiStageNoNewPageMetaStorage();

        method.invoke(logic, dbAreaDiv);

        verify(gnomesSystemBean, times(1)).getcGenReportMultiStageNoNewPageMeta();
        verify(gnomesSystemBean, times(0)).getcGenReportMultiStageNoNewPageMetaStorage();
    }

    @Test
    @DisplayName("CReportGen（多重多段）初期化：「1：通常領域」から帳票印刷情報を取得する")
    void test_initCReportGenMultipleMultiStage_dbAreaDiv_nomal() throws Exception {
        int dbAreaDiv = 1;

        Method method = PrintOutLogic.class.getDeclaredMethod(METHOD_INIT_CREPORT_GEN_MULTIPLE_MULTI_STAGE, int.class);
        method.setAccessible(true);

        Mockito.doReturn(new biz.grandsight.ex.rs_multiplemultistage.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultipleMultiStageMeta();
        Mockito.doReturn(new biz.grandsight.ex.rs_multiplemultistage.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultipleMultiStageMetaStorage();

        method.invoke(logic, dbAreaDiv);

        verify(gnomesSystemBean, times(1)).getcGenReportMultipleMultiStageMeta();
        verify(gnomesSystemBean, times(0)).getcGenReportMultipleMultiStageMetaStorage();
    }

    @Test
    @DisplayName("CReportGen（多重多段）初期化：「2：保管領域」から帳票印刷情報を取得する")
    void test_initCReportGenMultipleMultiStage_dbAreaDiv_storage() throws Exception {
        int dbAreaDiv = 2;

        Method method = PrintOutLogic.class.getDeclaredMethod(METHOD_INIT_CREPORT_GEN_MULTIPLE_MULTI_STAGE, int.class);
        method.setAccessible(true);

        Mockito.doReturn(new biz.grandsight.ex.rs_multiplemultistage.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultipleMultiStageMeta();
        Mockito.doReturn(new biz.grandsight.ex.rs_multiplemultistage.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultipleMultiStageMetaStorage();

        method.invoke(logic, dbAreaDiv);

        verify(gnomesSystemBean, times(0)).getcGenReportMultipleMultiStageMeta();
        verify(gnomesSystemBean, times(1)).getcGenReportMultipleMultiStageMetaStorage();
    }

    @Test
    @DisplayName("CReportGen（多重多段）初期化：通常領域も保管領域でも指定ない場合はデフォルトで通常領域から帳票印刷情報を取得する")
    void test_initCReportGenMultipleMultiStage_dbAreaDiv_default() throws Exception {
        int dbAreaDiv = 3;

        Method method = PrintOutLogic.class.getDeclaredMethod(METHOD_INIT_CREPORT_GEN_MULTIPLE_MULTI_STAGE, int.class);
        method.setAccessible(true);

        Mockito.doReturn(new biz.grandsight.ex.rs_multiplemultistage.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultipleMultiStageMeta();
        Mockito.doReturn(new biz.grandsight.ex.rs_multiplemultistage.CGenReportMeta()).when(gnomesSystemBean).getcGenReportMultipleMultiStageMetaStorage();

        method.invoke(logic, dbAreaDiv);

        verify(gnomesSystemBean, times(1)).getcGenReportMultipleMultiStageMeta();
        verify(gnomesSystemBean, times(0)).getcGenReportMultipleMultiStageMetaStorage();
    }

    @Test
    @DisplayName("印字エラーメッセージ編集：2000文字を超えたら2000文字目までを返却する")
    void test_editPrintoutErrorMessage_2000Over() throws GnomesAppException, NoSuchMethodException, SecurityException,
        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String message = RandomStringUtils.randomAlphabetic(2100);
        Method method = PrintOutLogic.class.getDeclaredMethod(METHOD_EDIT_ERROR_MESSAGE, String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(logic, message);
        assertTrue(result.length() == 2000);
    }

    @Test
    @DisplayName("印字エラーメッセージ編集：2001文字のとき2000文字目までを返却する")
    void test_editPrintoutErrorMessage_2001() throws GnomesAppException, NoSuchMethodException, SecurityException,
        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String message = RandomStringUtils.randomAlphabetic(2001);
        Method method = PrintOutLogic.class.getDeclaredMethod(METHOD_EDIT_ERROR_MESSAGE, String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(logic, message);
        assertTrue(result.length() == 2000);
    }

    @Test
    @DisplayName("印字エラーメッセージ編集：1999文字のとき1999文字を返却する")
    void test_editPrintoutErrorMessage_1999() throws GnomesAppException, NoSuchMethodException, SecurityException,
        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String message = RandomStringUtils.randomAlphabetic(1999);
        Method method = PrintOutLogic.class.getDeclaredMethod(METHOD_EDIT_ERROR_MESSAGE, String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(logic, message);
        assertTrue(result.length() == 1999);
    }

    /**
     * XML定義ファイル作成
     * @param xmlPath ファイルパス
     * @param directoryName ディレクトリ名
     * @throws ParserConfigurationException
     * @throws TransformerException
     * @throws IOException
     */
    private void createXmlDefinitionFile(Path xmlPath, String directoryName) throws ParserConfigurationException, TransformerException, IOException {
        Path targetDirectoryPath = tempDir.resolve(directoryName);

        Document document = createXmlDocument(directoryName);
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
     * エラー発生用 XML定義ファイル作成
     * @param xmlPath ファイルパス
     * @param directoryName ディレクトリ名
     * @throws ParserConfigurationException
     * @throws TransformerException
     * @throws IOException
     */
    private void createErrorXmlDefinitionFile(Path xmlPath, String directoryName) throws ParserConfigurationException, TransformerException, IOException {
        Document document = createXmlDocument(directoryName);

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

}
