package com.gnomes.common.command;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Priority;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums.DoingClassMethodDiv;
import com.gnomes.common.data.BaseFunctionBean;
import com.gnomes.common.data.CommandData;
import com.gnomes.common.data.CommandScreenData;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.GnomesTransactional;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.view.BaseFormBean;
import com.gnomes.common.view.IFormBean;
import com.gnomes.common.view.IServiceFormBean;

/**
*
* サービス共通コマンド
* <!-- TYPE DESCRIPTION --><pre>
* </pre>
*/
/* ========================== MODIFICATION HISTORY ==========================
* Release  Date       ID/Name                   Comment
* --------------------------------------------------------------------------
* R0.01.01 2018/01/19 YJP/K.Fujiwara            初版
* [END OF MODIFICATION HISTORY]
* ==========================================================================
*/
@RequestScoped
@Alternative
@Priority(CommonConstants.GNOMESINTERCEPTOR_PLATFORM)
@ICommandQualifier(CommonConstants.SERVICE_COMMON_COMMAND)
public class ServiceCommonCommand extends ServiceBaseCommand {

    @Inject
    protected CommonCommand commonCommand;

    protected BaseFormBean baseFormBean;

    protected BaseFunctionBean baseFunctionBean;

    protected CommandData commandData;

    protected Object responseBean;

    protected GnomesAppException postConstructException;

    /** 初期処理 */
//    @PostConstruct
    public void setupCommand() {
        //      if (!FacesContext.getCurrentInstance().isPostback()) {
        //      }

        try {
            commandData = commonCommand
                    .getCommandData(requestContext.getCommandId());

            String formBeanName = null;
            String functionBeanName = null;

            CommandScreenData commandScreenData = null;

            if (commandData.getScreenId() != null) {
                commandScreenData = commonCommand
                    .getCommandScreenData(commandData.getScreenId());
            }

            if (commandScreenData != null) {
                formBeanName = commandScreenData.getCommonFormBeanName();
                functionBeanName = commandScreenData
                        .getCommonFunctionBeanName();
            }

            if (formBeanName == null) {
                formBeanName = commandData.getServiceFormBeanName();
            }
            if (functionBeanName == null) {
                functionBeanName = commandData.getServiceFunctionBeanName();
            }

            // 共通FormBeanを取得する。
            baseFormBean = commonCommand.getCDIInstance(formBeanName);

            // 共通画面情報.FunctionBeanクラス名が未設定でない場合
            if (functionBeanName != null) {
                // 共通FunctionBeanを取得
                baseFunctionBean = commonCommand
                        .getCDIInstance(functionBeanName);
            }

            if (commandData.getServiceResponseBeanName() != null) {
                // ResponseBeanを作成
                Class<?> clazz;
                try {
                    clazz = Class
                            .forName(commandData.getServiceResponseBeanName());
                    responseBean = clazz.newInstance();

                } catch (ClassNotFoundException | InstantiationException
                        | IllegalAccessException e) {

                    // ME01.0157：「インスタンス取得でエラーが発生しました。詳細についてはログを確認してください。取得クラス：{0} 」
                    GnomesAppException gae = new GnomesAppException(e);
                    gae.setMessageNo(GnomesMessagesConstants.ME01_0157);

                    Object[] errParam = {
                            commandData.getServiceResponseBeanName()
                    };
                    gae.setMessageParams(errParam);

                    throw gae;
                }
            }

        } catch (GnomesAppException e) {
            postConstructException = e;
        }

    }

    @Override
    public IServiceFormBean getFormBean() {
        return (IServiceFormBean) baseFormBean;
    }

    @Override
    public Object getCommandResponse() {
        return responseBean;
    }

    /**
     * トランザクション管理有無
     * @return
     */
    @Override
    public boolean isTransactional() {
        return commandData.isTransactional();
    }

    /**
     * 共通メイン処理（トランザクションあり）
     */
    @TraceMonitor
    @ErrorHandling
    @Override
    @GnomesTransactional
    public void mainExecute() throws Exception {
        this.mainFunction();
    }


    /**
     * 共通メイン処理（トランザクションなし）
     */
    @TraceMonitor
    @ErrorHandling
    @Override
    public void mainExecuteNoTransactional() throws Exception {
        this.mainFunction();
    }

