package com.gnomes.external.logic.talend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import com.gnomes.TestUtil;
import com.gnomes.common.constants.CommonEnums.SendRecvStateType;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.external.data.FileTransferBean;
import com.gnomes.external.entity.ExternalIfSendDataDetail;
import com.gnomes.external.entity.QueueExternalIfSendStatus;

/**
 * 送信状態取得ジョブテスト
 * 
 * @author 30041979
 *
 */
class GetSendStateJobTest {

	FileTransferBean fileTransferBean;

	GetSendStateJob job;

	EntityManager entityManager;

	@BeforeEach
	void setUp() throws Exception {
		job = TestUtil.createBean(GetSendStateJob.class);

		fileTransferBean = new FileTransferBean();
		fileTransferBean.setExternalTargetCode("testExternalTargetCode");
		Whitebox.setInternalState(job, "fileTransferBean", fileTransferBean);

		Whitebox.setInternalState(job, "exceptionFactory", new GnomesExceptionFactory());

		entityManager = mock(EntityManager.class);
		doReturn(entityManager).when(job.em).getEntityManager();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	@DisplayName("送信状態データが存在しない場合はエラーが発生する")
	void testProcess1() throws Exception {
		List<QueueExternalIfSendStatus> sendStateList = new ArrayList<>();
		doReturn(sendStateList).when(job.queueExternalIfSendStatusDao).getSendStateQuery(
				SendRecvStateType.Request.getValue(), fileTransferBean.getExternalTargetCode(), entityManager);

		try {
			job.process();
			fail("Excetpion test failed.");
		} catch (Exception e) {
			assertTrue(e instanceof GnomesAppException);
			String msgNo = ((GnomesAppException) e).getMessageNo();
			assertEquals(GnomesMessagesConstants.ME01_0116, msgNo);
		}

	}

	@Test
	@DisplayName("送信状態データを取得する")
	void testProcess2() throws Exception {

		Set<ExternalIfSendDataDetail> dataDetailSet = new HashSet<>();
		ExternalIfSendDataDetail dataDetail = new ExternalIfSendDataDetail();
		dataDetail.setFile_type("testFileType");
		dataDetailSet.add(dataDetail);

		QueueExternalIfSendStatus sendStatus = new QueueExternalIfSendStatus();
		sendStatus.setRequest_seq(1);
		sendStatus.setExternalIfSendDataDetails(dataDetailSet);
		List<QueueExternalIfSendStatus> sendStateList = new ArrayList<>();
		sendStateList.add(sendStatus);

		doReturn(sendStateList).when(job.queueExternalIfSendStatusDao).getSendStateQuery(
				SendRecvStateType.Request.getValue(), fileTransferBean.getExternalTargetCode(), entityManager);

		job.process();

		verify(job.queueExternalIfSendStatusDao, Mockito.times(1)).getSendStateQuery(
				SendRecvStateType.Request.getValue(), fileTransferBean.getExternalTargetCode(), entityManager);
		assertEquals(sendStateList, fileTransferBean.getSendStateList());

	}

}
