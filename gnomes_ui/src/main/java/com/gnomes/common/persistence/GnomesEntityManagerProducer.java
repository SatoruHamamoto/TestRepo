package com.gnomes.common.persistence;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums.RegionType;
import com.gnomes.common.data.GnomesEjbBean;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.persistence.annotation.GnomesDS;

/**
 * エンティティマネージャープロデューサー
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/07/04 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class GnomesEntityManagerProducer {

    /** セッションビーン */
    @Inject
    protected GnomesSessionBean gnomesSessionBean;

    /**
     * EJBスレッドを表し、管理されるリクエストスコープビーン
     */
    @Inject
    protected GnomesEjbBean     ejbBean;

    /** エンティティマネージャーファクトリ（通常領域） */
    @PersistenceUnit(unitName = CommonConstants.PERSISTENCE_UNIT_NAME_NON_JTA)
    private EntityManagerFactory emf;

    /** エンティティマネージャーファクトリ（保管領域） */
    @PersistenceUnit(unitName = CommonConstants.PERSISTENCE_UNIT_NAME_NON_JTA_STORAGE)
    private EntityManagerFactory emfStorage;

    /**
     * エンティティマネージャーファクトリ取得.
     * <pre>
     * セッション情報の領域区分を元に判定し、通常領域または、
     * 保管領域のエンティティマネージャーファクトリを返却する。
     * 領域区分が未設定の場合は、<code>null</code>を返却する。
     * </pre>
     * @return エンティティマネージャーファクトリ
     */
    @Produces
    @GnomesDS
    protected EntityManagerFactory getEntityManagerFactory() {

        //EJBスレッドの場合は通常領域固定になる
        if(this.ejbBean.isEjbBatch()){
            return this.emf;
        }
        else {
            //通常画面処理だとsessionBeanを見てどちらかを選ぶ
            if (RegionType.NORMAL.equals(RegionType.getEnum(
                    this.gnomesSessionBean.getRegionType()))) {

                return this.emf;

            } else if (RegionType.STORAGE.equals(RegionType.getEnum(
                    this.gnomesSessionBean.getRegionType()))) {

                return this.emfStorage;

            }
        }

        return null;

    }

}
