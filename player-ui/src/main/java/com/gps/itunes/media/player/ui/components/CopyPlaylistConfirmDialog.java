package com.gps.itunes.media.player.ui.components;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class CopyPlaylistConfirmDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel dialogTextPanel;
    private JLabel dialogTextLabel;
    private JRadioButton analyzeDuplicatesRadioButton;

    private final List<ResponseHanlder> responseHanlders = new ArrayList<ResponseHanlder>();

    private static final Logger LOGGER =
            LogManager.getLogger(CopyPlaylistConfirmDialog.class);

    public CopyPlaylistConfirmDialog() {
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

    }

    private void onOK() {
        for (ResponseHanlder responseHanlder : responseHanlders) {
            responseHanlder.onResponseRecieved(false, analyzeDuplicatesRadioButton.isSelected());
        }
        dispose();
    }

    private void onCancel() {
        for (ResponseHanlder responseHanlder : responseHanlders) {
            responseHanlder.onResponseRecieved(true, analyzeDuplicatesRadioButton.isSelected());
        }
        dispose();
    }

    public void addResponseHandler(ResponseHanlder responseHanlder) {
        responseHanlders.add(responseHanlder);
    }

    public void ask() {
        this.pack();
        this.setVisible(true);
    }

    public static abstract class ResponseHanlder {

        public abstract void onResponseRecieved(boolean cancelled, boolean analyzeDuplicates);

    }

    public static void main(String[] args) {
        CopyPlaylistConfirmDialog dialog = new CopyPlaylistConfirmDialog();
        dialog.addResponseHandler(new ResponseHanlder() {
            @Override
            public void onResponseRecieved(boolean cancelled, boolean analyzeDuplicates) {
                System.out.println("Cancelled: " + cancelled + "; analyzeDuplicates: " + analyzeDuplicates);
            }
        });
        dialog.ask();
        System.exit(0);
    }
}
