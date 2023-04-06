package com.gnomes.external.logic.talend;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.powermock.reflect.Whitebox;

import com.gnomes.TestUtil;
import com.gnomes.common.constants.CommonEnums.ExternalIfIsDataItemId;
import com.gnomes.common.constants.CommonEnums.FormatId;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.resource.GnomesLogMessageConstants;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.util.GnomesDateUtil;
import com.gnomes.external.data.DataDefine;
import com.gnomes.external.data.FileTransferBean;
import com.gnomes.external.data.SendRecvDataBean;
import com.gnomes.external.data.SystemDefine;

/**
 * 受信伝文マッピングテスト
 *
 * @author 30041979
 *
 */
class RecvDataMappingJobTest {

	private RecvDataMappingJob job;

	private FileTransferBean fileTransferBean;

	private GnomesExceptionFactory exceptionFactory;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		job = TestUtil.createBean(RecvDataMappingJob.class);

		fileTransferBean = new FileTransferBean();
		SystemDefine systemDefine = new SystemDefine();
		systemDefine.setTime_zone(TimeZone.getDefault().toString());
		fileTransferBean.setSystemDefine(systemDefine);
		fileTransferBean.setBeanClass(TestSendRecvDataData.class);
		Whitebox.setInternalState(job, "fileTransferBean", fileTransferBean);

