package ru.alee.software.test.view;

import javax.swing.*;
import java.awt.*;

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

    public JFileManager() {
        JFrame frame = new JFrame();
        frame.setContentPane(rootPanel);

        frame.setPreferredSize(new Dimension(1200,900));

        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
    public static void createGUI(){
        new JFileManager();
    }

}
