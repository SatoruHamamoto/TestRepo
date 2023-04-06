package com.gnomes.external.data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonEnums.ExternalIfSendRecvClearFlag;
import com.gnomes.common.constants.CommonEnums.ExternalIfSendRecvRetryFlag;
import com.gnomes.common.constants.CommonEnums.SendRecvStateType;
import com.gnomes.common.constants.CommonEnums.SendRecvType;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.external.entity.ExtIfDataSrActual;
import com.gnomes.external.entity.ExtIfDataSrActualDetail;
import com.gnomes.external.entity.ExternalIfSendDataDetail;
import com.gnomes.external.entity.ExternalIfSendFileSeqNo;
import com.gnomes.external.entity.MstrExternalIfDataDefine;
import com.gnomes.external.entity.MstrExternalIfFileDefine;
import com.gnomes.external.entity.MstrExternalIfFormatDefine;
import com.gnomes.external.entity.MstrExternalIfSystemDefine;
import com.gnomes.external.entity.QueueExternalIfRecv;
import com.gnomes.external.entity.QueueExternalIfSendStatus;

/**
 * 送受信 トランスファークラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/09/25 YJP/T.Kamizuru            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */

@RequestScoped
public class FileTransferBean implements Serializable {

    @Inject
    protected transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected transient LogHelper logHelper;

    /**
     * 送受信データBeanList
     */
    private List<SendRecvDataBean> sendRecvDataBeanList;

    /**
     * 送受信データBeanクラス
     */
    private Class<?> beanClass;

    /**
     * バリデート対象項目リスト
     */
    private Map<Integer, DataDefine> validateCheckList;

    /**
     * 行データList
     */
    private List<String> lineDataList;

    /** 行ステータス */
    private Integer lineStatus;

    /**
     * エラーコメント
     */
    private String errorComment;

    /**
     * エラー発生箇所（Talend Job名）
     */
    private String errorJobName;

    /**
     * エラー発生箇所（Talend Component名）
     */
    private String errorComponentName;

    /**
     * ファイル種別
     */
    private String fileType;

    /** 要求内連番 */
    private int requestSeq;

    /**
     * 外部I/F対象システムコード
     */
    private String externalTargetCode;

    /**
     * ステータス
     */
    private SendRecvStateType status;

    /** 送受信区分 */
    private SendRecvType sendRecvType;

    /** 再処理フラグ */
    private ExternalIfSendRecvRetryFlag retryFlag;

    /** クリアフラグ */
    private ExternalIfSendRecvClearFlag clearFlag;

    /**
     * ファイル名称
     */
    private String dataTypeName;

    /**
     * 送受信ファイル名
     */
    private String sendRecvFileName;

    /**
     * 遷移元フォルダ名
     */
    private String moveFromFolderName;

    /**
     * 遷移先フォルダ名
     */
    private String moveToFolderName;

    /**
     * 移動ファイル存在チェックの有無
     */
    private String isFileCheck;

    /**
     * 受信ファイル文字コード
     */
    private String recvFileEncode;

    /**
     * 作成ファイル文字コード
     */
    private String createFileEncode;

    /**
     * プロトコル種別
     */
    private Integer procType;

    /**
     * エラ-行情報(エラー行No,エラーコメント）
     */
    private Map<Integer, String> errorLineInfo;

    /**
     * エラ-情報(エラー行No,エラー）
     */
    private Map<Integer, Exception> errorLineExceptionMap;

    /**
     * 受信ファイル情報
     */
    private RecvFileInfo recvFileInfo;

    /**
     * 受信ファイル変換データ
     */
    private List<Map<String, String>> recvConvData;

    /**
     * 送信ファイルマッピングデータ
     */
    private List<Map<String, String>> sendMappingData;

    /**
     * HULFT送信コマンド
     */
    private String hulftSendCommand;

    /**
     * 送受信結果
     */
    private boolean sendRecvResult;

    /**
     * 実行ジョブ名
     */
    private String jobName;

    /**
     * 外部I/F送信状態キュー
     */
    private QueueExternalIfSendStatus queueExternalIfSendStatus;

    /**
     * 外部I/F送信状態キューlist
     */
    private List<QueueExternalIfSendStatus> queueExternalIfSendStatusList;

