package com.gnomes.common.util;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.gnomes.common.constants.CommonEnums.RoundCalculateDiv;
import com.gnomes.common.exception.GnomesAppException;

class ConverterUtilsTest {

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
    @DisplayName("丸め処理:四捨五入/1.0")
    void test_roundCalculate_roundHalfUp_Zero() throws GnomesAppException {
    	//変換前値
        BigDecimal beforNum = BigDecimal.valueOf(1.0);
        //変換後値
        BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundHalfUp);
        //期待値
        BigDecimal expectedNum = BigDecimal.valueOf(1.0);
        //比較
        assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:四捨五入/1.4")
    void test_roundCalculate_roundHalfUp_odd_four() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(1.4);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundHalfUp);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(1.0);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:四捨五入/1.5")
    void test_roundCalculate_roundHalfUp_odd_five() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(1.5);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundHalfUp);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(2.0);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:四捨五入/1.6")
    void test_roundCalculate_roundHalfUp_odd_six() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(1.6);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundHalfUp);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(2.0);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:四捨五入/2.4")
    void test_roundCalculate_roundHalfUp_even_four() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(2.4);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundHalfUp);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(2.0);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:四捨五入/2.5")
    void test_roundCalculate_roundHalfUp_even_five() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(2.5);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundHalfUp);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(3.0);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:四捨五入/2.6")
    void test_roundCalculate_roundHalfUp_even_six() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(2.6);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundHalfUp);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(3.0);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    
    
    
    @Test
    @DisplayName("丸め処理:切り捨て/1.0")
    void test_roundCalculate_roundDown_Zero() throws GnomesAppException {
    	//変換前値
        BigDecimal beforNum = BigDecimal.valueOf(1.0);
        //変換後値
        BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundDown);
        //期待値
        BigDecimal expectedNum = BigDecimal.valueOf(1.0);
        //比較
        assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:切り捨て/1.4")
    void test_roundCalculate_roundDown_odd_four() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(1.4);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundDown);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(1.0);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:切り捨て/1.5")
    void test_roundCalculate_roundDown_odd_five() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(1.5);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundDown);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(1.0);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:切り捨て/1.6")
    void test_roundCalculate_roundDown_odd_six() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(1.6);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundDown);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(1.0);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:切り捨て/2.4")
    void test_roundCalculate_roundDown_even_four() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(2.4);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundDown);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(2.0);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:切り捨て/2.5")
    void test_roundCalculate_roundDown_even_five() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(2.5);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundDown);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(2.0);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:切り捨て/2.6")
    void test_roundCalculate_roundDown_even_six() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(2.6);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundDown);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(2.0);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    
    
    
    @Test
    @DisplayName("丸め処理:切り上げ/1.0")
    void test_roundCalculate_roundUp_Zero() throws GnomesAppException {
    	//変換前値
        BigDecimal beforNum = BigDecimal.valueOf(1.0);
        //変換後値
        BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundUp);
        //期待値
        BigDecimal expectedNum = BigDecimal.valueOf(1.0);
        //比較
        assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:切り上げ/1.4")
    void test_roundCalculate_roundUp_odd_four() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(1.4);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundUp);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(2.0);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:切り上げ/1.5")
    void test_roundCalculate_roundUp_odd_five() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(1.5);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundUp);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(2.0);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:切り上げ/1.6")
    void test_roundCalculate_roundUp_odd_six() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(1.6);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundUp);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(2.0);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:切り上げ/2.4")
    void test_roundCalculate_roundUp_even_four() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(2.4);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundUp);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(3.0);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:切り上げ/2.5")
    void test_roundCalculate_roundUp_even_five() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(2.5);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundUp);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(3.0);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:切り上げ/2.6")
    void test_roundCalculate_roundUp_even_six() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(2.6);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundUp);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(3.0);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    
    
    
    @Test
    @DisplayName("丸め処理:丸めなし/1.0")
    void test_roundCalculate_roundNone_Zero() throws GnomesAppException {
    	//変換前値
        BigDecimal beforNum = BigDecimal.valueOf(1.0);
        //変換後値
        BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundNone);
        //期待値
        BigDecimal expectedNum = BigDecimal.valueOf(1.0);
        //比較
        assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:丸めなし/1.4")
    void test_roundCalculate_roundNone_odd_four() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(1.4);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundNone);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(1.4);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:丸めなし/1.5")
    void test_roundCalculate_roundNone_odd_five() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(1.5);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundNone);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(1.5);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:丸めなし/1.6")
    void test_roundCalculate_roundNone_odd_six() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(1.6);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundNone);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(1.6);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:丸めなし/2.4")
    void test_roundCalculate_roundNone_even_four() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(2.4);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundNone);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(2.4);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:丸めなし/2.5")
    void test_roundCalculate_roundNone_even_five() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(2.5);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundNone);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(2.5);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:丸めなし/2.6")
    void test_roundCalculate_roundNone_even_six() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(2.6);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundNone);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(2.6);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    
    
    
    @Test
    @DisplayName("丸め処理:JIS丸め/1.0")
    void test_roundCalculate_roundJIS_Zero() throws GnomesAppException {
    	//変換前値
        BigDecimal beforNum = BigDecimal.valueOf(1.0);
        //変換後値
        BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundJIS);
        //期待値
        BigDecimal expectedNum = BigDecimal.valueOf(1.0);
        //比較
        assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:JIS丸め/1.4")
    void test_roundCalculate_roundJIS_odd_four() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(1.4);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundJIS);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(1.0);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:JIS丸め/1.5")
    void test_roundCalculate_roundJIS_odd_five() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(1.5);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundJIS);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(2.0);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:JIS丸め/1.6")
    void test_roundCalculate_roundJIS_odd_six() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(1.6);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundJIS);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(2.0);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:JIS丸め/2.4")
    void test_roundCalculate_roundJIS_even_four() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(2.4);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundJIS);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(2.0);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:JIS丸め/2.5")
    void test_roundCalculate_roundJIS_even_five() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(2.5);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundJIS);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(2.0);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
    @Test
    @DisplayName("丸め処理:JIS丸め/2.6")
    void test_roundCalculate_roundJIS_even_six() throws GnomesAppException {
    	//変換前値
    	BigDecimal beforNum = BigDecimal.valueOf(2.6);
    	//変換後値
    	BigDecimal afterNum = ConverterUtils.roundCalculate(beforNum, 0, RoundCalculateDiv.RoundCalculateDiv_RoundJIS);
    	//期待値
    	BigDecimal expectedNum = BigDecimal.valueOf(3.0);
    	//比較
    	assertTrue(afterNum.compareTo(expectedNum) == 0);
    }
}
