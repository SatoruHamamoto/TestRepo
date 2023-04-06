package com.gnomes.common.importdata;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.Dependent;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.gnomes.common.data.FileUpLoadData;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.GnomesLogMessageConstants;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;

/**
 * インポートデータExcel 処理クラス
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
public class ImportDataExcel extends ImportDataBase {

    /**
     * コンストラクター
     */
    public ImportDataExcel() {
    }

    /**
     * poiでexcelファイルをbeanに読込
     *
     * @param sheetName シート名
     * @param importExportDefinition インポートデータ 定義情報
     * @param inData ファイル情報
     * @param clazz beanクラス
     * @param errMap エラー設定先
     * @param locale ロケール
     * @return bean
     * @throws GnomesAppException 例外
     */
    public <T> List<T> poiToBean(
            ImportExportDefExcel importExportDefinition,
            FileUpLoadData inData,
            Class<T> clazz,
            Map<String, String[]> errMap,
            Locale locale) throws GnomesAppException {

        // ファイルタイプチェック
        if (checkFileType(ImportFileType.FileType_Excel, inData, errMap,
                locale) == false) {
            return null;
        }

        // インポートエクスポート位置でソート
        List<ImportExportColumnDef> importExportColumDefinitions = importExportDefinition
                .getImportExportColumDefinitions();

        List<T> result = new ArrayList<>();
        Workbook wb = null;

        try {
            InputStream inp = new ByteArrayInputStream(inData.getData());

            //共通インターフェースを扱える、WorkbookFactoryで読み込む
            wb = WorkbookFactory.create(inp);

            // シート取得
            Sheet sheet = null;
            String sheetName = importExportDefinition.getSheetName();
            if (sheetName != null && sheetName.length() > 0) {
                sheet = wb.getSheet(sheetName);
            } else {
                sheet = wb.getSheetAt(0);
            }

            if (sheet == null) {
                // シート存在なしエラー
                //   メッセージ： "{0}シートが存在しません。ファイル名：{1}"
                String param = "";
                if (sheetName != null && sheetName.length() > 0) {
                    param = sheetName;
                }
                String[] errParam = {
                        GnomesMessagesConstants.ME01_0093,
                        param,
                        inData.getFileName()
                };
                errMap.put(inData.getFileName(), errParam);

                return null;
            }

            // ヘッダ 1行目
            String[] headLine = null;
            Row rowHead = sheet.getRow(0);
            if (rowHead != null) {
                // 列数
                int headColNum = rowHead.getLastCellNum();

                if (headColNum > 0) {
                    headLine = new String[headColNum];
                    boolean isHeadStringErr = false;
                    for (int i = 0; i < headColNum; i++) {
                        Cell cell = rowHead.getCell(i);
                        // 文字列で取得
                        try {
                            String val = cell.getStringCellValue();
                            headLine[i] = val;
                        } catch (IllegalStateException ie) {
                            // ヘッダ文字列で取得できなかった
                            isHeadStringErr = true;
                            // "{0}ヘッダの列{1}:セルは文字列形式で作成してください。"
                            // セルは文字列形式で作成してください。
                            String mesDetail = MessagesHandler.getString(
                                    GnomesLogMessageConstants.ME01_0096,
                                    locale);
                            putErrHead(errMap, importExportDefinition, i,
                                    mesDetail, locale);
                        } catch (NullPointerException ne) {
                            headLine[i] = "";
                        }
                    }
                    if (isHeadStringErr) {
                        return null;
                    }
                }
            }

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

            // データ 1行づつ読み込み
            for (int rowIndex = 1; rowIndex <= sheet
                    .getLastRowNum(); rowIndex++) {

                Row rowData = sheet.getRow(rowIndex);
                T addData = clazz.newInstance();

                for (int i = 0; i < importExportColumDefinitions.size(); i++) {

                    ImportExportColumnDef colDef = importExportColumDefinitions
                            .get(i);
                    Cell cell = rowData.getCell(i);
                    // 文字列で取得
                    String val;
                    try {
                        val = cell.getStringCellValue();
                    } catch (IllegalStateException ie) {

                        // セルは文字列形式で作成してください。
                        String mesDetail = MessagesHandler.getString(
                                GnomesLogMessageConstants.ME01_0096, locale);
                        putErrCol(errMap, importExportDefinition.getKey(),
                                rowIndex - 1, colDef, sheetName, mesDetail,
                                locale);
                        continue;
                    } catch (NullPointerException ne) {
                        val = "";
                    }

                    //フィールド名よりフィールドを取得
                    String fieldName = colDef.getFieldName();
                    Field fld = clazz.getDeclaredField(fieldName);
                    fld.setAccessible(true);
                    //フィールドに読み込みデータを設定
                    fld.set(addData, val);

                    //型チェック
                    try {
                        checkColData(locale, colDef, val);
                    } catch (ParseException e) {
                        // 変換エラー
                        addErrCol(errMap, importExportDefinition.getKey(),
                                rowIndex - 1, colDef, sheetName, locale);
                    }
                }
                result.add(addData);

            }
            wb.close();
            wb = null;
        } catch (InvalidFormatException
                | IOException
                | InstantiationException
                | IllegalAccessException
                | NoSuchFieldException
                | SecurityException
                | org.apache.poi.EmptyFileException e) {
            //   メッセージ： "EXCELファイルの取込処理時にエラーが発生しました。 詳細はエラーメッセージを確認してください。 ファイル名： {0}")
            GnomesAppException ex = new GnomesAppException(e);
            ex.setMessageNo(GnomesMessagesConstants.ME01_0094);
            Object[] errParam = {
                    inData.getFileName()
            };
            ex.setMessageParams(errParam);
            throw ex;

        } finally {
            if (wb != null) {
                try {
                    wb.close();
                } catch (IOException e) {
                    // 処理なし
                }
            }
        }

        return result;
    }

}
