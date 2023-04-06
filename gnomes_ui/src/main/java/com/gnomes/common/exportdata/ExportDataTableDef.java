package com.gnomes.common.exportdata;

import java.util.List;

import com.gnomes.common.search.data.SearchInfoPack;

/**
 * 一覧エクスポート 定義クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/08/30 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class ExportDataTableDef {

    /**
     * テーブル辞書タグ名
     */
    private String tableTagName;

    /**
     * 検索条件
     */
    private SearchInfoPack search;

    /**
     * エクスポートデータ
     */
    private List<?> datas;

    /**
     * @return tableTagName
     */
    public String getTableTagName() {
        return tableTagName;
    }

    /**
     * @param tableTagName セットする tableTagName
     */
    public void setTableTagName(String tableTagName) {
        this.tableTagName = tableTagName;
    }

    /**
     * @return search
     */
    public SearchInfoPack getSearch() {
        return search;
    }

    /**
     * @param search セットする search
     */
    public void setSearch(SearchInfoPack search) {
        this.search = search;
    }

    /**
     * @return datas
     */
    public List<?> getDatas() {
        return datas;
    }

    /**
     * @param datas セットする datas
     */
    public void setDatas(List<?> datas) {
        this.datas = datas;
    }

}
