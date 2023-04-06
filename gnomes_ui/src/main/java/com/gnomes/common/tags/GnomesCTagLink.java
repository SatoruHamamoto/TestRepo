package com.gnomes.common.tags;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.picketbox.util.StringUtil;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.view.IScreenFormBean;
import com.gnomes.system.data.PartsPrivilegeResultInfo;

/**
 * リンク カスタムタグ
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
public class GnomesCTagLink extends GnomesCTagBasePrivilege {

    /** 辞書：リンク名 */
    private static final String INFO_LINK_NAME = "link_name";

    /** 辞書：リンクリソースID */
    private static final String INFO_LINK_RESOUCE_ID = "link_resource_id";

    /** 辞書：リンクURL */
    private static final String INFO_LINK_URL = "link_url";

    /** 辞書：コマンドID */
    private static final String INFO_COMMAND_ID = "command_id";

    /** 辞書：BeanからコマンドIDを取得 */
    private static final String INFO_BEAN_COMMAND_ID = "bean_command_id";

    /** 辞書：オンクリック */
    private static final String INFO_ONCLICK = "onclick";

    /** 辞書：別ウィンドウ表示 */
    private static final String INFO_OPEN_WINDOW = "open_window";

    /** 辞書：スタイルシート追加クラス */
    private static final String INFO_ADD_STYLE = "add_style";

    /** 辞書：ボタンID */
    private static final String INFO_BUTTON_ID = "button_id";

    /**  辞書：工程端末表示か否か　 */
    private static final String INFO_IS_PANECON = "is_panecon";

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
     * リンクタグ出力
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
            // リンク辞書取得
            Map<String,Object> linkInfo = dict.getLinkInfo(this.dictId);

            // パーツ権限情報Listを取得
            List<PartsPrivilegeResultInfo> stmPartsPrivilegeResultInfo = getPartsPrivilegeResultInfoList(this.bean);

            // 出力元データのクラス
            Class<?> clsBean = bean.getClass();

            // リンク名取得
            String linkName = (String)linkInfo.get(INFO_LINK_NAME);
            if(StringUtil.isNullOrEmpty(linkName)){
            	linkName = "";
            }

            String strLabel = "";
            String okResourceLabel = "";
            // リンクリソースID取得
            String labelRourceId = (String)linkInfo.get(INFO_LINK_RESOUCE_ID);
            if (!StringUtil.isNullOrEmpty(labelRourceId)) {
                strLabel = this.getStringEscapeHtml(ResourcesHandler.getString(labelRourceId, userLocale));
                okResourceLabel = strLabel;
            }

            // 別ウィンドウ表示取得
            String openW0indow = (String)linkInfo.get(INFO_OPEN_WINDOW);

            // onclcik
            String onclick = "";
            String okScript = "";
            String linkUrl = "";
            // コマンドID取得
            if(!StringUtil.isNullOrEmpty((String)linkInfo.get(INFO_COMMAND_ID))){
            	String commandId = (String)linkInfo.get(INFO_COMMAND_ID);
            	if(!StringUtil.isNullOrEmpty(openW0indow)){
            		// 別タブ表示
            		onclick = "openWindow(\'" + commandId + "\');";
            		okScript = String.format(COMMAND_SCRIPT_FORMAT_PARAM, commandId);
            	}
            	else {
            		onclick = String.format(COMMAND_SCRIPT_FORMAT, commandId);
            		okScript = String.format(COMMAND_SCRIPT_FORMAT_PARAM, commandId);
            	}
            }
            // BeanからコマンドID取得
            else if(!StringUtil.isNullOrEmpty((String)linkInfo.get(INFO_BEAN_COMMAND_ID))){
            	Object valueObj = getData(clsBean, bean, (String)linkInfo.get(INFO_BEAN_COMMAND_ID));
            	if (valueObj != null) {
            		if(!StringUtil.isNullOrEmpty(openW0indow)){
            			// 別タブ表示
                		onclick = "openWindow(\'" + String.valueOf(valueObj) + "\');";
                		okScript = String.format(COMMAND_SCRIPT_FORMAT_PARAM, String.valueOf(valueObj));
                	}
                	else {
                		onclick = String.format(COMMAND_SCRIPT_FORMAT, String.valueOf(valueObj));
                		okScript = String.format(COMMAND_SCRIPT_FORMAT_PARAM, String.valueOf(valueObj));
                	}

                }
            }
            // onclick取得
            else if(!StringUtil.isNullOrEmpty((String)linkInfo.get(INFO_ONCLICK))){
            	onclick = (String)linkInfo.get(INFO_ONCLICK);

            }
            // リンクURL取得
            else if(!StringUtil.isNullOrEmpty((String)linkInfo.get(INFO_LINK_URL))){

            	linkUrl = (String)linkInfo.get(INFO_LINK_URL);

            }

            // スタイルシート追加クラス取得
            String addStyle = (String)linkInfo.get(INFO_ADD_STYLE);
            if(StringUtil.isNullOrEmpty(addStyle)){
            	 addStyle = "";
            }

            // ボタンID取得
            String buttonId = (String)linkInfo.get(INFO_BUTTON_ID);

            //R1.02追加（接続領域ごと操作可否で領域が違うと非表示になる)
            String disabledClass = judgeAndSetDisabledClass(linkInfo);

            PartsPrivilegeResultInfo partsPrivilegeResultInfo = null;

            if (!StringUtil.isNullOrEmpty(buttonId)) {
                // パーツ権限を取得
                partsPrivilegeResultInfo = getPartsPrivilegeResultInfo(buttonId, stmPartsPrivilegeResultInfo);
                // 権限からボタンのhtml属性取得
                Map<String, String> mapAttribute =
                		getButtonAttribute(partsPrivilegeResultInfo, strLabel, okResourceLabel, okScript, (String)linkInfo.get(INFO_ONCLICK), userLocale);

                // onclickスクリプト
                onclick = mapAttribute.get(MAP_KEY_ONCLICK);

                // disableが空でない（権限無し）場合
                if(!StringUtil.isNullOrEmpty(mapAttribute.get(MAP_KEY_DISABLE))){
                	// 非活性表示クラス＊業務側の処理によりで対応すること
                	// disabledClass = " common-disabled";
                }

            }

         // 工程端末か否か（デフォルトは管理端末表示）
            boolean isPanecon = false;
            if (!StringUtil.isNullOrEmpty((String)linkInfo.get(INFO_IS_PANECON))) {
                isPanecon = true;
            }

            // tabindex取得
            String tabindex = "";
            if(isPanecon){
            	tabindex = "-1";
            }

            // リンクURL出力
            if (!StringUtil.isNullOrEmpty(linkUrl)) {
            	out.print("<div name=\"" + linkName + "\"  class=\"common-link-url " + disabledClass + " "  + addStyle + "\"tabindex=\"" + tabindex + "\" onclick=\"openURL('" + linkUrl + "');\">" + strLabel + "</div>\n");
            }
            // onclick出力
            else if (!StringUtil.isNullOrEmpty(onclick)) {
                out.print("<div name=\"" + linkName + "\"  class=\"common-link " + disabledClass + " " + addStyle + "\"tabindex=\"" + tabindex + "\" onclick=\"" + onclick + "\">" + strLabel + "</div>\n");
            }

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