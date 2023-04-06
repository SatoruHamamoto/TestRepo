package com.gnomes.common.logic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.batch.operations.JobOperator;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.JobExecution;

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
import com.gnomes.common.batch.batchlet.EJBWrapperInterceptor;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.uiservice.ContainerRequest;

class BaseJbatchLogicTest {

    private static final String JOB_XML_FILE = "test-job.xml";
    private static final String RECV_FILE_NAME = "test-recv.csv";

    private BaseJbatchLogic logic;

    private MockedStatic<BatchRuntime> runtime;

    private JobOperator jobOperator;

    private JobExecution jobExecution;

    private Map<String, String> mapProperties;

    private ContainerRequest req;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() throws Exception {
        logic = TestUtil.createBean(BaseJbatchLogic.class);
        runtime = Mockito.mockStatic(BatchRuntime.class);
        jobOperator = mock(JobOperator.class);
        jobExecution = mock(JobExecution.class);

        mapProperties = new HashMap<String,String>() {
            {
                put("dataType", GnomesResourcesConstants.FileType_Excel);
                put("recvFileName", RECV_FILE_NAME);
            }
        };
    }

    @AfterEach
    void tearDown() throws Exception {
        runtime.close();
    }

    @Test
    @DisplayName("バッチ実行：正常終了（重複チェック無し）")
    void test_runBatch_success_dupCheck_false() throws GnomesAppException {
        setupMocks(false);
        try {
            assertNotNull(logic.runBatch(JOB_XML_FILE, mapProperties, false));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @DisplayName("バッチ実行：正常終了（重複チェック有り）")
    void test_runBatch_success_dupCheck_true() throws GnomesAppException {
        setupMocks(false);
        try {
            assertNotNull(logic.runBatch(JOB_XML_FILE, mapProperties, true));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @DisplayName("バッチ実行：実行中JOBが既にに存在する場合は何もせずリターンする")
    void test_runBatch_checkExistRunningJob_true() throws GnomesAppException {
        setupMocks(false);
        setupMockCheckExistRunningJob();
        assertNull(logic.runBatch(JOB_XML_FILE, null, true));
    }

    @Test
    @DisplayName("バッチ実行：バッチ起動時のException確認")
    void test_runBatch_GnomesAppException() throws GnomesAppException {
        setupMocks(true);
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> logic.runBatch(JOB_XML_FILE, null, false));
        assertEquals(e.getMessageNo(), GnomesMessagesConstants.ME01_0124);
        assertEquals(e.getMessageParams()[0], JOB_XML_FILE);
    }

    @Test
    @DisplayName("バッチパラメータ共通設定：パラメータ確認")
    void test_setCommonBatchJobParameter() throws GnomesAppException {
        initContainerRequest();
        Properties p = new Properties();

        ReflectionTestUtils.invokeMethod(logic, "setCommonBatchJobParameter", p);

        assertEquals(p.getProperty(EJBWrapperInterceptor.EJB_REQUEST_PARAM_KEY_COMPUTERNAME), req.getComputerName());
        assertEquals(p.getProperty(EJBWrapperInterceptor.EJB_REQUEST_PARAM_KEY_IPADDRESS), req.getIpAddress());
        assertEquals(p.getProperty(EJBWrapperInterceptor.EJB_REQUEST_PARAM_KEY_LANGUAGE), req.getLanguage());
        assertEquals(p.getProperty(EJBWrapperInterceptor.EJB_REQUEST_PARAM_KEY_USERLOCALE_LANGUAGE), req.getUserLocale().getLanguage());
        assertEquals(p.getProperty(EJBWrapperInterceptor.EJB_REQUEST_PARAM_KEY_USERLOCALE_COUNTRY), req.getUserLocale().getCountry());
        assertEquals(p.getProperty(EJBWrapperInterceptor.EJB_REQUEST_PARAM_KEY_SITECODE), req.getSiteCode());
        assertEquals(p.getProperty(EJBWrapperInterceptor.EJB_REQUEST_PARAM_KEY_SITENAME), req.getSiteName());
        assertEquals(p.getProperty(EJBWrapperInterceptor.EJB_REQUEST_PARAM_KEY_USERID), req.getUserId());
        assertEquals(p.getProperty(EJBWrapperInterceptor.EJB_REQUEST_PARAM_KEY_USERNAME), req.getUserName());
        assertEquals(p.getProperty(EJBWrapperInterceptor.EJB_REQUEST_PARAM_KEY_USERKEY), req.getUserKey());
    }

    private void setupMocks(boolean isError) {
        runtime.when(() -> BatchRuntime.getJobOperator()).thenReturn(jobOperator);
        initContainerRequest();
        if (!isError) {
            doReturn(jobExecution).when(jobOperator).getJobExecution(anyLong());
        } else {
            doThrow(new NoSuchJobExecutionException()).when(jobOperator).getJobExecution(anyLong());
        }
    }

    private void setupMockCheckExistRunningJob() {
        List<Long> listJobExecutionIds = Arrays.<Long> asList(99L);
        doReturn(listJobExecutionIds).when(jobOperator).getRunningExecutions(anyString());
        doReturn(JOB_XML_FILE).when(jobExecution).getJobName();
    }

    private void initContainerRequest() {
        req  = new ContainerRequest();
        req.setComputerName("test_computer_name");
        req.setIpAddress("0.0.0.0");
        req.setLanguage(Locale.US.toString());
        req.setUserLocale(Locale.US);
        req.setSiteCode("test_site_code");
        req.setSiteName("test_site_name");
        req.setUserId("test_user_id");
        req.setUserName("test_user_name");
        req.setUserKey("test_user_key");
        Whitebox.setInternalState(logic, "req", req);
    }

}
