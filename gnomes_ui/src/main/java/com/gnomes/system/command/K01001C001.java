package com.gnomes.system.command;

import java.util.List;

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
import com.gnomes.rest.service.GnomesWebServiceDataInput;
import com.gnomes.system.logic.PrintOutCallBatchlet;
import com.gnomes.system.view.K01001ServiceFormBean;

/**
 * 帳票印字処理
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
@RequestScoped
@Alternative
@Priority(CommonConstants.GNOMESINTERCEPTOR_PLATFORM)
@ICommandQualifier(CommandIdConstants.COMMAND_ID_K01001C001)
public class K01001C001 extends ServiceBaseCommand {

    @Inject
    protected K01001ServiceFormBean k01001ServiceFormBean;

    /** 帳票印字 バッチレット */
    @Inject
    protected PrintOutCallBatchlet printOutCallBatchlet;

    @Override
    public IServiceFormBean getFormBean() {
        return this.k01001ServiceFormBean;
    }

    @Override
    public Object getCommandResponse() {
        return null;
    }

    @Override
    public void mainExecute() throws Exception {

        GnomesWebServiceDataInput batParam = (GnomesWebServiceDataInput) requestContext.getServiceRequest();
        List<String> paramList = batParam.getParamList();
        String localeId = paramList.get(0);
        Boolean dupCheck = batParam.getDupCheck();
        this.printOutCallBatchlet.printOutJbatch(localeId, dupCheck);

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
