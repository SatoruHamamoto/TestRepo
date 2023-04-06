package com.gnomes.common.search;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.picketbox.util.StringUtil;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.data.BaseFunctionBean;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.dto.PullDownDto;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.persistence.GnomesEntityManager;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.search.data.ConditionInfo;
import com.gnomes.common.search.data.MstCondition;
import com.gnomes.common.search.data.MstOrdering;
import com.gnomes.common.search.data.MstSearchInfo;
import com.gnomes.common.search.data.OrderingInfo;
import com.gnomes.common.search.data.SearchInfoPack;
import com.gnomes.common.search.data.SearchSetting;
import com.gnomes.common.search.serializer.ConditionParamSaveTypeDeserializer;
import com.gnomes.common.search.serializer.ConditionParamSaveTypeSerializer;
import com.gnomes.common.search.serializer.ConditionRequiredTypeDeserializer;
import com.gnomes.common.search.serializer.ConditionRequiredTypeSerializer;
import com.gnomes.common.search.serializer.ConditionTypeDeserializer;
import com.gnomes.common.search.serializer.ConditionTypeSerializer;
import com.gnomes.common.search.serializer.OrderTypeDeserializer;
import com.gnomes.common.search.serializer.OrderTypeSerializer;
import com.gnomes.common.tags.GnomesCTagDictionary;
import com.gnomes.common.tags.GnomesCTagTable;
import com.gnomes.common.util.ConverterUtils;

/**
 * 検索共通  検索情報処理クラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/03/01 YJP/30022467              初版
 * R0.01.02 2018/08/29 YJP/S.Hamamoto            検索条件の定義エラーがわかるようにException Logを補足用に追加
 * R0.01.03 2019/05/27 YJP/S.Hamamoto            ソートや検索条件のマスター検索にindexを廃止、カラム名を使うように変更
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */

@RequestScoped
public class SearchInfoController
{

    @Inject
    GnomesExceptionFactory        exceptionFactory;

    //@Inject
    //GnomesTagDictionary gnomesTagDictionary;

    @Inject
    GnomesCTagDictionary          gnomesCTagDictionary;

    @Inject
    protected GnomesEntityManager em;

    @Inject
    protected GnomesSessionBean   gnomesSessionBean;

    @Inject
    transient Logger              logger;

    /** ローカルストレージ　検索情報キーフォーマット */
    public static final String    FMT_LOCAL_STRAGE_KEY = "{0}_SearchInfo_{1}";

    /** カラム名 NullPointerException Message */
    private static final String   MESSAGE_COLUMNNAME   = "columnName";

    /** 検索項目表示順設定無し */
    protected static final String DISPLAY_ORDER_NO = "0";

    /** 検索項目表示順設定有り */
    protected static final String DISPLAY_ORDER_YES = "1";

    /** 条件タイプ */
    @JsonSerialize(using = ConditionTypeSerializer.class)
    @JsonDeserialize(using = ConditionTypeDeserializer.class)
    public enum ConditionType
    {
        STRING_LIKE(0), // 文字入力 (含む、から始まるなど）
        STRING_PULLDOWN(1), // 文字 プルダウン
        NUMBER_PULLDOWN(2), // 数値 プルダウン
        NUMBER(3), // 数値入力
        DATE(4), // 日付（年月日、
        DATE_TIME(5), // 日時 (年月日時間（分まで
        DATE_TIME_SS(6), // 日時 (年月日時間（秒まで
        STRING_PULLDOWN_AUTOCOMPLETE(7), // 文字 プルダウン(オートコンプリート有り) value + text 表示
        CHECKBOX(8), // チェックボタン
        RADIOBUTTON(9), // ラジオボタン
        NUMBER_PULLDOWN_RANGE(10), // from (数値 プルダウン) to (数値 プルダウン)
        NUMBER_PULLDOWN_AUTOCOMPLETE(11), // 数値 プルダウン(オートコンプリート有り)
        NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE(12), // from (数値 プルダウン) to (数値 プルダウン)(オートコンプリート有り)
        STRING_PULLDOWN_AUTOCOMPLETE_1(13), // 文字 プルダウン(オートコンプリート有り) valueのみ表示 (mode 1)
        STRING_PULLDOWN_AUTOCOMPLETE_2(14), // 文字 プルダウン(オートコンプリート有り) textのみ表示  (mode 2)
        NUMBER_PULLDOWN_AUTOCOMPLETE_1(15), // 数値 プルダウン(オートコンプリート有り) valueのみ表示 (mode 1)
        NUMBER_PULLDOWN_AUTOCOMPLETE_2(16), // 数値 プルダウン(オートコンプリート有り) textのみ表示  (mode 2)
        NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE_1(17), // from (数値 プルダウン) to (数値 プルダウン)(オートコンプリート有り) valueのみ表示 (mode 1)
        NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE_2(18), // from (数値 プルダウン) to (数値 プルダウン)(オートコンプリート有り) textのみ表示  (mode 2)
        DATE_YM(19), // 日付（年月、
        MULTIFORMAT_DATESTR(20), //日付が入った文字でかつ年月や年月日などフォーマットが複数ある
        DATE_TIME_MM00(21), // 日時 (年月日時間（分まで,分は00固定
        DATE_TIME_SS00(22), // 日時 (年月日時間（秒まで,秒は00固定
        DATE_YM_BASIS(23); // 日付（年月
        
        private int value;

        private ConditionType(int n)
        {
            this.value = n;
        }

        public int getValue()
        {
            return this.value;
        }

        public static ConditionType getEnum(int num)
        {
            // enum型全てを取得します。
            ConditionType[] enumArray = ConditionType.values();

            // 取得出来たenum型分ループします。
            for (ConditionType enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }
    }

    /** 検索条件 必須タイプ */
    @JsonSerialize(using = ConditionRequiredTypeSerializer.class)
    @JsonDeserialize(using = ConditionRequiredTypeDeserializer.class)
    public enum ConditionRequiredType
    {
        NONE(0), // 必須でない
        REQUIRED(1); // 必須

        private int value;

        private ConditionRequiredType(int n)
        {
            this.value = n;
        }

        public int getValue()
        {
            return this.value;
        }

        public static ConditionRequiredType getEnum(int num)
        {
            // enum型全てを取得します。
            ConditionRequiredType[] enumArray = ConditionRequiredType.values();

            // 取得出来たenum型分ループします。
            for (ConditionRequiredType enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }
    }

    /** パラメータ保存タイプ */
    @JsonSerialize(using = ConditionParamSaveTypeSerializer.class)
    @JsonDeserialize(using = ConditionParamSaveTypeDeserializer.class)
    public enum ConditionParamSaveType
    {
        NO_SAVE(0), // 保存なし
        SAVE(1), // そのまま保存
        SYSTEM_DATE_SAVE(2); // システム日付から算出保存

        private int value;

        private ConditionParamSaveType(int n)
        {
            this.value = n;
        }

        public int getValue()
        {
            return this.value;
        }

        public static ConditionParamSaveType getEnum(int num)
        {
            // enum型全てを取得します。
            ConditionParamSaveType[] enumArray = ConditionParamSaveType.values();

            // 取得出来たenum型分ループします。
            for (ConditionParamSaveType enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }

    }

    /** 曖昧タイプ */
    public enum ConditionLikeType
    {
        ConditionLikeType_INCLUDE(0, "%{0}%"), // 含む
        ConditionLikeType_STARTS_FROM(1, "{0}%"), // から始まる
        ConditionLikeType_ENDS_BY(2, "%{0}"), // で終わる
        ConditionLikeType_PARALLEL_WITH(3, "{0}"); // 一致する

        private int    value;
        private String sqlFormat;

        private ConditionLikeType(int n, String f)
        {
            this.value = n;
            this.sqlFormat = f;
        }

