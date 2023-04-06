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
import com.gnomes.external.data.FileDefine;
import com.gnomes.external.entity.MstrExternalIfFileDefine;

/**
 * 外部I/Fファイル構成定義マスタ Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/09/07 KCC/A.Oomori                初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class MstrExternalIfFileDefineDao extends BaseDao implements Serializable {

	 /**
     * コンストラクタ.
     */
    public MstrExternalIfFileDefineDao() {

    }

    /**
     * 外部I/Fファイル構成定義マスタ取得
     * @return 外部I/Fファイル構成定義マスタ
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrExternalIfFileDefine> getMstrExternalIfFileDefineList() throws GnomesAppException {

        List<MstrExternalIfFileDefine> datas = super.gnomesSystemModel.getMstrExternalIfFileDefineList();

        return datas;
    }

    /**
     * 外部I/Fファイル構成定義マスタ 取得
     * @param fileType ファイル種別
     * @return 外部I/Fファイル構成定義マスタ
     */
    @TraceMonitor
    @ErrorHandling
    public FileDefine getFileDefine(String fileType) throws GnomesAppException {

        // パラメータチェック
        if (StringUtil.isNullOrEmpty(fileType)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            MstrExternalIfFileDefine.COLUMN_NAME_FILE_TYPE, fileType)});

        }

        List<MstrExternalIfFileDefine> result = gnomesSystemModel.getMstrExternalIfFileDefineList().stream()
                .filter(item -> item.getFile_type().equals(fileType))
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            return null;
        }

        FileDefine fileDefine = new FileDefine();
        fileDefine.setFile_type(fileType);

        // 外部I/Fファイル構成定義マスタの件数分繰り返す
        for (MstrExternalIfFileDefine MstrExternalIfFileDefine: result) {

            try {

                // 項目名からフィールドを探す。ない場合NoSuchFieldException
                Field fld = fileDefine.getClass().getDeclaredField(MstrExternalIfFileDefine.getDefinition_code());

                fld.setAccessible(true);
                if (fld.getType() == (Class<?>)int.class || fld.getType() == (Class<?>)Integer.class) {
                    fld.set(fileDefine, ConverterUtils.stringToNumber(
                            false, MstrExternalIfFileDefine.getSet_value()).intValue());
                } else if (fld.getType() == (Class<?>)Date.class) {
                    fld.set(fileDefine, ConverterUtils.stringToDate(MstrExternalIfFileDefine.getSet_value()));
                } else {
                    fld.set(fileDefine, MstrExternalIfFileDefine.getSet_value());
                }

            } catch (Exception e) {
                continue;
            }
        }

        return fileDefine;

    }

    /**
     * 外部I/Fファイル構成定義マスタ取得
     * @param fileType ファイル種別
     * @param definitionCode 項目コード
     * @return 外部I/Fファイル構成定義マスタ
     */
    @TraceMonitor
    @ErrorHandling
    public String[] getSetValue(String fileType, String definitionCode) throws GnomesAppException{


        // パラメータチェック
        if (StringUtil.isNullOrEmpty(fileType)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            MstrExternalIfFileDefine.COLUMN_NAME_FILE_TYPE, fileType)});

        }

        //パラメータチェック
        if (StringUtil.isNullOrEmpty(definitionCode)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            MstrExternalIfFileDefine.COLUMN_NAME_DEFINITION_CODE, definitionCode)});

        }

        List<MstrExternalIfFileDefine> result = gnomesSystemModel.getMstrExternalIfFileDefineList().stream()
                .filter(item -> item.getFile_type().equals(fileType) && item.getDefinition_code().equals(definitionCode))
                .collect(Collectors.toList());

        if (result.isEmpty() || StringUtil.isNullOrEmpty(result.get(0).getSet_value())) {
            return null;
        }

        return result.get(0).getSet_value().split(",");

    }

}
