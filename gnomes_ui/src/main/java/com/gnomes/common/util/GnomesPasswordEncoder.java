package com.gnomes.common.util;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * パスワードのエンコード、及びエンコード済みパスワードと平文のパスワードの一致を確認するためのクラス。
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/11/01 YJP/K.Tada                初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public final class GnomesPasswordEncoder {
	private static final String DELIMITER = "$";
	private static final String DELIMITER_LITERAL = Pattern.quote(DELIMITER);

	private static final int SALT_BYTES = 8;

	private static final SecureRandom RANDOM = new SecureRandom();

	/**
	 * デフォルト・コンストラクタ
	 */
	private GnomesPasswordEncoder() {
	}

	/**
	 * パスワードをエンコードする。<br>
	 * 同じパスワードを引数に取っても、結果は毎度変わります。
	 * そのため、このメソッドを利用してエンコード済みパスワードと平文パスワードが一致しているかの確認はできません。
	 * 一致の確認には {@link #matches(CharSequence, String)} を利用してください。
	 *
	 * @param rawPassword
	 *            平文のパスワード（nullは不許可）
	 * @return エンコード済みパスワード
	 */
	public static String encode(CharSequence rawPassword) {
		String salt = generateSalt(SALT_BYTES);
		return encode(rawPassword, salt);
	}

	   /**
     * パスワードのSaltを取得する。
     *
     * @param rawPassword
     *            エンコード済みパスワード（nullは不許可）
     * @return エンコード済みパスワード
     */
	public static String getSalt(String encodedPassword) {
	    return extractSalt(encodedPassword);
	}

	/**
	 * エンコード済みパスワードと平文パスワードの一致を確認する。
	 *
	 * @param rawPassword
	 *            平文のパスワード（nullは不許可）
	 * @param encodedPassword
	 *            エンコード済みパスワード（nullは不許可）
	 * @return {@code true}: 一致する場合、{@code false}: 一致しない場合
	 */
    public static boolean matches(CharSequence rawPassword, String encodedPassword) {
        String salt = extractSalt(encodedPassword);
        return encodedPassword.equals(encode(rawPassword, salt));
    }

	private static String generateSalt(int byteLength) {
		byte[] data = new byte[byteLength];
		RANDOM.nextBytes(data);
		return Hex.encodeHexString(data);
	}

	private static String extractSalt(String encodedPassword) {
		Objects.requireNonNull(encodedPassword, "encodedPassword must not be null");
		String[] parts = encodedPassword.split(DELIMITER_LITERAL, 2);
		String salt = parts[0];
		//String hashedPassword = parts[1];
		return salt;
	}

	private static String encode(CharSequence rawPassword, String salt) {
		Objects.requireNonNull(rawPassword, "rawPassword must not be null");
		String password = rawPassword.toString() + salt;
		String hashedPassword = DigestUtils.sha256Hex(password);
		String encodedPassword = salt + DELIMITER + hashedPassword;
		return encodedPassword;
	}

}
