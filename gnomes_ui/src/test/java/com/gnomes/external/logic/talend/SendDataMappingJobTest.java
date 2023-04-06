package com.gnomes.external.logic.talend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

import com.gnomes.TestUtil;
import com.gnomes.common.constants.CommonEnums.ExternalIfIsDataItemId;
import com.gnomes.common.constants.CommonEnums.FormatId;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.external.data.DataDefine;
import com.gnomes.external.data.FileTransferBean;
import com.gnomes.external.data.SendRecvDataBean;
import com.gnomes.external.data.SystemDefine;
import com.gnomes.system.entity.MstrMessageDefine;

/**
 * 送信伝文マッピングテスト
 * 
 * @author 30041979
 *
 */
class SendDataMappingJobTest {

	private SendDataMappingJob job;

	private FileTransferBean fileTransferBean;

	private GnomesExceptionFactory exceptionFactory;

	private MockedStatic<MessagesHandler> msgHandlerMock;

	private MockedStatic<ResourcesHandler> resHandlerMock;

	@BeforeEach
	void setUp() throws Exception {
		msgHandlerMock = Mockito.mockStatic(MessagesHandler.class);
		resHandlerMock = Mockito.mockStatic(ResourcesHandler.class);

		job = TestUtil.createBean(SendDataMappingJob.class);

		fileTransferBean = new FileTransferBean();
		SystemDefine systemDefine = new SystemDefine();
		systemDefine.setTime_zone(TimeZone.getDefault().toString());
		fileTransferBean.setSystemDefine(systemDefine);
		Whitebox.setInternalState(job, "fileTransferBean", fileTransferBean);

		exceptionFactory = new GnomesExceptionFactory();
		Whitebox.setInternalState(job, "exceptionFactory", exceptionFactory);

		MstrMessageDefine md = new MstrMessageDefine();
		md.setMessage_no("testNo1");

		msgHandlerMock.when(() -> MessagesHandler.getString(anyString(), any(Locale.class), any()))
				.then(createMsgAnswer(0));

		resHandlerMock.when(() -> ResourcesHandler.getString(anyString())).then(createMsgAnswer(0));
	}

	@AfterEach
	void tearDown() throws Exception {
		msgHandlerMock.close();
		resHandlerMock.close();
	}

	@Test
	@DisplayName("全ての項目をマッピングする")
	void test1() throws Exception {
		String id = "id";
		String str = "abcd";
		Date date = new Date();

		DataDefine d1 = createDataDefine(0, "checkRequire", FormatId.String);
		DataDefine d2 = createDataDefine(1, "checkLength", FormatId.String);
		DataDefine d3 = createDataDefine(2, "checkDate", FormatId.Date);
		List<DataDefine> dataDefineList = createList(d1, d2, d3);

		TestSendRecvDataData data = new TestSendRecvDataData();
		data.setCheckRequire(id);
		data.setCheckLength(str);
		data.setCheckDate(date);
		List<SendRecvDataBean> sendRecvDataBeanList = createList(data);

		List<Map<String, String>> mapList = job.process(sendRecvDataBeanList, dataDefineList);

		assertEquals(1, mapList.size());

		Map<String, String> map = mapList.get(0);
		assertEquals(3, map.size());
		assertEquals(id, map.get("checkRequire"));
		assertEquals(str, map.get("checkLength"));

		String strDate = ConverterUtils.dateTimeToString(data.getCheckDate(), d3.getDate_format());
		assertEquals(strDate, map.get("checkDate"));
	}

	@Test
	@DisplayName("存在しない項目があった場合はマッピングエラーとする")
	void test2() throws Exception {
		try {
			String id = "id";

			DataDefine d1 = createDataDefine(0, "checkRequire", FormatId.String);
			DataDefine d2 = createDataDefine(1, "checkNotExistItem", FormatId.String);
			List<DataDefine> dataDefineList = createList(d1, d2);

			TestSendRecvDataData data = new TestSendRecvDataData();
			data.setCheckRequire(id);
			List<SendRecvDataBean> sendRecvDataBeanList = createList(data);

			job.process(sendRecvDataBeanList, dataDefineList);
			fail("Excetpion test failed.");
		} catch (GnomesAppException e) {
			assertEquals(GnomesMessagesConstants.ME01_0134, e.getMessageNo());
		}

	}

	@Test
	@DisplayName("無効な項目はマッピングしない")
	void test3() throws Exception {
		String id = "id";
		String str = "abcd";

		DataDefine d1 = createDataDefine(0, "checkRequire", FormatId.String);
		DataDefine d2 = createDataDefine(1, "checkLength", FormatId.String);
		d2.setIsdata_item_id(ExternalIfIsDataItemId.INVALID.getValue());
		List<DataDefine> dataDefineList = createList(d1, d2);

		TestSendRecvDataData data = new TestSendRecvDataData();
		data.setCheckRequire(id);
		data.setCheckLength(str);
		List<SendRecvDataBean> sendRecvDataBeanList = createList(data);

		List<Map<String, String>> mapList = job.process(sendRecvDataBeanList, dataDefineList);

		assertEquals(1, mapList.size());

		Map<String, String> map = mapList.get(0);
		assertEquals(2, map.size());
		assertEquals(id, map.get("checkRequire"));
		assertEquals(StringUtils.EMPTY, map.get("checkLength"));
	}

	private static DataDefine createDataDefine(int num, String dataItemId, FormatId formatId) {
		DataDefine df = new DataDefine();
		df.setIsdata_item_id(ExternalIfIsDataItemId.VALID.getValue());
		df.setData_item_number(num);
		df.setData_item_id(dataItemId);
		df.setFormat_id(formatId.getValue());
		df.setDate_format("yyyy-MM-dd HH:mm");
		return df;
	}

	private static <T> List<T> createList(T... ds) {
		List<T> list = new ArrayList<>();
		for (T d : ds) {
			list.add(d);
		}
		return list;
	}

	private static Answer<String> createMsgAnswer(int argIndex) {
		return (Answer<String>) v -> {
			Object[] args = v.getArguments();
			String result = (String) args[argIndex];
			return result;
		};
	}

}
