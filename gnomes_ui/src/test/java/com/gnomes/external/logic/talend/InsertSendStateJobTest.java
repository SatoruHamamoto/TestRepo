package com.gnomes.external.logic.talend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import com.gnomes.TestUtil;
import com.gnomes.common.constants.CommonEnums.SendRecvStateType;
import com.gnomes.external.data.FileDefine;
import com.gnomes.external.data.FileTransferBean;
import com.gnomes.external.entity.QueueExternalIfSendStatus;

/**
 * 外部I/F送信状態登録テスト
 *
 * @author 30041979
 *
 */
class InsertSendStateJobTest {

	private FileTransferBean fileTransferBean;

	private InsertSendStateJob job;

	@BeforeEach
	void setUp() throws Exception {
		job = TestUtil.createBean(InsertSendStateJob.class);

		fileTransferBean = new FileTransferBean();
		Whitebox.setInternalState(job, "fileTransferBean", fileTransferBean);
	}

	@Test
	@DisplayName("外部I/F送信状態を登録する")
	void testProcess() {
		String extTargetCode = "testExtTargetCode";
		String dataTypeName = "testDataTypeName";
		String callClassName = "testCallClassName";
		String fileType = "testFileType";
		String sendRecvFileName = "testSendRecvFileName";
		int seq = 1;
		FileDefine fileDefine = new FileDefine();
		fileTransferBean.setFileDefine(fileDefine);
		fileDefine.setExt_target_code(extTargetCode);
		fileDefine.setData_type_name(dataTypeName);
		fileDefine.setCall_class_name(callClassName);
		fileTransferBean.setRequestSeq(1);
		fileTransferBean.setFileType(fileType);
		fileTransferBean.setSendRecvFileName(sendRecvFileName);
		fileTransferBean.setStatus(SendRecvStateType.Request);

		job.process();

		verify(job.queueExternalIfSendStatusDao, Mockito.times(1)).insert(any(), any());

		QueueExternalIfSendStatus data = fileTransferBean.getQueueExternalIfSendStatus();
		assertEquals(seq, data.getRequest_seq());
		assertEquals(fileType, data.getFile_type());
		assertEquals(extTargetCode, data.getExternal_if_target_code());
		assertEquals(dataTypeName, data.getFile_name());
		assertEquals(sendRecvFileName, data.getSend_file_name());
		assertEquals(callClassName, data.getCall_class_name());
		assertEquals(SendRecvStateType.Request.getValue(), data.getSend_status());

	}

}
