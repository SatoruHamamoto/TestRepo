package com.gnomes.system.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceUnit;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.constants.CommonEnums;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.model.BaseModel;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.GnomesDateUtil;
import com.gnomes.system.dao.MessageDao;
import com.gnomes.system.dao.MstrLinkDao;
import com.gnomes.system.data.PopupMessageInfo;
import com.gnomes.system.dto.PopupMessageListDto;

/**
 * ポップアップメッセージ機能
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/01/12 KCC/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class PopupMessageModel extends BaseModel {

    /** ロガー */
    @Inject
    protected transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected transient LogHelper logHelper;

    /** メッセージDao */
    @Inject
    protected MessageDao messageDao;

    /** リンク情報Dao */
    @Inject
    protected MstrLinkDao mstrLinkDao;

    /** 独自管理トランザクション */
    @PersistenceUnit(unitName = CommonConstants.PERSISTENCE_UNIT_NAME_NON_JTA)
    protected transient EntityManagerFactory emf;
    /**
     * デフォルト・コンストラクタ
     */
    public PopupMessageModel() {
    }

    /**
     * ポップアップメッセージ取得
     * @param popupMessageInfoList ポップアップメッセージ情報一覧
     * @return ポップアップメッセージ情報
     */
    public List<PopupMessageInfo> getPopupMessage() throws Exception
    {
        List<PopupMessageInfo> popupMessageInfoList = null;

        // ポップアップメッセージ表示件数
        int displayCount = 1;

        if (gnomesSessionBean.getPopupDisplayCount() != null) {

            displayCount = gnomesSessionBean.getPopupDisplayCount();

            if (displayCount < 1) {
                displayCount = 1;
            }
        }

        popupMessageInfoList = this.getPopupMessage(
                gnomesSessionBean.getComputerName(),
                gnomesSessionBean.getUserLocale(),
                displayCount);

        gnomesSessionBean.setMessageList(popupMessageInfoList);

        return popupMessageInfoList;
    }

    /**
     * ポップアップメッセージ取得
     * @param dispComputerNamer 表示先コンピュータ名
     * @param userLocale ユーザロケール
     * @param displayCount ポップアップメッセージ表示件数
     * @return ポップアップメッセージ
     * @throws Exception
     */
    public List<PopupMessageInfo> getPopupMessage(
            String dispComputerNamer,
            Locale userLocale,
            int displayCount) throws Exception
    {
    	boolean successFlag = true;

    	//独自トランザクション
        EntityManager em = this.emf.createEntityManager();
        EntityTransaction tran = em.getTransaction();

        List<PopupMessageInfo> popupMessageInfoList;

        tran.begin();
        em.setFlushMode(FlushModeType.AUTO);

        List<PopupMessageListDto> datasPopupMessageListDto = null;

        try {
	        // 最大取得件数を指定して取得
	        datasPopupMessageListDto = messageDao.getPopupMessageList(displayCount,em);

        } catch (Exception e) {
	        this.logHelper.severe(this.logger, null, null, e.getMessage());
	        successFlag = false;
	        return new ArrayList<PopupMessageInfo>();
	    } finally {
	    	em.flush();

            if (successFlag) {
                tran.commit();
            } else {
                tran.rollback();
            }
            em.close();
	    }
        //メッセージ変換
        popupMessageInfoList = MessagesHandler.getMessageInfoList(
                datasPopupMessageListDto, userLocale, PopupMessageInfo.class);

        // 日付フォーマット
        String datePatternUserLocale = ResourcesHandler.getString(CommonConstants.RES_OCCURDATE_FORMAT, gnomesSessionBean.getUserLocale());


        for (int i = 0; i <  popupMessageInfoList.size(); i++) {

            String localeDate = "";
            //Web.xmlのタイムゾーンがUTCの場合,
            if (CommonConstants.ZONEID_UTC.equals(TimeZone.getDefault().getID())) {

                Date date = ConverterUtils.stringToDateFormat(popupMessageInfoList.get(i).getOccurDate(), ResourcesHandler.getString(CommonConstants.RES_OCCURDATE_FORMAT));

                //UTCのDate型の値を任意のロケール(タイムゾーン)のString型に変更
                localeDate = GnomesDateUtil.convertDateToLocaleString(date, datePatternUserLocale,
                        gnomesSessionBean.getUserTimeZone(), gnomesSessionBean.getUserLocale());
            }
            else {
                //Web.xmlのタイムゾーンがUTCではない場合は変換を行わない
                localeDate = ConverterUtils.dateTimeToString(popupMessageInfoList.get(i).getOccurDate(),datePatternUserLocale);
            }

            //popupMessageInfoList.get(i).setOccurDate(ConverterUtils.dateTimeToString(popupMessageInfoList.get(i).getOccurDate(), datePatternUserLocale));
            popupMessageInfoList.get(i).setOccurDate(localeDate);
            // ガイダンスメッセージ
            popupMessageInfoList.get(i).setGuidanceMessage(datasPopupMessageListDto.get(i).getGuidance_message());
            // リンク情報
            popupMessageInfoList.get(i).setLinkURL(datasPopupMessageListDto.get(i).getLink_info());
            // リンク名
            popupMessageInfoList.get(i).setLinkName(datasPopupMessageListDto.get(i).getLink_name());
            // 領域の文字列（領域区分は1,2しかないが、2以外は実行領域のみとする
            Integer dbAreaDiv = datasPopupMessageListDto.get(i).getDb_area_div();
            //保管領域のみ比較する
            if(Objects.nonNull(dbAreaDiv) && (dbAreaDiv.equals(Integer.valueOf(CommonEnums.RegionType.STORAGE.getValue())))){
                //保管領域
                popupMessageInfoList.get(i).setDbAreaDiv(ResourcesHandler.getString(GnomesResourcesConstants.DI01_0116, gnomesSessionBean.getUserLocale()));
            }
            else {
                popupMessageInfoList.get(i).setDbAreaDiv(ResourcesHandler.getString(GnomesResourcesConstants.DI01_0115, gnomesSessionBean.getUserLocale()));
            }
        }

        return popupMessageInfoList;
    }

}