    /**
     * 共通メイン処理
     * @throws Exception
     */
    @TraceMonitor
    @ErrorHandling
    public void mainFunction() throws Exception {

        // FormBean(入力情報)をFunctionBeanに保存
        setFunctionBeanFromForm();

        // モデル実行（クラス名.メソッド名）
        doModel();

        // ビジネスロジック実行（クラス名.メソッド名）
        doBusinessLogic();

        // FunctionBean → CommandResponse
        setCommandResponseFromFunction();

    }

    @Override
    public void preExecute() throws Exception {
        // 処理なし
    }

    @Override
    public void postExecute() throws Exception {
        // 処理なし
    }

    /**
     * バリデーションチェック項目設定
     */
    @TraceMonitor
    @ErrorHandling
    @Override
    public void initCheckList() throws GnomesAppException, Exception {
        if (commandData.getCheckList() != null) {
            // 検証対象リストの設定
            Map<Integer, String> checkList = new HashMap<Integer, String>();
            int seq = 0;

            for (String resId : commandData.getCheckList()) {
                checkList.put(++seq, resId);
            }

            ((IFormBean) baseFormBean).setCheckList(checkList);
        }

    }

    @Override
    public void validate() throws GnomesAppException {
        // 処理なし
    }

    @Override
    public void setFormBeanForRestore() throws GnomesException {
        // 処理なし
    }

    /**
    * ページトークンチェックフラグを取得
    * @return tokenCheckFlg
    */
    @Override
    public boolean getTokenCheckFlg() {
        return commandData.isTokenCheckFlg();
    }

    /**
     * FunctionBean設定（FormBean→FunctionBean)
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void setFunctionBeanFromForm() throws GnomesAppException {

        // 入力項目(FormBean(入力情報)をFunctionBeanに保存)
        Map<String, String> map = commandData.getMappingFunctionBeanFromForm();
        if (map != null) {
            commonCommand.mappingBean(baseFunctionBean, baseFormBean, map);
        }
    }

    /**
     * モデル実行
     * @throws GnomesAppException
     */
    public void doModel() throws GnomesAppException {
        if (commandData.getDoModelClassMethod() != null) {
            commonCommand.doClassMethod(commandData.getDoModelClassMethod(),
                    DoingClassMethodDiv.MODEL, requestContext);
        }
    }

    /**
     * ビジネスロジック実行
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void doBusinessLogic() throws GnomesAppException {

        if (commandData.getDoBusinessClassMethod() != null) {
            Object businessBean = null;

            // BusinessBean作成
            if (commandData.getBusinessBeanName() != null) {
                businessBean = commonCommand
                        .getCDIInstance(commandData.getBusinessBeanName());
            }

            // BusinessBeanマッピング
            if (businessBean != null && commandData
                    .getMappingBusinessBeanFromFunction() != null) {
                commonCommand.mappingBean(businessBean, baseFunctionBean,
                        commandData.getMappingBusinessBeanFromFunction());
            }

            // ビジネスロジック実行
            commonCommand.doClassMethod(commandData.getDoBusinessClassMethod(),
            		 DoingClassMethodDiv.BUSINESS_LOGIC, requestContext);

            // FunctionBeanマッピング
            if (businessBean != null && commandData
                    .getMappingFunctionBeanFromBusiness() != null) {
                commonCommand.mappingBean(baseFunctionBean, businessBean,
                        commandData.getMappingFunctionBeanFromBusiness());
            }
        }
    }

    /**
     * ResponseBean設定（FunctionBean→ResponseBean)
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public void setCommandResponseFromFunction() throws GnomesAppException {

        Map<String, String> map = commandData
                .getMappingBeanFromFunction();
        if (map != null) {
            commonCommand.mappingBean(responseBean, baseFunctionBean, map);
        }

    }

    /**
     * @return postConstructException
     */
    public GnomesAppException getPostConstructException() {
        return postConstructException;
    }

    /**
     * @param postConstructException セットする postConstructException
     */
    public void setPostConstructException(
            GnomesAppException postConstructException) {
        this.postConstructException = postConstructException;
    }

}