    /**
     * 外部I/F送信データ詳細
     */
    private List<ExternalIfSendDataDetail> externalIfSendDataDetail;

    /**
     * 外部I/Fシステム定義マスタ
     */
    private MstrExternalIfSystemDefine mstrExternalIfSystemDefine;

    /**
     * 外部I/Fファイル構成定義マスタ
     */
    private MstrExternalIfFileDefine mstrExternalIfFileDefine;

    /**
     * 外部I/Fデータ項目定義マスタ
     */
    private List<MstrExternalIfDataDefine> mstrExternalIfDataDefine;

    /** 外部I/Fフォーマット定義マスタ */
    private MstrExternalIfFormatDefine mstrExternalIfFormatDefine;

    /**
     * 外部I/Fシステム定義マスタ
     */
    private SystemDefine systemDefine;

    /**
     * 外部I/Fファイル構成定義
     */
    private FileDefine fileDefine;

    /**
     * 外部I/Fデータ項目定義リスト
     */
    private List<DataDefine> dataDefine;

    /**
     * 外部I/F送信ファイル連番管理
     */
    private ExternalIfSendFileSeqNo externalIfSendFileSeqNo;

    /**
     * 外部I/Fデータファイル送受信実績
     */
    private ExtIfDataSrActual extIfDataSrActual;

    /**
     * 外部I/Fデータファイル送受信実績詳細
     */
    private List<ExtIfDataSrActualDetail> extIfDataSrActualDetail;

    /**
     * 外部I/F受信キュー
     */
    private QueueExternalIfRecv queueExternalIfRecv;

    /** 独自管理エンティティマネージャー */
    private EntityManager eml;

    /**
    * 外部I/F画面外処理成功フラグ
    */
   private boolean successFlag;
   /**
   * 外部I/F画面外処理スキップフラグ
   */
   private boolean skipFlag;
   /**
   * 外部I/F画面外処理キュー存在フラグ
   */
   private boolean quereExistFlag;

   /**
    * 改行コード
    */
   private String fileDefaultDelimiter;

   /**
    * 全キュー行数カウント
    */
   private int totalRecordSeqCnt = 0;

	public void clear() {
        queueExternalIfRecv = null;
    }

    /**
     * 送受信データBeanListを取得
     * @return 送受信データBeanList
     */
    public List<SendRecvDataBean> getSendRecvDataBeanList() {
        return sendRecvDataBeanList;
    }

    /**
     * 送受信データBeanListを設定
     * @param sendRecvDataBeanList 送受信データBeanList
     */
    public void setSendRecvDataBeanList(
            List<SendRecvDataBean> sendRecvDataBeanList) {
        this.sendRecvDataBeanList = sendRecvDataBeanList;
    }

    /**
     * バリデート対象項目リストを取得
     * @return バリデート対象項目リスト
     */
    public Map<Integer, DataDefine> getValidateCheckList() {
        return validateCheckList;
    }

    /**
     * バリデート対象項目リストを設定
     * @param validateCheckList バリデート対象項目リスト
     */
    public void setValidateCheckList(
            Map<Integer, DataDefine> validateCheckList) {
        this.validateCheckList = validateCheckList;
    }

    /**
     * 行ステータスを取得
     * @return 行ステータス
     */
    public Integer getLineStatus() {
        return lineStatus;
    }

    /**
     * 行ステータスを設定
     * @param lineStatus 行ステータス
     */
    public void setLineStatus(Integer lineStatus) {
        this.lineStatus = lineStatus;
    }

    /**
     * エラーコメントを取得
     * @return エラーコメント
     */
    public String getErrorComment() {
        return errorComment;
    }

    /**
     * エラーコメントを設定
     * @param errorComment エラーコメント
     */
    public void setErrorComment(String errorComment) {
        this.errorComment = errorComment;
    }

    /**
     * ファイル種別取得
     * @return ファイル種別
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * ファイル種別設定
     * @param fileType ファイル種別
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /**
     * 要求内連番を取得
     * @return 要求内連番
     */
    public int getRequestSeq() {
        return requestSeq;
    }

    /**
     * 要求内連番を設定
     * @param requestSeq 要求内連番
     */
    public void setRequestSeq(int requestSeq) {
        this.requestSeq = requestSeq;
    }

    /**
     * 外部I/F対象システムコード取得
     * @return 外部I/F対象システムコード
     */
    public String getExternalTargetCode() {
        return externalTargetCode;
    }

