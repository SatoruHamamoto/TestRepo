package com.gnomes.system.dao;

import java.io.Serializable;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.gnomes.common.dao.BaseDao;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.persistence.GnomesEntityManager;
import com.gnomes.system.entity.HistCertificationWrite;

/**
 * 認証履歴 Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/07/12 KCC/A.Oomori                初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class HistCertificationWriteDao extends BaseDao implements Serializable {

    /**
     * エンティティマネージャー
     */
    @Inject
    protected GnomesEntityManager em;

    /**
     * コンストラクタ
     */
    public HistCertificationWriteDao() {
    }

    /**
     * 認証履歴 登録
     * @param item 認証履歴
     */
    @TraceMonitor
    @ErrorHandling
    public void insert(HistCertificationWrite item) {
        if (item != null) {
            // 監査証跡情報を登録
            item.setReq(req);
            // 登録
            em.getEntityManager().persist(item);
            em.getEntityManager().flush();
        }
    }

    /**
     * 認証履歴 登録(参照領域指定)
     * @param item 認証履歴
     */
    @TraceMonitor
    @ErrorHandling
    public void insert(HistCertificationWrite item, EntityManager eml) {
        if (item != null) {

            // 監査証跡情報を登録
            item.setReq(req);
            // 登録
            eml.persist(item);
            eml.flush();
        }
    }

}