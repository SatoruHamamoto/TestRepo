package com.gnomes.system.logic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.gnomes.system.data.printout.PrintOutCallbackInfo;

class K01CPrintOutCallbackProcessTest {

    private final static String REPRINT_FILE_NAME = "re-print.xlsx";

    private final static String TARGET_METHOD = "deleteRePrintExcelFile";

    @InjectMocks
    K01CPrintOutCallbackProcess process;

    @Mock
    PrintOutCallbackInfo callbackInfo;

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
    @DisplayName("再印字対象電子ファイル削除：正常終了")
    void test_deleteRePrintExcelFile() throws Exception {
        doReturn(tempDir.toString()).when(callbackInfo).getPathName();
        doReturn(REPRINT_FILE_NAME).when(callbackInfo).getRePrintFileName();
        Path rePrintPath = tempDir.resolve(REPRINT_FILE_NAME);
        Files.createFile(rePrintPath);

        Method method = K01CPrintOutCallbackProcess.class.getDeclaredMethod(TARGET_METHOD);
        method.setAccessible(true);

        if (!Files.exists(rePrintPath)) {
            fail();
        }
        method.invoke(process);
        assertTrue(!Files.exists(rePrintPath));
    }

    @Test
    @DisplayName("再印字対象電子ファイル削除：パス名が取得できないときは処理を終了する")
    void test_deleteRePrintExcelFile_cannot_getPath() throws Exception {
        doReturn(null).when(callbackInfo).getPathName();
        doReturn(REPRINT_FILE_NAME).when(callbackInfo).getRePrintFileName();
        Path rePrintPath = tempDir.resolve(REPRINT_FILE_NAME);
        Files.createFile(rePrintPath);

        Method method = K01CPrintOutCallbackProcess.class.getDeclaredMethod(TARGET_METHOD);
        method.setAccessible(true);

        if (!Files.exists(rePrintPath)) {
            fail();
        }
        method.invoke(process);
        assertTrue(Files.exists(rePrintPath));
    }

    @Test
    @DisplayName("再印字対象電子ファイル削除：ファイル名がnullの場合は削除されない")
    void test_deleteRePrintExcelFile_fileName_null() throws Exception {
        doReturn(tempDir.toString()).when(callbackInfo).getPathName();
        doReturn(null).when(callbackInfo).getRePrintFileName();
        Path rePrintPath = tempDir.resolve(REPRINT_FILE_NAME);
        Files.createFile(rePrintPath);

        Method method = K01CPrintOutCallbackProcess.class.getDeclaredMethod(TARGET_METHOD);
        method.setAccessible(true);

        if (!Files.exists(rePrintPath)) {
            fail();
        }
        method.invoke(process);
        assertTrue(Files.exists(rePrintPath));
    }

    @Test
    @DisplayName("再印字対象電子ファイル削除：ファイル名が空文字の場合はフォルダが削除される")
    void test_deleteRePrintExcelFile_fileName_empty() throws Exception {
        doReturn(tempDir.toString()).when(callbackInfo).getPathName();
        doReturn("").when(callbackInfo).getRePrintFileName();
        Path rePrintPath = tempDir.resolve(REPRINT_FILE_NAME);
        Files.createFile(rePrintPath);

        Method method = K01CPrintOutCallbackProcess.class.getDeclaredMethod(TARGET_METHOD);
        method.setAccessible(true);

        if (!Files.exists(rePrintPath)) {
            fail();
        }
        method.invoke(process);
        assertTrue(!Files.exists(rePrintPath));
        assertTrue(!Files.exists(rePrintPath.getParent()));
    }

}
