package com.gnomes.common.data;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import com.gnomes.common.command.RequestServiceFileInfo;
import com.gnomes.common.view.IServiceFormBean;
import com.gnomes.system.data.IScreenPrivilegeBean;
import com.gnomes.system.data.PartsPrivilegeResultInfo;

@Named("RemoteConnectFormBean")
@RequestScoped
public class RemoteConnectFormBean implements Serializable, IServiceFormBean, IScreenPrivilegeBean {

    @Override
    public String getScreenTitle() {
        return null;
    }

    @Override
    public String getScreenId() {
        return null;
    }

    @Override
    public String getScreenName() {
        return null;
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
    public List<String> getProcessCode() {
        return null;
    }

    @Override
    public List<String> getSiteCode() {
        return null;
    }

    @Override
    public List<String> getOrderProcessCode() {
        return null;
    }

    @Override
    public List<String> getWorkProcessCode() {
        return null;
    }

    @Override
    public void setPartsPrivilegeResultInfo(List<PartsPrivilegeResultInfo> partsPrivilegeResultInfo) {

    }

    @Override
    public List<PartsPrivilegeResultInfo> getPartsPrivilegeResultInfo() {
        return null;
    }

    @Override
    public RequestServiceFileInfo getRequestServiceFileInfo() {
        return null;
    }

}
