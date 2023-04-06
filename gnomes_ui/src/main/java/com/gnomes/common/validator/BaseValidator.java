package com.gnomes.common.validator;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.data.GnomesSystemBean;
import com.gnomes.uiservice.ContainerRequest;

/**
 * バリデーション 基底クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/06/13 KCC/A.Oomori                  初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public abstract class BaseValidator {

    @Inject
    protected
    ContainerRequest req;

    @Inject
    protected
    GnomesSessionBean gnomesSessionBean;

    @Inject
    protected
    GnomesSystemBean gnomesSystemBean;

    /**
     * デフォルトコンストラクタ
     */
    public BaseValidator() {
    }

}
