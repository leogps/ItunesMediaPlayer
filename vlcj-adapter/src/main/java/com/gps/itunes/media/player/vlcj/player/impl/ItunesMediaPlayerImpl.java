package com.gps.itunes.media.player.vlcj.player.impl;

import com.gps.imp.utils.*;
import com.gps.imp.utils.process.AsyncProcess;
import com.gps.imp.utils.ssl.HttpClientUtils;
import com.gps.imp.utils.ui.AsyncTaskListener;
import com.gps.imp.utils.ui.InterruptableAsyncTask;
import com.gps.imp.utils.ui.InterruptableProcessDialog;
import com.gps.imp.utils.ui.fileutils.FileBrowserDialog;
import com.gps.imp.utils.ui.fileutils.FileBrowserDialogListener;
import com.gps.itunes.lib.items.tracks.Track;
import com.gps.itunes.lib.parser.utils.OSInfo;
import com.gps.itunes.lib.parser.utils.PropertyManager;
import com.gps.itunes.media.player.vlcj.player.*;
import com.gps.itunes.media.player.vlcj.player.events.MediaPlayerEventListener;
import com.gps.itunes.media.player.vlcj.ui.player.BasicPlayerControlPanel;
import com.gps.itunes.media.player.vlcj.ui.player.NowPlayingListData;
import com.gps.itunes.media.player.vlcj.ui.player.NowPlayingListFrame;
import com.gps.itunes.media.player.vlcj.ui.player.PlayerControlPanel;
import com.gps.itunes.media.player.vlcj.ui.player.events.*;
import com.gps.itunes.media.player.vlcj.ui.player.events.handler.NetworkFileOpenEventHandler;
import com.gps.itunes.media.player.vlcj.ui.player.utils.GoToSpinnerDialog;
import com.gps.itunes.media.player.vlcj.ui.player.utils.GotoValueSubmissionEventListener;
import com.gps.itunes.media.player.vlcj.ui.player.utils.TrackTime;
import com.gps.itunes.media.player.vlcj.utils.YoutubeDLUtils;
import com.gps.youtube.dl.YoutubeDL;
import com.gps.youtube.dl.YoutubeDLProcessor;
import com.gps.youtube.dl.YoutubeDLResult;
import com.gps.youtube.dl.event.YoutubeDLResultEvent;
import com.gps.youtube.dl.event.YoutubeDLResultEventListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.TrackInfo;
import uk.co.caprica.vlcj.runtime.x.LibXUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.gps.imp.utils.Constants.EMPTY;
import static com.gps.imp.utils.Constants.SPACE;

/**
 * Created by leogps on 10/4/14.
 */
public class ItunesMediaPlayerImpl implements ItunesMediaPlayer {

    private static final int DISABLE_SUBTITLES = -1;

    /**
     * Helps in playing a fixed number of tracks at a single point of time.
     */
    private static CountDownLatch playSignal = new CountDownLatch(0);

    private static Logger log = Logger.getLogger(ItunesMediaPlayerImpl.class);

    /**
     * To publish this MediaPlayer's events.
     */
    private static List<MediaPlayerEventListener> eventListenerList = new ArrayList<MediaPlayerEventListener>();

    /**
     * Player control panel on the main window.
     */
    private final PlayerControlPanel playerControlPanel;

    /**
     * Holds currently playing track.
     */
    private NowPlayingListData currentTrack;

    private final String[] mediaFactoryArgs;
    private final MediaPlayerFactory mediaPlayerFactory;

    private final AtomicBoolean libXInitialized = new AtomicBoolean(false);

    /**
     * Now Playing list.
     */
    private final TraversableLinkedList<NowPlayingListData> nowPlaylingList = new TraversableLinkedList<NowPlayingListData>() {
        @Override
        public boolean add(NowPlayingListData nowPlayingListData) {
            boolean added = super.add(nowPlayingListData);
            nowPlayingListFrame.add(nowPlayingListData);
            return added;
        }

        @Override
        public void clear() {
            super.clear();
            nowPlayingListFrame.clear();
        }
    };

    /**
     * Now Playing List frame.
     */
    private final NowPlayingListFrame nowPlayingListFrame = new NowPlayingListFrame();

    /**
     * List iterator to traverse left and right in the playlist.
     */
    private final TraversableLinkedList<NowPlayingListData>.ListTraverser<NowPlayingListData> listTraverser
            = nowPlaylingList.getListTraverser();

    /**
     * Audio Player that is closely bound to the VLCJ adapter.
     */
    private final VLCJPlayer VLCJ_AUDIO_PLAYER;

    /**
     * Video Player that is closely bound to the VLCJ adapter.
     */
    private final VLCJPlayer VLCJ_VIDEO_PLAYER;

    /**
     * Array containing both players.
     */
    private final VLCJPlayer[] VLCJ_PLAYERS;

    /**
     * Instance of this class.
     */
    private ItunesMediaPlayer instance;

    /**
     * Seek Event listener, that listens to seeking on both player control panel and video control panel.
     */
    private static SeekEventListener seekEventListener;

    /**
     * Progress handler for the track that is being played.
     */
    private static PlayProgressHandler playProgressHandler;

    private static UserCommandEventListener userCommandEventListener;

    private static float startFrom = 0;

    private final AtomicBoolean manualVolumeChange = new AtomicBoolean(false);

    private final FileBrowserDialogListener mediaFileOpenEventListener = new FileBrowserDialogListener() { // TODO: Init once.
        public void onFileSelected(File selectedFile) {
            if (selectedFile != null) {
                play(selectedFile);
            }
        }

        public void onCancel() {

        }
    };

    private final AtomicBoolean isTogglingFullscreen = new AtomicBoolean();

