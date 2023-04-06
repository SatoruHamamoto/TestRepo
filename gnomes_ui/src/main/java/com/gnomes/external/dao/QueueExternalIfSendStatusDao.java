package com.gnomes.external.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PessimisticLockException;
import javax.persistence.TypedQuery;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.GnomesQueryConstants;
import com.gnomes.common.dao.BaseDao;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.util.PessimisticLockSession;
import com.gnomes.external.entity.QueueExternalIfSendStatus;

/**
 * 外部I/F送信状態キュー Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/09/07 KCC/A.Oomori                初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class QueueExternalIfSendStatusDao extends BaseDao implements Serializable {

    /**
     * コンストラクタ.
     */
    public QueueExternalIfSendStatusDao() {

    }

    /**
     * 外部I/F送信状態キュー(ロック)取得
     * @param externalIfSendStatusKey 外部I/F送信状態キー
     * @param lockSession 悲観ロックセッション
     * @return 外部I/F送信状態キュー
     */
    @TraceMonitor
    @ErrorHandling
    public QueueExternalIfSendStatus getExternalIfSendStatusLock(
            String externalIfSendStatusKey, PessimisticLockSession lockSession) throws GnomesAppException {

        // パラメータチェック
        if (StringUtil.isNullOrEmpty(externalIfSendStatusKey)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            QueueExternalIfSendStatus.COLUMN_NAME_EXTERNAL_IF_SEND_STATUS_KEY, externalIfSendStatusKey)});

        }

        try {
            // 1件取得 行ロック(待機時間なし)
            Object object = lockSession.lock(QueueExternalIfSendStatus.class, externalIfSendStatusKey);
            // 取得できた場合
            if (!Objects.isNull(object)) {

                return (QueueExternalIfSendStatus) object;

            }

        } catch (PessimisticLockException e) {
            return null;
        }

        return null;

    }

    /**
     * 外部I/F送信状態キュー 取得
     * @param sendStatus 送信状態
     * @param externalIfTargetCode 外部I/F対象システムコード
     * @param em エンティティマネージャー
     * @return 外部I/F送信状態キュー
     */
    @TraceMonitor
    @ErrorHandling
    public List<QueueExternalIfSendStatus> getSendStateQuery(
            Integer sendStatus, String externalIfTargetCode, EntityManager em) throws GnomesAppException {

        // パラメータチェック
        if (Objects.isNull(sendStatus)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            QueueExternalIfSendStatus.COLUMN_NAME_SEND_STATUS, sendStatus)});

        }

        // パラメータチェック
        if (StringUtil.isNullOrEmpty(externalIfTargetCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            QueueExternalIfSendStatus.COLUMN_NAME_EXTERNAL_IF_TARGET_CODE, externalIfTargetCode)});

        }

        // 外部I/F送信状態キュー 取得
        TypedQuery<QueueExternalIfSendStatus> querySendState =
                em.createNamedQuery(GnomesQueryConstants.QUERY_NAME_QUEUE_EXTERNAL_IF_SEND_STATUS, QueueExternalIfSendStatus.class);
        querySendState.setParameter(QueueExternalIfSendStatus.COLUMN_NAME_SEND_STATUS, sendStatus);
        querySendState.setParameter(QueueExternalIfSendStatus.COLUMN_NAME_EXTERNAL_IF_TARGET_CODE, externalIfTargetCode);

        return querySendState.getResultList();

    }

    /**
     * 外部I/F送信状態キュー 取得
     * @param sendStatus 送信状態
     * @param externalIfTargetCode 外部I/F対象システムコード
     * @param em エンティティマネージャー
     * @return 外部I/F送信状態キュー
     */
    @TraceMonitor
    @ErrorHandling
    public List<QueueExternalIfSendStatus> getSendStateQuery(
            List<Integer> sendStatus, String externalIfTargetCode, EntityManager em) throws GnomesAppException {

        // パラメータチェック
        if (Objects.isNull(sendStatus)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            QueueExternalIfSendStatus.COLUMN_NAME_SEND_STATUS, sendStatus)});

        }

        // パラメータチェック
        if (StringUtil.isNullOrEmpty(externalIfTargetCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            QueueExternalIfSendStatus.COLUMN_NAME_EXTERNAL_IF_TARGET_CODE, externalIfTargetCode)});

        }

        // 外部I/F送信状態キュー 取得
        TypedQuery<QueueExternalIfSendStatus> querySendState =
                em.createNamedQuery(GnomesQueryConstants.QUERY_NAME_QUEUE_EXTERNAL_IF_SEND_STATUS, QueueExternalIfSendStatus.class);
        querySendState.setParameter(QueueExternalIfSendStatus.COLUMN_NAME_SEND_STATUS, sendStatus);
        querySendState.setParameter(QueueExternalIfSendStatus.COLUMN_NAME_EXTERNAL_IF_TARGET_CODE, externalIfTargetCode);

        return querySendState.getResultList();

    }

    /**
     * 外部I/F送信状態キュー 登録
     * @param item 外部I/F送信状態キュー
     * @param em エンティティマネージャー
     */
    @TraceMonitor
    @ErrorHandling
    public void insert(QueueExternalIfSendStatus item, EntityManager em) {

        // 登録
        super.persist(em, item);

    }

    /**
     * 外部I/F送信状態キュー 更新
     * @param item 外部I/F送信状態キュー
     * @param em エンティティマネージャー
     */
    @TraceMonitor
    @ErrorHandling
    public void update(QueueExternalIfSendStatus item, EntityManager em) {

        // 更新
        super.update(em, item);

    }

    /**
     * 外部I/F送信状態キュー 削除
     * @param item 外部I/F送信状態キュー
     * @param eml 独自管理エンティティマネージャー
     */
    @TraceMonitor
    @ErrorHandling
    public void delete(QueueExternalIfSendStatus item, EntityManager em) {

        // 削除
        super.remove(em, item);

    }

}
