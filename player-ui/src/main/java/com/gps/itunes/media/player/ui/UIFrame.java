package com.gps.itunes.media.player.ui;

import com.gps.imp.utils.Constants;
import com.gps.imp.utils.JavaVersionUtils;
import com.gps.imp.utils.ui.AsyncTaskListener;
import com.gps.imp.utils.ui.InterruptableAsyncTask;
import com.gps.imp.utils.ui.InterruptableProcessDialog;
import com.gps.itunes.lib.items.playlists.Playlist;
import com.gps.itunes.lib.items.tracks.Track;
import com.gps.itunes.lib.parser.utils.OSInfo;
import com.gps.itunes.lib.parser.utils.PropertyManager;
import com.gps.itunes.media.player.ui.components.TracksContextMenu;
import com.gps.itunes.media.player.ui.events.UIFrameEventListener;
import com.gps.imp.utils.ui.fileutils.FileBrowserTree;
import com.gps.imp.utils.ui.fileutils.FileBrowserTreeEventListener;
import com.gps.imp.utils.ui.fileutils.FileNode;
import com.gps.itunes.media.player.ui.handlers.StatusMessageWriter;
import com.gps.itunes.media.player.dto.PlaylistHolder;
import com.gps.itunes.media.player.dto.TrackHolder;
import com.gps.itunes.media.player.ui.tablehelpers.models.PlaylistTableModel;
import com.gps.itunes.media.player.ui.tablehelpers.models.TracksTableModel;
import com.gps.itunes.media.player.updater.UpdateResult;
import com.gps.itunes.media.player.vlcj.VLCJUtils;
import com.gps.itunes.media.player.vlcj.ui.player.PlayerControlPanel;
import com.gps.itunes.media.player.vlcj.ui.player.events.PlayerKeyEventListener;
import com.gps.itunes.media.player.vlcj.utils.YoutubeDLUtils;
import com.gps.youtube.dl.update.YoutubeDLUpdater;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import javax.swing.ImageIcon;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by leogps on 10/11/15.
 */
public class UIFrame extends JFrame {
    private static final Logger LOG = Logger.getLogger(UIFrame.class);
    protected static final String ERROR_OCCURRED_MSG = "Error occurred!!";

    private UIFrame instance;

    private JPanel wrapperPanel;
    private JPanel contentPanel;
    private JPanel bottomPanel;
    private JPanel topPanel;
    private JPanel bodyPanel;
    private JPanel leftHeaderPanel;
    private JPanel centerHeaderPanel;
    private JPanel rightHeaderPanel;
    private JLabel titleLabel;
    private JLabel versionLabel;
    private JPanel titlePanel;
    private JPanel contactPanel;
    private JLabel emailLabel;
    private JPanel bottomTopPanel;
    private JPanel tasksPanel;

    private PlayerControlPanel playerControlPanel;

    private JTextField searchBox;
    private static final String SEARCH_MSG = "Search selected playlist(s)...";
    boolean disableSearchBoxUpdateEvt = false;

    private JPanel tablesPanel;
    private JTable playlistTable;
    private JTable tracksTable;
    private JPanel statusPanel;
    private JLabel statusLabel;
    private JTextArea statusTextArea;
    private JProgressBar progressBar;
    private JScrollPane playlistScrollPane;
    private JScrollPane tracksTableScrollPane;
    private JPanel tracksTablePanel;
    private JPanel playlistTablePanel;
    private JLabel tracksTableHeadingLabel;
    private JPanel tracksTableHeadingPanel;
    private JPanel searchPanel;
    private NowPlayingPanel nowPlayingPanel;
    private JPanel fileBrowserWrapperPanel;
    private JPanel fileBrowserHeaderPanel;
    private JLabel fileBrowserHeaderLabel;
    private FileBrowserTree fileBrowserTree;
    private JLabel refreshLabel;
    private JPanel fileBrowserTreePanel;

    private List<UIFrameEventListener> uiFrameEventListenerList = new ArrayList<UIFrameEventListener>();

    protected static final ResourceBundle RESOURCE_BUNDLE =
            ResourceBundle.getBundle("ui");