        public int getValue()
        {
            return this.value;
        }

        public String getSqlFormat()
        {
            return this.sqlFormat;
        }

        public static ConditionLikeType getEnum(int num)
        {
            // enum型全てを取得します。
            ConditionLikeType[] enumArray = ConditionLikeType.values();

            // 取得出来たenum型分ループします。
            for (ConditionLikeType enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }
    }

    /**
     * 条件 日付タイプ<br>
     * 検索条件ダイアログで使うコントローラの中で使用する
     */
    public enum ConditionDateType
    {
        DATE(0, GnomesResourcesConstants.YY01_0003), // 年月日
        DATE_TIME(1, GnomesResourcesConstants.YY01_0002), // 年月日時間（分まで）
        DATE_TIME_SS(2, GnomesResourcesConstants.YY01_0001), // 年月日時間（秒まで）
        DATE_YM(3, GnomesResourcesConstants.YY01_0094), // 年月
    	DATE_TIME_MM00(4, GnomesResourcesConstants.YY01_0109), // 年月日時間（分まで、分は00固定）
    	DATE_TIME_SS00(5, GnomesResourcesConstants.YY01_0110), // 年月日時間（秒まで、秒は00固定）
        DATE_YM_BASIS(6, GnomesResourcesConstants.YY01_0094); // 年月

        private int    value;
        private String format;

        private ConditionDateType(int n, String f)
        {
            this.value = n;
            this.format = f;
        }

        public int getValue()
        {
            return this.value;
        }

        public String getFormat()
        {
            return this.format;
        }

        public static ConditionDateType getEnum(int num)
        {
            // enum型全てを取得します。
            ConditionDateType[] enumArray = ConditionDateType.values();

            // 取得出来たenum型分ループします。
            for (ConditionDateType enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }

    }

    /** 順序タイプ */
    @JsonSerialize(using = OrderTypeSerializer.class)
    @JsonDeserialize(using = OrderTypeDeserializer.class)
    public enum OrderType
    {
        NONE(-1), // ソートなし
        ASCENDING(0), DESCENDING(1);

        private int value;

        private OrderType(int n)
        {
            this.value = n;
        }

        public int getValue()
        {
            return this.value;
        }

        public static OrderType getEnum(int num)
        {
            // enum型全てを取得します。
            OrderType[] enumArray = OrderType.values();

            // 取得出来たenum型分ループします。
            for (OrderType enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (num == enumInt.getValue()) {
                    return enumInt;
                }
            }
            return null;
        }
    }

    /**
     * 条件マスター情報を取得
     *
     * @param searchInfoPack 検索ダイアログ情報
     * @param index 条件マスター情報Index
     * @return 条件マスター情報
     */
    public static MstCondition geMstCondition(SearchInfoPack searchInfoPack, int index)
    {

        MstCondition mst = searchInfoPack.getMstConditions().get(index);
        return mst;
    }

    /**
     * カラムIDで条件マスターを取得
     * @param searchInfoPack 検索ダイアログ情報
     * @param columnId 条件マスター情報キー
     * @return 条件マスター情報
     */
    public static MstCondition geMstCondition(SearchInfoPack searchInfoPack, String columnId)
    {
        MstCondition mst = searchInfoPack.getMstConditions(columnId);
        return mst;
    }

    /**
     * 順序マスター情報を取得
     *
     * @param searchInfoPack 検索ダイアログ情報
     * @param index 順序マスター情報Index
     * @return 順序マスター情報
     */
    public static MstOrdering getMstOrdering(SearchInfoPack searchInfoPack, int index)
    {

        MstOrdering mst = searchInfoPack.getMstOrdering().get(index);
        return mst;
    }

    /**
     *
     * カラムIDを指定して順序マスター情報を取得
     *
     * @param searchInfoPack 検索ダイアログ情報
     * @param columnId カラムID
     * @return 順序マスター情報
     */
    public static MstOrdering getMstOrdering(SearchInfoPack searchInfoPack, String columnId)
    {
        MstOrdering mst = searchInfoPack.getMstOrdering(columnId);
        return mst;
    }

    /**
     * コンストラクタ
     */
    public SearchInfoController()
    {
    }

    /** 項目名　リソース */
    protected static final String DIC_SEARCH_TITLE_RESOCE_ID = "search_title_resource_id";

    /** 項目名　物理 */
    protected static final String DIC_SEARCH_COLUMN_NAME     = "search_column_name";

    /** 項目名 パラメータ名 */
    protected static final String DIC_PARAM_NAME             = "param_name";

    /** 検索有無  0:なし 1:あり */
    //    private static final String DIC_IS_SEARCH = "is_search";

    /** 検索種別 */
    protected static final String DIC_CONDITION_TYPE         = "condition_type";

    /** 検索　プルダウン　Enum名(クラス名) */
    protected static final String DIC_CONDITION_ENUM         = "condition_enum";

    /** 検索　プルダウン　Query名指定 */
    protected static final String DIC_CONDITION_QUERY        = "condition_query";

    /** 検索条件　プルダウンキー　初期値 */
    protected static final String DIC_CONDITION_KEY          = "condition_type_key";

    /** 検索条件　パラメータ1 初期値 */
    protected static final String DIC_CONDITION_PARAM1       = "condition_param1";

    /** 検索条件　パラメータ2 初期値 */
    protected static final String DIC_CONDITION_PARAM2       = "condition_param2";

    /** 検索条件　パラメータ3 初期値 */
    protected static final String DIC_CONDITION_PARAM3       = "condition_param3";

    /** 検索条件　パラメータ4 初期値 */
    protected static final String DIC_CONDITION_PARAM4       = "condition_param4";

    /** 検索保存タイプ1 */
    protected static final String DIC_CONDITION_SAVE_TYPE1   = "condition_save_types1";

    /** 検索保存タイプ2 */
    protected static final String DIC_CONDITION_SAVE_TYPE2   = "condition_save_types2";

    /** 検索条件 有効 */
    protected static final String DIC_CONDITION_ENABLE       = "condition_enable";

    /** 検索条件 必須 */
    protected static final String DIC_CONDITION_REQUIRED     = "condition_required";

    /** 表示有無 0:表示 1:非表示 初期値 */
    protected static final String DIC_HIDDEN                 = "hidden";

    /** ソート設定順 初期値 */
    protected static final String DIC_ORDER_NUM              = "order_num";

    /** ソート設定値 必須 */
    protected static final String DIC_SORT_REQUIRED          = "sort_required";

    /** ソート方向 初期値 */
    protected static final String DIC_ORDER_TYPE             = "order_type";

    /** オートコンプリート設定 */
    protected static final String DIC_LIST_AUTOCOMPLETE      = "list_autocomplete";

    /** enumの一覧取得　メソッド名 */
    protected static final String METHOD_ENUM_GET_VALUES     = "values";

    /** enumの値取得　メソッド名 */
    protected static final String METHOD_ENUM_GET_VALUE      = "getValue";

    /** enumのリソースID取得　メソッド名 */
    protected static final String METHOD_ENUM_GET_RESOUCE_ID = "toString";

    /** falseを表す */
    protected static final String DIC_OFF                    = "0";

    /** trueを表す  */
    //protected static final String DIC_ON = "1";

    /** オートコンプリートモード: value表示 */
    protected static final String AUTO_MODE_VALUE            = "1";

    /** オートコンプリートモード: text表示 */
    protected static final String AUTO_MODE_TEXT             = "2";

    /**
     * 検索マスターを取得する
     * @param tableTagId テーブルタグID
     * @return 検索マスター
     * @throws Exception 例外
     */
    public MstSearchInfo getMstSearchInfo(String tableTagId) throws Exception
    {
        return getMstSearchInfo(tableTagId, Locale.getDefault());
    }

