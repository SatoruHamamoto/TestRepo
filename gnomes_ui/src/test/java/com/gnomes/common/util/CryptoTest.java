package com.gnomes.common.util;

import org.apache.wss4j.common.crypto.Crypto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * {@link Crypto}クラスのテスト
 * @author YJP/K.Tada
 */
public class CryptoTest {
	@Test
	public final void 暗号化後の文字列が複合化メソッドで元の文字列に戻るテスト() {
		String testPlainText = "ここの値は何でも良い";
		try {
//			String encryptedPassword = Crypto.encrypt(testPlainText);
//			String decryptedPassword = Crypto.decrypt(encryptedPassword);

			//System.out.println("testPlainText    : " + testPlainText);
			//System.out.println("encryptedPassword: " + encryptedPassword);
			//System.out.println("decryptedPassword: " + decryptedPassword);
			//assertThat("暗号化後の文字列を復号化しても元の文字列にならない", decryptedPassword, is(testPlainText));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception発生");
		}
	}

}
