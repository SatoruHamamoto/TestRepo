package com.gnomes.common.search.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 検索共通  検索ダイアログ情報クラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/03/01 YJP/30022467              初版
 * R0.01.02 2019/05/27 YJP/S.Hamamoto            条件や順序マスターをカラム名でアクセスできるようにHashMapを定義
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */

public class SearchInfoPack {

		/** 条件マスター情報("mstConditions") array (表示順) */
		private List<MstCondition> mstConditions;

		private Map<String,MstCondition> mapMstConditions = new HashMap<>();

		/** 順序マスター情報("mstOrdering") array (表示順) */
		private List<MstOrdering> mstOrdering;

		private Map<String,MstOrdering> mapMstOrdering = new HashMap<>();

		/** 検索時の設定内容 */
		private SearchSetting searchSetting;

		/**
		 * @return mstConditions
		 */
		public List<MstCondition> getMstConditions() {
			return mstConditions;
		}

		/**
		 * @param mstConditions セットする mstConditions
		 */
		public void setMstConditions(List<MstCondition> mstConditions) {
			this.mstConditions = mstConditions;
			//カラム名のHashMapを作成
			for(MstCondition cond : mstConditions){
				this.mapMstConditions.put(cond.getColumnName(), cond);
			}
		}

		/**
		 * ソートマスターのリストを得る
		 *
		 * @param columnId
		 * @return mstOrdering
		 */
		public List<MstOrdering> getMstOrdering() {
			return mstOrdering;
		}

		/**
		 * ソートマスターをカラム名から引き出す
		 *
		 * @param columnId
		 * @return
		 */
		public MstOrdering getMstOrdering(String columnId) {
			return mapMstOrdering.get(columnId);
		}

		/**
		 * @param mstOrdering セットする mstOrdering
		 */
		public void setMstOrdering(List<MstOrdering> mstOrdering) {
			this.mstOrdering = mstOrdering;
			//カラム名のHashMapを作成
			for(MstOrdering odr : mstOrdering){
				this.mapMstOrdering.put(odr.getColumnName(),odr);
			}
		}

        public SearchSetting getSearchSetting() {
            return searchSetting;
        }

        public void setSearchSetting(SearchSetting searchSetting) {
            this.searchSetting = searchSetting;
        }

		/**
		 * 条件マスター
		 *
		 * @param columnId
		 * @return
		 */
		public MstCondition getMstConditions(String columnId) {
			return mapMstConditions.get(columnId);
		}


	}