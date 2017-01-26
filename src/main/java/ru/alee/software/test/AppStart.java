package ru.alee.software.test;

import org.apache.log4j.Logger;
import ru.alee.software.test.exceptions.DirectoryNotExistException;
import ru.alee.software.test.model.FileManager;
import ru.alee.software.test.view.JFileManager;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Class for starting work. It creates file manager and form.
 *
 * @author Ilya
 */
public class AppStart {

    private final static Logger logger = Logger.getLogger(AppStart.class);

    public static void main(String[] args) {
        logger.debug("Application start!");
        try {
            FileManager fileManager = new FileManager("D:\\log\\testfolder");
            /*for (int i = 0; i < 100; i++) {
                File tmpFile = new File("D:\\log\\testfolder\\a".concat(String.valueOf(i)));
                tmpFile.createNewFile();
            }*/
            JFileManager jFileManager = new JFileManager(fileManager);
        } catch (DirectoryNotExistException e) {
            logger.debug("cant open D:\\log\\testfolder" + e);
        }// catch (IOException e) {
         //   logger.debug("cant create ai file" + e);
        //}
    }
}
