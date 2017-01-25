package ru.alee.software.test;

import org.apache.log4j.Logger;
import ru.alee.software.test.model.FileManager;

import java.io.File;

public class AppStart {

    private final static Logger logger = Logger.getLogger(AppStart.class);

    public static void main(String[] args) {

        if (logger.isDebugEnabled()) {
            logger.debug("Application start.");
        }
        FileManager fileManager = new FileManager();
        FileManager fileManager2 = new FileManager("C://");
        File file = new File("D://");
        FileManager fileManager3 = new FileManager(file);
        logger.debug("print default: ");
        fileManager.printFileList();
        fileManager.printFileAbsolutePathList();
        logger.debug("print with C:// argument ");
        fileManager2.printFileList();
        fileManager2.printFileAbsolutePathList();
        logger.debug("print with File(D://) argument ");
        fileManager3.printFileList();
        fileManager3.printFileAbsolutePathList();

    }
}
