package com.gnomes.system.dao;

import java.io.Serializable;
import java.util.List;

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
import com.gnomes.system.entity.InfoUser;

/**
 * ユーザ情報 Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/07/12 KCC/A.Oomori                初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class InfoUserDao extends BaseDao implements Serializable {

    /**
     * エンティティマネージャー
     */
    @Inject
    protected GnomesEntityManager em;

    /**
     * コンストラクタ
     */
    public InfoUserDao() {
    }

    /**
     * ユーザ情報取得
     * @return ユーザ情報
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public InfoUser getInfoUser(String userId) throws  GnomesAppException{

        if (StringUtil.isNullOrEmpty(userId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,String.valueOf(userId));

        }

        InfoUser data = null;

        // ユーザ情報を取得
        TypedQuery<InfoUser> query =
                this.em.getEntityManager().createNamedQuery(
                        GnomesQueryConstants.QUERY_NAME_BLSECURITY_INFO_USER,
                        InfoUser.class);

        query.setParameter(InfoUser.COLUMN_NAME_USER_ID, userId);

        List<InfoUser> datas = query.getResultList();
        if (datas.size() > 0) {
            data = datas.get(0);
        }

        return data;
    }

    /**
     * ユーザ情報取得(参照領域指定)
     * @return ユーザ情報
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public InfoUser getInfoUser(String userId, EntityManager eml) throws  GnomesAppException{

        if (StringUtil.isNullOrEmpty(userId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,String.valueOf(userId));

        }

        InfoUser data = null;

        // ユーザ情報を取得
        TypedQuery<InfoUser> query =
                eml.createNamedQuery(
                        GnomesQueryConstants.QUERY_NAME_BLSECURITY_INFO_USER,
                        InfoUser.class);

        query.setParameter(InfoUser.COLUMN_NAME_USER_ID, userId);

        List<InfoUser> datas = query.getResultList();
        if (datas.size() > 0) {
            data = datas.get(0);
        }

        return data;
    }

    /**
     * ユーザ情報 登録
     * @param item ユーザ情報
     */
    @TraceMonitor
    @ErrorHandling
    public void insert(InfoUser item) {
        if (item != null) {
            // 監査証跡情報を登録
            item.setReq(req);

            // 登録
            this.em.getEntityManager().persist(item);
            this.em.getEntityManager().flush();
        }
    }

    /**
     * ユーザ情報 登録
     * @param item ユーザ情報
     */
    @TraceMonitor
    @ErrorHandling
    public void insert(InfoUser item, EntityManager eml) {
        if (item != null) {

            // 監査証跡情報を登録
            item.setReq(req);

            // 登録
            eml.persist(item);
            eml.flush();
        }
    }


    /**
     * ユーザ情報 更新
     * @param item ユーザ情報
     */
    @TraceMonitor
    @ErrorHandling
    public void update(InfoUser item) {
        if (item != null) {
            // 監査証跡情報を登録
            item.setReq(req);

            // 更新
            this.em.getEntityManager().flush();
        }

    }

    /**
     * ユーザ情報 更新
     * @param item ユーザ情報
     */
    @TraceMonitor
    @ErrorHandling
    public void update(InfoUser item, EntityManager eml) {
        if (item != null) {

            // 監査証跡情報を登録
            item.setReq(req);

            // 更新
            eml.flush();
        }

    }

}