package com.gnomes.common.logic;

import javax.enterprise.context.Dependent;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.system.logic.InsecureHostnameVerifier;
import com.gnomes.system.logic.InsecureTrustManager;

/**
 * JAX-RS サーバアクセスクラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/06/07 KCC/A.Oomori            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class RsClient extends BaseLogic {

    protected Client rsClient = null;
    protected WebTarget target = null;
    protected Invocation.Builder builder = null;

    /**
     * デフォルト・コンストラクタ
     */
    public RsClient() {
    }

    /**
     * サービス呼出処理(POSTメゾット)
     * @param servicePath
     * @param paramEntity
     * @param clazz
     * @return resultValue
     * @throws Exception
     */
    @ErrorHandling
    public <T> T post(String servicePath, Entity<?> paramEntity, Class<T> clazz) throws GnomesAppException
    {
        T resultValue = null;

        try{

            if (rsClient == null)
            {
                // RSClient オブジェクトの生成
                createClient();
            }

            // 接続先サービスのURLを指定
            target = rsClient.target(servicePath);

            // リクエストの生成
            builder = target.request();

            // POSTメゾットの実行、送信パラメータと返却される型を指定
            resultValue = builder.post(paramEntity, clazz);

        }
        catch (GnomesAppException e) {
            throw e;
        }
        catch (GnomesException e) {
        	throw e;
        }
        // HTTP 404 Not Found path設定ミス
        catch(NotFoundException e){
            // Web サービス呼び出しに失敗しました。（接続先不正） ({0}) 詳細はエラーメッセージを確認してください。
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0056, e, servicePath);
            throw ex;
        }
        // タイムアウト
        catch(ProcessingException e){
            // Web サービス呼び出しに失敗しました。（タイムアウト） ({0}) 詳細はエラーメッセージを確認してください。
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0057, e, servicePath);
            throw ex;
        }
        // その他
        catch(Exception e){
            // Web サービス呼び出しに失敗しました。({0}) 詳細はエラーメッセージを確認してください。
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0074, e, servicePath);
            throw ex;
        }

        return resultValue;
    }

    /**
     * JAX-RSクライアント取得.
     * @return JAX-RSクライアント
     */
    public Client getClient() {
        return this.rsClient;
    }

    /**
     * RSClient オブジェクトの生成
     */
    private void createClient() throws GnomesAppException {

        // 以下のコードは https://stackoverflow.com/questions/2145431/https-using-jersey-client を参考に実装

        try {

            //SSLContext sc = SSLContext.getInstance(CommonConstants.RSCLIENT_SECURE_SOCKET_PROTOCOL_SSL);//Java 6
            SSLContext sc = SSLContext.getInstance(CommonConstants.RSCLIENT_SECURE_SOCKET_PROTOCOL_TLS);//Java 8
            System.setProperty(CommonConstants.RSCLIENT_HTTP_PROTOCOLS, CommonConstants.RSCLIENT_SECURE_SOCKET_PROTOCOL_TLS);//Java 8


            TrustManager[] trustAllCerts = { new InsecureTrustManager() };
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HostnameVerifier allHostsValid = new InsecureHostnameVerifier();

            this.rsClient = ClientBuilder.newBuilder().sslContext(sc).hostnameVerifier(allHostsValid).build();
            this.rsClient.property(CommonConstants.RSCLIENT_PROPERTY_TIMEOUT, 1);

        }
        catch (Exception e) {
            // Web サービス・クライアントの生成に失敗しました。 詳細はエラーメッセージを確認してください。
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0073, e);
            throw ex;
        }

    }

}
