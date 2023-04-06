package com.gnomes.system.logic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.system.dao.InfoUserDao;
import com.gnomes.system.data.SystemFunctionBean;
import com.gnomes.system.entity.InfoUser;
import com.gnomes.system.entity.MstrPersonSecPolicy;


public class BLSecurityCheckLockAccountTest
{
    @InjectMocks
    BLSecurity blSecurity;

    @Mock
    SystemFunctionBean systemFunctionBean;

    @Mock
    InfoUserDao infoUserDao;

    @Mock
    EntityManager em;

    MockedStatic<MessagesHandler> msgHandlerMock;

    InfoUser infoUser;

    private int isSuccess = 0;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        MstrPersonSecPolicy mstrPersonSecPolicy = new MstrPersonSecPolicy();
        Mockito.doReturn(mstrPersonSecPolicy).when(systemFunctionBean).getMstrUsrsecPolicy();
        mstrPersonSecPolicy.setCertify_failure_times(5);

        infoUser = new InfoUser();
        Mockito.doReturn(infoUser).when(systemFunctionBean).getInfoUser();
        infoUser.setCertify_failure_times(0);
        infoUser.setIs_lock_out(0);

        // メッセージが正しく設定されたかを確認するため、メッセージキーを返却するように設定
        msgHandlerMock = Mockito.mockStatic(MessagesHandler.class);
        msgHandlerMock.when(() -> MessagesHandler.getString(anyString(), any(Object[].class))).then(createMsgAnswer(0));
    }

    @AfterEach
    void tearDown() throws Exception {
        msgHandlerMock.close();
    }

    @Test
    @DisplayName("認証エラー回数が許容回数を下回っている場合、アカウントがロックされない")
    void test_notLocked() throws Exception {
        infoUser.setCertify_failure_times(0);
        blSecurity.checkLockAccount(isSuccess, em);
        assertEquals(1, infoUser.getCertify_failure_times());
        assertEquals(0, infoUser.getIs_lock_out());
    }

    @Test
    @DisplayName("認証エラー回数が許容回数を下回っている場合、アカウントがロックされない")
    void test_notLocked_underLimit() throws Exception {
        infoUser.setCertify_failure_times(3);
        blSecurity.checkLockAccount(isSuccess, em);
        assertEquals(4, infoUser.getCertify_failure_times());
        assertEquals(0, infoUser.getIs_lock_out());
    }

    @Test
    @DisplayName("認証エラー回数が許容回数と等しくなった場合、アカウントがロックされる")
    void test_accountLocked_equalsLimit() throws Exception {
        infoUser.setCertify_failure_times(4);
        blSecurity.checkLockAccount(isSuccess, em);
        assertEquals(5, infoUser.getCertify_failure_times());
        assertEquals(1, infoUser.getIs_lock_out());
    }

    @Test
    @DisplayName("認証エラー回数が許容回数を超えた場合、アカウントがロックされる")
    void test_accountLocked_overLimit() throws Exception {
        infoUser.setCertify_failure_times(5);
        blSecurity.checkLockAccount(isSuccess, em);
        assertEquals(6, infoUser.getCertify_failure_times());
        assertEquals(1, infoUser.getIs_lock_out());
    }

    @Test
    @DisplayName("認証成功の場合、アカウントロックされず、認証失敗回数がリセットされる")
    void test_certifiedSuccess() throws Exception {
        isSuccess = 1;
        infoUser.setCertify_failure_times(4);
        blSecurity.checkLockAccount(isSuccess, em);
        assertEquals(0, infoUser.getCertify_failure_times());
        assertEquals(0, infoUser.getIs_lock_out());
    }

    private Answer<String> createMsgAnswer(int argIndex){
        return (Answer<String>) v ->{
            Object[] args = v.getArguments();
            String result = (String)args[argIndex];
            return result;
        };
    }
}
