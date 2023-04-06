package com.gnomes.common.search.query;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.camel.cdi.Mock;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.search.SearchInfoController.ConditionType;
import com.gnomes.common.search.data.ConditionInfo;

class SearchConditionTest {

	@Spy
    @InjectMocks
    SearchCondition searchCondition;
	
	 /** セッションビーン  */
    @Mock
    GnomesSessionBean gnomesSessionBean;
    
    /** 初期タイムゾーン　*/
    private TimeZone defaultTimeZone;
	
    @BeforeAll
    static void setUpBeforeClass() throws Exception {

    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {

    }

    @BeforeEach
    void setUp() throws Exception {
    	defaultTimeZone = TimeZone.getDefault();
    	TimeZone utc = TimeZone.getTimeZone("UTC");
    	TimeZone.setDefault(utc);
    	
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
    	TimeZone.setDefault(defaultTimeZone);
    }

    @Test
    @DisplayName("検索条件パラメータをMapping用Objectに変換:年月日時分")
    void test_convertParameter_DATE_TIME() throws GnomesAppException, DateTimeParseException, ParseException {
    	
    	ConditionInfo info = makeConditionInfo();
    	
    	//日本時間を設定
    	setupDoReturnGnomesSessionBean();
    	Object searchObj = searchCondition.convertParameter(ConditionType.DATE_TIME, info, "2022/01/01 18:15", false);
    	
    	//UTCに変換されているか判定
    	SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	Date date = df.parse("2022/01/01 09:15:00");
    	assertEquals(date, searchObj);
    	
    }
    
    @Test
    @DisplayName("検索条件パラメータをMapping用Objectに変換:年月日時分00")
    void test_convertParameter_DATE_TIME_MM00() throws GnomesAppException, DateTimeParseException, ParseException {
    	
    	
    	ConditionInfo info = makeConditionInfo();
    	
    	//日本時間を設定
    	setupDoReturnGnomesSessionBean();
    	Object searchObj = searchCondition.convertParameter(ConditionType.DATE_TIME_MM00, info, "2022/01/01 18:00", false);
    	
    	//UTCに変換されているか判定
    	SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	Date date = df.parse("2022/01/01 09:00:00");
    	assertEquals(date, searchObj);
    	
    }
    
    
    @Test
    @DisplayName("検索条件パラメータをMapping用Objectに変換:年月日時分秒")
    void test_convertParameter_DATE_TIME_SS() throws GnomesAppException, DateTimeParseException, ParseException {
    	
    	ConditionInfo info = makeConditionInfo();
    	
    	//日本時間を設定
    	setupDoReturnGnomesSessionBean();
    	Object searchObj = searchCondition.convertParameter(ConditionType.DATE_TIME_SS, info, "2022/01/01 18:15:13", false);
    	
    	//UTCに変換されているか判定
    	SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	Date date = df.parse("2022/01/01 09:15:13");
    	assertEquals(date, searchObj);
    	
    }
    
    @Test
    @DisplayName("検索条件パラメータをMapping用Objectに変換:年月日時分秒00")
    void test_convertParameter_DATE_TIME_SS00() throws GnomesAppException, DateTimeParseException, ParseException {
    	
    	ConditionInfo info = makeConditionInfo();
    	
    	//日本時間を設定
    	setupDoReturnGnomesSessionBean();
    	Object searchObj = searchCondition.convertParameter(ConditionType.DATE_TIME_SS00, info, "2022/01/01 18:15:00", false);
    	
    	//UTCに変換されているか判定
    	SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	Date date = df.parse("2022/01/01 09:15:00");
    	assertEquals(date, searchObj);
    	
    }
    
    @Test
    @DisplayName("検索条件パラメータをMapping用Objectに変換:年月日_FROM")
    void test_convertParameter_DATE_FROM() throws GnomesAppException, DateTimeParseException, ParseException {
    	
    	ConditionInfo info = makeConditionInfo();
    	
    	//日本時間を設定
    	setupDoReturnGnomesSessionBean();
    	Object searchObj = searchCondition.convertParameter(ConditionType.DATE, info, "2022/01/01", false);
    	
    	//UTCに変換されているか判定
    	SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	Date date = df.parse("2021/12/31 15:00:00");
    	assertEquals(date, searchObj);
    	
    }
    
    @Test
    @DisplayName("検索条件パラメータをMapping用Objectに変換:年月日_TO")
    void test_convertParameter_DATE_TO() throws GnomesAppException, DateTimeParseException, ParseException {
    	
    	ConditionInfo info = makeConditionInfo();
    	
    	//日本時間を設定
    	setupDoReturnGnomesSessionBean();
    	Object searchObj = searchCondition.convertParameter(ConditionType.DATE, info, "2022/01/01", true);
    	
    	//UTCに変換されているか判定
    	SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	Date date = df.parse("2022/01/01 14:59:59");
    	assertEquals(date, searchObj);
    	
    }
    
    @Test
    @DisplayName("検索条件パラメータをMapping用Objectに変換:MULTIFORMAT_FROM")
    void test_convertParameter_MULTIFORMAT_DATESTR_FROM() throws GnomesAppException, DateTimeParseException, ParseException {
    	
    	ConditionInfo info = makeConditionInfo();
    	
    	//日本時間を設定
    	setupDoReturnGnomesSessionBean();
    	Object searchObj = searchCondition.convertParameter(ConditionType.MULTIFORMAT_DATESTR, info, "2022/01/01", false);
    	
    	//UTCに変換されているか判定
    	SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	Date date = df.parse("2021/12/31 15:00:00");
    	assertEquals(date, searchObj);
    	
    }
    
    @Test
    @DisplayName("検索条件パラメータをMapping用Objectに変換:MULTIFORMAT_TO")
    void test_convertParameter_MULTIFORMAT_DATESTR_TO() throws GnomesAppException, DateTimeParseException, ParseException {
    	
    	ConditionInfo info = makeConditionInfo();
    	
    	//日本時間を設定
    	setupDoReturnGnomesSessionBean();
    	Object searchObj = searchCondition.convertParameter(ConditionType.MULTIFORMAT_DATESTR, info, "2022/01/01", true);
    	
    	//UTCに変換されているか判定
    	SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	Date date = df.parse("2022/01/01 14:59:59");
    	assertEquals(date, searchObj);
    	
    }
    
    @Test
    @DisplayName("検索条件パラメータをMapping用Objectに変換:年月_FROM")
    void test_convertParameter_DATE_YM_FROM() throws GnomesAppException, DateTimeParseException, ParseException {
    	
    	ConditionInfo info = makeConditionInfo();
    	
    	//日本時間を設定
    	setupDoReturnGnomesSessionBean();
    	Object searchObj = searchCondition.convertParameter(ConditionType.DATE_YM, info, "2022/01", false);
    	
    	//UTCに変換されているか判定
    	SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	Date date = df.parse("2021/12/31 15:00:00");
    	assertEquals(date, searchObj);
    	
    }
    
    @Test
    @DisplayName("検索条件パラメータをMapping用Objectに変換:年月_TO")
    void test_convertParameter_DATE_YM_TO() throws GnomesAppException, DateTimeParseException, ParseException {
    	
    	ConditionInfo info = makeConditionInfo();
    	
    	//日本時間を設定
    	setupDoReturnGnomesSessionBean();
    	Object searchObj = searchCondition.convertParameter(ConditionType.DATE_YM, info, "2022/01", true);
    	
    	//UTCに変換されているか判定
    	SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	Date date = df.parse("2022/01/31 14:59:59");
    	assertEquals(date, searchObj);
    	
    }
    
    /**
     * ConditionInfo作成
     */
    private ConditionInfo makeConditionInfo() {
    	
    	ConditionInfo info = new ConditionInfo();
    	info.setColumnId("testDatetime");
    	info.setHiddenItem(false);
    	info.setEnable("1");
    	
    	List<String> palams = new ArrayList<String>();
    	info.setParameters(palams);
    	
    	List<String> keys = new ArrayList<String>();
    	info.setPatternKeys(keys);
    	
    	return info;
    	
    }
   
    /**
     * GnomesSessionBeanモック化
     */
    private void setupDoReturnGnomesSessionBean() {
        TimeZone jp = TimeZone.getTimeZone("Asia/Tokyo");
        Locale locale = Locale.JAPAN;
        
        //ロケールとタイムゾーン設定
        Mockito.doReturn(jp).when(gnomesSessionBean).getUserTimeZone();
        Mockito.doReturn(locale).when(gnomesSessionBean).getUserLocale();
    }
}
