package com.gnomes.system.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

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

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.resource.GnomesMessagesConstants;

class JudgePersonLicenseDaoTest {

    private static final String USER_ID = "test_userId";
    private static final String PRIVILEGE_ID = "test_privilegeId";
    private static final String SITE_CODE = "test_siteCode";
    private static final String ORDER_PROCESS_CODE = "test_orderProcessCode";
    private static final String WORK_PROCESS_CODE = "test_workProcessCode";
    private static final String WORK_CELL_CODE = "test_workCellCode";

    @InjectMocks
    JudgePersonLicenseDao dao;

    @Mock
    GnomesExceptionFactory exceptionFactory;

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
    @DisplayName("ユーザ作業権限判定：ユーザIDが空の場合GnomesAppExceptionをスロー")
    void test_judgePersonLicense_userId_empty() throws Exception {
        setupDoReturnGnomesAppException("");
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> testRunJudgePersonLicense(USER_ID));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ユーザ作業権限判定：権限IDが空の場合GnomesAppExceptionをスロー")
    void test_judgePersonLicense_privilegeId_empty() throws Exception {
        setupDoReturnGnomesAppException("");
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> testRunJudgePersonLicense(PRIVILEGE_ID));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ユーザ作業権限判定：拠点コードが空の場合GnomesAppExceptionをスロー")
    void test_judgePersonLicense_siteCode_empty() throws Exception {
        setupDoReturnGnomesAppException("");
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> testRunJudgePersonLicense(SITE_CODE));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ユーザ作業権限判定：指図工程コードが空の場合GnomesAppExceptionをスロー")
    void test_judgePersonLicense_orderProcessCode_empty() throws Exception {
        setupDoReturnGnomesAppException("");
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> testRunJudgePersonLicense(ORDER_PROCESS_CODE));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ユーザ作業権限判定：作業工程コードが空の場合GnomesAppExceptionをスロー")
    void test_judgePersonLicense_workProcessCode_empty() throws Exception {
        setupDoReturnGnomesAppException("");
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> testRunJudgePersonLicense(WORK_PROCESS_CODE));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ユーザ委託代替作業権限を元にした権限判定：ユーザIDが空の場合GnomesAppExceptionをスロー")
    void test_judgePersonLicenseSubstitute_userId_empty() throws Exception {
        setupDoReturnGnomesAppException("");
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> testRunJudgePersonLicenseSubstitute(USER_ID));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ユーザ委託代替作業権限を元にした権限判定：権限IDが空の場合GnomesAppExceptionをスロー")
    void test_judgePersonLicenseSubstitute_privilegeId_empty() throws Exception {
        setupDoReturnGnomesAppException("");
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> testRunJudgePersonLicenseSubstitute(PRIVILEGE_ID));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ユーザ委託代替作業権限を元にした権限判定：システム日付がnullの場合GnomesAppExceptionをスロー")
    void test_judgePersonLicenseSubstitute_date_null() throws Exception {
        setupDoReturnGnomesAppException(null);
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> testRunJudgePersonLicenseSubstitute(new Date()));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ユーザ委託代替作業権限を元にした権限判定：拠点コードが空の場合GnomesAppExceptionをスロー")
    void test_judgePersonLicenseSubstitute_siteCode_empty() throws Exception {
        setupDoReturnGnomesAppException("");
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> testRunJudgePersonLicenseSubstitute(SITE_CODE));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ユーザ委託代替作業権限を元にした権限判定：指図工程コードが空の場合GnomesAppExceptionをスロー")
    void test_judgePersonLicenseSubstitute_orderProcessCode_empty() throws Exception {
        setupDoReturnGnomesAppException("");
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> testRunJudgePersonLicenseSubstitute(ORDER_PROCESS_CODE));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ユーザ委託代替作業権限を元にした権限判定：作業工程コードが空の場合GnomesAppExceptionをスロー")
    void test_judgePersonLicenseSubstitute_workProcessCode_empty() throws Exception {
        setupDoReturnGnomesAppException("");
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> testRunJudgePersonLicenseSubstitute(WORK_PROCESS_CODE));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ユーザ作業権限判定(作業場所含む)：ユーザIDが空の場合GnomesAppExceptionをスロー")
    void test_judgePersonLicenseAddWorkCell_userId_empty() throws Exception {
        setupDoReturnGnomesAppException("");
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> testRunJudgePersonLicenseAddWorkCell(USER_ID));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ユーザ作業権限判定(作業場所含む)：権限IDが空の場合GnomesAppExceptionをスロー")
    void test_judgePersonLicenseAddWorkCell_privilegeId_empty() throws Exception {
        setupDoReturnGnomesAppException("");
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> testRunJudgePersonLicenseAddWorkCell(PRIVILEGE_ID));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ユーザ作業権限判定(作業場所含む)：拠点コードが空の場合GnomesAppExceptionをスロー")
    void test_judgePersonLicenseAddWorkCell_siteCode_empty() throws Exception {
        setupDoReturnGnomesAppException("");
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> testRunJudgePersonLicenseAddWorkCell(SITE_CODE));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ユーザ作業権限判定(作業場所含む)：指図工程コードが空の場合GnomesAppExceptionをスロー")
    void test_judgePersonLicenseAddWorkCell_orderProcessCode_empty() throws Exception {
        setupDoReturnGnomesAppException("");
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> testRunJudgePersonLicenseAddWorkCell(ORDER_PROCESS_CODE));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ユーザ作業権限判定(作業場所含む)：作業工程コードが空の場合GnomesAppExceptionをスロー")
    void test_judgePersonLicenseAddWorkCell_workProcessCode_empty() throws Exception {
        setupDoReturnGnomesAppException("");
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> testRunJudgePersonLicenseAddWorkCell(WORK_PROCESS_CODE));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ユーザ作業権限判定(作業場所含む)：作業場所コードが空の場合GnomesAppExceptionをスロー")
    void test_judgePersonLicenseAddWorkCell_workCellCode_empty() throws Exception {
        setupDoReturnGnomesAppException("");
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> testRunJudgePersonLicenseAddWorkCell(WORK_CELL_CODE));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ユーザ委託代替作業権限を元にした権限判定(作業場所含む)：ユーザIDが空の場合GnomesAppExceptionをスロー")
    void test_judgePersonLicenseSubstituteAddWorkCell_userId_empty() throws Exception {
        setupDoReturnGnomesAppException("");
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> testRunJudgePersonLicenseSubstituteAddWorkCell(USER_ID));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ユーザ委託代替作業権限を元にした権限判定(作業場所含む)：権限IDが空の場合GnomesAppExceptionをスロー")
    void test_judgePersonLicenseSubstituteAddWorkCell_privilegeId_empty() throws Exception {
        setupDoReturnGnomesAppException("");
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> testRunJudgePersonLicenseSubstituteAddWorkCell(PRIVILEGE_ID));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ユーザ委託代替作業権限を元にした権限判定(作業場所含む)：システム日付がnullの場合GnomesAppExceptionをスロー")
    void test_judgePersonLicenseSubstituteAddWorkCell_date_null() throws Exception {
        setupDoReturnGnomesAppException(null);
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> testRunJudgePersonLicenseSubstituteAddWorkCell(new Date()));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ユーザ委託代替作業権限を元にした権限判定(作業場所含む)：拠点コードが空の場合GnomesAppExceptionをスロー")
    void test_judgePersonLicenseSubstituteAddWorkCell_siteCode_empty() throws Exception {
        setupDoReturnGnomesAppException("");
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> testRunJudgePersonLicenseSubstituteAddWorkCell(SITE_CODE));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ユーザ委託代替作業権限を元にした権限判定(作業場所含む)：指図工程コードが空の場合GnomesAppExceptionをスロー")
    void test_judgePersonLicenseSubstituteAddWorkCell_orderProcessCode_empty() throws Exception {
        setupDoReturnGnomesAppException("");
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> testRunJudgePersonLicenseSubstituteAddWorkCell(ORDER_PROCESS_CODE));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ユーザ委託代替作業権限を元にした権限判定(作業場所含む)：作業工程コードが空の場合GnomesAppExceptionをスロー")
    void test_judgePersonLicenseSubstituteAddWorkCell_workProcessCode_empty() throws Exception {
        setupDoReturnGnomesAppException("");
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> testRunJudgePersonLicenseSubstituteAddWorkCell(WORK_PROCESS_CODE));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ユーザ委託代替作業権限を元にした権限判定(作業場所含む)：作業場所コードが空の場合GnomesAppExceptionをスロー")
    void test_judgePersonLicenseSubstituteAddWorkCell_workCellCode_empty() throws Exception {
        setupDoReturnGnomesAppException("");
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> testRunJudgePersonLicenseSubstituteAddWorkCell(WORK_CELL_CODE));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    /**
     * モック化 GnomesAppException
     * @param param パラメータ
     */
    private void setupDoReturnGnomesAppException(String param) {
        Mockito.doReturn(new GnomesAppException(null, GnomesMessagesConstants.ME01_0050, "")).when(exceptionFactory)
            .createGnomesAppException(null, GnomesMessagesConstants.ME01_0050, String.valueOf(param));
    }