    /**
     * 検索マスターを取得する
     * @param tableTagId テーブルタグID
     * @param userLocale ユーザロケール
     * @return 検索マスター
     * @throws Exception 例外
     */
    public MstSearchInfo getMstSearchInfo(String tableTagId, Locale userLocale) throws Exception
    {

        MstSearchInfo mstSearchInfo = new MstSearchInfo();

        // 条件マスタ
        List<MstCondition> lstMstCondition = new ArrayList<MstCondition>();

        // 条件設定
        List<ConditionInfo> lstConditionInfo = new ArrayList<ConditionInfo>();

        // 順序マスタ
        List<MstOrdering> lstMstOrdering = new ArrayList<MstOrdering>();

        // 順序表示 初期設定
        List<OrderingInfo> lstOrderingInfo = new ArrayList<OrderingInfo>();

        try {
            Map<String, Object> map = gnomesCTagDictionary.getTableInfo(tableTagId);

            List<Map<String, Object>> lstTableInfo = gnomesCTagDictionary.getTableColumnInfo(map);

            for (int index = 0; index < lstTableInfo.size(); index++) {

                Map<String, Object> tr = lstTableInfo.get(index);
                @SuppressWarnings("unchecked")
                Map<String, String> headInfo = (Map<String, String>) tr.get(GnomesCTagDictionary.MAP_NAME_TABLE_INFO);
                @SuppressWarnings("unchecked")
                Map<String, Object> mapColInfo = (Map<String, Object>) tr.get(
                        GnomesCTagDictionary.MAP_NAME_COLUMN_INFO);

                String type = null;
                String param_name = (String) mapColInfo.get(DIC_PARAM_NAME);

                //パラメータ名が空白の場合があり、空白だったら登録しない
                if (StringUtil.isNullOrEmpty(param_name)) {
                    continue;
                }

                if (headInfo != null) {
                    type = headInfo.get(GnomesCTagTable.INFO_TAG_TYPE);
                }
                if (type == null || type.length() == 0) {
                    type = (String) mapColInfo.get(GnomesCTagTable.INFO_TAG_TYPE);
                }

                // 検索ダイアログ 表示文言のリソースID
                String resourceId = (String) mapColInfo.get(DIC_SEARCH_TITLE_RESOCE_ID);
                String text = null;

                if (resourceId == null) {
                    // 未設定の場合、テーブルのラベルと同じ
                    if (headInfo != null) {
                        resourceId = headInfo.get(GnomesCTagDictionary.MAP_NAME_RESOURCE_ID);
                    }
                }
                if (!StringUtil.isNullOrEmpty(resourceId)) {
                    text = ResourcesHandler.getString(resourceId, userLocale);
                }

                // カラム名は定義シートのパラメータ名を使用する
                String columnName = (String) formatParamName(param_name);

                // 検索カラム名は物理カラム名を指定する
                String search_column_name = (String) mapColInfo.get(DIC_SEARCH_COLUMN_NAME);

                // 検索対象有無
                if (mapColInfo.get(DIC_CONDITION_TYPE) != null) {
                    if (StringUtil.isNullOrEmpty(columnName)) {
                        throw new NullPointerException(MESSAGE_COLUMNNAME);

                    }
                    // 条件マスタ、初期条件の設定
                    //SQLに渡すのでカラム物理名で設定する
                    this.putCondition(em.getEntityManager(), lstMstCondition, lstConditionInfo, mapColInfo, columnName,
                            search_column_name, text, userLocale);
                }

                if (!type.equals(GnomesCTagTable.TAG_TYPE_HIDDEN)) {
                    // 順序、表示の設定
                    //SQLに渡すのでカラム物理名で設定する
                    this.putOrdering(lstMstOrdering, lstOrderingInfo, mapColInfo, columnName, search_column_name, text,
                            index, userLocale);
                }
            }

            SearchSetting ss = new SearchSetting();

            // 並び替えの必要があるは判断
            boolean isSort = false;
            for (ConditionInfo conditionInfo: lstConditionInfo) {
                if (conditionInfo.isEnable().equals(DISPLAY_ORDER_YES)) {
                    isSort = true;
                }
            }

            // 順序で並び替え
            if (isSort) {
                Collections.sort(lstConditionInfo, new Comparator<ConditionInfo>() {
                    public int compare(ConditionInfo value1, ConditionInfo value2) {
                        //大小比較を行う。昇順の場合はvalue1-value2
                        return Integer.parseInt(value1.isEnable()) - Integer.parseInt(value2.isEnable());
                    }
                });
            }
            
            ss.setConditionInfos(lstConditionInfo);
            ss.setOrderingInfos(lstOrderingInfo);

            // 列固定値
            if (map.containsKey(GnomesCTagTable.INFO_COLS)) {
                ss.setFixedColNum(Integer.valueOf((String) map.get(GnomesCTagTable.INFO_COLS)));
            }
            else {
                ss.setFixedColNum(0);
            }

            mstSearchInfo.setMstConditions(lstMstCondition);
            mstSearchInfo.setMstOrdering(lstMstOrdering);
            mstSearchInfo.setDefaultSearchSetting(ss);
        }
        catch (Exception e) {

            logger.severe(MessagesHandler.getString(GnomesMessagesConstants.ME01_0078) + " tableTagId = " + tableTagId);
            logger.severe("Exception Message=" + e.getMessage());

            // ME01.0078:「検索マスター取得時にエラーが発生しました。詳細はエラーメッセージを確認してください。タグID：{0}、言語：{1}」
            throw exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0078, e, tableTagId,
                    userLocale);

        }

        return mstSearchInfo;
    }

    /**
     *
     * 受けた文字に"planProgress,resultProgress"のように","が入っているものがあるので
     * "planProgress_resultProgress"のように"_"を置き換える
     *
     * @param src
     * @return
     */
    protected String formatParamName(String src)
    {

        return src.replace(",", "_");

    }

    /**
     * 検索情報を取得する
     * @param jsonMstSearchInfo 検索マスタ(JSON)
     * @param jsonSearchSetting 検索条件(JSON)
     * @return 検索情報
     * @throws Exception 例外
     */
    public SearchInfoPack getSearchInfoPack(String jsonMstSearchInfo, String jsonSearchSetting) throws Exception
    {

        MstSearchInfo mst = ConverterUtils.readJson(jsonMstSearchInfo, MstSearchInfo.class);
        SearchSetting search = ConverterUtils.readJson(jsonSearchSetting, SearchSetting.class);

        return getSearchInfoPack(mst, search);
    }

    /**
     * 検索情報を取得する
     * @param mstSearchInfo 検索マスタ
     * @param searchSetting 検索条件
     * @return 検索情報
     */
    public SearchInfoPack getSearchInfoPack(MstSearchInfo mstSearchInfo, SearchSetting searchSetting)
    {

        SearchInfoPack searchInfoPack = new SearchInfoPack();
        searchInfoPack.setMstConditions(mstSearchInfo.getMstConditions());
        searchInfoPack.setMstOrdering(mstSearchInfo.getMstOrdering());
        searchInfoPack.setSearchSetting(searchSetting);

        return searchInfoPack;

    }

    /**
     * パラメータ設定
     * @param mstSearchInfo 検索マスタ
     * @param searchSetting 検索条件
     * @param columnId カラムUD
     * @param param 設定値
     */
    public void setConditionParam(MstSearchInfo mstSearchInfo, SearchSetting searchSetting, String columnId,
            String[] param)
    {

        this.setSettingConditionParam(mstSearchInfo.getDefaultSearchSetting(), columnId, param);
        this.setSettingConditionParam(searchSetting, columnId, param);
    }