    private UIMenuBar uiMenuBar;

    private PlayerKeyEventListener playerKeyEventListener;

    // Refresh Icons
    private static final Icon refreshIconBlue = new ImageIcon(UIFrame.class.getClassLoader().getResource("images/refresh-icon-blue.png"));
    private static final Icon refreshIconGreen = new ImageIcon(UIFrame.class.getClassLoader().getResource("images/refresh-icon-green.png"));
    private static final Icon refreshIconRed = new ImageIcon(UIFrame.class.getClassLoader().getResource("images/refresh-icon-red.png"));

    public UIFrame() {
        super(RESOURCE_BUNDLE.getString("title"));


        pack();
        instance = this;

        if (OSInfo.isOSMac()) {
//            try {
//                UIFrame.class.forName("com.gps.itunes.media.player.OSXUtils");
//            } catch (ClassNotFoundException e) {
//                LOG.debug(e.getMessage(), e);
//            }
        }

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(new Rectangle(0, 0, 860, 640));
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        //setIconImage(fetchIconImage());
        setMinimumSize(new Dimension(850, 450));
        //setPreferredSize(new Dimension(1024, 660));

        titleLabel.setFont(new Font("Lucida Grande", 1, 14)); // NOI18N
        titleLabel.setText(RESOURCE_BUNDLE.getString("name"));

        versionLabel.setText(RESOURCE_BUNDLE.getString("version"));

        nowPlayingPanel.getTrackNameLabel().setText(Constants.EMPTY);
        nowPlayingPanel.getTrackAlbumNameLabel().setText(Constants.EMPTY);
        nowPlayingPanel.getTrackArtistNameLabel().setText(Constants.EMPTY);

        // emailLabel.setForeground(new Color(0, 0, 255));
//        emailLabel.setText(RESOURCE_BUNDLE.getString("email"));
//        emailLabel.setToolTipText("Click to email the author");
//        emailLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        searchBox.setForeground(new Color(153, 153, 153));
        searchBox.setText(SEARCH_MSG);
        searchBox.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                searchBoxFocussed(evt);
            }

