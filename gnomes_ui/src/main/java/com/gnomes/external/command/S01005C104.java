package com.gnomes.external.command;

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
import com.gnomes.external.logic.FileTransferCallBatchlet;
import com.gnomes.external.view.S01005ServiceFormBean;
import com.gnomes.rest.service.GnomesWebServiceDataInput;

/**
 * 受信処理 実行サービス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/20 YJP/I.Shibasaka           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
@Alternative
@Priority(CommonConstants.GNOMESINTERCEPTOR_PLATFORM)
@ICommandQualifier(CommandIdConstants.COMMAND_ID_S01005C104)
public class S01005C104 extends ServiceBaseCommand {

    @Inject
    S01005ServiceFormBean s01005ServiceFormBean;

    @Inject
    FileTransferCallBatchlet fileTransferCallBatchlet;

    @Override
    public IServiceFormBean getFormBean() {
        return s01005ServiceFormBean;
    }

    @Override
    public Object getCommandResponse() {
        return null;
    }

    @Override
    public void mainExecute() throws Exception {
        GnomesWebServiceDataInput batParam = (GnomesWebServiceDataInput) requestContext.getServiceRequest();
        //String externalTargetCode = batParam.getParamList();
        List<String> paramList = batParam.getParamList();
        String externalTargetCode = paramList.get(0);
        Boolean dupCheck = batParam.getDupCheck();
        // recvProc
        fileTransferCallBatchlet.recvProcJbatch(externalTargetCode, dupCheck);

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