    /**
     * 外部I/F対象システムコード設定
     * @param dataType 外部I/F対象システムコード
     */
    public void setExternalTargetCode(String externalTargetCode) {
        this.externalTargetCode = externalTargetCode;
    }

    /**
     * ステータス取得
     * @return ステータス
     */
    public SendRecvStateType getStatus() {
        return status;
    }

    /**
     * ステータス設定
     * @param status ステータス
     */
    public void setStatus(SendRecvStateType status) {
        this.status = status;
    }

    /**
     * 送受信区分を取得
     * @return 送受信区分
     */
    public SendRecvType getSendRecvType() {
        return sendRecvType;
    }

    /**
     * 送受信区分を設定
     * @param sendRecvType 送受信区分
     */
    public void setSendRecvType(SendRecvType sendRecvType) {
        this.sendRecvType = sendRecvType;
    }

    /**
     * 再処理フラグを取得
     * @return 再処理フラグ
     */
    public ExternalIfSendRecvRetryFlag getRetryFlag() {
        return retryFlag;
    }

    /**
     * 再処理フラグを設定
     * @param retryFlag 再処理フラグ
     */
    public void setRetryFlag(ExternalIfSendRecvRetryFlag retryFlag) {
        this.retryFlag = retryFlag;
    }

    /**
     * クリアフラグを取得
     * @return クリアフラグ
     */
    public ExternalIfSendRecvClearFlag getClearFlag() {
        return clearFlag;
    }

    /**
     * クリアフラグを設定
     * @param clearFlag クリアフラグ
     */
    public void setClearFlag(ExternalIfSendRecvClearFlag clearFlag) {
        this.clearFlag = clearFlag;
    }

    /**
     * 送受信ファイル名を取得
     * @return 送受信ファイル名
     */
    public String getSendRecvFileName() {
        return sendRecvFileName;
    }

    /**
     * 送受信ファイル名を設定
     * @param sendRecvFileName 送受信ファイル名
     */
    public void setSendRecvFileName(String sendRecvFileName) {
        this.sendRecvFileName = sendRecvFileName;
    }

    /**
     * 遷移元フォルダ名を取得
     * @return 遷移元フォルダ名
     */
    public String getMoveFromFolderName() {
        return moveFromFolderName;
    }

    /**
     * 遷移元フォルダ名を設定
     * @param moveFromFolderName 遷移元フォルダ名
     */
    public void setMoveFromFolderName(String moveFromFolderName) {
        this.moveFromFolderName = moveFromFolderName;
    }

    /**
     * 遷移先フォルダ名を取得
     * @return 遷移先フォルダ名
     */
    public String getMoveToFolderName() {
        return moveToFolderName;
    }

    /**
     * 遷移先フォルダ名を設定
     * @param moveToFolderName 遷移先フォルダ名
     */
    public void setMoveToFolderName(String moveToFolderName) {
        this.moveToFolderName = moveToFolderName;
    }

    /**
     * 移動ファイル存在チェックの有無を取得
     * @return isFileCheck 移動ファイル存在チェックの有無
     */
    public String getIsFileCheck() {
        return isFileCheck;
    }

    /**
     * 移動ファイル存在チェックの有無を設定
     * @param isFileCheck 移動ファイル存在チェックの有無
     */
    public void setIsFileCheck(String isFileCheck) {
        this.isFileCheck = isFileCheck;
    }

    /**
     * 受信ファイルエンコードを取得
     *
     * @return 受信ファイルエンコード "MS932","UTF-8","Shift-JIS"
     */
    public String getRecvFileEncode() {
        return recvFileEncode;
    }

    /**
     * 受信ファイルエンコードを設定
     *
     * @param recvFileEncode 受信ファイルエンコード  "MS932","UTF-8","Shift-JIS"
     */
    public void setRecvFileEncode(String recvFileEncode) {
        this.recvFileEncode = recvFileEncode;
    }

    /**
     * 作成ファイル文字コードを取得
     * @return createFileEncode 作成ファイル文字コード
     */
    public String getCreateFileEncode() {
        return createFileEncode;
    }

    /**
     * 作成ファイル文字コードを設定
     * @param createFileEncode 作成ファイル文字コード
     */
    public void setCreateFileEncode(String createFileEncode) {
        this.createFileEncode = createFileEncode;
    }

