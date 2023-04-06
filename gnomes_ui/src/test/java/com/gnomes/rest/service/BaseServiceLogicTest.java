package com.gnomes.rest.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

import com.gnomes.common.command.LogicFactory;
import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.data.GnomesEjbBean;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.view.SystemFormBean;
import com.gnomes.uiservice.ContainerRequest;
import com.gnomes.uiservice.ContainerResponse;

class BaseServiceLogicTest {
    private final static String COMMAND_RESPONSE = "TEST_COMMAND_RESPONSE";

    private BaseServiceLogic logic;

    private GnomesSessionBean gnomesSessionBean;

    private ContainerRequest req;

    private LogicFactory logicFactory;

    private SystemFormBean systemFormBean;

    private LogHelper logHelper;

    private ContainerResponse responseContext;

    private MockedStatic<MessagesHandler> msgHandlerMock;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() throws Exception {
        logic = mock(BaseServiceLogic.class, Mockito.CALLS_REAL_METHODS);
        logicFactory = mock(LogicFactory.class);
        logHelper = mock(LogHelper.class);
        responseContext = new ContainerResponse();
        req = spy(new ContainerRequest());
        systemFormBean = new SystemFormBean();
        msgHandlerMock = Mockito.mockStatic(MessagesHandler.class);

        doReturn("test_windowId").when(req).getServiceRequestParam(anyString());
        systemFormBean.setCertUserId("test_cert_user_id");

        initGnomesSessionBean();
        Whitebox.setInternalState(logic, "gnomesSessionBean", gnomesSessionBean);
        Whitebox.setInternalState(logic, "req", req);
        Whitebox.setInternalState(logic, "systemFormBean", systemFormBean);
        Whitebox.setInternalState(logic, "exceptionFactory", new GnomesExceptionFactory());
        Whitebox.setInternalState(logic, "logHelper", logHelper);
        Whitebox.setInternalState(logic, "responseContext", responseContext);

        msgHandlerMock
            .when(() -> MessagesHandler.getExceptionMessage(any(ContainerRequest.class), any(GnomesException.class)))
            .then(createMsgAnswer(1));
    }

    @AfterEach
    void tearDown() throws Exception {
        msgHandlerMock.close();
    }

    @Test
    @DisplayName("ロジック実行：SF専用ロジック実行かつ正常終了")
    void test_doLogic_success_sf() throws Exception {
        setupMockLogicFactory(true, null);

        RestServiceResult result = logic.doLogic(new GnomesWebServiceDataInput());

        assertNotNull(result);
        assertEquals(result.getCertUserId(), systemFormBean.getCertUserId());
        assertEquals(result.getCommandResponse(), COMMAND_RESPONSE);
        assertEquals(result.getIsSessionError(), false);
        assertEquals(result.getIsSuccess(), true);
        verify(logicFactory, times(1)).executeServiceLogicForSF();

        // ユーザ情報の確認はContainerRequestのテストで実施する
//        assertEquals(req.getAreaId(), gnomesSessionBean.getAreaId());
//        assertEquals(req.getAreaName(), gnomesSessionBean.getAreaName());
//        assertEquals(req.getComputerName(), gnomesSessionBean.getComputerName());
//        assertEquals(req.getIpAddress(), gnomesSessionBean.getIpAddress());
//        assertEquals(req.getLanguage(), gnomesSessionBean.getLanguage());
//        assertEquals(req.getUserLocale(), gnomesSessionBean.getUserLocale());
//        assertEquals(req.getSiteCode(), gnomesSessionBean.getSiteCode());
//        assertEquals(req.getSiteName(), gnomesSessionBean.getSiteName());
//        assertEquals(req.getUserId(), gnomesSessionBean.getUserId());
//        assertEquals(req.getUserName(), gnomesSessionBean.getUserName());
//        assertEquals(req.getUserKey(), gnomesSessionBean.getUserKey());
    }

    @Test
    @DisplayName("ロジック実行：通常ロジック実行かつ正常終了")
    void test_doLogic_success() throws Exception {
        setupMockLogicFactory(false, null);

        RestServiceResult result = logic.doLogic(new GnomesWebServiceDataInput());

        assertNotNull(result);
        assertEquals(result.getCertUserId(), systemFormBean.getCertUserId());
        assertEquals(result.getCommandResponse(), COMMAND_RESPONSE);
        assertEquals(result.getIsSessionError(), false);
        assertEquals(result.getIsSuccess(), true);
        verify(logicFactory, times(1)).executeServiceLogic();
    }

