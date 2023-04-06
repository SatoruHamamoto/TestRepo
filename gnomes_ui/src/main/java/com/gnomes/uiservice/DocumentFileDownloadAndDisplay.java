package com.gnomes.uiservice;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.mozilla.universalchardet.UniversalDetector;
import org.picketbox.util.StringUtil;

import com.gnomes.common.data.FileDownLoadData;
import com.gnomes.common.data.FilePDFData;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.CurrentTimeStamp;

/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2020/02/03 10:48 YJP/S.Hamamoto           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
*/

/**
 * ServletContainerから呼ばれるドキュメントを画面表示したりダウンロードするためのロジック
 * @author 03501213
 *
 */
@RequestScoped
public class DocumentFileDownloadAndDisplay {
    //ロガー
    @Inject
    transient Logger logger;

    /** ログヘルパー */
    @Inject
    protected LogHelper logHelper;

    private static final String className = "DocumentFileDownloadAndDisplay";

    @Inject
    private ContainerResponse responseContext;

    /** ファイル拡張子とContentsTypeの変換 */
    private static Map<String, String> suffixContentsMap = new HashMap<>();
    static {
        suffixContentsMap.put("PDF", "application/pdf");
        suffixContentsMap.put("TXT", "text/plain");
        suffixContentsMap.put("CSV", "text/csv");
        suffixContentsMap.put("HTML", "text/html");
        suffixContentsMap.put("HTM", "text/html");
        suffixContentsMap.put("JPEG", "image/jpeg");
        suffixContentsMap.put("JPG", "image/jpeg");
        suffixContentsMap.put("PNG", "image/png");
        suffixContentsMap.put("GIF", "image/bmp");
        suffixContentsMap.put("TIFF", "image/tiff");
    }

    /**
     * PDF表示処理
     * @param response 出力先レスポンス
     * @throws Exception
     */
    /**
     * @param response
     * @throws Exception
     */
    public void dispPDF(HttpServletResponse response) throws Exception {

        response.reset();

        ServletOutputStream op = null;
        DataInputStream in = null;
        FilePDFData filePDFData = responseContext.getFilePDFData();

        String filePath = filePDFData.getLoadFilePath();

        //サフィックスからContentTypeを得る
        String contentsType = responseContext.getDocumentContext();

        try {
            if (filePath != null) {
                java.io.File file = new File(filePath);
                FileInputStream fis = new FileInputStream(file);
                byte[] bytesFile = IOUtils.toByteArray(fis);

                //HTTPヘッダの出力
                response.setContentType(contentsType);
                response.setContentLength((int) file.length());
                response.setHeader("Expires", "0");
                response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
                response.setHeader("Pragma", "private");

                op = response.getOutputStream();
                op.write(bytesFile, 0, (int) file.length());
                op.flush();
            } else {
                byte[] bytesFile = filePDFData.getData();

                //HTTPヘッダの出力
                response.setContentType(contentsType);
                response.setContentLength((int) bytesFile.length);
                response.setHeader("Expires", "0");
                response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
                response.setHeader("Pragma", "private");

                op = response.getOutputStream();
                op.write(bytesFile, 0, (int) bytesFile.length);
                op.flush();
            }

        } catch (SocketException e) {

            //エラーログ
            this.logHelper.severe(this.logger, null, null, e.getLocalizedMessage());

        } catch (IOException e) {
            // キャンセル時のエラーは無視
        } catch (Exception e) {
            throw e;
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(op);
        }

    }

    /**
     *
     * ドキュメントの拡張子でコンテンツタイプを得る
     *
     * @param documentSuffixString  ドキュメントの拡張子（大文字）
     *
     * @return contentsType文字("application/pdf"とか）
     */
    public String getContentsTypeFromFileSuffix(String documentSuffixString) {
        if (suffixContentsMap.containsKey(documentSuffixString)) {
            return suffixContentsMap.get(documentSuffixString);
        }
        return "";
    }

