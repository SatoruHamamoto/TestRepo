package com.gnomes.external.command;

import javax.annotation.Priority;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import com.gnomes.common.command.ICommandQualifier;
import com.gnomes.common.command.ServiceBaseCommand;
import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.view.CommandIdConstants;
import com.gnomes.common.view.IServiceFormBean;
import com.gnomes.external.view.S01008ServiceFormBean;
import com.gnomes.system.logic.MailNotice;

/**
 * メール通知処理
 */
/* ========================== MODIFICATION HISTORY ==========================
* Release  Date       ID/Name                   Comment
* --------------------------------------------------------------------------
* R0.01.01 2017/12/15 YJP/H.Yamada              初版
* [END OF MODIFICATION HISTORY]
* ==========================================================================
*/
@RequestScoped
@Alternative
@Priority(CommonConstants.GNOMESINTERCEPTOR_PLATFORM)
@ICommandQualifier(CommandIdConstants.COMMAND_ID_S01008C101)
public class S01008C101 extends ServiceBaseCommand {

    @Inject
    protected S01008ServiceFormBean s01008ServiceFormBean;

    @Inject
    protected MailNotice mailNotice;

    @Override
    public void mainExecute() throws Exception {

        this.mailNotice.notification();

    }

    @Override
    public void preExecute() throws Exception {
        // 処理なし

    }

    @Override
    public void postExecute() throws Exception {
        // 処理なし

    }

    @Override
    public void initCheckList() throws GnomesAppException, Exception {
        // 処理なし

    }

    @Override
    public void validate() throws GnomesAppException {
        // 処理なし

    }

    @Override
    public void setFormBeanForRestore() throws GnomesException {
        // 処理なし

    }

    @Override
    public Object getCommandResponse() {
        return null;
    }

    @Override
    public IServiceFormBean getFormBean() {
        return s01008ServiceFormBean;
    }
}
