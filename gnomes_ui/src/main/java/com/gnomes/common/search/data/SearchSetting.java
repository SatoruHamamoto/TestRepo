package com.gnomes.common.search.data;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gnomes.common.search.serializer.DispTypeDeserializer;
import com.gnomes.common.search.serializer.DispTypeSerializer;

/**
 * 検索共通  設定情報クラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/03/01 YJP/30022467              初版
 * R0.01.02 2019/05/29 YJP/S.Hamamoto            検索ソート構造変更によるアクセス手段変更
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class SearchSetting {


    /** 表示タイプ */
    @JsonSerialize(using = DispTypeSerializer.class)
    @JsonDeserialize(using = DispTypeDeserializer.class)
    public enum DispType {
        DispType_List(0),                // 文字入力 (含む、から始まるなど）
        DispType_Paging(1);              // 文字 プルダウン

        private int value;

        private DispType(int n) {
            this.value = n;
        }

        public int getValue() {
            return this.value;
        }

        public static DispType getEnum(int num) {
            // enum型全てを取得します。
            DispType[] enumArray = DispType.values();

            // 取得出来たenum型分ループします。
            for(DispType enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()){
                    return enumInt;
                }
            }
            return null;
        }
    }

    /** 表示タイプ */
    private DispType dispType;

    /** 最大表示可能件数 */
    public int maxDispCount;

    /** 現在のページ */
    public int nowPage;

    /** １ページ表示件数 */
    public int onePageDispCount;

    /** 全件件数 */
    public int allDataCount;

	/** 条件("conditionInfos") array (表示順) */
	private List<ConditionInfo> conditionInfos=new ArrayList<>();

	/** 順序("orderingInfos") array(表示順) */
	private List<OrderingInfo> orderingInfos=new ArrayList<>();

	/** col固定位置 */
	private int fixedColNum;

	/** 検索実施フラグ */
	private Integer searchFlag;

	/** ページング実施フラグ */
	private Integer pagingFlag;

	/**
     * @return dispType
     */
    public DispType getDispType() {
        return dispType;
    }

    /**
     * @param dispType セットする dispType
     */
    public void setDispType(DispType dispType) {
        this.dispType = dispType;
    }

    /**
     * @return maxDispCount
     */
    public int getMaxDispCount() {
        return maxDispCount;
    }

    /**
     * @param maxDispCount セットする maxDispCount
     */
    public void setMaxDispCount(int maxDispCount) {
        this.maxDispCount = maxDispCount;
    }

    /**
     * @return nowPage
     */
    public int getNowPage() {
        return nowPage;
    }

    /**
     * @param nowPage セットする nowPage
     */
    public void setNowPage(int nowPage) {
        this.nowPage = nowPage;
    }


    /**
     * @return onePageDispCount
     */
    public int getOnePageDispCount() {
        return onePageDispCount;
    }

    /**
     * @param onePageDispCount セットする onePageDispCount
     */
    public void setOnePageDispCount(int onePageDispCount) {
        this.onePageDispCount = onePageDispCount;
    }

    /**
     * @return allDataCount
     */
    public int getAllDataCount() {
        return allDataCount;
    }

    /**
     * @param allDataCount セットする allDataCount
     */
    public void setAllDataCount(int allDataCount) {
        this.allDataCount = allDataCount;
    }

    /**
	 * @param conditionInfos セットする conditionInfos
	 */
	public void setConditionInfos(List<ConditionInfo> conditionInfos) {

		this.conditionInfos = conditionInfos;
	}

	/**
	 * @param orderingInfos セットする orderingInfos
	 */
	public void setOrderingInfos(List<OrderingInfo> orderingInfos) {
		this.orderingInfos = orderingInfos;
	}

	public List<ConditionInfo> getConditionInfos() {
		return this.conditionInfos;
	}

	public List<OrderingInfo> getOrderingInfos() {
		return this.orderingInfos;
	}

    /**
     * @return fixedColNum
     */
    public int getFixedColNum() {
        return fixedColNum;
    }

    /**
     * @param fixedColNum セットする fixedColNum
     */
    public void setFixedColNum(int fixedColNum) {
        this.fixedColNum = fixedColNum;
    }

	/**
	 * @return searchFlag
	 */
	public Integer getSearchFlag() {
		return searchFlag;
	}

	/**
	 * @param searchFlag セットする searchFlag
	 */
	public void setSearchFlag(Integer searchFlag) {
		this.searchFlag = searchFlag;
	}

	/**
	 * @return pagingFlag
	 */
	public Integer getPagingFlag() {
		return pagingFlag;
	}

	/**
	 * @param pagingFlag セットする pagingFlag
	 */
	public void setPagingFlag(Integer pagingFlag) {
		this.pagingFlag = pagingFlag;
	}

	/**
	 * カラム名をキーに条件オブジェクトを見つけて返す
	 *
	 * @param columnName カラム名
	 * @return 条件オブジェクト
	 */
	public ConditionInfo getConditionInfo(String columnName) {
		for(ConditionInfo info : this.conditionInfos){
			if(columnName.equals(info.getColumnId())){
				return info;
			}
		}

		return null;
	}
}