package com.gnomes.external.logic.talend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

import com.gnomes.TestUtil;
import com.gnomes.common.constants.CommonEnums;
import com.gnomes.common.constants.CommonEnums.ExternalIfIsDataItemId;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.external.data.DataDefine;
import com.gnomes.external.data.FileTransferBean;
import com.gnomes.external.data.SendRecvDataBean;
import com.gnomes.system.entity.MstrMessageDefine;

/**
 * ビーンバリデーションテスト
 *
 * @author 30041979
 *
 */
class BeanValidationJobTest {

	private BeanValidationJob job;

	private FileTransferBean fileTransferBean;

	private GnomesExceptionFactory exceptionFactory;

	private MockedStatic<MessagesHandler> msgHandlerMock;

	private MockedStatic<ResourcesHandler> resHandlerMock;

	@BeforeEach
	void setUp() throws Exception {
		msgHandlerMock = Mockito.mockStatic(MessagesHandler.class);
		resHandlerMock = Mockito.mockStatic(ResourcesHandler.class);

		job = TestUtil.createBean(BeanValidationJob.class);

		fileTransferBean = new FileTransferBean();
		Whitebox.setInternalState(job, "fileTransferBean", fileTransferBean);

		exceptionFactory = new GnomesExceptionFactory();
		Whitebox.setInternalState(job, "exceptionFactory", exceptionFactory);

		MstrMessageDefine md = new MstrMessageDefine();
		md.setMessage_no("testNo1");
		doReturn(md).when(job.mstrMessageDefineDao).getMstrMessageDefine(anyString());

		msgHandlerMock.when(() -> MessagesHandler.getString(anyString(), any(Locale.class), any()))
				.then(createMsgAnswer(0));

	}

	@AfterEach
	void tearDown() throws Exception {
		msgHandlerMock.close();
		resHandlerMock.close();
	}

	@Test
	@DisplayName("送受信データとテータ定義が正しくセットされているかを確認する")
	void test1() throws Exception {
		List<SendRecvDataBean> sendRecvDataBeanList = new ArrayList<>();
		List<DataDefine> dataDefineList = new ArrayList<>();

		job.process(sendRecvDataBeanList, dataDefineList);

		assertEquals(sendRecvDataBeanList, fileTransferBean.getSendRecvDataBeanList());
		assertEquals(dataDefineList, fileTransferBean.getDataDefine());

	}

	@Test
	@DisplayName("必須チェックを行う")
	void test21() throws Exception {
		try {
			DataDefine d1 = new DataDefine();
			d1.setIsdata_item_id(ExternalIfIsDataItemId.VALID.getValue());
			d1.setData_check(0);
			d1.setData_item_number(0);
			d1.setData_item_id("checkRequire");
			d1.setLength_check(1);

			fileTransferBean.setDataDefine(createList(d1));

			TestSendRecvDataBase t1 = new TestSendRecvDataBase();
			t1.setCheckRequire("A");
			t1.setCheckList(createSortedMap(d1));
			TestSendRecvDataBase t2 = new TestSendRecvDataBase();
			t2.setCheckRequire("");
			t2.setCheckList(createSortedMap(d1));

			fileTransferBean.setSendRecvDataBeanList(createList(t1, t2));

			job.process();
		} catch (Exception e) {
			assertTrue(e instanceof GnomesAppException);
			assertEquals(1, fileTransferBean.getErrorLineInfo().size());
			assertTrue(fileTransferBean.getErrorLineInfo().containsKey(2));
		}

	}

	@Test
	@DisplayName("必須チェックOFFの場合、チェックしない")
	void test22() throws Exception {
		DataDefine d2 = new DataDefine();
		d2.setIsdata_item_id(ExternalIfIsDataItemId.VALID.getValue());
		d2.setData_check(1);
		d2.setData_item_number(0);
		d2.setData_item_id("checkRequire");
		d2.setLength_check(1);

		fileTransferBean.setDataDefine(createList(d2));

		TestSendRecvDataBase t3 = new TestSendRecvDataBase();
		t3.setCheckRequire("");
		t3.setCheckList(createSortedMap(d2));

		fileTransferBean.setSendRecvDataBeanList(createList(t3));

		job.process();

		assertTrue(fileTransferBean.getErrorLineInfo().isEmpty());

	}

