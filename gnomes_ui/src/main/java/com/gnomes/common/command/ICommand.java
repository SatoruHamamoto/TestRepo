package com.gnomes.common.command;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.system.data.IScreenPrivilegeBean;

/**
 * 画面用コマンドインターフェイス。
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/07/01 YJP/K.Gotanda             初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public interface ICommand {

    /**
     * execute
     * @throws GnomesAppException
     * @throws Exception
     */
    public void mainExecute() throws Exception;

    /**
     * execute
     * @throws GnomesAppException
     * @throws Exception
     */
    public void mainExecuteNoTransactional() throws Exception;

    /**
     * トランザクション管理有無
     * @return
     */
    public boolean isTransactional();


    /**
     * 前処理
     * @throws Exception
     */
    public void preExecute() throws Exception;

    /**
     * 後処理
     * @throws Exception
     */
    public void postExecute() throws Exception;

    /**
     * チェック項目設定処理
     * initCheckList
     * @throws GnomesAppException
     * @throws Exception
     */
    public void initCheckList() throws GnomesAppException, Exception;

    /**
     * validate
     * @throws GnomesAppException
     */
    public void validate() throws GnomesAppException;

    public boolean getTokenCheckFlg();

    public boolean getTokenUpdateFlg();

    public void setScreenInfo();

    public void setFormBeanForRestore() throws GnomesException;

    public boolean judgePersonsLicenseCheck(IScreenPrivilegeBean formBean, String screenId) throws GnomesException;




}
