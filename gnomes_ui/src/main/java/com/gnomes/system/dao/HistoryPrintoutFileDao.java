package com.gnomes.system.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.picketbox.util.StringUtil;
import com.gnomes.common.dao.BaseDao;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.system.entity.HistoryPrintoutFile;

/**
 * 帳票ラベル印字履歴ファイル Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/07/13 KCC/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class HistoryPrintoutFileDao extends BaseDao implements Serializable {

    /** コンストラクタ */
    public HistoryPrintoutFileDao() {

    }

    /**
     * 帳票ラベル印字履歴ファイル情報取得.
     * <pre>
     * 帳票ラベル印字履歴キーを基に、帳票ラベル印字履歴ファイル情報の取得を行う。
     * </pre>
     * @param historyPrintoutKey 帳票ラベル印字履歴キー
     * @param em 独自管理エンティティマネージャー
     * @return 帳票ラベル印字履歴ファイル情報
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public HistoryPrintoutFile getHistoryPrintoutFile(
            String historyPrintoutKey, EntityManager em) throws GnomesAppException {

        // パラメータチェック
        if (StringUtil.isNullOrEmpty(historyPrintoutKey)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            HistoryPrintoutFile.COLUMN_NAME_HISTORY_PRINTOUT_KEY, historyPrintoutKey)});

        }

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<HistoryPrintoutFile> query = builder.createQuery(HistoryPrintoutFile.class);
        Root<HistoryPrintoutFile> root = query.from(HistoryPrintoutFile.class);

        // 条件設定
        List<Predicate> predicate = new ArrayList<>();
        // 帳票ラベル印字履歴キー
        predicate.add(builder.equal(
                root.get(HistoryPrintoutFile.COLUMN_NAME_HISTORY_PRINTOUT_KEY), historyPrintoutKey));

        query.where(builder.and(predicate.toArray(new Predicate[predicate.size()])));

        TypedQuery<HistoryPrintoutFile> typedQuery = em.createQuery(query);
        List<HistoryPrintoutFile> result = typedQuery.getResultList();

        if (Objects.isNull(result) || result.isEmpty()) {
            return null;
        }

        return result.get(0);

    }

    /**
     * 帳票ラベル印字履歴ファイル情報取得.
     * <pre>
     * 再印字ソース要求イベントIDと再印字ソース要求内連番を基に、帳票ラベル印字履歴ファイル情報の取得を行う。
     * </pre>
     * @param reprintEventId 再印字ソース要求イベントID
     * @param reprintRequestSeq 再印字ソース要求内連番
     * @param em 独自管理エンティティマネージャー
     * @return 帳票ラベル印字履歴ファイル情報
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public HistoryPrintoutFile getHistoryPrintoutFile(
            String reprintEventId, Integer reprintRequestSeq, EntityManager em) throws GnomesAppException {

        // パラメータチェック
        if (StringUtil.isNullOrEmpty(reprintEventId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            HistoryPrintoutFile.COLUMN_NAME_EVENT_ID, reprintEventId)});

        }
        if (Objects.isNull(reprintRequestSeq)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            HistoryPrintoutFile.COLUMN_NAME_REQUEST_SEQ, reprintRequestSeq)});

        }

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<HistoryPrintoutFile> query = builder.createQuery(HistoryPrintoutFile.class);
        Root<HistoryPrintoutFile> root = query.from(HistoryPrintoutFile.class);

        // 条件設定
        List<Predicate> predicate = new ArrayList<>();
        // 再印字ソース要求イベントID
        predicate.add(builder.equal(
                root.get(HistoryPrintoutFile.COLUMN_NAME_EVENT_ID), reprintEventId));
        // 再印字ソース要求内連番
        predicate.add(builder.equal(
                root.get(HistoryPrintoutFile.COLUMN_NAME_REQUEST_SEQ), reprintRequestSeq));

        query.where(builder.and(predicate.toArray(new Predicate[predicate.size()])));

        TypedQuery<HistoryPrintoutFile> typedQuery = em.createQuery(query);
        List<HistoryPrintoutFile> result = typedQuery.getResultList();

        if (Objects.isNull(result) || result.isEmpty()) {
            return null;
        }

        return result.get(0);

    }
    /**
     * 帳票ラベル印字履歴ファイル 登録
     * @param item 帳票ラベル印字履歴ファイル
     * @param em 独自管理エンティティマネージャー
     */
    @TraceMonitor
    @ErrorHandling
    public void insert(HistoryPrintoutFile item, EntityManager em) {

        if (item != null) {
            // 登録
            item.setReq(super.req);
            em.persist(item);
            em.flush();

        }

    }

}
