package com.gnomes.common.patlamp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.gnomes.common.constants.CommonEnums;
import com.gnomes.common.constants.CommonEnums.SoundPatternData;
import com.gnomes.common.data.TalendJobInfoBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.ptlamp.SendRequestPatlamp;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.util.TalendJobRun;
import com.gnomes.system.dao.MstrPatlampDao;
import com.gnomes.system.dao.MstrPatlampModelDao;
import com.gnomes.system.entity.MstrPatlamp;
import com.gnomes.system.entity.MstrPatlampModel;

public class SendRequestPatlampTest
{
    @InjectMocks
    SendRequestPatlamp sendRequestPatlamp;

    @Mock
    MstrPatlampDao mstrPatlampDao;

    @Mock
    MstrPatlampModelDao mstrPatlampModelDao;

    @Mock
    TalendJobInfoBean talendJobInfoBean;

    @Spy
    GnomesExceptionFactory exceptionFactory;

    MockedStatic<TalendJobRun> talendJobRunMock;

    MockedStatic<ResourcesHandler> resourcesHandlerMock;

    MstrPatlamp mstrPatlamp;

    MstrPatlampModel mstrPatlampModel;

    String patlampId;

    Map<String, Integer> patternData = new HashMap<>();

    SoundPatternData soundPatternData;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        patlampId = "testPatlampId";
        patternData.put(CommonEnums.PatlampColor.RED.getValue(), CommonEnums.lightPatternData.FLASH1.getValue());
        soundPatternData = CommonEnums.SoundPatternData.OFF;

        // パトランプ情報を作成
        mstrPatlamp = new MstrPatlamp();
        Mockito.doReturn(mstrPatlamp).when(mstrPatlampDao).getMstrPatlamp(patlampId);
        mstrPatlamp.setPatlamp_model_id("testPatlampModelId");
        mstrPatlamp.setIp_address("127.0.0.1");
        mstrPatlamp.setLight_sound_parameter_string_01("lightSoundParameterString01");

        // パトランプモデル情報を作成
        mstrPatlampModel = new MstrPatlampModel();
        Mockito.doReturn(mstrPatlampModel).when(mstrPatlampModelDao).getMstrPatlampModel(mstrPatlamp.getPatlamp_model_id());
        mstrPatlampModel.setTalend_job_name("testTalendJobName");

        // TalendJobInfoBean をモック化
        Mockito.doReturn("").when(talendJobInfoBean).getErrorJobName();
        Mockito.doReturn("").when(talendJobInfoBean).getErrorComponentName();

        // TalendJobRun のモック化
        talendJobRunMock = Mockito.mockStatic(TalendJobRun.class);
        talendJobRunMock.when(() -> TalendJobRun.runJob(anyString(), any(), anyBoolean())).thenAnswer(v -> null);

