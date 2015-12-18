package com.gps.itunes.media.player.vlcj.ui.player;

import com.gps.youtube.dl.process.AsyncProcess;

import javax.swing.*;
import java.awt.event.*;

public class InterruptableProcessDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonCancel;
    private JLabel dialogText;
    private JProgressBar progressBar1;

    private final AsyncProcess asyncProcess;

    public InterruptableProcessDialog(final AsyncProcess asyncProcess) {
        this.asyncProcess = asyncProcess;
        setContentPane(contentPane);
        setModal(true);

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

        setTitle("Processing...");
    }

    private void onCancel() {
        asyncProcess.interruptProcess();
        dispose();
    }

    public void setMessage(String message) {
        dialogText.setText(message);
    }

    public void showDialog() {
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        InterruptableProcessDialog dialog = new InterruptableProcessDialog(null);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public void close() {
        dispose();
    }
}
