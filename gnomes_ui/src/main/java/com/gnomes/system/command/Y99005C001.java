package com.gnomes.system.command;

import java.util.List;
import java.util.Objects;

import javax.annotation.Priority;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import com.gnomes.common.command.ICommandQualifier;
import com.gnomes.common.command.ServiceBaseCommand;
import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.data.GnomesSystemBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.view.CommandIdConstants;
import com.gnomes.common.view.IServiceFormBean;
import com.gnomes.rest.service.GnomesWebServiceDataInput;
import com.gnomes.system.view.Y99005ServiceFormBean;

/**
 * マスタデータキャッシュグループ リセットコマンド
 * <pre>
 * <!-- TYPE DESCRIPTION -->
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/04/25 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
@Alternative
@Priority(CommonConstants.GNOMESINTERCEPTOR_PLATFORM)
@ICommandQualifier(CommandIdConstants.COMMAND_ID_Y99005C001)
public class Y99005C001 extends ServiceBaseCommand {

    /** マスタデータキャッシュグループサービスフォームビーン */
    @Inject
    protected Y99005ServiceFormBean y99005ServiceFormBean;

    /** アプリケーションビーン */
    @Inject
    protected GnomesSystemBean gnomesSystemBean;


    @Override
    public IServiceFormBean getFormBean() {
        return this.y99005ServiceFormBean;
    }

    @Override
    public Object getCommandResponse() {
        return null;
    }

    @Override
    @ErrorHandling
    public void mainExecute() throws Exception {

        // リクエストパラメータ取得
        GnomesWebServiceDataInput input =
                (GnomesWebServiceDataInput) super.requestContext.getServiceRequest();

        List<String> paramList = input.getParamList();

        if (!Objects.isNull(paramList)) {
            for (int i = 0; i < paramList.size(); i++) {
                // マスタデータキャッシュグループリセット
                this.gnomesSystemBean.getGnomesSystemModel().resetCashGroup(paramList.get(i));
            }
        }
        //全マスタ定義情報読込み
        this.gnomesSystemBean.getGnomesSystemModel().readAllMstrEntity();

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
