package com.gnomes.common.search.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gnomes.common.search.SearchInfoController.OrderType;

/**
 * 検索共通  順序情報クラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/03/01 YJP/30022467              初版
 * R0.01.01 2019/05/27 YJP/S.Hamamoto            順序情報のindexを廃止、カラム名で対応する
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderingInfo {

	/** 順序マスター情報Index ←　順序マスター情報("mstOrdering")のIndex*/

	/** テーブル内　表示有無 */
	private boolean isHiddenTable;

	/** ソート順 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer orderNum;

	/** ソート必須設定 */
	private Boolean sortRequired;

	/** ソート方向 */
	private OrderType orderType;

	/** カラム名 */
	private String columnId;


	/**
	 * @return isHiddenTable
	 */
	public boolean isHiddenTable() {
		return isHiddenTable;
	}

	/**
	 * @return orderNum
	 */
	public Integer getOrderNum() {
		return orderNum;
	}

	/**
	 * @return orderType
	 */
	public OrderType getOrderType() {
		return orderType;
	}

	/**
	 * @return columnName
	 */
	public String getColumnId() {
		return columnId;
	}

	/**
	 * @param isHiddenTable セットする isHiddenTable
	 */
	public void setHiddenTable(boolean isHiddenTable) {
		this.isHiddenTable = isHiddenTable;
	}

	/**
	 * @param orderNum セットする orderNum
	 */
	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}

	/**
	 * @param orderType セットする orderType
	 */
	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}


	/**
	 * @param カラム名をセットする columnId
	 */
	public void setColumnId(String columnName) {
		this.columnId = columnName;
	}

	/**
	 * ソート必須かどうかを確認する
	 * @return true:必須 false:必須でない
	 */
    public Boolean getSortRequired()
    {
        return sortRequired;
    }

    /**
     * ソート必須かどうかを設定すr
     *
     * @param sortRequired true:必須 false:必須でない
     */
    public void setSortRequired(Boolean sortRequired)
    {
        this.sortRequired = sortRequired;
    }

}
