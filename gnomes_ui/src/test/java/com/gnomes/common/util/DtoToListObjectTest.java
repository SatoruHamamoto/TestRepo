package com.gnomes.common.util;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.search.data.OrderingInfo;
import com.gnomes.system.data.A01001FunctionBean;

class DtoToListObjectTest {

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() throws Exception {
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    @DisplayName("CSV出力用LIST作成")
    void test_make_csv_data_with_header() throws GnomesAppException {
        try {
            //変換前値
            List<Object> lstHeader = new ArrayList<Object>();
            List<?> lstDataObject = new ArrayList<>();
            Class<?> clazz = lstDataObject.getClass(); 
            Locale userLocale = Locale.getDefault(); 
            String dictId = ""; 
            List<OrderingInfo> ordInfoLst = new ArrayList<OrderingInfo>(); 
            Map<String, Object> mapColInfoMap = new HashMap<>();
    
            //変換後値
            Method method = DtoToListObject.class.getDeclaredMethod("makeCsvDataWithHeader", 
                    List.class, Class.class, List.class,
                    Locale.class, String.class, List.class, Map.class);
            method.setAccessible(true);
            List<Object> afterList = (List<Object>)method.invoke(lstHeader, clazz, lstDataObject,
                    userLocale, dictId, ordInfoLst, mapColInfoMap);
            //期待値
            List<Object> expectedStr = null;
            //比較
            assertTrue(Objects.equals(afterList, expectedStr) == true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    @DisplayName("プルダウン出力取得")
    void test_get_pulldown_constant() throws GnomesAppException {
        try {
            //変換前値
            Map<String, Object> mapColInfo = new HashMap<>();
            Object data = new A01001FunctionBean();
            Class<?> clsData = data.getClass();
            int rowCnt = 1;
            //変換後値
            Method method = DtoToListObject.class.getDeclaredMethod("getPullDownConstant", Map.class, Class.class, Object.class, int.class);
            method.setAccessible(true);
            String afterStr = (String)method.invoke(mapColInfo, clsData, data, rowCnt);
            //期待値
            String expectedStr = null;
            //比較
            assertTrue(Objects.equals(afterStr, expectedStr) == true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    @DisplayName("プルダウン出力（データ項目）取得")
    void test_get_pulldown_data() throws GnomesAppException {
        try {
            //変換前値
            Map<String, Object> mapColInfo = new HashMap<>();
            Object data = new A01001FunctionBean();
            Class<?> clsData = data.getClass();
            int rowCnt = 1;
            //変換後値
            Method method = DtoToListObject.class.getDeclaredMethod("getPullDownData", Map.class, Class.class, Object.class, int.class);
            method.setAccessible(true);
            String afterStr = (String)method.invoke(mapColInfo, clsData, data, rowCnt);
            //期待値
            String expectedStr = null;
            //比較
            assertTrue(Objects.equals(afterStr, expectedStr) == true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    @DisplayName(" プルダウン出力（データ項目）取得(DataType用")
    void test_get_pulldown_data_data_type() throws GnomesAppException {
        try {
            //変換前値
            Map<String, Object> mapColInfo = new HashMap<>();
            Object data = new A01001FunctionBean();
            Class<?> clsData = data.getClass();
            int rowCnt = 1;
            //変換後値
            Method method = DtoToListObject.class.getDeclaredMethod("getPullDownDataDataType", Map.class, Class.class, Object.class, int.class);
            method.setAccessible(true);
            String afterStr = (String)method.invoke(mapColInfo, clsData, data, rowCnt);
            //期待値
            String expectedStr = null;
            //比較
            assertTrue(Objects.equals(afterStr, expectedStr) == true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
