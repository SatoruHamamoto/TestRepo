package com.gnomes.common.importdata;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.tags.GnomesCTagDictionary;

/**
 * インポートエクスポートデータCsv 定義情報クラス
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
public class ImportExportDefCsv extends ImportExportDefBase {

    /**
     * デフォルトコンストラクター
     */
    public ImportExportDefCsv() {
        super();
    }

    /**
     * コンストラクタ テーブル辞書より作成
     * @param gnomesTagDictionary テーブル辞書
     * @param dictId 辞書ID
     * @param locale ロケール
     * @throws GnomesAppException
     */
    public ImportExportDefCsv(GnomesCTagDictionary gnomesCTagDictionary,
            String dictId, Locale userLocale) throws GnomesAppException {
        super(gnomesCTagDictionary, dictId, userLocale);
    }
    /**
     * コンストラクタ テーブルカラム情報リストより作成
     * @param lstTableInfo テーブルカラム情報リスト
     * @param dictId 辞書ID
     * @param locale ロケール
     * @throws GnomesAppException
     */
    public ImportExportDefCsv(GnomesCTagDictionary gnomesCTagDictionary,
            List<Map<String, Object>> lstTableInfo,
            String dictId, Locale locale) throws GnomesAppException {
        super(gnomesCTagDictionary, lstTableInfo, dictId, locale);
    }
}
