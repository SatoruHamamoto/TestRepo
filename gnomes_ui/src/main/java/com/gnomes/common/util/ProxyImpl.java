package com.gnomes.common.util;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;

import org.picketbox.util.StringUtil;

import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.data.ProxyInfo;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.logic.RsClient;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.system.data.CertInfo;

/**
 * プロキシ処理クラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/08/17 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class ProxyImpl {

    /** JAX-RS サーバアクセスクラス. */
    @Inject
    protected RsClient rsClient;

    /** セッションビーン. */
    @Inject
    protected GnomesSessionBean gnomesSessionBean;

    /** GnomesExceptionFactory. */
    @Inject
    protected GnomesExceptionFactory exceptionFactory;

    /** 接続先 サービスURL(共通). */
    protected String serviceUri;

    /** リモート呼び出しクラス名. */
    protected String className;

    /** リモート呼び出しメソッド名. */
    protected String methodName;

    /** リモート呼び出しstaticメソッドフラグ. */
    protected boolean staticMethodFlag;

    /**
     * コンストラクタ.
     */
    public ProxyImpl() {

        this.serviceUri = "";
        this.className = "";
        this.methodName = "";
        this.staticMethodFlag = false;

    }

    /**
     * プロキシ生成.
     * @param proxyInfo プロキシ情報
     * @throws GnomesAppException
     */
    public void create(ProxyInfo proxyInfo) throws GnomesAppException {

        this.serviceUri = proxyInfo.getServiceUri();

        // ログイン認証の接続先サービスURL設定
        String servicePath = this.getServicePath("A01001S001", "Certify");

        // ログイン認証設定
        CertInfo certInfo = this.setLoginCertInfo(proxyInfo);

        CertInfo result = this.rsClient.post(servicePath, Entity.json(certInfo), CertInfo.class);
        // 認証に失敗した場合
        if (result.getIsSuccess() == ConverterUtils.boolToInt(false)) {
            throw exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0102);
        }

    }

    /**
     * リモート接続.
     * @param className Webサービスクラス名
     * @param methodName Webサービスメソッド名
     * @param paramEntity Webサービスパラメータ
     * @param clazz 戻り値クラス
     * @return
     * @throws GnomesAppException
     */
    public <T> T post(String className, String methodName,
            Entity<?> paramEntity, Class<T> clazz) throws GnomesAppException {

        return this.rsClient.post(this.getServicePath(className, methodName), paramEntity, clazz);

    }

    /**
     * プロキシ生成済み確認.
     * @return プロキシ生成済みの場合、<code>true</code><br>未生成の場合、<code>false</code>
     */
    public boolean isCreateProxy() {

        if (this.rsClient == null
                || this.rsClient.getClient() == null) {
            return false;
        }

        return true;

    }

    /**
     * プロキシ破棄.
     */
    public void remove() {

        if (this.rsClient != null
                && this.rsClient.getClient() != null) {

            this.rsClient.getClient().close();
            this.rsClient = null;

        }

    }

    /**
     * リモート呼び出し情報設定.
     * @param className クラス名
     * @param methodName メソッド名
     * @param staticMethodFlag staticフラグ
     */
    public void setRemoteInfo(String className, String methodName, boolean staticMethodFlag) {

        this.className = className;
        this.methodName = methodName;
        this.staticMethodFlag = staticMethodFlag;

    }

    /**
     * リモート呼び出しクラス名取得.
     * @return リモート呼び出しクラス名
     */
    public String getRemoteClassName() {
        return this.className;
    }

    /**
     * リモート呼び出しメソッド名取得.
     * @return リモート呼び出しメソッド名
     */
    public String getRemoteMethodName() {
        return this.methodName;
    }

    /**
     * リモート呼び出しstaticメソッドフラグ確認.
     * @return リモート呼び出しメソッド
     */
    public boolean isStaticMethodFlag() {
        return staticMethodFlag;
    }

    /**
     * ログイン認証.
     * @throws GnomesAppException
     */
    protected CertInfo setLoginCertInfo(ProxyInfo proxyInfo) throws GnomesAppException {

        CertInfo certInfo = new CertInfo();

        // ユーザID
        if (StringUtil.isNullOrEmpty(proxyInfo.getUserId())) {
            certInfo.setUserId(gnomesSessionBean.getUserId());
        } else {
            certInfo.setUserId(proxyInfo.getUserId());

        }
        // パスワード
        if (StringUtil.isNullOrEmpty(proxyInfo.getPassword())) {
            certInfo.setPassword(gnomesSessionBean.getPassword());
        } else {
            //2020/02/05 デッドコードで使わないのでコメントアウトだけする
            //おそらくBLをリモートで呼ぶための仕組みだと思われる
            //certInfo.setPassword(Crypto.decrypt(proxyInfo.getPassword()));
        }
        // ロケールID
        certInfo.setLocalId(gnomesSessionBean.getLocaleId());
        // エリアID
        certInfo.setSiteCode(gnomesSessionBean.getSiteCode());

        return certInfo;

    }

    /**
     * Webサービス呼び出しURL取得.
     * @param className Webサービスクラス名
     * @param methodName Webサービスメソッド名
     * @return Webサービス呼び出しURL
     */
    protected String getServicePath(String className, String methodName) {

        return String.format("%s%s/%s/%s", this.serviceUri, "rest", className, methodName);

    }

}
