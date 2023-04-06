package com.gnomes.system.data;

import java.util.List;

import com.gnomes.common.dto.PullDownDto;

/**
 * JSON データ用のクラス定義（メニューサービス）
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/12/19 YJP/A.Oomori               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */

public class MenuInfo {

    /**
     * ユーザID
     */
    private String userId;
    /**
     * コンピュータ名
     */
    private String computerName;
    /**
     * エリア名
     */
    private String areaName;
    /**
     * サイト名
     */
    private String siteName;
    /**
     * 呼出処理
     */
    private String callProcess;
    /**
     * プルダウン情報
     */
    private List<PullDownDto> pullDownInfo;
    /**
     * 選択値
     */
    private String selectPullDownValue;
    /**
     * 選択名
     */
    private String selectPullDownName;
    /**
     * メッセージ情報
     */
    private String message;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getComputerName() {
        return computerName;
    }

    public void setComputerName(String computerName) {
        this.computerName = computerName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getCallProcess() {
        return callProcess;
    }

    public void setCallProcess(String callProcess) {
        this.callProcess = callProcess;
    }

    public List<PullDownDto> getPullDownInfo() {
        return pullDownInfo;
    }

    public void setPullDownInfo(List<PullDownDto> pullDownInfo) {
        this.pullDownInfo = pullDownInfo;
    }

    public String getSelectPullDownValue() {
        return selectPullDownValue;
    }

    public void setSelectPullDownValue(String selectPullDownValue) {
        this.selectPullDownValue = selectPullDownValue;
    }

    public String getSelectPullDownName() {
        return selectPullDownName;
    }

    public void setSelectPullDownName(String selectPullDownName) {
        this.selectPullDownName = selectPullDownName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
