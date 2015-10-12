/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gps.itunes.media.player.ui;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

/**
 *
 * @author leogps
 */
public class UserInformer {

    private final JComponent component;

    public UserInformer(final JComponent component) {
        this.component = component;
    }

    public void informUser(final String message) {
        JOptionPane.showMessageDialog(component, message);
    }

    public boolean askUser(final String message) {
        final String[] options = {"OK", "CANCEL"};
        
        return 0 == JOptionPane.showOptionDialog(null, message, "Warning",
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                null, options, options[1]);
    }
}