		exceptionFactory = new GnomesExceptionFactory();
		Whitebox.setInternalState(job, "exceptionFactory", exceptionFactory);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	@DisplayName("全ての項目をマッピングする")
	void test1() throws Exception {
		DataDefine d1 = createDataDefine(0, "checkInteger", FormatId.Integer);
		DataDefine d2 = createDataDefine(1, "checkString", FormatId.String);
		DataDefine d3 = createDataDefine(2, "checkDate", FormatId.Date);
		DataDefine d4 = createDataDefine(3, "checkBigDecimal", FormatId.BigDecimal);
		DataDefine d5 = createDataDefine(4, "checkFixedValue", FormatId.FixedValue);
		List<DataDefine> dataDefineList = createList(d1, d2, d3, d4, d5);
		fileTransferBean.setDataDefine(dataDefineList);

		Integer id = 123;
		String str = "abcd";
		String strDate = LocalDateTime.now().toString(ResourcesHandler.getString(d3.getDate_format()));
		BigDecimal bigDecimal = new BigDecimal("0.1234567");
		String fixedValue = "testFixedValue";
		d5.setFixed_value_string(fixedValue);

		Map<String, String> map = new LinkedHashMap<>();
		map.put(d1.getData_item_id(), id.toString());
		map.put(d2.getData_item_id(), str);
		map.put(d3.getData_item_id(), strDate);
		map.put(d4.getData_item_id(), bigDecimal.toString());
		map.put(d5.getData_item_id(), fixedValue);
		List<Map<String, String>> recvConDataList = createList(map);
		fileTransferBean.setRecvConvData(recvConDataList);

		try {
			job.process();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		List<SendRecvDataBean> dataList = fileTransferBean.getSendRecvDataBeanList();

		assertEquals(1, dataList.size());

		TestSendRecvDataData data = (TestSendRecvDataData) dataList.get(0);

		assertEquals(id, data.getCheckInteger());

		assertEquals(str, data.getCheckString());

		Date date = GnomesDateUtil.convertStringLocaleToUtcDateTimePattern(strDate,
				ResourcesHandler.getString(d3.getDate_format()), TimeZone.getDefault());
		assertEquals(date, data.getCheckDate());

		assertEquals(bigDecimal, data.getCheckBigDecimal());

		assertEquals(fixedValue, data.getCheckFixedValue());

		assertTrue(fileTransferBean.getErrorLineInfo().isEmpty());
	}

	@Test
	@DisplayName("処理対象外の文字列項目は空文字をセットする")
	void test2() throws Exception {
		DataDefine d1 = createDataDefine(0, "checkInteger", FormatId.Integer);
		DataDefine d2 = createDataDefine(1, "checkString", FormatId.String);
		d2.setIsdata_item_id(ExternalIfIsDataItemId.INVALID.getValue());
		DataDefine d3 = createDataDefine(2, "checkDate", FormatId.Date);
		List<DataDefine> dataDefineList = createList(d1, d2, d3);
		fileTransferBean.setDataDefine(dataDefineList);

		Integer id = 123;
		String str = "abcd";
		String strDate = LocalDateTime.now().toString(ResourcesHandler.getString(d3.getDate_format()));

		Map<String, String> map = new LinkedHashMap<>();
		map.put(d1.getData_item_id(), id.toString());
		map.put(d2.getData_item_id(), str);
		map.put(d3.getData_item_id(), strDate);
		List<Map<String, String>> recvConDataList = createList(map);
		fileTransferBean.setRecvConvData(recvConDataList);

		job.process();

		List<SendRecvDataBean> dataList = fileTransferBean.getSendRecvDataBeanList();

		assertEquals(1, dataList.size());

		TestSendRecvDataData data = (TestSendRecvDataData) dataList.get(0);

		assertEquals(id, data.getCheckInteger());

		assertEquals("", data.getCheckString());

		Date date = GnomesDateUtil.convertStringLocaleToUtcDateTimePattern(strDate,
				ResourcesHandler.getString(d3.getDate_format()), TimeZone.getDefault());
		assertEquals(date, data.getCheckDate());

		assertTrue(fileTransferBean.getErrorLineInfo().isEmpty());
	}

	@Test
	@DisplayName("Dateフォーマットが不正の場合はエラーで終了する")
	void test3() throws Exception {
		DataDefine d1 = createDataDefine(0, "checkInteger", FormatId.Integer);
		DataDefine d2 = createDataDefine(1, "checkString", FormatId.String);
		DataDefine d3 = createDataDefine(2, "checkDate", FormatId.Date);
		List<DataDefine> dataDefineList = createList(d1, d2, d3);
		fileTransferBean.setDataDefine(dataDefineList);

		Integer id = 123;
		String str = "abcd";
		String strDate = LocalDateTime.now().toString(ResourcesHandler.getString(d3.getDate_format())) + "badDate";

		Map<String, String> map = new LinkedHashMap<>();
		map.put(d1.getData_item_id(), id.toString());
		map.put(d2.getData_item_id(), str);
		map.put(d3.getData_item_id(), strDate);
		List<Map<String, String>> recvConDataList = createList(map);
		fileTransferBean.setRecvConvData(recvConDataList);

		GnomesAppException e = assertThrows(GnomesAppException.class, () -> job.process());

		assertEquals(GnomesMessagesConstants.ME01_0106, e.getMessageNo());

		Map<Integer, String> errorMap = fileTransferBean.getErrorLineInfo();
		assertEquals(1, errorMap.size());

		String errorMsg = createLineErrorMsg(GnomesLogMessageConstants.ME01_0100, "1", d3, strDate);
		assertEquals(errorMap.get(1), errorMsg);
	}

	@Test
	@DisplayName("数値型データの入力値が不正の場合はエラーで終了する")
	void test4() throws Exception {
		DataDefine d1 = createDataDefine(0, "checkInteger", FormatId.Integer);
		DataDefine d2 = createDataDefine(1, "checkBigDecimal", FormatId.BigDecimal);
		List<DataDefine> dataDefineList = createList(d1, d2);
		fileTransferBean.setDataDefine(dataDefineList);

		String num = "123abc";
		String bigDecimal = "223abcd";

		Map<String, String> map = new LinkedHashMap<>();
		map.put(d1.getData_item_id(), num);
		map.put(d2.getData_item_id(), bigDecimal);
		List<Map<String, String>> recvConDataList = createList(map);
		fileTransferBean.setRecvConvData(recvConDataList);

		GnomesAppException e = assertThrows(GnomesAppException.class, () -> job.process());

		assertEquals(GnomesMessagesConstants.ME01_0106, e.getMessageNo());

		Map<Integer, String> errorMap = fileTransferBean.getErrorLineInfo();
		assertEquals(1, errorMap.size());

		String errorMsg1 = createLineErrorMsg(GnomesLogMessageConstants.ME01_0099, "1", d1, num);
		String errorMsg2 = createLineErrorMsg(GnomesLogMessageConstants.ME01_0099, "1", d2, bigDecimal);
		StringBuilder mesDetail = new StringBuilder().append(errorMsg1).append(" " + System.lineSeparator())
				.append(errorMsg2);
		assertEquals(errorMap.get(1), mesDetail.toString());
	}

	private static String createLineErrorMsg(String msgNo, String lineNo, DataDefine df, String value) {
		String errorMsg = df.getFormat_id() == FormatId.Date.getValue() ? MessagesHandler.getString(msgNo, value,
				ResourcesHandler.getString(df.getDate_format()), df.getData_item_name())
				: MessagesHandler.getString(msgNo, value, df.getData_item_name());
		String lineMsg = MessagesHandler.getString(GnomesMessagesConstants.MV01_0027, lineNo,
				df.getData_item_number().toString(), errorMsg);
		return lineMsg;
	}

	private static DataDefine createDataDefine(int num, String dataItemId, FormatId formatId) {
		DataDefine df = new DataDefine();
		df.setIsdata_item_id(ExternalIfIsDataItemId.VALID.getValue());
		df.setData_item_number(num);
		df.setData_item_id(dataItemId);
		df.setFormat_id(formatId.getValue());
		df.setDate_format("YY01.0002");
		return df;
	}

	private static <T> List<T> createList(T... ds) {
		List<T> list = new ArrayList<>();
		for (T d : ds) {
			list.add(d);
		}
		return list;
	}

}
