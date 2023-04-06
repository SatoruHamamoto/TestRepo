package com.gnomes.external.logic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.springframework.test.util.ReflectionTestUtils;

import com.gnomes.TestUtil;
import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums.ExternalIfSendRecvClearFlag;
import com.gnomes.common.constants.CommonEnums.ExternalIfSendRecvRetryFlag;
import com.gnomes.common.data.GnomesEjbBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.util.TalendJobRun;
import com.gnomes.external.data.FileTransferBean;

class FileTransferCallTalendTest {

    private FileTransferCallTalend talend;

    private EntityManager em;

    private EntityManager originalEm;

    private EntityManager screenEm;;

    private EntityTransaction transaction;

    private MockedStatic<TalendJobRun> jobRunMock;

    private FileTransferBean fileTransferBean;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() throws Exception {
        talend = TestUtil.createBean(FileTransferCallTalend.class);
        em = mock(EntityManager.class);
        originalEm = mock(EntityManager.class);
        screenEm = mock(EntityManager.class);
        transaction = mock(EntityTransaction.class);
        jobRunMock = Mockito.mockStatic(TalendJobRun.class);

        GnomesExceptionFactory exceptionFactory = new GnomesExceptionFactory();
        Whitebox.setInternalState(talend, "exceptionFactory", exceptionFactory);
    }

    @AfterEach
    void tearDown() throws Exception {
        jobRunMock.close();
    }

    @Test
    @DisplayName("送信要求呼出：EJBスレッドの場合個別トランザクション開始かつ正常終了")
    void test_runSendRequestJobs_success_transaction_isActive_true() throws Exception {
        setupMockSendRequest(true, false, false);
        talend.runSendRequestJobs();

        // 個別トランザクションが使用された
        assertEquals(fileTransferBean.getEml(), originalEm);
        // トランザクション開始された
        verify(talend.lockSession, times(1)).begin();
        // コミットされた
        verify(talend.lockSession, times(1)).commit();
        // クローズされた
        verify(talend.lockSession, times(1)).close();
    }

    @Test
    @DisplayName("送信要求呼出：EJBスレッドの場合個別トランザクション開始かつ失敗")
    void test_runSendRequestJobs_fail_transaction_isActive_true() throws Exception {
        setupMockSendRequest(true, false, true);
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> talend.runSendRequestJobs());

