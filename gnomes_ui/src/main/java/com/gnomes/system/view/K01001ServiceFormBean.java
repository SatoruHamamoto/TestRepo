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
 * 帳票印字ServiceFormBean
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/03/20 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Named("K01001ServiceFormBean")
@RequestScoped
public class K01001ServiceFormBean implements Serializable, IServiceFormBean {

    /** productCid */
    @Inject
    @RequestParam("productCid")
    private String productCid;

    /** コンピュータ名 */
    private String computerName;

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

    /**
     * 画面タイトルを取得
     * @return 画面タイトル
     */
    @Override
    public String getScreenTitle() {
        return ResourcesHandler.getString(GnomesResourcesConstants.SC02_K01001);
    }

    /**
     * 画面IDを取得
     * @return 画面ID
     */
    @Override
    @ScreenInfo(name="screenId")
    public String getScreenId() {
        return ScreenIdConstants.SCID_K01001;
    }

    /**
     * 画面名を取得
     * @return 画面名
     */
    @Override
    @ScreenInfo(name="screenName")
    public String getScreenName(){
        return ResourcesHandler.getString(GnomesResourcesConstants.SC02_K01001);
    }


    @Override
    public Locale getUserLocale() {
        return null;
    }

    @Override
    public void setUserLocale(Locale locale) {

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

    @Override
    public RequestServiceFileInfo getRequestServiceFileInfo() {
        return null;
    }

}