        // ResourcesHandler のモック化
        resourcesHandlerMock = Mockito.mockStatic(ResourcesHandler.class);
        resourcesHandlerMock.when(() -> ResourcesHandler.getString(GnomesResourcesConstants.YY01_0035)).thenReturn(",");
    }

    @AfterEach
    void tearDown() throws Exception {
        talendJobRunMock.close();
        resourcesHandlerMock.close();
    }

    // パターンデータを渡さない方式の lightOn
    @Test
    @DisplayName("パトランプIDが登録されている場合、lightOn でTalendJobRun が正しく呼び出される")
    void test_lightOn_success() throws Exception {
        sendRequestPatlamp.lightOn(patlampId);
        talendJobRunMock.verify(() -> TalendJobRun.runJob(anyString(), any(), anyBoolean()), Mockito.times(1));
    }

    @Test
    @DisplayName("パトランプIDが登録されていない場合、lightOn で例外が返される")
    void test_lightOn_invalidPatlampId() throws Exception {
        patlampId = "testInvalidPatlampId";
        assertThrows(GnomesAppException.class, () -> sendRequestPatlamp.lightOn(patlampId));
        talendJobRunMock.verify(() -> TalendJobRun.runJob(anyString(), any(), anyBoolean()), Mockito.never());
    }

    @Test
    @DisplayName("パトランプモデルIDが登録されていない場合、lightOn で例外が返される")
    void test_lightOn_invalidPatlampModelId() throws Exception {
        mstrPatlamp.setPatlamp_model_id("testInvalidPatlampModelId");
        assertThrows(GnomesAppException.class, () -> sendRequestPatlamp.lightOn(patlampId));
        talendJobRunMock.verify(() -> TalendJobRun.runJob(anyString(), any(), anyBoolean()), Mockito.never());
    }

    @Test
    @DisplayName("TalendJob 名が null の場合、lightOn で例外が返される")
    void test_lightOn_nullTalendJobName() throws Exception {
        mstrPatlampModel.setTalend_job_name(null);
        assertThrows(NullPointerException.class, () -> sendRequestPatlamp.lightOn(patlampId));
        talendJobRunMock.verify(() -> TalendJobRun.runJob(anyString(), any(), anyBoolean()), Mockito.never());
    }

    @Test
    @DisplayName("IP アドレスが null の場合、lightOn で例外は返されない")
    void test_lightOn_nullIpAddress() throws Exception {
        mstrPatlamp.setIp_address(null);
        assertDoesNotThrow(() -> sendRequestPatlamp.lightOn(patlampId));
        talendJobRunMock.verify(() -> TalendJobRun.runJob(anyString(), any(), anyBoolean()), Mockito.times(1));
    }

    @Test
    @DisplayName("TalendJobRun.runJob でエラーが発生した場合、例外が返される")
    void test_lightOn_errorOccured() throws Exception {
        Mockito.doReturn("errorJobName").when(talendJobInfoBean).getErrorJobName();
        Mockito.doReturn("errorComponentName").when(talendJobInfoBean).getErrorComponentName();
        assertThrows(GnomesAppException.class, () -> sendRequestPatlamp.lightOn(patlampId));
        talendJobRunMock.verify(() -> TalendJobRun.runJob(anyString(), any(), anyBoolean()), Mockito.times(1));
    }

    // パターンデータを渡す方式の lightOn
    @Test
    @DisplayName("パトランプIDが登録されていて、パターンデータが妥当な場合、lightOn でTalendJobRun が正しく呼び出される")
    void test_lightOnPattern_success() throws Exception {
        try {
            sendRequestPatlamp.lightOn(patlampId, patternData, soundPatternData);
            talendJobRunMock.verify(() -> TalendJobRun.runJob(anyString(), any(), anyBoolean()), Mockito.times(1));
        } catch (Exception e) {
            e.printStackTrace();
        } catch (ExceptionInInitializerError error) {
            error.getCause().printStackTrace();
            fail(error);
        }
    }

    @Test
    @DisplayName("パトランプIDが登録されていない場合、lightOn で例外が返される")
    void test_lightOnPattern_invalidPatlampId() throws Exception {
        patlampId = "testInvalidPatlampId";
        assertThrows(GnomesAppException.class, () -> sendRequestPatlamp.lightOn(patlampId, patternData, soundPatternData));
        talendJobRunMock.verify(() -> TalendJobRun.runJob(anyString(), any(), anyBoolean()), Mockito.never());
    }

    @Test
    @DisplayName("パトランプモデルIDが登録されていない場合、lightOn で例外が返される")
    void test_lightOnPattern_invalidPatlampModelId() throws Exception {
        mstrPatlamp.setPatlamp_model_id("testInvalidPatlampModelId");
        assertThrows(GnomesAppException.class, () -> sendRequestPatlamp.lightOn(patlampId, patternData, soundPatternData));
        talendJobRunMock.verify(() -> TalendJobRun.runJob(anyString(), any(), anyBoolean()), Mockito.never());
    }

    @Test
    @DisplayName("TalendJob 名が null の場合、lightOn で例外が返される")
    void test_lightOnPattern_nullTalendJobName() throws Exception {
        mstrPatlampModel.setTalend_job_name(null);
        assertThrows(NullPointerException.class, () -> sendRequestPatlamp.lightOn(patlampId, patternData, soundPatternData));
        talendJobRunMock.verify(() -> TalendJobRun.runJob(anyString(), any(), anyBoolean()), Mockito.never());
    }

    @Test
    @DisplayName("IP アドレスが null の場合、lightOn で例外は返されない")
    void test_lightOnPattern_nullIpAddress() throws Exception {
        mstrPatlamp.setIp_address(null);
        assertDoesNotThrow(() -> sendRequestPatlamp.lightOn(patlampId, patternData, soundPatternData));
        talendJobRunMock.verify(() -> TalendJobRun.runJob(anyString(), any(), anyBoolean()), Mockito.times(1));
    }

    @Test
    @DisplayName("TalendJobRun.runJob でエラーが発生した場合、例外が返される")
    void test_lightOnPattern_errorOccured() throws Exception {
        Mockito.doReturn("errorJobName").when(talendJobInfoBean).getErrorJobName();
        Mockito.doReturn("errorComponentName").when(talendJobInfoBean).getErrorComponentName();
        assertThrows(GnomesAppException.class, () -> sendRequestPatlamp.lightOn(patlampId, patternData, soundPatternData));
        talendJobRunMock.verify(() -> TalendJobRun.runJob(anyString(), any(), anyBoolean()), Mockito.times(1));
    }

    // lightOff
    @Test
    @DisplayName("パトランプIDが登録されている場合、lightOff で TalendJobRun が正しく呼び出される")
    void test_lightOff_success() throws Exception {
        sendRequestPatlamp.lightOff(patlampId);
        talendJobRunMock.verify(() -> TalendJobRun.runJob(anyString(), any(), anyBoolean()), Mockito.times(1));
    }

    @Test
    @DisplayName("パトランプIDが登録されていない場合、lightOff で例外が返される")
    void test_lightOff_invalidPatlampId() throws Exception {
        patlampId = "testInvalidPatlampId";
        assertThrows(GnomesAppException.class, () -> sendRequestPatlamp.lightOff(patlampId));
        talendJobRunMock.verify(() -> TalendJobRun.runJob(anyString(), any(), anyBoolean()), Mockito.never());
    }

    @Test
    @DisplayName("パトランプモデルIDが登録されていない場合、lightOff で例外が返される")
    void test_lightOff_invalidPatlampModelId() throws Exception {
        mstrPatlamp.setPatlamp_model_id("testInvalidPatlampModelId");
        assertThrows(GnomesAppException.class, () -> sendRequestPatlamp.lightOff(patlampId));
        talendJobRunMock.verify(() -> TalendJobRun.runJob(anyString(), any(), anyBoolean()), Mockito.never());
    }

    @Test
    @DisplayName("TalendJob 名が null の場合、lightOff で例外が返される")
    void test_lightOff_nullTalendJobName() throws Exception {
        mstrPatlampModel.setTalend_job_name(null);
        assertThrows(NullPointerException.class, () -> sendRequestPatlamp.lightOff(patlampId));
        talendJobRunMock.verify(() -> TalendJobRun.runJob(anyString(), any(), anyBoolean()), Mockito.never());
    }

    @Test
    @DisplayName("IP アドレスが null の場合、lightOff で例外は返されない")
    void test_lightOff_nullIpAddress() throws Exception {
        mstrPatlamp.setIp_address(null);
        assertDoesNotThrow(() -> sendRequestPatlamp.lightOff(patlampId));
        talendJobRunMock.verify(() -> TalendJobRun.runJob(anyString(), any(), anyBoolean()), Mockito.times(1));
    }

    @Test
    @DisplayName("TalendJobRun.runJob でエラーが発生した場合、例外が返される")
    void test_lightOff_errorOccured() throws Exception {
        Mockito.doReturn("errorJobName").when(talendJobInfoBean).getErrorJobName();
        Mockito.doReturn("errorComponentName").when(talendJobInfoBean).getErrorComponentName();
        assertThrows(GnomesAppException.class, () -> sendRequestPatlamp.lightOff(patlampId));
        talendJobRunMock.verify(() -> TalendJobRun.runJob(anyString(), any(), anyBoolean()), Mockito.times(1));
    }
}
