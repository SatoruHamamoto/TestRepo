package com.gnomes.external.logic.talend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

import com.gnomes.TestUtil;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.external.data.FileTransferBean;

class FileRenameJobTest {

    private FileRenameJob job;

    private FileTransferBean fileTransferBean;

    private MockedStatic<MessagesHandler> msgHandlerMock;

    private MockedStatic<ResourcesHandler> resHandlerMock;

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
        job = TestUtil.createBean(FileRenameJob.class);
        msgHandlerMock = Mockito.mockStatic(MessagesHandler.class);
        resHandlerMock = Mockito.mockStatic(ResourcesHandler.class);

        String renameFileName = "test-rename.csv";
        Path testRenamePath = tempDir.resolve("rename");
        fileTransferBean = new FileTransferBean();
        fileTransferBean.setSendRecvFileName(renameFileName);
        fileTransferBean.setMoveFromFolderName(testRenamePath.toString());
        Files.createDirectory(testRenamePath);
        Whitebox.setInternalState(job, "fileTransferBean", fileTransferBean);

        msgHandlerMock.when(() -> MessagesHandler.getString(anyString(), any(Object.class))).then(createMsgAnswer(0));
        resHandlerMock.when(() -> ResourcesHandler.getString(anyString())).then(createMsgAnswer(0));

        GnomesExceptionFactory exceptionFactory = new GnomesExceptionFactory();
        Whitebox.setInternalState(job, "exceptionFactory", exceptionFactory);
    }

    @AfterEach
    void tearDown() throws Exception {
        msgHandlerMock.close();
        resHandlerMock.close();
    }

    @Test
    @DisplayName("ファイル名リネーム：参照元ファイルが存在しない場合エラー")
    void test_process_moveFromFile_exists_false() throws Exception {
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> job.process());

        assertEquals(e.getMessageNo(), GnomesMessagesConstants.ME01_0044);
        assertNotNull(fileTransferBean.getErrorLineInfo());
    }

    @Test
    @DisplayName("ファイル名リネーム：正常終了")
    void test_process_success() throws Exception {
        String fromFileName = fileTransferBean.getSendRecvFileName();
        Path removeFilePath = Paths.get(fileTransferBean.getMoveFromFolderName(), fileTransferBean.getSendRecvFileName());
        Files.createFile(removeFilePath);

        try {
            job.process();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        assertFalse(Files.exists(removeFilePath));
        assertNull(fileTransferBean.getErrorLineInfo());
        assertNotEquals(fromFileName, fileTransferBean.getSendRecvFileName());
    }

    private Answer<String> createMsgAnswer(int argIndex){
        return (Answer<String>) v ->{
            Object[] args = v.getArguments();
            String result = (String)args[argIndex];
            return result;
        };
    }

}
