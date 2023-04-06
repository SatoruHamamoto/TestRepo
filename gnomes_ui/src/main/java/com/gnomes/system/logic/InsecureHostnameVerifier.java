package com.gnomes.system.logic;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * JAX-RS サーバアクセス ヘルパクラス
 * 以下のコードは https://stackoverflow.com/questions/2145431/https-using-jersey-client を参考に実装
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/06/07 KCC/A.Oomori            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class InsecureHostnameVerifier implements HostnameVerifier {
    @Override
    public boolean verify(String hostname, SSLSession session) {
    	// 要件に沿って証明書のホスト名と証明書チェーンの検証をスキップして、自己署名証明書を使用可能にしている
        return true; // NOSONAR
    }
}