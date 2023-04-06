package com.gnomes.common.tags;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonEnums.DisplayDivision;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.view.IScreenFormBean;
import com.gnomes.system.data.PartsPrivilegeResultInfo;

/**
 * バーコード入力 カスタムタグ
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/02/06 YJP/I.Shibasaka           初版
 * R0.01.02 2018/11/29 YJP/A.Oomori              バーコード入力欄を右側に隠さず出力
 *                                               ボタンコマンドID、BeanコマンドID、onclick、バーコード入力IDの廃止
 *                                               左側ボタンは右側入力欄にフォーカスを当てるだけのボタンとする。
 * R0.01.03 2018/12/13 YJP/A.Oomori              入力欄ボタンIDを追加。Enterキーでのコマンド実行権限チェック時に使用する。
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesCTagInputBarCode extends GnomesCTagBasePrivilege {

	/** 辞書:ボタン名 */
	private static final String INFO_BUTTON_NAME = "button_name";

	/** 辞書:ボタンリソースID */
	private static final String INFO_BUTTON_RESOURCE_ID = "button_resource_id";

//	/** 辞書:ボタンコマンドID */
//	private static final String INFO_BUTTON_COMMAND_ID = "button_command_id";

	/** 辞書:バーコード入力追加スタイル */
    private static final String INFO_BUTTON_ADD_STYLE = "button_add_style";

//    /** 辞書：BeanからコマンドIDを取得 */
//    private static final String INFO_BEAN_COMMAND_ID = "bean_command_id";
//
//    /** 辞書：onclick */
//    private static final String INFO_ONCLICK = "onclick";

    /** 辞書:バーコード入力name */
    private static final String INFO_INPUT_NAME = "input_name";

//	/** 辞書:バーコード入力id */
//    private static final String INFO_INPUT_ID = "input_id";

    /** 辞書:バーコード入力コマンドid */
    private static final String INFO_INPUT_ONKEY_COMMAND = "input_command_id";

    /** 辞書:Beanからバーコード入力コマンドidを取得 */
    private static final String INFO_BEAN_ONKEY_COMMAND = "input_bean_command_id";

	/** 辞書:バーコード入力初期値Bean */
    private static final String INFO_INPUT_VALUE = "value";

	/** 辞書:バーコード入力追加スタイル */
    private static final String INFO_INPUT_ADD_STYLE = "input_add_style";

    /** 辞書：入力欄ボタンID */
    private static final String INFO_INPUT_BUTTON_ID = "input_button_id";

    /** 辞書：入力値変更後の処理 スクリプト */
    private static final String INFO_ON_CHANGE_EVENT = "on_change_event";

    /** 辞書：入力値変更後の処理 コマンドID */
    private static final String INFO_ON_CHANGE_COMMAND_ID = "on_change_command_id";

    /** 辞書：入力値変更後の処理 コマンド */
    private static final String INFO_ON_CHANGE_BEAN_COMMAND_ID = "on_change_bean_command_id";

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
	 * バーコード入力
	 */
	@Override
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

            // バーコード入力辞書取得
            Map<String,Object> mapInfo = dict.getBarCodeInfo(this.dictId);

            // パーツ権限情報Listを取得
            List<PartsPrivilegeResultInfo> stmPartsPrivilegeResultInfo = getPartsPrivilegeResultInfoList(this.bean);

            // 出力元データのクラス
            Class<?> clsBean = bean.getClass();

            // ボタン名取得
            String buttonName = (String)mapInfo.get(INFO_BUTTON_NAME);
            if(StringUtil.isNullOrEmpty(buttonName)){
            	buttonName = "";
            }

            String strLabel = "";
            // ボタンリソースID取得
            String labelRourceId = (String)mapInfo.get(INFO_BUTTON_RESOURCE_ID);
            if (!StringUtil.isNullOrEmpty(labelRourceId)) {
                strLabel = this.getStringEscapeHtml(ResourcesHandler.getString(labelRourceId, userLocale));
            }

            // 左側ボタンのOnclick
            String onclick = "";
