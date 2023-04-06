package com.gnomes.external.dao;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;

import org.picketbox.util.StringUtil;

import com.gnomes.common.dao.BaseDao;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.external.data.DataDefine;
import com.gnomes.external.entity.MstrExternalIfDataDefine;

/**
 * 外部I/Fデータ項目定義マスタ Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/09/07 KCC/A.Oomori                初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class MstrExternalIfDataDefineDao extends BaseDao implements Serializable {

    /**
     * コンストラクタ.
     */
    public MstrExternalIfDataDefineDao() {

    }

    /**
     * 外部I/Fデータ項目定義マスタ取得
     * @return 外部I/Fデータ項目定義マスタ
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrExternalIfDataDefine> getMstrExternalIfDataDefineList() throws GnomesAppException {

        List<MstrExternalIfDataDefine> datas = super.gnomesSystemModel.getMstrExternalIfDataDefineList();

        return datas;
    }

    /**
     * 外部I/Fデータ項目定義マスタ取得
     * @param formatId フォーマットID
     * @return 外部I/Fデータ項目定義マスタ
     */
    @TraceMonitor
    @ErrorHandling
    public List<DataDefine> getDataDefineList(String formatId) throws GnomesAppException {

        // パラメータチェック
        if (StringUtil.isNullOrEmpty(formatId)) {
             // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,
                    new Object[]{super.createMessageParamsRequired(
                            MstrExternalIfDataDefine.COLUMN_NAME_FORMAT_ID, formatId)});

        }

        List<DataDefine> dataDefineList = new ArrayList<>();
        DataDefine dataDefine = new DataDefine();

        Integer itemNumber = 1;
        for (;;itemNumber++) {
            dataDefine = this.getDataDefine(formatId, itemNumber);
            if(dataDefine == null){
                break;
            } else if(StringUtil.isNullOrEmpty(dataDefine.getData_item_name())){
            	continue;
            }
            dataDefineList.add(dataDefine);
        }
        return dataDefineList;

    }

    /**
     * 外部I/Fデータ項目定義マスタ取得
     * @param formatId フォーマットID
     * @param itemNumber データ項目番号
     * @return 外部I/Fデータ項目定義マスタ
     */
    @TraceMonitor
    @ErrorHandling
    public DataDefine getDataDefine(String formatId, Integer itemNumber) throws GnomesAppException {

        // パラメータチェック
        if (StringUtil.isNullOrEmpty(formatId)) {
            // ME01.0050:「パラメータが不正です。({0})」
           throw super.exceptionFactory.createGnomesAppException(
                   null, GnomesMessagesConstants.ME01_0050,
                   new Object[]{super.createMessageParamsRequired(
                           MstrExternalIfDataDefine.COLUMN_NAME_FORMAT_ID, formatId)});
        }
        // パラメータチェック
        if (Objects.isNull(itemNumber)) {
            // ME01.0050:「パラメータが不正です。({0})」
           throw super.exceptionFactory.createGnomesAppException(
                   null, GnomesMessagesConstants.ME01_0050,
                   new Object[]{super.createMessageParamsRequired(
                           MstrExternalIfDataDefine.COLUMN_NAME_DATA_ITEM_NUMBER, formatId)});

        }

        List<MstrExternalIfDataDefine> result = gnomesSystemModel.getMstrExternalIfDataDefineList().stream()
                .filter(item -> item.getFormat_id().equals(formatId) && item.getData_item_number() == itemNumber)
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            return null;
        }

        DataDefine dataDefine = new DataDefine();
        dataDefine.setData_item_number(itemNumber);

        // 外部I/Fデータ項目定義マスタの件数分繰り返す
        for (MstrExternalIfDataDefine mstrExternalIfDataDefine: result) {

            try {

                // 項目名からフィールドを探す。ない場合NoSuchFieldException
                Field fld = dataDefine.getClass().getDeclaredField(mstrExternalIfDataDefine.getDefinition_code());

                fld.setAccessible(true);
                if (Objects.isNull(mstrExternalIfDataDefine.getSet_value())) {
                    continue;
                } else if (fld.getType() == (Class<?>)int.class || fld.getType() == (Class<?>)Integer.class) {
                    fld.set(dataDefine, ConverterUtils.stringToNumber(
                            false, mstrExternalIfDataDefine.getSet_value()).intValue());
                } else if (fld.getType() == (Class<?>)Date.class) {
                    fld.set(dataDefine, ConverterUtils.stringToDate(mstrExternalIfDataDefine.getSet_value()));
                } else {
                    fld.set(dataDefine, mstrExternalIfDataDefine.getSet_value());
                }

            } catch (Exception e) {
                continue;
            }
        }

        return dataDefine;

    }

}
