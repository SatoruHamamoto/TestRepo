package com.gnomes.system.logic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.text.ParseException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.gnomes.common.constants.CommonEnums.CertificateType;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.data.UserInfo;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.system.dao.HistCertificationWriteDao;
import com.gnomes.system.dao.JudgePersonLicenseDao;
import com.gnomes.system.data.CertInfo;

class BLSecurityUserTest {

    @Spy
    @InjectMocks
    BLSecurity security;

    @Mock
    GnomesSessionBean gnomesSessionBean;

    @Mock
    JudgePersonLicenseDao judgePersonLicenseDao;

    @Mock
    HistCertificationWriteDao histCertificationWriteDao;

    @Mock
    EntityManager manager;

    @Mock
    EntityManagerFactory factory;

    @Mock
    EntityTransaction tran;

    @Mock
    GnomesExceptionFactory gnomesExceptionFactory;

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
    @DisplayName("ログインユーザ作業権限チェック：ユーザに委託された権限を元にした権限判定でtrueの場合権限有り")
    void test_judgeLoginUserLicense_judgePersonLicense_true() throws Exception {
        setDoReturnRunJudgeLoginUserLicense(true, false);
        assertTrue(testRunJudgeLoginUserLicense(CertificateType.APPROVE_PRIVILEGE_CHECK));
        // ユーザ委託代替作業権限を元にした権限判定が実行されていないこと
        verify(judgePersonLicenseDao, Mockito.times(0)).judgePersonLicenseSubstitute(Mockito.any(), Mockito.any(),
            Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("ログインユーザ作業権限チェック：ユーザに委託された権限を元にした権限判定でfalseの場合権限無し")
    void test_judgeLoginUserLicense_judgePersonLicense_false() throws Exception {
        setDoReturnRunJudgeLoginUserLicense(false, false);
        assertTrue(!testRunJudgeLoginUserLicense(CertificateType.APPROVE_PRIVILEGE_CHECK));
        // ユーザ委託代替作業権限を元にした権限判定が実行されていないこと
        verify(judgePersonLicenseDao, Mockito.times(0)).judgePersonLicenseSubstitute(Mockito.any(), Mockito.any(),
            Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("ログインユーザ作業権限チェック：ユーザ委託代替作業権限を元にした権限判定でtrueの場合権限有り")
    void test_judgeLoginUserLicense_judgePersonLicenseSubstitute_true() throws Exception {
        setDoReturnRunJudgeLoginUserLicense(false, true);
        assertTrue(testRunJudgeLoginUserLicense(CertificateType.ALTERNATE_APPROVE_PRIVILEGE_CHECK));
        // ユーザ作業権限判定が実行されていないこと
        verify(judgePersonLicenseDao, Mockito.times(0)).judgePersonLicense(Mockito.any(), Mockito.any(),
            Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("ログインユーザ作業権限チェック：ユーザ委託代替作業権限を元にした権限判定でfalse権限無し")
    void test_judgeLoginUserLicense_judgePersonLicenseSubstitute_false() throws Exception {
        setDoReturnRunJudgeLoginUserLicense(false, false);
        assertTrue(!testRunJudgeLoginUserLicense(CertificateType.ALTERNATE_APPROVE_PRIVILEGE_CHECK));
        // ユーザ作業権限判定が実行されていないこと
        verify(judgePersonLicenseDao, Mockito.times(0)).judgePersonLicense(Mockito.any(), Mockito.any(),
            Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("ログインユーザ作業権限チェック：認証履歴登録時にExceptionが発生した場合GnomesExceptionをスロー")
    void test_judgeLoginUserLicense_insertHistCertification_Exception() throws Exception {
        setDoReturnRunJudgeLoginUserLicense(true, false);
        // 認証履歴登録時GnomesAppExceptionをスローさせる
        Mockito.doThrow(new GnomesAppException(new Exception())).when(security).insertHistCertification(Mockito.any(CertInfo.class),
            Mockito.any(UserInfo.class), Mockito.anyBoolean(), Mockito.any(CertificateType.class), Mockito.anyString(),
            Mockito.any(EntityManager.class));

        assertThrows(GnomesException.class, () -> testRunJudgeLoginUserLicense(CertificateType.APPROVE_PRIVILEGE_CHECK));
    }

    @Test
    @DisplayName("ログインユーザ作業権限チェック：ユーザに委託された権限を元にした権限判定時にExceptionが発生した場合GnomesExceptionをスロー")
    void test_judgeLoginUserLicense_judgePersonLicense_exception() throws Exception {
        setDoReturnRunJudgeLoginUserLicense(true, true);
        // ユーザ作業権限判定時GnomesAppExceptionをスローさせる
        Mockito.doThrow(new GnomesAppException(new Exception())).when(judgePersonLicenseDao)
            .judgePersonLicense(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        assertThrows(GnomesException.class, () -> testRunJudgeLoginUserLicense(CertificateType.APPROVE_PRIVILEGE_CHECK));

        // 認証履歴登録処理が実行されていないこと
        verify(security, Mockito.times(0)).insertHistCertification(Mockito.any(CertInfo.class),
            Mockito.any(UserInfo.class), Mockito.anyBoolean(), Mockito.any(CertificateType.class), Mockito.anyString(),
            Mockito.any(EntityManager.class));
    }

    @Test
    @DisplayName("ログインユーザ作業権限チェック：ユーザ委託代替作業権限を元にした権限判定時にExceptionが発生した場合GnomesExceptionをスロー")
    void test_judgeLoginUserLicense_judgePersonLicenseSubstitute_exception() throws Exception {
        setDoReturnRunJudgeLoginUserLicense(true, true);
        // ユーザ委託代替作業権限を元にした権限判定時GnomesAppExceptionをスローさせる
        Mockito.doThrow(new GnomesAppException(new Exception())).when(judgePersonLicenseDao)
            .judgePersonLicenseSubstitute(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        assertThrows(GnomesException.class, () -> testRunJudgeLoginUserLicense(CertificateType.ALTERNATE_APPROVE_PRIVILEGE_CHECK));

        // 認証履歴登録処理が実行されていないこと
        verify(security, Mockito.times(0)).insertHistCertification(Mockito.any(CertInfo.class),
            Mockito.any(UserInfo.class), Mockito.anyBoolean(), Mockito.any(CertificateType.class), Mockito.anyString(),
            Mockito.any(EntityManager.class));
    }


    /**
     * モック化 ログインユーザ作業権限チェック
     * @param isPrivilege1 作業権限チェック判定結果
     * @param isPrivilege2 ユーザ委託代替作業権限を元にした権限判定結果
     * @throws GnomesAppException
     * @throws ParseException
     */
    private void setDoReturnRunJudgeLoginUserLicense(boolean isPrivilege1, boolean isPrivilege2) throws GnomesAppException, ParseException {
        Mockito.doReturn("tester_judge_user_license_01").when(gnomesSessionBean).getUserId();
        Mockito.doReturn("test_computer_01").when(gnomesSessionBean).getComputerId();
        Mockito.doReturn("0.0.0.0").when(gnomesSessionBean).getIpAddress();
        Mockito.doReturn("test_user_01").when(gnomesSessionBean).getUserName();
        Mockito.doReturn("testComputer01").when(gnomesSessionBean).getComputerName();
        Mockito.doReturn("1").when(gnomesSessionBean).getRegionType();
        Mockito.doReturn(manager).when(factory).createEntityManager();
        Mockito.doReturn(tran).when(manager).getTransaction();

        Mockito.doReturn(isPrivilege1).when(judgePersonLicenseDao).judgePersonLicense(Mockito.any(), Mockito.any(),
            Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doReturn(isPrivilege2).when(judgePersonLicenseDao).judgePersonLicenseSubstitute(Mockito.any(),
            Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doReturn(new GnomesException(new Exception())).when(gnomesExceptionFactory)
            .createGnomesException(Mockito.any(), Mockito.anyString());

        Mockito.doNothing().when(security).insertHistCertification(Mockito.any(CertInfo.class),
            Mockito.any(UserInfo.class), Mockito.anyBoolean(), Mockito.any(CertificateType.class), Mockito.anyString(),
            Mockito.any(EntityManager.class));
    }

    /**
     * テスト対象メソッドの実行
     * @param type 認証区分
     * @return 期待値
     * @throws Exception
     */
    private boolean testRunJudgeLoginUserLicense(CertificateType type) throws GnomesAppException {
        String privilegeId = "test_privilege_01";
        String siteCode = "test_site";
        String orderProcessCode = "test_order_process_code";
        String workProcessCode = "test_work_process_code";
        return security.judgeLoginUserLicense(privilegeId, siteCode, orderProcessCode, workProcessCode, type);
    }

}
