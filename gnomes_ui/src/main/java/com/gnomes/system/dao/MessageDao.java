package com.gnomes.system.dao;


import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonEnums;
import com.gnomes.common.constants.CommonEnums.PushNoticeFlag;
import com.gnomes.common.constants.GnomesQueryConstants;
import com.gnomes.common.constants.MessageConstants;
import com.gnomes.common.dao.BaseDao;
import com.gnomes.common.dao.CheckMaxCount;
import com.gnomes.common.dto.CountDto;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.search.data.SearchSetting;
import com.gnomes.common.search.query.Condition;
import com.gnomes.common.search.query.SearchCondition;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.system.dto.MessageCntDto;
import com.gnomes.system.dto.MessageListDto;
import com.gnomes.system.dto.MessageWatcherSearchListDto;
import com.gnomes.system.dto.PopupMessageListDto;
import com.gnomes.system.entity.Message;
import com.gnomes.system.entity.MstrMessageDefine;

/**
 * メッセージ情報 Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/04/28 KCC/T.Kamizuru               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class MessageDao extends BaseDao implements Serializable {

	/** Gnomesエンティティマネージャ */
	//[2019/03/05 浜本記載] 独自トランザクションのため廃止
	//	@Inject
	//	protected GnomesEntityManager em;

	/** 検索条件 */
    @Inject
    transient SearchCondition sc;


    /**
     * コンストラクタ
     */
    public MessageDao() {
    }

    /**
     * メッセージ取得
     * @param messageKey メッセージキー
     * @return data M201:メッセージ情報
     */
    @TraceMonitor
    @ErrorHandling
    public Message getMessage(String messageKey, EntityManager em) throws GnomesAppException {
        if(StringUtil.isNullOrEmpty(messageKey)){
            // ME02.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0050,"メッセージキー");
            throw ex;
        }
        Message data = em.find(Message.class, messageKey);
        return data;
    }

    /**
     * ポップアップメッセージ取得
     * @param displayCount ポップアップメッセージ表示件数
     * @param em 独自エンティティマネージャー
     * @return ポップアップメッセージ情報リスト
     */
    @TraceMonitor
    @ErrorHandling
    public List<PopupMessageListDto> getPopupMessageList(int displayCount,EntityManager em) throws GnomesAppException {

        if(displayCount < 0){
            // ME02.0050:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0050,"ポップアップメッセージ表示件数");
            throw ex;
        }

        // 「ポップアップメッセージ取得」クエリ
        TypedQuery<PopupMessageListDto> queryGetPopupMessageList =
                em.createNamedQuery(GnomesQueryConstants.QUERY_NAME_GET_POPUP_MESSAGE_LIST,
                        PopupMessageListDto.class);

        queryGetPopupMessageList.setParameter(MstrMessageDefine.COLUMN_NAME_IS_NOTICE_PUSH, PushNoticeFlag.ON.getValue());

        // 最大取得件数を指定して取得
        List<PopupMessageListDto> datasPopupMessageListDto =
                queryGetPopupMessageList.setFirstResult(0).setMaxResults(displayCount).getResultList();

        return datasPopupMessageListDto;
    }

    /**
     * メッセージ一覧取得
     * @return メッセージ一覧情報
     * @param dispComputerNamer 表示先コンピュータ名
     * @param userLocale ユーザロケール
     * @param maxResult メッセージ表示件数
     * @throws GnomesAppException
     * @throws ParseException
     */
    @TraceMonitor
    @ErrorHandling
    @CheckMaxCount
    public List<MessageListDto> getMessageList(
            String dateFrom,
            String dateTo,
            String siteId,
            String msgNo,
            String category,
            //String status,
            SearchSetting searchSetting) throws GnomesAppException, ParseException {
        Condition condition = new Condition();

        // 条件
        if(!StringUtil.isNullOrEmpty(dateFrom)) condition.and(condition.ge(Message.COLUMN_NAME_OCCUR_DATE,":dateFrom",null));
        if(!StringUtil.isNullOrEmpty(dateTo)) condition.and(condition.le(Message.COLUMN_NAME_OCCUR_DATE,":dateTo",null));
        if(!StringUtil.isNullOrEmpty(siteId)) condition.and(condition.equalCheck(Message.COLUMN_NAME_OCCUR_SITE_CODE, ":site_code",null));
        if(!StringUtil.isNullOrEmpty(msgNo)) condition.and(condition.equalCheck(Message.COLUMN_NAME_MESSAGE_NO, ":messageno",null));
        if(!StringUtil.isNullOrEmpty(category)) condition.and(condition.equalCheck(Message.COLUMN_NAME_CATEGORY, ":category",null));
        sc.setCondition(condition);

        // パラメータ作成
        Map<String,Object> param = new HashMap<String,Object>();
        Date zdVal = null;
        String format = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0002, req.getUserLocale());

        if(!StringUtil.isNullOrEmpty(dateFrom)){
            zdVal = ConverterUtils.stringToDateFormat(dateFrom, format);
            param.put(":dateFrom", zdVal);
        }

        if(!StringUtil.isNullOrEmpty(dateTo)){
            zdVal = ConverterUtils.stringToDateFormat(dateTo, format);
            param.put(":dateTo", zdVal);
        }

        if(!StringUtil.isNullOrEmpty(siteId)) param.put(":site_code", siteId);
        if(!StringUtil.isNullOrEmpty(msgNo)) param.put(":messageno", msgNo);
        if(!StringUtil.isNullOrEmpty(category)) param.put(":category", category);

        // クエリ作成
        TypedQuery<MessageListDto> getMessageListQuery =
                sc.replaceQueryCondition(GnomesQueryConstants.QUERY_NAME_GET_MESSAGE_LIST, MessageListDto.class, param);

        // 共通検索ダイアログを使用していないので、取得範囲は個別で指定
        getMessageListQuery.setFirstResult(0).setMaxResults(searchSetting.getMaxDispCount() + 1);

        //最大件数を指定して取得
        List<MessageListDto> datasMessageListDto = getMessageListQuery.getResultList();

        return datasMessageListDto;
    }

    /**
     * メッセージ一覧取得
     * @return メッセージ一覧情報
     * @param dispComputerNamer 表示先コンピュータ名
     * @param userLocale ユーザロケール
     * @param maxResult メッセージ表示件数
     * @throws GnomesAppException
     * @throws ParseException
     */
    @TraceMonitor
    @ErrorHandling
    public List<MessageListDto> getMessageListPaging(
            String dateFrom,
            String dateTo,
            String siteId,
            String msgNo,
            String category,
            SearchSetting searchSetting) throws GnomesAppException, ParseException{
        Condition condition = new Condition();

        // 条件
        if(!StringUtil.isNullOrEmpty(dateFrom)) condition.and(condition.ge(Message.COLUMN_NAME_OCCUR_DATE, ":dateFrom",null));
        if(!StringUtil.isNullOrEmpty(dateTo)) condition.and(condition.le(Message.COLUMN_NAME_OCCUR_DATE, ":dateTo",null));
        if(!StringUtil.isNullOrEmpty(siteId)) condition.and(condition.equalCheck(Message.COLUMN_NAME_OCCUR_SITE_CODE, ":site_code",null));
        if(!StringUtil.isNullOrEmpty(msgNo)) condition.and(condition.equalCheck(Message.COLUMN_NAME_MESSAGE_NO, ":messageno",null));
        if(!StringUtil.isNullOrEmpty(category)) condition.and(condition.equalCheck(Message.COLUMN_NAME_CATEGORY, ":category",null));
        sc.setCondition(condition);

        // パラメータ作成
        Map<String,Object> param = new HashMap<String,Object>();
        Date zdVal = null;
        String format = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0002, req.getUserLocale());

        if(!StringUtil.isNullOrEmpty(dateFrom)){
            zdVal = ConverterUtils.stringToDateFormat(dateFrom, format);
            param.put(":dateFrom", zdVal);
        }

        if(!StringUtil.isNullOrEmpty(dateTo)){
            zdVal = ConverterUtils.stringToDateFormat(dateTo, format);
            param.put(":dateTo", zdVal);
        }

        if(!StringUtil.isNullOrEmpty(siteId)) param.put(":site_code", siteId);
        if(!StringUtil.isNullOrEmpty(msgNo)) param.put(":messageno", msgNo);
        if(!StringUtil.isNullOrEmpty(category)) param.put(":category", category);

        // クエリ作成
        TypedQuery<MessageListDto> getMessageListQuery =
                sc.replaceQueryCondition(GnomesQueryConstants.QUERY_NAME_GET_MESSAGE_LIST, MessageListDto.class, param);

        // 共通検索ダイアログを使用していないので、取得範囲は個別で指定
        int start = searchSetting.getNowPage() - 1;
        if (start <= 0) {
            start = 0;
        } else {
            start *= searchSetting.getOnePageDispCount();
        }
        getMessageListQuery.setFirstResult(start).setMaxResults(searchSetting.getOnePageDispCount());


        // 取得
        List<MessageListDto> datasMessageListDto =
                getMessageListQuery.getResultList();

        return datasMessageListDto;
    }


    /**
     * メッセージ件数情報取得
     * @return メッセージ件数情報情報
     * @param dispComputerNamer 表示先コンピュータ名
     * @param userLocale ユーザロケール
     * @param displayCount メッセージ表示件数
     * @throws GnomesAppException
     * @throws ParseException
     */
    @TraceMonitor
    @ErrorHandling
    public MessageCntDto getMessageCntInfo(String dateFrom,String dateTo,String siteId,String msgNo) throws GnomesAppException, ParseException{


        Condition condition = new Condition();

        // 条件
        if(!StringUtil.isNullOrEmpty(dateFrom)) condition.and(condition.gt(Message.COLUMN_NAME_OCCUR_DATE, ":dateFrom",null));
        if(!StringUtil.isNullOrEmpty(dateTo)) condition.and(condition.le(Message.COLUMN_NAME_OCCUR_DATE, ":dateTo",null));
        if(!StringUtil.isNullOrEmpty(siteId)) condition.and(condition.equalCheck(Message.COLUMN_NAME_OCCUR_SITE_CODE, ":site_id",null));
        if(!StringUtil.isNullOrEmpty(msgNo)) condition.and(condition.equalCheck(Message.COLUMN_NAME_MESSAGE_NO, ":messageno",null));

        sc.setCondition(condition);

        // パラメータ作成
        Map<String,Object> param = new HashMap<String,Object>();
        Date zdVal = null;
        String format = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0002, req.getUserLocale());

        if(!StringUtil.isNullOrEmpty(dateFrom)){
            zdVal = ConverterUtils.stringToDateFormat(dateFrom, format);
            param.put(":dateFrom", zdVal);
        }

        if(!StringUtil.isNullOrEmpty(dateTo)){
            zdVal = ConverterUtils.stringToDateFormat(dateTo, format);
            param.put(":dateTo", zdVal);
        }
        if(!StringUtil.isNullOrEmpty(siteId)) param.put(":site_id", siteId);
        if(!StringUtil.isNullOrEmpty(msgNo)) param.put(":messageno", msgNo);

        // クエリ作成
        TypedQuery<MessageCntDto> getMessageCntQuery =
                sc.replaceQueryCondition(GnomesQueryConstants.QUERY_NAME_GET_MESSAGE_OCCUR_CNT, MessageCntDto.class, param);

        //パラメータ設定
        getMessageCntQuery.setParameter(MessageConstants.MSG_CATEGORY_ALERT, CommonEnums.MessageCategory.MessageCategory_Alarm.getValue());
        getMessageCntQuery.setParameter(MessageConstants.MSG_CATEGORY_OPERATION_GUIDE, CommonEnums.MessageCategory.MessageCategory_OperationGuide.getValue());

        //件数取得
        MessageCntDto datasMessageCntDto = getMessageCntQuery.getSingleResult();

        return datasMessageCntDto;
    }

    /**
     * メッセージ件数情報取得
     * @return メッセージ件数情報情報
     * @param dispComputerNamer 表示先コンピュータ名
     * @param userLocale ユーザロケール
     * @param displayCount メッセージ表示件数
     * @throws GnomesAppException
     * @throws ParseException
     */
    @TraceMonitor
    @ErrorHandling
    public CountDto getMessageCnt(String dateFrom,String dateTo,String siteId,String msgNo,String category) throws GnomesAppException, ParseException{

        Condition condition = new Condition();

        if(!StringUtil.isNullOrEmpty(dateFrom)) condition.and(condition.ge(Message.COLUMN_NAME_OCCUR_DATE, ":dateFrom",null));
        if(!StringUtil.isNullOrEmpty(dateTo)) condition.and(condition.le(Message.COLUMN_NAME_OCCUR_DATE, ":dateTo",null));
        if(!StringUtil.isNullOrEmpty(siteId)) condition.and(condition.equalCheck(Message.COLUMN_NAME_OCCUR_SITE_CODE, ":site_id",null));
        if(!StringUtil.isNullOrEmpty(msgNo)) condition.and(condition.equalCheck(Message.COLUMN_NAME_MESSAGE_NO, ":messageno",null));
        if(!StringUtil.isNullOrEmpty(category)) condition.and(condition.equalCheck(Message.COLUMN_NAME_CATEGORY, ":category",null));
        sc.setCondition(condition);

        // パラメータ作成
        Map<String,Object> param = new HashMap<String,Object>();
        Date zdVal = null;
        String format = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0002, req.getUserLocale());

        if(!StringUtil.isNullOrEmpty(dateFrom)){
            zdVal = ConverterUtils.stringToDateFormat(dateFrom, format);
            param.put(":dateFrom", zdVal);
        }

        if(!StringUtil.isNullOrEmpty(dateTo)){
            zdVal = ConverterUtils.stringToDateFormat(dateTo, format);
            param.put(":dateTo", zdVal);
        }
        if(!StringUtil.isNullOrEmpty(siteId)) param.put(":site_id", siteId);
        if(!StringUtil.isNullOrEmpty(msgNo)) param.put(":messageno", msgNo);
        if(!StringUtil.isNullOrEmpty(category)) param.put(":category", category);

        // クエリ作成
        TypedQuery<CountDto> getCountQuery =
                sc.replaceQueryCondition(GnomesQueryConstants.QUERY_NAME_GET_MESSAGE_CNT, CountDto.class, param);


        //件数を取得する
        CountDto cntDto = getCountQuery.getSingleResult();

        return cntDto;

    }

    /**
     * メッセージ一監視機能用Key覧取得
     * @return メッセージ監視機能用Key一覧情報
     * @param dateFrom 対象日時（From）
     * @param dateTo 対象日時（From）
     * @throws GnomesAppException
     * @throws ParseException
     */
    @TraceMonitor
    @ErrorHandling
    public List<MessageWatcherSearchListDto> getMessageWatcherSearchList(
            String dateFrom, String dateTo, EntityManager em) throws GnomesAppException, ParseException{

        // パラメータ作成
        Date occurDateFrom = null;
        Date occurDateTo = null;
        String format = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0002, req.getUserLocale());

        if(!StringUtil.isNullOrEmpty(dateFrom)){
            occurDateFrom = ConverterUtils.stringToDateFormat(dateFrom, format);
        }

        if(!StringUtil.isNullOrEmpty(dateTo)){
            occurDateTo = ConverterUtils.stringToDateFormat(dateTo, format);
        }

        // クエリ作成
        TypedQuery<MessageWatcherSearchListDto> getMessageWatcherSearchListQuery = em.createNamedQuery(
                GnomesQueryConstants.QUERY_NAME_GET_MESSAGE_WATCHER_SEARCH_LIST, MessageWatcherSearchListDto.class);

        getMessageWatcherSearchListQuery.setParameter("last_regist_datetime_from", occurDateFrom);
        getMessageWatcherSearchListQuery.setParameter("last_regist_datetime_to", occurDateTo);

        // 取得
        List<MessageWatcherSearchListDto> datasMessageWatcherSearchListDto = getMessageWatcherSearchListQuery.getResultList();

        return datasMessageWatcherSearchListDto;
    }

}
