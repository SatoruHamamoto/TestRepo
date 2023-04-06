package com.gnomes.rest.client;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation.Builder;

import org.picketbox.util.StringUtil;

import java.util.logging.Logger;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.data.EventDrivenFromBatchData;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.rest.service.GnomesWebServiceDataInput;
import com.gnomes.system.logic.InsecureHostnameVerifier;
import com.gnomes.system.logic.InsecureTrustManager;
import com.gnomes.rest.service.RestServiceResult;

/**
 * コマンド使用RESTサービス化クラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2021/07/06  YJP/K.Nakanishi          初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */

@RequestScoped
public class DemandGnomesServiceRun {
    // ロガー
    @Inject
    transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected LogHelper logHelper;

    @Inject
    EventDrivenFromBatchData eventDrivenFromBatchData;

    protected Client rsClient = null;

    public void execute() throws Exception {

        try {

            // 接続先サービスURL
            // 外部IFはGnomesCommonBatService、帳票はGnomesInternalWebServiceForSFのクラスを参照
            // どちらもstartBatchProcessメソッドの内容は同じで、引数は別のクラスを参照しているが内容は同じ
            // 以下では外部IFに合わせている
            // GnomesCommonBatServiceの方が関数が多く高機能なのでGnomesInternalWebServiceForSFはいらないかも
            String servicePath = String.format("http://%s/UI/%s/%s/%s",
                        "localhost", "rest", "GnomesCommonBatService", "startBatchProcess");

            // コマンド終了後に処理するバッチが無ければ終了
            if(eventDrivenFromBatchData.getCommandAfterBatchParameter().size() <= 0 ){
                return;
            }

            // 取得したコマンド数分のRESTサービスをバッチ実行
            for (String commandId : eventDrivenFromBatchData.getCommandAfterBatchParameter().keySet()) {

                // 処理するバッチのコマンドIDが存在すれば実行
                if(!StringUtil.isNullOrEmpty(commandId)){

                    // 処理するバッチのRESTサービスの準備
                    GnomesWebServiceDataInput pdata = new GnomesWebServiceDataInput();
                    pdata.setCommand(commandId);
                    pdata.setParamList(eventDrivenFromBatchData.getCommandAfterBatchParameter().get(commandId));
                    pdata.setDupCheck(false);

                    // RESTサービス実行
                    this.post(servicePath, Entity.json(pdata), RestServiceResult.class);
                }
            }

         } catch (Exception e) {
             this.logHelper.severe(this.logger, null, null, e.getMessage(), e);
         } finally {
             // RESTサービス初期化（コマンド後バッチはコマンド毎に追加設定する為、処理後にクリア）
             eventDrivenFromBatchData.getCommandAfterBatchParameter().clear();
             eventDrivenFromBatchData.getSendProcParameter().clear();
             try
            {
                if (rsClient != null) {

                    rsClient.close();
                    rsClient = null;
                }
            }
            catch(Exception e)
            {
                this.logHelper.severe(this.logger, null, null, e.getMessage(), e);
            }
        }
    }

    /**
     * サービス呼出処理(POSTメゾット).
     * @param servicePath サービスパス
     * @param paramEntity エンティティ
     * @param clazz クラス
     * @throws Exception
     * @see com.gnomes.common.logic.RsClient.post
     */
    private <T> T post(String servicePath, Entity<?> paramEntity, Class<T> clazz) throws Exception {

        T resultValue = null;

        // RSClientオブジェクト
        rsClient = this.createClient();
        // 接続先サービスのURLを指定
        WebTarget target = rsClient.target(servicePath);
        // リクエストの生成
        Builder builder = target.request();
        // POSTメゾットの実行、送信パラメータと返却される型を指定
        resultValue = builder.post(paramEntity, clazz);

        return resultValue;

    }
    /**
     * RSClientオブジェクト生成.
     * @return RSClientオブジェクト
     * @throws NoSuchAlgorithmException
     * @throws Exception
     * @see com.gnomes.common.logic.RsClient.createClient
     */
    private Client createClient() throws Exception {

        SSLContext sc = SSLContext.getInstance(CommonConstants.RSCLIENT_SECURE_SOCKET_PROTOCOL_TLS);//Java 8
        System.setProperty(CommonConstants.RSCLIENT_HTTP_PROTOCOLS, CommonConstants.RSCLIENT_SECURE_SOCKET_PROTOCOL_TLS);//Java 8

        TrustManager[] trustAllCerts = { new InsecureTrustManager() };
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HostnameVerifier allHostsValid = new InsecureHostnameVerifier();

        rsClient = ClientBuilder.newBuilder().sslContext(sc).hostnameVerifier(allHostsValid).build();
        rsClient.property(CommonConstants.RSCLIENT_PROPERTY_TIMEOUT, 1);

        return rsClient;

    }
}
