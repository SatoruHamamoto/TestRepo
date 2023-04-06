package com.gnomes.external.logic.talend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import com.gnomes.TestUtil;
import com.gnomes.common.constants.CommonEnums.SendRecvStateType;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.external.dao.ExternalIfSendFileSeqNoDao;
import com.gnomes.external.data.FileDefine;
import com.gnomes.external.data.FileTransferBean;
import com.gnomes.external.entity.ExternalIfSendFileSeqNo;

/**
 * 送信ファイル名作成ジョブテスト
 *
 * @author 30041979
 *
 */
class CreateFileNameJobTest {

	private CreateFileNameJob job;

	private FileTransferBean fileTransferBean;

	private MockedStatic<MessagesHandler> msgHandlerMock;

	private MockedStatic<ResourcesHandler> resHandlerMock;

	private ExternalIfSendFileSeqNoDao externalIfSendFileSeqNoDao;

	private GnomesExceptionFactory exceptionFactory;

	@BeforeEach
	void setUp() throws Exception {
		msgHandlerMock = Mockito.mockStatic(MessagesHandler.class);
		resHandlerMock = Mockito.mockStatic(ResourcesHandler.class);

		job = TestUtil.createBean(CreateFileNameJob.class);

		fileTransferBean = new FileTransferBean();
		Whitebox.setInternalState(job, "fileTransferBean", fileTransferBean);

		exceptionFactory = new GnomesExceptionFactory();
		Whitebox.setInternalState(job, "exceptionFactory", exceptionFactory);

		externalIfSendFileSeqNoDao = job.externalIfSendFileSeqNoDao;
	}

	@AfterEach
	void tearDown() throws Exception {
		msgHandlerMock.close();
		resHandlerMock.close();
	}

	@Test
	@DisplayName("外部送信ファイル連番管理データがない場合はエラーが発生する")
	void testProcess1() {
		try {
			job.process();
			fail("Excetpion test failed.");
		} catch (Exception e) {
			assertTrue(e instanceof GnomesAppException);
			String msgNo = ((GnomesAppException) e).getMessageNo();
			assertEquals(GnomesMessagesConstants.ME01_0111, msgNo);

			assertEquals(SendRecvStateType.FailedCreateFile, fileTransferBean.getStatus());
			assertEquals(2, fileTransferBean.getProcType());
		}
	}

	@Test
	@DisplayName("外部送信ファイル連番上限を超過していない場合、「現在番号+1」でデータを登録する")
	void testProcess2() throws Exception {
		ExternalIfSendFileSeqNo externalSendFileSeqNo = new ExternalIfSendFileSeqNo();
		externalSendFileSeqNo.setMax_seq(100);
		externalSendFileSeqNo.setSeq(1);
		doReturn(externalSendFileSeqNo).when(externalIfSendFileSeqNoDao).getExternalIfSendFileSeqNoQuery(any());

		FileDefine fileDefine = new FileDefine();
		fileDefine.setFile_name("TestFileName");
		fileTransferBean.setFileDefine(fileDefine);

		job.process();

		assertEquals("TestFileName.0002", fileTransferBean.getSendRecvFileName());

		assertEquals(2, externalSendFileSeqNo.getSeq());
		verify(externalIfSendFileSeqNoDao, Mockito.times(1)).update(externalSendFileSeqNo);
	}

	@Test
	@DisplayName("外部送信ファイル連番上限を超過した場合、番号を振り直してから登録する")
	void testProcess3() throws Exception {
		ExternalIfSendFileSeqNo externalSendFileSeqNo = new ExternalIfSendFileSeqNo();
		externalSendFileSeqNo.setMax_seq(100);
		externalSendFileSeqNo.setMin_seq(1);
		externalSendFileSeqNo.setSeq(100);
		doReturn(externalSendFileSeqNo).when(externalIfSendFileSeqNoDao).getExternalIfSendFileSeqNoQuery(any());

		FileDefine fileDefine = new FileDefine();
		fileDefine.setFile_name("TestFileName");
		fileTransferBean.setFileDefine(fileDefine);

		job.process();

		assertEquals("TestFileName.0002", fileTransferBean.getSendRecvFileName());

		assertEquals(2, externalSendFileSeqNo.getSeq());
		verify(externalIfSendFileSeqNoDao, Mockito.times(1)).update(externalSendFileSeqNo);
	}

}
