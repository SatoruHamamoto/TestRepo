package com.gnomes.common.exportdata;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.importdata.ImportExportColumnDef;
import com.gnomes.common.importdata.ImportExportDefCsv;
import com.opencsv.CSVWriter;

/**
 * エクスポートデータCsv 処理クラス
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
public class ExportDataCsv extends ExportDataBase {

    @Inject
    CsvCommon csvCommon;

    /**
     * コンストラクター
     */
    public ExportDataCsv() {
        super();
    }

    /**
     * 最終行から追加
     * @param binary Execlファイルのバイナリー
     * @param charsetName 文字コード
     * @param addMap 追加情報 シート名, 追加データ
     * @param lastOffsetRow 追加開始行オフセット値
     * @return 作成したExeclファイルのバイナリー
     * @throws UnsupportedEncodingException
     */
    public byte[] addTail(
            byte[] binary,
            String charsetName, Map<String, List<String>> addMap,
            int lastOffsetRow) throws UnsupportedEncodingException {

        StringBuilder addData = new StringBuilder();

        // オフセット分改行追加
        for (int i = 0; i < lastOffsetRow; i++) {
            addData.append(System.lineSeparator());
        }

        // 追加データ追加
        boolean isStarted = false;
        for (Entry<String, List<String>> e : addMap.entrySet()) {
            for (String data : e.getValue()) {

                if (isStarted) {
                    addData.append(System.lineSeparator());
                } else {
                    isStarted = true;
                }
                addData.append(data);
            }
        }

        byte[] addByte = null;
        addByte = addData.toString().getBytes(charsetName);

        ByteBuffer byteBuf = ByteBuffer
                .allocate(binary.length + addByte.length);
        byteBuf.put(binary);
        byteBuf.put(addByte);
        byte[] outByte = byteBuf.array();

        return outByte;
    }

    /**
     * データリストをCsv出力
     * @param importExportDefinition インポートエクスポートデータCsv定義情報
     * @param data 出力元データリスト
     * @param charsetName 文字コード
     * @param locale ロケール
     * @return Csvファイル（バイト配列）
     * @throws IOException
     * @throws GnomesAppException
     */
    public byte[] beanToOpenCsv(ImportExportDefCsv importExportDefinition,
            List<?> data, String charsetName, Locale locale)
            throws IOException, GnomesAppException {

        String outCharsetName;

        // 文字コード指定なし
        if (charsetName == null || charsetName.length() == 0) {
            outCharsetName = csvCommon.getSysCharsetName();

            // 文字コード指定あり
        } else {
            outCharsetName = charsetName;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(baos,
                Charset.forName(outCharsetName));
        CSVWriter csvWriter = null;

        csvWriter = new CSVWriter(writer, csvCommon.getSysSeparator(),
                csvCommon.getSysQuotechar(), csvCommon.getSysEndLine());

        List<ImportExportColumnDef> importExportColumDefinitions = importExportDefinition
                .getImportExportColumDefinitions();

        // ヘッダー作成
        String headerLine[] = getHeaderLine(importExportColumDefinitions,
                locale);
        csvWriter.writeNext(headerLine);

        Class<?> clsRowData = null;

        if (data.size() > 0) {
            clsRowData = data.get(0).getClass();
        }

        // データ作成
        for (int i = 0; i < data.size(); i++) {
            String dataLine[] = this.getDataLine(importExportColumDefinitions,
                    clsRowData, data.get(i));
            csvWriter.writeNext(dataLine);
        }

        csvWriter.close();

        byte[] bytes = baos.toByteArray();

        return bytes;

    }

}
