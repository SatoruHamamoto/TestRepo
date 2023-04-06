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
import com.gnomes.system.entity.TmpQueuePrintPreviewParameter;

/**
 * 印刷プレビューキューパラメータ Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2021/12/02 YJP-D/Jixin.Sun           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class TmpQueuePrintPreviewParameterDao extends BaseDao implements Serializable
{

    @Inject
    JdbcAccessDao jdbcAccessDao;

    /**
     * コンストラクター
     */
    public TmpQueuePrintPreviewParameterDao()
    {

    }

    /**
     * 印刷プレビューキューパラメータ取得
     * @param em 独自管理エンティティマネージャー
     * @param queuePrintoutKey nk印刷プレビューキューキー (FK)
     * @return
     */
    @TraceMonitor
    @ErrorHandling
    public List<TmpQueuePrintPreviewParameter> getParameter(EntityManager em, String queuePrintPreviewKey)
    {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<TmpQueuePrintPreviewParameter> query = builder.createQuery(TmpQueuePrintPreviewParameter.class);
        Root<TmpQueuePrintPreviewParameter> root = query.from(TmpQueuePrintPreviewParameter.class);

        List<Predicate> preds = new ArrayList<>();
        preds.add(builder.equal(root.get(TmpQueuePrintPreviewParameter.COLUMN_NAME_QUEUE_PRINT_PREVIEW_KEY),
                queuePrintPreviewKey));

        query.where(builder.and(preds.toArray(new Predicate[]{})));
        query.orderBy(builder.asc(root.get(TmpQueuePrintPreviewParameter.COLUMN_NAME_PARAMETER_NAME)));
        TypedQuery<TmpQueuePrintPreviewParameter> typedQuery = em.createQuery(query);
        List<TmpQueuePrintPreviewParameter> result = typedQuery.getResultList();

        return result;
    }

    /**
     * 印刷プレビューキューパラメータ登録
     * @param items 印刷プレビューキューパラメータ
     * @param em 独自管理エンティティマネージャー
     */
    @TraceMonitor
    @ErrorHandling
    public void insert(List<TmpQueuePrintPreviewParameter> items, EntityManager em)
    {
        for (TmpQueuePrintPreviewParameter item : items) {
            // 登録
            item.setReq(super.req);
            em.persist(item);
        }
        em.flush();
    }

    /**
     * 印刷プレビューキューパラメータ 登録(Bulk)
     * @param items 印刷プレビューキューパラメータ
     * @param em 独自管理エンティティマネージャー
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void bulkPersist(List<TmpQueuePrintPreviewParameter> items, EntityManager em) throws GnomesAppException
    {

        if (!Objects.isNull(items)) {
            // 登録
            jdbcAccessDao.bulkPersist(em, items);

        }
        em.flush();
    }

    /**
     * 印刷プレビューキューパラメータ削除
     * @param items 印刷プレビューキューパラメータ
     * @param em 独自管理エンティティマネージャー
     */
    @TraceMonitor
    @ErrorHandling
    public void delete(List<TmpQueuePrintPreviewParameter> items, EntityManager em)
    {
        for (TmpQueuePrintPreviewParameter item : items) {
            // 登録
            item.setReq(super.req);
            em.remove(item);
        }
        em.flush();
    }
}
