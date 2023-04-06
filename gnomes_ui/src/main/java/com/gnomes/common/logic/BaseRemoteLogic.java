package com.gnomes.common.logic;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.gnomes.common.data.ProxyInfo;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.util.ProxyImpl;

/**
 * リモートロジック 基底クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/08/23 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class BaseRemoteLogic extends BaseLogic {

    /** プロキシ処理クラス. */
    @Inject
    protected ProxyImpl proxy;

    /**
     * コンストラクタ.
     */
    public BaseRemoteLogic() {
        super();
    }

    /**
     * プロキシ生成.
     * @param proxyInfo プロキシ情報
     * @throws GnomesAppException
     */
    public void createProxy(ProxyInfo proxyInfo) throws GnomesAppException {

        // プロキシ生成
        this.proxy.create(proxyInfo);

    }

    /**
     * プロキシ処理クラス取得.
     * @return プロキシ処理クラス
     */
    public ProxyImpl getProxy() {
        return this.proxy;
    }

    /**
     * プロキシ破棄.
     */
    public void removeProxy() {

        this.proxy.remove();

    }

}