    /**
     * パラメータ設定
     * @param searchSetting 検索条件
     * @param columnId カラムID
     * @param param 設定値
     */
    private void setSettingConditionParam(SearchSetting searchSetting, String columnId, String[] param)
    {

        ConditionInfo c = searchSetting.getConditionInfo(columnId);
        if (c != null) {
            List<String> paramlist = Arrays.asList(param);
            c.setParameters(paramlist);
        }
    }

    /**
     * 条件マスタ、初期条件の設定
     * @param em エンティティマネージャー
     * @param mcLst 条件マスタリスト
     * @param ciLst 条件設定リスト
     * @param mapColInfo テーブルのカラム辞書
     * @param columnName カラム名
     * @param search_column_name 物理カラム名(DB)
     * @param text 項目名
     * @param resBundle リソースバンドル
     * @throws Exception 例外
     */
    public void putCondition(EntityManager em, List<MstCondition> mcLst, List<ConditionInfo> ciLst,
            Map<String, Object> mapColInfo, String columnName, String search_column_name, String text,
            Locale userLocale) throws Exception
    {

        // 条件マスタ
        MstCondition mc = new MstCondition();
        mc.setColumnName(columnName);
        mc.setSearch_column_name(search_column_name);
        mc.setText(text);

        // 検索種別の設定
        this.putMstConditionType(em, mapColInfo, mc, userLocale);
        mcLst.add(mc);

        // プルダウンキー
        List<String> strConditionTypeKeys = getStrConditionTypeKey(mapColInfo);

        // パラメータ1
        String strParam1 = (String) mapColInfo.get(DIC_CONDITION_PARAM1);

        // パラメータ2
        String strParam2 = (String) mapColInfo.get(DIC_CONDITION_PARAM2);

        // 有効無効
        String enable = (String) mapColInfo.get(DIC_CONDITION_ENABLE);

        String columnId = (String) formatParamName((String) mapColInfo.get(DIC_PARAM_NAME));

        int conditionNotAddCheckCount = 0;
        //
        //以下の条件に一致する場合は条件を追加しない
        //
        //1.columnIDが空白またはnullの場合
        if (StringUtil.isNullOrEmpty(columnId)) {
            return;
        }

        //2.enableがOFFの場合
        if (enable == null || enable.equals(DIC_OFF)) {
            return;
        }

        //
        //条件をクリアしたものは、引数のciLstに追加する
        //

        // 条件設定
        ConditionInfo ci = new ConditionInfo();
        ci.setEnable(enable);
        ci.setColumnId(columnId);

        if (strConditionTypeKeys.size() > 0) {
            ci.getPatternKeys().addAll(strConditionTypeKeys);
        }
        if (mc.getType() == ConditionType.DATE ||
        	mc.getType() == ConditionType.DATE_TIME ||
        	mc.getType() == ConditionType.DATE_TIME_SS ||
        	mc.getType() == ConditionType.DATE_YM || 
        	mc.getType() == ConditionType.DATE_TIME_MM00 || 
        	mc.getType() == ConditionType.DATE_TIME_SS00) {

            // 拡張情報
            List<String> extParams = new ArrayList<String>();

            List<ConditionParamSaveType> saveTypes = mc.getSaveParamTypes();

            if (strParam1 != null && strParam1.length() > 0) {
                if (saveTypes != null && saveTypes.get(0) == ConditionParamSaveType.SYSTEM_DATE_SAVE) {

                    String extParam = (String) mapColInfo.get(DIC_CONDITION_PARAM3);

                    ci.getParameters().add(getSysDateAddParam(mc.getType(), strParam1, extParam));
                    extParams.add(strParam1);
                }
                else {
                    ci.getParameters().add(strParam1);
                    extParams.add(null);
                }
            }
            else {
                ci.getParameters().add("");
                extParams.add(null);
            }

            if (strParam2 != null && strParam2.length() > 0) {
                if (saveTypes != null && saveTypes.get(1) == ConditionParamSaveType.SYSTEM_DATE_SAVE) {

                    String extParam = (String) mapColInfo.get(DIC_CONDITION_PARAM4);

                    ci.getParameters().add(getSysDateAddParam(mc.getType(), strParam2, extParam));
                    extParams.add(strParam2);
                }
                else {
                    ci.getParameters().add(strParam2);
                    extParams.add(null);
                }
            }
            else {
                ci.getParameters().add("");
                extParams.add(null);
            }
            ci.getParameters().addAll(extParams);

        }
        else {
            if (strParam1 != null && strParam1.length() > 0) {
                ci.getParameters().add(strParam1);
            }
            if (strParam2 != null && strParam2.length() > 0) {
                if (strParam1 == null || strParam1.length() == 0) {
                    ci.getParameters().add("");
                }
                ci.getParameters().add(strParam2);
            }
        }
        ciLst.add(ci);
    }

    /**
     * システム日付加算
     * @param conditionType タイプ
     * @param param パラメータ
     * @param extParam 拡張パラメータ
     * @return システム日付加算結果
     * @throws ParseException
     */
    protected String getSysDateAddParam(ConditionType conditionType, String param, String extParam) throws ParseException
    {

        ConditionDateType conditionDateType;

        String systemDateFormat = "";

        if (conditionType == ConditionType.DATE || conditionType == ConditionType.MULTIFORMAT_DATESTR) {
            systemDateFormat = CommonConstants.TABLE_SEARCH_DATE;
            conditionDateType = ConditionDateType.DATE;
        }
        else if (conditionType == ConditionType.DATE_TIME) {
            systemDateFormat = CommonConstants.TABLE_SEARCH_DATE_TIME;
            conditionDateType = ConditionDateType.DATE_TIME;
        }
        else if (conditionType == ConditionType.DATE_TIME_SS) {
            systemDateFormat = CommonConstants.TABLE_SEARCH_DATE_TIME_SS;
            conditionDateType = ConditionDateType.DATE_TIME_SS;
        }
        else if (conditionType == ConditionType.DATE_TIME_MM00) {
            systemDateFormat = CommonConstants.TABLE_SEARCH_DATE_TIME_MM00;
            conditionDateType = ConditionDateType.DATE_TIME_MM00;
        }
        else if (conditionType == ConditionType.DATE_TIME_SS00) {
            systemDateFormat = CommonConstants.TABLE_SEARCH_DATE_TIME_SS00;
            conditionDateType = ConditionDateType.DATE_TIME_SS00;
        }
        else {
            systemDateFormat = CommonConstants.TABLE_SEARCH_DATE_YM;
            conditionDateType = ConditionDateType.DATE_YM;
        }

        // 現在
        Date dt = new Date();
        // 加算日数
        int addDate = Integer.valueOf(param);

        // カレンダークラスのインスタンスを取得
        Calendar cal = Calendar.getInstance();

        // 現在時刻を設定
        cal.setTime(dt);

        // addDateを加算
        cal.add(Calendar.DATE, addDate);

        if (conditionType == ConditionType.DATE_TIME || conditionType == ConditionType.DATE_TIME_SS || conditionType == ConditionType.DATE_TIME_MM00 || conditionType == ConditionType.DATE_TIME_SS00) {

            Date orgDate = ConverterUtils.stringToDateFormat(extParam, systemDateFormat);

            // カレンダークラスのインスタンスを取得
            Calendar calOrg = Calendar.getInstance();
            calOrg.setTime(orgDate);

            cal.set(Calendar.HOUR_OF_DAY, calOrg.get(Calendar.HOUR_OF_DAY)); //時を指定する
            cal.set(Calendar.MINUTE, calOrg.get(Calendar.MINUTE)); //分を指定する

            if (conditionType == ConditionType.DATE_TIME_SS || conditionType == ConditionType.DATE_TIME_SS00) {
                cal.set(Calendar.SECOND, calOrg.get(Calendar.SECOND)); //秒を指定する
            }

        }

        String p = ConverterUtils.dateTimeToString(cal.getTime(), ResourcesHandler.getString(
                conditionDateType.getFormat(), gnomesSessionBean.getUserLocale()));
        return p;
    }

