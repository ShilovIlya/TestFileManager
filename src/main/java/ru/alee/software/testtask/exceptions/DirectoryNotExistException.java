package ru.alee.software.testtask.exceptions;

public class DirectoryNotExistException extends Exception {

    private String directoryName;

    public DirectoryNotExistException(String directoryName) {
        this.directoryName = directoryName;
    }

    @Override
    public String toString() {
        return "Directory " + directoryName + " not exist";
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }
}
