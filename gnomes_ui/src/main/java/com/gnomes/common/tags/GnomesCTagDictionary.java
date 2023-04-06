package com.gnomes.common.tags;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesLogMessageConstants;

/**
 * カスタムタグ辞書
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
@ApplicationScoped
public class GnomesCTagDictionary {

	@Inject
	Logger logger;

	@Inject
	LogHelper loghelper;

    /** テーブルの項目タグ名 */
    public static final String MAP_NAME_TABLE_COLUMN = "column";
    /** テーブルのヘッダタグ名 */
    public static final String MAP_NAME_TABLE_INFO = "headinfo";

    /** テーブルの詳細タグ名 */
    public static final String MAP_NAME_COLUMN_INFO = "colinfo";

    public static final String MAP_NAME_RESOURCE_ID = "resource_id";


    /** パターン定義の値名 */
    public static final String MAP_NAME_PTN_VALUE = "value";

    /** パターン定義のタイトル名 */
    public static final String MAP_NAME_PTN_TITLE = "title";


    /** テーブルの項目詳細情報一覧 */
    public static final String MAP_NAME_TABLE_COOLUMNS = "columns";


    /** メニューの項目のタグ名 */
    public static final String MAP_NAME_MENU_ITEM_INFO = "menu_item_info";

    /** メニューの項目の値名 */
    public static final String MAP_NAME_ITEM_NUMBER = "item_number";

    /** メニューボタンの項目のタグ名 */
    public static final String MAP_NAME_MENU_BUTTON_ITEM_INFO = "menu_button_item_info";

    /** メニューボタンの項目の値名 */
    public static final String MAP_NAME_BUTTON_ITEM_NUMBER = "button_item_number";

    /** IDの区切り文字 */
    public static final String ID_SEPARATOR = ".";

    /** IDの区切り文字 */
    private static final String ID_SEPARATOR_REGEX = "\\.";

    /** ラベルの項目のタグ名 */
    public static final String MAP_NAME_LABEL_ITEM_INFO = "label_item_info";

    /** Exceptionに出力する共通のメッセージ */
    public static final String EXCEPTION_MSG_NOTKEY = "The custom tag key set in the JSP file does not exist in the screen item definition";
    /**
     * ボタン用の辞書
     */
    private Map<String, Object> buttonDict;

    /**
     * リンク用の辞書
     */
    private Map<String, Object> linkDict;

    /**
     * メニュー（右ペイン）用の辞書
     */
    private Map<String, Object> menuDict;

    /**
     * 検索メニュー（右ペイン）用の辞書
     */
    private Map<String, Object> searchMenuDict;

    /**
     * 検索ダイアログ用の辞書
     */
    private Map<String, Object> searchDialogDict;

    /**
     * ソートダイアログ用の辞書
     */
    private Map<String, Object> sortDialogDict;

    /**
     * テーブル用の辞書
     */
    private Map<String, Object> tableDict;

    /**
     * Hidden用の辞書
     */
    private Map<String, Object> hiddenDict;

    /**
     * テキスト用の辞書
     */
    private Map<String, Object> textDict;

    /**
     * テキストエリア用の辞書
     */
    private Map<String, Object> textAreaDict;

    /**
     * ラベル用の辞書
     */
    private Map<String, Object> labelDict;

    /**
     * プルダウン用の辞書
     */
    private Map<String, Object> pullDownDict;

    /**
     * 数値用の辞書
     */
    private Map<String, Object> numberDict;

    /**
     * 日付用の辞書
     */
    private Map<String, Object> dateDict;

    /**
     * 期間用の辞書
     */
    private Map<String, Object> periodDict;


    /**
     * パターンの辞書
     */
    private Map<String, Object> patternDict;

    /**
     * タブ用の辞書
     */
    private Map<String, Object> tabDict;

    /**
     * メニューボタン用の辞書
     */
    private Map<String, Object> menuButtonDict;

    /**
     * アイコンメニューボタン用の辞書
     */
    private Map<String, Object> iconMenuDict;

    /**
     * ツリーボタン用の辞書
     */
    private Map<String, Object> treeDict;

    /**
     * ページングボタン用の辞書
     */
    private Map<String, Object> pagingButtonDict;

    /**
     * ラジオボタン用の辞書
     */
    private Map<String, Object> radioDict;

    /**
     * チェックボックス用の辞書
     */
    private Map<String, Object> checkboxDict;

    /**
     * ヘッダー用の辞書
     */
    private Map<String, Object> headerDict;

    /**
     * バーコード入力用の辞書
     */
    private Map<String, Object> barCodeDict;

    /**
     * コンストラクタ―
     */
    public GnomesCTagDictionary() {
    }


    /**
     * ボタン情報を取得
     * @param id 辞書キー
     * @return ボタン辞書情報
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getButtonInfo(String id) throws GnomesAppException {
    	if(buttonDict.containsKey(id)==false){
    		loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "Button",id);
    		throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
    	}
        return (Map<String, Object>) buttonDict.get(id);
    }

    /**
     * リンク情報を取得
     * @param id 辞書キー
     * @return リンク辞書情報
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getLinkInfo(String id) throws GnomesAppException {
    	if(linkDict.containsKey(id)==false){
    		loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "Link",id);
    		throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
    	}
        return (Map<String, Object>) linkDict.get(id);
    }

    /**
     * メニュー（右ペイン）辞書情報を取得
     * @param id 辞書キー
     * @return メニュー辞書情報
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    public Map<String,Object> getMenuInfo(String id) throws GnomesAppException {
    	if(menuDict.containsKey(id)==false){
    		loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "MenuPain",id);
    		throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
    	}
        return (Map<String, Object>) menuDict.get(id);
    }

    /**
     * メニュー項目定義情報を取得
     * @param id 辞書キー
     * @return 該当メニュー項目の定義情報
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getMenuItemInfo(String id) throws GnomesAppException {
        Map<String, Object> result = null;

        if (menuDict.containsKey(id)) {
            Map<String, Object> mapMenuItem = (Map<String, Object>) menuDict.get(id);
            if (mapMenuItem.containsKey(MAP_NAME_MENU_ITEM_INFO)) {
                result = (Map<String, Object>) mapMenuItem.get(MAP_NAME_MENU_ITEM_INFO);
            }
        }
        else {
        	loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "Menu",id);
        	throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
        }
        return result;
    }

    /**
     * 検索メニュー（右ペイン）辞書情報を取得
     * @param id 辞書キー
     * @return 検索メニュー辞書情報
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    public Map<String,Object> getSearchMenuInfo(String id) throws GnomesAppException {
    	if(searchMenuDict.containsKey(id)==false){
    		loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "searchMenu",id);
    		throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
    	}
        return (Map<String, Object>) searchMenuDict.get(id);
    }

    /**
     * 検索ダイアログ辞書情報を取得
     * @param id 辞書キー
     * @return 検索ダイアログ辞書情報
     */
    @SuppressWarnings("unchecked")
    public Map<String,Object> getSearchDialogInfo(String id) throws GnomesAppException {
    	if(searchDialogDict.containsKey(id)==false){
    		loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "searchDialog",id);
    		throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
    	}
        return (Map<String, Object>) searchDialogDict.get(id);
    }

    /**
     * ソートダイアログ辞書情報を取得
     * @param id 辞書キー
     * @return ソートダイアログ辞書情報
     */
    @SuppressWarnings("unchecked")
    public Map<String,Object> getSortDialogInfo(String id) throws GnomesAppException {
    	if(sortDialogDict.containsKey(id)==false){
    		loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "sortDialog",id);
    		throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
    	}
        return (Map<String, Object>) sortDialogDict.get(id);
    }

    /**
     * メニューボタン辞書情報を取得
     * @param id 辞書キー
     * @return メニュー辞書情報
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    public Map<String,Object> getMenuButtonInfo(String id) throws GnomesAppException {
    	if(menuButtonDict.containsKey(id)==false){
    		loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "menuButton",id);
    		throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
    	}
        return (Map<String, Object>) menuButtonDict.get(id);
    }

    /**
     * アイコンメニュー辞書情報を取得
     * @param id 辞書キー
     * @return メニュー辞書情報
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    public Map<String,Object> getIconMenuInfo(String id) throws GnomesAppException {
    	if(iconMenuDict.containsKey(id)==false){
    		loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "iconMenu",id);
    		throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
    	}
        return (Map<String, Object>) iconMenuDict.get(id);
    }


    /**
     * メニューボタン項目定義情報を取得
     * @param id 辞書キー
     * @return 該当メニュー項目の定義情報
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getMenuButtonItemInfo(String id) throws GnomesAppException {
        Map<String, Object> result = null;

        if (menuButtonDict.containsKey(id)) {
            Map<String, Object> mapMenuItem = (Map<String, Object>) menuButtonDict.get(id);
            if (mapMenuItem.containsKey(MAP_NAME_MENU_BUTTON_ITEM_INFO)) {
                result = (Map<String, Object>) mapMenuItem.get(MAP_NAME_MENU_BUTTON_ITEM_INFO);
            }
        }
        else {
        	loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "menuButton",id);
    		throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
        }
        return result;
    }

    /**
     * テキスト辞書情報を取得
     * @param id 辞書キー
     * @return テキスト辞書情報
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getTextInfo(String id) throws GnomesAppException {
    	if(textDict.containsKey(id)==false){
    		loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "text",id);
    		throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
    	}
        return (Map<String, Object>) textDict.get(id);
    }

    /**
     * Hidden辞書情報を取得
     * @param id 辞書キー
     * @return テキスト辞書情報
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getHiddenInfo(String id) throws GnomesAppException {
    	if(hiddenDict.containsKey(id)==false){
    		loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "hidden",id);
    		throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
    	}
        return (Map<String, Object>) hiddenDict.get(id);
    }

    /**
     * テキストエリア辞書情報を取得
     * @param id 辞書キー
     * @return テキスト辞書情報
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getTextAreaInfo(String id) throws GnomesAppException {
    	if(textAreaDict.containsKey(id)==false){
    		loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "textArea",id);
    		throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
    	}
        return (Map<String, Object>) textAreaDict.get(id);
    }

    /**
     * ラベル辞書情報を取得
     * @param id 辞書キー
     * @return ラベル辞書情報
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getLabelInfo(String id) throws GnomesAppException {
    	if(labelDict.containsKey(id)==false){
    		loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "label",id);
    		throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
    	}
        return (Map<String, Object>) labelDict.get(id);
    }


    /**
     * ラベル項目定義情報を取得
     * @param id 辞書キー
     * @return 該当ラベル項目の定義情報
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getLabelItemInfo(String id) throws GnomesAppException {
        Map<String, Object> result = null;

        if (labelDict.containsKey(id)) {
            Map<String, Object> mapLabelItem = (Map<String, Object>) labelDict.get(id);
            if (mapLabelItem.containsKey(MAP_NAME_LABEL_ITEM_INFO)) {
                result = (Map<String, Object>) mapLabelItem.get(MAP_NAME_LABEL_ITEM_INFO);
            }
        }
        else {
        	loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "label",id);
    		throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
        }
        return result;
    }


    /**
     * プルダウン辞書情報を取得
     * @param id 辞書キー
     * @return ドロップダウン辞書情報
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getPullDownInfo(String id) throws GnomesAppException {
    	if(pullDownDict.containsKey(id)==false){
    		loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "pullDown",id);
    		throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
    	}
        return (Map<String, Object>) pullDownDict.get(id);
    }

    /**
     * 数値辞書情報を取得
     * @param id 辞書キー
     * @return 数値辞書情報
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getNumberInfo(String id) throws GnomesAppException {
    	if(numberDict.containsKey(id)==false){
    		loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "number",id);
    		throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
    	}
        return (Map<String, Object>) numberDict.get(id);
    }

    /**
     * 日付辞書情報を取得
     * @param id 辞書キー
     * @return 日付辞書情報
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getDateInfo(String id) throws GnomesAppException {
    	if(dateDict.containsKey(id)==false){
    		loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "date",id);
    		throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
    	}
        return (Map<String, Object>) dateDict.get(id);
    }

    /**
     * 期間辞書情報を取得
     * @param id 辞書キー
     * @return 日付辞書情報
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getPeriodInfo(String id) throws GnomesAppException {
    	if(periodDict.containsKey(id)==false){
    		loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "period",id);
    		throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
    	}
        return (Map<String, Object>) periodDict.get(id);
    }

    /**
     * パターン定義情報を取得
     * @param id 辞書キー
     * @param pid パターン値
     * @return 該当パターンの定義情報
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getPatternInfo(String id, String pid) throws GnomesAppException {
        Map<String, Object> result = null;

        if (patternDict.containsKey(id)) {
            Map<String, Object> mapPtn = (Map<String, Object>) patternDict.get(id);
            if (mapPtn.containsKey(pid)) {
                result = (Map<String, Object>) mapPtn.get(pid);
            }
        }
        else {
        	loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "pattern",id);
    		throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
        }
        return result;
    }

    /**
     * パターン辞書取得
     * @param id 辞書キー
     * @return 該当パターン辞書
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getPatternDict(String id) throws GnomesAppException {
        Map<String, Object> mapPtn = null;

        if (patternDict.containsKey(id)) {
            mapPtn = (Map<String, Object>) patternDict.get(id);
        }
        else {
        	loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "pattern",id);
    		throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
        }
        return mapPtn;
    }

    /**
     * タブ辞書情報を取得
     * @param id 辞書キー
     * @return ラベル辞書情報
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getTabInfo(String id) throws GnomesAppException {
    	if(tabDict.containsKey(id)==false){
    		loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "tab",id);
    		throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
    	}
        return (Map<String, Object>) tabDict.get(id);
    }

    /**
     * テーブル辞書情報を取得
     * @param id 辞書キー
     * @return テーブル辞書情報
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    public Map<String,Object> getTableInfo(String id) throws GnomesAppException {
    	if(tableDict.containsKey(id)==false){
    		loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "table",id);
    		throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
    	}
        return (Map<String, Object>) tableDict.get(id);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String,Object>> getTableColumnInfo(Map<String, Object> map) {
        return (List<Map<String,Object>>) map.get(MAP_NAME_TABLE_COOLUMNS);
    }

    /**
     * 画面ID内のテーブルID、pageing有無を取得
     * @param screenId 画面ID
     * @return テーブルID、ページングID(ページングなしはNULL)
     */
    @SuppressWarnings("unchecked")
    public Map<String, Boolean> getTableId(String screenId) {

        Map<String, Boolean> result = new HashMap<>();
        //        String prefix = this.convScreenTagId(screenId).concat(ID_SEPARATOR);
        String prefix = screenId.concat(ID_SEPARATOR);

        int sepCnt = screenId.split(ID_SEPARATOR_REGEX).length + 1;

        // 画面内のページング取得
        List<Map<String, Object>> pageingMapList = new ArrayList<>();
        for (String key : pagingButtonDict.keySet()) {
            if (key.startsWith(prefix) &&
                    sepCnt == key.split(ID_SEPARATOR_REGEX).length) {

                pageingMapList
                        .add((Map<String, Object>) pagingButtonDict.get(key));
            }
        }

        // 画面内のテーブル検索
        for (String tableId : tableDict.keySet()) {
            if (tableId.startsWith(prefix) &&
                    sepCnt == tableId.split(ID_SEPARATOR_REGEX).length) {

                // テーブルのページング判定
                Boolean isPageing = new Boolean(false);

                for (Map<String, Object> pageing : pageingMapList) {
                    String pageingTableId = (String) pageing
                            .get(GnomesCTagPageNaviButton.INFO_TABLE_ID);
                    if (tableId.equals(pageingTableId)) {
                        isPageing = true;
                        break;
                    }
                }

                result.put(tableId, isPageing);
            }
        }
        return result;
    }

    /**
     * ツリー辞書情報を取得
     * @param id 辞書キー
     * @return ラベル辞書情報
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getTreeInfo(String id) throws GnomesAppException {
    	if(treeDict.containsKey(id)==false){
    		loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "tree",id);
    		throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
    	}
        return (Map<String, Object>) treeDict.get(id);
    }

    /**
     * ツリー辞書情報を取得
     * @param id 辞書キー
     * @return ラベル辞書情報
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getPagingButtonInfo(String id) throws GnomesAppException {
    	if(pagingButtonDict.containsKey(id)==false){
    		loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "pagingButton",id);
    		throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
    	}
        return (Map<String, Object>) pagingButtonDict.get(id);
    }

    /**
     * ラジオボタン辞書情報を取得
     * @param id 辞書キー
     * @return ラベル辞書情報
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getRadioInfo(String id) throws GnomesAppException {
    	if(radioDict.containsKey(id)==false){
    		loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "radio",id);
    		throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
    	}
        return (Map<String, Object>) radioDict.get(id);
    }

    /**
     * チェックボックス辞書情報を取得
     * @param id 辞書キー
     * @return ラベル辞書情報
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getCheckBoxInfo(String id) throws GnomesAppException {
    	if(checkboxDict.containsKey(id)==false){
    		loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "checkbox",id);
    		throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
    	}
        return (Map<String, Object>) checkboxDict.get(id);
    }

    /**
     * ヘッダー辞書情報を取得
     * @param id 辞書キー
     * @return ヘッダー辞書情報
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getHeaderInfo(String id) throws GnomesAppException {
    	if(headerDict.containsKey(id)==false){
    		loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "header",id);
    		throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
    	}
        return (Map<String, Object>) headerDict.get(id);
    }

    /**
     * ヘッダーメニュー項目定義情報を取得
     * @param id 辞書キー
     * @return ヘッダーメニュー項目の定義情報
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getHeaderMenuItemInfo(String id) throws GnomesAppException {
        Map<String, Object> result = null;

        if (headerDict.containsKey(id)) {
            Map<String, Object> mapMenuItem = (Map<String, Object>) headerDict.get(id);
            if (mapMenuItem.containsKey(MAP_NAME_MENU_ITEM_INFO)) {
                result = (Map<String, Object>) mapMenuItem.get(MAP_NAME_MENU_ITEM_INFO);
            }
        }
        else {
        	loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "header",id);
    		throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
        }
        return result;
    }

    /**
     * バーコード入力辞書情報を取得
     * @param id 辞書キー
     * @return ラベル辞書情報
     * @throws GnomesAppException
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getBarCodeInfo(String id) throws GnomesAppException {
    	if(barCodeDict.containsKey(id)==false){
    		loghelper.severeOfResourceID(logger, GnomesLogMessageConstants.ME01_0180, "barCode",id);
    		throw new GnomesAppException(EXCEPTION_MSG_NOTKEY);
    	}
        return (Map<String, Object>) barCodeDict.get(id);
    }

    /**
     * 辞書読込
     * @param path 辞書ファイルパス
     */
    public void readDict(String path) {

        File file = new File(path);

        Document doc = null;

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            // XML解析で外部エンティティへのアクセスを無効にする
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            DocumentBuilder db = dbf.newDocumentBuilder();

            dbf.setIgnoringComments(true);

            doc = db.parse(file);
        } catch (ParserConfigurationException pce) {
            throw new GnomesException("Parser was not configured properly", pce);
        } catch (IOException io) {
            throw new GnomesException("Cannot read input file", io);
        } catch (SAXException se) {
            throw new GnomesException("Problem parsing the file", se);
        } catch (IllegalArgumentException ae) {
            throw new GnomesException("Please specify an XML source", ae);
        }

        Element root = doc.getDocumentElement();

        //辞書Mapのインスタンス生成
        if(buttonDict == null){
            buttonDict = new HashMap<String, Object>();
        }

        if(linkDict == null){
            linkDict = new HashMap<String, Object>();
        }

        if(menuDict == null){
            menuDict = new HashMap<String, Object>();
        }

        if(searchMenuDict == null){
            searchMenuDict = new HashMap<String, Object>();
        }

        if(searchDialogDict == null){
            searchDialogDict = new HashMap<String, Object>();
        }

        if(sortDialogDict == null){
            sortDialogDict = new HashMap<String, Object>();
        }

        if(tableDict == null){
            tableDict = new HashMap<String, Object>();
        }

        if(tabDict == null){
        	tabDict = new HashMap<String, Object>();
        }

        if(hiddenDict == null){
            hiddenDict = new HashMap<String, Object>();
        }

        if(textDict == null){
            textDict = new HashMap<String, Object>();
        }

        if(textAreaDict == null){
            textAreaDict = new HashMap<String, Object>();
        }

        if(labelDict == null){
            labelDict = new HashMap<String, Object>();
        }

        if(pullDownDict == null){
            pullDownDict = new HashMap<String, Object>();
        }

        if(numberDict == null){
            numberDict = new HashMap<String, Object>();
        }

        if(dateDict == null){
            dateDict = new HashMap<String, Object>();
        }

        if(periodDict == null){
            periodDict = new HashMap<String, Object>();
        }

        if(patternDict == null){
            patternDict = new HashMap<String, Object>();
        }

        if(menuButtonDict == null){
        	menuButtonDict = new HashMap<String, Object>();
        }

        if(iconMenuDict == null){
        	iconMenuDict = new HashMap<String, Object>();
        }


        if(treeDict == null){
        	treeDict = new HashMap<String, Object>();
        }

        if(pagingButtonDict == null){
        	pagingButtonDict = new HashMap<String, Object>();
        }

        if(radioDict == null){
        	radioDict = new HashMap<String, Object>();
        }

        if(checkboxDict == null){
        	checkboxDict = new HashMap<String, Object>();
        }

        if(headerDict == null){
        	headerDict = new HashMap<String, Object>();
        }

        if(barCodeDict == null){
        	barCodeDict = new HashMap<String, Object>();
        }

        Map<String, Object> dict = new HashMap<String, Object>();

        // ボタン定義
        dict = makeDict(root, "buttoninfo");
        for(Map.Entry<String, Object> e : dict.entrySet()) {
            buttonDict.put(e.getKey(), e.getValue());
        }

        // リンク定義
        dict = makeDict(root, "linkinfo");
        for(Map.Entry<String, Object> e : dict.entrySet()) {
            linkDict.put(e.getKey(), e.getValue());
        }

        // メニュー定義
        dict = makeMenuDict(root, "menuinfo");
        for(Map.Entry<String, Object> e : dict.entrySet()) {
            menuDict.put(e.getKey(), e.getValue());
        }

        // 検索メニュー定義
        dict = makeDict(root, "searchmenuinfo");
        for(Map.Entry<String, Object> e : dict.entrySet()) {
            searchMenuDict.put(e.getKey(), e.getValue());
        }

        // 検索ダイアログ定義
        dict = makeDict(root, "searchdialoginfo");
        for(Map.Entry<String, Object> e : dict.entrySet()) {
            searchDialogDict.put(e.getKey(), e.getValue());
        }

        // ソートダイアログ定義
        dict = makeDict(root, "sortdialoginfo");
        for(Map.Entry<String, Object> e : dict.entrySet()) {
            sortDialogDict.put(e.getKey(), e.getValue());
        }

        // テーブル定義
        dict = makeTableDict(root, "tableinfo");
        for(Map.Entry<String, Object> e : dict.entrySet()) {
            tableDict.put(e.getKey(), e.getValue());
        }

        // タブ定義
        dict = makeDict(root, "tabinfo");
        for(Map.Entry<String, Object> e : dict.entrySet()) {
            tabDict.put(e.getKey(), e.getValue());
        }

        //Hidden定義
        dict = makeDict(root, "hiddeninfo");
        for(Map.Entry<String, Object> e : dict.entrySet()) {
            hiddenDict.put(e.getKey(), e.getValue());
        }

        //テキスト定義
        dict = makeDict(root, "textinfo");
        for(Map.Entry<String, Object> e : dict.entrySet()) {
            textDict.put(e.getKey(), e.getValue());
        }

        //テキストエリア定義
        dict = makeDict(root, "textareainfo");
        for(Map.Entry<String, Object> e : dict.entrySet()) {
            textAreaDict.put(e.getKey(), e.getValue());
        }

        //数値定義
        dict = makeDict(root, "numberinfo");
        for(Map.Entry<String, Object> e : dict.entrySet()) {
            numberDict.put(e.getKey(), e.getValue());
        }

        //ラベル定義
        dict = makeLabelDict(root, "labelinfo");
        for(Map.Entry<String, Object> e : dict.entrySet()) {
            labelDict.put(e.getKey(), e.getValue());
        }

        //日付定義
        dict = makeDict(root, "dateinfo");
        for(Map.Entry<String, Object> e : dict.entrySet()) {
            dateDict.put(e.getKey(), e.getValue());
        }

        //日付範囲定義
        dict = makeDict(root, "periodinfo");
        for(Map.Entry<String, Object> e : dict.entrySet()) {
            periodDict.put(e.getKey(), e.getValue());
        }

        //プルダウン定義
        dict = makeDict(root, "pulldowninfo");
        for(Map.Entry<String, Object> e : dict.entrySet()) {
            pullDownDict.put(e.getKey(), e.getValue());
        }

        //パターン定義
        dict = makePatternDict(root, "patterninfo");
        for(Map.Entry<String, Object> e : dict.entrySet()) {
            patternDict.put(e.getKey(), e.getValue());
        }

        // メニューボタン定義
        dict = makeMenuButtonDict(root, "menubuttoninfo");
        for(Map.Entry<String, Object> e : dict.entrySet()) {
            menuButtonDict.put(e.getKey(), e.getValue());
        }

        // アイコンメニュー定義
        dict = makeDict(root, "iconmenuinfo");
        for(Map.Entry<String, Object> e : dict.entrySet()) {
            iconMenuDict.put(e.getKey(), e.getValue());
        }

        // ツリー定義
        dict = makeDict(root, "treeinfo");
        for(Map.Entry<String, Object> e : dict.entrySet()) {
        	treeDict.put(e.getKey(), e.getValue());
        }

        // ページングボタン定義
        dict = makeDict(root, "paginginfo");
        for(Map.Entry<String, Object> e : dict.entrySet()) {
        	pagingButtonDict.put(e.getKey(), e.getValue());
        }

        // ラジオボタン定義
        dict = makeDict(root, "radioinfo");
        for(Map.Entry<String, Object> e : dict.entrySet()) {
        	radioDict.put(e.getKey(), e.getValue());
        }

        // チェックボックス定義
        dict = makeDict(root, "checkboxinfo");
        for(Map.Entry<String, Object> e : dict.entrySet()) {
        	checkboxDict.put(e.getKey(), e.getValue());
        }

        // ヘッダー定義
        dict = makeMenuDict(root, "headerinfo");
        for(Map.Entry<String, Object> e : dict.entrySet()) {
        	headerDict.put(e.getKey(), e.getValue());
        }

        // バーコード入力定義
        dict = makeDict(root, "barcodeinfo");
        for(Map.Entry<String, Object> e : dict.entrySet()) {
        	barCodeDict.put(e.getKey(), e.getValue());
        }
    }

    /**
     * 辞書作成
     * @param root 辞書XMLのルート
     * @param name 作成辞書名
     * @return 作成辞書
     */
    private Map<String, Object> makeDict(Element root, String name) {

        Map<String, Object> dict = new HashMap<String, Object>();

        NodeList methodsNodeList = root.getElementsByTagName(name);

        for (int i = 0; methodsNodeList.getLength() > i; i++) {

            Element emp = (Element) methodsNodeList.item(i);
            String id = emp.getAttribute("id");
            String type = emp.getAttribute("type");

            Map<String, Object> map = new HashMap<String, Object>();
            if (type != null) {
                map.put("type", type);
            }

            for (int j = 0; j < emp.getChildNodes().getLength(); j++) {
                Node item = emp.getChildNodes().item(j);

                if (item.getNodeType() == Node.ELEMENT_NODE) {

                    String key = item.getNodeName();
                    String v = item.getTextContent();
                    map.put(key, v);
                }
            }
            dict.put(id, map);
        }
        return dict;
    }

    /**
     * テーブルタグ辞書作成
     * @param root 辞書XMLのルート
     * @param name 作成辞書名
     * @return 作成辞書
     */
    private  Map<String, Object> makeTableDict(Element root, String name) {
        Map<String, Object> dict = new HashMap<String, Object>();

        NodeList methodsNodeList = root.getElementsByTagName(name);

        for (int i = 0; i < methodsNodeList.getLength(); i++) {
            // tableinfo内の解析

            // tableinfo内のcolum情報格納先
            List<Map<String,Object>> lstColumn = new ArrayList<>();

            Element emtTableInfo = (Element) methodsNodeList.item(i);
            // id
            String id = emtTableInfo.getAttribute("id");

            Map<String, Object> dictBase = new HashMap<String, Object>();

            for (int j = 0; j < emtTableInfo.getChildNodes().getLength(); j++) {
                Node item = emtTableInfo.getChildNodes().item(j);

                if (item.getNodeType() == Node.ELEMENT_NODE) {

                    String key = item.getNodeName();
                    if (MAP_NAME_TABLE_COLUMN.equals(key)) {
                        continue;
                    }

                    String v = item.getTextContent();

                    dictBase.put(key, v);
                }
            }

            NodeList ndlColumn = emtTableInfo.getElementsByTagName(MAP_NAME_TABLE_COLUMN);

            for (int j = 0; j < ndlColumn.getLength(); j++) {
                // column内の解析
                Element emtColumnInfo = (Element) ndlColumn.item(j);
                NodeList ndlColChild = emtColumnInfo.getChildNodes();

                Map<String, Object> mapColumn = new HashMap<String, Object>();

                for (int c = 0; c < ndlColChild.getLength(); c++ ) {
                    Node nodeColumn = ndlColChild.item(c);

                    if (nodeColumn.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }

                    if (nodeColumn.getNodeName().equals("headinfo")) {
                        Map<String, Object> headMap = new HashMap<String, Object>();

                        NamedNodeMap headNodeMap = nodeColumn.getAttributes();

                        for (int index = 0; index < headNodeMap.getLength(); index++) {
                            Node attr = headNodeMap.item(index);  // 属性ノード
                            headMap.put(attr.getNodeName(), attr.getNodeValue());
                        }
                        mapColumn.put(MAP_NAME_TABLE_INFO, headMap);
                    }
                    else if (nodeColumn.getNodeName().equals("colInfo")) {
                        Map<String, Object> colMap = new HashMap<String, Object>();

                        for (int index = 0; index < nodeColumn.getChildNodes().getLength(); index++) {
                            Node col = nodeColumn.getChildNodes().item(index);
                            if (col.getNodeType() == Node.ELEMENT_NODE) {
                                String key = col.getNodeName();
                                String v = col.getTextContent();
                                colMap.put(key, v);
                            }
                        }
                        mapColumn.put(MAP_NAME_COLUMN_INFO, colMap);
                    }
                }
                // columnの情報追加
                lstColumn.add(mapColumn);
            }

            dictBase.put(MAP_NAME_TABLE_COOLUMNS, lstColumn);

            // tableinfoの追加
            dict.put(id, dictBase);
        }
        return dict;
    }

    /**
     * パターン定義辞書作成
     * @param root 辞書XMLのルート
     * @param name 作成辞書名
     * @return 作成辞書
     */
    private  Map<String, Object> makePatternDict(Element root, String name) {
        Map<String, Object> dict = new HashMap<String, Object>();

        NodeList methodsNodeList = root.getElementsByTagName(name);

        for (int i = 0; i < methodsNodeList.getLength(); i++) {
            // patterninfo内の解析
            // patterninfo内のpattern情報格納先
            Map<String,Object> mapColumn = new HashMap<String, Object>();

            Element emtPtnInfo = (Element) methodsNodeList.item(i);
            // id
            String id = emtPtnInfo.getAttribute("id");
            NodeList ndlColumn = emtPtnInfo.getElementsByTagName("pattern");

            for (int j = 0; j < ndlColumn.getLength(); j++) {
                // pattern内の解析
                Element emtColumnInfo = (Element) ndlColumn.item(j);

                String pid = emtColumnInfo.getAttribute("pid");
                NodeList ndlColChild = emtColumnInfo.getChildNodes();
                Map<String, Object> mapNode = new HashMap<String, Object>();

                for (int c = 0; c < ndlColChild.getLength(); c++ ) {
                    Node nodeColumn = ndlColChild.item(c);

                    if (nodeColumn.getNodeType() == Node.ELEMENT_NODE) {

                        String key = nodeColumn.getNodeName();
                        String v = nodeColumn.getTextContent();
                        mapNode.put(key, v);
                    }
                }
                // patternの情報追加
                mapColumn.put(pid, mapNode);
            }
            // patterninfoの追加
            dict.put(id, mapColumn);
        }
        return dict;
    }

    /**
     * メニュー定義辞書作成
     * @param root 辞書XMLのルート
     * @param name 作成辞書名
     * @return 作成辞書
     */
    private  Map<String, Object> makeMenuDict(Element root, String name) {

    	 Map<String, Object> dict = new HashMap<String, Object>();

    	 // menuinfo
         NodeList menuInfoNodeList = root.getElementsByTagName(name);

         // menuinfoの件数毎に、menuinfo内の解析
         for (int i = 0; i < menuInfoNodeList.getLength(); i++) {

             // menuinfo内の要素取得
             Element menuInfoElement = (Element) menuInfoNodeList.item(i);
             // id
             String id = menuInfoElement.getAttribute("id");

             // 1データ分のmenuinfoのマッピング先
             Map<String, Object> dictMenuInfo = new HashMap<String, Object>();

             // menu_item_info以外の要素をマッピング
             for (int j = 0; j < menuInfoElement.getChildNodes().getLength(); j++) {
                 Node item = menuInfoElement.getChildNodes().item(j);

                 if (item.getNodeType() == Node.ELEMENT_NODE) {

                     String key = item.getNodeName();
                     if (MAP_NAME_MENU_ITEM_INFO.equals(key)) {
                         continue;
                     }

                     String v = item.getTextContent();

                     dictMenuInfo.put(key, v);
                 }
             }

             // menu_item_info
             NodeList menuItemInfoNodeList = menuInfoElement.getElementsByTagName(MAP_NAME_MENU_ITEM_INFO);

             // 1データ分のmenu_item_infoのマッピング先
             Map<String, Object> dictMenuItemInfo = new HashMap<String, Object>();

             // menu_item_infoの件数分、menu_item_info内の解析
             for (int j = 0; j < menuItemInfoNodeList.getLength(); j++) {

                 // menu_item_info内の要素取得
                 Element menuItemInfoElement = (Element) menuItemInfoNodeList.item(j);

                 // item_number
                 String itemNumber = menuItemInfoElement.getAttribute(MAP_NAME_ITEM_NUMBER);

                 // 1データ分のmenu_item_info内の要素取得
                 NodeList menuItemInfoChildNodeList = menuItemInfoElement.getChildNodes();
                 Map<String, Object> mapNode = new HashMap<String, Object>();

                 // menu_item_info内の要素毎にマッピング
                 for (int c = 0; c < menuItemInfoChildNodeList.getLength(); c++ ) {
                     Node nodeMenuItemInfo = menuItemInfoChildNodeList.item(c);

                     if (nodeMenuItemInfo.getNodeType() == Node.ELEMENT_NODE) {

                         String key = nodeMenuItemInfo.getNodeName();
                         String v = nodeMenuItemInfo.getTextContent();
                         mapNode.put(key, v);
                     }

                 }

                 //
                 dictMenuItemInfo.put(itemNumber, mapNode);

             }


             dictMenuInfo.put(MAP_NAME_MENU_ITEM_INFO, dictMenuItemInfo);

             // menuinfoの追加
             dict.put(id, dictMenuInfo);
         }
         return dict;
    }

    /**
     * ラベル定義辞書作成
     * @param root 辞書XMLのルート
     * @param name 作成辞書名
     * @return 作成辞書
     */
    private  Map<String, Object> makeLabelDict(Element root, String name) {

    	 Map<String, Object> dict = new HashMap<String, Object>();

    	 // labelinfo
         NodeList labelInfoNodeList = root.getElementsByTagName(name);

         // labelinfoの件数毎に、labelinfo内の解析
         for (int i = 0; i < labelInfoNodeList.getLength(); i++) {

             // labelinfo内の要素取得
             Element labelInfoElement = (Element) labelInfoNodeList.item(i);
             // id
             String id = labelInfoElement.getAttribute("id");

             // 1データ分のlabelinfoのマッピング先
             Map<String, Object> dictLabelInfo = new HashMap<String, Object>();

             // label_item_info以外の要素をマッピング
             for (int j = 0; j < labelInfoElement.getChildNodes().getLength(); j++) {
                 Node item = labelInfoElement.getChildNodes().item(j);

                 if (item.getNodeType() == Node.ELEMENT_NODE) {

                     String key = item.getNodeName();
                     if (MAP_NAME_MENU_ITEM_INFO.equals(key)) {
                         continue;
                     }

                     String v = item.getTextContent();

                     dictLabelInfo.put(key, v);
                 }
             }

             // label_item_info
             NodeList labelItemInfoNodeList = labelInfoElement.getElementsByTagName(MAP_NAME_LABEL_ITEM_INFO);

             // 1データ分の label_item_infoのマッピング先
             Map<String, Object> dictLabelItemInfo = new HashMap<String, Object>();

             // label_item_infoの件数分、label_item_info内の解析
             for (int j = 0; j < labelItemInfoNodeList.getLength(); j++) {

                 // label_item_info内の要素取得
                 Element labelItemInfoElement = (Element) labelItemInfoNodeList.item(j);

                 // item_number
                 String itemNumber = labelItemInfoElement.getAttribute(MAP_NAME_ITEM_NUMBER);

                 // 1データ分のlabel_item_info内の要素取得
                 NodeList labelItemInfoChildNodeList = labelItemInfoElement.getChildNodes();
                 Map<String, Object> mapNode = new HashMap<String, Object>();

                 // label_item_info内の要素毎にマッピング
                 for (int c = 0; c < labelItemInfoChildNodeList.getLength(); c++ ) {
                     Node nodeLabelItemInfo = labelItemInfoChildNodeList.item(c);

                     if (nodeLabelItemInfo.getNodeType() == Node.ELEMENT_NODE) {

                         String key = nodeLabelItemInfo.getNodeName();
                         String v = nodeLabelItemInfo.getTextContent();
                         mapNode.put(key, v);
                     }

                 }
                 //
                 dictLabelItemInfo.put(itemNumber, mapNode);

             }

             dictLabelInfo.put(MAP_NAME_LABEL_ITEM_INFO, dictLabelItemInfo);

             // labelinfoの追加
             dict.put(id, dictLabelInfo);
         }
         return dict;
    }

    /**
     * メニューボタン定義辞書作成
     * @param root 辞書XMLのルート
     * @param name 作成辞書名
     * @return 作成辞書
     */
    @SuppressWarnings("unused")
	private  Map<String, Object> makeMenuButtonDict(Element root, String name) {

    	 Map<String, Object> dict = new HashMap<String, Object>();

    	 // menuinfo
         NodeList menuInfoNodeList = root.getElementsByTagName(name);

         // menuinfoの件数毎に、menuinfo内の解析
         for (int i = 0; i < menuInfoNodeList.getLength(); i++) {

             // menu_item_info情報格納先
             List<Map<String,Object>> lstMenuItemInfo = new ArrayList<>();

             // menuinfo内の要素取得
             Element menuInfoElement = (Element) menuInfoNodeList.item(i);
             // id
             String id = menuInfoElement.getAttribute("id");

             // 1データ分のmenuinfoのマッピング先
             Map<String, Object> dictMenuInfo = new HashMap<String, Object>();

             // menu_item_info以外の要素をマッピング
             for (int j = 0; j < menuInfoElement.getChildNodes().getLength(); j++) {
                 Node item = menuInfoElement.getChildNodes().item(j);

                 if (item.getNodeType() == Node.ELEMENT_NODE) {

                     String key = item.getNodeName();
                     if (MAP_NAME_MENU_BUTTON_ITEM_INFO.equals(key)) {
                         continue;
                     }

                     String v = item.getTextContent();

                     dictMenuInfo.put(key, v);
                 }
             }

             // menu_item_info
             NodeList menuItemInfoNodeList = menuInfoElement.getElementsByTagName(MAP_NAME_MENU_BUTTON_ITEM_INFO);

             // 1データ分のmenu_item_infoのマッピング先
             Map<String, Object> dictMenuItemInfo = new HashMap<String, Object>();

             // menu_item_infoの件数分、menu_item_info内の解析
             for (int j = 0; j < menuItemInfoNodeList.getLength(); j++) {

                 // menu_item_info内の要素取得
                 Element menuItemInfoElement = (Element) menuItemInfoNodeList.item(j);

                 // item_number
                 String itemNumber = menuItemInfoElement.getAttribute(MAP_NAME_BUTTON_ITEM_NUMBER);

                 // 1データ分のmenu_item_info内の要素取得
                 NodeList menuItemInfoChildNodeList = menuItemInfoElement.getChildNodes();
                 Map<String, Object> mapNode = new HashMap<String, Object>();

                 // menu_item_info内の要素毎にマッピング
                 for (int c = 0; c < menuItemInfoChildNodeList.getLength(); c++ ) {
                     Node nodeMenuItemInfo = menuItemInfoChildNodeList.item(c);

                     if (nodeMenuItemInfo.getNodeType() == Node.ELEMENT_NODE) {

                         String key = nodeMenuItemInfo.getNodeName();
                         String v = nodeMenuItemInfo.getTextContent();
                         mapNode.put(key, v);
                     }

                 }

                 //
                 dictMenuItemInfo.put(itemNumber, mapNode);

             }


             dictMenuInfo.put(MAP_NAME_MENU_BUTTON_ITEM_INFO, dictMenuItemInfo);

             // menuinfoの追加
             dict.put(id, dictMenuInfo);
         }
         return dict;
    }

}