    /**
     * ユーザ作業権限判定テスト実行
     * @param param パラメータ
     * @return 期待値
     * @throws GnomesAppException
     */
    private boolean testRunJudgePersonLicense(String param) throws GnomesAppException {
        String userId = USER_ID;
        String privilegeId = PRIVILEGE_ID;
        String siteCode = SITE_CODE;
        String orderProcessCode = ORDER_PROCESS_CODE;
        String workProcessCode = WORK_PROCESS_CODE;

        if (param != null) {
            if (param.equals(userId)) {
                userId = "";
            } else if (param.equals(privilegeId)) {
                privilegeId = "";
            } else if (param.equals(siteCode)) {
                siteCode = "";
            } else if (param.equals(orderProcessCode)) {
                orderProcessCode = "";
            } else if (param.equals(workProcessCode)) {
                workProcessCode = "";
            }
        }
        return dao.judgePersonLicense(userId, privilegeId, siteCode, orderProcessCode, workProcessCode);
    }

    /**
     * ユーザ委託代替作業権限を元にした権限判定テスト実行
     * @param param パラメータ
     * @return 期待値
     * @throws GnomesAppException
     */
    private boolean testRunJudgePersonLicenseSubstitute(Object param) throws GnomesAppException {
        String userId = USER_ID;
        String privilegeId = PRIVILEGE_ID;
        Date date = new Date();
        String siteCode = SITE_CODE;
        String orderProcessCode = ORDER_PROCESS_CODE;
        String workProcessCode = WORK_PROCESS_CODE;

        if (param != null && param instanceof String) {
            String emptyParam = (String) param;
            if (emptyParam.equals(userId)) {
                userId = "";
            } else if (emptyParam.equals(privilegeId)) {
                privilegeId = "";
            } else if (emptyParam.equals(siteCode)) {
                siteCode = "";
            } else if (emptyParam.equals(orderProcessCode)) {
                orderProcessCode = "";
            } else if (emptyParam.equals(workProcessCode)) {
                workProcessCode = "";
            }
        } else if (param != null && param instanceof Date) {
            date = null;
        }
        return dao.judgePersonLicenseSubstitute(userId, privilegeId, date, siteCode, orderProcessCode, workProcessCode);
    }

