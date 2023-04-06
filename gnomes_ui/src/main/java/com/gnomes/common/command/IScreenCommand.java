package com.gnomes.common.command;

import com.gnomes.common.view.IScreenFormBean;

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
public interface IScreenCommand extends ICommand {

    public void setupCommand();

    public IScreenFormBean getFormBean();

    public boolean commonValidate(IScreenFormBean formBean);

    public boolean validateResultCheck(IScreenFormBean formBean);

    public void setDefaultForward();

}
