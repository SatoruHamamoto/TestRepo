package com.gnomes.system.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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

import com.gnomes.common.constants.CommonEnums;
import com.gnomes.common.dao.BaseDao;
import com.gnomes.common.dao.JdbcAccessDao;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.system.entity.TmpQueuePrintPreview;

/**
 * 印刷プレビューキュー Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2021/12/02 YJP-D/Jixin.Sun           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class TmpQueuePrintPreviewDao extends BaseDao implements Serializable
{

    @Inject
    JdbcAccessDao jdbcAccessDao;

    /**
     * コンストラクタ.
     */
    public TmpQueuePrintPreviewDao()
    {

    }

    /**
     * 印刷プレビューキュー取得.
     * <pre>
     * 帳票印刷日時が最も古い印刷キューを1件取得する。
     * 取得できなかった場合、<code>null</code>を返却
     * </pre>
     * @param em 独自管理エンティティマネージャー
     * @return 印刷キュー
     */
    @TraceMonitor
    @ErrorHandling
    public TmpQueuePrintPreview getQueue(Date expiredDate, EntityManager em)
    {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<TmpQueuePrintPreview> query = builder.createQuery(TmpQueuePrintPreview.class);
        Root<TmpQueuePrintPreview> root = query.from(TmpQueuePrintPreview.class);
        List<Predicate> listOr = new ArrayList<>();
        listOr.add(builder.equal(root.get("print_preview_status"),
                CommonEnums.PrintPreviewStatus.PREVIEW_UNTREATED.getValue()));
        listOr.add(builder.equal(root.get("print_preview_status"),
                CommonEnums.PrintPreviewStatus.PRINT_UNTREATED.getValue()));
        listOr.add(builder.equal(root.get("preview_display_status"),
                CommonEnums.PreviewDisplayStatus.CLOSED.getValue()));
        listOr.add(builder.lessThan(root.get("first_regist_datetime"), expiredDate));
        query.where(builder.or(listOr.toArray(new Predicate[listOr.size()])));

        query.orderBy(builder.asc(root.get(TmpQueuePrintPreview.COLUMN_NAME_PRINT_PREVIEW_DATE)), builder.asc(root.get(
                TmpQueuePrintPreview.COLUMN_NAME_REQUEST_SEQ)));
        TypedQuery<TmpQueuePrintPreview> typedQuery = em.createQuery(query);
        List<TmpQueuePrintPreview> result = typedQuery.getResultList();

        if (Objects.isNull(result) || result.isEmpty()) {
            return null;
        }

        return result.get(0);

    }

    /**
     * 印刷プレビューキュー 登録
     * @param item 印刷プレビューキュー
     * @param em 独自管理エンティティマネージャー
     */
    @TraceMonitor
    @ErrorHandling
    public void insert(TmpQueuePrintPreview item, EntityManager em)
    {

        if (!Objects.isNull(item)) {
            // 登録
            item.setReq(this.req);
            em.persist(item);
            em.flush();

        }

    }

    /**
     * 印刷プレビューキュー 登録(Bulk)
     * @param item 印刷プレビューキュー
     * @param em 独自管理エンティティマネージャー
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void bulkPersist(List<TmpQueuePrintPreview> item, EntityManager em) throws GnomesAppException
    {

        if (!Objects.isNull(item)) {
            // 登録
            jdbcAccessDao.bulkPersist(em, item);

        }
        em.flush();
    }

    /**
     * 印刷プレビューキュー削除
     * @param item 印刷プレビューキュー
     * @param em 独自管理エンティティマネージャー
     */
    @TraceMonitor
    @ErrorHandling
    public void delete(TmpQueuePrintPreview item, EntityManager em)
    {

        if (!Objects.isNull(item)) {
            // 削除
            em.remove(item);
            em.flush();

        }

    }

    /**
     * 印刷プレビューキュー 更新
     * @param item 印刷プレビューキュー
     * @param em 独自管理エンティティマネージャー
     */
    @TraceMonitor
    @ErrorHandling
    public void update(TmpQueuePrintPreview item, EntityManager em)
    {

        if (item != null) {
            // 更新
            item.setReq(super.req);
            em.flush();

        }

    }

    /**
     * 印刷プレビューキュー取得.
     * <pre>
     * 帳票印刷日時が最も古い印刷キューを1件取得する。
     * 取得できなかった場合、<code>null</code>を返却
     * </pre>
     * @param em 独自管理エンティティマネージャー
     * @return 印刷プレビューキュー
     */
    @TraceMonitor
    @ErrorHandling
    public TmpQueuePrintPreview getQueueState(String queuePrintPreviewKey, EntityManager em)
    {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<TmpQueuePrintPreview> query = builder.createQuery(TmpQueuePrintPreview.class);
        Root<TmpQueuePrintPreview> root = query.from(TmpQueuePrintPreview.class);
        // 条件設定
        List<Predicate> predicate = new ArrayList<>();
        // ReportId
        predicate.add(builder.equal(root.get(TmpQueuePrintPreview.COLUMN_NAME_QUEUE_PRINT_PREVIEW_KEY),
                queuePrintPreviewKey));

        query.where(builder.and(predicate.toArray(new Predicate[predicate.size()])));

        query.orderBy(builder.asc(root.get(TmpQueuePrintPreview.COLUMN_NAME_PRINT_PREVIEW_DATE)), builder.asc(root.get(
                TmpQueuePrintPreview.COLUMN_NAME_REQUEST_SEQ)));

        TypedQuery<TmpQueuePrintPreview> typedQuery = em.createQuery(query);
        List<TmpQueuePrintPreview> result = typedQuery.getResultList();

        if (Objects.isNull(result) || result.isEmpty()) {
            return null;
        }

        return result.get(0);

    }
}
