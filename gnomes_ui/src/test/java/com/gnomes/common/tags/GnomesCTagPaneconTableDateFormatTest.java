package com.gnomes.common.tags;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.jsp.JspWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;
import com.gnomes.common.data.GnomesSessionBean;
import com.gnomes.common.data.ProcessTableBean;
import com.gnomes.system.view.TestFormBean;
import com.gnomes.uiservice.ContainerResponse;

class GnomesCTagPaneconTableDateFormatTest {

  @Spy
  @InjectMocks
  GnomesCTagPaneconTable cTagPaneconTable;
  @Spy
  GnomesCTagTableCommon cTagTableCommon;

  @Mock
  JspWriter out;
  @Mock
  ContainerResponse responseContext;
  @Mock
  GnomesSessionBean gnomesSessionBean;

  private InOrder inOrder;
  private Map<String, Object> mapColInfo;
  private Date beforeDate;

  @BeforeEach
  void setUp() throws Exception {
    // モックの初期化
    MockitoAnnotations.openMocks(this);
    inOrder = inOrder(out);

    cTagPaneconTable.setBean(new TestFormBean());
    cTagPaneconTable.setDictId("test_dict_id");

    // フォーマット前の日付設定
    beforeDate = TestCTagUtil.createDate(2020, 4, 1, 12, 30, 15);

    // 各テストケースで共通なパラメータの初期化とモックの戻り値の設定
    mapColInfo = new HashMap<String, Object>();
    mapColInfo.put(GnomesCTagTable.INFO_PARAM_NAME, "dataType,date");
    doReturn(Locale.JAPAN).when(gnomesSessionBean).getUserLocale();
    doReturn(beforeDate).when(responseContext).getResponseFormBean(any(), anyString(), anyInt(),
        any());
    cTagTableCommon.gnomesSessionBean = gnomesSessionBean;
  }

  @ParameterizedTest
  @ValueSource(ints = {GnomesCTagBase.PARAM_DATA_TYPE_DIV_YYYYMMDDHHMmmss,
      GnomesCTagBase.PARAM_DATA_TYPE_DIV_YYYYMMDD, GnomesCTagBase.PARAM_DATA_TYPE_DIV_DATE_HHmmss,
      GnomesCTagBase.PARAM_DATA_TYPE_DIV_DATE_HHmm,
      GnomesCTagBase.PARAM_DATA_TYPE_DIV_YYYYMMDDHHMmm, GnomesCTagBase.PARAM_DATA_TYPE_DIV_YYYYMM})
  @DisplayName("指定されたデータタイプによって取得したパターンによって日付フォーマットされていることの確認：入力")
  void test_format_date_by_dataType_input(int dataType) throws Exception {
    // @ParameterizedTestによって@ValueSourceの引数の数だけテストメソッドが呼ばれる
    setupMockGetData(dataType);

    // inputDataType実行
    ReflectionTestUtils.invokeMethod(cTagPaneconTable, "inputDataType", out, Locale.JAPAN,
        mapColInfo, null, null, initProcessTableBean(), 1);

    // InOrder.verifyでJspWriter.outが呼ばれることを検証
    // カスタムタグ自体の検証ではないのでフォーマット後日付部分のみ対象
    inOrder.verify(out, times(1))
        .print(" value=\"" + TestCTagUtil.convertDateToString(dataType, beforeDate) + "\""
            + " onchange=\"" + "setWarningFlag();" + "\"");
  }

  @ParameterizedTest
  @ValueSource(ints = {GnomesCTagBase.PARAM_DATA_TYPE_DIV_YYYYMMDDHHMmmss,
      GnomesCTagBase.PARAM_DATA_TYPE_DIV_YYYYMMDD, GnomesCTagBase.PARAM_DATA_TYPE_DIV_DATE_HHmmss,
      GnomesCTagBase.PARAM_DATA_TYPE_DIV_DATE_HHmm,
      GnomesCTagBase.PARAM_DATA_TYPE_DIV_YYYYMMDDHHMmm, GnomesCTagBase.PARAM_DATA_TYPE_DIV_YYYYMM})
  @DisplayName("指定されたデータタイプによって取得したパターンによって日付フォーマットされていることの確認：出力")
  void test_format_date_by_dataType_out(int dataType) throws Exception {
    // @ParameterizedTestによって@ValueSourceの引数の数だけテストメソッドが呼ばれる
    setupMockGetData(dataType);

    // outputDataType実行
    ReflectionTestUtils.invokeMethod(cTagPaneconTable, "outputDataType", out,
        new GnomesCTagDictionary(), mapColInfo, null, null, initProcessTableBean(), 1);

    // InOrder.verifyでJspWriter.outが呼ばれることを検証
    // カスタムタグ自体の検証ではないのでフォーマット後日付部分のみ対象
    inOrder.verify(out, times(1)).print("<div class=\"common-table-col-1\"" + ">"
        + TestCTagUtil.convertDateToString(dataType, beforeDate) + "</div>");
  }

  private void setupMockGetData(int dataType) throws Exception {
    // doReturn第1引数はinputDataBase、第2引数はoutputDataBase内のgetDataの戻り値
    doReturn(dataType, beforeDate).when(cTagPaneconTable).getData(any(), any(), any());
  }

  private ProcessTableBean initProcessTableBean() {
    ProcessTableBean ptb = new ProcessTableBean();
    ptb.setCols("1");
    ptb.setRows("1");
    ptb.setRowPosition("1");
    ptb.setRowNumber("1");
    ptb.setRowLineNum("1");
    return ptb;
  }

}