    /**
     * プロトコル種別を取得
     * @return プロトコル種別
     */
    public Integer getProcType() {
        return procType;
    }

    /**
     * プロトコル種別を設定
     * @param procType プロトコル種別
     */
    public void setProcType(Integer procType) {
        this.procType = procType;
    }

    /**
     * エラ-行情報(エラー行No,エラーコメント）を取得
     * @return エラ-行情報(エラー行No,エラーコメント）
     */
    public Map<Integer, String> getErrorLineInfo() {
        return errorLineInfo;
    }

    /**
     * エラ-行情報(エラー行No,エラーコメント）を設定
     * @param errorLineInfo エラ-行情報(エラー行No,エラーコメント）
     */
    public void setErrorLineInfo(Map<Integer, String> errorLineInfo) {
        this.errorLineInfo = errorLineInfo;
    }

    /**
     * エラ-情報(エラー行No,エラー）を取得
     * @return エラ-行情報(エラー行No,エラーコメント）
     */
    public Map<Integer, Exception> getErrorLineExceptionMap() {
        return errorLineExceptionMap;
    }

    /**
     * エラ-情報(エラー行No,エラー）を設定
     * @param errorLineInfo エラ-行情報(エラー行No,エラーコメント）
     */
    public void setErrorLineExceptionMap(
            Map<Integer, Exception> errorLineExceptionMap) {
        this.errorLineExceptionMap = errorLineExceptionMap;
    }

    /**
     * エラー情報をクリアする
     */
    public void clearErrorInfo() {

        //１．ExceptionMapをクリアする
        if (!Objects.isNull(this.errorLineExceptionMap)) {
            this.errorLineExceptionMap.clear();
        }
        errorLineExceptionMap = null;

        //2.エラーコメントをクリアする
        this.errorComment = null;

        //3.エラーコンポーネント名を見る
        this.errorComment = null;

        //4.エラー行情報をクリアする
        if (!Objects.isNull(errorLineInfo)) {
            this.errorLineInfo.clear();
        }
        this.errorLineInfo = null;
    }

    /**
     * エラー情報が格納されているかどうかチェックする
     * @return
     */
    public boolean isFoundErrorInfo() {

        //１．ExceptionMapを見る
        //ExceptionMapがあったらロールバックされる
        if ((!Objects.isNull(this.errorLineExceptionMap)) && this.errorLineExceptionMap.size() > 0) {
            return true;
        }
        //2.エラーコメントを見る
        if (!StringUtil.isNullOrEmpty(this.errorComment)) {
            return true;
        }
        //3.エラーコンポーネント名を見る
        if (!StringUtil.isNullOrEmpty(this.errorComponentName)) {
            return true;
        }

        return false;
    }

    /**
     * 受信ファイル情報を取得
     * @return recvFileInfo
     */
    public RecvFileInfo getRecvFileInfo() {
        return recvFileInfo;
    }

    /**
     * 受信ファイル情報を設定
     * @param recvFileInfo 受信ファイル情報
     */
    public void setRecvFileInfo(RecvFileInfo recvFileInfo) {
        this.recvFileInfo = recvFileInfo;
    }

    /**
     * 受信ファイル変換データを取得
     * @return recvConvData
     */
    public List<Map<String, String>> getRecvConvData() {
        return recvConvData;
    }

    /**
     * 受信ファイル変換データを設定
     * @param recvConvData 受信ファイル変換データ
     */
    public void setRecvConvData(List<Map<String, String>> recvConvData) {
        this.recvConvData = recvConvData;
    }

    public List<Map<String, String>> getSendMappingData() {
        return sendMappingData;
    }

    public void setSendMappingData(List<Map<String, String>> sendMappingData) {
        this.sendMappingData = sendMappingData;
    }

    /**
     * HULFT送信コマンドを取得
     * @return HULFT送信コマンド
     */
    public String getHulftSendCommand() {
        return hulftSendCommand;
    }

    /**
     * HULFT送信コマンドを設定
     * @param hulftSendCommand HULFT送信コマンド
     */
    public void setHulftSendCommand(String hulftSendCommand) {
        this.hulftSendCommand = hulftSendCommand;
    }

