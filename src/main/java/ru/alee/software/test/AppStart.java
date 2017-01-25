package ru.alee.software.test;

import org.apache.log4j.Logger;

public class AppStart {

    final static Logger logger = Logger.getLogger(AppStart.class);

    public static void main(String[] args) {

        if (logger.isDebugEnabled()) {
            logger.debug("Application start.");
        }


    }
}
