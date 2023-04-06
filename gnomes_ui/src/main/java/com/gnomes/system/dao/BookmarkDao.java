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
import com.gnomes.system.entity.Bookmark;

/**
 * ブックマーク管理 Dao
 */
/*
 * ========================== MODIFICATION HISTORY ==========================
 * Release Date ID/Name Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/02/01 YJP/K.Fujiwara 初版 [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class BookmarkDao extends BaseDao implements Serializable {

	@Inject
	protected GnomesEntityManager em;

    /**
     * コンストラクタ
     */
    public BookmarkDao() {
    }

    /**
     * ブックマーク管理 登録
     *
     * @param bookmark
     *            ブックマーク管理
     */
    @TraceMonitor
    @ErrorHandling
    public void insert(Bookmark bookmark) {

        if (bookmark != null) {
            em.getEntityManager().persist(bookmark);
            em.getEntityManager().flush();

        }

    }

    /**
     * ブックマーク管理 削除
     *
     * @param bookmark
     *            ブックマーク管理
     */
    @TraceMonitor
    @ErrorHandling
    public void delete(Bookmark bookmark) {

        if (bookmark != null) {
            em.getEntityManager().remove(bookmark);
            em.getEntityManager().flush();
        }
    }

    /**
     * ブックマーク管理 更新
     *
     * @param bookmark
     *            ブックマーク管理
     */
    @TraceMonitor
    @ErrorHandling
    public void update(Bookmark bookmark) {
        if (bookmark != null) {
            //登録
            em.getEntityManager().flush();
        }
    }


    /**
     * ブックマーク管理 取得
     *
     * @param userId
     *            ユーザID
     * @param ScreenId
     *            画面ID
     * @param regionType 領域区分
     * @return ブックマーク管理
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public Bookmark getBookmark(String userId, String ScreenId, Integer regionType) throws GnomesAppException {

        Bookmark data = null;

        if (StringUtil.isNullOrEmpty(userId)) {

            StringBuilder params = new StringBuilder();
            params.append(Bookmark.COLUMN_NAME_USER_ID);
            params.append("：").append(userId);

            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    params.toString());
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(ScreenId)) {

            StringBuilder params = new StringBuilder();
            params.append(Bookmark.COLUMN_NAME_BOOKMARK_SCREEN_ID);
            params.append("：").append(ScreenId);

            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    params.toString());
            throw ex;
        }

        TypedQuery<Bookmark> query = this.em.getEntityManager().createNamedQuery(
                GnomesQueryConstants.QUERY_NAME_BLBOOKMARK_BOOKMARK_USER_SCREEN,
                Bookmark.class);

        query.setParameter(Bookmark.COLUMN_NAME_USER_ID, userId);
        query.setParameter(Bookmark.COLUMN_NAME_BOOKMARK_SCREEN_ID, ScreenId);
        query.setParameter(Bookmark.COLUMN_NAME_DB_AREA_DIV, regionType);

        List<Bookmark> datas = query.getResultList();
        if (datas.size() > 0) {
            data = datas.get(0);
        }

        return data;
    }
    
    /**
     * ブックマーク管理 取得
     *
     * @param userId
     *            ユーザID
     * @param ScreenId
     *            画面ID
     * @param regionType 領域区分
     * @return ブックマーク管理(複数)
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<Bookmark> getBookmarks(String userId, String ScreenId, Integer regionType) throws GnomesAppException {

        Bookmark data = null;

        if (StringUtil.isNullOrEmpty(userId)) {

            StringBuilder params = new StringBuilder();
            params.append(Bookmark.COLUMN_NAME_USER_ID);
            params.append("：").append(userId);

            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    params.toString());
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(ScreenId)) {

            StringBuilder params = new StringBuilder();
            params.append(Bookmark.COLUMN_NAME_BOOKMARK_SCREEN_ID);
            params.append("：").append(ScreenId);

            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    params.toString());
            throw ex;
        }

        TypedQuery<Bookmark> query = this.em.getEntityManager().createNamedQuery(
                GnomesQueryConstants.QUERY_NAME_BLBOOKMARK_BOOKMARK_USER_SCREEN,
                Bookmark.class);

        query.setParameter(Bookmark.COLUMN_NAME_USER_ID, userId);
        query.setParameter(Bookmark.COLUMN_NAME_BOOKMARK_SCREEN_ID, ScreenId);
        query.setParameter(Bookmark.COLUMN_NAME_DB_AREA_DIV, regionType);

        List<Bookmark> datas = query.getResultList();

        return datas;
    }
    
    /**
     * ブックマーク管理 取得
     * @param userId
     * 		・ユーザID
     * @param ScreenId
     * 		・画面ID
     * @param regionType
     * 		・領域区分
     * @param parameter
     * 		・画面遷移パラメータ
     * @return ブックマーク管理
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public Bookmark getBookmarkParam(String userId, String ScreenId, Integer regionType, String parameter) throws GnomesAppException {

        Bookmark data = null;

        if (StringUtil.isNullOrEmpty(userId)) {

            StringBuilder params = new StringBuilder();
            params.append(Bookmark.COLUMN_NAME_USER_ID);
            params.append("：").append(userId);

            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    params.toString());
            throw ex;
        }

        if (StringUtil.isNullOrEmpty(ScreenId)) {

            StringBuilder params = new StringBuilder();
            params.append(Bookmark.COLUMN_NAME_BOOKMARK_SCREEN_ID);
            params.append("：").append(ScreenId);

            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    params.toString());
            throw ex;
        }

        TypedQuery<Bookmark> query = this.em.getEntityManager().createNamedQuery(
                GnomesQueryConstants.QUERY_NAME_BLBOOKMARK_BOOKMARK_USER_PARAMETER,
                Bookmark.class);

        query.setParameter(Bookmark.COLUMN_NAME_USER_ID, userId);
        query.setParameter(Bookmark.COLUMN_NAME_BOOKMARK_SCREEN_ID, ScreenId);
        query.setParameter(Bookmark.COLUMN_NAME_DB_AREA_DIV, regionType);
        query.setParameter(Bookmark.COLUMN_NAME_BOOKMARK_PARAMETER, parameter);

        List<Bookmark> datas = query.getResultList();
        if (datas.size() > 0) {
            data = datas.get(0);
        }

        return data;
    }

    /**
     * ブックマーク管理 取得
     *
     * @param userId ユーザID
     * @param regionType 領域区分
     * @return  ブックマーク管理リスト
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<Bookmark> getBookmarkList(String userId, Integer regionType) throws GnomesAppException {

        if (StringUtil.isNullOrEmpty(userId)) {

            StringBuilder params = new StringBuilder();
            params.append(Bookmark.COLUMN_NAME_USER_ID);
            params.append("：").append(userId);

            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    params.toString());
            throw ex;
        }


        TypedQuery<Bookmark> query = this.em.getEntityManager().createNamedQuery(
                GnomesQueryConstants.QUERY_NAME_BLBOOKMARK_BOOKMARK_USER,
                Bookmark.class);

        query.setParameter(Bookmark.COLUMN_NAME_USER_ID, userId);
        query.setParameter(Bookmark.COLUMN_NAME_DB_AREA_DIV, regionType);

        List<Bookmark> datas = query.getResultList();

        return datas;
    }

}