    /**
     * キーの取得
     * @param mapColInfo テーブルのカラム辞書
     * @return プルダウンキー
     * @throws Exception 例外
     */
    protected List<String> getStrConditionTypeKey(Map<String, Object> mapColInfo) throws Exception
    {

        List<String> result = new ArrayList<>();
        // キー
        String strConditionTypeKey = (String) mapColInfo.get(DIC_CONDITION_KEY);
        String[] strConditionTypeKeys = null;

        if (strConditionTypeKey != null && strConditionTypeKey.length() > 0) {
            strConditionTypeKeys = strConditionTypeKey.split("\\,");
        }
        else {
            strConditionTypeKeys = new String[0];
        }

        // ENUM
        String strConditionEnum = (String) mapColInfo.get(DIC_CONDITION_ENUM);

        if (strConditionTypeKeys != null && strConditionTypeKeys.length > 0 && strConditionEnum != null && strConditionEnum.length() > 0) {

            Class<?> c = Class.forName(strConditionEnum);

            Method method;
            method = c.getMethod(METHOD_ENUM_GET_VALUES, new Class[0]);
            Object[] objects = (Object[]) method.invoke(null, (Object[]) null);

            // 取得出来たenum型分ループします。
            for (Object enumInt : objects) {

                Method methodValue;
                methodValue = enumInt.getClass().getMethod(METHOD_ENUM_GET_VALUE, new Class[0]);

                Object value = methodValue.invoke(enumInt, (Object[]) null);

                Method methodResouceId;
                methodResouceId = enumInt.getClass().getMethod(METHOD_ENUM_GET_RESOUCE_ID, new Class[0]);

                String resouceId = (String) methodResouceId.invoke(enumInt, (Object[]) null);

                for (String k : strConditionTypeKeys) {
                    if (k.equals(resouceId)) {
                        result.add(String.valueOf(value));
                        break;
                    }
                }
            }
        }

        return result;
    }

    /**
     * 検索種別の設定
     * @param em エンティティマネージャー
     * @param mapColInfo テーブルのカラム辞書
     * @param mc 条件マスタリスト
     * @param resBundle リソースバンドル
     * @throws Exception 例外
     */
    public void putMstConditionType(EntityManager em, Map<String, Object> mapColInfo, MstCondition mc,
            Locale userLocale) throws Exception
    {
        // 検索種別
        String strConditionType = (String) mapColInfo.get(DIC_CONDITION_TYPE);
        ConditionType conditionType = getConditionType(strConditionType);

        // オートコンプリート変換
        String strAutoMode = (String) mapColInfo.get(DIC_LIST_AUTOCOMPLETE);
        if (!StringUtil.isNullOrEmpty(strAutoMode)) {
            if (conditionType == ConditionType.NUMBER_PULLDOWN_AUTOCOMPLETE) {
                if (strAutoMode.compareTo(AUTO_MODE_VALUE) == 0) {
                    conditionType = ConditionType.NUMBER_PULLDOWN_AUTOCOMPLETE_1;
                }
                else if (strAutoMode.compareTo(AUTO_MODE_TEXT) == 0) {
                    conditionType = ConditionType.NUMBER_PULLDOWN_AUTOCOMPLETE_2;
                }
            }
            else if (conditionType == ConditionType.NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE) {
                if (strAutoMode.compareTo(AUTO_MODE_VALUE) == 0) {
                    conditionType = ConditionType.NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE_1;
                }
                else if (strAutoMode.compareTo(AUTO_MODE_TEXT) == 0) {
                    conditionType = ConditionType.NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE_2;
                }
            }
            else if (conditionType == ConditionType.STRING_PULLDOWN_AUTOCOMPLETE) {
                if (strAutoMode.compareTo(AUTO_MODE_VALUE) == 0) {
                    conditionType = ConditionType.STRING_PULLDOWN_AUTOCOMPLETE_1;
                }
                else if (strAutoMode.compareTo(AUTO_MODE_TEXT) == 0) {
                    conditionType = ConditionType.STRING_PULLDOWN_AUTOCOMPLETE_2;
                }
            }
        }

        mc.setType(conditionType);

        // 必須タイプ
        String strConditionRequiredType = (String) mapColInfo.get(DIC_CONDITION_REQUIRED);
        if (strConditionRequiredType == null || strConditionRequiredType.trim().length() == 0) {
            mc.setRequiredType(ConditionRequiredType.NONE);
        }
        else {
            mc.setRequiredType(ConditionRequiredType.REQUIRED);
        }

        // 検索パラメータ保存タイプ
        List<ConditionParamSaveType> lstConditionSaveType = new ArrayList<>();

        // 保存タイプ1
        String strSaveType = (String) mapColInfo.get(DIC_CONDITION_SAVE_TYPE1);
        if (strSaveType != null && strSaveType.length() > 0) {
            lstConditionSaveType.add(getConditionParamSaveType(strSaveType));
        }
        else {
            lstConditionSaveType.add(null);
        }
        // 保存タイプ2
        strSaveType = (String) mapColInfo.get(DIC_CONDITION_SAVE_TYPE2);
        if (strSaveType != null && strSaveType.length() > 0) {
            lstConditionSaveType.add(getConditionParamSaveType(strSaveType));
        }
        else {
            lstConditionSaveType.add(null);
        }
        mc.setSaveParamTypes(lstConditionSaveType);

        // ENUM
        String strConditionEnum = (String) mapColInfo.get(DIC_CONDITION_ENUM);
        // Query
        String strConditionQuery = (String) mapColInfo.get(DIC_CONDITION_QUERY);

        if (strConditionEnum != null && strConditionEnum.length() > 0) {
            // Enumからリスト作成
            LinkedHashMap<String, String> ptn = this.getConditionEnum(strConditionEnum, conditionType, userLocale);
            mc.setPatterns(ptn);
        }
        else if (strConditionQuery != null && strConditionQuery.length() > 0) {
            // Queryからリスト作成
            LinkedHashMap<String, String> ptn = this.getConditionQuery(em, strConditionQuery, conditionType,
                    userLocale);
            mc.setPatterns(ptn);
        }
    }

    /**
     * 検索種別を取得
     * @param name 検索種別名
     * @return 検索種別
     */
    protected ConditionType getConditionType(String name)
    {
        ConditionType[] enumArray = ConditionType.values();

        // 取得出来たenum型分ループします。
        for (ConditionType enumType : enumArray) {
            if (enumType.toString().equals(name)) {
                return enumType;
            }
        }
        return null;
    }

    /**
     * 検索保存タイプを取得
     * @param name 検索保存タイプ名
     * @return 検索保存タイプ
     */
    protected ConditionParamSaveType getConditionParamSaveType(String name)
    {
        ConditionParamSaveType[] enumArray = ConditionParamSaveType.values();

        // 取得出来たenum型分ループします。
        for (ConditionParamSaveType enumType : enumArray) {
            if (enumType.toString().equals(name)) {
                return enumType;
            }
        }
        return null;
    }

    /**
     * 順序タイプを取得
     * @param name 順序タイプ名
     * @return 順序タイプ
     */
    private OrderType getOrderType(String name)
    {
        OrderType[] enumArray = OrderType.values();

        // 取得出来たenum型分ループします。
        for (OrderType enumType : enumArray) {
            if (enumType.toString().equals(name)) {
                return enumType;
            }
        }
        return null;
    }

