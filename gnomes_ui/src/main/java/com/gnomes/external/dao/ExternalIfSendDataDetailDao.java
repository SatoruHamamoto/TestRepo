package com.gnomes.external.dao;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.GnomesQueryConstants;
import com.gnomes.common.dao.BaseDao;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.external.entity.ExternalIfSendDataDetail;

/**
 * 外部I/F送信データ詳細Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/09/07 KCC/A.Oomori                初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class ExternalIfSendDataDetailDao extends BaseDao implements Serializable {

    /**
     * コンストラクタ.
     */
    public ExternalIfSendDataDetailDao() {

    }

    /**
     * 外部I/F送信データ詳細 取得
     * @param externalIfSendStatusKey 外部I/F送信状態キー
     * @param em エンティティマネージャー
     * @return 外部I/F送信データ詳細
     */
    @TraceMonitor
    @ErrorHandling
    public List<ExternalIfSendDataDetail> getSendDataDetailQuery(
            String externalIfSendStatusKey, EntityManager em) throws GnomesAppException {

        // パラメータチェック
        if (StringUtil.isNullOrEmpty(externalIfSendStatusKey)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            ExternalIfSendDataDetail.COLUMN_NAME_EXTERNAL_IF_SEND_STATUS_KEY, externalIfSendStatusKey)});

        }

        // 外部I/F送信データ詳細 取得
        TypedQuery<ExternalIfSendDataDetail> querySendDataDetail =
                em.createNamedQuery(GnomesQueryConstants.QUERY_NAME_EXTERNAL_IF_SEND_DATA_DETAILIL, ExternalIfSendDataDetail.class);
        querySendDataDetail.setParameter(ExternalIfSendDataDetail.COLUMN_NAME_EXTERNAL_IF_SEND_STATUS_KEY, externalIfSendStatusKey);
        List<ExternalIfSendDataDetail> datasSendDataDetail = querySendDataDetail.getResultList();

        return datasSendDataDetail;

    }

    /**
     * 外部I/F送信データ詳細 登録
     * @param itemList 外部I/F送信データ詳細リスト
     * @param em エンティティマネージャー
     */
    @TraceMonitor
    @ErrorHandling
    public void insert(List<ExternalIfSendDataDetail> itemList, EntityManager em) {

        for (ExternalIfSendDataDetail item : itemList) {

            if (item != null) {
                // 登録
                super.persist(em, item);
            }

        }
        em.flush();

    }

    /**
     * 外部I/F送信データ詳細 更新
     * @param item 外部I/F送信データ詳細
     * @param em エンティティマネージャー
     */
    @TraceMonitor
    @ErrorHandling
    public void update(ExternalIfSendDataDetail item, EntityManager em) {

        if (item != null) {
            // 更新
            super.update(em, item);
        }

    }

    /**
     * 外部I/F送信データ詳細 削除
     * @param itemList 外部I/F送信データ詳細リスト
     * @param em エンティティマネージャー
     */
    @TraceMonitor
    @ErrorHandling
    public void delete(List<ExternalIfSendDataDetail> itemList, EntityManager em) {

        if (!(itemList == null || itemList.isEmpty())) {
               super.remove(em, itemList.toArray(new ExternalIfSendDataDetail[itemList.size()]));
        }

    }

}
