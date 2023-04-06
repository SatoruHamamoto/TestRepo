package com.gnomes.common.command;

import javax.annotation.Priority;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.view.CloseWindowServiceFormBean;
import com.gnomes.common.view.IServiceFormBean;

/**
*
* ウィンドウを閉じる 共通コマンド
* <!-- TYPE DESCRIPTION --><pre>
* </pre>
*/
/* ========================== MODIFICATION HISTORY ==========================
* Release  Date       ID/Name                   Comment
* --------------------------------------------------------------------------
* R0.01.01 2017/12/19 YJP/K.Fujiwara            初版
* [END OF MODIFICATION HISTORY]
* ==========================================================================
*/
@RequestScoped
@Alternative
@Priority(CommonConstants.GNOMESINTERCEPTOR_PLATFORM)
@ICommandQualifier("CloseWindowCommand")
public class CloseWindowCommand extends ServiceBaseCommand {

    @Inject
    CloseWindowServiceFormBean closeWindowServiceFormBean;

    @Override
    public IServiceFormBean getFormBean() {
        return closeWindowServiceFormBean;
    }

    @Override
    public Object getCommandResponse() {
        return null;
    }

    @Override
    public void mainExecute() throws Exception {

        this.deleteCidMap(closeWindowServiceFormBean.getWindowId());
        this.deleteWindowId(closeWindowServiceFormBean.getWindowId());
    }

    @Override
    public void preExecute() throws Exception {

    }

    @Override
    public void postExecute() throws Exception {

    }

    @Override
    public void initCheckList() throws GnomesAppException, Exception {

    }

    @Override
    public void validate() throws GnomesAppException {

    }

    @Override
    public void setFormBeanForRestore() throws GnomesException {

    }

}
