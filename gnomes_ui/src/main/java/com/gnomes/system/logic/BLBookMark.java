package com.gnomes.system.logic;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonEnums.BookMarkType;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseLogic;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.view.SystemFormBean;
import com.gnomes.system.dao.BookmarkDao;
import com.gnomes.system.dao.MstrScreenTransitionDao;
import com.gnomes.system.data.BookMarkFunctionBean;
import com.gnomes.system.entity.Bookmark;
import com.gnomes.system.entity.MstrScreenTransition;

/**
 * ブックマーク機能
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/02/01 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class BLBookMark extends BaseLogic {

    @Inject
    BookMarkFunctionBean bookMarkFunctionBean;

    @Inject
    MstrScreenTransitionDao mstrScreenTransitionDao;

    @Inject
    BookmarkDao bookmarkDao;
    
    @Inject
    SystemFormBean systemformBean;

    /**
     *ブックマーク削除処理
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void deleteBookMark() throws GnomesAppException {
        String userId = req.getUserId();
        String screenId = bookMarkFunctionBean.getBookmarkScreenId();
        Integer regionType = Integer.parseInt(gnomesSessionBean.getRegionType());
        //機能拡張対応、BOOKMARK_PARAMETERにパラメータがつくことによる対応
        //ScreenTransitionModeにParameterが入っている場合はParameter付きの検索を呼ぶ
        String parameter = systemformBean.getScreenTransitionMode();
        Bookmark bookmark = null;
        if (StringUtil.isNullOrEmpty(parameter)) {
        	 bookmark = bookmarkDao.getBookmark(userId, screenId, regionType);
        }else {
             bookmark = bookmarkDao.getBookmarkParam(userId, screenId, regionType, parameter);
        }
        

        List<String> screenIdList = gnomesSessionBean.getBookMarkedScreenIdList();
        screenIdList.remove(screenId);
        gnomesSessionBean.setBookMarkedScreenIdList(screenIdList);

        bookmarkDao.delete(bookmark);

        bookMarkFunctionBean.setBookMarkKbn(BookMarkType.NO_MARK.getValue());
    }


    /**
     * ブックマーク追加処理
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void insertBookMark() throws GnomesAppException {
        String userId = req.getUserId();
        String screenId = bookMarkFunctionBean.getBookmarkScreenId();
        Integer regionType = Integer.parseInt(gnomesSessionBean.getRegionType());
        MstrScreenTransition mstr = mstrScreenTransitionDao.getMstrScreenTransition(screenId);
        //機能拡張対応、BOOKMARK_PARAMETERにパラメータがつくことによる対応
        //ScreenTransitionModeにParameterが入っている場合はParameter付きの検索を呼ぶ
        String parameter = systemformBean.getScreenTransitionMode();
        Bookmark bookmark = null;
        if (StringUtil.isNullOrEmpty(parameter)) {
        	 bookmark = bookmarkDao.getBookmark(userId, screenId, regionType);
        }else {
             bookmark = bookmarkDao.getBookmarkParam(userId, screenId, regionType, parameter);
        }
        
        if (mstr == null) {
            StringBuilder params = new StringBuilder();
            params.append(MstrScreenTransition.TABLE_NAME);
            params.append(".").append(MstrScreenTransition.COLUMN_NAME_COMMAND_ID);
            params.append("：").append(screenId);

            // ME01.0026:「データがみつかりません。（{0}） 」
            GnomesAppException ex = exceptionFactory
                    .createGnomesAppException(
                            null, GnomesMessagesConstants.ME01_0026,
                            params.toString());
            throw ex;
        }

        // セッション
        List<String> screenIdList = gnomesSessionBean.getBookMarkedScreenIdList();
        // 存在しない場合
        if (screenIdList.contains(screenId) == false) {
            screenIdList.add(screenId);
        }

        // DB
        if (bookmark == null) {
            bookmark = new Bookmark();
            bookmark.setBookmark_key(UUID.randomUUID().toString());
            bookmark.setBookmark_screen_id(screenId);
            bookmark.setUser_id(userId);
            bookmark.setScreen_transition_key(mstr.getScreen_transition_key());
            bookmark.setDb_area_div(Integer.parseInt(gnomesSessionBean.getRegionType()));
            bookmark.setReq(req);
            bookmark.setbookmark_parameter(systemformBean.getScreenTransitionMode());

            bookmarkDao.insert(bookmark);
        } else {
            bookmark.setLast_regist_datetime(new Date());
            bookmark.setReq(req);
            bookmarkDao.update(bookmark);
        }

        bookMarkFunctionBean.setBookMarkKbn(BookMarkType.MARKED.getValue());

    }


 

}
