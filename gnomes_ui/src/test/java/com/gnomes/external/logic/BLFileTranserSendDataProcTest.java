package com.gnomes.external.logic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

import com.gnomes.TestUtil;
import com.gnomes.common.constants.CommonEnums.SendRecvStateType;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.util.PessimisticLockSession;
import com.gnomes.external.data.DataDefine;
import com.gnomes.external.data.FileDefine;
import com.gnomes.external.data.FileTransferBean;
import com.gnomes.external.entity.ExternalIfSendDataDetail;
import com.gnomes.external.entity.QueueExternalIfSendStatus;
import com.gnomes.uiservice.ContainerRequest;

class BLFileTranserSendDataProcTest {

    private final static String HEADER_LINE = "TEST_HEADER_LINE";

    private BLFileTranserSendDataProc proc;

    private FileTransferBean fileTransferBean;
    private EntityManager em;
    private QueueExternalIfSendStatus sendStatus;
    private FileDefine fileDefine;
    private MockedStatic<MessagesHandler> msgHandlerMock;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() throws Exception {
        proc = TestUtil.createBean(BLFileTranserSendDataProc.class);
        em = mock(EntityManager.class);
        msgHandlerMock = Mockito.mockStatic(MessagesHandler.class);

        fileTransferBean = new FileTransferBean();
        // 成功フラグONに設定
        fileTransferBean.setSuccessFlag(true);
        // スキップフラグOFFに設定
        fileTransferBean.setSkipFlag(false);
        // キュー存在フラグONに設定
        fileTransferBean.setQuereExistFlag(true);
        Whitebox.setInternalState(proc, "fileTransferBean", fileTransferBean);
        Whitebox.setInternalState(proc, "exceptionFactory", new GnomesExceptionFactory());

        doReturn(em).when(proc.lockSession).getEntityManager();

        msgHandlerMock.when(() -> MessagesHandler
            .getString(anyString(), any(Object.class))).then(createMsgAnswer(0));
        msgHandlerMock.when(() -> MessagesHandler
            .getExceptionMessage(any(ContainerRequest.class), any(GnomesAppException.class))).then(createErrMsgAnswer(1));
    }

    @AfterEach
    void tearDown() throws Exception {
        msgHandlerMock.close();
    }

    @Test
    @DisplayName("送信キュー1件分の処理：実行中のキューが抜けない場合は処理を終了しFalseを返す")
    void test_QueueExternalIfSendDataProc_fail_runningTimeout_true() throws Exception {
        setupMockWaitAndCheckForRunningSendQueue(true);

        boolean result = proc.QueueExternalIfSendDataProc(null, "", 0, 3000, null);

        assertFalse(result);
        assertTrue(fileTransferBean.isSkipFlag());
    }

    @ParameterizedTest
    @MethodSource("versionParameter")
    @DisplayName("送信キュー1件分の処理：処理対象のキューがNullかバージョン違いの場合は処理を終了しTrueを返す")
    void test_QueueExternalIfSendDataProc_fail_sendStatus_null(Integer version) throws Exception {
        setupMockWaitAndCheckForRunningSendQueue(false);
        setupMockGetExternalIfSendStatusLock(version == null ? true : false);
        QueueExternalIfSendStatus data = getQueueExternalIfSendStatus(version != null ? version : 1);

        boolean result = proc.QueueExternalIfSendDataProc(data, "", 0, 3000, null);

        assertTrue(result);
        assertFalse(fileTransferBean.isSkipFlag());
        verify(proc.lockSession, times(1)).rollback();
    }

