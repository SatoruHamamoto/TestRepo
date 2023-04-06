package com.gnomes.common.tags;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.picketbox.util.StringUtil;

/**
 * 隠し項目カスタムタグ
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
public class GnomesCTagHidden extends GnomesCTagBase {

    /** 辞書：テキスト入力用のBean **/
    private static final String INFO_PARAM_NAME = "param_name";

    /** 辞書：テキスト入力参照用名称 **/
    private static final String INFO_INPUT_TEXT_NAME = "input_text_name";

    /** 辞書ID */
    private String dictId;

    /** bean */
    private Object bean;

    /** value */
    private String value;

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
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value セットする value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * hiddenタグ出力
     */
    public int doStartTag() throws JspException {

        JspWriter out = null;

        try {
            out = pageContext.getOut();

            // 辞書取得
            GnomesCTagDictionary dict = getCTagDictionary();
            // hidden辞書取得
            Map<String,Object> mapInfo = dict.getHiddenInfo(this.dictId);

            // hidden出力
            String strInputText;
            if (this.value == null) {

                if (this.bean != null) {
                    // Beanからの取得
                    // 出力元データのクラス
                    Class<?> clsBean = bean.getClass();

                    //INFO_PARAM_NAME:テキスト入力用のBean
                    String labelInputText = (String)mapInfo.get(INFO_PARAM_NAME);
                    strInputText = (String) getData(clsBean, bean, labelInputText);
                    if ( strInputText == null) {
                            strInputText ="";
                    }
                } else {
                    strInputText = null;
                }
            } else {
                strInputText = this.value;
            }
            if(StringUtil.isNullOrEmpty(strInputText)){
                strInputText = "";
            } else {
                strInputText = " value=\"" + strInputText + "\"" ;
            }

            // 左側ラベル出力
//        // typeName:タイプ指定 ( input/readonly/disabled)
            String strInputTextName = (String)mapInfo.get(INFO_INPUT_TEXT_NAME);
            if(StringUtil.isNullOrEmpty(strInputTextName)){
                strInputTextName = "";
            } else {
                strInputTextName = " name=\"" + strInputTextName + "\"";
            }

            out.print( "<input type=\"text\"" + strInputTextName + " hidden" +  strInputText + ">\n");

        } catch(Exception e) {
            if (!Objects.isNull(out)) {
                try {
                    out.print(getTagErrMes());
                    out.flush();
                } catch (IOException e1) {
                    throw new JspTagException(e1);
                } catch (Exception e2) {
                    throw new JspTagException(e2);
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