    /**
     * 外部I/F送信状態キューを取得
     * @return queueExternalIfSendStatus
     */
    public QueueExternalIfSendStatus getQueueExternalIfSendStatus() {
        return queueExternalIfSendStatus;
    }

    /**
     * 外部I/F送信状態キューを設定
     * @param queueExternalIfSendStatus 外部I/F送信状態キュー
     */
    public void setQueueExternalIfSendStatus(
            QueueExternalIfSendStatus queueExternalIfSendStatus) {
        this.queueExternalIfSendStatus = queueExternalIfSendStatus;
    }

    /**
     * 外部I/F送信状態キューを取得
     * @return queueExternalIfSendStatusList
     */
    public List<QueueExternalIfSendStatus> getSendStateList() {
        return queueExternalIfSendStatusList;
    }

    /**
     * 外部I/F送信状態キューを設定
     * @param queueExternalIfSendStatus 外部I/F送信状態キュー
     */
    public void setSendStateList(
            List<QueueExternalIfSendStatus> sendStateList) {
        this.queueExternalIfSendStatusList = sendStateList;
    }

    /**
     * 外部I/F送信データ詳細を取得
     * @return externalIfSendDataDetail
     */
    public List<ExternalIfSendDataDetail> getExternalIfSendDataDetail() {
        return externalIfSendDataDetail;
    }

    /**
     * 外部I/F送信データ詳細を設定
     * @param externalIfSendDataDetail 外部I/F送信データ詳細
     */
    public void setExternalIfSendDataDetail(
            List<ExternalIfSendDataDetail> externalIfSendDataDetail) {
        this.externalIfSendDataDetail = externalIfSendDataDetail;
    }

    /**
     * 外部I/Fシステム定義マスタを取得
     * @return mstrExternalIfSystemDefine
     */
    public MstrExternalIfSystemDefine getMstrExternalIfSystemDefine() {
        return mstrExternalIfSystemDefine;
    }

    /**
     * 外部I/Fシステム定義マスタを設定
     * @param mstrExternalIfSystemDefine 外部I/Fシステム定義マスタ
     */
    public void setMstrExternalIfSystemDefine(
            MstrExternalIfSystemDefine mstrExternalIfSystemDefine) {
        this.mstrExternalIfSystemDefine = mstrExternalIfSystemDefine;
    }

    /**
     * 外部I/Fファイル構成定義マスタを取得
     * @return mstrExternalIfFileDefine
     */
    public MstrExternalIfFileDefine getMstrExternalIfFileDefine() {
        return mstrExternalIfFileDefine;
    }

    /**
     * 外部I/Fファイル構成定義マスタを設定
     * @param mstrExternalIfFileDefine 外部I/Fファイル構成定義マスタ
     */
    public void setMstrExternalIfFileDefine(
            MstrExternalIfFileDefine mstrExternalIfFileDefine) {
        this.mstrExternalIfFileDefine = mstrExternalIfFileDefine;
    }

    /**
     * 外部I/Fデータ項目定義マスタを取得
     * @return mstrExternalIfDataDefine
     */
    public List<MstrExternalIfDataDefine> getMstrExternalIfDataDefine() {
        return mstrExternalIfDataDefine;
    }

    /**
     * 外部I/Fデータ項目定義マスタを設定
     * @param mstrExternalIfDataDefine 外部I/Fデータ項目定義マスタ
     */
    public void setMstrExternalIfDataDefine(
            List<MstrExternalIfDataDefine> mstrExternalIfDataDefine) {
        this.mstrExternalIfDataDefine = mstrExternalIfDataDefine;
    }

    /**
     * 外部I/Fフォーマット定義マスタを取得
     * @return 外部I/Fフォーマット定義マスタ
     */
    public MstrExternalIfFormatDefine getMstrExternalIfFormatDefine() {
        return mstrExternalIfFormatDefine;
    }

    /**
     * 外部I/Fフォーマット定義マスタを設定
     * @param mstrExternalIfFormatDefine 外部I/Fフォーマット定義マスタ
     */
    public void setMstrExternalIfFormatDefine(
            MstrExternalIfFormatDefine mstrExternalIfFormatDefine) {
        this.mstrExternalIfFormatDefine = mstrExternalIfFormatDefine;
    }

    /**
     * 外部I/F送信ファイル連番管理を取得
     * @return externalIfSendFileSeqNo
     */
    public ExternalIfSendFileSeqNo getExternalIfSendFileSeqNo() {
        return externalIfSendFileSeqNo;
    }

