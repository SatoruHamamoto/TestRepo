package com.gnomes.external.logic.talend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

import com.gnomes.TestUtil;
import com.gnomes.common.constants.CommonEnums.FileFormat;
import com.gnomes.common.constants.CommonEnums.FormatId;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.external.data.DataDefine;
import com.gnomes.external.data.FileDefine;
import com.gnomes.external.data.FileTransferBean;
import com.gnomes.external.data.SystemDefine;
import com.gnomes.system.entity.MstrMessageDefine;

/**
 * 送信伝文データ変換テスト
 * 
 * @author 30018232
 *
 */
class SendDataConvJobTest {

    private FileTransferBean fileTransferBean;

    private GnomesExceptionFactory exceptionFactory;

    private MockedStatic<MessagesHandler> msgHandlerMock;

    private MockedStatic<ResourcesHandler> resHandlerMock;

    private SendDataConvJob convJob;

    @BeforeEach
    void setUp() throws Exception {
        msgHandlerMock = Mockito.mockStatic(MessagesHandler.class);
        resHandlerMock = Mockito.mockStatic(ResourcesHandler.class);

        convJob = TestUtil.createBean(SendDataConvJob.class);

        fileTransferBean = new FileTransferBean();
        SystemDefine systemDefine = new SystemDefine();
        systemDefine.setTime_zone(TimeZone.getDefault().toString());
        fileTransferBean.setSystemDefine(systemDefine);

        exceptionFactory = new GnomesExceptionFactory();
        Whitebox.setInternalState(convJob, "exceptionFactory", exceptionFactory);

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
    @DisplayName("CSVの分解_成功")
    void test1() throws Exception {
        FileDefine fileDefine = createFileDefineCSV();
        List<DataDefine> dataDefineList = createDataDefineList();
        List<Map<String, String>> sendDataMappingList = createSendDataMappingList();

        List<String> sendStringList = convJob.process(sendDataMappingList, fileDefine, dataDefineList);
        assertEquals("\"200.200\",\"TEST1_VALUE_1-2\",\"TEST1_VALUE_1-3\",\"Fix_4\",\"TEST1_VALUE_1-5\",\"600\"", sendStringList.get(0));

    }

    @Test
    @DisplayName("固定長の分解_成功")
    void test2_1() throws Exception {
        FileDefine fileDefine = createFileDefineFIX();
        List<DataDefine> dataDefineList = createDataDefineList();
        List<Map<String, String>> sendDataMappingList = createSendDataMappingList();

        List<String> sendStringList = convJob.process(sendDataMappingList, fileDefine, dataDefineList);
        assertEquals("00000000200.200TEST1_VALUE_1-2TEST1_VALUE_1-3Fix_4          TEST1_VALUE_1-5600            ", sendStringList.get(0));

    }
    
    @Test
    @DisplayName("固定長の分解_文字列長すぎエラー")
    void test2_2() throws Exception {
        FileDefine fileDefine = createFileDefineFIXShort();
        List<DataDefine> dataDefineList = createDataDefineList();
        List<Map<String, String>> sendDataMappingList = createSendDataMappingList();

        GnomesAppException e = assertThrows(GnomesAppException.class, () -> convJob.process(sendDataMappingList, fileDefine, dataDefineList));
        assertEquals("ME01.0164", e.getMessageNo());
    }

    @Test
    @DisplayName("XMLの分解_非対応エラー")
    void test3() throws Exception {
        FileDefine fileDefine = createFileDefineXML();

        GnomesAppException e = assertThrows(GnomesAppException.class, () -> convJob.process(null, fileDefine, null));
        assertEquals("ME01.0256", e.getMessageNo());
    }

    private static Answer<String> createMsgAnswer(int argIndex) {
        return (Answer<String>) v -> {
            Object[] args = v.getArguments();
            String result = (String) args[argIndex];
            return result;
        };
    }
    private static FileDefine createFileDefineCSV() {
        FileDefine fileDefine = new FileDefine();
        fileDefine.setFile_format((FileFormat.Csv.getValue()));
        fileDefine.setFile_type("TestFileType1");
        fileDefine.setFile_name("TestFileName1");
        fileDefine.setCsv_delimiter(",");
        fileDefine.setChar_bundle("\"");
        fileDefine.setDecimal_length(3);

        return fileDefine;
    }
    private static FileDefine createFileDefineFIX() {
        FileDefine fileDefine = new FileDefine();
        fileDefine.setFile_format((FileFormat.FixedLength.getValue()));
        fileDefine.setFile_type("TestFileType1");
        fileDefine.setFile_name("TestFileName1");
        fileDefine.setCsv_delimiter(",");
        fileDefine.setChar_bundle("\"");
        fileDefine.setDecimal_length(3);
        fileDefine.setData_length(90);

        return fileDefine;
    }
    private static FileDefine createFileDefineFIXShort() {
        FileDefine fileDefine = new FileDefine();
        fileDefine.setFile_format((FileFormat.FixedLength.getValue()));
        fileDefine.setFile_type("TestFileType1");
        fileDefine.setFile_name("TestFileName1");
        fileDefine.setCsv_delimiter(",");
        fileDefine.setChar_bundle("\"");
        fileDefine.setDecimal_length(3);
        fileDefine.setData_length(89);

        return fileDefine;
    }
    private static FileDefine createFileDefineXML() {
        FileDefine fileDefine = new FileDefine();
        fileDefine.setFile_format((FileFormat.Xml.getValue()));
        fileDefine.setFile_type("TestFileType1");
        fileDefine.setFile_name("TestFileName1");

        return fileDefine;
    }

    private static List<DataDefine> createDataDefineList() {
        List<DataDefine> dataDefineList = new ArrayList<DataDefine>();
        DataDefine dataDefine1 = new DataDefine();
        dataDefine1.setData_item_id("Test_key_1-1");
        dataDefine1.setFormat_id(FormatId.BigDecimal.getValue());
        dataDefine1.setFixed_value_string("Fix_1");
        dataDefine1.setData_length(15);
        dataDefineList.add(dataDefine1);

        DataDefine dataDefine2 = new DataDefine();
        dataDefine2.setData_item_id("Test_key_1-2");
        dataDefine2.setFormat_id(FormatId.String.getValue());
        dataDefine2.setFixed_value_string("Fix_2");
        dataDefine2.setData_length(15);
        dataDefineList.add(dataDefine2);

        DataDefine dataDefine3 = new DataDefine();
        dataDefine3.setData_item_id("Test_key_1-3");
        dataDefine3.setFormat_id(FormatId.Date.getValue());
        dataDefine3.setFixed_value_string("Fix_3");
        dataDefine3.setData_length(15);
        dataDefineList.add(dataDefine3);

        DataDefine dataDefine4 = new DataDefine();
        dataDefine4.setData_item_id("Test_key_1-4");
        dataDefine4.setFormat_id(FormatId.FixedValue.getValue());
        dataDefine4.setFixed_value_string("Fix_4");
        dataDefine4.setData_length(15);
        dataDefineList.add(dataDefine4);

        DataDefine dataDefine5 = new DataDefine();
        dataDefine5.setData_item_id("Test_key_1-5");
        dataDefine5.setFormat_id(FormatId.Integer.getValue());
        dataDefine5.setFixed_value_string("Fix_5");
        dataDefine5.setData_length(15);
        dataDefineList.add(dataDefine5);

        DataDefine dataDefine6 = new DataDefine();
        dataDefine6.setData_item_id("Test_key_1-6");
        dataDefine6.setFormat_id(FormatId.String.getValue());
        dataDefine6.setFixed_value_string("Fix_6");
        dataDefine6.setData_length(15);
        dataDefineList.add(dataDefine6);

        return dataDefineList;
    }

    private static List<Map<String, String>> createSendDataMappingList() {
        List<Map<String, String>> sendDataMappingList = new ArrayList<Map<String, String>>();
        Map<String, String> dataMapping = new HashMap<>();
        dataMapping.put("Test_key_1-1", "200.2");
        dataMapping.put("Test_key_1-2", "TEST1_VALUE_1-2");
        dataMapping.put("Test_key_1-3", "TEST1_VALUE_1-3");
        dataMapping.put("Test_key_1-4", "TEST1_VALUE_1-4");
        dataMapping.put("Test_key_1-5", "TEST1_VALUE_1-5");
        dataMapping.put("Test_key_1-6", "600");
        dataMapping.put("Test_key_1-7", "TEST1_VALUE_1-7");

        sendDataMappingList.add(dataMapping);

        return sendDataMappingList;
    }

}
