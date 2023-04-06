package com.gnomes.system.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.gnomes.common.dao.BaseDao;
import com.gnomes.common.dao.JdbcAccessDao;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.system.entity.TmpQueuePrintout;

/**
 * 印字キュー Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/09/07 KCC/A.Oomori                初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class TmpQueuePrintoutDao extends BaseDao implements Serializable {

    @Inject
    JdbcAccessDao jdbcAccessDao;

    /**
     * コンストラクタ.
     */
    public TmpQueuePrintoutDao() {

    }

    /**
     * 印字キュー取得.
     * <pre>
     * 帳票印字日時が最も古い印字キューを1件取得する。
     * 取得できなかった場合、<code>null</code>を返却
     * </pre>
     * @param em 独自管理エンティティマネージャー
     * @return 印字キュー
     */
    @TraceMonitor
    @ErrorHandling
    public TmpQueuePrintout getQueue(EntityManager em) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<TmpQueuePrintout> query = builder.createQuery(TmpQueuePrintout.class);
        Root<TmpQueuePrintout> root = query.from(TmpQueuePrintout.class);

        query.orderBy(builder.asc(root.get(TmpQueuePrintout.COLUMN_NAME_PRINTOUT_DATE)),
                builder.asc(root.get(TmpQueuePrintout.COLUMN_NAME_REQUEST_SEQ)));
        TypedQuery<TmpQueuePrintout> typedQuery = em.createQuery(query);
        List<TmpQueuePrintout> result = typedQuery.getResultList();

        if (Objects.isNull(result) || result.isEmpty()) {
            return null;
        }

        return result.get(0);

    }

    /**
     * 印字キュー 登録
     * @param item 印字キュー
     * @param em 独自管理エンティティマネージャー
     */
    @TraceMonitor
    @ErrorHandling
    public void insert(TmpQueuePrintout item, EntityManager em) {

        if (!Objects.isNull(item)) {
            // 登録
            item.setReq(this.req);
            em.persist(item);
            em.flush();

        }

    }

    /**
     * 印字キュー 登録(Bulk)
     * @param item 印字キュー
     * @param em 独自管理エンティティマネージャー
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void bulkPersist(List<TmpQueuePrintout> item, EntityManager em) throws GnomesAppException {

        if (!Objects.isNull(item)) {
            // 登録
        	jdbcAccessDao.bulkPersist(em, item);

        }
        em.flush();
    }

    /**
     * 印字キュー削除
     * @param item 印字キュー
     * @param em 独自管理エンティティマネージャー
     */
    @TraceMonitor
    @ErrorHandling
    public void delete(TmpQueuePrintout item, EntityManager em) {


        if (!Objects.isNull(item)) {
            // 削除
            em.remove(item);
            em.flush();

        }

    }

}
