package com.gnomes.system.logic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.system.dao.JudgePersonLicenseDao;
import com.gnomes.system.dao.MstrScreenButtonDao;
import com.gnomes.system.data.PartsPrivilegeResultInfo;
import com.gnomes.system.entity.MstrScreenButton;
import com.gnomes.uiservice.ContainerRequest;

class BLSecurityButtonTest {

    private static final String USER_ID = "tester_judge_persons_license_check_01";
    private static final String SCREEN_ID = "test_screen_01";
    private static final String BUTTON_ID = "test_button_01";
    private static final String PRIVILEGE_ID = "test_privilege_01";

    @InjectMocks
    BLSecurity security;

    @Mock
    GnomesSessionBean gnomesSessionBean;

    @Mock
    GnomesExceptionFactory exceptionFactory;

    @Mock
    MstrScreenButtonDao mstrScreenButtonDao;

    @Mock
    JudgePersonLicenseDao judgePersonLicenseDao;

    @Mock
    LogHelper logHelper;

    @Mock
    ContainerRequest req;

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
    @DisplayName("操作権限チェック：取得したGnomesSessionBeanのユーザIDと入力パラメータのユーザIDが一致しない")
    void test_judgePersonsLicenseCheck_userId_mismatch() {
        setupDoReturnUserIdCheckError();
        GnomesAppException e = assertThrows(GnomesAppException.class,() -> testRunJudgePersonsLicenseCheck(null));
        assertEquals("ME01.0033", e.getMessageNo());
    }

