package com.gnomes.common.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.gnomes.common.constants.SystemDefConstants;
import com.gnomes.common.constants.CommonEnums.ComputerSelectableFlag;
import com.gnomes.common.constants.CommonEnums.DisplayType;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.persistence.GnomesEntityManager;
import com.gnomes.system.dao.MstrComputerDao;
import com.gnomes.system.dao.MstrSiteDao;
import com.gnomes.system.dao.MstrSystemDefineDao;
import com.gnomes.system.entity.MstrComputer;
import com.gnomes.system.entity.MstrSite;
import com.gnomes.system.entity.MstrSystemDefine;

/**
 * 共通プルダウン情報取得
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 *  --------------------------------------------------------------------------
 * R0.01.01 2016/11/21 KCC/A.Oomori               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class CommonPullDown implements ICommonPullDown {

	/**
	 * エンティティマネージャー
	 */
	@Inject
	protected GnomesEntityManager em;

	/**
	 * 端末定義 Dao
	 */
    @Inject
    protected MstrComputerDao mstrComputerDao;

	/**
	 * 拠点マスタ Dao
	 */
    @Inject
    protected MstrSiteDao mstrSiteDao;

	/**
	 * システム定義 Dao
	 */
    @Inject
    protected MstrSystemDefineDao mstrSystemDefineDao;

	/**
     * プルダウン情報取得 コンストラクタ
     */
    public CommonPullDown() {
    }

    /**
     * 言語選択プルダウン取得
     * @return 言語選択プルダウン
     */
    public List<PullDownDto> getPD0004() throws GnomesAppException {

        List<PullDownDto> localelist = new ArrayList<PullDownDto>();

        MstrSystemDefine mstrSystemDefine = this.mstrSystemDefineDao.getMstrSystemDefine(
                SystemDefConstants.SYSTEM_LOCALE, SystemDefConstants.CODE_LOCALE_TYPE);

        //表示件数情報がない場合は空のリストを返却
        if(mstrSystemDefine == null || StringUtils.isBlank(mstrSystemDefine.getChar1())){
            // 何も設定しない
        } else {
            String localeTimeZone = "";
            List<String> locale = Arrays.asList(mstrSystemDefine.getChar1().split(","));
            List<String> timeZone = Arrays.asList(mstrSystemDefine.getChar2().split(","));

            for(int i = 0; i < locale.size(); i++)
            {
                localeTimeZone = localeTimeZone + locale.get(i) + "_" + timeZone.get(i) + ",";
            }
            StringUtils.substring(localeTimeZone, 0, -1);

            localelist = Arrays.asList(localeTimeZone.split(","))
                .stream()
                .map(e -> e.toString().trim())
                .map(e -> new PullDownDto(e, e))
                .collect(Collectors.toList());
        }

        return localelist;

    }

    /**
     * 端末情報より端末選択プルダウンを取得
     * @param displayType 表示画面区分
     * @return 端末選択プルダウン
     * @throws GnomesAppException
     */
    public List<PullDownDto> getPD0006(DisplayType displayType) throws GnomesAppException {

        List<PullDownDto> personComputerList = new ArrayList<PullDownDto>();

        // 端末定義を取得
        List<MstrComputer> datasMstrComputer = mstrComputerDao.getMstrComputer();

        if(displayType.equals(DisplayType.CLIENT)){
        	datasMstrComputer = datasMstrComputer.stream()
                    .filter(item -> item.getManage_selectable_flag() == ComputerSelectableFlag.ON.getValue())
                    .sorted(Comparator.comparing(MstrComputer::getComputer_name))
                    .collect(Collectors.toList());
        }
        else if(displayType.equals(DisplayType.PANECON)){
        	datasMstrComputer = datasMstrComputer.stream()
                    .filter(item -> item.getOperate_selectable_flag() == ComputerSelectableFlag.ON.getValue())
                    .sorted(Comparator.comparing(MstrComputer::getComputer_name))
                    .collect(Collectors.toList());
        }

    	for (MstrComputer dataMstrComputer: datasMstrComputer) {
            personComputerList.add(new PullDownDto(
            		dataMstrComputer.getComputer_name(),
                    dataMstrComputer.getComputer_id()));
        }

        return personComputerList;

    }

    /**
     * サイト選択プルダウン取得
     * @return サイト選択プルダウン
     * @throws GnomesAppException
     */
    public List<PullDownDto> getPD0007() throws GnomesAppException {

        List<PullDownDto> pullDownList = new ArrayList<PullDownDto>();

        List<MstrSite> datasMstrSite = this.mstrSiteDao.getMstrSite();
        Collections.sort(datasMstrSite, Comparator.comparing(MstrSite::getSite_name));

        if (!Objects.isNull(datasMstrSite)) {

            for (int i = 0; i < datasMstrSite.size(); i++) {

                PullDownDto pullDownDto = new PullDownDto();
                pullDownDto.setName(datasMstrSite.get(i).getSite_name());
                pullDownDto.setValue(datasMstrSite.get(i).getSite_code());
                pullDownList.add(pullDownDto);

            }

        }

        return pullDownList;

    }

    /**
     * システム定義選択プルダウン取得
     * @param systemDefineCode システム定義コード
     * @return システム定義選択プルダウン
     */
    public List<PullDownDto> getPD0008(String systemDefineType) throws GnomesAppException {

        List<PullDownDto> mstrSystemDefineList = new ArrayList<PullDownDto>();

        // 端末定義を取得
        List<MstrSystemDefine> datasMstrSystemDefine = mstrSystemDefineDao.getMstrSystemDefine();

        datasMstrSystemDefine = datasMstrSystemDefine.stream()
                    .filter(item -> item.getSystem_define_type().equals(systemDefineType))
                    .collect(Collectors.toList());


    	for (MstrSystemDefine dataMstrSystemDefine: datasMstrSystemDefine) {
    		mstrSystemDefineList.add(new PullDownDto(
            		dataMstrSystemDefine.getSystem_define_name(),
                    dataMstrSystemDefine.getChar1()));
        }

        return mstrSystemDefineList;
    }


}