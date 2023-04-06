package com.gnomes.system.view;

import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.gnomes.common.command.RequestParam;
import com.gnomes.common.interceptor.ScreenInfo;
import com.gnomes.common.view.IScreenFormBean;

/**
 * インポートファイルエラー ダウンロード フォームビーン
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/08/22 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
@Named("Y99002FormBean")
public class Y99002FormBean implements java.io.Serializable, IScreenFormBean {


    /**
     * 画面ID
     */
    @Inject
    @RequestParam("screenId")
    private String screenId;

    /**
     * 画面名
     */
    @Inject
    @RequestParam("screenName")
    private String screenName;


    @Override
    public String getScreenTitle() {
        return null;
    }

    @ScreenInfo(name="screenId")
    @Override
    public String getScreenId() {
        return screenId;
    }

    @ScreenInfo(name="screenName")
    @Override
    public String getScreenName() {
        return screenName;
    }

    @Override
    public Locale getUserLocale() {
        return null;
    }

    @Override
    public void setUserLocale(Locale locale) {

    }

    @Override
    public String getComputerName() {
        return null;
    }

    @Override
    public void setComputerName(String computerName) {

    }

    @Override
    public void setCheckList(Map<Integer, String> checkList) {

    }

    @Override
    public Map<Integer, String> getCheckList() {
        return null;
    }

    @Override
    public String getJsonSaveSearchInfos() {
        return null;
    }

}
