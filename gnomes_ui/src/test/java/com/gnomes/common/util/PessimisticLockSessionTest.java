package com.gnomes.common.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.LockTimeoutException;
import javax.persistence.PersistenceException;
import javax.persistence.PessimisticLockException;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.dialect.lock.PessimisticEntityLockException;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.equipment.entity.MstrEquipment;

class PessimisticLockSessionTest {

    private static final String[] PRIMARY_KEYS = {
        "test_equipment_key_01",
        "test_equipment_key_02",
        "test_equipment_key_03"
    };

    @InjectMocks
    PessimisticLockSession lockSession;

    @Mock
    Session session;

    @Mock
    Transaction transaction;

    @Mock
    EntityManager em;

    @Mock
    Logger logger;

    @Mock
    LogHelper logHelper;

    @Mock
    GnomesExceptionFactory exceptionFactory;

    private MockedStatic<MessagesHandler> msgHandlerMock;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        msgHandlerMock = Mockito.mockStatic(MessagesHandler.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        msgHandlerMock.close();
    }

    @Test
    @DisplayName("行ロック（シングルアクセス）：エンティティが存在しない場合処理終了しnullを返す")
    void test_lock_entity_exists_false() throws Exception {
        setupMocks(TransactionStatus.NOT_ACTIVE, null);
        doReturn(null).when(em).find(any(), anyString(), any(LockModeType.class), anyMap());

        assertNull(lockSession.lock(MstrEquipment.class, 0, PRIMARY_KEYS[0]));
    }

    @Test
    @DisplayName("行ロック（シングルアクセス）：エンティティを返す")
    void test_lock_entity_exists_true() throws Exception {
        setupMocks(TransactionStatus.NOT_ACTIVE, null);
        doReturn(new MstrEquipment()).when(em).find(any(), anyString(), any(LockModeType.class), anyMap());
        MstrEquipment entity = lockSession.lock(MstrEquipment.class, 0, PRIMARY_KEYS[0]);

        assertNotNull(entity);
    }

    @Test
    @DisplayName("行ロック（バルクアクセス）：エンティティが存在しない場合処理終了しnullを返す")
    void test_lock_bulk_entity_exists_false() throws Exception {
        setupMocks(TransactionStatus.NOT_ACTIVE, null);
        doReturn(null).when(em).find(any(), anyString(), any(LockModeType.class), anyMap());

        assertNull(lockSession.lock(MstrEquipment.class, 0, PRIMARY_KEYS));
    }

    @Test
    @DisplayName("行ロック（バルクアクセス）：エンティティリストを返す")
    void test_lock_bulk_entity_exists_true() throws Exception {
        setupMocks(TransactionStatus.NOT_ACTIVE, null);
        doReturn(new MstrEquipment()).when(em).find(any(), anyString(), any(LockModeType.class), anyMap());
        List<MstrEquipment> entityList = lockSession.lock(MstrEquipment.class, 0, PRIMARY_KEYS);

        assertNotNull(entityList);
        assertEquals(entityList.size(), 3);
    }

    @Test
    @DisplayName("行ロック：例外発生 PessimisticEntityLockException→PessimisticLockExceptionをスロー")
    void test_lock_PessimisticLockException_PessimisticEntityLockException() throws Exception {
        setupMocks(TransactionStatus.NOT_ACTIVE, new PessimisticEntityLockException(MstrEquipment.class, null, null));

        assertThrows(PessimisticLockException.class, () -> lockSession.lock(MstrEquipment.class, 0, PRIMARY_KEYS));
    }

    @Test
    @DisplayName("行ロック：例外発生 LockAcquisitionExceptionn→PessimisticLockExceptionをスロー")
    void test_lock_PessimisticLockException_LockAcquisitionException() throws Exception {
        setupMocks(TransactionStatus.NOT_ACTIVE, new LockAcquisitionException(null, null));

        assertThrows(PessimisticLockException.class, () -> lockSession.lock(MstrEquipment.class, 0, PRIMARY_KEYS));
    }

