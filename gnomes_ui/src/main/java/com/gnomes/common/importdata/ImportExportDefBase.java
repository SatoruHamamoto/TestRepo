package com.gnomes.common.importdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.importdata.ImportExportColumnDef.ImportExportColumnDataType;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.tags.GnomesCTagDictionary;
import com.gnomes.common.tags.GnomesCTagTable;

/**
 * インポートエクスポートデータ 定義情報 基底クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/08/02 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class ImportExportDefBase {

    /** 辞書ID */
    private String key;

    /** インポートデータカラム 定義情報 */
    private List<ImportExportColumnDef> importExportColumDefinitions;

    /***
     * デフォルトコンストラクター
     */
    public ImportExportDefBase() {
    }

    /**
     * コンストラクタ テーブル辞書より作成
     * @param gnomesCTagDictionary テーブル辞書
     * @param dictId 辞書ID
     * @param locale ロケール
     * @throws GnomesAppException
     */
    public ImportExportDefBase(GnomesCTagDictionary gnomesCTagDictionary,
            String dictId, Locale locale) throws GnomesAppException {
        setImportExportDefinition(gnomesCTagDictionary, dictId, locale);
    }

    /**
     * コンストラクタ テーブルカラム情報リストより作成
     * @param lstTableInfo テーブルカラム情報リスト
     * @param dictId 辞書ID
     * @param locale ロケール
     * @throws GnomesAppException
     */
    public ImportExportDefBase(GnomesCTagDictionary gnomesCTagDictionary,
            List<Map<String, Object>> lstTableInfo,
            String dictId, Locale locale) throws GnomesAppException {
        this.key = dictId;
        setColDef(gnomesCTagDictionary, lstTableInfo, locale);
    }

    /**
     * テーブル定義情報からインポートエクスポート定義情報を取得
     * @param dictId 辞書キー
     * @return インポートエクスポート定義情報
     * @throws GnomesAppException
     */
    private void setImportExportDefinition(
            GnomesCTagDictionary gnomesCTagDictionary, String dictId,
            Locale locale) throws GnomesAppException {

        this.key = dictId;

        // テーブル辞書取得
        List<Map<String, Object>> lstTableInfo = gnomesCTagDictionary
                .getTableColumnInfo(gnomesCTagDictionary
                        .getTableInfo(dictId));

        setColDef(gnomesCTagDictionary, lstTableInfo, locale);
    }

    /**
     * カラム情報設定
     * @param lstTableInfo テーブルカラム情報リスト
     * @param locale ロケール
     * @throws GnomesAppException
     */
    private void setColDef(GnomesCTagDictionary gnomesCTagDictionary,
            List<Map<String, Object>> lstTableInfo,
            Locale locale) throws GnomesAppException {

        this.importExportColumDefinitions = new ArrayList<>();

        int position = 0;
        for (int i = 0; i < lstTableInfo.size(); i++) {

            Map<String, Object> tr = lstTableInfo.get(i);
            @SuppressWarnings("unchecked")
            Map<String, String> headInfo = (Map<String, String>) tr
                    .get(GnomesCTagDictionary.MAP_NAME_TABLE_INFO);

            String resourceId = headInfo.get(
                    GnomesCTagDictionary.MAP_NAME_RESOURCE_ID);
            if (resourceId == null) {
                continue;
            }

            ImportExportColumnDataType dateType = null;

            @SuppressWarnings("unchecked")
            Map<String, Object> mapColInfo = (Map<String, Object>) tr
                    .get(GnomesCTagDictionary.MAP_NAME_COLUMN_INFO);

            // 非表示
            if (mapColInfo.containsKey(
                    GnomesCTagTable.INFO_HIDDEN)) {
                continue;
            }

            String type = (String) mapColInfo
                    .get(GnomesCTagTable.INFO_TAG_TYPE);
            String decimalPointFieldName = null;
            String formatResourceId = null;
            String imgPtnDictId = null;

            switch (type) {
            case GnomesCTagTable.TAG_TYPE_TEXT:
                dateType = ImportExportColumnDataType.TEXT;
                break;
            case GnomesCTagTable.TAG_TYPE_NUMBER:
                dateType = ImportExportColumnDataType.NUMBER;
                decimalPointFieldName = (String) mapColInfo.get(
                        GnomesCTagTable.INFO_DECIMAL_POINT_PARAM_NAME);
                break;
            case GnomesCTagTable.TAG_TYPE_DATE:
                dateType = ImportExportColumnDataType.DATE;
                formatResourceId = (String) mapColInfo.get(
                        GnomesCTagTable.INFO_FORMAT_RESOURCE_ID);
                break;
            case GnomesCTagTable.TAG_TYPE_ZONEDDATETIME:
                dateType = ImportExportColumnDataType.ZONEDDATETIME;
                formatResourceId = (String) mapColInfo.get(
                        GnomesCTagTable.INFO_FORMAT_RESOURCE_ID);
                break;
            case GnomesCTagTable.TAG_TYPE_IMG_PATTERN:
                dateType = ImportExportColumnDataType.IMG_PATTERN;
                imgPtnDictId = (String) mapColInfo.get(GnomesCTagTable.INFO_PATTERN_ID);
                break;
            default:
                continue;
            }

            String fieldName = (String) mapColInfo
                    .get(GnomesCTagTable.INFO_PARAM_NAME);

            ImportExportColumnDef importExportColumDefinition = new ImportExportColumnDef();
            importExportColumDefinition
                    .setHeaderResouceId(resourceId);
            importExportColumDefinition.setPosition(position);
            importExportColumDefinition.setDataType(dateType);
            importExportColumDefinition.setFieldName(fieldName);


            importExportColumDefinition.setDecimalPointFieldName(decimalPointFieldName);

            if (formatResourceId != null) {
                String format = ResourcesHandler.getString(
                        formatResourceId, locale);
                importExportColumDefinition.setFormat(format);
            }
            if (imgPtnDictId != null) {
                importExportColumDefinition.setMapImgPtn(
                        getImgPatternMapLocale(gnomesCTagDictionary, imgPtnDictId, locale));
            }

            importExportColumDefinitions
                    .add(importExportColumDefinition);
            position++;
        }
    }


    /**
     * ロケールされたイメージパターンの取得
     * @param gnomesCTagDictionary 辞書
     * @param imgPtnDictId イメージパターンID
     * @param locale ロケール
     * @return ロケールされたイメージパターン
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getImgPatternMapLocale(GnomesCTagDictionary gnomesCTagDictionary, String imgPtnDictId, Locale locale) throws GnomesAppException {

        Map<String, Object> mapImgPtnLocale = new HashMap<String, Object>();

        Map<String, Object> mapImgPtn = gnomesCTagDictionary.getPatternDict(imgPtnDictId);


        for(Map.Entry<String, Object> mapImgPtnVals : mapImgPtn.entrySet()) {

            Map<String, Object> mapImgPtnValsLocale = new HashMap<String, Object>();

            for(Map.Entry<String, Object> v : ((Map<String, Object>) mapImgPtnVals.getValue()).entrySet()) {
                if (GnomesCTagDictionary.MAP_NAME_PTN_TITLE.equals(v.getKey())) {
                    // リソース取得
                    String title = ResourcesHandler.getString(
                            v.getValue().toString(), locale);
                    mapImgPtnValsLocale.put(v.getKey(), title);
                } else {
                    mapImgPtnValsLocale.put(v.getKey(), v.getValue());
                }
            }
            mapImgPtnLocale.put(mapImgPtnVals.getKey(), mapImgPtnValsLocale);
        }
        return mapImgPtnLocale;
    }


    /**
     * @return key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key セットする key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return importExportColumDefinitions
     */
    public List<ImportExportColumnDef> getImportExportColumDefinitions() {
        return importExportColumDefinitions;
    }

    /**
     * @param importExportColumDefinitions セットする importExportColumDefinitions
     */
    public void setImportExportColumDefinitions(
            List<ImportExportColumnDef> importExportColumDefinitions) {
        this.importExportColumDefinitions = importExportColumDefinitions;
    }

}
