package com.gnomes.common.importdata;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.tags.GnomesCTagDictionary;

/**
 * インポートエクスポートデータExcel 定義情報クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/08/28 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class ImportExportDefExcel extends ImportExportDefBase {

    /**
     * シート名
     */
    private String sheetName;

    /***
     * デフォルトコンストラクター
     */
    public ImportExportDefExcel() {
        super();
    }

    /**
     * コンストラクタ テーブル辞書より作成
     * @param gnomesTagDictionary テーブル辞書
     * @param dictId 辞書ID
     * @param sheetName シート名
     * @param locale ロケール
     * @throws GnomesAppException
     */
    public ImportExportDefExcel(GnomesCTagDictionary gnomesCTagDictionary,
            String dictId, String sheetName, Locale locale) throws GnomesAppException {
        super(gnomesCTagDictionary, dictId, locale);
        this.sheetName = sheetName;
    }

    /**
     * コンストラクタ テーブルカラム情報リストより作成
     * @param lstTableInfo テーブルカラム情報リスト
     * @param dictId 辞書ID
     * @param sheetName シート名
     * @param locale ロケール
     * @throws GnomesAppException
     */
    public ImportExportDefExcel(GnomesCTagDictionary gnomesCTagDictionary,
            List<Map<String, Object>> lstTableInfo,
            String dictId, String sheetName, Locale locale) throws GnomesAppException {
        super(gnomesCTagDictionary, lstTableInfo, dictId, locale);
        this.sheetName = sheetName;
    }

    /**
     * @return sheetName
     */
    public String getSheetName() {
        return sheetName;
    }

    /**
     * @param sheetName セットする sheetName
     */
    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

}
