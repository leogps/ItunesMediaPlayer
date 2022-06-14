package com.gps.imp.utils.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.*;

public class NetworkFileOpenDialog extends JDialog {

    private static Logger LOG = LogManager.getLogger(NetworkFileOpenDialog.class);

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton pasteFromClipboardButton;
    private JTextArea inputTextArea;
    private JLabel pasteFromClipboardLabel;
    private NetworkFileOpenEventListener listener;

    public NetworkFileOpenDialog(NetworkFileOpenEventListener listener) {
        this.listener = listener;
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
        pasteFromClipboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                handleClipboardPasteEvent();
            }
        });
        inputTextArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    if (e.getModifiersEx() > 0) {
                        inputTextArea.transferFocusBackward();
                    } else {
                        inputTextArea.transferFocus();
                    }
                    e.consume();
                }
            }
        });
        pasteFromClipboardLabel.setIcon(new ImageIcon(NetworkFileOpenDialog.class.getClassLoader().getResource("icons/paste.png")));
    }

    private void handleClipboardPasteEvent() {
        try {
            String data = (String) Toolkit.getDefaultToolkit()
                    .getSystemClipboard().getData(DataFlavor.stringFlavor);
            LOG.info("Clipboard: " + data);
            inputTextArea.setText(data);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private void onOK() {
        listener.onOk(inputTextArea.getText());
        dispose();
    }

    private void onCancel() {
        listener.onCancel();
        dispose();
    }
}
