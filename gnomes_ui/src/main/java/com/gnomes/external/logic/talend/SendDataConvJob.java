package com.gnomes.external.logic.talend;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonEnums.FileFormat;
import com.gnomes.common.constants.CommonEnums.FormatId;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseJobLogic;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.external.data.DataDefine;
import com.gnomes.external.data.FileDefine;

/**
 * 送信伝文データ変換
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/14 KCC/I.SHibasaka           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class SendDataConvJob extends BaseJobLogic {

    /** 定数：半角スペース. */
    private static final String HALF_SPACE = " ";

    /**
     * 送信伝文データ変換.
     * <pre>
     * 送信伝文データをマスタ定義に従って変換します。
     * </pre>
     * @param sendDataMappingList 送信ファイルマッピングデータリスト
     * @param fileDefine 外部IFファイル構成定義
     * @param dataDefineList 外部IFデータ項目定義リスト
     * @return
     * @throws GnomesAppException
     */
    @ErrorHandling
    @TraceMonitor
    public List<String> process(List<Map<String, String>> sendDataMappingList,
            FileDefine fileDefine, List<DataDefine> dataDefineList) throws GnomesAppException {

        List<String> sendConvDataList = new ArrayList<>();

        // ファイル形式が CSV/TSV の場合
        if (FileFormat.Csv.equals(FileFormat.getEnum(fileDefine.getFile_format())) || FileFormat.Tsv.equals(FileFormat.getEnum(fileDefine.getFile_format()))) {
            // CSV区切り文字
            String delimiter;
            if(FileFormat.Tsv.equals(FileFormat.getEnum(fileDefine.getFile_format()))){
                delimiter = "\t";
            } else {
                delimiter = fileDefine.getCsv_delimiter();
            }
            // 文字列の括り文字(nullの場合は空文字)
            String charBundle = "";
            if(fileDefine.getChar_bundle() != null) {
                charBundle = fileDefine.getChar_bundle();
            } 
            // 変換文字列
            StringBuilder convDelimiterData = new StringBuilder();

            // 送信ファイルマッピングデータの件数分繰り返す
            for (Map<String, String> sendDataMapping : sendDataMappingList) {

                StringBuilder convBundleData = new StringBuilder();

                // 外部IFデータ項目定義の件数分繰り返す
                for (DataDefine dataDefine: dataDefineList) {

                    if (!sendDataMapping.containsKey(dataDefine.getData_item_id())) {
                        continue;
                    }

                    FormatId formatId = FormatId.getEnum(dataDefine.getFormat_id());

                    convDelimiterData.setLength(0);
                    convDelimiterData.append(charBundle);
                    //固定値の場合
                    if(FormatId.FixedValue.equals(formatId)){
                    	convDelimiterData.append(dataDefine.getFixed_value_string());
                    }
                    //数値型・小数点有りの場合
                    else if(FormatId.BigDecimal.equals(formatId)){
                    	String data = (sendDataMapping.get(dataDefine.getData_item_id()));
                    	//Nullや空白なら変換しない
                    	if (StringUtil.isNullOrEmpty(data)) {
                    		convDelimiterData.append(data);
                    	} else {
                    		convDelimiterData.append(
                    				ConverterUtils.decimalPadFraction(new BigDecimal(data),
                    						fileDefine.getDecimal_length()));
                    	}
                    } else {
                    	convDelimiterData.append(sendDataMapping.get(dataDefine.getData_item_id()));
                    }
                    convDelimiterData.append(charBundle);

                    if (convBundleData.length() > 0) {
                        convBundleData.append(delimiter);
                    }
                    convBundleData.append(convDelimiterData.toString());
                }
                sendConvDataList.add(convBundleData.toString());
            }

        }
        // ファイル形式が 固定長の場合
        else if (FileFormat.FixedLength.equals(FileFormat.getEnum(fileDefine.getFile_format()))) {

            // 送信ファイルマッピングデータの件数分繰り返す
            for (Map<String, String> sendDataMapping : sendDataMappingList) {
                StringBuilder convBundleData = new StringBuilder();


                // 外部IFデータ項目定義の件数分繰り返す
                for (DataDefine dataDefine : dataDefineList) {

                    if (!sendDataMapping.containsKey(dataDefine.getData_item_id())) {
                        continue;
                    }

                    // データ
                    String data = sendDataMapping.get(dataDefine.getData_item_id());

                    FormatId formatId = FormatId.getEnum(dataDefine.getFormat_id());

                    // 文字列型または日付型の場合
                    if (FormatId.String.equals(formatId) || FormatId.Date.equals(formatId)) {
                        convBundleData.append(ConverterUtils.rpad(data, HALF_SPACE, dataDefine.getData_length()));
                    }
                    // 数値型・小数点無しの場合
                    else if (FormatId.Integer.equals(formatId)) {
                        if (StringUtil.isNullOrEmpty(data)) {
                            convBundleData.append(ConverterUtils.rpad("", HALF_SPACE, dataDefine.getData_length()));
                        } else {
                            convBundleData.append(ConverterUtils.rpad(data, "0", dataDefine.getData_length()));
                        }
                    }
                    // 数値型・小数点有りの場合
                    else if (FormatId.BigDecimal.equals(formatId)) {

                        if (StringUtil.isNullOrEmpty(data)) {
                            convBundleData.append(ConverterUtils.rpad("", HALF_SPACE,
                                    dataDefine.getData_length()));
                        } else {
                        	//整数値の桁数は全体桁数 - 小数桁数 - 小数点(.)1文字分
                            convBundleData.append(
                                    ConverterUtils.decimalPad(new BigDecimal(data),
                                            dataDefine.getData_length() - fileDefine.getDecimal_length() - 1, fileDefine.getDecimal_length()));

                        }

                    }
                    //固定値の場合
                    else if(FormatId.FixedValue.equals(formatId)) {
                    	convBundleData.append(ConverterUtils.rpad(dataDefine.getFixed_value_string(), HALF_SPACE, dataDefine.getData_length()));
                    }

                }

                if (fileDefine.getData_length() < convBundleData.toString().getBytes().length) {
                    // 送信電文データ変換結果、データ項目長が指定桁数を超えています。
                    // （ファイル名称：{0}、データ項目長：{1}、送信伝文文字列長：{2}）
                    throw exceptionFactory.createGnomesAppException(null,
                            GnomesMessagesConstants.ME01_0164,
                            new Object[]{fileDefine.getFile_name(),
                                    fileDefine.getData_length(),
                                    convBundleData.toString().getBytes().length});

                }

                sendConvDataList.add(convBundleData.toString());

            }

        } else if(FileFormat.Xml.equals(FileFormat.getEnum(fileDefine.getFile_format()))) {
            // XMLの送信は現在未対応のためエラーメッセージ表示
            // XML形式での外部I/F送信は対応していません。マスタ定義を修正してください。ファイル種別:{0}、ファイル形式{1}、ファイル名称：{2} */
            throw exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0256,
                    new Object[]{fileDefine.getFile_type(),
                            fileDefine.getFile_format(),
                            fileDefine.getFile_name()});
        }

        return sendConvDataList;

    }

}
