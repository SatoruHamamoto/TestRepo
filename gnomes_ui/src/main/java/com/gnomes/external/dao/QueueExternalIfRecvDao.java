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
import com.gnomes.external.entity.QueueExternalIfRecv;

/**
 * 外部I/F受信キュー Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/09/15 KCC/T.Kamizuru                初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class QueueExternalIfRecvDao extends BaseDao implements Serializable {

    /**
     * コンストラクタ.
     */
    public QueueExternalIfRecvDao() {

    }

    /**
     * 外部I/F受信キュー(ロック)取得
     * @param queueExternalIfRecvKey 外部I/F受信キューキー
     * @param lockSession 悲観ロックセッション
     * @return List <QueueExternalIfRecv> 外部I/F受信キュー
     */
    @TraceMonitor
    @ErrorHandling
    public QueueExternalIfRecv getExternalIfRecvQueQueryLock(
            String queueExternalIfRecvKey, PessimisticLockSession lockSession) throws GnomesAppException {

        // パラメータチェック
        if (StringUtil.isNullOrEmpty(queueExternalIfRecvKey)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            QueueExternalIfRecv.COLUMN_NAME_QUEUE_EXTERNAL_IF_RECV_KEY, queueExternalIfRecvKey)});

        }

        try {
            // 1件取得 行ロック(待機時間なし)
            Object object = lockSession.lock(QueueExternalIfRecv.class, queueExternalIfRecvKey);
            // 取得できた場合
            if (!Objects.isNull(object)) {

                return (QueueExternalIfRecv) object;

            }

        } catch (PessimisticLockException e) {
            return null;
        }

        return null;

    }

    /**
     * 外部I/F受信キュー 取得
     * @param recvState 受信状態
     * @param externalIfTargetCode 外部IF対象システムコード
     * @param em エンティティマネージャー
     * @return X400:外部I/F受信キュー
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<QueueExternalIfRecv> getExternalIfRecvQueQuery(Integer recvState,
            String externalIfTargetCode, EntityManager em) throws GnomesAppException {

        // パラメータチェック
        if (Objects.isNull(recvState)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            QueueExternalIfRecv.COLUMN_NAME_RECV_STATUS, recvState)});

        }

        // パラメータチェック
        if (StringUtil.isNullOrEmpty(externalIfTargetCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            QueueExternalIfRecv.COLUMN_NAME_EXTERNAL_IF_TARGET_CODE, externalIfTargetCode)});

        }

        // 外部I/F受信キュー 取得
        TypedQuery<QueueExternalIfRecv> queryRecvState =
                em.createNamedQuery(GnomesQueryConstants.QUERY_NAME_QUEUE_EXTERNAL_IF_RECV, QueueExternalIfRecv.class);
        queryRecvState.setParameter(QueueExternalIfRecv.COLUMN_NAME_RECV_STATUS, recvState);
        queryRecvState.setParameter(QueueExternalIfRecv.COLUMN_NAME_EXTERNAL_IF_TARGET_CODE, externalIfTargetCode);
        List<QueueExternalIfRecv> datasExternalIfRecvQue = queryRecvState.getResultList();

        return datasExternalIfRecvQue;

    }

    /**
     * 外部I/F受信キュー 取得
     * @param recvState 受信状態
     * @param externalIfTargetCode 外部IF対象システムコード
     * @param em エンティティマネージャー
     * @return X400:外部I/F受信キュー
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<QueueExternalIfRecv> getExternalIfRecvQueQuery(List<Integer> recvStatus,
            String externalIfTargetCode, EntityManager em) throws GnomesAppException {

        // パラメータチェック
        if (Objects.isNull(recvStatus)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            QueueExternalIfRecv.COLUMN_NAME_RECV_STATUS, recvStatus)});

        }

        // パラメータチェック
        if (StringUtil.isNullOrEmpty(externalIfTargetCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            QueueExternalIfRecv.COLUMN_NAME_EXTERNAL_IF_TARGET_CODE, externalIfTargetCode)});

        }

        // 外部I/F受信キュー 取得
        TypedQuery<QueueExternalIfRecv> queryRecvState =
                em.createNamedQuery(GnomesQueryConstants.QUERY_NAME_QUEUE_EXTERNAL_IF_RECV, QueueExternalIfRecv.class);

        queryRecvState.setParameter(QueueExternalIfRecv.COLUMN_NAME_RECV_STATUS, recvStatus);
        queryRecvState.setParameter(QueueExternalIfRecv.COLUMN_NAME_EXTERNAL_IF_TARGET_CODE, externalIfTargetCode);
        List<QueueExternalIfRecv> datasExternalIfRecvQue = queryRecvState.getResultList();

        return datasExternalIfRecvQue;

    }

    /**
     * 外部I/F受信キュー登録
     * @param item 外部I/F受信キュー
     * @param em エンティティマネージャー
     */
    @TraceMonitor
    @ErrorHandling
    public void insert(QueueExternalIfRecv item, EntityManager em) {

        // 登録
        super.persist(em, item);

    }

    /**
     * 外部I/F受信キュー 更新
     * @param item 外部I/F受信キュー
     * @param em エンティティマネージャー
     */
    @TraceMonitor
    @ErrorHandling
    public void update(QueueExternalIfRecv item, EntityManager em) {

        // 更新
        super.update(em, item);

    }

    /**
     * 外部I/F受信キュー削除
     * @param item 外部I/F受信キュー
     * @param em エンティティマネージャー
     */
    public void delete(QueueExternalIfRecv item, EntityManager em) {

         // 削除
         super.remove(em, item);

    }

}
