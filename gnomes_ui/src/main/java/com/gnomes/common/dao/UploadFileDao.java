package com.gnomes.common.dao;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.GnomesQueryConstants;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.persistence.GnomesEntityManager;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.system.entity.UploadFile;

@Dependent
public class UploadFileDao extends BaseDao  implements Serializable {

	@Inject
	protected GnomesEntityManager em;

    @TraceMonitor
    @ErrorHandling
    public void insert(UploadFile item) {
        if(item != null){

            //A109:アップロードファイル管理
            em.getEntityManager().persist(item);
            em.getEntityManager().flush();

        }

    }

    @TraceMonitor
    @ErrorHandling
    public void detele(UploadFile item) {
        if(item != null){

            //削除
            em.getEntityManager().remove(item);
            em.getEntityManager().flush();

        }

    }

    @TraceMonitor
    @ErrorHandling
    public List<UploadFile> getUploadFileWithFileName(String folderName, String fileName) throws GnomesAppException {

        if(StringUtil.isNullOrEmpty(folderName)){
            // ME02.0009:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0050,String.valueOf(folderName));
            throw ex;
        }
        if(StringUtil.isNullOrEmpty(fileName)){
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0050,String.valueOf(fileName));
            throw ex;

        }

        // アップロードファイル管理テーブルより取得
        TypedQuery<UploadFile> query = this.em.getEntityManager().createNamedQuery(GnomesQueryConstants.QUERY_NAME_BLFILEMANAGET_UPLOAD_FILE, UploadFile.class);
        // フォルダー名
        query.setParameter(UploadFile.COLUMN_NAME_FOLDER_NAME, folderName);
        // 実ファイル名
        query.setParameter(UploadFile.COLUMN_NAME_FILE_NAME, fileName);

        query.setLockMode(LockModeType.PESSIMISTIC_WRITE);

        return query.getResultList();
    }

    @TraceMonitor
    @ErrorHandling
    public List<UploadFile> getUploadFileWithSysFileName(String folderName, String sysFileName) throws GnomesAppException {


        if(StringUtil.isNullOrEmpty(folderName)){
            // ME02.0009:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0050,String.valueOf(folderName));
            throw ex;


        }
        if(StringUtil.isNullOrEmpty(sysFileName)){
            // ME02.0009:「パラメータが不正です。({0})」
            GnomesAppException ex = exceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0050,String.valueOf(sysFileName));
            throw ex;

        }

        TypedQuery<UploadFile> query = this.em.getEntityManager().createNamedQuery(GnomesQueryConstants.QUERY_NAME_BLFILEMANAGET_UPLOAD_FILE_SYS, UploadFile.class);
        // フォルダー名
        query.setParameter(UploadFile.COLUMN_NAME_FOLDER_NAME, folderName);
        // システムファイル名
        query.setParameter(UploadFile.COLUMN_NAME_SYSTEM_FILE_NAME, sysFileName);

        return query.getResultList();
    }

}
