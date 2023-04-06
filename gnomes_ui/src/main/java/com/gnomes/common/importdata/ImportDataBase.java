package com.gnomes.common.importdata;

import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.gnomes.common.data.FileUpLoadData;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.importdata.ImportExportColumnDef.ImportExportColumnDataType;
import com.gnomes.common.resource.GnomesLogMessageConstants;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.StringUtils;

/**
 * インポートデータ 基底クラス
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
public class ImportDataBase {

    // 行と列の文字列化フォーマット
    public static final String FORMAT_ROW_COL = "%6s";

    // エラー情報のキーフォーマット
    public static final String FORMAT_ERR_KEY = "key=%s,row=%d,col=%d";

    // エラー情報のキーフォーマットの追加情報（シート名)
    public static final String FORMAT_ERR_KEY_OPT_SHEET_NAME = ",sheetname=%s";

    // エラー情報のキーパターン
    public static final String PATTERN_ERR_KEY = "(key=(.+?),row=(.+?),col=(.+?))";

    // エラー情報のキーの追加情報（シート名）パターン
    public static final String PATTERN_ERR_KEY_POT_SHEET_NAME = "sheetname=(.+)";

    /**
     * コンストラクター
     */
    public ImportDataBase() {
    }

    /**
     * インポートファイルタイプ
     */
    public enum ImportFileType {
        FileType_Csv {
            @Override
            public String[] getValue() {
                String[] stringArray = { "csv" };
                return stringArray;
            }
        }, // CSV
        FileType_Excel {
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
     * ファイルタイプチェック
     * @param type ファイルタイプ
     * @param inData ファイル情報
     * @param errMap エラー追加先
     * @param locale 言語
     * @return boolean true:エラーなし,false:エラーあり
     * @throws GnomesAppException 該当ファイルタイプでない
     */
    protected boolean checkFileType(
            ImportFileType type,
            FileUpLoadData inData,
            Map<String, String[]> errMap,
            Locale locale) {
        // 拡張子チェック
        String suffix = StringUtils.getSuffix(inData.getFileName());
        for (String ext : type.getValue()) {
            if (ext.equalsIgnoreCase(suffix)) {
                return true;
            }
        }
        //   メッセージ： "{0}を選択してください。ファイル名：{1}"
        String typeLabel = ResourcesHandler.getString(type.toString(), locale);
        String[] errParam = {
                GnomesMessagesConstants.ME01_0087,
                typeLabel,
                inData.getFileName()
        };
        errMap.put(inData.getFileName(), errParam);
        return false;
    }

    /**
     * ヘッダーチェック
     * @param sheetName
     * @param head ヘッダ
     * @param importExportColumDefinitions カラム定義情報
     * @param errMap エラー追加先
     * @param locale 言語
     * @return boolean true:エラーなし,false:エラーあり
     * @throws GnomesAppException ヘッダチェックエラー
     */
    protected boolean checkHeader(
            ImportExportDefBase def,
            String fileName,
            //            String sheetName,
            String[] head,
            List<ImportExportColumnDef> importExportColumDefinitions,
            Map<String, String[]> errMap,
            Locale locale) {

        // Excel特有
        String sheetStr = "";
        String sheetName = getExcelSheetName(def);
        if (sheetName != null && sheetName.length() > 0) {
            // "シート「{0}」："
            sheetStr = MessagesHandler.getString(
                    GnomesLogMessageConstants.ME01_0088, locale, sheetName);
        }

        if (head == null) {
            //   メッセージ： "{0}ヘッダを入力してください。ファイル名： {1}""
            String[] errParam = {
                    sheetStr,
                    fileName
            };
            addErrHead(errMap, def.getKey(), null, GnomesMessagesConstants.ME01_0089,
                    errParam);
            return false;
        }

        if (head.length != importExportColumDefinitions.size()) {
            //   メッセージ： "{0}ヘッダの項目数は{1}個を入力してください。ファイル名：{2} 項目数：{3}"
            String[] errParam = {
                    sheetStr,
                    String.valueOf(importExportColumDefinitions.size()),
                    fileName,
                    String.valueOf(head.length)
            };
            addErrHead(errMap, def.getKey(), sheetName, GnomesMessagesConstants.ME01_0090,
                    errParam);
            return false;
        }

        boolean isNoError = true;
        for (int i = 0; i < head.length; i++) {
            String defStr = ResourcesHandler.getString(
                    importExportColumDefinitions.get(i).getHeaderResouceId(),
                    locale);
            if (defStr.equals(head[i]) == false) {

                String headStr = "--";
                if (head[i] != null) {
                    headStr = head[i];
                }

                // 項目名は「{0}」を入力してください。項目名：{1}
                String mesDetail = MessagesHandler.getString(
                        GnomesLogMessageConstants.ME01_0091, locale, defStr,
                        headStr);
                putErrHead(errMap, def, i, mesDetail, locale);

                isNoError = false;
            }
        }
        return isNoError;
    }

    /**
     * errMapのパラメータ作成
     * @param messageNo メッセージNo
     * @param mesParams メッセージパラメータ
     * @return errMapのパラメータ
     */
    protected String[] makeErrMapParams(String messageNo, String[] mesParams) {
        int msgParamCount = 0;
        if (mesParams != null) {
            msgParamCount = mesParams.length;
        }
        String[] params = new String[1 + msgParamCount];
        params[0] = messageNo;

        for (int i = 0; i < msgParamCount; i++) {
            params[1 + i] = mesParams[i];
        }
        return params;
    }

    /**
     * シート名取得
     * @param def インポート定義
     * @return シート名
     */
    protected String getExcelSheetName(ImportExportDefBase def) {
        String sheetName = null;
        // Excel判定
        if (def instanceof ImportExportDefExcel) {
            sheetName = ((ImportExportDefExcel) def).getSheetName();
        }
        return sheetName;
    }


    /**
     * エラー設定 カラムなし
     * @param errMap エラー設定オブジェクト
     * @param key 辞書ID
     * @param messageNo メッセージNo
     * @param mesParams メッセージパラメータ
     */
    protected void addErr(Map<String, String[]> errMap, String key,
            String messageNo, String[] mesParams) {

        String mapKey = String.format("key=%s", key);
        String[] params = makeErrMapParams(messageNo, mesParams);
        errMap.put(mapKey, params);
    }

    /**
     * エラー設定 ヘッダ カラムなし
     * @param errMap エラー設定オブジェクト
     * @param key 辞書ID
     * @param sheetName シート名
     * @param messageNo メッセージNo
     * @param mesParams メッセージパラメータ
     */
    protected void addErrHead(Map<String, String[]> errMap, String key,
            String sheetName,
            String messageNo, String[] mesParams) {

        String mapKey = String.format(FORMAT_ERR_KEY, key, 0, 0);
        // シート名あり
        if (sheetName != null && sheetName.length() > 0) {
            mapKey = mapKey
                    + String.format(FORMAT_ERR_KEY_OPT_SHEET_NAME, sheetName);
        }
        String[] params = makeErrMapParams(messageNo, mesParams);
        errMap.put(mapKey, params);
    }


    /**
     * エラー設定（ヘッダ情報）
     * @param errMap エラー設定オブジェクト
     * @param key 辞書ID
     * @param colIndex カラムIndex
     * @param sheetName シート名
     * @param mesDetail メッセージ詳細
     * @param locale ロケール
     */
    protected void putErrHead(
            Map<String, String[]> errMap,
            ImportExportDefBase def,
            int colIndex,
            String mesDetail,
            Locale locale)

    {
        String mapKey = String.format(FORMAT_ERR_KEY, def.getKey(), 0,
                colIndex);

        // Excel特有
        String sheetStr = "";
        String sheetName = getExcelSheetName(def);
        if (sheetName != null && sheetName.length() > 0) {
            // "シート「{0}」："
            sheetStr = MessagesHandler.getString(
                    GnomesLogMessageConstants.ME01_0088, locale, sheetName);

            // マップキーにシート名を付加
            mapKey = mapKey
                    + String.format(FORMAT_ERR_KEY_OPT_SHEET_NAME, sheetName);
        }

        // {0}ヘッダの列{1}:{2}
        // {2}は        リクエストチェックメッセージ
        String messageNo = GnomesMessagesConstants.MV01_0019;
        String[] params = {
                messageNo,
                sheetStr,
                String.format(FORMAT_ROW_COL, String.valueOf(colIndex)),
                mesDetail };

        errMap.put(mapKey, params);

    }

    /**
     * エラー設定 カラム指定
     * @param errMap エラー設定オブジェクト
     * @param key 辞書ID
     * @param rowIndex 行
     * @param colDef 列情報
     * @param locale ロケール
     */
    protected void addErrCol(
            Map<String, String[]> errMap,
            String key, int rowIndex,
            ImportExportColumnDef colDef,
            String sheetName,
            Locale locale) {
        String resId = colDef.getHeaderResouceId();
        String colLabel = ResourcesHandler.getString(resId, locale);
        String typeLabel = getStringDataType(colDef.getDataType(), locale);
        String format = colDef.getFormat();

        String mesDetail;
        if (format == null) {
            mesDetail = MessagesHandler.getString(
                    GnomesLogMessageConstants.MV01_0001, locale, colLabel,
                    typeLabel);
        } else {
            mesDetail = MessagesHandler.getString(
                    GnomesLogMessageConstants.MV01_0016, locale, colLabel,
                    typeLabel, format);
        }

        putErrCol(errMap, key, rowIndex, colDef, sheetName, mesDetail, locale);
    }

    /**
     * エラー設定
     * @param errMap エラー設定オブジェクト
     * @param key 辞書ID
     * @param rowIndex 行
     * @param colDef 列情報
     * @param sheetName シート名
     * @param mesDetail メッセージ詳細
     * @param locale ロケール
     */
    protected void putErrCol(
            Map<String, String[]> errMap,
            String key, int rowIndex,
            ImportExportColumnDef colDef,
            String sheetName,
            String mesDetail,
            Locale locale) {

        String mapKey = String.format(FORMAT_ERR_KEY, key, rowIndex + 1,
                colDef.getPosition());

        String sheetStr = "";
        if (sheetName != null && sheetName.length() > 0) {
            // "シート「{0}」："
            sheetStr = MessagesHandler.getString(
                    GnomesLogMessageConstants.ME01_0088, locale, sheetName);

            // マップキーにシート名を付加
            mapKey = mapKey
                    + String.format(FORMAT_ERR_KEY_OPT_SHEET_NAME, sheetName);

        }

        // 行    11,列     1：個数は数値を入力してください。
        // {0}行{1},列{2}：{3}
        // {3}は        リクエストチェックメッセージ
        String messageNo = GnomesMessagesConstants.MV01_0017;
        String[] params = {
                messageNo,
                sheetStr,
                String.format(FORMAT_ROW_COL, String.valueOf(rowIndex + 1)),
                String.format(FORMAT_ROW_COL,
                        String.valueOf(colDef.getPosition())),
                mesDetail };

        errMap.put(mapKey, params);
    }

    /**
     * カラム値チェック
     * @param locale ロケール
     * @param colDef 列情報
     * @param value 検証値
     * @throws ParseException 型変換エラー
     */
    protected void checkColData(
            Locale locale, ImportExportColumnDef colDef, String value)
            throws ParseException {

        if (value != null
                && (colDef.getDataType() != ImportExportColumnDataType.TEXT)) {

            if (colDef.getDataType() == ImportExportColumnDataType.NUMBER ||
                    colDef.getDataType() == ImportExportColumnDataType.CURRENCY) {
                // 通貨、数値の型チェック
                boolean isCurrency = false;
                if (colDef
                        .getDataType() == ImportExportColumnDataType.CURRENCY) {
                    isCurrency = true;
                }
                ConverterUtils.stringToNumber(isCurrency, value);
                //                stringToNumber(isCurrency, value);
            } else {
                // 日付型のチェック
                String format = colDef.getFormat();
                ConverterUtils.stringToDateFormat(value, format);
            }
        }
    }

    /**
     * データタイプの文言取得
     * @param dataType 取得データタイプ
     * @return 取得した文言
     */
    private String getStringDataType(ImportExportColumnDataType dataType,
            Locale locale) {
        String result = null;
        switch (dataType) {
        case NUMBER:
            result = ResourcesHandler
                    .getString(GnomesResourcesConstants.YY01_0009, locale);
            break;

        case CURRENCY:
            result = ResourcesHandler
                    .getString(GnomesResourcesConstants.YY01_0010, locale);
            break;

        case DATE:
        case ZONEDDATETIME:
            result = ResourcesHandler
                    .getString(GnomesResourcesConstants.YY01_0011, locale);
            break;

        default:
            throw new GnomesException(GnomesMessagesConstants.ME01_0004);
        }

        return result;
    }

}
