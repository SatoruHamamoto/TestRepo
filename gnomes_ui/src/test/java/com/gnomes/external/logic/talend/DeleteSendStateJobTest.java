package com.gnomes.external.logic.talend;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import com.gnomes.TestUtil;
import com.gnomes.external.data.FileTransferBean;
import com.gnomes.external.entity.QueueExternalIfSendStatus;

/**
 * 外部I/F送信状態削除テスト
 * 
 * @author 30041979
 *
 */
class DeleteSendStateJobTest {

	@Test
	@DisplayName("外部I/F送信状態を削除する")
	void test() {
		DeleteSendStateJob job = TestUtil.createBean(DeleteSendStateJob.class);

		FileTransferBean fileTransferBean = new FileTransferBean();
		Whitebox.setInternalState(job, "fileTransferBean", fileTransferBean);

		QueueExternalIfSendStatus sendStatus = new QueueExternalIfSendStatus();
		fileTransferBean.setQueueExternalIfSendStatus(sendStatus);

		job.process();

		verify(job.queueExternalIfSendStatusDao, Mockito.times(1)).delete(sendStatus, fileTransferBean.getEml());
	}

}
