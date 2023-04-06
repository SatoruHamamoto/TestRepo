package com.gnomes.external.dao;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;

import org.picketbox.util.StringUtil;

import com.gnomes.common.dao.BaseDao;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.external.data.SystemDefine;
import com.gnomes.external.entity.MstrExternalIfSystemDefine;

/**
 * 外部I/Fシステム定義マスタ Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/09/07 KCC/A.Oomori                初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class MstrExternalIfSystemDefineDao extends BaseDao implements Serializable {

    /**
     * コンストラクタ.
     */
    public MstrExternalIfSystemDefineDao() {

    }

    /**
     * 外部I/Fシステム定義マスタ 取得
     * @return 外部I/Fシステム定義マスタ
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrExternalIfSystemDefine> getMstrExternalIfSystemDefine() throws GnomesAppException {

        List<MstrExternalIfSystemDefine> datas = super.gnomesSystemModel.getMstrExternalIfSystemDefineList();

        return datas;
    }

    /**
     * 外部I/Fシステム定義マスタ 取得
     * @param externalIfTargetCode 外部I/F対象システムコード
     * @return 外部I/Fシステム定義マスタ
     */
    @TraceMonitor
    @ErrorHandling
    public SystemDefine getSystemDefine(String externalIfTargetCode) throws GnomesAppException{

        // パラメータチェック
        if (StringUtil.isNullOrEmpty(externalIfTargetCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            MstrExternalIfSystemDefine.COLUMN_NAME_EXTERNAL_IF_TARGET_CODE, externalIfTargetCode)});

        }

        List<MstrExternalIfSystemDefine> result = gnomesSystemModel.getMstrExternalIfSystemDefineList().stream()
                .filter(item -> item.getExternal_if_target_code().equals(externalIfTargetCode))
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            return null;
        }

        SystemDefine systemDefine = new SystemDefine();
        systemDefine.setExternal_target_code(externalIfTargetCode);

        // 外部I/Fシステム定義マスタの件数分繰り返す
        for (MstrExternalIfSystemDefine mstrExternalIfSystemDefine: result) {

            try {

                // 項目名からフィールドを探す。ない場合NoSuchFieldException
                Field fld = systemDefine.getClass().getDeclaredField(mstrExternalIfSystemDefine.getDefinition_code());

                fld.setAccessible(true);
                if (fld.getType() == (Class<?>)int.class || fld.getType() == (Class<?>)Integer.class) {
                    fld.set(systemDefine, ConverterUtils.stringToNumber(
                            false, mstrExternalIfSystemDefine.getSet_value()).intValue());
                } else if(fld.getType() == (Class<?>)Date.class) {
                    fld.set(systemDefine, ConverterUtils.stringToDate(mstrExternalIfSystemDefine.getSet_value()));
                } else {
                    fld.set(systemDefine, mstrExternalIfSystemDefine.getSet_value());
                }

            } catch (Exception e) {
                continue;
            }
        }

        return systemDefine;

    }

}
