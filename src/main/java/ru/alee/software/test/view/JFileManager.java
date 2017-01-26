package ru.alee.software.test.view;

import org.apache.log4j.Logger;
import ru.alee.software.test.exceptions.DirectoryNotExistException;
import ru.alee.software.test.model.FileManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

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
    private JButton pasteButton;
    private DefaultListModel listModel;
    private JScrollPane scroll;
    private JFrame frame;

    private FileManager fileManager;

    public JFileManager(FileManager fileManager) {
        this.fileManager = fileManager;

        frame = new JFrame();
        frame.setContentPane(rootPanel);
        frame.setPreferredSize(new Dimension(1000,600));

        //scroll = new JScrollPane();
        //frame.getContentPane().add(scroll);

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
                if (filesFoldersList.getSelectedIndices().length > 0) {
                    fileManager.bufferedFiles(filesFoldersList.getSelectedIndices(), "copy");
                    pasteButton.setEnabled(true);
                }
            }
        });

        cutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (filesFoldersList.getSelectedIndices().length > 0) {
                    fileManager.bufferedFiles(filesFoldersList.getSelectedIndices(), "cut");
                    pasteButton.setEnabled(true);
                }
            }
        });

        pasteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Thread th = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Integer taskSize = fileManager.getBufferSize(fileManager.getFilesBuffer());
                        MyDialog myDialog = new MyDialog(frame, taskSize);
                        synchronized (myDialog) {
                            try {
                                myDialog.setVisible(true);
                                myDialog.dialogProgressBar.setVisible(true);

                                pasteButton.setEnabled(false);
                                try {
                                    fileManager.copyFromBuffer();
                                } catch (IOException e) {
                                    logger.error("Files copy error:" + e);
                                    newPathField.setText("Sorry, it's some copy error. Check that you're correct and try again.");
                                }

                                while (fileManager.getProgress() < taskSize) {
                                    myDialog.dialogProgressBar.setValue(fileManager.getProgress());
                                    Thread.currentThread().sleep(15);
                                }
                                myDialog.wait();
                            } catch (InterruptedException ex) {
                                logger.error("paste operation interrupted: " + ex);
                            } finally {
                                fileManager.updateFilesFolderList();
                                filesFoldersListFill();
                            }
                        }
                    }
                });
                th.start();

            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (filesFoldersList.getSelectedIndices().length > 0) {
                    fileManager.bufferedFiles(filesFoldersList.getSelectedIndices(), "delete");
                }
                fileManager.deleteFiles();
            }
        });

        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }



    public void filesFoldersListFill(){
        fileManager.updateFilesFolderList();
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
        /*
        scroll = new JScrollPane();
        scroll.setBounds(100,52,130,50);
        scroll.setViewportView(filesFoldersList);
        */
    }

    class MyDialog extends JDialog implements ActionListener {

        JButton cancelButton;
        JProgressBar dialogProgressBar;

        public MyDialog(Frame owner, Integer progressSize) {
            super(owner,"Progress Dialog");
            try {
                setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

                Dimension dialogSize = new Dimension(100, 100);
                setSize(dialogSize);

                JPanel panel = (JPanel)getContentPane();
                panel.setLayout(new FlowLayout(FlowLayout.CENTER));
                cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(this);
                panel.add(cancelButton);

                dialogProgressBar = new JProgressBar(0, progressSize);
                panel.add(dialogProgressBar);

                panel.add(BorderLayout.NORTH, new JLabel("Progress..."));

                pack();
            } catch (Exception exception) {
                logger.debug("Exception in myDialog constructor: " + exception);
            }
        }

        public synchronized void actionPerformed(ActionEvent actionEvent) {
            logger.debug("myDialog actionPerformed cancel");

            dispose();
        }
    }
}