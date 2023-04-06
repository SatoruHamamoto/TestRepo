package com.gnomes.system.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import com.gnomes.common.dao.BaseDao;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.system.entity.HistoryPrintoutParameter;

/**
 * 帳票ラベル印字履歴パラメータ Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/11/19 KCC/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class HistoryPrintoutParameterDao extends BaseDao implements Serializable {


    /**
     * コンストラクター
     */
    public HistoryPrintoutParameterDao() {

    }

	/**
	 * 帳票ラベル印字履歴パラメータ取得
	 * @param em 独自管理エンティティマネージャー
	 * @param historyPrintoutKey nk帳票ラベル印字履歴キー (FK)
	 * @return
	 */
    @TraceMonitor
    @ErrorHandling
    public List<HistoryPrintoutParameter> getParameter(EntityManager em, String historyPrintoutKey ) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<HistoryPrintoutParameter> query = builder.createQuery(HistoryPrintoutParameter.class);
        Root<HistoryPrintoutParameter> root = query.from(HistoryPrintoutParameter.class);

        List<Predicate> preds = new ArrayList<>();
        preds.add(builder.equal(root.get(HistoryPrintoutParameter.COLUMN_NAME_HISTORY_PRINTOUT_KEY),
        		historyPrintoutKey));

        query.where(builder.and(preds.toArray(new Predicate[]{})));
        query.orderBy(builder.asc(root.get(HistoryPrintoutParameter.COLUMN_NAME_PARAMETER_NAME)));
        TypedQuery<HistoryPrintoutParameter> typedQuery = em.createQuery(query);
        List<HistoryPrintoutParameter> result = typedQuery.getResultList();

        return result;
    }

	/**
	 * 帳票ラベル印字履歴パラメータ取得
	 * @param em 独自管理エンティティマネージャー
     * @param reprintEventId 再印字ソース要求イベントID
     * @param reprintRequestSeq 再印字ソース要求内連番
	 * @return
	 */
    @TraceMonitor
    @ErrorHandling
    public List<HistoryPrintoutParameter> getParameter(EntityManager em, String reprintEventId, Integer reprintRequestSeq ) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<HistoryPrintoutParameter> query = builder.createQuery(HistoryPrintoutParameter.class);
        Root<HistoryPrintoutParameter> root = query.from(HistoryPrintoutParameter.class);

        List<Predicate> preds = new ArrayList<>();
        preds.add(builder.equal(root.get(HistoryPrintoutParameter.COLUMN_NAME_EVENT_ID),
        		reprintEventId));
        preds.add(builder.equal(root.get(HistoryPrintoutParameter.COLUMN_NAME_REQUEST_SEQ),
        		reprintRequestSeq));

        query.where(builder.and(preds.toArray(new Predicate[]{})));
        query.orderBy(builder.asc(root.get(HistoryPrintoutParameter.COLUMN_NAME_PARAMETER_NAME)));
        TypedQuery<HistoryPrintoutParameter> typedQuery = em.createQuery(query);
        List<HistoryPrintoutParameter> result = typedQuery.getResultList();

        return result;
    }

    /**
     * 帳票ラベル印字履歴パラメータ 登録
     * @param items 帳票ラベル印字履歴パラメータ
     * @param em 独自管理エンティティマネージャー
     */
    @TraceMonitor
    @ErrorHandling
    public void insert(List<HistoryPrintoutParameter>items, EntityManager em) {

        for(HistoryPrintoutParameter item : items) {
            // 登録
            item.setReq(super.req);
            em.persist(item);
        }
        em.flush();
    }
}
