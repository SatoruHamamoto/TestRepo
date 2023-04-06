package com.gnomes.external.logic.talend;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.gnomes.common.constants.CommonEnums.SendRecvType;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.logic.BaseJobLogic;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.external.dao.MstrExternalIfDataDefineDao;
import com.gnomes.external.dao.MstrExternalIfFileDefineDao;
import com.gnomes.external.dao.MstrExternalIfFormatDefineDao;
import com.gnomes.external.dao.MstrExternalIfSystemDefineDao;
import com.gnomes.external.data.DataDefine;
import com.gnomes.external.data.FileDefine;
import com.gnomes.external.data.FileTransferBean;
import com.gnomes.external.data.SystemDefine;
import com.gnomes.external.entity.MstrExternalIfFormatDefine;

/**
 * 外部連携マスタ取得
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/14 KCC/A.Oomori               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@RequestScoped
public class GetExternalIfMstrJob extends BaseJobLogic {

    /** 外部I/Fシステム定義マスタ Dao */
    @Inject
    protected MstrExternalIfSystemDefineDao mstrExternalIfSystemDefineDao;

    /** 外部I/Fファイル構成定義マスタ Dao */
    @Inject
    protected MstrExternalIfFileDefineDao mstrExternalIfFileDefineDao;

    /** 外部I/Fデータ項目定義マスタ Dao */
    @Inject
    protected MstrExternalIfDataDefineDao mstrExternalIfDataDefineDao;

    /** 外部I/Fフォーマット定義マスタ Dao */
    @Inject
    protected MstrExternalIfFormatDefineDao mstrExternalIfFormatDefineDao;

    /**
     * 外部連携マスタ取得
     * @throws Exception
     */
    @ErrorHandling
    @TraceMonitor
    public void process() throws GnomesAppException {

        // 外部I/Fファイル構成定義取得
        this.getFileDefine(super.fileTransferBean.getFileType());
        // 外部I/Fフォーマット定義取得
        this.getFormatDefine(super.fileTransferBean.getFileType());
        // 外部I/Fデータ項目定義取得
        this.getDataDefineList();
        // 外部I/Fシステム定義取得
        this.getSystemDefine();

    }

    /**
     * 外部連携マスタ取得.
     * <pre>
     * 外部IF連携マスタを取得します。
     * </pre>
     * @param fileType ファイル種別
     * @return 送受信トランスファー情報
     * @throws GnomesAppException
     */
    @ErrorHandling
    @TraceMonitor
    public FileTransferBean process(String fileType) throws GnomesAppException {

        // ファイル種別設定
        super.fileTransferBean.setFileType(fileType);
        // 外部連携マスタ取得
        this.process();

        return super.fileTransferBean;

    }

    /**
     * 外部I/Fファイル構成定義情報取得.
     * @param fileType ファイル種別
     * @throws GnomesAppException
     */
    private void getFileDefine(String fileType) throws GnomesAppException {

        FileDefine fileDefine = this.mstrExternalIfFileDefineDao.getFileDefine(fileType);

        if (Objects.isNull(fileDefine)) {

            // ME01.0107:「X102:上位I/Fファイル構成定義に対象のデータが存在しません。ファイル種別:{0}、送受信区分:{1} 」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0107, fileType, SendRecvType.Send.name());

        }

        super.fileTransferBean.setFileDefine(fileDefine);

    }

    /**
     * 外部I/Fフォーマット定義情報取得
     * @param fileType ファイル種別
     * @throws GnomesAppException
     */
    private void getFormatDefine(String fileType) throws GnomesAppException {

        // 外部I/FフォーマットID
        String externalIfFormatId = super.fileTransferBean.getFileDefine().getExternal_if_format_id();

        MstrExternalIfFormatDefine formatDefine =
                this.mstrExternalIfFormatDefineDao.getMstrExternalIfFormatDefine(externalIfFormatId);

        if (Objects.isNull(formatDefine)) {

            // ME01.0160:「上位I/Fフォーマット定義に対象のデータが存在しません。ファイル種別:{0}、送受信区分:{1}、フォーマットID:{2} 」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0160,
                    fileType, SendRecvType.Send.name(), externalIfFormatId);

        }

        super.fileTransferBean.setMstrExternalIfFormatDefine(formatDefine);

    }

    /**
     * 外部I/Fデータ項目定義情報取得
     * @throws GnomesAppException
     */
    private void getDataDefineList() throws GnomesAppException {

        // 外部I/FフォーマットID
        String externalIfFormatId = super.fileTransferBean.getFileDefine().getExternal_if_format_id();

        List<DataDefine> dataDefineList =
                this.mstrExternalIfDataDefineDao.getDataDefineList(externalIfFormatId);

        if (dataDefineList.isEmpty()) {
            // ME01.0108:「X103:上位I/Fデータ項目定義に対象のデータが存在しません。ファイル種別:{0}、送受信区分{1}、フォーマットID:{2} 」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0108,
                    super.fileTransferBean.getFileType(), SendRecvType.Send.name(), externalIfFormatId);

        }

        // ソート
        Collections.sort(dataDefineList, Comparator.comparing(DataDefine::getData_item_number));
        super.fileTransferBean.setDataDefine(dataDefineList);

    }

    /**
     * 外部I/Fシステム定義情報取得
     * @throws GnomesAppException
     */
    private void getSystemDefine() throws GnomesAppException {

        // 外部I/F対象システムコード
        String externalIfTargetCode = super.fileTransferBean.getFileDefine().getExt_target_code();

        SystemDefine systemDefine = mstrExternalIfSystemDefineDao.getSystemDefine(externalIfTargetCode);

        if (Objects.isNull(systemDefine)) {
            // ME01.0109:「X101:上位I/F受信ファイル構成定義に対象のデータが存在しません。外部I/F対象システムコード:{0} 」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0109, externalIfTargetCode);

        }

        super.fileTransferBean.setSystemDefine(systemDefine);

    }

}
