package com.gnomes.common.tags;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.picketbox.util.StringUtil;

import com.gnomes.common.data.MenuItemInfoBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.view.IScreenFormBean;
import com.gnomes.system.data.PartsPrivilegeResultInfo;

/**
 * メニュー（右ペイン） カスタムタグ
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/01/17 YJP/	A.Oomori               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesCTagMenu extends GnomesCTagBaseMenu {

    /** 辞書：メニュータイトルリソースID */
    private static final String INFO_MENU_TITLE_RESOURCE_ID = "menu_title_resource_id";

    /** 辞書：スタイルシート追加クラス */
    private static final String INFO_TITLE_ADD_STYLE = "title_add_style";

    /** 辞書：メニューボックススタイルクラス追加 */
    private static final String INFO_MENU_BOX_ADD_CLASS = "menu_box_add_style";

    /** 辞書：メニュー項目定義Bean */
    private static final String INFO_MENU_ITEM_LIST_BEAN = "menu_item_list_bean";

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
     * メニュータグ出力
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
            // メニュー辞書取得
            Map<String,Object> menuInfo = dict.getMenuInfo(this.dictId);
            // メニュー項目辞書取得
            Map<String,Object> menuItemInfo = dict.getMenuItemInfo(this.dictId);


            // パーツ権限情報Listを取得
            List<PartsPrivilegeResultInfo> stmPartsPrivilegeResultInfo = getPartsPrivilegeResultInfoList(this.bean);


            // 出力元データのクラス
            Class<?> clsBean = bean.getClass();


            String strLabel = "";
            // メニュータイトルリソースID取得
            String labelRourceId = (String)menuInfo.get(INFO_MENU_TITLE_RESOURCE_ID);
            if (labelRourceId != null) {
                strLabel = this.getStringEscapeHtml(ResourcesHandler.getString(labelRourceId, userLocale));
            }

            // メニュー項目定義Bean取得
            String menuItemListBeanName = (String)menuInfo.get(INFO_MENU_ITEM_LIST_BEAN);

            // タイトルスタイルシート追加クラス取得
            String addStyle = (String)menuInfo.get(INFO_TITLE_ADD_STYLE);
            if(StringUtil.isNullOrEmpty(addStyle)){
            	 addStyle = "";
            }

            // メニューボックススタイルシート追加クラス取得
            String menuBoxAddStyle = (String)menuInfo.get(INFO_MENU_BOX_ADD_CLASS);
            if(StringUtil.isNullOrEmpty(menuBoxAddStyle)){
            	menuBoxAddStyle = "";
           }
            out.print("  <div class=\"common-flexMenu-function-header clearfix\">\n");
            out.print("    <span class=\"common-flexMenu-function-header-title "+ addStyle + "\">" + strLabel+ "\n");
            out.print("      <span class=\"common-flexMenu-function-header-icon\"><img alt=\"\" src=\"./images/gnomes/icons/icon-arrow-7.png\"></span>\n");
            out.print("    </span>\n");
            out.print("  </div>\n");
            out.print("  <div class=\"common-flexMenu-function-box common-flexMenu-size "+ menuBoxAddStyle + "\">\n");

            out.print("    <ul>\n");

            // 辞書にメニュー項目が定義されている場合
            if(menuItemInfo != null){
            	outMenuItem(out, menuItemInfo, stmPartsPrivilegeResultInfo, userLocale, bean, dictId);
            }

            // メニュー項目Beanが指定されている場合、取得したメニュー項目を追加表示
            if(!StringUtil.isNullOrEmpty(menuItemListBeanName)){

            	// 出力元データのクラス
            	List<MenuItemInfoBean> menuItemInfoBeanList = (List<MenuItemInfoBean>)getData(clsBean, bean, menuItemListBeanName);

                // メニュー項目定義Beanを取得した場合
                if (menuItemInfoBeanList != null && menuItemInfoBeanList.size() > 0) {
                	// メニュー項目出力内容を定義Beanの情報を元に追加表示
                	outMenuItemFormBean(out, menuItemInfoBeanList, stmPartsPrivilegeResultInfo, userLocale);
                }

            }



            out.print("    </ul>\n");
            out.print("  </div>\n");


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