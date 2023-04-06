package com.gnomes.system.dao;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;

import org.picketbox.util.StringUtil;

import com.gnomes.common.dao.BaseDao;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.system.entity.MstrPatlampModel;

/**
 * パトランプ機種マスタ Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/07/05 KCC/I.Shibasaka           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class MstrPatlampModelDao extends BaseDao implements Serializable {

    /**
     * コンストラクタ
     */
    public MstrPatlampModelDao() {
    }

    /**
     * パトランプ機種マスタリスト 取得
     * @return パトランプ機種マスタリスト
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrPatlampModel> getMstrPatlampModel() throws GnomesAppException {
    	List<MstrPatlampModel> datas = gnomesSystemModel.getMstrPatlampModelList();
    	return datas;
    }

    /**
     * パトランプ機種マスタ 取得
     * @param patlampModelId パトランプ機種ID
     * @return パトランプ機種マスタ
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public MstrPatlampModel getMstrPatlampModel(String patlampModelId) throws GnomesAppException {

    	if (StringUtil.isNullOrEmpty(patlampModelId)) {
            StringBuilder params = new StringBuilder();
            params.append("patlampModelId：").append(patlampModelId);

            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050, params.toString());
    	}

        List<MstrPatlampModel> datas = gnomesSystemModel.getMstrPatlampModelList().stream()
                .filter(item -> item.getPatlamp_model_id().equals(patlampModelId))
                .collect(Collectors.toList());

        if (datas.isEmpty()) {
            return null;
        }

        return datas.get(0);
    }

}
