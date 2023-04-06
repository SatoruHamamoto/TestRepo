package com.gnomes.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;

import javax.servlet.ServletContext;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;

/**
 * ファイル処理クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/07/11 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public final class FileUtils {

    /** 空白 */
    private static final String EMPTY = "";

    /** 全角スペース */
    private static final String SPACE_CHAR_WIDE = "　";

    /** デフォルト・コンストラクタ */
    private FileUtils() {

    }

    /**
     * ディレクトリ作成.
     * <pre>
     * ディレクトリの作成を行う。
     * 既に作成済みの場合は、作成を行わない。
     * </pre>
     * @param directoryPath ディレクトリパス
     * @throws GnomesAppException
     */
    public static void makeDirectory(String directoryPath) throws GnomesAppException {

        // パラメータチェック
        if (isNullOrEmpty(directoryPath)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesExceptionFactory ef = new GnomesExceptionFactory();

            StringBuilder params = new StringBuilder();
            params.append("directoryPath");
            params.append(CommonConstants.COLON);
            params.append(directoryPath);

            throw ef.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050, params.toString());

        }

        File file = new File(directoryPath);

        // ディレクトリ存在チェック
        if (!file.exists()) {
            // ディレクトリ作成
            if (!file.mkdirs()) {
                // ME01.0189:「ディレクトリの作成に失敗しました。（ディレクトリパス: {0}）」
                GnomesExceptionFactory ef = new GnomesExceptionFactory();

                throw ef.createGnomesAppException(
                        null, GnomesMessagesConstants.ME01_0189, directoryPath);

            }

        }

    }

    /**
     * ファイル移動.
     * <pre>
     * ファイルの移動を行う。
     * </pre>
     * @param directoryPathFrom 移動元ディレクトリパス
     * @param directoryPathTo 移動先ディレクトリパス
     * @param fileName 移動対象ファイル名
     * @throws GnomesAppException
     */
    public static void move(String directoryPathFrom, String directoryPathTo, String fileName) throws GnomesAppException {

        // パラメータチェック
        StringBuilder params = null;

        // 移動元ディレクトリパス
        if (isNullOrEmpty(directoryPathFrom)) {
            params = new StringBuilder();
            params.append("directoryPathFrom");
            params.append(CommonConstants.COLON);
            params.append(directoryPathFrom);

        }
        // 移動先ディレクトリパス
        if (isNullOrEmpty(directoryPathTo)) {
            if (Objects.isNull(params)) {
                params = new StringBuilder();
            } else {
                params.append(CommonConstants.COMMA);
            }
            params.append("directoryPathTo");
            params.append(CommonConstants.COLON);
            params.append(directoryPathTo);

        }
        // 移動対象ファイル名
        if (isNullOrEmpty(fileName)) {
            if (Objects.isNull(params)) {
                params = new StringBuilder();
            } else {
                params.append(CommonConstants.COMMA);
            }
            params.append("fileName");
            params.append(CommonConstants.COLON);
            params.append(fileName);

        }

        if (!Objects.isNull(params)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            throw ef.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050, params.toString());
        }

        // 移動元
        StringBuilder pathFrom = new StringBuilder();
        pathFrom.append(directoryPathFrom);
        pathFrom.append(File.separator);
        pathFrom.append(fileName);
        File fileFrom = new File(pathFrom.toString());

        // 移動先
        StringBuilder pathTo = new StringBuilder();
        pathTo.append(directoryPathTo);
        pathTo.append(File.separator);
        pathTo.append(fileName);
        File fileTo = new File(pathTo.toString());

        // ファイル移動
        if (!fileFrom.renameTo(fileTo)) {
            // ME01.0190:「ファイルの移動に失敗しました。（移動元: {0}、移動先: {1}、ファイル名: {2}）」
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            throw ef.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0190,
                    new Object[]{directoryPathFrom, directoryPathTo, fileName});

        }

    }

    /**
     * ファイル名変更
     * <pre>
     * ファイル名の変更を行う。
     * </pre>
     * @param directoryPath ディレクトリパス
     * @param fileNameFrom 変更前ファイル名
     * @param fileNameTo 変更後ファイル名
     * @throws GnomesAppException
     */
    public static void rename(String directoryPath, String fileNameFrom, String fileNameTo) throws GnomesAppException {

        // パラメータチェック
        StringBuilder params = null;

        // ディレクトリパス
        if (isNullOrEmpty(directoryPath)) {
            params = new StringBuilder();
            params.append("directoryPath");
            params.append(CommonConstants.COLON);
            params.append(directoryPath);

        }
        // 変更前ファイル名
        if (isNullOrEmpty(fileNameFrom)) {
            if (Objects.isNull(params)) {
                params = new StringBuilder();
            } else {
                params.append(CommonConstants.COMMA);
            }
            params.append("fileNameFrom");
            params.append(CommonConstants.COLON);
            params.append(fileNameFrom);

        }
        // 変更後ファイル名
        if (isNullOrEmpty(fileNameTo)) {
            if (Objects.isNull(params)) {
                params = new StringBuilder();
            } else {
                params.append(CommonConstants.COMMA);
            }
            params.append("fileNameTo");
            params.append(CommonConstants.COLON);
            params.append(fileNameTo);

        }

        if (!Objects.isNull(params)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            throw ef.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050, params.toString());
        }

        // 変更前
        StringBuilder pathFrom = new StringBuilder();
        pathFrom.append(directoryPath);
        pathFrom.append(File.separator);
        pathFrom.append(fileNameFrom);
        File fileFrom = new File(pathFrom.toString());

        // 変更後
        StringBuilder pathTo = new StringBuilder();
        pathTo.append(directoryPath);
        pathTo.append(File.separator);
        pathTo.append(fileNameTo);
        File fileTo = new File(pathTo.toString());

        // ファイル名変更
        if (!fileFrom.renameTo(fileTo)) {
            // ME01.0191:「ファイル名の変更に失敗しました。（ディレクトリパス: {0}、変更前: {1}、変更後: {2}）」
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            throw ef.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0191,
                    new Object[]{directoryPath, fileNameFrom, fileNameTo});

        }

    }

    /**
     * 削除.
     * <pre>
     * ディレクトリまたはファイルの削除を行う。
     * 削除対象パスがディレクトリの場合、再帰的に削除を行う。
     * </pre>
     * @param path 削除対象パス
     * @throws GnomesAppException
     */
    public static void delete(String path) throws GnomesAppException {

        // パラメータチェック
        if (isNullOrEmpty(path)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesExceptionFactory ef = new GnomesExceptionFactory();

            StringBuilder params = new StringBuilder();
            params.append("path");
            params.append(CommonConstants.COLON);
            params.append(path);

            throw ef.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050, params.toString());

        }

        File file = new File(path);

        // ディレクトリ削除
        recursiveDeleteFile(file);

    }

    /**
     * ファイルバイナリ変換
     * <pre>
     * ファイルバイナリ変換を行う。
     * </pre>
     * @param filePath ファイルパス
     * @return 読み込んだバイト配列
     * @throws GnomesAppException
     */
    public static byte[] readFileToByte(String filePath) throws GnomesAppException {

        // パラメータチェック
        if (isNullOrEmpty(filePath)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesExceptionFactory ef = new GnomesExceptionFactory();

            StringBuilder params = new StringBuilder();
            params.append("filePath");
            params.append(CommonConstants.COLON);
            params.append(filePath);

            throw ef.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050, params.toString());

        }

        File file = new File(filePath);
        // ファイル存在チェック
        if (!(file.isFile() && file.exists())) {
            // ME01.0193:「変換対象のファイルが存在しません。（ファイルパス: {0}）」
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            throw ef.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0193, filePath);
        }

        BufferedInputStream inputStream = null;
        ByteArrayOutputStream outStream = null;

        try {
            // ファイル読込み
            inputStream = new BufferedInputStream(new FileInputStream(filePath));

            outStream = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int size = 0;

            while ((size = inputStream.read(buf, 0, buf.length)) != -1) {
                outStream.write(buf, 0, size);
            }

            return outStream.toByteArray();

        } catch (IOException e) {
            // ME01.0194:「ファイルの変換に失敗しました。（ファイルパス: {0}）」
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            throw ef.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0194, filePath);

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // 何もしない
                }

            }
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    // 何もしない
                }

            }

        }

    }

    /**
     * バイナリデータファイル変換
     * <pre>
     * バイナリデータからファイルに変換を行う。
     * </pre>
     * @param filePath 作成先ファイルパス
     * @param fileData 変換対象ファイルデータ
     * @throws GnomesAppException
     */
    public static void writeFileFromByte(String filePath, byte[] fileData) throws GnomesAppException {

        // パラメータチェック
        StringBuilder params = null;

        // 作成先ファイルパス
        if (isNullOrEmpty(filePath)) {
            params = new StringBuilder();
            params.append("filePath");
            params.append(CommonConstants.COLON);
            params.append(filePath);

        }
        // 変換対象ファイルデータ
        if (Objects.isNull(fileData)) {
            if (Objects.isNull(params)) {
                params = new StringBuilder();
            } else {
                params.append(CommonConstants.COMMA);
            }
            params.append("fileData");
            params.append(CommonConstants.COLON);
            params.append(fileData);

        }

        if (!Objects.isNull(params)) {
            // ME01.0050:「パラメータが不正です。({0})」
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            throw ef.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050, params.toString());
        }

        FileOutputStream outputStream = null;
        BufferedOutputStream outStream = null;

        try {

            outputStream = new FileOutputStream(filePath);
            outStream = new BufferedOutputStream(outputStream);
            outStream.write(fileData);

        } catch (IOException e) {
            // ME01.0195:「ファイルの作成に失敗しました。」
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            throw ef.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0195);

        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    // 何もしない
                }

            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    // 何もしない
                }

            }

        }

    }

    /**
     * パラメータチェック.
     * <pre>
     * チェック対象値の必須チェックを行う。
     * </pre>
     * @param value チェック対象値
     * @return チェック結果
     */
    private static boolean isNullOrEmpty(String value) {

        if (StringUtil.isNotNull(value)) {

            String checkValue = value.trim().replace(SPACE_CHAR_WIDE, EMPTY);

            if (StringUtil.isNullOrEmpty(checkValue)) {
                return true;
            }
            return false;

        }

        return true;

    }

    /**
     * 対象のファイルオブジェクトの削除を行う.
     * <pre>
     * ディレクトリの場合は再帰処理を行い、削除する。
     * </pre>
     *
     * @param file ファイルオブジェクト
     * @throws GnomesAppException
     */
    private static void recursiveDeleteFile(File file) throws GnomesAppException {

        // 存在しない場合は処理終了
        if (!file.exists()) {
            return;
        }
        // 対象がディレクトリの場合は再帰処理
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                recursiveDeleteFile(child);
            }
        }
        // 削除
        if(!file.delete()) {
            // ME01.0192:「ディレクトリの削除に失敗しました。（ディレクトリパス: {0}）」
            GnomesExceptionFactory ef = new GnomesExceptionFactory();
            throw ef.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0192, file.toString());
        }

    }

    /**
     * プロジェクト内のファイルの絶対パスの取得（絶対パス＋ファイルのカレントパス）
     * 取得例：C:\EX\UI\wildfly-10.1.0.Final\standalone\deployments\UI.war + 引数.ファイルのカレントパス
     * @param fileCurrentPath ファイルのカレントパス
     * @param servletContext サーブレット
     * @return ファイルの絶対パス
     * @throws GnomesAppException
     */
    public static String getFullPath(String fileCurrentPath, ServletContext servletContext) throws GnomesAppException {
		return servletContext.getRealPath(fileCurrentPath);
	}

	/**
	 * PDFバイナリデータに挿入画像を貼り付け
	 * @param pdfData PDFバイナリデータ
	 * @param drawImagePath 挿入画像の絶対パス
	 * @return 画像貼り付け後のPDFバイナリデータ
	 * @throws GnomesAppException
	 */
	public static byte[] getPdfDrawnImage(byte[] pdfData, String drawImagePath) throws GnomesAppException {
		return getPdfDrawnImage(pdfData, drawImagePath, 10, 10, (float)1.0);
	}

	/**
	 * PDFバイナリデータに挿入画像を貼り付け
	 * @param pdfData PDFバイナリデータ
	 * @param drawImagePath 挿入画像のパス
	 * @param x 画像の挿入位置（x）
	 * @param y 画像の挿入位置（y）
	 * @param scale 挿入画像の拡大率
	 * @return 画像貼り付け後のPDFバイナリデータ
	 * @throws GnomesAppException
	 */
