package com.gnomes.external.logic;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.Dependent;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums.FileFormat;
import com.gnomes.common.constants.CommonEnums.FormatId;
import com.gnomes.common.constants.CommonEnums.HeaderItemType;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseJobLogic;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.external.data.DataDefine;
import com.gnomes.external.data.FileDefine;
import com.gnomes.external.entity.ExternalIfSendDataDetail;

/**
 * 送信伝文ヘッダ作成
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2020/06/09 YJP/S.Hosokawa            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class SendDataCreateHeader extends BaseJobLogic {

	/** 定数：半角スペース. */
	private static final String HALF_SPACE = " ";

	/**
	 * 送信伝文ヘッダ作成
	 * <pre>
	 * 送信伝文ヘッダをマスタ定義に従って作成します。
	 * </pre>
	 * @param externalIfSendDataDetail 送信データ詳細
	 * @param fileDefine 外部IFファイル構成定義
	 * @param dataDefineList 外部IFデータ項目定義リスト
	 * @return
	 * @throws GnomesAppException
	 */
	@ErrorHandling
	@TraceMonitor
	public String process(List<ExternalIfSendDataDetail> externalIfSendDataDetail,
			FileDefine fileDefine, List<DataDefine> dataDefineList) throws GnomesAppException {

		String sendHeaderData = new String();

		try{

			// ファイル形式が CSV の場合
			if (FileFormat.Csv.equals(FileFormat.getEnum(fileDefine.getFile_format()))) {
				// CSV区切り文字
				String delimiter = fileDefine.getCsv_delimiter();
	            // 文字列の括り文字(nullの場合は空文字)
	            String charBundle = "";
	            if(fileDefine.getChar_bundle() != null) {
	                charBundle = fileDefine.getChar_bundle();
	            } 
				// 変換文字列
				StringBuilder convDelimiterData = new StringBuilder();


				StringBuilder convBundleData = new StringBuilder();

				// 外部IFデータ項目定義の件数分繰り返す
				for (DataDefine dataDefine: dataDefineList) {

					Integer headerItemType = dataDefine.getHeader_item_type();

					if(headerItemType == null){
						continue;
					}

					String headerItemStr = this.getHeaderData(externalIfSendDataDetail, dataDefine);

					FormatId formatId = FormatId.getEnum(dataDefine.getFormat_id());

                    convDelimiterData.setLength(0);
                    convDelimiterData.append(charBundle);
                    //固定値の場合
                    if(FormatId.FixedValue.equals(formatId)){
                    	convDelimiterData.append(dataDefine.getFixed_value_string());
                    }
                    //数値型・小数点有りの場合
                    else if(FormatId.BigDecimal.equals(formatId)){
                    	//Nullや空白なら変換しない
                    	if (StringUtil.isNullOrEmpty(headerItemStr)) {
                    		convDelimiterData.append(headerItemStr);
                    	} else {
                    	convDelimiterData.append(
                    			ConverterUtils.decimalPadFraction(new BigDecimal(headerItemStr),
                                fileDefine.getDecimal_length()));
                    	}
                    } else {
                    	convDelimiterData.append(headerItemStr);
                    }
                    convDelimiterData.append(charBundle);

					if (convBundleData.length() > 0) {
						convBundleData.append(delimiter);
					}
					convBundleData.append(convDelimiterData.toString());
				}
				sendHeaderData = convBundleData.toString();


			}
			// ファイル形式が 固定長の場合
			else if (FileFormat.FixedLength.equals(FileFormat.getEnum(fileDefine.getFile_format()))) {

				StringBuilder convBundleData = new StringBuilder();

				// 外部IFデータ項目定義の件数分繰り返す
				for (DataDefine dataDefine : dataDefineList) {

					// データ
					String data = this.getHeaderData(externalIfSendDataDetail, dataDefine);

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

				sendHeaderData = convBundleData.toString();

			}

		}catch(Exception e){
            // ME01.0134:「外部I/Fファイル連携処理：{0} （処理名：{1}) にてエラーが発生しました。　エラーの詳細については、メッセージ履歴を確認してください。 」
            throw exceptionFactory.createGnomesAppException(null,
                    GnomesMessagesConstants.ME01_0134, e,
                    CommonConstants.SEND_DATA_CREATE_HEADER, CommonConstants.PROCESS_NAME);
		}
		return sendHeaderData;

	}

	@ErrorHandling
	@TraceMonitor
	/** ヘッダー項目作成 */
	private String getHeaderData(List<ExternalIfSendDataDetail> externalIfSendDataDetail, DataDefine dataDefine) throws ParseException{

		switch (HeaderItemType.getEnum(dataDefine.getHeader_item_type())) {
		case SendDate:
			//送信日時は現在時刻をフォーマットして渡す
			return ConverterUtils.dateTimeToString(new Date(), ResourcesHandler.getString(dataDefine.getDate_format()));
		case SendDataNum:
			//送信件数
			return String.valueOf(externalIfSendDataDetail.size());
		default:
			return null;
		}
	}

}
