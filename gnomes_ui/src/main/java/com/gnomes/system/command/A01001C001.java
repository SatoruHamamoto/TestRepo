package com.gnomes.system.command;

import javax.annotation.Priority;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import com.gnomes.common.command.ICommandQualifier;
import com.gnomes.common.command.IScreenCommand;
import com.gnomes.common.command.LogicFactory;
import com.gnomes.common.command.ScreenBaseCommand;
import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.view.CommandIdConstants;
import com.gnomes.common.view.IScreenFormBean;
import com.gnomes.common.view.ResourcePathConstants;
import com.gnomes.system.data.A01001FunctionBean;
import com.gnomes.system.model.A01001Model;
import com.gnomes.system.view.A01001FormBean;


/**
 *
 * ログイン画面 画面表示
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/12/26 YJP/A.Oomori               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
@Alternative
// 以下の@Priorityが正解。コンテンツがある為、強制的に先に表示するようにPriorityを変更。コマンドIDもコンテンツに合わせている。
@Priority(CommonConstants.GNOMESINTERCEPTOR_PLATFORM)
@ICommandQualifier(CommandIdConstants.COMMAND_ID_A01001C001)
//以下の@Priorityは基盤優先する場合。コンテンツがある為、強制的に先に表示するようにPriorityを変更。コマンドIDもコンテンツに合わせている。
//@Priority(CommonConstants.GNOMESINTERCEPTOR_JOBCUSTOMIZE)
//@ICommandQualifier(CommandIdConstants.COMMAND_ID_LOGIN_CLIENT)
public class A01001C001 extends ScreenBaseCommand implements IScreenCommand {

    // ロジックファクトリー
    @Inject
    transient LogicFactory logicFactory;

    /**
     * ログイン画面 ビーン
     */
    @Inject
    A01001FormBean a01001FormBean;

    /**
     * ログイン ファンクションビーン
     */
    @Inject
    A01001FunctionBean a01001FunctionBean;

    /**
     * ログイン画面 モデル
     */
    @Inject
    A01001Model a01001Model;

    /**
     * デフォルト・コンストラクタ
     */
    public A01001C001() {

    }

    /**
     * ログイン画面 画面表示
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    @Override
    public void mainExecute() throws GnomesAppException {

        // 現セッションのクリア
        if(gnomesSessionBean.getUserId() != null){
            gnomesSessionBean = logicFactory.createSessionBean();
        }
        // ログイン.画面表示データ取得
        a01001Model.getDispData();

        // A01001FunctionBean → A01001FormBean
        a01001FormBean.setFormBean(a01001FunctionBean);

    }

    @TraceMonitor
    @ErrorHandling
    @Override
    public void initCheckList() {
        // 処理なし
    }

    /**
     * FormBean
     * @return IFormBean
     */
    @Override
    public IScreenFormBean getFormBean() {
        return a01001FormBean;
    }

    @Override
    public void setFormBeanForRestore() {
        // 処理なし

    }

    /**
     * デフォルトフォワード先設定
     */
    @Override
    public void setDefaultForward() {
        responseContext.setForward(ResourcePathConstants.RESOURCE_PATH_A01001);


    }

    @Override
    @TraceMonitor
    @ErrorHandling
    public void preExecute() throws Exception {
        // 処理なし

    }

    @Override
    @TraceMonitor
    @ErrorHandling
    public void postExecute() throws Exception {
        // 処理なし

    }

    /**
     * validate
     * 追加のValidate処理がある場合に実行
     * エラーがある場合、responseContextにメッセージエラー情報を設定する
     */
    @Override
    @TraceMonitor
    @ErrorHandling
    public void validate() throws GnomesAppException {
        // 処理なし

    }

}