//            if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_BUTTON_COMMAND_ID))){
//            	onclick = "document.main." + (String)mapInfo.get(INFO_BUTTON_COMMAND_ID) + ".focus();";
//            }
//            // BeanからコマンドID取得
//            else if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_BEAN_COMMAND_ID))){
//            	Object valueObj = getData(clsBean, bean, (String)mapInfo.get(INFO_BEAN_COMMAND_ID));
//            	if (valueObj != null) {
//            		onclick = "document.main." + String.valueOf(valueObj) + ".focus();";
//                }
//            }
//            else if ((String)mapInfo.get(INFO_ONCLICK) != null) {
//            	// 何もしない
//            	onclick = (String)mapInfo.get(INFO_ONCLICK);
//            }

            // スタイルシート追加クラス取得
            String btnAddStyle = (String)mapInfo.get(INFO_BUTTON_ADD_STYLE);
            if(StringUtil.isNullOrEmpty(btnAddStyle)){
            	btnAddStyle = "";
            }

            // バーコード入力名取得
            String inputName = (String)mapInfo.get(INFO_INPUT_NAME);
            if(StringUtil.isNullOrEmpty(inputName)){
            	inputName = "";
            }

            // 左側ボタンのOnclickの設定…右側バーコード入力欄にフォーカスを当てる。
            onclick = "$('[name=" + inputName + "]').focus();$('[name=" + inputName + "]').select();";

            // バーコード入力ID取得
