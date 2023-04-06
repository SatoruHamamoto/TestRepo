package com.gnomes.system.logic;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

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
 */public class InsecureTrustManager implements X509TrustManager {
    /**
     * {@inheritDoc}
     */
    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException { // NOSONAR
    	// 要件に沿って証明書のホスト名と証明書チェーンの検証をスキップして、自己署名証明書を使用可能にしている
        // Everyone is trusted!
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException { // NOSONAR
    	// 要件に沿って証明書のホスト名と証明書チェーンの検証をスキップして、自己署名証明書を使用可能にしている
        // Everyone is trusted!
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}