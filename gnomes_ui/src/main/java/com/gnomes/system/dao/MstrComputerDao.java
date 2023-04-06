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
import com.gnomes.system.entity.MstrComputer;

/**
 * 端末定義 Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/04/28 KCC/T.Kamizuru            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class MstrComputerDao extends BaseDao implements Serializable {

    /**
     * コンストラクタ
     */
    public MstrComputerDao() {
    }


    /**
     * 端末定義 取得
     * @return 端末定義
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrComputer> getMstrComputer() throws GnomesAppException {

        List<MstrComputer> datas = gnomesSystemModel.getMstrComputerList();

        return datas;
    }

    /**
     * 端末定義 取得
     * @param 端末ID
     * @return 端末定義
     */
    @TraceMonitor
    @ErrorHandling
    public MstrComputer getMstrComputer(String computerId) throws GnomesAppException {

        if (StringUtil.isNullOrEmpty(computerId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex =
                    exceptionFactory.createGnomesAppException(
                            null,
                            GnomesMessagesConstants.ME01_0050,
                            String.valueOf(computerId));
            throw ex;

        }

        List<MstrComputer> result = gnomesSystemModel.getMstrComputerList().stream()
                .filter(item -> item.getComputer_id().equals(computerId))
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }

}
