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
import com.gnomes.system.entity.InfoComputerProcWorkcell;

/**
 * 端末工程作業場所選択情報 Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/07/12 KCC/A.Oomori                初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class InfoComputerProcWorkcellDao extends BaseDao implements Serializable {

    /**
     * エンティティマネージャー
     */
    @Inject
    protected GnomesEntityManager em;

    /**
     * コンストラクタ
     */
    public InfoComputerProcWorkcellDao() {
    }

    /**
     * 端末工程作業場所選択情報取得
     * @return 端末工程作業場所選択情報
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public InfoComputerProcWorkcell getInfoComputerProcWorkcell(String computerId, String siteCode) throws  GnomesAppException{

        if (StringUtil.isNullOrEmpty(computerId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,String.valueOf(computerId));

        }

        if (StringUtil.isNullOrEmpty(siteCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,String.valueOf(siteCode));

        }

        InfoComputerProcWorkcell data = null;

        // 端末工程作業場所選択情報を取得
        TypedQuery<InfoComputerProcWorkcell> query =
                this.em.getEntityManager().createNamedQuery(
                        GnomesQueryConstants.QUERY_NAME_BLSECURITY_INFO_COMPUTER_PROC_WORKCELL,
                        InfoComputerProcWorkcell.class);

        query.setParameter(InfoComputerProcWorkcell.COLUMN_NAME_CLIENT_DEVICE_ID, computerId);
        query.setParameter(InfoComputerProcWorkcell.COLUMN_NAME_SITE_CODE, siteCode);

        List<InfoComputerProcWorkcell> datas = query.getResultList();
        if (datas.size() > 0) {
            data = datas.get(0);
        }

        return data;
    }

    /**
     * 端末工程作業場所選択情報取得
     * @return 端末工程作業場所選択情報
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public InfoComputerProcWorkcell getInfoComputerProcWorkcell(String computerId, String siteCode, EntityManager eml) throws  GnomesAppException{

        if (StringUtil.isNullOrEmpty(computerId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,String.valueOf(computerId));

        }

        if (StringUtil.isNullOrEmpty(siteCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,String.valueOf(siteCode));

        }

        InfoComputerProcWorkcell data = null;

        // 端末工程作業場所選択情報を取得
        TypedQuery<InfoComputerProcWorkcell> query =
        		eml.createNamedQuery(
                        GnomesQueryConstants.QUERY_NAME_BLSECURITY_INFO_COMPUTER_PROC_WORKCELL,
                        InfoComputerProcWorkcell.class);

        query.setParameter(InfoComputerProcWorkcell.COLUMN_NAME_CLIENT_DEVICE_ID, computerId);
        query.setParameter(InfoComputerProcWorkcell.COLUMN_NAME_SITE_CODE, siteCode);

        List<InfoComputerProcWorkcell> datas = query.getResultList();
        if (datas.size() > 0) {
            data = datas.get(0);
        }

        return data;
    }

    /**
     * 端末工程作業場所選択情報 登録
     * @param item 端末工程作業場所選択情報
     */
    @TraceMonitor
    @ErrorHandling
    public void insert(InfoComputerProcWorkcell item) {
        if (item != null) {
            // 監査証跡情報を登録
            item.setReq(req);

            // 登録
            this.em.getEntityManager().persist(item);
            this.em.getEntityManager().flush();
        }
    }

    /**
     * 端末工程作業場所選択情報 更新
     * @param item 端末工程作業場所選択情報
     */
    @TraceMonitor
    @ErrorHandling
    public void update(InfoComputerProcWorkcell item) {
        if (item != null) {
            // 監査証跡情報を登録
            item.setReq(req);
            // 更新
            this.em.getEntityManager().flush();
        }

    }

    /**
     * 端末工程作業場所選択情報 更新
     * @param item 端末工程作業場所選択情報
     */
    @TraceMonitor
    @ErrorHandling
    public void update(InfoComputerProcWorkcell item, EntityManager eml) {
        if (item != null) {
            // 監査証跡情報を登録
            item.setReq(req);
            // 更新
            eml.flush();
        }

    }

}