    @Test
    @DisplayName("送信キュー1件分の処理：外部I/Fファイル構成定義取得中にエラーが発生した場合は処理を終了しFalseを返す")
    void test_QueueExternalIfSendDataProc_fail_getFileDefine_exception() throws Exception {
        setupMockWaitAndCheckForRunningSendQueue(false);
        setupMockGetExternalIfSendStatusLock(false);
        setupMockGetFileDefine(true, 1, 1, false);
        QueueExternalIfSendStatus data = getQueueExternalIfSendStatus(1);

        boolean result = proc.QueueExternalIfSendDataProc(data, "", 0, 3000, null);

        assertFalse(result);
        assertFalse(fileTransferBean.isSkipFlag());
        // Throwされていないのでコミットされる
        verify(proc.lockSession, times(1)).commit();

        assertEquals(fileTransferBean.getErrorComment(), GnomesMessagesConstants.ME01_0107);
        assertEquals(fileTransferBean.getStatus(), SendRecvStateType.Failed);
        assertFalse(fileTransferBean.isSuccessFlag());
    }

    @Test
    @DisplayName("送信キュー1件分の処理：対象のファイル種別が使用不可の場合は処理を終了しFalseを返す")
    void test_QueueExternalIfSendDataProc_fail_isUsable_no() throws Exception {
        setupMockWaitAndCheckForRunningSendQueue(false);
        setupMockGetExternalIfSendStatusLock(false);
        setupMockGetFileDefine(false, 0, 1, false);
        QueueExternalIfSendStatus data = getQueueExternalIfSendStatus(1);

        boolean result = proc.QueueExternalIfSendDataProc(data, "", 0, 3000, null);

        assertFalse(result);
        assertFalse(fileTransferBean.isSkipFlag());
        // Throwされていないのでコミットされる
        verify(proc.lockSession, times(1)).commit();

        assertEquals(fileTransferBean.getErrorComment(), GnomesMessagesConstants.ME01_0129);
        assertEquals(fileTransferBean.getStatus(), SendRecvStateType.Failed);
        assertFalse(fileTransferBean.isSuccessFlag());
    }

    @Test
    @DisplayName("送信キュー1件分の処理：対象のファイル種別がエラー時に処理を中断する場合は処理を終了しTrueを返す")
    void test_QueueExternalIfSendDataProc_success_isContinueError_stop() throws Exception {
        setupMockWaitAndCheckForRunningSendQueue(false);
        setupMockGetExternalIfSendStatusLock(false);
        setupMockGetFileDefine(false, 1, 0, false);
        setupMockGetSendStateQuery();
        QueueExternalIfSendStatus data = getQueueExternalIfSendStatus(1);

        boolean result = proc.QueueExternalIfSendDataProc(data, "", 0, 3000, null);

        assertTrue(result);
        assertFalse(fileTransferBean.isSkipFlag());
        verify(proc.lockSession, times(1)).commit();

        assertNull(fileTransferBean.getErrorComment());
        assertNull(fileTransferBean.getStatus());
    }

    @Test
    @DisplayName("送信キュー1件分の処理：送信処理中にException")
    void test_QueueExternalIfSendDataProc_fail_runSendErrorJobs_exception() throws Exception {
        setupMockWaitAndCheckForRunningSendQueue(false);
        setupMockGetExternalIfSendStatusLock(false);
        setupMockGetFileDefine(false, 1, 1, false);
        doThrow(GnomesAppException.class).when(proc.fileTransferCallTalend).runSendProcJobs();
        QueueExternalIfSendStatus data = getQueueExternalIfSendStatus(1);
        List<QueueExternalIfSendStatus> sendStateList = getSendStateList();

        boolean result = proc.QueueExternalIfSendDataProc(data, "", 0, 3000, sendStateList);

        assertTrue(result);
        assertFalse(fileTransferBean.isSkipFlag());
        // セッション再作成時も含めても最大2回コミットされている
        verify(proc.lockSession, atMost(2)).commit();

        assertNull(fileTransferBean.getErrorComment());
        // Exception発生時に取得したキューが設定されている
        assertNotEquals(fileTransferBean.getQueueExternalIfSendStatus(), sendStatus);
        assertEquals(fileTransferBean.getExternalIfSendDataDetail().size(), 5);
        assertEquals(fileTransferBean.getStatus(), SendRecvStateType.Failed);
        assertFalse(fileTransferBean.isSuccessFlag());
    }

