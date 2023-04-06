package com.gnomes.common.tags;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang3.time.DateUtils;
import org.picketbox.util.StringUtil;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.view.IScreenFormBean;

/**
 * 日付時刻カスタムタグ
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/2/27  YJP/S.Michiura            初版
 * R0.01.02 2018/11/09 YJP/A.Oomori              readonly時、tabIndex=-1を追加出力
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesCTagInputDateTime extends GnomesCTagBase {

    /** 辞書：ラベルリソースID **/
    private static final String INFO_LABEL_RESOUCE_ID = "label_resource_id";

    /** 辞書：日付時刻Bean (必須) **/
    private static final String INFO_PARAM_NAME = "param_name";

    /** 辞書：日付フォーマット リソースID **/
    private static final String INFO_FORMAT_DATE = "format_date";

    /** 辞書：時刻フォーマット リソースID **/
    private static final String INFO_FORMAT_TIME = "format_time";

    /** 辞書：日付フォーマット Bean項目名 **/
    private static final String INFO_FORMAT_DATE_BEAN_ITEM = "format_date_bean_item";

    /** 辞書：時刻フォーマット Bean項目名 **/
    private static final String INFO_FORMAT_TIME_BEAN_ITEM = "format_time_bean_item";

    /** 辞書：Inputタイプ  **/
    private static final String INFO_INPUT_TYPE = "input_type";

    /** 辞書：入力活性非活性Bean **/
    private static final String INFO_INPUT_ACTIVITY = "input_activity";

    /** 辞書：日付入力参照用Name  **/
    private static final String INFO_INPUT_DATE_NAME = "input_date_name";

    /** 辞書：時間入力参照用Name  **/
    private static final String INFO_INPUT_TIME_NAME = "input_time_name";

    /** 辞書：ラベル出力スタイルシートの追加設定 **/
    private static final String INFO_LABEL_CLASS_NAME = "label_add_class";

    /** 辞書：INPUT差し替えクラス */
    private static final String INFO_INPUT_CHANGE_CLASS = "input_change_class";

    /** 辞書：追加クラス */
    private static final String INFO_ADD_CLASS = "add_class";

    /** 辞書：INPUTの追加クラス */
    private static final String INFO_INPUT_ADD_STYLE = "input_add_style";

    /** 辞書：工程端末表示か否か */
    private static final String INFO_IS_PANECON = "is_panecon";

    /** 辞書：入力値変更後の処理 スクリプト */
    private static final String INFO_ON_CHANGE_EVENT = "on_change_event";

    /** 辞書：入力値変更後の処理 コマンドID */
    private static final String INFO_ON_CHANGE_COMMAND_ID = "on_change_command_id";

    /** 辞書：入力値変更後の処理 コマンド */
    private static final String INFO_ON_CHANGE_BEAN_COMMAND_ID = "on_change_bean_command_id";

    /** 辞書ID */
    private String dictId;

    /** bean */
    private Object bean;

    /** inputタイプ：入力形式をパラメータから決定 */
    private static final String TAG_TYPE_INPUT_DATA_TYPE = "input_data_type";

    /** 入力タイプ：年月日時分秒 */
    private static final Integer INPUT_DATA_TYPE_DATE_TIME = 1;

    /** 入力タイプ：年月日 */
    private static final Integer INPUT_DATA_TYPE_DATE = 2;

    /** 入力タイプ：年月 */
    private static final Integer INPUT_DATA_TYPE_DATE_YM = 3;

    /**
     * 辞書IDを取得
     * @return dictId
     */
    public String getDictId() {
        return dictId;
    }

    /**
     * 辞書IDを設定
     * @param dictId 辞書ID
     */
    public void setDictId(String dictId) {
        this.dictId = dictId;
    }

    /**
     * Bean取得
     * @return bean
     */
    public Object getBean() {
        return bean;
    }

    /**
     * Bean設定
     * @param bean Bean
     */
    public void setBean(Object bean) {
        this.bean = bean;
    }

    /**
     * 日付時間入力タグ出力
     */
    public int doStartTag() throws JspException {

        JspWriter out = null;

        try {

            //Beanの指定が間違ってnullになっているのをチェック
            if ( this.bean == null){
                logHelper.severe(this.logger,null,null,NO_BEAN_ERR_MES);
                throw new GnomesAppException(NO_BEAN_ERR_MES);
            }

            Locale userLocale = ((IScreenFormBean)this.bean).getUserLocale();
            out = pageContext.getOut();

            // 辞書取得
            GnomesCTagDictionary dict = getCTagDictionary();
            // ラベル辞書取得
            Map<String,Object> mapInfo = dict.getDateInfo(this.dictId);

            // 出力元データのクラス
            Class<?> clsBean = bean.getClass();

            // 工程端末か否か（デフォルトは管理端末表示）
            boolean isPanecon = false;
            if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_IS_PANECON))){
                isPanecon = true;
            }

            // ラベル出力　※リソースIDより取得
            String labelRourceId = (String)mapInfo.get(INFO_LABEL_RESOUCE_ID);
            String dataClassName = "common-header-col-data-titleless";
            if ( labelRourceId != null) {
                // 出力ラベル名
                String strLabel = this.getStringEscapeHtml(ResourcesHandler.getString(labelRourceId, userLocale));
                if(StringUtil.isNullOrEmpty(strLabel)){
                    strLabel = "";
                }

                // 追加クラスの設定
                String strLabelClassName = (String)mapInfo.get(INFO_LABEL_CLASS_NAME);
                if(StringUtil.isNullOrEmpty(strLabelClassName)){
                    strLabelClassName = "";
                } else {    // 追加がある場合はスペースを加える
                    strLabelClassName = " " + strLabelClassName;
                }

                // 左ラベルの出力
                out.print("  <div class=\"common-header-col-title" + strLabelClassName + "\">" + strLabel + "</div>");

                // データ部のCSSクラス
                dataClassName = "common-header-col-data";
            }

            // 左側ラベル出力
            // typeName:タイプ指定 ( input/readonly/disabled/input_data_type) ※ 直接取得
            String strInputType = (String)mapInfo.get(INFO_INPUT_TYPE);

            // inputNumberName:input name="inputNumberName"　※ 直接取得
            String strInputDateName = (String)mapInfo.get(INFO_INPUT_DATE_NAME);
            String strInputTimeName = (String)mapInfo.get(INFO_INPUT_TIME_NAME);

            // 日付フォーマット ※リソースIDより取得
            String FormatDateId = (String)mapInfo.get(INFO_FORMAT_DATE);
            String strFormatDate = ResourcesHandler.getString(FormatDateId, userLocale);

            // 時間フォーマット ※リソースIDより取得
            String FormatTimeId = (String)mapInfo.get(INFO_FORMAT_TIME);
            String strFormatTime = "";
            if (!StringUtil.isNullOrEmpty(FormatTimeId)) {
            	strFormatTime = ResourcesHandler.getString(FormatTimeId, userLocale);
            }

            // 日付フォーマットBean項目名 ※直接取得→リソースIDより取得
            String FormatDateIdBeanIten = (String)mapInfo.get(INFO_FORMAT_DATE_BEAN_ITEM);
            //String strFormatDateBeanIten = "";
            if (!StringUtil.isNullOrEmpty(FormatDateIdBeanIten)) {
            	Object strFormat = getData(clsBean, bean, FormatDateIdBeanIten);
            	if (strFormat instanceof String) {
            		String strFormatDateResouceID = (String)strFormat;
            		strFormatDate = ResourcesHandler.getString(strFormatDateResouceID, userLocale);
            	}
            }

            // 時間フォーマットBean項目名 ※直接取得→リソースIDより取得
            String FormatTimeIdBeanIten = (String)mapInfo.get(INFO_FORMAT_TIME_BEAN_ITEM);
            //String strFormatTimeBeanIten = "";
            if (!StringUtil.isNullOrEmpty(FormatTimeIdBeanIten)) {
            	Object strFormat = getData(clsBean, bean, FormatTimeIdBeanIten);
            	if (strFormat instanceof String) {
            		String strFormatTimeResouceID = (String)strFormat;
            		strFormatTime = ResourcesHandler.getString(strFormatTimeResouceID, userLocale);
            	}
            }

            // inputActivity:入力活性非活性Bean
            boolean inputActivity = true;
            String labelInputActivity = (String)mapInfo.get(INFO_INPUT_ACTIVITY);
            if ( labelInputActivity != null) {
            	Object inputActivityData = getData(clsBean, bean, labelInputActivity);
            	if (inputActivityData instanceof Boolean) {
            		inputActivity = (Boolean)inputActivityData;
            	}
            }

            String strOutDisabled = "";
            if ( strInputType.equals(INPUT_DISABLED)){
                strOutDisabled = INPUT_DISABLED;
            } else if (( strInputType.equals(INPUT_READONLY)) || !inputActivity ){
                strOutDisabled = INPUT_READONLY;
            }

            String tabIndex = "";
            // readonlyの場合、タブ移動によるフォーカスを当てさせない
            if (strInputType.equals(INPUT_READONLY)) {
            	tabIndex = " tabindex=\"-1\"";
            }

            //INFO_PARAM_NAME:入力する時間のBean ※ Beanからの取得
            String paramName = (String)mapInfo.get(INFO_PARAM_NAME);

            String labelDateTime = "";
            String inputTypeFlag = "";
            // inputタイプ：input_data_typeの場合、入力フラグタイプBeanと日付データBeanを日付時刻Beanから取得する。
            if (strInputType.equals(TAG_TYPE_INPUT_DATA_TYPE)) {
            	String paramNames[] = paramName.split(",");
            	inputTypeFlag = paramNames[0];
            	labelDateTime = paramNames[1];
            }
            else {
            	// 日付データBean=日付時刻Bean
            	labelDateTime = paramName;
            }


            // 時間入力欄を隠すか否か
            Boolean isTimeHidden = false;
            // inputタイプ：入力形式をパラメータから決定
            if (strInputType.equals(TAG_TYPE_INPUT_DATA_TYPE)) {
            	// データタイプ取得
                Integer dataType = (Integer) getData(clsBean, bean, inputTypeFlag);

                // データタイプ（有効期限単位区分 1:年月日時分秒, 2:年月日, 3:年月）以外の場合はデフォルトで
                // 1:年月日時分秒 とする
                if(dataType == null){
                    dataType = INPUT_DATA_TYPE_DATE_TIME;
                }

                String errorMessage;
                if (dataType.equals(INPUT_DATA_TYPE_DATE_TIME)) {
                	if (StringUtil.isNullOrEmpty(strInputDateName) || StringUtil.isNullOrEmpty(strInputTimeName)) {
                		// ME01.0205:「入力タイプ：年月日時分秒の場合、日付入力参照用Nameと時刻入力参照用Nameは必須です。（タグ名：{0}）」
                		errorMessage =MessagesHandler.getString(GnomesMessagesConstants.ME01_0205, userLocale, this.dictId);
                		logger.severe(errorMessage);
                        GnomesAppException ex = exceptionFactory.createGnomesAppException(errorMessage);
                        throw ex;
                	}
                }
                else if (dataType.equals(INPUT_DATA_TYPE_DATE)) {
                	if (StringUtil.isNullOrEmpty(strInputDateName)) {
                		// ME01.0206:「入力タイプ：年月日の場合、日付入力参照用Nameは必須です。（タグ名：{0}）」
                		errorMessage =MessagesHandler.getString(GnomesMessagesConstants.ME01_0206, userLocale, this.dictId);
                		logger.severe(errorMessage);
                        GnomesAppException ex = exceptionFactory.createGnomesAppException(errorMessage);
                        throw ex;
                	}

                	isTimeHidden = true;
                }
                else if (dataType.equals(INPUT_DATA_TYPE_DATE_YM)) {
                	if (StringUtil.isNullOrEmpty(strInputDateName)) {
                		// 入力タイプ：年月の場合、日付入力参照用Nameは必須です。（タグ名：{0}）」
                		errorMessage =MessagesHandler.getString(GnomesMessagesConstants.ME01_0251, userLocale, this.dictId);
                		logger.severe(errorMessage);
                		GnomesAppException ex = exceptionFactory.createGnomesAppException(errorMessage);
                		throw ex;
                	}

                	isTimeHidden = true;
                }
                else {
                	logHelper.info(this.logger,null,null,"dataType is invalid value=" + dataType.toString());
                	// ME01.0207:「入力タイプは1（年月日時分秒）、2（年月日）のいずれかを指定してください。（タグ名：{0}）」
            		errorMessage =MessagesHandler.getString(GnomesMessagesConstants.ME01_0207, userLocale, this.dictId);
            		logger.severe(errorMessage);
                    GnomesAppException ex = exceptionFactory.createGnomesAppException(errorMessage);
                    throw ex;

                }
            }

            String strDate = null;
            String strTime = null;
            if (responseContext.isLogicError()) {
                if (strInputDateName != null) {
                    strDate = (String)responseContext.getResponseFormBean(bean, labelDateTime, strInputDateName);
                }
                if (strInputTimeName != null) {
                    strTime = (String)responseContext.getResponseFormBean(bean, labelDateTime, strInputTimeName);
                }
            } else {
                Object objDateTime = responseContext.getResponseFormBean(bean, labelDateTime, null);
                strDate = getStringDate(dictId, labelDateTime, objDateTime, strFormatDate);
                if (!StringUtil.isNullOrEmpty(strFormatTime)) {
                	if (isTimeHidden) {
                		// 00:00:00
                		Date initTime = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
                		strTime = getStringDate(dictId, labelDateTime, initTime, strFormatTime);
                	}
                	else {
                		strTime = getStringDate(dictId, labelDateTime, objDateTime, strFormatTime);
                	}

                }
            }


            if (strDate == null) {
                strDate = "";
            }
            if (strTime == null) {
                strTime = "";
            }

            // 追加クラスの設定
            String strAddClass = (String)mapInfo.get(INFO_ADD_CLASS);
            if(StringUtil.isNullOrEmpty(strAddClass)){
                strAddClass = "";
            } else {    // 追加がある場合はスペースを加える
                strAddClass = " " + strAddClass;
            }

            // INPUT追加クラスの設定
            String strInputAddStyle = (String)mapInfo.get(INFO_INPUT_ADD_STYLE);
            if(StringUtil.isNullOrEmpty(strInputAddStyle)){
                strInputAddStyle = "";
            } else {
                strInputAddStyle = " " + strInputAddStyle;
            }

            // 工程端末の場合
            String strInputStyle = "";
            if ( isPanecon ){
                // 工程端末表示用スタイルの追加
                // ＊現時点では設定無しとする。今後工程端末表示用スタイルを用意する場合は下記の通り編集する。
                // strInputStyle = " style=\"line-height: normal;\"";
            }

            out.print( "<div class=\"" + dataClassName + " common-input" + strAddClass +  "\">");

            // INPUT差し替えクラス
            String strDateClass = (String)mapInfo.get(INFO_INPUT_CHANGE_CLASS);
            // INPUT差し替えクラスが設定されていない場合
            if(StringUtil.isNullOrEmpty(strDateClass)){
                // 共通の日付クラスを使用（日付入力ダイアログを表示）
                strDateClass = "datetime";
            }

            // jsp出力用に日付フォーマットを修正する
            String inputDatePattern = getStringDateFormatHtml(strFormatDate);

            // 入力値変更後の処理
            String onchange = "";
            if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_ON_CHANGE_EVENT))){
            	onchange = " onchange=\"" + SET_WARMING_FLAG + (String)mapInfo.get(INFO_ON_CHANGE_EVENT) + "\"";
            }
            else if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_ON_CHANGE_COMMAND_ID))){
            	onchange = " onchange=\"" + SET_WARMING_FLAG + String.format(COMMAND_SCRIPT_FORMAT, String.valueOf((String)mapInfo.get(INFO_ON_CHANGE_COMMAND_ID))) + "\"";

            }
            else if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_ON_CHANGE_BEAN_COMMAND_ID))){
            	Object commandId = getData(clsBean, bean, (String)mapInfo.get(INFO_ON_CHANGE_BEAN_COMMAND_ID));
            	if (commandId != null) {
            		onchange = " onchange=\"" + SET_WARMING_FLAG + String.format(COMMAND_SCRIPT_FORMAT, String.valueOf(commandId)) + "\"";
            	} else {
            		onchange = " onchange=\"" + SET_WARMING_FLAG +  "\"";
            	}
            }
            else {
           	 onchange = " onchange=\"" + SET_WARMING_FLAG +  "\"";
            }
            if ( !StringUtil.isNullOrEmpty(strInputDateName) ){

                out.print("<input type=\"text\" name=\"" +
                        strInputDateName + "\" value=\""
                        + this.getStringEscapeHtmlValue(strDate) + "\" class=\""
                        + strDateClass + strInputAddStyle
                        + "\" data-date-format=\"" + inputDatePattern + "\""
                        + strOutDisabled + tabIndex + strInputStyle + onchange + ">");

                out.print( "&nbsp;" );
            }

            if ( !StringUtil.isNullOrEmpty(strInputTimeName) ){

            	// 入力タイプ：年月日の場合、時間入力欄を隠し項目とする。
            	if (isTimeHidden) {
            		 out.print("<input type=\"hidden\" name=\"" +
                             strInputTimeName + "\" value=\""
                             + this.getStringEscapeHtmlValue(strTime) + "\">");
            	}
            	else {
            		 out.print("<input type=\"text\" name=\"" +
                             strInputTimeName + "\" value=\""
                             + this.getStringEscapeHtmlValue(strTime) + "\" class=\""
                             + strDateClass + strInputAddStyle
                             + "\" data-date-format=\"" + strFormatTime + "\""
                             + strOutDisabled + tabIndex + strInputStyle + onchange + ">");
            	}

            }
            out.print( "</div>\n");

        } catch(Exception e) {
            if (!Objects.isNull(out)) {
                try {
                    out.print(this.getCTagErrorToMenu());
                    out.flush();
                } catch (Exception e1) {
                    throw new JspTagException(e1);
                }
            }

            throw new JspException(e);
        }

        return SKIP_BODY;
    }

    /**
     * 終了処理
     */
    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

    /**
     * 解放処理
     */
    public void release() {}



}