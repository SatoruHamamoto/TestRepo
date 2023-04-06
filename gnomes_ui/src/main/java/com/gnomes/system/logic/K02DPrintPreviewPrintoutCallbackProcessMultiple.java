package com.gnomes.system.logic;

import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.system.data.printout.PrintPreviewCallbackInfo;

import biz.grandsight.ex.rs_multiple.CReportGen;
import biz.grandsight.ex.rs_multiple.CReportGenCallback;
import biz.grandsight.ex.rs_multiple.CReportGenException;

/**
 * 帳票印刷コールバック関数処理
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2021/12/02 YJP-D/Jixin.Sun           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class K02DPrintPreviewPrintoutCallbackProcessMultiple extends CReportGenCallback
{

    /** 帳票印刷コールバック情報 */
    private PrintPreviewCallbackInfo callbackInfo;

    /** CReportGen. */
    private CReportGen               cReportGen;

    /**
     * コンストラクタ.
     * @param callbackInfo 帳票印刷コールバック情報
     * @param cReportGen CReportGen
     */
    public K02DPrintPreviewPrintoutCallbackProcessMultiple(PrintPreviewCallbackInfo callbackInfo, CReportGen cReportGen)
    {

        this.callbackInfo = callbackInfo;
        this.cReportGen = cReportGen;

    }

    @Override
    public String call() throws Exception
    {

        try {

            this.callbackInfo.setReportId(super.reportId);

            // 印字結果確認
            if (cReportGen.GetPrintStatus(super.reportId) < 0) {
                callbackInfo.setPrintErrorMsg(cReportGen.GetPrintErrorMsg(super.reportId));

            }

        }
        catch (CReportGenException e) {
            callbackInfo.setPrintErrorMsg(e.getMessage());
        }

        // 接続先サービスURL
        String servicePath = String.format("http://%s/UI/%s/%s/%s", "localhost", "rest",
                "K02CPrintPreviewPrintoutCallbackService", "process");

        this.post(servicePath, Entity.json(callbackInfo));

        return null;
    }

    /**
     * サービス呼出処理(POSTメゾット).
     * @param servicePath サービスパス
     * @param paramEntity エンティティ
     * @throws Exception
     * @see com.gnomes.common.logic.RsClient.post
     */
    private void post(String servicePath, Entity<?> paramEntity) throws Exception
    {

        // RSClientオブジェクト
        Client rsClient = this.createClient();
        // 接続先サービスのURLを指定
        WebTarget target = rsClient.target(servicePath);
        // リクエストの生成
        Builder builder = target.request();
        // POSTメゾットの実行、送信パラメータと返却される型を指定
        builder.post(paramEntity);

    }

    /**
     * RSClientオブジェクト生成.
     * @return RSClientオブジェクト
     * @throws NoSuchAlgorithmException
     * @throws Exception
     * @see com.gnomes.common.logic.RsClient.createClient
     */
    private Client createClient() throws Exception
    {

        Client rsClient = null;

        SSLContext sc = SSLContext.getInstance(CommonConstants.RSCLIENT_SECURE_SOCKET_PROTOCOL_TLS);//Java 8
        System.setProperty(CommonConstants.RSCLIENT_HTTP_PROTOCOLS,
                CommonConstants.RSCLIENT_SECURE_SOCKET_PROTOCOL_TLS);//Java 8

        TrustManager[] trustAllCerts = {new InsecureTrustManager()};
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HostnameVerifier allHostsValid = new InsecureHostnameVerifier();

        rsClient = ClientBuilder.newBuilder().sslContext(sc).hostnameVerifier(allHostsValid).build();
        rsClient.property(CommonConstants.RSCLIENT_PROPERTY_TIMEOUT, 1);

        return rsClient;

    }

}
