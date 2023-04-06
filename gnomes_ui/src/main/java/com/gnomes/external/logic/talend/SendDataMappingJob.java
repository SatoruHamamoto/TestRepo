package com.gnomes.external.logic.talend;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.enterprise.context.RequestScoped;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums.ExternalIfIsDataItemId;
import com.gnomes.common.constants.CommonEnums.FormatId;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseJobLogic;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.GnomesDateUtil;
import com.gnomes.external.data.DataDefine;
import com.gnomes.external.data.SendRecvDataBean;
import com.gnomes.external.data.SystemDefine;

/**
 * 送信伝文マッピング
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/14 KCC/I.SHibasaka           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class SendDataMappingJob extends BaseJobLogic {

    /**
     * 送信伝文マッピング.
     * <pre>
     * 送信伝文マッピングを取得します。
     * </pre>
     * @param sendRecvDataBeanList 送信データBeanリスト
     * @param dataDefineList 外部I/Fデータ項目定義リスト
     * @return 送信ファイルマッピングデータリスト
     * @throws GnomesAppException
     */
    @ErrorHandling
    @TraceMonitor
    public List<Map<String, String>> process(
            List<SendRecvDataBean> sendRecvDataBeanList, List<DataDefine> dataDefineList) throws GnomesAppException {

        List<Map<String, String>> mappingList = new ArrayList<>();

        // 送信データの件数分繰り返す
        for (SendRecvDataBean sendDataBean: sendRecvDataBeanList) {

            Map<String, String> dataMapping = new HashMap<>();

            // 外部I/Fﾃﾞｰﾀ項目定義の件数分繰り返す
            for (DataDefine externalIfDataDefine: dataDefineList) {

                try {

                    // データ項目識別ID有効/無効 = 無効の場合
                    if (ExternalIfIsDataItemId.INVALID.equals(
                            ExternalIfIsDataItemId.getEnum(externalIfDataDefine.getIsdata_item_id()))) {
                        // 空文字を設定
                        dataMapping.put(externalIfDataDefine.getData_item_id(), "");

                    } else {
                    	//クラスを取得
                    	Class<?> clazz = sendDataBean.getClass();

                    	//フィールドを生成
                    	Field fld = null;

                    	// 項目名からフィールドを探す。ない場合NoSuchFieldException
                    	//親クラスが存在する場合は再帰的に親からフィールドを取得
                    	while (clazz != null) {
                    		try {
                    			fld = clazz.getDeclaredField(externalIfDataDefine.getData_item_id());
                    			break;
                    		} catch (NoSuchFieldException e) {
                    			clazz = clazz.getSuperclass();
                    		}
                    	}

                    	if (fld == null) {
                    		throw new NoSuchFieldException(externalIfDataDefine.getData_item_id());
                    	}

                    	fld.setAccessible(true);

                        String data = this.getStringData(externalIfDataDefine, fld.get(sendDataBean));
                        dataMapping.put(externalIfDataDefine.getData_item_id(), data);


                    }


                } catch (Exception e) {
                    // ME01.0134:「外部I/Fファイル連携処理：{0} （処理名：{1}) にてエラーが発生しました。　エラーの詳細については、メッセージ履歴を確認してください。 」
                    throw exceptionFactory.createGnomesAppException(null,
                            GnomesMessagesConstants.ME01_0134, e,
                            CommonConstants.SEND_DATA_MAPPING_JOB, CommonConstants.PROCESS_NAME);

                }

            }
            mappingList.add(dataMapping);
        }

        return mappingList;

    }

    /**
     * 文字列取得
     * @param define X103
     * @param data データ
     * @return 文字列
     * @throws ParseException
     * @throws GnomesAppException
     */
    private String getStringData(DataDefine define, Object data) throws ParseException, GnomesAppException {

        if (data == null) {
            return "";
        }
        if (FormatId.Date.equals(FormatId.getEnum(define.getFormat_id()))) {

            SystemDefine systemDefine = fileTransferBean.getSystemDefine();
            String timeZoneStr = systemDefine.getTime_zone();
            TimeZone tz;
            String strDate = new String();

            //Web.xmlのタイムゾーンを取得
            TimeZone defTz = TimeZone.getDefault();
            //Web.xmlのタイムゾーンがUTCの場合,かつ外部IFシステム定義からタイムゾーンを取得できた場合
            if(defTz.getID().equals(CommonConstants.ZONEID_UTC) && timeZoneStr != null && !timeZoneStr.equals("")){
                //年月日時分秒、年月日時分のフォーマットであった場合はローカル時刻に変換する
                String strFormatDate = ResourcesHandler.getString(define.getDate_format());
                tz = TimeZone.getTimeZone(timeZoneStr);
                strDate = GnomesDateUtil.convertStringUtcToLocaleDateTimePattern((Date)data, strFormatDate, tz);
            } else {
                //Web.xmlのタイムゾーンがUTCではない場合は変換を行わない
                strDate = ConverterUtils.dateTimeToString(data, ResourcesHandler.getString(define.getDate_format()));
            }

            return strDate;


         } else if (FormatId.String.equals(FormatId.getEnum(define.getFormat_id()))) {
        	String str = data.toString();

        	//除去文字列が存在する場合は空白に置き換える
        	if(str != null && !str.isEmpty() && define.getRemove_string() != null && !define.getRemove_string().isEmpty()){
        		str = str.replace(define.getRemove_string(), "");
        	}

        	return str;
         } else {
            return data.toString();
        }
    }


}