package com.gnomes.common.view;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.picketbox.util.StringUtil;

import com.gnomes.common.command.RequestParamMap;
import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums.TagHiddenKind;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.search.data.MstSearchInfo;
import com.gnomes.common.search.data.SearchSetting;

/**
 * FormBeanの基底クラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/19 YJP/K.Fujiwara             初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Named("BaseFormBean")
@RequestScoped
public class BaseFormBean {

    /** セッションビーン */
    @Inject
    protected GnomesSessionBean gnomesSessionBean;

    /** windowId */
    @Inject
    @RequestParamMap
    private String windowId;

    /**
     * 検索条件マスター情報マップ（レスポンスのみ）
     *      key:    辞書ID
     *      value:  MstSearchInfo
     */
    private Map<String, MstSearchInfo> mstSearchInfoMap;

    /**
     * リクエストとレスポンス 検索条件マップ
     *      key:    辞書ID
     *      value:  SearchSetting
     */
    @Inject
    @RequestParamMap
    private Map<String, SearchSetting> searchSettingMap;

    /**
     * 上書き表示用のテーブルタイトルマップ
     *      key1:    テーブルID
     *      key2:    カラム名
     *      value:   表示値
     */
	private Map<String, Map<String, String>> tableDispHeaders = new HashMap<>();

    /** ページトークン */
    private String pageToken;

    /**
     * ローカルストレージ
     */
    @Inject
    @RequestParamMap
    private Map<String, String>  localStorage;

    /**
     * カスタムタグの非表示区分
     */
    private Map<String,TagHiddenKind> tagHiddenKindMap;

    /**
     * 動的画面ID
     */
    @Inject
    @RequestParamMap
    private String dynamicScreenId;

    /**
     * 呼出画面Parametar
     */
    @Inject
    @RequestParamMap
    private String screenTransitionMode;

    /**
     * 動的画面名ID
     */
    @Inject
    @RequestParamMap
    private String dynamicScreenNameId;


    /**
     * 動的タイトルID
     */
    @Inject
    @RequestParamMap
    private String dynamicTitleId;

    /**
     * 編集有フラグ
     */
    @Inject
    @RequestParamMap
    private String inputChangeFlag;

    /**
     * 動的画面IDを取得
     * @return dynamicScreenId 動的画面ID
     */
    public String getDynamicScreenId() {
        return dynamicScreenId;
    }


    /**
     * 動的画面IDの設定
     * @param dynamicScreenId セットする 動的画面ID
     */
    public void setDynamicScreenId(String dynamicScreenId) {
        this.dynamicScreenId = dynamicScreenId;
    }

    /**
     * 呼出画面Parameterを取得
     * @return screenTransitionMode 呼出画面Parameter
     */
    public String getScreenTransitionMode() {
        return screenTransitionMode;
    }


    /**
     * 呼出画面Parameterの設定
     * @param screenTransitionMode セットする 呼出画面Parameter
     */
    public void setScreenTransitionMode(String screenTransitionMode) {
        this.screenTransitionMode = screenTransitionMode;
    }
    
    /**
     * 動的画面名IDを取得
     * @return dynamicScreenNameId 動的画面名ID
     */
    public String getDynamicScreenNameId() {
        return dynamicScreenNameId;
    }


    /**
     * 動的画面名IDを設定
     * @param dynamicScreenNameId セットする 動的画面名ID
     */
    public void setDynamicScreenNameId(String dynamicScreenNameId) {
        this.dynamicScreenNameId = dynamicScreenNameId;
    }


    /**
     * 動的タイトルIDを取得
     * @return dynamicTitleId 動的タイトルID
     */
    public String getDynamicTitleId() {
        return dynamicTitleId;
    }


    /**
     * 動的タイトルIDを設定
     * @param dynamicTitleId セットする 動的タイトルID
     */
    public void setDynamicTitleId(String dynamicTitleId) {
        this.dynamicTitleId = dynamicTitleId;
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
     * @return tagHiddenKindDictIdMap;
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
     * @return windowId
     */
    public String getWindowId() {
        return windowId;
    }

    /**
     * @param windowId セットする windowId
     */
    public void setWindowId(String windowId) {
        this.windowId = windowId;
    }

    /**
     * ページトークンを取得
     * @return pageToken
     */
    public String getPageToken() {
        return pageToken;
    }

    /**
     * ページトークンを設定
     * @param pageToken
     */
    public void setPageToken(String pageToken) {
        this.pageToken=pageToken;
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
     * @return localStorage
     */
    public Map<String, String> getLocalStorage() {
        return localStorage;
    }

	/**
	 * @param localStorage セットする localStorage
	 */
    public void setLocalStorage(Map<String, String> localStorage) {
        this.localStorage = localStorage;
    }

	/**
	 * クライアントから送信された、テーブルのチェック情報をリストに反映
	 *
	 * @param checks       クライアントから送信された、テーブルのチェック情報
	 * @param tableList    テーブル
	 * @param propertyName プロパティ名
	 * @throws GnomesAppException
	 */
    @TraceMonitor
    @ErrorHandling
    protected void setRequestCheckBoxTable(Integer[] checks, List<?> tableList,
            String propertyName) throws GnomesAppException {

    	//吐き出すテーブルがNULLならば何もしない
        if (tableList != null) {
            // checkがnullにかかわらず、一度tableListを全てチェックなしに設定
            for (Object item : tableList) {
                setObjectProperty(item, propertyName,
                        CommonConstants.DTO_CHECK_BOX_NO_CHECKED_VALUE);
            }
            // checkがnullでない（１件でもチェックが入っている）場合のみ
            // tableListにcheckの内容を入れる
            if ( checks != null){
                for (int i = 0; i < checks.length; i++) {
                    int index = checks[i];
                    setObjectProperty(tableList.get(index), propertyName,
                            CommonConstants.DTO_CHECK_BOX_CHECKED_VALUE);
                }
            }

        }
    }

    /**
     * クライアントから送信された、テーブルの入力情報をリストに反映
     *
     * @param requestDatas クライアントから送信された、テーブルの入力情報
     * @param tableList テーブル
     * @param propertyName プロパティ名
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    protected void setRequestDataTable(Object[] requestDatas, List<?> tableList,
            String propertyName) throws GnomesAppException {
        if (requestDatas != null && tableList != null) {
            for (int i = 0; i < requestDatas.length; i++) {
                setObjectProperty(tableList.get(i), propertyName, requestDatas[i]);
            }
        }
    }

    /**
     * オブジェクトのプロパティに値を設定
     *
     * @param dstBean 更新オブジェクト
     * @param propertyName プロパティ名
     * @param value 値
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    protected void setObjectProperty(Object dstBean, String propertyName,
            Object value) throws GnomesAppException {
        PropertyDescriptor dstProperties;
        try {
            dstProperties = new PropertyDescriptor(
                    propertyName, dstBean.getClass());

            Method setter = dstProperties.getWriteMethod(); //setter取得

            if (value instanceof ZonedDateTime) {
                // ZonedDateTimeはDateに変換して設定
                Date zd2date = Date.from(((ZonedDateTime)value).toInstant());
                setter.invoke(dstBean, zd2date); //プロパティ値をセット
            } else {
                setter.invoke(dstBean, value); //プロパティ値をセット
            }

        } catch (IntrospectionException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {

            // ME01.0163：「プロパティ設定でエラーが発生しました。詳細についてはログを確認してください。オブジェクト：{0} 、プロパティ：{1}」
            GnomesAppException gae = new GnomesAppException(e);
            gae.setMessageNo(GnomesMessagesConstants.ME01_0163);

            Object[] errParam = {
                    dstBean.getClass().getName(),
                    propertyName
            };
            gae.setMessageParams(errParam);

            throw gae;

        }
    }



    /**
     * FormBeanリストアの後処理
     */
    public void setFormBeanForRestoreAfter() throws GnomesAppException {
        // 必要に応じてオーバーライド
    }

    /**
     * 定義上の画面ID取得
     *   将来の拡張があった場合のため、基底クラスでコントロールできるように基底を経由する
     * @param orgScreenId 画面ID
     * @return 画面ID
     */
    protected String getOrgScreenId(String orgScreenId) {
    	return orgScreenId;
    }


    /**
     * 画面ID取得
     * @param screenId 画面ID
     * @return 画面ID
     */
    protected String getScreenId(String screenId) {
        String dynamicScreenId = this.getDynamicScreenId();
        if (StringUtil.isNullOrEmpty(dynamicScreenId)) {
            return screenId;
        }
        return dynamicScreenId;
    }

    /**
     * 画面名取得
     * @param screenNameId 画面名リソースID
     * @return 画面名
     */
    protected String getScreenName(String screenNameId) {
        String nameId = this.getDynamicScreenNameId();
        if (StringUtil.isNullOrEmpty(nameId)) {
            nameId = screenNameId;
        }
        return ResourcesHandler.getString(nameId, gnomesSessionBean.getUserLocale());
    }


    /**
     * 画面タイトル取得
     * @param screenTitleId 画面タイトルリソースID
     * @return
     */
    protected String getScreenTitle(String screenTitleId) {
        String titleId = this.getDynamicTitleId();
        if (StringUtil.isNullOrEmpty(titleId)) {
            titleId = screenTitleId;
        }
        return ResourcesHandler.getString(titleId, gnomesSessionBean.getUserLocale());
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("[@RequestScoped] FormBean post construct : " + this.getClass().getName());
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("[@RequestScoped] FormBean pre destroy : " + this.getClass().getName());
    }
}
