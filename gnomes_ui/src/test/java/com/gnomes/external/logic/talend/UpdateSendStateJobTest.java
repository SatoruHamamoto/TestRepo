package com.gnomes.external.logic.talend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import com.gnomes.TestUtil;
import com.gnomes.common.constants.CommonEnums.SendRecvStateType;
import com.gnomes.external.data.FileTransferBean;
import com.gnomes.external.entity.QueueExternalIfSendStatus;

/**
 * 外部I/F送信状態更新テスト
 * 
 * @author 30041979
 *
 */
class UpdateSendStateJobTest {

	FileTransferBean fileTransferBean;

	UpdateSendStateJob job;

	@BeforeEach
	void setUp() throws Exception {
		job = TestUtil.createBean(UpdateSendStateJob.class);

		fileTransferBean = new FileTransferBean();
		Whitebox.setInternalState(job, "fileTransferBean", fileTransferBean);
	}

	@Test
	@DisplayName("外部I/F送信状態を更新する")
	void testProcess() {
		QueueExternalIfSendStatus sendStatus = new QueueExternalIfSendStatus();
		fileTransferBean.setQueueExternalIfSendStatus(sendStatus);
		fileTransferBean.setStatus(SendRecvStateType.Request);
		fileTransferBean.setEml(mock(EntityManager.class));

		job.process();
		verify(job.queueExternalIfSendStatusDao, Mockito.times(1)).update(sendStatus, fileTransferBean.getEml());
		assertEquals(SendRecvStateType.Request.getValue(), sendStatus.getSend_status());
	}

}
