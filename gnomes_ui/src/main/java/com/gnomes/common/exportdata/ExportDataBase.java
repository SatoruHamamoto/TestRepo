package com.gnomes.common.exportdata;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.Dependent;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.importdata.ImportExportColumnDef;
import com.gnomes.common.importdata.ImportExportColumnDef.ImportExportColumnDataType;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.tags.GnomesCTagBaseGetData;
import com.gnomes.common.tags.GnomesCTagDictionary;

/**
 * エクスポート処理 基底クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/08/25 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class ExportDataBase extends GnomesCTagBaseGetData {

    /**
     * デフォルトコンストラクター
     */
    public ExportDataBase() {
    }

    /**
     * エクスポートファイルタイプ
     */
    public enum ExportFileType {
        ExportFileType_FileType_Csv {
            @Override
            public String[] getValue() {
                String[] stringArray = { "csv" };
                return stringArray;
            }
        }, // CSV
        ExportFileType_FileType_Excel {
            @Override
            public String[] getValue() {
                String[] stringArray = { "xls", "xlsx" };
                return stringArray;
            }
        }; // EXCEL

        public String[] getValue() {
            return null;
        }
    }

    /**
     * ヘッダ行取得
     * @param importExportColumDefinitions データカラム情報リスト
     * @param locale ロケール
     * @return ヘッダー行
     */
    protected String[] getHeaderLine(
            List<ImportExportColumnDef> importExportColumDefinitions,
            Locale locale) {
        // ヘッダー作成
        String headerLine[] = new String[importExportColumDefinitions.size()];

        for (int i = 0; i < importExportColumDefinitions.size(); i++) {
            ImportExportColumnDef importExportColumDefinition = importExportColumDefinitions
                    .get(i);

            headerLine[i] = ResourcesHandler.getString(
                    importExportColumDefinition.getHeaderResouceId(), locale);
        }

        return headerLine;
    }

    /**
     * データ行取得
     * @param importExportColumDefinitions データカラム情報リスト
     * @param clsRowData データクラス
     * @param rowData データ
     * @return データ行
     * @throws GnomesAppException
     */
    protected String[] getDataLine(
            List<ImportExportColumnDef> importExportColumDefinitions,
            Class<?> clsRowData,
            Object rowData) throws GnomesAppException {
        // 行作成
        String dataLine[] = new String[importExportColumDefinitions.size()];

        for (int j = 0; j < importExportColumDefinitions.size(); j++) {
            ImportExportColumnDef colDef = importExportColumDefinitions.get(j);

            // データ取得
            Object objData = getObject(clsRowData, rowData,
                    colDef.getFieldName());

            if (colDef.getDataType() == ImportExportColumnDataType.TEXT) {

                dataLine[j] = (String) objData;

            } else if (colDef
                    .getDataType() == ImportExportColumnDataType.NUMBER) {

                dataLine[j] = getNumber(colDef, clsRowData, rowData, objData,
                        false);

            } else if (colDef
                    .getDataType() == ImportExportColumnDataType.CURRENCY) {

                dataLine[j] = getNumber(colDef, clsRowData, rowData, objData,
                        true);

            } else if (colDef
                    .getDataType() == ImportExportColumnDataType.DATE) {

                dataLine[j] = getDate(colDef, objData, false);

            } else if (colDef
                    .getDataType() == ImportExportColumnDataType.ZONEDDATETIME) {

                dataLine[j] = getDate(colDef, objData, true);
            } else if (colDef
                    .getDataType() == ImportExportColumnDataType.IMG_PATTERN) {

                dataLine[j] = getTitleImagePattern(colDef, objData);
            }
        }
        return dataLine;
    }

    /**
     * フィールド名よりデータ取得
     * @param clsBean ビーンクラス
     * @param objBean ビーンオブジェクト
     * @param fieldName フィールド名
     * @return 取得Object
     * @throws GnomesAppException 例外
     */
    private Object getObject(Class<?> clsBean, Object objBean,
            String fieldName) throws GnomesAppException {

        Object objData = null;
        try {

            objData = getObjectData(clsBean, objBean,
                    fieldName);

        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {

            // クラス[{0}]のフィールド名[{1}]より値取得に失敗しました
            GnomesAppException ex = new GnomesAppException(e);
            ex.setMessageNo(GnomesMessagesConstants.ME01_0006);
            Object[] errParam = {
                    clsBean.getName(),
                    fieldName
            };
            ex.setMessageParams(errParam);
            throw ex;

        } catch (IntrospectionException e) {

            // cのクラスにnameのフィールドが存在しません
            GnomesAppException ex = new GnomesAppException(e);
            ex.setMessageNo(GnomesMessagesConstants.ME01_0005);
            Object[] errParam = {
                    clsBean.getName(),
                    fieldName
            };
            ex.setMessageParams(errParam);
            throw ex;
        }
        return objData;
    }

    /**
     * オブジェクトの数値文字列取得
     * @param colDef カラム定義
     * @param clsBean ビーンクラス
     * @param objBean ビーンオブジェクト
     * @param objData データオブジェクト
     * @param isCurrency 金額判定フラグ
     * @return オブジェクトの数値文字列
     * @throws GnomesAppException 例外
     */
    private String getNumber(ImportExportColumnDef colDef, Class<?> clsBean,
            Object objBean, Object objData, boolean isCurrency)
            throws GnomesAppException {

        String result = null;
        Integer intObj = 0;
        String decimalPointName = colDef.getDecimalPointFieldName();

        if (decimalPointName != null && decimalPointName.length() > 0) {
            // 少数点桁数を取得
            intObj = (Integer) (getObject(clsBean, objBean,
                    decimalPointName));
        }

        try {
            result = getStringNumber(objData, isCurrency, intObj);
        } catch (ParseException e) {
            // 数値の文字列変換で、エラーが発生しました。値=[{0}] フィールド名=[{1}]
            GnomesAppException ex = new GnomesAppException(e);
            ex.setMessageNo(GnomesMessagesConstants.ME01_0099);
            Object[] errParam = {
                    objData,
                    colDef.getFieldName()
            };
            ex.setMessageParams(errParam);
            throw ex;
        }
        return result;
    }

    /**
     * オブジェクトの日付文字列取得
     * @param colDef カラム定義
     * @param objData データオブジェクト
     * @param isZonedDataTime タイムゾーンフラグ
     * @return オブジェクトの日付文字列
     * @throws GnomesAppException 例外
     */
    private String getDate(ImportExportColumnDef colDef, Object objData,
            boolean isZonedDataTime) throws GnomesAppException {

        String result = null;

        try {
            /*
            result = getStringDate(objData, colDef.getFormat(),
                    isZonedDataTime);
            */
            result = getStringDate(objData, colDef.getFormat());

        } catch (ParseException e) {
            // 日付の文字列変換時、エラーが発生しました。値=[{0}], フォーマット=[{1}], フィールド名=[{2}]
            GnomesAppException ex = new GnomesAppException(e);
            ex.setMessageNo(GnomesMessagesConstants.ME01_0100);
            Object[] errParam = {
                    objData,
                    colDef.getFormat(),
                    colDef.getFieldName()
            };
            ex.setMessageParams(errParam);
            throw ex;
        }
        return result;
    }

    /**
     * オブジェクトのイメージパターンタイトル取得
     * @param colDef カラム定義
     * @param objData データオブジェクト
     * @return
     */
    @SuppressWarnings("unchecked")
    private String getTitleImagePattern(ImportExportColumnDef colDef, Object objData) {

        String result = null;

        if (colDef.getMapImgPtn().containsKey(objData.toString())) {
            Map<String, Object> mapPtn = (Map<String, Object>) colDef.getMapImgPtn().get(objData.toString());
            if (mapPtn.containsKey(GnomesCTagDictionary.MAP_NAME_PTN_TITLE)) {
                result = (String) mapPtn.get(GnomesCTagDictionary.MAP_NAME_PTN_TITLE);
            }
        }

        return result;
    }
}
