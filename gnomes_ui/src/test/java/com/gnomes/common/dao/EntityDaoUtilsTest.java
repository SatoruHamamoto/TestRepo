package com.gnomes.common.dao;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.gnomes.external.entity.ExtIfDataSrActual;
import com.gnomes.system.entity.Message;
import com.gnomes.uiservice.ContainerRequest;

public class EntityDaoUtilsTest
{
    @InjectMocks
    EntityDaoUtils entityDaoUtils;

    ContainerRequest req;

    Message message;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        req = new ContainerRequest();
        req.setEventId("testEventId");
        req.setUserId("testUserId");
        req.setUserName("testUserName");

        message = new Message();
        message.setReq(req);
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    @DisplayName("BaseEntityの共通フィールドを取得")
    void test_prePersist() throws Exception {
        entityDaoUtils.prePersist(message);
        assertEquals("testEventId", message.getFirst_regist_event_id());
        assertEquals("testUserId", message.getFirst_regist_user_number());
        assertEquals("testUserName", message.getFirst_regist_user_name());
        assertNotNull(message.getFirst_regist_datetime());
        assertEquals("testEventId", message.getLast_regist_event_id());
        assertEquals("testUserId", message.getLast_regist_user_number());
        assertEquals("testUserName", message.getLast_regist_user_name());
        assertNotNull(message.getLast_regist_datetime());
    }

    @Test
    @DisplayName("ContainerRequestがnullの場合、共通フィールドは取得できない")
    void test_prePersist_ReqIsNull() throws Exception {
        message.setReq(null);
        entityDaoUtils.prePersist(message);
        assertNull(message.getFirst_regist_event_id());
        assertNull(message.getFirst_regist_user_number());
        assertNull(message.getFirst_regist_user_name());
        assertNull(message.getFirst_regist_datetime());
        assertNull(message.getLast_regist_event_id());
        assertNull(message.getLast_regist_user_number());
        assertNull(message.getLast_regist_user_name());
        assertNull(message.getLast_regist_datetime());
    }

    @Test
    @DisplayName("Entityからテーブル名を取得")
    void test_getTableNameFromEntity() throws Exception {
        String tableName = entityDaoUtils.getTableNameFromEntity(message);
        assertEquals("message", tableName);
    }

    @Test
    @DisplayName("Entityが履歴登録対象でないことチェック")
    void test_historyRegisterCheck_false() throws Exception {
        boolean result = entityDaoUtils.historyRegisterCheck(message);
        assertFalse(result);
    }

    @Test
    @DisplayName("Entityが履歴登録対象であることをチェック")
    void test_historyRegisterCheck_true() throws Exception {
        boolean result = entityDaoUtils.historyRegisterCheck(new ExtIfDataSrActual());
        assertTrue(result);
    }
}
