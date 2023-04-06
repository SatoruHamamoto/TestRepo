package com.gnomes.common.command;

import com.gnomes.common.view.IServiceFormBean;

/**
 * サービス用コマンドインターフェイス。
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/08/02 YJP/30022467               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public interface IServiceCommand extends ICommand {

    public void setupCommand();

    public IServiceFormBean getFormBean();

    public boolean commonValidate(IServiceFormBean formBean);

    public boolean validateResultCheck(IServiceFormBean formBean);

	/**
	 * コマンド返却値取得
	 * @return コマンド返却
	 */
	public Object getCommandResponse();

}
