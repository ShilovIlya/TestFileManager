package ru.alee.software.test.model;

import org.apache.log4j.Logger;
import ru.alee.software.test.exceptions.DirectoryNotExistException;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Class for managment files and directories.
 *
 * @author Ilya
 */
public class FileManager {

    private static final Logger logger = Logger.getLogger(FileManager.class);

    private static final String defaultDirectoryPath = "C://";
    private File currentDirectory;
    private List<File> filesFoldersList;

    /**
     * Class constructor setting curentDirectory to current path
     */
    public FileManager() {
        String currentPath = Paths.get("").toAbsolutePath().toString();
        this.currentDirectory = new File(currentPath);
        updateFilesFolderList();
    }


    /**
     * Class constructor setting curentDirectory to parameter path
     * @param path - path to "current" directory
     */
    public FileManager(String path) throws DirectoryNotExistException{
        try {
            this.currentDirectory = new File(path);
            updateFilesFolderList();
        } catch (Exception e) {
            throw new DirectoryNotExistException(path);
        }
    }

    /**
     * Class constructor setting curentDirectory to exist File object
     * @param currentDirectory exist File object
     */
    public FileManager(File currentDirectory) {
       this.currentDirectory = currentDirectory;
        updateFilesFolderList();
    }

    public void printFileList() {
        for (File file : filesFoldersList) {
            System.out.println(file.getName());
        }
    }

    public void printFileAbsolutePathList() {
        if (filesFoldersList == null) {
            logger.error("files list = null");
            return;
        }
        for (File file : filesFoldersList){
            System.out.println(file.getAbsolutePath());
        }
    }

    public File getCurrentDirectory() {
        return currentDirectory;
    }

    public List<File> getFilesFoldersList() {
        return filesFoldersList;
    }

    /**
     * Update list of files and folders with information form current directory
     */
    public void updateFilesFolderList() {
        String [] list = currentDirectory.list();
        filesFoldersList = new ArrayList<>(list.length);
        for (String str : list) {
            filesFoldersList.add(new File(currentDirectory.getAbsolutePath().concat("\\").concat(str)));
        }
    }

    /**
     * Change current directory to parent directory and update files-folders list
     */
    public void moveToParentDirectory() throws DirectoryNotExistException{
        changeDirectory(currentDirectory.getParent());
    }

    /**
     * Change current directory to dirPath directory and update files-folders list
     *
     * @param dirPath - path to new directory
     */
    public void changeDirectory(String dirPath) throws DirectoryNotExistException {
        try {
            File parent = new File(dirPath);
            if (!parent.exists())
                throw new DirectoryNotExistException(dirPath);
            currentDirectory = parent;
            updateFilesFolderList();
        } catch (Exception e) {
            throw new DirectoryNotExistException(dirPath);
        }
    }

    /**
     * Sort files and directories by name ascending.
     * Make own comparator.
     */
    public void sortByNameAsc() {
        Collections.sort(filesFoldersList, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                return file1.getName().compareTo(file2.getName());
            }
        });
    }

    /**
     * Sort files and directories by name descending.
     * Make own comparator.
     */
    public void sortByNameDesc() {
        Collections.sort(filesFoldersList, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                return -file1.getName().compareTo(file2.getName());
            }
        });
    }

}
