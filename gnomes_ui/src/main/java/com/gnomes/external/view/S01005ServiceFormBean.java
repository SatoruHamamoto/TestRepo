package com.gnomes.external.view;

import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.gnomes.common.command.RequestParam;
import com.gnomes.common.command.RequestServiceFileInfo;
import com.gnomes.common.interceptor.ScreenInfo;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.view.IServiceFormBean;
import com.gnomes.common.view.ScreenIdConstants;

/**
 * ファイル送受信サービスビーン
 *
 */
@Named("S01005ServiceFormBean")
@RequestScoped
public class S01005ServiceFormBean implements java.io.Serializable, IServiceFormBean {

    /** productCid */
    @Inject
    @RequestParam("productCid")
    private String productCid;

    /**
     * @return productCid
     */
    public String getProductCid() {
        return productCid;
    }

    /**
     * @param productCid セットする productCid
     */
    public void setProductCid(String productCid) {
        this.productCid = productCid;
    }

    /** ユーザロケール */
    private Locale userLocale;

    /** 入力項目チェックリスト */
    private Map<Integer,String> checkList;

    @Override
    public String getScreenTitle() {
        return ResourcesHandler.getString(GnomesResourcesConstants.SC02_S01005);
    }

    @Override
    @ScreenInfo(name="screenId")
    public String getScreenId() {
        return ScreenIdConstants.SCID_S01005;
    }

    @Override
    @ScreenInfo(name="screenName")
    public String getScreenName() {
        return ResourcesHandler.getString(GnomesResourcesConstants.SC01_S01005);
    }

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

    @Override
    public Locale getUserLocale() {
        return userLocale;
    }

    @Override
    public void setUserLocale(Locale locale) {
        this.userLocale = locale;

    }

    @Override
    public String getComputerName() {
        return null;
    }

    @Override
    public void setComputerName(String computerName) {

    }

    @Override
    public RequestServiceFileInfo getRequestServiceFileInfo() {
        return null;
    }

}