    public ItunesMediaPlayerImpl(final PlayerControlPanel playerControlPanel) {
        mediaFactoryArgs = parseMediaFactoryArgs(PropertyManager.getConfigurationMap().get("vlc.media.factory.args"));
        mediaPlayerFactory = new MediaPlayerFactory(mediaFactoryArgs);
        mediaPlayerFactory.setUserAgent(PropertyManager.getConfigurationMap().get("vlc.user.agent"));

        VLCJ_VIDEO_PLAYER = new VLCJVideoPlayer(mediaPlayerFactory);


        this.playerControlPanel = playerControlPanel;
        this.VLCJ_AUDIO_PLAYER = new VLCJAudioPlayer(mediaPlayerFactory, playerControlPanel);
        this.VLCJ_PLAYERS = new VLCJPlayer[]{VLCJ_AUDIO_PLAYER, VLCJ_VIDEO_PLAYER};

        attachVolumeSyncEvents();
        instance = this;

        MediaPlayerEventAdapter mediaPlayerEventAdapter = new ItunesMediaPlayerEventAdapter();

        for(MediaPlayer mediaPlayer : getAllPlayers()) {
            mediaPlayer.addMediaPlayerEventListener(mediaPlayerEventAdapter);
        }


        userCommandEventListener = new UserCommandEventListener() {
            @Override
            public void onFullScreenToggleCommand() {
                handleFullScreenToggle();
            }

            @Override
            public void onPlayToggleCommand() {
                pause();
            }

            @Override
            public void onVolumeIncreaseCommand(int increasedBy) {
                handleVolumeIncreasedEvent(increasedBy);
                notifyOnVideoSurface("Volume increased");
            }

            @Override
            public void onVolumeDecreaseCommand(int decreasedBy) {
                handleVolumeDecreasedEvent(decreasedBy);
                notifyOnVideoSurface("Volume decreased");
            }

            @Override
            public void onFastForwardCommand() {
                playerControlPanel.getSeekbar().setValue(playerControlPanel.getSeekbar().getValue() + 10);
                notifyOnVideoSurface("Seeked forwards");
            }

            @Override
            public void onFastReverseCommand() {
                playerControlPanel.getSeekbar().setValue(playerControlPanel.getSeekbar().getValue() - 10);
                notifyOnVideoSurface("Seeked backwards");
            }

            @Override
            public void onSkipForwardCommand() {
                getCurrentPlayer().setTime(getCurrentPlayer().getTime() + 3000);
            }

            @Override
            public void onSkipReverseCommand() {
                getCurrentPlayer().setTime(getCurrentPlayer().getTime() - 3000);
            }

            @Override
            public void onExitFullscreenCommand() {
                float currentMediaPosition = getPlayer().getPosition();
                if(getPlayer() == VLCJ_VIDEO_PLAYER.getPlayer() ) {
                    VLCJVideoPlayer videoPlayer = ((VLCJVideoPlayer) VLCJ_VIDEO_PLAYER);
                    if(videoPlayer.isFullscreen()) {
                        videoPlayer.toggleFullScreen();
                        isTogglingFullscreen.set(true);
                        playFrom(currentMediaPosition);
                    }
                }
            }

            @Override
            public void onAttentionRequested() {
                //TODO: get correct players position.
                if(getPlayer() == VLCJ_VIDEO_PLAYER.getPlayer() ) {
                    VLCJVideoPlayer videoPlayer = ((VLCJVideoPlayer) VLCJ_VIDEO_PLAYER);
                    videoPlayer.showSlider();
                }
            }

            @Override
            public void onFileOpenCommand() {
                handleFileOpenEvent();
            }

            @Override
            public void onNetworkFileOpenCommand() {
                handleNetworkFileOpenEvent();
            }

            @Override
            public void onSeekDecreasedCommand(int decreasedBy) {
                addToSeekValue(decreasedBy);
                notifyOnVideoSurface("Seeked backwards");
            }

            @Override
            public void onSeekIncreasedCommand(int increasedBy) {
                addToSeekValue(increasedBy);
                notifyOnVideoSurface("Seeked forwards");
            }

            @Override
            public void onToggleSubtitles() {
                if(getPlayer() == VLCJ_VIDEO_PLAYER.getPlayer() ) {
                    VLCJVideoPlayer videoPlayer = ((VLCJVideoPlayer) VLCJ_VIDEO_PLAYER);
                    if(videoPlayer.getPlayer().getSpu() == DISABLE_SUBTITLES) {
                        // Subtitles disabled, enabling subtitles.
                        videoPlayer.getPlayer().setSpu(getEmbeddedSubtitleFile(videoPlayer.getPlayer().getTrackInfo()));
                    } else {
                        // Subtitles enabled, disabling subtitles.
                        videoPlayer.getPlayer().setSpu(DISABLE_SUBTITLES);
                    }
                    notifyOnVideoSurface("Subtitles toggled");
                }

            }

            @Override
            public void onMuteToggleCommand() {
                toggleMute();
                notifyOnVideoSurface("Volume toggled");
            }

            private void addToSeekValue(int value) {
                // Seeking one seekbar will seek the rest of them.
                JSlider seekbar = ((VLCJVideoPlayer) VLCJ_VIDEO_PLAYER).getVideoPlayerFrame().getSeekbar();
                seekbar.setValue(seekbar.getValue() + value);
            }

            @Override
            public void goTo() {
                handleGoToEvent();
                notifyOnVideoSurface("Seek complete");
            }
        };

        //TODO: Is this alright? need new command listener for each?
        VideoPlayerKeyListener videoPlayerKeyListener = new VideoPlayerKeyListener();
        videoPlayerKeyListener.addUserCommandEventListener(userCommandEventListener);
        VLCJ_VIDEO_PLAYER.attachCommandListener(videoPlayerKeyListener);

        VideoPlayerMouseAdapter videoPlayerMouseAdapter = new VideoPlayerMouseAdapter();
        videoPlayerMouseAdapter.addUserCommandEventListener(userCommandEventListener);
        VLCJ_VIDEO_PLAYER.attachCommandListener(videoPlayerMouseAdapter);

        VideoPlayerMouseWheelListener videoPlayerMouseWheelListener = new VideoPlayerMouseWheelListener();
        videoPlayerMouseWheelListener.addUserCommandEventListener(userCommandEventListener);
        VLCJ_VIDEO_PLAYER.attachCommandListener(videoPlayerMouseWheelListener);

        /**
         * NowPlayingList frame utils.
         */
        addMediaPlayerListener(new MediaPlayerEventListener() {
            public void playing(ItunesMediaPlayer player, NowPlayingListData currentTrack) {
                updateStatusCells(true);
            }

            private void updateStatusCells(final boolean isPlaying) {
                nowPlayingListFrame.updateCellStatus(isPlaying, currentTrack.getTrackId());
            }

            public void paused(ItunesMediaPlayer player, String location) {
                updateStatusCells(false);
            }

            public void stopped(ItunesMediaPlayer player, String location) {}
            public void finished(ItunesMediaPlayer player, String location) {}
            public void onPlayProgressed() {}
        });

        nowPlayingListFrame.addNowPlayingListTrackSelectedEventListener(new NowPlayingListTrackSelectedEventListener() {
            @Override
            public void onNowPlayingListTrackSelectedEvent(NowPlayingListData nowPlayingListData) {
                synchronized (currentTrack) {
                    stopPlay();
                    nowPlaylingList.traverseTo(nowPlayingListData);
                    currentTrack = nowPlayingListData;
                    play();
                }
            }
        });
    }

