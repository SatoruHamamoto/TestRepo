package com.gnomes.common.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.gnomes.system.entity.Message;

public class JdbcAccessUtilTest
{
    @InjectMocks
    JdbcAccessUtil jdbcAccessUtil;

    Message message;
    List<String> columns;
    List<Field> fields;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        message = new Message();
        columns = new ArrayList<>();
        fields = new ArrayList<>();
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    @DisplayName("Entity定義のカラム情報を取得する")
    void test_getEntityColumnNameInfoList() throws Exception {
        jdbcAccessUtil.getEntityColumnNameInfoList(message.getClass(), columns, fields);
        assertEquals(columns.size(), fields.size());
        for (int i = 0; i < columns.size(); i++) {
            assertEquals(columns.get(i), fields.get(i).getName());
        }
    }

    @Test
    @DisplayName("Entity定義スーパークラスのカラム情報を取得する")
    void test_getEntityColumnNameInfoList_superClass() throws Exception {
        jdbcAccessUtil.getEntityColumnNameInfoList(message.getClass().getSuperclass(), columns, fields);
        assertEquals(columns.size(), fields.size());
        for (int i = 0; i < columns.size(); i++) {
            assertEquals(columns.get(i), fields.get(i).getName());
        }
    }

    @Test
    @DisplayName("カラム情報を元にInsert文を生成する")
    void test_akeInsertSQLString() throws Exception {
        jdbcAccessUtil.getEntityColumnNameInfoList(message.getClass(), columns, fields);
        String sql = jdbcAccessUtil.makeInsertSQLString("message", columns);
        assertTrue(sql.startsWith("insert into message (message_key,")); // 全文は冗長なので先頭部分のみチェック
    }

    @Test
    @DisplayName("カラム名を元にDelete文を生成する")
    void test_makeDeleteSQLString() throws Exception {
        jdbcAccessUtil.getEntityColumnNameInfoList(message.getClass(), columns, fields);
        String sql = jdbcAccessUtil.makeDeleteSQLString("message", "message_key");
        assertEquals("delete from message where message_key = ?", sql);
    }
}
