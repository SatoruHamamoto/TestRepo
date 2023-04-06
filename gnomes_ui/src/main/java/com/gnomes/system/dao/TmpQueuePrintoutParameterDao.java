package com.gnomes.system.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import com.gnomes.common.dao.BaseDao;
import com.gnomes.common.dao.JdbcAccessDao;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.system.entity.TmpQueuePrintout;
import com.gnomes.system.entity.TmpQueuePrintoutParameter;

/**
 * 印字キューパラメータ Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/11/19 KCC/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class TmpQueuePrintoutParameterDao extends BaseDao implements Serializable {

    @Inject
    JdbcAccessDao jdbcAccessDao;

    /**
	 * コンストラクター
	 */
	public TmpQueuePrintoutParameterDao() {

	}


	/**
	 * 印字キューパラメータ取得
	 * @param em 独自管理エンティティマネージャー
	 * @param queuePrintoutKey nk印字キューキー (FK)
	 * @return
	 */
    @TraceMonitor
    @ErrorHandling
    public List<TmpQueuePrintoutParameter> getParameter(EntityManager em, String queuePrintoutKey ) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<TmpQueuePrintoutParameter> query = builder.createQuery(TmpQueuePrintoutParameter.class);
        Root<TmpQueuePrintoutParameter> root = query.from(TmpQueuePrintoutParameter.class);

        List<Predicate> preds = new ArrayList<>();
        preds.add(builder.equal(root.get(TmpQueuePrintoutParameter.COLUMN_NAME_QUEUE_PRINTOUT_KEY),
        		queuePrintoutKey));

        query.where(builder.and(preds.toArray(new Predicate[]{})));
        query.orderBy(builder.asc(root.get(TmpQueuePrintoutParameter.COLUMN_NAME_PARAMETER_NAME)));
        TypedQuery<TmpQueuePrintoutParameter> typedQuery = em.createQuery(query);
        List<TmpQueuePrintoutParameter> result = typedQuery.getResultList();

        return result;
    }

    /**
     * 印字キューパラメータ登録
     * @param items 印字キューパラメータ
     * @param em 独自管理エンティティマネージャー
     */
    @TraceMonitor
    @ErrorHandling
    public void insert(List<TmpQueuePrintoutParameter> items, EntityManager em) {
        for(TmpQueuePrintoutParameter item : items) {
            // 登録
            item.setReq(super.req);
            em.persist(item);
        }
        em.flush();
    }

    /**
     * 印字キューパラメータ 登録(Bulk)
     * @param items 印字キューパラメータ
     * @param em 独自管理エンティティマネージャー
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void bulkPersist(List<TmpQueuePrintoutParameter> items, EntityManager em) throws GnomesAppException {

        if (!Objects.isNull(items)) {
            // 登録
        	jdbcAccessDao.bulkPersist(em, items);

        }
        em.flush();
    }

    /**
     * 印字キューパラメータ削除
     * @param items 印字キューパラメータ
     * @param em 独自管理エンティティマネージャー
     */
    @TraceMonitor
    @ErrorHandling
    public void delete(List<TmpQueuePrintoutParameter> items, EntityManager em) {
        for(TmpQueuePrintoutParameter item : items) {
            // 登録
            item.setReq(super.req);
            em.remove(item);
        }
        em.flush();
    }
}
