package com.gnomes.external.dao;


import java.io.Serializable;
import java.util.List;
import java.util.Objects;

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
import com.gnomes.external.entity.ExtIfDataSrActualDetail;

/**
 * 外部I/Fデータファイル送受信実績詳細 Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/09/07 KCC/A.Oomori                初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class ExtIfDataSrActualDetailDao extends BaseDao implements Serializable {

    /**
     * コンストラクタ.
     */
    public ExtIfDataSrActualDetailDao() {

    }

    /**
     * 外部I/Fデータファイル送受信実績詳細取得
     * @param externalIfDataSrActualKey 外部I/Fデータファイル送受信実績キー
     * @param em エンティティマネージャー
     * @return 外部I/Fデータファイル送受信実績詳細
     */
    @TraceMonitor
    @ErrorHandling
    public List<ExtIfDataSrActualDetail> getExtIfDataSendRecvActualDetail(
            String externalIfDataSrActualKey, EntityManager em) throws GnomesAppException {

        //パラメータチェック
        if (StringUtil.isNullOrEmpty(externalIfDataSrActualKey)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            ExtIfDataSrActualDetail.COLUMN_NAME_EXTERNAL_IF_DATA_SR_ACTUAL_KEY, externalIfDataSrActualKey)});

        }

        // 外部I/Fデータファイル送受信実績詳細取得
        TypedQuery<ExtIfDataSrActualDetail> queryExtIfDataSrActualDetail =
                em.createNamedQuery(GnomesQueryConstants.QUERY_NAME_GET_EXT_IF_DATA_SR_ACTUAL_DETAIL, ExtIfDataSrActualDetail.class);
        queryExtIfDataSrActualDetail.setParameter(ExtIfDataSrActualDetail.COLUMN_NAME_EXTERNAL_IF_DATA_SR_ACTUAL_KEY, externalIfDataSrActualKey);

        List<ExtIfDataSrActualDetail> datasExtIfDataSrActualDetail = queryExtIfDataSrActualDetail.getResultList();

        return datasExtIfDataSrActualDetail;

    }


    /**
     * 外部I/Fデータファイル送受信実績詳細 登録
     * @param externalIfDataDetail 外部I/Fデータファイル送受信実績詳細リスト
     * @param em エンティティマネージャー
     */
    @TraceMonitor
    @ErrorHandling
    public void insert(List<ExtIfDataSrActualDetail> externalIfDataDetail, EntityManager em) {

        if (Objects.nonNull(externalIfDataDetail)) {

            super.persist(em, externalIfDataDetail.toArray(
                    new ExtIfDataSrActualDetail[externalIfDataDetail.size()]));

        }

    }

    /**
     * 外部I/Fデータファイル送受信実績詳細 削除
     * @param externalIfDataSrActualKey 外部I/Fデータファイル送受信実績キー
     * @param em エンティティマネージャー
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void detele(String externalIfDataSrActualKey, EntityManager em) throws GnomesAppException {

        List<ExtIfDataSrActualDetail> dataSendRecvActualDetail =
                this.getExtIfDataSendRecvActualDetail(externalIfDataSrActualKey, em);

        if (Objects.nonNull(dataSendRecvActualDetail)) {
            super.remove(em, dataSendRecvActualDetail.toArray(
                    new ExtIfDataSrActualDetail[dataSendRecvActualDetail.size()]));

        }

    }

}
