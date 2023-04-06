package com.gnomes.external.logic.talend;


import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums;
import com.gnomes.common.constants.CommonEnums.FileFormat;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseJobLogic;
import com.gnomes.external.dao.MstrExternalIfFileDefineDao;
import com.gnomes.external.data.DataDefine;
import com.gnomes.external.data.FileDefine;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;


/**
 * 受信伝文データ変換
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/14 KCC/I.Shibasaka           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class RecvDataConvJob extends BaseJobLogic {

    /** 外部I/Fファイル構成定義マスタ Dao */
    @Inject
    protected MstrExternalIfFileDefineDao mstrExternalIfFileDefineDao;

    /**
     * 文字の切り出しモード
     * @author 03501213
     *
     */
    public enum StringCutMode {
        /**
         * 切り出し＝バイトタイプ（2バイトコードは2文字と数える)
         */
        cutmode_byte,
        /**
         * 切り出し＝文字タイプ（2バイトコードも１文字と数える)
         */
        cutmode_char;
    }

    /**
     * 切り出しロジックの種類
     * @author 03501213
     *
     */
    public enum CutLogicKind {
        /**
         * 切り出しロジック：CSV切り出し
         */
        cutProcForCSV,
        /**
         * 切り出しロジック：バイト単位切り出し
         */
        cutProcForFixedLengthByteRead,
        /**
         * 切り出しロジック：文字単位切り出し
         */
        cutProcForFixedLengthCharRead,
    	/**
    	 * 切り出しロジック：XML切り出し
    	 */
    	cutProcForXML;

    }
    /**
     * 受信伝文データ変換
     * @throws Exception
     */
    @ErrorHandling
    @TraceMonitor
    public void process() throws Exception {

        //x102:外部I/Fファイル構成定義を取得
        FileDefine fileDefine = fileTransferBean.getFileDefine();

        //詳細データリスト
        List<Map<String, String>> detailDataList = new ArrayList<Map<String, String>>();

        // ファイル区切り文字を取得
        String delimiter;
        if(FileFormat.Tsv.equals(FileFormat.getEnum(fileDefine.getFile_format()))){
            delimiter = "\t";
        } else {
            delimiter = fileDefine.getCsv_delimiter();
        }

        // ファイル括り文字を取得
        String enclose = fileDefine.getChar_bundle();

        // 受信ファイルのファイル形式を取得
        int fileFormat = fileDefine.getFile_format();

        // 文字エンコード方式を参照し、バイトタイプか文字（チャラタイプ）かを判定する
        StringCutMode cutMode = getStringCutMode();

        //ファイルフォーマットと切り出しロジックの組み合わせで呼び出しロジックを決める
        CutLogicKind cutLogic = getCutLogic(fileFormat,cutMode);

        // X103:外部IFデータ項目定義取得
        List<DataDefine> dataDefinelist =  fileTransferBean.getDataDefine();
        // データ項目番号昇順でソート
        Collections.sort(dataDefinelist,Comparator.comparing(DataDefine::getData_item_number));

        //文字コードセットを受ける
        Charset charSet = Charset.forName(this.fileTransferBean.getRecvFileEncode());

        //FileTransferBeanから、受信ファイル行データを取得 setRecvFileLineData
        List<String> recvFileLineData = fileTransferBean.getLineDataList();

        // ヘッダ部の桁数を取得
        int headerNum = fileDefine.getHeader_line_count();


        // ヘッダ部の行を削除
        for (int i = 0; i < headerNum; i++) {
        	if(recvFileLineData == null || recvFileLineData.size() == 0){
        		break;
        	}
        	recvFileLineData.remove(0);
        }
        
        //BOM判定・削除
        if(recvFileLineData != null && recvFileLineData.size() != 0){

            String firstStr = recvFileLineData.get(0);
            if(firstStr.startsWith(CommonConstants.BOM)){
                recvFileLineData.set(0, firstStr.substring(1));
            }
        }

        fileTransferBean.setLineDataList(recvFileLineData);        	

        // 受信ファイル行データ分変換処理
        for(String lineData: recvFileLineData){
            HashMap<String,String> lineWordMap = null;

            switch(cutLogic){
                case cutProcForCSV:
                    lineWordMap = cutProcForCSV(lineData,delimiter,enclose,dataDefinelist);
                    //上記で作成したMapをListに設定する。
                    detailDataList.add(lineWordMap);
                    break;
                case cutProcForFixedLengthCharRead:
                    lineWordMap = cutProcForFixedLengthCharRead(lineData,dataDefinelist);
                    //上記で作成したMapをListに設定する。
                    detailDataList.add(lineWordMap);
                    break;
                case cutProcForFixedLengthByteRead:
                    lineWordMap = cutProcForFixedLengthByteRead(lineData,charSet,dataDefinelist);
                    //上記で作成したMapをListに設定する。
                    detailDataList.add(lineWordMap);
                    break;
                case cutProcForXML:
                	if(detailDataList.size() == 0) {
                		lineWordMap = new HashMap<>();
                		//空MapをListに設定する。
                		detailDataList.add(lineWordMap);
                	}
                	break;
            }

        }
        //作成したListをFileTransferBeanに設定する。
        fileTransferBean.setRecvConvData(detailDataList);
    }

    /**
     * 文字列切り出しロジックを判定する
     * @param fileFormat    ファイルフォーマット(CSVか固定長か)
     * @param cutMode       切り出しモード（バイト単位か文字単位か）
     * @return  切り出しロジック
     */
    private CutLogicKind getCutLogic(int fileFormat, StringCutMode cutMode) {

        if (fileFormat == CommonEnums.FileFormat.Csv.getValue() || fileFormat == CommonEnums.FileFormat.Tsv.getValue()){
            return CutLogicKind.cutProcForCSV;
        }
        else if(fileFormat == CommonEnums.FileFormat.FixedLength.getValue()){
            if(cutMode == StringCutMode.cutmode_char){
                return CutLogicKind.cutProcForFixedLengthCharRead;
            }
            else if (cutMode == StringCutMode.cutmode_byte){
                return CutLogicKind.cutProcForFixedLengthByteRead;
            }
        } else if (fileFormat == CommonEnums.FileFormat.Xml.getValue()){
        	return CutLogicKind.cutProcForXML;
        }
        return CutLogicKind.cutProcForFixedLengthByteRead;
    }

    /**
     * 文字をCSV形式としてCUTして個々の文字を切り出す
     *
     * @param lineData  行データ
     * @param delimiter デリミタ
     * @param enclose   エンクロージャー
     * @param dataDefinelist    データ定義辞書
     * @return
     */
    private HashMap<String,String> cutProcForCSV(String lineData,String delimiter,String enclose,List<DataDefine> dataDefinelist)
    {
        HashMap<String,String> map = new HashMap<>();

        // 区切り文字・くくり文字を分割
        List<String> lineList = stringSplit(lineData, delimiter, enclose);
        Integer readCount = 1;
        for (String str : lineList) {
            for (Iterator<DataDefine> dataDefineList = dataDefinelist.iterator(); dataDefineList.hasNext();) {
                DataDefine externalIfDataDefine = dataDefineList.next();
                // 受信データの項目順と、Zm133:外部I/Fデータ項目定義．CSVデータ位置あるいは
                // Zm133:外部I/Fデータ項目定義．ﾃﾞｰﾀ項目番号の一致する レコードからﾃﾞｰﾀ項目識別IDを取得
                if(externalIfDataDefine.getCsv_data_position() != null){
                	if(readCount.equals(externalIfDataDefine.getCsv_data_position())){
                		String itemId = externalIfDataDefine.getData_item_id();
                		// Zm133:外部I/Fデータ項目定義.ﾃﾞｰﾀ項目識別IDをkey名として、CSV項目値をMapに設定する。
                		map.put(itemId, str);
                	}
                } else {
                	if(readCount.equals(externalIfDataDefine.getData_item_number())){
                		String itemId = externalIfDataDefine.getData_item_id();
                		// Zm133:外部I/Fデータ項目定義.ﾃﾞｰﾀ項目識別IDをkey名として、CSV項目値をMapに設定する。
                		map.put(itemId, str);
                	}
                }
            }
            readCount++;
        }

        return(map);
    }
    /**
     * 文字切り出しロジック 固定長レコード 文字単位切り出し
     *
     * @param lineData          切り出し対象の1行分の文字
     * @param dataDefinelist    データ定義辞書
     * @return                  切り出した文字のMAP
     */
    private HashMap<String,String> cutProcForFixedLengthCharRead(String lineData,List<DataDefine> dataDefinelist)
    {
        HashMap<String,String> map = new HashMap<>();

        // 受信ファイル行データの順番とX103.ﾃﾞｰﾀ項目番号の一致するよう、ﾃﾞｰﾀ項目識別ID、分割データをマッピングする。
        //x103のループ
        for (DataDefine dataDefine : dataDefinelist) {
            //X103:外部I/Fデータ項目定義．ﾃﾞｰﾀ開始位置、ﾃﾞｰﾀ桁数から受信データの先頭から順に項目を取得
            // データ開始位置
            Integer dataStart = dataDefine.getData_start() - 1;
            // データ桁数
            Integer len = dataDefine.getData_length();
            Integer dataEnd = dataStart + len;
            //データ項目識別ID
            String itemId = dataDefine.getData_item_id();

            String dataItem = null;
            //データ項目取得
            dataItem = lineData.substring(dataStart, dataEnd);
            //X103:外部I/Fデータ項目定義.ﾃﾞｰﾀ項目識別IDをkey名として、CSV項目値をMapに設定する。
            map.put(itemId, dataItem);
        }

        return map;
    }
    /**
     * 文字切り出しロジック 固定長レコード バイト単位切り出し
     *
     * @param lineData          切り出し対象の1行分の文字
     * @param dataDefinelist    データ定義辞書
     * @return                  切り出した文字のMAP
     */
    private HashMap<String,String> cutProcForFixedLengthByteRead(String lineData,Charset charSet,List<DataDefine> dataDefinelist)
    {
        HashMap<String,String> map = new HashMap<>();

        //1行データをバイト配列に変換
        byte[] lineDataBytes = lineData.getBytes();

        // 受信ファイル行データの順番とX103.ﾃﾞｰﾀ項目番号の一致するよう、ﾃﾞｰﾀ項目識別ID、分割データをマッピングする。
        //x103のループ
        for (DataDefine dataDefine : dataDefinelist) {
            //データ項目識別ID有効/無効が無効(1)の場合は読み飛ばす
            if(dataDefine.getIsdata_item_id() != 0){
                continue;
            }

            //X103:外部I/Fデータ項目定義．ﾃﾞｰﾀ開始位置、ﾃﾞｰﾀ桁数から受信データの先頭から順に項目を取得
            // データ開始位置
            Integer dataStart = dataDefine.getData_start() - 1;
            // データ桁数
            Integer len = dataDefine.getData_length();

            //データ桁数が０の場合は読み飛ばす
            if(len.intValue() <= 0){
                continue;
            }

            Integer dataEnd = dataStart + len;
            //データ項目識別ID
            String itemId = dataDefine.getData_item_id();

            String dataItemValue = null;
            //データ項目取得
            byte[] wordBytes = Arrays.copyOfRange(lineDataBytes,dataStart,dataEnd);

            //前後にスペースがあるので削る
            dataItemValue = new String(wordBytes).trim();

            //データが入っていない、また空白を削って何もなくなった場合map設定しない
            if(StringUtil.isNullOrEmpty(dataItemValue)){
                continue;
            }

            //X103:外部I/Fデータ項目定義.ﾃﾞｰﾀ項目識別IDをkey名として、項目値をMapに設定する。
            map.put(itemId, dataItemValue);
        }

        return map;
    }

    /**
     * 文字列分割
     * @param parseItem
     * @return
     */
    private List<String> stringSplit(final String readLine, final String delimiter, final String enclose) {

    	char[] delimiterArray = delimiter.toCharArray();
    	CSVParserBuilder csvParserBuilder = new CSVParserBuilder()
        		.withSeparator(delimiterArray[0]);

    	if(enclose != null && !enclose.isEmpty()){
    		char[] encloseArray = enclose.toCharArray();
    		csvParserBuilder.withQuoteChar(encloseArray[0]);
    	}

        CSVParser csvparser = csvParserBuilder.build();

        String[] vs = null;
        try {
			vs = csvparser.parseLine(readLine);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

        List<String> lineList = Arrays.asList(vs);

        return lineList;

    }
    /**
     * 文字の切り出し方を受信ファイルの文字コードから判定する
     * @return
     */
    private StringCutMode getStringCutMode(){

        final Map<String, StringCutMode> encodeMap = new HashMap<String, StringCutMode>() {

            private static final long serialVersionUID = 1L;

            {
                put("MS932", StringCutMode.cutmode_byte);
                put("Shift-JIS", StringCutMode.cutmode_byte);
                put("EUC", StringCutMode.cutmode_byte);
                put("windows-31j",StringCutMode.cutmode_byte);
                put("UTF-8", StringCutMode.cutmode_byte);
                put("UTF-16", StringCutMode.cutmode_char);
            }
        };

        String recvFileEncode = this.fileTransferBean.getRecvFileEncode();

        //MAPに見つかったら設定された切り出し方にする
        if(encodeMap.containsKey(recvFileEncode)){
            return encodeMap.get(recvFileEncode);
        }
        else {
            //MAPに見つからなかったら文字タイプにする(UTF-8と同じにする)
            return(StringCutMode.cutmode_char);
        }

    }
}