    /**
     * Enumの一覧取得
     * @param conditionEnum enumのクラス名
     * @param resBundle リソースバンドル
     * @return ドロップダウン情報
     * @throws Exception 例外
     */
    protected LinkedHashMap<String, String> getConditionEnum(String conditionEnum, ConditionType conditionType,
            Locale userLocale) throws Exception
    {
        LinkedHashMap<String, String> ptn = new LinkedHashMap<String, String>();

        Class<?> c = Class.forName(conditionEnum);

        Method method;
        method = c.getMethod(METHOD_ENUM_GET_VALUES, new Class[0]);
        Object[] objects = (Object[]) method.invoke(null, (Object[]) null);

        // 取得出来たenum型分ループします。
        for (Object enumInt : objects) {

            Method methodValue;
            methodValue = enumInt.getClass().getMethod(METHOD_ENUM_GET_VALUE, new Class[0]);

            Object value = methodValue.invoke(enumInt, (Object[]) null);

            Method methodResouceId;
            methodResouceId = enumInt.getClass().getMethod(METHOD_ENUM_GET_RESOUCE_ID, new Class[0]);

            String resouceId = (String) methodResouceId.invoke(enumInt, (Object[]) null);

            String text = ResourcesHandler.getString(resouceId, userLocale);

            // 追加
            if (conditionType == ConditionType.NUMBER_PULLDOWN_RANGE) {
                // 表示は"値:テキスト"
                ptn.put(String.valueOf(value), String.valueOf(value) + ":" + text);
            }
            else {
                ptn.put(String.valueOf(value), text);
            }
        }

        return ptn;
    }

    /**
     * Queryの一覧取得
     * @param conditionQuery クエリ名
     * @param resBundle リソースバンドル
     * @return ドロップダウン情報
     */
    protected LinkedHashMap<String, String> getConditionQuery(EntityManager em, String conditionQuery,
            ConditionType conditionType, Locale userLocale)
    {
        LinkedHashMap<String, String> ptn = new LinkedHashMap<String, String>();

        // 作業定義IDプルダウン取得
        TypedQuery<PullDownDto> getPdQuery = em.createNamedQuery(conditionQuery, PullDownDto.class);

        List<PullDownDto> lstPullDownDto = getPdQuery.getResultList();

        for (PullDownDto pullDownDto : lstPullDownDto) {

            // 追加
            if (conditionType == ConditionType.NUMBER_PULLDOWN_RANGE) {
                // 表示は"値:テキスト"
                ptn.put(pullDownDto.getName(), pullDownDto.getName() + ":" + pullDownDto.getValue());
            }
            else {
                ptn.put(pullDownDto.getName(), pullDownDto.getValue());
            }
        }

        return ptn;
    }

    /**
     * 順序表示マスタと初期値の設定
     * @param moLst 順序マスタリスト
     * @param oiLst 順書設定リスト
     * @param mapColInfo テーブルのカラム辞書
     * @param columnName カラムID、カラム名
     * @param search_column_name 物理カラム名
     * @param text 項目名
     * @param index 項目index
     * @param resBundle リソースバンドル
     */
    private void putOrdering(List<MstOrdering> moLst, List<OrderingInfo> oiLst, Map<String, Object> mapColInfo,
            String columnName, String search_column_name, String text, int index, Locale userLocale)
    {
        // 順序マスタ
        MstOrdering mo = new MstOrdering();
        mo.setColumnName(columnName);
        mo.setText(text);
        mo.setSearch_column_name(search_column_name);
        mo.setTableTagColumnIndex(index);
        moLst.add(mo);

        // テーブルに表示有無
        String strHidden = (String) mapColInfo.get(DIC_HIDDEN);

        // ソート設定順
        String strOrderNum = (String) mapColInfo.get(DIC_ORDER_NUM);

        // ソート必須
        String sortRequired = (String) mapColInfo.get(DIC_SORT_REQUIRED);

        // ソート方向
        String strOrderType = (String) mapColInfo.get(DIC_ORDER_TYPE);

        // 順序表示 初期設定
        OrderingInfo oi = new OrderingInfo();
        oi.setColumnId(columnName);
        if (strHidden == null || strHidden.equals(DIC_OFF)) {
            // 表示
            oi.setHiddenTable(false);
        }
        else {
            // 非表示
            oi.setHiddenTable(true);
        }
        if (strOrderNum != null) {
            // ソート設定順の設定
            oi.setOrderNum(Integer.valueOf(strOrderNum));
        }

        if (columnName != null) {
            oi.setColumnId(columnName);
        }

        // 必須項目が入っていたら必須かどうかを設定する
        if (StringUtil.isNotNull(sortRequired)) {
            oi.setSortRequired(true);
        }
        else {
            oi.setSortRequired(false);
        }

        if (strOrderType != null) {
            oi.setOrderType(getOrderType(strOrderType));
        }
        else {
            // 初期未定義の場合
            oi.setOrderType(OrderType.ASCENDING);
        }
        oiLst.add(oi);
    }

    /**
     * 検索チェックをクリア
     * @param searchSetting 検索条件
     */
    public void clearNoCheckCondition(SearchSetting searchSetting)
    {

        List<ConditionInfo> newLstConditionInfo = new ArrayList<>();
        if (searchSetting.getConditionInfos() != null) {
            for (ConditionInfo conditionInfo : searchSetting.getConditionInfos()) {
                if( conditionInfo.isEnable() != null && !conditionInfo.isEnable().equals(DIC_OFF)){
                    newLstConditionInfo.add(conditionInfo);
                }
            }
        }
        searchSetting.setConditionInfos(newLstConditionInfo);
    }

