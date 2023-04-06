package com.gnomes.common.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;

import org.junit.jupiter.api.Test;

/**
 * {@link GnomesPasswordEncoder}クラスのテスト
 * @author YJP/K.Tada
 */
public class GnomesPasswordEncoderTest {
	@Test
	public final void エンコード結果と元のパスワードが同じかどうか確認可能であることのテスト() {
		String testRawPassword = "ここの値は何でも良い";
		String encodedPassword = GnomesPasswordEncoder.encode(testRawPassword);

		boolean match = GnomesPasswordEncoder.matches(testRawPassword, encodedPassword);
		assertTrue(match, "エンコード結果との一致確認が取れない");
	}

	@Test
	public final void 同じパスワードのエンコードでも毎度結果が変化することのテスト() {
		String testRawPassword = "ここの値は何でも良い";
		String encodedPassword1 = GnomesPasswordEncoder.encode(testRawPassword);
		String encodedPassword2 = GnomesPasswordEncoder.encode(testRawPassword);

		//System.out.println("encodedPassword1: " + encodedPassword1);
		//System.out.println("encodedPassword2: " + encodedPassword2);
		assertNotEquals(encodedPassword2, encodedPassword1, "エンコード結果が変化していない");
	}

	@Test
	public final void パスワードの長さに寄らず出力が固定長になることのテスト() {
		int sha256Length = 64; // DigestUtils.sha256Hex() が出力する文字列の長さ
		int delimiterLength = 1;
		int saltLength = 8 * 2;
		int encodedPasswordLength = saltLength + delimiterLength + sha256Length;
		String[] testRawPasswords = { //
				"" // 0文字
				, "1" // 1文字
				, "1234567890" // 10文字
				, String.join("", Collections.nCopies(16, "1234567890")) // 160文字（パスワード入力欄のmaxlength）
				, String.join("", Collections.nCopies(100, "1234567890")) // 1000文字
		};

		for (String testRawPassword : testRawPasswords) {
			String encodedPassword = GnomesPasswordEncoder.encode(testRawPassword);

			// System.out.println("testRawPassword: " + testRawPassword);
			// System.out.println("encodedPassword: " + encodedPassword);
			assertEquals(encodedPassword.length(), encodedPasswordLength, "出力長が想定と違う");
		}
	}

}
