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
import com.gnomes.system.entity.MstrInvalidPasswd;

/**
 * パスワード禁止文字 Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/04/28 KCC/T.Kamizuru            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class MstrInvalidPasswdDao extends BaseDao implements Serializable {

	@Inject
	protected GnomesEntityManager em;

    /**
     * コンストラクタ
     */
    public MstrInvalidPasswdDao() {
    }

    /**
     * パスワード禁止文字 取得
     * @return パスワード禁止文字
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrInvalidPasswd> getMstrInvalidPasswd() throws GnomesAppException {

        List<MstrInvalidPasswd> datas = gnomesSystemModel.getMstrInvalidPasswdList();

        return datas;
    }

    /**
     * パスワード禁止文字 取得
     * @return パスワード禁止文字
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrInvalidPasswd> getMstrInvalidPasswdQuery() {

        TypedQuery<MstrInvalidPasswd> queryInvalidPasswd =
                this.em.getEntityManager().createNamedQuery(
                        GnomesQueryConstants.QUERY_NAME_BLSECURITY_MSTR_INVALID_PASSWD,
                        MstrInvalidPasswd.class);

        List<MstrInvalidPasswd> datasInvalidPasswd = queryInvalidPasswd.getResultList();

        return datasInvalidPasswd;
    }

}
