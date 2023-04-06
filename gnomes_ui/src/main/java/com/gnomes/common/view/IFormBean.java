package com.gnomes.common.view;

import java.util.Locale;
import java.util.Map;

import com.gnomes.common.interceptor.ScreenInfo;
/**
 * Interface for FormBean
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/12/05 YJP/H.Gojo                  初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public interface IFormBean {

    /**
     * 画面タイトルを取得する
     * @return 画面タイトル
     */
    public String getScreenTitle();

    /**
     * 画面IDを取得する
     * @return 画面ID
     */
    public String getScreenId();

    public default String getOrgScreenId() {
        return null;
    }

    /**
     * 画面名を取得する
     * @return 画面名
     */
    public String getScreenName();

    /**
     * ユーザ言語を取得する
     * @return ユーザ言語
     */
    public Locale getUserLocale();

    /**
     * ユーザ言語を設定する
     * @param locale 言語
     */
    public void setUserLocale(Locale locale);

    /**
     * コンピュータ名を取得する
     * @return コンピュータ名
     */
    public String getComputerName();

    /**
     * コンピュータ名を設定する
     * @param computerName コンピュータ名
     */
    public void setComputerName(String computerName);

    /**
     * 入力項目チェックリストを設定する
     * @param checkList 入力項目チェックリスト
     */
    public void setCheckList(Map<Integer,String> checkList);

    /**
     * 入力項目チェックリストを取得する
     * @return 入力項目チェックリスト
     */
    public Map<Integer,String> getCheckList();

}
