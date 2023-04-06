package com.gnomes.external.batchlet;

import java.util.Properties;

import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.gnomes.common.batch.batchlet.BaseBatchlet;
import com.gnomes.common.data.GnomesEjbBean;
import com.gnomes.external.ejb.FileTransferEJB;

/**
 * 送信結果 バッチレット クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/10/04 YJP/A.Oomori            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
@Named
public class SendResultBatchlet extends BaseBatchlet {

    @EJB
    FileTransferEJB fileTransferEJB;

    @Inject
    private GnomesEjbBean ejbBean;

    @Override
    public String process() throws Exception {

        ejbBean.setEjbBatch(true);

        // ビジネスロジック実行
        fileTransferEJB.sendResult(this.getParameters());

        return PROCESS_RESULT_COMPLETED;
    }

    @Override
    public void setBatchCommonRequestPartameter(Properties p) {

    }
}