    @Test
    @DisplayName("ロジック実行：windowIdが確認できなかった場合は処理を終了する")
    void test_doLogic_exists_false_windowId() throws Exception {
        setupMockLogicFactory(true, null);

        List<String> windowIdList = new ArrayList<String>() {
            {
                add("test_doLogic_exists_false_windowId");
            }
        };
        gnomesSessionBean.setWindowIdList(windowIdList);

        RestServiceResult result = logic.doLogic(new GnomesWebServiceDataInput());

        assertNotNull(result);
        assertEquals(result.getIsSessionError(), true);
        assertEquals(result.getIsSuccess(), false);
    }

    @Test
    @DisplayName("ロジック実行：ロジック実行時にGnomesException")
    void test_doLogic_GnomesException() throws Exception {
        String param = "TEST";
        GnomesException ex = new GnomesException(null, GnomesMessagesConstants.ME01_0050, param);
        setupMockLogicFactory(true, ex);

        RestServiceResult result = logic.doLogic(new GnomesWebServiceDataInput());

        assertNotNull(result);
        assertEquals(result.getIsSessionError(), false);
        assertEquals(result.getIsSuccess(), false);

        GnomesAppException appEx = result.getGnomesAppException();
        assertEquals(appEx.getMessageNo(), GnomesMessagesConstants.ME01_0050);
        assertEquals(appEx.getMessageParams()[0], param);
    }

    @Test
    @DisplayName("ロジック実行：ロジック実行時にGnomesAppException")
    void test_doLogic_GnomesAppException() throws Exception {
        setupMockLogicFactory(true, new GnomesAppException(null, GnomesMessagesConstants.ME01_0110));

        RestServiceResult result = logic.doLogic(new GnomesWebServiceDataInput());

        assertNotNull(result);
        assertEquals(result.getIsSessionError(), false);
        assertEquals(result.getIsSuccess(), false);

        GnomesAppException appEx = result.getGnomesAppException();
        assertEquals(appEx.getMessageNo(), GnomesMessagesConstants.ME01_0110);
        // パラメータがないメッセージの場合はNull
        assertNull(appEx.getMessageParams());
    }

    @Test
    @DisplayName("ロジック実行：ロジック実行時にその他Exception")
    void test_doLogic_Throwable() throws Exception {
        setupMockLogicFactory(true, new IOException());

        RestServiceResult result = logic.doLogic(new GnomesWebServiceDataInput());

        assertNotNull(result);
        assertEquals(result.getIsSessionError(), false);
        assertEquals(result.getIsSuccess(), false);

        GnomesAppException appEx = result.getGnomesAppException();
        assertEquals(appEx.getMessageNo(), GnomesMessagesConstants.ME01_0001);

        // レスポンス確認
        assertFalse(responseContext.isRedirect());
        assertEquals(responseContext.getUri(), CommonConstants.PATH_ERR_PAGE);
    }

    private void initGnomesSessionBean() {
        gnomesSessionBean = new GnomesSessionBean();
        gnomesSessionBean.setAreaId("");
        gnomesSessionBean.setAreaName("");
        gnomesSessionBean.setComputerName("");
        gnomesSessionBean.setIpAddress(CommonConstants.IPADDRESS_LOCALHOST);
        gnomesSessionBean.setLanguage(Locale.US.toString());
        gnomesSessionBean.setSiteCode("");
        gnomesSessionBean.setSiteName("");
        gnomesSessionBean.setUserId("SYS_BATCH");
        gnomesSessionBean.setUserName(CommonConstants.USERNAME_BATCH);
        gnomesSessionBean.setUserKey("");

        List<String> windowIdList = new ArrayList<String>() {
            {
                add("test_windowId");
            }
        };
        gnomesSessionBean.setWindowIdList(windowIdList);
    }

    private void setupMockLogicFactory(boolean isEjbBatch, Exception ex) throws Exception {
        GnomesEjbBean ejbBean = new GnomesEjbBean();
        try { ejbBean.setEjbBatch(isEjbBatch); } catch (NullPointerException e) {}
        Whitebox.setInternalState(logic, "ejbBean", ejbBean);

        if (ex == null) {
            if (isEjbBatch) {
                doReturn(COMMAND_RESPONSE).when(logicFactory).executeServiceLogicForSF();
            } else {
                doReturn(COMMAND_RESPONSE).when(logicFactory).executeServiceLogic();
            }
        } else {
            doThrow(ex).when(logicFactory).executeServiceLogicForSF();
        }
        Whitebox.setInternalState(logic, "logicFactory", logicFactory);
    }

    private Answer<String> createMsgAnswer(int argIndex){
        return (Answer<String>) v ->{
            Object[] args = v.getArguments();
            GnomesException ex = (GnomesException)args[argIndex];
            return ex.getMessageNo();
        };
    }

}
