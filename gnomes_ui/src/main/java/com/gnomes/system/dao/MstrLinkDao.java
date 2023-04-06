package com.gnomes.system.dao;


import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;

import org.picketbox.util.StringUtil;

import com.gnomes.common.dao.BaseDao;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.system.entity.MstrLink;

/**
 * リンク情報 Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/04/28 KCC/T.Kamizuru            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class MstrLinkDao extends BaseDao implements Serializable {

    /**
     * コンストラクタ
     */
    public MstrLinkDao() {
    }

    /**
     * リンク情報 取得
     * @return リンク情報
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrLink> getMstrLink() throws GnomesAppException {
        return gnomesSystemModel.getMstrLinkList();
    }

    /**
     * リンク情報 取得(一件)
     * @param messageKey
     * @return リンク情報
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public MstrLink getMstrLinkInfo(String messageKey) throws GnomesAppException {

        if (StringUtil.isNullOrEmpty(messageKey)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex =
                    exceptionFactory.createGnomesAppException(
                            null,
                            GnomesMessagesConstants.ME01_0050,
                            String.valueOf(messageKey));
            throw ex;


        }

        List<MstrLink> result = gnomesSystemModel.getMstrLinkList().stream()
                .filter(item -> item.getMessage_key().equals(messageKey))
                .collect(Collectors.toList());


        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }

    /**
     * リンク情報 取得（複数件）
     * @param messageKeyList
     * @return リンク情報
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrLink> getMstrLinkInfo(Set<String> messageKeyList) throws GnomesAppException {

        if (Objects.isNull(messageKeyList)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesAppException ex =
                    exceptionFactory.createGnomesAppException(
                            null,
                            GnomesMessagesConstants.ME01_0050,
                            String.valueOf(messageKeyList));
            throw ex;

        }

        List<MstrLink> result = gnomesSystemModel.getMstrLinkList().stream()
                .filter(item -> messageKeyList.contains(item.getMessage_key()))
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            return Collections.emptyList();
        }

        return result;
    }
}
