package com.gnomes.common.logic;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.data.GnomesEjbBean;
import com.gnomes.external.data.FileTransferBean;

/**
 * ジョブ ロジック 基底クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/12 KCC/A.Oomori            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public abstract class BaseJobLogic extends BaseLogic {

    /** 送受信 トランスファー */
    @Inject
    protected FileTransferBean fileTransferBean;

    /**
     * EJB処理されるBeanは必ずejbBeanがInjectされる
     */
    @Inject
    protected GnomesEjbBean ejbBean;

}
