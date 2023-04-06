package com.gnomes.system.dao;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.gnomes.common.constants.CommonEnums.MailNoticeStatus;
import com.gnomes.common.constants.GnomesQueryConstants;
import com.gnomes.common.dao.BaseDao;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.system.entity.TmpQueueMailNotice;

/**
 * メール通知キュー Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/13 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class TmpQueueMailNoticeDao extends BaseDao implements Serializable {

    /**
     * コンストラクタ.
     */
    public TmpQueueMailNoticeDao() {

    }

    /**
     * メール通知キュー情報リスト取得.
     * <pre>
     * メール通知キュー情報リストの取得を行う。
     * </pre>
     * @return メール通知キュー情報リスト
     */
    @TraceMonitor
    @ErrorHandling
    public List<TmpQueueMailNotice> getTmpQueueMailNoticeListForNotNotice(EntityManager em) {

        TypedQuery<TmpQueueMailNotice> query = em.createNamedQuery(
                GnomesQueryConstants.QUERY_NAME_GET_TMP_QUEUE_MAIL_NOTICE_LIST, TmpQueueMailNotice.class);
        query.setParameter(TmpQueueMailNotice.COLUMN_NAME_MAIL_NOTICE_STATUS, MailNoticeStatus.NotNotice.getValue());

        List<TmpQueueMailNotice> result = query.getResultList();

        return result;

    }

}
