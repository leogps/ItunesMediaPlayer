package com.gps.itunes.media.player.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formdev.flatlaf.FlatLightLaf;
import com.gps.imp.utils.JavaVersionUtils;
import com.gps.imp.utils.ssl.HttpClientUtils;
import com.gps.itunes.lib.exceptions.NoChildrenException;
import com.gps.itunes.lib.items.tracks.Track;
import com.gps.itunes.lib.parser.utils.LogInitializer;
import com.gps.itunes.lib.parser.utils.OSInfo;
import com.gps.itunes.lib.parser.utils.PropertyManager;
import com.gps.itunes.media.player.db.ConfigPropertyDao;
import com.gps.itunes.media.player.db.DbManager;
import com.gps.itunes.media.player.db.DbManagerImpl;
import com.gps.itunes.media.player.db.model.ConfigProperty;
import com.gps.itunes.media.player.ui.config.AppConfiguration;
import com.gps.itunes.media.player.ui.controller.Controller;
import com.gps.itunes.media.player.ui.theme.UITheme;
import com.gps.itunes.media.player.vlcj.player.events.UIDropTarget;
import com.gps.itunes.media.player.ui.events.UIFrameEventListener;
import com.gps.itunes.media.player.ui.exceptions.TaskExecutionException;
import com.gps.itunes.media.player.ui.splash.SplashAnimator;
import com.gps.itunes.media.player.vlcj.VLCJUtils;
import com.gps.itunes.media.player.vlcj.player.ItunesMediaPlayer;
import com.gps.itunes.media.player.vlcj.player.events.MediaPlayerEventListener;
import com.gps.itunes.media.player.vlcj.player.impl.ItunesMediaPlayerImpl;
import com.gps.itunes.media.player.vlcj.ui.player.NowPlayingListData;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * Created by leogps on 10/4/14.
 */
public class Main {

    private static Logger LOG = Logger.getLogger(Main.class);
    private static final java.util.logging.Logger BASIC_LOGGER
            = java.util.logging.Logger.getLogger(Main.class.getName());

    private static boolean vlcjInitSucceeded = false;

    public static boolean isVlcjInitSucceeded() {
        return vlcjInitSucceeded;
    }

    private static ItunesMediaPlayer itunesMediaPlayer;
    private static final AppConfiguration appConfiguration = new AppConfiguration();

    private static DbManager dbManager = DbManagerImpl.getInstance();

    static {
        LogInitializer.getInstance();
    }

