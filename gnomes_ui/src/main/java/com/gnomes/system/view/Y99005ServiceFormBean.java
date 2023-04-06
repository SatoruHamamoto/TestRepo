package com.gnomes.system.view;

import java.io.Serializable;
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
 * マスタデータキャッシュグループサービスフォームビーン
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/04/25 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Named("Y99005ServiceFormBean")
@RequestScoped
public class Y99005ServiceFormBean implements Serializable, IServiceFormBean {

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
        return ResourcesHandler.getString(GnomesResourcesConstants.SC02_Y99005);
    }

    @Override
    @ScreenInfo(name="screenId")
    public String getScreenId() {
        return ScreenIdConstants.SCID_Y99005;
    }

    @Override
    @ScreenInfo(name="screenName")
    public String getScreenName() {
        return ResourcesHandler.getString(GnomesResourcesConstants.SC02_Y99005);
    }

    @Override
    public Map<Integer,String> getCheckList() {
        return this.checkList;
    }

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