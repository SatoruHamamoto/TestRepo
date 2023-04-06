package com.gnomes.system.dao;


import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.TypedQuery;

import com.gnomes.common.constants.GnomesQueryConstants;
import com.gnomes.common.dao.BaseDao;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.persistence.GnomesEntityManager;
import com.gnomes.system.entity.MstrPersonSecPolicy;

/**
 * ユーザアカウントセキュリティポリシー Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/04/28 KCC/T.Kamizuru            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class MstrPersonSecPolicyDao extends BaseDao implements Serializable {

	@Inject
	protected GnomesEntityManager em;

    /**
     * コンストラクタ
     */
    public MstrPersonSecPolicyDao() {
    }


    /**
     * ユーザアカウントセキュリティポリシー 取得
     * @return ユーザアカウントセキュリティポリシー
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrPersonSecPolicy> getMstrPersonSecPolicy() throws GnomesAppException {

        List<MstrPersonSecPolicy> datas = gnomesSystemModel.getMstrPersonSecPolicyList();

        return datas;
    }

    /**
     * ユーザアカウントセキュリティポリシー 取得
     * @return ユーザアカウントセキュリティポリシー
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrPersonSecPolicy> getMstrPersonSecPolicyQuery() {

        // ２．ユーザアカウントセキュリテイポリシーの取得
        TypedQuery<MstrPersonSecPolicy> queryUsrsecPolicy =
                this.em.getEntityManager().createNamedQuery(
                        GnomesQueryConstants.QUERY_NAME_BLSECURITY_MSTR_PERSON_SEC_POLICY,
                        MstrPersonSecPolicy.class);
        List<MstrPersonSecPolicy> datasUsrsecPolicy = queryUsrsecPolicy.getResultList();

        return datasUsrsecPolicy;
    }

}
