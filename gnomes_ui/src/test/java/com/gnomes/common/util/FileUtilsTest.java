package com.gnomes.common.util;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;

class FileUtilsTest {

    @TempDir
    Path tempDir;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    @DisplayName("ディレクトリ作成：正常終了")
    void test_makeDirectory() throws GnomesAppException {
        Path path = tempDir.resolve("gnomes");
        FileUtils.makeDirectory(path.toString());
        assertTrue(Files.exists(path));
    }

    @Test
    @DisplayName("ディレクトリ作成：パスが空文字")
    void test_makeDirectory_empty() {
        String param = "";
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> FileUtils.makeDirectory(param));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ディレクトリ作成：パスがNull")
    void test_makeDirectory_null() {
        String param = null;
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> FileUtils.makeDirectory(param));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ファイル移動：正常終了")
    void test_move() throws IOException, GnomesAppException {
        Path fromPath = tempDir.resolve("gnomesFrom");
        Path fromTo = tempDir.resolve("gnomesTo");
        Path filePath = fromPath.resolve("gnomes-temp.xlsx");
        Files.createDirectories(fromPath);
        Files.createDirectories(fromTo);
        File file = new File(filePath.toString());
        file.createNewFile();
        FileUtils.move(fromPath.toString(), fromTo.toString(), file.getName());
        assertTrue(!Files.exists(filePath));
        assertTrue(Files.exists(fromTo.resolve(file.getName())));
    }

