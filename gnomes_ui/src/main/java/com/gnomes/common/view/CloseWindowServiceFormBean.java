package com.gnomes.common.view;

import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import com.gnomes.common.command.RequestServiceFileInfo;
import com.gnomes.common.interceptor.ScreenInfo;

/**
 * ウィンドウ閉じる処理用 フォームビーン
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/02/15 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Named("CloseWindowServiceFormBean")
@RequestScoped
public class CloseWindowServiceFormBean extends BaseFormBean implements IServiceFormBean  {

    @Override
    public String getScreenTitle() {
        return "";
    }

    @Override
    @ScreenInfo(name="screenId")
    public String getScreenId() {
        return "";
    }

    @Override
    @ScreenInfo(name="screenName")
    public String getScreenName() {
        return "";
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
    public RequestServiceFileInfo getRequestServiceFileInfo() {
        return null;
    }

}
