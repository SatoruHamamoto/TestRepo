package com.gnomes.system.logic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

import com.gnomes.TestUtil;
import com.gnomes.common.constants.CommonEnums.HistNgFlag;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.util.GnomesPasswordEncoder;
import com.gnomes.system.data.CertInfo;
import com.gnomes.system.entity.HistChangePassword;
import com.gnomes.system.entity.HistChangePasswordDetail;
import com.gnomes.system.entity.InfoUser;
import com.gnomes.system.entity.MstrInvalidPasswd;
import com.gnomes.system.entity.MstrPerson;
import com.gnomes.system.entity.MstrPersonSecPolicy;

/**
 * BLSecurityパスワード変更テストクラス
 * @author YJP/kei
 */
class BLSecurityChangePasswordTest {

	private static final String TEST_HISTHORY_PW = "testHistoryPW";

	private BLSecurity blSecurity;

	private MstrPersonSecPolicy mstrPersonSecPolicy;

	private InfoUser infoUser;

	private EntityManager em;

	private CertInfo certInfo;

	private MockedStatic<MessagesHandler> msgHandlerMock;

	@BeforeEach
	void setUp() throws Exception {
		certInfo = createCertInfo();
		blSecurity = TestUtil.createBean(BLSecurity.class);
		em = mock(EntityManager.class);

		//セキュリティ設定情報を作成する
		List<MstrPersonSecPolicy> mstrUsrsecPolicies = new ArrayList<>();
		mstrPersonSecPolicy = new MstrPersonSecPolicy();
		mstrUsrsecPolicies.add(mstrPersonSecPolicy);
		doReturn(mstrUsrsecPolicies).when(blSecurity.mstrPersonSecPolicyDao).getMstrPersonSecPolicyQuery();
		mstrPersonSecPolicy.setMinimum_password_size(1);
		mstrPersonSecPolicy.setPassword_limit_day(1);
		mstrPersonSecPolicy.setRepeat_same_password(1);
		mstrPersonSecPolicy.setIs_specific_character_prohibit(0);
		mstrPersonSecPolicy.setIs_alphabet_and_numeral_only(0);
		mstrPersonSecPolicy.setIs_one_character_password_prohibit(0);
		mstrPersonSecPolicy.setIs_user_id_password_prohibit(0);


		//mstrPersonデータを作成する
		List<MstrPerson> mstrPersons = new ArrayList<>();
		MstrPerson mstrPerson = new MstrPerson();
		mstrPersons.add(mstrPerson);
		doReturn(mstrPersons).when(blSecurity.mstrPersonDao).getMstrPersonQuery(certInfo.getUserId(), em);

		//ユーザーデータを初期作成する
		infoUser = new InfoUser();
		doReturn(infoUser).when(blSecurity.infoUserDao).getInfoUser(certInfo.getUserId(), em);
		infoUser.setPassword(GnomesPasswordEncoder.encode(certInfo.getPassword()));
		infoUser.setUser_id_valid_from(getDateFromToday(-1));
		infoUser.setUser_id_valid_to(getDateFromToday(1));
		infoUser.setPassword_update_date(new Date());
		infoUser.setIs_lock_out(0);
		doAnswer((Answer<Void>) v ->{
			Object[] args = v.getArguments();
			InfoUser arg0 = (InfoUser)args[0];
			infoUser.setPassword(arg0.getPassword());
			return null;
		}).when(blSecurity.infoUserDao).update(infoUser, em);

		//パスワード履歴データを初期作成する
		List<HistChangePassword> histChangePasswords = new ArrayList<>();
		HistChangePassword histChangePassword = new HistChangePassword();
		histChangePasswords.add(histChangePassword);
		doReturn(histChangePasswords).when(blSecurity.histChangePasswordDao).getHistChangePassword(certInfo.getUserId(), HistNgFlag.OFF.getValue(), em);
		HistChangePasswordDetail dataHistChangePasswordDetail = new HistChangePasswordDetail();
		histChangePassword.setHistChangePasswordDetails(new HashSet<HistChangePasswordDetail>());
		histChangePassword.getHistChangePasswordDetails().add(dataHistChangePasswordDetail);
		dataHistChangePasswordDetail.setPassword(GnomesPasswordEncoder.encode(TEST_HISTHORY_PW));


		//特定文字の使用禁止制限データを初期作成する
		List<MstrInvalidPasswd> mstrInvalidPasswds = new ArrayList<>();
		MstrInvalidPasswd mstrInvalidPasswd = new MstrInvalidPasswd();
		mstrInvalidPasswds.add(mstrInvalidPasswd);
		doReturn(mstrInvalidPasswds).when(blSecurity.mstrInvalidPasswdDao).getMstrInvalidPasswd();
		mstrInvalidPasswd.setDisapprove_charactor("-");

		//セッションデータ設定
		GnomesSessionBean gnomesSessionBean = new GnomesSessionBean();
		gnomesSessionBean.setUserId(certInfo.getUserId());
		Whitebox.setInternalState(blSecurity, "gnomesSessionBean", gnomesSessionBean);

		msgHandlerMock = Mockito.mockStatic(MessagesHandler.class);
		//メッセージが正しく設定されたかを確認するため、メッセージキーを返却するように設定
		msgHandlerMock.when(() -> MessagesHandler.getString(anyString(), any(Locale.class))).then(createMsgAnswer(0));
		msgHandlerMock.when(() -> MessagesHandler.getString(anyString(), any(Locale.class), any())).then(createMsgAnswer(0));

	}

