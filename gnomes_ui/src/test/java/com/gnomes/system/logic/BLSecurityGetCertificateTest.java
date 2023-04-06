package com.gnomes.system.logic;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.SystemDefConstants;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.data.GnomesSystemBean;
import com.gnomes.common.data.UserInfo;
import com.gnomes.common.resource.GnomesLogMessageConstants;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.GnomesPasswordEncoder;
import com.gnomes.common.util.Holder;
import com.gnomes.system.dao.InfoComputerProcWorkcellDao;
import com.gnomes.system.dao.InfoUserDao;
import com.gnomes.system.dao.MstrComputerDao;
import com.gnomes.system.dao.MstrPersonDao;
import com.gnomes.system.dao.MstrPersonSecPolicyDao;
import com.gnomes.system.dao.MstrSiteDao;
import com.gnomes.system.dao.MstrSystemDefineDao;
import com.gnomes.system.data.CertInfo;
import com.gnomes.system.data.SystemFunctionBean;
import com.gnomes.system.entity.InfoComputerProcWorkcell;
import com.gnomes.system.entity.InfoUser;
import com.gnomes.system.entity.MstrComputer;
import com.gnomes.system.entity.MstrPerson;
import com.gnomes.system.entity.MstrPersonSecPolicy;
import com.gnomes.system.entity.MstrSite;
import com.gnomes.system.entity.MstrSystemDefine;
import com.gnomes.uiservice.ContainerRequest;

public class BLSecurityGetCertificateTest
{
    @InjectMocks
    BLSecurity blSecurity;

    @Mock
    SystemFunctionBean systemFunctionBean;

    @Mock
    GnomesSystemBean gnomesSystemBean;

    @Mock
    GnomesSessionBean gnomesSessionBean;

    @Mock
    MstrPersonSecPolicyDao mstrPersonSecPolicyDao;

    @Mock
    InfoUserDao infoUserDao;

    @Mock
    MstrComputerDao mstrComputerDao;

    @Mock
    MstrSiteDao mstrSiteDao;

    @Mock
    MstrPersonDao mstrPersonDao;

    @Mock
    MstrSystemDefineDao mstrSystemDefineDao;

    @Mock
    InfoComputerProcWorkcellDao infoComputerProcWorkcellDao;

    @Mock
    EntityManager em;

    @Mock
    ContainerRequest req;

    MockedStatic<MessagesHandler> msgHandlerMock;

    MockedStatic<ConverterUtils> converterUtilsMock;


    private MstrPersonSecPolicy mstrPersonSecPolicy;

    private InfoUser infoUser;


    private CertInfo certInfo;

    private UserInfo userInfo;

    private Boolean isLoginCertify;

    Holder<Integer> isSuccess;

    Holder<Integer> isChangePassword;

    Holder<String> message;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // blSecurity.getCertificate() へ渡す引数を初期化
        certInfo = createCertInfo();
        userInfo = new UserInfo();
        isLoginCertify = true;
        isSuccess = new Holder<Integer>(0);
        isChangePassword = new Holder<Integer>(0);
        message = new Holder<String>("");

        // セキュリティポリシー情報を作成
        List<MstrPersonSecPolicy> mstrPersonSecPolicies = new ArrayList<>();
        Mockito.doReturn(mstrPersonSecPolicies).when(mstrPersonSecPolicyDao).getMstrPersonSecPolicy();
        mstrPersonSecPolicy = new MstrPersonSecPolicy();
        mstrPersonSecPolicy.setUser_id_limit_day(10);
        mstrPersonSecPolicy.setPassword_limit_day(30);
        mstrPersonSecPolicy.setPassword_limit_caution(10);
        mstrPersonSecPolicy.setApplication_lock_waiting_time(5);
        mstrPersonSecPolicies.add(mstrPersonSecPolicy);

        // ユーザー情報を作成
        infoUser = new InfoUser();
        Mockito.doReturn(infoUser).when(infoUserDao).getInfoUser(certInfo.getUserId(), em);
        infoUser.setPassword(GnomesPasswordEncoder.encode(certInfo.getPassword()));
        infoUser.setUser_id_valid_from(getDateFromToday(-30));
        infoUser.setUser_id_valid_to(getDateFromToday(30));
        infoUser.setPassword_update_date(getDateFromToday(-1));
        infoUser.setIs_lock_out(0);

