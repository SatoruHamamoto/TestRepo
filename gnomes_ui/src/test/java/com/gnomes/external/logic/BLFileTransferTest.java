package com.gnomes.external.logic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

import com.gnomes.TestUtil;
import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums;
import com.gnomes.common.constants.CommonEnums.ExternalIfSendRecvClearFlag;
import com.gnomes.common.constants.CommonEnums.ExternalIfSendRecvRetryFlag;
import com.gnomes.common.constants.CommonEnums.SendRecvStateType;
import com.gnomes.common.constants.CommonEnums.SendRecvType;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.util.PessimisticLockSession;
import com.gnomes.external.data.FileDefine;
import com.gnomes.external.data.FileTransferBean;
import com.gnomes.external.data.SendRecvDataBean;
import com.gnomes.external.data.SystemDefine;
import com.gnomes.external.entity.QueueExternalIfRecv;
import com.gnomes.external.entity.QueueExternalIfSendStatus;
import com.gnomes.system.entity.MstrSystemDefine;
import com.gnomes.uiservice.ContainerRequest;

class BLFileTransferTest {

    private static final String SEND_RECV_FILE_NAME = "send-recv.csv";

    private static final String EXTERNAL_IF_SEND_STATUS_KEY = "test_externalIfSendStatusKey";

    private static final String EXTERNAL_IF_RECV_KEY = "test_externalIfRecvKey";

    private static final String EXTERNAL_IF_TARGET_CODE = "test_externalIfTargetCode";

    private static final int DEFAULT_REQUEST_WAIT_TIME = 10;
    private static final int DEFAULT_REQUEST_WAIT_COUNT = 3;

    private List<SendRecvDataBean> sendRecvDataBeanList;

    private List<String> lineDataList;

    private List<String> sendDataList;

    private BLFileTransfer transfer;

    private FileTransferBean fileTransferBean;

    private EntityManager em;

    private ContainerRequest req;

    private String[] extTargetCode;

    private List<String> sendProcParameter;

    private FileDefine fileDefine;

    private SystemDefine systemDefine;

    private QueueExternalIfSendStatus status;

    private QueueExternalIfRecv recvQue;

    private MstrSystemDefine mstrSystemDefine;

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
        transfer = TestUtil.createBean(BLFileTransfer.class);
        em = mock(EntityManager.class);
        req = mock(ContainerRequest.class);
        msgHandlerMock = Mockito.mockStatic(MessagesHandler.class);
        resHandlerMock = Mockito.mockStatic(ResourcesHandler.class);

        initSendDataList();
        sendRecvDataBeanList = new ArrayList<>();
        lineDataList = new ArrayList<>();
        sendProcParameter = new ArrayList<>();
        extTargetCode = new String[] { "TEST_MST_01", "TEST_MST_02", "TEST_MST_03" };

        // モック化共通
        // 送信要求呼出
        // 詳細なテストはFileTransferCallTalend側で実施する
        doNothing().when(transfer.fileTransferCallTalend).runSendRequestJobs();

        // 受信要求呼び出し
        // 詳細なテストはFileTransferCallTalend側で実施する
        doNothing().when(transfer.fileTransferCallTalend).runRecvRequestJobs();

        // 送信状態変更
        // 詳細なテストはFileTransferCallTalend側で実施する
        doNothing().when(transfer.fileTransferCallTalend).runSendChangeStateJobs();

        doReturn(extTargetCode).when(transfer.mstrExternalIfFileDefineDao).getSetValue(anyString(), anyString());
        doReturn(sendProcParameter).when(transfer.eventDrivenFromBatchData).getSendProcParameter();

