package com.gnomes.system.data;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.gnomes.common.dto.PullDownDto;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.system.entity.MstrPerson;
import com.gnomes.system.entity.MstrPersonSecPolicy;

/**
 * ファンクションビーン
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 -          - / -                       -
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class A01001FunctionBean implements java.io.Serializable {

    @Inject
    transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected transient LogHelper logHelper;

    @Inject
    private Conversation conversation;

    /** 拠点リスト */
    private List<PullDownDto> siteList;

    /** セッション取得エラーメッセージ */
    private String errMessageNoSession;

    /** システムロケールID */
    private String systemLocale;

    /** A320:ユーザアカウントセキュリティポリシー */
    private MstrPersonSecPolicy dataUsrsecPolicy;

    /** A310:ユーザ */
    private MstrPerson dataPerson;

    /** ロケールリスト */
    private List<PullDownDto> localeList;

    /** 端末リスト */
    private List<PullDownDto> computerList;

    /** 端末拠点情報 */
    private Map<String, String> computerInfoMap;

    /**
     * conversationスコープの開始
     */
    public void begin(){
        if (conversation.isTransient()) {
            conversation.begin();
        }
    }

    /**
     * conversationスコープの終了
     */
    public void end(){
        if (!conversation.isTransient()) {
            conversation.end();
        }
    }

    /**
     * 拠点リストを取得
     * @return siteList
     */
    public List<PullDownDto> getSiteList() {
        return siteList;
    }

    /**
     * 拠点リストを設定
     * @param siteList 拠点リスト
     */
    public void setSiteList(List<PullDownDto> siteList) {
        this.siteList = siteList;
    }

    /**
     * セッション取得エラーメッセージを取得
     * @return errMessageNoSession
     */
    public String getErrMessageNoSession() {
        return errMessageNoSession;
    }

     /**
      * セッション取得エラーメッセージを設定
      * @param errMessageNoSession セッション取得エラーメッセージを設定
      */
    public void setErrMessageNoSession(String errMessageNoSession) {
        this.errMessageNoSession = errMessageNoSession;
    }

    /**
     * システムロケールIDを取得
     * @return systemLocale
     */
    public String getSystemLocale() {
        return systemLocale;
    }

    /**
     * システムロケールIDを設定
     * @param systemLocale システムロケールID
     */
    public void setSystemLocale(String systemLocale) {
        this.systemLocale = systemLocale;
    }

    /**
     * A320:ユーザアカウントセキュリティポリシーを取得
     * @return dataUsrsecPolicy
     */
    public MstrPersonSecPolicy getMstrUsrsecPolicy() {
        return dataUsrsecPolicy;
    }

    /**
     * A320:ユーザアカウントセキュリティポリシーを設定
     * @param dataUsrsecPolicy A320:ユーザアカウントセキュリティポリシー
     */
    public void setMstrUsrsecPolicy(MstrPersonSecPolicy dataUsrsecPolicy) {
        this.dataUsrsecPolicy = dataUsrsecPolicy;
    }

    /**
     * A310:ユーザを取得
     * @return dataPerson
     */
    public MstrPerson getMstrPerson() {
        return dataPerson;
    }

    /**
     * A310:ユーザを設定
     * @param dataPerson A310:ユーザ
     */
    public void setMstrPerson(MstrPerson dataPerson) {
        this.dataPerson = dataPerson;
    }

    /**
     * ロケールリストを取得
     * @return localeList
     */
    public List<PullDownDto> getLocaleList() {
        return localeList;
    }

    /**
     * ロケールリストを設定
     * @param localeList ロケールリスト
     */
    public void setLocaleList(List<PullDownDto> localeList) {
        this.localeList = localeList;
    }

    /**
     * 端末リストを取得
     * @return computerList
     */
    public List<PullDownDto> getComputerList() {
        return computerList;
    }

    /**
     * 端末リストを設定
     * @param computerList 端末リスト
     */
    public void setComputerList(List<PullDownDto> computerList) {
        this.computerList = computerList;
    }

    /**
     * 端末拠点情報を取得
     * @return computerInfoMap
     */
	public Map<String, String> getComputerInfoMap() {
		return computerInfoMap;
	}

	/**
	 * 端末拠点情報を設定
	 * @param computerInfoMap 端末拠点情報
	 */
	public void setComputerInfoMap(Map<String, String> computerInfoMap) {
		this.computerInfoMap = computerInfoMap;
	}

    /**
     * 初期処理
     */
    @PostConstruct
    private void init() {
        this.logHelper.fine(this.logger, null, null, "@PostConstruct: " + this);
    }

    /**
     * 後処理
     */
    @PreDestroy
    private void destroy() {
        this.logHelper.fine(this.logger,null, null, "@PreDestroy: " + this);
    }

}
