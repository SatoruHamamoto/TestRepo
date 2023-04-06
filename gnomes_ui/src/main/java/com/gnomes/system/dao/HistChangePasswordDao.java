package com.gnomes.system.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.GnomesQueryConstants;
import com.gnomes.common.dao.BaseDao;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.persistence.GnomesEntityManager;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.system.entity.HistChangePassword;

/**
 * パスワード変更履歴 Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/07/12 KCC/A.Oomori                初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class HistChangePasswordDao extends BaseDao implements Serializable {

    /**
     * エンティティマネージャー
     */
    @Inject
    protected GnomesEntityManager em;

    /**
     * コンストラクタ
     */
    public HistChangePasswordDao() {
    }

    /**
     * パスワード変更履歴取得
     * @return パスワード変更履歴
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<HistChangePassword> getHistChangePassword(String userId, Integer ngFlag, EntityManager eml) throws  GnomesAppException{

        if (StringUtil.isNullOrEmpty(userId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,String.valueOf(userId));

        }

        if(Objects.isNull(ngFlag)){
            // ME02.0009:「パラメータが不正です。({0})」
            throw exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050, String.valueOf(ngFlag));
        }

        // パスワード変更履歴を取得
        TypedQuery<HistChangePassword> query =
                eml.createNamedQuery(
                        GnomesQueryConstants.QUERY_NAME_BLSECURITY_HIST_CHANGE_PASSWORD,
                        HistChangePassword.class);

        query.setParameter(HistChangePassword.COLUMN_NAME_USER_NUMBER, userId);
        query.setParameter(HistChangePassword.COLUMN_NAME_NG_FLAG, ngFlag);

        List<HistChangePassword> datas = query.getResultList();

        return datas;
    }

    /**
     * パスワード変更履歴 登録
     * @param item パスワード変更履歴
     */
    @TraceMonitor
    @ErrorHandling
    public void insert(HistChangePassword item, EntityManager eml) {
        if (item != null) {

            // 監査証跡情報を登録
            item.setReq(req);

            // 登録
            eml.persist(item);
            eml.flush();
        }
    }

}