package com.gnomes.common.data;

/**
 * プロキシ接続情報クラス定義.
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/08/04 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class ProxyInfo {

    /** ユーザID. */
    private String userId;

    /** パスワード. */
    private String password;

    /** 接続先 サービスURI. */
    private String serviceUri;

    /**
     * ユーザID取得.
     * @return ユーザID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * ユーザID設定.
     * @param userId ユーザID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * パスワード取得.
     * @return パスワード
     */
    public String getPassword() {
        return password;
    }

    /**
     * パスワード設定.
     * @param password パスワード
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 接続先 サービスURI取得.
     * <pre>
     * 「http://」(SSLの場合「https://」)) + 接続先 ホスト名 + 「/fides/」
     * </pre>
     * @return 接続先 サービスURL
     */
    public String getServiceUri() {
        return serviceUri;
    }

    /**
     * 接続先 サービスURL設定.
     * <pre>
     * 「http://」(SSLの場合「https://」)) + 接続先 ホスト名 + 「/fides/」
     * </pre>
     * @param serviceUri 接続先 サービスURI
     */
    public void setServiceUri(String serviceUri) {
        this.serviceUri = serviceUri;
    }

}
