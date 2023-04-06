package com.gnomes.common.exportdata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.gnomes.common.constants.SystemDefConstants;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.importdata.ImportExportDefExcel;
import com.gnomes.system.dao.MstrSystemDefineDao;
import com.gnomes.system.entity.MstrSystemDefine;

/**
 * エクスポートデータExcel 処理クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/08/24 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class ExportDataExcel extends ExportDataBase {

    @Inject
    MstrSystemDefineDao mstrSystemDefineDao;

    /**
     * エクセルファイルバージョン
     */
    public enum ExcelFileVersion {
        ExcelFileVersion97 {
            @Override
            public String[] getValue() {
                String[] stringArray = { "xls" };
                return stringArray;
            }

            @Override
            public SpreadsheetVersion getVersion() {
                return SpreadsheetVersion.EXCEL97;
            }
        },
        ExcelFileVersion2007 {
            @Override
            public String[] getValue() {
                String[] stringArray = { "xlsx" };
                return stringArray;
            }

            @Override
            public SpreadsheetVersion getVersion() {
                return SpreadsheetVersion.EXCEL2007;
            }
        };

        public String[] getValue() {
            return null;
        }

        public SpreadsheetVersion getVersion() {
            return null;
        }
    }

    /**
     * RGBカラー文字列区切り文字
     */
    private static final String RGB_SPLIT_CHAR = ",";

    /**
     * コンストラクター
     */
    public ExportDataExcel() {
        super();
    }

    /**
     * 最終行から追加
     * @param binary Execlファイルのバイナリー
     * @param addMap 追加情報 シート名, 追加データ
     * @param lastOffsetRow 追加開始行オフセット値
     * @return 作成したExeclファイルのバイナリー
     * @throws IOException
     * @throws InvalidFormatException
     * @throws EncryptedDocumentException
     * @throws GnomesAppException
     */
    public byte[] addTail(byte[] binary, Map<String, List<String>> addMap,
            int lastOffsetRow)
            throws IOException, EncryptedDocumentException,
            InvalidFormatException, GnomesAppException {
        Workbook wb = null;
        byte[] result = null;
        InputStream inp = new ByteArrayInputStream(binary);

        //共通インターフェースを扱える、WorkbookFactoryで読み込む
        try {
            String fontName = getSysFontName();

            wb = WorkbookFactory.create(inp);

            // データスタイル
            CellStyle dataCellStyle = wb.createCellStyle();
            Font dataFont = wb.createFont();
            dataFont.setFontName(fontName);
            //旧 dataFont.setBoldweight(Font.BOLDWEIGHT_NORMAL);
            dataFont.setBold(false);
            dataCellStyle.setFont(dataFont);


            for (Entry<String, List<String>> e : addMap.entrySet()) {

                String sheetName = e.getKey();

                // シート取得
                Sheet sheet = null;
                if (sheetName != null && sheetName.length() > 0) {
                    sheet = wb.getSheet(sheetName);
                } else {
                    sheet = wb.getSheetAt(0);
                }

                if (sheet == null) {
                    throw new IllegalArgumentException(
                            "not found sheet sheetName=[" + sheetName + "]");
                }

                // 最終行 + 1 + 追加開始行オフセット値
                int addRowNum = sheet.getLastRowNum() + 1 + lastOffsetRow;

                // 追加データの追加
                for (String addData : e.getValue()) {
                    //Rows(行にあたる)を作る。Rowsは0始まり。
                    Row row = sheet.createRow(addRowNum);
                    //cell(列にあたる)を作って、そこに値を入れる。
                    Cell cell = row.createCell(0);
                    cell.setCellValue(addData);
                    cell.setCellStyle(dataCellStyle);
                    addRowNum++;
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            // メモリ上に出力してすぐに取得
            wb.write(out);
            result = out.toByteArray();
            out.close();

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

    /**
     * データリストをExcel出力
     * @param importExportDefinition インポートエクスポートデータExcel定義情報
     * @param data 出力元データリスト
     * @param version 出力エクセルバージョン
     * @param locale ロケール
     * @return Excelファイル（バイト配列）
     * @throws IOException
     * @throws GnomesAppException
     */
    public byte[] beanToPoi(ImportExportDefExcel importExportDefinition,
            List<?> data, SpreadsheetVersion version, Locale locale)
            throws IOException, GnomesAppException {

        Workbook wb = null;
        Sheet sheet = null;
        byte[] result = null;

        try {

            String fontName = getSysFontName();
            short excel97HdrColIndex = getSysExcel97HeaderFillForegoundColorIndex();
            int[] hdrColRgb = getSysHeaderFillForegoundColor();

            wb = createEmptyWorkbook(version);

            if (importExportDefinition.getSheetName() == null) {
                sheet = wb.createSheet();
            } else {
                sheet = wb.createSheet(importExportDefinition.getSheetName());
            }

            // ヘッダスタイル
            CellStyle headCellStyle = wb.createCellStyle();
            Font headFont = wb.createFont();
            headFont.setFontName(fontName);
            //旧 headFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
            headFont.setBold(true);
            headCellStyle.setFont(headFont);
            //旧headCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
            headCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            setFillForegroundColor(wb, headCellStyle, excel97HdrColIndex,
                    hdrColRgb[0], hdrColRgb[1], hdrColRgb[2]);

            // データスタイル
            CellStyle dataCellStyle = wb.createCellStyle();
            Font dataFont = wb.createFont();
            dataFont.setFontName(fontName);
            //旧dataFont.setBoldweight(Font.BOLDWEIGHT_NORMAL);
            dataFont.setBold(false);
            dataCellStyle.setFont(dataFont);

            // ヘッダー作成
            String headerLine[] = getHeaderLine(
                    importExportDefinition.getImportExportColumDefinitions(),
                    locale);

            Row row = sheet.createRow(0);
            for (int col = 0; col < headerLine.length; col++) {
                Cell cell = row.createCell(col);
                cell.setCellValue(headerLine[col]);
                cell.setCellStyle(headCellStyle);
            }

            Class<?> clsRowData = null;

            if (data.size() > 0) {
                clsRowData = data.get(0).getClass();
            }

            // データ作成
            for (int i = 0; i < data.size(); i++) {
                String dataLine[] = this.getDataLine(
                        importExportDefinition
                                .getImportExportColumDefinitions(),
                        clsRowData, data.get(i));

                row = sheet.createRow(1 + i);

                for (int col = 0; col < dataLine.length; col++) {
                    Cell cell = row.createCell(col);
                    cell.setCellValue(dataLine[col]);
                    cell.setCellStyle(dataCellStyle);
                }
            }

            // セル幅自動調整
            for (int col = 0; col < headerLine.length; col++) {
                sheet.autoSizeColumn(col, true);
            }

            // 出力
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            // メモリ上に出力してすぐに取得
            wb.write(out);
            result = out.toByteArray();
            out.close();

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

    /**
     * セルスタイルに前景色を設定
     * @param wb ワークブック
     * @param cellStyle セルスタイル
     * @param index パレットIndex(EXCEL97)
     * @param r R
     * @param g G
     * @param b B
     */
    private void setFillForegroundColor(Workbook wb, CellStyle cellStyle,
            short index, int r, int g, int b) {

        if (cellStyle instanceof HSSFCellStyle) {
            HSSFPalette palette = ((HSSFWorkbook) wb).getCustomPalette();
            palette.setColorAtIndex(index, (byte) r, (byte) g, (byte) b);

            cellStyle.setFillForegroundColor(index);

        } else if (cellStyle instanceof XSSFCellStyle) {

            ((XSSFCellStyle) cellStyle).setFillForegroundColor(
                    new XSSFColor(new java.awt.Color(r, g, b)));
        } else {
            throw new IllegalStateException("Unexpected cellStyle: "
                    + (cellStyle == null ? "null" : cellStyle.getClass())
                    + ")");
        }
    }

    /**
     * 新規ワークブック作成
     * @param version 作成バージョン
     * @return 新規ワークブック
     */
    private Workbook createEmptyWorkbook(SpreadsheetVersion version) {
        switch (version) {
        case EXCEL97:
            return new HSSFWorkbook();
        case EXCEL2007:
            return new XSSFWorkbook();
        default:
            throw new IllegalArgumentException(version.toString());
        }
    }

    /**
     * システム定義マスタのエクセルフォント名取得
     * @return フォント名
     * @throws GnomesAppException 例外
     */
    private String getSysFontName() throws GnomesAppException {

        MstrSystemDefine item = mstrSystemDefineDao
                .getMstrSystemDefine(
                        SystemDefConstants.IMPORT_EXPORT,
                        SystemDefConstants.IMPORT_EXPORT_EXCEL_FONT);

        return item.getChar1();
    }

    /**
     *システム定義マスタのEXCEL97ファイルヘッダ色指定パレットIndex取得
     * @return パレットIndex
     * @throws GnomesAppException 例外
     */
    private short getSysExcel97HeaderFillForegoundColorIndex()
            throws GnomesAppException {

        MstrSystemDefine item = mstrSystemDefineDao
                .getMstrSystemDefine(
                        SystemDefConstants.IMPORT_EXPORT,
                        SystemDefConstants.IMPORT_EXPORT_EXCEL97_HEADER_FILLFOREGROUND_COLORINDEX);

        return item.getNumeric1().shortValue();
    }

    /**
     * システム定義マスタのEXCELファイルヘッダー色(RGB)取得
     * @return 色(RGB)
     * @throws GnomesAppException 例外
     */
    private int[] getSysHeaderFillForegoundColor() throws GnomesAppException {
        int[] colorRgb = new int[3];

        MstrSystemDefine item = mstrSystemDefineDao
                .getMstrSystemDefine(
                        SystemDefConstants.IMPORT_EXPORT,
                        SystemDefConstants.IMPORT_EXPORT_EXCEL_HEADER_FILLFOREGROUND_COLOR);

        String strRgb = item.getChar1();

        String[] rgb = strRgb.split(RGB_SPLIT_CHAR);

        colorRgb[0] = Integer.parseInt(rgb[0]);
        colorRgb[1] = Integer.parseInt(rgb[1]);
        colorRgb[2] = Integer.parseInt(rgb[2]);

        return colorRgb;
    }

}
