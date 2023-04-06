package com.gnomes.external.logic.talend;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonEnums.SendRecvStateType;
import com.gnomes.common.constants.CommonEnums.State;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.SessionContextOperation;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseJobLogic;
import com.gnomes.common.resource.GnomesLogMessageConstants;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.external.dao.MstrExternalIfFileDefineDao;
import com.gnomes.external.data.SendRecvDataBean;

/**
 * 受信データ処理呼出
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/14 KCC/I.Shibasaka           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
@SessionContextOperation
public class RecvFileCallJob extends BaseJobLogic {

    /** 外部I/Fファイル構成定義マスタ Dao */
    @Inject
    protected MstrExternalIfFileDefineDao mstrExternalIfFileDefineDao;

    /**
     * 受信データ処理呼出
     * @throws Exception
     */
    @ErrorHandling
    @TraceMonitor
    public void process() throws Exception {

        Map<Integer, String> errorInfo = new HashMap<Integer, String>();
        State ret = State.Success;

        ret = this.workerLogic(
                super.fileTransferBean.getSendRecvDataBeanList(),
                super.fileTransferBean.getSendRecvFileName());

        if (State.Error.equals(ret)) {

            super.fileTransferBean.setSendRecvResult(false);
            super.fileTransferBean.setStatus(SendRecvStateType.NG);


            //受信データ処理でエラーがあった場合メッセージ作成
            if(super.fileTransferBean.getErrorLineExceptionMap() != null
            		&& super.fileTransferBean.getErrorLineExceptionMap().size() != 0){
            	for(Map.Entry<Integer, Exception> entry : super.fileTransferBean.getErrorLineExceptionMap().entrySet()){
            		String message;
            		Exception e = entry.getValue();
            		//業務エラーの場合はメッセージを設定
            		if(e.getClass().equals(GnomesException.class)){
            			message = MessagesHandler.getString(((GnomesException)e).getMessageNo(), ((GnomesException)e).getMessageParams());
            		} else if(e.getClass().equals(GnomesAppException.class)) {
            			GnomesException ex = new GnomesException((GnomesAppException)e);
            			message = MessagesHandler.getString(ex.getMessageNo(), ex.getMessageParams());
            		} else {
            			message = e.toString();
            		}
            		errorInfo.put(entry.getKey(), message);
            	}
            } else {
                //MessagesHandler.setMessageNo(this.req , GnomesMessagesConstants.ME01_0149);
                errorInfo.put(1, MessagesHandler.getString(GnomesLogMessageConstants.ME01_0149));
            }

            super.fileTransferBean.setErrorLineInfo(errorInfo);

            throw exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0149);

        } else {
            super.fileTransferBean.setSendRecvResult(true);

        }

    }

    /**
     *  業務処理呼出
     *  各伝文毎のコールクラスを呼出（各業務テーブルへのデータ登録処理など）
     *  正式の呼び出し先がまだのため、仮実装
     */
    @ErrorHandling
    @TraceMonitor
    public State workerLogic(List<SendRecvDataBean> list, String filename) {

        State iRet = State.Error;

        String callClassName = fileTransferBean.getFileDefine().getCall_class_name();

        if(StringUtil.isNullOrEmpty(callClassName)){
            //クラスが定義されていないことをログに指定する
            String message = "*** Tarend JOB Business Logic Call Error *** callClassName is Empty of table[mstr_external_if_file_define] fileType=" +
                    fileTransferBean.getFileDefine().getFile_type() + " fileName =" + filename;

            this.logHelper.severe(this.logger, "RecvFileCallJob", "workerLogic", message);

            iRet = State.Error;
            return iRet;
        }

        Class<?> callClass;
        try {
            Object result;
            // コールクラスをコール
            CDI<Object> current = CDI.current();
            callClass = Class.forName(callClassName);
            Instance<?> instance = current.select(callClass);
            Object obj = instance.get();

            Method method = obj.getClass().getMethod("doReceive", List.class, String.class);

            //メソッド名をContainerRequestにセット
            req.setBusinessClassName(callClass.getSimpleName());

            result = method.invoke(obj, list, filename);
            // 正常終了:Success/異常終了:Error
            iRet = ((boolean)result == true) ? State.Success : State.Error;
        } catch (Exception e) {
            iRet = State.Error;
        }

        return iRet;
    }


}
