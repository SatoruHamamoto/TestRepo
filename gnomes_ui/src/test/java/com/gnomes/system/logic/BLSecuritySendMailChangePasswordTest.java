package com.gnomes.system.logic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import java.math.BigDecimal;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.stubbing.Answer;

import com.gnomes.common.constants.CommonEnums.ChangePasswordMailNoticeFlag;
import com.gnomes.common.constants.SystemDefConstants;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.util.KeyStoreUtilities;
import com.gnomes.common.util.TalendJobRun;
import com.gnomes.system.dao.MstrSystemDefineDao;
import com.gnomes.system.data.CertInfo;
import com.gnomes.system.data.MailInfoBean;
import com.gnomes.system.data.SystemFunctionBean;
import com.gnomes.system.entity.MstrPerson;
import com.gnomes.system.entity.MstrSystemDefine;

public class BLSecuritySendMailChangePasswordTest
{
    @InjectMocks
    BLSecurity blSecurity;

    @Spy
    MailInfoBean mailInfoBean;

    @Mock
    SystemFunctionBean systemFunctionBean;

    @Mock
    GnomesSessionBean gnomesSessionBean;

    @Mock
    MstrSystemDefineDao mstrSystemDefineDao;

    @Mock
    KeyStoreUtilities keyStoreUtilities;

    @Mock
    LogHelper logHelper;

    MockedStatic<MessagesHandler> msgHandlerMock;

    MockedStatic<ResourcesHandler> resourcesHandlerMock;

    MockedStatic<TalendJobRun> talendJobRunMock;

    MstrPerson mstrPerson;

    MstrSystemDefine mailFlagDefine;

    MstrSystemDefine addressFromDefine;

    MstrSystemDefine userIdDefine;

    MstrSystemDefine encodeDefine;

    MstrSystemDefine mailServerHostDefine;

    MstrSystemDefine mailServerPortDefine;

    MstrSystemDefine mailSendDivDefine;

    private CertInfo certInfo;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        certInfo = createCertInfo();

        // SessionBean の作成
        Mockito.doReturn(Locale.JAPAN).when(gnomesSessionBean).getUserLocale();

        // システム定数を作成
        mailFlagDefine = new MstrSystemDefine();
        Mockito.doReturn(mailFlagDefine).when(mstrSystemDefineDao).getMstrSystemDefine(
                SystemDefConstants.SECURITY, SystemDefConstants.PASSWORD_CHANGE_MAIL_FLAG);
        mailFlagDefine.setNumeric1(new BigDecimal(ChangePasswordMailNoticeFlag.ON.getValue()));

        addressFromDefine = new MstrSystemDefine();
        Mockito.doReturn(addressFromDefine).when(mstrSystemDefineDao).getMstrSystemDefine(
                SystemDefConstants.TYPE_MAIL_SERVER_INFO, SystemDefConstants.CODE_MAIL_ADDRESS_FROM_CHANGE_PASSWORD);
        addressFromDefine.setChar1("testAddressFrom@testServer");

        userIdDefine = new MstrSystemDefine();
        Mockito.doReturn(userIdDefine).when(mstrSystemDefineDao).getMstrSystemDefine(
                SystemDefConstants.TYPE_MAIL_SERVER_INFO, SystemDefConstants.CODE_AUTHENTICATED_USERID_CHANGE_PASSWORD);
        userIdDefine.setChar1("testUserId");

        encodeDefine = new MstrSystemDefine();
        Mockito.doReturn(encodeDefine).when(mstrSystemDefineDao).getMstrSystemDefine(
                SystemDefConstants.TYPE_MAIL_SERVER_INFO, SystemDefConstants.CODE_ENCODE);
        encodeDefine.setChar1("UTF-8");

        mailServerHostDefine = new MstrSystemDefine();
        Mockito.doReturn(mailServerHostDefine).when(mstrSystemDefineDao).getMstrSystemDefine(
                SystemDefConstants.TYPE_MAIL_SERVER_INFO, SystemDefConstants.MAIL_SERVER_HOST);
        mailServerHostDefine.setChar1("testMailServerHost");

        mailServerPortDefine = new MstrSystemDefine();
        Mockito.doReturn(mailServerPortDefine).when(mstrSystemDefineDao).getMstrSystemDefine(
                SystemDefConstants.TYPE_MAIL_SERVER_INFO, SystemDefConstants.MAIL_SERVER_PORT);
        mailServerPortDefine.setChar1("25");

        mailSendDivDefine = new MstrSystemDefine();
        Mockito.doReturn(mailSendDivDefine).when(mstrSystemDefineDao).getMstrSystemDefine(
                SystemDefConstants.TYPE_MAIL_SERVER_INFO, SystemDefConstants.MAIL_SEND_DIV);
        mailSendDivDefine.setChar1("STARTTLS,NOAUTH");

        // ユーザーマスタ情報を作成
        mstrPerson = new MstrPerson();
        Mockito.doReturn(mstrPerson).when(systemFunctionBean).getMstrPerson();
        mstrPerson.setMail_address("testAddress@testServer");

        // KeyStoreUtilities の作成
        Mockito.doReturn("secretKey").when(keyStoreUtilities).getSecretKeyValue(anyString());

        // メッセージが正しく設定されたかを確認するため、メッセージキーを返却するように設定
        msgHandlerMock = Mockito.mockStatic(MessagesHandler.class);
        msgHandlerMock.when(() -> MessagesHandler.getString(anyString(), any(Locale.class))).then(createMsgAnswer(0));
        msgHandlerMock.when(() -> MessagesHandler.getString(anyString(), any(Locale.class), any())).then(createMsgAnswer(0));

