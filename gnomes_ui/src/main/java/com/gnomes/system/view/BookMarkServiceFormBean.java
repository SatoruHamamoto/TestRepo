package com.gnomes.system.view;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.gnomes.common.command.RequestParamMap;
import com.gnomes.common.command.RequestServiceFileInfo;
import com.gnomes.common.interceptor.ScreenInfo;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.view.BaseFormBean;
import com.gnomes.common.view.IServiceFormBean;
import com.gnomes.common.view.ScreenIdConstants;
import com.gnomes.system.data.BookMarkFunctionBean;

/**
 * ブックマークサービス フォームビーン
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 -          - / -                     -
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Named("BookMarkServiceFormBean")
@RequestScoped
public class BookMarkServiceFormBean extends BaseFormBean implements Serializable, IServiceFormBean {

    /** ロガー */
    @Inject
    protected transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected transient LogHelper logHelper;

    /** BookMarkFunctionBean */
    @Inject
    protected BookMarkFunctionBean functionBean;

    /** コンピュータ名 */
    private String computerName;

    /** 画面ID */
    @Inject
    @RequestParamMap(value = "bookmarkScreenId")
    private String bookmarkScreenId;

    // カスタムタグの変更時の対応
    /**
     * ユーザロケールを取得
     * @return userLocale
     */
    @Override
    public Locale getUserLocale() {
        return null;
    }

    // カスタムタグの変更時の対応
    /**
     * ユーザロケールを設定
     * @param locale ユーザロケール
     */
    @Override
    public void setUserLocale(Locale locale) {

    }

    /**
     * cidを取得
     * @return cid
     */
    public String getCid() {
        return functionBean.getCid();
    }

    /**
     * インポートファイル取得値を取得
     * @return null
     */
    @Override
    public RequestServiceFileInfo getRequestServiceFileInfo() {
        return null;
    }

    /** 入力項目チェックリスト */
    private Map<Integer,String> checkList;

    /**
     * 入力項目チェックリストを取得
     * @return checkList
     */
    @Override
    public Map<Integer,String> getCheckList() {
        return this.checkList;
    }

    /**
     * 入力項目チェックリストを設定
     * @param checkList 入力項目チェックリスト
     */
    @Override
    public void setCheckList(Map<Integer,String> checkList) {
        this.checkList = checkList;

    }

    /**
     * 画面IDを取得
     * @return bookmarkScreenId
     */
    public String getBookmarkScreenId() {
        return this.bookmarkScreenId;
    }

    /**
     * 画面IDを設定
     * @param bookmarkScreenId 画面ID
     */
    public void setBookmarkScreenId(String bookmarkScreenId) {
        this.bookmarkScreenId = bookmarkScreenId;
    }

    /**
     * 画面タイトルを取得
     * @return 画面タイトル
     */
    @Override
    public String getScreenTitle(){
        return ResourcesHandler.getString(GnomesResourcesConstants.SC02_Y99003);
    }

    /**
     * 画面IDを取得
     * @return 画面ID
     */
    @Override
    @ScreenInfo(name="screenId")
    public String getScreenId(){
        return ScreenIdConstants.SCID_Y99003;
    }

    /**
     * 画面名を取得
     * @return 画面名
     */
    @Override
    @ScreenInfo(name="screenName")
    public String getScreenName(){
        return ResourcesHandler.getString(GnomesResourcesConstants.SC01_Y99003);
    }

    /**
     * コンピュータ名を取得
     * @return computerName
     */
    @Override
    public String getComputerName() {
        return this.computerName;
    }

    /**
     * コンピュータ名を設定
     * @param computerName コンピュータ名
     */
    @Override
    public void setComputerName(String computerName) {
        this.computerName = computerName;
    }

    /** 初期処理 */
    @PostConstruct
    private void init() {
        // 以下の行は画面ID、画面名を CotainerRequest に設定するために必須のため削除しないこと
        this.logHelper.fine(this.logger, null, null, "@PostConstruct: " + this + " ScreenId: " + this.getScreenId() + "ScreenName: " + this.getScreenName());
    }

    /** 後処理 */
    @PreDestroy
    private void destroy() {
        this.logHelper.fine(this.logger, null, null, "@PreDestroy: " + this);
    }

}
