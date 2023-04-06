package com.gnomes.system.logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums.TableSearchSettingType;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseLogic;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.search.SearchInfoController;
import com.gnomes.common.search.SearchInfoController.ConditionDateType;
import com.gnomes.common.search.SearchInfoController.ConditionParamSaveType;
import com.gnomes.common.search.SearchInfoController.ConditionType;
import com.gnomes.common.search.data.ConditionInfo;
import com.gnomes.common.search.data.MstCondition;
import com.gnomes.common.search.data.MstSearchInfo;
import com.gnomes.common.search.data.SearchSetting;
import com.gnomes.common.tags.GnomesCTagDictionary;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.GnomesDateUtil;
import com.gnomes.system.dao.TableSearchSettingDao;
import com.gnomes.system.data.TableSearchSettingFunctionBean;
import com.gnomes.system.entity.TableSearchSetting;

/**
 * テーブル検索条件管理 機能
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/02/02 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class BLTableSearchSetting extends BaseLogic
{

    @Inject
    TableSearchSettingFunctionBean tableSearchSettingFunctionBean;

    @Inject
    TableSearchSettingDao          tableSearchSettingDao;

    @Inject
    protected SearchInfoController searchInfoController;

    /**
     * 検索条件 保存処理
     * @throws Exception
     */
    @TraceMonitor
    @ErrorHandling
    public void save() throws Exception
    {
        String userId = req.getUserId();
        String saveScreenId = tableSearchSettingFunctionBean.getSaveScreenId();
        String tableId = tableSearchSettingFunctionBean.getTableId();
        int settingType = tableSearchSettingFunctionBean.getSettingType();
        SearchSetting setting = tableSearchSettingFunctionBean.getSetting();

        String jsonSetting = getJsonSaveSearchSetting(settingType, tableId, setting);

        // テーブルタグ名は定義上の画面ID.XXXX
        int index = tableId.indexOf(GnomesCTagDictionary.ID_SEPARATOR);
        String tabelTag = tableId.substring(index, tableId.length());

        // 保存されている検索条件のキーは
        // 表示上の画面ID.XXXX
        String saveTableTagName = saveScreenId + tabelTag;

        TableSearchSetting data = tableSearchSettingDao.getTableSearchSetting(userId, saveTableTagName, settingType);
        if (data == null) {
            data = new TableSearchSetting();
            data.setTable_search_setting_key(UUID.randomUUID().toString());
            data.setUser_id(userId);
            data.setTable_id(saveTableTagName);
            data.setSetting_type(settingType);
            data.setSetting(jsonSetting);
            data.setReq(req);
            tableSearchSettingDao.insert(data);
        }
        else {
            data.setSetting(jsonSetting);
            data.setReq(req);
            tableSearchSettingDao.update(data);
        }
    }

    /**
     * 検索条件 削除処理
     * @throws Exception
     */
    @TraceMonitor
    @ErrorHandling
    public void delete() throws Exception
    {
        String userId = req.getUserId();
        String saveScreenId = tableSearchSettingFunctionBean.getSaveScreenId();
        String tableId = tableSearchSettingFunctionBean.getTableId();
        int settingType = tableSearchSettingFunctionBean.getSettingType();

        // テーブルタグ名は定義上の画面ID.XXXX
        int index = tableId.indexOf(GnomesCTagDictionary.ID_SEPARATOR);
        String tabelTag = tableId.substring(index, tableId.length());

        // 保存されている検索条件のキーは
        // 表示上の画面ID.XXXX
        String saveTableTagName = saveScreenId + tabelTag;

        TableSearchSetting data = tableSearchSettingDao.getTableSearchSetting(userId, saveTableTagName, settingType);
        tableSearchSettingDao.delete(data);
    }

    /**
     * 保存用の検索条件を作成
     * @param type 設定種類
     * @param tableId テーブルID
     * @param setting 検索条件設定
     * @return JSON文字列の検索条件設定
     * @throws Exception
     */
    public String getJsonSaveSearchSetting(int type, String tableId, SearchSetting setting) throws Exception
    {

        // 検索条件の場合
        if (type == TableSearchSettingType.SEARCH.getValue()) {

            MstSearchInfo mst = searchInfoController.getMstSearchInfo(tableId);

            List<ConditionInfo> lstConditionInfo = new ArrayList<>();

            for (ConditionInfo condition : setting.getConditionInfos()) {
                MstCondition masterCondition = mst.getMstCondition(condition.getColumnId());
                //条件マスターにないものは無視して次に進む
                if (masterCondition == null) {
                    continue;
                }
                if (masterCondition.getSaveParamTypes() == null || condition.getParameters() == null) {
                    lstConditionInfo.add(condition);
                    continue;
                }

                ConditionInfo newCondition = new ConditionInfo();
                newCondition.setColumnId(condition.getColumnId());
                newCondition.setEnable(condition.isEnable());
                newCondition.setPatternKeys(condition.getPatternKeys());
                newCondition.setHiddenItem(condition.isHiddenItem());

                List<String> newParams = new ArrayList<>();
                // 拡張情報
                List<String> newParamsExt = new ArrayList<>();
                for (int i = 0; i < condition.getParameters().size(); i++) {
                    ConditionParamSaveType saveType = masterCondition.getSaveParamTypes().get(i);

                    if (saveType == null || saveType == ConditionParamSaveType.SAVE) {
                        newParams.add(condition.getParameters().get(i));
                        newParamsExt.add(null);
                    }
                    else if (saveType == ConditionParamSaveType.SYSTEM_DATE_SAVE) {

                        if (condition.getParameters().get(i) != null && condition.getParameters().get(i).length() > 0) {

                            //newParams.add(condition.getParameters().get(i));
                            String inputLocaleDate = "";
                            String format = "";

                            // 通常情報は差分日数
                            ConditionDateType conditionDateType;
                            if (masterCondition.getType() == ConditionType.DATE || masterCondition.getType() == ConditionType.MULTIFORMAT_DATESTR) {
                                conditionDateType = ConditionDateType.DATE;
                            }
                            else if (masterCondition.getType() == ConditionType.DATE_TIME) {
                                conditionDateType = ConditionDateType.DATE_TIME;
                            }
                            else if (masterCondition.getType() == ConditionType.DATE_TIME_SS) {
                                conditionDateType = ConditionDateType.DATE_TIME_SS;
                            }
                            else if (masterCondition.getType() == ConditionType.DATE_TIME_MM00) {
                                conditionDateType = ConditionDateType.DATE_TIME_MM00;
                            }
                            else if (masterCondition.getType() == ConditionType.DATE_TIME_SS00) {
                                conditionDateType = ConditionDateType.DATE_TIME_SS00;
                            }
                            else {
                                conditionDateType = ConditionDateType.DATE_YM;
                            }

                            format = ResourcesHandler.getString(conditionDateType.getFormat(),
                                    gnomesSessionBean.getUserLocale());
                            inputLocaleDate = condition.getParameters().get(i);

                            // 現在
                            Date dt = new Date();

                            // 指定日
                            Date toDt = ConverterUtils.stringToDateFormat(inputLocaleDate, format);

                            Date localnewDate = new Date();

                            //Web.xmlのタイムゾーンがUTCの場合,
                            if (CommonConstants.ZONEID_UTC.equals(TimeZone.getDefault().getID())) {
                                // ロケール(タイムゾーン)のString型の値をUTCのDate型に変換する
                                localnewDate = GnomesDateUtil.convertLocalStringToDateFormat(inputLocaleDate, format,
                                        gnomesSessionBean.getUserTimeZone(), gnomesSessionBean.getUserLocale());
                                // UTCのDateから経過ミリ秒に変換する
                                long toDtTime = localnewDate.getTime();

                                condition.getParameters().set(i, Long.toString(toDtTime));
                                newParams.add(condition.getParameters().get(i));
                            }
                            else {
                                //Web.xmlのタイムゾーンがUTCではない場合は変換を行わない
                                //日付_フォーマットで保存する
                                newParams.add(condition.getParameters().get(i) + CommonConstants.SPLIT_CHAR + format);
                            }

                            // 差分日数
                            int day = getDiffDays(dt, toDt);

                            // 拡張情報側には、差分日数
                            newParamsExt.add(String.valueOf(day));

                        }
                        else {
                            newParams.add(condition.getParameters().get(i));
                            newParamsExt.add(null);
                        }

                    }
                    else {
                        // NO_SAVE
                        newParams.add("");
                        newParamsExt.add(null);
                    }
                }
                newParams.addAll(newParamsExt);
                newCondition.setParameters(newParams);
                lstConditionInfo.add(newCondition);
            }
            setting.setConditionInfos(lstConditionInfo);
        }

        return ConverterUtils.getJson(setting);
    }

    /**
     * <p>[概 要] 日付の差分日数取得処理</p>
     * <p>[詳 細] </p>
     * <p>[備 考] </p>
     * @param  fromDate 開始日付
     * @param  toDate 終了日付
     * @return 差分日数（パラメータがnullの場合は0を返します。）
     */
    protected int getDiffDays(Date fromDate, Date toDate)
    {

        int diffDays = 0;
        if (fromDate != null && toDate != null) {

            Date fromTran = DateUtils.truncate(fromDate, Calendar.DAY_OF_MONTH);
            Date toTran = DateUtils.truncate(toDate, Calendar.DAY_OF_MONTH);

            long one_date_time = 1000L * 60L * 60L * 24L;
            long datetime1 = fromTran.getTime();
            long datetime2 = toTran.getTime();
            diffDays = (int) ((datetime2 - datetime1) / one_date_time);

        }

        return diffDays;

    }

}