        // ResourcesHandler のモック化
        resourcesHandlerMock = Mockito.mockStatic(ResourcesHandler.class);
        resourcesHandlerMock.when(() -> ResourcesHandler.getString(GnomesResourcesConstants.YY01_0001)).thenReturn("yyyy/MM/dd HH:mm:ss");

        // TalendJobRun のモック化
        talendJobRunMock = Mockito.mockStatic(TalendJobRun.class);
        talendJobRunMock.when(() -> TalendJobRun.runJob(anyString(), any(), anyBoolean())).thenAnswer(v -> null);
    }

    @AfterEach
    void tearDown() throws Exception {
        msgHandlerMock.close();
        resourcesHandlerMock.close();
        talendJobRunMock.close();
    }

    @Test
    @DisplayName("メール送信に必要な設定が揃っている場合、メール送信が行われる")
    void test_sendMail_success() throws Exception {
        blSecurity.sendMailChangePassword(certInfo);
        assertEquals("testAddress@testServer", mailInfoBean.getTo());
        assertEquals("testAddressFrom@testServer", mailInfoBean.getFrom());
        assertEquals("testUserId", mailInfoBean.getAuthenticatedUserId());
        assertEquals("secretKey", mailInfoBean.getAuthenticatedPassword());
        assertEquals("UTF-8", mailInfoBean.getEncode());
        assertEquals("testMailServerHost", mailInfoBean.getMailhost());
        assertEquals("25", mailInfoBean.getPort());
        assertEquals("STARTTLS,NOAUTH", mailInfoBean.getMailSendDiv());
        assertNotNull(mailInfoBean.getSubject());
        assertNotNull(mailInfoBean.getMessage());
        talendJobRunMock.verify(() -> TalendJobRun.runJob(anyString(), any(), anyBoolean()), Mockito.times(1));
    }

    @Test
    @DisplayName("メール送信フラグがOFFの場合、メール送信は行われない")
    void test_mailNoticeFlagOFF() throws Exception {
        mailFlagDefine.setNumeric1(new BigDecimal(ChangePasswordMailNoticeFlag.OFF.getValue()));
        blSecurity.sendMailChangePassword(certInfo);
        talendJobRunMock.verify(() -> TalendJobRun.runJob(anyString(), any(), anyBoolean()), Mockito.never());
    }

    @Test
    @DisplayName("宛先のメールアドレスが設定されていない場合、メール送信は行われない")
    void test_toMailAddressNotRegistered() throws Exception {
        mstrPerson.setMail_address("");
        blSecurity.sendMailChangePassword(certInfo);
        assertNull(mailInfoBean.getTo());
        talendJobRunMock.verify(() -> TalendJobRun.runJob(anyString(), any(), anyBoolean()), Mockito.never());
    }

    @Test
    @DisplayName("差出人のメールアドレスが設定されていない場合、メール送信は行われない")
    void test_fromAddressNotDefined() throws Exception {
        addressFromDefine.setChar1("");
        blSecurity.sendMailChangePassword(certInfo);
        assertNull(mailInfoBean.getFrom());
        talendJobRunMock.verify(() -> TalendJobRun.runJob(anyString(), any(), anyBoolean()), Mockito.never());
    }

    @Test
    @DisplayName("ユーザーIDが設定されていない場合、メール送信は行われない")
    void test_userIdNotDefined() throws Exception {
        userIdDefine.setChar1("");
        blSecurity.sendMailChangePassword(certInfo);
        assertNull(mailInfoBean.getAuthenticatedUserId());
        talendJobRunMock.verify(() -> TalendJobRun.runJob(anyString(), any(), anyBoolean()), Mockito.never());
    }

    @Test
    @DisplayName("エンコードが設定されていない場合、メール送信は行われない")
    void test_encodeNotDefined() throws Exception {
        encodeDefine.setChar1("");
        blSecurity.sendMailChangePassword(certInfo);
        assertNull(mailInfoBean.getEncode());
        talendJobRunMock.verify(() -> TalendJobRun.runJob(anyString(), any(), anyBoolean()), Mockito.never());
    }

    @Test
    @DisplayName("メールサーバーホストが設定されていない場合、メール送信は行われない")
    void test_serverHostNotDefined() throws Exception {
        mailServerHostDefine.setChar1("");
        blSecurity.sendMailChangePassword(certInfo);
        assertNull(mailInfoBean.getMailhost());
        talendJobRunMock.verify(() -> TalendJobRun.runJob(anyString(), any(), anyBoolean()), Mockito.never());
    }

    @Test
    @DisplayName("メールサーバーポート番号が設定されていない場合、メール送信は行われない")
    void test_serverPortNotDefined() throws Exception {
        mailServerPortDefine.setChar1("");
        blSecurity.sendMailChangePassword(certInfo);
        assertNull(mailInfoBean.getPort());
        talendJobRunMock.verify(() -> TalendJobRun.runJob(anyString(), any(), anyBoolean()), Mockito.never());
    }

    @Test
    @DisplayName("送信種別が設定されていない場合、メール送信は行われない")
    void test_mailSendDivNotDefined() throws Exception {
        mailSendDivDefine.setChar1("");
        blSecurity.sendMailChangePassword(certInfo);
        assertNull(mailInfoBean.getMailSendDiv());
        talendJobRunMock.verify(() -> TalendJobRun.runJob(anyString(), any(), anyBoolean()), Mockito.never());
    }

    private CertInfo createCertInfo() {
        CertInfo certInfo = new CertInfo();
        certInfo.setUserId("testUserID");
        certInfo.setNewPassword("testNewPassword");
        return certInfo;
    }

    private Answer<String> createMsgAnswer(int argIndex){
        return (Answer<String>) v ->{
            Object[] args = v.getArguments();
            String result = (String)args[argIndex];
            return result;
        };
    }
}