	@Test
	@DisplayName("データ長をチェックする")
	void test31() throws Exception {
		try {
			DataDefine d1 = new DataDefine();
			d1.setLength_check(0);
			d1.setIsdata_item_id(ExternalIfIsDataItemId.VALID.getValue());
			d1.setIsdata_item_id(0);
			d1.setData_item_number(0);
			d1.setData_item_id("checkLength");
			d1.setData_length(2);

			fileTransferBean.setDataDefine(createList(d1));

			TestSendRecvDataBase t1 = new TestSendRecvDataBase();
			t1.setCheckLength("a");
			t1.setCheckList(createSortedMap(d1));
			TestSendRecvDataBase t2 = new TestSendRecvDataBase();
			t2.setCheckLength("ab");
			t2.setCheckList(createSortedMap(d1));
			TestSendRecvDataBase t3 = new TestSendRecvDataBase();
			t3.setCheckLength("abc");
			t3.setCheckList(createSortedMap(d1));
			fileTransferBean.setSendRecvDataBeanList(createList(t1, t2, t3));

			job.process();
		} catch (Exception e) {
			assertTrue(e instanceof GnomesAppException);
			assertEquals(1, fileTransferBean.getErrorLineInfo().size());
			assertTrue(fileTransferBean.getErrorLineInfo().containsKey(3));
		}
	}

	@Test
	@DisplayName("データ長チェックOFFの場合、チェックしない")
	void test32() throws Exception {
		DataDefine d2 = new DataDefine();
		d2.setLength_check(1);
		d2.setIsdata_item_id(ExternalIfIsDataItemId.VALID.getValue());
		d2.setIsdata_item_id(0);
		d2.setData_item_number(0);
		d2.setData_item_id("checkLength");
		d2.setData_length(2);

		fileTransferBean.setDataDefine(createList(d2));

		TestSendRecvDataBase t4 = new TestSendRecvDataBase();
		t4.setCheckLength("abc");
		t4.setCheckList(createSortedMap(d2));

		fileTransferBean.setSendRecvDataBeanList(createList(t4));

		job.process();

		assertTrue(fileTransferBean.getErrorLineInfo().isEmpty());

	}

	@Test
	@DisplayName("Date型はデータ長をチェックしない")
	void test4() throws Exception {
		DataDefine d1 = new DataDefine();
		d1.setFormat_id(CommonEnums.FormatId.Date.getValue());
		d1.setIsdata_item_id(ExternalIfIsDataItemId.VALID.getValue());
		d1.setData_item_number(0);
		d1.setData_item_id("checkDate");
		d1.setLength_check(0);
		d1.setData_length(2);

		fileTransferBean.setDataDefine(createList(d1));

		TestSendRecvDataData t1 = new TestSendRecvDataData();
		Date now = new Date();
		t1.setCheckDate(new Date());
		t1.setCheckList(createSortedMap(d1));

		fileTransferBean.setSendRecvDataBeanList(createList(t1));

		job.process();

		assertTrue(fileTransferBean.getErrorLineInfo().isEmpty());

	}

	@Test
	@DisplayName("処理対象外の項目はチェックしない")
	void test5() throws Exception {
		DataDefine d1 = new DataDefine();
		d1.setIsdata_item_id(ExternalIfIsDataItemId.VALID.getValue());
		d1.setData_item_number(0);
		d1.setData_item_id("checkRequire");
		d1.setData_length(3);

		DataDefine d2 = new DataDefine();
		d2.setIsdata_item_id(ExternalIfIsDataItemId.INVALID.getValue());
		d2.setData_item_number(1);
		d2.setData_item_id("checkLength");
		d2.setData_length(3);

		fileTransferBean.setDataDefine(createList(d1, d2));

		TestSendRecvDataBase t1 = new TestSendRecvDataBase();
		t1.setCheckRequire("A");
		t1.setCheckLength("abcd");
		t1.setCheckList(createSortedMap(d1, d2));
		fileTransferBean.setSendRecvDataBeanList(createList(t1));

		job.process();

		assertTrue(fileTransferBean.getErrorLineInfo().isEmpty());

	}

	private static <T> List<T> createList(T... ds) {
		List<T> list = new ArrayList<>();
		for (T d : ds) {
			list.add(d);
		}
		return list;
	}

	private static <T> LinkedHashMap createSortedMap(T... ds) {
		LinkedHashMap<Integer, T> map = new LinkedHashMap<>();
		for (int i = 0; i < ds.length; i++) {
			map.put(i, ds[i]);
		}
		return map;
	}

	private static Answer<String> createMsgAnswer(int argIndex) {
		return (Answer<String>) v -> {
			Object[] args = v.getArguments();
			String result = (String) args[argIndex];
			return result;
		};
	}

}
