package ru.alee.software.test.view;

import ru.alee.software.test.model.FileManager;
import java.io.File;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class JFileManager{
    private JPanel rootPanel;
    private JTextArea currentDirectoryPath;
    private JButton moveToParentButton;
    private JButton sortByNameAscButton;
    private JButton sortByNameDescButton;
    private JList filesFoldersList;
    private JTextArea newPathField;
    private JLabel currentDirectoryLabel;
    private JLabel newPathLabel;

    private FileManager fileManager;
    public JFileManager(FileManager fileManager) {
        this.fileManager = fileManager;

        JFrame frame = new JFrame();
        frame.setContentPane(rootPanel);

        frame.setPreferredSize(new Dimension(1200,900));

        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void filesFoldersListFill(){
        for (File file : fileManager.getFilesFoldersList()) {
            filesFoldersList
        }
    }

}
