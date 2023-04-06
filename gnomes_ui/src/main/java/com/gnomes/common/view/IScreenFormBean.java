package com.gnomes.common.view;

/**
 * Interface for ScreenFormBean
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/12/05 YJP/H.Gojo                  初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public interface IScreenFormBean extends IFormBean {

    /** 保存済みの検索条件を取得 */
    public String getJsonSaveSearchInfos();

}