        msgHandlerMock.when(() -> MessagesHandler
            .getExceptionMessage(any(ContainerRequest.class), any(GnomesAppException.class))).then(createMsgAnswer(0));
        resHandlerMock.when(() -> ResourcesHandler.getString(anyString())).then(createMsgAnswer(0));
    }

    @AfterEach
    void tearDown() throws Exception {
        msgHandlerMock.close();
        resHandlerMock.close();
    }

    @Test
    @DisplayName("ファイル送信要求：送信データリストがNullの場合GnomesAppExceptionをスロー")
    void test_sendRequest_GnomesAppException_sendDataList_null() throws Exception {
        setupMockDoReturn();
        setupMockThrowsException(GnomesMessagesConstants.ME01_0050);

        // 実際はGnomesAppExceptionがスローされる
        assertThrows(NullPointerException.class,
            () -> transfer.sendRequest(null, GnomesResourcesConstants.FileType_Excel, 11, SendRecvStateType.Request));
        verify(transfer.fileTransferBean, times(0)).setLineDataList(anyList());
    }

    @Test
    @DisplayName("ファイル送信要求：送信データリストが空の場合GnomesAppExceptionをスロー")
    void test_sendRequest_GnomesAppException_sendDataList_empty() throws Exception {
        setupMockDoReturn();
        // リストを空にする
        sendDataList.clear();
        setupMockThrowsException(GnomesMessagesConstants.ME01_0050);

        // 実際はGnomesAppExceptionがスローされる
        assertThrows(NullPointerException.class,
            () -> transfer.sendRequest(sendDataList, GnomesResourcesConstants.FileType_Excel, 11, SendRecvStateType.Request));
        verify(transfer.fileTransferBean, times(0)).setLineDataList(anyList());
    }

    @Test
    @DisplayName("ファイル送信要求：ファイル種別がNullの場合GnomesAppExceptionをスロー")
    void test_sendRequest_GnomesAppException_fileType_null() throws Exception {
        setupMockDoReturn();
        setupMockThrowsException(GnomesMessagesConstants.ME01_0050);

        // 実際はGnomesAppExceptionがスローされる
        assertThrows(NullPointerException.class,
            () -> transfer.sendRequest(sendDataList, null, 11, SendRecvStateType.Request));
        verify(transfer.fileTransferBean, times(0)).setLineDataList(anyList());
    }

    @Test
    @DisplayName("ファイル送信要求：ファイル種別が空の場合GnomesAppExceptionをスロー")
    void test_sendRequest_GnomesAppException_fileType_empty() throws Exception {
        setupMockDoReturn();
        setupMockThrowsException(GnomesMessagesConstants.ME01_0050);

        // 実際はGnomesAppExceptionがスローされる
        assertThrows(NullPointerException.class,
            () -> transfer.sendRequest(sendDataList, "", 11, SendRecvStateType.Request));
        verify(transfer.fileTransferBean, times(0)).setLineDataList(anyList());
    }

    @Test
    @DisplayName("ファイル送信要求：要求内連番がNullの場合GnomesAppExceptionをスロー")
    void test_sendRequest_GnomesAppException_requestSeq_null() throws Exception {
        setupMockDoReturn();
        setupMockThrowsException(GnomesMessagesConstants.ME01_0050);

        // 実際はGnomesAppExceptionがスローされる
        assertThrows(NullPointerException.class,
            () -> transfer.sendRequest(sendDataList, GnomesResourcesConstants.FileType_Excel, null, SendRecvStateType.Request));
        verify(transfer.fileTransferBean, times(0)).setLineDataList(anyList());
    }

    @Test
    @DisplayName("ファイル送信要求：送信状態がNullの場合GnomesAppExceptionをスロー")
    void test_sendRequest_GnomesAppException_sendState_null() throws Exception {
        setupMockDoReturn();
        setupMockThrowsException(GnomesMessagesConstants.ME01_0050);

        // 実際はGnomesAppExceptionがスローされる
        assertThrows(NullPointerException.class,
            () -> transfer.sendRequest(sendDataList, GnomesResourcesConstants.FileType_Excel, 11, null));
        verify(transfer.fileTransferBean, times(0)).setLineDataList(anyList());
    }

    @Test
    @DisplayName("ファイル送信要求：送信状態が0(要求中)、-1(待機中)以外の場合GnomesAppExceptionをスロー")
    void test_sendRequest_GnomesAppException_sendState_not_requestOrWaiting() throws Exception {
        setupMockDoReturn();
        setupMockThrowsException(GnomesMessagesConstants.ME01_0210);

        // 実際はGnomesAppExceptionがスローされる
        assertThrows(NullPointerException.class,
            () -> transfer.sendRequest(sendDataList, GnomesResourcesConstants.FileType_Excel, 11, SendRecvStateType.Running));
        verify(transfer.fileTransferBean, times(0)).setLineDataList(anyList());
    }

    @Test
    @DisplayName("ファイル送信要求：正常終了")
    void test_sendRequest_success() throws Exception {
        setupMockWhitebox();
        try {
            transfer.sendRequest(sendDataList, GnomesResourcesConstants.FileType_Excel, 11, SendRecvStateType.Request);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        // バッチイベント駆動化データに外部I/F対象システムコードが設定されていること
        assertEquals(sendProcParameter.get(0), extTargetCode[0]);
        // リストア状況
        assertEquals(fileTransferBean.getSendRecvType(), SendRecvType.Recv);
        assertEquals(fileTransferBean.getSendRecvFileName(), SEND_RECV_FILE_NAME);
        assertEquals(fileTransferBean.getSendRecvDataBeanList(), sendRecvDataBeanList);
        assertEquals(fileTransferBean.getFileType(), GnomesResourcesConstants.FileType_Csv);
        assertEquals(fileTransferBean.getLineDataList(), lineDataList);
        assertEquals(fileTransferBean.getRequestSeq(), 99);
        assertEquals(fileTransferBean.getStatus(), SendRecvStateType.Waiting);
        assertEquals(fileTransferBean.getEml(), em);
    }

    @Test
    @DisplayName("受信要求受付：ファイル種別がNullの場合GnomesAppExceptionをスロー")
    void test_recvRequest_fileType_null() throws Exception {
        setupMockWhitebox();
        setupMockThrowsException(GnomesMessagesConstants.ME01_0050);

        // 実際はGnomesAppExceptionがスローされる
        assertThrows(NullPointerException.class, () -> transfer.recvRequest(null, SEND_RECV_FILE_NAME));
        verify(req, times(0)).setWatcherSearchKey(anyString());
        // 成功フラグがOFF
        assertFalse(fileTransferBean.isSuccessFlag());
    }

    @Test
    @DisplayName("受信要求受付：ファイル種別が空の場合GnomesAppExceptionをスロー")
    void test_recvRequest_fileType_empty() throws Exception {
        setupMockWhitebox();
        setupMockThrowsException(GnomesMessagesConstants.ME01_0050);

        // 実際はGnomesAppExceptionがスローされる
        assertThrows(NullPointerException.class, () -> transfer.recvRequest("", SEND_RECV_FILE_NAME));
        verify(req, times(0)).setWatcherSearchKey(anyString());
        // 成功フラグがOFF
        assertFalse(fileTransferBean.isSuccessFlag());
    }

    @Test
    @DisplayName("受信要求受付：受信ファイル名がNullの場合GnomesAppExceptionをスロー")
    void test_recvRequest_fileName_null() throws Exception {
        setupMockWhitebox();
        setupMockThrowsException(GnomesMessagesConstants.ME01_0050);

        // 実際はGnomesAppExceptionがスローされる
        assertThrows(NullPointerException.class, () -> transfer.recvRequest(GnomesResourcesConstants.FileType_Csv, null));
        // 成功フラグがOFF
        assertFalse(fileTransferBean.isSuccessFlag());
    }

    @Test
    @DisplayName("受信要求受付：受信ファイル名が空の場合GnomesAppExceptionをスロー")
    void test_recvRequest_fileName_empty() throws Exception {
        setupMockWhitebox();
        setupMockThrowsException(GnomesMessagesConstants.ME01_0050);

        // 実際はGnomesAppExceptionがスローされる
        assertThrows(NullPointerException.class, () -> transfer.recvRequest(GnomesResourcesConstants.FileType_Csv, ""));
        // 成功フラグがOFF
        assertFalse(fileTransferBean.isSuccessFlag());
    }

    @Test
    @DisplayName("受信要求受付：外部I/Fファイル構成定義マスタがNullの場合GnomesAppExceptionをスロー")
    void test_recvRequest_GnomesAppException_getFileDefine() throws Exception {
        setupMockWhitebox();
        setupMockThrowsException(GnomesMessagesConstants.ME01_0107);
        setupMockFileDefine(true);
        setupMockGetSystemDefine(false, false);

        // 実際はGnomesAppExceptionがスローされる
        assertThrows(NullPointerException.class,
            () -> transfer.recvRequest(GnomesResourcesConstants.FileType_Csv, SEND_RECV_FILE_NAME));
        // 成功フラグがOFF
        assertFalse(fileTransferBean.isSuccessFlag());
    }

    @Test
    @DisplayName("受信要求受付：外部I/Fシステム定義マスタがNullの場合GnomesAppExceptionをスロー")
    void test_recvRequest_GnomesAppException_getSystemDefine() throws Exception {
        setupMockWhitebox();
        setupMockThrowsException(GnomesMessagesConstants.ME01_0109);
        setupMockFileDefine(false);
        setupMockGetSystemDefine(true, false);

        // 実際はGnomesAppExceptionがスローされる
        assertThrows(NullPointerException.class,
            () -> transfer.recvRequest(GnomesResourcesConstants.FileType_Csv, SEND_RECV_FILE_NAME));
        // 成功フラグがOFF
        assertFalse(fileTransferBean.isSuccessFlag());
    }

    @Test
    @DisplayName("受信要求受付：正常終了")
    void test_recvRequest_success() throws Exception {
        setupMockWhitebox();
        setupMockFileDefine(false);
        setupMockGetSystemDefine(false, false);

        try {
            transfer.recvRequest(GnomesResourcesConstants.FileType_Csv, SEND_RECV_FILE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        // 成功フラグがON
        assertTrue(fileTransferBean.isSuccessFlag());
    }

    @Test
    @DisplayName("送信状態変更（待機中→要求中）：外部I/F送信状態キーがNullの場合GnomesAppExceptionをスロー")
    void test_sendChangeStateRequest_externalIfSendStatusKey_null() throws GnomesAppException {
        // 実際はGnomesAppExceptionがスローされる
        assertThrows(NullPointerException.class, () -> transfer.sendChangeStateRequest(null));
        // セッション情報は作成されない
        verify(transfer.lockSession, times(0)).createSessionNormal();
    }

    @Test
    @DisplayName("送信状態変更（待機中→要求中）：外部I/F送信状態キーが空の場合GnomesAppExceptionをスロー")
    void test_sendChangeStateRequest_externalIfSendStatusKey_empty() throws GnomesAppException {
        // 実際はGnomesAppExceptionがスローされる
        assertThrows(NullPointerException.class, () -> transfer.sendChangeStateRequest(""));
        // セッション情報は作成されない
        verify(transfer.lockSession, times(0)).createSessionNormal();
    }

    @Test
    @DisplayName("送信状態変更（待機中→要求中）：外部I/F送信状態キューがNullの場合GnomesAppExceptionをスロー")
    void test_sendChangeStateRequest_externalIfSendStatusLock_null() throws Exception {
        setupMockGetExternalIfSendStatusLock(true, 0, false);

        // 実際はGnomesAppExceptionがスローされる
        assertThrows(NullPointerException.class,
            () -> transfer.sendChangeStateRequest(EXTERNAL_IF_SEND_STATUS_KEY));
        // 外部I/F送信状態キューがNull
        assertNull(status);
        // ロールバックされた
        verify(transfer.lockSession, times(1)).rollback();
        // クローズされた
        verify(transfer.lockSession, times(1)).close();
    }

    @Test
    @DisplayName("送信状態変更（待機中→要求中）：送信状態が待機中以外の場合GnomesAppExceptionをスロー")
    void test_sendChangeStateRequest_sendStatus_not_waiting() throws Exception {
        setupMockGetExternalIfSendStatusLock(false, SendRecvStateType.Running.getValue(), false);

        // 実際はGnomesAppExceptionがスローされる
        assertThrows(NullPointerException.class,
            () -> transfer.sendChangeStateRequest(EXTERNAL_IF_SEND_STATUS_KEY));
        // 外部I/F送信状態キューが取得された
        assertNotNull(status);
        // ロールバックされた
        verify(transfer.lockSession, times(1)).rollback();
        // クローズされた
        verify(transfer.lockSession, times(1)).close();
    }

    @Test
    @DisplayName("送信状態変更（待機中→要求中）：正常終了")
    void test_sendChangeStateRequest_success() throws Exception {
        setupMockGetExternalIfSendStatusLock(false, SendRecvStateType.Waiting.getValue(), false);

        // 実際はGnomesAppExceptionがスローされる
        transfer.sendChangeStateRequest(EXTERNAL_IF_SEND_STATUS_KEY);
        // 外部I/F送信状態キューが取得された
        assertNotNull(status);
        // 送信状態が要求中に変更された
        assertEquals(status.getSend_status(), SendRecvStateType.Request.getValue());
        // コミットされた
        verify(transfer.lockSession, times(1)).commit();
        // クローズされた
        verify(transfer.lockSession, times(1)).close();
    }

    @Test
    @DisplayName("再送信かクリア実行：外部I/F送信状態キーがNullの場合GnomesAppExceptionをスロー")
    void test_sendChangeState_externalIfSendStatusKey_null() throws GnomesAppException {
        // 実際はGnomesAppExceptionがスローされる
        assertThrows(NullPointerException.class, () -> transfer.sendChangeState(null, 0));
        // セッション情報は作成されない
        verify(transfer.lockSession, times(0)).createSessionNormal();
    }

    @Test
    @DisplayName("再送信かクリア実行：外部I/F送信状態キーが空の場合GnomesAppExceptionをスロー")
    void test_sendChangeState_externalIfSendStatusKey_empty() throws GnomesAppException {
        // 実際はGnomesAppExceptionがスローされる
        assertThrows(NullPointerException.class, () -> transfer.sendChangeState("", 0));
        // セッション情報は作成されない
        verify(transfer.lockSession, times(0)).createSessionNormal();
    }

    @Test
    @DisplayName("再送信かクリア実行：再送信クリアフラグがNullの場合GnomesAppExceptionをスロー")
    void test_sendChangeState_reSendClearFlag_null() throws GnomesAppException {
        // 実際はGnomesAppExceptionがスローされる
        assertThrows(NullPointerException.class, () -> transfer.sendChangeState(EXTERNAL_IF_SEND_STATUS_KEY, null));
        // セッション情報は作成されない
        verify(transfer.lockSession, times(0)).createSessionNormal();
    }

    @Test
    @DisplayName("再送信かクリア実行：外部I/Fシステム定義がNullの場合GnomesAppExceptionをスロー")
    void test_sendChangeState_systemDefine_null() throws GnomesAppException, Exception {
        setupMockGetExternalIfSendStatusLock(false, SendRecvStateType.Waiting.getValue(), false);
        setupMockWhitebox();
        setupMockGetSystemDefine(true, false);

        // 実際はGnomesAppExceptionがスローされる
        assertThrows(NullPointerException.class, () -> transfer.sendChangeState(EXTERNAL_IF_SEND_STATUS_KEY, 0));
        // セッション情報は作成された
        verify(transfer.lockSession, times(1)).createSessionNormal();
        // トランザクションが開始された
        verify(transfer.lockSession, times(1)).begin();
        // ロールバックされた
        verify(transfer.lockSession, times(1)).rollback();
        // クローズされた
        verify(transfer.lockSession, times(1)).close();
    }

    @Test
    @DisplayName("再送信かクリア実行：エラーファイル無しかつ再送信")
    void test_sendChangeState_errorFileExists_false_retry() throws GnomesAppException, Exception {
        setupMockGetSystemDefine(false, false);
        setupMockGetExternalIfSendStatusLock(false, SendRecvStateType.Waiting.getValue(), false);
        setupMockWhitebox();

        try {
            transfer.sendChangeState(EXTERNAL_IF_SEND_STATUS_KEY, 0);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        // セッション情報は作成された
        verify(transfer.lockSession, times(1)).createSessionNormal();
        // トランザクションが開始された
        verify(transfer.lockSession, times(1)).begin();
        // ロールバックされた
        verify(transfer.lockSession, times(1)).commit();
        // クローズされた
        verify(transfer.lockSession, times(1)).close();

        assertEquals(fileTransferBean.getSendRecvType(), SendRecvType.Send);
        assertEquals(fileTransferBean.getQueueExternalIfSendStatus(), status);
        assertEquals(fileTransferBean.getFileType(), status.getFile_type());
        assertEquals(fileTransferBean.getSystemDefine(), systemDefine);
        // エラーファイル無し
        assertEquals(fileTransferBean.getMoveFromFolderName(), systemDefine.getSend_folder_path());
        assertEquals(fileTransferBean.getMoveToFolderName(), systemDefine.getSend_backup_folder_path());
        assertEquals(fileTransferBean.getSendRecvFileName(), status.getSend_file_name());
        assertEquals(fileTransferBean.getIsFileCheck(), "false");
        // 再送信
        assertEquals(fileTransferBean.getStatus(), SendRecvStateType.Request);
        assertEquals(fileTransferBean.getRetryFlag(), ExternalIfSendRecvRetryFlag.ON);
        assertNull(fileTransferBean.getClearFlag());
    }

    @Test
    @DisplayName("再送信かクリア実行：エラーファイル無しかつクリア")
    void test_sendChangeState_errorFileExists_false_clear() throws GnomesAppException, Exception {
        setupMockGetSystemDefine(false, false);
        setupMockGetExternalIfSendStatusLock(false, SendRecvStateType.Waiting.getValue(), false);
        setupMockWhitebox();

        try {
            transfer.sendChangeState(EXTERNAL_IF_SEND_STATUS_KEY, 1);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        // セッション情報は作成された
        verify(transfer.lockSession, times(1)).createSessionNormal();
        // トランザクションが開始された
        verify(transfer.lockSession, times(1)).begin();
        // ロールバックされた
        verify(transfer.lockSession, times(1)).commit();
        // クローズされた
        verify(transfer.lockSession, times(1)).close();

        assertEquals(fileTransferBean.getSendRecvType(), SendRecvType.Send);
        assertEquals(fileTransferBean.getQueueExternalIfSendStatus(), status);
        assertEquals(fileTransferBean.getFileType(), status.getFile_type());
        assertEquals(fileTransferBean.getSystemDefine(), systemDefine);
        // エラーファイル無し
        assertEquals(fileTransferBean.getMoveFromFolderName(), systemDefine.getSend_folder_path());
        assertEquals(fileTransferBean.getMoveToFolderName(), systemDefine.getSend_backup_folder_path());
        assertEquals(fileTransferBean.getSendRecvFileName(), status.getSend_file_name());
        assertEquals(fileTransferBean.getIsFileCheck(), "false");
        // クリア
        assertNull(fileTransferBean.getRetryFlag());
        assertEquals(fileTransferBean.getClearFlag(), ExternalIfSendRecvClearFlag.ON);
    }

    @Test
    @DisplayName("再送信かクリア実行：エラーファイル有りかつ再送信")
    void test_sendChangeState_errorFileExists_true_retry() throws GnomesAppException, Exception {
        setupMockGetSystemDefine(false, false);
        setupMockGetExternalIfSendStatusLock(false, SendRecvStateType.Waiting.getValue(), true);
        setupMockWhitebox();

        try {
            // 0:再送信
            transfer.sendChangeState(EXTERNAL_IF_SEND_STATUS_KEY, 0);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        // セッション情報は作成された
        verify(transfer.lockSession, times(1)).createSessionNormal();
        // トランザクションが開始された
        verify(transfer.lockSession, times(1)).begin();
        // ロールバックされた
        verify(transfer.lockSession, times(1)).commit();
        // クローズされた
        verify(transfer.lockSession, times(1)).close();

        assertEquals(fileTransferBean.getSendRecvType(), SendRecvType.Send);
        assertEquals(fileTransferBean.getQueueExternalIfSendStatus(), status);
        assertEquals(fileTransferBean.getFileType(), status.getFile_type());
        assertEquals(fileTransferBean.getSystemDefine(), systemDefine);
        // エラーファイル有り
        assertEquals(fileTransferBean.getMoveFromFolderName(), systemDefine.getSend_temp_folder_path());
        assertEquals(fileTransferBean.getMoveToFolderName(), systemDefine.getSend_backup_folder_path());
        assertEquals(fileTransferBean.getSendRecvFileName(), status.getSend_file_name());
        assertEquals(fileTransferBean.getIsFileCheck(), "false");
        // 再送信
        assertEquals(fileTransferBean.getStatus(), SendRecvStateType.Request);
        assertEquals(fileTransferBean.getRetryFlag(), ExternalIfSendRecvRetryFlag.ON);
        assertNull(fileTransferBean.getClearFlag());
    }

    @Test
    @DisplayName("再送信かクリア実行：エラーファイル有りかつクリア")
    void test_sendChangeState_errorFileExists_true_clear() throws GnomesAppException, Exception {
        setupMockGetSystemDefine(false, false);
        setupMockGetExternalIfSendStatusLock(false, SendRecvStateType.Waiting.getValue(), true);
        setupMockWhitebox();

        try {
            // 1:クリア
            transfer.sendChangeState(EXTERNAL_IF_SEND_STATUS_KEY, 1);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        // セッション情報は作成された
        verify(transfer.lockSession, times(1)).createSessionNormal();
        // トランザクションが開始された
        verify(transfer.lockSession, times(1)).begin();
        // ロールバックされた
        verify(transfer.lockSession, times(1)).commit();
        // クローズされた
        verify(transfer.lockSession, times(1)).close();

        assertEquals(fileTransferBean.getSendRecvType(), SendRecvType.Send);
        assertEquals(fileTransferBean.getQueueExternalIfSendStatus(), status);
        assertEquals(fileTransferBean.getFileType(), status.getFile_type());
        assertEquals(fileTransferBean.getSystemDefine(), systemDefine);
        // エラーファイル有り
        assertEquals(fileTransferBean.getMoveFromFolderName(), systemDefine.getSend_temp_folder_path());
        assertEquals(fileTransferBean.getMoveToFolderName(), systemDefine.getSend_backup_folder_path());
        assertEquals(fileTransferBean.getSendRecvFileName(), status.getSend_file_name());
        assertEquals(fileTransferBean.getIsFileCheck(), "false");
        // クリア
        assertNull(fileTransferBean.getRetryFlag());
        assertEquals(fileTransferBean.getClearFlag(), ExternalIfSendRecvClearFlag.ON);
    }

    @Test
    @DisplayName("再送信かクリア実行：再送信、クリア以外")
    void test_sendChangeState_other() throws GnomesAppException, Exception {
        setupMockGetSystemDefine(false, false);
        setupMockGetExternalIfSendStatusLock(false, SendRecvStateType.Waiting.getValue(), true);
        setupMockWhitebox();

        try {
            transfer.sendChangeState(EXTERNAL_IF_SEND_STATUS_KEY, 2);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        // セッション情報は作成された
        verify(transfer.lockSession, times(1)).createSessionNormal();
        // トランザクションが開始された
        verify(transfer.lockSession, times(1)).begin();
        // ロールバックされた
        verify(transfer.lockSession, times(1)).commit();
        // クローズされた
        verify(transfer.lockSession, times(1)).close();

        assertEquals(fileTransferBean.getSendRecvType(), SendRecvType.Send);
        assertEquals(fileTransferBean.getQueueExternalIfSendStatus(), status);
        assertEquals(fileTransferBean.getFileType(), status.getFile_type());
        assertEquals(fileTransferBean.getSystemDefine(), systemDefine);
        assertEquals(fileTransferBean.getMoveFromFolderName(), systemDefine.getSend_temp_folder_path());
        assertEquals(fileTransferBean.getMoveToFolderName(), systemDefine.getSend_backup_folder_path());
        assertEquals(fileTransferBean.getSendRecvFileName(), status.getSend_file_name());
        assertEquals(fileTransferBean.getIsFileCheck(), "false");
        // 再送信、クリア以外
        assertNull(fileTransferBean.getRetryFlag());
        assertNull(fileTransferBean.getClearFlag());
    }

    @Test
    @DisplayName("受信状態変更：受信キューキーがNullの場合GnomesAppExceptionをスロー")
    void test_recvChangeState_externalIfRecvKey_null() throws GnomesAppException {
        // 実際はGnomesAppExceptionがスローされる
        assertThrows(NullPointerException.class, () -> transfer.recvChangeState(null, 0));
        // セッション情報は作成されない
        verify(transfer.lockSession, times(0)).createSessionNormal();
    }

    @Test
    @DisplayName("受信状態変更：受信キューキーが空の場合GnomesAppExceptionをスロー")
    void test_recvChangeState_externalIfRecvKey_empty() throws GnomesAppException {
        // 実際はGnomesAppExceptionがスローされる
        assertThrows(NullPointerException.class, () -> transfer.recvChangeState("", 0));
        // セッション情報は作成されない
        verify(transfer.lockSession, times(0)).createSessionNormal();
    }

    @Test
    @DisplayName("受信状態変更：外部I/Fシステム定義がNullの場合GnomesAppExceptionをスロー")
    void test_recvChangeState_ExternalIfRecvQueQueryLock_null() throws GnomesAppException, IOException {
        setupMockExternalIfRecvQueQueryLock();
        setupMockGetSystemDefine(true, true);
        setupMockWhitebox();

        // 実際はGnomesAppExceptionがスローされる
        assertThrows(NullPointerException.class, () -> transfer.recvChangeState(EXTERNAL_IF_RECV_KEY, 0));
        // セッション情報は作成された
        verify(transfer.lockSession, times(1)).createSessionNormal();
        // ロールバックされた
        verify(transfer.lockSession, times(1)).rollback();
        // クローズされた
        verify(transfer.lockSession, times(1)).close();
    }

    @Test
    @DisplayName("受信状態変更：Recv_backup_folder_pathが空の場合GnomesAppExceptionをスロー")
    void test_recvChangeState_recvBackupFolderPath_empty() throws GnomesAppException, IOException {
        setupMockExternalIfRecvQueQueryLock();
        setupMockGetSystemDefine(false, true);
        setupMockWhitebox();

        // 実際はGnomesAppExceptionがスローされる
        assertThrows(NullPointerException.class, () -> transfer.recvChangeState(EXTERNAL_IF_RECV_KEY, 0));
        // セッション情報は作成された
        verify(transfer.lockSession, times(1)).createSessionNormal();
        // ロールバックされた
        verify(transfer.lockSession, times(1)).rollback();
        // クローズされた
        verify(transfer.lockSession, times(1)).close();
    }

    @Test
    @DisplayName("受信状態変更：再受信かつ正常終了")
    void test_recvChangeState_success_retry() throws GnomesAppException, IOException {
        setupMockExternalIfRecvQueQueryLock();
        setupMockGetSystemDefine(false, false);
        setupMockWhitebox();

        try {
            transfer.recvChangeState(EXTERNAL_IF_RECV_KEY, 0);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        // セッション情報は作成された
        verify(transfer.lockSession, times(1)).createSessionNormal();
        // コミットされた
        verify(transfer.lockSession, times(1)).commit();
        // クローズされた
        verify(transfer.lockSession, times(1)).close();

        assertEquals(fileTransferBean.getSendRecvType(), SendRecvType.Recv);
        assertEquals(fileTransferBean.getQueueExternalIfRecv(), recvQue);
        assertEquals(fileTransferBean.getFileType(), recvQue.getFile_type());
        assertEquals(fileTransferBean.getSystemDefine(), systemDefine);
        assertEquals(fileTransferBean.getMoveFromFolderName(), systemDefine.getRecv_proc_folder_path());
        assertEquals(fileTransferBean.getMoveToFolderName(), systemDefine.getRecv_backup_folder_path());
        assertEquals(fileTransferBean.getSendRecvFileName(), recvQue.getRecv_file_name());
        assertEquals(fileTransferBean.getIsFileCheck(), "false");
        // 再送受信
        assertEquals(fileTransferBean.getStatus(), SendRecvStateType.Request);
        assertEquals(fileTransferBean.getRetryFlag(), ExternalIfSendRecvRetryFlag.ON);
        assertNull(fileTransferBean.getClearFlag());
    }

    @Test
    @DisplayName("受信状態変更：クリアかつ正常終了")
    void test_recvChangeState_success_clear() throws GnomesAppException, IOException {
        setupMockExternalIfRecvQueQueryLock();
        setupMockGetSystemDefine(false, false);
        setupMockWhitebox();

        try {
            transfer.recvChangeState(EXTERNAL_IF_RECV_KEY, 1);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        // セッション情報は作成された
        verify(transfer.lockSession, times(1)).createSessionNormal();
        // コミットされた
        verify(transfer.lockSession, times(1)).commit();
        // クローズされた
        verify(transfer.lockSession, times(1)).close();

        assertEquals(fileTransferBean.getSendRecvType(), SendRecvType.Recv);
        assertEquals(fileTransferBean.getQueueExternalIfRecv(), recvQue);
        assertEquals(fileTransferBean.getFileType(), recvQue.getFile_type());
        assertEquals(fileTransferBean.getSystemDefine(), systemDefine);
        assertEquals(fileTransferBean.getMoveFromFolderName(), systemDefine.getRecv_proc_folder_path());
        assertEquals(fileTransferBean.getMoveToFolderName(), systemDefine.getRecv_backup_folder_path());
        assertEquals(fileTransferBean.getSendRecvFileName(), recvQue.getRecv_file_name());
        assertEquals(fileTransferBean.getIsFileCheck(), "false");
        // クリア
        assertNull(fileTransferBean.getRetryFlag());
        assertEquals(fileTransferBean.getClearFlag(), ExternalIfSendRecvClearFlag.ON);
    }

    @Test
    @DisplayName("受信状態変更：再受信、クリア以外")
    void test_recvChangeState_success_other() throws GnomesAppException, IOException {
        setupMockExternalIfRecvQueQueryLock();
        setupMockGetSystemDefine(false, false);
        setupMockWhitebox();

        try {
            transfer.recvChangeState(EXTERNAL_IF_RECV_KEY, 2);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        // セッション情報は作成された
        verify(transfer.lockSession, times(1)).createSessionNormal();
        // コミットされた
        verify(transfer.lockSession, times(1)).commit();
        // クローズされた
        verify(transfer.lockSession, times(1)).close();

        assertEquals(fileTransferBean.getSendRecvType(), SendRecvType.Recv);
        assertEquals(fileTransferBean.getQueueExternalIfRecv(), recvQue);
        assertEquals(fileTransferBean.getFileType(), recvQue.getFile_type());
        assertEquals(fileTransferBean.getSystemDefine(), systemDefine);
        assertEquals(fileTransferBean.getMoveFromFolderName(), systemDefine.getRecv_proc_folder_path());
        assertEquals(fileTransferBean.getMoveToFolderName(), systemDefine.getRecv_backup_folder_path());
        assertEquals(fileTransferBean.getSendRecvFileName(), recvQue.getRecv_file_name());
        assertEquals(fileTransferBean.getIsFileCheck(), "false");
        // 再受診、クリア以外
        assertNull(fileTransferBean.getRetryFlag());
        assertNull(fileTransferBean.getClearFlag());
    }

    @ParameterizedTest
    @MethodSource("isNullOrEmpty")
    @DisplayName("送信処理要求：外部IF対象システムコードがNullまたは空の場合GnomesAppExceptionをスロー")
    void test_sendProc_GnomesAppException_externalIfTargetCode_isNullOrEmpty_false(String externalIfTargetCode) throws GnomesAppException, IOException {
        setupMockWhitebox();

        // 実際はGnomesAppExceptionがスローされる
        assertThrows(NullPointerException.class, () -> transfer.sendProc(externalIfTargetCode, 60, 10));
        // 外部I/F送信状態キューリスト取得処理が行われなかった
        verify(transfer.queueExternalIfSendStatusDao, times(0)).getSendStateQuery(anyInt(), anyString(),
            any(EntityManager.class));

        assertFalse(fileTransferBean.isSuccessFlag());
        assertFalse(fileTransferBean.isSkipFlag());
        assertTrue(fileTransferBean.isQuereExistFlag());
//        verify(req, times(1)).addMessageInfo(GnomesMessagesConstants.MG01_0043);
    }

    @Test
    @DisplayName("送信処理要求：外部I/F送信状態キューリストが取得できなかった場合処理を終了")
    void test_sendProc_sendStateList_nullEmpty() throws GnomesAppException, IOException {
        setupMockWhitebox();
        // 外部I/F送信状態キューリストをNullで返す
        setupMockGetSendStateQuery(true);

        try {
            transfer.sendProc(EXTERNAL_IF_TARGET_CODE, 60, 10);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        assertTrue(fileTransferBean.isSuccessFlag()); // 正常終了扱い
        assertFalse(fileTransferBean.isSkipFlag());
        assertFalse(fileTransferBean.isQuereExistFlag());
    }

    @Test
    @DisplayName("送信処理要求：HULFT送信コマンドがNullの場合GnomesAppExceptionをスロー")
    void test_sendProc_GnomesAppException_hulftSendCommand_null() throws GnomesAppException, IOException {
        setupMockWhitebox();
        setupMockGetSendStateQuery(false);
        // 第2引数TrueでHULFT送信コマンドをNullで返す
        setupMockGetMstrSystemDefine(false, true, "3");
        setupMockGetSystemDefine(false, false);

        // 実際はGnomesAppExceptionがスローされる
        assertThrows(NullPointerException.class, () -> transfer.sendProc(EXTERNAL_IF_TARGET_CODE, 60, 10));

        assertFalse(fileTransferBean.isSuccessFlag());
        assertFalse(fileTransferBean.isSkipFlag());
        assertTrue(fileTransferBean.isQuereExistFlag());
    }

    @Test
    @DisplayName("送信処理要求：キュー処理時例外発生時、成功フラグはFalseになる")
    void test_sendProc_Exception_queueExternalIfSendDataProc() throws Exception {
        setupMockWhitebox();
        setupMockGetSendStateQuery(false);
        setupMockGetMstrSystemDefine(false, false, "3");
        setupMockGetSystemDefine(false, false);
        // Exceptionを発生させる
        setupBLFileTranserSendDataProc(true);

        transfer.sendProc(EXTERNAL_IF_TARGET_CODE, 60, 10);

        assertFalse(fileTransferBean.isSuccessFlag());
        assertFalse(fileTransferBean.isSkipFlag());
        assertTrue(fileTransferBean.isQuereExistFlag());
    }

    @Test
    @DisplayName("送信処理要求：正常終了")
    void test_sendProc_success() throws Exception {
        setupMockWhitebox();
        setupMockGetSendStateQuery(false);
        setupMockGetMstrSystemDefine(false, false, "3");
        setupMockGetSystemDefine(false, false);
        setupBLFileTranserSendDataProc(false);

        transfer.sendProc(EXTERNAL_IF_TARGET_CODE, 60, 10);

        assertTrue(fileTransferBean.isSuccessFlag());
        assertFalse(fileTransferBean.isSkipFlag());
        assertTrue(fileTransferBean.isQuereExistFlag());

        // Bean設定確認
        assertEquals(fileTransferBean.getFileDefaultDelimiter(), CommonEnums.FileDefaultDelimiter.getEnum(3));
        assertEquals(fileTransferBean.getSystemDefine(), systemDefine);
        assertEquals(fileTransferBean.getMoveFromFolderName(), systemDefine.getSend_temp_folder_path());
        assertEquals(fileTransferBean.getMoveToFolderName(), systemDefine.getSend_folder_path());
        assertEquals(fileTransferBean.getSendRecvType(), SendRecvType.Send);
        assertEquals(fileTransferBean.getHulftSendCommand(), mstrSystemDefine.getChar1());
    }

    @Test
    @DisplayName("送信処理要求：改行コードが取得できなかった場合デフォルトの改行コードを設定")
    void test_sendProc_fileDefaultDelimiter_null() throws Exception {
        setupMockWhitebox();
        setupMockGetSendStateQuery(false);
        // 第1引数Trueで改行コードをNullで返す
        setupMockGetMstrSystemDefine(true, false, "3");
        setupMockGetSystemDefine(false, false);
        setupBLFileTranserSendDataProc(false);

        transfer.sendProc(EXTERNAL_IF_TARGET_CODE, 60, 10);

        assertEquals(fileTransferBean.getFileDefaultDelimiter(), CommonConstants.FILE_DEFAULT_DELIMITER);
    }

    @Test
    @DisplayName("送信処理要求：改行コードがEnumに存在しない場合デフォルトの改行コードを設定")
    void test_sendProc_fileDefaultDelimiter_noMatch() throws Exception {
        setupMockWhitebox();
        setupMockGetSendStateQuery(false);
        // Enumに存在しない値を設定する
        setupMockGetMstrSystemDefine(false, false, "99");
        setupMockGetSystemDefine(false, false);
        setupBLFileTranserSendDataProc(false);

        transfer.sendProc(EXTERNAL_IF_TARGET_CODE, 60, 10);

        assertEquals(fileTransferBean.getFileDefaultDelimiter(), CommonConstants.FILE_DEFAULT_DELIMITER);
    }

    @Test
    @DisplayName("送信処理要求：改行コードが数値変換に失敗した場合デフォルトの改行コードを設定")
    void test_sendProc_fileDefaultDelimiter_NumberFormatException() throws Exception {
        setupMockWhitebox();
        setupMockGetSendStateQuery(false);
        // 文字列を設定する
        setupMockGetMstrSystemDefine(false, false, "str");
        setupMockGetSystemDefine(false, false);
        setupBLFileTranserSendDataProc(false);

        transfer.sendProc(EXTERNAL_IF_TARGET_CODE, 60, 10);

        assertEquals(fileTransferBean.getFileDefaultDelimiter(), CommonConstants.FILE_DEFAULT_DELIMITER);
    }

    @Test
    @DisplayName("送信処理要求：要求処理待機時間(秒)と要求処理待機回数が未設定の場合デフォルト値が設定される")
    void test_sendProc_defaultParameter_notSet_arguments() throws Exception {
        setupMockWhitebox();
        setupMockGetSendStateQuery(false);
        setupMockGetMstrSystemDefine(false, false, "3");
        setupMockGetSystemDefine(false, false);
        setupBLFileTranserSendDataProc(false);

        int[] arguments = testArgumentsQueueExternalIfSendDataProc(null, null);
        assertEquals(arguments[0], (DEFAULT_REQUEST_WAIT_TIME * 1000));
        assertEquals(arguments[1], DEFAULT_REQUEST_WAIT_COUNT);
    }

    @Test
    @DisplayName("送信処理要求：要求処理待機時間(秒)と要求処理待機回数が設定されている場合引数の値で使用される")
    void test_sendProc_use_arguments() throws Exception {
        setupMockWhitebox();
        setupMockGetSendStateQuery(false);
        setupMockGetMstrSystemDefine(false, false, "3");
        setupMockGetSystemDefine(false, false);
        setupBLFileTranserSendDataProc(false);

        Integer requestWaitTimeParam = 60;
        Integer requestWaitCountParam = 300;

        int[] arguments = testArgumentsQueueExternalIfSendDataProc(requestWaitTimeParam, requestWaitCountParam);
        assertEquals(arguments[0], (requestWaitTimeParam * 1000));
        assertEquals(arguments[1], requestWaitCountParam);
    }

    private void setupMockDoReturn() throws GnomesAppException {
        doReturn(SendRecvType.Recv).when(transfer.fileTransferBean).getSendRecvType();
        doReturn(SEND_RECV_FILE_NAME).when(transfer.fileTransferBean).getSendRecvFileName();
        doReturn(sendRecvDataBeanList).when(transfer.fileTransferBean).getSendRecvDataBeanList();
        doReturn(GnomesResourcesConstants.FileType_Csv).when(transfer.fileTransferBean).getFileType();
        doReturn(lineDataList).when(transfer.fileTransferBean).getLineDataList();
        doReturn(99).when(transfer.fileTransferBean).getRequestSeq();
        doReturn(SendRecvStateType.Waiting).when(transfer.fileTransferBean).getStatus();
        doReturn(em).when(transfer.fileTransferBean).getEml();
    }

    private void setupMockWhitebox() throws GnomesAppException {
        fileTransferBean = new FileTransferBean();
        fileTransferBean.setSendRecvType(SendRecvType.Recv);
        fileTransferBean.setSendRecvFileName(SEND_RECV_FILE_NAME);
        fileTransferBean.setSendRecvDataBeanList(sendRecvDataBeanList);
        fileTransferBean.setFileType(GnomesResourcesConstants.FileType_Csv);
        fileTransferBean.setLineDataList(lineDataList);
        fileTransferBean.setRequestSeq(99);
        fileTransferBean.setStatus(SendRecvStateType.Waiting);
        fileTransferBean.setEml(em);
        Whitebox.setInternalState(transfer, "fileTransferBean", fileTransferBean);
    }

    // ここではGnomesExceptionFactory#createGnomesAppExceptionメソッドのスタブを設定している
    // しかし、BLFileTransfer#sendRequestではsuperメソッドを呼び出しておりモック化できないためNullPointerExceptionが発生する
    // Mockitoの機能だけではsuperメソッドのスタブ化は実現不可能
    // PowerMockを使用すれば可能だがjunit5でサポートされていないため一旦保留
    private void setupMockThrowsException(String messageNo) {
        if (messageNo.equals(GnomesMessagesConstants.ME01_0050)
            || messageNo.equals(GnomesMessagesConstants.ME01_0107)
            || messageNo.equals(GnomesMessagesConstants.ME01_0109)) {
            doReturn(new GnomesAppException(null, messageNo, "")).when(transfer.exceptionFactory)
                .createGnomesAppException(anyString(), anyString(), any(Object[].class));
        } else {
            doReturn(new GnomesAppException(null, messageNo)).when(transfer.exceptionFactory)
                .createGnomesAppException(anyString(), anyString());
        }
    }

    private void setupMockFileDefine(boolean isNull) throws GnomesAppException {
        if (!isNull) {
            fileDefine = new FileDefine();
            fileDefine.setData_type_name("file-define.csv");
            fileDefine.setExt_target_code("test_ext_target_code");
        }

        doReturn(fileDefine).when(transfer.mstrExternalIfFileDefineDao).getFileDefine(anyString());
    }

    private void setupMockGetSystemDefine(boolean isNull, boolean isEmpty) throws GnomesAppException, IOException {
        if (!isNull) {
            Path sendFolderPath = tempDir.resolve("send");
            Path sendBackupFolderPath = tempDir.resolve("send-backup");
            Path sendTempFolderPath = tempDir.resolve("send-temp");
            Path recvFolderPath = tempDir.resolve("recv");
            Path recvProcFolderPath = tempDir.resolve("recv-proc");
            Path recvBackupFolderPath = tempDir.resolve("recv-backup");
            Files.createDirectory(sendFolderPath);
            Files.createDirectory(sendBackupFolderPath);
            Files.createDirectory(sendTempFolderPath);

            systemDefine = new SystemDefine();
            systemDefine.setRecv_folder_path(recvFolderPath.toString());
            systemDefine.setRecv_proc_folder_path(recvProcFolderPath.toString());
            systemDefine.setRecv_backup_folder_path(!isEmpty ? recvBackupFolderPath.toString() : "");
            systemDefine.setSend_folder_path(sendFolderPath.toString());
            systemDefine.setSend_backup_folder_path(sendBackupFolderPath.toString());
            systemDefine.setSend_temp_folder_path(sendTempFolderPath.toString() + File.separator);
            systemDefine.setSend_recv_mode(0);
            systemDefine.setProtocol_type(0);
        }

        doReturn(systemDefine).when(transfer.mstrExternalIfSystemDefineDao).getSystemDefine(anyString());
    }

    private void setupMockGetExternalIfSendStatusLock(boolean isNull, int sendStatus, boolean isError) throws GnomesAppException, IOException {
        status = null;
        if (!isNull) {
            String sendFileName = "test-send.csv";

            status = new QueueExternalIfSendStatus();
            status.setSend_status(sendStatus);
            status.setExternal_if_target_code("test_external_if_target_code");
            status.setFile_type(GnomesResourcesConstants.FileType_Csv);
            status.setSend_file_name(sendFileName);

            if (isError) {
                Path errorFilePath = Paths
                    .get(systemDefine.getSend_temp_folder_path() + status.getSend_file_name());
                Files.createFile(errorFilePath);
            }
        }
        doReturn(status).when(transfer.queueExternalIfSendStatusDao).getExternalIfSendStatusLock(anyString(),
            any(PessimisticLockSession.class));
    }

    private void setupMockExternalIfRecvQueQueryLock() throws GnomesAppException {
        recvQue = new QueueExternalIfRecv();
        recvQue.setFile_type(GnomesResourcesConstants.FileType_Csv);
        recvQue.setRecv_file_name(SEND_RECV_FILE_NAME);
        recvQue.setExternal_if_target_code("test_external_if_target_code");

        doReturn(recvQue).when(transfer.queueExternalIfRecvDao).getExternalIfRecvQueQueryLock(anyString(),
            any(PessimisticLockSession.class));
    }

    private void setupMockGetSendStateQuery(boolean isNull) throws GnomesAppException {
        List<QueueExternalIfSendStatus> sendStateList = null;

        if (!isNull) {
            sendStateList = new ArrayList<QueueExternalIfSendStatus>();
            QueueExternalIfSendStatus sendState = new QueueExternalIfSendStatus();
            sendState.setFile_type(GnomesResourcesConstants.FileType_Csv);
            sendStateList.add(sendState);
        }

        doReturn(sendStateList).when(transfer.queueExternalIfSendStatusDao).getSendStateQuery(anyInt(), anyString(),
            any());
    }

    private void setupMockGetMstrSystemDefine(boolean isNull1, boolean isNull2, String char1) throws GnomesAppException, IOException {
        MstrSystemDefine delimiterDefine = null;
        mstrSystemDefine = null;

        if (!isNull1) {
            delimiterDefine = new MstrSystemDefine();
            delimiterDefine.setChar1(char1);
        }

        if (!isNull2) {
            Path hulftSendPath = tempDir.resolve("hulft");
            Path filePath = hulftSendPath.resolve(SEND_RECV_FILE_NAME);
            Files.createDirectory(hulftSendPath);
            Files.createFile(filePath);
            mstrSystemDefine = new MstrSystemDefine();
            mstrSystemDefine.setChar1(filePath.toString());
        }

        doReturn(delimiterDefine)
            .doReturn(mstrSystemDefine).when(transfer.mstrSystemDefineDao)
            .getMstrSystemDefine(anyString(), anyString());
    }

    private void setupBLFileTranserSendDataProc(boolean isError) throws Exception {
        if (isError) {
            doThrow(new SQLException()).when(transfer.bLFileTranserSendDataProc).QueueExternalIfSendDataProc(
                any(QueueExternalIfSendStatus.class), anyString(), anyInt(), anyInt(), anyList());
        } else {
            doReturn(true).when(transfer.bLFileTranserSendDataProc).QueueExternalIfSendDataProc(
                any(QueueExternalIfSendStatus.class), anyString(), anyInt(), anyInt(), anyList());
        }
    }

    @SuppressWarnings("unchecked")
    private int[] testArgumentsQueueExternalIfSendDataProc(Integer requestWaitTimeParam, Integer requestWaitCountParam) throws Exception {
        ArgumentCaptor<QueueExternalIfSendStatus> queueExternalIfSendStatusCaptor = ArgumentCaptor.forClass(QueueExternalIfSendStatus.class);
        ArgumentCaptor<String> externalIfTargetCodeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> requestWaitCountCaptor = ArgumentCaptor.forClass(int.class);
        ArgumentCaptor<Integer> requestWaitTimeCaptor = ArgumentCaptor.forClass(int.class);
        ArgumentCaptor<List<QueueExternalIfSendStatus>> sendStateListCaptor = ArgumentCaptor.forClass(List.class);

        transfer.sendProc(EXTERNAL_IF_TARGET_CODE, requestWaitTimeParam, requestWaitCountParam);

        verify(transfer.bLFileTranserSendDataProc, times(1)).QueueExternalIfSendDataProc(
            queueExternalIfSendStatusCaptor.capture(), externalIfTargetCodeCaptor.capture(),
            requestWaitCountCaptor.capture(),
            requestWaitTimeCaptor.capture(), sendStateListCaptor.capture());

        return new int[] { requestWaitTimeCaptor.getValue(), requestWaitCountCaptor.getValue() };
    }

    private void initSendDataList() {
        sendDataList = new ArrayList<>();

        sendDataList.add("TEST_DATA_01");
        sendDataList.add("TEST_DATA_02");
        sendDataList.add("TEST_DATA_03");
    }

    private Answer<String> createMsgAnswer(int argIndex){
        return (Answer<String>) v ->{
            Object[] args = v.getArguments();
            String result = (String)args[argIndex];
            return result;
        };
    }

    public static Stream<String> isNullOrEmpty() {
        return Stream.of(null, "");
    }

}