package com.gnomes.system.ejb;

import java.util.Properties;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.gnomes.common.batch.batchlet.BaseEJB;
import com.gnomes.common.batch.batchlet.EJBWrapper;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.system.logic.K01APrintOut;

/**
 * 帳票印字 EJB クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/03/20 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Stateless
public class PrintOutEjb extends BaseEJB {

    /** 帳票印刷処理 */
    @Inject
    protected K01APrintOut k01APrintOut;

    /**
     * コンストラクタ
     */
    public PrintOutEjb() {

    }

    /**
     * 帳票印字処理
     * @throws Exception
     */
    @TraceMonitor
    @EJBWrapper
    public void process(Properties p) throws Exception {

        // セッション情報の設定
        this.setSessionInfo();

        String localeId = p.getProperty(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0106));

        this.k01APrintOut.print(localeId);

    }

    /**
     * 呼び出しクラス名取得
     * @return 呼び出しクラス名
     */
    public String getClassName() {

        return this.k01APrintOut.getClassName();

    }

    /**
     * 呼び出しメソッド名取得
     * @return 呼び出しメソッド名
     */
    public String getMethodName() {

        return this.k01APrintOut.getMethodName();

    }

}
