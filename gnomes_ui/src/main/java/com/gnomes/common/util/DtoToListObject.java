package com.gnomes.common.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.dao.JdbcAccessUtil;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.exportdata.DownLoadCsv;
import com.gnomes.common.resource.GnomesLogMessageConstants;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.search.data.OrderingInfo;
import com.gnomes.common.search.data.SearchSetting;
import com.gnomes.common.tags.GnomesCTagDictionary;
import com.gnomes.common.tags.GnomesCTagTableCommon;
import com.gnomes.uiservice.ContainerResponse;

/**
 * DTOからCSVへの出力用LIST作成処理
 * 
 *  <!-- TYPE DESCRIPTION -->
 * 
 * <pre>
 * </pre>
 */
/*
 * ========================== MODIFICATION HISTORY ==========================
 * Release Date ID/Name Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2022/05/10 YJP/M.Kitada 初版 [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class DtoToListObject {
	@Inject
	protected GnomesCTagDictionary gnomesTagDictionary;
	@Inject
	protected GnomesSessionBean gnomesSessionBean;
	@Inject
	protected GnomesCTagDictionary gnomesCTagDictionary;
	@Inject
	protected GnomesExceptionFactory exceptionFactory;
	@Inject
	protected ContainerResponse responseContext;
	@Inject
	protected transient Logger logger;
	@Inject
	protected DownLoadCsv downLoadCsv;
	@Inject
	protected JdbcAccessUtil jdbcAccessUtil;
    @Inject
    protected GnomesCTagTableCommon gnomesCTagTableCommon;
    
	// 定数宣言
	/** タグタイプ：チェックボックス */
	private static final String TAG_TYPE_CHECKBOX = "checkbox";
	/** タグタイプ：非表示 */
	private static final String TAG_TYPE_HIDDEN = "hidden";
	/** タグタイプ：テキスト入力エリアパターン */
	private static final String TAG_TYPE_INPUT_TEXT = "input_text";
	/** タグタイプ：数値入力テキストエリアパターン */
	private static final String TAG_TYPE_INPUT_NUMBER = "input_number";
	/** タグタイプ：日付入力エリアパターン */
	private static final String TAG_TYPE_INPUT_DATE = "input_date";
	/** タグタイプ：日時入力パターン */
	private static final String TAG_TYPE_INPUT_ZONEDDATETIME = "input_timezone";
	/** タグタイプ：入力形式をパラメータから決定 */
	private static final String TAG_TYPE_INPUT_DATA_TYPE = "input_data_type";
	/** タグタイプ：表示形式をパラメータから決定 */
	private static final String TAG_TYPE_OUTPUT_DATA_TYPE = "output_data_type";
	/** タグタイプ：テキスト */
	private static final String TAG_TYPE_TEXT = "text";
	/** タグタイプ：数値 */
	private static final String TAG_TYPE_NUMBER = "number";
	/** タグタイプ：日付 */
	private static final String TAG_TYPE_DATE = "date";
	/** タグタイプ：タイムゾーン付きの日付 */
	private static final String TAG_TYPE_ZONEDDATETIME = "zonedDateTime";
	/** タグタイプ：ラベル */
	private static final String TAG_TYPE_LABEL = "label";
    /** タグタイプ：ボタン */
    public static final String TAG_TYPE_BUTTON = "button";
    /** タグタイプ：画像パターン */
    public static final String TAG_TYPE_IMG_PATTERN = "imgPattern";
    /** タグタイプ：テキストエリアパターン */
    public static final String TAG_TYPE_TEXTAREA = "textarea";
     /** タグタイプ：プログレスバー */
    public static final String TAG_TYPE_PROGRESS = "progress";
	/** 辞書：タグタイプ */
	private static final String INFO_TAG_TYPE = "type";
	/** 辞書：パラメータ名 */
	private static final String INFO_PARAM_NAME = "param_name";
	/** 辞書：フォーマットリソースID */
	private static final String INFO_FORMAT_RESOURCE_ID = "format_resource_id";
	/** 辞書：タグ名 */
	private static final String INFO_TAG_NAME = "name";
	/** 辞書：非表示 */
	private static final String INFO_HIDDEN = "hidden";
	/** 辞書：スタイル */
	private static final String INFO_STYLE = "style";
	/** 辞書：小数点桁数パラメータ名 */
	private static final String INFO_DECIMAL_POINT_PARAM_NAME = "decimal_point_name";
	/** 辞書：小数点桁数（固定値） */
	private static final String INFO_DECIMAL_POINT_VALUE = "decimal_point_value";
	/** 辞書：プルダウン（コード等定数）パターン */
	private static final String TAG_TYPE_PULLDOWN_CODE = "pulldown_code";
	/** 辞書：プルダウン（コード等定数）パターン 先頭空白無し */
	private static final String TAG_TYPE_PULLDOWN_CODE_NO_SPACE = "pulldown_code_no_space";
	/** 辞書：プルダウン（データ項目）パターン */
	private static final String TAG_TYPE_PULLDOWN_DATA = "pulldown_data";
	/** 辞書：プルダウン（データ項目）パターン 先頭空白無し */
	private static final String TAG_TYPE_PULLDOWN_DATA_NO_SPACE = "pulldown_data_no_space";
	/** 辞書：プルダウン候補パラメータ名 */
	private static final String INFO_LIST_PARAM_NAME = "list_param_name";
	/** 辞書：プルダウン選択パラメータ名 */
	private static final String INFO_SELECT_PARAM_NAME = "select_param_name";
	/** プルダウン候補名 */
	private static final String INFO_PULLDOWN_NAME = "name";
	/** プルダウン候補値 */
	private static final String INFO_PULLDOWN_VALUE = "value";
	/** 辞書：最大桁数 */
	private static final String INFO_INPUT_MAX_LENGTH = "input_max_length";
	/** 辞書：小数点入力桁数 */
	private static final String INFO_INPUT_DECIMAL_DIGITS = "input_decimal_digits";
	/** データタイプ：数値 */
	private static final int PARAM_DATA_TYPE_DIV_NUMBER = 1;
	/** データタイプ：二値 */
	private static final int PARAM_DATA_TYPE_DIV_PULLDOWN = 3;
	/** データタイプ：二値 (先頭空白無し) */
	private static final int PARAM_DATA_TYPE_DIV_PULLDOWN_NO_SPACE = 8;
	/** データタイプ・フォーマット マッピング */
	private static final Map<Integer, String> dataTypeFormatMap = new HashMap<>();
	/** データタイプ：年月日時分秒 */
	private static final int PARAM_DATA_TYPE_DIV_YYYYMMDDHHMmmss = 4;
	/** データタイプ：年月日 */
	private static final int PARAM_DATA_TYPE_DIV_YYYYMMDD = 5;
	/** データタイプ：時分秒 */
	private static final int PARAM_DATA_TYPE_DIV_DATE_HHmmss = 6;
	/** データタイプ：時分 */
	private static final int PARAM_DATA_TYPE_DIV_DATE_HHmm = 7;
	/** データタイプ：年月日時分 */
	private static final int PARAM_DATA_TYPE_DIV_YYYYMMDDHHMmm = 9;
	/** データタイプ：年月 */
	private static final int PARAM_DATA_TYPE_DIV_YYYYMM = 10;
	/** 上書き表示用のテーブルタイトル取得用フィールド名 */
	private static final String DISP_HEADERS_FIELD_NAME = "tableDispHeaders";
    /** 検索条件マップ取得用フィールド名 */
    private static final String SEARCH_SETTING_FIELD_NAME = "searchSettingMap";
	
	static {
		/** データタイプフォーマット：年月日時分秒 */
		dataTypeFormatMap.put(PARAM_DATA_TYPE_DIV_YYYYMMDDHHMmmss, GnomesResourcesConstants.YY01_0001);
		/** データタイプフォーマット：年月日 */
		dataTypeFormatMap.put(PARAM_DATA_TYPE_DIV_YYYYMMDD, GnomesResourcesConstants.YY01_0003);
		/** データタイプフォーマット：時分秒 */
		dataTypeFormatMap.put(PARAM_DATA_TYPE_DIV_DATE_HHmmss, GnomesResourcesConstants.YY01_0072);
		/** データタイプフォーマット：時分 */
		dataTypeFormatMap.put(PARAM_DATA_TYPE_DIV_DATE_HHmm, GnomesResourcesConstants.YY01_0069);
		/** データタイプフォーマット：年月日時分 */
		dataTypeFormatMap.put(PARAM_DATA_TYPE_DIV_YYYYMMDDHHMmm, GnomesResourcesConstants.YY01_0002);
		/** データタイプフォーマット：年月 */
		dataTypeFormatMap.put(PARAM_DATA_TYPE_DIV_YYYYMM, GnomesResourcesConstants.YY01_0094);
	}
    
    /** 明細タイプCSV出力しない 定義 */
	/** 出力しないTypeだけ定義する */
    private static final List<String> infoTagTypeNotOutPutList = new ArrayList<String>();
    static {
        /** 画像パターン */
        infoTagTypeNotOutPutList.add(TAG_TYPE_IMG_PATTERN);
        /** テキストエリア */
        infoTagTypeNotOutPutList.add(TAG_TYPE_TEXTAREA);
        /** プログレスバー */
        infoTagTypeNotOutPutList.add(TAG_TYPE_PROGRESS);
    }
    /** データタイプCSV出力しない 定義 */
    /** 出力しないTypeだけ定義する */
    private static final List<Integer> infoDataTypeNotOutPutList = new ArrayList<Integer>();
    static {
        /** 実装時では対象外はなし。登録例としてコメント化している */
        // /** データタイプ：数値 */
        // infoDataTypeNotOutPutList.add(PARAM_DATA_TYPE_DIV_NUMBER);
        // /** データタイプ：二値  */
        // infoDataTypeNotOutPutList.add(PARAM_DATA_TYPE_DIV_PULLDOWN);
        // /** データタイプ：二値 (先頭空白無し) */
        // infoDataTypeNotOutPutList.add(PARAM_DATA_TYPE_DIV_PULLDOWN_NO_SPACE);
    }    
    
	/**
	 * DTOデータのオブジェクトをリストへ変換しCSVファイルをダウンロード
	 * 
	 * テーブルの列名を動的に変更しファンクションビーンまたはクラスがNULL場合は変更した列名ではなく辞書の列名が出力されます
	 * DTOのリストがNULLの場合はデータがないCSVを出力します</br>
	 * CSVセパレータでタブ区切りにしたい場合は'/t'を指定します
	 * 
	 * @param tableCustomTagId  テーブルカスタムタグID
	 * @param dataList          DTOのリスト
	 * @param clazz             DTOクラス
	 * @param functionBean      ファンクションビーン
	 * @param functionBeanclazz ファンクションビーンのクラス
	 * @param separator         ','や'/t'などのセパレータ
	 * @param fileName          ダウンロードするファイル名
	 * @throws GnomesAppException
	 */
	public void downLoadDtoCsv(String tableCustomTagId, List<?> dataList, Class<?> clazz, Object functionBean,
			Class<?> functionBeanclazz, char separator, String fileName) throws GnomesAppException {
		try {
			List<Object> csvDataLst = new ArrayList<>();
			if (!Objects.isNull(clazz)) {
                // テーブル辞書取得
			    List<Map<String, Object>> lstTableInfo = gnomesCTagDictionary
						.getTableColumnInfo(gnomesCTagDictionary.getTableInfo(tableCustomTagId));
				Locale userLocale = gnomesSessionBean.getUserLocale();
				// 検索条件取得
                SearchSetting searchSetting = getSerchSettingMap(tableCustomTagId, functionBean, functionBeanclazz);
                List<OrderingInfo> ordInfoLst = searchSetting.getOrderingInfos();
                Map<String, Object> mapColInfoMap = getMapColInfo(lstTableInfo);
				List<Object> headerName = new ArrayList<>();
				headerName = getHeaderName(userLocale, lstTableInfo, tableCustomTagId, functionBean, functionBeanclazz, ordInfoLst, clazz, dataList);
				csvDataLst = makeCsvDataWithHeader(headerName, clazz, dataList, userLocale, tableCustomTagId, ordInfoLst, mapColInfoMap);
			}
			downLoadCsv.downLoadDataCsv(csvDataLst, separator, fileName);
		} catch (Exception e) {
			throw exceptionFactory.createGnomesAppException(e);
		}
	}

    /**
     * テーブルの表題の取得
     * 
     * @param userLocale ユーザーロケール
     * @param info テーブル辞書情報
     * @param dictId テーブルカスタムタグID
     * @param functionBean ファンクションビーン
     * @param functionBeanclazz ファンクションビーンのクラス
     * @param ordInfoLst 検索条件
     * @param clsRowData 対象クラス
     * @param objRowData 対象データ
     * @return テーブルの表題
     * @throws Exception 例外
     */
    @SuppressWarnings("unchecked")
    private List<Object> getHeaderName(Locale userLocale, List<Map<String, Object>> info, String dictId,
            Object functionBean, Class<?> functionBeanclazz, List<OrderingInfo> ordInfoLst, Class<?> clsRowData, 
            List<?> objRowData) throws Exception {
        List<Object> headerColNameLst = new ArrayList<>();

        Map<String, String> mapChild = null;
        boolean upDateDispExistFlg = false;
        // 上書きテーブルヘッダー取得
        if (functionBeanclazz != null && functionBean != null) {
            Object dataVal = getData(functionBeanclazz, functionBean, DISP_HEADERS_FIELD_NAME);
            Map<String, Map<String, String>> mapParent = null;
            if (!Objects.isNull(dataVal)) {
                mapParent = (Map<String, Map<String, String>>) dataVal;
                if (mapParent.containsKey(dictId)) {
                    mapChild = mapParent.get(dictId);
                    upDateDispExistFlg = true;
                }
            }
        }

        Map<String, String> tableHeaderColMap = new HashMap<>();
        for (int i = 0; i < info.size(); i++) {
            Map<String, Object> tr = info.get(i);
            Map<String, Object> mapColInfo = (Map<String, Object>) tr.get(GnomesCTagDictionary.MAP_NAME_COLUMN_INFO);
            Map<String, String> headInfo = (Map<String, String>) tr.get(GnomesCTagDictionary.MAP_NAME_TABLE_INFO);
            String name = (String) mapColInfo.get(INFO_TAG_NAME);
            String paramName = getNameForMapKey(mapColInfo);
            
            // 非表示のTagTypeかチェック
            if(!isOutputCsvType(mapColInfo, clsRowData, objRowData)) {
                continue;
            }
            if (upDateDispExistFlg) {
                if (mapChild.containsKey(name)) {
                    // 上書きテーブルヘッダーがありかつデータが設定されている場合
                    tableHeaderColMap.put(paramName, mapChild.get(name));
                } else {
                    // 上書きテーブルヘッダーがありかつデータが設定されていない場合
                    tableHeaderColMap.put(paramName, getDispColHeader(dictId, headInfo, userLocale));
                }
            } else {
                // 上書きテーブルヘッダーがない場合
                tableHeaderColMap.put(paramName, getDispColHeader(dictId, headInfo, userLocale));
            }
        }
        // 検索条件から表題部を作成
        for (int i = 0; i < ordInfoLst.size(); i++) {
            // 非表示項目か否か(Trueが非表示)
            if(ordInfoLst.get(i).isHiddenTable()) {
                continue;
            }
            if (tableHeaderColMap.containsKey(ordInfoLst.get(i).getColumnId())) {
                headerColNameLst.add(tableHeaderColMap.get(ordInfoLst.get(i).getColumnId()));
            }
        }
        return headerColNameLst;
    }

    /**
     * DTOからデータを取得するための辞書情報の取得
     * 
     * @param info テーブル辞書情報
     * @return DTOからデータを取得するための辞書情報
     * @throws Exception 例外
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getMapColInfo(List<Map<String, Object>> info) throws Exception {

        Map<String, Object> mapColInfoMap = new HashMap<>();
        // DTOからデータを取得するための辞書情報をMapを作成
        for (int i = 0; i < info.size(); i++) {
            Map<String, Object> tr = info.get(i);
            Map<String, Object> mapColInfo = (Map<String, Object>) tr.get(GnomesCTagDictionary.MAP_NAME_COLUMN_INFO);
            String paramName = getNameForMapKey(mapColInfo);
            mapColInfoMap.put(paramName, mapColInfo);
        }
        return mapColInfoMap;
    }

	/**
	 * テーブルタイトルを返却する
	 *
	 * @param tableId    テーブルID
	 * @param headInfo   タイトル情報
	 * @param userLocale ユーザーロケール
	 * @return テーブルタイトル
	 * @throws Exception
	 */
	private String getDispColHeader(String tableId, Map<String, String> headInfo, Locale userLocale) throws Exception {
		String header = "";
		if (StringUtils.isNotBlank(headInfo.get(GnomesCTagDictionary.MAP_NAME_RESOURCE_ID))) {
			header = ResourcesHandler.getString(headInfo.get(GnomesCTagDictionary.MAP_NAME_RESOURCE_ID), userLocale);
		}
		return header;
	}

	/**
	 * Beanの値取得
	 * 
	 * @param clazz  対象クラス
	 * @param object 対象Object
	 * @param name   field名
	 * @return 取得値
	 * @throws Exception
	 */
	protected Object getData(Class<?> clazz, Object object, String name) throws Exception {
		Object ret = null;

		try {
			ret = getObjectData(clazz, object, name);

		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// クラス[{0}]のフィールド名[{1}]より値取得に失敗しました
			String mes = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0006, clazz.getName(), name);
			throw new Exception(mes, e);
		} catch (IntrospectionException e) {

			// clazzのクラスにnameのフィールドが存在しません
			String mes = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0005, clazz.getName(), name);
			throw new Exception(mes, e);
		}
		return ret;

	}

	/**
	 * Objectのゲッターメソッドから値取得
	 *
	 * @param clazz  対象クラス
	 * @param object 対象Object
	 * @param name   field名
	 * @return 取得値
	 * @throws IntrospectionException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	protected Object getObjectData(Class<?> clazz, Object object, String name)
			throws IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object ret = null;

		// デスクリプタを用意
		PropertyDescriptor nameProp = new PropertyDescriptor(name, clazz);
		Method nameGetter = nameProp.getReadMethod();
		ret = nameGetter.invoke(object, (Object[]) null);

		return ret;
	}

    /**
     * CSV出力用LIST作成
     * 
     * @param lstHeader     CSV出力するデータ(表題部のみデータ入り)
     * @param clazz         対象クラス
     * @param lstDataObject 対象オブジェクト
     * @param userLocale    ユーザーロケール
     * @param info          テーブル辞書情報
     * @param dictId        テーブルカスタムタグID
     * @param ordInfoLst    検索条件
     * @param mapColInfoMap DTOからデータを取得するための辞書情報
     * @return CSV出力用LIST
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private List<Object> makeCsvDataWithHeader(List<Object> lstHeader, Class<?> clazz, List<?> lstDataObject,
            Locale userLocale, String dictId, List<OrderingInfo> ordInfoLst, Map<String, Object> mapColInfoMap) throws Exception {

        List<Object> csvDataList = new ArrayList<>();
        csvDataList.add(lstHeader.toArray());

        List<Object> dataList = new ArrayList<>();
        if(!Objects.isNull(lstDataObject)) {
            for (int ii = 0; ii < lstDataObject.size(); ii++) {
                dataList.clear();
                for (int i = 0; i < ordInfoLst.size(); i++) {
                    String ordInfoLstName = ordInfoLst.get(i).getColumnId();
                    if (mapColInfoMap.containsKey(ordInfoLstName)) {
                        // 非表示項目か否か(Trueが非表示)
                        if(ordInfoLst.get(i).isHiddenTable()) {
                            continue;
                        }
                        
                        Map<String, Object> mapColInfo = (Map<String, Object>) mapColInfoMap.get(ordInfoLstName);
                        String type = (String) mapColInfo.get(INFO_TAG_TYPE);
                        if (type == null) {
                            continue;
                        }
                        // 非表示のTagTypeかチェック
                        if(!isOutputCsvType(mapColInfo, clazz, lstDataObject)) {
                            continue;
                        }
                        Object dataVal = null;
                        switch (type) {
                        // チェックボックス
                        case TAG_TYPE_CHECKBOX:
                            dataVal = getCheckBoxData(mapColInfo, clazz, lstDataObject.get(ii), ii);
                            break;
                        // ラベル（テキスト）
                        case TAG_TYPE_TEXT:
                            dataVal = getTextData(mapColInfo, clazz, lstDataObject.get(ii));
                            break;
                        // ラベル（数値）
                        case TAG_TYPE_NUMBER:
                            dataVal = getNumber(dictId, mapColInfo, clazz, lstDataObject.get(ii));
                            break;
                        // ラベル（日付）
                        case TAG_TYPE_DATE:
                        case TAG_TYPE_ZONEDDATETIME:
                            dataVal = getDate(dictId, userLocale, mapColInfo, clazz, lstDataObject.get(ii));
                            break;
                        // プルダウン（コード等定数）
                        case TAG_TYPE_PULLDOWN_CODE:
                            // プルダウン（コード等定数） 先頭空白無し
                        case TAG_TYPE_PULLDOWN_CODE_NO_SPACE:
                            dataVal = getPullDownConstant(mapColInfo, clazz, lstDataObject.get(ii), ii);
                            break;
                        // プルダウン（データ項目）
                        case TAG_TYPE_PULLDOWN_DATA:
                            // プルダウン（データ項目） 先頭空白無し
                        case TAG_TYPE_PULLDOWN_DATA_NO_SPACE:
                            dataVal = getPullDownData(mapColInfo, clazz, lstDataObject.get(ii), ii);
                            break;
                        // テキスト入力
                        case TAG_TYPE_INPUT_TEXT:
                        	dataVal = getInputText(mapColInfo, clazz, lstDataObject.get(ii), ii);
                            break;
                        // 数値入力
                        case TAG_TYPE_INPUT_NUMBER:
                        	dataVal = getInputNumber(dictId, mapColInfo, clazz, lstDataObject.get(ii), ii);
                            break;
                        // 日付入力
                        case TAG_TYPE_INPUT_DATE:
                        case TAG_TYPE_INPUT_ZONEDDATETIME:
                            dataVal = getDate(dictId, userLocale, mapColInfo, clazz, lstDataObject.get(ii));
                            break;
                        // 入力形式、出力形式をパラメータから決定    
                        case TAG_TYPE_INPUT_DATA_TYPE:
                        case TAG_TYPE_OUTPUT_DATA_TYPE:
                            dataVal = getInputDataType(dictId, userLocale, mapColInfo, clazz, lstDataObject.get(ii), ii);
                            break;
                        // ラベル
                        case TAG_TYPE_LABEL:
                            dataVal = getTextData(mapColInfo, clazz, lstDataObject.get(ii));
                            break;
                            // ボタン
                        case TAG_TYPE_BUTTON:
                            dataVal = getButtonData(mapColInfo, clazz, lstDataObject.get(ii));
                            break;
                        // 上記以外は空文字
                        default:
                            break;
                        }
                        String setVal = "";
                        if (!Objects.isNull(dataVal)) {
                            setVal = dataVal.toString();
                        }
                        dataList.add(setVal);
                    }
                }
                csvDataList.add(dataList.toArray());
            }
        }
        return csvDataList;

    }

	/**
	 * 日付出力データ取得
	 * 
	 * @param dictId     テーブルカスタムタグID
	 * @param userLocale ユーザーロケール
	 * @param mapColInfo 項目辞書
	 * @param clsData    対象クラス
	 * @param data       対象データ
	 * @return 取得データ
	 * @throws Exception
	 */
	private Object getDate(String dictId, Locale userLocale, Map<String, Object> mapColInfo, Class<?> clsData,
			Object data) throws Exception {

		// パラメータ名
		String paramName = (String) mapColInfo.get(INFO_PARAM_NAME);
		paramName = paramNameSplit(paramName);
		// フォーマットリソースID
		String formatResourceId = (String) mapColInfo.get(INFO_FORMAT_RESOURCE_ID);
		// 日付パターン
		String datePattern = ResourcesHandler.getString(formatResourceId, userLocale);

		Object ret = outDateBase(dictId, paramName, datePattern, clsData, data);
		return ret;
	}

	/**
	 * 日付出力データ取得
	 * 
	 * @param dictId      テーブルカスタムタグID
	 * @param paramName   パラメータ名
	 * @param datePattern 日付パターン
	 * @param mapColInfo  項目辞書
	 * @param clsData     対象クラス
	 * @param data        対象データ
	 * @return 取得データ
	 * @throws Exception 例外
	 */
	private Object outDateBase(String dictId, String paramName, String datePattern, Class<?> clsData, Object data)
			throws Exception {
		// スタイルクラス
		Object valueObj = getData(clsData, data, paramName);

		// 表示データの取得
		String strDate = getStringDate(dictId, paramName, valueObj, datePattern);
		Object retVal = strDate;
		return retVal;

	}

	/**
	 * Objectから日付の文字列を取得
	 * 
	 * @param dictId      辞書ID
	 * @param paramName   パラメータ名
	 * @param valueObj    変換元値
	 * @param datePattern 日付パターン
	 * @return 変換したデータ
	 * @throws Exception 例外
	 */
	protected String getStringDate(String dictId, String paramName, Object valueObj, String datePattern)
			throws Exception {
		String result = "";
		try {
			result = getStringDate(valueObj, datePattern);

		} catch (ParseException e) {
			// {0}から{1}に変換時、変換エラーが発生しました。ID=[{2}], param_name=[{3}], value=[{4}]
			String mesErr = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0008,
					ResourcesHandler.getString(GnomesResourcesConstants.YY01_0012),
					ResourcesHandler.getString(GnomesResourcesConstants.YY01_0011), dictId, paramName, valueObj);
			throw new Exception(mesErr, e);
		}
		return result;
	}

	/**
	 * 日付の文字列変換
	 *
	 * @param valueObj    対象値
	 * @param datePattern 日付パターン
	 * @return 変換後文字列
	 * @throws ParseException     変換エラー
	 * @throws GnomesAppException
	 */
	protected String getStringDate(Object valueObj, String datePattern) throws ParseException, GnomesAppException {
		String result = "";
		Object value = valueObj;

		if (value != null) {
			if (value instanceof String) {
				String strDate = value.toString();
				try {
					value = ConverterUtils.stringToDateTime(strDate);
				} catch (ParseException e) {
					value = ConverterUtils.stringToDateFormat(strDate, datePattern);
				}
			}

			if (value != null) {
				// Web.xmlのタイムゾーンがUTCの場合,
				if (CommonConstants.ZONEID_UTC.equals(TimeZone.getDefault().getID())) {
					// UTCのDate型の値を任意のロケール(タイムゾーン)のString型に変更
					result = GnomesDateUtil.convertDateToLocaleString((Date) value, datePattern,
							gnomesSessionBean.getUserTimeZone(), gnomesSessionBean.getUserLocale());
				} else {
					// Web.xmlのタイムゾーンがUTCではない場合は変換を行わない
					result = ConverterUtils.dateTimeToString((Date) value, datePattern);
				}
				return result;
			}

		}
		result = ConverterUtils.dateTimeToString(value, datePattern);

		return result;
	}

	/**
	 * チェックボックスデータ取得
	 * 
	 * @param mapColInfo 項目辞書
	 * @param clazz      対象クラス
	 * @param data       対象データ
	 * @param rowCnt     行数
	 * @return 取得データ
	 * @throws Exception 例外
	 */
	private Object getCheckBoxData(Map<String, Object> mapColInfo, Class<?> clazz, Object data,
			int rowCnt) throws Exception {

		// チェック状態
		String strChecked = "";
		// パラメータ名
		String paramName = (String) mapColInfo.get(INFO_PARAM_NAME);
		paramName = paramNameSplit(paramName);
		// タグ名
		String name = null;

		if (!StringUtil.isNullOrEmpty((String) mapColInfo.get(INFO_TAG_NAME))) {
			name = (String) mapColInfo.get(INFO_TAG_NAME);
		}

		// 表示データ
		if (!StringUtil.isNullOrEmpty(paramName) && !StringUtil.isNullOrEmpty(name)) {
			Object valueObj = responseContext.getResponseCheckBoxFormBean(data, paramName, rowCnt, name);

			if (valueObj != null) {
				if (valueObj instanceof Integer) {
					if (((Integer) valueObj).intValue() == CommonConstants.DTO_CHECK_BOX_CHECKED_VALUE) {
						strChecked = "checked";
					}
				}
			}
		}
		return strChecked;
	}

	/**
	 * テキスト出力データ取得
	 * 
	 * @param mapColInfo 項目辞書
	 * @param clsData    対象クラス
	 * @param data       対象データ
	 * @return 取得データ
	 * @throws Exception 例外
	 */
	private String getTextData(Map<String, Object> mapColInfo, Class<?> clsData, Object data) throws Exception {

		// パラメータ名
		String paramName = (String) mapColInfo.get(INFO_PARAM_NAME);
		paramName = paramNameSplit(paramName);
		
		// 値の取得
		String strVal = "";
		Object value = getData(clsData, data, paramName);
		if (value != null) {
			strVal = value.toString();
		}
		return strVal;
	}

	/**
	 * 数値出力データ取得
	 * 
	 * @param dictId     テーブルカスタムタグID
	 * @param mapColInfo 項目辞書
	 * @param clsData    対象クラス
	 * @param data       対象データ
	 * @return 取得データ
	 * @throws Exception 例外
	 */
	private String getNumber(String dictId, Map<String, Object> mapColInfo, Class<?> clsData, Object data)
			throws Exception {

		// パラメータ名
		String paramName = (String) mapColInfo.get(INFO_PARAM_NAME);
		paramName = paramNameSplit(paramName);
		// 小数点桁数パラメータ名
		String decimalPointName = (String) mapColInfo.get(INFO_DECIMAL_POINT_PARAM_NAME);
		// 小数点桁数（固定値）
		String decimalPointValue = (String) mapColInfo.get(INFO_DECIMAL_POINT_VALUE);
		Object valueObj = getData(clsData, data, paramName);
		// 表示データ
		String strNum = "";
		Integer intObj = null;

		// 小数点桁数パラメータ名の指定がある場合にBeanより桁数を取得する
		if (decimalPointName != null) {
			intObj = (Integer) (getData(clsData, data, decimalPointName));
		}
		// 小数点桁数（固定値）の指定がある場合に桁数を取得する
		else if (decimalPointValue != null) {
			intObj = Integer.valueOf(decimalPointValue);
		}

		// 表示データを取得
		strNum = getStringNumber(dictId, paramName, valueObj, intObj);
		return strNum;

	}

	/**
	 * Objectから数値の文字列を取得
	 * 
	 * @param dictId       テーブルカスタムタグID
	 * @param paramName    パラメータ名
	 * @param valueObj     変換元値
	 * @param decimalPoint 小数点桁数
	 * @return 変換後文字列
	 * @throws Exception 例外
	 */
	protected String getStringNumber(String dictId, String paramName, Object valueObj, Integer decimalPoint)
			throws Exception {
		String result = "";

		try {
			result = getStringNumber(valueObj, false, decimalPoint);
		} catch (ParseException e) {
			// {0}から{1}に変換時、変換エラーが発生しました。ID=[{2}], param_name=[{3}], value=[{4}]
			String mesErr = MessagesHandler.getString(GnomesLogMessageConstants.ME01_0008,
					ResourcesHandler.getString(GnomesResourcesConstants.YY01_0012),
					ResourcesHandler.getString(GnomesResourcesConstants.YY01_0009), dictId, paramName, valueObj);
			throw new Exception(mesErr, e);
		}

		return result;

	}

	/**
	 * 数値の文字列変換
	 *
	 * @param valueObj     対象値
	 * @param isCurrency   金額判定フラグ
	 * @param decimalPoint 小数点桁数
	 * @return 変換後文字列
	 * @throws ParseException 変換エラー
	 */
	protected String getStringNumber(Object valueObj, boolean isCurrency, Integer decimalPoint) throws ParseException {
		String result = "";
		Object value = valueObj;

		Locale userLocal = gnomesSessionBean.getUserLocale();
		if (value != null) {
			if (value instanceof String) {
				String strNum = value.toString();
				value = ConverterUtils.stringToNumber(false, strNum, gnomesSessionBean.getUserLocale().toString());
			}
			result = ConverterUtils.numberToString(isCurrency, userLocal.toString(), value, decimalPoint);
		}
		return result;
	}

	/**
	 * プルダウン出力取得
	 * 
	 * @param mapColInfo 項目辞書
	 * @param clsData    対象クラス
	 * @param data       対象データ
	 * @param rowCnt     行数
	 * @return 取得データ
	 * @throws Exception 例外
	 */
	@SuppressWarnings({ "unchecked" })
	private String getPullDownConstant(Map<String, Object> mapColInfo, Class<?> clsData, Object data, int rowCnt)
			throws Exception {

		// タグ名
		String name = null;
		if (!StringUtil.isNullOrEmpty((String) mapColInfo.get(INFO_TAG_NAME))) {
			name = (String) mapColInfo.get(INFO_TAG_NAME);
		}

		// FormBeanから取得するBean名取得
		String listParamName = (String) mapColInfo.get(INFO_LIST_PARAM_NAME);

		// FormBeanからプルダウンを取得
		List<Object> listValue = (List<Object>) getData(clsData, data, listParamName);

		// FormBeanからプルダウン選択状態を取得
		String selectParamName = (String) mapColInfo.get(INFO_SELECT_PARAM_NAME);
		if (StringUtil.isNullOrEmpty(selectParamName)) {
			selectParamName = "";
		}
		// 選択値パラメータ名と差し替え用選択名パラメータ名を分割
		String selectParamNames[] = selectParamName.split(",");

		Object valueObj = null;
		// プルダウン選択項目値を取得
		if (selectParamNames.length >= 1 && !StringUtil.isNullOrEmpty(selectParamNames[0])) {
			valueObj = responseContext.getResponseFormBean(data, selectParamNames[0], rowCnt, name);
		}

		// プルダウン選択項目値
		String selValue = "";

		// プルダウン選択項目名
		String selName = "";

		String[] outSel = new String[2];
		// プルダウン選択項目値が候補リストに存在する場合、選択値を設定
		if (getSelect(listValue, valueObj, outSel)) {
			selValue = outSel[0];
			selName = outSel[1];
		}

		// 差し替え用選択名パラメータ名が設定されている場合、差し替え用プルダウン選択項目名を取得
		if (selectParamNames.length == 2 && !StringUtil.isNullOrEmpty(selectParamNames[1])) {
			Object nameObj = responseContext.getResponseFormBean(data, selectParamNames[1], rowCnt, name);
			if (nameObj != null && !StringUtil.isNullOrEmpty(selValue)) {
				selName = nameObj.toString();
			}
		}

		return selName;
	}

	/**
	 * プルダウン選択取得
	 * 
	 * @param listValue 選択候補リスト
	 * @param selValue  選択値
	 * @param outSel    選択されているキーと選択文字列
	 * @return true 選択あり
	 * @throws Exception
	 */
	protected boolean getSelect(List<Object> listValue, Object selValue, String[] outSel) throws Exception {

		String compValue = null;
		if (selValue != null) {
			if (selValue instanceof BigDecimal) {
				compValue = String.valueOf(((BigDecimal) (selValue)).intValue());
			} else {
				compValue = selValue.toString();
			}
		}

		outSel[0] = "";
		outSel[1] = "";

		// プルダウン内のデータ生成
		if (listValue != null && listValue.size() > 0) {
			Class<?> lstClass = listValue.get(0).getClass();
			for (int l = 0; l < listValue.size(); l++) {
				Object item = listValue.get(l);

				Object key = getData(lstClass, item, INFO_PULLDOWN_NAME);
				Object value = getData(lstClass, item, INFO_PULLDOWN_VALUE);
				if (value == null) {
					value = "";
				}

				if (compValue != null && compValue.compareTo(key.toString()) == 0) {
					outSel[0] = compValue;
					outSel[1] = value.toString();
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * プルダウン出力（データ項目）取得
	 * 
	 * @param mapColInfo 項目辞書
	 * @param clsData    対象クラス
	 * @param data       対象データ
	 * @param rowCnt     行数
	 * @return           取得データ
	 * @throws Exception 例外
	 */
	@SuppressWarnings({ "unchecked" })
	private String getPullDownData(Map<String, Object> mapColInfo, Class<?> clsData, Object data, int rowCnt)
			throws Exception {

		// パラメータ名
		String paramName = (String) mapColInfo.get(INFO_PARAM_NAME);
		paramName = paramNameSplit(paramName);
		// FormBeanからプルダウン選択状態を取得
		String selectParamName = (String) mapColInfo.get(INFO_SELECT_PARAM_NAME);

		// プルダウン候補パラメータ名
		String listParamName = (String) mapColInfo.get(INFO_LIST_PARAM_NAME);

		// プルダウン候補パラメータ名からリストデータを取得
		List<Object> listValue = (List<Object>) getData(clsData, data, listParamName);

		String selName = "";
		selName = outPullDownDataBase(listValue, selectParamName, paramName, mapColInfo, clsData, data, rowCnt);
		return selName;

	}

	/**
	 * プルダウン出力（データ項目）
	 * 
	 * @param listValue       プルダウン候補
	 * @param selectParamName 選択値パラメータ名
	 * @param paramName       読取フラグ確認用パラメータ名
	 * @param style           スタイル
	 * @param mapColInfo      項目辞書
	 * @param clsData         対象クラス
	 * @param data            対象データ
	 * @param rowCnt          行数
	 * @return 取得データ
	 * @throws Exception
	 */
	private String outPullDownDataBase(List<Object> listValue, String selectParamName, String paramName,
			Map<String, Object> mapColInfo, Class<?> clsData, Object data, int rowCnt) throws Exception {
		// タグ名
		String name = null;
		if (!StringUtil.isNullOrEmpty((String) mapColInfo.get(INFO_TAG_NAME))) {
			name = (String) mapColInfo.get(INFO_TAG_NAME);
		}

		if (StringUtil.isNullOrEmpty(selectParamName)) {
			selectParamName = "";
		}

		// 選択値パラメータ名と差し替え用選択名パラメータ名を分割
		String selectParamNames[] = selectParamName.split(",");
		Object valueObj = null;
		// プルダウン選択項目値を取得
		if (selectParamNames.length >= 1 && !StringUtil.isNullOrEmpty(selectParamNames[0])) {
			valueObj = responseContext.getResponseFormBean(data, selectParamNames[0], rowCnt, name);
		}

		// プルダウン選択項目値を取得
		String selValue = "";

		// プルダウン選択項目名
		String selName = "";

		String[] outSel = new String[2];
		// プルダウン選択項目値が候補リストに存在する場合、選択値を設定
		if (getSelect(listValue, valueObj, outSel)) {
			selValue = outSel[0];
			selName = outSel[1];
		}

		// 差し替え用選択名パラメータ名が設定されている場合、差し替え用プルダウン選択項目名を取得
		if (selectParamNames.length == 2 && !StringUtil.isNullOrEmpty(selectParamNames[1])) {
			Object nameObj = responseContext.getResponseFormBean(data, selectParamNames[1], rowCnt, name);
			if (nameObj != null && !StringUtil.isNullOrEmpty(selValue)) {
				selName = nameObj.toString();
			}
		}
		return selName;

	}

	/**
	 * テキスト入力データ取得
	 * 
	 * @param mapColInfo 項目辞書
	 * @param clsRowData 対象クラス
	 * @param objRowData 対象データ
	 * @param i          行数
	 * @return 取得データ
	 * @throws Exception
	 */
	private String getInputText(Map<String, Object> mapColInfo, Class<?> clsRowData, Object objRowData, int i)
			throws Exception {

		// パラメータ名
		String paramName = (String) mapColInfo.get(INFO_PARAM_NAME);
		paramName = paramNameSplit(paramName);
		String retVal = "";
		retVal = inputTextBase(paramName, mapColInfo, clsRowData, objRowData, i, false);
		return retVal;
	}

	/**
	 * テキスト入力データ取得
	 * 
	 * @param paramName   パラメータ名
	 * @param mapColInfo  項目辞書
	 * @param clsRowData  対象クラス
	 * @param objRowData  対象データ
	 * @param i           行数
	 * @param isMultiType true:可変型データタイプ
	 * @return 取得データ
	 * @throws Exception 例外
	 */
	private String inputTextBase(String paramName, Map<String, Object> mapColInfo, Class<?> clsRowData,
			Object objRowData, int i, boolean isMultiType) throws Exception {

		// タグ名
		String name = null;
		if (!StringUtil.isNullOrEmpty((String) mapColInfo.get(INFO_TAG_NAME))) {
			name = (String) mapColInfo.get(INFO_TAG_NAME);
		}

		// 表示データ
		String strVal = "";
		Object valueObj = responseContext.getResponseFormBean(objRowData, paramName, i, name);

		// 最大桁数
		String inputMaxLength = (String) mapColInfo.get(INFO_INPUT_MAX_LENGTH);

		// 可変型データタイプの対応
		// 最大件数がカンマで区切られたら数値用と文字用に分け、文字用として切り出す
		// （カンマがなかったらそのままOUTとする)
		if (isMultiType) {
			inputMaxLength = getMaxLengthMultiString(inputMaxLength);
		}

		if (valueObj != null) {
			strVal = valueObj.toString();
		}

		return strVal;
	}

	/**
	 * MaxLengthが"40,20"などカンマで区切られた場合、1番目の40の所をText系のMaxLength値とする
	 * カンマがなかったらそのままの値を返す
	 *
	 * @param maxLengthString カンマで区切られたMaxLength値
	 * @return カンマの最初の値または、値そのもの（文字）
	 * @throws IllegalArgumentException
	 */
	private String getMaxLengthMultiString(String maxLengthString) {
		// nullチェック
		if (StringUtil.isNullOrEmpty(maxLengthString)) {
			return maxLengthString;
		}
		// ","で区切り
		String[] maxLengths = maxLengthString.split(",");
		// ","がない。つまり区切られていない場合は文字用長さとみなしそのままリターンする
		if (maxLengths.length == 1) {
			return maxLengthString;
		}
		// カンマがたくさんあっても一応先頭を対応する
		if (maxLengths.length > 1) {
			return maxLengths[0];
		} else {
			// MaxLength指定のフォーマットがおかしい
			String errmsg = String.format("maxLengthString is invalid format param value = [%s]", maxLengthString);
			logger.severe(errmsg);
			throw new IllegalArgumentException(errmsg);
		}
	}

	/**
	 * 数値入力データ取得
	 * 
	 * @param dictId     テーブルカスタムタグID
	 * @param mapColInfo 項目辞書
	 * @param clsRowData 対象クラス
	 * @param objRowData 対象データ
	 * @param i          行数
	 * @return 取得データ
	 * @throws Exception 例外
	 */
	private String getInputNumber(String dictId, Map<String, Object> mapColInfo, Class<?> clsRowData, Object objRowData,
			int i) throws Exception {

		// パラメータ名
		String paramName = (String) mapColInfo.get(INFO_PARAM_NAME);
		paramName = paramNameSplit(paramName);
		String retVal = "";
		retVal = inputNumberBase(dictId, paramName, mapColInfo, clsRowData, objRowData, i, false);

		return retVal;
	}

	/**
	 * 数値入力データ取得
	 * 
	 * @param dictId      テーブルカスタムタグID
	 * @param paramName   パラメータ名
	 * @param mapColInfo  項目辞書
	 * @param clsRowData  対象クラス
	 * @param objRowData  対象データ
	 * @param i           行数
	 * @param isMultiType true:可変タイプ
	 * @return 取得データ
	 * @throws Exception 例外
	 */
	private String inputNumberBase(String dictId, String paramName, Map<String, Object> mapColInfo, Class<?> clsRowData,
			Object objRowData, int i, boolean isMultiType) throws Exception {

		// 表示データ
		String strNum = "";
		// 小数点桁数パラメータ名
		String decimalPointName = (String) mapColInfo.get(INFO_DECIMAL_POINT_PARAM_NAME);
		// 小数点桁数（固定値）
		String decimalPointValue = (String) mapColInfo.get(INFO_DECIMAL_POINT_VALUE);
		// タグ名
		String name = null;
		if (!StringUtil.isNullOrEmpty((String) mapColInfo.get(INFO_TAG_NAME))) {
			name = (String) mapColInfo.get(INFO_TAG_NAME);
		}
		// 最大桁数
		String inputMaxLength = (String) mapColInfo.get(INFO_INPUT_MAX_LENGTH);

		// 可変タイプ対応
		// 最大件数がカンマで区切られたら数値用と文字用に分ける
		// （カンマがなかったらそのままOUTとする)
		if (isMultiType) {
			inputMaxLength = getMaxLengthMultiDigits(inputMaxLength);
		}

		// 小数点入力桁数
		String inputDecimalDigits = (String) mapColInfo.get(INFO_INPUT_DECIMAL_DIGITS);

		// 表示データを取得
		if (!StringUtil.isNullOrEmpty(paramName) && !StringUtil.isNullOrEmpty(name)) {

			Object valueObj = responseContext.getResponseFormBean(objRowData, paramName, i, name);
			if (valueObj instanceof String) {
				strNum = (String) valueObj;
			} else {
				Integer intObj = null;

				// 小数点桁数パラメータ名 の指定がある場合、Beanより小数点桁数を取得
				if (decimalPointName != null) {
					intObj = (Integer) (getData(clsRowData, objRowData, decimalPointName));
				}
				// 小数点桁数の指定がある場合、小数点桁数を取得
				else if (decimalPointValue != null) {
					intObj = Integer.valueOf(decimalPointValue);
				}
				// 小数点入力桁数の指定がある場合、小数点入力桁数を小数点桁数とする
				else if (!StringUtil.isNullOrEmpty(inputDecimalDigits)) {
					intObj = Integer.valueOf(inputDecimalDigits);
				}

				// 小数点桁数の設定がある場合
				if (intObj != null) {
					inputDecimalDigits = Integer.toString(intObj);
				}

				strNum = getStringNumber(dictId, paramName, valueObj, intObj);
			}
		}
		return strNum;
	}

	/**
	 * MaxLengthが"40,20"などカンマで区切られた場合、2番目の20の所をDigit系のMaxLength値とする
	 * カンマがなかったら数値型Lengthを指定していないとしnullを返す
	 *
	 * @param maxLengthString カンマで区切られたMaxLength値
	 * @return カンマの左側の値（文字）
	 * @throws IllegalArgumentException
	 */
	private String getMaxLengthMultiDigits(String maxLengthString) {
		// nullチェック nullか空白はチェックしない
		if (StringUtil.isNullOrEmpty(maxLengthString)) {
			return maxLengthString;
		}
		// ","で区切り
		String[] maxLengths = maxLengthString.split(",");
		// ","がない。つまり区切られていない場合は固定列の対応とし
		// maxLengthStringの値をそのまま返す
		if (maxLengths.length == 1) {
			return null;
		}
		// カンマがたくさんあっても2番目の数値を対応する
		if (maxLengths.length > 1) {
			return maxLengths[1];
		} else {
			// MaxLength指定のフォーマットがおかしい
			String errmsg = String.format("maxLengthString is invalid format param value = [%s]", maxLengthString);
			logger.severe(errmsg);
			throw new IllegalArgumentException(errmsg);
		}
	}

	/**
	 * タグタイプ：入力形式をパラメータから決定
	 * 
	 * @param dictId     テーブルカスタムタグID
	 * @param userLocale ユーザーロケール
	 * @param mapColInfo 項目辞書
	 * @param clsRowData 対象クラス
	 * @param objRowData 対象データ
	 * @param i          行数
	 * @return 取得データ
	 * @throws Exception
	 */
	private Object getInputDataType(String dictId, Locale userLocale, Map<String, Object> mapColInfo,
			Class<?> clsRowData, Object objRowData, int i) throws Exception {

		// パラメータ名
		String infoParamName = (String) mapColInfo.get(INFO_PARAM_NAME);
		String paramNames[] = infoParamName.split(",");

		// データタイプ取得
		Integer dataType = (Integer) getData(clsRowData, objRowData, paramNames[0]);
		
        String beanParamName;
        if (dataType == PARAM_DATA_TYPE_DIV_NUMBER || dataType == PARAM_DATA_TYPE_DIV_PULLDOWN || dataType == PARAM_DATA_TYPE_DIV_PULLDOWN_NO_SPACE) {
            beanParamName = paramNames[2];
        } else {
            beanParamName = paramNames[1];
        }
        
		Object retVal = null;
		// 日付時刻の場合
		if (isDataTypeDateTime(dataType)) {
		    String datePattern = gnomesCTagTableCommon.getDataTypeDatePattern(dataType);
			retVal = getInputDateBase(dictId, beanParamName, datePattern, mapColInfo, clsRowData, objRowData, i);
			return retVal;
		}

		switch (dataType) {
		// 数値入力の場合
		case PARAM_DATA_TYPE_DIV_NUMBER:
		    retVal = inputNumberBase(dictId, beanParamName, mapColInfo, clsRowData, objRowData, i, true);
			return retVal;
		// 二値(プルダウン)
		case PARAM_DATA_TYPE_DIV_PULLDOWN:
		// 二値(プルダウン) 先頭空白無し
		case PARAM_DATA_TYPE_DIV_PULLDOWN_NO_SPACE:
			retVal = getPullDownDataDataType(beanParamName,mapColInfo, clsRowData, objRowData, i);
			return retVal;

		// 上記以外の場合
		default:
			// 文字入力の場合
			retVal = getInputText(mapColInfo, clsRowData, objRowData, i);
			return retVal;
		}
	}

	/**
	 * dataTypeが日付時刻形式かどうかを判定
	 *
	 * @param dataType
	 * @return true:日付時刻形式
	 */
	protected boolean isDataTypeDateTime(Integer dataType) {
		return dataTypeFormatMap.containsKey(dataType);
	}

    /**
     * ボタンデータ取得
     * 
     * @param mapColInfo 項目辞書
     * @param clsRowData 対象クラス
     * @param objRowData 対象データ
     * @throws Exception 例外
     */
    private String getButtonData(
            Map<String, Object> mapColInfo,
            Class<?> objRowData,
            Object clsRowData) throws Exception {

        String retVal = "";
        
        String paramName = getName(mapColInfo);
        
        if (!StringUtil.isNullOrEmpty(paramName)) {
          Object value = getData(objRowData, clsRowData, paramName);
          if (value != null) {
              retVal = value.toString();
          }
        }
        
        return retVal;
    }
    
    /**
     * カンマ(,)区切りを行い先頭の文字列を返す
     *
     * @param paramName カンマ区切りのデータ
     * @return カンマ区切りの先頭の文字列
     */
    private String paramNameSplit(String paramName) {

        String[] arrVal = paramName.split(",", 1);
        return arrVal[0];

    }
    
    /**
     * 検索条件(リクエストとレスポンス)を返却する
     *
     * @param dictId テーブルカスタムタグID
     * @param functionBean ファンクションビーン
     * @param functionBeanclazz ファンクションビーンのクラス
     * @return 検索条件
     * @throws Exception
     */
    private SearchSetting getSerchSettingMap(String dictId, Object functionBean, Class<?> functionBeanclazz) throws Exception {
        Object dataVal = getData(functionBeanclazz, functionBean, SEARCH_SETTING_FIELD_NAME);
        Map<String, SearchSetting> mapSerchSetting = null;
        SearchSetting ss = null;
        if (!Objects.isNull(dataVal)) {
            mapSerchSetting = (Map<String, SearchSetting>) dataVal;
            if (mapSerchSetting.containsKey(dictId)) {
                ss = mapSerchSetting.get(dictId);
            }
        }
        return ss;
    }
    
    /**
     * Name取得
     * <pre>
     * Typeがボタン以外の場合はNameを返す
     * 
     * Typeがボタンの場合はParamNameを返す
     * その際ParamNameが3つ以上の場合は3番目の値を返す
     * 2つの場合は2番目の値を返す
     * それ以外は1番目の値を返す
     * </pre>
     * @param mapColInfo 項目辞書
     * @return NameまたはparamName
     * @throws Exception 例外
     */
    private String getName(Map<String, Object> mapColInfo) throws Exception {

        String retVal = "";
        String infoName = (String) mapColInfo.get(INFO_TAG_NAME);
        String infoParamName = (String) mapColInfo.get(INFO_PARAM_NAME);
        String infoType = (String) mapColInfo.get(INFO_TAG_TYPE);

        retVal = infoName;
        if (!StringUtil.isNullOrEmpty(infoType)) {
            if (TAG_TYPE_BUTTON.equals(infoType)) {
                if (StringUtil.isNullOrEmpty(infoParamName)) {
                    infoParamName = "";
                }
                String paramNames[] = infoParamName.split(",");

                if (paramNames.length >= 3 && !StringUtil.isNullOrEmpty(paramNames[2])) {
                    retVal = paramNames[2];
                }
                if (paramNames.length == 2 && !StringUtil.isNullOrEmpty(paramNames[1])) {
                    retVal = paramNames[1];
                }
                if(paramNames.length == 1) {
                    retVal = paramNames[0];
                }
            }
        }
        return retVal;
    }
    /**
     * Name取得
     * <pre>
     * Typeがボタン以外の場合はNameを返す
     * 
     * Typeがボタンの場合はParamNameを返す
     * その際ParamNameの「,」を「_」に置換した値を返す
     * </pre>
     * @param mapColInfo 項目辞書
     * @return NameまたはparamName
     * @throws Exception 例外
     */
    private String getNameForMapKey(Map<String, Object> mapColInfo) throws Exception {

        String retVal = "";
        String infoName = (String) mapColInfo.get(INFO_TAG_NAME);
        String infoParamName = (String) mapColInfo.get(INFO_PARAM_NAME);
        String infoType = (String) mapColInfo.get(INFO_TAG_TYPE);

        retVal = infoName;
        if (!StringUtil.isNullOrEmpty(infoType)) {
            if (TAG_TYPE_BUTTON.equals(infoType)) {
                if (StringUtil.isNullOrEmpty(infoParamName)) {
                    infoParamName = "";
                }
                retVal = infoParamName.replace(",", "_");
            }
        }
        return retVal;
    }
    

    /**
     * 日付入力
     * @param dictId 辞書ID
     * @param paramName paramName
     * @param datePattern 日付パターン
     * @param mapColInfo 項目辞書
     * @param clsRowData Beanクラス
     * @param objRowData Beanデータ
     * @param i 行数
     * @throws Exception 例外
     */
    private String getInputDateBase(String dictId, String paramName, String datePattern, Map<String, Object> mapColInfo, Class<?> clsRowData,
            Object objRowData, int i) throws Exception {
        
        // タグ名
        String name = null;
        if (!StringUtil.isNullOrEmpty((String) mapColInfo.get(INFO_TAG_NAME))) {
            name = (String) mapColInfo.get(INFO_TAG_NAME);
        }

        // 表示データ
        String strDate = null;
        Object valueObj = responseContext.getResponseFormBean(objRowData,
                paramName, i, name);
        if (valueObj instanceof String) {
            strDate = (String) valueObj;
        } else {
            strDate = getStringDate(dictId, paramName, valueObj, datePattern);
        }
        if(strDate == null){
            strDate ="";
        }
        return strDate;

    }
    /**
     * プルダウン出力（データ項目）取得(DataType用)
     * 
     * @param paramName  paramName
     * @param mapColInfo 項目辞書
     * @param clsData    対象クラス
     * @param data       対象データ
     * @param rowCnt     行数
     * @return 取得データ
     * @throws Exception 例外
     */
    @SuppressWarnings({ "unchecked" })
    private String getPullDownDataDataType(String paramName, Map<String, Object> mapColInfo, Class<?> clsData, Object data, int rowCnt)
            throws Exception {

        // FormBeanからプルダウン選択状態を取得
        String selectParamName = (String) mapColInfo.get(INFO_SELECT_PARAM_NAME);

        // プルダウン候補パラメータ名
        String listParamName = (String) mapColInfo.get(INFO_LIST_PARAM_NAME);

        // プルダウン候補パラメータ名からリストデータを取得
        List<Object> listValue = (List<Object>) getData(clsData, data, listParamName);

        String selName = "";
        selName = outPullDownDataBase(listValue, selectParamName, paramName, mapColInfo, clsData, data, rowCnt);
        return selName;

    }
    
    /**
     * CSV出力する明細タイプか判定
     * <pre>
     * </pre>
     * @param mapColInfo 項目辞書
     * @param clsRowData 対象クラス
     * @param objRowData 対象データ
     * @return true:CSV出力するTagType
     * @throws Exception 例外
     */
    private boolean isOutputCsvType(Map<String, Object> mapColInfo, Class<?> clsRowData, List<?> objRowData) throws Exception {

        boolean retVal = false;

        String type = (String) mapColInfo.get(INFO_TAG_TYPE);
        if (type != null) {
            // TagTypeが対象外に登録されているか
            if(!infoTagTypeNotOutPutList.contains(type)) {
                // 以下のTagTypeの場合はDataTypeもチェック
                if (type.equals(TAG_TYPE_OUTPUT_DATA_TYPE) || type.equals(TAG_TYPE_INPUT_DATA_TYPE)) {
                    // 出力するデータがない場合は表題のみ出力のため、表題は出力対象とする
                    if (!Objects.isNull(objRowData)) {
                        String infoParamName = (String) mapColInfo.get(INFO_PARAM_NAME);
                        String paramNames[] = infoParamName.split(",");
                        // データタイプ取得
                        Integer dataType = (Integer) getData(clsRowData, objRowData.get(0), paramNames[0]);
                        // DataTypeが対象外に登録されているか
                        if (!infoDataTypeNotOutPutList.contains(dataType)) {
                            retVal = true;  
                        }
                    } else {
                        retVal = true;
                    }
                } else {
                    retVal = true;    
                }
            }
        }
        
        return retVal;
    }    
}
