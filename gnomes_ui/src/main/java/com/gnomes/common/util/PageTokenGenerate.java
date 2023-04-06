package com.gnomes.common.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
/**
 * ページトークン生成クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/02/20 YJP/T.Kamizuru             初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public final class PageTokenGenerate {

    private static int TOKEN_LENGTH = 16;//16*2=32バイト

    //32バイトのCSRFトークンを作成
    public static String getCsrfToken() throws NoSuchAlgorithmException {
      byte token[] = new byte[TOKEN_LENGTH];
      StringBuffer buf = new StringBuffer();
      SecureRandom random = null;

        random = SecureRandom.getInstance("SHA1PRNG");
        random.nextBytes(token);

        for (int i = 0; i < token.length; i++) {
          buf.append(String.format("%02x", token[i]));
        }


      return buf.toString();
    }
}

