package com.gnomes.common.exportdata;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.gnomes.common.data.FileDownLoadData;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.system.dao.MstrSystemDefineDao;
import com.gnomes.uiservice.ContainerResponse;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;

/**
 * CSV Download 処理
 * 
 * <!-- TYPE DESCRIPTION -->
 * 
 * <pre>
 * </pre>
 * 
 */
/*
 * ========================== MODIFICATION HISTORY ==========================
 * Release Date ID/Name Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2022/05/10 YJP/M.Kitada 初版 [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */

@Dependent
public class DownLoadCsv {
	@Inject
	protected GnomesExceptionFactory exceptionFactory;
	@Inject
	protected CsvCommon csvCommon;
	@Inject
	protected ContainerResponse containerResponse;
	@Inject
	protected MstrSystemDefineDao mstrSystemDefineDao;
	/**
     * データのCSV出力
     * <pre>
     * パラメータのObjectの配列のリストは空白はObject(null)でも""でも可、他の行と数を合わせなくても可、Objectの中身は配列
     * CSVセパレータでタブ区切りにしたい場合は'/t'を指定します
     * ダウンロードするファイル名は「渡されたパラメータ」+「通算秒」+「.csv」
     * </pre>
     * @param data      データリスト
     * @param separator ','や'/t'などのセパレータ
     * @param fileName  ダウンロードするファイル名
     * @throws GnomesAppException
     */
	public void downLoadDataCsv(List<Object> data, char separatorChar, String fileName) throws GnomesAppException {

		try {
			Date date = new Date();
			String fileNameSuffix = String.valueOf(date.getTime());
			
            if (Objects.isNull(fileName)) {
                fileName = "";
            }
            fileName = fileName + fileNameSuffix + ".csv";

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// 文字コード
			String outCharsetName = csvCommon.getSysCharsetName();

			OutputStreamWriter writer = new OutputStreamWriter(baos, Charset.forName(outCharsetName));
			CSVWriterBuilder builder = new CSVWriterBuilder(writer);

			// セパレータ
			// DBから取得する場合はコメントをはずす
			// separatorChar = csvCommon.getSysSeparator();
			builder.withSeparator(separatorChar);

			// 改行コード
			String endLine = csvCommon.getSysEndLine();
			builder.withLineEnd(endLine);

			// 囲い文字
			char quote = csvCommon.getSysQuotechar();
			builder.withQuoteChar(quote);

			ICSVWriter csvWriter = builder.build();
			// 内容の書込み
			if (!Objects.isNull(data)) {
				for (int i = 0; i < data.size(); i++) {
					List<String> list = new ArrayList<String>();
					int length = Array.getLength(data.get(i));
					for (int ii = 0; ii < length; ii++) {
						if (Objects.isNull(Array.get(data.get(i), ii))) {
							// データがNullの場合は空文字で設定
							list.add("");
						} else {
							list.add(Array.get(data.get(i), ii).toString());
						}
					}
					String[] arr = list.toArray(new String[list.size()]);
					csvWriter.writeNext(arr, false);
				}
			}
			csvWriter.close();

			byte[] bytes = baos.toByteArray();

			FileDownLoadData fileDownLoadData = new FileDownLoadData();
			fileDownLoadData.setSaveFileName(fileName);
			fileDownLoadData.setData(bytes);
			List<FileDownLoadData> fileDownLoadDatas = new ArrayList<>();
			fileDownLoadDatas.add(fileDownLoadData);
			containerResponse.setFileDownLoadDatas(fileDownLoadDatas);

		} catch (Exception e) {
			throw exceptionFactory.createGnomesAppException(e);
		}
	}
}
