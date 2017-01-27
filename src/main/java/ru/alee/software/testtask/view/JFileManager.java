package ru.alee.software.testtask.view;

import org.apache.log4j.Logger;
import ru.alee.software.testtask.exceptions.DirectoryNotExistException;
import ru.alee.software.testtask.model.FileManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
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
    private JButton pasteButton;
    private DefaultListModel listModel;
    private JScrollPane scroll;
    private JButton searchFileButton;
    private JFrame frame;

    private FileManager fileManager;

    public JFileManager(FileManager fileManager) {
        this.fileManager = fileManager;

        frame = new JFrame();
        frame.setContentPane(rootPanel);
        frame.setPreferredSize(new Dimension(1200,800));


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
                List<String> strList = new ArrayList<>();
                for (File file : fileManager.getFilesFoldersList()){
                    strList.add(file.getName());
                }
                fillList(strList);
            }
        });

        sortByNameDescButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fileManager.sortByNameDesc();
                List<String> strList = new ArrayList<>();
                for (File file : fileManager.getFilesFoldersList()){
                    strList.add(file.getName());
                }
                fillList(strList);
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

        searchFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = newPathField.getText();
                List<File> searchResult = fileManager.search(fileName, currentDirectoryPath.getText());
                List<String> strList = new ArrayList<>();
                for (File file : searchResult){
                    strList.add(file.getAbsolutePath());
                }
                fillList(strList);
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
                        logger.debug("taskSize = " + taskSize);
                        synchronized (myDialog) {
                            try {
                                myDialog.setVisible(true);
                                myDialog.dialogProgressBar.setVisible(true);

                                pasteButton.setEnabled(false);
                                Thread copyThread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            fileManager.copyFromBuffer();
                                        } catch (IOException e) {
                                            logger.error("Files copy error:" + e);
                                            newPathField.setText("Sorry, it's some copy error. Check that you're correct and try again.");
                                        } catch (InterruptedException e) {
                                            logger.error("Copy interrupted " + e);
                                        }
                                    }
                                });
                                myDialog.setProgressThread(copyThread, "paste");
                                copyThread.start();

                                int progress = fileManager.getProgress();
                                while ((progress <= taskSize-1) && (!fileManager.isPastInterrupted())) {
                                    progress = fileManager.getProgress();
                                    myDialog.dialogProgressBar.setValue(progress);
                                }
                                myDialog.progressStatus.setText("End");
                                copyThread.interrupt();
                                myDialog.dispose();
                            } catch (Exception ex) {
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
                    Thread th = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Integer taskSize = fileManager.getBufferSize(fileManager.getFilesBuffer());
                            MyDialog myDialog = new MyDialog(frame, taskSize);
                            logger.debug("taskSize = " + taskSize);
                            synchronized (myDialog) {
                                try {
                                    myDialog.setVisible(true);
                                    myDialog.dialogProgressBar.setVisible(true);

                                    pasteButton.setEnabled(false);
                                    Thread deleteThread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                fileManager.deleteFiles();
                                            } catch (InterruptedException e) {
                                                logger.error("Copy interrupted " + e);
                                            }
                                        }
                                    });
                                    myDialog.setProgressThread(deleteThread, "delete");
                                    deleteThread.start();

                                    int progress = fileManager.getProgress();
                                    while ((progress <= taskSize - 1) && (!fileManager.isDeleteInterrupted())) {
                                        progress = fileManager.getProgress();
                                        myDialog.dialogProgressBar.setValue(progress);
                                    }

                                    myDialog.progressStatus.setText("End");

                                    myDialog.dispose();
                                } catch (Exception ex) {
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
            }
        });

        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }



    public void filesFoldersListFill(){
        fileManager.updateFilesFolderList();
        List<String> strList = new ArrayList<>();
        for (File file : fileManager.getFilesFoldersList()) {
            strList.add(file.getName());
        }
        fillList(strList);
    }

    public void fillList(List<String> list) {
        listModel.removeAllElements();
        for (String str: list) {
            listModel.addElement(str);
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

    class MyDialog extends JDialog implements ActionListener {

        JButton cancelButton;
        JProgressBar dialogProgressBar;
        JLabel progressStatus;
        Thread progressThread;
        String command;

        public void setProgressThread(Thread progressThread, String command) {
            this.progressThread = progressThread;
            this.command = command;
        }

        public MyDialog(Frame owner, Integer progressSize) {
            super(owner,"Progress Dialog");
            try {
                setDefaultCloseOperation(DISPOSE_ON_CLOSE);

                Dimension dialogSize = new Dimension(100, 100);
                setSize(dialogSize);

                JPanel panel = (JPanel)getContentPane();
                panel.setLayout(new FlowLayout(FlowLayout.CENTER));

                dialogProgressBar = new JProgressBar(0, progressSize);
                panel.add(dialogProgressBar);

                progressStatus = new JLabel("Progress...");
                panel.add(BorderLayout.NORTH, progressStatus);

                cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(this);
                panel.add(cancelButton);

                pack();
            } catch (Exception exception) {
                logger.debug("Exception in myDialog constructor: " + exception);
            }
        }

        public synchronized void actionPerformed(ActionEvent actionEvent) {
            if (command.equals("paste")) {
                fileManager.setPastInterrupted(true);
            }
            if (command.equals("delete")) {
                fileManager.setDeleteInterrupted(true);
            }
            dispose();
        }
    }
}