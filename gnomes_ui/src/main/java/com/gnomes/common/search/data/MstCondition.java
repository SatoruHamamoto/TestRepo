package com.gnomes.common.search.data;

import java.util.LinkedHashMap;
import java.util.List;

import com.gnomes.common.search.SearchInfoController.ConditionParamSaveType;
import com.gnomes.common.search.SearchInfoController.ConditionRequiredType;
import com.gnomes.common.search.SearchInfoController.ConditionType;

/**
 * 検索共通  条件マスター情報クラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/03/01 YJP/30022467              初版
 * R0.01.02 2019/05/29 YJP/S.Hamamoto            検索ソート構造変更によるアクセス手段変更
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class MstCondition {

	/** カラム物理名 */
	private String columnName;

	/** カラム物理名 */
	private String search_column_name;


	/** カラム名（多言語) */
	private String text;

	/** タイプ -- 文字、数値、日付 */
	private ConditionType type;

	/** 必須タイプ */
	private ConditionRequiredType requiredType;

	/** 保存タイプ */
	private List<ConditionParamSaveType> saveParamTypes;

	/** パターンリスト key=(値), value=(表示テキスト 多言語)*/
	private LinkedHashMap<String, String> patterns;

    /** 入力補助設定（TGC対応で追加、通常は使用しない） */
    private String inputAdvice;

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
	 * @return type
	 */
	public ConditionType getType() {
		return type;
	}

	/**
	 * @param type セットする type
	 */
	public void setType(ConditionType type) {
		this.type = type;
	}

	/**
     * @return requiredType
     */
    public ConditionRequiredType getRequiredType() {
        return requiredType;
    }

    /**
     * @param requiredType セットする requiredType
     */
    public void setRequiredType(ConditionRequiredType requiredType) {
        this.requiredType = requiredType;
    }

    /**
	 * @return patterns
	 */
	public LinkedHashMap<String, String> getPatterns() {
		return patterns;
	}

	/**
	 * @param patterns セットする patterns
	 */
	public void setPatterns(LinkedHashMap<String, String> patterns) {
		this.patterns = patterns;
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

    /**
     * @return saveParamTypes
     */
    public List<ConditionParamSaveType> getSaveParamTypes() {
        return saveParamTypes;
    }

    /**
     * @param saveParamTypes セットする saveParamTypes
     */
    public void setSaveParamTypes(List<ConditionParamSaveType> saveParamTypes) {
        this.saveParamTypes = saveParamTypes;
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

}