    @Test
    @DisplayName("行ロック：例外発生 LockTimeoutException→PessimisticLockExceptionをスロー")
    void test_lock_PessimisticLockException_LockTimeoutException() throws Exception {
        setupMocks(TransactionStatus.NOT_ACTIVE, new LockTimeoutException());

        assertThrows(PessimisticLockException.class, () -> lockSession.lock(MstrEquipment.class, 0, PRIMARY_KEYS));
    }

    @Test
    @DisplayName("行ロック：例外発生 PersistenceException(LockAcquisitionException)→PessimisticLockExceptionをスロー")
    void test_lock_PersistenceException_LockTimeoutException() throws Exception {
        setupMocks(TransactionStatus.NOT_ACTIVE, new PersistenceException(new LockAcquisitionException(null, null)));

        assertThrows(PessimisticLockException.class, () -> lockSession.lock(MstrEquipment.class, 0, PRIMARY_KEYS));
    }

    @Test
    @DisplayName("行ロック：例外発生 PersistenceException(Exception)→GnomesAppExceptionをスロー")
    void test_lock_PersistenceException_GnomesAppException() throws Exception {
        setupMocks(TransactionStatus.NOT_ACTIVE, new PersistenceException(new IllegalArgumentException()));

        GnomesAppException e = assertThrows(GnomesAppException.class, () -> lockSession.lock(MstrEquipment.class, 0, PRIMARY_KEYS));
        assertEquals(GnomesMessagesConstants.ME01_0169, e.getMessageNo());
    }

    @Test
    @DisplayName("行ロック：例外発生 Exception→GnomesAppExceptionをスロー")
    void test_lock_Exception_GnomesAppException() throws Exception {
        setupMocks(TransactionStatus.NOT_ACTIVE, new IllegalArgumentException());

        GnomesAppException e = assertThrows(GnomesAppException.class, () -> lockSession.lock(MstrEquipment.class, 0, PRIMARY_KEYS));
        assertEquals(GnomesMessagesConstants.ME01_0169, e.getMessageNo());
    }

    @Test
    @DisplayName("行ロック：ロールバック確認")
    void test_lock_rollback() throws Exception {
        setupMocks(TransactionStatus.ACTIVE, new LockTimeoutException());

        assertThrows(PessimisticLockException.class, () -> lockSession.lock(MstrEquipment.class, 0, PRIMARY_KEYS));
        // 悲観ロックに失敗したらロールバック処理が呼ばれる
        verify(transaction, times(1)).rollback();
    }

    private void setupMocks(TransactionStatus status, Exception ex) throws ClassNotFoundException {
        // トランザクション設定
        doReturn(status).when(transaction).getStatus();

        if (status != TransactionStatus.ACTIVE) {
            // トランザクション開始
            doReturn(transaction).when(session).beginTransaction();
        }

        if (ex != null) {
            // エラー発生
            doThrow(ex).when(em).find(any(), anyString(), any(LockModeType.class), anyMap());

            if (ex instanceof IllegalArgumentException || ex.getCause() instanceof IllegalArgumentException) {
                doReturn(
                    new GnomesAppException(null, GnomesMessagesConstants.ME01_0169, ex, MstrEquipment.class.getSimpleName()))
                        .when(exceptionFactory).createGnomesAppException(null, GnomesMessagesConstants.ME01_0169, ex,
                            MstrEquipment.class.getSimpleName());
            }
        }
        // メッセージが正しく設定されたかを確認するため、メッセージキーを返却するように設定
        msgHandlerMock.when(() -> MessagesHandler.getString(anyString(), any(Locale.class))).then(createMsgAnswer(0));
    }

    private Answer<String> createMsgAnswer(int argIndex){
        return (Answer<String>) v ->{
            Object[] args = v.getArguments();
            String result = (String)args[argIndex];
            return result;
        };
    }

}
