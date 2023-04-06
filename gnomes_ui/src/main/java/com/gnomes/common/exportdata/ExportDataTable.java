package com.gnomes.common.exportdata;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.poi.ss.SpreadsheetVersion;

import com.gnomes.common.data.FileDownLoadData;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.importdata.ImportExportDefCsv;
import com.gnomes.common.importdata.ImportExportDefExcel;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.search.data.SearchInfoPack;
import com.gnomes.common.tags.GnomesCTagDictionary;
import com.gnomes.common.tags.GnomesCTagTableCommon;
import com.gnomes.common.util.StringUtils;

/**
 * 一覧エクスポート 処理クラス
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
public class ExportDataTable {

    // テーブル定義　ヘッダタグ　出力ファイル名
    private static final String HEAD_TAG_EXP_FILE_NAME = "export_filename";

    // テーブル定義　ヘッダタグ　出力文字コード
    private static final String HEAD_TAG_EXP_CHAR_SET = "export_charset";

    // テーブル定義　ヘッダタグ　出力シート
    private static final String HEAD_TAG_EXP_SHEET = "export_sheet";

    @Inject
    GnomesCTagTableCommon gnomesTagTableCommon;

    @Inject
    ExportDataCsv exportDataCsv;

    @Inject
    ExportDataExcel exportDataExcel;

    @Inject
    GnomesCTagDictionary gnomesTagDictionary;

    /**
     * コンストラクタ
     */
    public ExportDataTable() {
    }

    /**
     * 一覧エクスポート
     * @param def 一覧エクスポート定義情報
     * @param locale ロケール
     * @return ダウンロードファイル情報
     * @throws GnomesAppException
     */
    public FileDownLoadData doExport(ExportDataTableDef def,
            Locale locale) throws GnomesAppException {

        FileDownLoadData fileDownLoadData = new FileDownLoadData();
        String tableTagName = null;

        try {
            if (def == null) {
                throw new IllegalArgumentException("def not null");
            }

            tableTagName = def.getTableTagName();

            // テーブル辞書取得
            Map<String, Object> mapTableInfo = gnomesTagDictionary
                    .getTableInfo(tableTagName);

            // ファイル名の取得
            String fileNameId = (String) mapTableInfo
                    .get(HEAD_TAG_EXP_FILE_NAME);

            if (fileNameId == null) {
                throw new IllegalArgumentException(MessageFormat
                        .format("not found export_filename in dictId=[{0}]",
                                tableTagName));
            }
            String fileName = ResourcesHandler.getString(fileNameId, locale);

            // ファイル名の設定
            fileDownLoadData.setSaveFileName(fileName);

            // ファイル名の拡張子からファイルタイプを判別
            // 拡張子取得
            String suffix = StringUtils.getSuffix(fileName);

            // 拡張子からファイルタイプ判定
            ExportDataBase.ExportFileType type = null;
            for (ExportDataBase.ExportFileType d : ExportDataBase.ExportFileType
                    .values()) {
                for (String ext : d.getValue()) {
                    if (ext.equalsIgnoreCase(suffix)) {
                        type = d;
                        break;
                    }
                }
            }
            if (type == null) {
                throw new IllegalArgumentException(MessageFormat
                        .format("suffix=[{0}], type=[{1}]", suffix, type));
            }

            SearchInfoPack search = def.getSearch();

            // 辞書と検索条件からテーブル列情報を作成
            List<Map<String, Object>> lstTableInfo = null;

            // 検索条件が設定されている場合
            if (search != null) {
                // 検索条件の出力有無、出力順に従う
                lstTableInfo = gnomesTagTableCommon
                        .getTableColumnInfo(search, tableTagName);
            }

            byte[] fileByte = null;

            // Csv出力の場合
            if (type == ExportDataBase.ExportFileType.ExportFileType_FileType_Csv) {

                ImportExportDefCsv ImpExpDef;
                String charsetName = null;

                // 文字コードの取得
                String charsetId = (String) mapTableInfo
                        .get(HEAD_TAG_EXP_CHAR_SET);
                if (charsetId != null) {
                    charsetName = ResourcesHandler.getString(charsetId, locale);
                }

                // 検索条件なしの場合
                if (lstTableInfo == null) {
                    ImpExpDef = new ImportExportDefCsv(
                            gnomesTagDictionary, tableTagName, locale);

                    // 検索条件ありの場合
                } else {
                    ImpExpDef = new ImportExportDefCsv(
                            gnomesTagDictionary,
                            lstTableInfo, tableTagName, locale);
                }

                // CSV出力実行
                fileByte = exportDataCsv.beanToOpenCsv(
                        (ImportExportDefCsv) ImpExpDef, def.getDatas(),
                        charsetName, locale);

            } else if (type == ExportDataBase.ExportFileType.ExportFileType_FileType_Excel) {

                ImportExportDefExcel ImpExpDef;
                String sheetName = null;

                // エクセルファイルバージョン
                SpreadsheetVersion version = null;
                // 拡張子からエクセルファイルバージョン判定
                for (ExportDataExcel.ExcelFileVersion d : ExportDataExcel.ExcelFileVersion
                        .values()) {
                    for (String ext : d.getValue()) {
                        if (ext.equalsIgnoreCase(suffix)) {
                            version = d.getVersion();
                            break;
                        }
                    }
                }
                if (version == null) {
                    throw new IllegalArgumentException(MessageFormat.format(
                            "suffix=[{0}], version=[{1}]", suffix, version));
                }

                // シート名の取得
                String sheetId = (String) mapTableInfo.get(HEAD_TAG_EXP_SHEET);
                if (sheetId != null) {
                    sheetName = ResourcesHandler.getString(sheetId, locale);
                }

                // 検索条件なしの場合
                if (lstTableInfo == null) {
                    ImpExpDef = new ImportExportDefExcel(
                            gnomesTagDictionary, tableTagName,
                            sheetName,
                            locale);

                    // 検索条件ありの場合
                } else {
                    ImpExpDef = new ImportExportDefExcel(
                            gnomesTagDictionary,
                            lstTableInfo, tableTagName,
                            sheetName,
                            locale);
                }

                // EXCEL出力実行
                fileByte = exportDataExcel.beanToPoi(ImpExpDef, def.getDatas(),
                        version, locale);

            } else {
                throw new IllegalArgumentException(MessageFormat
                        .format("suffix=[{0}], type=[{1}]", suffix, type));
            }
            fileDownLoadData.setData(fileByte);

        } catch (NullPointerException | IllegalArgumentException
                | IOException e) {
            //   メッセージ： "一覧エクスポート処理でエラーが発生しました。 詳細はエラーメッセージを確認してください。 定義名： {0}")
            GnomesAppException ex = new GnomesAppException(e);
            ex.setMessageNo(GnomesMessagesConstants.ME01_0098);
            Object[] errParam = {
                    tableTagName
            };
            ex.setMessageParams(errParam);
            throw ex;
        }

        return fileDownLoadData;
    }

}
