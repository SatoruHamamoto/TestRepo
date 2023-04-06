package com.gnomes.common.search.query;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.ParseException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.TypedQuery;

import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.dto.CountDto;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.persistence.GnomesEntityManager;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.search.SearchInfoController;
import com.gnomes.common.search.SearchInfoController.ConditionDateType;
import com.gnomes.common.search.SearchInfoController.ConditionLikeType;
import com.gnomes.common.search.SearchInfoController.ConditionRequiredType;
import com.gnomes.common.search.SearchInfoController.ConditionType;
import com.gnomes.common.search.SearchInfoController.OrderType;
import com.gnomes.common.search.data.ConditionInfo;
import com.gnomes.common.search.data.MstCondition;
import com.gnomes.common.search.data.MstOrdering;
import com.gnomes.common.search.data.MstSearchInfo;
import com.gnomes.common.search.data.OrderingInfo;
import com.gnomes.common.search.data.SearchInfoPack;
import com.gnomes.common.search.data.SearchSetting;
import com.gnomes.common.search.data.SearchSetting.DispType;
import com.gnomes.common.search.query.Ordering.Dir;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.GnomesDateUtil;
import com.gnomes.uiservice.ContainerRequest;

/**
 * クエリ共通  クエリ実行処理クラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/03/01 YJP/30022467              初版
 * R0.01.02 2018/10/03 YJP/S.Hamamoto			 replaceQueryConditionで複数回条件置き換えを実施
 * R0.01.03 2019/05/27 YJP/S.Hamamoto            indexからColumnIDをキーにしたアクセスに変更
 *
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class SearchCondition {

    /** セッションビーン  */
    @Inject
    protected GnomesSessionBean gnomesSessionBean;

    /** ログヘルパー */
    @Inject
    protected LogHelper logHelper;

    /**  クエリ文字列 フィールド名 */
    protected static final String QUERY_STRING_FIELD_NAME = "queryString";

    /**  検索条件　置き換え対象文字 */
    protected static final String REPLASE_WHERE = "'SEARCH_COND_START' <> 'SEARCH_COND_END'";

    /**  ORDER BY 文字 */
    protected static final String ORDER_BY = "ORDER BY ";

    /** パラメータ指定用（項目名の後ろに「_p」を付与して基本クエリ内のパラメータと区別 */
    protected static final String SQL_PARAM_POSTFIX = "_p";

    /** クエリ内のバインド変数名  先頭文字 */
    protected static final String QUERY_PARAM_STRAT = "p";

    /** バインド変数　パラメータ２用に付加する名称 */
    protected static final String BAIND_SEC_PARAM_NAME = "_ConditionSecParam";

    /** 日付型から文字列 フォーマット */
    protected static final String FORMAT_DATA_STR_DATE = "yyyy/MM/dd HH:mm:ss";

    /** 日付(From)からdatetimeに変換用　フォーマット */
    protected static final String FORMAT_DATE_FROM = "yyyy/MM/dd 00:00:00";

    /** 日付(To)からdatetimeに変換用　フォーマット */
    protected static final String FORMAT_DATE_TO = "yyyy/MM/dd 23:59:59";

    /** 日時(From)からdatetimeに変換用　フォーマット */
    protected static final String FORMAT_DATETIME_FROM = "yyyy/MM/dd HH:mm:00";

    /** 日時(To)からdatetimeに変換用　フォーマット */
    protected static final String FORMAT_DATETIME_TO = "yyyy/MM/dd HH:mm:59";

    /** 日付(月まで)(From)からdatetimeに変換用　フォーマット */
    protected static final String FORMAT_DATE_YM_FROM = "yyyy/MM/01 00:00:00";

    /** 検索項目表示順設定無し */
    protected static final String DISPLAY_ORDER_NO = "0";

    /** ------------- 以降 独自マッピング用の定義 -----------------*/

    /** バインド変数　識別子 */
    protected static final String SQL_BAIND_START_STR = ":";

    @Inject
    protected GnomesEntityManager em;

    @Inject
    GnomesExceptionFactory exceptionFactory;

    @Inject
    SearchInfoController searchInfoController;

    @Inject
    ContainerRequest requestContext;

    @Inject
    transient Logger logger;


    /** 条件 */
    protected Condition condition;

    /** 順序 */
    protected Ordering ordering;


    /**
     * コンストラクター
     */
    public SearchCondition() {};

    /**
     * 条件設定
     * @param condition  Where句
     */
    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    /**
     * 順序設定
     * @param ordering Order句
     */
    public void setOrdering(Ordering ordering) {
        this.ordering = ordering;
    }

    /**
     * クエリ置き換え作成 クエリ名 に対して条件句、Order句を追加設定する
     * @param mstSearchInfo 検索マスター情報
     * @param searchSetting 検索情報
     * @param queryName クエリ名
     * @param clazz クラス
     * @return クエリオブジェクト(TypedQuery)
     * @throws GnomesAppException 例外
     */
    public <T> TypedQuery<T> replaceQueryCondition(
            MstSearchInfo mstSearchInfo, SearchSetting searchSetting, String queryName, Class<T> clazz) throws GnomesAppException
    {
        if(mstSearchInfo == null){
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0050,String.valueOf(mstSearchInfo));
            throw ex;
        }
        if(searchSetting == null){
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0050,String.valueOf(searchSetting));
            throw ex;
        }

        // 取得開始位置と取得件数の設定
        TypedQuery<T> result = null;
        SearchInfoPack searchInfoPack = searchInfoController.getSearchInfoPack(mstSearchInfo, searchSetting);
        result = replaceQueryCondition(searchInfoPack, queryName, clazz);

        // 件数取得でない場合
        if (clazz != CountDto.class)
        {
            if (searchSetting.getDispType() == DispType.DispType_List) {
//            	result.setFirstResult(0).setMaxResults(searchSetting.getMaxDispCount());
            } else {
                int start = searchSetting.getNowPage() - 1;
                if (start <= 0) {
                    start = 0;
                } else {
                    start *= searchSetting.getOnePageDispCount();
                }

                result.setFirstResult(start)
                      .setMaxResults(searchSetting.getOnePageDispCount());
            }
        }

        // 検索実施フラグON
        searchSetting.setSearchFlag(1);

        // ページングフラグがOFFの場合、現在のページを1ページ目に設定
        if (searchSetting.getPagingFlag() != null && searchSetting.getPagingFlag() != 1){
        	searchSetting.setNowPage(1);
        }

        return result;

    }


    /**
     * クエリ置き換え作成 クエリ名 に対して条件句、Order句を追加設定する
     * @param searchInfoPack 検索情報
     * @param queryName クエリ名
     * @param clazz クラス
     * @return クエリオブジェクト(TypedQuery)
     * @throws GnomesAppException 例外
     */
    public <T> TypedQuery<T> replaceQueryCondition(SearchInfoPack searchInfoPack, String queryName, Class<T> clazz) throws GnomesAppException
    {
        Map<String, Object> mapParam = null;

        try
        {
            mapParam = convertSearchInfoPack(searchInfoPack);
        }
        catch (Exception e)
        {
            if (e instanceof GnomesAppException) {
                throw (GnomesAppException)e;
            }

            //   メッセージ： "SQLクエリ生成時にエラーが発生しました。 詳細はエラーメッセージを確認してください。 クエリ名： {0}")
            GnomesAppException ex =new GnomesAppException(e);
            ex.setMessageNo(GnomesMessagesConstants.ME01_0035);
            Object[] errParam = {
                    queryName
            };
            ex.setMessageParams(errParam);
            throw ex;
        }

        TypedQuery<T> replaseQuery = replaceQueryCondition(queryName, clazz, mapParam);
        return replaseQuery;
    }

    /**
     * クエリ置き換え作成 クエリ名 に対して条件句、Order句を追加設定する
     * @param queryName クエリ名
     * @param clazz クラス
     * @param mapParam パラメータ
     * @return クエリオブジェクト(TypedQuery)
     * @throws GnomesAppException 例外
     */
    public <T> TypedQuery<T> replaceQueryCondition(String queryName, Class<T> clazz, Map<String, Object> mapParam) throws GnomesAppException
    {
        TypedQuery<T> replaseQuery = null;
        try
        {
            replaseQuery = em.getEntityManager().createNamedQuery(queryName, clazz);
            Field fldQueryString = null;
            String str = null;
            //Field fldQueryString = AbstractQueryImpl.class.getDeclaredField(QUERY_STRING_FIELD_NAME);
            //Field fldQueryString = replaseQuery.getClass().getDeclaredField(QUERY_STRING_FIELD_NAME);
            try {
                fldQueryString = replaseQuery.getClass().getDeclaredField("sqlString");
                fldQueryString.setAccessible( true );
                str = (String) fldQueryString.get( replaseQuery.unwrap(org.hibernate.Query.class) );
            }
            catch(NoSuchFieldException e){
                fldQueryString = replaseQuery.getClass().getDeclaredField("query");
                fldQueryString.setAccessible( true );
                str = fldQueryString.get(replaseQuery).toString();
            }

//            fldQueryString.setAccessible( true );
//            String str = (String) fldQueryString.get( replaseQuery.unwrap(org.hibernate.Query.class) );
            StringBuilder queryString = new StringBuilder(str);

            // バインド変数の個数を取得
            int bnum = getOrgQueryBindNum(queryString);

            // 条件
            String where = this.condition.getQueryString();

            where = this.replaceMapParam(replaseQuery, bnum, where, mapParam);

            if (where != null && where.length() > 0) {
            	//R0.01.02【浜本18/10/03】複数回リプレースを行うため、見つかるまで回す
            	int wherePos = 0;
            	while((wherePos = queryString.indexOf(REPLASE_WHERE)) != -1){
                    queryString.replace(wherePos, wherePos + REPLASE_WHERE.length(), where);
            	}
            }

            // 順序
            if(!Objects.isNull(ordering)){
                int orderPos = queryString.indexOf(ORDER_BY);
                if (orderPos > 0) {
                    queryString.delete(orderPos + ORDER_BY.length(), queryString.length());
                    String orderBy = ordering.getQueryString();
                    queryString.append(orderBy);
                }
            }

            fldQueryString.set( replaseQuery.unwrap(org.hibernate.Query.class) , queryString.toString());
        }
        catch (Exception e)
        {
            //   メッセージ： "SQLクエリ生成時にエラーが発生しました。 詳細はエラーメッセージを確認してください。 クエリ名： {0}")
            GnomesAppException ex =new GnomesAppException(e);
            ex.setMessageNo(GnomesMessagesConstants.ME01_0035);
            Object[] errParam = {
                    queryName
            };
            ex.setMessageParams(errParam);
            throw ex;
        }

        return replaseQuery;
    }

    /**
     * バインド変数設定個数を取得
     * @param queryStr クエリ
     * @return 個数
     */
    protected int getOrgQueryBindNum(StringBuilder queryStr)
    {
        int cnt = 0;
        int start = queryStr.length() - 1;
        int bindPos = -1;

        // 後方より :p1 を検索
        while(true) {
            int pos = queryStr.lastIndexOf(SQL_BAIND_START_STR + QUERY_PARAM_STRAT + "1", start);
            if (pos >= 0) {
                bindPos = pos;
                start = pos - 1;
            } else {
                break;
            }
        }

        if(bindPos >= 0){
            String cut = queryStr.substring(bindPos, queryStr.length());
            int paramPos = cut.indexOf(SQL_BAIND_START_STR);
            String param = cut.substring(paramPos, cut.length());
            String[] params = param.split(" ", 0);
            cnt = params.length;

            // 固定バインド変数部分 削除
            queryStr.delete(bindPos, queryStr.length());

        }


        return cnt;
    }

    /**
     *
     * バインド変数から値に置き換え
     *
     * @param strWhere SQL Where 文字
     * @param param バインドパラメータ
     * @return 置き換えSQL文字
     * @throws Exception
     */
    public String replaceMapParam(
            TypedQuery<?> replaseQuery, int bindNum,
            String strWhere, Map<String, Object>param) throws Exception
    {

        int cnt = 0;
        StringBuilder where = new StringBuilder(strWhere);

        if (param != null) {

            for(Map.Entry<String, Object> e : param.entrySet()) {
                String key = SQL_BAIND_START_STR + e.getKey() + SQL_BAIND_START_STR;
                String pkey = QUERY_PARAM_STRAT + String.valueOf(cnt + 1);
                String psql = SQL_BAIND_START_STR + pkey;
                int pos;

                //パラメータが複数存在するため、一致するまでリプレースをかける
                //また、リプレースするための検索文字に類似文字がヒットするため、
                //パラメータ文字列の前後に":" をつけて類似文字がヒットしないようにする。
                // 例 where = ":param123 and :param123cde" に :param123をAAAに置き換えると 右側が :AAAcdeになるため
                //    where = ":param123: and :param123cde:" にし、 :param123: を AAAに置き換える

                while((pos = where.indexOf(key)) >= 0){
                    where.replace(pos, pos + key.length(), psql);
                }

                replaseQuery.setParameter(pkey, e.getValue());
                cnt ++;
            }
        }

        for (int i = cnt; i < bindNum; i++) {
            if (where.length() > 0) {
                where.append(" AND");
            }
            String pkey = QUERY_PARAM_STRAT + String.valueOf(i + 1);
            String psql = SQL_BAIND_START_STR + pkey;
            where.append(" '").append(String.valueOf(i)).append("' = ").append(psql);
            replaseQuery.setParameter(pkey, String.valueOf(i));
        }

        return where.toString();
    }


    /**
     *
     * 検索ダイアログ情報から検索条件を設定
     *
     * @param searchInfoPack 検索ダイアログ情報
     * @return バインドパラメータ
     * @throws Exception 例外
     */
    @TraceMonitor
    @ErrorHandling
    public Map<String, Object> convertSearchInfoPack(SearchInfoPack searchInfoPack) throws Exception
    {
        Map<String, Object> params = new HashMap<String, Object>();

        this.condition = new Condition();
        this.ordering = new Ordering();

        SearchSetting searchSetting = searchInfoPack.getSearchSetting();


        // 条件
        List<ConditionInfo> conditionInfos = searchSetting.getConditionInfos();

        for (ConditionInfo info : conditionInfos) {

            // 有効の場合
            if( info.isEnable() != null && !info.isEnable().equals(DISPLAY_ORDER_NO)){
                //カラムIDでマスターを検索する
                MstCondition mst = SearchInfoController.geMstCondition(searchInfoPack, info.getColumnId());
                Map<String, Object> param =  this.andCondition(this.condition, mst, info);

                if (param != null && param.size() > 0) {
                    // 登録済みキー判定
                    for (String key : param.keySet()) {
                        if (params.containsKey(key)) {
                            throw new Exception("containsKey:" + key );
                        }
                    }
                    params.putAll(param);
                }
            }
        }

        // 順序
        List<OrderingInfo> orderingInfos = searchSetting.getOrderingInfos();

        // 有効なもののみ
        List<OrderingInfo> orderInfos = new ArrayList<OrderingInfo>();
        for (OrderingInfo info : orderingInfos) {
            if (info.getOrderNum() != null) {
                // 順序がNULLでないものが有効
                orderInfos.add(info);
            }
        }

        // 順序で並び替え
        Collections.sort(orderInfos, new Comparator<OrderingInfo>() {
            public int compare(OrderingInfo value1, OrderingInfo value2) {
                //大小比較を行う。昇順の場合はvalue1-value2
                return value1.getOrderNum() - value2.getOrderNum();
            }
        });

        for (OrderingInfo info : orderInfos) {
        	//カラムIDで順序マスターを得る
            MstOrdering mst = SearchInfoController.getMstOrdering(searchInfoPack, info.getColumnId());

            Dir dir;
            if (info.getOrderType() == OrderType.ASCENDING) {
                dir = Dir.ASCENDING;
            } else {
                dir = Dir.DESCENDING;
            }

			//SQLに渡すため、カラム物理名で設定する
            Ordering newOrdering = new Ordering(mst.getSearch_column_name(), dir);
            this.ordering.add(newOrdering);
        }

        return params;
    }


    /**
     * 条件追加
     * @param addCondition 条件
     * @param mst 条件マスター
     * @param info 条件情報
     * @throws Exception
     */
    protected Map<String, Object> andCondition(Condition addCondition, MstCondition mst, ConditionInfo info)
            throws DateTimeParseException, ParseException {
/*

タイプ					パターンリスト

文字1	LIKE			キーワード手入力によるあいまい検索
文字2	EQU				実データのプルダウンリストからの選択      "value", "text"
文字2	EQU				マスタデータからのプルダウンリストの選択
文字2	EQU				固定値のプルダウンリストからの選択

数値1	EQU				固定値のプルダウンリストからの選択		"value", "text"
数値2	BETWEEN,<,>		最大、最小、範囲の指定

日付1	BETWEEN,<,>		最大、最小、範囲の指定 （日付）
日付2	BETWEEN,<,>		最大、最小、範囲の指定 （日時）

*/

//		Condition newCondition = null;
        String paramKeyBase = mst.getColumnName() + SQL_PARAM_POSTFIX;


        Map<String, Object> mapParam = new HashMap<String, Object>();



        switch(mst.getType()) {
            case CHECKBOX:
            case STRING_PULLDOWN:
            case STRING_PULLDOWN_AUTOCOMPLETE:
            case STRING_PULLDOWN_AUTOCOMPLETE_1:
            case STRING_PULLDOWN_AUTOCOMPLETE_2:
            case NUMBER_PULLDOWN:
            case NUMBER_PULLDOWN_AUTOCOMPLETE:
            case NUMBER_PULLDOWN_AUTOCOMPLETE_1:
            case NUMBER_PULLDOWN_AUTOCOMPLETE_2:
                {
                    // 選択なしは、info.getPatternKeysに入らないこと
                    if (info.getPatternKeys() == null || info.getPatternKeys().size() == 0) {
                        return null;
                    }

                    Condition newCondition = null;
                    // 1件の場合
                    if (info.getPatternKeys().size() == 1) {
                        // 未入力の場合
                        if (info.getPatternKeys() == null || info.getPatternKeys().get(0) == null || info.getPatternKeys().get(0).length() == 0) {
                            return null;
                        }
                        if (addCondition != null) {
                        	//SQLに渡すのでカラム物理名で設定する
                            newCondition = addCondition.equalCheck(mst.getSearch_column_name(), paramKeyBase,mst.getType());
                        }
                        Object objParam = convertParameter(mst.getType(), null, info.getPatternKeys().get(0));
                        mapParam.put(paramKeyBase, objParam);

                    // 複数はin
                    } else {
                        List<Object> inDatas = new ArrayList<Object>();
                        for (String patternKey : info.getPatternKeys()) {
                            if (patternKey != null && patternKey.length() > 0) {
                                Object objParam = convertParameter(mst.getType(), null, patternKey);
                                inDatas.add(objParam);
                            }
                        }
                        // 未入力の場合
                        if (inDatas.size() == 0) {
                            return null;
                        }
                        if (addCondition != null) {
                        	//SQLに渡すのでカラム物理名で設定する
                            newCondition = addCondition.in(mst.getSearch_column_name(), paramKeyBase,mst.getType());
                        }
                        mapParam.put(paramKeyBase, inDatas);
                    }
                    if (addCondition != null) {
                        addCondition.and(newCondition);
                    }
                }
                break;

            case STRING_LIKE:
                {
                    if (info.getPatternKeys() == null || info.getPatternKeys().size() == 0) {
                        return null;
                    }

                    String patternKey = info.getPatternKeys().get(0);
                    String strParam = info.getParameters().get(0);

                    if (strParam == null || strParam.length() == 0) {
                        // 検索文字が、未入力の場合
                        return null;
                    }

                    Condition newCondition = null;
                    ConditionLikeType likeType = ConditionLikeType.getEnum(Integer.valueOf(patternKey));

                    if (addCondition != null) {
                        if (likeType == ConditionLikeType.ConditionLikeType_PARALLEL_WITH) {
                            // 一致する
                            newCondition = addCondition.equalCheck(mst.getSearch_column_name(), paramKeyBase,mst.getType());
                        } else {
                            newCondition = addCondition.like(mst.getSearch_column_name(), paramKeyBase,mst.getType());
                        }
                        addCondition.and(newCondition);
                    }

                    String strSqlFormat = likeType.getSqlFormat();
                    String escapedParam = null;
                    //Like演算子の正規表現を抑制するためにエスケープする
                    if (likeType == ConditionLikeType.ConditionLikeType_PARALLEL_WITH) {
                        // 一致するクエリはエスケープしないでよい
                        escapedParam = strParam;
                    }
                    else {
                        //含む、始まる、終わるの場合はエスケープする

                        escapedParam = strParam.replaceAll("[\\[%_]", "[$0]");
                    }

                    Object objParam = convertParameter(mst.getType(), null, escapedParam);

                    MessageFormat sqlFormat = new MessageFormat(strSqlFormat);
                    Object[] arguments = { objParam };
                    String mapParamValue = sqlFormat.format(arguments);

                    mapParam.put(paramKeyBase, mapParamValue);
                }
                break;

            case NUMBER:
            case DATE:
            case DATE_TIME:
            case DATE_TIME_SS:
            case NUMBER_PULLDOWN_RANGE:
            case NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE:
            case NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE_1:
            case NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE_2:
            case DATE_YM:
            case MULTIFORMAT_DATESTR:
            case DATE_TIME_MM00:
            case DATE_TIME_SS00:
                {
                    String strParam1 = null;
                    String strParam2 = null;

                    if (info.getParameters() == null || info.getParameters().size() == 0) {
                        return null;
                    }

                    strParam1 = info.getParameters().get(0);

                    //パラメータが2以上の場合はstrParam2に2つ目を入れる
                    //パラメータが1以下の場合はstrParam2はnullになる
                    if (info.getParameters().size() > 1) {
                        strParam2 = info.getParameters().get(1);
                    }

                    //パラメータ１も２も定義されていない場合
                    //SQL文としては何もしない
                    if ((strParam1 == null || strParam1.length() == 0)
                            && (strParam2 == null || strParam2.length() == 0)) {
                        // 未入力の場合
                        return null;
                    }

                    //---------------------------------------------------------
                    //  ここではパラメータ１か１、２両方入っている場合の処理
                    //---------------------------------------------------------
                    Condition newCondition = null;

                    //------------------------------------------
                    // パラメータ1のみ設定されている場合
                    // ＤＢ列 [≧] パラメータとする
                    //------------------------------------------
                    if (strParam2 == null || strParam2.length() == 0)
                    {
                        // 以上
                        if (addCondition != null) {
                            newCondition = addCondition.ge(mst.getSearch_column_name(), paramKeyBase,mst.getType());
                        }
                        mapParam.put(paramKeyBase, convertParameter(mst.getType(), info, strParam1));
                    }
                    //------------------------------------------
                    // パラメータ２しか設定されていない場合
                    // ＤＢ列 [ ≦ ] パラメータ2
                    //------------------------------------------
                    else if (strParam1 == null || strParam1.length() == 0)
                    {
                        // 以下
                        if (addCondition != null) {
                            newCondition = addCondition.le(mst.getSearch_column_name(), paramKeyBase,mst.getType());
                        }
                        mapParam.put(paramKeyBase, convertParameter(mst.getType(), info, strParam2, true));
                    }
                    //------------------------------------------
                    // パラメータ1，2とも設定されている場合
                    // ＤＢ列 [ BETWEEN ] パラメータ１ and パラメータ２
                    //------------------------------------------
                    else
                    {
                        // 以上以下
                        if (addCondition != null) {
                            newCondition = addCondition.between(mst.getSearch_column_name(),
                                    paramKeyBase, paramKeyBase + BAIND_SEC_PARAM_NAME,mst.getType());
                        }
                        mapParam.put(paramKeyBase, convertParameter(mst.getType(), info, strParam1));
                        mapParam.put(paramKeyBase + BAIND_SEC_PARAM_NAME,
                                convertParameter(mst.getType(), info, strParam2, true));
                    }
                    if (addCondition != null) {
                        addCondition.and(newCondition);
                    }
                }
                break;
        default:
            break;
        }

        return mapParam;
    }

    /**
     * パラメータをMapping用Objectに変換
     * @param type 条件タイプ
     * @param info 条件設定情報
     * @param parameter パラメータ
     * @return Mapping用Object
     * @throws Exception 例外
     */
    protected Object convertParameter(ConditionType type, ConditionInfo info, String parameter)
            throws DateTimeParseException, ParseException {
        return convertParameter(type, info, parameter, false);
    }


    /**
     * パラメータをMapping用Objectに変換
     * @param type 条件タイプ
     * @param info 条件設定情報
     * @param parameter パラメータ
     * @param isTo 至項目フラグ
     * @return Mapping用Object
     * @throws Exception 例外
     */
    public Object convertParameter(ConditionType type, ConditionInfo info, String parameter, boolean isTo)
            throws DateTimeParseException, ParseException
    {
        switch(type) {
            case CHECKBOX:
            case STRING_PULLDOWN:
            case STRING_PULLDOWN_AUTOCOMPLETE:
            case STRING_PULLDOWN_AUTOCOMPLETE_1:
            case STRING_PULLDOWN_AUTOCOMPLETE_2:
            case STRING_LIKE:
                String strParam = parameter.toString();
                return strParam;

            case NUMBER_PULLDOWN:
            case NUMBER_PULLDOWN_AUTOCOMPLETE:
            case NUMBER_PULLDOWN_AUTOCOMPLETE_1:
            case NUMBER_PULLDOWN_AUTOCOMPLETE_2:
            case NUMBER:
            case NUMBER_PULLDOWN_RANGE:
            case NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE:
            case NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE_1:
            case NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE_2:

                Number num = ConverterUtils.stringToNumber(false, parameter,this.gnomesSessionBean.getUserLocale().toString());
                BigDecimal decParam = new BigDecimal(num.toString());
                return decParam;

            case DATE:
            case MULTIFORMAT_DATESTR:
            case DATE_TIME:
            case DATE_TIME_SS:
            case DATE_YM:
            case DATE_TIME_MM00:
            case DATE_TIME_SS00:
            {
                ConditionDateType conditionDateType;

                if (type == ConditionType.DATE || type == ConditionType.MULTIFORMAT_DATESTR) {
                    conditionDateType = ConditionDateType.DATE;
                } else if (type == ConditionType.DATE_TIME){
                    conditionDateType = ConditionDateType.DATE_TIME;
                } else if (type == ConditionType.DATE_TIME_SS) {
                    conditionDateType = ConditionDateType.DATE_TIME_SS;
                } else if (type == ConditionType.DATE_TIME_MM00) {
                    conditionDateType = ConditionDateType.DATE_TIME_MM00;
                } else if (type == ConditionType.DATE_TIME_SS00) {
                    conditionDateType = ConditionDateType.DATE_TIME_SS00;
                } else {
                	conditionDateType = ConditionDateType.DATE_YM;
                }

                Date dateDef = ConverterUtils.stringToDateFormat(parameter, ResourcesHandler.getString(conditionDateType.getFormat(), gnomesSessionBean.getUserLocale()));
                String strDef = null;

                if (conditionDateType == ConditionDateType.DATE)
                {
                    // 日付
                    if (!isTo) {
                        strDef = ConverterUtils.dateTimeToString(dateDef, FORMAT_DATE_FROM);
                    } else {
                        strDef = ConverterUtils.dateTimeToString(dateDef, FORMAT_DATE_TO);
                    }
                }
                else if (conditionDateType == ConditionDateType.DATE_TIME)
                {
                    // 日時
                    if (!isTo) {
                        strDef = ConverterUtils.dateTimeToString(dateDef, FORMAT_DATETIME_FROM);
                    } else {
                        strDef = ConverterUtils.dateTimeToString(dateDef, FORMAT_DATETIME_TO);
                    }
                }
                else if (conditionDateType == ConditionDateType.DATE_TIME_SS)
                {
                    //strDef = parameter;
                    strDef = ConverterUtils.dateTimeToString(dateDef, FORMAT_DATA_STR_DATE);
                }
                else if (conditionDateType == ConditionDateType.DATE_TIME_MM00)
                {
                    //strDef = parameter;
                    strDef = ConverterUtils.dateTimeToString(dateDef, FORMAT_DATA_STR_DATE);
                }
                else if (conditionDateType == ConditionDateType.DATE_TIME_SS00)
                {
                    //strDef = parameter;
                    strDef = ConverterUtils.dateTimeToString(dateDef, FORMAT_DATA_STR_DATE);
                }
                else
                {
                    // 日付(月まで)
                    if (!isTo) {
                        strDef = ConverterUtils.dateTimeToString(dateDef, FORMAT_DATE_YM_FROM);
                    } else {
                        Calendar c = Calendar.getInstance();;
                        c.setTime(dateDef);

                        int lastDay = c.getActualMaximum(Calendar.DATE);
                        c.set(Calendar.DATE, lastDay); // 最大日を設定

                        // その日の最後の瞬間にする
                        c.set(Calendar.HOUR_OF_DAY, 23);
                        c.set(Calendar.MINUTE, 59);
                        c.set(Calendar.SECOND, 59);
                        c.set(Calendar.MILLISECOND, 999);

                        strDef = ConverterUtils.dateTimeToString(c.getTime(), FORMAT_DATE_TO);
                    }
                }
                
                Date date;
				try {
					//画面から入力されたローカル時刻をDB上で扱っているUTCに変換
					date = GnomesDateUtil.convertStringLocaleToUtc(strDef,
							FORMAT_DATA_STR_DATE, gnomesSessionBean.getUserTimeZone());
				} catch (GnomesAppException e) {
					throw new ParseException("Unparseable date: \"" + strDef + "\"", 0);
				}
                return date;
            }
        default:
            break;
        }
        return null;
    }


    /**
     * 入力チェック
     * @param tableId テーブルID
     * @param mstSearchInfo マスター
     * @param searchSetting 検索入力
     * @return エラー情報
     */
    public Map<String, String[]> checkInput(String tableId, MstSearchInfo mstSearchInfo, SearchSetting searchSetting) {

        Map<String, String[]> requestParamErr = new LinkedHashMap<String, String[]>();
        List<ConditionInfo> conditionInfos = null;

        try {
        // 条件
        	conditionInfos = searchSetting.getConditionInfos();

        }
        catch(NullPointerException ex){
        	logHelper.severe(this.logger,null,null,"Search Condition setting not found tableid=" + tableId + " searchInfo = " + mstSearchInfo + " SearchSetting = " + searchSetting);
        	throw ex;
        }

        // 条件入力があるカラム名
        List<String> inputNames = new ArrayList<>();

        for (ConditionInfo info : conditionInfos) {

            // 有効の場合
            if( info.isEnable() != null && !info.isEnable().equals(DISPLAY_ORDER_NO)){
                //カラムIDで順序マスターを得る
                MstCondition mst = mstSearchInfo.getMstCondition(info.getColumnId());
                Map<String, Object> param =  null;

                try {
                    param =  this.andCondition(null, mst, info);
                } catch (DateTimeParseException | ParseException  e ) {
                    // 変換エラー
                    String dataType = null;

                    if (mst.getType() == ConditionType.NUMBER_PULLDOWN || mst.getType() == ConditionType.NUMBER) {
                        // 数値
                        dataType = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0009);
                    } else {
                        // 日付
                        dataType = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0011);
                    }
                    /** {0}は{1}を入力してください。 */
                    //カラム物理名でメッセージを指定する
                    String requestErrKey = tableId.concat(".").concat(mst.getSearch_column_name());
                    String[] objs = { GnomesMessagesConstants.MV01_0001, mst.getText(), dataType};
                    requestParamErr.put(requestErrKey, objs);
                }

                if (param != null && param.size() > 0) {
                    // 条件入力があるカラム名を追加
                    inputNames.add(mst.getColumnName());
                }
            }
        }

        if (requestParamErr.size() == 0) {
            // 必須入力チェック
            for (MstCondition m : mstSearchInfo.getMstConditions()) {

                // 必須
                if (m.getRequiredType() == ConditionRequiredType.REQUIRED) {
                    if (!inputNames.contains(m.getColumnName())) {

                        /** {0}は必須です。 */
                        String requestErrKey = tableId.concat(".").concat(m.getColumnName());
                        String[] objs = { GnomesMessagesConstants.MV01_0002, m.getText() };

                        requestParamErr.put(requestErrKey, objs);
                    }
                }
            }
        }


        if (requestParamErr.size() == 0) {
            // 表示・ソートの情報
            List<OrderingInfo> orderInfos = searchSetting.getOrderingInfos();

            // 表示有無は必須
            boolean isFoundDisp = false;

            // ソート順序は必須
            boolean isFoundOrder = false;

            for (OrderingInfo info : orderInfos) {
                if (!info.isHiddenTable()) {
                    isFoundDisp = true;
                }
                if (info.getOrderNum() != null) {
                    isFoundOrder = true;
                }
            }
            if (!isFoundDisp) {
                // 表示有無
                // {0}は必須です。
                String requestErrKey = tableId;
                String[] objs = { GnomesMessagesConstants.MV01_0002,
                        ResourcesHandler.getString(GnomesResourcesConstants.DI91_0222) };

                requestParamErr.put(requestErrKey, objs);
            }
            else if (!isFoundOrder) {
                // ソート順序
                // {0}は必須です。
                String requestErrKey = tableId;
                String[] objs = { GnomesMessagesConstants.MV01_0002,
                        ResourcesHandler.getString(GnomesResourcesConstants.DI91_0224) };

                requestParamErr.put(requestErrKey, objs);
            }

        }


        return requestParamErr;
    }
}
