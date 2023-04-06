package com.gnomes.system.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.GnomesQueryConstants;
import com.gnomes.common.dao.BaseDao;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.system.entity.HistoryPrintout;

/**
 * 帳票ラベル印字履歴 Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/09/07 KCC/A.Oomori                初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class HistoryPrintoutDao extends BaseDao implements Serializable {


    protected static final String PROCESSING = "_processing";                         //sqlパラメーター名追加部

    /**
     * コンストラクタ.
     */
    public HistoryPrintoutDao() {

    }

    /**
     * 帳票ラベル印字履歴取得
     * @param reportId ReportId
     * @param em 独自管理エンティティマネージャー
     * @return 帳票ラベル印字履歴
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public HistoryPrintout getConditionReportId(String reportId, EntityManager em) throws GnomesAppException {

        if (StringUtil.isNullOrEmpty(reportId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            HistoryPrintout.COLUMN_NAME_REPORT_ID, reportId)});

        }

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<HistoryPrintout> query = builder.createQuery(HistoryPrintout.class);
        Root<HistoryPrintout> root = query.from(HistoryPrintout.class);

        // 条件設定
        List<Predicate> predicate = new ArrayList<>();
        // ReportId
        predicate.add(builder.equal(
                root.get(HistoryPrintout.COLUMN_NAME_REPORT_ID), reportId));

        query.where(builder.and(predicate.toArray(new Predicate[predicate.size()])));

        TypedQuery<HistoryPrintout> typedQuery = em.createQuery(query);
        List<HistoryPrintout> result = typedQuery.getResultList();

        if (Objects.isNull(result) || result.isEmpty()) {
            return null;
        }

        return result.get(0);

    }

    /**
     * 帳票ラベル印字履歴再印字回数取得
     * @param reprintEventId 再印字ソース要求イベントID
     * @param reprintRequestSeq 再印字ソース要求内連番
     * @param em 独自管理エンティティマネージャー
     * @return 帳票ラベル印字履歴再印字回数
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public Integer getPrintoutNum(String reprintEventId, Integer reprintRequestSeq, EntityManager em) throws GnomesAppException {

        if (StringUtil.isNullOrEmpty(reprintEventId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            HistoryPrintout.COLUMN_NAME_REPORT_ID, reprintEventId)});

        }

        if (Objects.isNull(reprintRequestSeq)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            HistoryPrintout.COLUMN_NAME_REPORT_ID, reprintRequestSeq)});

        }

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<HistoryPrintout> query = builder.createQuery(HistoryPrintout.class);
        Root<HistoryPrintout> root = query.from(HistoryPrintout.class);

        // 条件設定
        List<Predicate> predicate = new ArrayList<>();
        // ReportId
        predicate.add(builder.equal(
                root.get(HistoryPrintout.COLUMN_NAME_REPRINT_SOURCE_EVENT_ID), reprintEventId));
        // ReportId
        predicate.add(builder.equal(
                root.get(HistoryPrintout.COLUMN_NAME_REPRINT_SOURCE_REQUEST_SEQ), reprintRequestSeq));

        query.where(builder.and(predicate.toArray(new Predicate[predicate.size()])));
        query.orderBy(builder.desc(root.get(HistoryPrintout.COLUMN_NAME_PRINTOUT_NUM)));

        TypedQuery<HistoryPrintout> typedQuery = em.createQuery(query);
        List<HistoryPrintout> result = typedQuery.getResultList();

        if (Objects.isNull(result) || result.isEmpty()) {
            return 0;
        }

        return result.get(0).getPrintout_num() + 1;

    }

    /**
     * 帳票ラベル印字履歴 登録
     * @param item 帳票ラベル印字履歴
     * @param em 独自管理エンティティマネージャー
     */
    @TraceMonitor
    @ErrorHandling
    public void insert(HistoryPrintout item, EntityManager em) {

        if (item != null) {
            // 登録
            item.setReq(super.req);
            em.persist(item);
            em.flush();

        }

    }

    /**
     * 帳票ラベル印字履歴 更新
     * @param item 帳票ラベル印字履歴
     * @param em 独自管理エンティティマネージャー
     */
    @TraceMonitor
    @ErrorHandling
    public void update(HistoryPrintout item, EntityManager em) {

        if (item != null) {
            // 更新
            item.setReq(super.req);
            em.flush();

        }

    }

    /**
     * 帳票ラベル印字履歴 更新
     * @param item 帳票ラベル印字履歴
     * @param em 独自管理エンティティマネージャー
     */
    @TraceMonitor
    @ErrorHandling
    public void executeUpdate(int printoutStatusError, int printoutStatusProcessing,
            String errMessage, Date printoutStatusProcTimeoutDatetime, EntityManager eml) throws GnomesAppException {

        // 更新イベントID
        String eventId = UUID.randomUUID().toString();
        Integer result = 0;

        TypedQuery<String> queryHistoryPrintout =
                eml.createNamedQuery(
                        GnomesQueryConstants.QUERY_NAME_UPDATE_PRINTOUT_STATUS_TIMEOUT_ERROR, String.class);
        queryHistoryPrintout.setParameter(HistoryPrintout.COLUMN_NAME_PRINTOUT_STATUS, printoutStatusError);
        queryHistoryPrintout.setParameter(HistoryPrintout.COLUMN_NAME_PRINTOUT_ERROR_MESSAGE, errMessage);
        queryHistoryPrintout.setParameter(HistoryPrintout.COLUMN_NAME_LAST_REGIST_EVENT_ID, eventId);
        queryHistoryPrintout.setParameter(HistoryPrintout.COLUMN_NAME_LAST_REGIST_USER_NUMBER, CommonConstants.USERID_SYSTEM);
        queryHistoryPrintout.setParameter(HistoryPrintout.COLUMN_NAME_LAST_REGIST_USER_NAME, CommonConstants.USERNAME_SYSTEM);
        queryHistoryPrintout.setParameter(HistoryPrintout.COLUMN_NAME_LAST_REGIST_DATETIME, new Date(), TemporalType.DATE);
        queryHistoryPrintout.setParameter(HistoryPrintout.COLUMN_NAME_VERSION, 1);
        queryHistoryPrintout.setParameter(HistoryPrintout.COLUMN_NAME_PRINTOUT_STATUS.concat(PROCESSING), printoutStatusProcessing);
        queryHistoryPrintout.setParameter(HistoryPrintout.COLUMN_NAME_FIRST_REGIST_DATETIME, printoutStatusProcTimeoutDatetime, TemporalType.DATE);
        result = queryHistoryPrintout.executeUpdate();

        // 更新
        eml.flush();
    }

    /**
     * 帳票ラベル印字履歴存在チェック
     * @param eventId nk要求イベントID
     * @param requestSeq nk要求内連番
     * @param em 独自管理エンティティマネージャー
     * @return boolean 帳票ラベル印字履歴が存在するか
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public boolean isHistoryPrintExisted(String eventId, Integer requestSeq, EntityManager em) throws GnomesAppException
    {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<HistoryPrintout> query = builder.createQuery(HistoryPrintout.class);
        Root<HistoryPrintout> root = query.from(HistoryPrintout.class);

        // 条件設定
        List<Predicate> predicate = new ArrayList<>();
        // ReportId
        predicate.add(builder.equal(root.get(HistoryPrintout.COLUMN_NAME_REPRINT_SOURCE_EVENT_ID), eventId));
        // ReportId
        predicate.add(builder.equal(root.get(HistoryPrintout.COLUMN_NAME_REPRINT_SOURCE_REQUEST_SEQ), requestSeq));

        query.where(builder.and(predicate.toArray(new Predicate[predicate.size()])));
        query.orderBy(builder.desc(root.get(HistoryPrintout.COLUMN_NAME_PRINTOUT_NUM)));

        TypedQuery<HistoryPrintout> typedQuery = em.createQuery(query);
        List<HistoryPrintout> result = typedQuery.getResultList();

        if (Objects.isNull(result) || result.isEmpty()) {
            return false;
        }

        return true;

    }
}
