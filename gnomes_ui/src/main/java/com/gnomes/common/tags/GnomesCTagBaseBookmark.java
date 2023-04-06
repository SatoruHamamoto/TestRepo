package com.gnomes.common.tags;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.gnomes.common.constants.CommonEnums.BookMarkRegistration;
import com.gnomes.common.constants.CommonEnums.BookMarkType;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.system.dao.BookmarkDao;
import com.gnomes.system.dao.MstrScreenTransitionDao;
import com.gnomes.system.entity.Bookmark;
import com.gnomes.system.entity.MstrScreenTransition;

/**
 * ブックマークを扱うカスタムタグの基底
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/1/18 YJP/A.Oomori               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public abstract class GnomesCTagBaseBookmark extends GnomesCTagBasePrivilege {

    @Inject
    protected GnomesExceptionFactory exceptionFactory;

    @Inject
    protected MstrScreenTransitionDao mstrScreenTransitionDao;

    @Inject
    protected BookmarkDao bookmarkDao;

    /**
     * 画面のブックマークタイプを取得する
     *
     * @return ブックマークタイプ
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    protected BookMarkType getScreenBookMarkType() throws GnomesAppException {

        BookMarkType result = BookMarkType.NONE;

        String screenId = requestContext.getScreenId();
        MstrScreenTransition mstrScreenTransition = mstrScreenTransitionDao
                .getMstrScreenTransition(screenId);
        // 画面遷移情報マスタに存在していて、ブックマーク登録化の場合
        if (mstrScreenTransition != null && mstrScreenTransition
                .getIs_bookmark() == BookMarkRegistration.YES.getValue()) {
            // ブックマーク登録済みの場合
            if (gnomesSessionBean.getBookMarkedScreenIdList()
                    .contains(screenId)) {
                // ブックマーク登録済み
                result = BookMarkType.MARKED;
            } else {
                // ブックマーク登録未
                result = BookMarkType.NO_MARK;
            }
        }

        return result;
    }

    /**
     * 画面遷移情報マスタを取得
     *
     * @param screenIdList 画面ID
     * @return 画面遷移情報マスタ
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    protected MstrScreenTransition getMstrScreenTransition(
            String screenId) throws GnomesAppException {

        MstrScreenTransition data = mstrScreenTransitionDao
                .getMstrScreenTransition(screenId);
        if (data == null) {
            StringBuilder params = new StringBuilder();
            params.append(MstrScreenTransition.TABLE_NAME);
            params.append(".").append(MstrScreenTransition.COLUMN_NAME_SCREEN_ID);
            params.append("：").append(screenId);

            // ME01.0026:「データがみつかりません。（{0}） 」
            GnomesAppException ex = exceptionFactory
                    .createGnomesAppException(
                            null, GnomesMessagesConstants.ME01_0026,
                            params.toString());
            throw ex;
        }

        return data;
    }

    /**
     * ブックマーク済の画面遷移情報マスタを取得
     *
     * @return 画面遷移情報マスタリスト
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    protected List<MstrScreenTransition> getBookmarkScreenTransitionList()
            throws GnomesAppException {

        List<MstrScreenTransition> result = new ArrayList<>();

        // ユーザのブックマーク管理を取得
        List<Bookmark> bookmarkList = bookmarkDao
                .getBookmarkList(requestContext.getUserId(), Integer.parseInt(gnomesSessionBean.getRegionType()));

        for (Bookmark b : bookmarkList) {
            result.add(getMstrScreenTransition(b.getBookmark_screen_id()));
        }
        return result;
    }
}
