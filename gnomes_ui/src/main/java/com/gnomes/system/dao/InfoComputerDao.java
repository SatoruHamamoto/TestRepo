package com.gnomes.system.dao;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.GnomesQueryConstants;
import com.gnomes.common.dao.BaseDao;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.persistence.GnomesEntityManager;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.system.entity.InfoComputer;

/**
 * 端末情報 Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/07/12 KCC/A.Oomori                初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class InfoComputerDao extends BaseDao implements Serializable {

    /**
     * エンティティマネージャー
     */
    @Inject
    protected GnomesEntityManager em;

    /**
     * コンストラクタ
     */
    public InfoComputerDao() {
    }

    /**
     * 端末情報取得
     * @return 端末情報
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<InfoComputer> getInfoComputer() throws  GnomesAppException{

        List<InfoComputer> datas = null;

        // 端末情報を取得
        TypedQuery<InfoComputer> query =
                this.em.getEntityManager().createNamedQuery(
                        InfoComputer.class.getSimpleName() + CommonConstants.FIND_ALL,
                        InfoComputer.class);

        datas = query.getResultList();

        return datas;
    }

    /**
     * 端末情報取得(参照領域指定）
     * ログイン画面表示用
     * @param eml エンティティマネージャー
     * @return 端末情報
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<InfoComputer> getInfoComputer(EntityManager eml) throws  GnomesAppException{

        List<InfoComputer> datas = null;

        // 作業依頼（作業指図）情報(Entity)を取得
        TypedQuery<InfoComputer> query =
                eml.createNamedQuery(
                        InfoComputer.class.getSimpleName() + CommonConstants.FIND_ALL,
                        InfoComputer.class);

        datas = query.getResultList();

        return datas;
    }

    /**
     * 端末情報取得
     * @return 端末情報
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public InfoComputer getInfoComputer(String computerId) throws  GnomesAppException{

        if (StringUtil.isNullOrEmpty(computerId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,String.valueOf(computerId));

        }

        InfoComputer data = null;

        // 作業依頼（作業指図）情報(Entity)を取得
        TypedQuery<InfoComputer> query =
                this.em.getEntityManager().createNamedQuery(
                        GnomesQueryConstants.QUERY_NAME_GET_INFO_COMPUTER,
                        InfoComputer.class);

        query.setParameter(InfoComputer.COLUMN_NAME_COMPUTER_ID, computerId);

        List<InfoComputer> datas = query.getResultList();
        if (datas.size() > 0) {
            data = datas.get(0);
        }

        return data;
    }

    /**
     * 端末情報取得(参照領域指定)
     * @return 端末情報
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public InfoComputer getInfoComputer(String computerId, EntityManager eml) throws  GnomesAppException{

        if (StringUtil.isNullOrEmpty(computerId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,String.valueOf(computerId));

        }

        InfoComputer data = null;

        // 作業依頼（作業指図）情報(Entity)を取得
        TypedQuery<InfoComputer> query =
                eml.createNamedQuery(
                        GnomesQueryConstants.QUERY_NAME_GET_INFO_COMPUTER,
                        InfoComputer.class);

        query.setParameter(InfoComputer.COLUMN_NAME_COMPUTER_ID, computerId);

        List<InfoComputer> datas = query.getResultList();
        if (datas.size() > 0) {
            data = datas.get(0);
        }

        return data;
    }


    /**
     * 端末情報 登録
     * @param item 端末情報
     */
    @TraceMonitor
    @ErrorHandling
    public void insert(InfoComputer item) {
        if (item != null) {

            // 監査証跡情報を登録
            item.setReq(req);

            // 登録
            this.em.getEntityManager().persist(item);
            this.em.getEntityManager().flush();
        }
    }

    /**
     * 端末情報 登録
     * @param item 端末情報
     */
    @TraceMonitor
    @ErrorHandling
    public void insert(InfoComputer item, EntityManager eml) {
        if (item != null) {

            // 監査証跡情報を登録
            item.setReq(req);

            // 登録
            eml.persist(item);
            eml.flush();
        }
    }

    /**
     * 端末情報 更新
     * @param item 端末情報
     */
    @TraceMonitor
    @ErrorHandling
    public void update(InfoComputer item) {
        if (item != null) {

            // 監査証跡情報を登録
            item.setReq(req);

            // 更新
            this.em.getEntityManager().flush();
        }

    }

    /**
     * 端末情報 更新
     * @param item 端末情報
     */
    @TraceMonitor
    @ErrorHandling
    public void update(InfoComputer item, EntityManager eml) {
        if (item != null) {

            // 監査証跡情報を登録
            item.setReq(req);

            // 更新
            eml.flush();
        }

    }


}