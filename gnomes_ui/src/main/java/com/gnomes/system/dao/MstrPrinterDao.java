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
import com.gnomes.system.entity.MstrPrinter;

/**
 * プリンタマスタ Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/11/28 KCC/A.Oomori               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class MstrPrinterDao extends BaseDao implements Serializable {

    /**
     * コンストラクタ
     */
    public MstrPrinterDao() {
    }


    /**
     * プリンタマスタ 取得
     * @return List<MstrPrinter>
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrPrinter> getMstrPrinter() throws GnomesAppException {

        // プリンタマスタ
        return gnomesSystemModel.getMstrPrinterList();
    }

    /**
     * プリンタマスタ 取得
     * @param printerName
     * @return MstrPrinter
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public MstrPrinter getMstrPrinter(String printerName) throws GnomesAppException {

        if (StringUtil.isNullOrEmpty(printerName)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050, printerName);

        }

        List<MstrPrinter> result = gnomesSystemModel.getMstrPrinterList().stream()
                .filter(item -> item.getPrinter_name().equals(printerName))
                .collect(Collectors.toList());

        if(result.size() > 0){
            return result.get(0);
        }
        return null;
    }

    /**
     * プリンタマスタ 取得(プリンタID)
     * @param printerId
     * @return MstrPrinter
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public MstrPrinter getMstrPrinterByPrinterId(String printerId) throws GnomesAppException {

        if (StringUtil.isNullOrEmpty(printerId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050, printerId);

        }

        List<MstrPrinter> result = gnomesSystemModel.getMstrPrinterList().stream()
                .filter(item -> item.getPrinter_id().equals(printerId))
                .collect(Collectors.toList());

        if(result.size() > 0){
            return result.get(0);
        }
        return null;
    }

}
