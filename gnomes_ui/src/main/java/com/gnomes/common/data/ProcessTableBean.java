package com.gnomes.common.data;

import java.util.Map;

/**
 * 工程端末一覧情報bean
 */
public class ProcessTableBean {

	/** 列位置 */
	private String colPosition;

	/** 行位置 */
	private String rowPosition;

	/** 行順番 */
	private String rowNumber;

	/** 行数（1列あたり）*/
	private String rows;

	/** 列数（1列あたり）*/
	private String cols;

	/** 列数（1列あたり）*/
	private String rowLineNum;

	private boolean rowEmpty;

	private boolean colEmpty;

	/** ヘッダースタイル */
	private String headerStyle;

	/** カラムスタイル */
	private String columnStyle;

	/** リソースID */
	private String resourceId;

	/** データありか空か*/
	private boolean empty;;

	/** 一覧情報 */
	private Map<String, Object> tableInfo;

	/** ヘッダー追加指定クラス */
	private String headerAddClass;

 	/** カラム追加指定クラス */
 	private String columnAddClass;

 	/**
 	 * 列位置を取得
 	 * @return
 	 */
	public String getColPosition() {
		return colPosition;
	}

	/**
	 * 列位置を設定
	 * @param colPosition
	 */
	public void setColPosition(String colPosition) {
		this.colPosition = colPosition;
	}

	/**
	 * 行位置を取得
	 * @return
	 */
	public String getRowPosition() {
		return rowPosition;
	}

	/**
	 * 行位置を設定
	 * @param rowPosition
	 */
	public void setRowPosition(String rowPosition) {
		this.rowPosition = rowPosition;
	}

	/**
	 * 行順番を取得
	 * @return
	 */
	public String getRowNumber() {
		return rowNumber;
	}

	/**
	 * 行順番を設定
	 * @param rowNumber
	 */
	public void setRowNumber(String rowNumber) {
		this.rowNumber = rowNumber;
	}

	/**
	 * 工程端末一覧を取得
	 * @return
	 */
	public Map<String, Object> getTableInfo() {
		return tableInfo;
	}

	/**
	 * 工程端末一覧を設定
	 * @param tableInfo
	 */
	public void setTableInfo(Map<String, Object> tableInfo) {
		this.tableInfo = tableInfo;
	}

	public String getResourceId() {
		return this.resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getRows() {
		return rows;
	}

	public void setRows(String rows) {
		this.rows = rows;
	}

	public String getCols() {
		return cols;
	}

	public void setCols(String cols) {
		this.cols = cols;
	}

	public boolean isEmpty() {
		return empty;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
	}

	public String getRowLineNum() {
		return rowLineNum;
	}

	public void setRowLineNum(String rowLineNum) {
		this.rowLineNum = rowLineNum;
	}

	public boolean isRowEmpty() {
		return rowEmpty;
	}

	public void setRowEmpty(boolean rowEmpty) {
		this.rowEmpty = rowEmpty;
	}

	public boolean isColEmpty() {
		return colEmpty;
	}

	public void setColEmpty(boolean colEmpty) {
		this.colEmpty = colEmpty;
	}

	/**
	 * ヘッダースタイルを取得
	 * @return headerStyle
	 */
	public String getHeaderStyle() {
		return headerStyle;
	}

	/**
	 * ヘッダースタイルを設定
	 * @param headerStyle
	 */
	public void setHeaderStyle(String headerStyle) {
		this.headerStyle = headerStyle;
	}

	/**
	 * カラムスタイルを取得
	 * @return columnStyle
	 */
	public String getColumnStyle() {
		return columnStyle;
	}

	/**
	 * カラムスタイルを設定
	 * @param columnStyle
	 */
	public void setColumnStyle(String columnStyle) {
		this.columnStyle = columnStyle;
	}

	/**
	 * ヘッダー追加指定クラスを取得
	 * @return headerAddClass
	 */
	public String getHeaderAddClass() {
		return headerAddClass;
	}

	/**
	 * ヘッダー追加指定クラスを設定
	 * @param headerAddClass
	 */
	public void setHeaderAddClass(String headerAddClass) {
		this.headerAddClass = headerAddClass;
	}

	/**
	 * カラム追加指定クラスを取得
	 * @return columnAddClass
	 */
	public String getColumnAddClass() {
		return columnAddClass;
	}

	/**
	 * カラム追加指定クラスを設定
	 * @param columnAddClass
	 */
	public void setColumnAddClass(String columnAddClass) {
		this.columnAddClass = columnAddClass;
	}

}
