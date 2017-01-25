package ru.alee.software.test.view;

import org.apache.log4j.Logger;
import ru.alee.software.test.exceptions.DirectoryNotExistException;
import ru.alee.software.test.model.FileManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manager form.
 *
 * Have field with current directory, field for path to change directory.
 * Buttons for navigation, sorting and managing files and directories.
 *
 * @author Ilya
 */
public class JFileManager{

    private static final Logger logger = Logger.getLogger(JFileManager.class);

    private JPanel rootPanel;

    private JLabel currentDirectoryLabel;
    private JTextArea currentDirectoryPath;

    private JLabel newPathLabel;
    private JTextArea newPathField;

    private JButton moveToParentButton;
    private JButton sortByNameAscButton;
    private JButton sortByNameDescButton;
    private JButton directoryChangeButton;
    private JButton makeDirectoryButton;
    private JButton copyButton;
    private JButton cutButton;
    private JButton deleteButton;

    private JList filesFoldersList;
    private DefaultListModel listModel;

    private FileManager fileManager;

    public JFileManager(FileManager fileManager) {
        this.fileManager = fileManager;

        JFrame frame = new JFrame();
        frame.setContentPane(rootPanel);
        frame.setPreferredSize(new Dimension(1000,600));

        updateCurrentDirectoryPath();

        moveToParentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    fileManager.moveToParentDirectory();
                    filesFoldersListFill();
                    updateCurrentDirectoryPath();
                } catch (DirectoryNotExistException e) {
                    logger.debug(e);
                }
            }
        });

        sortByNameAscButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fileManager.sortByNameAsc();
                filesFoldersListFill();
            }
        });

        sortByNameDescButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fileManager.sortByNameDesc();
                filesFoldersListFill();
            }
        });

        directoryChangeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    fileManager.changeDirectory(newPathField.getText());
                    updateCurrentDirectoryPath();
                    filesFoldersListFill();
                } catch (DirectoryNotExistException e) {
                    newPathField.setText("This path not exist. Please, enter new path.");
                    logger.debug(e);
                }
            }
        });

        makeDirectoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fileManager.makeDirectory(newPathField.getText());
                filesFoldersListFill();
            }
        });

        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (copyButton.getText().equals("copy")) {
                    if (filesFoldersList.getSelectedIndices().length > 0) {
                        copyButton.setText("paste");
                        fileManager.saveInMemoryFiles(filesFoldersList.getSelectedIndices());
                    }
                } else if (copyButton.getText().equals("paste")) {
                    try {
                        fileManager.pasteFromMemoryFiles();
                    } catch (IOException e) {
                        logger.error("Files copy error:" + e);
                        newPathField.setText("Sorry, it's some copy error. Check that you're correct and try again.");
                    }
                    copyButton.setText("copy");
                }
            }
        });

        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }



    public void filesFoldersListFill(){
        listModel.removeAllElements();
        for (File file : fileManager.getFilesFoldersList()) {
            listModel.addElement(file.getName());
        }
    }

    public void updateCurrentDirectoryPath() {
        currentDirectoryPath.setText(fileManager.getCurrentDirectory().getAbsolutePath());
    }

    private void createUIComponents() {
        listModel = new DefaultListModel();
        filesFoldersListFill();
        filesFoldersList = new JList(listModel);
    }

}
