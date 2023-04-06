package com.gnomes.system.dao;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.Dependent;
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
import com.gnomes.system.entity.TableSearchSetting;

/**
 * テーブル検索条件管理 Dao
 */
/*
 * ========================== MODIFICATION HISTORY ==========================
 * Release Date ID/Name Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/02/01 YJP/K.Fujiwara 初版 [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class TableSearchSettingDao extends BaseDao implements Serializable {

	@Inject
	protected GnomesEntityManager em;

    /**
     * コンストラクタ.
     */
    public TableSearchSettingDao() {

    }

    /**
     * テーブル検索条件管理 登録
     *
     * @param tableSearchSetting
     *            テーブル検索条件管理
     */
    @TraceMonitor
    @ErrorHandling
    public void insert(TableSearchSetting tableSearchSetting) {

        if (tableSearchSetting != null) {
            em.getEntityManager().persist(tableSearchSetting);
            em.getEntityManager().flush();
        }
    }

    /**
     * テーブル検索条件管理 更新
     *
     * @param tableSearchSetting
     *              テーブル検索条件管理
     */
    @TraceMonitor
    @ErrorHandling
    public void update(TableSearchSetting tableSearchSetting) {
        if (tableSearchSetting != null) {
            //登録
            em.getEntityManager().flush();

        }

    }

    /**
     * テーブル検索条件管理 削除
     *
     * @param tableSearchSetting
     *              テーブル検索条件管理
     */
    @TraceMonitor
    @ErrorHandling
    public void delete(TableSearchSetting tableSearchSetting) {
        if (tableSearchSetting != null) {
            //削除
            em.getEntityManager().remove(tableSearchSetting);
            em.getEntityManager().flush();
        }
    }

    /**
     * テーブル検索条件管理 取得
     *
     * @param userId
     *            ユーザID
     * @param tableId
     *            テーブルID
     * @param settingType
     *            設定種類
     *
     * @return ブックマーク管理
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public TableSearchSetting getTableSearchSetting(String userId,
            String tableId, int settingType) throws GnomesAppException {

        TableSearchSetting data = null;

        if (StringUtil.isNullOrEmpty(userId)) {

            StringBuilder params = new StringBuilder();
            params.append(TableSearchSetting.COLUMN_NAME_USER_ID);
            params.append("：").append(userId);

            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    params.toString());
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(tableId)) {

            StringBuilder params = new StringBuilder();
            params.append(TableSearchSetting.COLUMN_NAME_TABLE_ID);
            params.append("：").append(tableId);

            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    params.toString());
            throw ex;
        }

        TypedQuery<TableSearchSetting> query = this.em.getEntityManager().createNamedQuery(
                GnomesQueryConstants.QUERY_NAME_TABLE_SEARCH_SETTING_USER_TABLE_TYPE,
                TableSearchSetting.class);

        query.setParameter(TableSearchSetting.COLUMN_NAME_USER_ID, userId);
        query.setParameter(TableSearchSetting.COLUMN_NAME_TABLE_ID, tableId);
        query.setParameter(TableSearchSetting.COLUMN_NAME_SETTING_TYPE, settingType);

        List<TableSearchSetting> datas = query.getResultList();
        if (datas.size() > 0) {
            data = datas.get(0);
        }

        return data;
    }
}
