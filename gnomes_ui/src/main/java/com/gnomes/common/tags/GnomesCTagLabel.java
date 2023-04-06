package com.gnomes.common.tags;

import java.util.ArrayList;
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
 * ラベルカスタムタグ
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
  * R0.01.01 2018/2/27 YJP/S.Michiura             初版
  * R0.01.02 2018/10/18 YJP/S.Hamamoto			  日付ラベルでnullを指定した場合空白が潰れるので&nbspを付与
  * R0.01.03 2018/12/19 KCC/K.Fujiwara			  数値ラベルでnullを指定した場合空白が潰れるので&nbspを付与
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesCTagLabel extends GnomesCTagBase {

    /** 辞書：左側ラベルリソースID **/
    private static final String INFO_LABEL_RESOUCE_ID = "label_resource_id";

    /** 辞書：中央ラベル初期値Bean (必須) **/
    private static final String INFO_LABEL_BEAN_ID = "label_bean_id";

    /** 辞書：ラベル出力スタイルシートの追加設定 **/
    private static final String INFO_LABEL_STYLE_CLASS = "label_style_class";

    /** 辞書：追加クラス */
    private static final String INFO_ADD_CLASS = "add_class";

    /** 辞書：Beanタイプ */
    private static final String INFO_VALUE_TYPE = "value_type";

    /** 辞書：小数点桁数Bean*/
    private static final String INFO_DECIMAL_POINT = "decimal_point_bean";

    /** 辞書：日付時刻フォーマット リソースID */
    private static final String INFO_FORMAT_DATETIME = "format_date_time";

    /** 辞書：改行追加 */
    private static final String INFO_ADD_BR = "add_br";

    /** 辞書：ラベル参照用名称 **/
    private static final String INFO_LABEL_NAME = "label_name";

    /** タイトルの時のCSSスタイルクラス　*/
    private static final String CSS_TITLE = "common-header-col-title";

    /** データの時のCSSスタイルクラス */
    private static final String CSS_DATA = "common-header-col-data";

    /** データが数値の時のCSSスタイル追加クラス */
    private static final String CSS_NUMBER = " common-text-number";

    /** 辞書：工程端末表示か否か */
    private static final String INFO_IS_PANECON = "is_panecon";

    /** 改行するCSSスタイルクラス */
    private static final String CSS_WRAP = " common-text-wrap";

    /** CSS・HTMLコード */
    private static final String CSS_SPACE = " ";
    private static final String CSS_HALF_SPACE = "&nbsp;";
    private static final String CSS_SPAN = "<span>";
    private static final String CSS_SPAN_END = "</span>";
    private static final String CSS_BR = "<br>";



    /** 辞書ID */
    private String dictId;

    /** bean */
    private Object bean;

    /** 辞書 */
    GnomesCTagDictionary dict;
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

        	// ラベル項目辞書取得
            Map<String,Object> labelItemInfo = dict.getLabelItemInfo(this.dictId);

            // 出力元データのクラス
            Class<?> clsBean = bean.getClass();

            // データ出力フラグ
            boolean dataOutFlag = false;
            String strDataStyleClass = "";
            String strDataAddClass = "";
            String strDataCommonNumber = "";
            String strDataLabel = "";
            String strBr = "";
            String strInputTextName = "";
            String strWrapClass = "";

            // item_numberのリストを取得
        	List<String> itemNumberList = this.getItemNumberList(labelItemInfo);

            for(int i=0 ; i < itemNumberList.size() ; i++){
                // メニュー項目取得
                Map<String, Object> LabelItemInfoDetail = (Map<String, Object>)labelItemInfo.get(itemNumberList.get(i));

                if(Objects.isNull(LabelItemInfoDetail)){
                    //break;
                	continue;
                }

                int itemNum = Integer.valueOf(itemNumberList.get(i));

                // ラベルのIDからの取得
                String labelRourceId = (String)LabelItemInfoDetail.get(INFO_LABEL_RESOUCE_ID);
                String labelBeanId = (String)LabelItemInfoDetail.get(INFO_LABEL_BEAN_ID);
                if ( ( labelRourceId != null ) || ( labelBeanId != null )){
                    String strCommonNumber = "";
                    String strLabel = "";
                    // ラベルIDからの出力文字列取得
                    if ( labelRourceId != null){
                        strLabel = this.getStringEscapeHtml(ResourcesHandler.getString(labelRourceId, userLocale));
                    }

                    // Beanからの出力文字列取得　※Beanからの取得が優先される。
                    if ( labelBeanId != null ){
                        // 設定値種別取得
                        String strValueType = (String)LabelItemInfoDetail.get(INFO_VALUE_TYPE);
                        if(StringUtil.isNullOrEmpty(strValueType)){
                            strValueType = "";
                        }
                        if( !StringUtil.isNullOrEmpty(labelBeanId)){
                            Object valueObj  = getData(clsBean, bean, labelBeanId);
                            switch(strValueType){
                            case TAG_TYPE_NUMBER:
                                //decimalPoint:小数点桁数
                                Integer decimalPoint = 0;
                                String labelDecimalPoint = (String)LabelItemInfoDetail.get(INFO_DECIMAL_POINT);
                                if ( labelDecimalPoint != null) {
                                    decimalPoint = (Integer) getData(clsBean, bean, labelDecimalPoint);
                                }
                                strLabel = this.getStringEscapeHtml(getStringNumber(dictId, labelBeanId, valueObj, decimalPoint));
                                strCommonNumber = CSS_SPACE + CSS_NUMBER;

                                //空白になる場合がある（FormBean値にnullを入れた場合）&nbspを入れておく
                                if(StringUtil.isNullOrEmpty(strLabel)){
                                	strLabel = CSS_HALF_SPACE;
                                }

                                break;
                            case TAG_TYPE_DATE:
                            case TAG_TYPE_ZONEDDATETIME:

                            	//日付フォーマット ※リソースIDより取得
                                String FormatDateTimeId = (String)LabelItemInfoDetail.get(INFO_FORMAT_DATETIME);
                                String strFormatDateTime = ResourcesHandler.getString(FormatDateTimeId, userLocale);
                                strLabel = this.getStringEscapeHtml(getStringDate(dictId, labelBeanId,valueObj, strFormatDateTime));

                                //空白になる場合がある（FormBean値にnullを入れた場合）&nbspを入れておく
                                if(StringUtil.isNullOrEmpty(strLabel)){
                                	strLabel = CSS_HALF_SPACE;
                                }

                                break;
                            default:
                            	strLabel = this.getStringEscapeHtml((String)valueObj);
                                if(StringUtil.isNullOrEmpty(strLabel)){
                                	strLabel = CSS_HALF_SPACE;
                                }
                                break;
                            }
                        }
                    }
                    String strAddBr = (String)LabelItemInfoDetail.get(INFO_ADD_BR);
                    if ( strBr.equals("") ){
                        if ( !StringUtil.isNullOrEmpty(strAddBr)){
                            strBr = CSS_BR;
                        }
                    }

                    String strStyleClass = (String)LabelItemInfoDetail.get(INFO_LABEL_STYLE_CLASS);
                    if(StringUtil.isNullOrEmpty(strStyleClass)){
                        strStyleClass = "";
                    }
                    String strAddClass = (String)LabelItemInfoDetail.get(INFO_ADD_CLASS);
                    if(StringUtil.isNullOrEmpty(strAddClass)){
                        strAddClass = "";
                    } else {
                        strAddClass = " " + strAddClass;
                    }

                    strInputTextName = (String)LabelItemInfoDetail.get(INFO_LABEL_NAME);
                    if(StringUtil.isNullOrEmpty(strInputTextName)){
                        strInputTextName = "";
                    } else {
                        strInputTextName = " name=\"" + strInputTextName + "\" ";
                    }

                    switch(itemNum){
                        case 1:  //左ラベル出力
                                if ( strStyleClass.equals("") ){
                                	strStyleClass = CSS_TITLE;
                                }
                                out.print("  <div class=\"" + strStyleClass +  strAddClass + "\"" + strInputTextName + ">" + strLabel + "</div>" + strBr);
                                strBr = "";
                                break;
                        case 2:  //中央ラベル出力
                                if ( strStyleClass.equals("") ){
                                	strStyleClass = CSS_DATA;
                                }
                                strDataStyleClass = strStyleClass;
                                strDataAddClass = strAddClass;
                                strDataCommonNumber = strCommonNumber;
                                strDataLabel = strLabel;
                                strWrapClass = CSS_WRAP;
                                dataOutFlag = true;
                                break;
                        case 3:  // 単位出力
                                if ( !strLabel.equals("") ){
                                	strDataLabel = CSS_SPAN + strDataLabel + CSS_SPAN_END + CSS_HALF_SPACE
                                			+ CSS_SPAN + strLabel + CSS_SPAN_END;
                                }
                                break;
                    }
                }
            }
            /** データの出力があった場合*/
            if (dataOutFlag){
                out.print("<div class=\"" + strDataStyleClass +  strDataAddClass + strDataCommonNumber + strWrapClass + "\"" + strInputTextName + ">" +
                    strDataLabel + "</div>" + strBr + "\n");
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
     * Mapのキー（item_number）を取得
     * @param labelItemInfo
     * @return
     */
	private List<String> getItemNumberList(Map<String, Object> labelItemInfo) {
		List<String> list = new ArrayList<String>();

		// MapのKey一覧を取得
		for (String key : labelItemInfo.keySet()) {
			list.add(key);
		}

		return list;
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