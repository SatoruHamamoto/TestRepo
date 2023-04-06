package com.gnomes.common.tags;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.picketbox.util.StringUtil;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.view.IScreenFormBean;

/**
 * プルダウンカスタムタグ
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/01/25 YJP/I.Shibasaka           初版
 * R0.01.02 2019/01/28 YJP/A.Oomori              プルダウンの選択名を任意のパラメータ値に差し替えできるよう修正
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesCTagInputPullDown extends GnomesCTagBase {

    /** 辞書：ラベルリソースID */
    private static final String INFO_LABEL_RESOUCE_ID = "label_resource_id";

    /** 辞書：単位ラベルリソースID */
    private static final String INFO_UNIT_LIST = "label_list";

    /** 辞書：プルダウン名 */
    private static final String INFO_PULLDOWN_NAME = "pulldown_name";

    /** 辞書：オートコンプリート */
    private static final String INFO_IS_AUTOCOMLETE = "is_autocomplete";

    /** 辞書：プルダウン候補パラメータ名 */
    private static final String INFO_LIST_PARAM_NAME = "list_param_name";

    /** 辞書：プルダウン選択パラメータ名 */
    private static final String INFO_SELECT_PARAM_NAME = "select_param_name";

    /** 辞書：追加クラス */
    private static final String INFO_ADD_CLASS = "add_class";

    /** 辞書：先頭空白 */
    private static final String INFO_DEFAULT_SPACE = "default_space";

    /** 辞書：プルダウンのスタイル */
    private static final String INFO_PULLDOWN_STYLE = "style";

    /** 辞書：工程端末表示か否か */
    private static final String INFO_IS_PANECON = "is_panecon";

    /** 辞書：左側ラベルの追加クラス */
    private static final String INFO_HEADER_ADD_CLASS = "header_add_class";

    /** 辞書：右側ラベルの追加クラス */
    private static final String INFO_DATA_ADD_CLASS = "data_add_class";

    /** 辞書：INPUTタイプ **/
    private static final String INFO_INPUT_TYPE = "input_type";

    /** 辞書：入力活性非活性Bean **/
    private static final String INFO_INPUT_ACTIVITY = "input_activity";

    /** 辞書：onChangeイベントクラス名 **/
    private  static final String  INFO_ONCHANGE_EVENT = "on_change_event";

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
     * プルダウンタグ出力
     */
    @SuppressWarnings({ "unchecked" })
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
            // 辞書取得
            Map<String,Object> mapInfo = dict.getPullDownInfo(this.dictId);

            // 出力元データのクラス
            Class<?> clsBean = bean.getClass();

            // 工程端末か否か（デフォルトは管理端末表示）
            boolean isPanecon = false;
            if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_IS_PANECON))){
                isPanecon = true;
            }

            // ラベルリソースID
            String strLabel = null;
            String labelRourceId = (String)mapInfo.get(INFO_LABEL_RESOUCE_ID);
            if (labelRourceId != null && !labelRourceId.equals("")) {
                strLabel = this.getStringEscapeHtml(ResourcesHandler.getString(labelRourceId, userLocale));
            }

            // プルダウン名
            String pulldownName = (String)mapInfo.get(INFO_PULLDOWN_NAME);
            if (pulldownName == null) {
                pulldownName = "";
            }

            // プルダウン候補パラメータ名
            String listParamName = (String)mapInfo.get(INFO_LIST_PARAM_NAME);
            List<Object> lstValue = (List<Object>) getData(clsBean, bean, listParamName);

            // プルダウン選択パラメータ名
            Object selValue = null;
            String selectParamName = (String)mapInfo.get(INFO_SELECT_PARAM_NAME);
            if (StringUtil.isNullOrEmpty(selectParamName)) {
            	selectParamName = "";
            }

            // 選択値パラメータ名と差し替え用選択名パラメータ名を分割
            String selectParamNames[] = selectParamName.split(",");

            if(selectParamNames.length >= 1 && !StringUtil.isNullOrEmpty(selectParamNames[0])){

                selValue = responseContext.getResponseFormBean(bean,
                		selectParamNames[0], pulldownName);
                if (selValue == null ) {
                	selValue = "";
                }

            }

            // プルダウン選択値がNULLの場合
        	String[] outSel = new String[2];
        	// 選択値が選択候補に含まれていない場合、選択値を空に置き換える。
        	if (!getSelect(lstValue, selValue, outSel)) {
        		selValue = "";
        	}

            // プルダウン選択項目名
            String selName = "";

            // 差し替え用選択名パラメータ名が設定されている場合、差し替え用プルダウン選択項目名を取得
            if (selectParamNames.length == 2 && !StringUtil.isNullOrEmpty(selectParamNames[1])) {
            	Object nameObj = responseContext.getResponseFormBean(bean,
                		selectParamNames[1], pulldownName);
                if (nameObj != null && !StringUtil.isNullOrEmpty(selValue.toString())) {
                    selName = nameObj.toString();
                }
            }

            // 先頭空白有りか否か
            String dsFlag = (String) mapInfo.get(INFO_DEFAULT_SPACE);

            // onChangeイベントクラス名
            String strOnchange;
            String onchange = (String) mapInfo.get(INFO_ONCHANGE_EVENT );
            if (onchange == null) {
                onchange = "";
                strOnchange = " onchange=\"" + SET_WARMING_FLAG +  "\"";
            } else {
                strOnchange = "onchange=\""+ SET_WARMING_FLAG + onchange + "\"";
            }

            // 追加クラス
            String addClass = (String)mapInfo.get(INFO_ADD_CLASS);
            if (addClass == null) addClass = "";
            if ( !addClass.equals("")) addClass = " " + addClass;

            // 左側ラベルの追加クラス
            String headerAddClass = (String)mapInfo.get(INFO_HEADER_ADD_CLASS);
            if (headerAddClass == null) headerAddClass = "";
            if ( !headerAddClass.equals("")) headerAddClass = " " + headerAddClass;

            // 左側ラベルの追加クラス
            String dataAddClass = (String)mapInfo.get(INFO_DATA_ADD_CLASS);
            if (dataAddClass == null) dataAddClass = "";
            if ( !dataAddClass.equals("")) dataAddClass = " " + dataAddClass;


            String style = (String)mapInfo.get(INFO_PULLDOWN_STYLE);
            if (style == null) {
                style = "";
            }

            // オートコンプリートか否か
            String isAutocomplete = (String)mapInfo.get(INFO_IS_AUTOCOMLETE);

            // 単位ラベルリソースID
            String unitList = (String)mapInfo.get(INFO_UNIT_LIST);
            if ( unitList == null ){
                unitList = "";
            }

            // typeName:タイプ指定 ( input/readonly/disabled)
            String strInputType = (String)mapInfo.get(INFO_INPUT_TYPE);
            if(StringUtil.isNullOrEmpty(strInputType)){
                strInputType = "";
            }

            // 非活性の設定
            //inputActivity:入力活性非活性Bean
            boolean inputActivity = true;
            String labelInputActivity = (String)mapInfo.get(INFO_INPUT_ACTIVITY);
            if ( labelInputActivity != null) {
            	Object inputActivityData = getData(clsBean, bean, labelInputActivity);
            	if (inputActivityData instanceof Boolean) {
            		inputActivity = (Boolean)inputActivityData;
            	}
            }

            if ( !strInputType.equals(INPUT_DISABLED) && !strInputType.equals(INPUT_READONLY) ){
                if ( !inputActivity ){
                    strInputType = INPUT_READONLY;
                } else {
                    strInputType = "";
                }
            }

            //上記判定で活性でも、領域が異なると非活性になる
            if(strInputType.length() == 0){
            	if(! this.judgeDisplayFromConnectionArea(mapInfo)){
            		strInputType = INPUT_READONLY;
            	}
            }

            // readonlyの場合、選択データを隠し項目として出力し、プルダウン部はdisabledとして表示
            if(strInputType.equals(INPUT_READONLY)){

                out.print("<input type=\"hidden\" name=\""+ pulldownName + "\" value=\"" + selValue + "\">\n");
                strInputType = INPUT_DISABLED;
                pulldownName = pulldownName + "_pulldown";// リネーム
            }

            String dataClassName = "common-header-col-data-titleless";
            if (strLabel != null) {
                out.print("<div class=\"common-header-col-title " + headerAddClass + "\">" + strLabel + "</div>");
                dataClassName = "common-header-col-data";
            }

            out.print("<div class=\"" + dataClassName + " " + dataAddClass + "\">");

            String strInputClass;
            String strOnclick;
            // 工程端末の場合
            if ( isPanecon ){
               strInputClass = "common-data-input-size-item-code ";
               // プルダウン選択テーブルダイアログの出力
               strOnclick = "onclick=\"MakePullDownTableModal($(this), '" + strLabel + "', '" + onchange + "');\"";
               // プルダウン選択テーブルダイアログで選択した時に発生させるため、空にする。
               strOnchange = "";

            } else {
                strInputClass = " ";
                strOnclick = "";
            }

            // オートコンプリート有りの場合
            if ( !StringUtil.isNullOrEmpty(isAutocomplete) ){

                String mode = isAutocomplete;
                if (!mode.equals(AUTO_MODE_VALUE) && !mode.equals(AUTO_MODE_TEXT)) {
                    mode = AUTO_MODE_VALUE_TEXT;
                }
                out.println("<select name=\"" + pulldownName + "\" class=\"" + CLASS_AUTO_COMBO + " " + strInputClass + addClass  + "\" "+ strOnchange + " data-mode=\"" + mode + "\" " + strInputType +">");
            } else {
                out.print("<select name=\"" + pulldownName + "\" class=\"" + strInputClass + addClass  + "\""+ strOnchange + strOnclick + " " + strInputType + ">");
            }

            // 空行
            if (!StringUtil.isNullOrEmpty(dsFlag)) {
                out.print("<option value=\"\"></option>");
            }

            // プルダウン内データの生成
            outSelectOption(out, lstValue, selValue, selName);

            out.print("</select>");

            // 単位の生成
            if (unitList != null && !unitList.equals("")) {
                List<Object> lstUnit = (List<Object>) getData(clsBean, bean, unitList);

                outSel = new String[2];
                if (getSelect(lstUnit, selValue, outSel)) {
                    out.print(this.getStringEscapeHtml(outSel[1].toString()));
                }

            }
            out.print("</div>\n");
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