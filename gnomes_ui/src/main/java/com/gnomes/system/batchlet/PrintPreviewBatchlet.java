package com.gnomes.system.batchlet;

import java.util.Properties;

import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.gnomes.common.batch.batchlet.BaseBatchlet;
import com.gnomes.common.data.GnomesEjbBean;
import com.gnomes.system.ejb.PrintPreviewEjb;

/**
 * 帳票印刷プレビュー バッチレット クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2021/12/02 YJP-D/Jixin.Sun           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
@Named("PrintPreviewBatchlet")
public class PrintPreviewBatchlet extends BaseBatchlet
{

    /** 帳票印刷プレビュー EJB クラス */
    @EJB
    protected PrintPreviewEjb printPreviewEjb;

    @Inject
    private GnomesEjbBean     ejbBean;

    @Override
    public void setBatchCommonRequestPartameter(Properties p)
    {

    }

    @Override
    public String process() throws Exception
    {

        ejbBean.setEjbBatch(true);

        if (checkBatchInProcess(this.printPreviewEjb.getClassName(), this.printPreviewEjb.getMethodName())) {
            // ビジネスロジック実行
            this.printPreviewEjb.process(this.getParameters());
        }

        return PROCESS_RESULT_COMPLETED;

    }

}
