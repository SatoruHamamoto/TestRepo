package com.gnomes.common.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 03501213

 * DTOの扱いに関するユーティリティ・共通関数群
 *
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/09/18 YJP/S.Hamamoto            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class DTOUtil {

	/**
	 * 引数のDTOリストとチェックアイテムのチェックされた要素位置(0オリジン)が入っている配列を使って
	 * チェックされているDTOデータを新たに作って返す。
	 * チェックが１つも無い場合でも戻りオブジェクトは０個のリストとして返却する
	 *
	 * @param <E>
	 * @param <DTOClass>
	 *
	 * @param srcList DTOデータの入っているリスト
	 * @param selectedItems チェックされた要素位置(0オリジン)が入っている配列
	 * @return
	 */
	public static  <E> List<E> GetSelectedDTOList(List<E> srcList,Integer[] selectedItems)
	{
		//基本的nullチェック
		if(srcList == null ){
			throw new IllegalArgumentException("srcList is null.");
		}
//		//要素数が一致しなかったら引数Exceptionを返す
//		if(srcList.size() != selectedItems.length){
//			throw new GnomesException(new IllegalArgumentException("srcList count not match of selectedItems."));
//		}
		//戻りオブジェクトを作成
		List<E> retValue = new ArrayList<E>();

		//要素Indexの位置にあるアイテムを追加する
		//selectedItemsがnullの時（未選択）がある場合Listをnewしたまま返す
		if ( selectedItems != null ){
			int i=0;
			for(int index : selectedItems){
				retValue.add(srcList.get(index));
			}
		}
		return retValue;
	}
}
