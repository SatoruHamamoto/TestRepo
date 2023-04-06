package com.gnomes.common.data;

/**
 * メニュー項目情報
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/04/06 YJP/A.Oomori             初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class MenuItemInfoBean {

    /**
     * メニュー項目name
     */
    private String itemName;

    /**
     * メニュー項目リソースID
     */
    private String itemResourceId;

    /**
     * メニュー項目コマンドID
     */
    private String itemCommandId;

    /**
     * メニュー項目onclick
     */
    private String itemOnclick;

    /**
     * メニュー項目間区切りの有無
     */
    private boolean isSeparate;

    /**
     * スタイルシートの追加設定
     */
    private String addStyle;

    /**
     * メニュー項目権限ID
     */
    private String itemButtonId;

    /**
     * 接続領域ごと操作可否
     */
    private String operationConnectionArea;


    public MenuItemInfoBean() {
    }

	/**
	 * @return itemName
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * @param itemName セットする itemName
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * @return itemResourceId
	 */
	public String getItemResourceId() {
		return itemResourceId;
	}

	/**
	 * @param itemResourceId セットする itemResourceId
	 */
	public void setItemResourceId(String itemResourceId) {
		this.itemResourceId = itemResourceId;
	}

	/**
	 * @return itemCommandId
	 */
	public String getItemCommandId() {
		return itemCommandId;
	}

	/**
	 * @param itemCommandId セットする itemCommandId
	 */
	public void setItemCommandId(String itemCommandId) {
		this.itemCommandId = itemCommandId;
	}

	/**
	 * @return itemOnclick
	 */
	public String getItemOnclick() {
		return itemOnclick;
	}

	/**
	 * @param itemOnclick セットする itemOnclick
	 */
	public void setItemOnclick(String itemOnclick) {
		this.itemOnclick = itemOnclick;
	}

	/**
	 * @return isSeparate
	 */
	public boolean getIsSeparate() {
		return isSeparate;
	}

	/**
	 * @param isSeparate セットする isSeparate
	 */
	public void setIsSeparate(boolean isSeparate) {
		this.isSeparate = isSeparate;
	}

	/**
	 * @return addStyle
	 */
	public String getAddStyle() {
		return addStyle;
	}

	/**
	 * @param addStyle セットする addStyle
	 */
	public void setAddStyle(String addStyle) {
		this.addStyle = addStyle;
	}

	/**
	 * @return itemButtonId
	 */
	public String getItemButtonId() {
		return itemButtonId;
	}

	/**
	 * @param itemButtonId セットする itemButtonId
	 */
	public void setItemButtonId(String itemButtonId) {
		this.itemButtonId = itemButtonId;
	}

	/**
	 * @return operationConnectionArea
	 */
	public String getOperationConnectionArea() {
		return operationConnectionArea;
	}

	/**
	 * @param operationConnectionArea セットする operationConnectionArea
	 */
	public void setOperationConnectionArea(String operationConnectionArea) {
		this.operationConnectionArea = operationConnectionArea;
	}


}
