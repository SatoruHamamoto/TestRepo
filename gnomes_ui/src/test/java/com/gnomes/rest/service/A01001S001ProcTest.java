package com.gnomes.rest.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Locale;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import com.gnomes.TestUtil;
import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.data.GnomesSystemBean;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.Holder;
import com.gnomes.system.data.CertInfo;

class A01001S001ProcTest {

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
	@DisplayName("パスワード変更：ユーザIDがNULLまたは空文字の場合はエラーを返す")
	void testChangePasswordProc1() {
		EntityManager em = mock(EntityManager.class);
		A01001S001Proc a01001S001Proc = TestUtil.createBean(A01001S001Proc.class);
		a01001S001Proc.gnomesSessionBean = new GnomesSessionBean();


		try (MockedStatic<MessagesHandler> mocked = Mockito.mockStatic(MessagesHandler.class)) {
			CertInfo certInfo1 = new CertInfo();
			a01001S001Proc.changePasswordProc(certInfo1, em);
			assertFalse(certInfo1.getIsSuccessChange());
			verify(a01001S001Proc.blSecurity, times(0)).changePassword(certInfo1, em);

			CertInfo certInfo2 = new CertInfo();
			certInfo2.setUserId(StringUtils.EMPTY);
			a01001S001Proc.changePasswordProc(certInfo2, em);
			assertFalse(certInfo1.getIsSuccessChange());
			verify(a01001S001Proc.blSecurity, times(0)).changePassword(certInfo1, em);

		} catch (Exception e) {
			e.printStackTrace();
			fail(e);
		}
	}

	@Test
	@DisplayName("パスワード変更：ユーザIDがある場合はパスワード変更処理を行う")
	void testChangePasswordProc2() {
		EntityManager em = mock(EntityManager.class);
		A01001S001Proc a01001S001Proc = TestUtil.createBean(A01001S001Proc.class);
		a01001S001Proc.gnomesSessionBean = new GnomesSessionBean();

		try (MockedStatic<MessagesHandler> mocked = Mockito.mockStatic(MessagesHandler.class)) {
			CertInfo certInfo = new CertInfo();
			certInfo.setUserId("testUserID");
			certInfo.setPassword("testWrongPW");

			a01001S001Proc.changePasswordProc(certInfo, em);
			verify(a01001S001Proc.blSecurity, times(1)).changePassword(certInfo, em);

		} catch (Exception e) {
			e.printStackTrace();
			fail(e);
		}
	}

	@Test
	@DisplayName("アカウントロック解除：ユーザIDが入力されていない場合はエラーを返す")
	void testCertifyForResumeProc1() {
		EntityManager em = mock(EntityManager.class);
		A01001S001Proc a01001S001Proc = TestUtil.createBean(A01001S001Proc.class);
		a01001S001Proc.gnomesSessionBean = new GnomesSessionBean();
		a01001S001Proc.gnomesSessionBean.setIpAddress("testIP");
		a01001S001Proc.gnomesSessionBean.setComputerId("testComputerId");

		try (MockedStatic<MessagesHandler> mocked = createMessageHandlerMock()) {
			CertInfo certInfo = new CertInfo();
			certInfo.setUserId(null);

			a01001S001Proc.certifyForResumeProc(certInfo, em);
			assertFalse(ConverterUtils.IntTobool(certInfo.getIsSuccess()));
			assertEquals(GnomesMessagesConstants.ME01_0018, certInfo.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e);
		}
	}

	@Test
	@DisplayName("アカウントロック解除：GnomesSessionBean のユーザIDがnullの場合場合はエラーを返す")
	void testCertifyForResumeProc2() {
		EntityManager em = mock(EntityManager.class);
		A01001S001Proc a01001S001Proc = TestUtil.createBean(A01001S001Proc.class);
		a01001S001Proc.gnomesSessionBean = new GnomesSessionBean();
		a01001S001Proc.gnomesSessionBean.setUserId(null);
		a01001S001Proc.gnomesSessionBean.setIpAddress("testIP");
		a01001S001Proc.gnomesSessionBean.setComputerId("testComputerId");

		try (MockedStatic<MessagesHandler> mocked = createMessageHandlerMock()) {
			CertInfo certInfo = new CertInfo();
			certInfo.setUserId("testUserId");

			a01001S001Proc.certifyForResumeProc(certInfo, em);
			assertFalse(ConverterUtils.IntTobool(certInfo.getIsSuccess()));
			assertEquals(GnomesMessagesConstants.ME01_0040, certInfo.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e);
		}
	}

