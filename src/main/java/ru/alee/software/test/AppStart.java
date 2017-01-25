package ru.alee.software.test;

import org.apache.log4j.Logger;
import ru.alee.software.test.exceptions.DirectoryNotExistException;
import ru.alee.software.test.model.FileManager;
import ru.alee.software.test.view.JFileManager;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AppStart {

    private final static Logger logger = Logger.getLogger(AppStart.class);

    public static void main(String[] args) {

        if (logger.isDebugEnabled()) {
            logger.debug("Application start.");
        }

        FileManager fileManager = new FileManager();
        JFileManager jFileManager = new JFileManager(fileManager);
    }
}
