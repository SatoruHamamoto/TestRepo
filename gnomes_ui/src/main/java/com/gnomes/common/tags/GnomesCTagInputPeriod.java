package com.gnomes.common.tags;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.picketbox.util.StringUtil;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.view.IScreenFormBean;

/**
 * 日付期間入力カスタムタグ
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
public class GnomesCTagInputPeriod extends GnomesCTagBase {
    /** 辞書：ラベルリソースID **/
    private static final String INFO_LABEL_RESOUCE_ID = "label_resource_id";

    /** 辞書：日付フォーマット リソースID **/
    private static final String INFO_FORMAT_DATE = "format_date";

    /** 辞書：開始日　日付Bean **/
    private static final String INFO_FROM_DATE = "from_date";

    /** 辞書：開始日　Inputタイプ **/
    private static final String INFO_FROM_TYPE_NAME = "from_input_type";

    /** 辞書：開始日　入力活性非活性Bean **/
    private static final String INFO_FROM_INPUT_ACTIVITY = "from_input_activity";

    /** 辞書：開始日　参照用名称 **/
    private static final String INFO_FROM_INPUT_DATE_NAME = "from_input_date_name";

    /** 辞書：終了日　日付Bean **/
    private static final String INFO_TO_DATE = "to_date";

    /** 辞書：終了日　Inputタイプ **/
    private static final String INFO_TO_TYPE_NAME = "to_input_type";

    /** 辞書：終了日　入力活性非活性Bean **/
    private static final String INFO_TO_INPUT_ACTIVITY = "to_input_activity";

    /** 辞書：終了日　参照用名称 **/
    private static final String INFO_TO_INPUT_DATE_NAME = "to_input_date_name";

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

    /** 辞書：開始日 入力値変更後の処理 */
    private static final String INFO_FROM_ON_CHANGE_EVENT = "from_on_change_event";

    /** 辞書：入力値変更後の処理 コマンドID */
    private static final String INFO_FROM_ON_CHANGE_COMMAND_ID = "from_on_change_command_id";

    /** 辞書：入力値変更後の処理 コマンド */
    private static final String INFO_FROM_ON_CHANGE_BEAN_COMMAND_ID = "from_on_change_bean_command_id";

    /** 辞書：終了日 入力値変更後の処理 */
    private static final String INFO_TO_ON_CHANGE_EVENT = "to_on_change_event";

    /** 辞書：入力値変更後の処理 コマンドID */
    private static final String INFO_TO_ON_CHANGE_COMMAND_ID = "to_on_change_command_id";

    /** 辞書：入力値変更後の処理 コマンド */
    private static final String INFO_TO_ON_CHANGE_BEAN_COMMAND_ID = "to_on_change_bean_command_id";

    /** 辞書ID */
    private String dictId;

    /** bean */
    private Object bean;


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
     * 期間入力タグ出力
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
            Map<String,Object> mapInfo = dict.getPeriodInfo(this.dictId);

            // 出力元データのクラス
            Class<?> clsBean = bean.getClass();

            // 工程端末か否か（デフォルトは管理端末表示）
            boolean isPanecon = false;
            if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_IS_PANECON))){
                isPanecon = true;
            }

            String labelRourceId = (String)mapInfo.get(INFO_LABEL_RESOUCE_ID);
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
            }


            //日付フォーマット （リソースIDより取得） ※Beanからの取得
            String FormatDateId = (String)mapInfo.get(INFO_FORMAT_DATE);
            String strFormatDate = ResourcesHandler.getString(FormatDateId, userLocale);

            // typeName:タイプ指定 ( input/readonly/disabled)  ※直接読み込み
            String strFromInputType = (String)mapInfo.get(INFO_FROM_TYPE_NAME);
            String strToInputType = (String)mapInfo.get(INFO_TO_TYPE_NAME);


            //inputActivity:入力活性非活性Bean  ※Beanからの取得
            boolean fromInputActivity = true;
            String labelFromInputActivity = (String)mapInfo.get(INFO_FROM_INPUT_ACTIVITY);
            if ( labelFromInputActivity != null) {
                Object fromInputActivityData = getData(clsBean, bean, labelFromInputActivity);
            	if (fromInputActivityData instanceof Boolean) {
            		fromInputActivity = (Boolean)fromInputActivityData;
            	}
            }

            boolean toInputActivity = true;
            String labelToInputActivity = (String)mapInfo.get(INFO_TO_INPUT_ACTIVITY);
            if ( labelToInputActivity != null) {
            	Object toInputActivityData = getData(clsBean, bean, labelToInputActivity);
            	if (toInputActivityData instanceof Boolean) {
            		toInputActivity = (Boolean)toInputActivityData;
            	}
            }

            String strFromOutDisabled = "";
            if ( strFromInputType.equals(INPUT_DISABLED)){
                strFromOutDisabled = INPUT_DISABLED;
            } else if (( strFromInputType.equals(INPUT_READONLY)) || !fromInputActivity ){
                strFromOutDisabled = INPUT_READONLY;
            }

            String strToOutDisabled = "";
            if ( strToInputType.equals(INPUT_DISABLED)){
                strToOutDisabled = INPUT_DISABLED;
            } else if (( strToInputType.equals(INPUT_READONLY)) || !toInputActivity ){
                strToOutDisabled = INPUT_READONLY;
            }

            String tabIndexFrom = "";
            // readonlyの場合、タブ移動によるフォーカスを当てさせない
            if (strFromOutDisabled.equals(INPUT_READONLY)) {
            	tabIndexFrom = " tabindex=\"-1\"";
            }

            String tabIndexTo = "";
            // readonlyの場合、タブ移動によるフォーカスを当てさせない
            if (strToOutDisabled.equals(INPUT_READONLY)) {
            	tabIndexTo = " tabindex=\"-1\"";
            }

            // 日付入力Name ※直接読み込み
            String strFromInputDateName = (String)mapInfo.get(INFO_FROM_INPUT_DATE_NAME);
            String strToInputDateName = (String)mapInfo.get(INFO_TO_INPUT_DATE_NAME);

            // value
            String strFromDate = null;
            String strToDate = null;

            //INFO_PARAM_NAME:入力する数値のBean  ※Beanからの取得
            String labelFromDate = (String)mapInfo.get(INFO_FROM_DATE);
            Object objFromDate = responseContext.getResponseFormBean(bean, labelFromDate, strFromInputDateName);
            String labelToDate = (String)mapInfo.get(INFO_TO_DATE);
            Object objToDate = responseContext.getResponseFormBean(bean, labelToDate, strToInputDateName);

            if (objFromDate instanceof String) {
                strFromDate = (String)objFromDate;
                strToDate = (String)objToDate;
            } else {
                // 日付Format変換
                strFromDate = getStringDate(dictId, labelFromDate, objFromDate, strFormatDate);
                strToDate = getStringDate(dictId, labelToDate, objToDate, strFormatDate);
            }
            if (strFromDate == null) {
                strFromDate = "";
            }
            if (strToDate == null) {
                strToDate = "";
            }

            // INPUT差し替えクラス
            String strDateClass = (String)mapInfo.get(INFO_INPUT_CHANGE_CLASS);
            // INPUT差し替えクラスが設定されていない場合
            if(StringUtil.isNullOrEmpty(strDateClass)){
                // 共通の日付クラスを使用（日付入力ダイアログを表示）
                strDateClass = "datetime";
            }

            // 追加クラスの設定
            String strAddClass = (String)mapInfo.get(INFO_ADD_CLASS);
            if(StringUtil.isNullOrEmpty(strAddClass)){
                strAddClass = "";
            } else {
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

            // jsp出力用に日付フォーマットを修正する
            strFormatDate = strFormatDate.replaceAll("y", "Y");
            strFormatDate = strFormatDate.replaceAll("d", "D");

            // 開始日 入力値変更後の処理
            String fromOnchange = "";
            if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_FROM_ON_CHANGE_EVENT))){
            	fromOnchange = " onchange=\"" + (String)mapInfo.get(INFO_FROM_ON_CHANGE_EVENT) + "\"";
            }
            else if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_FROM_ON_CHANGE_COMMAND_ID))){
            	fromOnchange = " onchange=\"" + String.format(COMMAND_SCRIPT_FORMAT, String.valueOf((String)mapInfo.get(INFO_FROM_ON_CHANGE_COMMAND_ID))) + "\"";

            }
            else if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_FROM_ON_CHANGE_BEAN_COMMAND_ID))){
            	Object commandId = getData(clsBean, bean, (String)mapInfo.get(INFO_FROM_ON_CHANGE_BEAN_COMMAND_ID));
            	if (commandId != null) {
            		fromOnchange = " onchange=\"" + String.format(COMMAND_SCRIPT_FORMAT, String.valueOf(commandId)) + "\"";
            	}
            }

            // 終了日 入力値変更後の処理
            String toOnchange = "";
            if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_TO_ON_CHANGE_EVENT))){
            	toOnchange = " onchange=\"" + (String)mapInfo.get(INFO_TO_ON_CHANGE_EVENT) + "\"";
            }
            else if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_TO_ON_CHANGE_COMMAND_ID))){
            	toOnchange = " onchange=\"" + String.format(COMMAND_SCRIPT_FORMAT, String.valueOf((String)mapInfo.get(INFO_TO_ON_CHANGE_COMMAND_ID))) + "\"";

            }
            else if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_TO_ON_CHANGE_BEAN_COMMAND_ID))){
            	Object commandId = getData(clsBean, bean, (String)mapInfo.get(INFO_TO_ON_CHANGE_BEAN_COMMAND_ID));
            	if (commandId != null) {
            		toOnchange = " onchange=\"" + String.format(COMMAND_SCRIPT_FORMAT, String.valueOf(commandId)) + "\"";
            	}
            }

            out.print( "<div class=\"common-header-col-data common-header-table-date-input" + strAddClass + "\">\n");
            out.print("<input type=\"text\" name=\"" + strFromInputDateName
                    + "\" "
                    + " value=\"" + this.getStringEscapeHtmlValue(strFromDate)
                    + "\" class=\"" + strDateClass + strInputAddStyle
                    + "\" data-date-format=\"" + strFormatDate + "\""
                    + strFromOutDisabled + tabIndexFrom + strInputStyle + fromOnchange + ">");

            out.print( "<span class=\"common-text-center\">&nbsp;～&nbsp;</span>");

            out.print("<input type=\"text\" name=\"" + strToInputDateName + "\" "
                    + " value=\"" + this.getStringEscapeHtmlValue(strToDate)
                    + "\" class=\"" + strDateClass + strInputAddStyle
                    + "\" data-date-format=\"" + strFormatDate + "\""
                    + strToOutDisabled + tabIndexTo + strInputStyle + toOnchange + ">\n");

            out.print("</div>");
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