package com.gnomes.common.tags;

import java.util.List;
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
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.view.IScreenFormBean;

/**
 * ラジオボタンカスタムタグ
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/10/07 YJP/30022467               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesCTagRadioButton extends GnomesCTagBase {

    /** 辞書：選択値取得Bean */
    private static final String INFO_SELECT_ITEM_BEAN = "select_item_bean";

    /** 辞書：ラジオボタン参照用名称 */
    private static final String INFO_RADIO_NAME = "radio_name";

    /** 辞書：選択項目取得Bean */
    private static final String INFO_RADIO_ITEM = "radio_item";

    /** 辞書：縦表示か否か */
    private static final String INFO_IS_VERTICAL = "is_vertical";

    /** 辞書：ラベルリソースID */
    private static final String INFO_LABEL_RESOUCE_ID = "label_resource_id";

    /** 辞書：ラベル出力スタイルシートの追加設定 */
    private static final String INFO_LABEL_CLASS_NAME = "label_add_class";

    /** 辞書：追加クラス */
    private static final String INFO_ADD_CLASS = "add_class";

    /** 辞書：INPUTの追加クラス */
    private static final String INFO_INPUT_ADD_STYLE = "input_add_style";

    /** 辞書：工程端末表示か否か */
    private static final String INFO_IS_PANECON = "is_panecon";

    /** ラジオボタン候補名 */
    private static final String RADIOBUTTON_NAME = "name";

    /** ラジオボタン候補値 */
    private static final String RADIOBUTTON_VALUE = "value";

    /** 辞書：入力活性非活性Bean **/
    private static final String INFO_INPUT_ACTIVITY = "input_activity";

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
     * ラジオボタンタグ出力
     */
    @SuppressWarnings("unchecked")
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
            Map<String,Object> mapInfo = dict.getRadioInfo(this.dictId);


            // 出力元データのクラス
            Class<?> clsBean = bean.getClass();
            // 右側ラベル出力
            String labelRourceId = (String)mapInfo.get(INFO_LABEL_RESOUCE_ID);

            // radioName: <input type="radio" name="radioName"
            String strRadioName = (String)mapInfo.get(INFO_RADIO_NAME);


            // Beanからの取得
            //INFO_PARAM_NAME:選択されたボタン値
            String labelSelectItem = (String)mapInfo.get(INFO_SELECT_ITEM_BEAN);
            String strSelectItem = "";

            if (!StringUtil.isNullOrEmpty(labelSelectItem)) {
                strSelectItem = (String)responseContext.getResponseFormBean(bean, labelSelectItem, strRadioName);
            }

            // radioDirection:
            // IsVertical: 縦出力時の改行設定
            String strIsVertical= (String)mapInfo.get(INFO_IS_VERTICAL);
            if(StringUtil.isNullOrEmpty(strIsVertical)){
                strIsVertical = "";
            }
            String strVerticalBr="";
            String strTitleAddClass = "";
            String strHeaderColData = "";
            if(strIsVertical.equals("true")){
                strVerticalBr = "<br>";
                strTitleAddClass = " common-vertical-col-title";
                strHeaderColData = "common-vertical-col-data";
            } else {
                strHeaderColData = "common-header-col-data";
            }

            // 工程端末か否か（デフォルトは管理端末表示）
            boolean isPanecon = false;
            if (!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_IS_PANECON))) {
                isPanecon = true;
            }

            // INPUT追加クラスの設定
            String strInputAddStyle = "";
            if (!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_INPUT_ADD_STYLE))) {
                strInputAddStyle = (String)mapInfo.get(INFO_INPUT_ADD_STYLE);
            }

            // 工程端末の場合、スタイルクラスを追加
            if (isPanecon) {
                strTitleAddClass = " common-panecon-header-col-title-radiobutton" + strTitleAddClass;
                strInputAddStyle = "common-panecon-input-radiobutton " + strInputAddStyle;
            }

            if (!StringUtil.isNullOrEmpty(strInputAddStyle)) {
                strInputAddStyle = " class=\"" + strInputAddStyle + "\"";
            }

            // radioItem:項目値
            String labelradioItems = (String)mapInfo.get(INFO_RADIO_ITEM);
            List<Object> radioItems = (List<Object>)getData(clsBean, bean, labelradioItems);

            if ( labelRourceId != null) {
                // 出力ラベル名
                String strLabel = this.getStringEscapeHtmlValue(ResourcesHandler.getString(labelRourceId, userLocale));
                if(StringUtil.isNullOrEmpty(strLabel)){
                    strLabel = "";
                }

                // ラベルの追加クラスの設定
                String strLabelClassName = (String)mapInfo.get(INFO_LABEL_CLASS_NAME);
                if(StringUtil.isNullOrEmpty(strLabelClassName)){
                    strLabelClassName = "";
                } else {    // 追加がある場合はスペースを加える
                    strLabelClassName = " " + strLabelClassName;
                }

                // 左ラベルの出力
                out.print("  <div class=\"common-header-col-title" + strTitleAddClass +
                    strLabelClassName + "\">" + strLabel + "</div>");
            }

            // 追加クラスの設定
            String strAddClass = (String)mapInfo.get(INFO_ADD_CLASS);
            if(StringUtil.isNullOrEmpty(strAddClass)){
                strAddClass = "";
            } else {
                strAddClass = " " + strAddClass;
            }

            out.print( "<div class=\"" + strHeaderColData + strAddClass + "\">");

            if (radioItems != null && radioItems.size() > 0) {

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

                Class<?> lstClass = radioItems.get(0).getClass();
                String strChecked;
                for (int i=0; i<radioItems.size(); i++) {
                    Object item = radioItems.get(i);

                    String radioItemValue = (String)getData(lstClass, item, RADIOBUTTON_NAME);
                    String radioItemText = (String)getData(lstClass, item, RADIOBUTTON_VALUE);

                    if (StringUtil.isNullOrEmpty(radioItemText)) {
                        radioItemText = "";
                    }

                    String hiddenValue = null;
                    // チェックマーク
                    if ( radioItemValue.equals(strSelectItem)){
                        strChecked = " checked=\"checked\"";
                        hiddenValue = radioItemValue;
                    } else {
                        strChecked = "";
                    }

                    // 入力値変更後の処理
                    String onchange = " onchange=\"" + SET_WARMING_FLAG +  "\"";

                    // 出力
                    if (inputActivity) {
                        out.print("<input type=\"radio\" name=\"" + strRadioName
                                + "\" value=\"" + this.getStringEscapeHtmlValue(
                                        radioItemValue.toString())
                                + "\"" +
                                strChecked + strInputAddStyle + onchange + ">" + this.getStringEscapeHtml(radioItemText)
                                + strVerticalBr + "\n");
                    } else {
                        out.print("<input type=\"radio\" disabled " +
                                strChecked + strInputAddStyle + onchange + ">" + this.getStringEscapeHtml(radioItemText)
                                + strVerticalBr + "\n");
                        if (!StringUtil.isNullOrEmpty(hiddenValue)) {
                            out.print( "<input type=\"hidden\" name=\"" + strRadioName + "\" value=\"" +
                                    this.getStringEscapeHtmlValue(hiddenValue) +"\" >\n"  );

                        }
                    }
                }
            }

            //</div>
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