package com.gnomes.rest.service;

import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.powermock.reflect.Whitebox;
import org.springframework.test.util.ReflectionTestUtils;

import com.gnomes.TestUtil;
import com.gnomes.common.constants.CommonConstants;
import com.gnomes.uiservice.ContainerRequest;

class GnomesCommonBatServiceTest {

    private GnomesCommonBatService service;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() throws Exception {
        service = TestUtil.createBean(GnomesCommonBatService.class);
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    @DisplayName("バッチ起動時の共通情報を設定：設定項目確認")
    void test_setCommonBatch() {
        ContainerRequest req = new ContainerRequest();
        Whitebox.setInternalState(service, "req", req);

        ReflectionTestUtils.invokeMethod(service, "setCommonBatch");

        assertEquals(req.getAreaId(), "");
        assertEquals(req.getAreaName(), "");
        assertEquals(req.getComputerName(), "");
        assertEquals(req.getIpAddress(), CommonConstants.IPADDRESS_LOCALHOST);
        assertEquals(req.getLanguage(), Locale.getDefault().getLanguage());
        assertEquals(req.getUserLocale(), Locale.getDefault());
        assertEquals(req.getSiteCode(), "");
        assertEquals(req.getSiteName(), "");
        assertEquals(req.getUserId(), "SYS_BATCH");
        assertEquals(req.getUserName(), CommonConstants.USERNAME_BATCH);
        assertEquals(req.getUserKey(), "");
    }

}
