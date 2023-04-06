package com.gnomes.system.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.gnomes.common.constants.CommonEnums.PrintoutStatus;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseLogic;
import com.gnomes.common.persistence.GnomesEntityManager;
import com.gnomes.system.dao.TmpQueuePrintoutDao;
import com.gnomes.system.dao.TmpQueuePrintoutParameterDao;
import com.gnomes.system.data.printout.ReportPrintOutRequestInfo;
import com.gnomes.system.entity.TmpQueuePrintout;
import com.gnomes.system.entity.TmpQueuePrintoutParameter;

/**
 * 帳票印字要求
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/07/17 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class ReportPrintOutRequest extends BaseLogic {

    /** エンティティマネージャー */
    @Inject
    protected GnomesEntityManager em;

    /**
     * 印字キューDao
     */
    @Inject
    protected TmpQueuePrintoutDao tmpQueuePrintoutDao;

    /**
     * 印字キューパラメータ
     */
    @Inject
    protected TmpQueuePrintoutParameterDao tmpQueuePrintoutParameterDao;

    /**
     * 帳票印字要求
     * <pre>
     * 帳票印字要求を行う。
     * </pre>
     * @param requestInfo 帳票印字要求情報
     */
    @TraceMonitor
    @ErrorHandling
    public void request(ReportPrintOutRequestInfo requestInfo) {


        // 印字キュー登録
    	TmpQueuePrintout tmpQueuePrintout = this.setTmpQueuePrintout(requestInfo);
        this.tmpQueuePrintoutDao.insert(tmpQueuePrintout, this.em.getEntityManager());
        // 印字キューパラメータ登録
        this.tmpQueuePrintoutParameterDao.insert(
        		this.setTmpQueuePrintoutParameter(tmpQueuePrintout, requestInfo), em.getEntityManager());

    }

    /**
     * 印字キュー登録情報編集
     * @param requestInfo 帳票印字要求情報
     * @return
     */
    private TmpQueuePrintout setTmpQueuePrintout(ReportPrintOutRequestInfo requestInfo) {

        TmpQueuePrintout tmpQueuePrintout = new TmpQueuePrintout();

        // 印字キューキー
        tmpQueuePrintout.setQueue_printout_key(UUID.randomUUID().toString());
        // 端末ID
        tmpQueuePrintout.setComputer_id(super.gnomesSessionBean.getComputerId());
        // 端末名
        tmpQueuePrintout.setComputer_name(super.gnomesSessionBean.getComputerName());
        // 帳票様式番号
        tmpQueuePrintout.setPrint_command_no(requestInfo.getPrintCommandNo());
        // 帳票印字日時
        tmpQueuePrintout.setPrintout_date(new Date());
        // 要求内連番
        tmpQueuePrintout.setRequest_seq(requestInfo.getRequestSeq());
        // 帳票種類
        tmpQueuePrintout.setPrinter_type(requestInfo.getPrinterType().getValue());
        // 印字枚数
        tmpQueuePrintout.setPrint_times(requestInfo.getPrintTimes());
        // 帳票名
        tmpQueuePrintout.setReport_name(requestInfo.getReportName());
        //DB領域区分
        tmpQueuePrintout.setDb_area_div(Integer.parseInt(gnomesSessionBean.getRegionType()));
        // プリンタID
        tmpQueuePrintout.setPrinter_id(requestInfo.getPrinterId());
        // プリンタ名
        tmpQueuePrintout.setPrinter_name(requestInfo.getPrinterName());
        // ユーザID
        tmpQueuePrintout.setUser_id(super.req.getUserId());
        // ユーザ名
        tmpQueuePrintout.setUser_name(super.req.getUserName());
        // 印字理由コード
        tmpQueuePrintout.setPrint_reason_code(requestInfo.getPrintReasonCode());
        // 印字理由名
        tmpQueuePrintout.setPrint_reason_name(requestInfo.getPrintReasonName());
        // 印字理由コメント
        tmpQueuePrintout.setPrint_reason_comment(requestInfo.getPrintReasonComment());
        // 再印刷マーク出力有無
        tmpQueuePrintout.setIs_re_print_mark(requestInfo.getIsRePrintMark().getValue());
        // 再印刷マーク出力位置
        tmpQueuePrintout.setPosition_re_print_mark(requestInfo.getPositionRePrintMark());
        // 電子ファイル作成区分
        tmpQueuePrintout.setIs_file_create_type(requestInfo.getIsFileCreateType().getValue());
        // PDF電子ファイル名
        tmpQueuePrintout.setPdf_file_name(requestInfo.getPdfFileName());
        // 帳票印字状態
        tmpQueuePrintout.setPrintout_status(PrintoutStatus.UNTREATED.getValue());

        return tmpQueuePrintout;

    }


    /**
     * 印字キューパラメータ編集
     * @param tmpQueuePrintout 印字キュー
     * @param requestInfo 帳票印字要求情報
     * @return 印字キューパラメータ
     */
    private List<TmpQueuePrintoutParameter> setTmpQueuePrintoutParameter(
    		TmpQueuePrintout tmpQueuePrintout, ReportPrintOutRequestInfo requestInfo) {
    	List<TmpQueuePrintoutParameter> result = new ArrayList<TmpQueuePrintoutParameter>();

    	if (requestInfo.getPrintParameterMap() != null) {
    		for (Map.Entry<String, String> param: requestInfo.getPrintParameterMap().entrySet()) {
    			TmpQueuePrintoutParameter tmpQueuePrintoutParameter = new TmpQueuePrintoutParameter();
    			// 印字キューデータキー
    			tmpQueuePrintoutParameter.setTmp_queue_data_printout_key(UUID.randomUUID().toString());
    			// nk印字キューキー (FK)
    			tmpQueuePrintoutParameter.setQueue_printout_key(tmpQueuePrintout.getQueue_printout_key());
    			// nkパラメータ名
    			tmpQueuePrintoutParameter.setParameter_name(param.getKey());
    			// パラメータ値
    			tmpQueuePrintoutParameter.setParameter_value(param.getValue());

    			result.add(tmpQueuePrintoutParameter);
    		}
    	}

    	return result;
    }
}
