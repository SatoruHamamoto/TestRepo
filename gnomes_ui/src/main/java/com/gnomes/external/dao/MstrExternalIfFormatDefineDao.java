package com.gnomes.external.dao;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;

import org.picketbox.util.StringUtil;

import com.gnomes.common.dao.BaseDao;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.external.entity.MstrExternalIfFormatDefine;

/**
 * 外部I/Fフォーマット定義マスタ Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/03/06 YJP/H.Yamada                初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class MstrExternalIfFormatDefineDao extends BaseDao implements Serializable {

    /** コンストラクタ. */
    public MstrExternalIfFormatDefineDao() {

    }

    /**
     * 外部I/Fフォーマット定義マスタ 取得
     * @return 外部I/Fフォーマット定義マスタリスト
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrExternalIfFormatDefine> getMstrExternalIfFormatDefineList() throws GnomesAppException {

        List<MstrExternalIfFormatDefine> datas = super.gnomesSystemModel.getMstrExternalIfFormatDefineList();

        return datas;

    }

    /**
     *
     * @param externalIfFormatId 外部I/FフォーマットID
     * @return 外部I/Fフォーマット定義マスタ
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public MstrExternalIfFormatDefine getMstrExternalIfFormatDefine(String externalIfFormatId) throws GnomesAppException {

        // パラメータチェック
        if (StringUtil.isNullOrEmpty(externalIfFormatId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            MstrExternalIfFormatDefine.COLUMN_NAME_FORMAT_ID, externalIfFormatId)});

        }

        List<MstrExternalIfFormatDefine> result = this.gnomesSystemModel.getMstrExternalIfFormatDefineList().stream()
                .filter(item -> item.getFormat_id().equals(externalIfFormatId))
                .collect(Collectors.toList());


        if (!result.isEmpty()) {
            return result.get(0);
        }

        return null;

    }

}