    /**
     * 外部I/F送信ファイル連番管理を設定
     * @param externalIfSendFileSeqNo 外部I/F送信ファイル連番管理
     */
    public void setExternalIfSendFileSeqNo(
            ExternalIfSendFileSeqNo externalIfSendFileSeqNo) {
        this.externalIfSendFileSeqNo = externalIfSendFileSeqNo;
    }

    /**
     * 外部I/Fデータファイル送受信実績を取得
     * @return extIfDataSrActual
     */
    public ExtIfDataSrActual getExtIfDataSrActual() {
        return extIfDataSrActual;
    }

    /**
     * 外部I/Fデータファイル送受信実績を設定
     * @param extIfDataSrActual 外部I/Fデータファイル送受信実績
     */
    public void setExtIfDataSrActual(ExtIfDataSrActual extIfDataSrActual) {
        this.extIfDataSrActual = extIfDataSrActual;
    }

    /**
     * 外部I/Fデータファイル送受信実績詳細を取得
     * @return extIfDataSrActualDetail
     */
    public List<ExtIfDataSrActualDetail> getExtIfDataSrActualDetail() {
        return extIfDataSrActualDetail;
    }

    /**
     * 外部I/Fデータファイル送受信実績詳細を設定
     * @param extIfDataSrActualDetail 外部I/Fデータファイル送受信実績詳細
     */
    public void setExtIfDataSrActualDetail(
            List<ExtIfDataSrActualDetail> extIfDataSrActualDetail) {
        this.extIfDataSrActualDetail = extIfDataSrActualDetail;
    }

    /**
     * 外部I/F受信キューを取得
     * @return queueExternalIfRecv
     */
    public QueueExternalIfRecv getQueueExternalIfRecv() {
        return queueExternalIfRecv;
    }

    /**
     * 外部I/F受信キューを設定
     * @param queueExternalIfRecv 外部I/F受信キュー
     */
    public void setQueueExternalIfRecv(
            QueueExternalIfRecv queueExternalIfRecv) {
        this.queueExternalIfRecv = queueExternalIfRecv;
    }

    /**
     * 独自管理エンティティマネージャーを取得
     * @return 独自管理エンティティマネージャー
     */
    public EntityManager getEml() {
        return eml;
    }

    /**
     * 独自管理エンティティマネージャーを設定
     * @param eml 独自管理エンティティマネージャー
     */
    public void setEml(EntityManager eml) {
        this.eml = eml;
    }

    /**
     * 初期処理
     */
    @PostConstruct
    private void init() {
        this.logHelper.fine(this.logger, null, null, "@PostConstruct: " + this);
    }

    /**
     * 後処理
     */
    @PreDestroy
    private void destroy() {
        this.logHelper.fine(this.logger, null, null, "@PreDestroy: " + this);
    }

    /**
     * 行データListを取得
     * @return 行データList
     */
    public List<String> getLineDataList() {
        return lineDataList;
    }

    /**
     * 行データListを設定
     * @param lineDataList 行データList
     */
    public void setLineDataList(List<String> lineDataList) {
        this.lineDataList = lineDataList;
    }

    /**
     * 外部I/Fシステム定義を取得
     * @return systemDefine 外部I/Fシステム定義
     */
    public SystemDefine getSystemDefine() {
        return systemDefine;
    }

    /**
     * 外部I/Fシステム定義を設定
     * @param systemDefine 外部I/Fシステム定義
     */
    public void setSystemDefine(SystemDefine systemDefine) {
        this.systemDefine = systemDefine;
    }

    /**
     * 外部I/Fファイル構成定義を取得
     * @return 外部I/Fファイル構成定義
     */
    public FileDefine getFileDefine() {
        return fileDefine;
    }

    /**
     * 外部I/Fファイル構成定義を設定
     * @param fileDefine 外部I/Fファイル構成定義
     */
    public void setFileDefine(FileDefine fileDefine) {
        this.fileDefine = fileDefine;
    }

    /**
     * 外部I/Fデータ項目定義リストを取得
     * @return 外部I/Fデータ項目定義リスト
     */
    public List<DataDefine> getDataDefine() {
        return dataDefine;
    }

