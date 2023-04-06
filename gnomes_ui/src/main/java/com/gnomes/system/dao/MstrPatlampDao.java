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
import com.gnomes.system.entity.MstrPatlamp;

/**
 * パトランプマスタ Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/07/05 KCC/I.Shibasaka           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class MstrPatlampDao extends BaseDao implements Serializable {

    /**
     * コンストラクタ
     */
    public MstrPatlampDao() {
    }

    /**
     * パトランプマスタリスト 取得
     * @return パトランプマスタリスト
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrPatlamp> getMstrPatlamp() throws GnomesAppException {
    	List<MstrPatlamp> datas = gnomesSystemModel.getMstrPatlampList();
    	return datas;
    }

    /**
     * パトランプマスタ 取得
     * @param patlampId パトランプID
     * @return パトランプマスタ
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public MstrPatlamp getMstrPatlamp(String patlampId) throws GnomesAppException {

    	if (StringUtil.isNullOrEmpty(patlampId)) {
    		StringBuilder params = new StringBuilder();
    		params.append("patlampId：").append(patlampId);

	      // ME01.0050:「パラメータが不正です。({0})」
	      throw super.exceptionFactory.createGnomesAppException(
              null, GnomesMessagesConstants.ME01_0050, params.toString());
    	}

        List<MstrPatlamp> datas = gnomesSystemModel.getMstrPatlampList().stream()
                .filter(item -> item.getPatlamp_id().equals(patlampId))
                .collect(Collectors.toList());

        if (datas.isEmpty()) {
            return null;
        }

        return datas.get(0);
    }
}
