package ru.alee.software.test.model;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.alee.software.test.exceptions.DirectoryNotExistException;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

public class FileManagerTest {

    private static final Logger logger = Logger.getLogger(FileManagerTest.class);
    private FileManager sortFileManager;
    private File directory;

    @Before
    public void setUpFile() {
        directory = new File("D://");
    }

    @Test
    public void testDefaultConstructor() throws Exception {
        try {
            FileManager defaultFileManager = new FileManager();
        } catch (Exception e) {
            fail("throw Exception " + e);
        }
    }

    @Test
    public void testStringInputConstructor() throws Exception {
        try {
            FileManager stringFileManager = new FileManager("C://");
        } catch (Exception e) {
            fail("throw Exception " + e);
        }
    }

    @Test
    public void testFileInputConstructor() throws Exception {
        try {
            FileManager fileFileManager = new FileManager(directory);
        } catch (Exception e) {
            fail("throw Exception " + e);
        }
    }

    @Test
    public void moveToParentDirectoryDefault() throws Exception {
        testMoving(new FileManager());
    }

    @Test
    public void moveToParentDirectoryString() throws Exception {
        testMoving(new FileManager("C://"));
    }

    @Test
    public void moveToParentDirectoryFile() throws Exception {
        testMoving(new FileManager(directory));
    }

    public void testMoving(FileManager fileManager) {
        String oldPath = fileManager.getCurrentDirectory().getAbsolutePath();
        try {
            fileManager.moveToParentDirectory();
            String newPath = fileManager.getCurrentDirectory().getAbsolutePath();
            assertNotEquals(oldPath, newPath);
        } catch (DirectoryNotExistException e) {
            String newPath = fileManager.getCurrentDirectory().getAbsolutePath();
            assertEquals(oldPath, newPath);
        }
    }

    @Test(expected = DirectoryNotExistException.class)
    public void changeDirectoryFail() throws Exception {
        FileManager fileManager = new FileManager();
        fileManager.changeDirectory("D:/WRONG Folder");
    }

    @Test
    public void changeDirectory() throws Exception {
        FileManager fileManager = new FileManager();
        fileManager.changeDirectory("C:\\Users");
    }

    @Before
    public void createTestFilesAndDirectories() {
        File dir = new File("D:/test_folderQWERTY");
        dir.mkdir();
        try {
            File fileA = new File("D:/test_folderQWERTY/A.txt");
            fileA.createNewFile();
            File fileZ = new File("D:/test_folderQWERTY/Z.txt");
            fileZ.createNewFile();
        } catch (IOException e) {
            logger.debug("fail creating test files");
        }
        sortFileManager = new FileManager(dir);
    }

    @Test
    public void sortByNameAsc() throws Exception {
        sortFileManager.sortByNameAsc();
        String first = sortFileManager.getFilesFoldersList().get(0).getName();
        String second = sortFileManager.getFilesFoldersList().get(1).getName();
        assertEquals(first.concat(second), "A.txtZ.txt");
    }

    @Test
    public void sortByNameDesc() throws Exception {
        sortFileManager.sortByNameDesc();
        String first = sortFileManager.getFilesFoldersList().get(0).getName();
        String second = sortFileManager.getFilesFoldersList().get(1).getName();
        assertEquals(first.concat(second), "Z.txtA.txt");
    }

    @After
    public void deleteTestFilesAndDirectories() {
        File dir = new File("D:/test_folderQWERTY");
        File fileA = new File("D:/test_folderQWERTY/A.txt");
        fileA.delete();
        File fileZ = new File("D:/test_folderQWERTY/Z.txt");
        fileZ.delete();
        dir.delete();
    }

}