    @Test
    @DisplayName("ファイル移動：移動元が空文字")
    void test_move_from_empty() throws IOException, GnomesAppException {
        Path fromPath = tempDir.resolve("gnomesFrom");
        Path toPath = tempDir.resolve("gnomesTo");
        Path filePath = fromPath.resolve("gnomes-temp.xlsx");
        Files.createDirectories(fromPath);
        Files.createDirectories(toPath);
        File file = new File(filePath.toString());
        file.createNewFile();
        GnomesAppException e = assertThrows(GnomesAppException.class,
            () -> FileUtils.move("", toPath.toString(), file.getName()));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ファイル移動：移動先が空文字")
    void test_move_to_empty() throws IOException, GnomesAppException {
        Path fromPath = tempDir.resolve("gnomesFrom");
        Path toPath = tempDir.resolve("gnomesTo");
        Path filePath = fromPath.resolve("gnomes-temp.xlsx");
        Files.createDirectories(fromPath);
        Files.createDirectories(toPath);
        File file = new File(filePath.toString());
        file.createNewFile();
        GnomesAppException e = assertThrows(GnomesAppException.class,
            () -> FileUtils.move(fromPath.toString(), "", file.getName()));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ファイル移動：ファイル名が空文字")
    void test_move_fileName_empty() throws IOException, GnomesAppException {
        Path fromPath = tempDir.resolve("gnomesFrom");
        Path toPath = tempDir.resolve("gnomesTo");
        Path filePath = fromPath.resolve("gnomes-temp.xlsx");
        Files.createDirectories(fromPath);
        Files.createDirectories(toPath);
        File file = new File(filePath.toString());
        file.createNewFile();
        GnomesAppException e = assertThrows(GnomesAppException.class,
            () -> FileUtils.move(fromPath.toString(), toPath.toString(), ""));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ファイル移動：移動元がNull")
    void test_move_from_null() throws IOException, GnomesAppException {
        Path fromPath = tempDir.resolve("gnomesFrom");
        Path toPath = tempDir.resolve("gnomesTo");
        Path filePath = fromPath.resolve("gnomes-temp.xlsx");
        Files.createDirectories(fromPath);
        Files.createDirectories(toPath);
        File file = new File(filePath.toString());
        file.createNewFile();
        GnomesAppException e = assertThrows(GnomesAppException.class,
            () -> FileUtils.move(null, toPath.toString(), file.getName()));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ファイル移動：移動先がNull")
    void test_move_to_null() throws IOException, GnomesAppException {
        Path fromPath = tempDir.resolve("gnomesFrom");
        Path toPath = tempDir.resolve("gnomesTo");
        Path filePath = fromPath.resolve("gnomes-temp.xlsx");
        Files.createDirectories(fromPath);
        Files.createDirectories(toPath);
        File file = new File(filePath.toString());
        file.createNewFile();
        GnomesAppException e = assertThrows(GnomesAppException.class,
            () -> FileUtils.move(fromPath.toString(), null, file.getName()));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ファイル移動：ファイル名がNull")
    void test_move_fileName_null() throws IOException, GnomesAppException {
        Path fromPath = tempDir.resolve("gnomesFrom");
        Path toPath = tempDir.resolve("gnomesTo");
        Path filePath = fromPath.resolve("gnomes-temp.xlsx");
        Files.createDirectories(fromPath);
        Files.createDirectories(toPath);
        File file = new File(filePath.toString());
        file.createNewFile();
        GnomesAppException e = assertThrows(GnomesAppException.class,
            () -> FileUtils.move(fromPath.toString(), toPath.toString(), null));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ファイル移動：ファイル名が見つからない")
    void test_move_fileName_exists_false() throws IOException, GnomesAppException {
        Path fromPath = tempDir.resolve("gnomesFrom");
        Path toPath = tempDir.resolve("gnomesTo");
        Path filePath = fromPath.resolve("gnomes-temp.xlsx");
        Files.createDirectories(fromPath);
        Files.createDirectories(toPath);
        File file = new File(filePath.toString());
        GnomesAppException e = assertThrows(GnomesAppException.class,
            () -> FileUtils.move(fromPath.toString(), toPath.toString(), file.getName()));
        assertEquals("ME01.0190", e.getMessageNo());
    }

    @Test
    @DisplayName("ファイル移動：移動先が見つからない")
    void test_move_to_exists_false() throws IOException, GnomesAppException {
        Path fromPath = tempDir.resolve("gnomesFrom");
        Path toPath = tempDir.resolve("gnomesTo");
        Path filePath = fromPath.resolve("gnomes-temp.xlsx");
        Files.createDirectories(fromPath);
        File file = new File(filePath.toString());
        file.createNewFile();
        GnomesAppException e = assertThrows(GnomesAppException.class,
            () -> FileUtils.move(fromPath.toString(), toPath.toString(), file.getName()));
        assertEquals("ME01.0190", e.getMessageNo());
    }

    @Test
    @DisplayName("ファイル名変更：正常終了")
    void test_rename() throws IOException, GnomesAppException {
        String fromName = "gnomes-from.xlsx";
        String toName = "gnomes-to.xlsx";
        Path filePathFrom = tempDir.resolve(fromName);
        Path filePathTo = tempDir.resolve(toName);
        File file = new File(filePathFrom.toString());
        file.createNewFile();
        FileUtils.rename(tempDir.toString(), fromName, toName);
        assertTrue(!Files.exists(tempDir.resolve(filePathFrom)));
        assertTrue(Files.exists(tempDir.resolve(filePathTo)));
    }

    @Test
    @DisplayName("ファイル名変更：ディレクトリパスが空文字")
    void test_rename_directoryPath_empty() throws IOException, GnomesAppException {
        String fromName = "gnomes-from.xlsx";
        String toName = "gnomes-to.xlsx";
        Path filePath = tempDir.resolve(fromName);
        File file = new File(filePath.toString());
        file.createNewFile();
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> FileUtils.rename("", fromName, toName));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ファイル名変更：変更前ファイル名が空文字")
    void test_rename_fileNameFrom_empty() throws IOException, GnomesAppException {
        String fromName = "gnomes-from.xlsx";
        String toName = "gnomes-to.xlsx";
        Path filePath = tempDir.resolve(fromName);
        File file = new File(filePath.toString());
        file.createNewFile();
        GnomesAppException e = assertThrows(GnomesAppException.class,
            () -> FileUtils.rename(tempDir.toString(), "", toName));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ファイル名変更：変更後ファイル名が空文字")
    void test_rename_fileNameTo_empty() throws IOException, GnomesAppException {
        String fromName = "gnomes-from.xlsx";
        Path filePath = tempDir.resolve(fromName);
        File file = new File(filePath.toString());
        file.createNewFile();
        GnomesAppException e = assertThrows(GnomesAppException.class,
            () -> FileUtils.rename(tempDir.toString(), fromName, ""));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ファイル名変更：ディレクトリパスがNull")
    void test_rename_directoryPath_null() throws IOException, GnomesAppException {
        String fromName = "gnomes-from.xlsx";
        String toName = "gnomes-to.xlsx";
        Path filePath = tempDir.resolve(fromName);
        File file = new File(filePath.toString());
        file.createNewFile();
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> FileUtils.rename(null, fromName, toName));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ファイル名変更：変更前ファイル名がNull")
    void test_rename_fileNameFrom_null() throws IOException, GnomesAppException {
        String fromName = "gnomes-from.xlsx";
        String toName = "gnomes-to.xlsx";
        Path filePath = tempDir.resolve(fromName);
        File file = new File(filePath.toString());
        file.createNewFile();
        GnomesAppException e = assertThrows(GnomesAppException.class,
            () -> FileUtils.rename(tempDir.toString(), null, toName));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ファイル名変更：変更後ファイル名がNull")
    void test_rename_fileNameTo_null() throws IOException, GnomesAppException {
        String fromName = "gnomes-from.xlsx";
        Path filePath = tempDir.resolve(fromName);
        File file = new File(filePath.toString());
        file.createNewFile();
        GnomesAppException e = assertThrows(GnomesAppException.class,
            () -> FileUtils.rename(tempDir.toString(), fromName, null));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ファイル名変更：ディレクトリが見つからない")
    void test_rename_directory_exists_false() throws IOException, GnomesAppException {
        Path tempPath = tempDir.resolve("temp");
        String fromName = "gnomes-from.xlsx";
        String toName = "gnomes-to.xlsx";
        Path filePath = tempDir.resolve(fromName);
        File file = new File(filePath.toString());
        file.createNewFile();
        GnomesAppException e = assertThrows(GnomesAppException.class,
            () -> FileUtils.rename(tempPath.toString(), fromName, toName));
        assertEquals("ME01.0191", e.getMessageNo());
    }

    @Test
    @DisplayName("ファイル名変更：変更前ファイルが見つからない")
    void test_rename_fileFrom_exists_false() throws IOException, GnomesAppException {
        Path tempPath = tempDir.resolve("temp");
        String fromName = "gnomes-from.xlsx";
        String toName = "gnomes-to.xlsx";
        String notExistName = "not-exist-gnomes-from.xlsx";
        Path filePath = tempDir.resolve(fromName);
        File file = new File(filePath.toString());
        file.createNewFile();
        GnomesAppException e = assertThrows(GnomesAppException.class,
            () -> FileUtils.rename(tempPath.toString(), notExistName, toName));
        assertEquals("ME01.0191", e.getMessageNo());
    }

    @Test
    @DisplayName("ファイル名変更：変更前と後のファイル名が同じ")
    void test_rename_fileName_same() throws IOException, GnomesAppException {
        Path tempPath = tempDir.resolve("temp");
        String fromName = "gnomes-from.xlsx";
        Path filePath = tempDir.resolve(fromName);
        File file = new File(filePath.toString());
        file.createNewFile();
        GnomesAppException e = assertThrows(GnomesAppException.class,
            () -> FileUtils.rename(tempPath.toString(), fromName, fromName));
        assertEquals("ME01.0191", e.getMessageNo());
    }

    @Test
    @DisplayName("ファイル削除：正常終了")
    void test_delete_file() throws IOException, GnomesAppException {
        Path filePath = tempDir.resolve("gnomes-temp.xlsx");
        File file = new File(filePath.toString());
        file.createNewFile();
        FileUtils.delete(file.getPath());
        assertTrue(!Files.exists(filePath));
    }

    @Test
    @DisplayName("フォルダ削除：正常終了")
    void test_delete_directory() throws IOException, GnomesAppException {
        Path tempPath = tempDir.resolve("gnomes");
        Files.createDirectory(tempPath);
        FileUtils.delete(tempPath.toString());
        assertTrue(!Files.exists(tempPath));
    }

    @Test
    @DisplayName("フォルダ削除：再帰的に削除する 正常終了")
    void test_delete_directory_recursively() throws IOException, GnomesAppException {
        Path tempPath = tempDir.resolve("gnomes\\temp\\tmp\\t\\m\\p");
        Path tempFilePath = tempPath.resolve("gnomes-temp.xlsx");
        Files.createDirectories(tempPath);
        File file = new File(tempFilePath.toString());
        file.createNewFile();

        List<Path> pathElements = new ArrayList<>();
        tempDir.iterator().forEachRemaining(pathElements::add);

        Path parentPath = tempFilePath;
        while (true) {
            parentPath = parentPath.getParent();
            List<Path> parentPathElements = new ArrayList<>();
            parentPath.iterator().forEachRemaining(parentPathElements::add);
            if ((pathElements.size() + 1) == parentPathElements.size()) {
                break;
            }
        }
        FileUtils.delete(parentPath.toString());

        Path resultPath = tempFilePath;
        while (true) {
            if (Files.exists(resultPath)) {
                break;
            }
            resultPath = resultPath.getParent();
        }
        assertEquals(tempDir, resultPath);
    }

    @Test
    @DisplayName("フォルダ削除：削除対象パスが空文字")
    void test_delete_path_empty() throws IOException, GnomesAppException {
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> FileUtils.delete(""));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("フォルダ削除：削除対象パスがNull")
    void test_delete_path_Null() throws IOException, GnomesAppException {
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> FileUtils.delete(null));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ファイルバイナリ変換：書込みあり 正常終了")
    void test_readFileToByte_write() throws IOException, GnomesAppException {
        String fileName = "gnomes-temp.xlsx";
        Path filePath = tempDir.resolve(fileName);
        Files.createFile(Paths.get(tempDir.toString(), fileName));
        Files.write(Paths.get(tempDir.toString(), fileName), getOutputString(), Charset.forName("UTF-8"),
            StandardOpenOption.WRITE);
        byte[] fileData = FileUtils.readFileToByte(filePath.toString());
        assertTrue(fileData.length > 0);
    }

    @Test
    @DisplayName("ファイルバイナリ変換：書込みなし 正常終了")
    void test_readFileToByte() throws IOException, GnomesAppException {
        String fileName = "gnomes-temp.xlsx";
        Path filePath = tempDir.resolve(fileName);
        Files.createFile(Paths.get(tempDir.toString(), fileName));
        byte[] fileData = FileUtils.readFileToByte(filePath.toString());
        assertTrue(fileData.length == 0);
    }

    @Test
    @DisplayName("ファイルバイナリ変換：ファイルパスが空文字")
    void test_readFileToByte_filePath_empty() throws IOException, GnomesAppException {
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> FileUtils.readFileToByte(""));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ファイルバイナリ変換：ファイルパスがNull")
    void test_readFileToByte_filePath_null() throws IOException, GnomesAppException {
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> FileUtils.readFileToByte(null));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("ファイルバイナリ変換：ファイルが存在しない")
    void test_readFileToByte_file_exists_false() throws IOException, GnomesAppException {
        Path tempFilePath = tempDir.resolve("gnomes-temp.xlsx");
        GnomesAppException e = assertThrows(GnomesAppException.class,
            () -> FileUtils.readFileToByte(tempFilePath.toString()));
        assertEquals("ME01.0193", e.getMessageNo());
    }

    @Test
    @DisplayName("ファイルバイナリ変換：ファイルが指定されていない")
    void test_readFileToByte_isFile_false() throws IOException, GnomesAppException {
        Path tempFilePath = tempDir.resolve("gnomes");
        Files.createDirectories(tempFilePath);
        GnomesAppException e = assertThrows(GnomesAppException.class,
            () -> FileUtils.readFileToByte(tempFilePath.toString()));
        assertEquals("ME01.0193", e.getMessageNo());
    }

    // FileLockはLinuxとWindowsの挙動がことなるためCI環境ではIOExceptionがスローされない
    // またテスト対象メソッド内のByteArrayOutputStreamのインスタンスをモック化することもできないためDisabledとする
    // ローカルでは動作確認済みなので実施する場合は@Disabledを@Testに変更する
    @Disabled
    @DisplayName("ファイルバイナリ変換：IOExceptionを発生させる")
    void test_readFileToByte_ioexception() throws IOException, GnomesAppException {
        String fileName = "gnomes-temp.xlsx";
        Path filePath = tempDir.resolve(fileName);
        Files.createFile(filePath);
        Files.write(filePath, getOutputString(), Charset.forName("UTF-8"), StandardOpenOption.WRITE);
        try (FileChannel fc = FileChannel.open(filePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            FileLock lock = fc.tryLock()) {
            if (lock == null) {
                fail();
            }
            GnomesAppException e = assertThrows(GnomesAppException.class,
                () -> FileUtils.readFileToByte(filePath.toString()));
            assertEquals("ME01.0194", e.getMessageNo());
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @DisplayName("バイナリデータファイル変換：正常終了")
    void test_writeFileFromByte() throws IOException, GnomesAppException {
        String fileName = "gnomes-temp.xlsx";
        Path newFilePath = tempDir.resolve("gnomes-new-temp.xlsx");
        Path oldFilePath = tempDir.resolve(fileName);
        Files.createFile(Paths.get(tempDir.toString(), fileName));
        Files.write(Paths.get(tempDir.toString(), fileName), getOutputString(), Charset.forName("UTF-8"),
            StandardOpenOption.WRITE);
        byte[] fileData = FileUtils.readFileToByte(oldFilePath.toString());
        FileUtils.writeFileFromByte(newFilePath.toString(), fileData);
    }

    @Test
    @DisplayName("バイナリデータファイル変換：ファイルパスが空文字")
    void test_writeFileFromByte_filePath_empty() throws IOException, GnomesAppException {
        String fileName = "gnomes-temp.xlsx";
        Path oldFilePath = tempDir.resolve(fileName);
        Files.createFile(Paths.get(tempDir.toString(), fileName));
        Files.write(Paths.get(tempDir.toString(), fileName), getOutputString(), Charset.forName("UTF-8"),
            StandardOpenOption.WRITE);
        byte[] fileData = FileUtils.readFileToByte(oldFilePath.toString());
        GnomesAppException e = assertThrows(GnomesAppException.class, () -> FileUtils.writeFileFromByte("", fileData));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("バイナリデータファイル変換：ファイルパスがNull")
    void test_writeFileFromByte_filePath_null() throws IOException, GnomesAppException {
        String fileName = "gnomes-temp.xlsx";
        Path oldFilePath = tempDir.resolve(fileName);
        Files.createFile(Paths.get(tempDir.toString(), fileName));
        Files.write(Paths.get(tempDir.toString(), fileName), getOutputString(), Charset.forName("UTF-8"),
            StandardOpenOption.WRITE);
        byte[] fileData = FileUtils.readFileToByte(oldFilePath.toString());
        GnomesAppException e = assertThrows(GnomesAppException.class,
            () -> FileUtils.writeFileFromByte(null, fileData));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("バイナリデータファイル変換：ファイルデータがNull")
    void test_writeFileFromByte_fileData_null() throws IOException, GnomesAppException {
        String fileName = "gnomes-temp.xlsx";
        Path newFilePath = tempDir.resolve("gnomes-new-temp.xlsx");
        Files.createFile(Paths.get(tempDir.toString(), fileName));
        Files.write(Paths.get(tempDir.toString(), fileName), getOutputString(), Charset.forName("UTF-8"),
            StandardOpenOption.WRITE);
        GnomesAppException e = assertThrows(GnomesAppException.class,
            () -> FileUtils.writeFileFromByte(newFilePath.toString(), null));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("PDFバイナリデータに挿入画像を貼り付け：正常終了")
    void test_getPdfDrawnImage() throws IOException, GnomesAppException {
        Path filePath = tempDir.resolve("gnomes-temp.pdf");
        byte[] fileData = covertToByteArray(filePath);
        Path imagePath = createImage();
        byte[] result = null;

        try (MockedStatic<ResourcesHandler> mocked = Mockito.mockStatic(ResourcesHandler.class)) {
            mocked.when(() -> {
                ResourcesHandler.getString(GnomesResourcesConstants.DI01_0139);
            }).thenReturn("印刷プレビュー");

            result = FileUtils.getPdfDrawnImage(fileData, imagePath.toString(), 10, 10, 1.0f);
            assertTrue(result.length > 0);
        }
    }

    @Test
    @DisplayName("PDFバイナリデータに挿入画像を貼り付け：挿入画像パスが空文字")
    void test_getPdfDrawnImage_drawImagePath_empty() throws IOException, GnomesAppException {
        Path filePath = tempDir.resolve("gnomes-temp.pdf");
        byte[] fileData = covertToByteArray(filePath);
        GnomesAppException e = assertThrows(GnomesAppException.class,
            () -> FileUtils.getPdfDrawnImage(fileData, "", 10, 10, 1.0f));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("PDFバイナリデータに挿入画像を貼り付け：挿入画像パスがNull")
    void test_getPdfDrawnImage_drawImagePath_null() throws IOException, GnomesAppException {
        Path filePath = tempDir.resolve("gnomes-temp.pdf");
        byte[] fileData = covertToByteArray(filePath);
        GnomesAppException e = assertThrows(GnomesAppException.class,
            () -> FileUtils.getPdfDrawnImage(fileData, "", 10, 10, 1.0f));
        assertEquals("ME01.0050", e.getMessageNo());
    }

    @Test
    @DisplayName("PDFバイナリデータに挿入画像を貼り付け：PDFデータ以外を指定")
    void test_getPdfDrawnImage_isPdfData_false() throws IOException, GnomesAppException {
        Path filePath = tempDir.resolve("gnomes-temp.txt");
        Files.createFile(filePath);
        byte[] fileData = Files.readAllBytes(filePath);
        Path imagePath = createImage();
        GnomesAppException e = assertThrows(GnomesAppException.class,
            () -> FileUtils.getPdfDrawnImage(fileData, imagePath.toString(), 10, 10, 1.0f));
        assertEquals("ME01.0208", e.getMessageNo());
    }

    @Test
    @DisplayName("PDFバイナリデータに挿入画像を貼り付け：挿入画像が存在しない")
    void test_getPdfDrawnImage_drawImagePath_exists_false() throws IOException, GnomesAppException {
        Path notExistFilePath = tempDir.resolve("hogehoge.png");
        Path filePath = tempDir.resolve("gnomes-temp.pdf");
        byte[] fileData = covertToByteArray(filePath);
        createImage();
        GnomesAppException e = assertThrows(GnomesAppException.class,
            () -> FileUtils.getPdfDrawnImage(fileData, notExistFilePath.toString(), 10, 10, 1.0f));
        assertEquals("ME01.0193", e.getMessageNo());
    }

    @Test
    @DisplayName("PDFバイナリデータに挿入画像を貼り付け：ファイルを指定していない")
    void test_getPdfDrawnImage_drawImagePath_exists_isFile_false() throws IOException, GnomesAppException {
        Path tempFilePath = tempDir.resolve("gnomes");
        Files.createDirectories(tempFilePath);
        Path filePath = tempDir.resolve("gnomes-temp.pdf");
        byte[] fileData = covertToByteArray(filePath);
        GnomesAppException e = assertThrows(GnomesAppException.class,
            () -> FileUtils.getPdfDrawnImage(fileData, tempFilePath.toString(), 10, 10, 1.0f));
        assertEquals("ME01.0193", e.getMessageNo());
    }

    private List<String> getOutputString() {
        List<String> result = new ArrayList<>();
        result.add("ファイルバイナリ変換テスト用");
        result.add("または");
        result.add("バイナリデータファイル変換テスト用");
        return result;
    }

    private byte[] covertToByteArray(Path path) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            document.save(path.toString());
            document.close();
        }
        return Files.readAllBytes(path);
    }

    private Path createImage() throws IOException {
        Path imagePath = tempDir.resolve("gnomes-image.png");
        BufferedImage img = new BufferedImage(120, 40, BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = img.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 120, 40);
        g.setColor(Color.BLACK);
        g.drawLine(10, 10, 39, 39);
        g.drawString("FileUtils", 15, 15);
        g.dispose();
        ImageIO.write(img, "png", imagePath.toFile());
        return imagePath;
    }

}