	@AfterEach
	void tearDown() throws Exception {
		msgHandlerMock.close();
	}

	@Test
	@Disabled
	@DisplayName("パスワード変更：入力パスワードに問題がない場合は変更する")
	void testChangePassword1() throws Exception {
		blSecurity.changePassword(certInfo, em);
		assertTrue(certInfo.getIsSuccessChange());
		assertEquals(GnomesMessagesConstants.MG01_0020, certInfo.getMessage());
		assertTrue(GnomesPasswordEncoder.matches(certInfo.getNewPassword(), infoUser.getPassword()));
	}

	@Test
	@DisplayName("パスワード変更：「新パスワード」と「パスワード確認」の値が一致しない場合はエラーを返す")
	void testChangePassword2() throws Exception {
		certInfo.setNewPassword("testNewPassword1");
		certInfo.setNewPasswordConfirm("testNewPassword2");
		blSecurity.changePassword(certInfo, em);
		assertFalse(certInfo.getIsSuccessChange());
		assertEquals(GnomesMessagesConstants.ME01_0013, certInfo.getMessage());
		assertTrue(GnomesPasswordEncoder.matches(certInfo.getPassword(), infoUser.getPassword()));
	}

	@Test
	@DisplayName("パスワード変更：桁数が足りない場合はエラーを返す")
	void testChangePassword3() throws Exception {
		mstrPersonSecPolicy.setMinimum_password_size(8);
		certInfo.setNewPassword("test123");
		certInfo.setNewPasswordConfirm("test123");
		blSecurity.changePassword(certInfo, em);
		assertFalse(certInfo.getIsSuccessChange());
		assertEquals(GnomesMessagesConstants.ME01_0019, certInfo.getMessage());
		assertTrue(GnomesPasswordEncoder.matches(certInfo.getPassword(), infoUser.getPassword()));
	}

	@Test
	@Disabled
	@DisplayName("パスワード変更：有効期限が切れた場合はエラーを返す")
	void testChangePassword4() throws Exception {
		infoUser.setUser_id_valid_from(getDateFromToday(-2));
		infoUser.setUser_id_valid_to(getDateFromToday(-1));
		blSecurity.changePassword(certInfo, em);
		assertFalse(certInfo.getIsSuccessChange());
		assertEquals(GnomesMessagesConstants.ME01_0015, certInfo.getMessage());
		assertTrue(GnomesPasswordEncoder.matches(certInfo.getPassword(), infoUser.getPassword()));
	}

    @Test
    @Disabled
    @DisplayName("パスワード変更：有効期間が未来の場合はエラーを返す")
    void testChangePassword_accountWillBevalid() throws Exception {
        infoUser.setUser_id_valid_from(getDateFromToday(1));
        infoUser.setUser_id_valid_to(getDateFromToday(31));
        blSecurity.changePassword(certInfo, em);
        assertFalse(certInfo.getIsSuccessChange());
        assertEquals(GnomesMessagesConstants.ME01_0015, certInfo.getMessage());
        assertTrue(GnomesPasswordEncoder.matches(certInfo.getPassword(), infoUser.getPassword()));
    }

    @Test
    @Disabled
    @DisplayName("パスワード変更：パスワードの有効期限が切れた場合はエラーを返す")
    void testChangePassword_passwordExpired() throws Exception {
        infoUser.setPassword_update_date(getDateFromToday(-31));
        mstrPersonSecPolicy.setPassword_limit_day(30);
        blSecurity.changePassword(certInfo, em);
        assertFalse(certInfo.getIsSuccessChange());
        assertEquals(GnomesMessagesConstants.ME01_0016, certInfo.getMessage());
        assertTrue(GnomesPasswordEncoder.matches(certInfo.getPassword(), infoUser.getPassword()));
    }