//	public static byte[] getPdfDrawnImage(byte[] pdfData, String drawImagePath, float x, float y, float scale) throws GnomesAppException {
//
//        // PDFファイル
//        PDDocument document = new PDDocument();
//		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//
//		// PDFファイルの読み込み
//		PDPage page = new PDPage();
//		document.addPage(page);
//
//	    // 画像オブジェクトを作成する
//	    PDImageXObject image;
//		try {
//			image = PDImageXObject.createFromFile(drawImagePath, document);
//
////		    PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
//		    PDPageContentStream contentStream = new PDPageContentStream(document, page);
//			contentStream.drawImage(image, x, y, image.getWidth() * scale, image.getHeight() * scale);
//
//			// ストリームを閉じる
//			contentStream.close();
//
//			document.setAllSecurityToBeRemoved(false);
//			document.save(byteArrayOutputStream);
//
//			document.close();
//
//
//		} catch (IOException e) {
//			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		}
//		return byteArrayOutputStream.toByteArray();
//
//	}

	/**
	 * PDFバイナリデータに挿入画像を貼り付け
	 * @param pdfData PDFバイナリデータ
	 * @param drawImagePath 挿入画像のパス
	 * @param x 画像の挿入位置（x）
	 * @param y 画像の挿入位置（y）
	 * @param scale 挿入画像の拡大率
	 * @return 画像貼り付け後のPDFバイナリデータ
	 * @throws GnomesAppException
	 */
	public static byte[] getPdfDrawnImage(byte[] pdfData, String drawImagePath, float x, float y, float scale) throws GnomesAppException {

        // PDFファイル
        PDDocument document = null;

		try {
			// PDFファイルの読み込み
			document = PDDocument.load(pdfData);
			document.setAllSecurityToBeRemoved(true);
		}
		// 読み込みに失敗、PDFデータではない場合
		catch (Exception e) {
			// ME01.0208:「PDF形式のバイナリデータを指定してください。」
			GnomesExceptionFactory ef = new GnomesExceptionFactory();
            throw ef.createGnomesAppException(null, GnomesMessagesConstants.ME01_0208);
        }

        // 挿入画像パスのチェック
        if (isNullOrEmpty(drawImagePath)) {

        	StringBuilder arguments = new StringBuilder();
        	arguments.append("drawImagePath");
        	arguments.append(CommonConstants.COLON);
        	arguments.append(String.valueOf(drawImagePath));
        	// ME01.0050:「パラメータが不正です。({0})」
         	GnomesExceptionFactory ef = new GnomesExceptionFactory();
            throw ef.createGnomesAppException(null, GnomesMessagesConstants.ME01_0050, arguments.toString());
        }

        File file = new File(drawImagePath);
        // 画像ファイル存在チェック
        if (!(file.isFile() && file.exists())) {
            // ME01.0193:「変換対象のファイルが存在しません。（ファイルパス: {0}）」
        	GnomesExceptionFactory ef = new GnomesExceptionFactory();
            throw ef.createGnomesAppException(null, GnomesMessagesConstants.ME01_0193, drawImagePath);
        }

		try {

			// 画像オブジェクトを作成する
			PDImageXObject image = PDImageXObject.createFromFile(drawImagePath, document);
			Iterator<PDPage> iter = document.getPages().iterator();

			PDDocumentInformation info = document.getDocumentInformation();
			info.setTitle(ResourcesHandler.getString(GnomesResourcesConstants.DI01_0139));

			// 毎ページ画像を挿入
			while(iter.hasNext())
		    {

				PDPage page = (PDPage)iter.next();

				// 出力用のストリームを開いて画像を描写する
				PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
				contentStream.drawImage(image, x, y, image.getWidth() * scale, image.getHeight() * scale);

				// ストリームを閉じる
				contentStream.close();
			}

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			document.save(byteArrayOutputStream);
			document.close();

			return byteArrayOutputStream.toByteArray();

		} catch (IOException e) {
			GnomesExceptionFactory ef = new GnomesExceptionFactory();
			throw ef.createGnomesAppException(e);
        }

	}
}
