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
import com.gnomes.system.entity.MstrPerson;

/**
 * ユーザ Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/04/28 KCC/T.Kamizuru            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class MstrPersonDao extends BaseDao implements Serializable {

    /**
     * エンティティマネージャー
     */
    @Inject
    protected GnomesEntityManager em;

    /**
     * コンストラクタ
     */
    public MstrPersonDao() {
    }

    /**
     * ユーザ 取得
     * @return ユーザ
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrPerson> getMstrPersonQuery(String userid) throws GnomesAppException {

        if (StringUtil.isNullOrEmpty(userid)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            MstrPerson.COLUMN_NAME_USER_ID, userid)});

            throw ex;

        }

        // ３．ユーザマスタの取得
        TypedQuery<MstrPerson> queryPerson =
                this.em.getEntityManager().createNamedQuery(
                        GnomesQueryConstants.QUERY_NAME_BLSECURITY_MSTR_PERSON,
                        MstrPerson.class);

        queryPerson.setParameter(MstrPerson.COLUMN_NAME_USER_ID, userid);
        List<MstrPerson> datasPerson = queryPerson.getResultList();

        // 0件チェック
        this.checkExistDataList(
            "ユーザー(mstr_user)",
            GnomesQueryConstants.QUERY_NAME_BLSECURITY_MSTR_PERSON,
            queryPerson,
            datasPerson);

        return datasPerson;
    }

    /**
     * ユーザ 取得
     * @return ユーザ
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrPerson> getMstrPersonQuery(String userid, EntityManager eml) throws GnomesAppException {

        if (StringUtil.isNullOrEmpty(userid)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            MstrPerson.COLUMN_NAME_USER_ID, userid)});

            throw ex;

        }

        // ３．ユーザマスタの取得
        TypedQuery<MstrPerson> queryPerson =
                eml.createNamedQuery(
                        GnomesQueryConstants.QUERY_NAME_BLSECURITY_MSTR_PERSON,
                        MstrPerson.class);

        queryPerson.setParameter(MstrPerson.COLUMN_NAME_USER_ID, userid);
        List<MstrPerson> datasPerson = queryPerson.getResultList();

        return datasPerson;
    }

    /**
     * ユーザー情報リスト取得.
     * @param useridList ユーザIDリスト
     * @return ユーザー情報リスト
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrPerson> getMstrPersonQuery(List<String> useridList) throws GnomesAppException {

        if (useridList == null || useridList.isEmpty()) {

            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            MstrPerson.COLUMN_NAME_USER_ID, useridList)});

        }

        TypedQuery<MstrPerson> queryPerson =
                this.em.getEntityManager().createNamedQuery(
                        GnomesQueryConstants.QUERY_NAME_BLSECURITY_MSTR_PERSON,
                        MstrPerson.class);

        queryPerson.setParameter(MstrPerson.COLUMN_NAME_USER_ID, useridList);

        return queryPerson.getResultList();

    }

    /**
     * ユーザー情報リスト取得.
     * @param useridList ユーザIDリスト
     * @return ユーザー情報リスト
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrPerson> getMstrPersonQuery(List<String> useridList, EntityManager eml) throws GnomesAppException {

        if (useridList == null || useridList.isEmpty()) {

            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            MstrPerson.COLUMN_NAME_USER_ID, useridList)});

        }

        TypedQuery<MstrPerson> queryPerson =
                eml.createNamedQuery(
                        GnomesQueryConstants.QUERY_NAME_BLSECURITY_MSTR_PERSON,
                        MstrPerson.class);

        queryPerson.setParameter(MstrPerson.COLUMN_NAME_USER_ID, useridList);

        return queryPerson.getResultList();

    }

    /**
     * ユーザーマスター取得
     * @param userId ユーザーID
     * @return ユーザーマスターEntity
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public MstrPerson getMstrPerson(String userId) throws GnomesAppException {

    	return this.getMstrPersonQuery(userId).get(0);

    }

    /**
     * ユーザ 登録
     * @param mstrPerson ユーザ
     */
    @TraceMonitor
    @ErrorHandling
    public void insert(MstrPerson mstrPerson) {
        if (mstrPerson != null) {

            // 監査証跡情報を登録
            mstrPerson.setReq(req);
            // 登録
            em.getEntityManager().persist(mstrPerson);
            em.getEntityManager().flush();

        }

    }

    /**
     * ユーザ 更新
     * @param mstrPerson ユーザ
     */
    @TraceMonitor
    @ErrorHandling
    public void update(MstrPerson mstrPerson) {
        if (mstrPerson != null) {

            // 監査証跡情報を登録
            mstrPerson.setReq(req);

            // 登録
            em.getEntityManager().flush();

        }

    }

    /**
     * ユーザ 更新
     * @param mstrPerson ユーザ
     */
    @TraceMonitor
    @ErrorHandling
    public void update(MstrPerson mstrPerson, EntityManager eml) {
        if (mstrPerson != null) {

            // 監査証跡情報を登録
            mstrPerson.setReq(req);
            // 登録
            eml.flush();

        }

    }


}
