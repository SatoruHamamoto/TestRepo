package com.gnomes.common.search.data;

/**
 * 検索共通  順序マスター情報クラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/03/01 YJP/30022467              初版
 * R0.01.02 2019/05/29 YJP/S.Hamamoto            検索ソート構造変更によるアクセス手段変更
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class MstOrdering {

	/** カラム物理名 (Indexとして使われる)*/
	private String columnName;

	/** カラム物理名 (SQLに埋め込むカラム名として使われる)*/
	private String search_column_name;

	/** カラム名（多言語) */
	private String text;


	/** テーブルカスタムタグの項目と結合用 */
	private int tableTagColumnIndex;

	/**
	 * @return columnName
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * @param columnName セットする columnName
	 */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	/**
	 * @return text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text セットする text
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * カラム物理名の取得
	 *
	 * @return カラム物理名
	 */
	public String getSearch_column_name() {
		return search_column_name;
	}

	/**
	 * カラム物理名の設定
	 *
	 * @param search_column_name
	 */
	public void setSearch_column_name(String search_column_name) {
		this.search_column_name = search_column_name;
	}

	public int getTableTagColumnIndex() {
        return tableTagColumnIndex;
    }

    public void setTableTagColumnIndex(int tableTagColumnIndex) {
        this.tableTagColumnIndex = tableTagColumnIndex;
    }

}