    /**
     * 外部I/Fデータ項目定義リストを設定
     * @param dataDefine 外部I/Fデータ項目定義リスト
     */
    public void setDataDefine(List<DataDefine> dataDefine) {
        this.dataDefine = dataDefine;
    }

    /**
     * @return errorJobName
     */
    public String getErrorJobName() {
        return errorJobName;
    }

    /**
     * @param errorJobName セットする errorJobName
     */
    public void setErrorJobName(String errorJobName) {
        this.errorJobName = errorJobName;
    }

    /**
     * @return errorComponentName
     */
    public String getErrorComponentName() {
        return errorComponentName;
    }

    /**
     * @param errorComponentName セットする errorComponentName
     */
    public void setErrorComponentName(String errorComponentName) {
        this.errorComponentName = errorComponentName;
    }

    /**
     * 送受信結果を取得
     * @return 送受信結果
     */
    public boolean isSendRecvResult() {
        return sendRecvResult;
    }

    /**
     * 送受信結果を設定
     * @param sendRecvResult 送受信結果
     */
    public void setSendRecvResult(boolean sendRecvResult) {
        this.sendRecvResult = sendRecvResult;
    }

    /**
     * @return jobName
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * @param jobName セットする jobName
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * ファイル名称を取得
     * @return ファイル名称
     */
    public String getDataTypeName() {
        return dataTypeName;
    }

    /**
     * ファイル名称を設定
     * @param dataTypeName ファイル名称
     */
    public void setDataTypeName(String dataTypeName) {
        this.dataTypeName = dataTypeName;
    }

    /**
     * 送受信データBeanクラスを取得
     * @return 送受信データBeanクラス
     */
    public Class<?> getBeanClass() {
        return beanClass;
    }

    /**
     * 送受信データBeanクラスを設定
     * @param beanClass セットする beanClass
     */
    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    /**
     * 外部I/F画面外処理成功フラグを取得
     * @return 外部I/F画面外処理成功フラグ
     */
    public boolean isSuccessFlag() {
		return successFlag;
	}

    /**
     * 外部I/F画面外処理成功フラグを設定
     * @param successFlag 外部I/F画面外処理成功フラグ
     */
	public void setSuccessFlag(boolean successFlag) {
		this.successFlag = successFlag;
	}

	/**
     * 外部I/F画面外処理スキップフラグを取得
     * @return 外部I/F画面外処理スキップフラグ
     */
    public boolean isSkipFlag() {
		return skipFlag;
	}

    /**
     * 外部I/F画面外処理スキップフラグを設定
     * @param successFlag 外部I/F画面外処理スキップフラグ
     */
	public void setSkipFlag(boolean skipFlag) {
		this.skipFlag = skipFlag;
	}

	/**
	 * 外部I/F画面外処理キュー存在フラグを取得
	 * @return 外部I/F画面外処理キュー存在フラグ
	 */
	public boolean isQuereExistFlag() {
		return quereExistFlag;
	}

	/**
	 * 外部I/F画面外処理キュー存在フラグを設定
	 * @param quereExistFlag 外部I/F画面外処理キュー存在フラグ
	 */
	public void setQuereExistFlag(boolean quereExistFlag) {
		this.quereExistFlag = quereExistFlag;
	}

    /**
     * 改行コードを取得
     * @return 改行コード
     */
    public String getFileDefaultDelimiter() {
        return fileDefaultDelimiter;
    }

    /**
     * 改行コードを設定
     * @param fileDefaultDelimiter 改行コード
     */
    public void setFileDefaultDelimiter(String fileDefaultDelimiter) {
        this.fileDefaultDelimiter = fileDefaultDelimiter;
    }

    /**
     * 全キュー行数カウントを取得
     * @return 全キュー行数カウント
     */
    public int getTotalRecordSeqCnt() {
        return totalRecordSeqCnt;
    }

    /**
     * 全キュー行数カウントを設定
     * @param totalRecordSeqCnt 全キュー行数カウント
     */
    public void setTotalRecordSeqCnt(int totalRecordSeqCnt) {
        this.totalRecordSeqCnt = totalRecordSeqCnt;
    }

    /**
     * 全キュー行数カウントをカウントアップ
     * @return 全キュー行数カウント
     */
    public int countTotalRecordSeqCnt() {
        return ++totalRecordSeqCnt;
    }

}