    public static void main(String[] args) {
        if(args != null && args.length > 0) {
            LOG.debug("Arguments passed...");
            for(String arg : args) {
                LOG.debug(arg);
            }
            LOG.debug("End of args.");
        }
        appConfiguration.configure();

        try {
            dbManager.initialize();
        } catch (Exception e) {
            LOG.warn("Failed to initialize DB.", e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                LOG.debug("Shutting down initiated...");
                if(itunesMediaPlayer != null) {
                    LOG.debug("Releasing Media Player resources...");
                    itunesMediaPlayer.releaseResources();
                }
                try {
                    dbManager.shutdown();
                } catch (SQLException e) {
                    BASIC_LOGGER.log(Level.INFO, e.getMessage());
                }

                LOG.debug("Shutting down the application...");
                BASIC_LOGGER.log(Level.INFO, "Shutting down the application...");
            }
        }));


        if(!JavaVersionUtils.isGreaterThan6()) {
            LOG.debug("Java Version 6 found.");
            try {
                LOG.debug("Loading custom SSL Factory Provider for TLSV2 support.");
                HttpClientUtils.initBouncyCastle();
                LOG.debug("Loading custom SSL Factory Provider for TLSV2 support success.");
            } catch (Exception e) {
                LOG.debug("Loading custom SSL Factory Provider for TLSV2 support failed.", e);
            }
        }

        if(OSInfo.isOSMac()) {
            // take the menu bar off the jframe
            System.setProperty("apple.laf.useScreenMenuBar", "true");

            // set the name of the application menu item
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "iTunes Media Player");
            ImageIcon imageIcon = new ImageIcon(Main.class.getClassLoader().getResource("images/imp.png"));
            try {
                Object instance = Class.forName("com.apple.eawt.Application").getMethod("getApplication").invoke(null);
                Class.forName("com.apple.eawt.Application").getMethod("setDockIconImage", Image.class).invoke(instance, imageIcon.getImage());
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
            try {
                Main.class.forName("com.gps.itunes.media.player.OSXUtils");
            } catch (ClassNotFoundException e) {
                LOG.debug(e.getMessage(), e);
            }
        }
        enableTheming();
        final SplashAnimator splashAnimator = new SplashAnimator();

        /*
         * Create and display the form
         */
        try {
            splashAnimator.renderSplashFrame(10, "Checking VLC Engine...");
            vlcjInitSucceeded = VLCJUtils.isVlcInitSucceeded();
            splashAnimator.renderSplashFrame(15, "VLC Engine loaded successfully.");
        } catch(Exception ex) {
            LOG.error("VLCJ initialization failed!!", ex);
            splashAnimator.renderSplashFrame(15, "VLC initialization failed.");
        } catch(UnsatisfiedLinkError unsatisfiedLinkError) {
            LOG.error("VLCJ initialization failed!!", unsatisfiedLinkError);
            splashAnimator.renderSplashFrame(15, "VLC initialization failed.");
        }

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {

                    splashAnimator.renderSplashFrame(15, "Reading properties...");
                    Map<String, String> configurationMap = PropertyManager.getConfigurationMap();

                    splashAnimator.renderSplashFrame(20, "Logging properties...");
                    LOG.debug("Dumping properties read: ");
                    for(Object keyObj : configurationMap.keySet()) {
                        String key = (String) keyObj;
                        LOG.debug(key + ": " + configurationMap.get(key));
                    }

                    splashAnimator.renderSplashFrame(20, "Initializing UI Frames...");
                    final UIFrame uiFrame = new UIFrame();
                    if(OSInfo.isOSMac()) {
                        try {

                            Object instance = Class.forName("com.apple.eawt.Application").getMethod("getApplication").invoke(null);
                            Class.forName("com.apple.eawt.Application").getMethod("requestUserAttention", boolean.class).invoke(instance, true);
                        } catch (Exception ex) {
                            LOG.error(ex.getMessage(), ex);
                        }
                    }
                    uiFrame.setDropTarget(new UIDropTarget() {
                        @Override
                        public void onFilesDroppedEvent(List<File> fileList, DropTargetDropEvent dropTargetDropEvent) {
                            getItunesMediaPlayer().playFiles(fileList);
                        }

                        @Override
                        public void onException(Exception e, DropTargetDropEvent dropTargetDropEvent) {
                            JOptionPane.showMessageDialog(uiFrame, "Action could not be performed at the moment, please see logs for more info.");
                        }
                    });
                    uiFrame.setState(Frame.MAXIMIZED_BOTH);
//                    if(OSInfo.isOSMac()) {
//                        try {
//                            Class.forName("com.apple.eawt.FullScreenUtilities").getMethod("setWindowCanFullScreen", Window.class, boolean.class)
//                                    .invoke(null, uiFrame, true);
//                        } catch (Exception ex) {
//                            LOG.error(ex.getMessage(), ex);
//                        }
//                    }

                    splashAnimator.renderSplashFrame(25, "Initializing Controller...");
                    final Controller controller = new Controller(uiFrame);

                    // Initializing audio player on the UIFrame.
                    if(isVlcjInitSucceeded()) {

                        splashAnimator.renderSplashFrame(35, "Initializing Media Player...");
                        try {
                            itunesMediaPlayer = new ItunesMediaPlayerImpl(uiFrame.getPlayerControlPanel());

                            splashAnimator.renderSplashFrame(40, "Registering Media Player Event Listeners...");
                            controller.registerPlayerEventListener();

                            itunesMediaPlayer.addMediaPlayerListener(new MediaPlayerEventListener() {
                                @Override
                                public void playing(ItunesMediaPlayer player, final NowPlayingListData currentTrack) {
                                    uiFrame.getNowPlayingPanel().getTrackNameLabel().setText(currentTrack.getName());
                                    uiFrame.getNowPlayingPanel().getTrackNameLabel().setToolTipText(currentTrack.getName());

                                    uiFrame.getNowPlayingPanel().getTrackAlbumNameLabel().setText(currentTrack.getAlbum());
                                    uiFrame.getNowPlayingPanel().getTrackAlbumNameLabel().setToolTipText(currentTrack.getAlbum());

                                    uiFrame.getNowPlayingPanel().getTrackArtistNameLabel().setText(currentTrack.getArtist());
                                    uiFrame.getNowPlayingPanel().getTrackArtistNameLabel().setToolTipText(currentTrack.getArtist());

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            uiFrame.getFileBrowserTree().attemptToShowInTree(currentTrack.getLocation());
                                        }
                                    }).start();
                                }

                                @Override
                                public void paused(ItunesMediaPlayer player, String location) {

                                }

                                @Override
                                public void stopped(ItunesMediaPlayer player, String location) {

                                }

                                @Override
                                public void finished(ItunesMediaPlayer player, String location) {

                                }

                                @Override
                                public void onPlayProgressed() {

                                }
                            });

                        } catch(Exception ex) {
                            vlcjInitSucceeded = false;
                            LOG.error("MediaPlayer Initialization failed.", ex);
                        } catch(Error ex) {
                            vlcjInitSucceeded = false;
                            LOG.error("MediaPlayer Initialization failed.", ex);
                        }

                    }

                    splashAnimator.renderSplashFrame(50, "Registering UI Event Listeners...");
                    uiFrame.addUIFrameEventListener(new UIFrameEventListener() {
                        @Override
                        public void onPlaylistSelectedEvent(UIFrame uiFrame) {
                            try {
                                controller.loadTracks();

                            } catch (TaskExecutionException ex) {
                                LOG.error(ex);
                                LOG.info(UIFrame.ERROR_OCCURRED_MSG);
                            }
                        }

                        @Override
                        public void onTracksPlayRequested(List<Track> trackList) {
                            if (isVlcjInitSucceeded()) {
                                controller.playTracks(trackList);
                            } else {
                                new UserInformer(uiFrame.getMainUIPanel())
                                        .informUser("VLCJ Audio libraries instantiation failed. Content cannot be played!!");
                            }
                        }

                        @Override
                        public void onReloadLibraryRequested(UIFrame uiFrame) {
                            try {
                                controller.reloadLibrary();
                            } catch (NoChildrenException ex) {
                                LOG.error(ex);
                                LOG.info(UIFrame.ERROR_OCCURRED_MSG);
                            } catch (TaskExecutionException tee) {
                                LOG.error(tee);
                                LOG.info(UIFrame.ERROR_OCCURRED_MSG);
                            }
                        }

                        @Override
                        public void onCopyPlaylistRequested(UIFrame uiFrame) {
                            try {
                                controller.copyPlaylists();
                            } catch (TaskExecutionException ex) {
                                LOG.error(ex);
                                LOG.info("Error occurred when copying playlist(s).");
                            }
                        }

                        @Override
                        public void onSearch(String searchQuery, UIFrame uiFrame) {
                            try {
                                controller.searchTracks(searchQuery);
                            } catch (TaskExecutionException ex) {
                                LOG.error(ex);
                                LOG.info("Error occurred when searching for Tracks.");
                            }
                        }

                        @Override
                        public void onFileOpenRequested() {
                            controller.handleFileOpenRequest();
                        }

                        @Override
                        public void onNetworkFileOpenRequested() {
                            controller.handleNetworkFileOpenRequest();
                        }
                    });

                    splashAnimator.renderSplashFrame(60, "Parsing Itunes Library, Loading Library data...");
                    controller.takeControl();

                    splashAnimator.renderSplashFrame(95, "Showing UI...");
                    if(!isVlcjInitSucceeded()) {
                        uiFrame.getPlayerControlPanel().setVisible(false);
                    } else {
                        uiFrame.getPlayerControlPanel().setVisible(true);
                    }

                    splashAnimator.renderSplashFrame(100, "Done.");
                    splashAnimator.close();

                    uiFrame.setVisible(true);
                    uiFrame.toFront();

                } catch (final NoChildrenException ex) {
                    LOG.error(ex);
                    LOG.info(UIFrame.ERROR_OCCURRED_MSG);
                } catch (final TaskExecutionException tee) {
                    LOG.error(tee);
                    LOG.info(UIFrame.ERROR_OCCURRED_MSG);
                } catch (IOException e) {
                    LOG.error(e);
                }
            }
        });
    }

    private static void enableTheming() {
        UITheme savedTheme = null;
        try {
            savedTheme = retrieveSavedUIThemeProperty();
        } catch (Exception ex) {
            LOG.warn("Could not retrieve saved theme", ex);
        }
        if (savedTheme != null) {
            LOG.info("Found saved theme: " + savedTheme.getName());
        } else {
            LOG.info("No saved theme found. Using default theme.");
            savedTheme = new UITheme();
            savedTheme.setName("Light Flat");
            savedTheme.setClassName("com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme");
        }
        try {
            UIManager.setLookAndFeel( new FlatLightLaf() );
            Method method = Class.forName(savedTheme.getClassName()).getDeclaredMethod("setup");
            method.invoke(null);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            try {
                LOG.info("Falling back to system default look and feel...");
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    private static UITheme retrieveSavedUIThemeProperty() throws SQLException, IOException {
        ConfigPropertyDao configPropertyDao = fetchConfigPropertyDao();
        if(configPropertyDao == null) {
            return null;
        }

        ConfigProperty configProperty = configPropertyDao.findByKey("ui_theme");
        if (configProperty == null) {
            return null;
        }
        return new ObjectMapper().readValue(configProperty.getValue(), UITheme.class);
    }

    private static ConfigPropertyDao fetchConfigPropertyDao() {
        if(!DbManagerImpl.getInstance().isInitiated()) {
            return null;
        }
        return new ConfigPropertyDao(DbManagerImpl.getInstance().getConnection());
    }

    private static Integer retrieveFontSize() {
        if(!dbManager.isInitiated()) {
            return null;
        }
        try {
            ConfigPropertyDao configPropertyDao = new ConfigPropertyDao(dbManager.getConnection());
            ConfigProperty configProperty = configPropertyDao.findByKey("font_size");
            if(configProperty == null) {
                LOG.debug("Font size property not found.");
                return null;
            }

            try {
                return Integer.parseInt(configProperty.getValue());
            } catch (NumberFormatException e) {
                LOG.error("font_size is not valid.", e);
            }
        } catch (Exception ex) {
            LOG.debug("Could not retrieve font_size", ex);
        }
        return null;
    }

    public static void setDefaultSize(int size) {
        Set<Object> keySet = UIManager.getLookAndFeelDefaults().keySet();
        Object[] keys = keySet.toArray(new Object[keySet.size()]);

        for (Object key : keys) {

            if (key != null && key.toString().toLowerCase().contains("font")) {

                LOG.info("Updating font: " + key);
                Font font = UIManager.getDefaults().getFont(key);
                if (font != null) {
                    font = font.deriveFont((float)size);
                    UIManager.put(key, font);
                }

            }

        }

    }

    public static ItunesMediaPlayer getItunesMediaPlayer() {
        return itunesMediaPlayer;
    }
}