            public void focusLost(FocusEvent evt) {
                searchBoxBlurred(evt);
            }
        });

        playlistScrollPane.setViewportView(playlistTable);
        if (!JavaVersionUtils.isGreaterThan6()) {
            // In Java 6 for Mac on El Capitan, the scroll event does not repaint the table contents correctly.
            playlistScrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
                @Override
                public void adjustmentValueChanged(AdjustmentEvent adjustmentEvent) {
                    if (!adjustmentEvent.getValueIsAdjusting()) {
                        playlistTable.repaint();
                    }
                }
            });
        }

        tracksTableScrollPane.setViewportView(tracksTable);
        if (!JavaVersionUtils.isGreaterThan6()) {
            // In Java 6 for Mac on El Capitan, the scroll event does not repaint the table contents correctly.
            tracksTableScrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
                @Override
                public void adjustmentValueChanged(AdjustmentEvent adjustmentEvent) {
                    if (!adjustmentEvent.getValueIsAdjusting()) {
                        tracksTable.repaint();
                    }
                }
            });
        }

        attachEvents();

        add(wrapperPanel);
        bottomPanel.addKeyListener(new KeyAdapter() {
        });

        this.setJMenuBar(uiMenuBar);
        if (!Main.isVlcjInitSucceeded()) {
            uiMenuBar.getOpenMenuItem().setEnabled(false);
            uiMenuBar.getOpenNetworkFileMenuItem().setEnabled(false);
            uiMenuBar.getIncreaseVolumeMenuItem().setEnabled(false);
            uiMenuBar.getDecreaseVolumeMenuItem().setEnabled(false);
            uiMenuBar.getGoToMenuItem().setEnabled(false);
            uiMenuBar.getCopyTracksMenuItem().setEnabled(false);
        }

        /*
        * Adding right-click context menu for JTable.
         */
        final ActionListener tracksContextEventListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JMenuItem source = (JMenuItem) actionEvent.getSource();
                if (source.getText().equals(TracksContextMenu.INFORMATION)) {
                    // TODO: Implement Track Information popup.
                } else if (source.getText().equals(TracksContextMenu.PLAY_TEXT)) {
                    for (UIFrameEventListener uiFrameEventListener : uiFrameEventListenerList) {
                        uiFrameEventListener.onTracksPlayRequested(getSelectedTracks());
                    }
                }
            }
        };

        tracksTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger() && tracksTable.getSelectedRowCount() > 0) {
                    e.consume();

                    TracksContextMenu contextMenu = new TracksContextMenu(tracksTable.getSelectedRowCount());

                    // Add popup selection event listener
                    contextMenu.getInformationMenu().addActionListener(tracksContextEventListener);
                    contextMenu.getPlayMenu().addActionListener(tracksContextEventListener);

                    e.translatePoint(instance.getContentPane().getX(), instance.getContentPane().getY());
                    contextMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        refreshLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                FileSystemRefreshTask fileSystemRefreshTask = new FileSystemRefreshTask();
                final InterruptableProcessDialog interruptableProcessDialog = new InterruptableProcessDialog(fileSystemRefreshTask, true);

                fileSystemRefreshTask.registerListener(new AsyncTaskListener() {
                    @Override
                    public void onSuccess(InterruptableAsyncTask interruptableAsyncTask) {
                        interruptableProcessDialog.close();
                    }

                    @Override
                    public void onFailure(InterruptableAsyncTask interruptableAsyncTask) {
                        interruptableProcessDialog.close();
                        JOptionPane.showMessageDialog(null, "File System refresh failed.");
                    }
                });

                fileSystemRefreshTask.execute();
                interruptableProcessDialog.showDialog();
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                refreshLabel.setIcon(refreshIconRed);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                refreshLabel.setIcon(refreshIconGreen);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                refreshLabel.setIcon(refreshIconBlue);
            }
        });
    }

    protected class FileSystemRefreshTask implements InterruptableAsyncTask<Void, Void> {

        private final InterruptableAsyncTask _this;

        public FileSystemRefreshTask() {
            _this = this;
        }

        private Runnable refreshTask = new Runnable() {
            @Override
            public void run() {
                try {

                    final TreePath selectedPath = fileBrowserTree.getCurrentSelectionPath();
                    fileBrowserTree.refresh();

                    if(selectedPath != null) {
                        fileBrowserTree.select(selectedPath);
                        fileBrowserTree.getJFileTree().expandPathAsync(selectedPath, new FileBrowserTree.JFileTreeNodeExpansionProcessor() {
                            @Override
                            public void onNodeExpansion() {
                                fileBrowserTree.getJFileTree().scrollPathToVisible(selectedPath);
                                for(AsyncTaskListener asyncTaskListener : asyncTaskListenerList) {
                                    asyncTaskListener.onSuccess(_this);
                                }
                            }
                        });
                    }

                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                    for(AsyncTaskListener asyncTaskListener : asyncTaskListenerList) {
                        asyncTaskListener.onFailure(_this);
                    }
                }
            }
        };
        private Thread executableThread;
        private final List<AsyncTaskListener> asyncTaskListenerList = new ArrayList<AsyncTaskListener>();

        public Void execute() {
            if(executableThread != null && executableThread.isAlive()) {
                throw new IllegalStateException("A previously submitted task is still getting executed.");
            }
            executableThread = new Thread(refreshTask);
            executableThread.start();
            return null;
        }

        @Override
        public void registerListener(AsyncTaskListener asyncTaskListener) {
            asyncTaskListenerList.add(asyncTaskListener);
        }

        @Override
        public void interrupt() {
            if(executableThread != null & executableThread.isAlive()) {
                executableThread.interrupt();
            }
        }

        @Override
        public boolean isInterrupted() {
            return executableThread.isInterrupted();
        }

        @Override
        public Void getResult() {
            return null;
        }
    }

    /**
     * @param window
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void enableOSXFullscreen(Window window) {
        if (window != null) {
            try {
                Class util = Class.forName("com.apple.eawt.FullScreenUtilities");
                Class params[] = new Class[]{Window.class, Boolean.TYPE};
                Method method = util.getMethod("setWindowCanFullScreen", params);
                method.invoke(util, window, true);
            } catch (ClassNotFoundException e1) {
                LOG.error("OS X Fullscreen FAIL", e1);
            } catch (Exception e) {
                LOG.error("OS X Fullscreen FAIL", e);

            }
        }
    }

    private void attachEvents() {

//        emailLabel.addMouseListener(new java.awt.event.MouseAdapter() {
//            public void mouseClicked(MouseEvent evt) {
//                emailLinkHandler(evt);
//            }
//        });

        //PlaylistTableStuff
        this.playlistTable.getSelectionModel().
                addListSelectionListener(new ListSelectionListener() {

                    @Override
                    public void valueChanged(final ListSelectionEvent lse) {

                        if (playlistTable.getSelectedRowCount() < 1) {
                            tracksTableHeadingLabel.setText("");
                            uiMenuBar.getCopyPlaylistsMenuItem().setEnabled(false);
                            return;
                        }

                        clearSearchBox();

                        if (!lse.getValueIsAdjusting()
                                && lse.getSource() == playlistTable.getSelectionModel()) {

                            LOG.debug(playlistTable.getSelectedRows().length + " playlists selected.");

                            if (playlistTable.getSelectedRows().length > 1) {

                            } else {
                                final Playlist playlist =
                                        ((PlaylistHolder) playlistTable.getValueAt(playlistTable.getSelectedRows()[0], 0)).getPlaylist();


                            }

                            for (UIFrameEventListener uiFrameEventListener : uiFrameEventListenerList) {
                                uiFrameEventListener.onPlaylistSelectedEvent(instance);
                            }

                        }

                    }
                });


        this.playlistTable.setRowSorter(new TableRowSorter(this.playlistTable.getModel()));


        //TracksTableStuff
        ((TracksTableModel) this.tracksTable.getModel()).setTracksTableRowSorter(tracksTable);


        final int doubleClickValue = 2;
        this.tracksTable.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent me) {
            }

            @Override
            public void mousePressed(MouseEvent me) {
                if (!me.isPopupTrigger() && !SwingUtilities.isRightMouseButton(me) && me.getClickCount() == doubleClickValue && !me.isConsumed()) {
                    JTable table = (JTable) me.getSource();
                    Point p = me.getPoint();
                    int row = table.rowAtPoint(p);
                    if (me.getClickCount() == 2 && row >= 0) {

                        final TrackHolder holder = (TrackHolder) tracksTable.getValueAt(row, TracksTableModel.getHolderIndex());
                        if(holder != null) {
                            final List<Track> trackList = getSelectedTracks();
                            if(trackList != null && !trackList.isEmpty()) {
                                for (UIFrameEventListener uiFrameEventListener : uiFrameEventListenerList) {
                                    uiFrameEventListener.onTracksPlayRequested(trackList);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                //
            }

            @Override
            public void mouseEntered(MouseEvent me) {
                //
            }

            @Override
            public void mouseExited(MouseEvent me) {
                //
            }


        });

        /**
         * Tasks Panel Events
         */

        // Search box
        searchBox.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent de) {
                updated(de);
            }

            @Override
            public void removeUpdate(DocumentEvent de) {
                updated(de);
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
                //Nothing
            }

            public void updated(final DocumentEvent de) {
                if (!disableSearchBoxUpdateEvt) {
                    Document doc = de.getDocument();
                    try {
                        String searchQuery = doc.getText(0, doc.getLength()).trim().toUpperCase();
                        LOG.debug(searchQuery);

                        if (searchQuery.equalsIgnoreCase(SEARCH_MSG)) {
                            searchQuery = null;
                        }

                        for (UIFrameEventListener uiFrameEventListener : uiFrameEventListenerList) {
                            uiFrameEventListener.onSearch(searchQuery, instance);
                        }

                    } catch (BadLocationException ex) {
                        LOG.error("Error occurred when reading search query", ex);
                    }
                }
            }

        });

        uiMenuBar = new UIMenuBar();
        if (Main.isVlcjInitSucceeded()) {
            // Setting Menu bar event listener.
            uiMenuBar.getOpenMenuItem().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    // Propagating event as UI Frame Event.
                    for (UIFrameEventListener uiFrameEventListener : uiFrameEventListenerList) {
                        uiFrameEventListener.onFileOpenRequested();
                    }
                }
            });
            uiMenuBar.getOpenMenuItem().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

            uiMenuBar.getOpenNetworkFileMenuItem().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    // Propagating event as UI Frame Event.
                    for (UIFrameEventListener uiFrameEventListener : uiFrameEventListenerList) {
                        uiFrameEventListener.onNetworkFileOpenRequested();
                    }
                }
            });
            uiMenuBar.getOpenNetworkFileMenuItem().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        }

        uiMenuBar.getIncreaseVolumeMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Main.getItunesMediaPlayer().handleVolumeIncreasedEvent(Constants.DEFAULT_VOLUME_CHANGE);
            }
        });
        uiMenuBar.getIncreaseVolumeMenuItem().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.SHIFT_MASK));

        uiMenuBar.getDecreaseVolumeMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Main.getItunesMediaPlayer().handleVolumeDecreasedEvent(Constants.DEFAULT_VOLUME_CHANGE);
            }
        });
        uiMenuBar.getDecreaseVolumeMenuItem().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.SHIFT_MASK));

        uiMenuBar.getGoToMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Main.getItunesMediaPlayer().handleGoToEvent();
            }
        });
        uiMenuBar.getGoToMenuItem().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        uiMenuBar.getReloadLibraryMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // Propagating event as UI Frame Event.
                if (JOptionPane.showConfirmDialog(instance, "Do you really want to reload the library?", "Warning", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    for (UIFrameEventListener uiFrameEventListener : uiFrameEventListenerList) {
                        uiFrameEventListener.onReloadLibraryRequested(instance);
                    }
                }
            }
        });
        uiMenuBar.getReloadLibraryMenuItem().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        uiMenuBar.getCopyPlaylistsMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // Propagating event as UI Frame Event.
                for (UIFrameEventListener uiFrameEventListener : uiFrameEventListenerList) {
                    uiFrameEventListener.onCopyPlaylistRequested(instance);
                }
            }
        });
        uiMenuBar.getCopyPlaylistsMenuItem().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        uiMenuBar.getCopyPlaylistsMenuItem().setEnabled(false); // initially, no playlist is selected.

        uiMenuBar.getVlcMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                StringBuffer message = new StringBuffer();
                if(VLCJUtils.isVlcInitSucceeded()) {

                    message.append("VLC initialized successfully.");
                    message.append(String.format("\nVLC Version: %s", VLCJUtils.getVlcVersion()));
                    message.append("\n" + RESOURCE_BUNDLE.getString("vlc.link"));
                    //TODO: JEditorPane for hyperlink.

                } else {
                    message.append("VLC failed to initialize.");
                }
                JOptionPane.showMessageDialog(null, message, "About VLC Engine", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        uiMenuBar.getVlcjMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringBuffer message = new StringBuffer();
                if(VLCJUtils.isVlcInitSucceeded()) {

                    message.append(String.format("VLCJ version %s", VLCJUtils.getVlcJVersion()));
                    message.append("\n" + RESOURCE_BUNDLE.getString("vlcj.link") + "\n");

                    message.append("\nVLC initialized successfully.");
                    message.append(String.format("\nVLC Version: %s", VLCJUtils.getVlcVersion()));

                } else {
                    message.append("VLC failed to initialize.");
                }
                JOptionPane.showMessageDialog(null, message, "About VLCJ", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        uiMenuBar.getUpdatesMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                YoutubeDLUpdater youtubeDLUpdater = new YoutubeDLUpdater();
                try {
                    InterruptableAsyncTask<Void, UpdateResult> asyncProcess = youtubeDLUpdater.update(YoutubeDLUtils.fetchYoutubeDLExecutable(),
                            PropertyManager.getConfigurationMap().get("youtube-dl.repository"),
                            PropertyManager.getConfigurationMap().get("youtube-dl.repository.asset.name"),
                            PropertyManager.getConfigurationMap().get("youtube-dl.repository.md5sums.name"));

                    if(asyncProcess == null) {
                        JOptionPane.showMessageDialog(null, "Update failed. Error code: " + 1001,
                                "Failed", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    final InterruptableProcessDialog interruptableProcessDialog = new InterruptableProcessDialog(asyncProcess, true);
                    asyncProcess.registerListener(new AsyncTaskListener<Void, UpdateResult>() {
                        public void onSuccess(InterruptableAsyncTask<Void, UpdateResult> interruptableAsyncTask) {

                            interruptableProcessDialog.close();
                            UpdateResult updateResult = interruptableAsyncTask.getResult();
                            if(updateResult.getReason() == UpdateResult.Reason.UPDATE_NOT_AVAILABLE) {
                                JOptionPane.showMessageDialog(null, "No new updates available for Youtube-DL component.",
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null, "Youtube-DL component updated successfully.",
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }

                        public void onFailure(InterruptableAsyncTask<Void, UpdateResult> interruptableAsyncTask) {
                            interruptableProcessDialog.close();
                            UpdateResult updateResult = interruptableAsyncTask.getResult();
                            JOptionPane.showMessageDialog(null, "Youtube-DL component failed to update: \n"
                                    + updateResult.getReason().getReason(), "Failed", JOptionPane.ERROR_MESSAGE);
                        }

                    });
                    asyncProcess.execute();
                    interruptableProcessDialog.showDialog();
                } catch (Exception ex) {
                    String message = ex.getMessage();
                    JOptionPane.showMessageDialog(null, message, "Failed", JOptionPane.ERROR_MESSAGE);
                    LOG.error(message, ex);
                }
            }
        });

        uiMenuBar.getAboutIMPMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                StringBuffer message = new StringBuffer();
                message.append(RESOURCE_BUNDLE.getString("name"));
                message.append(" ");
                message.append(RESOURCE_BUNDLE.getString("version"));

                message.append("\n");
                message.append("\n");

                message.append("Author: " + RESOURCE_BUNDLE.getString("author"));

                message.append("\n");

                message.append("Github: " + RESOURCE_BUNDLE.getString("github"));

                JOptionPane.showMessageDialog(null, message, "About Itunes Media Player", JOptionPane.INFORMATION_MESSAGE);
            }
        });


        this.setExtendedState(MAXIMIZED_BOTH);

        StatusMessageWriter.setStatusLabel(statusTextArea);
    }

    /**
     * Fetches the icon image specified in the ui.properties file.
     *
     * @return
     */
    private static Image fetchIconImage() {
        return new ImageIcon(RESOURCE_BUNDLE.getString("imp")).getImage();
    }

    private void emailLinkHandler(MouseEvent evt) {
        openThis(RESOURCE_BUNDLE.getString("mailto"));
    }

    private void openThis(final String location) {
        final Desktop desktop;

        try {
            final URI uri = new URI(location);
            desktop = Desktop.getDesktop();

            if (!desktop.isSupported(Desktop.Action.BROWSE)) {

                LOG.error("Desktop does not support BROWSE action.");
            } else {
                desktop.browse(uri);
            }
        } catch (URISyntaxException ex) {
            LOG.error("Error occurred creating email or track link.", ex);
        } catch (IOException ioe) {
            LOG.error("Error occurred browsing to email or track link.", ioe);
        }
    }

    private void searchBoxFocussed(FocusEvent evt) {
        doSearchBoxFocussed();
    }

    private void searchBoxBlurred(FocusEvent evt) {
        doSearchBoxBlurred();
    }

    private void doSearchBoxBlurred() {
        if (searchBox.getText().trim().equals("")) {
            searchBox.setForeground(Color.GRAY);
            disableSearchBoxUpdateEvt(true);
            searchBox.setText(SEARCH_MSG);
            disableSearchBoxUpdateEvt(false);
        }
    }

    private void doSearchBoxFocussed() {
        if (searchBox.getText().equals(SEARCH_MSG)) {
            disableSearchBoxUpdateEvt(true);
            searchBox.setText("");
            disableSearchBoxUpdateEvt(false);
            searchBox.setForeground(Color.BLACK);
        }
    }

    private void clearSearchBox() {
        disableSearchBoxUpdateEvt(true);
        searchBox.setText("");
        doSearchBoxBlurred();
        disableSearchBoxUpdateEvt(false);
    }

    private void disableSearchBoxUpdateEvt(final boolean b) {
        this.disableSearchBoxUpdateEvt = b;
    }

    public void addUIFrameEventListener(UIFrameEventListener uiFrameEventListener) {
        uiFrameEventListenerList.add(uiFrameEventListener);
    }

    public JPanel getMainUIPanel() {
        return contentPanel;
    }

    public PlayerControlPanel getPlayerControlPanel() {
        return playerControlPanel;
    }

    public List<Track> getSelectedTracks() {

        final int[] rowIndices = tracksTable.getSelectedRows();
        LOG.debug("No. of items involved in this action: " + rowIndices.length + " items.");

        final List<Track> trackList = new ArrayList<Track>();

        for (final int index : rowIndices) {
            final TrackHolder holder = (TrackHolder) tracksTable.getValueAt(index, TracksTableModel.getHolderIndex());
            trackList.add(holder.getTrack());
        }

        return trackList;
    }

    public JTable getPlaylistTable() {
        return playlistTable;
    }

    public JTable getTracksTable() {
        return tracksTable;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public NowPlayingPanel getNowPlayingPanel() {
        return nowPlayingPanel;
    }

    private void createUIComponents() {

        //Tracks Table
        tracksTable = new JTable() {

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                int modelIndex = convertRowIndexToModel(row);
                Component returnComp = super.prepareRenderer(renderer, row, column);

                if (!returnComp.getBackground().equals(getSelectionBackground())) {
                    Color bg = (modelIndex % 2 == 0 ? Color.WHITE : ALTERNATE_COLOR);
                    returnComp.setBackground(bg);
                }

                JComponent jcomp = (JComponent) returnComp;
                if (returnComp == jcomp) {
                    String tooltipText = String.valueOf(getModel().getValueAt(modelIndex, column));
                    if (tooltipText.equals("null")) {
                        tooltipText = Constants.EMPTY;
                    }
                    jcomp.setToolTipText(tooltipText);
                }

                return returnComp;
            }
        };

        tracksTable.setModel(new TracksTableModel());
        tracksTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        tracksTable.setIntercellSpacing(new Dimension(1, 2));
        tracksTable.setRowHeight(21);
        tracksTable.setShowGrid(true);
        tracksTable.getTableHeader().setReorderingAllowed(false);
        tracksTable.getColumnModel().getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        //Playlist Table
        playlistTable = new JTable();
        playlistTable.setModel(new PlaylistTableModel());
        playlistTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        playlistTable.setColumnSelectionAllowed(true);
        playlistTable.setIntercellSpacing(new Dimension(1, 2));
        playlistTable.setRowHeight(24);
        //playlistTable.getColumnModel().setColumnMargin(18);
        playlistTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        playlistTable.setShowGrid(false);
        playlistTable.getTableHeader().setReorderingAllowed(false);
        playlistTable.getColumnModel().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        fileBrowserTree = new FileBrowserTree(System.getProperty("user.home"), TreeSelectionModel.SINGLE_TREE_SELECTION);
        fileBrowserTree.registerFileBrowserTreeEventListener(new FileBrowserTreeEventListener() {
            @Override
            public void onNodeDoubleClicked(FileNode fileNode) {
                File file = fileNode.getFile();
                if(!file.isDirectory()) {
                    Main.getItunesMediaPlayer().play(file);
                }
            }
        });
    }

    private static final Color ALTERNATE_COLOR = new Color(252, 242, 206);

    public JLabel getTracksTableHeadingLabel() {
        return tracksTableHeadingLabel;
    }

    public UIMenuBar getUiMenuBar() {
        return uiMenuBar;
    }

    public FileBrowserTree getFileBrowserTree() {
        return fileBrowserTree;
    }
}