    @Test
    @DisplayName("送信キュー1件分の処理：外部I/Fファイル構成定義にヘッダーフォーマットIDが存在しない場合ヘッダーは作成されない")
    void test_QueueExternalIfSendDataProc_fileDefine_header_false() throws Exception {
        setupMockWaitAndCheckForRunningSendQueue(false);
        setupMockGetExternalIfSendStatusLock(false);
        setupMockGetFileDefine(false, 1, 1, false);
        QueueExternalIfSendStatus data = getQueueExternalIfSendStatus(1);

        boolean result = proc.QueueExternalIfSendDataProc(data, "", 0, 3000, null);

        assertTrue(result);
        assertFalse(fileTransferBean.isSkipFlag());
        // セッション再作成時も含めても最大2回コミットされている
        verify(proc.lockSession, atMost(2)).commit();

        assertNull(fileTransferBean.getErrorComment());
        assertEquals(fileTransferBean.getQueueExternalIfSendStatus(), sendStatus);
        // ヘッダーが作成されていないのでサイズは3のまま
        assertEquals(fileTransferBean.getExternalIfSendDataDetail().size(), 3);
        // 正常に処理が終了したのでキューが続行されている
        assertEquals(fileTransferBean.getStatus(), SendRecvStateType.Running);
        assertTrue(fileTransferBean.isSuccessFlag());
    }

    @Test
    @DisplayName("送信キュー1件分の処理：外部I/Fファイル構成定義にヘッダーフォーマットIDが存在する場合ヘッダーが作成される")
    void test_QueueExternalIfSendDataProc_fileDefine_header_true() throws Exception {
        setupMockWaitAndCheckForRunningSendQueue(false);
        setupMockGetExternalIfSendStatusLock(false);
        setupMockGetFileDefine(false, 1, 1, true);
        QueueExternalIfSendStatus data = getQueueExternalIfSendStatus(1);

        boolean result = proc.QueueExternalIfSendDataProc(data, "", 0, 3000, null);

        assertTrue(result);
        assertFalse(fileTransferBean.isSkipFlag());
        // セッション再作成時も含めても最大2回コミットされている
        verify(proc.lockSession, atMost(2)).commit();

        assertNull(fileTransferBean.getErrorComment());
        assertEquals(fileTransferBean.getQueueExternalIfSendStatus(), sendStatus);
        // ヘッダーが作成されていないのでサイズは3→4に増える
        assertEquals(fileTransferBean.getExternalIfSendDataDetail().size(), 4);
        assertEquals(fileTransferBean.getExternalIfSendDataDetail().get(0).getLine_data(), HEADER_LINE);
        // 正常に処理が終了したのでキューが続行されている
        assertEquals(fileTransferBean.getStatus(), SendRecvStateType.Running);
        assertTrue(fileTransferBean.isSuccessFlag());
    }

    private void setupMockWaitAndCheckForRunningSendQueue(boolean bRunningTimeout) throws GnomesAppException {
        List<QueueExternalIfSendStatus> sendStateRunningList = new ArrayList<>();

        if (bRunningTimeout) {
            QueueExternalIfSendStatus status = new QueueExternalIfSendStatus();
            status.setFile_name("send-recv.csv");
            status.setFile_type(GnomesResourcesConstants.FileType_Csv);
            status.setSend_date(new Date());
            status.setSend_file_name("test-send.csv");
            sendStateRunningList.add(status);
        }

        doReturn(sendStateRunningList).when(proc.queueExternalIfSendStatusDao).getSendStateQuery(anyInt(), anyString(),
            any());
    }

