package com.gnomes.external.dao;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonEnums.ExternalIfSendRecvClearFlag;
import com.gnomes.common.constants.CommonEnums.SendRecvType;
import com.gnomes.common.dao.BaseDao;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.external.entity.ExtIfDataSrActual;

/**
 * 外部I/Fデータファイル送受信実績 Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/09/07 KCC/A.Oomori                初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class ExtIfDataSrActualDao extends BaseDao implements Serializable {

    /**
     * コンストラクタ.
     */
    public ExtIfDataSrActualDao() {

    }

    /**
     * 外部I/Fデータファイル送受信実績取得.
     * <pre>
     * 外部I/Fデータファイル送受信実績の取得を行う。
     * クリアフラグが未設定の場合、取得条件対象外
     * </pre>
     * @param externalIfSendRecvQueueKey 外部I/F送受信キューキー
     * @param sendRecvType 送受信区分
     * @param clearFlag クリアフラグ(設定値以外)
     * @param em エンティティマネージャー
     * @return 外部I/Fデータファイル送受信実績
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public ExtIfDataSrActual getExternalIfDataSendRecvActual(String externalIfSendRecvQueueKey,
            SendRecvType sendRecvType, ExternalIfSendRecvClearFlag clearFlag, EntityManager em) throws GnomesAppException {

        // パラメータチェック
        // 外部I/F送受信キューキー
        if (StringUtil.isNullOrEmpty(externalIfSendRecvQueueKey)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            ExtIfDataSrActual.COLUMN_NAME_EXTERNAL_IF_SEND_RECV_QUEUE_KEY, externalIfSendRecvQueueKey)});

        }
        // 送受信区分
        if (Objects.isNull(sendRecvType)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            ExtIfDataSrActual.COLUMN_NAME_SEND_RECV_TYPE, sendRecvType)});

        }

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<ExtIfDataSrActual> query = builder.createQuery(ExtIfDataSrActual.class);
        Root<ExtIfDataSrActual> root = query.from(ExtIfDataSrActual.class);

        // 条件設定
        List<Predicate> predicate = new ArrayList<>();
        // 外部I/F送受信キューキー
        predicate.add(builder.equal(
                root.get(ExtIfDataSrActual.COLUMN_NAME_EXTERNAL_IF_SEND_RECV_QUEUE_KEY),
                externalIfSendRecvQueueKey));
        // 送受信区分
        predicate.add(builder.equal(
                root.get(ExtIfDataSrActual.COLUMN_NAME_SEND_RECV_TYPE),
                sendRecvType.getValue()));
        // クリアフラグ
        if (!Objects.isNull(clearFlag)) {
            predicate.add(builder.notEqual(
                    root.get(ExtIfDataSrActual.COLUMN_NAME_CLEAR_FLAG),
                    clearFlag.getValue()));

        }
        query.where(builder.and(predicate.toArray(new Predicate[predicate.size()])));

        TypedQuery<ExtIfDataSrActual> typedQuery = em.createQuery(query);
        List<ExtIfDataSrActual> result = typedQuery.getResultList();

        if (Objects.isNull(result) || result.isEmpty()) {

            return null;

        }
        return result.get(0);

    }

    /**
     * 外部I/Fデータファイル送受信実績 登録
     * @param item 外部I/Fデータファイル送受信実績
     * @param eml エンティティマネージャー
     */
    @TraceMonitor
    @ErrorHandling
    public void insert(ExtIfDataSrActual item, EntityManager em) {

        super.persist(em, item);

    }

    /**
     * 外部I/Fデータファイル送受信実績 登録
     * @param item 外部I/Fデータファイル送受信実績
     * @param em エンティティマネージャー
     */
    @TraceMonitor
    @ErrorHandling
    public void insert(ExtIfDataSrActual[] item, EntityManager em) {

        super.persist(em, item);

    }

    /**
     * 外部I/Fデータファイル送受信実績 登録
     * @param item 外部I/Fデータファイル送受信実績
     * @param em エンティティマネージャー
     */
    @TraceMonitor
    @ErrorHandling
    public void update(ExtIfDataSrActual item, EntityManager em) {

        super.update(em, item);

    }

    /**
     * 外部I/Fデータファイル送受信実績 登録
     * @param item 外部I/Fデータファイル送受信実績
     * @param em エンティティマネージャー
     */
    @TraceMonitor
    @ErrorHandling
    public void update(ExtIfDataSrActual[] item, EntityManager em) {

        super.update(em, item);

    }

    /**
     * 外部I/Fデータファイル送受信実績 削除
     * @param item 外部I/Fデータファイル送受信実績
     * @param em エンティティマネージャー
     */
    @TraceMonitor
    @ErrorHandling
    public void detele(ExtIfDataSrActual item, EntityManager em) {

        super.remove(em, item);

    }

    /**
     * 外部I/Fデータファイル送受信実績 削除
     * @param item 外部I/Fデータファイル送受信実績
     * @param em エンティティマネージャー
     */
    @TraceMonitor
    @ErrorHandling
    public void detele(ExtIfDataSrActual[] item, EntityManager em) {

        super.remove(em, item);

    }

}