    /**
     * ユーザ作業権限判定(作業場所含む)テスト実行
     * @param param パラメータ
     * @return 期待値
     * @throws GnomesAppException
     */
    private boolean testRunJudgePersonLicenseAddWorkCell(String param) throws GnomesAppException {
        String userId = USER_ID;
        String privilegeId = PRIVILEGE_ID;
        String siteCode = SITE_CODE;
        String orderProcessCode = ORDER_PROCESS_CODE;
        String workProcessCode = WORK_PROCESS_CODE;
        String workCellCode = WORK_CELL_CODE;

        if (param != null) {
            if (param.equals(userId)) {
                userId = "";
            } else if (param.equals(privilegeId)) {
                privilegeId = "";
            } else if (param.equals(siteCode)) {
                siteCode = "";
            } else if (param.equals(orderProcessCode)) {
                orderProcessCode = "";
            } else if (param.equals(workProcessCode)) {
                workProcessCode = "";
            } else if (param.equals(workCellCode)) {
                workCellCode = "";
            }
        }
        return dao.judgePersonLicenseAddWorkCell(userId, privilegeId, siteCode, orderProcessCode, workProcessCode, workCellCode);
    }

    /**
     * ユーザ委託代替作業権限を元にした権限判定(作業場所含む)テスト実行
     * @param param パラメータ
     * @return 期待値
     * @throws GnomesAppException
     */
    private boolean testRunJudgePersonLicenseSubstituteAddWorkCell(Object param) throws GnomesAppException {
        String userId = USER_ID;
        String privilegeId = PRIVILEGE_ID;
        Date date = new Date();
        String siteCode = SITE_CODE;
        String orderProcessCode = ORDER_PROCESS_CODE;
        String workProcessCode = WORK_PROCESS_CODE;
        String workCellCode = WORK_CELL_CODE;

        if (param != null && param instanceof String) {
            String emptyParam = (String) param;
            if (emptyParam.equals(userId)) {
                userId = "";
            } else if (emptyParam.equals(privilegeId)) {
                privilegeId = "";
            } else if (emptyParam.equals(siteCode)) {
                siteCode = "";
            } else if (emptyParam.equals(orderProcessCode)) {
                orderProcessCode = "";
            } else if (emptyParam.equals(workProcessCode)) {
                workProcessCode = "";
            } else if (param.equals(workCellCode)) {
                workCellCode = "";
            }
        } else if (param != null && param instanceof Date) {
            date = null;
        }
        return dao.judgePersonLicenseSubstituteAddWorkCell(userId, privilegeId, date, siteCode, orderProcessCode, workProcessCode, workCellCode);
    }

}
