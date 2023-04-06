package com.gnomes.common.tags;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.servlet.jsp.JspException;

import org.picketbox.util.StringUtil;

import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.view.BaseFormBean;
import com.gnomes.common.view.IScreenFormBean;
import com.gnomes.system.data.PartsPrivilegeResultInfo;

/**
 * テーブルカスタムタグ 共通処理
 *
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2020/01/28 YJP/Nweniwah              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class GnomesCTagButtonCommon extends GnomesCTagBasePrivilege {

    @Inject
    transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected LogHelper logHelper;

    /** 辞書：ボタンID */
    protected static final String INFO_BUTTON_ID = "button_id";

    /** 辞書：ボタンリソースID */
    protected static final String INFO_BUTTON_RESOUCE_ID = "button_resource_id";

    /** 辞書：OKボタンのリソースID */
    protected static final String INFO_OK_RESOURCE_ID = "ok_resource_id";

    /** 辞書：コマンドID */
    protected static final String INFO_COMMAND_ID = "command_id";

    /** 辞書：BeanからコマンドIDを取得 */
    protected static final String INFO_BEAN_COMMAND_ID = "bean_command_id";

    /** 辞書：オンクリック */
    protected static final String INFO_ON_CLICK = "onclick";

    /** コマンド実行フォーマット */
    protected static final String COMMAND_SCRIPT_FORMAT = "commandSubmit(\'%s\');";

    /** コマンド実行フォーマット(パラメータ使用) */
    protected static final String COMMAND_SCRIPT_FORMAT_PARAM = "commandSubmit(\\'%s\\');";

    /**
     * ボタンのonclickを取得
     * @param bean ビーン
     * @param buttonDicId ボタン辞書Id
     * @return onclick情報
     * @throws Exception
     */
    public String getOnclickButtonAttribute(BaseFormBean bean,String buttonDicId) throws Exception {

    	// テーブル辞書情報取得
        Locale userLocale = ((IScreenFormBean) bean).getUserLocale();

        GnomesCTagDictionary dict = getCTagDictionary();
        // ボタン辞書取得
        Map<String,Object> mapInfo = dict.getButtonInfo(buttonDicId);

        // パーツ権限情報Listを取得
        List<PartsPrivilegeResultInfo> stmPartsPrivilegeResultInfo = getPartsPrivilegeResultInfoList(bean);

        // 出力元データのクラス
        Class<?> clsBean = bean.getClass();

        String strLabel = "";
        // ボタンリソースID取得
        String labelRourceId = (String)mapInfo.get(INFO_BUTTON_RESOUCE_ID);
        if (!StringUtil.isNullOrEmpty(labelRourceId)) {
            strLabel = this.getStringEscapeHtml(ResourcesHandler.getString(labelRourceId, userLocale));
        }

        // OKボタンのリソースID取得
        String okResourceLabel = "";
        String okResourceId = (String)mapInfo.get(INFO_OK_RESOURCE_ID);
        if (!StringUtil.isNullOrEmpty(okResourceId)) {
            okResourceLabel = ResourcesHandler.getString(okResourceId, userLocale);
        }

        // onclick
        String onclick = "";
        String okScript = "";
        // コマンドID取得
        if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_COMMAND_ID))){
            onclick = String.format(COMMAND_SCRIPT_FORMAT, (String)mapInfo.get(INFO_COMMAND_ID));
            okScript = String.format(COMMAND_SCRIPT_FORMAT_PARAM, (String)mapInfo.get(INFO_COMMAND_ID));
        }
        // BeanからコマンドID取得
        else if(!StringUtil.isNullOrEmpty((String)mapInfo.get(INFO_BEAN_COMMAND_ID))){
            Object valueObj = this.getData(clsBean, bean, (String)mapInfo.get(INFO_BEAN_COMMAND_ID));
            if (valueObj != null) {
                onclick = String.format(COMMAND_SCRIPT_FORMAT, String.valueOf(valueObj));
                okScript = String.format(COMMAND_SCRIPT_FORMAT_PARAM, String.valueOf(valueObj));
            }
        }
        // onclick取得
        else if((String)mapInfo.get(INFO_ON_CLICK) != null){
            onclick = (String)mapInfo.get(INFO_ON_CLICK);
        }

        // ボタンID取得
        String buttonId = (String)mapInfo.get(INFO_BUTTON_ID);

        PartsPrivilegeResultInfo partsPrivilegeResultInfo = null;

        if (!StringUtil.isNullOrEmpty(buttonId)) {
             // パーツ権限を取得
             partsPrivilegeResultInfo = getPartsPrivilegeResultInfo(buttonId, stmPartsPrivilegeResultInfo);
             // 権限からボタンのhtml属性取得
             Map<String, String> mapAttribute = getButtonAttribute(partsPrivilegeResultInfo, strLabel, okResourceLabel, okScript, (String)mapInfo.get(INFO_ON_CLICK) , userLocale);

             // onclickスクリプト
             onclick = mapAttribute.get(INFO_ON_CLICK);
             if (onclick != null) {
                 return onclick;
            }
         }
        return null;
    }

	@Override
	public int doStartTag() throws JspException {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	@Override
	public int doEndTag() throws JspException {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	@Override
	public void release() {
		// TODO 自動生成されたメソッド・スタブ

	}
}