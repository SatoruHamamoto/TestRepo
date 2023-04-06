package com.gnomes.system.batchlet;

import java.util.Properties;

import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.gnomes.common.batch.batchlet.BaseBatchlet;
import com.gnomes.common.data.GnomesEjbBean;
import com.gnomes.system.ejb.PrintOutEjb;

/**
 * 帳票印字 バッチレット クラス
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
@Dependent
@Named("PrintOutBatchlet")
public class PrintOutBatchlet extends BaseBatchlet {

    /** 帳票印字 EJB クラス */
    @EJB
    protected PrintOutEjb printOutEjb;

    @Inject
    private GnomesEjbBean ejbBean;

    @Override
    public void setBatchCommonRequestPartameter(Properties p) {

    }

    @Override
    public String process() throws Exception {

        ejbBean.setEjbBatch(true);

        if (checkBatchInProcess(
                this.printOutEjb.getClassName(), this.printOutEjb.getMethodName())) {
            // ビジネスロジック実行
            this.printOutEjb.process(this.getParameters());
        }

        return PROCESS_RESULT_COMPLETED;

    }

}
