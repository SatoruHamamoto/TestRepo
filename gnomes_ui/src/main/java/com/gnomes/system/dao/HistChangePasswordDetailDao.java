package com.gnomes.system.dao;

import java.io.Serializable;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.gnomes.common.dao.BaseDao;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.persistence.GnomesEntityManager;
import com.gnomes.system.entity.HistChangePasswordDetail;

/**
 * パスワード変更履歴詳細 Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/07/12 KCC/A.Oomori                初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class HistChangePasswordDetailDao extends BaseDao implements Serializable {

    /**
     * エンティティマネージャー
     */
    @Inject
    protected GnomesEntityManager em;

    /**
     * コンストラクタ
     */
    public HistChangePasswordDetailDao() {
    }

    /**
     * パスワード変更履歴詳細 登録
     * @param item パスワード変更履歴詳細
     */
    @TraceMonitor
    @ErrorHandling
    public void insert(HistChangePasswordDetail item, EntityManager eml) {
        if (item != null) {

            // 監査証跡情報を登録
            item.setReq(req);

            // 登録
            eml.persist(item);
            eml.flush();
        }
    }

}