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
import com.gnomes.system.entity.MstrSite;

/**
 * 拠点マスタ Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/04/28 KCC/T.Kamizuru               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class MstrSiteDao extends BaseDao implements Serializable {

    /**
     * コンストラクタ
     */
    public MstrSiteDao() {
    }


    /**
     * 拠点マスタ 取得
     * @return 拠点マスタ
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrSite> getMstrSite() throws GnomesAppException {

        List<MstrSite> datas = gnomesSystemModel.getMstrSiteList();

        return datas;
    }

    /**
     * 拠点マスタ(1件) 取得
     * @param siteCode 拠点コード
     * @return 拠点マスタ(1件)
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public MstrSite getMstrSite(String siteCode) throws GnomesAppException {

        if (StringUtil.isNullOrEmpty(siteCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,String.valueOf(siteCode));

        }

        List<MstrSite> result = gnomesSystemModel.getMstrSiteList().stream()
                .filter(item -> item.getSite_code().equals(siteCode))
                .collect(Collectors.toList());


        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }

}
