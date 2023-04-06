package com.gnomes.rest.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

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
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.data.UserInfo;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.persistence.GnomesEntityManager;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.Holder;
import com.gnomes.system.data.CertInfo;
import com.gnomes.system.logic.BLSecurity;
import com.gnomes.uiservice.ContainerRequest;

class A01001S001Test{

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {

	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}


	@Test
	@DisplayName("ログイン：ユーザIDがない場合はエラーを返す")
	void testCertify1() {
		A01001S001 service = new A01001S001();
		CertInfo certInfo = new CertInfo();

		setFields(service);

		try {

			try (MockedStatic<MessagesHandler> mocked = Mockito.mockStatic(MessagesHandler.class)) {
				mocked.when(() -> MessagesHandler.setMessageNo(any(), any(), any())).thenAnswer((Answer<Void>) invocation -> null);
				mocked.when(() -> MessagesHandler.getString(any(), anyString())).thenReturn("errorMsg");
				mocked.when(() -> MessagesHandler.getString(any())).thenReturn("errorMsg");
				CertInfo result =service.certify(certInfo);
				assertEquals("errorMsg", result.getMessage());
				assertFalse(ConverterUtils.IntTobool(result.getIsSuccess()));
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail(e);
		}
	}

	@Test
	@DisplayName("ログイン：拠点コードがない場合はエラーを返す")
	void testCertify2() {
		A01001S001 service = new A01001S001();
		CertInfo certInfo = new CertInfo();
		certInfo.setUserId("testUserID");

		setFields(service);

		try {

			try (MockedStatic<MessagesHandler> mocked = Mockito.mockStatic(MessagesHandler.class)) {
				mocked.when(() -> MessagesHandler.setMessageNo(any(), any(), any())).thenAnswer((Answer<Void>) invocation -> null);
				mocked.when(() -> MessagesHandler.getString(any(), anyString())).thenReturn("errorMsg");
				mocked.when(() -> MessagesHandler.getString(any())).thenReturn("errorMsg");
				CertInfo result =service.certify(certInfo);
				assertFalse(ConverterUtils.IntTobool(result.getIsSuccess()));
				assertEquals("errorMsg", result.getMessage());
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail(e);
		}
	}

	@Test
	@DisplayName("ログイン：IDとパスワードを正しく入力した場合はログインする")
	void testCertify3() {
		A01001S001 service = new A01001S001();
		CertInfo certInfo = new CertInfo();
		certInfo.setUserId("testUserID");
		certInfo.setPassword("testPW");
		certInfo.setSiteCode("testSiteCode");
		certInfo.setComputerId("testComputerId");

		setFields(service);

		BLSecurity blSecurity = Mockito.mock(BLSecurity.class);
		try {
			doAnswer((Answer<Void>) v ->{
				Object[] args = v.getArguments();
				CertInfo arg0 = (CertInfo)args[0];
				UserInfo arg1 = (UserInfo)args[1];
				Holder<Integer> arg3 = (Holder<Integer>) args[3];
				if("testUserID".equals(arg0.getUserId()) && "testPW".equals(arg0.getPassword())) {
					arg1.setUserKey("testUserKey");
					arg1.setUserId(certInfo.getUserId());
					arg1.setPassword(certInfo.getPassword());
					arg1.setUserName("testUserName");
					arg1.setLocaleId(Locale.JAPANESE.toString());
					arg1.setLanguage(Locale.JAPANESE.getLanguage());
					arg1.setComputerId(certInfo.getComputerId());
					arg1.setComputerName("testComputerName");
					arg1.setSiteCode(certInfo.getSiteCode());
					arg1.setSiteName("testSiteName");
					arg1.setProcessCode("testProcessCode");
					arg1.setWorkProcessCode("testWorkProcessCode");
					arg1.setWorkCellCode("testWorkCellCode");
					arg1.setIsScreenLocked(false);
					arg1.setScreenLockTimeoutTime(1);
					arg1.setMaxListDisplayCount(1);
					arg1.setPopupDisplayCount(1);
					arg1.setWatchPeriodForPopup(1);
					arg3.value = 1;
				}else {
					arg3.value = 0;
				}
				return null;
			}).when(blSecurity).getCertificate(any(), any(), any(), any(), any(), any(), any());
		} catch (Exception e) {
			fail(e);
		}
		Whitebox.setInternalState(service, "blSecurity", blSecurity);

		LogicFactory logicFactory = Mockito.mock(LogicFactory.class);
		Mockito.doReturn(new GnomesSessionBean()).when(logicFactory).createSessionBean();
		Whitebox.setInternalState(service, "logicFactory", logicFactory);


		try {

			try (MockedStatic<MessagesHandler> mocked = Mockito.mockStatic(MessagesHandler.class)) {
				mocked.when(() -> MessagesHandler.setMessageNo(any(), any(), any())).thenAnswer((Answer<Void>) invocation -> null);
				mocked.when(() -> MessagesHandler.getString(any(), anyString())).thenReturn(null);
				mocked.when(() -> MessagesHandler.getString(any())).thenReturn(null);
				CertInfo result =service.certify(certInfo);
				assertTrue(ConverterUtils.IntTobool(result.getIsSuccess()));
				assertEquals("testUserID", result.getUserId());
				assertEquals("testPW", result.getPassword());

				assertEquals("testUserKey", service.gnomesSessionBean.getUserKey());
				assertEquals(certInfo.getUserId(), service.gnomesSessionBean.getUserId());
				assertEquals(certInfo.getPassword(), service.gnomesSessionBean.getPassword());
				assertEquals("testUserName", service.gnomesSessionBean.getUserName());
				assertEquals(Locale.JAPANESE.toString(), service.gnomesSessionBean.getLocaleId());
				assertEquals(Locale.JAPANESE.getLanguage(), service.gnomesSessionBean.getLanguage());
				assertEquals("testIP", service.gnomesSessionBean.getIpAddress());
				assertEquals("testComputerId", service.gnomesSessionBean.getComputerId());
				assertEquals("testComputerName", service.gnomesSessionBean.getComputerName());
				assertEquals("testProcessCode", service.gnomesSessionBean.getOrderProcessCode());
				assertEquals("testWorkProcessCode", service.gnomesSessionBean.getWorkProcessCode());
				assertFalse(service.gnomesSessionBean.getIsScreenLocked());
				assertEquals(1, service.gnomesSessionBean.getScreenLockTimeoutTime());
				assertEquals(1, service.gnomesSessionBean.getMaxListDisplayCount());
				assertEquals(1, service.gnomesSessionBean.getPopupDisplayCount());
				assertEquals(1, service.gnomesSessionBean.getWatchPeriodForPopup());

				Mockito.verify(service.request, Mockito.times(1)).login(certInfo.getUserId(), certInfo.getPassword());
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail(e);
		}
	}

	@Test
	@DisplayName("ログイン：入力パスワードが間違った場合はエラーを返す")
	void testCertify4() {
		A01001S001 service = new A01001S001();
		CertInfo certInfo = new CertInfo();
		certInfo.setUserId("testUserID");
		certInfo.setPassword("testWrongPW");
		certInfo.setSiteCode("testSiteCode");

		setFields(service);
		service.gnomesSessionBean.setIpAddress("testIP");

		BLSecurity blSecurity = Mockito.mock(BLSecurity.class);
		try {
			doAnswer((Answer<Void>) v ->{
				Object[] args = v.getArguments();
				CertInfo arg0 = (CertInfo)args[0];
				UserInfo arg1 = (UserInfo)args[1];
				Holder<Integer> arg3 = (Holder<Integer>) args[3];
				Holder<String> arg5 = (Holder<String>)args[5];
				if("testUserID".equals(arg0.getUserId()) && "testPW".equals(arg0.getPassword())) {
					arg3.value = 1;
				}else {
					arg3.value = 0;
					arg5.value = "errMsg";
				}
				return null;
			}).when(blSecurity).getCertificate(any(), any(), any(), any(), any(), any(), any());
		} catch (Exception e) {
			fail(e);
		}
		Whitebox.setInternalState(service, "blSecurity", blSecurity);

		LogicFactory logicFactory = Mockito.mock(LogicFactory.class);
		Mockito.doReturn(new GnomesSessionBean()).when(logicFactory).createSessionBean();
		Whitebox.setInternalState(service, "logicFactory", logicFactory);


		try {

			try (MockedStatic<MessagesHandler> mocked = Mockito.mockStatic(MessagesHandler.class)) {
				mocked.when(() -> MessagesHandler.setMessageNo(any(), any(), any())).thenAnswer((Answer<Void>) invocation -> null);
				mocked.when(() -> MessagesHandler.getString(any(), anyString())).thenReturn(null);
				mocked.when(() -> MessagesHandler.getString(any())).thenReturn(null);
				CertInfo result =service.certify(certInfo);
				//認証結果はFalse
				assertFalse(ConverterUtils.IntTobool(result.getIsSuccess()));
				assertEquals("errMsg", result.getMessage());
				//ログインはしない
				Mockito.verify(service.request, Mockito.times(0)).login(certInfo.getUserId(), certInfo.getPassword());
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail(e);
		}
	}

	@Test
	@DisplayName("ログアウト：既にログインしている場合はログアウトする")
	void testLogout1() {
		A01001S001 service = new A01001S001();
		CertInfo certInfo = new CertInfo();

		setFields(service);
		service.gnomesSessionBean.setIpAddress("testIP");

		A01001S001Proc a01001S001Proc = Mockito.mock(A01001S001Proc.class);
		try {
			Mockito.doNothing().when(a01001S001Proc).logoutProc(Mockito.any(), Mockito.any());
		} catch (Exception e) {
			fail(e);
		}
		Whitebox.setInternalState(service, "a01001S001Proc", a01001S001Proc);

		GnomesEntityManager em = Mockito.mock(GnomesEntityManager.class);
		Whitebox.setInternalState(service, "em", em);


		try {
			service.logout(certInfo);
			Mockito.verify(a01001S001Proc, Mockito.times(1)).logoutProc(certInfo, em.getEntityManager());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e);
		}
	}

	@Test
	@DisplayName("ログアウト：ログインしていない場合は何もしない")
	void testLogout2() {
		A01001S001 service = new A01001S001();
		CertInfo certInfo = new CertInfo();
		setFields(service);

		A01001S001Proc a01001S001Proc = mock(A01001S001Proc.class);
		try {
			Mockito.doNothing().when(a01001S001Proc).logoutProc(any(), any());
		} catch (Exception e) {
			fail(e);
		}
		Whitebox.setInternalState(service, "a01001S001Proc", a01001S001Proc);

		GnomesEntityManager em = Mockito.mock(GnomesEntityManager.class);
		Whitebox.setInternalState(service, "em", em);


		try {
			CertInfo result = service.logout(certInfo);
			assertTrue(ConverterUtils.IntTobool(result.getIsSuccess()));
			verify(a01001S001Proc, times(0)).logoutProc(certInfo, em.getEntityManager());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e);
		}
	}

	@Test
	@DisplayName("ロックセッション：正常に動作する場合はセッションがロックされる")
	void testLockSession1() {
		A01001S001 service = new A01001S001();
		CertInfo certInfo = new CertInfo();

		GnomesSessionBean gnomesSessionBean = new GnomesSessionBean();
		gnomesSessionBean.setIsScreenLocked(false);
		LogicFactory logicFactory = Mockito.mock(LogicFactory.class);
		Mockito.doReturn(gnomesSessionBean).when(logicFactory).getSessionBean();
		Whitebox.setInternalState(service, "logicFactory", logicFactory);

		CertInfo result = service.lockSession(certInfo);
		assertTrue(ConverterUtils.IntTobool(result.getIsSuccess()));
		assertTrue(gnomesSessionBean.getIsScreenLocked());
	}

	@Test
	@DisplayName("ロックセッション：セッションBeanが存在しない場合、結果はFalse")
	void testLockSession2() {
		A01001S001 service = new A01001S001();
		CertInfo certInfo = new CertInfo();
		setFields(service);

		GnomesExceptionFactory exceptionFactory = new GnomesExceptionFactory();
		Whitebox.setInternalState(service, "exceptionFactory", exceptionFactory);

		LogicFactory logicFactory = Mockito.mock(LogicFactory.class);
		Mockito.doThrow(new RuntimeException("errMsg")).when(logicFactory).getSessionBean();
		Whitebox.setInternalState(service, "logicFactory", logicFactory);

		try (MockedStatic<MessagesHandler> mocked = Mockito.mockStatic(MessagesHandler.class)) {
			CertInfo result = service.lockSession(certInfo);
			assertFalse(ConverterUtils.IntTobool(result.getIsSuccess()));
		}
	}

	private void setFields(A01001S001 service) {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.doReturn("testIP").when(request).getRemoteAddr();
		Mockito.doReturn("testHost").when(request).getRemoteHost();
		Whitebox.setInternalState(service, "request", request);

		LogHelper logHelper = Mockito.mock(LogHelper.class);
		Whitebox.setInternalState(service, "logHelper", logHelper);

		ContainerRequest req = new ContainerRequest();
		Whitebox.setInternalState(service, "req", req);

		GnomesEntityManager em = Mockito.mock(GnomesEntityManager.class);
		Whitebox.setInternalState(service, "em", em);

		GnomesSessionBean gnomesSessionBean = new GnomesSessionBean();
		Whitebox.setInternalState(service, "gnomesSessionBean", gnomesSessionBean);
	}


	@Test
	@DisplayName("パスワード変更：セッション切れの場合はエラーを返す")
	void testChangePassword1() {
		A01001S001 service = new A01001S001();
		setFields(service);

		try (MockedStatic<MessagesHandler> mocked = Mockito.mockStatic(MessagesHandler.class)) {
			//gnomesSessionBeanが存在しない場合
			service.gnomesSessionBean = null;
			CertInfo certInfo1 = new CertInfo();
			CertInfo result1 = service.changePassword(certInfo1);
			assertFalse(ConverterUtils.IntTobool(result1.getIsSuccess()));

			//ログインしていない場合
			service.gnomesSessionBean = new GnomesSessionBean();
			CertInfo certInfo2 = new CertInfo();
			CertInfo result2 = service.changePassword(certInfo2);
			assertFalse(ConverterUtils.IntTobool(result2.getIsSuccess()));

		} catch (Exception e) {
			e.printStackTrace();
			fail(e);
		}
	}

	@Test
	@DisplayName("パスワード変更：既にログインしている場合はパスワード変更処理を行う")
	void testChangePassword2() {
		A01001S001 service = new A01001S001();
		setFields(service);
		service.gnomesSessionBean.setIpAddress("testIP");

		try (MockedStatic<MessagesHandler> mocked = Mockito.mockStatic(MessagesHandler.class)) {
			CertInfo certInfo = new CertInfo();

			A01001S001Proc a01001S001Proc = mock(A01001S001Proc.class);
			Whitebox.setInternalState(service, "a01001S001Proc", a01001S001Proc);

			service.changePassword(certInfo);
			verify(a01001S001Proc, times(1)).changePasswordProc(certInfo, service.em.getEntityManager());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e);
		}
	}

	@Test
	@DisplayName("アカウントロック解除：セッションが有効な場合はロック解除処理を行う")
	void testCertifyForResume1() {
		A01001S001 service = new A01001S001();
		setFields(service);
		service.gnomesSessionBean.setIpAddress("testIP");

		try (MockedStatic<MessagesHandler> mocked = Mockito.mockStatic(MessagesHandler.class)) {
			CertInfo certInfo = new CertInfo();

			A01001S001Proc a01001S001Proc = mock(A01001S001Proc.class);
			Whitebox.setInternalState(service, "a01001S001Proc", a01001S001Proc);

			service.certifyForResume(certInfo);
			verify(a01001S001Proc, times(1)).certifyForResumeProc(certInfo, service.em.getEntityManager());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e);
		}
	}

	@Test
	@DisplayName("アカウントロック解除：セッション切れの場合はエラーを返す")
	void testCertifyForResume2() {
		A01001S001 service = new A01001S001();
		setFields(service);

		try (MockedStatic<MessagesHandler> mocked = Mockito.mockStatic(MessagesHandler.class)) {
			//gnomesSessionBeanが存在しない場合
			service.gnomesSessionBean = null;
			CertInfo certInfo1 = new CertInfo();
			CertInfo result1 = service.certifyForResume(certInfo1);
			assertFalse(ConverterUtils.IntTobool(result1.getIsSuccess()));

			//ログインしていない場合
			service.gnomesSessionBean = new GnomesSessionBean();
			CertInfo certInfo2 = new CertInfo();
			CertInfo result2 = service.certifyForResume(certInfo2);
			assertFalse(ConverterUtils.IntTobool(result2.getIsSuccess()));

		} catch (Exception e) {
			e.printStackTrace();
			fail(e);
		}
	}
}
