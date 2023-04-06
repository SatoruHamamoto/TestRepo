package com.gnomes.common.importdata;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.gnomes.common.data.FileUpLoadData;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exportdata.CsvCommon;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.opencsv.CSVReader;

/**
 * インポートデータCsv 処理クラス
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
@Dependent
public class ImportDataCsv extends ImportDataBase {

    @Inject
    CsvCommon csvCommon;

    private static final String LINE_SEPARATOR_PATTERN = "\r\n|[\n\r\u2028\u2029\u0085]";

    /**
     * コンストラクター
     */
    public ImportDataCsv() {
    }

    /**
     * opencsvでcsvファイルをbeanに読込
     *
     * @param importExportDefinition インポートデータ 定義情報
     * @param inData ファイル情報
     * @param clazz beanクラス
     * @param errMap エラー設定先
     * @param locale ロケール
     * @return bean
     * @throws GnomesAppException 例外
     */
    public <T> List<T> opencsvToBean(
            ImportExportDefCsv importExportDefinition,
            FileUpLoadData inData,
            Class<T> clazz,
            Map<String, String[]> errMap,
            Locale locale) throws GnomesAppException {

        CSVReader reader = null;
        try {
            // ファイルタイプチェック
            if (checkFileType(ImportFileType.FileType_Csv, inData, errMap,
                    locale) == false) {
                return null;
            }

            // インポートエクスポート位置でソート
            List<ImportExportColumnDef> importExportColumDefinitions = importExportDefinition
                    .getImportExportColumDefinitions();

            // ColumnMap作成
            String[] columnMap = new String[importExportColumDefinitions
                    .size()];
            for (int i = 0; i < importExportColumDefinitions.size(); i++) {
                ImportExportColumnDef e = importExportColumDefinitions.get(i);
                columnMap[i] = e.getFieldName();
            }

            String outCharsetName;

            // 文字コード指定なし
            if (inData.getCharsetName() == null
                    || inData.getCharsetName().length() == 0) {
                outCharsetName = csvCommon.getSysCharsetName();

                // 文字コード指定あり
            } else {
                outCharsetName = inData.getCharsetName();
            }

            InputStream bais = new ByteArrayInputStream(inData.getData());
            InputStreamReader inputStreamReader = new InputStreamReader(bais,
                    outCharsetName);

            reader = new CSVReader(inputStreamReader,
                    csvCommon.getSysSeparator(), csvCommon.getSysQuotechar(),
                    0);

            // ヘッダ読み込み
            String[] headLine;
            headLine = reader.readNext();

            // ヘッダチェック
            if (checkHeader(
                    importExportDefinition,
                    inData.getFileName(),
                    headLine,
                    importExportColumDefinitions,
                    errMap,
                    locale) == false) {
                return null;
            }

            List<T> result = new ArrayList<>();

            //1行ごと読込処理
            String[] nextLine;
            int rowIndex = -1;
            long enterCount = 0;
            while ((nextLine = reader.readNext()) != null) {

                rowIndex++;

                // 読込項目数が定義情報と異なる場合
                if (nextLine.length != importExportColumDefinitions.size()) {
                    //行{0}：データを{1}件入力してください。データ件数：{3}
                    String[] errParam = {
                            String.format(FORMAT_ROW_COL,
                                    String.valueOf(rowIndex + 1)),
                            String.valueOf(importExportColumDefinitions.size()),
                            String.valueOf(nextLine.length)
                    };
                    String mapKey = String.format(FORMAT_ERR_KEY,
                            importExportDefinition.getKey(), rowIndex + 1, 0);
                    String[] params = makeErrMapParams(
                            GnomesMessagesConstants.MV01_0018, errParam);
                    errMap.put(mapKey, params);

                    //列ごとの処理は行わなず、次の行を処理
                    continue;
                }

                T row = clazz.newInstance();

                for (int i = 0; i < importExportColumDefinitions.size(); i++) {
                    ImportExportColumnDef colDef = importExportColumDefinitions
                            .get(i);

                    if (nextLine.length > i) {
                        //フィールド名よりフィールドを取得
                        String fieldName = colDef.getFieldName();
                        Field fld = clazz.getDeclaredField(fieldName);
                        fld.setAccessible(true);
                        //フィールドに読み込みデータを設定
                        fld.set(row, nextLine[i]);

                        //型チェック
                        try {
                            checkColData(locale, colDef, nextLine[i]);
                        } catch (ParseException e) {
                            // 変換エラー
                            addErrCol(errMap, importExportDefinition.getKey(),
                                    rowIndex, colDef, null, locale);
                        }
                    }
                }

                // 改行
                for (int i = 0; i < nextLine.length; i++) {
                    if (nextLine[i] != null && nextLine[i].length() > 0) {
                        enterCount = enterCount + (nextLine[i]
                                .split(LINE_SEPARATOR_PATTERN).length - 1);
                    }
                }

                result.add(row);
            }

            // 不正行があった（CSVフォーマット異常)
            isErrCsvFormat(importExportDefinition, reader, enterCount, errMap);

            reader.close();
            reader = null;

            return result;

        } catch (IOException
                | InstantiationException
                | IllegalAccessException
                | NoSuchFieldException
                | SecurityException e) {
            //   メッセージ： "CSVファイルの取込処理時にエラーが発生しました。 詳細はエラーメッセージを確認してください。 ファイル名： {0}")
            GnomesAppException ex = new GnomesAppException(e);
            ex.setMessageNo(GnomesMessagesConstants.ME01_0092);
            Object[] errParam = {
                    inData.getFileName()
            };
            ex.setMessageParams(errParam);
            throw ex;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    // 処理なし
                }
                reader = null;
            }
        }
    }

    /**
     * CSVフォーマットエラーチェック
     * @param importExportDefinition インポートデータ 定義情報
     * @param reader CSVリーダー
     * @param enterCount 改行数
     * @param errMap エラー情報
     * @return エラー有無
     */
    private boolean isErrCsvFormat(ImportExportDefCsv importExportDefinition,
            CSVReader reader, long enterCount, Map<String, String[]> errMap) {
        boolean result = false;

        // 不正行があった（CSVフォーマット異常)
        if (reader.getLinesRead() != reader.getRecordsRead() + enterCount) {

            result = true;

            String[] errParam = {
                    String.format(FORMAT_ROW_COL,
                            String.valueOf(reader.getRecordsRead()))
            };
            String mapKey = String.format(FORMAT_ERR_KEY,
                    importExportDefinition.getKey(),
                    reader.getRecordsRead(), 0);
            String[] params = makeErrMapParams(
                    GnomesMessagesConstants.ME01_0120, errParam);
            errMap.put(mapKey, params);

        }

        return result;
    }

}