//            String inputId = (String)mapInfo.get(INFO_INPUT_ID);
//            if(StringUtil.isNullOrEmpty(inputId)){
//            	inputId = "";
//            }

            String value = "";
            // バーコード入力初期値
            if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_INPUT_VALUE))){

                String paramName = (String)mapInfo.get(INFO_INPUT_VALUE);
                Object valueObj = responseContext.getResponseFormBean(bean, paramName, inputName);
            	if (valueObj != null) {
            		value = String.valueOf(valueObj);
                }
            }

            String commandId = (String)mapInfo.get(INFO_INPUT_ONKEY_COMMAND);
            String okScript = "";
            String onKeyDown = "";
            // バーコード入力欄のコマンドを設定（入力欄にフォーカスが当たっている状態でEnterキーを押すとコマンドを実行する。）
            if(!StringUtil.isNullOrEmpty(commandId)){
            	onKeyDown = "if(window.event.keyCode == 13){ "  + String.format(COMMAND_SCRIPT_FORMAT, commandId) + "}";
            	okScript = String.format(COMMAND_SCRIPT_FORMAT_PARAM, String.valueOf(commandId));
            } else if (!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_BEAN_ONKEY_COMMAND))) {
            	commandId = (String) getData(clsBean, bean, (String)mapInfo.get(INFO_BEAN_ONKEY_COMMAND));
            	if (!StringUtil.isNullOrEmpty(commandId)) {
            		onKeyDown = "if(window.event.keyCode == 13){ " + String.format(COMMAND_SCRIPT_FORMAT, commandId) + "}";
            		okScript = String.format(COMMAND_SCRIPT_FORMAT_PARAM, String.valueOf(commandId));
                }
            }

            // スタイルシート追加クラス取得
            String addStyle = (String)mapInfo.get(INFO_INPUT_ADD_STYLE);
            if(StringUtil.isNullOrEmpty(addStyle)){
            	 addStyle = "";
            }

            // ボタンID取得
            String buttonId = (String)mapInfo.get(INFO_INPUT_BUTTON_ID);

            PartsPrivilegeResultInfo partsPrivilegeResultInfo = null;

            // 権限無しのClass名
            String nonePrivilegeClassName = "";

            if (!StringUtil.isNullOrEmpty(buttonId) && !StringUtil.isNullOrEmpty(okScript)) {
                // パーツ権限を取得
                partsPrivilegeResultInfo = getPartsPrivilegeResultInfo(buttonId, stmPartsPrivilegeResultInfo);
                String okResourceLabel = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0019, userLocale);

                // 権限からボタンのhtml属性取得
                Map<String, String> mapAttribute =
                		getButtonAttribute(partsPrivilegeResultInfo, strLabel, okResourceLabel, okScript, null, userLocale);

                // onclickスクリプト
                onKeyDown = "if(window.event.keyCode == 13){ "  + mapAttribute.get(MAP_KEY_ONCLICK) + "}";

                // 権限があるかをチェック
                if (partsPrivilegeResultInfo != null && !partsPrivilegeResultInfo.isPrivilege()) {
                    // 権限無しの場合、ボタン表示区分を取得
                    DisplayDivision displayDivision = partsPrivilegeResultInfo.getDisplayDiv();

                    if (displayDivision == DisplayDivision.DISABLE) {
                        // ボタン表示区分が非活性である場合
                        nonePrivilegeClassName = " common-bar-code-disabled";
                    }
                    else if (displayDivision == DisplayDivision.HIDE) {
                        // ボタン表示区分が非表示である場合
                        nonePrivilegeClassName = " common-bar-code-hide";
                    }
                    else {
                        nonePrivilegeClassName = "";
                    }
                }
            }

            // 入力値変更後の処理
            String onchange = getOnChange(mapInfo);

            if (!StringUtil.isNullOrEmpty(nonePrivilegeClassName)) {
                // 権限無しの場合、ボタン表示区分を取得
                // 左側ボタン
                out.print(
                        "<div name=\"" + buttonName + "\" class=\"common-button " + btnAddStyle + nonePrivilegeClassName + "\">" + strLabel + "</div>");
                // 右側
                out.print("<div class=\"common-header-col-data" + nonePrivilegeClassName + "\">");
                // バーコード入力
                out.print(
                        "  <input type=\"tel\" class=\"common-table-input-size-max " + addStyle + "\" name=\"" + inputName + "\" value=\"" + getStringEscapeHtmlValue(
                                value) + "\" readonly>");
                out.print("</div>");
            }
            else {
                // 権限ありの場合、または、ボタン表示区分が「エラーを表示（従来の動き）」である場合
                // 左側ボタン
                out.print(
                        "<div name=\"" + buttonName + "\" class=\"common-button common-header-col-title " + btnAddStyle + "\" onclick=\"" + onclick + "\">" + strLabel + "</div>");
                // 右側
                out.print("<div class=\"common-header-col-data\">");
                // バーコード入力
                out.print(
                        "  <input type=\"tel\" class=\"common-table-input-size-max common-input-barcode-style " + addStyle + "\" name=\"" + inputName + "\" value=\"" + getStringEscapeHtmlValue(
                                value) + "\" onKeyDown=\"" + onKeyDown + "\" onKeyPress=\"if(window.event.keyCode == 29) {window.event.srcElement.value=window.event.srcElement.value+'\\x1d';}\"" + onchange + ">");
                out.print("</div>");
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
     * onChange時の処理を生成する
     * @param mapColInfo 項目辞書
     * @return onChange処理
     */
    private String getOnChange(Map<String, Object> mapInfo) throws Exception {
        // 入力値変更後の処理
        // 出力元データのクラス
        Class<?> clsBean = bean.getClass();
        String onchange = "";
        if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_ON_CHANGE_EVENT))){
            onchange = " onchange=\"" + SET_WARMING_FLAG + (String)mapInfo.get(INFO_ON_CHANGE_EVENT) + "\"";
        }
        else if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_ON_CHANGE_COMMAND_ID))){
            onchange = " onchange=\"" + SET_WARMING_FLAG + String.format(COMMAND_SCRIPT_FORMAT, String.valueOf((String)mapInfo.get(INFO_ON_CHANGE_COMMAND_ID))) + "\"";

        }
        else if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_ON_CHANGE_BEAN_COMMAND_ID))){
            Object commandId = getData(clsBean, bean, (String)mapInfo.get(INFO_ON_CHANGE_BEAN_COMMAND_ID));
            if (commandId != null) {
                onchange = " onchange=\"" + SET_WARMING_FLAG + String.format(COMMAND_SCRIPT_FORMAT, String.valueOf(commandId)) + "\"";
            } else {
                onchange = " onchange=\"" + SET_WARMING_FLAG +  "\"";
            }
        } else {
             onchange = " onchange=\"" + SET_WARMING_FLAG +  "\"";
        }
        return onchange;
    }

    /**
     * 終了処理
     */
	@Override
	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}

    /**
     * 解放処理
     */
	@Override
	public void release() {
	}


}