        // 端末情報を作成
        MstrComputer mstrComputer = new MstrComputer();
        Mockito.doReturn(mstrComputer).when(mstrComputerDao).getMstrComputer(certInfo.getComputerId());
        mstrComputer.setComputer_name("testComputerName");

        // サイト情報を作成
        MstrSite mstrSite = new MstrSite();
        Mockito.doReturn(mstrSite).when(mstrSiteDao).getMstrSite(certInfo.getSiteCode());
        mstrSite.setSite_name("testSiteName");

        // ユーザーマスタ情報を作成
        List<MstrPerson> mstrPersons = new ArrayList<>();
        MstrPerson mstrPerson = new MstrPerson();
        Mockito.doReturn(mstrPersons).when(mstrPersonDao).getMstrPersonQuery(certInfo.getUserId(), em);
        mstrPersons.add(mstrPerson);
        mstrPerson.setUser_key("testUserKey");
        mstrPerson.setUser_name("testUserName");

        // システム定数を作成
        List<MstrSystemDefine> mstrSystemDefines = new ArrayList<>();
        Mockito.doReturn(mstrSystemDefines).when(mstrSystemDefineDao).getMstrSystemDefine();
        MstrSystemDefine defineMaxListDisplayCount = new MstrSystemDefine();
        defineMaxListDisplayCount.setSystem_define_code(SystemDefConstants.MESSAGE_MAX_LIST_DISPLAY_COUNT);
        defineMaxListDisplayCount.setNumeric1(new BigDecimal(100));
        mstrSystemDefines.add(defineMaxListDisplayCount);
        MstrSystemDefine definePopupDisplayCount = new MstrSystemDefine();
        definePopupDisplayCount.setSystem_define_code(SystemDefConstants.MESSAGE_POPUP_DISPLAY_COUNT);
        definePopupDisplayCount.setNumeric1(new BigDecimal(10));
        mstrSystemDefines.add(definePopupDisplayCount);
        MstrSystemDefine defineWatchPeriodForPopup = new MstrSystemDefine();
        defineWatchPeriodForPopup.setSystem_define_code(SystemDefConstants.MESSAGE_WATCH_PERIOD_FOR_POPUP);
        defineWatchPeriodForPopup.setNumeric1(new BigDecimal(5));
        mstrSystemDefines.add(defineWatchPeriodForPopup);

        // 端末工程作業場所選択情報を作成
        InfoComputerProcWorkcell infoComputerProcWorkcell = new InfoComputerProcWorkcell();
        Mockito.doReturn(infoComputerProcWorkcell).when(infoComputerProcWorkcellDao).getInfoComputerProcWorkcell(
                certInfo.getComputerId(), certInfo.getSiteCode());
        infoComputerProcWorkcell.setProcess_code("testProcessCode");
        infoComputerProcWorkcell.setWorkprocess_code("testWorkprocedsCode");
        infoComputerProcWorkcell.setWorkcell_code("testWorkcellCode");

        // SystemBean の作成
        Mockito.doReturn(CommonConstants.JDBC).when(gnomesSystemBean).getLoginModuleType();

        // SessionBean の作成
        Mockito.doReturn(Locale.JAPAN).when(gnomesSessionBean).getUserLocale();

        // メッセージが正しく設定されたかを確認するため、メッセージキーを返却するように設定
        msgHandlerMock = Mockito.mockStatic(MessagesHandler.class);
        msgHandlerMock.when(() -> MessagesHandler.getString(anyString(), any(Locale.class))).then(createMsgAnswer(0));
        msgHandlerMock.when(() -> MessagesHandler.getString(anyString(), any(Locale.class), any())).then(createMsgAnswer(0));

        // ConverterUtils.stringToNumber を Locale 指定なしで呼び出した場合のモック化
        converterUtilsMock = Mockito.mockStatic(ConverterUtils.class);
        converterUtilsMock.when(() -> ConverterUtils.stringToNumber(anyBoolean(), anyString())).thenAnswer(v -> {
            return NumberFormat.getNumberInstance().parseObject(v.getArgument(1));
        });