    private String[] parseMediaFactoryArgs(String argsString) {
        if(StringUtils.isBlank(argsString)) {
            return new String[]{EMPTY};
        }
        return argsString.split(SPACE);
    }

    private void notifyOnVideoSurface(String message) {
        if(currentTrack.isMovie()
                && !checkIfDisableOverlays()) {
            ((VLCJVideoPlayer) VLCJ_VIDEO_PLAYER).addOverlay(message, false);
        }
    }

    private boolean checkIfDisableOverlays() {
        String option = PropertyManager.getConfigurationMap().get("disable.video.overlay.messages");
        return option != null && Boolean.parseBoolean(option);
    }

    private int getEmbeddedSubtitleFile(List<TrackInfo> trackInfoList) {
        for(int index = 0; index < trackInfoList.size(); index++) {
            TrackInfo trackInfo = trackInfoList.get(index);
            if(trackInfo != null) {
                String language = trackInfo.language();
                if(language != null) {
                    log.debug("Subtitle embedded in track: " + language);
                    return index;
                }
            }
        }

        log.debug("No embedded subtitles file found in the track");
        return DISABLE_SUBTITLES;
    }

    public void handleVolumeIncreasedEvent(int increasedBy) {
        if(playerControlPanel.getVolumeSlider().getValue() < BasicPlayerControlPanel.VOL_MAX) {
            playerControlPanel.getVolumeSlider().setValue(playerControlPanel.getVolumeSlider().getValue() + increasedBy);
        }
    }

    public void handleVolumeDecreasedEvent(int decreasedBy) {
        if(playerControlPanel.getVolumeSlider().getValue() > BasicPlayerControlPanel.VOL_MIN) {
            playerControlPanel.getVolumeSlider().setValue(playerControlPanel.getVolumeSlider().getValue() - decreasedBy);
        }
    }

    public void handleGoToEvent() {
        long time = getCurrentPlayer().getTime();
        final long mediaLength = getCurrentPlayer().getLength();

        TrackTime trackLimit = TrackTime.get(mediaLength);
        TrackTime initTime = TrackTime.get(time);

        if(((VLCJVideoPlayer)VLCJ_VIDEO_PLAYER).isFullscreen()) {
            handleFullScreenToggle();
        }

        new GoToSpinnerDialog(trackLimit, initTime,
                new GotoValueSubmissionEventListener() {
                    public void onSubmit(TrackTime seekTo) {
                        long seekToTime = TrackTime.valueOf(seekTo);
                        float percentage = (float) seekToTime / mediaLength;
                        getPlayer().setPosition(percentage);
                    }
                });
    }

    private void handleFullScreenToggle() {
        float currentMediaPosition = getPlayer().getPosition();
        if(getPlayer() == VLCJ_VIDEO_PLAYER.getPlayer()) {
            log.debug("Fullscreen toggle requested.");
            ((VLCJVideoPlayer) VLCJ_VIDEO_PLAYER).toggleFullScreen();
            isTogglingFullscreen.set(true);
            playFrom(currentMediaPosition);
        }
    }