    private void setupMockGetExternalIfSendStatusLock(boolean isNull) throws GnomesAppException {
        sendStatus = null;

        if (!isNull) {
            sendStatus = new QueueExternalIfSendStatus();
            sendStatus.setVersion(1);
            sendStatus.setExternalIfSendDataDetails(getExternalIfSendDataDetails(3));
            sendStatus.setSend_file_name("test-send-status.csv");
        }

        // 2回モックが呼ばれる場合、2回目は送信エラー用のキューを返す
        QueueExternalIfSendStatus errSendStatus = new QueueExternalIfSendStatus();
        errSendStatus.setExternalIfSendDataDetails(getExternalIfSendDataDetails(5));

        doReturn(sendStatus).doReturn(errSendStatus).when(proc.queueExternalIfSendStatusDao)
            .getExternalIfSendStatusLock(anyString(), any(PessimisticLockSession.class));
    }

    private void setupMockGetFileDefine(boolean isNull, int isUsable, int isContinueError, boolean isHeader) throws GnomesAppException {
        fileDefine = null;

        if (!isNull) {
            fileDefine = new FileDefine();
            fileDefine.setIs_usable(isUsable);
            fileDefine.setIs_continue_error(isContinueError);
            fileDefine.setCreate_file_encode("UTF-8");
            if (isHeader) {
                // ヘッダー有
                fileDefine.setHeader_format_id("test_header_format_id");
                setupMockCreateSendTelegramHeader();
            }
        }
        doReturn(fileDefine).when(proc.mstrExternalIfFileDefineDao).getFileDefine(any());
    }

    private void setupMockCreateSendTelegramHeader() throws GnomesAppException {
        setupMockGetDataDefine();
        doReturn(HEADER_LINE).when(proc.sendDataCreateHeader).process(anyList(), any(FileDefine.class),
            anyList());
    }

    private void setupMockGetDataDefine() throws GnomesAppException {
        List<DataDefine> dataDefineList = new ArrayList<DataDefine>();
        DataDefine dDefine = new DataDefine();
        dataDefineList.add(dDefine);
        doReturn(dataDefineList).when(proc.mstrExternalIfDataDefineDao).getDataDefineList(anyString());
    }

    private void setupMockGetSendStateQuery() throws GnomesAppException {
        List<QueueExternalIfSendStatus> sendQueNGList = new ArrayList<QueueExternalIfSendStatus>() {
            {
                add(new QueueExternalIfSendStatus());
            }
        };
        doReturn(sendQueNGList).when(proc.queueExternalIfSendStatusDao).getSendStateQuery(anyList(), anyString(),
            any());
    }

    private QueueExternalIfSendStatus getQueueExternalIfSendStatus(int version) {
        QueueExternalIfSendStatus data = new QueueExternalIfSendStatus();
        data.setExternal_if_target_code("test_External_if_target_code");
        data.setExternal_if_send_status_key("test_External_if_send_status_key");
        data.setFile_type(GnomesResourcesConstants.FileType_Csv);
        data.setVersion(version);
        return data;
    }

    private List<QueueExternalIfSendStatus> getSendStateList() {
        List<QueueExternalIfSendStatus> sendStateList = new ArrayList<>();
        QueueExternalIfSendStatus sendState = new QueueExternalIfSendStatus();
        sendState.setExternal_if_send_status_key("test_External_if_send_status_key");
        sendStateList.add(sendState);
        return sendStateList;
    }

    private Set<ExternalIfSendDataDetail> getExternalIfSendDataDetails(int count) {
        Set<ExternalIfSendDataDetail> details = new HashSet<>();

        for (int i = 0; i < count; i++) {
            ExternalIfSendDataDetail detail = new ExternalIfSendDataDetail();
            detail.setLine_number(i+1);
            details.add(detail);
        }

        return details;
    }

    private Answer<String> createMsgAnswer(int argIndex){
        return (Answer<String>) v ->{
            Object[] args = v.getArguments();
            String result = (String)args[argIndex];
            return result;
        };
    }

    private Answer<String> createErrMsgAnswer(int argIndex){
        return (Answer<String>) v ->{
            Object[] args = v.getArguments();
            GnomesAppException ex = (GnomesAppException)args[argIndex];
            return ex.getMessageNo();
        };
    }

    private static Stream<Integer> versionParameter() {
        return Stream.of(null, 2);
    }

}