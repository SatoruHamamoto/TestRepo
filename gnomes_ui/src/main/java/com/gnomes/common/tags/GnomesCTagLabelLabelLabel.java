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
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.view.IScreenFormBean;

/**
 * ラベル+ラベル+ラベルカスタムタグ
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
public class GnomesCTagLabelLabelLabel extends GnomesCTagBase {

    /** 辞書：左側ラベルリソースID **/
    private static final String INFO_LABEL_RESOUCE_ID = "label_resource_id";

    /** 辞書：中央ラベル初期値Bean (必須) **/
    private static final String INFO_LABEL_BEAN_ID = "label_bean_id";

    /** 辞書：右側ラベルBean **/
    private static final String INFO_UNIT_NAME = "unit_name";

    /** 辞書：ラベル出力スタイルシートの追加設定 **/
    private static final String INFO_LABEL_CLASS_NAME = "label_add_class";

    /** 辞書：追加クラス */
    private static final String INFO_ADD_CLASS = "add_class";

    /** 辞書：中央ラベル Beanタイプ */
    private static final String INFO_VALUE_TYPE = "value_type";

    /** 辞書：中央ラベル 小数点桁数Bean*/
    private static final String INFO_DECIMAL_POINT = "decimal_point_bean";

    /** 辞書：中央ラベル 日付時刻フォーマット リソースID */
    private static final String INFO_FORMAT_DATETIME = "format_date_time";


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
     * ラベルタグ出力
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
            Map<String,Object> mapInfo = dict.getLabelInfo(this.dictId);

            // 出力元データのクラス
            Class<?> clsBean = bean.getClass();

            // 左側ラベル出力　※Beanより取得
            String labelRourceId = (String)mapInfo.get(INFO_LABEL_RESOUCE_ID);
            if ( labelRourceId != null) {
                /* 出力ラベル名 */
                String strLabel = ResourcesHandler.getString(labelRourceId, userLocale);
                if(StringUtil.isNullOrEmpty(strLabel)){
                	strLabel = "";
                }

                /* 追加クラスの設定*/
                String strLabelClassName = (String)mapInfo.get(INFO_LABEL_CLASS_NAME);
                if(StringUtil.isNullOrEmpty(strLabelClassName)){
                	strLabelClassName = "";
                } else {    // 追加がある場合はスペースを加える
                	strLabelClassName = " " + strLabelClassName;
                }

                /* 左ラベルの出力 */
                out.print("  <div class=\"common-header-col-title" + strLabelClassName + "\">" + strLabel + "</div>");
            }

            // 右側ラベル出力　※Beanより取得
            // 右側ラベル出力　※Beanより取得

            String strCommonNumber = "";
            String strBeanLabel= "";
            String strValueType = (String)mapInfo.get(INFO_VALUE_TYPE);
            if(StringUtil.isNullOrEmpty(strValueType)){
            	strValueType = "";
            }
            String strLabelBeanId = (String)mapInfo.get(INFO_LABEL_BEAN_ID);
            if( !StringUtil.isNullOrEmpty(strLabelBeanId)){
                Object valueObj  = getData(clsBean, bean, strLabelBeanId);
                switch(strValueType){
                case TAG_TYPE_NUMBER:
                    //decimalPoint:小数点桁数
                    Integer decimalPoint = 0;
                    String labelDecimalPoint = (String)mapInfo.get(INFO_DECIMAL_POINT);
                    if ( labelDecimalPoint != null) {
                        decimalPoint = (Integer) getData(clsBean, bean, labelDecimalPoint);
                    }
                    strBeanLabel = getStringNumber(dictId, strLabelBeanId, valueObj, decimalPoint);
                    strCommonNumber = " common-text-number";
                    break;
                case TAG_TYPE_DATE:
                case TAG_TYPE_ZONEDDATETIME:

                	//日付フォーマット ※リソースIDより取得
                    String FormatDateTimeId = (String)mapInfo.get(INFO_FORMAT_DATETIME);
                    String strFormatDateTime = ResourcesHandler.getString(FormatDateTimeId, userLocale);
                    strBeanLabel = getStringDate(dictId, strLabelBeanId,valueObj, strFormatDateTime);
                    break;

                default:
                    if ( strLabelBeanId != null) {
                        strBeanLabel = (String)valueObj;
                        if(StringUtil.isNullOrEmpty(strBeanLabel)){
                        	strBeanLabel = "";
                        }
                    }
                    break;
                }


                //unitName:単位名称　※Beanより取得
                String strUnitName = "";
                String labelUnitName = (String)mapInfo.get(INFO_UNIT_NAME);
                if ( labelUnitName != null) {
                    strUnitName = (String) getData(clsBean, bean, labelUnitName);
                    if(StringUtil.isNullOrEmpty(strUnitName)){
                    	strUnitName = "";
                    }
                }
                if ( !strUnitName.equals("")){
                	strUnitName = "&nbsp;" + strUnitName;
                }

                String addClass = (String)mapInfo.get(INFO_ADD_CLASS);
                if(StringUtil.isNullOrEmpty(addClass)){
                	addClass = "";
                } else {
                	addClass = " " + addClass;
                }

                out.print( "<div class=\"common-header-col-data" +
                    addClass + strCommonNumber +"\">" + strBeanLabel + strUnitName + "</div>\n");
            }
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