package com.gnomes.equipment.spi;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.equipment.data.EquipmentIfReadData;

import biz.grandsight.looponex.user.api.TagDataManager;
import biz.grandsight.looponex.user.api.model.TagData;

class AbstractEquipmentTest {

	/** 設備ID */
	private String equipmentId = null;

	/** 設備パラメータ項目ID配列 */
	private String[] parameterItemIdArray = null;

	/** タグの値 */
	private String value = null;

	/** 品質コード */
	private int qualityCode = 0;

	/** 読取品質Goodフラグ */
	private boolean isGoodQuality=false;

//	/** エラーメッセージ */
//	private String errorMessage = null;
//
//	/** 入力データ値配列 */
//	private String[] inputDataArray=null;
//
//	/** 特殊変換処理区分 */
//	private List<Integer> specialConvertDivList=null;
//
//	/** サブシステムタグデータ書込みリスト */
//	private List<Integer> statusList = null;

	@Mock
	private TagDataManager tagDataManager;

	@InjectMocks
	@Spy
	private AbstractEquipment abstractEquipment = new AbstractEquipment();

	private AutoCloseable closeable;

	@BeforeEach
	void beforeEach() {
		closeable = MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void afterEach() throws Exception {
		closeable.close();
	}

	/**
	 * タグデータ読み込みの値設定
	 * @param equipmentId 設備ID
	 * @param parameterItemIdArray 設備パラメータ項目ID配列
	 * @param value タグの値
	 * @param qualityCode 品質コード
	 * @param isGoodQuality 読取品質Goodフラグ
	 * @throws GnomesAppException
	 */
	@ParameterizedTest(name = "Run {index}: equipmentId={0}, parameterItemIdArray={1}, value={2}, qualityCode={3},isGoodQuality={4}")
	@MethodSource("testReadTagData_Parameters")
	public void testReadTagData_Parameters(String equipmentId, String[] parameterItemIdArray, String value,
			int qualityCode, boolean isGoodQuality) throws GnomesAppException {
		this.equipmentId = equipmentId;
		this.parameterItemIdArray = parameterItemIdArray;
		this.value = value;
		this.qualityCode = qualityCode;
		this.isGoodQuality=isGoodQuality;
	}

	/**
	 * タグデータ読み込みのテストパラメータ
	 */
	static Stream<Arguments> testReadTagData_Parameters() throws Throwable {
		return Stream.of(
				Arguments.of("PLCAgent1", new String[] { "D1" }, "1", 1, false),
				Arguments.of("PLCAgent2", new String[] { "D2", "D3" }, "2", 1100, false),
				Arguments.of("PLCAgent3", new String[] { "D4" }, "4357", 10000000, false),
				Arguments.of("PLCAgent4", new String[] { "D5", "D6" }, "99999", 11000000, false));
	}

//	/**
//	 * タグデータ書き込みの値設定
//	 * @param equipmentId 設備ID
//	 * @param parameterItemIdArray 設備パラメータ項目ID配列
//	 * @param value タグの値
//	 * @param inputDataArray 入力データ値配列
//	 * @param specialConvertDivList 特殊変換処理区分
//	 * @param statusList サブシステムタグデータ書込みリスト
//	 * @param expectedMessage エラーメッセージ
//	 * @throws GnomesAppException
//	 */
//	@ParameterizedTest(name = "Run {index}: equipmentId={0}, parameterItemIdArray={1}, value={2}, inputDataArray={3}, errorMessage={4}")
//	@MethodSource("testTagData_Parameters")
//	public void testWriteTagData_Parameters(String equipmentId, String[] parameterItemIdArray, String value,
//			String[] inputDataArray, List<Integer> specialConvertDivList,List<Integer> statusList,String errorMessage) throws GnomesAppException {
//		this.equipmentId = equipmentId;
//		this.parameterItemIdArray = parameterItemIdArray;
//		this.value = value;
//		this.inputDataArray = inputDataArray;
//		this.specialConvertDivList=specialConvertDivList;
//		this.statusList=statusList;
//		this.errorMessage = errorMessage;
//	}
//
//	/**
//	 * タグデータ書き込みのテストパラメータ
//	 */
//	static Stream<Arguments> testWriteTagData_Parameters() throws Throwable {
//		return Stream.of(
//				Arguments.of("PLCAgent1", new Object[] { "D1" }, "1", new Object[] { "1","2","3" },new Object[] { 1,2,3 },new Object[] { 1,2,3 }, null),
//				Arguments.of("PLCAgent2", new Object[] { "D2", "D3" }, "2", new Object[] { "1","2","3" },new Object[] { 1,2,3 }, new Object[] { 1,2,3 },null),
//				Arguments.of("PLCAgent3", new Object[] { "D4" }, "4357", new Object[] { "1","2","3" },new Object[] { 1,2,3 }, new Object[] { 1,2,3 },null),
//				Arguments.of("PLCAgent4", new Object[] { "D5", "D6" }, "99999", new Object[] { "1","2","3" },new Object[] { 1,2,3 }, new Object[] { 1,2,3 },null));
//	}

	@DisplayName("サブシステムID、設備パラメータ項目ID配列をもとにタグデータを取得(複数件対応)")
	@Test
	public void readTagData() throws GnomesAppException {
		// tagDataListに値を設定
		List<TagData> tagDataList = new ArrayList<>();
		TagData tagData = new TagData();
		tagData.setTagItemName(equipmentId);
		tagData.setTagValue(value);
		tagData.setQualityCode(qualityCode);
		tagDataList.add(tagData);

		/**
		 * getSubSystemTagData()をMock化
		 */
		Mockito.doReturn(tagDataList).when(abstractEquipment).getSubSystemTagData(equipmentId, parameterItemIdArray);

		// 実際の戻り値
		List<EquipmentIfReadData> actualList = new ArrayList<>();
		actualList = abstractEquipment.readSubSystemTagData(equipmentId, parameterItemIdArray);
		String actualTagItemName = actualList.get(0).getParameterItemId();
		String actualTagValue = actualList.get(0).getValue();
		boolean actualQuality = actualList.get(0).getIsGoodQuality();

		// 値の判定
		assertEquals(equipmentId,actualTagItemName);
		assertEquals(value,actualTagValue);
		assertEquals(isGoodQuality,actualQuality);
	}

	@DisplayName("タグデータを取得できない場合、GnomesAppExceptionを発生させる")
	@Test
	public void readTagData_notReading() throws GnomesAppException {
		/** 設備ID */
		String nonExistingEquipmentId="abc";

		/** 設備パラメータ項目ID配列 */
		String[] notReadingParameterItemIdArray= {"D5","D6"};

		/** タグの値 */
		String notReadingTagValue="123";

		/** 品質コード */
		int notReadingQualityCode=0;

		// tagDataListに値を設定
		List<TagData> tagDataList = new ArrayList<>();
		TagData tagData = new TagData();
		tagData.setTagItemName(nonExistingEquipmentId);
		tagData.setTagValue(notReadingTagValue);
		tagData.setQualityCode(notReadingQualityCode);
		tagDataList.add(tagData);

		/**
		 * getSubSystemTagData()をMock化
		 */
		Mockito.doThrow(GnomesAppException.class).when(abstractEquipment).getSubSystemTagData(nonExistingEquipmentId,
				notReadingParameterItemIdArray);

		// 値の判定
		try {
			abstractEquipment.readSubSystemTagData(nonExistingEquipmentId,
					notReadingParameterItemIdArray);
			fail();
		} catch (GnomesAppException e) {
		}

	}

	@DisplayName("取得したタグデータにNaNが存在する場合、GnomesAppExceptionを発生させる")
	@Test
	public void readTagData_nullTagData() throws GnomesAppException {
		/** 設備ID */
		String readNullEquipmentId="abc";

		/** 設備パラメータ項目ID配列 */
		String[] readNullParameterItemIdArray= {"D7","D8"};

		/** タグの値 */
		String readNullTagValue=null;

		/** 品質コード */
		int readNullQualityCode=0;

		// tagDataListに値を設定
		List<TagData> tagDataList = new ArrayList<>();
		TagData tagData = new TagData();
		tagData.setTagItemName(readNullEquipmentId);
		tagData.setTagValue(readNullTagValue);
		tagData.setQualityCode(readNullQualityCode);
		tagDataList.add(tagData);

		/**
		 * getSubSystemTagData()をMock化
		 */
		Mockito.doThrow(GnomesAppException.class).when(abstractEquipment).getSubSystemTagData(readNullEquipmentId, readNullParameterItemIdArray);

		// 実際の戻り値
		try {
			abstractEquipment.readSubSystemTagData(readNullEquipmentId, readNullParameterItemIdArray);
			fail();
		} catch (GnomesAppException e) {
		}
	}

	/**
	 * writeTagData()のテスト実施なし
	 * 理由：writeSubsystemTagData()をMock化したが、直前で対象クラス(TagDataManager)がnewされているため値が差し込めない。基盤のソース修正をすればUT作成できるが、影響が大きいと判断し対応なし。
	 * 対処法： writeTagData()の「ITagDataManager tagDataManager = new TagDataManager();」を修正する。
	 *          @Disabledは削除する。
	 */
//	@DisplayName("書込タグ情報作成(複数件対応)")
//	@Test
//	@Disabled
//	public void writeTagData() throws GnomesAppException, LoopOnExException, NoSuchMethodException, SecurityException,
//			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		/**
//		 *  アイテム名リストを設定
//		 */
//		List<String> itemNameList = new ArrayList<>();
//		itemNameList.add(equipmentId);
//
//		/**
//		 * 設備I/Fパラメータ情報取得
//		 */
//		EquipmentIfParamInfo paramInfo = new EquipmentIfParamInfo();
//		paramInfo.setItemNameList(itemNameList);
//		paramInfo.setSpecialConvertDivList(specialConvertDivList);
//
//		/**
//		 * 書込タグ情報作成
//		 */
//		List<TagValue> paramList = new ArrayList<>();
//		TagValue tagValue = new TagValue();
//		tagValue.setTagItemName(equipmentId);
//		tagValue.setValue(value);
//		paramList.add(tagValue);
//
//		/**
//		 * getInterfaceParameter()をMock化
//		 */
//		Mockito.doReturn(paramInfo).when(abstractEquipment).getInterfaceParameter(Mockito.any(), Mockito.any());
//
//		/**
//		 * getDeclaredMethod()をMock化
//		 */
//		Method method = AbstractEquipment.class.getDeclaredMethod("setWriteSubsystemTagParam", String.class,
//				EquipmentIfParamInfo.class, String[].class);
//		method.setAccessible(true);
//
//		/**
//		 * writeSubsystemTagData()をMock化
//		 */
//		Mockito.doReturn(statusList).when(tagDataManager).writeSubsystemTagData(Mockito.any(), Mockito.any());
//
//		// 実際の値
//		List<TagValue> actual = (List<TagValue>) method.invoke(abstractEquipment, equipmentId, paramInfo,
//				inputDataArray);
//
//		// 期待値
//		Integer expectedValue = 1;
//		List<Integer> actualValueList = new ArrayList<>();
//		actualValueList = abstractEquipment.writeSubSystemTagData(equipmentId, parameterItemIdArray, inputDataArray);
//
//		// 値の判定
//		assertEquals(expectedValue, actualValueList.get(0));
//
//	}
//
//	@DisplayName("書込タグ情報作成_サブシステムIDが存在しない場合")
//	@Test
//	@Disabled
//	public void writeTagData_null() throws GnomesAppException, LoopOnExException {
//
//	}
//
//	@DisplayName("書込タグ情報作成_すでに書き込みがある場合")
//	@Test
//	@Disabled
//	public void writeTagData_existValue() throws GnomesAppException, LoopOnExException {
//
//	}

}
