package com.gps.itunes.media.player.ui;

import com.gps.itunes.media.player.db.ConfigPropertyDao;
import com.gps.itunes.media.player.db.DbManagerImpl;
import com.gps.itunes.media.player.db.model.ConfigProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.sql.SQLException;

public class FontDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JSlider fontSizeSlider;
    private JButton resetButton;
    private JPanel fontSizeSliderPanel;
    private JTextField sliderLabel;

    private static Logger LOGGER = LogManager.getLogger(FontDialog.class);

    public FontDialog(JFrame parent) {
        super(parent, "Set Font size");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onReset();
            }
        });
        fontSizeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                fontSizeSliderUpdated(fontSizeSlider.getValue());
            }
        });
    }

    private void fontSizeSliderUpdated(int value) {
        sliderLabel.setText(String.valueOf(value));
    }

    private void onReset() {
        try {
            resetFontSize();
        } catch (Exception ex) {
            LOGGER.warn("Failed to reset font size.", ex);
            JOptionPane.showMessageDialog(this, "Failed to reset font size.");
        }
        dispose();
    }

    private void resetFontSize() throws SQLException {
        ConfigProperty configProperty = retrieveFontSizeProperty();
        if(configProperty != null) {
            ConfigPropertyDao configPropertyDao = fetchConfigPropertyDao();
            configPropertyDao.delete(configProperty.getId());
        }
    }

    private ConfigProperty retrieveFontSizeProperty() throws SQLException {
        ConfigPropertyDao configPropertyDao = fetchConfigPropertyDao();
        if(configPropertyDao == null) {
            return null;
        }

        ConfigProperty configProperty = configPropertyDao.findByKey("font_size");
        return configProperty;
    }

    private ConfigPropertyDao fetchConfigPropertyDao() {
        if(!DbManagerImpl.getInstance().isInitiated()) {
            JOptionPane.showMessageDialog(this, "Cannot set/reset font size.");
            return null;
        }
        return new ConfigPropertyDao(DbManagerImpl.getInstance().getConnection());
    }

    private void onOK() {
        int value = fontSizeSlider.getValue();
        try {
            ConfigPropertyDao configPropertyDao = fetchConfigPropertyDao();
            if (configPropertyDao != null) {
                ConfigProperty configProperty = new ConfigProperty();
                configProperty.setProperty("font_size");
                configProperty.setValue(String.valueOf(value));
                configPropertyDao.insertOrUpdate(configProperty);
                JOptionPane.showMessageDialog(this, "Font size set successfully. Restart to see the changes.");
            }
        } catch (Exception ex) {
            LOGGER.warn("Failed to set font size.", ex);
            JOptionPane.showMessageDialog(this, "Failed to set font size.");
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public void showNow() {
        try {
            ConfigProperty configProperty = retrieveFontSizeProperty();
            if (configProperty != null) {
                int value = Integer.parseInt(configProperty.getValue());
                fontSizeSlider.setValue(value);
            }
        } catch (Exception ex) {
            LOGGER.info("Could not fetch font size.");
        }
        fontSizeSliderUpdated(fontSizeSlider.getValue());
        this.pack();
        this.setVisible(true);
    }
}
