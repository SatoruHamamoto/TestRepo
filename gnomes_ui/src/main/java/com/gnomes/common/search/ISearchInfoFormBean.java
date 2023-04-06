package com.gnomes.common.search;

/**
*
* 検索情報フォームビーン インターフェイス
* <!-- TYPE DESCRIPTION --><pre>
* </pre>
*/
/* ========================== MODIFICATION HISTORY ==========================
* Release  Date       ID/Name                   Comment
* --------------------------------------------------------------------------
* R0.01.01 2017/04/11 KCC/K.Fujiwara            初版
* [END OF MODIFICATION HISTORY]
* ==========================================================================
*/
public interface ISearchInfoFormBean {

    /** 検索条件JSON取得 */
    public String getJsonSearchInfo();

    /** 検索条件JSON設定 */
    public void setJsonSearchInfo(String jsonSearchInfo);

    /** 検索画面設定JSON取得 */
    public String getJsonSearchMasterInfo();

    /** 検索画面設定JSON設定 */
    public void setJsonSearchMasterInfo(String jsonSearchMasterInfo);

}
