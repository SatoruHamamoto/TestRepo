package com.gnomes.system.command;

import javax.annotation.Priority;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import com.gnomes.common.command.ICommandQualifier;
import com.gnomes.common.command.IExportTableCommand;
import com.gnomes.common.command.IScreenCommand;
import com.gnomes.common.command.ScreenBaseCommand;
import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.exportdata.ExportDataTableDef;
import com.gnomes.common.view.CommandIdConstants;
import com.gnomes.common.view.IScreenFormBean;
import com.gnomes.common.view.ResourcePathConstants;
import com.gnomes.system.view.Y99002FormBean;

/**
 *
 * テーブル一覧エクスポートコマンド
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/08/29 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
@Alternative
@Priority(CommonConstants.GNOMESINTERCEPTOR_PLATFORM)
@ICommandQualifier(CommandIdConstants.COMMAND_ID_Y99002C002)
public class Y99002C002 extends ScreenBaseCommand
        implements IScreenCommand, IExportTableCommand {

    @Inject
    Y99002FormBean y99002FormBean;

    /**
     * コンストラクター
     */
    public Y99002C002() {
        // ページトークン更新なし
        setTokenUpdateFlgFalse();
    }

    @Override
    public void mainExecute() throws Exception {
        // 処理なし

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
    public IScreenFormBean getFormBean() {
        return y99002FormBean;
    }

    @Override
    public void setDefaultForward() {
        responseContext.setForward(ResourcePathConstants.RESOURCE_PATH_DOWNLOAD);
    }

    @Override
    public ExportDataTableDef getExportDataTableDefinition() {
        return gnomesSessionBean.getExportDataTableDef();
    }

}
