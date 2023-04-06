package com.gnomes.common.tags;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.picketbox.util.StringUtil;

import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.data.MessageData;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.search.SearchInfoController;
import com.gnomes.common.search.data.MstOrdering;
import com.gnomes.common.search.data.OrderingInfo;
import com.gnomes.common.search.data.SearchInfoPack;
import com.gnomes.common.util.ConverterUtils;

/**
 * テーブルカスタムタグ 共通処理
 *
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/01/16 YJP/I.Shibasaka           初版
 * R0.01.02 2019/05/27 YJP/S.Hamamoto            indexを廃止
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class GnomesCTagTableCommon {

    @Inject
    transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected LogHelper logHelper;


    @Inject
    GnomesCTagDictionary gnomesCTagDictionary;

    @Inject
    GnomesSessionBean gnomesSessionBean;

    /**
     * テーブルカラム情報を取得
     * テーブルカラム表示有無と表示順反映
     * @param searchInfoPack 検索情報
     * @param dictId 対象テーブル辞書ID
     * @return テーブルカラム情報
     * @throws GnomesAppException 例外
     */
    public List<Map<String,Object>> getTableColumnInfo(SearchInfoPack searchInfoPack, String dictId) throws GnomesAppException {

        List<Map<String,Object>> lstTableInfo = null;

        if (searchInfoPack != null) {
            // 非表示と表示順の反映

            lstTableInfo = new ArrayList<Map<String,Object>>();

            // テーブル辞書取得
            List<Map<String,Object>> lstOrg = new ArrayList<Map<String,Object>>(
                    gnomesCTagDictionary.getTableColumnInfo(gnomesCTagDictionary.getTableInfo(dictId)));

            List<Map<String,Object>> lstRemove = new ArrayList<Map<String,Object>>();

            for (OrderingInfo order : searchInfoPack.getSearchSetting().getOrderingInfos()) {

            	String columnId = order.getColumnId();

            	//カラムIDが定義されていない場合は異常なので表示をパスする
            	if(StringUtil.isNullOrEmpty(columnId)){
            	    logHelper.severe(this.logger,null,null,"Columnid is not defined. dictid = " + dictId);
            		continue;
            	}

            	MstOrdering mstr = SearchInfoController.getMstOrdering(searchInfoPack,order.getColumnId());

            	//カラムIDがマスターに存在しない場合は表示をパスする
                if(mstr == null){
                	logHelper.severe(this.logger, null, null, "Columnid is not found in MstOrdering dictid = " + dictId + "columnId = " + order.getColumnId());
                	continue;
                }

                // オリジナルから対象を取得
                Map<String,Object> targetOrg = lstOrg.get(mstr.getTableTagColumnIndex());
                if (!order.isHiddenTable()) {
                    // 表示の場合、追加
                    Map<String,Object> dispTargetOrg = new HashMap<String,Object>();
                    // ディープコピーに修正
                    //dispTargetOrg.putAll(targetOrg);
                    for(Map.Entry<String, Object> entry : targetOrg.entrySet()) {

                        if (entry.getKey().equals(GnomesCTagDictionary.MAP_NAME_COLUMN_INFO)) {

                            Map<String,Object> cpyColInfo = new HashMap<String,Object>();

                            @SuppressWarnings("unchecked")
                            Map<String,Object> mapColInfo = (Map<String,Object>)entry.getValue();
                            for(Map.Entry<String, Object> colInfo : mapColInfo.entrySet()) {
                                cpyColInfo.put(colInfo.getKey(), colInfo.getValue());
                            }
                            dispTargetOrg.put(entry.getKey(), cpyColInfo);
                        } else {
                            dispTargetOrg.put(entry.getKey(), entry.getValue());
                        }
                    }

                    // 表示設定
                    @SuppressWarnings("unchecked")
                    Map<String,Object> mapColInfo = (Map<String,Object>)dispTargetOrg.get(GnomesCTagDictionary.MAP_NAME_COLUMN_INFO);
                    mapColInfo.remove(GnomesCTagTable.INFO_HIDDEN);
                    lstTableInfo.add(dispTargetOrg);

                    lstRemove.add(targetOrg);
                }
            }
            // 対象分を削除して残りのみにする
            lstOrg.removeAll(lstRemove);

            // 残りを追加
            for (Map<String,Object> zan : lstOrg) {

                // ヘッダをhiddenに設定
                @SuppressWarnings("unchecked")
                Map<String,String> headInfo = (Map<String,String>)zan.get(GnomesCTagDictionary.MAP_NAME_TABLE_INFO);
                if (!GnomesCTagTable.TAG_TYPE_HIDDEN.equals((String)headInfo.get(GnomesCTagTable.INFO_TAG_TYPE)) ) {

                    Map<String,Object> zanCopy = new HashMap<String, Object>();

                    // hiddenでない項目はhiddenに設定
                    Map<String,String> headInfoCopy = new HashMap<String, String>();
                    headInfoCopy.putAll(headInfo);
                    headInfoCopy.put(GnomesCTagTable.INFO_TAG_TYPE, GnomesCTagTable.TAG_TYPE_HIDDEN);
                    zanCopy.put(GnomesCTagDictionary.MAP_NAME_TABLE_INFO, headInfoCopy);

                    Map<String,Object> mapColInfoCopy = new HashMap<String, Object>();
                    @SuppressWarnings("unchecked")
                    Map<String,Object> mapColInfo = (Map<String,Object>)zan.get(GnomesCTagDictionary.MAP_NAME_COLUMN_INFO);
                    mapColInfoCopy.putAll(mapColInfo);
                    mapColInfoCopy.put(GnomesCTagTable.INFO_HIDDEN, true);
                    zanCopy.put(GnomesCTagDictionary.MAP_NAME_COLUMN_INFO, mapColInfoCopy);

                    // 追加
                    lstTableInfo.add(zanCopy);

                } else {
                    // そのまま追加
                    lstTableInfo.add(zan);
                }
            }

        } else {
            // テーブル辞書取得
            lstTableInfo = gnomesCTagDictionary.getTableColumnInfo(gnomesCTagDictionary.getTableInfo(dictId));
        }
        return lstTableInfo;
    }

    /**
     * テーブルカラム情報を取得(工程端末)
     * テーブルカラム表示有無と表示順反映
     * @param searchInfoPack 検索情報
     * @param dictId 対象テーブル辞書ID
     * @return テーブルカラム情報
     * @throws GnomesAppException 例外
     */
    public List<Map<String, Object>> getProcessTableColumnInfo(SearchInfoPack searchInfoPack, String dictId) throws GnomesAppException {
        List<Map<String,Object>> lstTableInfo = null;
        if (searchInfoPack != null) {
            // 非表示と表示順の反映
            lstTableInfo = gnomesCTagDictionary.getTableColumnInfo(gnomesCTagDictionary.getTableInfo(dictId));
        }
        return lstTableInfo;
    }

    public String getDataTypeDatePattern(int dataType) {
        return ResourcesHandler.getString(GnomesCTagBase.getDataTypeDateTimeFormat(dataType), gnomesSessionBean.getUserLocale());
    }

    /**
     * 必須チェック
     * @param name 項目名
     * @param value チェック値
     * @return 入力がない場合、MessageData
     */
    public MessageData isNullOrEmpty(String name, Object value) {

        if (value == null || (value != null && value instanceof String && value.toString().trim().length() == 0)) {
            return new MessageData(GnomesMessagesConstants.MV01_0002,
                            new Object[] { name });
        }
        return null;
    }

    /**
     * 明細タイプがinput_data_typeの場合の型チェック
     * @param dataTypeDiv チェックする型タイプ
     * @param name 項目名
     * @param value チェック値
     * @return 型不一致の場合、MessageData
     */
    public MessageData checkDataTypeDiv(int dataTypeDiv, String name, String value) {
        String typeName = null;
        try {

            switch (dataTypeDiv) {
                // 数値
                case GnomesCTagBase.PARAM_DATA_TYPE_DIV_NUMBER:
                    typeName = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0009);
                    ConverterUtils.stringToNumber(false, value);
                    break;
                // 文字
                case GnomesCTagBase.PARAM_DATA_TYPE_DIV_STRING:
                    // チェックなし
                    break;
                // 二値
                case GnomesCTagBase.PARAM_DATA_TYPE_DIV_PULLDOWN:
                    // チェックなし
                    break;
                case GnomesCTagBase.PARAM_DATA_TYPE_DIV_YYYYMMDDHHMmmss: // 年月日時分秒
                case GnomesCTagBase.PARAM_DATA_TYPE_DIV_YYYYMMDD: // 年月日
                case GnomesCTagBase.PARAM_DATA_TYPE_DIV_DATE_HHmmss: // 時分秒
                case GnomesCTagBase.PARAM_DATA_TYPE_DIV_DATE_HHmm: // 時分
                case GnomesCTagBase.PARAM_DATA_TYPE_DIV_YYYYMMDDHHMmm: // 年月日時分
                case GnomesCTagBase.PARAM_DATA_TYPE_DIV_YYYYMM: // 年月
                    // いずれも共通のチェック
                    typeName = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0011);
                    ConverterUtils.stringToDateFormat(value, getDataTypeDatePattern(dataTypeDiv));
                    break;
            }
        } catch (ParseException e) {
            return new MessageData(GnomesMessagesConstants.MV01_0001,
                            new Object[] {
                                    name,
                                    typeName });
        }
        return null;
    }
}
