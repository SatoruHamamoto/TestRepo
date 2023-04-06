package com.gnomes.uiservice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
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

import com.gnomes.TestUtil;
import com.gnomes.common.data.GnomesEjbBean;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.data.MessageInfo;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.system.entity.MstrMessageDefine;

class ContainerRequestTest {

    private static final String SCREEN_ID = "test_screenId";
    private static final String SITE_CODE = "test_siteCode";
    private static final String COMPUTER_NAME = "test_computerName";
    private static final String USER_ID = "test_userId";
    private static final String USER_NAME = "test_userName";
    private static final String COMMAND_ID = "test_commandId";

    private ContainerRequest request;

    private MockedStatic<ResourcesHandler> resHandlerMock;

    private MockedStatic<MessagesHandler> msgHandlerMock;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() throws Exception {
        request = spy(TestUtil.createBean(ContainerRequest.class));
        request.setScreenId(SCREEN_ID);
        request.setSiteCode(SITE_CODE);
        request.setComputerName(COMPUTER_NAME);
        request.setUserId(USER_ID);
        request.setUserName(USER_NAME);
        request.setCommandId(COMMAND_ID);

        resHandlerMock = Mockito.mockStatic(ResourcesHandler.class);
        msgHandlerMock = Mockito.mockStatic(MessagesHandler.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        resHandlerMock.close();
        msgHandlerMock.close();
    }

    @Test
    @DisplayName("メッセージ情報追加：メッセージ情報リストに追加されている確認")
    void test() throws Exception {
        setupDoReturn(initMstrMessageDefine());
        List<String> msgParamList = new ArrayList<>();
        request.addMessageInfo(GnomesMessagesConstants.ME01_0003, msgParamList);

        assertEquals(request.getMessageInfoList().size(), 1);
        assertEquals(request.getMessageInfoList().get(0).getMessageNo(), GnomesMessagesConstants.ME01_0003);
        assertEquals(request.getMessageInfoList().get(0).getScreenId(), SCREEN_ID);
        assertEquals(request.getMessageInfoList().get(0).getSiteCode(), SITE_CODE);
        assertEquals(request.getMessageInfoList().get(0).getOccurHost(), COMPUTER_NAME);
        assertEquals(request.getMessageInfoList().get(0).getOccrUserId(), USER_ID);
        assertEquals(request.getMessageInfoList().get(0).getOccrUserName(), USER_NAME);
        assertEquals(request.getMessageInfoList().get(0).getCommandId(), COMMAND_ID);
    }

    @Test
    @DisplayName("メッセージ情報追加： Exceptionがスローされている場合同期でメッセージ登録される")
    void test_addMessageInfo_isThrowException_true() throws Exception {
        // Exceptionスロー
        request.setIsThrowException(true);

        setupDoReturn(initMstrMessageDefine());
        List<String> msgParamList = new ArrayList<>();

        if (!request.getIsThrowException()) {
            fail();
        }
        request.addMessageInfo(GnomesMessagesConstants.ME01_0003, msgParamList);

        assertTrue(!request.getIsThrowException());
        verify(request, Mockito.times(1)).insertMessage(any(MessageInfo.class));
        verify(request.asynchronousProcess, Mockito.times(0)).insertMessage(any(MessageInfo.class),
            any(ContainerRequest.class));
    }

    @Test
    @DisplayName("メッセージ情報追加： Exceptionがスローされていない場合非同期でメッセージ登録される")
    void test_addMessageInfo_isThrowException_false() throws Exception {
        setupDoReturn(initMstrMessageDefine());
        List<String> msgParamList = new ArrayList<>();

        if (request.getIsThrowException()) {
            fail();
        }
        request.addMessageInfo(GnomesMessagesConstants.ME01_0003, msgParamList);

        assertTrue(!request.getIsThrowException());
        verify(request, Mockito.times(0)).insertMessage(any(MessageInfo.class));
        verify(request.asynchronousProcess, Mockito.times(1)).insertMessage(any(MessageInfo.class),
            any(ContainerRequest.class));
    }

    @Test
    @DisplayName("メッセージ情報追加： トレースクラス名リストにクラス名が格納されている場合メッセージ情報にクラス名#メソッド名が格納される")
    void test_addMessageInfo_traceClazzNameList_empty_false() throws Exception {
        String className = this.getClass().getName();
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        request.getTraceClazzNameList().add(className);
        request.getTraceMethodNameList().add(methodName);

        setupDoReturn(initMstrMessageDefine());
        List<String> msgParamList = new ArrayList<>();
        request.addMessageInfo(GnomesMessagesConstants.ME01_0003, msgParamList);

        assertEquals(request.getMessageInfoList().size(), 1);
        assertEquals(request.getMessageInfoList().get(0).getSourceInfo(), String.format("%s#%s", className, methodName));
    }

    @Test
    @DisplayName("メッセージ情報追加：メッセージ定義を取得できなかった場合処理終了")
    void test_addMessageInfo_mstrMessageDefine_exists_false() throws Exception {
        setupDoReturn(null);

        List<String> msgParamList = new ArrayList<>();
        request.addMessageInfo(GnomesMessagesConstants.ME01_0003, msgParamList);

        // メッセージ情報リストが空であること
        assertEquals(request.getMessageInfoList().size(), 0);
    }

    @Test
    @DisplayName("メッセージ情報追加：メッセージ定義取得時にエラーが発生した場合何もせずリターン")
    void test_addMessageInfo_get_mstrMessageDefine_exception() throws Exception {
        doThrow(new IllegalArgumentException()).when(request.mstrMessageDefineDao)
            .getMstrMessageDefine(GnomesMessagesConstants.ME01_0003);

        List<String> msgParamList = new ArrayList<>();
        request.addMessageInfo(GnomesMessagesConstants.ME01_0003, msgParamList);

        // メッセージ情報リストが空であること
        assertEquals(request.getMessageInfoList().size(), 0);
    }

    @Test
    @DisplayName("ユーザ情報を保持：セッションBeanがNullの場合何もしない")
    void test_setSessionInfo_sessionBean_null() throws Exception {
        ContainerRequest req = new ContainerRequest();
        GnomesSessionBean gnomesSessionBean = null;
        Whitebox.setInternalState(req, "gnomesSessionBean", gnomesSessionBean);

        req.setSessionInfo();

        // ユーザー情報がセットされていないこと
        assertNull(req.getAreaId());
        assertNull(req.getAreaName());
        assertNull(req.getComputerName());
        assertNull(req.getIpAddress());
        assertEquals(req.getLanguage(), Locale.getDefault().toString());
        assertEquals(req.getUserLocale(), Locale.getDefault());
        assertNull(req.getSiteCode());
        assertNull(req.getSiteName());
        assertNull(req.getUserId());
        assertNull(req.getUserName());
        assertNull(req.getUserKey());
    }

    @Test
    @DisplayName("ユーザ情報を保持：正常終了")
    void test_setSessionInfo_setting_locale() throws Exception {
        ContainerRequest req = new ContainerRequest();
        GnomesEjbBean ejbBean = new GnomesEjbBean();
        GnomesSessionBean gnomesSessionBean = new GnomesSessionBean();

        gnomesSessionBean.setAreaId("test_sessionBean_area_id");
        gnomesSessionBean.setAreaName("test_sessionBean_area_name");
        gnomesSessionBean.setComputerName("test_sessionBean_computer_name");
        gnomesSessionBean.setIpAddress("1.2.3.4");
        gnomesSessionBean.setLanguage(Locale.US.toString());
        gnomesSessionBean.setSiteCode("test_sessionBean_site_code");
        gnomesSessionBean.setSiteName("test_sessionBean_site_name");
        gnomesSessionBean.setUserId("test_sessionBean_user_id");
        gnomesSessionBean.setUserName("test_sessionBean_user_id");
        gnomesSessionBean.setUserKey("test_sessionBean_user_name");
        Whitebox.setInternalState(req, "gnomesSessionBean", gnomesSessionBean);
        Whitebox.setInternalState(gnomesSessionBean, "ejbBean", ejbBean);

        req.setSessionInfo();

        // ユーザー情報がセットされていること
        assertEquals(req.getAreaId(), gnomesSessionBean.getAreaId());
        assertEquals(req.getAreaName(), gnomesSessionBean.getAreaName());
        assertEquals(req.getComputerName(), gnomesSessionBean.getComputerName());
        assertEquals(req.getIpAddress(), gnomesSessionBean.getIpAddress());
        assertEquals(req.getLanguage(), gnomesSessionBean.getLanguage());
        assertEquals(req.getUserLocale(), gnomesSessionBean.getUserLocale());
        assertEquals(req.getSiteCode(), gnomesSessionBean.getSiteCode());
        assertEquals(req.getSiteName(), gnomesSessionBean.getSiteName());
        assertEquals(req.getUserId(), gnomesSessionBean.getUserId());
        assertEquals(req.getUserName(), gnomesSessionBean.getUserName());
        assertEquals(req.getUserKey(), gnomesSessionBean.getUserKey());
    }

    private void setupDoReturn(MstrMessageDefine mstrMessageDefine) throws GnomesAppException {
        doReturn(true).when(request.ejbBean).isEjbBatch();
        doReturn(mstrMessageDefine).when(request.mstrMessageDefineDao)
            .getMstrMessageDefine(GnomesMessagesConstants.ME01_0003);
        doNothing().when(request).insertMessage(any(MessageInfo.class));

        //メッセージが正しく設定されたかを確認するため、メッセージキーを返却するように設定
        resHandlerMock.when(() -> ResourcesHandler.getString(anyString())).then(createMsgAnswer(0));
        msgHandlerMock.when(() -> MessagesHandler.getString(anyString(), any(Locale.class))).then(createMsgAnswer(0));
    }

    private MstrMessageDefine initMstrMessageDefine() {
        return new MstrMessageDefine("test_message_define_key", GnomesMessagesConstants.ME01_0003, "test_resource_id", 1, 1, 1, 1, 1, 1,
            1, "test_send_mail_group_id", 1, 1, "test_talend_job_name", "test_talend_context_param",
            "test_message_title_resource_id", "test_message_text_resource_id", "test_first_regist_event_id",
            "test_first_regist_user_number", "test_first_regist_user_name", new Date(), "test_last_regist_event_id",
            "test_last_regist_user_number", "test_last_regist_user_name", new Date(), 1);
    }

    private Answer<String> createMsgAnswer(int argIndex){
        return (Answer<String>) v ->{
            Object[] args = v.getArguments();
            String result = (String)args[argIndex];
            return result;
        };
    }

}
