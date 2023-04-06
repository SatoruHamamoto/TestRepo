package com.gnomes.external.dao;


import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.TypedQuery;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.GnomesQueryConstants;
import com.gnomes.common.dao.BaseDao;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.persistence.GnomesEntityManager;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.external.entity.ExternalIfSendFileSeqNo;

/**
 * 外部I/F送信ファイル連番管理 Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/09/07 KCC/A.Oomori                初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class ExternalIfSendFileSeqNoDao extends BaseDao implements Serializable {

	@Inject
	protected GnomesEntityManager em;

    /**
     * コンストラクタ.
     */
    public ExternalIfSendFileSeqNoDao() {

    }

    /**
     * 外部I/F送信ファイル連番管理取得
     * @param fileType ファイル種別
     * @return 外部I/F送信ファイル連番管理
     */
    @TraceMonitor
    @ErrorHandling
    public ExternalIfSendFileSeqNo getExternalIfSendFileSeqNoQuery(String fileType) throws GnomesAppException {

        //パラメータチェック
        if (StringUtil.isNullOrEmpty(fileType)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            ExternalIfSendFileSeqNo.COLUMN_NAME_FILE_TYPE, fileType)});

        }

        // 外部I/F送信ファイル連番管理の取得
        TypedQuery<ExternalIfSendFileSeqNo> queryExternalSendFileSeqNo =
                this.em.getEntityManager().createNamedQuery(GnomesQueryConstants.QUERY_NAME_EXTERNAL_IF_SEND_FILE_SEQ_NO, ExternalIfSendFileSeqNo.class);
        queryExternalSendFileSeqNo.setParameter(ExternalIfSendFileSeqNo.COLUMN_NAME_FILE_TYPE, fileType);
        List<ExternalIfSendFileSeqNo> datasExternalSendFileSeqNo = queryExternalSendFileSeqNo.getResultList();

        if (!datasExternalSendFileSeqNo.isEmpty()) {
            return datasExternalSendFileSeqNo.get(0);
        }

        return null;

    }

    /**
     * 外部I/F送信ファイル連番管理 更新
     * @param item 外部I/F送信ファイル連番管理
     */
    @TraceMonitor
    @ErrorHandling
    public void update(ExternalIfSendFileSeqNo item) {

        if (item != null) {
            // 監査証跡情報を登録
            item.setReq(this.req);
            // 更新
            this.em.getEntityManager().flush();

        }

    }

}
