package com.gnomes.common.dto;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.resource.GnomesLogMessageConstants;
import com.gnomes.common.resource.spi.MessagesHandler;

/**
 * DTOに対してデータの取得、設定を行う共通クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/11/01 YJP/A.Oomori               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public final class DtoUtils {


    /**
     * チェックされた行から引数dtoKeyItemNameで指定された属性の値をリストにして返す。
     * @param dtoList DTOデータリスト
     * @param checkboxItemName チェックボックスフィールド名
     * @param dtoKeyItemName データ取得先フィールド名
     * @return チェックされた行の取得先フィールド名に該当する項目のデータリスト
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
	public static <T, E> List<T> GetCheckedKeyList(List<E> dtoList, String checkboxItemName, String dtoKeyItemName) throws Exception {
        List<T> result = new ArrayList<>();

        // DTOリストが空の場合、終了
        if(dtoList == null || dtoList.size() == 0){
        	return result;
        }
        Class<?> clazz = dtoList.get(0).getClass();
        try {
            // デスクリプタを用意
            PropertyDescriptor namePropCheck = new PropertyDescriptor(checkboxItemName, clazz);
            Method nameGetterCheck = namePropCheck.getReadMethod();

            PropertyDescriptor namePropKey = new PropertyDescriptor(dtoKeyItemName, clazz);
            Method nameGetterKey = namePropKey.getReadMethod();
	    	for (E dtoData: dtoList) {

	    		Integer getValue;
	    		// チェックボックスのチェック状態を取得
                getValue = (Integer) nameGetterCheck.invoke(dtoData, (Object[]) null);

                // チェックボックスがチェック状態の場合
	    		if(CommonConstants.DTO_CHECK_BOX_CHECKED_VALUE.equals(getValue)){
	    			T getKey;
	    			// データ取得先フィールドからデータを取得
	                getKey = (T) nameGetterKey.invoke(dtoData, (Object[]) null);
	                // 追加
	        		result.add(getKey);
	    		}

	        }

	    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
	        // クラス[{0}]のフィールド名[{1}]より値取得に失敗しました。
	        String mes = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0006, clazz, checkboxItemName + CommonConstants.COMMA + dtoKeyItemName);
	        throw new Exception(mes,e);
	    } catch (IntrospectionException e) {
	        // クラス名[{0}]にフィールド名[{1}]のアクセス用メソッドが存在しません。
	        String mes = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0005, clazz, checkboxItemName + CommonConstants.COMMA + dtoKeyItemName);
	        throw new Exception(mes,e);
	    }
        return result;
    }

    /**
     * dtoKeyItemNameで指定された属性の値がcheckKeyListに含まれる行の、dtoChekItemNameで指定された属性の値をOn（1)にする。
     * @param dtoList DTOデータリスト
     * @param checkKeyList データ参照先フィールド名に該当する項目のデータリスト
     * @param checkboxItemName チェックボックスフィールド名
     * @param dtoKeyItemName データ参照先フィールド名
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static <T, E> void SetCheck(List<E> dtoList, List<T> checkKeyList, String checkboxItemName, String dtoKeyItemName) throws Exception {

    	// DTOリストが空の場合、終了
        if(dtoList == null || dtoList.size() == 0){
        	return;
        }
        Class<?> clazz = dtoList.get(0).getClass();

    	try {
            // デスクリプタを用意
            PropertyDescriptor namePropKey = new PropertyDescriptor(dtoKeyItemName, clazz);
            Method nameGetterKey = namePropKey.getReadMethod();

    		PropertyDescriptor namePropCheck = new PropertyDescriptor(checkboxItemName, clazz);
            Method nameWriterCheck = namePropCheck.getWriteMethod();

	    	for (E dtoData: dtoList) {

	    		// チェックボックスのチェックを外す
	    		nameWriterCheck.invoke(dtoData, 0);

				T getValue;
				// データ参照先フィールドからデータを取得
	            getValue = (T) nameGetterKey.invoke(dtoData, (Object[]) null);

	            // 取得したデータがデータ参照先フィールド名に該当する項目のデータリストに含まれている場合
	            if (checkKeyList.contains(getValue)) {
	            	// チェックボックスをチェック状態にする
		            nameWriterCheck.invoke(dtoData, 1);
	            }
	        }
	    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
	        // クラス[{0}]のフィールド名[{1}]より値取得に失敗しました。
	        String mes = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0006, clazz, checkboxItemName + CommonConstants.COMMA + dtoKeyItemName);
	        throw new Exception(mes,e);
	    } catch (IntrospectionException e) {
	        // クラス名[{0}]にフィールド名[{1}]のアクセス用メソッドが存在しません。
	        String mes = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0005,  clazz, checkboxItemName + CommonConstants.COMMA + dtoKeyItemName);
	        throw new Exception(mes,e);
	    }
    }

}

