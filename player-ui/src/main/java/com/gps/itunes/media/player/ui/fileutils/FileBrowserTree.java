package com.gps.itunes.media.player.ui.fileutils;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leogps on 10/11/15.
 */
public class FileBrowserTree extends JPanel {
    private JTree fileTree;
    private JScrollPane scrollPane;
    private JPanel wrapperPanel;
    private JPanel contentPanel;
    private JFileChooser fileChooser = new JFileChooser();
    private final String userRequestedFilePath;

    private int selectionMode;

    private final List<FileBrowserTreeEventListener> fileBrowserTreeEventListenerList = new ArrayList<FileBrowserTreeEventListener>();

    public FileBrowserTree(String userRequestedFilePath, int selectionMode) {
        this.userRequestedFilePath = userRequestedFilePath;
        this.selectionMode = selectionMode;

        scrollPane.setViewportView(fileTree);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent adjustmentEvent) {
                if (!adjustmentEvent.getValueIsAdjusting()) {
                    fileTree.repaint();
                }
            }
        });

        fileTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                if(mouseEvent.getClickCount() == 2) {
                    TreePath[] selectedTreePath = getFileTree().getSelectionPaths();
                    if (selectedTreePath != null && selectedTreePath.length > 0) {
                        TreePath selectionPath = selectedTreePath[selectedTreePath.length - 1];
                        FileNode fileNode = (FileNode) selectionPath.getLastPathComponent();
                        for(FileBrowserTreeEventListener fileBrowserTreeEventListener : fileBrowserTreeEventListenerList) {
                            fileBrowserTreeEventListener.onNodeDoubleClicked(fileNode);
                        }
                    }
                }
            }
        });
    }

    private void createUIComponents() {
        fileTree = new JTree();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initializeComponents();
            }
        });
    }

    private void initializeComponents() {
        File file = new File(File.separator);
        final FileNode rootNode = new FileNode(file);
        fileTree.setModel(new DefaultTreeModel(rootNode));
        fileTree.getSelectionModel().setSelectionMode(selectionMode);

        fileTree.setCellRenderer(new DefaultTreeCellRenderer() {

            @Override
            public Component getTreeCellRendererComponent(JTree tree,
                                                          Object value, boolean selected, boolean expanded,
                                                          boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
                FileNode node = (FileNode) value;
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();
                }
                Icon icon = fileChooser.getUI().getFileView(fileChooser).getIcon(node.getFile());
                setIcon(icon);

                String fileName = FileSystemView.getFileSystemView().getSystemDisplayName(node.getFile());
                setText(fileName);
                setToolTipText(fileName);
                return this;
            }
        });
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                String defaultPath = null;
                if (userRequestedFilePath != null) {
                    File userRequestedFile = new File(userRequestedFilePath);
                    if (userRequestedFile.exists()) {
                        defaultPath = userRequestedFilePath;
                    }
                }

                if (defaultPath == null && System.getProperty("user.home") != null) {
                    defaultPath = System.getProperty("user.home");
                }

                if (defaultPath == null) {
                    defaultPath = File.separator;
                }

                File defaultFile = new File(defaultPath);
                if (defaultFile.exists()) {
                    try {
                        TreePath path = rootNode.getPathTo(defaultFile);
                        fileTree.expandPath(path);
                        fileTree.setSelectionPath(path);
                        fileTree.scrollPathToVisible(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    public JTree getFileTree() {
        return fileTree;
    }

    public void registerFileBrowserTreeEventListener(FileBrowserTreeEventListener fileBrowserTreeEventListener) {
        fileBrowserTreeEventListenerList.add(fileBrowserTreeEventListener);
    }

}
