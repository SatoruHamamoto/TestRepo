package com.gnomes.common.search.query;

import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;

import com.gnomes.common.search.SearchInfoController.ConditionType;

/**
 * クエリ共通  Where句処理クラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/03/01 YJP/30022467              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class Condition {

	/**
     * オペレーター
     */
    public enum Operator {
    	AND(" AND"),
    	OR(" OR"),
    	NOT(" NOT"),
    	RIGHT_BRAKET(" ("),
    	LEFT_BRAKET(" )"),
    	SPACE(" ,"),
    	COMMA(" ,"),
    	COLON(" :"),
    	ENDCOLON(":"),
    	PARAM_POSTFIX("_p"),
    	EQUAL(" ="),
    	NOT_EQUAL(" <>"),
    	G(" >"),
    	GT(" >="),
    	L(" <"),
    	LT(" <="),
    	IN(" IN"),
    	LIKE(" LIKE"),
    	BETWEEN(" BETWEEN"),
    	IS_NULL(" IS NULL"),
    	IS_NOT_NULL(" IS NOT NULL"),
    	NONE(" ");

    	private String value;

        private Operator(String n) {
            this.value = n;
        }

        public String getValue() {
            return this.value;
        }

        public static Operator getEnum(String num) {
            // enum型全てを取得します。
        	Operator[] enumArray = Operator.values();

            // 取得出来たenum型分ループします。
            for(Operator enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num.equals(enumInt.getValue())){
                    return enumInt;
                }
            }
            return null;
        }
    }

    /** Where句データ情報クラス */
    private class ConditionData {

    	/** 項目名 */
    	private String name;
    	/** 比較条件 */
    	private Operator operator;
    	/** 否定有無 */
    	private boolean isNot;
    	/** パラメータ */
    	private String parameter;
    	/** 第二パラメータ */
    	private String secParameter;
    	/** 接続条件 */
    	private Operator junction;
    	/** データタイプ */
    	private ConditionType conditionType;

        /**
    	 * コンストラクター
    	 * @param name 項目名
    	 * @param operator 比較条件
    	 * @param parameter パラメータ
    	 */
    	public ConditionData(String name, Operator operator, String parameter,ConditionType conditionType)
    	{
    		this.junction = Operator.NONE;
    		this.name = name;
    		this.isNot = false;
    		this.operator = operator;
    		this.parameter = parameter;
    		this.conditionType = conditionType;
    	}

    	/**
    	 * コンストラクター
    	 * @param name 項目名
    	 * @param operator 比較条件
    	 * @param parameter パラメータ
    	 */
    	public ConditionData(String name, Operator operator, String parameter, String secParameter,ConditionType conditionType)
    	{
    		this.junction = Operator.NONE;
    		this.name = name;
    		this.isNot = false;
    		this.operator = operator;
    		this.parameter = parameter;
    		this.secParameter = secParameter;
            this.conditionType = conditionType;
    	}

    	/**
    	 * 接続条件を設定
    	 * @param junction 接続条件
    	 */
    	public void setJunction(Operator junction) {
    		this.junction = junction;
    	}

    	/**
    	 * 接続条件を取得
    	 * @return Operator 接続条件
    	 */
    	public Operator getJunction() {
    		return this.junction;
    	}

    	/**
    	 * 項目名を取得
    	 * @return String 項目名
    	 */
    	public String getName() {
    		return this.name;
    	}

    	/**
    	 * 比較条件取得
    	 * @return Operator 比較条件
    	 */
    	public Operator getOperator() {
    		return this.operator;
    	}

    	/**
    	 * パラメータを取得
    	 * @return String パラメータ
    	 */
    	public String getParameter() {
    		return this.parameter;
    	}

    	/**
    	 * 第二パラメータを取得
    	 * @return String 第二パラメータ
    	 */
    	public String getSecParameter() {
    		return this.secParameter;
    	}

    	/**
    	 * 否定設定
    	 * @param isNot 否定
    	 */
    	public void setIsNot(boolean isNot) {
    		this.isNot = isNot;
    	}

    	/**
    	 * 否定設定の取得
    	 * @return isNot
    	 */
    	public boolean getIsNot() {
    		return this.isNot;
    	}

    	/**
    	 * 条件データタイプの取得
    	 * @return 条件データタイプ
    	 */
        public ConditionType getConditionType()
        {
            return conditionType;
        }

        /**
         * 条件データタイプの設定
         * @param conditionType 条件データタイプ
         */
        public void setConditionType(ConditionType conditionType)
        {
            this.conditionType = conditionType;
        }

    }



    /** 条件句リスト */
    private LinkedList<Object> conditions = new LinkedList<Object>();


    /**
     * 条件句追加
     * @param data 追加条件句
     */
	public void addConditions(Object data)
	{
		conditions.add(data);
	}

	/**
	 * 条件句リストを取得
	 * @return LinkedList 条件句リスト
	 */
	public LinkedList<Object> getConditions() {
		return this.conditions;
	}

	/**
	 * 項目名で指定された項目とパラメータ値の条件（＝）チェック
	 * @param name 項目名
	 * @param parameter パラメータ
	 * @return Condition 条件句
	 */
	public Condition equalCheck(String name, String parameter,ConditionType conditionType)
    {
    	Condition equalCondition = new Condition();
    	ConditionData conditionData = new ConditionData(name, Operator.EQUAL, parameter,conditionType);
    	equalCondition.addConditions(conditionData);
    	return equalCondition;
    }

	/**
	 * 項目名で指定された項目とパラメータ値の条件（≠）チェック
	 * @param name 項目名
	 * @param parameter パラメータ
	 * @return Condition 条件句
	 */
	public Condition notEqual(String name, String parameter,ConditionType conditionType)
	{
    	Condition equalCondition = new Condition();
    	ConditionData conditionData = new ConditionData(name, Operator.NOT_EQUAL, parameter,conditionType);
    	equalCondition.addConditions(conditionData);
    	return equalCondition;
	}

	/**
	 * 項目名で指定された項目とパラメータ値の一致チェック（あいまい検索）
	 * @param name 項目名
	 * @param parameter パラメータ
	 * @return Condition 条件句
	 */
	public Condition like(String name, String parameter,ConditionType conditionType)
	{
    	Condition equalCondition = new Condition();
    	ConditionData conditionData = new ConditionData(name, Operator.LIKE, parameter,conditionType);
    	equalCondition.addConditions(conditionData);
    	return equalCondition;
	}

	/**
	 * 項目名で指定された項目とパラメータ値の条件（＜）チェック
	 * @param name 項目名
	 * @param parameter パラメータ
	 * @return Condition 条件句
	 */
	public Condition gt(String name, String parameter,ConditionType conditionType)
	{
    	Condition equalCondition = new Condition();
    	ConditionData conditionData = new ConditionData(name, Operator.G, parameter,conditionType);
    	equalCondition.addConditions(conditionData);
    	return equalCondition;
	}

	/**
	 * 項目名で指定された項目とパラメータ値の条件（≦）チェック
	 * @param name 項目名
	 * @param parameter パラメータ
	 * @return Condition 条件句
	 */
	public Condition ge(String name, String parameter,ConditionType conditionType)
	{
    	Condition equalCondition = new Condition();
    	ConditionData conditionData = new ConditionData(name, Operator.GT, parameter,conditionType);
    	equalCondition.addConditions(conditionData);
    	return equalCondition;
	}


	/**
	 * 項目名で指定された項目とパラメータ値の条件（＞）チェック
	 * @param name 項目名
	 * @param parameter パラメータ
	 * @return Condition 条件句
	 */
	public Condition lt(String name, String parameter,ConditionType conditionType)
	{
    	Condition equalCondition = new Condition();
    	ConditionData conditionData = new ConditionData(name, Operator.L, parameter,conditionType);
    	equalCondition.addConditions(conditionData);
    	return equalCondition;
	}

	/**
	 * 項目名で指定された項目とパラメータ値の条件（≧）チェック
	 * @param name 項目名
	 * @param parameter パラメータ
	 * @return Condition 条件句
	 */
	public Condition le(String name, String parameter,ConditionType conditionType)
	{
    	Condition equalCondition = new Condition();
    	ConditionData conditionData = new ConditionData(name, Operator.LT, parameter,conditionType);
    	equalCondition.addConditions(conditionData);
    	return equalCondition;
	}

	/**
	 * 項目名で指定された項目とパラメータ値(リスト）の条件（IN）チェック
	 * @param name 項目名
	 * @param parameter パラメータ
	 * @return Condition 条件句
	 */
	public Condition in(String name, String parameter,ConditionType conditionType)
	{
    	Condition equalCondition = new Condition();
    	ConditionData conditionData = new ConditionData(name, Operator.IN, parameter,conditionType);
    	equalCondition.addConditions(conditionData);
    	return equalCondition;
	}


	/**
	 * 項目名で指定された項目とパラメータ値(1,2）の条件（BETWEEN）チェック
	 * @param name 項目名
	 * @param parameter1 パラメータ1
	 * @param parameter2 パラメータ2
	 * @return Condition 条件句
	 */
	public Condition between(String name, String parameter1, String parameter2,ConditionType conditionType)
	{
    	Condition equalCondition = new Condition();
    	ConditionData conditionData = new ConditionData(name, Operator.BETWEEN, parameter1, parameter2,conditionType);
    	equalCondition.addConditions(conditionData);
    	return equalCondition;
	}


	/**
	 * 項目名で指定された項目とパラメータ値の条件（NULL）チェック
	 * @param name 項目名
	 * @return Condition 条件句
	 */
	public Condition isNull(String name,ConditionType conditionType)
	{
    	Condition equalCondition = new Condition();
    	ConditionData conditionData = new ConditionData(name, Operator.IS_NULL, null,conditionType);
    	equalCondition.addConditions(conditionData);
    	return equalCondition;
	}

	/**
	 * 項目名で指定された項目とパラメータ値の条件（NULL）チェック
	 * @param name 項目名
	 * @return Condition 条件句
	 */
	public Condition isNotNull(String name,ConditionType conditionType)
	{
    	Condition equalCondition = new Condition();
    	ConditionData conditionData = new ConditionData(name, Operator.IS_NOT_NULL, null,conditionType);
    	equalCondition.addConditions(conditionData);
    	return equalCondition;
	}


	/**
	 * 条件句を AND で追加（複数の条件句を指定可能）
	 * @param andCondition 追加条件
	 */
	public void and(Condition andCondition) {

		if (this.conditions.size() > 0) {
			((ConditionData)andCondition.getConditions().getFirst()).setJunction(Operator.AND);
		}
		this.conditions.addAll(andCondition.getConditions());
	}

	/**
	 * 条件句を OR で追加（複数の条件句を指定可能）
	 * @param orCondition 追加条件
	 */
	public void or(Condition orCondition) {

		if (this.conditions.size() > 0) {
			((ConditionData)orCondition.getConditions().getFirst()).setJunction(Operator.OR);
		}
		this.conditions.add(orCondition.getConditions());

	}

	/**
	 * 条件句の否定（複数の条件句を指定可能）
	 * @param notCondition 追加条件
	 */
	public Condition not() {
		Object first = this.conditions.getFirst();

		if (first instanceof ConditionData) {
			((ConditionData)this.conditions.getFirst()).setIsNot(true);
		}

		return this;
	}

	/**
	 * 再帰的に条件句を繋げて条件句のクエリを生成
	 * @return String 条件句クエリ
	 */
	public String getQueryString() {
		return this.getQueryString(this.conditions, false);
	}

	/**
	 * 再帰的に条件句を繋げて条件句のクエリを生成
	 * @param conditions 条件リスト
	 * @return String 条件句クエリ
	 */
	@SuppressWarnings("unchecked")
	private String getQueryString(LinkedList<Object> conditions, boolean isLoop) {
		boolean isTop = true;
		StringBuilder str = new StringBuilder();

		for(Object item : conditions) {
			if (item instanceof ConditionData) {
				ConditionData data = (ConditionData)item;

				ConditionType conditionType = data.getConditionType();

				str.append(data.getJunction().getValue());
				if (isLoop && isTop) {
					str.append(Operator.RIGHT_BRAKET.getValue());
				}

				if (data.getIsNot()) {
					str.append(Operator.NOT.getValue());
				}

				String leftColumnName = data.getName();
				String param1 = Operator.COLON.getValue() + data.getParameter() + Operator.ENDCOLON.getValue();
				String param2 = Operator.COLON.getValue() + data.getSecParameter() + Operator.ENDCOLON.getValue();
				String strOperator = data.getOperator().getValue();

				str.append(" ");
				//----------------------------------------------------
				// 特殊加工 日付文字の多種フォーマットの場合における
				//  文字比較
				//----------------------------------------------------

				if(conditionType == ConditionType.MULTIFORMAT_DATESTR){


				    //例文
                    //select
                    //    a.validLimitDatetime
                    //from
                    //    infolot a
                    //where
                    //    a.lotNumber='T2010101'
                    //    and validLimitDatetime BETWEEN
                    //        (
                    //            CASE
                    //                WHEN LEN(validLimitDatetime) > 7
                    //                    THEN CONVERT(NVARCHAR, :p1,111)
                    //                    ELSE LEFT(CONVERT(NVARCHAR, :p1,111),7)
                    //            END
                    //        )
                    //    AND
                    //        (
                    //            CASE
                    //                WHEN LEN(validLimitDatetime) > 7
                    //                    THEN CONVERT(NVARCHAR, :p2,111)
                    //                    ELSE LEFT(CONVERT(NVARCHAR, :p2,111),7)
                    //            END
                    //        )

				    if(StringUtils.isNotEmpty(param1)){
				        param1 = "( CASE WHEN LEN(" + leftColumnName + ") > 7 THEN CONVERT(NVARCHAR, " + param1 + ",111) ELSE LEFT(CONVERT(NVARCHAR, " + param1 + ",111),7) END )";

				    }
                    if(StringUtils.isNotEmpty(param2)){
                        param2 = "( CASE WHEN LEN(" + leftColumnName + ") > 7 THEN CONVERT(NVARCHAR, " + param2 + ",111) ELSE LEFT(CONVERT(NVARCHAR, " + param2 + ",111),7) END )";
                    }
				}

				str.append(leftColumnName);
				switch (data.getOperator()) {
					case IN:
						str.append(strOperator);
						str.append(Operator.RIGHT_BRAKET.getValue());
						//str.append(Operator.COLON.getValue());
						str.append(param1);
						str.append(Operator.LEFT_BRAKET.getValue());
						break;
					case BETWEEN:
						str.append(strOperator);
						//str.append(Operator.COLON.getValue());
						str.append(param1);
						str.append(Operator.AND.getValue());
						//str.append(Operator.COLON.getValue());
						str.append(param2);
						break;
					case IS_NULL:
					case IS_NOT_NULL:
						str.append(strOperator);
						break;
					default:
						str.append(strOperator);
						//str.append(Operator.COLON.getValue());
						str.append(param1);
						break;
				}

			} else {
				// 入れ子
				String addStr = this.getQueryString((LinkedList<Object>)item, true);

				str.append(addStr);
				str.append(Operator.LEFT_BRAKET.getValue());
			}
			isTop = false;
		}
		return str.toString();
	}


}