	@Test
	@DisplayName("アカウントロック解除：GnomesSessionBean のユーザIDが入力したユーザIDと一致しない場合はエラーを返す")
	void testCertifyForResumeProc3() {
		EntityManager em = mock(EntityManager.class);
		A01001S001Proc a01001S001Proc = TestUtil.createBean(A01001S001Proc.class);
		a01001S001Proc.gnomesSessionBean = new GnomesSessionBean();
		a01001S001Proc.gnomesSessionBean.setUserId("testUserId1");
		a01001S001Proc.gnomesSessionBean.setIpAddress("testIP");
		a01001S001Proc.gnomesSessionBean.setComputerId("testComputerId");

		try (MockedStatic<MessagesHandler> mocked = createMessageHandlerMock()) {
			CertInfo certInfo = new CertInfo();
			certInfo.setUserId("testUserId2");

			a01001S001Proc.certifyForResumeProc(certInfo, em);
			assertFalse(ConverterUtils.IntTobool(certInfo.getIsSuccess()));
			assertEquals(GnomesMessagesConstants.ME01_0014, certInfo.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e);
		}
	}

	@Test
	@DisplayName("アカウントロック解除：入力されたユーザIDに問題がない場合はログイン認証し、アカウントロックを解除する")
	void testCertifyForResumeProc4() {
		EntityManager em = mock(EntityManager.class);
		A01001S001Proc a01001S001Proc = TestUtil.createBean(A01001S001Proc.class);

		a01001S001Proc.gnomesSessionBean = new GnomesSessionBean();
		a01001S001Proc.gnomesSessionBean.setUserId("testUserId");
		a01001S001Proc.gnomesSessionBean.setIpAddress("testIP");
		a01001S001Proc.gnomesSessionBean.setComputerId("testComputerId");

		a01001S001Proc.gnomesSystemBean = new GnomesSystemBean();
		a01001S001Proc.gnomesSystemBean.setLoginModuleType(CommonConstants.JDBC);

		doReturn(a01001S001Proc.gnomesSessionBean).when(a01001S001Proc.logicFactory).getSessionBean();

		CertInfo certInfo = new CertInfo();
		certInfo.setUserId("testUserId");
		certInfo.setNewPassword("testPassword");

		try {
			doAnswer((Answer<Void>) v ->{
				Object[] args = v.getArguments();
				Holder<Integer> arg3 = (Holder<Integer>) args[3];
				arg3.value = 1;
				return null;
			}).when(a01001S001Proc.blSecurity).getCertificate(any(), any(), any(), any(), any(), any(), any());
		} catch (Exception e) {
			fail(e);
		}

		try (MockedStatic<MessagesHandler> mocked = createMessageHandlerMock()) {
			a01001S001Proc.certifyForResumeProc(certInfo, em);
			//ログイン認証処理が行う
			verify(a01001S001Proc.blSecurity, times(1)).getCertificate(any(), any(), any(), any(), any(), any(), any());
			//スクリーンロック解除
			assertFalse(a01001S001Proc.gnomesSessionBean.getIsScreenLocked());
			//処理成功
			assertTrue(ConverterUtils.IntTobool(certInfo.getIsSuccess()));

		} catch (Exception e) {
			e.printStackTrace();
			fail(e);
		}
	}

	private Answer<String> createMsgAnswer(int argIndex){
		return (Answer<String>) v ->{
			Object[] args = v.getArguments();
			String result = (String)args[argIndex];
			return result;
		};
	}

	private MockedStatic<MessagesHandler> createMessageHandlerMock() {
		MockedStatic<MessagesHandler> mocked = Mockito.mockStatic(MessagesHandler.class);
		//メッセージキーを返却するように設定
		mocked.when(() -> MessagesHandler.getString(anyString(), any(Locale.class))).then(createMsgAnswer(0));
		mocked.when(() -> MessagesHandler.getString(anyString(), any(Locale.class), any())).then(createMsgAnswer(0));
		return mocked;
	}

}
