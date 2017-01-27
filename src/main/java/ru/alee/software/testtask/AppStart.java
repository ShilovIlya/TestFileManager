package ru.alee.software.testtask;

import org.apache.log4j.Logger;
import ru.alee.software.testtask.exceptions.DirectoryNotExistException;
import ru.alee.software.testtask.model.FileManager;
import ru.alee.software.testtask.view.JFileManager;

/**
 * Class for starting work. It creates file manager and form.
 *
 * @author Ilya
 */
public class AppStart {

    private final static Logger logger = Logger.getLogger(AppStart.class);

    public static void main(String[] args) {
        FileManager fileManager;
        try {
            if (args.length > 0) {
                fileManager = new FileManager(args[0]);
            } else {
                fileManager = new FileManager();
            }
        } catch (DirectoryNotExistException e) {
            fileManager = new FileManager();
        }
        JFileManager jFileManager = new JFileManager(fileManager);
    }
}
