package com.gnomes.system.view;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

import com.gnomes.common.view.BaseFormBean;
import com.gnomes.common.view.IScreenFormBean;

public class TestFormBean extends BaseFormBean implements Serializable, IScreenFormBean {


    private Locale locale;
    private String computerName;
    private Map<Integer, String> checkList;

    @Override
    public String getScreenTitle() {
        return "test_screen_title";
    }

    @Override
    public String getScreenId() {
        return "test_screen_id";
    }

    @Override
    public String getScreenName() {
        return "test_screen_name";
    }

    @Override
    public Locale getUserLocale() {
        return locale;
    }

    @Override
    public void setUserLocale(Locale locale) {
        this.locale = locale == null ? Locale.getDefault() : locale;
    }

    @Override
    public String getComputerName() {
        return computerName;
    }

    @Override
    public void setComputerName(String computerName) {
        this.computerName = computerName;
    }

    @Override
    public void setCheckList(Map<Integer, String> checkList) {
        this.checkList = checkList;
    }

    @Override
    public Map<Integer, String> getCheckList() {
        return checkList;
    }

    @Override
    public String getJsonSaveSearchInfos() {
        return null;
    }

}