    /**
     * カラムIndexを取得
     * @param m 検索マスタ
     * @param name カラム名
     * @return カラムIndex
     */
    private int getColIndex(MstSearchInfo m, String name)
    {
        List<MstCondition> ml = m.getMstConditions();

        for (int i = 0; i < ml.size(); i++) {
            if (ml.get(i).getColumnName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * カラムの条件マスター情報から条件タイプを取得
     * @param m 検索マスタ
     * @param name カラム名
     * @return カラム条件マスター情報
     */
    private ConditionType getTypeMstCondition(MstSearchInfo m, String name)
    {
        List<MstCondition> ml = m.getMstConditions();

        for (int i = 0; i < ml.size(); i++) {
            if (ml.get(i).getColumnName().equals(name)) {
                return ml.get(i).getType();
            }
        }
        return null;
    }

    /**
     * FunctionBeanに設定している検索条件をデフォルトにリセットする
     *
     * @param functionBean 対象のFunctionBean
     * @param tableId 対象のテーブルID
     */
    public void setDefaultSearchSetting(BaseFunctionBean functionBean, String tableId)
    {
        MstSearchInfo mstSrchInfo = functionBean.getMstSearchInfoMap().get(tableId);
        SearchSetting srchSetting = functionBean.getSearchSettingMap().get(tableId);
        SearchSetting defaultSrchSetting = mstSrchInfo.getDefaultSearchSetting();

        srchSetting.setConditionInfos(defaultSrchSetting.getConditionInfos());
    }

    /**
     * 検索条件を追加設定
     * @param functionBean ファンクションビーン
     * @param tableId テーブルID
     * @param columnName カラム名
     * @param param 検索パラメータ
     * @throws ParseException
     * @throws GnomesAppException
     */
    public void addConditon(BaseFunctionBean functionBean, String tableId, String columnName, String[] param)
    {

        MstSearchInfo m = functionBean.getMstSearchInfoMap().get(tableId);
        SearchSetting s = functionBean.getSearchSettingMap().get(tableId);

        if (m == null) {
            // 例外 functionBeanの検索条件マスターに存在しません。
            throw new IllegalArgumentException("functionBean mstSearchInfoMap Not Found tableId = " + String.valueOf(
                    tableId));
        }
        if (s == null) {
            // 例外 functionBeanの検索条件に存在しません。
            throw new IllegalArgumentException("functionBean searchSettingMap Not Found tableId = " + String.valueOf(
                    tableId));
        }
        //引数のカラム名はDBアイテム名を指定するのに対し
        //マスター検索情報はparam_name(パラメータ名）がキーで格納されているため
        //マスター検索情報をDBアイテム名で指定して検索するように変更
        MstCondition mstCondition = m.getMstConditionOfSearchColumnName(columnName);
        ConditionType type = mstCondition.getType();

        if (mstCondition == null || type == null) {
            // 例外 tableIdにcolumnNameが存在しません。
            throw new IllegalArgumentException("mstConditions Not Found tableId = " + String.valueOf(
                    tableId) + ",columnName = " + String.valueOf(columnName));
        }
        else {
            // プルダウンはキーを設定する
            // 検索のパターンによって設定する先がことなる
            try {
                setConditionParam(m, s, mstCondition, type, param);
            }
            catch(Exception ex)
            {
                // 例外 検索条件またはパラメータの内容に誤りがあります。
                throw new IllegalArgumentException("There is an error in the search condition or parameter content. tableId = " +
                        String.valueOf(tableId) + ",columnName = " + String.valueOf(columnName));
            }
        }
    }

    /**
     * 検索条件が保存データからロードされたものかの判定
     *
     * @param functionBean ファンクションビーン
     * @param tableId テーブルID
     * @return boolean true:保存データからロードされている, false:保存データからロードされていない
     */
    public boolean isConditonSaveDataLoaded(BaseFunctionBean functionBean, String tableId)
    {

        MstSearchInfo m = functionBean.getMstSearchInfoMap().get(tableId);

        if (m == null) {
            // 例外 functionBeanの検索条件マスターに存在しません。
            throw new IllegalArgumentException(
                    "isConditonSaveDataLoaded functionBean mstSearchInfoMap Not Found tableId = " + String.valueOf(
                            tableId));
        }
        return m.isConditonSaveDataLoaded();
    }

    /**
     * 条件が設定されているかどうかを調べる
     *
     * @param functionBean  調べる対象の保存された条件
     * @param tableId       調べる対象のテーブルID
     * @param columnName    調べる対象のカラムID
     *
     * @return 項目が設定され、条件値も設定されている場合はtrue それ以外はfalse
     */
    public boolean isSettedCondition(BaseFunctionBean functionBean, String tableId, String... columnNames)
    {
        SearchSetting s = functionBean.getSearchSettingMap().get(tableId);
        MstSearchInfo m = functionBean.getMstSearchInfoMap().get(tableId);
        if (m == null) {
            // 例外 functionBeanの検索条件マスターに存在しません。
            throw new IllegalArgumentException("functionBean mstSearchInfoMap Not Found tableId = " + String.valueOf(
                    tableId));
        }
        if (s == null) {
            // 例外 functionBeanの検索条件に存在しません。
            throw new IllegalArgumentException("functionBean searchSettingMap Not Found tableId = " + String.valueOf(
                    tableId));
        }

        //複数のカラム名に対して1つでも設定されていなければfalseを返す
        for (String columnName : columnNames) {

            MstCondition mstCondition = m.getMstConditionOfSearchColumnName(columnName);
            ConditionInfo condition = s.getConditionInfo(mstCondition.getColumnName());

            //検索条件が項目ごと保存されていなければfalseリターン
            if (condition == null) {
                return false;
            }

            //有効・無効フグラグを見て無効だったらfalseリターン
            if( condition.isEnable() == null || condition.isEnable().equals(DIC_OFF)){
                return false;
            }

            List<String> params = condition.getParameters();
            List<String> patterns = condition.getPatternKeys();

            boolean paramFound = false;

            //パラメータの先頭に何も入っていなかったらfalseリターン
            if (params != null && params.size() > 0) {
                //パラメータを調べて1つでも設定していたら
                //設定ありとみなす
                for (String paramString : params) {
                    if (StringUtil.isNullOrEmpty(paramString) == false) {
                        paramFound = true;
                        break;
                    }
                }
            }
            //パターン定義も調べる
            if (paramFound == false && patterns != null && patterns.size() > 0) {
                //パラメータを調べて1つでも設定していたら
                //設定ありとみなす
                for (String patternString : patterns) {
                    if (StringUtil.isNullOrEmpty(patternString) == false) {
                        paramFound = true;
                        break;
                    }
                }
            }

            //パラメータやパターンが全く設定されていない場合はfalseリターン
            if (paramFound == false) {
                return false;
            }

        }

        //項目が保存されていて、値が入っている場合はtrueとなる
        return true;
    }

    /**
     * パラメータ設定
     * @param mstSearchInfo 検索マスタ
     * @param searchSetting 検索条件
     * @param mstCondition 条件マスタ
     * @param type 条件タイプ
     * @param param 設定値
     * @throws ParseException
     */
    public void setConditionParam(MstSearchInfo mstSearchInfo, SearchSetting searchSetting, MstCondition mstCondition,
            ConditionType type, String[] param) throws ParseException
    {
        this.setSettingConditionParam(mstSearchInfo.getDefaultSearchSetting(), mstCondition, type, param);
        this.setSettingConditionParam(searchSetting, mstCondition, type, param);
    }

    /**
     * パラメータ設定
     * @param searchSetting 検索条件
     * @param index 設定項目
     * @param type 条件タイプ
     * @param param 設定値
     * @throws ParseException
     */
    public void setSettingConditionParam(SearchSetting searchSetting, MstCondition mstCondition, ConditionType type,
            String[] param) throws ParseException
    {

        List<ConditionInfo> conditionInfoList = searchSetting.getConditionInfos();
        //検索条件をカラム名から探す
        ConditionInfo conditionInfo = searchSetting.getConditionInfo(mstCondition.getColumnName());
        //検索条件が存在する場合、パラメータを設定する
        if (conditionInfo != null) {
            setConditionInfoParam(conditionInfo, type, param);
            conditionInfo.setEnable(conditionInfo.isEnable());
            return;
        }

        // 検索条件に含まれていない項目の場合、検索条件に追加
        ConditionInfo addCondition = new ConditionInfo();
        addCondition.setEnable(DISPLAY_ORDER_YES);
        addCondition.setColumnId(mstCondition.getColumnName());
        addCondition.setHiddenItem(true);
        setConditionInfoParam(addCondition, type, param);
        conditionInfoList.add(addCondition);
    }

    /**
     * 検索パラメータ設定
     * @param conditionInfo 設定項目条件情報
     * @param type 条件タイプ
     * @param param 設定値
     * @throws ParseException
     */
    protected void setConditionInfoParam(ConditionInfo conditionInfo, ConditionType type, String[] param)
            throws ParseException
    {

        // 検索パラメータが空の場合、終了
        if (param == null || param.length == 0) {
            return;
        }

        // 条件タイプによりparamの格納先を切替
        switch (type) {
            case CHECKBOX :
            case STRING_PULLDOWN :
            case STRING_PULLDOWN_AUTOCOMPLETE :
            case STRING_PULLDOWN_AUTOCOMPLETE_1 :
            case STRING_PULLDOWN_AUTOCOMPLETE_2 :
            case NUMBER_PULLDOWN :
            case NUMBER_PULLDOWN_AUTOCOMPLETE :
            case NUMBER_PULLDOWN_AUTOCOMPLETE_1 :
            case NUMBER_PULLDOWN_AUTOCOMPLETE_2 :
            {
                // patternsKeyに設定
                conditionInfo.setPatternKeys(Arrays.asList(param));

            }
                break;
            case STRING_LIKE :
            {
                List<String> list = new ArrayList<>();
                list.add(param[0]);
                // 1番目の文字列を検索文字（parameters）に設定
                conditionInfo.setParameters(list);

                list = new ArrayList<>();
                list.add(param[1]);
                // 2番目の文字列を曖昧プルダウン選択値（patternsKey）に設定
                conditionInfo.setPatternKeys(list);
            }
                break;
            case NUMBER :
            case NUMBER_PULLDOWN_RANGE :
            case NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE :
            case NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE_1 :
            case NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE_2 :
            {
                // parametersに設定
                conditionInfo.setParameters(Arrays.asList(param));
            }
                break;
            case DATE :
            case MULTIFORMAT_DATESTR :
            case DATE_TIME :
            case DATE_TIME_SS :
            case DATE_YM :
            case DATE_TIME_MM00 :
            case DATE_TIME_SS00 :
            {
                List<String> paramList = new ArrayList<String>();

                for (String para : param) {

                    ConditionDateType conditionDateType;
                    String defaultDateFormat = "";

                    if (type == ConditionType.DATE) {
                        defaultDateFormat = CommonConstants.TABLE_SEARCH_DATE;
                        conditionDateType = ConditionDateType.DATE;
                    }
                    else if (type == ConditionType.DATE_TIME) {
                        defaultDateFormat = CommonConstants.TABLE_SEARCH_DATE_TIME;
                        conditionDateType = ConditionDateType.DATE_TIME;
                    }
                    else if (type == ConditionType.DATE_TIME_SS) {
                        defaultDateFormat = CommonConstants.TABLE_SEARCH_DATE_TIME_SS;
                        conditionDateType = ConditionDateType.DATE_TIME_SS;
                    }
                    else if (type == ConditionType.DATE_TIME_MM00) {
                        defaultDateFormat = CommonConstants.TABLE_SEARCH_DATE_TIME_MM00;
                        conditionDateType = ConditionDateType.DATE_TIME_MM00;
                    }
                    else if (type == ConditionType.DATE_TIME_SS00) {
                        defaultDateFormat = CommonConstants.TABLE_SEARCH_DATE_TIME_SS00;
                        conditionDateType = ConditionDateType.DATE_TIME_SS00;
                    }
                    else {
                        defaultDateFormat = CommonConstants.TABLE_SEARCH_DATE_YM;
                        conditionDateType = ConditionDateType.DATE_YM;
                    }

                    // 日付型から文字列に変換
                    Date date = ConverterUtils.stringToDateFormat(para, defaultDateFormat);
                    // 文字列から日付型に変換
                    String stringDate = ConverterUtils.dateTimeToString(date, ResourcesHandler.getString(
                            conditionDateType.getFormat(), gnomesSessionBean.getUserLocale()));

                    paramList.add(stringDate);
                }
                conditionInfo.setParameters(paramList);
            }
                break;
            default :
                break;
        }

        return;
    }

    /**
     * 検索マスターを取得する
     * @param tableTagId テーブルタグID
     * @param userLocale ユーザロケール
     * @return 検索マスター
     * @throws Exception 例外
     */
    public LinkedHashMap<String, String> getConditionPatterns(
            String tableTagId, String columnId, String repConditionQuery, Map<String, Object> parameter) throws Exception {

        MstSearchInfo mstSearchInfo = new MstSearchInfo();

        try{
            Map<String, Object> map = gnomesCTagDictionary.getTableInfo(tableTagId);

            List<Map<String,Object>> lstTableInfo = gnomesCTagDictionary.getTableColumnInfo(map);

            LinkedHashMap<String, String> ptn = new LinkedHashMap<>();

            for (int index = 0; index < lstTableInfo.size(); index ++) {

                Map<String,Object> tr = lstTableInfo.get(index);
                @SuppressWarnings("unchecked")
                Map<String,Object> mapColInfo = (Map<String,Object>)tr.get(GnomesCTagDictionary.MAP_NAME_COLUMN_INFO);


                String param_name = (String)mapColInfo.get(DIC_PARAM_NAME);

                // カラム名は定義シートのパラメータ名を使用する
                String columnName = (String)formatParamName(param_name);

                // 検索対象有無
                if (mapColInfo.get(DIC_CONDITION_TYPE) != null) {
                    if(StringUtil.isNullOrEmpty(columnName)){
                        throw new NullPointerException(MESSAGE_COLUMNNAME);

                    }
                    if (columnName.equals(columnId)) {
                        // 条件マスタ、初期条件の取得
                        //SQLに渡すのでカラム物理名で設定する
                        ptn = this.getMstConditionPtn(em.getEntityManager(), mapColInfo, repConditionQuery, parameter);
                        break;
                    }
                }

            }

            return ptn;
        }
        catch(Exception e){

            logger.severe(MessagesHandler.getString(GnomesMessagesConstants.ME01_0078) + " tableTagId = " + tableTagId);
            logger.severe("Exception Message=" + e.getMessage());

            // ME01.0078:「検索マスター取得時にエラーが発生しました。詳細はエラーメッセージを確認してください。タグID：{0}、言語：{1}」
            throw exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0078, e, tableTagId, gnomesSessionBean.getUserLocale());

        }
    }

    /**
     * 検索種別の設定
     * @param em エンティティマネージャー
     * @param mapColInfo テーブルのカラム辞書
     * @param mc 条件マスタリスト
     * @param resBundle リソースバンドル
     * @throws Exception 例外
     */
    protected LinkedHashMap<String, String> getMstConditionPtn(
            EntityManager em, Map<String,Object> mapColInfo, String repConditionQuery, Map<String, Object> parameter
            ) throws Exception
    {
        // 検索種別
        String strConditionType = (String)mapColInfo.get(DIC_CONDITION_TYPE);
        ConditionType conditionType = getConditionType(strConditionType);

        // ENUM
        String strConditionEnum = (String)mapColInfo.get(DIC_CONDITION_ENUM);
        // Query
        String strConditionQuery = (String)mapColInfo.get(DIC_CONDITION_QUERY);

        LinkedHashMap<String, String> ptn = new LinkedHashMap<>();
        if (strConditionEnum != null && strConditionEnum.length() > 0) {
            // Enumからリスト作成
            ptn = this.getConditionEnum(strConditionEnum, conditionType, gnomesSessionBean.getUserLocale());
        }
        else if (strConditionQuery != null && strConditionQuery.length() > 0) {
            if (repConditionQuery != null) {
                // 差し替えQueryからリスト作成
                ptn = this.getConditionQuery(em, repConditionQuery, conditionType, gnomesSessionBean.getUserLocale(), parameter);
            } else {
                // 辞書のQueryからリスト作成
                ptn = this.getConditionQuery(em, strConditionQuery, conditionType, gnomesSessionBean.getUserLocale());
            }
        }
        return ptn;
    }

    /**
     * Queryの一覧取得
     * @param conditionQuery クエリ名
     * @param resBundle リソースバンドル
     * @return ドロップダウン情報
     */
    protected LinkedHashMap<String, String> getConditionQuery(
            EntityManager em, String conditionQuery, ConditionType conditionType, Locale userLocale, Map<String, Object> parameter )
    {
        LinkedHashMap<String, String> ptn = new LinkedHashMap<String, String>();

        // 作業定義IDプルダウン取得
        TypedQuery<PullDownDto> getPdQuery = em.createNamedQuery(conditionQuery , PullDownDto.class);
        for (String key : parameter.keySet()) {
            getPdQuery.setParameter(key, parameter.get(key));
        }
        List<PullDownDto> lstPullDownDto = getPdQuery.getResultList();

        for (PullDownDto pullDownDto : lstPullDownDto) {

            // 追加
            if (conditionType == ConditionType.NUMBER_PULLDOWN_RANGE) {
                // 表示は"値:テキスト"
                ptn.put(pullDownDto.getName(), pullDownDto.getName() + ":" + pullDownDto.getValue());
            } else {
                ptn.put(pullDownDto.getName(), pullDownDto.getValue());
            }
        }

        return ptn;
    }
}