        // @ToDo: 上記は暫定対策：本来は BLSecurity 側を修正して、Locale 指定ありで呼び出すようにする
        // もしくは、Locale.getDefault が "en", "jp" 等の場合でも、ConverterUtils.stringToNumber が動作するように修正する
        // （現在は、Locale.getDefault が "en_US, "ja_JP" のような形式の場合にだけ、正常に動作する実装になっている）
    }

    @AfterEach
    void tearDown() throws Exception {
        msgHandlerMock.close();
        converterUtilsMock.close();
    }

    @Test
    @DisplayName("ログイン認証成功の場合、ユーザー情報を取得できる")
    void test_getCertificate_success() throws Exception {
        blSecurity.getCertificate(certInfo, userInfo, isLoginCertify, isSuccess, isChangePassword, message, em);
        assertEquals(1, isSuccess.value);
        assertEquals(0, isChangePassword.value);
        assertEquals("", message.value);
        assertEquals(certInfo.getUserId(), userInfo.getUserId());
        assertEquals(certInfo.getPassword(), userInfo.getPassword());
        assertEquals(certInfo.getLocaleId(), userInfo.getLocaleId());
        assertEquals(certInfo.getComputerId(), userInfo.getComputerId());
        assertEquals(certInfo.getSiteCode(), userInfo.getSiteCode());
        assertEquals("testComputerName", userInfo.getComputerName());
        assertEquals("testSiteName", userInfo.getSiteName());
        assertEquals("testUserKey", userInfo.getUserKey());
        assertEquals("testUserName", userInfo.getUserName());
        assertEquals(100, userInfo.getMaxListDisplayCount());
        assertEquals(10, userInfo.getPopupDisplayCount());
        assertEquals(5, userInfo.getWatchPeriodForPopup());
        assertEquals("testProcessCode", userInfo.getProcessCode());
        assertEquals("testWorkprocedsCode", userInfo.getWorkProcessCode());
        assertEquals("testWorkcellCode", userInfo.getWorkCellCode());
        assertEquals("LOCALE_JA_JP", userInfo.getLanguage());
        assertEquals(5, userInfo.getScreenLockTimeoutTime());
        assertFalse(userInfo.getIsScreenLocked());
    }

    @Test
    @DisplayName("ログイン以外の認証成功の場合、ユーザー情報を取得できる")
    void test_getCertificateNotLogin_success() throws Exception {
        isLoginCertify = false;
        blSecurity.getCertificate(certInfo, userInfo, isLoginCertify, isSuccess, isChangePassword, message, em);
        assertEquals(1, isSuccess.value);
        assertEquals(0, isChangePassword.value);
        assertEquals("", message.value);
        assertEquals(certInfo.getUserId(), userInfo.getUserId());
        assertEquals(certInfo.getPassword(), userInfo.getPassword());
        assertEquals(certInfo.getLocaleId(), userInfo.getLocaleId());
        assertEquals(certInfo.getComputerId(), userInfo.getComputerId());
        assertEquals(certInfo.getSiteCode(), userInfo.getSiteCode());
        assertEquals("testComputerName", userInfo.getComputerName());
        assertEquals("testSiteName", userInfo.getSiteName());
        assertEquals("testUserKey", userInfo.getUserKey());
        assertEquals("testUserName", userInfo.getUserName());
        assertEquals(100, userInfo.getMaxListDisplayCount());
        assertEquals(10, userInfo.getPopupDisplayCount());
        assertEquals(5, userInfo.getWatchPeriodForPopup());
        assertNull(userInfo.getProcessCode());
        assertNull(userInfo.getWorkProcessCode());
        assertNull(userInfo.getWorkCellCode());
        assertEquals("LOCALE_JA_JP", userInfo.getLanguage());
        assertEquals(5, userInfo.getScreenLockTimeoutTime());
        assertFalse(userInfo.getIsScreenLocked());
    }

    @Test
    @DisplayName("ユーザーIDが登録されていない場合、エラーを返す")
    void test_userIdNotExists() throws Exception {
        certInfo.setUserId("testInvalidUserID");
        blSecurity.getCertificate(certInfo, userInfo, isLoginCertify, isSuccess, isChangePassword, message, em);
        assertEquals(0, isSuccess.value);
        assertEquals(0, isChangePassword.value);
        assertEquals(GnomesMessagesConstants.ME01_0198, message.value);
    }

    @Test
    @DisplayName("アカウントがロックされている場合、エラーを返す")
    void test_accountLocked() throws Exception {
        infoUser.setIs_lock_out(1);
        blSecurity.getCertificate(certInfo, userInfo, isLoginCertify, isSuccess, isChangePassword, message, em);
        assertEquals(0, isSuccess.value);
        assertEquals(0, isChangePassword.value);
        assertEquals(GnomesMessagesConstants.ME01_0010, message.value);
    }

    @Test
    @DisplayName("アカウントの有効期限が切れている場合、エラーを返す")
    void test_accountExpired() throws Exception {
        infoUser.setUser_id_valid_from(getDateFromToday(-31)); // アカウントの有効期間の開始が31日前
        infoUser.setUser_id_valid_to(getDateFromToday(-1)); // アカウントの有効期間の終了が1日前
        blSecurity.getCertificate(certInfo, userInfo, isLoginCertify, isSuccess, isChangePassword, message, em);
        assertEquals(0, isSuccess.value);
        assertEquals(0, isChangePassword.value);
        assertEquals(GnomesMessagesConstants.ME01_0015, message.value);
    }

    @Test
    @DisplayName("アカウントの有効期間が未来の場合、エラーを返す")
    void test_accountWillBeValid() throws Exception {
        infoUser.setUser_id_valid_from(getDateFromToday(1)); // アカウントの有効期間の開始が1日後
        infoUser.setUser_id_valid_to(getDateFromToday(31)); // アカウントの有効期間の終了が31日後
        blSecurity.getCertificate(certInfo, userInfo, isLoginCertify, isSuccess, isChangePassword, message, em);
        assertEquals(0, isSuccess.value);
        assertEquals(0, isChangePassword.value);
        assertEquals(GnomesMessagesConstants.ME01_0015, message.value);
    }

    @Test
    @DisplayName("パスワードの有効期限が切れている場合、エラーを返す")
    void test_passwordExpired() throws Exception {
        infoUser.setPassword_update_date(getDateFromToday(-31)); // パスワードを更新したのが31日前
        mstrPersonSecPolicy.setPassword_limit_day(30); // パスワードの有効期間が30日間
        blSecurity.getCertificate(certInfo, userInfo, isLoginCertify, isSuccess, isChangePassword, message, em);
        assertEquals(0, isSuccess.value);
        assertEquals(0, isChangePassword.value);
        assertEquals(GnomesMessagesConstants.ME01_0016, message.value);
    }

    @Test
    @DisplayName("アカウントの有効期限切れが迫っている場合、警告を返す")
    void test_accountWillBeExpired() throws Exception {
        infoUser.setUser_id_valid_from(getDateFromToday(-29)); // アカウントの有効期間の開始が29日前
        infoUser.setUser_id_valid_to(getDateFromToday(1)); // アカウントの有効期間の終了が1日後
        mstrPersonSecPolicy.setUser_id_limit_day(10); // アカウントの有効期限切れの10日前から警告を出す
        blSecurity.getCertificate(certInfo, userInfo, isLoginCertify, isSuccess, isChangePassword, message, em);
        assertEquals(1, isSuccess.value);
        assertEquals(0, isChangePassword.value);
        assertEquals(GnomesMessagesConstants.MA01_0002, message.value);
    }

    @Test
    @DisplayName("パスワードの有効期限切れが迫っている場合、警告を返す")
    void test_passwordWillBeExpired() throws Exception {
        infoUser.setPassword_update_date(getDateFromToday(-29)); // パスワードを更新したのが29日前
        mstrPersonSecPolicy.setPassword_limit_day(30); // パスワードの有効期間が30日間
        mstrPersonSecPolicy.setPassword_limit_caution(10); // パスワードの有効期限切れの10日前から警告を出す
        blSecurity.getCertificate(certInfo, userInfo, isLoginCertify, isSuccess, isChangePassword, message, em);
        assertEquals(1, isSuccess.value);
        assertEquals(1, isChangePassword.value);
        assertEquals(GnomesMessagesConstants.MA01_0003, message.value);
    }

    @Test
    @DisplayName("パスワードの有効期限切れが迫っていても、パスワード変更は不要と指定した場合、警告を返さない")
    void test_passwordWillBeExpired_withoutWarning() throws Exception {
        infoUser.setPassword_update_date(getDateFromToday(-29)); // パスワードを更新したのが29日前
        mstrPersonSecPolicy.setPassword_limit_day(30); // パスワードの有効期間が30日間
        mstrPersonSecPolicy.setPassword_limit_caution(10); // パスワードの有効期限切れの10日前から警告を出す
        certInfo.setIsChangePassword(0); // パスワード変更は不要
        blSecurity.getCertificate(certInfo, userInfo, isLoginCertify, isSuccess, isChangePassword, message, em);
        assertEquals(1, isSuccess.value);
        assertEquals(0, isChangePassword.value);
        assertEquals("", message.value);
    }

    @Test
    @DisplayName("端末IDが登録されていない場合、エラーを返す")
    void test_invalidComputerId() throws Exception {
        certInfo.setComputerId("testInvaiComputerID");
        blSecurity.getCertificate(certInfo, userInfo, isLoginCertify, isSuccess, isChangePassword, message, em);
        assertEquals(0, isSuccess.value);
        assertEquals(0, isChangePassword.value);
        assertEquals(GnomesMessagesConstants.ME01_0250, message.value);
    }

    @Test
    @DisplayName("拠点コードが登録されていない場合、エラーを返す")
    void test_invalidSiteCode() throws Exception {
        certInfo.setSiteCode("testInvalidSiteCode");
        blSecurity.getCertificate(certInfo, userInfo, isLoginCertify, isSuccess, isChangePassword, message, em);
        assertEquals(0, isSuccess.value);
        assertEquals(0, isChangePassword.value);
        assertEquals(GnomesLogMessageConstants.ME01_0026, message.value);
    }

    @Test
    @Disabled
    @DisplayName("ログイン認証ではなく、パスワード照合が一致しない場合、エラーを返す")
    void test_invalidPassword() throws Exception {
        certInfo.setPassword("testInvalidPassword");
        isLoginCertify = false;
        blSecurity.getCertificate(certInfo, userInfo, isLoginCertify, isSuccess, isChangePassword, message, em);
        assertEquals(0, isSuccess.value);
        assertEquals(0, isChangePassword.value);
        assertEquals(GnomesMessagesConstants.ME01_0198, message.value);
    }

    @Test
    @DisplayName("ログイン認証ではなく、登録されているパスワードが空の場合、エラーを返す")
    void test_passwordNotRegistered() throws Exception {
        infoUser.setPassword("");
        isLoginCertify = false;
        blSecurity.getCertificate(certInfo, userInfo, isLoginCertify, isSuccess, isChangePassword, message, em);
        assertEquals(0, isSuccess.value);
        assertEquals(0, isChangePassword.value);
        assertEquals(GnomesMessagesConstants.ME01_0198, message.value);
    }

    private CertInfo createCertInfo() {
        CertInfo certInfo = new CertInfo();
        certInfo.setUserId("testUserID");
        certInfo.setPassword("testPassword");
        certInfo.setLocalId("ja_JP");
        certInfo.setComputerId("testComputerID");
        certInfo.setSiteCode("testSiteCode");
        certInfo.setIsChangePassword(1);
        return certInfo;
    }

    private Date getDateFromToday(int interval_day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, interval_day);
        return cal.getTime();
    }

    private Answer<String> createMsgAnswer(int argIndex){
        return (Answer<String>) v ->{
            Object[] args = v.getArguments();
            String result = (String)args[argIndex];
            return result;
        };
    }
}
