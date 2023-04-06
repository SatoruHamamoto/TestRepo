package com.gnomes.system.data;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.gnomes.common.dto.PullDownDto;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.system.entity.InfoUser;
import com.gnomes.system.entity.MstrPerson;
import com.gnomes.system.entity.MstrPersonSecPolicy;

/**
 * システム共通処理用ファンクションビーン
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 -          - / -                       -
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class SystemFunctionBean implements java.io.Serializable {

    @Inject
    transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected transient LogHelper logHelper;

    /** 言語リスト */
    private List<PullDownDto> languageList;

    /** エリアリスト */
    private List<PullDownDto> areaList;

    /** セッション取得エラーメッセージ */
    private String errMessageNoSession;

    /** システムロケールID */
    private String systemLocale;

    /** A320:ユーザアカウントセキュリティポリシー */
    private MstrPersonSecPolicy dataUsrsecPolicy;

    /** A310:ユーザ */
    private MstrPerson dataPerson;

    /** ユーザ情報 */
    private InfoUser dataInfoUser;

    /**
     * 言語リストを取得
     * @return languageList
     */
    public List<PullDownDto> getLanguageList() {
        return languageList;
    }

    /**
     * 言語リストを設定
     * @param languageList 言語リスト
     */
    public void setLanguageList(List<PullDownDto> languageList) {
        this.languageList = languageList;
    }

    /**
     * 言語リストを追加
     * @param languageList 言語リスト
     */
    public void addLanguageList(PullDownDto param) {
        this.languageList.add(param);
    }

    /**
     * エリアリストを取得
     * @return areaList
     */
    public List<PullDownDto> getAreaList() {
        return areaList;
    }

    /**
     * エリアリストを設定
     * @param areaList エリアリスト
     */
    public void setAreaList(List<PullDownDto> areaList) {
        this.areaList = areaList;
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
    public void setMstrPersonSecPolicyList(MstrPersonSecPolicy dataUsrsecPolicy) {
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
     * ユーザ情報を取得
     * @return dataInfoUser
     */
    public InfoUser getInfoUser() {
        return dataInfoUser;
    }

    /**
     * ユーザ情報を設定
     * @param dataInfoUser ユーザ情報
     */
    public void setInfoUser(InfoUser dataInfoUser) {
        this.dataInfoUser = dataInfoUser;
    }


    /**
     * 初期処理
     */
    @PostConstruct
    private void init() {
        this.logHelper.fine(this.logger, null, null, "@PostConstruct: " + this);
        languageList = new ArrayList<PullDownDto>();
        areaList = new ArrayList<PullDownDto>();
    }

    /**
     * 後処理
     */
    @PreDestroy
    private void destroy() {
        this.logHelper.fine(this.logger, null, null, "@PreDestroy: " + this);
    }

}
