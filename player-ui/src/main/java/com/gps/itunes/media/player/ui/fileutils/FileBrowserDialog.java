package com.gps.itunes.media.player.ui.fileutils;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileBrowserDialog extends JDialog {

    private final List<FileBrowserDialogListener> listeners = new ArrayList<FileBrowserDialogListener>();

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private String userRequestedFilePath;
    private FileBrowserTree fileBrowserTree;

    public FileBrowserDialog(JFrame parent, String title, String userRequestedFilePath) {
        super(parent, title);
        this.userRequestedFilePath = userRequestedFilePath;


        setContentPane(contentPane);
        setModal(true);
        setTitle(title);

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
        TreePath[] selectedTreePath = fileBrowserTree.getFileTree().getSelectionPaths();
        if (selectedTreePath == null || selectedTreePath.length < 1) {
            JOptionPane.showMessageDialog(fileBrowserTree.getFileTree(), "Please select a file!!");
        } else {
            TreePath selectionPath = selectedTreePath[selectedTreePath.length - 1];
            FileNode fileNode = (FileNode) selectionPath.getLastPathComponent();
            for (FileBrowserDialogListener fileBrowserDialogListener : listeners) {
                fileBrowserDialogListener.onFileSelected(fileNode.getFile());
            }
            dispose();
        }
    }

    private void onCancel() {
// add your code here if necessary
        for (FileBrowserDialogListener fileBrowserDialogListener : listeners) {
            fileBrowserDialogListener.onCancel();
        }
        dispose();
    }

    public static void main(String[] args) {
        FileBrowserDialog dialog = new FileBrowserDialog(null, "Select file...", null);
        dialog.registerFileBrowserDialogListener(new FileBrowserDialogListener() {
            public void onFileSelected(File file) {
                System.out.println("File selected: " + file.getAbsolutePath());
            }

            public void onCancel() {
                System.out.println("Cancelled!!");
            }
        });
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }


    public void registerFileBrowserDialogListener(FileBrowserDialogListener fileBrowserDialogListener) {
        listeners.add(fileBrowserDialogListener);
    }

    private void createUIComponents() {
        fileBrowserTree = new FileBrowserTree(this.userRequestedFilePath, TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

}