	@Test
	@DisplayName("パスワード変更：過去利用したパスワードを入力した場合はエラーを返す")
	void testChangePassword5() throws Exception {
		//パスワード履歴設定はsetUp()を参照
		certInfo.setNewPassword(TEST_HISTHORY_PW);
		certInfo.setNewPasswordConfirm(TEST_HISTHORY_PW);
		blSecurity.changePassword(certInfo, em);
		assertFalse(certInfo.getIsSuccessChange());
		assertEquals(GnomesMessagesConstants.ME01_0017, certInfo.getMessage());
		assertTrue(GnomesPasswordEncoder.matches(certInfo.getPassword(), infoUser.getPassword()));
	}

	@Test
	@DisplayName("パスワード変更：DBに登録された使用禁止の文字列を入力した場合はエラーを返す")
	void testChangePassword6() throws Exception {
		mstrPersonSecPolicy.setIs_specific_character_prohibit(1);
		certInfo.setNewPassword("testInvaild-PW");
		certInfo.setNewPasswordConfirm("testInvaild-PW");
		blSecurity.changePassword(certInfo, em);
		assertFalse(certInfo.getIsSuccessChange());
		assertEquals(GnomesMessagesConstants.ME01_0022, certInfo.getMessage());
		assertTrue(GnomesPasswordEncoder.matches(certInfo.getPassword(), infoUser.getPassword()));
	}

	@DisplayName("パスワード変更：アルファベットのみ、数字のみのPWを入力した場合はエラーを返す")
	@ParameterizedTest
	@ValueSource(strings = {"EnglishOnly", "12345678"})
	void testChangePassword7(String pw) throws Exception {
		//半角英数字のみのパスワードが利用禁止フラグ→ON
		mstrPersonSecPolicy.setIs_alphabet_and_numeral_only(1);
		certInfo.setNewPassword(pw);
		certInfo.setNewPasswordConfirm(pw);
		blSecurity.changePassword(certInfo, em);
		assertFalse(certInfo.getIsSuccessChange());
		assertEquals(GnomesMessagesConstants.ME01_0023, certInfo.getMessage());
		assertTrue(GnomesPasswordEncoder.matches(certInfo.getPassword(), infoUser.getPassword()));
	}

	@Test
	@DisplayName("パスワード変更：ユーザーIDをPWとして使用した場合はエラーを返す")
	void testChangePassword8() throws Exception {
		//ユーザID利用禁止フラグ→ON
		mstrPersonSecPolicy.setIs_user_id_password_prohibit(1);
		certInfo.setNewPassword("testUserID");
		certInfo.setNewPasswordConfirm("testUserID");
		blSecurity.changePassword(certInfo, em);
		assertFalse(certInfo.getIsSuccessChange());
		assertEquals(GnomesMessagesConstants.ME01_0021, certInfo.getMessage());
		assertTrue(GnomesPasswordEncoder.matches(certInfo.getPassword(), infoUser.getPassword()));
	}

	@Test
	@DisplayName("パスワード変更：全て同じ文字のPWを使用した場合はエラーを返す")
	void testChangePassword9() throws Exception {
		mstrPersonSecPolicy.setIs_one_character_password_prohibit(1);
		certInfo.setNewPassword("aaaaaa");
		certInfo.setNewPasswordConfirm("aaaaaa");
		blSecurity.changePassword(certInfo, em);
		assertFalse(certInfo.getIsSuccessChange());
		assertEquals(GnomesMessagesConstants.ME01_0020, certInfo.getMessage());
		assertTrue(GnomesPasswordEncoder.matches(certInfo.getPassword(), infoUser.getPassword()));
	}

	@Test
	@Disabled
	@DisplayName("パスワード変更：現在使用中のパスワードを使用した場合はエラーを返す")
	void testChangePassword10() throws Exception {
		certInfo.setNewPassword("testOldPassword");
		certInfo.setNewPasswordConfirm("testOldPassword");
		blSecurity.changePassword(certInfo, em);
		assertFalse(certInfo.getIsSuccessChange());
		assertEquals(GnomesMessagesConstants.ME01_0197, certInfo.getMessage());
		assertTrue(GnomesPasswordEncoder.matches(certInfo.getPassword(), infoUser.getPassword()));
	}

	private CertInfo createCertInfo() {
		CertInfo certInfo = new CertInfo();
		certInfo.setUserId("testUserID");
		certInfo.setPassword("testOldPassword");
		certInfo.setNewPassword("testNewPassword");
		certInfo.setNewPasswordConfirm("testNewPassword");
		certInfo.setIsInitPassword(0);
		return certInfo;
	}

	private Date getDateFromToday(int interval) {
		Date nowDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(nowDate);
		cal.add(Calendar.DAY_OF_MONTH, interval);
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
