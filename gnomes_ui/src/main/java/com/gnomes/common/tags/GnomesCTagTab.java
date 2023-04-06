package com.gnomes.common.tags;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.picketbox.util.StringUtil;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.GnomesLogMessageConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.view.IScreenFormBean;

/**
 * タブ カスタムタグ
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/01/17 YJP/	I.Shibasaka          初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesCTagTab extends GnomesCTagBase {

    /** 辞書：タブ名 */
    private static final String INFO_TAB_NAME = "tab_name";

    /** 辞書：タブID */
    private static final String INFO_TAB_ID = "tab_id";

    /** 辞書：タブフォームID */
    private static final String INFO_TAB_FORM_ID = "tab_form_id";

    /** 辞書：タブ値 */
    private static final String INFO_TAB_VALUE = "tab_value";

    /** 辞書：スタイルシート追加クラス */
    private static final String INFO_ADD_STYLE = "add_style";

    /** 辞書：タブリソースID */
    private static final String INFO_TAB_LABEL_RESOUCE_ID = "tab_label_resource_id";

    /** 辞書：タブ選択状態 */
    private static final String INFO_TAB_CHECKED = "tab_checked";

    /** FormBeanタブ選択状態 */
    private static final String FORMBEAN_TAB_CHECKED = "tab";

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
     * 数値タグ出力
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
            Map<String,Object> mapInfo = dict.getTabInfo(this.dictId);

            // 出力元データのクラス
            Class<?> clsBean = bean.getClass();

            // タブ名
            String tabName = (String)mapInfo.get(INFO_TAB_NAME);
            if (StringUtil.isNullOrEmpty(tabName)) {
            	tabName = "";
            }

            // タブID
            String tabId = (String)mapInfo.get(INFO_TAB_ID);
            if (StringUtil.isNullOrEmpty(tabId)) {
            	tabId = "";
            }

            // タブ値
            String tabValue = this.getStringEscapeHtmlValue((String)mapInfo.get(INFO_TAB_VALUE));
            if (StringUtil.isNullOrEmpty(tabValue)) {
            	tabValue = "";
            }

            // タブFormID
            String tabFormId = (String)mapInfo.get(INFO_TAB_FORM_ID);
            if (StringUtil.isNullOrEmpty(tabFormId)) {
            	tabFormId = "";
            }

            // 追加クラス
            String addStyle = (String)mapInfo.get(INFO_ADD_STYLE);
            if (StringUtil.isNullOrEmpty(addStyle)) {
            	addStyle = "";
            }

            // ラベルをリソースから取得
            String label = "";
            String labelResouce = (String)mapInfo.get(INFO_TAB_LABEL_RESOUCE_ID);
            if (!StringUtil.isNullOrEmpty(labelResouce)) {
            	label = this.getStringEscapeHtmlValue(ResourcesHandler.getString(labelResouce, userLocale));
            }
            String tabChecked = null;

            // FormBeanにタブ選択値が設定されている場合、こちらの選択状態を優先
            //タブが複数ある場合にタブName_tabという変数名を探して値をとる。
            //取得できればそれがタブcheckedになる(タブName="tab1tab"という変数名が
            //FormBeanに定義されていることが前提(互換性を保つため必ず最後に"tab"を含めておくこと）
            tabChecked = searchAndGetTabValue(clsBean,bean,tabName,FORMBEAN_TAB_CHECKED);

            if(!StringUtil.isNullOrEmpty(tabChecked)){
            	// タブ選択値とタブ値が一致する場合
            	if (tabValue.equals(tabChecked)) {
                	tabChecked = "checked";
                }
            	else {
            		tabChecked = "";
                }
            }
            else {
            	// タグ定義からタブ選択状態を取得
            	tabChecked = (String)mapInfo.get(INFO_TAB_CHECKED);
            	if (StringUtil.isNullOrEmpty(tabChecked)) {
                	tabChecked = "";
                }
            	else {
                	if (!tabChecked.equals("checked")) {
                		tabChecked = "";
                	}
                }
            }

            out.print("<input type=\"radio\" id=\"" + tabId + "\" name=\"" + tabName + "\" value=\"" + tabValue 
                    + "\" " + tabChecked + " onclick = \"FixedMidashi.create(); setDblClickEvent();\"" + " >");
            out.print("<label for=\"" + tabFormId + "\" class=\"" + addStyle + "\">"+ label + "</label>\n");

        } catch(Exception e) {
            if (out != null) {
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
     * 引数のプロパティ名を検索し、最初に見つけたプロパティの値をとる
     * （複数のプロパティはいづれか１つしか存在しないことが条件）
     * さもないと、最初に見つけたプロパティの値が取れて、どのプロパティの値なのかわからくなる
     * @param clsBean   探すBeanのクラス
     * @param bean      探すBeanのオブジェクト
     * @param propNames 探すプロパティ名の可変引数
     *
     * @return 取れたプロパティの値。何も取れなかったらnull
     */
    private String searchAndGetTabValue(Class<?> clsBean,Object bean,String... propNames) throws Exception
    {
        String tabChecked = null;

        BeanInfo bi;
        try {
            bi = Introspector.getBeanInfo(clsBean);
            PropertyDescriptor props[] = bi.getPropertyDescriptors();
            for(PropertyDescriptor prop : props){
                for(String propName : propNames){
                    if(prop.getName().compareTo(propName)==0){
                        Method nameGetter = prop.getReadMethod();
                        tabChecked =  (String)nameGetter.invoke(bean, (Object[]) null);
                        return tabChecked;
                    }
                }
            }
        }
         catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            // クラス[{0}]のフィールド名[{1}]より値取得に失敗しました
            String mes = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0006, clsBean.getName(), propNames);
            throw new Exception(mes,e);
        } catch (IntrospectionException e) {
            // cのクラスにnameのフィールドが存在しません
            String mes = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0005, clsBean.getName(), propNames);
            throw new Exception(mes,e);
        }

        return tabChecked;
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