    @Test
    @DisplayName("操作権限チェック：画面IDが空の場合操作権限有り")
    void test_judgePersonsLicenseCheck_screenId_empty() throws Exception {
        setupDoReturnMstrScreenButtonDao(initMstrScreenButtonSimple());
        assertTrue(testRunJudgePersonsLicenseCheck(SCREEN_ID));
        // 画面ボタンマスタの取得処理が実行されていないこと
        verify(mstrScreenButtonDao, Mockito.times(0)).getMstrScreenButton(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("操作権限チェック：ボタンIDが空の場合操作権限有り")
    void test_judgePersonsLicenseCheck_buttonId_empty() throws Exception {
        setupDoReturnMstrScreenButtonDao(initMstrScreenButtonSimple());
        assertTrue(testRunJudgePersonsLicenseCheck(BUTTON_ID));
        // 画面ボタンマスタの取得処理が実行されていないこと
        verify(mstrScreenButtonDao, Mockito.times(0)).getMstrScreenButton(Mockito.any(), Mockito.any());
    }


    @Test
    @DisplayName("操作権限チェック：画面ボタンマスタがnullの場合操作権限有り")
    void test_judgePersonsLicenseCheck_mstrScreenButton_null() throws Exception {
        setupDoReturnMstrScreenButtonDao(null);
        assertTrue(testRunJudgePersonsLicenseCheck(null));
    }

    @Test
    @DisplayName("操作権限チェック：取得した画面ボタンマスタの権限IDが空の場合操作権限有り")
    void test_judgePersonsLicenseCheck_mstrScreenButton_privilegeId_empty() throws Exception {
        setupDoReturnMstrScreenButtonDao(initMstrScreenButtonSimple());
        assertTrue(testRunJudgePersonsLicenseCheck(null));
    }

    @Test
    @DisplayName("操作権限チェック：パーツ権限情報リストがnullの場合操作権限有り")
    void test_judgePersonsLicenseCheck_partsPrivilegeList_null() throws Exception {
        setupDoReturnAllPass();
        assertTrue(testRunJudgePersonsLicenseCheck(new ArrayList<>()));
    }

    @Test
    @DisplayName("操作権限チェック：押下されたボタンの権限がない")
    void test_judgePersonsLicenseCheck_isPrivilege_false() throws GnomesAppException {
        setupDoReturnJudge(false);
        GnomesAppException e = assertThrows(GnomesAppException.class,() -> testRunJudgePersonsLicenseCheck(null));
        assertEquals("ME01.0049", e.getMessageNo());
    }

    @Test
    @DisplayName("操作権限チェック：ユーザ委託代替作業権限を元にした権限判定がTrue")
    void test_judgePersonsLicenseCheck_isPrivilege_licenseSubstituteAddWorkCell_true() throws Exception {
        setupDoReturnJudge(true);
        assertTrue(testRunJudgePersonsLicenseCheck(null));
    }

    @Test
    @DisplayName("操作権限チェック：操作権限有り")
    void test_judgePersonsLicenseCheck_isPrivilege_true() throws Exception {
        setupDoReturnAllPass();
        assertTrue(testRunJudgePersonsLicenseCheck(null));
    }

    @Test
    @DisplayName("操作権限チェック：権限IDが空の場合権限有り")
    void test_judgePersonsLicenseCheck_privilegeId_empty() throws Exception {
        setupDoReturnAllPass();
        assertTrue(testRunJudgePersonsLicenseCheck(PRIVILEGE_ID));
        // ユーザ作業権限判定処理が実行されていないこと
        verify(judgePersonLicenseDao, Mockito.times(0)).judgePersonLicenseAddWorkCell(Mockito.any(), Mockito.any(),
            Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    /**
     * モック化 ユーザIDチェックエラー
     */
    private void setupDoReturnUserIdCheckError() {
        Mockito.doReturn("gnomes_session_bean").when(gnomesSessionBean).getUserId();
        Mockito.doReturn(new GnomesAppException(null, GnomesMessagesConstants.ME01_0033)).when(exceptionFactory)
            .createGnomesAppException(null, GnomesMessagesConstants.ME01_0033);
    }

    /**
     * モック化 画面ボタンマスタDao
     * @param entity Zm004画面ボタンマスタ エンティティ
     * @throws GnomesAppException
     */
    private void setupDoReturnMstrScreenButtonDao(MstrScreenButton entity) throws GnomesAppException {
        Mockito.doReturn(USER_ID).when(gnomesSessionBean).getUserId();
        Mockito.doReturn(entity).when(mstrScreenButtonDao).getMstrScreenButton(Mockito.any(), Mockito.any());
    }

    /**
     * モック化 権限有り
     * @throws GnomesAppException
     */
    private void setupDoReturnAllPass() throws GnomesAppException {
        Mockito.doReturn(USER_ID).when(gnomesSessionBean).getUserId();
        Mockito.doReturn(initMstrScreenButton()).when(mstrScreenButtonDao).getMstrScreenButton(Mockito.any(), Mockito.any());
        Mockito.doReturn(new GnomesAppException(null, GnomesMessagesConstants.ME01_0049)).when(exceptionFactory).createGnomesAppException(null, GnomesMessagesConstants.ME01_0049, BUTTON_ID);
        Mockito.doReturn(true).when(judgePersonLicenseDao).judgePersonLicenseAddWorkCell(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    /**
     * モック化 権限判定
     * @param isPrivilege 判定結果
     * @throws GnomesAppException
     */
    private void setupDoReturnJudge(boolean isPrivilege) throws GnomesAppException {
        Mockito.doReturn(USER_ID).when(gnomesSessionBean).getUserId();
        Mockito.doReturn(initMstrScreenButton()).when(mstrScreenButtonDao).getMstrScreenButton(Mockito.any(), Mockito.any());
        Mockito.doReturn(new GnomesAppException(null, GnomesMessagesConstants.ME01_0049)).when(exceptionFactory).createGnomesAppException(null, GnomesMessagesConstants.ME01_0049, BUTTON_ID);
        Mockito.doReturn(false).when(judgePersonLicenseDao).judgePersonLicenseAddWorkCell(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doReturn(isPrivilege).when(judgePersonLicenseDao).judgePersonLicenseSubstituteAddWorkCell(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    /**
     * Zm004画面ボタンマスタ初期化
     * @return Zm004画面ボタンマスタ
     */
    private MstrScreenButton initMstrScreenButtonSimple() {
        return new MstrScreenButton("test_screen_button_key", SCREEN_ID, BUTTON_ID, "UT", 1, 1,1);
    }

    /**
     * Zm004画面ボタンマスタ初期化
     * @return Zm004画面ボタンマスタ
     */
    private MstrScreenButton initMstrScreenButton() {
        return new MstrScreenButton("test_screen_button_key", SCREEN_ID, BUTTON_ID, PRIVILEGE_ID, "UT", 0,
            "UT01.0001", 1, 1, "UT02.0001", 1, 1, 1, "test_first_event_01", "30038536", "tester01", new Date(),
            "test_last_event_01", "99999999", "tester99", new Date(), 1);
    }

    /**
     * テスト対象メソッドの実行
     * @param param パラメータ
     * @return 期待値
     * @throws Exception
     */
    private boolean testRunJudgePersonsLicenseCheck(Object param) throws Exception {
        String screenId = SCREEN_ID;
        String buttonId = BUTTON_ID;
        List<PartsPrivilegeResultInfo> partsPrivilegeList = initPartsPrivilegeResultInfoList(PRIVILEGE_ID);
        String siteCode = "test_site";
        String orderProcessCode = "test_order_process_code";
        String workProcessCode = "test_work_process_code";
        String workCellCode = "test_work_cell_code";

        if (param != null && param instanceof String) {
            String emptyParam = (String) param;
            if (emptyParam.equals(screenId)) {
                screenId = "";
            } else if (emptyParam.equals(buttonId)) {
                buttonId = "";
            } else if (emptyParam.equals(PRIVILEGE_ID)) {
                partsPrivilegeList = initPartsPrivilegeResultInfoList("");
            }
        } else if (param != null && param instanceof List<?>) {
            partsPrivilegeList = null;
        }

        return security.judgePersonsLicenseCheck(USER_ID, screenId, buttonId, partsPrivilegeList, siteCode,
            orderProcessCode, workProcessCode, workCellCode);
    }

    /**
     * パーツ権限結果情報リスト初期化
     * @param privilegeId 権限ID
     * @return パーツ権限結果情報リスト
     */
    private List<PartsPrivilegeResultInfo> initPartsPrivilegeResultInfoList(String privilegeId) {
        List<PartsPrivilegeResultInfo> partsPrivilegeList = new ArrayList<>();
        PartsPrivilegeResultInfo info = new PartsPrivilegeResultInfo();
        info.setButtonId(BUTTON_ID);
        info.setPrivilegeId(privilegeId);
        partsPrivilegeList.add(info);
        return partsPrivilegeList;
    }

}
