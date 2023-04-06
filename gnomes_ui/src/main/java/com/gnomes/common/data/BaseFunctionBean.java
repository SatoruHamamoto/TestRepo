package com.gnomes.common.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import com.gnomes.common.constants.CommonEnums.TagHiddenKind;
import com.gnomes.common.search.data.MstSearchInfo;
import com.gnomes.common.search.data.SearchSetting;

/**
 * FounctionBeanの基底クラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/19 YJP/K.Fujiwara             初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@ConversationScoped
public class BaseFunctionBean implements java.io.Serializable {

    @Inject
    private Conversation conversation;

    /** アプリケーションBean */
    @Inject
    protected GnomesSystemBean gnomesSystemBean;

    /** セッションBean
     * CIDマップを操作するため
     */
    @Inject
    protected GnomesSessionBean gnomesSessionBean;

    /**
     * 延命措置用に何回呼び出したかを数えるため
     */
    private int lifeCounter = 0;

    /**
     * 検索条件マスター情報マップ（レスポンスのみ）
     *      key:    辞書ID
     *      value:  MstSearchInfo
     */
    private Map<String, MstSearchInfo> mstSearchInfoMap = new HashMap<String, MstSearchInfo>();

    /**
     * リクエストとレスポンス 検索条件マップ
     *      key:    辞書ID
     *      value:  SearchSetting
     */
    private Map<String, SearchSetting> searchSettingMap = new HashMap<String, SearchSetting>();

    /**
     * 上書き表示用のテーブルタイトルマップ
     *      key1:    テーブルID
     *      key2:    カラム名
     *      value:   表示値
     */
	private Map<String, Map<String, String>> tableDispHeaders = new HashMap<>();

    /**
     * 遷移タイプ
     */
    private String screenTransitionType;

    /**
     * フォワード先コマンドID
     */
    private String forwardCommandId;

    /**
     * ConversationのIDを保持
     */
    private String conversationId;

    /**
     * フォワード先コマンドFormBean引継パラメータマッピング情報
     *      key:    遷移先FormBeanフィールド名
     *      value:  遷移元FormBeanフィールド名
     */
    private Map<String, String> transitionParameterMap = new HashMap<String, String>();

    /**
     * カスタムタグの非表示区分
     */
    private Map<String,TagHiddenKind> tagHiddenKindMap = new HashMap<String, TagHiddenKind>();

    /**
     * 遷移先画面ID
     */
    private String transitionScreenID;


    /**
     * 遷移先画面名ID
     */
    private String transitionScreenNameId;

    /**
     * 遷移先タイトルID
     */
    private String transitionTitleId;

    /**
     * 編集有フラグ
     */
    private String inputChangeFlag;

    /**
     * 遷移先の画面情報を設定
     * @param screenId 画面ID
     * @param screenNameId 画面名Id
     * @param titleId タイトルId
     */
    public void setTransitionScreen(String screenId, String screenNameId, String titleId) {
        this.transitionScreenID = screenId;
        this.transitionScreenNameId = screenNameId;
        this.transitionTitleId = titleId;
    }

    /**
     * 遷移先の画面情報をクリア
     */
    public void clearTransitionScreen() {
        this.transitionScreenID = null;
        this.transitionScreenNameId = null;
        this.transitionTitleId = null;
    }

    /** 遷移先画面IDを取得
	 * @return transitionScreenID 遷移先画面ID
	 */
	public String getTransitionScreenID() {
		return transitionScreenID;
	}

	/**
	 * 遷移先画面IDを設定<br>
	 * 遷移先の画面IDを動的に設定時に設定する
	 * @param transitionScreenID セットする 遷移先画面ID
	 */
	public void setTransitionScreenID(String transitionScreenID) {
		this.transitionScreenID = transitionScreenID;
	}


	/**
	 * 動的画面名IDを取得
	 * @return transitionScreenNameId 動的画面名ID
	 */
	public String getTransitionScreenNameId() {
		return transitionScreenNameId;
	}


	/**
	 * 動的画面名IDを設定<br>
	 * 遷移先の画面名を動的に設定時に画面名のリソースIDを設定する
	 * @param transitionScreenNameId セットする 動的画面名ID
	 */
	public void setTransitionScreenNameId(String transitionScreenNameId) {
		this.transitionScreenNameId = transitionScreenNameId;
	}


	/**
	 * 遷移先タイトルIDを取得
	 * @return transitionTitleId 遷移先タイトルID
	 */
	public String getTransitionTitleId() {
		return transitionTitleId;
	}


	/**
	 * 遷移先タイトルIDを設定<br>
	 * 遷移先のタイトルを動的に設定時にタイトルのリソースIDを設定する
	 * @param transitionTitleId セットする 遷移先タイトルID
	 */
	public void setTransitionTitleId(String transitionTitleId) {
		this.transitionTitleId = transitionTitleId;
	}


	/**
     * 編集有フラグを取得
     * @return inputChangeFlag 編集有フラグ
     */
    public String getInputChangeFlag() {
        return inputChangeFlag;
    }


    /**
     * 編集有フラグを設定
     * @param inputChangeFlag 編集有フラグ
     */
    public void setInputChangeFlag(String inputChangeFlag) {
        this.inputChangeFlag = inputChangeFlag;
    }

    /**
     * カスタムタグの非表示区分を取得
     *
     * @return tagHiddenKindMap;
     */
    public Map<String,TagHiddenKind> getTagHiddenKindMap() {
        return tagHiddenKindMap;
    }

   /**
    * カスタムタグの非表示区分を設定
    *
    * @param tagHiddenKindMap カスタムタグの非表示区分
    */
    public void setTagHiddenKindMap(Map<String,TagHiddenKind> tagHiddenKindMap) {
        this.tagHiddenKindMap = tagHiddenKindMap;
    }

    /**
     * カスタムタグの非表示区分を設定
     *
     * @param key キー
     * @param tagHiddenKind 非表示区分
     */
    public void setTagHiddenKind(String key, TagHiddenKind tagHiddenKind) {
        if (tagHiddenKindMap.containsKey(key)) {
            tagHiddenKindMap.replace(key, tagHiddenKind);
        } else {
            tagHiddenKindMap.put(key, tagHiddenKind);
        }
    }

    /**
     * カスタムタグ辞書IDの非表示区分を設定をクリア
     *
     * @param key キー
     */
    public void clearTagHiddenKind(String key) {
        tagHiddenKindMap.remove(key);
    }

    /**
     * 会話のIDを取得する。
     * Conversation#beginを実行すると、ユニークIDが生成される。
     * 会話スコープを継続するときは、このIDをリクエストパラメータに含める。
     * @return CID
     */
    public String getCid() {
        return conversation.getId();
    }

    /**
     * 保持会話IDの取得
     * @return
     */
    public String getConversationId() {
        return conversationId;
    }

    /**
     * 保持会話IDの設定
     * @param conversationId
     */
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    /**
     * conversationスコープの開始
     */
    public boolean begin(){
        if (conversation.isTransient()) {
            conversation.begin();
            conversation.setTimeout(gnomesSystemBean.getConversionTimeOut());
            this.setConversationId(conversation.getId());
            return true;
        }
        return false;
    }

    /**
     * conversationスコープの終了
     */
    public String end(){
        String result = null;
        if (!conversation.isTransient()) {
            result = conversation.getId();
            conversation.end();
        }
        return result;
    }


    /**
     * @return mstSearchInfoMap
     */
    public Map<String, MstSearchInfo> getMstSearchInfoMap() {
        return mstSearchInfoMap;
    }

    /**
     * @param mstSearchInfoMap セットする mstSearchInfoMap
     */
    public void setMstSearchInfoMap(Map<String, MstSearchInfo> mstSearchInfoMap) {
        this.mstSearchInfoMap = mstSearchInfoMap;
    }


    /**
     * @return searchSettingMap
     */
    public Map<String, SearchSetting> getSearchSettingMap() {
        return searchSettingMap;
    }

    /**
     * @param searchSettingMap セットする searchSettingMap
     */
    public void setSearchSettingMap(Map<String, SearchSetting> searchSettingMap) {
        this.searchSettingMap = searchSettingMap;
    }

	/**
	 * @return tableDispHeaders
	 */
	public Map<String, Map<String, String>> getTableDispHeaders() {
		return tableDispHeaders;
	}

	/**
	 * @param tableDispHeaders セットする tableDispHeaders
	 */
	public void setTableDispHeaders(Map<String, Map<String, String>> tableDispHeaders) {
		this.tableDispHeaders = tableDispHeaders;
	}

	/**
	 * @return screenTransitionType
	 */
    public String getScreenTransitionType() {
        return screenTransitionType;
    }

    /**
     * @param screenTransitionType セットする screenTransitionType
     */
    public void setScreenTransitionType(String screenTransitionType) {
        this.screenTransitionType = screenTransitionType;
    }

    /**
     * @return forwardCommandId
     */
    public String getForwardCommandId() {
        return forwardCommandId;
    }

    /**
     * @param forwardCommandId セットする forwardCommandId
     */
    public void setForwardCommandId(String forwardCommandId) {
        this.forwardCommandId = forwardCommandId;
    }

	/**
	 * フォワード先コマンドFormBean引継パラメータマッピング情報を取得
	 * @return transitionParameterMap
	 */
	public Map<String, String> getTransitionParameterMap() {
		return transitionParameterMap;
	}

	/**
	 * フォワード先コマンドFormBean引継パラメータマッピング情報を設定
	 * @param transitionParameterMap
	 */
	public void setTransitionParameterMap(Map<String, String> transitionParameterMap) {
		this.transitionParameterMap = transitionParameterMap;
	}

    /**
     * クリアー処理
     */
    public void clear() {
        /** 継承先でクリアー処理を記載 */
        this.mstSearchInfoMap = new HashMap<String, MstSearchInfo>();
        this.searchSettingMap = new HashMap<String, SearchSetting>();
    }

    /**
     * 検索実施済みか否かを取得
     * @param tableId
     * @return Boolean(true：検索実施済み、false：検索未実施)
     */
    public Boolean getIsSearchFlag(String tableId) {

    	// 検索実施フラグを取得
    	Integer searchFlag = this.searchSettingMap.get(tableId).getSearchFlag();

    	// フラグONの場合、true
    	if(searchFlag != null && searchFlag == 1){
    		return true;
    	}
    	return false;
    }
    /**
     * FunctionBean生成時の初期処理
     */
    @PostConstruct
    public void postConstruct() {
        System.out.println("[@ConversationScoped] FunctionBean post construct : " + this.toString());

        // FunctionBeanのクラス名を保持するリストにエントリ
        this.gnomesSessionBean.addFunctionBean(this.toString(),this.getClass().getName());
    }

    /**
     * FunctionBean破棄時の終了処理
     */
    @PreDestroy
    public void preDestroy() {
        System.out.println("[@ConversationScoped] FunctionBean pre destroy : " + this.toString());
        List<CidMap> cidMapList = this.gnomesSessionBean.getCidMapList();

        // CIDマップリストを破棄する
        if(Objects.nonNull(cidMapList)){
            gnomesSessionBean.deleteCidMapList(this.getConversationId());
        }

        //FunctionBeanのクラス名を保持するリストから削除
        this.gnomesSessionBean.removeFunctionBean(this.toString(),this.getClass().getName());

    }

    /**
     * 延命措置用にカウンタをアップさせる
     */
    public void countUp() {
        lifeCounter++;
    }

    /**
     * デバッグ用に出力
     */
    public void DebugOutput() {
        //this.begin();
        System.out.println("================================");
        System.out.println("< Functin Bean Dump >  " + this.getClass().getName());
        System.out.println("|    instance = " + this.toString());
        System.out.println("|    hashCode = " + this.hashCode());
        System.out.println("|    count    = " + this.lifeCounter);
        System.out.println("|    conversationId    = " + this.conversationId);
        System.out.println("|    conversationId    = " + this.conversation.getId());
        System.out.println("|    timeout           = " + this.conversation.getTimeout());
        System.out.println("|    isTransient       = " + this.conversation.isTransient());
        System.out.println("================================");



    }

}