        assertEquals(e.getMessageNo(), GnomesMessagesConstants.ME01_0248);
        // 個別トランザクションが使用された
        assertEquals(fileTransferBean.getEml(), originalEm);
        // トランザクション開始された
        verify(talend.lockSession, times(1)).begin();
        // ロールバックされた
        verify(talend.lockSession, times(1)).rollback();
        // クローズされた
        verify(talend.lockSession, times(1)).close();
    }

    @Test
    @DisplayName("送信要求呼出：EJBスレッドの場合個別トランザクションなしかつ正常終了")
    void test_runSendRequestJobs_success_transaction_isActive_false() throws Exception {
        setupMockSendRequest(true, true, false);
        talend.runSendRequestJobs();

        // すでに生成されたトランザクションが使用された
        assertEquals(fileTransferBean.getEml(), em);
        // 個別トランザクションが開始されていない
        verify(talend.lockSession, times(0)).begin();
        // コミットされない
        verify(talend.lockSession, times(0)).commit();
        // クローズされない
        verify(talend.lockSession, times(0)).close();
    }

    @Test
    @DisplayName("送信要求呼出：EJBスレッドの場合個別トランザクションなしかつ失敗")
    void test_runSendRequestJobs_fail_transaction_isActive_false() throws Exception {
        setupMockSendRequest(true, true, true);
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> talend.runSendRequestJobs());

        assertEquals(e.getMessageNo(), GnomesMessagesConstants.ME01_0248);
        // すでに生成されたトランザクションが使用された
        assertEquals(fileTransferBean.getEml(), em);
        // 個別トランザクションが開始されていない
        verify(talend.lockSession, times(0)).begin();
        // ロールバックされない
        verify(talend.lockSession, times(0)).rollback();
        // クローズされない
        verify(talend.lockSession, times(0)).close();
    }

    @Test
    @DisplayName("送信要求呼出：画面からの送信要求かつ正常終了")
    void test_runSendRequestJobs_success_fromScreen() throws Exception {
        setupMockSendRequest(false, false, false);
        talend.runSendRequestJobs();

        // GnomesScreenEntityManagerBeanが使用された
        assertEquals(fileTransferBean.getEml(), screenEm);
        // 個別トランザクションが開始されていない
        verify(talend.lockSession, times(0)).begin();
        // コミットされない
        verify(talend.lockSession, times(0)).commit();
        // クローズされない
        verify(talend.lockSession, times(0)).close();
    }

    @Test
    @DisplayName("送信要求呼出：画面からの送信要求かつ失敗")
    void test_runSendRequestJobs_fail_fromScreen() throws Exception {
        setupMockSendRequest(false, false, true);
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> talend.runSendRequestJobs());

        assertEquals(e.getMessageNo(), GnomesMessagesConstants.ME01_0248);
        // GnomesScreenEntityManagerBeanが使用された
        assertEquals(fileTransferBean.getEml(), screenEm);
        // 個別トランザクションが開始されていない
        verify(talend.lockSession, times(0)).begin();
        // ロールバックされない
        verify(talend.lockSession, times(0)).rollback();
        // クローズされない
        verify(talend.lockSession, times(0)).close();
    }

    @Test
    @DisplayName("受信要求呼出：エラー行情報が入っていない場合のエラー")
    void test_runRecvRequestJobs_errorLineInfo_empty_false() {
        setupMockRecvRequest(true, false);
        GnomesAppException e = assertThrows(GnomesAppException.class, () ->  talend.runRecvRequestJobs());

        assertEquals(e.getMessageNo(), GnomesMessagesConstants.ME01_0247);
    }

    @Test
    @DisplayName("受信要求呼出：エラー行情報が入っている場合のエラー")
    void test_runRecvRequestJobs_errorLineInfo_empty_true() {
        setupMockRecvRequest(true, true);
        GnomesAppException e = assertThrows(GnomesAppException.class, () ->  talend.runRecvRequestJobs());

        assertEquals(e.getMessageNo(), GnomesMessagesConstants.ME01_0249);
    }

    @Test
    @DisplayName("受信要求呼出：正常終了")
    void test_runRecvRequestJobs_success() {
        setupMockRecvRequest(false, false);

        try {
            talend.runRecvRequestJobs();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @DisplayName("エラー情報を1つの文字にして加工する：行番号が0以外の場合")
    void test_MakeDetailMessage_lineNo_othar_than_zero() {
        int lineNo = 100;
        String message = "TEST_JOB_ERROR";
        Map<Integer, String> errorInfo = new HashMap<Integer, String>() {
            {
                put(lineNo, message);
            }
        };
        String errorInfoStr = ReflectionTestUtils.invokeMethod(talend, "MakeDetailMessage", errorInfo);

        // 「行番号：メッセージ」の形式で返却される
        assertEquals(errorInfoStr, lineNo + ":" + message);
    }

    @Test
    @DisplayName("エラー情報を1つの文字にして加工する：行番号が0の場合")
    void test_MakeDetailMessage_lineNo_zero() {
        int lineNo = 0;
        String message = "TEST_JOB_ERROR";
        Map<Integer, String> errorInfo = new HashMap<Integer, String>() {
            {
                put(lineNo, message);
            }
        };
        String errorInfoStr = ReflectionTestUtils.invokeMethod(talend, "MakeDetailMessage", errorInfo);

        // メッセージのみが返却される
        assertEquals(errorInfoStr,  message);
    }

    @Test
    @DisplayName("送信状態変更呼出：再処理かつエラーが出力された場合")
    void test_runSendChangeStateJobs_fail_retry() throws Exception {
        setupMockSendRecvChangeState(0, true, false);
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> talend.runSendChangeStateJobs());

        assertEquals(e.getMessageNo(), GnomesMessagesConstants.ME01_0248);
        // 送信状態が更新された
        verify(talend.updateSendStateJob, times(1)).process();
        // クローズされた
        verify(fileTransferBean.getEml(), times(1)).close();
    }

    @Test
    @DisplayName("送信状態変更呼出：クリアかつエラーが出力された場合")
    void test_runSendChangeStateJobs_fail_clear() throws Exception {
        setupMockSendRecvChangeState(1, true, false);
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> talend.runSendChangeStateJobs());

        assertEquals(e.getMessageNo(), GnomesMessagesConstants.ME01_0248);
        // 送信キューが削除された
        verify(talend.deleteSendStateJob, times(1)).process();
        // クローズされた
        verify(fileTransferBean.getEml(), times(1)).close();
    }

    @Test
    @DisplayName("送信状態変更呼出：その他エラー")
    void test_runSendChangeStateJobs_exception() throws Exception {
        setupMockSendRecvChangeState(0, false, true);
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> talend.runSendChangeStateJobs());

        assertEquals(e.getMessageNo(), GnomesMessagesConstants.ME01_0131);
        // クローズされた
        verify(fileTransferBean.getEml(), times(1)).close();
    }

    @Test
    @DisplayName("送信状態変更呼出：再処理かつ正常終了")
    void test_runSendChangeStateJobs_success_retry() throws Exception {
        setupMockSendRecvChangeState(0, false, false);

        try {
            talend.runSendChangeStateJobs();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        // 送信状態が更新された
        verify(talend.updateSendStateJob, times(1)).process();
        // クローズされた
        verify(fileTransferBean.getEml(), times(1)).close();
    }

    @Test
    @DisplayName("送信状態変更呼出：クリアかつ正常終了")
    void test_runSendChangeStateJobs_success_clear() throws Exception {
        setupMockSendRecvChangeState(0, false, false);

        try {
            talend.runSendChangeStateJobs();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        // 送信状態が更新された
        verify(talend.updateSendStateJob, times(1)).process();
        // クローズされた
        verify(fileTransferBean.getEml(), times(1)).close();
    }

    @Test
    @DisplayName("受信状態変更呼出：再処理かつ正常終了")
    void test_runRecvChangeStateJobs_success_retry() throws Exception {
        setupMockSendRecvChangeState(0, false, false);

        try {
            talend.runRecvChangeStateJobs();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        // 受信キューが更新された
        verify(talend.updateRecvQueJob, times(1)).process();
        // 送受信実績データが更新された
        verify(talend.updateSendRecvActualDataJob, times(1)).process();
    }

    @Test
    @DisplayName("受信状態変更呼出：クリアかつエラーが出力された場合")
    void test_runRecvChangeStateJobs_fail_clear() throws Exception {
        setupMockSendRecvChangeState(1, true, false);
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> talend.runRecvChangeStateJobs());

        assertEquals(e.getMessageNo(), GnomesMessagesConstants.ME01_0247);
        // 送信キューが削除された
        verify(talend.deleteRecvQueJob, times(1)).process();
        // 送受信実績データが更新された
        verify(talend.updateSendRecvActualDataJob, times(1)).process();
    }

    @Test
    @DisplayName("受信状態変更呼出：クリアかつ正常終了")
    void test_runRecvChangeStateJobs_success_clear() throws Exception {
        setupMockSendRecvChangeState(1, false, false);

        try {
            talend.runRecvChangeStateJobs();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        // 送信キューが削除された
        verify(talend.deleteRecvQueJob, times(1)).process();
        // 送受信実績データが更新された
        verify(talend.updateSendRecvActualDataJob, times(1)).process();
    }

    @Test
    @DisplayName("受信状態変更呼出：その他エラー")
    void test_runRecvChangeStateJobs_exception() throws Exception {
        setupMockSendRecvChangeState(0, false, true);
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> talend.runRecvChangeStateJobs());

        assertEquals(e.getMessageNo(), GnomesMessagesConstants.ME01_0131);
    }

    private void setupMockSendRequest(boolean isEjbBatch, boolean isActive, boolean isError) throws GnomesAppException {
        doReturn(isEjbBatch).when(talend.gnomesEjbBean).isEjbBatch();

        doReturn(true).when(em).isOpen();
        doReturn(transaction).when(em).getTransaction();
        doReturn(isActive).when(transaction).isActive();

        doReturn(originalEm).when(talend.lockSession).getEntityManager();

        fileTransferBean = new FileTransferBean();
        fileTransferBean.setEml(em);
        if (!isEjbBatch) {
            // 画面からの送信要求
            doReturn(screenEm).when(talend.scrBean).getEntityManager();
        }
        if (isError) {
            fileTransferBean.setErrorJobName(CommonConstants.SEND_REQUEST_JOBS);
        }
        Whitebox.setInternalState(talend, "fileTransferBean", fileTransferBean);

        GnomesEjbBean ejbBean = new GnomesEjbBean();
        try {
            ejbBean.setEjbBatch(isEjbBatch);
        } catch (NullPointerException e) {
            // オブジェクト初期処理時にモック化していないところでNPEが発生するが送信要求呼出UTには関係無いので何もしない
            // isEjbBatchにtrueをセットしたいだけ
        }
        Whitebox.setInternalState(talend, "ejbBean", ejbBean);
    }

    private void setupMockRecvRequest(boolean isError, boolean errorLineInfo) {
        fileTransferBean = new FileTransferBean();
        if (isError) {
            fileTransferBean.setErrorJobName(CommonConstants.RECV_REQUEST_JOBS);
            if (errorLineInfo) {
                Map<Integer, String> errorInfo = new HashMap<Integer, String>() {
                    {
                        put(999, "TEST_JOB_ERROR");
                    }
                };
                fileTransferBean.setErrorLineInfo(errorInfo);
            }
        }
        Whitebox.setInternalState(talend, "fileTransferBean", fileTransferBean);
    }

    private void setupMockSendRecvChangeState(int reSendClearFlag, boolean isError, boolean isException) throws Exception {
        if (isException) {
            // 送受信実績データ更新
            doThrow(IllegalArgumentException.class).when(talend.updateSendRecvActualDataJob).process();
        }

        fileTransferBean = new FileTransferBean();
        fileTransferBean.setEml(em);
        fileTransferBean.setRetryFlag((reSendClearFlag == 0) ? ExternalIfSendRecvRetryFlag.ON : null);
        fileTransferBean.setClearFlag((reSendClearFlag == 1) ? ExternalIfSendRecvClearFlag.ON : null);
        fileTransferBean.setSendRecvFileName("test-send-recv.csv");
        if (isError) {
            fileTransferBean.setErrorJobName(CommonConstants.SEND_CHANGESTATE_JOBS_MOVE_FILE);
        }
        Whitebox.setInternalState(talend, "fileTransferBean", fileTransferBean);

        doReturn(true).when(em).isOpen();
    }

}
