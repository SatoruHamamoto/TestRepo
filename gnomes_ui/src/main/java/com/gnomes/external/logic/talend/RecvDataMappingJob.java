package com.gnomes.external.logic.talend;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.enterprise.context.RequestScoped;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseJobLogic;
import com.gnomes.common.resource.GnomesLogMessageConstants;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.GnomesDateUtil;
import com.gnomes.external.data.DataDefine;
import com.gnomes.external.data.SendRecvDataBean;
import com.gnomes.external.data.SystemDefine;
import com.gnomes.external.entity.MstrExternalIfSystemDefine;
import com.gnomes.system.entity.MstrSystemDefine;

/**
 * 受信伝文マッピング
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/14 KCC/I.Shibasaka           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class RecvDataMappingJob extends BaseJobLogic  {

    /**
     * 受信伝文マッピング
     * @throws Exception
     */
    @ErrorHandling
    @TraceMonitor
    public void process() throws Exception {

        // FileTransferBean.エラーコメントが空でない場合、本処理を行わず終了する。
        if (fileTransferBean.getErrorComment() != null && !fileTransferBean.getErrorComment().isEmpty()) {
            return;
        }

        // FormBeanの取得
        List<SendRecvDataBean> sendRecvDataBeanList = new ArrayList<SendRecvDataBean>();

        // FileTransferBeanから以下の情報を取得
        // 受信ファイル変換データ
        List<Map<String, String>> recvConDataList = fileTransferBean.getRecvConvData();

        // X103:外部I/Fデータ項目定義（X102の要素から取得）
        List<DataDefine> externalIfDataDefineList = fileTransferBean.getDataDefine();

        // マッピングするBeanクラスを取得
        Class<?> clazz = fileTransferBean.getBeanClass();
        Object recvDataBean;

        StringBuilder mesDetail = new StringBuilder();

        // エラーメッセージ
        String errorMessage = "";

        Integer lineNo = 1;
        Map<Integer, String> errorLineInfo = new HashMap<>();

        StringBuilder allLineError = new StringBuilder();
        // 受信ファイル変換データの件数分、以下の処理を行う。
        for (Map<String, String> recvConData : recvConDataList) {

            recvDataBean = clazz.newInstance();
            mesDetail = new StringBuilder();
            // X103の件数分、以下の処理を繰り返す。
            for (DataDefine datalist : externalIfDataDefineList) {

            	if(datalist.getData_item_id() == null || datalist.getData_item_id().isEmpty()){
            		continue;
            	}

                // X103.データ項目識別ID ＝ 受信ファイル変換データ.データ項目識別IDと一致する場合
                if(recvConData.containsKey(datalist.getData_item_id())){

                    // 受信ファイル変換データ.データをX103.フォーマット識別IDにより変換
                    // 受信ファイル変換データ.データ識別IDと一致するFormBeanの項目に変換データを設定
                    try {
                    	//再帰処理用のクラスを取得
                    	Class<?> clazzForList =  fileTransferBean.getBeanClass();

                    	//フィールドを生成
                    	Field fld = null;

                    	// 項目名からフィールドを探す。ない場合NoSuchFieldException
                    	//親クラスが存在する場合は再帰的に親からフィールドを取得
                    	while (clazzForList != null) {
                            try {
                            	fld = clazzForList.getDeclaredField(datalist.getData_item_id());
                                break;
                            } catch (NoSuchFieldException e) {
                            	clazzForList = clazzForList.getSuperclass();
                            }
                        }

                    	if (fld == null) {
                            throw new NoSuchFieldException(datalist.getData_item_id());
                        }

                        fld.setAccessible(true);

                        // データ項目識別ID有効/無効 = 無効の場合
                        if (datalist.getIsdata_item_id() == 1) {
                            // 空文字を設定
                            fld.set(recvDataBean, "");
                        } else if (datalist.getFormat_id() == CommonEnums.FormatId.String.getValue()) {
                            // String
                            fld.set(recvDataBean, recvConData.get(datalist.getData_item_id()).trim());
                        } else if (datalist.getFormat_id() == CommonEnums.FormatId.Date.getValue()) {

                            // ExternalIfDataDefineのフォーマットを使用して日時に変換

                            SystemDefine systemDefine = fileTransferBean.getSystemDefine();
                            String timeZoneStr = systemDefine.getTime_zone();
                            TimeZone tz;
                            Date dt;

                            //Web.xmlのタイムゾーンを取得
                            TimeZone defTz = TimeZone.getDefault();
                            //Web.xmlのタイムゾーンがUTCの場合,かつ外部IFシステム定義からタイムゾーンを取得できた場合
                            if(defTz.getID().equals(CommonConstants.ZONEID_UTC) && timeZoneStr != null && !timeZoneStr.equals("")){
                                tz = TimeZone.getTimeZone(timeZoneStr);
                              //年月日時分秒または年月日時分の場合のみローカル時刻からUTCに変換する
                                dt = GnomesDateUtil.convertStringLocaleToUtcDateTimePattern(recvConData.get(datalist.getData_item_id()).trim(),
                                        ResourcesHandler.getString(datalist.getDate_format()), tz);

                            } else {
                                //Web.xmlのタイムゾーンがUTCではない場合は変換を行わない
                                dt = ConverterUtils.stringToDateFormat(recvConData.get(datalist.getData_item_id()).trim(),
                                        ResourcesHandler.getString(datalist.getDate_format()));
                            }

                            fld.set(recvDataBean, dt);

                        } else if (datalist.getFormat_id() == CommonEnums.FormatId.Integer.getValue()) {
                            // Integer
                        	if(recvConData.get(datalist.getData_item_id()).trim()!= null && !recvConData.get(datalist.getData_item_id()).trim().isEmpty()){
                        		fld.set(recvDataBean, Integer.valueOf(recvConData.get(datalist.getData_item_id()).trim()));
                        	}
                        } else if (datalist.getFormat_id() == CommonEnums.FormatId.BigDecimal.getValue()) {
                        	// BigDecimal
                        	if(recvConData.get(datalist.getData_item_id()).trim() != null && !recvConData.get(datalist.getData_item_id()).trim().isEmpty()){
                        		fld.set(recvDataBean, BigDecimal.valueOf(ConverterUtils.stringToNumber(false, recvConData.get(datalist.getData_item_id()).trim()).doubleValue()));
                        	}
                        } else if (datalist.getFormat_id() == CommonEnums.FormatId.FixedValue.getValue()) {
                            // 固定値
                            fld.set(recvDataBean, datalist.getFixed_value_string());
                        } else {
                            // none
                        }
                    } catch (NoSuchFieldException e) {
                        continue;
                    } catch (SecurityException e) {

                    } catch (ParseException | NumberFormatException e){
                        if (mesDetail.length() > 0) {
                            mesDetail.append(" " + System.lineSeparator());
                        }

                        errorMessage = "";

                        if (datalist.getFormat_id() == CommonEnums.FormatId.Date.getValue()) {
                            // 日時
                            errorMessage = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0100, recvConData.get(datalist.getData_item_id()), ResourcesHandler.getString(datalist.getDate_format()), datalist.getData_item_name());
                        } else if (datalist.getFormat_id() == CommonEnums.FormatId.Integer.getValue() || datalist.getFormat_id() == CommonEnums.FormatId.BigDecimal.getValue()) {
                            // 数値
                            errorMessage = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0099, recvConData.get(datalist.getData_item_id()),  datalist.getData_item_name());

                        }

                        // ({0}行目、{1}番目）エラーメッセージ
                        mesDetail.append(MessagesHandler.getString(GnomesMessagesConstants.MV01_0027,
                                lineNo.toString(), datalist.getData_item_number().toString(), errorMessage));

                    }

                }

            }
            if(!StringUtil.isNullOrEmpty(mesDetail.toString())){
                // エラー情報が存在する場合

                if (allLineError.length() > 0) {
                    allLineError.append(System.lineSeparator());
                }
                allLineError.append(mesDetail.toString());
                errorLineInfo.put(lineNo, mesDetail.toString());

            }
            fileTransferBean.setErrorLineInfo(errorLineInfo);
            sendRecvDataBeanList.add((SendRecvDataBean) recvDataBean);
            lineNo++;
        }

        // 作成したFormBean、validateCheckListをFileTransferBeanに設定
        fileTransferBean.setSendRecvDataBeanList(sendRecvDataBeanList);

        if (allLineError.length() > 0) {
            String errorComment = MessagesHandler.getString(GnomesMessagesConstants.ME01_0106, sendRecvDataBeanList.get(0).getClass().getSimpleName(), allLineError.toString());
            fileTransferBean.setErrorComment(errorComment);
            //ME01.0106：「データが不正です。 詳細はエラーメッセージを確認してください。\n{0}」
            GnomesAppException ex = exceptionFactory.createGnomesAppException
            		(null, GnomesMessagesConstants.ME01_0106, sendRecvDataBeanList.get(0).getClass().getSimpleName(), allLineError.toString());
            throw ex;

        }
    }
}