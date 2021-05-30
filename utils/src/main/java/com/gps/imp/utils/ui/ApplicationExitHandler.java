package com.gps.imp.utils.ui;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * @author leogps
 * Created on 5/29/21
 */
public class ApplicationExitHandler {

    private static final Logger LOG = Logger.getLogger(ApplicationExitHandler.class);

    public static void handle(Component parent) {
        int result = JOptionPane.showConfirmDialog(parent,"Exit the application?", "Are you sure?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION){
            System.exit(0);
        } else if (result == JOptionPane.NO_OPTION){
            LOG.info("Not exiting...");
        }
    }
}
