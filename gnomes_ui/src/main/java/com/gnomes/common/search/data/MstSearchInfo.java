package com.gnomes.common.search.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 検索情報マスタークラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/03/01 YJP/30022467              初版
 * R0.01.02 2019/05/27 YJP/S.Hamamoto            検索情報を各カラム名でアクセスできるようにHashMapを追加
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class MstSearchInfo {

    /** 条件マスター情報("mstConditions") array (表示順) */
    private List<MstCondition> mstConditions;

    /** 条件マスターのマップ情報 */
    private Map<String,MstCondition> mapMstConditions = new HashMap<>();

    /** 順序マスター情報("mstOrdering") array (表示順) */
    private List<MstOrdering> mstOrdering;

    /** 順序マスターの情報のマップ情報 */
    private Map<String,MstOrdering> mapMstOrdering = new HashMap<>();

    /** デフォルト設定内容 */
    private SearchSetting defaultSearchSetting;

    /** 保存された検索条件を読み込んだフラグ */
    private boolean isConditonSaveDataLoaded = false;

    /**
     * @return mstConditions
     */
    public List<MstCondition> getMstConditions() {
        return mstConditions;
    }


    /**
     * カラムIDを指定して条件マスターを得る
     *
     * @param columnId 探したいカラムID
     * @return 条件マスター（見つからないときはnull)
     */
    public MstCondition getMstCondition(String columnId){
    	if(mapMstConditions == null){
    		return null;
    	}
    	MstCondition cond = this.mapMstConditions.get(columnId);
    	return cond;
    }

    /**
     *
     * 検索カラム名を使って条件マスターを得る
     *  引数のカラム名はDBアイテム名を指定するのに対し
     *  マスター検索情報はparam_name(パラメータ名）がキーで格納されているため
     *  マスター検索情報をDBアイテム名で指定して検索するように変更
     *
     * @param searchColumnName
     * @return 条件マスター（見つからないときはnull)
     */
    public MstCondition getMstConditionOfSearchColumnName(String searchColumnName){
    	for(MstCondition cond : this.mstConditions){
    		if(cond.getSearch_column_name().equals(searchColumnName)){
    			return cond;
    		}
    	}
    	return null;
    }

    /**
     * @param mstConditions セットする mstConditions
     */
    public void setMstConditions(List<MstCondition> mstConditions) {
        this.mstConditions = mstConditions;

        // 条件マスターのマップ情報を作成する
        for(MstCondition cond : mstConditions){
        	mapMstConditions.put(cond.getColumnName(), cond);
        }
    }

    /**
     * @return mstOrdering
     */
    public List<MstOrdering> getMstOrdering() {
        return mstOrdering;
    }

    /**
     * @param mstOrdering セットする mstOrdering
     */
    public void setMstOrdering(List<MstOrdering> mstOrdering) {
        this.mstOrdering = mstOrdering;
        //ソートマスターのマップ情報を作成する
        for(MstOrdering order : mstOrdering){
        	this.mapMstOrdering.put(order.getColumnName(), order);
        }
    }

    public SearchSetting getDefaultSearchSetting() {
        return defaultSearchSetting;
    }

    public void setDefaultSearchSetting(SearchSetting defaultSearchSetting) {
        this.defaultSearchSetting = defaultSearchSetting;
    }

	/**
	 * @return isConditonSaveDataLoaded
	 */
	public boolean isConditonSaveDataLoaded() {
		return isConditonSaveDataLoaded;
	}

	/**
	 * @param isConditonSaveDataLoaded セットする isConditonSaveDataLoaded
	 */
	public void setConditonSaveDataLoaded(boolean isConditonSaveDataLoaded) {
		this.isConditonSaveDataLoaded = isConditonSaveDataLoaded;
	}

	/**
	 * カラムIDを指定したソートマスターを得る
	 *
	 * @param indexLabel
	 * @return
	 */
	public MstOrdering getMsterOrderingInfo(String columnId) {
		return this.mapMstOrdering.get(columnId);
	}


}
