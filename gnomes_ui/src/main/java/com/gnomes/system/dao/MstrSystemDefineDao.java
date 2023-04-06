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
import com.gnomes.system.entity.MstrSystemDefine;

/**
 * システム定義 Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/04/28 KCC/T.Kamizuru               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class MstrSystemDefineDao extends BaseDao implements Serializable {

    /**
     * コンストラクタ
     */
    public MstrSystemDefineDao() {
    }

    /**
     * システム定義取得
     * @return List<MstrSystemDefine>
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrSystemDefine> getMstrSystemDefine() throws GnomesAppException {

        // システム定義
        return gnomesSystemModel.getMstrSystemDefineList();
    }

    /**
     * システム定義を取得
     * @param systemDefineType システム定義区分
     * @param systemDefineCode システム定義コード
     * @return システム定義
    */
    @TraceMonitor
    @ErrorHandling
    public MstrSystemDefine getMstrSystemDefine(
            String systemDefineType, String systemDefineCode) throws GnomesAppException {

        if (StringUtil.isNullOrEmpty(systemDefineType)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            MstrSystemDefine.COLUMN_NAME_SYSTEM_DEFINE_TYPE, systemDefineType)});

        }

        if (StringUtil.isNullOrEmpty(systemDefineCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            MstrSystemDefine.COLUMN_NAME_SYSTEM_DEFINE_CODE, systemDefineCode)});

        }

        MstrSystemDefine data = null;

        List<MstrSystemDefine> result = getMstrSystemDefine().stream()
             .filter(item -> item.getSystem_define_type().equals(systemDefineType)
                                 && item.getSystem_define_code().equals(systemDefineCode))
             .collect(Collectors.toList());

        if(result.size()>0){
            data = result.get(0);
        }

        return data;
    }

}
