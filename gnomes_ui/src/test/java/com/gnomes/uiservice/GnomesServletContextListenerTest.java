package com.gnomes.uiservice;

import com.gnomes.common.data.*;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.util.KeyStoreUtilities;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(EasyMockExtension.class)
public class GnomesServletContextListenerTest {

    @InjectMocks
    private GnomesServletContextListener gnomesServletContextListener = new GnomesServletContextListener();

    @Mock
    private static KeyStoreUtilities keyStoreUtilities;
    @Mock
    private static GnomesSystemBean gnomesSystemBean;
    @Mock
    private static GnomesSystemModel gnomesSystemModel;

    private AutoCloseable closeable;

    @BeforeEach
    void beforeEach() {
        closeable = MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void afterEach() throws Exception {
        closeable.close();
    }

    // TODO コマンド定義XMLの定義仕様を浜本さんから受領後に、読込処理の詳細ロジックを確認するテストケースを追加する
    // (`JAXB#unmarshal`での読込以外に特殊な仕様が定義されていればそれをチェックする)
    @Disabled
    @DisplayName("共通コマンド読込：コマンド定義XMLの定義仕様に沿った読込データの詳細チェック")
    public void test_getCommandDatas_detail() {
        ServletContext context = makeMockServletContext();
        String testResourceDirPath = "src/test/resources/com.gnomes.uiservice/GnomesServletContextListenerTest/getDataCommands/common-command1/";
        EasyMock.expect(easyMockContext.getRealPath("/WEB-INF/common-command/")).andStubReturn(testResourceDirPath);
        for(String fileName : new File(testResourceDirPath).list()){
            EasyMock.expect(easyMockContext.getRealPath("/WEB-INF/common-command/" + fileName)).andStubReturn(testResourceDirPath + fileName);
        }
        setupMockServletContext();

        // テスト対象メソッドの呼び出し
        CommandDatas actual = (CommandDatas) ReflectionTestUtils.invokeMethod(gnomesServletContextListener, "getCommandDatas", context);

        // TODO 期待値の用意、結果の確認 (このテストケースでは件数のみの簡易確認程度で、詳細ロジックは別のテストケースでもいいかも)
        CommandDatas expected = new CommandDatas();
        expected.setCommandDataList(new ArrayList<CommandData>());
        expected.setScreenDataList(new ArrayList<CommandScreenData>());
        assertEquals(expected.getCommandDataList().size(), actual.getCommandDataList().size());
        for(CommandData obj : actual.getCommandDataList()){
            assertTrue(expected.getCommandDataList().contains(obj), obj.getCommandId()+"が期待値と一致しない");
        }
        assertEquals(expected.getScreenDataList().size(), actual.getScreenDataList().size());
        for(CommandScreenData obj : actual.getScreenDataList()){
            assertTrue(expected.getScreenDataList().contains(obj), obj.getScreenId()+"が期待値と一致しない");
        }
    }

    @Test
    @DisplayName("共通コマンド読込：複数の定義ファイルに重複するIDがある場合は1件のみ有効にする")
    public void test_getCommandDatas_duplicateCommandId() {
        ServletContext context = makeMockServletContext();
        String testResourceDirPath = "src/test/resources/com.gnomes.uiservice/GnomesServletContextListenerTest/getDataCommands/duplicateCommandId/";
        EasyMock.expect(easyMockContext.getRealPath("/WEB-INF/common-command/")).andStubReturn(testResourceDirPath);
        for(String fileName : new File(testResourceDirPath).list()){
            EasyMock.expect(easyMockContext.getRealPath("/WEB-INF/common-command/" + fileName)).andStubReturn(testResourceDirPath + fileName);
        }
        setupMockServletContext();

        // テスト対象メソッドの呼び出し
        CommandDatas actual = (CommandDatas) ReflectionTestUtils.invokeMethod(gnomesServletContextListener, "getCommandDatas", context);

        // 期待値の用意
        // GnomesCommands_重複するコマンドIDを持つA.xml = COMMAND_ID_Y99003C001とCOMMAND_ID_Y99003C002がある
        // GnomesCommands_重複するコマンドIDを持つB.xml = COMMAND_ID_Y99003C001のみ
        // ＝ 重複は削除されて最終的な期待結果は2件となる ("COMMAND_ID_"部分は定義先のフィールドの値には入っていないのでその後ろのID部だけ照合する)
        List<String> expectedIdList = new ArrayList<String>(Arrays.asList("Y99003C001", "Y99003C002"));

        // テストの趣旨は重複データが複数入ってこないことを確認することなので、合計件数とコマンドIDだけで簡易確認
        assertEquals(expectedIdList.size(), actual.getCommandDataList().size());
        for(CommandData data : actual.getCommandDataList()){
            String commandId = data.getCommandId();
            assertTrue(expectedIdList.contains(commandId), commandId + "が期待値と一致しない");
            expectedIdList.remove(commandId);
        }
        // 2回とも同じ期待値にヒットしていないことを保証する為、以下の検証処理では不適切
        // actual.getCommandDataList().stream().forEach(act -> assertTrue(expectedIdList.contains(act.getCommandId()), act.getCommandId()+"が期待値と一致しない"));

        assertEquals(0, actual.getScreenDataList().size());
    }

    @Test
    @DisplayName("共通コマンド読込：検証項目に`ListFieldName:`が含まれていない場合の検証項目リストの取得")
    public void test_getCommandDatas_checkList1() {
        ServletContext context = makeMockServletContext();
        String testResourceDirPath = "src/test/resources/com.gnomes.uiservice/GnomesServletContextListenerTest/getDataCommands/checkList1/";
        EasyMock.expect(easyMockContext.getRealPath("/WEB-INF/common-command/")).andStubReturn(testResourceDirPath);
        for(String fileName : new File(testResourceDirPath).list()){
            EasyMock.expect(easyMockContext.getRealPath("/WEB-INF/common-command/" + fileName)).andStubReturn(testResourceDirPath + fileName);
        }
        setupMockServletContext();

        // テスト対象メソッドの呼び出し
        CommandDatas actual = (CommandDatas) ReflectionTestUtils.invokeMethod(gnomesServletContextListener, "getCommandDatas", context);

        // 期待値の用意
        // 基盤のソースだとGnomesResourcesConstantsしか定義先が含まれていないので、入力となるテストリソースの<checkList>にはこれに定義されたフィールド名のみを指定して、そのフィールドにセットされた値を期待値とする
        List<String> expectedCheckList = new ArrayList<String>(Arrays.asList("DI01.0001", "ConditionLikeType_ENDS_BY"));

        // テストの趣旨はcheckListに`ListFieldName:`が含まれていない時に値がXML定義の値を元にgetGnomesResourcesConstantsによって再取得してセットされることを確認すること
        // commandIdは元のテストリソースが共通定義されたコマンドでは無いので、基盤のみのソースのテスト環境では取得できないのでチェックしない
        assertEquals(1, actual.getCommandDataList().size());
        CommandData data = actual.getCommandDataList().get(0);
        for(String item : data.getCheckList()){
            assertTrue(expectedCheckList.contains(item), "<checkList>の要素" + item + "が期待値と一致しない");
            expectedCheckList.remove(item);
        }
        assertEquals(1, actual.getScreenDataList().size());
    }

    @Test
    @DisplayName("共通コマンド読込：検証項目に`ListFieldName:`が含まれている場合の検証項目リストの取得")
    public void test_getCommandDatas_checkList2() {
        ServletContext context = makeMockServletContext();
        String testResourceDirPath = "src/test/resources/com.gnomes.uiservice/GnomesServletContextListenerTest/getDataCommands/checkList2/";
        EasyMock.expect(easyMockContext.getRealPath("/WEB-INF/common-command/")).andStubReturn(testResourceDirPath);
        for(String fileName : new File(testResourceDirPath).list()){
            EasyMock.expect(easyMockContext.getRealPath("/WEB-INF/common-command/" + fileName)).andStubReturn(testResourceDirPath + fileName);
        }
        setupMockServletContext();

        // テスト対象メソッドの呼び出し
        CommandDatas actual = (CommandDatas) ReflectionTestUtils.invokeMethod(gnomesServletContextListener, "getCommandDatas", context);

        // 期待値の用意
        List<String> expectedCheckList = new ArrayList<String>(Arrays.asList("ListFieldName:workbatchInputList"));

        // テストの趣旨はcheckListに`ListFieldName:`が含まれている時に値がXML定義からそのままセットされることを確認すること
        // commandIdは元のテストリソースが共通定義されたコマンドでは無いので、基盤のみのソースのテスト環境では取得できないのでチェックしない
        assertEquals(1, actual.getCommandDataList().size());
        CommandData data = actual.getCommandDataList().get(0);
        for(String item : data.getCheckList()){
            assertTrue(expectedCheckList.contains(item), "<checkList>の要素" + item + "が期待値と一致しない");
            expectedCheckList.remove(item);
        }
        assertEquals(1, actual.getScreenDataList().size());
    }

    @ParameterizedTest
    @CsvSource({
            // テストリソースのフォルダごとに入れているファイル名が異なる、フィルタを通すものはファイルの中身はすべて同じ
            // フィルタを通す
            "filter1/, 2, GnomesCommands_から始まるファイルはフィルタを通す",
            "filter2/, 2, GnomesCommandsContents_から始まるファイルはフィルタを通す",
            "filter3/, 2, GnomesCommandsContentsJob_から始まるファイルはフィルタを通す",
            "filter4/, 0, フィルタの指定に一致しないのでファイルを読み込まない(＝取得されるコマンドの期待値が0件)"
    })
    @DisplayName("共通コマンド読込：読込対象ファイルのフィルタが働いていることを確認")
    public void test_getCommandDatas_filter(String testResourceAppendPath, int expectedCount, String comment) {
        ServletContext context = makeMockServletContext();
        String testResourceDirPath = "src/test/resources/com.gnomes.uiservice/GnomesServletContextListenerTest/getDataCommands/" + testResourceAppendPath;
        EasyMock.expect(easyMockContext.getRealPath("/WEB-INF/common-command/")).andStubReturn(testResourceDirPath);
        for(String fileName : new File(testResourceDirPath).list()){
            EasyMock.expect(easyMockContext.getRealPath("/WEB-INF/common-command/" + fileName)).andStubReturn(testResourceDirPath + fileName);
        }
        setupMockServletContext();

        // テスト対象メソッドの呼び出し
        CommandDatas actual = (CommandDatas) ReflectionTestUtils.invokeMethod(gnomesServletContextListener, "getCommandDatas", context);

        // 期待値の用意、結果の確認 (このテストケースでは簡易確認)
        assertEquals(expectedCount, actual.getCommandDataList().size(), comment);
        assertEquals(0, actual.getScreenDataList().size());
    }

    @Test
    @DisplayName("カスタムタグ定義ファイルのリスト取得：プロジェクト直下のresources/WEB-INF/taglibs/フォルダを検証")
    public void test_getDictFileList_0() {
        ServletContext context = makeMockServletContext();
        // 元のプロジェクト構成の問題で一般的なソースパッケージ配下のリソースフォルダではないので注意
        String tagLibsDirPath = "resources/WEB-INF/taglibs/";
        EasyMock.expect(context.getRealPath("/WEB-INF/taglibs/")).andStubReturn(tagLibsDirPath);
        setupMockServletContext();

        String[] actual = gnomesServletContextListener.getDictFileList(context);

        // WEB-INF/taglibsフォルダ内にあるファイルの内、接頭辞(gnomes_)と接尾辞(_tag_dict.xml)が一致するファイルを列挙
        String[] expected = new String[]{"gnomes_Common_tag_dict.xml"};
        assertArrayEquals(expected, actual,
                "WEB-INF/taglibs/にカスタムタグ定義として認識されるリソースが増えている、要確認。意図した追加であるか確認してテストケースの期待値を修正すること。");

        // WEB-INF/taglibsフォルダ内には現状で2ファイルある、
        File dir = new File(tagLibsDirPath);
        assertEquals(2, dir.list().length,
                "WEB-INF/taglibs/のファイル数が変化している、要確認。意図通りの識別がされているファイルであるか確認してテストケースの期待値を修正すること。");
    }

    @Test
    @DisplayName("カスタムタグ定義ファイルのリスト取得：接頭辞(gnomes_)と接尾辞(_tag_dict.xml)が一致するファイルを列挙")
    public void test_getDictFileList_1() {
        ServletContext context = makeMockServletContext();
        String testResourceDirPath = "src/test/resources/com.gnomes.uiservice/GnomesServletContextListenerTest/getDictFileList/taglibs1";
        EasyMock.expect(context.getRealPath("/WEB-INF/taglibs/")).andStubReturn(testResourceDirPath);
        setupMockServletContext();

        String[] actual = gnomesServletContextListener.getDictFileList(context);

        // 指定フォルダ内にあるファイルの内、接頭辞(gnomes_)と接尾辞(_tag_dict.xml)が一致するファイルを列挙
        List<String> expected = Arrays.asList(new String[]{"gnomes_Common_tag_dict.xml", "gnomes_test_tag_dict.xml"});
        for(String item : actual) {
            assertTrue(expected.contains(item));
        }
    }

    @Test
    @DisplayName("カスタムタグ定義ファイルのリスト取得：接頭辞(gnomes_)のみの一致は除外")
    public void test_getDictFileList_2() {
        ServletContext context = makeMockServletContext();
        String testResourceDirPath = "src/test/resources/com.gnomes.uiservice/GnomesServletContextListenerTest/getDictFileList/taglibs2";
        EasyMock.expect(context.getRealPath("/WEB-INF/taglibs/")).andStubReturn(testResourceDirPath);
        setupMockServletContext();

        String[] actual = gnomesServletContextListener.getDictFileList(context);

        // 指定フォルダ内にあるファイルは「ignore_test_tag_dict.xml」なので除外される
        String[] expected = new String[]{};
        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("カスタムタグ定義ファイルのリスト取得：接尾辞(_tag_dict.xml)のみの一致は除外")
    public void test_getDictFileList_3() {
        ServletContext context = makeMockServletContext();
        String testResourceDirPath = "src/test/resources/com.gnomes.uiservice/GnomesServletContextListenerTest/getDictFileList/taglibs3";
        EasyMock.expect(context.getRealPath("/WEB-INF/taglibs/")).andStubReturn(testResourceDirPath);
        setupMockServletContext();

        String[] actual = gnomesServletContextListener.getDictFileList(context);

        // 指定フォルダ内にあるファイルは「gnomes_test_tag_dict_ignore.xml」なので除外される
        String[] expected = new String[]{};
        assertArrayEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "GnomesResources_Contents_JobConstants",
            "GnomesResources_ContentsConstants",
            "GnomesResourcesConstants"
    })
    @DisplayName("検証項目取得：リソースIDの取得元として定義されたクラス名の検証")
    public void test_RESOURCE_CONSTANTS_CLASSNAME_LIST(String className) {
        // 方式設計書に列挙されたクラス名が「RESOURCE_CONSTANTS_CLASSNAME_LIST」フィールドにセットされており、これがリソースID取得時の入力値に使用される
        // GnomesResourcesConstants以外の2つが未実装で、基盤利用側で実装して初めて動く設計になっているので、ここでは入力値の検証のみ行って将来の仕様や実装変更時などに影響に気付けるようにする
        String [] classNames = (String[]) ReflectionTestUtils.getField(gnomesServletContextListener, "RESOURCE_CONSTANTS_CLASSNAME_LIST");
        List<String> classNameList = Arrays.asList(classNames);
        assertTrue(classNameList.contains(className), "方式設計書に記載の" + className + "がリソースIDの取得元に含まれていない");
    }

    @Test
    @DisplayName("検証項目取得：GnomesResourcesConstantsに定義されたものが取得できることの確認")
    public void test_getGnomesResourcesConstants() {
        // GnomesResourcesConstants以外の2つが未実装で、基盤利用側で実装して初めて動く設計になっているので、実際に値が取得できることの確認はGnomesResourcesConstantsに対して実施する

        // 入力のIDはフィールド名、取得結果はそのフィールドの値になる
        String actual = ReflectionTestUtils.invokeMethod(gnomesServletContextListener, "getGnomesResourcesConstants", "DI01_0001");
        assertEquals("DI01.0001", actual);
    }

    @Test
    @DisplayName("検証項目取得：処理中にClassNotFoundExceptionが発生しても処理を継続することの確認")
    public void test_getGnomesResourcesConstants_SkipClassNotFoundException() {
        // InvalidClassNameは同じパッケージ内に存在しないので、先にこれを検索させることで例外発生のスキップが確認できる
        String [] classNameList = new String[]{"InvalidClassName", "CommandIdConstants"};

        String actual = ReflectionTestUtils.invokeMethod(gnomesServletContextListener, "getGnomesResourcesConstants", "DI01_0001");
        assertEquals("DI01.0001", actual);
    }

    @Test
    @DisplayName("検証項目取得：処理中にNoSuchFieldExceptionが発生しても処理を継続することの確認")
    public void test_getGnomesResourcesConstants_SkipNoSuchFieldException() {
        // GnomesMessagesConstantsは同じパッケージ内にあるがDI01_0001フィールドを持たないので、先にこれを検索させることで例外発生のスキップが確認できる
        String [] classNameList = new String[]{"GnomesMessagesConstants", "CommandIdConstants"};

        String actual = ReflectionTestUtils.invokeMethod(gnomesServletContextListener, "getGnomesResourcesConstants", "DI01_0001");
        assertEquals("DI01.0001", actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "CommandIdConstantsContentsJob",
            "CommandIdConstantsContents",
            "CommandIdConstants"
    })
    @DisplayName("コマンドIDの取得：コマンドIDの取得元として定義されたクラス名の検証")
    public void test_COMMAND_ID_CONSTANTS_CLASSNAME_LIST(String className) {
        // 方式設計書に列挙されたクラス名が「COMMAND_ID_CONSTANTS_CLASSNAME_LIST」フィールドにセットされており、これがコマンドID取得時の入力値に使用される
        // CommandIdConstants以外の2つが未実装で、基盤利用側で実装して初めて動く設計になっているので、ここでは入力値の検証のみ行って将来の仕様や実装変更時などに影響に気付けるようにする
        String [] classNames = (String[]) ReflectionTestUtils.getField(gnomesServletContextListener, "COMMAND_ID_CONSTANTS_CLASSNAME_LIST");
        List<String> classNameList = Arrays.asList(classNames);
        assertTrue(classNameList.contains(className), "方式設計書に記載の" + className + "がコマンドIDの取得元に含まれていない");
    }

    @Test
    @DisplayName("コマンドIDの取得：CommandIdConstantsに定義されたものが取得できることの確認")
    public void test_getConstants_CommandIdConstants() {
        // CommandIdConstants以外の2つが未実装で、基盤利用側で実装して初めて動く設計になっているので、実際に値が取得できることの確認はCommandIdConstantsに対して実施する
        String [] classNameList = (String[]) ReflectionTestUtils.getField(gnomesServletContextListener, "COMMAND_ID_CONSTANTS_CLASSNAME_LIST");

        // 入力のIDはフィールド名、取得結果はそのフィールドの値になる
        String actual = ReflectionTestUtils.invokeMethod(gnomesServletContextListener, "getConstants", classNameList, "COMMAND_ID_K01001C001");
        assertEquals("K01001C001", actual);
    }

    @Test
    @DisplayName("コマンドIDの取得：処理中にClassNotFoundExceptionが発生しても処理を継続することの確認")
    public void test_getConstants_SkipClassNotFoundException() {
        // InvalidClassNameは同じパッケージ内に存在しないので、先にこれを検索させることで例外発生のスキップが確認できる
        String [] classNameList = new String[]{"InvalidClassName", "CommandIdConstants"};

        String actual = ReflectionTestUtils.invokeMethod(gnomesServletContextListener, "getConstants", classNameList, "COMMAND_ID_K01001C001");
        assertEquals("K01001C001", actual);
    }

    @Test
    @DisplayName("コマンドIDの取得：処理中にNoSuchFieldExceptionが発生しても処理を継続することの確認")
    public void test_getConstants_SkipNoSuchFieldException() {
        // ScreenIdConstantsは同じパッケージ内にあるがCOMMAND_ID_K01001C001フィールドを持たないので、先にこれを検索させることで例外発生のスキップが確認できる
        String [] classNameList = new String[]{"ScreenIdConstants", "CommandIdConstants"};

        String actual = ReflectionTestUtils.invokeMethod(gnomesServletContextListener, "getConstants", classNameList, "COMMAND_ID_K01001C001");
        assertEquals("K01001C001", actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "ScreenIdConstantsContentsJob",
            "ScreenIdConstantsContents",
            "ScreenIdConstants"}
    )
    @DisplayName("画面IDの取得元として定義されたクラス名の検証")
    public void test_SCREEN_ID_CONSTANTS_CLASSNAME_LIST(String className) {
        // 方式設計書に列挙されたクラス名が「SCREEN_ID_CONSTANTS_CLASSNAME_LIST」フィールドにセットされており、これが画面ID取得時の入力値に使用される
        // ScreenIdConstants以外の2つが未実装で、基盤利用側で実装して初めて動く設計になっているので、ここでは入力値の検証のみ行って将来の仕様や実装変更時などに影響に気付けるようにする
        String [] classNames = (String[]) ReflectionTestUtils.getField(gnomesServletContextListener, "SCREEN_ID_CONSTANTS_CLASSNAME_LIST");
        List<String> classNameList = Arrays.asList(classNames);
        assertTrue(classNameList.contains(className), "方式設計書に記載の" + className + "が画面IDの取得元に含まれていない");
    }

    @Test
    @DisplayName("画面IDの取得：ScreenIdConstantsに定義されたものが取得できることの確認")
    public void test_getConstants_ScreenIdConstants() {
        // CommandIdConstants以外の2つが未実装で、基盤利用側で実装して初めて動く設計になっているので、実際に値が取得できることの確認はCommandIdConstantsに対して実施する
        String [] classNameList = (String[]) ReflectionTestUtils.getField(gnomesServletContextListener, "SCREEN_ID_CONSTANTS_CLASSNAME_LIST");

        // 入力のIDはフィールド名、取得結果はそのフィールドの値になる
        String actual = ReflectionTestUtils.invokeMethod(gnomesServletContextListener, "getConstants", classNameList, "SCID_A01001");
        assertEquals("A01001", actual);
    }

    @Test
    @DisplayName("画面IDの取得：処理中にClassNotFoundExceptionが発生しても処理を継続することの確認")
    public void test_getConstants_SkipClassNotFoundException_forScreenId() {
        // InvalidClassNameは同じパッケージ内に存在しないので、先にこれを検索させることで例外発生のスキップが確認できる
        String [] classNameList = new String[]{"InvalidClassName", "ScreenIdConstants"};
        String actual = ReflectionTestUtils.invokeMethod(gnomesServletContextListener, "getConstants", classNameList, "SCID_A01001");
        assertEquals("A01001", actual);
    }

    @Test
    @DisplayName("画面IDの取得：処理中にNoSuchFieldExceptionが発生しても処理を継続することの確認")
    public void test_getConstants_SkipNoSuchFieldException_forScreenId() {
        // CommandIdConstantsは同じパッケージ内にあるがSCID_A01001フィールドを持たないので、先にこれを検索させることで例外発生のスキップが確認できる
        String [] classNameList = new String[]{"CommandIdConstants", "ScreenIdConstants"};
        String actual = ReflectionTestUtils.invokeMethod(gnomesServletContextListener, "getConstants", classNameList, "SCID_A01001");
        assertEquals("A01001", actual);
    }

    // TODO GnomesServletContextListener#contextInitializedは300行ぐらいあり、リファクタリングされていない、UT対象にするかどうか要検討
    @Disabled
    @DisplayName("コンテキスト初期化後イベント")
    public void test_contextInitialized(){
        // モック
        Mockito.doNothing().when(keyStoreUtilities).Wakeup();
        Mockito.doReturn(gnomesSystemModel).when(gnomesSystemBean).getGnomesSystemModel();
        try {
            Mockito.doNothing().when(gnomesSystemModel).readAllMstrEntity();
            Mockito.doNothing().when(gnomesSystemModel).initializeProc();
        } catch (GnomesAppException e) {
            e.printStackTrace();
        }

        ServletContext context = makeMockServletContext();

        // contextInitialized内の呼び出しに合わせたモック設定

        ServletContextEvent event = new ServletContextEvent(context);
        gnomesServletContextListener.contextInitialized(event);
    }

    private ServletConfig easyMockConfig;
    private ServletContext easyMockContext;
    /**
     * モック化したServletContextの作成処理
     * 実行後にテストケースに依存する任意の挙動を`EasyMock#expect`で呼び出し元で設定後、setupMockServletContext();を呼び出すこと
     * @return モック化したServletContext
     */
    private ServletContext makeMockServletContext() {
        // Mockの作成
        easyMockConfig = EasyMock.createNiceMock(ServletConfig.class);
        easyMockContext = EasyMock.createNiceMock(ServletContext.class);
        // === 関数が呼ばれたときの挙動エミュレート(仮)
        EasyMock.expect(easyMockConfig.getServletName()).andStubReturn("MyTestServlet");
        EasyMock.expect(easyMockConfig.getServletContext()).andStubReturn(easyMockContext);
        // EasyMock.expect(easyMockConfig.getInitParameter("applicationRoot")).andStubReturn("/usr/local/test");
        // EasyMock.expect(easyMockConfig.getInitParameter("properties")).andStubReturn("/conf/test.properties");

        // contextInitialized(GnomesServletContextListener.java:190)対応
        java.util.Enumeration<String> setout = new Enumeration<String>() {
            @Override
            public boolean hasMoreElements() {
                return false;
            }
            @Override
            public String nextElement() {
                return null;
            }
        };
        EasyMock.expect(easyMockContext.getInitParameterNames()).andStubReturn(setout);

        // モックの挙動は各テストケース側でも設定する可能性があるので、呼び出し元でsetupMockServletContextは呼び出すことにする
        // setupMockServletContext();
        return easyMockContext;
    }

    /**
     * EasyMock.expectで設定した挙動を実行可能にする
     */
    private void setupMockServletContext() {
        EasyMock.replay(easyMockConfig);
        EasyMock.replay(easyMockContext);
    }
}
