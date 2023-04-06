package com.gnomes.rest.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.collections4.IteratorUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;

import com.gnomes.common.constants.SystemDefConstants;
import com.gnomes.common.data.FileUpLoadData;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.system.dao.MstrSystemDefineDao;
import com.gnomes.system.entity.MstrSystemDefine;

/**
 *
 * ファイルアップロード マルチパートデータ操作
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/12/26 YJP/A.Oomori               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class MultiPartCommon {

    @Inject
    MstrSystemDefineDao mstrSystemDefineDao;


    /**
     * コンストラクター
     */
    public MultiPartCommon() {}

    /**
     * アップロードファイル情報取得
     * @param inputParts アップロードファイルInput情報
     * @return アップロードファイル情報
     * @throws GnomesAppException 例外
     */
    public List<FileUpLoadData> getUploadFiles(List<InputPart> inputParts) throws GnomesAppException {
        return getUploadFiles(inputParts, null);
    }


    /**
     * アップロードファイル情報取得
     * @param inputParts アップロードファイルInput情報
     * @param requestErr リクエストエラー出力先
     * @return アップロードファイル情報
     * @throws GnomesAppException 例外
     */
    public List<FileUpLoadData> getUploadFiles(List<InputPart> inputParts, Map<String,String[]> requestErr) throws GnomesAppException {

        int systemMaxFileSize = getSystemMaxFileSize();
        List<FileUpLoadData> uploadFiles = new ArrayList<>();

        for (InputPart inputPart : inputParts) {

            FileUpLoadData item = new FileUpLoadData();

            try {
                item.setFileName(getFileName(inputPart));

                //convert the uploaded file to inputstream
                InputStream inputStream = inputPart.getBody(InputStream.class,null);

                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int size = 0;

                while((size = inputStream.read(buf, 0, buf.length)) != -1) {
                    outStream.write(buf, 0, size);
                }

                int inputFileSize = outStream.size();

                // サイズオーバー判定
                if (inputFileSize >= systemMaxFileSize) {
                    // 少数第2位で切り上げ
                    BigDecimal decInFileSize = new BigDecimal(inputFileSize);
                    BigDecimal decMb = new BigDecimal(1048576);
                    decInFileSize = decInFileSize.divide(decMb, 1, BigDecimal.ROUND_UP);
                    if (requestErr == null) {
                        // メッセージ： ファイルサイズが{0}Mバイトを超えているため、アップロードできません。\nファイル名： {1}、ファイルサイズ：{2}Mバイト
                        GnomesAppException ex = new GnomesAppException(null,
                                GnomesMessagesConstants.ME01_0046,
                                (systemMaxFileSize / 1048576), item.getFileName(), decInFileSize.toString());
                        throw ex;
                    } else {
                        String[] errParam = {
                                GnomesMessagesConstants.ME01_0046,
                                String.valueOf(systemMaxFileSize / 1048576),
                                item.getFileName(),
                                decInFileSize.toString()
                        };
                        requestErr.put(item.getFileName(), errParam);
                        continue;
                    }
                }

//                item.setData(IOUtils.toByteArray(inputStream));
                item.setData(outStream.toByteArray());

            }
            catch (IOException e)
            {
                // アップロードファイルの取得に失敗しました。
                // メッセージ： アップロード時にエラーが発生しました。 詳細はエラーメッセージを確認してください。\nファイル名： {0}
                GnomesAppException ex = new GnomesAppException(e);
                ex.setMessageNo(GnomesMessagesConstants.ME01_0042);
                Object[] errParam = {
                        item.getFileName()
                };
                ex.setMessageParams(errParam);
                throw ex;
            }
           uploadFiles.add(item);
        }
        return uploadFiles;
    }

    /**
     * ファイル名取得
     * @param inputPart インプット
     * @return ファイル名
     * @throws GnomesAppException 例外
     */
    private String getFileName(InputPart inputPart) throws GnomesAppException {

        try {
            Field field = inputPart.getClass().getDeclaredField("bodyPart");
            field.setAccessible(true);
            Object bodyPart = field.get(inputPart);
            Method methodBodyPart = bodyPart.getClass().getMethod("getHeader", new Class[]{});
            Iterable<?> iterable = (Iterable<?>) methodBodyPart.invoke(bodyPart, (Object[])null);

            Object[] content = IteratorUtils.toArray(iterable.iterator());
            Method methodContent = content[0].getClass().getMethod("getRaw", new Class[]{});
            Object b = methodContent.invoke(content[0], (Object[])null);

            Method methodToByteArray = b.getClass().getMethod("toByteArray", new Class[]{});
            byte bb[] = (byte[]) methodToByteArray.invoke(b,  (Object[])null);

            String s = new String(bb, "UTF-8");

            String[] contentDisposition = s.split(";");

            for (String filename : contentDisposition) {
                if ((filename.trim().startsWith("filename"))) {

                    String[] name = filename.split("=");
                    String finalFileName = name[1].trim().replaceAll("\"", "");

                    finalFileName = new File(finalFileName).getName();

                    return finalFileName;
                }
            }
        }
        catch (Exception e)
        {
            // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
            GnomesAppException ex =new GnomesAppException(e);
            ex.setMessageNo(GnomesMessagesConstants.ME01_0001);
            throw ex;
        }

        return null;
    }

    /**
     * アップロードファイル最大サイズ (Mバイト）取得
     * @return Mバイト
     * @throws GnomesAppException 例外
     */
    private int getSystemMaxFileSize() throws GnomesAppException {
        MstrSystemDefine item = mstrSystemDefineDao.getMstrSystemDefine(
                SystemDefConstants.UPLOAD, SystemDefConstants.UPLOAD_MAX_SIZE);

        return item.getNumeric1().intValue() * 1048576;
    }
}
