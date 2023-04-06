package com.gnomes.common.search.data;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * 検索共通  条件情報クラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/03/01 YJP/30022467              初版
 * R0.01.02 2019/05/27 YJP/S.Hamamoto            Indexを廃止、ColumnIdで位置特定する
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY) 
public class ConditionInfo {

	/** 条件マスター情報のカラム	← 条件マスター情報("mstConditions")のColumnId */
	private String columnId;


	/** 有効無効（表示順を設定できるようにbooleanからStringに変更） */
	private String isEnable;

	/** 条件非表示 */
	private boolean hiddenItem;

	/** 入力補助設定（TGC対応で追加、通常は使用しない） */
	private String inputAdvice;

	/** パターンリストのキー */
	private List<String> patternKeys = new ArrayList<String>();

	/** パラメータリスト */
	private List<String> parameters = new ArrayList<String>();

	/**
	 * @return columnId
	 */
	public String getColumnId() {
		return columnId;
	}

	/**
	 * @param columnId セットする columnId
	 */
	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}

	/**
	 * @return isEnable
	 */
	public String isEnable() {
		return isEnable;
	}

	/**
	 * @param isEnable セットする isEnable
	 */
	public void setEnable(String isEnable) {
		this.isEnable = isEnable;
	}

	/**
	 * @return hiddenItem
	 */
	public boolean isHiddenItem() {
		return hiddenItem;
	}

	/**
	 * @param hiddenItem セットする hiddenItem
	 */
	public void setHiddenItem(boolean hiddenItem) {
		this.hiddenItem = hiddenItem;
	}

    /**
     * @return inputAdvice
     */
    public String getInputAdvice() {
        return inputAdvice;
    }

    /**
     * @param inputAdvice セットする inputAdvice
     */
    public void setInputAdvice(String inputAdvice) {
        this.inputAdvice = inputAdvice;
    }

	/**
	 * @return patternKeys
	 */
	public List<String> getPatternKeys() {
		return patternKeys;
	}

	/**
	 * @param patternKeys セットする patternKeys
	 */
	public void setPatternKeys(List<String> patternKeys) {
		this.patternKeys = patternKeys;
	}

	/**
	 * @return parameters
	 */
	public List<String> getParameters() {
		return parameters;
	}

	/**
	 * @param parameters セットする parameters
	 */
	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}

}