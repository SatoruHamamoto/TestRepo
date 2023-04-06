package com.gnomes.common.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Objects;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.picketbox.datasource.security.SecureIdentityLoginModule;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesMessagesConstants;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2020/02/04 13:12 YJP/S.Hamamoto           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
*/
/**
 * Winodowsファイルシステムをアクセスするユーティリティクラス
 * 特にUNCパスを対応する
 * @author 03501213
 *
 */
/**
 * @author 03501213
 *
 */
public class WindowsFileUtils {

    /** ロガー */
    @Inject
    protected transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected transient LogHelper logHelper;

    /** ユーザID */
    protected String userName;

    /** パスワード */
    protected String encryptedPassword;

    private static final int BUF_SIZE = 4096;

    private static final String className = "WindowsFileUtils";

//    /** セキュリティ */
//    private SecureIdentityLoginModule securityModule;

    @Inject
    protected GnomesExceptionFactory exceptionFactory;

    /**
     * @return userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName セットする userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return password
     */
    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    /**
     * @param 暗号化されたpassword セットする encryptedPassword
     */
    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    /**
     * コンストラクタ
     *
     * @param userName ユーザID（domain\\username）または".\\username"
     * @param encryptedPassword 暗号化されたパスワード。復号の仕方は教えない
     */
    public WindowsFileUtils(String userName, String encryptedPassword) {
        this.userName = userName;
        this.encryptedPassword = encryptedPassword;
    }

    /**
     * 通常のファイルシステムもしくは CIFS(Windowsファイル共有サービス) にファイルを書き込む
     *
     * @param is 書き込み内容
     * @param file 書き込み対象ファイル
     * @param isCifs CIFS か否か
     * @return
     * @throws GnomesAppException
     */
    public FileSystemUtilInfo writeToFile(InputStream is, File file, boolean isCifs) throws GnomesAppException {

        if (isCifs) {
            return writeToCifsFile(is, file);
        } else {
            return writeToLocalFile(is, file);
        }
    }

    /**
     *
     * ファイルシステムもしくは CIFS(Windowsファイル共有サービス) からファイルを読み込む
     *
     * @param file
     * @param isCifs
     * @param user
     * @param encryptedPassword
     * @return
     * @throws GnomesAppException
     */
    @ErrorHandling
    public byte[] readFromFileToByte(String filePath) throws GnomesAppException {
        final String methodName ="readFromFileToByte";
        try {
            java.io.File targrtFile = new File(filePath);

            if (filePath.startsWith("\\\\")) {

                return readFromCifsFile(targrtFile);
            } else {
                return readFromLocalFile(targrtFile);
            }
        } catch (Exception ex) {
            this.logHelper.severe(this.logger, className, methodName, filePath + " is exception", ex);
            throw exceptionFactory.createGnomesAppException(filePath + " is exception", ex);
        }
    }
    /**
     *
     * ファイルの存在チェック(UNCパス対応）
     *
     * @param filePath チェックするローカルファイルのパス
     * @param user
     * @param encryptedPassword
     * @return
     * @throws GnomesAppException
     */
    @ErrorHandling
    public boolean checkExistFilePath(String filePath) throws GnomesAppException {
        final String methodName ="checkExistFilePath";
        try {
            java.io.File targetFile = new File(filePath);

            if (filePath.startsWith("\\\\")) {

                return checkExistCifsFilePath(targetFile);
            } else {
                return targetFile.exists();
            }
        } catch (Exception ex) {
            this.logHelper.severe(this.logger, className, methodName, filePath + " is exception", ex);
            throw exceptionFactory.createGnomesAppException(filePath + " is exception", ex);
        }
    }

    /**
     * UNC形式ファイルの存在チェック
     * @param targetFile
     * @return
     * @throws GnomesAppException
     */
    private boolean checkExistCifsFilePath(File targetFile) throws GnomesAppException {
        final String methodName ="checkExistCifsFilePath";
        SmbFile file = null;
        try {
            file = new SmbFile(
                    cifsUrl(this.userName, cisfPassword(encryptedPassword), cisfPath(targetFile.getAbsolutePath())));
            return file.exists();
        } catch (MalformedURLException e) {
            this.logHelper.severe(this.logger, className, methodName, targetFile.getPath() + " is exception", e);
            throw exceptionFactory.createGnomesAppException(targetFile.getPath() + " is exception", e);
        } catch (SmbException e) {
            this.logHelper.severe(this.logger, className, methodName, targetFile.getPath() + " is exception", e);
            throw exceptionFactory.createGnomesAppException(targetFile.getPath() + " is exception", e);
        }
    }

    /**
     * java.io.File から、SmbFile オブジェクトを生成する
     *
     * @param targetFile
     * @param user
     * @param encordedPassword
     * @return
     */
    /**
     * @param targetFile
     * @return
     * @throws GnomesAppException
     */
    public SmbFile createSmbFileFromFile(File targetFile) throws GnomesAppException {
        final String methodName ="createSmbFileFromFile";
        SmbFile file = null;
        try {
            file = new SmbFile(cifsUrl(this.userName, cisfPassword(this.encryptedPassword),
                    cisfPath(targetFile.getAbsolutePath())));
        } catch (MalformedURLException e) {
            this.logHelper.severe(this.logger, className, methodName, targetFile.getPath() + " is exception", e);
            throw exceptionFactory.createGnomesAppException(targetFile.getPath() + " is exception", e);
        }

        return file;
    }