    /**
     * ファイル表示処理(表示することが前提）
     * @param response 出力先レスポンス
     * @throws Exception
     */
    public void dispFile(HttpServletResponse response) throws Exception {
        response.reset();

        ServletOutputStream op = null;
        DataInputStream in = null;

        List<FileDownLoadData> fileDownLoadDatas = responseContext.getFileDownLoadDatas();
        //サフィックスからContentTypeを得る
        String contentsType = responseContext.getDocumentContext();

        //サフィックスに該当のコンテンツタイプがなかったらダウンロードして終わる
        if (StringUtil.isNullOrEmpty(contentsType)) {
            this.download(response);
            return;
        }

        //データが入っていなかったら何もしない
        if (Objects.isNull(fileDownLoadDatas) || fileDownLoadDatas.isEmpty()) {
            return;
        }
        //リストは1個しか許可しない
        String filePath = fileDownLoadDatas.get(0).getLoadFilePath();

        try {
            //コンテンツタイプがテキストの場合ファイルを読んでUTF-8で対応する
            if ((!StringUtil.isNullOrEmpty(contentsType)) && contentsType.contains("text")) {
                java.io.File file = new File(filePath);
                String textStr = readFileToString(file);

                byte[] bytesFile = textStr.getBytes("UTF-8");

                //HTTPヘッダの出力
                response.setContentType(contentsType);
                response.setContentLength((int) bytesFile.length);
                response.setHeader("Expires", "0");
                response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
                response.setHeader("Pragma", "private");

                op = response.getOutputStream();
                op.write(bytesFile, 0, (int) bytesFile.length);
                op.flush();

            } else {
                java.io.File file = new File(filePath);
                FileInputStream fis = new FileInputStream(file);
                byte[] bytesFile = IOUtils.toByteArray(fis);

                //HTTPヘッダの出力
                response.setContentType(contentsType);
                response.setContentLength((int) file.length());
                response.setHeader("Expires", "0");
                response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
                response.setHeader("Pragma", "private");

                op = response.getOutputStream();
                op.write(bytesFile, 0, (int) file.length());
                op.flush();
            }

        } catch (SocketException e) {

            //エラーログ
            this.logHelper.severe(this.logger, null, null, e.getLocalizedMessage());

        } catch (IOException e) {
            // キャンセル時のエラーは無視
        } catch (Exception e) {
            throw e;
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(op);
        }

    }

