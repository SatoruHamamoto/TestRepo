package com.gnomes.common.tags;

import java.util.Arrays;
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
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.view.IScreenFormBean;

/**
 * チェックボックスカスタムタグ
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/2/27 YJP/S.Michiura             初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesCTagCheckBox extends GnomesCTagBase {
    /** 辞書：ラベルリソースID */
    private static final String INFO_LABEL_RESOUCE_ID = "label_resource_id";

    /** 辞書：チェックボックスの選択されたボタン値 */
    private static final String INFO_SELECT_ITEM_BEAN = "select_item_bean";

    /** 辞書：チェックボックスの参照名 */
    private static final String INFO_CHECK_BOX_NAME = "checkbox_name";

    /** 辞書：チェックボックスのアイテムリスト */
    private static final String INFO_CHECK_BOX_ITEM = "checkbox_item";

    /** 辞書：縦・横指定 **/
    private static final String INFO_IS_VERTICAL = "is_vertical";

    /** チェックボックスのボックス名表示 **/
    private static final String CHECKBOX_NAME = "name";

    /** チェックボックスのボックス名 **/
    private static final String CHECKBOX_VALUE = "value";

    /** 辞書：ラベル出力スタイルシートの追加設定 **/
    private static final String INFO_LABEL_CLASS_NAME = "label_add_class";

    /** 辞書：追加クラス */
    private static final String INFO_ADD_CLASS = "add_class";

    /** 辞書：INPUT追加クラス */
    private static final String INFO_INPUT_ADD_STYLE = "input_add_style";

    /** 辞書：工程端末表示か否か */
    private static final String INFO_IS_PANECON = "is_panecon";

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
     * チェックボックスタグ出力
     */
    @SuppressWarnings("unchecked")
    public int doStartTag() throws JspException {

        JspWriter out = pageContext.getOut();

        try {
            //Beanの指定が間違ってnullになっているのをチェック
            if (this.bean == null) {
                logHelper.severe(this.logger,null,null,NO_BEAN_ERR_MES);
                throw new GnomesAppException(NO_BEAN_ERR_MES);
            }

            Locale userLocale = ((IScreenFormBean) this.bean).getUserLocale();

            // 辞書取得
            GnomesCTagDictionary dict = getCTagDictionary();
            // ラベル辞書取得
            Map<String, Object> mapInfo = dict.getCheckBoxInfo(this.dictId);

            // 出力元データのクラス
            Class<?> clsBean = bean.getClass();

            // Beanからの取得
            //INFO_PARAM_NAME:選択されたボタン値
            String labelCheckBoxChecked = (String) mapInfo
                    .get(INFO_SELECT_ITEM_BEAN);

            // checkBoxName: <input type="checkbox" name="checkBoxName"
            String strCheckBoxName = (String) mapInfo.get(INFO_CHECK_BOX_NAME);

            Object checkedListObject = null;

            if (!StringUtil.isNullOrEmpty(labelCheckBoxChecked)) {
                checkedListObject = responseContext.getResponsesFormBean(bean,
                        labelCheckBoxChecked, strCheckBoxName);
            }

            List<String> lstCheckBoxChecked = null;

            if (checkedListObject instanceof String[]) {
                lstCheckBoxChecked = Arrays
                        .asList((String[]) checkedListObject);
            } else {
                lstCheckBoxChecked = (List<String>) checkedListObject;
            }

            // IsVertical: 縦出力時の改行設定
            String strIsVertical = (String) mapInfo.get(INFO_IS_VERTICAL);
            if (StringUtil.isNullOrEmpty(strIsVertical)) {
                strIsVertical = "";
            }
            String strVerticalBr = "";
            String strTitleAddClass = "";
            String strHeaderColData = "";
            if (strIsVertical.equals("true")) {
                strVerticalBr = "<br>";
                strTitleAddClass = " common-vertical-col-title";
                strHeaderColData = "common-vertical-col-data";
            } else {
                strHeaderColData = "common-header-col-data";
            }

            // 追加クラスの設定
            String strAddClass = (String) mapInfo.get(INFO_ADD_CLASS);
            if (StringUtil.isNullOrEmpty(strAddClass)) {
                strAddClass = "";
            } else {
                strAddClass = " " + strAddClass;
            }

            // 工程端末か否か（デフォルトは管理端末表示）
            boolean isPanecon = false;
            if (!StringUtil
                    .isNullOrEmpty((String) mapInfo.get(INFO_IS_PANECON))) {
                isPanecon = true;
            }

            // INPUT追加クラスの設定
            String strInputAddStyle = "";
            if (!StringUtil.isNullOrEmpty(
                    (String) mapInfo.get(INFO_INPUT_ADD_STYLE))) {
                strInputAddStyle = (String) mapInfo.get(INFO_INPUT_ADD_STYLE);
            }

            // 工程端末の場合、スタイルクラスを追加
            if (isPanecon) {
                strTitleAddClass = " common-panecon-header-col-title-checkbox"
                        + strTitleAddClass;
                strInputAddStyle = "common-input-checkbox " + strInputAddStyle;
            }

            if (!StringUtil.isNullOrEmpty(strInputAddStyle)) {
                strInputAddStyle = " class=\"" + strInputAddStyle + "\"";
            }

            // checkBoxItem:項目値
            String labelCheckBoxItems = (String) mapInfo
                    .get(INFO_CHECK_BOX_ITEM);
            List<Object> checkBoxItems = (List<Object>) getData(clsBean, bean,
                    labelCheckBoxItems);

            // ラベル出力　※リソースIDより取得
            String labelRourceId = (String) mapInfo.get(INFO_LABEL_RESOUCE_ID);
            if (labelRourceId != null) {
                // 出力ラベル名
                String strLabel = this.getStringEscapeHtml(
                        ResourcesHandler.getString(labelRourceId, userLocale));
                if (StringUtil.isNullOrEmpty(strLabel)) {
                    strLabel = "";
                }

                // 追加クラスの設定
                String strLabelClassName = (String) mapInfo
                        .get(INFO_LABEL_CLASS_NAME);
                if (StringUtil.isNullOrEmpty(strLabelClassName)) {
                    strLabelClassName = "";
                } else { // 追加がある場合はスペースを加える
                    strLabelClassName = " " + strLabelClassName;
                }

                // 左ラベルの出力
                out.print("  <div class=\"common-header-col-title"
                        + strTitleAddClass + strLabelClassName + "\">"
                        + strLabel + "</div>");
            }

            // 出力:<div class="common-header-col-data">
            out.print("<div class=\"" + strHeaderColData + strAddClass + "\">");

            if (checkBoxItems != null && checkBoxItems.size() > 0) {

                // 非活性の設定
                //inputActivity:入力活性非活性Bean
                boolean inputActivity = true;
                String labelInputActivity = (String) mapInfo
                        .get(INFO_INPUT_ACTIVITY);
                if (labelInputActivity != null) {
                	Object inputActivityData = getData(clsBean, bean, labelInputActivity);
                	if (inputActivityData instanceof Boolean) {
                		inputActivity = (Boolean)inputActivityData;
                	}
                }

                Class<?> lstClass = checkBoxItems.get(0).getClass();
                for (int i = 0; i < checkBoxItems.size(); i++) {
                    Object item = checkBoxItems.get(i);

                    String checkBoxItemValue = (String) getData(lstClass, item,
                            CHECKBOX_NAME);
                    String checkBoxItemText = (String) getData(lstClass, item,
                            CHECKBOX_VALUE);

                    String checkBoxChecked = "";
                    String hiddenValue = null;
                    if (lstCheckBoxChecked != null) {
                        if (lstCheckBoxChecked.contains(checkBoxItemValue)) {
                            checkBoxChecked = "checked";
                            hiddenValue = checkBoxItemValue;
                        }
                    }

                    String onchange = " onchange=\"" + SET_WARMING_FLAG +  "\"";

                    // チェック項目の出力
                    if (inputActivity) {
                        out.print("<input type=\"checkbox\" name=\""
                                + strCheckBoxName + "\" value=\""
                                + this.getStringEscapeHtmlValue(
                                        checkBoxItemValue)
                                + "\"" + checkBoxChecked + strInputAddStyle + onchange
                                + ">"
                                + this.getStringEscapeHtml(checkBoxItemText)
                                + strVerticalBr + "\n");
                    } else {
                        out.print("<input type=\"checkbox\" disabled "
                                + checkBoxChecked + strInputAddStyle + onchange + ">"
                                + this.getStringEscapeHtml(checkBoxItemText)
                                + strVerticalBr + "\n");
                        if (!StringUtil.isNullOrEmpty(hiddenValue)) {
                            out.print("<input type=\"hidden\" name=\""
                                    + strCheckBoxName + "\" value=\""
                                    + this.getStringEscapeHtmlValue(hiddenValue)
                                    + "\" >\n");
                        }
                    }
                }
            }

            // 出力:</div>
            out.print("</div>\n");
        } catch (Exception e) {
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
    public void release() {
    }

}