    /**
     * 通常のファイルシステムにファイルを作成
     *
     * @param is
     * @param targetFile
     * @return
     */
    private FileSystemUtilInfo writeToLocalFile(InputStream is, File targetFile) {

        FileSystemUtilInfo info = new FileSystemUtilInfo();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(targetFile);
            BufferedInputStream bis = new BufferedInputStream(is);

            int len = 0;
            long fileSize = 0;
            byte[] buffer = new byte[BUF_SIZE];
            while ((len = bis.read(buffer)) >= 0) {
                fos.write(buffer, 0, len);
                fileSize += len;
            }
            info.setFileSize(fileSize);

            bis.close();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            try {
                if (Objects.nonNull(fos)) {
                    fos.close();
                }
            } catch (Exception e2) {
            }
        }
        return info;
    }

    /**
     * 通常のファイルシステムにファイルをリード
     *
     * @param is
     * @param targetFile
     * @return
     * @throws IOException
     * @throws GnomesAppException
     */
    @ErrorHandling
    private byte[] readFromLocalFile(File targetFile) throws IOException, GnomesAppException {
        final String methodName ="readFromLocalFile";
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(targetFile);
            return (IOUtils.toByteArray(fis));
        } catch (Exception e) {
            this.logHelper.severe(this.logger, className, methodName, targetFile.getPath() + " is exception", e);
            throw exceptionFactory.createGnomesAppException(targetFile.getPath() + " is exception", e);
        } finally {
            if (Objects.nonNull(fis)) {
                fis.close();
            }
        }
    }
    /**
     * CIFS ファイルシステムにファイルを作成
     *
     * @param is
     * @param targetFile
     * @return
     * @throws GnomesAppException
     * @see http://jcifs.samba.org/src/docs/api/jcifs/smb/SmbFile.html
     */
    private FileSystemUtilInfo writeToCifsFile(InputStream is, File targetFile) throws GnomesAppException {

        final String methodName ="FileSystemUtilInfo";
        FileSystemUtilInfo info = new FileSystemUtilInfo();
        SmbFileOutputStream fos = null;
        BufferedInputStream bis = null;

        try {
            SmbFile file = new SmbFile(cifsUrl(this.userName, cisfPassword(this.encryptedPassword),
                    cisfPath(targetFile.getAbsolutePath())));

            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new SmbFileOutputStream(file);
            bis = new BufferedInputStream(is);

            int len = 0;
            long fileSize = 0;
            byte[] buffer = new byte[BUF_SIZE];
            while ((len = bis.read(buffer)) >= 0) {
                fos.write(buffer, 0, len);
                fileSize += len;
            }
            info.setFileSize(fileSize);

            bis.close();

        } catch (Exception e) {
            this.logHelper.severe(this.logger, className, methodName, targetFile.getPath() + " is exception", e);
            throw exceptionFactory.createGnomesAppException(targetFile.getPath() + " is exception", e);
        } finally {
            try {
                if (Objects.nonNull(bis)) {
                    bis.close();
                }
                if (Objects.nonNull(fos)) {
                    fos.close();
                }
            } catch (IOException e) {
                this.logHelper.severe(this.logger, className, methodName, targetFile.getPath() + " is exception", e);
            }
        }

        return info;
    }

    /**
     * CIFS ファイルシステムからファイルを読み込む
     *
     * @param targetFile
     * @param user
     * @param encryptedPassword
     * @return
     * @throws IOException
     */
    @ErrorHandling
    private byte[] readFromCifsFile(File targetFile) throws IOException {

        final String methodName = "readFromCifsFile";
        SmbFileInputStream fis = null;

        try {
            makeCifsDirectories(targetFile.getParentFile());
            SmbFile file = new SmbFile(cifsUrl(this.userName, cisfPassword(this.encryptedPassword),
                    cisfPath(targetFile.getAbsolutePath())));

            if (!file.exists()) {
                file.createNewFile();
            }
            fis = new SmbFileInputStream(file);

            return (IOUtils.toByteArray(fis));
        } catch (Exception e) {
            this.logHelper.severe(this.logger, className, methodName, targetFile.getPath() + " is exception", e);
        } finally {
            if (!Objects.isNull(fis)) {
                fis.close();
            }
        }

        return null;
    }

    /**
     * CIFS ディレクトリが存在しない場合、作成する
     *
     * @param dir
     * @param user
     * @param encryptedPassword
     * @throws MalformedURLException
     * @throws SmbException
     */
    private void makeCifsDirectories(File dir) throws MalformedURLException, SmbException {

        SmbFile cifsDir = new SmbFile(
                cifsUrl(this.userName, cisfPassword(this.encryptedPassword), cisfPath(dir.getAbsolutePath())));

        if (!cifsDir.exists()) {
            cifsDir.mkdirs();
        }
    }

    /**
     * CIFS アクセス URL
     * @param user
     * @param password
     * @param path
     * @return
     */
    private String cifsUrl(String user, String password, String path) {
        return String.format("smb://%s:%s@%s", user, password, path);
    }
    /**
     * '@' is URL encoded with the '%40' hexcode escape
     *
     * @param password
     * @return
     */
    private static String cisfPassword(String password) {
        return password.replaceAll("@", "%40");
    }

    /**
     * "\\" で始まる、UNCパス を "server/share/path/to/file.txt" のような書式に変換
     *
     * @param path
     * @return
     */
    private String cisfPath(String path) {
        // \\ を取り除く
        if (path.substring(0, 2).equals("\\\\")) {
            path = path.substring(2);
        }
        return path.replaceAll("\\\\", "\\/");
    }

    /**
     *  結果情報
     *
     */
    public class FileSystemUtilInfo {
        private long fileSize;

        public long getFileSize() {
            return fileSize;
        }

        public void setFileSize(long fileSize) {
            this.fileSize = fileSize;
        }
    }
}
