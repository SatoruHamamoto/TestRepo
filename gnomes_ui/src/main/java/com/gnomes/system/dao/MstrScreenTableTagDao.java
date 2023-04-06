package com.gnomes.system.dao;


import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;

import org.picketbox.util.StringUtil;

import com.gnomes.common.dao.BaseDao;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.system.entity.MstrScreenTableTag;

/**
 * 画面テーブル設定マスタ Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/01/29 KCC/A.Oomori               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class MstrScreenTableTagDao extends BaseDao implements Serializable {

    /** テーブルタグIDの区切り文字 */
    private static final String TABLE_TAG_ID_SEPARATOR_REGEX = "\\.";

    /**
     * コンストラクタ
     */
    public MstrScreenTableTagDao() {
    }

    /**
     * 画面テーブル設定 取得
     * @return 画面テーブル設定
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrScreenTableTag> getMstrScreenTableTag() throws GnomesAppException {

        List<MstrScreenTableTag> datas = gnomesSystemModel.getMstrScreenTableTagList();

        return datas;
    }

    /**
     * 画面テーブル設定(1件) 取得
     * @param screenTableId 画面ID.テーブルタグID
     * @return 画面テーブル設定(1件)
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public MstrScreenTableTag getMstrScreenTableTag(String screenTableId) throws GnomesAppException {

        if (StringUtil.isNullOrEmpty(screenTableId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,String.valueOf(screenTableId));

        }

        // 画面ID
        String screenId = screenTableId.split(TABLE_TAG_ID_SEPARATOR_REGEX)[0];
        // テーブルタグID
        String tableTagId = screenTableId.split(TABLE_TAG_ID_SEPARATOR_REGEX)[1];

        return getMstrScreenTableTag(screenId, tableTagId);

    }

    /**
     * 画面テーブル設定(1件) 取得
     * @param screenId 画面ID
     * @param tableTagId テーブルタグID
     * @return 画面テーブル設定(1件)
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public MstrScreenTableTag getMstrScreenTableTag(String screenId, String tableTagId) throws GnomesAppException {

        if (StringUtil.isNullOrEmpty(screenId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,String.valueOf(screenId));

        }
        if (StringUtil.isNullOrEmpty(tableTagId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,String.valueOf(tableTagId));

        }

        List<MstrScreenTableTag> result = gnomesSystemModel.getMstrScreenTableTagList().stream()
                .filter(item -> item.getScreen_id().equals(screenId)
                			&& item.getTable_tag_id().equals(tableTagId))
                .collect(Collectors.toList());


        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }

}
