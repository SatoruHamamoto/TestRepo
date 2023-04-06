package com.gnomes.common.interceptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;

import javax.enterprise.context.NonexistentConversationException;
import javax.interceptor.InvocationContext;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.validation.ValidationException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.gnomes.TestUtil;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;

class ErrorHandlingInterceptorTest {

    private ErrorHandlingInterceptor Interceptor;

    private InvocationContext invocationContext;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() throws Exception {
        Interceptor = TestUtil.createBean(ErrorHandlingInterceptor.class);
        invocationContext = mock(InvocationContext.class);
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    @DisplayName("ハンドリング処理：楽観ロック例外発生時メッセージ設定後GnomesExceptionをスロー（トレースモニター有り）")
    void test_handling_exception_OptimisticLockException_existTraceMonitor_true() throws Exception {
        setupDoContext(new OptimisticLockException(), true);
        GnomesException e = assertThrows(GnomesException.class, () -> Interceptor.handling(invocationContext));
        assertEquals("ME01.0003", e.getMessageNo());
        // トレースモニター有りの場合requestContext.setIsThrowException(true)は2回呼ばれる
        verify(Interceptor.requestContext, Mockito.times(2)).setIsThrowException(true);
    }

    @Test
    @DisplayName("ハンドリング処理：楽観ロック例外発生時メッセージ設定後GnomesExceptionをスロー（トレースモニター無し）")
    void test_handling_exception_OptimisticLockException_existTraceMonitor_false() throws Exception {
        setupDoContext(new OptimisticLockException(), false);
        GnomesException e = assertThrows(GnomesException.class, () -> Interceptor.handling(invocationContext));
        assertEquals("ME01.0003", e.getMessageNo());
        // トレースモニター無しの場合requestContext.setIsThrowException(true)は1回のみ呼ばれる
        verify(Interceptor.requestContext, Mockito.times(1)).setIsThrowException(true);
    }

    @Test
    @DisplayName("ハンドリング処理：Gnomesシステム例外発生時そのままスロー")
    void test_handling_exception_GnomesException() throws Exception {
        setupDoContext(new GnomesException(""), true);
        assertThrows(GnomesException.class, () -> Interceptor.handling(invocationContext));
    }

    @Test
    @DisplayName("ハンドリング処理：アプリケーション例外発生時GnomesExceptionをスロー")
    void test_handling_exception_GnomesAppException() throws Exception {
        setupDoContext(new GnomesAppException(""), true);
        GnomesException e = assertThrows(GnomesException.class, () -> Interceptor.handling(invocationContext));
        assertEquals("ME01.0001", e.getMessageNo());
    }

    @Test
    @DisplayName("ハンドリング処理：NonexistentConversationException発生時そのままスロー")
    void test_handling_exception_NonexistentConversationException() throws Exception {
        setupDoContext(new NonexistentConversationException(), true);
        assertThrows(NonexistentConversationException.class, () -> Interceptor.handling(invocationContext));
    }

    @Test
    @DisplayName("ハンドリング処理：ValidationException発生時なにもせずnull返却")
    void test_handling_exception_ValidationException() throws Exception {
        setupDoContext(new ValidationException(), true);
        assertNull(Interceptor.handling(invocationContext));
    }

    @Test
    @DisplayName("ハンドリング処理：Selectで件数が0件の場合にハンドルされない状態で例外をキャッチした場合GnomesAppExceptionをスロー")
    void test_handling_exception_NoResultException() throws Exception {
        setupDoContext(new NoResultException(), true);

        try (MockedStatic<MessagesHandler> mocked = Mockito.mockStatic(MessagesHandler.class)) {
            GnomesAppException e = assertThrows(GnomesAppException.class, () -> Interceptor.handling(invocationContext));
            assertEquals("ME01.0241", e.getMessageNo());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e);
        }
    }

    @Test
    @DisplayName("ハンドリング処理：その他Exception発生時GnomesExceptionをスロー")
    void test_handling_exception_Exception() throws Exception {
        setupDoContext(new IllegalArgumentException(), true);
        GnomesException e = assertThrows(GnomesException.class, () -> Interceptor.handling(invocationContext));
        assertEquals("ME01.0001", e.getMessageNo());
    }

    private void setupDoContext(Exception ex, boolean exists) throws Exception {
        Method method = ErrorHandlingInterceptor.class.getDeclaredMethod("handling", InvocationContext.class);
        Object[] parameters = { "test_parameter" };

        Mockito.doReturn(exists).when(Interceptor.requestContext).existTraceMonitor();
        Mockito.doReturn(Interceptor).when(invocationContext).getTarget();
        Mockito.doReturn(method).when(invocationContext).getMethod();
        Mockito.doReturn(parameters).when(invocationContext).getParameters();
        Mockito.doReturn(new GnomesAppException(null, GnomesMessagesConstants.ME01_0241))
            .when(Interceptor.exceptionFactory).createGnomesAppException(null, GnomesMessagesConstants.ME01_0241);

        Mockito.doThrow(ex).when(invocationContext).proceed();
    }

}
