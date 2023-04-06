package com.gnomes.common.tags;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonEnums;
import com.gnomes.common.constants.CommonEnums.TagHiddenKind;
import com.gnomes.common.constants.StringSanitizeConstants;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.data.GnomesSystemBean;
import com.gnomes.common.data.MenuItemInfoBean;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesLogMessageConstants;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.view.BaseFormBean;
import com.gnomes.system.data.IScreenPrivilegeBean;
import com.gnomes.system.data.PartsPrivilegeResultInfo;
import com.gnomes.uiservice.ContainerRequest;
import com.gnomes.uiservice.ContainerResponse;

/**
 * カスタムタグ 基底クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/1/18 YJP/A.Oomori               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public abstract class GnomesCTagBase extends GnomesCTagBaseGetData implements Tag {

    /** ログヘルパー */
    @Inject
    protected LogHelper logHelper;

    @Inject
    protected transient Logger logger;

    @Inject
    protected
    ContainerRequest requestContext;

    @Inject
    protected
    ContainerResponse responseContext;

    @Inject
    protected
    GnomesCTagDictionary gnomesCTagDictionary;

    @Inject
    protected
    GnomesSessionBean gnomesSessionBean;

    @Inject
    protected
    GnomesSystemBean gnomesSystemBean;

    @Inject
    protected
    GnomesExceptionFactory exceptionFactory;

