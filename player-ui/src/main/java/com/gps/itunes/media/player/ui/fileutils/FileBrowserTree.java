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
                if (mouseEvent.getClickCount() == 2) {
                    TreePath[] selectedTreePath = getFileTree().getSelectionPaths();
                    if (selectedTreePath != null && selectedTreePath.length > 0) {
                        TreePath selectionPath = selectedTreePath[selectedTreePath.length - 1];
                        FileNode fileNode = (FileNode) selectionPath.getLastPathComponent();
                        for (FileBrowserTreeEventListener fileBrowserTreeEventListener : fileBrowserTreeEventListenerList) {
                            fileBrowserTreeEventListener.onNodeDoubleClicked(fileNode);
                        }
                    }
                }
            }
        });
    }

    private void createUIComponents() {
        fileTree = new JTree() {

            @Override
            public void expandPath(final TreePath treePath) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        expandPathAsync(treePath);
                    }
                });
            }

            private void expandPathAsync(TreePath treePath) {
                super.expandPath(treePath);
            }

            @Override
            public void expandRow(final int i) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        expandRowAsync(i);
                    }
                });
            }

            private void expandRowAsync(int i) {
                super.expandRow(i);
            }
        };

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initializeComponents();
            }
        });
    }

    private void initializeComponents() {
        final String ROOT_FILE_NAME = "__ROOT__";
        File virtualRootFile = new VirtualFolder(ROOT_FILE_NAME);
        final FileNode rootNode = new FileNode(virtualRootFile);

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
                if(node != rootNode) {
                    Icon icon = fileChooser.getUI().getFileView(fileChooser).getIcon(node.getFile());
                    setIcon(icon);
                }

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

    /**
     * Represents a Virtual Folder.
     *
     */
    private class VirtualFolder extends File {

        private final String name;

        public VirtualFolder(String name) {
            super(name);
            this.name = name;
        }

        @Override
        public boolean isDirectory() {
            return true;
        }

        @Override
        public File[] listFiles() {
            return File.listRoots();
        }

        @Override
        public String[] list() {
            List<String> fileStr = new ArrayList<String>();
            for(File file : listFiles()) {
                fileStr.add(file.getName());
            }
            return fileStr.toArray(new String[fileStr.size()]);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getParent() {
            return null;
        }

        @Override
        public File getParentFile() {
            return null;
        }

        @Override
        public String getPath() {
            return name;
        }

        @Override
        public boolean isAbsolute() {
            return true;
        }

        @Override
        public String getAbsolutePath() {
            return name;
        }

        @Override
        public File getAbsoluteFile() {
            return this;
        }

        @Override
        public String getCanonicalPath() throws IOException {
            return name;
        }
    }

}
