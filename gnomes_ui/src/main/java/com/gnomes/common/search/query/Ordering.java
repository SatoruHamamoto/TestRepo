package com.gnomes.common.search.query;

import java.util.ArrayList;
import java.util.List;

/**
 * クエリ共通  Order句処理クラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/03/01 YJP/30022467              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class Ordering {

	/** ORDER BY 複数指定用　カンマ */
	private static final String ORDERING_COMMA = ", ";


	/**
     * 方向
     */
    public enum Dir {
    	ASCENDING(" ASC"),
    	DESCENDING(" DESC");

    	private String value;

        private Dir(String n) {
            this.value = n;
        }

        public String getValue() {
            return this.value;
        }

        public static Dir getEnum(String num) {
            // enum型全てを取得します。
        	Dir[] enumArray = Dir.values();

            // 取得出来たenum型分ループします。
            for(Dir enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num.equals(enumInt.getValue())){
                    return enumInt;
                }
            }
            return null;
        }
    }

    /** Order句データ情報 */
    private class OrderingData {

    	/** 項目名 */
    	private String name;
    	/** 方向 */
    	private Dir dir;

    	/**
    	 * コンストラクター
    	 * @param name 項目名
    	 * @param dir 方向
    	 */
    	public OrderingData(String name, Dir dir) {
    		this.name = name;
    		this.dir = dir;
    	}

    	/**
    	 * 項目名を取得
    	 * @return String 項目名
    	 */
    	public String getName() {
    		return this.name;
    	}

    	/**
    	 * 方向を取得
    	 * @return Dir 方向
    	 */
    	public Dir getDir() {
    		return this.dir;
    	}

    }


    /** Order句リスト */
    List<OrderingData> orders;

    /**
     * Order句リストを取得
     * @return LinkedList Order句リスト
     */
    public List<OrderingData> getOrders() {
    	return this.orders;
    }

    /**
     * コンストラクタ
     */
    public Ordering() {
    	orders = new ArrayList<OrderingData>();
    }

    /**
     * コンストラクタ
     * @param name 項目名
     * @param dir 方向
     */
    public Ordering(String name, Dir dir) {
    	orders = new ArrayList<OrderingData>();

    	OrderingData m = new OrderingData(name, dir);
    	this.orders.add(m);
    }

    /**
     * Order句を追加する （複数の Order句を追加可能）
     * @param ordering Order句
     */
    public void add(Ordering ordering) {
    	this.orders.addAll(ordering.getOrders());
    }


    /**
     * 再帰的にOrder 句を繋げてOrder 句のクエリを生成
     * @return Order句
     */
    public String getQueryString() {

		StringBuilder str = new StringBuilder();

	   for (OrderingData m : this.orders) {

		   if (str.length() > 0) {
			   str.append(ORDERING_COMMA);
		   }

		   str.append(m.getName());
		   str.append(m.getDir().getValue());
	   }

	   return str.toString();
   }


}
