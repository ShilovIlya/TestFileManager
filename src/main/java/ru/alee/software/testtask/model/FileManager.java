package ru.alee.software.testtask.model;

import org.apache.log4j.Logger;
import ru.alee.software.testtask.exceptions.DirectoryNotExistException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

/**
 * Class for managment files and directories.
 *
 * @author Ilya
 */
public class FileManager {

    private static final Logger logger = Logger.getLogger(FileManager.class);

    private File currentDirectory;
    private List<File> filesFoldersList;
    private List<File> filesBuffer;
    private String filesBufferCommand;
    private volatile int progress;
    private boolean isPastInterrupted;
    private boolean isDeleteInterrupted;

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

    public boolean isPastInterrupted() {
        return isPastInterrupted;
    }

    public void setPastInterrupted(boolean pastInterrupted) {
        isPastInterrupted = pastInterrupted;
    }

    public boolean isDeleteInterrupted() {
        return isDeleteInterrupted;
    }

    public void setDeleteInterrupted(boolean deleteInterrupted) {
        isDeleteInterrupted = deleteInterrupted;
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

    /**
     * Create new directory if not exist inf current folder
     *
     * @param name - folder name
     */
    public void makeDirectory(String name) {
        File newDir = new File(name);
        if (!newDir.mkdir()) {
            logger.debug("directory wasn't created.");
        }
    }

    /**
     * Save in memory files and directories which were selected.
     *
     * @param selectedIndices
     * @param filesBufferSource
     */
    public void bufferedFiles(int[] selectedIndices, String filesBufferSource) {
        filesBuffer = new ArrayList<>(selectedIndices.length);
        this.filesBufferCommand = filesBufferSource;
        for (int i : selectedIndices) {
            filesBuffer.add(filesFoldersList.get(i));
        }
    }

    /**
     * Try to copy files from buffer to current directory.
     *
     * @throws IOException
     */
    public void copyFromBuffer() throws IOException, InterruptedException {
        isPastInterrupted = false;
        progress = 0;
        for (File file: filesBuffer) {
            if (isPastInterrupted) {
                break;
            }
            copyFile(file, new File(currentDirectory.getAbsolutePath().concat("\\").concat(file.getName())));
            if (filesBufferCommand.equals("cut")) {
                deleteFile(file);
            }
        }
    }

    /**
     * Copy file source to file dist. If dist is exist try to rewrite it.
     * If file is directory call copyDirectory function.
     *
     * @param source - path to source file
     * @param dist - path to destination file
     * @throws IOException
     */
    public void copyFile(File source, File dist) throws IOException, InterruptedException {
        if (source.isFile()) {
            if (dist.exists()){
                dist.delete();
            }
            Files.copy(source.toPath(), dist.toPath());
            //Sleep to slowdown fast copy for giving time to user for interrupt process
            Thread.currentThread().sleep(100);
        } else {
            copyDirectory(source, dist);
        }
        progress++;
    }

    /**
     * Copy directory dirSource to dirDist.
     * If dirDist is not exist, create it.
     * Try to copy all files from dirSource.
     *
     * @param dirSource - path to source directory
     * @param dirDist - path to destination directory
     * @throws IOException
     */
    public void copyDirectory(File dirSource, File dirDist) throws IOException, InterruptedException {
        try {
            if (!dirDist.exists())
                dirDist.mkdir();
            for (String fileName : dirSource.list()) {
                copyFile(new File(dirSource.getAbsolutePath().concat("\\").concat(fileName)),
                            new File(dirDist.getAbsolutePath().concat("\\").concat(fileName)));
                //Sleep to slowdown fast delete for giving time to user for interrupt process
                Thread.currentThread().sleep(100);
            }
        }
        catch (IOException e) {
            logger.error("Copy error: " + e);
        }
    }

    /**
     * Check that files in buffer intended for deleting and delete them.
    */
    public void deleteFiles() throws InterruptedException {
        progress = 0;
        isDeleteInterrupted = false;
        if (filesBufferCommand.equals("delete")) {
            for (File file : filesBuffer) {
                if (isDeleteInterrupted) {
                    break;
                }
                deleteFile(file);
                //Sleep to slowdown fast delete for giving time to user for interrupt process
                Thread.currentThread().sleep(100);
            }
        }
    }

    public void deleteFile(File file) {
        if (!file.delete()) {
            logger.error("Can't delete file.");
        }
        progress++;
    }

    public List<File> getFilesBuffer() {
        return filesBuffer;
    }

    public String getFilesBufferCommand() {
        return filesBufferCommand;
    }

    public int getProgress() {
        return progress;
    }

    /**
     * Count files and direcotries.
     * If command is copy or delete than file-action equals count files and directories.
     * If command is cut than file-action equals double of count files and directories.
     * If get directory in buffer list count recursively it content.
     *
     * @param buffer - list of files and directories for counting
     * @return count of file-action needs
     */
    public Integer getBufferSize(List<File> buffer) {
        Integer commandModifier = 1;
        if (filesBufferCommand.equals("cut")) {
            commandModifier = 2;
        }
        Integer size = 0;
        for (File file : buffer){
            size += commandModifier;
            if (file.isDirectory()) {
                size += getBufferSize(Arrays.asList(file.listFiles()));
            }
        }
        return size;
    }

    /**
     * Search files by name or part of name
     *
     * @param fileName - searching file name
     * @param currentPath - path to parent directory for searching
     * @return list of File object that match file name
     */
    public List<File> search(String fileName, String currentPath) {
        List<File> result = new ArrayList<>();
        File currentDir = new File(currentPath);
        for (File file : currentDir.listFiles()) {
            if (file.isDirectory()) {
                result.addAll(search(fileName, file.getAbsolutePath()));
            } else if (file.getName().contains(fileName))
                result.add(file);
        }
        return result;
    }
}