//    /** タグ出力でエラー時の出力内容 */
//    protected String TAG_ERR_MES = "<label class=\"TABLE_LABEL\" type=\"text\" >" + ResourcesHandler.getString(GnomesResourcesConstants.DI01_0134, gnomesSessionBean.getUserLocale()) + "</label>";

    /** Bean={bean} の定義誤りのエラーの出力内容 */
    protected static final String NO_BEAN_ERR_MES = "The specified bean name is not defined or misspelled.";

    /** 辞書：タグ名 */
    protected static final String INFO_TAG_NAME = "name";

    /** タグタイプ：テキスト */
    public static final String TAG_TYPE_TEXT = "text";

    /** タグタイプ：数値 */
    public static final String TAG_TYPE_NUMBER = "number";

    /** タグタイプ：日付 */
    public static final String TAG_TYPE_DATE = "date";

    /** タグタイプ　プログレスバー */
    public static final String TAG_TYPE_PROGRESS = "progress";


    /** タグタイプ：タイムゾーン付きの日付 */
    public static final String TAG_TYPE_ZONEDDATETIME = "zonedDateTime";

    /** コマンド実行フォーマット */
    protected static final String COMMAND_SCRIPT_FORMAT = "commandSubmit(\'%s\');";

    /** コマンド実行フォーマット(パラメータ使用) */
    protected static final String COMMAND_SCRIPT_FORMAT_PARAM = "commandSubmit(\\'%s\\');";

    /** コマンドセットフォーマット */
    protected static final String COMMAND_SET_FORMAT = "document.main.command.value=\'%s\';";

    /** OKスクリプトフォーマット */
    protected static final String OK_SCRIPT_FORMAT = "document.main.command.value=\\'%s\\';document.main.submit();";

    /** 警告チェック設定 */
    protected static final String SET_WARMING_FLAG = "setWarningFlag();";

    /** 入力非活性：無効 */
    protected static final String INPUT_DISABLED = "disabled";

    /** 入力非活性：読み出し専用 */
    protected static final String INPUT_READONLY = "readonly";

    /** プルダウン候補名 */
    protected static final String INFO_PULLDOWN_NAME = "name";

    /** プルダウン候補値 */
    protected static final String INFO_PULLDOWN_VALUE = "value";

    protected PageContext pageContext;
    protected Tag parentTag;

	/** 日付フォーマット置換文字 */
    private static final String[][] DATE_PATTERN_REPLACE =
        {
                //変換前→変換後
                { "y",  "Y" },
                { "d",  "D" }
        };

    /** 非入力データ置換文字 */
    private static final String[][] NOT_INPUT_VALUE_REPLACE =
        {
                //変換前→変換後
                { " ",  "&nbsp;" },
                { "\r\n",  "<br>" },
                { "\r",  "<br>" },
                { "\n",  "<br>" }
        };

    /** コンボ オートコンプリート クラス名 */
    protected static final String CLASS_AUTO_COMBO = "gnomes-auto-combo";

    /** オートコンプリートモード: value表示 */
    protected static final String AUTO_MODE_VALUE = "1";

    /** オートコンプリートモード: text表示 */
    protected static final String AUTO_MODE_TEXT = "2";

    /** オートコンプリートモード: value + text表示 */
    protected static final String AUTO_MODE_VALUE_TEXT = "3";

    /** データタイプ：数値 */
    public static final int PARAM_DATA_TYPE_DIV_NUMBER = 1;

    /** データタイプ：文字列 */
    public static final int PARAM_DATA_TYPE_DIV_STRING = 2;

    /** データタイプ：二値 */
    public static final int PARAM_DATA_TYPE_DIV_PULLDOWN = 3;

    /** データタイプ：二値 (先頭空白無し) */
    public static final int PARAM_DATA_TYPE_DIV_PULLDOWN_NO_SPACE = 8;

    /** データタイプ：年月日時分秒 */
    public static final int PARAM_DATA_TYPE_DIV_YYYYMMDDHHMmmss = 4;

    /** データタイプ：年月日 */
    public static final int PARAM_DATA_TYPE_DIV_YYYYMMDD = 5;

    /** データタイプ：時分秒 */
    public static final int PARAM_DATA_TYPE_DIV_DATE_HHmmss = 6;

    /** データタイプ：時分 */
    public static final int PARAM_DATA_TYPE_DIV_DATE_HHmm = 7;

    /** データタイプ：年月日時分 */
    public static final int PARAM_DATA_TYPE_DIV_YYYYMMDDHHMmm = 9;

    /** データタイプ：年月 */
    public static final int PARAM_DATA_TYPE_DIV_YYYYMM = 10;

    /** データタイプ・フォーマット マッピング */
    private static final Map<Integer, String> dataTypeFormatMap = new HashMap<>();
    static {
        /** データタイプフォーマット：年月日時分秒 */
        dataTypeFormatMap.put(PARAM_DATA_TYPE_DIV_YYYYMMDDHHMmmss, GnomesResourcesConstants.YY01_0001);
        /** データタイプフォーマット：年月日 */
        dataTypeFormatMap.put(PARAM_DATA_TYPE_DIV_YYYYMMDD, GnomesResourcesConstants.YY01_0003);
        /** データタイプフォーマット：時分秒 */
        dataTypeFormatMap.put(PARAM_DATA_TYPE_DIV_DATE_HHmmss, GnomesResourcesConstants.YY01_0072);
        /** データタイプフォーマット：時分 */
        dataTypeFormatMap.put(PARAM_DATA_TYPE_DIV_DATE_HHmm, GnomesResourcesConstants.YY01_0069);
        /** データタイプフォーマット：年月日時分 */
        dataTypeFormatMap.put(PARAM_DATA_TYPE_DIV_YYYYMMDDHHMmm, GnomesResourcesConstants.YY01_0002);
        /** データタイプフォーマット：年月 */
        dataTypeFormatMap.put(PARAM_DATA_TYPE_DIV_YYYYMM, GnomesResourcesConstants.YY01_0094);
    }

    /** 工程端末 */
    protected static final String IS_PANECON = "MG";
    /** エラー時出力HTML：label */
    protected static final String ERROR_LABEL_HTML = "<label style=\"position: absolute; left: 0px; top: 0px; width: 100vw; height: 100vw; background-color: white;\">";

    /** エラー時出力HTML：onclick */
    protected static final String ERROR_ONCLICK_HTML = "<br><u><a onclick=\"document.main.command.value='";

    /** エラー時出力HTML：submit */
    protected static final String ERROR_SUBMIT_HTML = "';document.main.submit();\">";

    /** エラー時出力HTML：/a */
    protected static final String ERROR_ANCHOR_END_HTML = "</a></u>";

    /** エラー時出力HTML：/label */
    protected static final String ERROR_LABEL_END_HTML = "</label>";

    /** 接続領域ごと操作可否 **/
    protected static final String TAGNAME_OPERATION_CONNECTION_AREA = "operation_connection_area";

    /** 接続領域ごと操作可否タグ値 **/
    protected static enum OperationConnectionAreaTagval {
    	AREA_ALL("0"),			//0:実行保管領域区別なし全部
    	AREA_NORMAL("1"),		//1:実行領域接続時のみ操作可能
    	AREA_STORAGE("2");		//2:保管領域接続時のみ操作可能
        private final String text;

        private OperationConnectionAreaTagval(final String text) {
          this.text = text;
        }

        public String getValue() {
            return this.text;
        }

        /**
         * 設定された値を返す
         */
        @Override
        public String toString() {
          return this.text;
        }
    }
    /** 接続領域ごと操作可否ENM値 **/
    protected static enum OperationConnectionArea
    {
    	AREA_ALL,			//0:実行保管領域区別なし全部
    	AREA_NORMAL,		//1:実行領域接続時のみ操作可能
    	AREA_STORAGE;		//2:保管領域接続時のみ操作可能
    }

    public void setPageContext(PageContext pageContext) {
        this.pageContext = pageContext;
    }

    public void setParent(Tag parentTag) {
        this.parentTag = parentTag;
    }

    public Tag getParent() {
        return this.parentTag;
    }

    /**
     * 辞書取得
     * @return 辞書
     */
    protected GnomesCTagDictionary getCTagDictionary() {

        return this.gnomesCTagDictionary;

    }

    /**
     * Beanの値取得
     * @param c Beanクラス
     * @param object Bean
     * @param name フィールド名
     * @return 取得値
     * @throws Exception
     */
    protected Object getData(Class<?> c, Object object ,String name) throws Exception {
        Object ret = null;

        try {
            ret = getObjectData(c, object, name);

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            // クラス[{0}]のフィールド名[{1}]より値取得に失敗しました
            String mes = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0006, c.getName(), name);
            throw new Exception(mes,e);
        } catch (IntrospectionException e) {
            // cのクラスにnameのフィールドが存在しません
            String mes = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0005, c.getName(), name);
            throw new Exception(mes,e);
        }
        return ret;

    }

    /**
     * Objectのフィールドから値取得
     *
     * @param object 取得元beanObject
     * @param name 取得フィールド名
     * @return 取得値
     * @throws Exception 例外
     */
    protected Object getDataFromField(Object object, String name) throws Exception {

        Object ret = null;
        try {
            ret =  getObjectDataFromField(object, name);
        } catch (IllegalAccessException | IllegalArgumentException | SecurityException e) {
            // クラス[{0}]のフィールド名[{1}]より値取得に失敗しました
            String mes = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0006, object.getClass().getName(), name);
            throw new Exception(mes,e);
        } catch (NoSuchFieldException e) {
            // cのクラスにnameのフィールドが存在しません
            String mes = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0005, object.getClass().getName(), name);
            throw new Exception(mes,e);
        }
        return ret;
    }



    /**
     * メソッド名から実行
     * @param object 実行クラスのオブジェクト
     * @param name 実行メソッド名
     * @return 実行戻り値
     * @throws Exception 例外
     */
    protected Object getDataFromMethod(Object object ,String name) throws Exception {
        Object ret = null;

        Method method;
        try {
            method = object.getClass().getMethod(name, new Class[0]);
            ret = method.invoke(object, (Object[]) null);
        } catch (Exception e) {
            responseContext.setMessageInfo(GnomesMessagesConstants.MG02_0006, object.getClass().getName(), name);
        }


        return ret;
    }

    /**
     * Methodをあるかどうかチェック
     * @param object 実行クラスのオブジェクト
     * @param name 実行メソッド名
     * @return 実行戻り値
     * @throws Exception 例外
     */
    protected Boolean checkMethodExitOrNot(Object object ,String name) throws Exception {

        Method[] methods;
        Boolean checkFlag = false;
        try {
        	methods = object.getClass().getMethods();
            for (Method method : methods) {
                if (method.getName().equals(name)) {
                	checkFlag = true;
	            }
	        }
        } catch (Exception e) {
        	responseContext.setMessageInfo(GnomesMessagesConstants.MG02_0006, object.getClass().getName(), name);
        }
        return checkFlag;
    }


    /**
     * htmlエスケープValue
     * @param inStr 対象文字列
     * @return 変換後文字列
     * @throws Exception
     */
    protected String getStringEscapeHtmlValue(String inStr) throws Exception {
        return getStringEscapeHtml(inStr, true);
    }

    /**
     * htmlエスケープ
     * @param inStr 対象文字列
     * @return 変換後文字列
     * @throws Exception
     */
    protected String getStringEscapeHtml(String inStr) throws Exception {
        return getStringEscapeHtml(inStr, false);
    }


    /**
     * htmlエスケープ
     * @param inStr 対象文字列
     * @return 変換後文字列
     * @throws Exception
     */
    private String getStringEscapeHtml(String inStr, boolean isValue) throws Exception {

        String result = inStr;

        if (result != null && result.length() > 0) {

            for (int i = 0; i < StringSanitizeConstants.getSanitizeString().length; i++) {
                result = Pattern.compile(StringSanitizeConstants.getSanitizeString()[i][0]).matcher(
                		result).replaceAll(StringSanitizeConstants.getSanitizeString()[i][1]);
            }
            if (isValue == false) {
        	    for (int i = 0; i < NOT_INPUT_VALUE_REPLACE.length; i++) {
                    result = Pattern.compile(NOT_INPUT_VALUE_REPLACE[i][0]).matcher(
                    		result).replaceAll(NOT_INPUT_VALUE_REPLACE[i][1]);
        	    }
            }

        }
        return result;

    }


    /**
     * htmlアンエスケープ
     * @param str 対象文字列
     * @return 変換後文字列
     * @throws Exception
     */
    public String getStringUnEscapeHtml(String str){
    	String strUnEscapeHtml = str;

    	if (strUnEscapeHtml != null && strUnEscapeHtml.length() > 0) {
        	for (int i = 0; i < StringSanitizeConstants.getSanitizeString().length; i++) {
        		strUnEscapeHtml = Pattern.compile(StringSanitizeConstants.getSanitizeString()[i][1]).matcher(
        				strUnEscapeHtml).replaceAll(StringSanitizeConstants.getSanitizeString()[i][0]);
        	}

        }
    	return strUnEscapeHtml;
    }


    /**
     * Objectから数値の文字列を取得
     * @param dictId 辞書ID
     * @param paramName パラメータ名
     * @param valueObj 変換元値
     * @param decimalPoint 少数点位置
     * @return String
     * @throws Exception 例外
     */
    protected String getStringNumber(String dictId, String paramName, Object valueObj, Integer decimalPoint) throws Exception {
        String result = "";


        try {
            result = getStringNumber(valueObj, false, decimalPoint);
        } catch (ParseException e) {
            // {0}から{1}に変換時、変換エラーが発生しました。ID=[{2}], param_name=[{3}], value=[{4}]
            String mesErr = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0008,
                    ResourcesHandler.getString(GnomesResourcesConstants.YY01_0012),
                    ResourcesHandler.getString(GnomesResourcesConstants.YY01_0009),
                                dictId, paramName, valueObj);
            throw new Exception(mesErr, e);
        }

        return result;

    }

    /**
     * Objectから日付の文字列を取得
     * @param dictId 辞書ID
     * @param paramName パラメータ名
     * @param valueObj 変換元値
     * @param datePattern 日付パターン
     * @return String
     * @throws Exception 例外
     */
    protected String getStringDate(String dictId, String paramName, Object valueObj, String datePattern) throws Exception {
        String result = "";


        try {
            result = getStringDate(valueObj, datePattern);

        } catch (ParseException e) {
            // {0}から{1}に変換時、変換エラーが発生しました。ID=[{2}], param_name=[{3}], value=[{4}]
            String mesErr = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0008,
                ResourcesHandler.getString(GnomesResourcesConstants.YY01_0012),
                ResourcesHandler.getString(GnomesResourcesConstants.YY01_0011),
                dictId, paramName, valueObj);
            throw new Exception(mesErr, e);
        }
        return result;

    }

    /**
     * HTML日付フォーマット取得
     * @param datePattern 対象日付パターン
     * @return 変換後文字列
     * @throws Exception
     */
    protected String getStringDateFormatHtml(String datePattern) throws Exception {

        String result = datePattern;

        if (result != null && result.length() > 0) {
        	for (int j = 0; j < DATE_PATTERN_REPLACE.length; j++) {
        		result = Pattern.compile(DATE_PATTERN_REPLACE[j][0]).matcher(result).replaceAll(DATE_PATTERN_REPLACE[j][1]);
        	}
        }
        return result;

    }

    /**
     * パーツ権限情報Listを取得
     * @param bean ビーン
     * @return パーツ権限情報
     */
    protected List<PartsPrivilegeResultInfo> getPartsPrivilegeResultInfoList(Object bean) {
        List<PartsPrivilegeResultInfo> stmPartsPrivilegeResultInfo = null;
        Class<?> clazz = bean.getClass();
        Class<?> intrfc = IScreenPrivilegeBean.class;

        // インターフェースを実装したクラスであるかどうかをチェック
        if (!clazz.isInterface() && intrfc.isAssignableFrom(clazz)
                && !Modifier.isAbstract(clazz.getModifiers())) {

            IScreenPrivilegeBean privilegeBean = null;
            privilegeBean = (IScreenPrivilegeBean) bean;

            if (privilegeBean.getPartsPrivilegeResultInfo() != null) {
                stmPartsPrivilegeResultInfo = privilegeBean.getPartsPrivilegeResultInfo();
            }
        }
        return stmPartsPrivilegeResultInfo;
    }

    /**
     * tagIdの対応するパーツ権限情報を取得
     * @param buttonId ボタンID
     * @param stmPartsPrivilegeResultInfo パーツ権限情報List
     * @return パーツ権限情報
     */
    protected PartsPrivilegeResultInfo getPartsPrivilegeResultInfo(final String buttonId, List<PartsPrivilegeResultInfo> stmPartsPrivilegeResultInfo) {

        PartsPrivilegeResultInfo partsPrivilegeResultInfo = null;

        if (stmPartsPrivilegeResultInfo != null) {

            List<PartsPrivilegeResultInfo> partsPrivilegeResultInfos =
                    stmPartsPrivilegeResultInfo.stream()
                    .filter(item -> item.getButtonId().equals(buttonId))
                    .collect(Collectors.toList());
            if (partsPrivilegeResultInfos.size() == 1) {
                partsPrivilegeResultInfo = partsPrivilegeResultInfos.get(0);
            }
        }
        return partsPrivilegeResultInfo;
    }


    /**
     * BaseFormBeanのカスタムタグの非表示区分よりstyleを取得する
     * (style属性付き)
     *
     * @param formBean foemBean基底
     * @param key キー
     * @return スタイル
     */
    protected String getStyleHiddenKindWithAttribute(Object formBean, String dictId) {
    	String style = getStyleHiddenKind(formBean, dictId);

    	if (style.length() > 0) {
    		style = " style=\"" + style + "\"";
    	}
    	return style;
    }


    /**
     * BaseFormBeanのカスタムタグの非表示区分よりstyleを取得する
     *
     * @param formBean foemBean基底
     * @param key キー
     * @return スタイル
     */
    protected String getStyleHiddenKind(Object formBean, String key) {
        String strStyle = "";

        Map<String,TagHiddenKind> mapHiddenNone = ((BaseFormBean)formBean).getTagHiddenKindMap();

        if (mapHiddenNone != null) {
            TagHiddenKind tagHiddenNone = mapHiddenNone.get(key);
            if (tagHiddenNone != null) {

                switch (tagHiddenNone) {
                    case HIDDEN:
                        // そのタグは非表示になる。ただしそのタグがあったスペースはそのまま残る。（レイアウトは変わらない）
                        strStyle = "visibility:hidden;";
                        break;

                    case NONE:
                        // そのタグ自体の存在が消えたようになる（そのタグがあったスペースは縮まって無くなる）。（レイアウトが変わる）
                        strStyle = "display:none;";
                        break;

                    default:
                        break;
                }
            }
        }

        return strStyle;
    }

    /**
     * Bean,Dtoの項目_readonlyから入力可能か否かを判定する。
     * 取得できない場合は入力可能として判定する。
     * @param listcls 取得元クラス
     * @param data 取得元data
     * @param paramName パラメータ名
     * @return 入力可能か否か
     */
    protected Boolean isInputReadOnlyParam(Class<?> cls, Object data, String paramName) {

    	// "パラメータ名_readonly"のフィールド名
        String paramNameReadOnly = paramName + "_readonly";

        // 入力可能か否か（：入力可能）
        Boolean result = false;

        try {
        	// 入力可能か否かを取得
			result = ConverterUtils.IntTobool((Integer)getData(cls, data, paramNameReadOnly));
		} catch (Exception e) {
			// 処理なし
		}
        return result;
    }

    /**
     * プルダウン選択取得
     * @param listValue 選択候補リスト
     * @param selValue 選択値
     * @param outSelKey 選択キー
     * @param outSelText 選択文字
     * @return true 選択あり
     * @throws Exception
     */
    protected boolean getSelect(List<Object> listValue, Object selValue, String[] outSel) throws Exception {

    	String compValue = null;
        if (selValue != null) {
            if (selValue instanceof BigDecimal) {
                compValue = String.valueOf(((BigDecimal)(selValue)).intValue());
            } else {
                compValue = selValue.toString();
            }
        }

    	outSel[0] = "";
    	outSel[1] = "";

        // プルダウン内のデータ生成
        if (listValue != null && listValue.size() > 0) {
            Class<?> lstClass = listValue.get(0).getClass();
            for (int l=0; l<listValue.size(); l++) {
                Object item = listValue.get(l);

                Object key = getData(lstClass, item, INFO_PULLDOWN_NAME);
                Object value = getData(lstClass, item, INFO_PULLDOWN_VALUE);
                if (value == null) {
                	value = "";
                }

                if (compValue != null && compValue.compareTo(key.toString()) == 0) {
                	outSel[0] = compValue;
                	outSel[1] = value.toString();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 選択候補の出力
     * @param out 出力先
     * @param listValue 選択候補リスト
     * @param selValue 選択値
     * @throws Exception
     */
    protected void outSelectOption(JspWriter out, List<Object> listValue, Object selValue) throws Exception {

    	outSelectOption(out, listValue, selValue, null);
    }

    /**
     * 選択候補の出力
     * @param out 出力先
     * @param listValue 選択候補リスト
     * @param selValue 選択値
     * @param selName (差し替え用)選択値表示名
     * @throws Exception
     */
    protected void outSelectOption(JspWriter out, List<Object> listValue, Object selValue, String selName) throws Exception {

    	String compValue = null;
        if (selValue != null) {
            if (selValue instanceof BigDecimal) {
                compValue = String.valueOf(((BigDecimal)(selValue)).intValue());
            } else {
                compValue = selValue.toString();
            }
        }

    	// プルダウン内のデータ生成
        boolean isSelected = false;
        if (listValue != null && listValue.size() > 0) {
            Class<?> lstClass = listValue.get(0).getClass();
            for (int i=0; i<listValue.size(); i++) {
                Object item = listValue.get(i);

                Object key = getData(lstClass, item, INFO_PULLDOWN_NAME);
                String value = (String)getData(lstClass, item, INFO_PULLDOWN_VALUE);
                if (value == null) {
                    value = "";
                }
                String selected = "";

                // 選択候補と選択値が一致する場合
                if (isSelected == false && compValue != null && compValue.compareTo(key.toString()) == 0) {
                    selected = " selected";
                    isSelected = true;

                    // (差し替え用)選択値表示名が設定されている場合
                    if (!StringUtil.isNullOrEmpty(selName)) {
                    	value = selName;
                    }
                }
                out.print("<option value=\""
                        + this.getStringEscapeHtmlValue((String) key.toString()) + "\""
                        + selected + ">"
                        + this.getStringEscapeHtml(value)
                        + "</option>\n");
            }
        }

    }

    /**
     * パターン定義からパターン項目値の取得
     * @param dict 辞書
     * @param patternId パターンID
     * @param keyValue キー値
     * @return
     * @throws Exception
     */
    protected String getPatternValue(GnomesCTagDictionary dict, String patternId, String keyValue) throws Exception {

        String value = "";

        // パターン定義を取得
        Map<String, Object> mapPtn = dict.getPatternInfo(patternId, keyValue);

        if(!Objects.isNull(mapPtn)) {
        	// パターン項目値を取得
            value = (String) mapPtn.get(GnomesCTagDictionary.MAP_NAME_PTN_VALUE);

            if(StringUtil.isNullOrEmpty(value)) {
            	value = "";
            }
        }

        return value;
    }

    /**
     * エラー時のメニューへのリンク生成
     * @return
     * @throws Exception
     */
    protected String getCTagErrorToMenu() throws Exception {

        String value = ERROR_LABEL_HTML
                + getTagErrMes()
                + ERROR_ANCHOR_END_HTML
                + ERROR_LABEL_END_HTML;
        return value;
    }

    /**
     * 接続領域ごと操作可否を画面アイテム定義辞書から入手し、enumで返す
     *
     * @param mapTagValue	画面アイテム定義辞書
     * @return 接続領域ごと操作可否
     */
    private OperationConnectionArea getOperationConnectionArea(Map<String, Object> mapTagValue) {

    	String operationConnectionAreaVal = (String)mapTagValue.get(TAGNAME_OPERATION_CONNECTION_AREA);

    	//nullまたは空白ならALLでリターンする
    	if(StringUtil.isNullOrEmpty(operationConnectionAreaVal)){
    		return OperationConnectionArea.AREA_ALL;
    	}

    	//全体の場合(0)
    	if(operationConnectionAreaVal.equals(OperationConnectionAreaTagval.AREA_ALL.getValue()))
    	{
    		return OperationConnectionArea.AREA_ALL;
    	}

    	//通常領域の場合(1)
    	if(operationConnectionAreaVal.equals(OperationConnectionAreaTagval.AREA_NORMAL.getValue()))
    	{
    		return OperationConnectionArea.AREA_NORMAL;
    	}

    	//保管領域の場合(1)
    	if(operationConnectionAreaVal.equals(OperationConnectionAreaTagval.AREA_STORAGE.getValue()))
    	{
    		return OperationConnectionArea.AREA_STORAGE;
    	}

    	//１つも属さない場合はALLで戻す
    	return OperationConnectionArea.AREA_ALL;
	}

    /**
     * 続領域ごと操作可否を画面アイテム定義辞書から入手し、それが現在の接続情報と食い違っている場合
     * falseを返して非表示などの制御に使う
     *
     * @param mapTagValue	画面アイテム定義辞書
     * @return 表示・非表示の判定
     */
    protected boolean judgeDisplayFromConnectionArea(Map<String, Object> mapTagValue)
    {
    	OperationConnectionArea connectionArea = getOperationConnectionArea(mapTagValue);

    	//画面アイテム定義辞書から入手した領域指定がALLだったらtrueで返す
    	if(connectionArea == OperationConnectionArea.AREA_ALL){
    		return true;
    	}

    	//保存先が通常・保管かどうかを取得
    	String regionType = this.gnomesSessionBean.getRegionType();

    	//nullや空白だと原則表示とする
    	if(StringUtil.isNullOrEmpty(regionType)){
    		return true;
    	}

    	//SessionBean通常領域でmapが保管領域指定の場合はfalseにする
    	if(regionType.equals(CommonEnums.RegionType.NORMAL.getValue())){
    		if(connectionArea == OperationConnectionArea.AREA_STORAGE){
    			return false;
    		}
    		else {
    			return true;
    		}
    	}
    	//SessionBean保管領域でmapが通常領域指定の場合はfalseにする
    	if(regionType.equals(CommonEnums.RegionType.STORAGE.getValue())){
    		if(connectionArea == OperationConnectionArea.AREA_NORMAL){
    			return false;
    		}
    		else {
    			return true;
    		}
    	}
    	//ここに来ることはないが、念のためtrue（表示）とする
    	return true;
    }
    /**
     * 画面アイテム定義辞書から入手し、セッションの領域と違っていたらstyle(display:none)クラスで
     * 設定する
     *
     * @param mapTagValue
     * @return 表示する場合は何も指定しない("") 非表示する場合は非表示クラスを返す
     */
    protected String judgeAndSetDisabledClass(Map<String, Object> mapTagValue) {
    	//判定してfalseなら非表示と指定されたクラスを指定する
    	if(!judgeDisplayFromConnectionArea(mapTagValue)){
    		return " gnomes_display_none ";
    	}
    	else {
    		return "";
    	}
	}

    /**
     * 画面アイテム定義辞書から入手し、セッションの領域と違っていたら非活性 style(display:none)クラスで
     * 設定する
     *
     * @param mapTagValue
     * @return
     */
    protected String judgeAndSetActivityInClass(Map<String, Object> mapTagValue) {
    	//判定してfalseなら非活性と指定されたクラスを指定する
    	if(!judgeDisplayFromConnectionArea(mapTagValue)){
    		return " disabled ";
    	}
    	else {
    		return "";
    	}
	}

    /**
     * 接続領域ごと操作可否をMenuItemInfoBeanから入手し、enumで返す
     *
     * @param mapTagValue   画面アイテム定義辞書
     * @return 接続領域ごと操作可否
     */
    private OperationConnectionArea getOperationConnectionAreaByBean(MenuItemInfoBean menuItemInfoBean) {

        String operationConnectionAreaVal = menuItemInfoBean.getOperationConnectionArea();

        //nullまたは空白ならALLでリターンする
        if(StringUtil.isNullOrEmpty(operationConnectionAreaVal)){
            return OperationConnectionArea.AREA_ALL;
        }

        //全体の場合(0)
        if(operationConnectionAreaVal.equals(OperationConnectionAreaTagval.AREA_ALL.getValue()))
        {
            return OperationConnectionArea.AREA_ALL;
        }

        //通常領域の場合(1)
        if(operationConnectionAreaVal.equals(OperationConnectionAreaTagval.AREA_NORMAL.getValue()))
        {
            return OperationConnectionArea.AREA_NORMAL;
        }

        //保管領域の場合(1)
        if(operationConnectionAreaVal.equals(OperationConnectionAreaTagval.AREA_STORAGE.getValue()))
        {
            return OperationConnectionArea.AREA_STORAGE;
        }

        //１つも属さない場合はALLで戻す
        return OperationConnectionArea.AREA_ALL;
    }

    /**
     * 続領域ごと操作可否をMenuItemInfoBeanから入手し、それが現在の接続情報と食い違っている場合
     * falseを返して非表示などの制御に使う
     *
     * @param mapTagValue   画面アイテム定義辞書
     * @return 表示・非表示の判定
     */
    protected boolean judgeDisplayFromConnectionAreaByBean(MenuItemInfoBean menuItemInfoBean)
    {
        OperationConnectionArea connectionArea = getOperationConnectionAreaByBean(menuItemInfoBean);

        //画面アイテム定義辞書から入手した領域指定がALLだったらtrueで返す
        if(connectionArea == OperationConnectionArea.AREA_ALL){
            return true;
        }

        //保存先が通常・保管かどうかを取得
        String regionType = this.gnomesSessionBean.getRegionType();

        //nullや空白だと原則表示とする
        if(StringUtil.isNullOrEmpty(regionType)){
            return true;
        }

        //SessionBean通常領域でmapが保管領域指定の場合はfalseにする
        if(regionType.equals(CommonEnums.RegionType.NORMAL.getValue())){
            if(connectionArea == OperationConnectionArea.AREA_STORAGE){
                return false;
            }
            else {
                return true;
            }
        }
        //SessionBean保管領域でmapが通常領域指定の場合はfalseにする
        if(regionType.equals(CommonEnums.RegionType.STORAGE.getValue())){
            if(connectionArea == OperationConnectionArea.AREA_NORMAL){
                return false;
            }
            else {
                return true;
            }
        }
        //ここに来ることはないが、念のためtrue（表示）とする
        return true;
    }

    /**
     * 画面アイテム定義辞書から入手し、セッションの領域と違っていたらstyle(display:none)クラスで
     * 設定する
     *
     * @param mapTagValue
     * @return 表示する場合は何も指定しない("") 非表示する場合は非表示クラスを返す
     */
    protected String judgeAndSetDisabledClassByBean(MenuItemInfoBean menuItemInfoBean) {
        //判定してfalseなら非表示と指定されたクラスを指定する
        if(!judgeDisplayFromConnectionAreaByBean(menuItemInfoBean)){
            return " gnomes_display_none ";
        }
        else {
            return "";
        }
    }

    /**
     * dataTypeが日付時刻形式かどうかを判定
     *
     * @param dataType
     * @return
     */
    protected boolean isDataTypeDateTime(Integer dataType) {
        return dataTypeFormatMap.containsKey(dataType);
    }

    /**
     * dataTypeに紐付いている日付時刻フォーマット文字列を取得
     *
     * @param dataType
     * @return
     */
    protected static String getDataTypeDateTimeFormat(Integer dataType) {
        String datePattern = null;
        if (dataTypeFormatMap.containsKey(dataType)) {
            datePattern = dataTypeFormatMap.get(dataType);
        }
        return datePattern;
    }

    /**
     * タグ出力でエラー時の出力内容
     * @return
     * @throws Exception
     */
    protected String getTagErrMes() throws Exception {
        // タグ出力でエラー時の出力内容
        Locale userLocale = gnomesSessionBean.getUserLocale();
        String tagErrMes = "<label class=\"TABLE_LABEL\" type=\"text\" >" + ResourcesHandler.getString(GnomesResourcesConstants.DI01_0134, userLocale) + "</label>";

        return tagErrMes;
    }

    /**
     * 共通機能：辞書名のxxx.yyyに対してyyyを抜き出し、id="yyy" name="yyy"　を出力する
     * カスタムタグIDが "."で区切られていない場合は、辞書名そのものを出力する
     * @param out 出力コンテクスト
     * @param dictId "MGA000_PH.customTagId"のように指定するカスタムタグID
     * @throws IOException
     */
    protected void outTagNameToIdName(JspWriter out, String dictId) throws IOException
    {
        String outIdTagName;
        String[] workTblIds = dictId.split("\\.");
        if(workTblIds.length >= 2) {
            outIdTagName = workTblIds[1];
        }
        else {
            outIdTagName = dictId;
        }
        out.print(" id = \"" + outIdTagName + "\" name = \"" + outIdTagName + "\" ");
    }
    /**
     * 辞書情報のタグ名を使って文字列Id="name" name="name"を出力する
     * @param mapColInfo　辞書情報
     * @return idとname宣言のDOM文字列
     */
    protected String outIdNameString(Map<String, Object> mapColInfo) {
        // タグ名
        String name = (String)mapColInfo.get(INFO_TAG_NAME);

        return(" id=\"" + name + "\" name=\"" + name + "\" ");

    }
}