    /**
     * エンコード文字を返す
     * UniversalDetector を使用
     * http://java.akjava.com/library/juniversalchardet
     *
     * @param file ファイルパス
     * @return  文字コードの文字
     */
    private String getEncoding(File file) {
        final String methodName ="getEncoding";
        java.io.FileInputStream fis = null;
        try {
            fis = new java.io.FileInputStream(file);
            byte[] buf = new byte[4096];
            UniversalDetector detector = new UniversalDetector(null);

            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }

            detector.dataEnd();
            return detector.getDetectedCharset();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(Objects.nonNull(fis)){
                try {
                    fis.close();
                } catch (IOException e) {
                    this.logHelper.severe(this.logger, className, methodName, file.getPath() + " is exception",e);
                }
            }
        }
        return null;
    }

    /**
     * ファイルを文字コード自動判定付きでリードしてStringに変換する
     *
     * @param file  入力Fileオブジェクト
     * @return      自動変換して返された文字列
     * @throws IOException
     */
    private String readFileToString(File file) throws IOException {
        String encode = getEncoding(file);
        if (encode == null) {
            encode = "UTF-8";
        }
        String text = FileUtils.readFileToString(file, encode);

        //BOMありだったらBOM無しにする
        if (text.charAt(0) == 65279) {//UTF-8 marker
            text = text.substring(1);
        }
        return text;
    }

    /**
     * ダウンロード処理
     * @param response 出力先レスポンス
     * @throws Exception
     */
    public void download(HttpServletResponse response) throws Exception {
        response.reset();

        //ダウンロードをクライアントで検知するためクッキーを設定する
        Cookie cookie = new Cookie("downloaded","yes");
        cookie.setPath("/");
        response.addCookie(cookie);

        ServletOutputStream op = null;

        List<FileDownLoadData> fileDownLoadDatas = this.responseContext.getFileDownLoadDatas();

        // ダウンロードファイル1件
        if (fileDownLoadDatas.size() == 1) {

            FileDownLoadData item = fileDownLoadDatas.get(0);

            DataInputStream in = null;

            String filePath = item.getLoadFilePath();
            String fileName = URLEncoder.encode(item.getSaveFileName(), "UTF-8");

            try {
                if (filePath != null) {
                    File file = new File(filePath);
                    FileInputStream fis = new FileInputStream(file);
                    byte[] bytesFile = IOUtils.toByteArray(fis);

                    //HTTPヘッダの出力
                    response.setContentType("application/octet-stream");
                    response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");
                    response.setContentLength((int) file.length());
                    response.setHeader("Expires", "0");
                    response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
                    response.setHeader("Pragma", "private");

                    op = response.getOutputStream();
                    op.write(bytesFile, 0, (int) file.length());
                    op.flush();
                } else {
                    byte[] bytesFile = item.getData();

                    //HTTPヘッダの出力
                    response.setContentType("application/octet-stream");
                    response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");
                    response.setContentLength((int) bytesFile.length);
                    response.setHeader("Expires", "0");
                    response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
                    response.setHeader("Pragma", "private");

                    op = response.getOutputStream();
                    op.write(bytesFile, 0, (int) bytesFile.length);
                    op.flush();
                }

            } catch (SocketException e) {
                //●ダウンロード処理中にダウンロードダイアログの「キャンセル」が
                //クリックされた場合の例外。
                //●ただし、ダウンロードダイアログが表示されているバックグラウンドで
                //ブラウザへのダウンロードが行われていることに留意すること。
                //●つまり小さいファイルでは、ダイアログが表示される時には、ダウンロード
                //処理は完了し、サーブレットは終了してしまっており、SocketExceptionの
                //も発生しないということです。

                //エラーログ
                this.logHelper.severe(this.logger, null, null, e.getLocalizedMessage());

            } catch (IOException e) {
                // キャンセル時のエラーは無視
            } catch (Exception e) {
                throw e;
            } finally {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(op);
            }
        }
        // ダウンロードファイル複数件
        else {
            try {
                Timestamp timestamp = CurrentTimeStamp.getSystemCurrentTimeStamp();
                String datePattern = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0054);

                String zipFileName = String.format("file_download_%s.zip",
                        ConverterUtils.tsToString(timestamp, datePattern));

                op = response.getOutputStream();

                response.setContentType("application/zip");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + zipFileName + "\"");
                response.setHeader("Expires", "0");
                response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
                response.setHeader("Pragma", "private");

                // ダウンロードファイルをZip圧縮
                byte[] zip = this.createZipDownloadFiles();

                op.write(zip);
                op.flush();

            } catch (SocketException e) {
                //●ダウンロード処理中にダウンロードダイアログの「キャンセル」が
                //クリックされた場合の例外。
                //●ただし、ダウンロードダイアログが表示されているバックグラウンドで
                //ブラウザへのダウンロードが行われていることに留意すること。
                //●つまり小さいファイルでは、ダイアログが表示される時には、ダウンロード
                //処理は完了し、サーブレットは終了してしまっており、SocketExceptionの
                //も発生しないということです。

                //エラーログ
                this.logHelper.severe(this.logger, null, null, e.getLocalizedMessage());

            } catch (IOException e) {
                // キャンセル時のエラーは無視
                throw e;
            } catch (Exception e) {
                throw e;
            } finally {
                IOUtils.closeQuietly(op);
            }

        }
    }

    /**
     * zipファイル作成
     * ダウンロードファイルをzip圧縮する
     * @return zipファイルバイナリデータ
     * @throws IOException
     * @throws ParseException
     */
    private byte[] createZipDownloadFiles() throws IOException, ParseException {
        final String methodName = "createZipDownloadFiles";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        byte bytes[] = new byte[2048];
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        ByteArrayInputStream bais = null;
        FileWriter fileWriter = null;
        StringBuilder errorFileInfo = new StringBuilder();
        List<FileDownLoadData> fileDownLoadDatas = this.responseContext.getFileDownLoadDatas();

        try {

            for (FileDownLoadData item : fileDownLoadDatas) {

                String filePath = item.getLoadFilePath();
                String fileName = item.getSaveFileName();
                fis = null;
                bis = null;
                bais = null;

                if (filePath != null) {
                    File file = new File(filePath);
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                } else {
                    byte[] bytesFile = item.getData();
                    bais = new ByteArrayInputStream(bytesFile);
                    bis = new BufferedInputStream(bais);
                }

                try {
                    zos.putNextEntry(new ZipEntry(fileName));

                    int bytesRead;
                    while ((bytesRead = bis.read(bytes)) != -1) {
                        zos.write(bytes, 0, bytesRead);
                    }
                    zos.closeEntry();
                    bis.close();
                    if (fis != null) {
                        fis.close();
                        fis = null;
                    }
                    if (bais != null) {
                        bais.close();
                        bais = null;
                    }

                } catch (ZipException ze) {
                    // 同一ファイル名がZipファイル内に存在する場合、エラーファイル情報に追加し、次のダウンロードファイルを処理
                    if (ze.getMessage().contains("duplicate entry")) {
                        if (errorFileInfo.length() != 0) {
                            errorFileInfo.append(System.lineSeparator());
                        }
                        // 同一ファイル名が存在するため、ダウンロードできませんでした。（ファイル名：{0}）
                        errorFileInfo.append(MessagesHandler.getString(GnomesMessagesConstants.ME01_0212, fileName));

                        continue;
                    }
                } finally {
                    try {
                        if (fis != null) {
                            fis.close();
                            fis = null;
                        }
                        if (bais != null) {
                            bais.close();
                            bais = null;
                        }
                        if (Objects.nonNull(bis)){
                            bis.close();
                            bis = null;
                        }
                    }
                    catch(Exception ex){
                        this.logHelper.severe(this.logger, className, methodName, "", ex);
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {

            try {
                // エラーファイルが存在する場合、ログファイルを作成し、次のダウンロードファイルを処理
                if (errorFileInfo.length() != 0) {
                    Timestamp timestamp = CurrentTimeStamp.getSystemCurrentTimeStamp();
                    String datePattern = ResourcesHandler.getString(GnomesResourcesConstants.YY01_0054);

                    String errorLogFileName = String.format("error_file_download_%s.log",
                            ConverterUtils.tsToString(timestamp, datePattern));

                    File file = new File(errorLogFileName);
                    fileWriter = new FileWriter(file);
                    fileWriter.write(errorFileInfo.toString());
                    fileWriter.close();
                    fileWriter = null;

                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);

                    // 次のダウンロードファイルを処理
                    zos.putNextEntry(new ZipEntry(errorLogFileName));
                    int bytesRead;

                    while ((bytesRead = bis.read(bytes)) != -1) {
                        zos.write(bytes, 0, bytesRead);
                    }
                    zos.closeEntry();
                    zos = null;
                    bis.close();
                    bis = null;
                    if (fis != null) {
                        fis.close();
                        fis = null;
                    }
                    if (bais != null) {
                        bais.close();
                        bais = null;
                    }
                }
            } catch (Exception ex) {
                //エラーログ
                this.logHelper.severe(this.logger, null, null, ex.getMessage());
                throw ex;
            } finally {

                if (zos != null) {
                    zos.flush();
                    zos.close();
                }
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
                if (bis != null) {
                    bis.close();
                }
                if (fis != null) {
                    fis.close();
                }
                if (bais != null) {
                    bais.close();
                }
                if (Objects.nonNull(fileWriter)){
                    fileWriter.close();
                }
            }
        }
        return baos.toByteArray();
    }
}