    public void handleNetworkFileOpenEvent() {
        String url = new NetworkFileOpenEventHandler().handle();
        if(url != null) {
            try {
                play(new URL(url));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        "Could not play the network stream. Error details: " + ex.getLocalizedMessage(),
                        "Error Occurred!",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void registerPlayerControlEventListener(PlayerControlEventListener playerControlEventListener) {
        VLCJ_AUDIO_PLAYER.registerPlayerControlEventListener(playerControlEventListener);
        VLCJ_VIDEO_PLAYER.registerPlayerControlEventListener(playerControlEventListener);
    }

    public void handleFileOpenEvent() {
        // Will not show file selection dialog in fullscreen mode.
        VLCJVideoPlayer videoPlayer = ((VLCJVideoPlayer) VLCJ_VIDEO_PLAYER);
        if(videoPlayer.isFullscreen()) {
            videoPlayer.toggleFullScreen();
        }

        FileBrowserDialog fileBrowserDialog = new FileBrowserDialog(null, "Open Media file to play...", null);
        fileBrowserDialog.registerFileBrowserDialogListener(mediaFileOpenEventListener);
        fileBrowserDialog.pack();
        fileBrowserDialog.setVisible(true);
    }

    private void attachVolumeSyncEvents() {
        final JSlider volumeSlider = ((VLCJVideoPlayer) VLCJ_VIDEO_PLAYER).getVideoPlayerFrame().getBasicPlayerControlPanel().getVolumeSlider();
        final JSlider fullScreenVolumeSlider = ((VLCJVideoPlayer) VLCJ_VIDEO_PLAYER).getFullscreenFrame().getBasicPlayerControlPanel().getVolumeSlider();
        final JSlider fxFrameVolumeSlider = ((VLCJVideoPlayer) VLCJ_VIDEO_PLAYER).getFxPlayerFrame().getBasicPlayerControlPanel().getVolumeSlider();

        playerControlPanel.getVolumeSlider().addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent ce) {
                if(!manualVolumeChange.get()) {
                    int volume = playerControlPanel.getVolumeSlider().getValue();
                    log.debug("Volume changed to: " + volume);
                    setVolume(volume);
                    manualVolumeChange.set(true);
                    volumeSlider.setValue(volume);
                    fullScreenVolumeSlider.setValue(volume);
                    fxFrameVolumeSlider.setValue(volume);
                    manualVolumeChange.set(false);
                }
            }
        });

        volumeSlider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent changeEvent) {
                if (!manualVolumeChange.get()) {
                    int volume = volumeSlider.getValue();
                    log.debug("Volume changed to: " + volume);
                    setVolume(volume);
                    manualVolumeChange.set(true);
                    playerControlPanel.getVolumeSlider().setValue(volume);
                    fullScreenVolumeSlider.setValue(volume);
                    fxFrameVolumeSlider.setValue(volume);
                    manualVolumeChange.set(false);
                }
            }
        });

        fullScreenVolumeSlider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent changeEvent) {
                if(!manualVolumeChange.get()) {
                    int volume = fullScreenVolumeSlider.getValue();
                    log.debug("Volume changed to: " + volume);
                    setVolume(volume);
                    manualVolumeChange.set(true);
                    playerControlPanel.getVolumeSlider().setValue(volume);
                    volumeSlider.setValue(volume);
                    fxFrameVolumeSlider.setValue(volume);
                    manualVolumeChange.set(false);
                }
            }
        });

        fxFrameVolumeSlider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent changeEvent) {
                if(!manualVolumeChange.get()) {
                    int volume = fxFrameVolumeSlider.getValue();
                    log.debug("Volume changed to: " + volume);
                    setVolume(volume);
                    manualVolumeChange.set(true);
                    playerControlPanel.getVolumeSlider().setValue(volume);
                    volumeSlider.setValue(volume);
                    fullScreenVolumeSlider.setValue(volume);
                    manualVolumeChange.set(false);
                }
            }
        });
    }

    /**
     *
     * VLCJ media player event adapter. Listens to events occurred on the VLCJ player.
     */
    private class ItunesMediaPlayerEventAdapter extends MediaPlayerEventAdapter {

        @Override
        public void finished(MediaPlayer mediaPlayer) {
            if(mediaPlayer.getSubItemMediaMeta().size() <= 1) {
                super.finished(mediaPlayer);
                log.debug("Finished!!");
                reportPlayCompletion();
                VLCJ_AUDIO_PLAYER.setPaused();
                VLCJ_VIDEO_PLAYER.setPaused();
                next();
            }
        }

        @Override
        public void mediaSubItemAdded(MediaPlayer mediaPlayer, libvlc_media_t subItem) {
            log.debug(mediaPlayer.getSubItemMediaMeta());
        }

        @Override
        public void mediaSubItemTreeAdded(MediaPlayer mediaPlayer, libvlc_media_t item) {
            log.debug(mediaPlayer.getSubItemMediaMeta());
        }

        private void reportPlayCompletion() {
            for(MediaPlayerEventListener eventListener : eventListenerList) {
                eventListener.finished(instance, getNowPlayingUrl());
            }
            playSignal.countDown();

            if(playProgressHandler != null) {
                playProgressHandler.shutdown();
            }

        }

        @Override
        public void stopped(MediaPlayer mediaPlayer) {
            super.stopped(mediaPlayer);
            reportPlayCompletion();
            VLCJ_AUDIO_PLAYER.setPaused();
            VLCJ_VIDEO_PLAYER.setPaused();
        }


        @Override
        public void error(MediaPlayer mediaPlayer) {
            super.error(mediaPlayer);
            log.debug("Error occurred!!");
            reportPlayCompletion();
            JOptionPane.showMessageDialog(null, "Error!", "Error Occurred!", JOptionPane.ERROR_MESSAGE);
        }

        @Override
        public void opening(MediaPlayer mediaPlayer) {
            super.opening(mediaPlayer);
        }

        @Override
        public void buffering(MediaPlayer mediaPlayer, float newCache) {
            super.buffering(mediaPlayer, newCache);
            log.debug("Buffering percentage: " + newCache);
            for(VLCJPlayer vlcjPlayer : VLCJ_PLAYERS) {
                vlcjPlayer.setBufferingValue(newCache);
            }
        }

        @Override
        public void playing(MediaPlayer mediaPlayer) {
            for(MediaPlayerEventListener eventListener : eventListenerList) {
                eventListener.playing(instance, currentTrack);
            }

            //TODO: Define currentlyPlayingPlayer() and use below.
            log.debug("Currently playing: " + getPlayer().getTrackInfo());
            super.playing(mediaPlayer);
            VLCJ_AUDIO_PLAYER.setPlaying();
            VLCJ_VIDEO_PLAYER.setPlaying();

            if(!nowPlayingListFrame.isVisible()) {
                nowPlayingListFrame.setVisible(true);
            }

            registerPlayProgressHandler(mediaPlayer);
            registerNewSeekEventListeners();
        }

        @Override
        public void paused(MediaPlayer mediaPlayer) {
            for(MediaPlayerEventListener eventListener : eventListenerList) {
                eventListener.paused(instance, getNowPlayingUrl());
            }

            super.paused(mediaPlayer);
            VLCJ_AUDIO_PLAYER.setPaused();
            VLCJ_VIDEO_PLAYER.setPaused();

            if(playProgressHandler != null) {
                playProgressHandler.shutdown();
            }
        }

    }

    private boolean isSeekValueAdjusting() {
        return VLCJ_AUDIO_PLAYER.isSeekValueAdjusting() || VLCJ_VIDEO_PLAYER.isSeekValueAdjusting();
    }


    public void play() {
        this.stopPlay();
        new Thread(this).start();
    }

    public void playFiles(final List<File> files) {

        clearNowPlayingList();

        /*
         * If more than one track passed, play the first track readily, process the rest of the tracks asynchronously.
         * */

        if(!files.isEmpty()) {
            File file = files.get(0);
            NowPlayingListData nowPlayingListData = new NowPlayingListData(nowPlaylingList.size(), file.getName(), file.getName(), file.getName(),
                    file.getAbsolutePath(), true);
            addTrack(nowPlayingListData);
        }

        //resetIteratorPos();
        log.debug("Playable item available? " + listTraverser.hasNext());
        if(listTraverser.hasNext()){
            currentTrack = listTraverser.next();
            log(currentTrack);
        }
        play();


        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                for (int i = 1; i < files.size(); i++) {
                    File file = files.get(i);
                    NowPlayingListData nowPlayingListData = new NowPlayingListData(nowPlaylingList.size(), file.getName(), file.getName(), file.getName(),
                            file.getAbsolutePath(), true);
                    addTrack(nowPlayingListData);
                }
            }
        });

    }

    public void play(File file) {
        this.stopPlay();
        nowPlaylingList.clear();

        this.currentTrack = new NowPlayingListData(nowPlaylingList.size(), file.getName(), file.getName(), file.getName(),
                file.getAbsolutePath(), true);
        nowPlaylingList.add(currentTrack);
        new Thread(this).start();
    }

    public void play(URL url) {
        this.stopPlay();
        nowPlaylingList.clear();

        final String urlStr = url.toString();

        boolean videoURLFetchComplete = false;
        if(isYoutubeVideo(urlStr)) {
            videoURLFetchComplete = attemptYoutubeVideoUrlFetch(urlStr);
        }

        if(!videoURLFetchComplete) {
            fallbackToYoutubeDL(urlStr);
        }
    }

    private void fallbackToYoutubeDL(final String urlStr) {
        String youtubeDLExecutable = YoutubeDLUtils.fetchYoutubeDLExecutable();
        try {
            final InterruptableAsyncTask asyncProcess;
            final InterruptableProcessDialog interruptableProcessDialog;

            boolean watchURLProcessed = false;
            if(YoutubeDL.hasPlaylist(urlStr)) {

                // Fetch watch video first and then fetch playlist urls.
                String effectiveURL = urlStr;
                if(YoutubeDL.isWatchURL(urlStr)) {
                    String watchURL = YoutubeDL.normalizeWatchURL(urlStr);
                    fallbackToYoutubeDL(watchURL);
                    watchURLProcessed = true;
                    effectiveURL = YoutubeDL.normalizePlaylistURL(urlStr);
                }

                asyncProcess = YoutubeDL.fetchPlaylistAsync(youtubeDLExecutable, effectiveURL);
                interruptableProcessDialog = new InterruptableProcessDialog(asyncProcess, false);
                YoutubeDLResultEventListener youtubeDLResultEventListener = fetchDefaultPlaylistFetchProcessListener(effectiveURL, watchURLProcessed);
                ((YoutubeDLProcessor) asyncProcess).addYoutubeDLResultEventListener(youtubeDLResultEventListener);

            } else {
                asyncProcess =
                    YoutubeDL.fetchBestAsyncProcess(youtubeDLExecutable, urlStr, fetchDefaultFetchProcessListeners(urlStr));
                interruptableProcessDialog = new InterruptableProcessDialog(asyncProcess, true);
            }

            asyncProcess.registerListener(new AsyncTaskListener() {
                public void onSuccess(InterruptableAsyncTask interruptableAsyncTask) {
                    if(interruptableProcessDialog.isVisible()) {
                        interruptableProcessDialog.close();
                    }
                }

                public void onFailure(InterruptableAsyncTask interruptableAsyncTask) {
                    if(isYoutubeVideo(urlStr)) {
                        attemptYoutubeVideoUrlFetch(urlStr);
                    }
                    if(interruptableProcessDialog.isVisible()) {
                        interruptableProcessDialog.close();
                    }
                }
            });

            asyncProcess.execute();
            interruptableProcessDialog.showDialog();

        } catch (Exception ex) {
            handleYoutubeDLFailure(ex.getMessage(), ex, urlStr);
        }
    }

    private YoutubeDLResultEventListener fetchDefaultPlaylistFetchProcessListener(final String urlStr, final boolean watchURLProcessed) {
        final AtomicInteger counter = new AtomicInteger(0);
        return new YoutubeDLResultEventListener() {
            @Override
            public void onYoutubeDLResultEvent(YoutubeDLResultEvent youtubeDLResultEvent) {
                YoutubeDLResult youtubeDLResult = youtubeDLResultEvent.getYoutubeDLResult();
                if(counter.incrementAndGet() == 1 && !watchURLProcessed) {

                    doHandleYoutubeDLCompletion(youtubeDLResult, urlStr);

                } else {
                    // Adding to now playing list.
                    String retrievedTitle = youtubeDLResult.getTitle();
                    String retrievedUrl = youtubeDLResult.getUrl();
                    String retrievedFilename = youtubeDLResult.getFilename();

                    NowPlayingListData nowPlayingListData = new NowPlayingListData(nowPlaylingList.size(), retrievedTitle, retrievedTitle, retrievedFilename,
                            retrievedUrl, true);
                    addTrack(nowPlayingListData);
                    log.debug(String.format("Added %s to playlist at position %s", retrievedFilename, nowPlaylingList.size()));
                }
            }
        };
    }

    private boolean isYoutubeVideo(String urlStr) {
        return (urlStr.toLowerCase().contains("youtube.com") || urlStr.toLowerCase().contains("youtu.be"));
    }

    private void handleYoutubeDLFailure(String message, Exception ex, String urlStr) {
        JOptionPane.showMessageDialog(null, message, "Failed", JOptionPane.ERROR_MESSAGE);
        log.error(message, ex);
    }

    private List<AsyncTaskListener> fetchDefaultFetchProcessListeners(final String urlStr) {
        AsyncTaskListener asyncTaskListener = new AsyncTaskListener() {
            public void onSuccess(InterruptableAsyncTask interruptableAsyncTask) {
                if(!interruptableAsyncTask.isInterrupted()) {
                    AsyncProcess asyncProcess = (AsyncProcess) interruptableAsyncTask;
                    handleYoutubeDLCompletion(asyncProcess.getProcess(), urlStr);
                }
            }

            public void onFailure(InterruptableAsyncTask interruptableAsyncTask) {
                if(!interruptableAsyncTask.isInterrupted()) {
                    handleYoutubeDLFailure("Failed to retrieve media.", null, urlStr);
                }
            }
        };
        return wrapInList(asyncTaskListener);
    }

    private void handleYoutubeDLCompletion(Process process, String urlStr) {
        try {
            YoutubeDLResult youtubeDLResult = YoutubeDL.retrieveYoutubeDLResult(process);
            doHandleYoutubeDLCompletion(youtubeDLResult, urlStr);
        } catch (Exception ex) {
            handleYoutubeDLFailure(ex.getMessage(), ex, urlStr);
        }
    }

    private void doHandleYoutubeDLCompletion(YoutubeDLResult youtubeDLResult, String urlStr) {
        try {
            clearNowPlayingList();

            String retrievedTitle = youtubeDLResult.getTitle();
            String retrievedUrl = youtubeDLResult.getUrl();
            String retrievedFilename = youtubeDLResult.getFilename();
            this.currentTrack = new NowPlayingListData(nowPlaylingList.size(), retrievedTitle, retrievedTitle, retrievedFilename,
                    retrievedUrl, true);

            log.debug(youtubeDLResult);

            addToNowPlayinglistAndStartPlaying();
        } catch (Exception ex) {
            handleYoutubeDLFailure(ex.getMessage(), ex, urlStr);
        }
    }

    private static List wrapInList(Object object) {
        List list = new ArrayList();
        list.add(object);
        return list;
    }

    private boolean attemptYoutubeVideoUrlFetch(String urlStr) {
        try {
            if(isYoutubeVideo(urlStr)) {
                YoutubeLink youtubeLink = YoutubeUrlFetcher.getBest(YoutubeUrlFetcher.fetch(urlStr));
                urlStr = youtubeLink.getUrl();

                if(!testURL(urlStr)) {
                    return false;
                }

                this.currentTrack = new NowPlayingListData(nowPlaylingList.size(), youtubeLink.getFileName(), urlStr, urlStr,
                        urlStr, true);
            } else {
                this.currentTrack = new NowPlayingListData(nowPlaylingList.size(), urlStr, urlStr, urlStr,
                        urlStr, true);
            }

            addToNowPlayinglistAndStartPlaying();
            return true;

        } catch (URLFetchException ex) {
            log.error("Could not recognize URL.", ex);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Could not recognize URL. Error: " + ex, "Error Occurred!", JOptionPane.ERROR_MESSAGE);
            log.error("Could not recognize URL.", ex);
        }
        return false;
    }

    private boolean testURL(String urlStr) {
        Client client = null;
        try {
             client = HttpClientUtils.getNewClient();

            Response response = client.target(urlStr).
                    request()
                    .get();
            if (response == null || response.getStatus() != Response.Status.OK.getStatusCode()) {
                log.error(String.format("URL could not be connected correctly: %s | Response code: %s", urlStr, response));
                return false;
            }
        } catch (Exception e) {
            log.error(String.format("Exception occurred when fetching URL: %s", urlStr), e);
            return false;
        } finally {
            if(client != null) {
                client.close();
            }
        }
        return true;
    }

    private void addToNowPlayinglistAndStartPlaying() {
        nowPlaylingList.add(currentTrack);
        new Thread(this).start();
    }

    private void playFrom(float time) {
        log.debug("Playing from: " + time);
        startFrom = time;
        new Thread(this).start();
    }

    public void pause() {
        for(MediaPlayer mediaPlayer : getAllPlayers()) {
            mediaPlayer.pause();
        }
        log.debug("Playing paused");

        if (!getPlayer().isPlaying()) {
            VLCJ_AUDIO_PLAYER.setPaused();
            VLCJ_VIDEO_PLAYER.setPaused();
        }
    }

    public void toggleMute() {
        getCurrentPlayer().mute(!getCurrentPlayer().isMute());
    }


    public void stopPlay() {
        for(VLCJPlayer vlcjPlayer : VLCJ_PLAYERS) {
            vlcjPlayer.getPlayer().stop();
        }
        log.debug("Playing stopped");
        VLCJ_AUDIO_PLAYER.setPaused();
        VLCJ_VIDEO_PLAYER.setPaused();
    }

    public int getVolume() {
        return this.getPlayer().getVolume();
    }

    public void setVolume(int volume) {
        for(MediaPlayer mediaPlayer : this.getAllPlayers()) {
            mediaPlayer.setVolume(volume);
        }
    }

    /**
     * This class's main thread run implies media play.
     */
    public void run() {
        synchronized (libXInitialized) {
            if (!libXInitialized.get()) {
                log.info("libX not initialized. Initializing...");
                LibXUtil.initialise();
                libXInitialized.set(true);
                log.info("libX initialization successful.");
            }
        }
        try {
            if (this.currentTrack != null && this.currentTrack.getLocation() != null) {

                log.debug("Signal count" + playSignal.getCount());

                boolean togglingFullscreen = isTogglingFullscreen.getAndSet(false);
                if(togglingFullscreen) {
                    stopPlay();
                }
                // Before playing the requested track, the previous one needs to be stopped; Aiming for single instance media player.
                waitToBeReady();

                resetSeekbar();

                if(currentTrack.isMovie() && JavaVersionUtils.isGreaterThan6() && OSInfo.isOSMac()) {

                    VLCJVideoPlayer videoPlayer = ((VLCJVideoPlayer) VLCJ_VIDEO_PLAYER);
                    videoPlayer.setTitle(this.currentTrack.getName());

                    ((VLCJVideoPlayer) VLCJ_VIDEO_PLAYER)
                            .playInFx(currentTrack.getLocation());

                    if (startFrom != 0) {
                        ((VLCJVideoPlayer) VLCJ_VIDEO_PLAYER).getFXPlayer().setPosition(startFrom);
                        startFrom = 0;
                    }

                } else {

                    // Hardware rendering available.

                    final MediaPlayer mediaPlayer = getPlayer();
                    if (mediaPlayer == VLCJ_VIDEO_PLAYER.getPlayer()) {
                        VLCJVideoPlayer videoPlayer = ((VLCJVideoPlayer) VLCJ_VIDEO_PLAYER);
                        videoPlayer.setVisible(true);
                        videoPlayer.setTitle(this.currentTrack.getName());
                    } else {
                        ((VLCJVideoPlayer) VLCJ_VIDEO_PLAYER).setVisible(false);
                    }

                    try {
                        mediaPlayer.setPlaySubItems(true);
                        if(togglingFullscreen) {
                            log.debug("Playing already loaded media: " + this.currentTrack.getLocation());
                            mediaPlayer.play();
                        } else {
                            log.debug("Loading and playing new media...");
                            //TODO: Make options dynamic from UI.
                            String[] options = loadMediaPlayerOptions();
                            log.debug("Media Player Options loaded: " + StringUtils.join(options, " "));
                            mediaPlayer.playMedia(this.currentTrack.getLocation(), options);

                            log.debug("Starting media: " + this.currentTrack.getLocation());
                            mediaPlayer.startMedia(this.currentTrack.getLocation());

                        }

                        if (startFrom != 0) {
                            log.debug("Setting media position to: " + startFrom);
                            mediaPlayer.setPosition(startFrom);
                            startFrom = 0;
                        }

                    } catch (Exception ex) {
                        log.error(ex.getMessage(), ex);
                        JOptionPane.showMessageDialog(null, "Could not play the file. Error: " + ex, "Error Occurred!", JOptionPane.ERROR_MESSAGE);
                        if(!mediaPlayer.isPlaying()) {
                            playSignal.countDown();
                            playerControlPanel.setPaused();
                        }
                    }
                }

                playSignal = new CountDownLatch(1);
                playerControlPanel.getSeekbar().setEnabled(true);

            } else {
                playerControlPanel.setPaused();
                //TODO: Do same for v players.
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Could not play the file. Error: " + ex, "Error Occurred!", JOptionPane.ERROR_MESSAGE);
            log.debug("Could not play the file. Error: ", ex);
        }
    }

    private String[] loadMediaPlayerOptions() {
        List<String> optionsList = new ArrayList<String>();
        Iterator<String> it = PropertyManager.getConfigurationMap().keySet().iterator();
        while(it.hasNext()) {
            String key = it.next();
            if(StringUtils.contains(key, "media.player.options.")) {
                String option = PropertyManager.getConfigurationMap().get(key);
                optionsList.add(option);
            }
        }
        return optionsList.toArray(new String[optionsList.size()]);
    }

    private void registerPlayProgressHandler(MediaPlayer mediaPlayer) {
        if(playProgressHandler == null) {
            // Lazy initialize PlayProgressHandler instance.
            playProgressHandler = new PlayProgressHandler(VLCJ_AUDIO_PLAYER, VLCJ_VIDEO_PLAYER);
            playProgressHandler.start(mediaPlayer);
        } else {
            synchronized (playProgressHandler) {
                playProgressHandler.restart(mediaPlayer);
            }
        }
    }

    private synchronized void registerNewSeekEventListeners() {
        if(seekEventListener == null) {
            seekEventListener = getNewDefaultSeekEventListener();
            for(VLCJPlayer vlcjPlayer : VLCJ_PLAYERS) {
                vlcjPlayer.registerSeekEventListener(seekEventListener);
            }
        } else {
            synchronized (seekEventListener) {
                for(VLCJPlayer vlcjPlayer : VLCJ_PLAYERS) {
                    vlcjPlayer.unRegisterSeekEventListener(seekEventListener);
                }
                seekEventListener = getNewDefaultSeekEventListener();
                for(VLCJPlayer vlcjPlayer : VLCJ_PLAYERS) {
                    vlcjPlayer.registerSeekEventListener(seekEventListener);
                }
            }
        }
    }

    private SeekEventListener getNewDefaultSeekEventListener() {
        return new SeekEventListener() {
            @Override
            public void onSeeked(int value) {
                seekTo(value / 100f);
            }
        };
    }

    private void resetSeekbar() {
        VLCJ_AUDIO_PLAYER.resetSeekbar();
        VLCJ_VIDEO_PLAYER.resetSeekbar();
    }

    private void waitToBeReady() {
        try {
            playSignal.await();
        } catch (InterruptedException e) {
            log.error(e);
        }
    }

    public String getNowPlayingUrl() {
        return this.currentTrack.getLocation();
    }

    public synchronized void play(final List<Track> trackList) {

        clearNowPlayingList();

        /*
         * If more than one track passed, play the first track readily, process the rest of the tracks asynchronously.
         * */

        if(!trackList.isEmpty()) {
            NowPlayingListData nowPlayingListData = getNowPlayingListData(trackList.get(0));
            addTrack(nowPlayingListData);
        }

        //resetIteratorPos();
        log.debug("Playable item available? " + listTraverser.hasNext());
        if(listTraverser.hasNext()){
            currentTrack = listTraverser.next();
            log(currentTrack);
        }
        play();


        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                for (int i = 1; i < trackList.size(); i++) {
                    addTrack(getNowPlayingListData(trackList.get(i)));
                }
            }
        });

    }

    private void log(NowPlayingListData currentTrack) {
        if (currentTrack != null) {
            log.debug(String.format("Playing item -> name: %s, location: %s, trackid: %s, isMovie: %s", currentTrack.getName(),
                    currentTrack.getLocation(), String.valueOf(currentTrack.getTrackId()), currentTrack.isMovie()));
        } else {
            log.debug("Track is null.");
        }
    }

    public void previous() {
        if(hasPrevious()){
            currentTrack = listTraverser.previous();
            play();
        }
    }

    public boolean hasNext() {
        return listTraverser.hasNext();
    }

    public void next() {
        if(hasNext()){
            NowPlayingListData nextTrack = currentTrack;
            currentTrack = listTraverser.next();
            avoidPlayingSameTrackOnNext(nextTrack, currentTrack);
        }
    }

    private void avoidPlayingSameTrackOnNext(NowPlayingListData nextTrack, NowPlayingListData currentTrack) {
        if(nextTrack == currentTrack) {
            next();
        } else {
            play();
        }
    }

    public void seekTo(float percentage) {
        MediaPlayer player = getCurrentPlayer();
        log.debug("Setting position to: " + percentage);
        player.setPosition(percentage);


        if(!isPlaying() && !isSeekValueAdjusting() && hasPrevious()) {
            // Seeked when nothing is playing.
            listTraverser.previous();
            play();

        } else if(!player.isPlaying()) {
            // Seeked when paused.
            updateSeekPositions(player, VLCJ_PLAYERS);
        }
    }

    public boolean hasPrevious() {
        return listTraverser.hasPrevious();
    }

    public void addMediaPlayerListener(final MediaPlayerEventListener listener) {
        eventListenerList.add(listener);
    }

    public TraversableLinkedList<NowPlayingListData> getNowPlaylingList() {
        return nowPlaylingList;
    }

    public boolean isPlaying() {
        return playSignal.getCount() > 0;
    }

    public JPanel getPlayerControlPanel() {
        return playerControlPanel;
    }

    public void clearNowPlayingList() {
        nowPlaylingList.clear();
    }

    private void addTrack(final NowPlayingListData nowPlayingListData) {
        nowPlaylingList.add(nowPlayingListData);
    }

    public MediaPlayer getPlayer() {
        if(currentTrack != null && currentTrack.isMovie()) {
            return VLCJ_VIDEO_PLAYER.getPlayer();
        }

        return VLCJ_AUDIO_PLAYER.getPlayer();
    }

    /**
     * Player that is currently being used.
     *
     * @return
     */
    public MediaPlayer getCurrentPlayer() {
        if(currentTrack != null && currentTrack.isMovie()) {
            if(JavaVersionUtils.isGreaterThan6() && OSInfo.isOSMac()) {
                return ((VLCJVideoPlayer) VLCJ_VIDEO_PLAYER).getFXPlayer();
            }
            return VLCJ_VIDEO_PLAYER.getPlayer();
        }

        return VLCJ_AUDIO_PLAYER.getPlayer();

    }

    public List<MediaPlayer> getAllPlayers() {
        List<MediaPlayer> playerList = new ArrayList<MediaPlayer>();
        playerList.add(VLCJ_AUDIO_PLAYER.getPlayer());
        playerList.add(VLCJ_VIDEO_PLAYER.getPlayer());
        if(JavaVersionUtils.isFXAvailable()) {
            playerList.add(
                    ((VLCJVideoPlayer) VLCJ_VIDEO_PLAYER).getFXPlayer());
        }

        return playerList;
    }

    /**
     *
     * Handles Play progress for a given VLCJPlayers.
     *
     */
    private class PlayProgressHandler {

        private final VLCJPlayer[] vlcjPlayers;
        private Future<?> future;
        private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        public PlayProgressHandler(VLCJPlayer... vlcjPlayers) {
            this.vlcjPlayers = vlcjPlayers;
        }

        public void start(final MediaPlayer mediaPlayer) {
            restart(mediaPlayer);
        }

        public void restart(final MediaPlayer mediaPlayer) {
            shutdown();
            future = init(mediaPlayer);
        }

        private Future<?> init(final MediaPlayer mediaPlayer) {
            return scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

                public void run() {

                    updateSeekPositions(mediaPlayer, vlcjPlayers);

                }
            }, 0, 900, TimeUnit.MILLISECONDS); // We update once every slightly < 1sec to see (seek) change for every sec.
        }

        private void shutdown() {
            if(future != null && !future.isCancelled()) {
                future.cancel(true);
            }
        }
    }

    private void updateSeekPositions(MediaPlayer mediaPlayer, VLCJPlayer[] vlcjPlayers) {
        long mediaLength = mediaPlayer.getLength();
        int seekPosition = (int) (mediaPlayer.getPosition() * 100);
        log.debug("Updating seek position to: " + seekPosition);

        SeekInfo seekInfo = new SeekInfo(mediaPlayer.getTime(), mediaLength, seekPosition);

        for (VLCJPlayer vlcjPlayer : vlcjPlayers) {
            vlcjPlayer.updateSeekbar(seekInfo);
        }
    }

    public static NowPlayingListData getNowPlayingListData(Track track) {
        return new NowPlayingListData(track.getTrackId(), track.getTrackName(), track.getAdditionalTrackInfo().getAdditionalInfo("Artist"),
                track.getAdditionalTrackInfo().getAdditionalInfo("Album"), track.getLocation(),
                track.isMovie() || track.hasVideo());
    }

    public boolean isCurrentTrack(long trackId) {
        // TODO: Handle Long.MAX_VALUE.
        if(isPlaying() && currentTrack != null) {
            return currentTrack.getTrackId() == trackId;
        }
        return false;
    }

    public void releaseResources() {
        for(MediaPlayer mediaPlayer : getAllPlayers()) {
            mediaPlayer.release();
        }
        mediaPlayerFactory.release();
    }

    public void registerDragAndDropEventListener(PlayerMediaFilesDroppedEventListener playerMediaFilesDroppedEventListener) {
        for(final VLCJPlayer vlcjPlayer : VLCJ_PLAYERS) {
            vlcjPlayer.registerDragAndDropEvent(playerMediaFilesDroppedEventListener);
        }
    }

    @Override
    public void toggleNowPlayingList() {
        nowPlayingListFrame.setVisible(!nowPlayingListFrame.isVisible());
    }
}