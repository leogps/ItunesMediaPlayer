package com.gps.itunes.media.player.vlcj.player.impl;

import com.gps.ilp.utils.JavaVersionUtils;
import com.gps.ilp.utils.YoutubeLink;
import com.gps.ilp.utils.YoutubeUrlFetcher;
import com.gps.itunes.media.player.vlcj.player.*;
import com.gps.itunes.media.player.vlcj.player.events.MediaPlayerEventListener;
import com.gps.itunes.media.player.vlcj.ui.player.BasicPlayerControlPanel;
import com.gps.itunes.media.player.vlcj.ui.player.NowPlayingListData;
import com.gps.itunes.media.player.vlcj.ui.player.PlayerControlPanel;
import com.gps.itunes.media.player.vlcj.ui.player.events.*;
import com.gps.itunes.media.player.vlcj.ui.player.events.handler.FileOpenEventHandler;
import com.gps.itunes.media.player.vlcj.ui.player.events.handler.NetworkFileOpenEventHandler;
import com.gps.itunes.media.player.vlcj.ui.player.utils.GoToSpinnerDialog;
import com.gps.itunes.media.player.vlcj.ui.player.utils.GotoValueSubmissionEventListener;
import com.gps.itunes.media.player.vlcj.ui.player.utils.TrackTime;
import org.apache.log4j.Logger;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by leogps on 10/4/14.
 */
public class ItunesMediaPlayerImpl implements ItunesMediaPlayer {

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

    /**
     * Now Playing list.
     */
    private final List<NowPlayingListData> nowPlaylingList = new NowPlayingList();

    /**
     * List iterator to traverse left and right in the playlist.
     */
    private ListIterator<NowPlayingListData> listIterator = nowPlaylingList.listIterator();

    /**
     * Audio Player that is closely bound to the VLCJ adapter.
     */
    private final VLCJPlayer VLCJ_AUDIO_PLAYER;

    /**
     * Video Player that is closely bound to the VLCJ adapter.
     */
    private static final VLCJPlayer VLCJ_VIDEO_PLAYER = new VLCJVideoPlayer();

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

    private final FileOpenEventHandler fileOpenEventHandler = new FileOpenEventHandler();

    public ItunesMediaPlayerImpl(final PlayerControlPanel playerControlPanel) {
        this.playerControlPanel = playerControlPanel;
        this.VLCJ_AUDIO_PLAYER = new VLCJAudioPlayer(playerControlPanel);
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
            }

            @Override
            public void onVolumeDecreaseCommand(int decreasedBy) {
                handleVolumeDecreasedEvent(decreasedBy);
            }

            @Override
            public void onFastForwardCommand() {
                playerControlPanel.getSeekbar().setValue(playerControlPanel.getSeekbar().getValue() + 10);
            }

            @Override
            public void onFastReverseCommand() {
                playerControlPanel.getSeekbar().setValue(playerControlPanel.getSeekbar().getValue() - 10);
            }

            @Override
            public void onExitFullscreenCommand() {
                //TODO: get correct players position.
                if(getPlayer() == VLCJ_VIDEO_PLAYER.getPlayer() ) {
                    VLCJVideoPlayer videoPlayer = ((VLCJVideoPlayer) VLCJ_VIDEO_PLAYER);
                    if(videoPlayer.isFullscreen()) {
                        pause();
                        videoPlayer.toggleFullScreen();
                        playFrom(getPlayer().getPosition());
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
            }

            @Override
            public void onSeekIncreasedCommand(int increasedBy) {
                addToSeekValue(increasedBy);
            }

            @Override
            public void onToggleSubtitles() {
                if(getPlayer() == VLCJ_VIDEO_PLAYER.getPlayer() ) {
                    VLCJVideoPlayer videoPlayer = ((VLCJVideoPlayer) VLCJ_VIDEO_PLAYER);
                    videoPlayer.getPlayer().setSubTitleFile((String) null);
                }
            }

            @Override
            public void onMuteToggleCommand() {
                toggleMute();
            }

            private void addToSeekValue(int value) {
                // Seeking one seekbar will seek the rest of them.
                JSlider seekbar = ((VLCJVideoPlayer) VLCJ_VIDEO_PLAYER).getVideoPlayerFrame().getSeekbar();
                seekbar.setValue(seekbar.getValue() + value);
            }

            @Override
            public void goTo() {
                handleGoToEvent();
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
                        playFrom(percentage);
                    }
                });
    }

    private void handleFullScreenToggle() {
        if(getPlayer() == VLCJ_VIDEO_PLAYER.getPlayer()) {
            log.debug("Fullscreen toggle requested.");
            pause();
            ((VLCJVideoPlayer) VLCJ_VIDEO_PLAYER).toggleFullScreen();
            playFrom(getPlayer().getPosition());
        }
    }

    public void handleNetworkFileOpenEvent() {
        String url = NetworkFileOpenEventHandler.handle();
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
        File selectedFile = fileOpenEventHandler.handle();
        if (selectedFile != null) {
            play(selectedFile);
        }
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
            super.finished(mediaPlayer);
            log.debug("Finished!!");
            reportPlayCompletion();
            VLCJ_AUDIO_PLAYER.setPaused();
            VLCJ_VIDEO_PLAYER.setPaused();
            next();
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
        }

        @Override
        public void opening(MediaPlayer mediaPlayer) {
            super.opening(mediaPlayer);
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
            NowPlayingList npList = (NowPlayingList) getNowPlaylingList();
            if(!npList.isNowPlaylingListFrameVisible()) {
                npList.showNowPlayingList();
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

    public void play(File file) {
        this.stopPlay();
        nowPlaylingList.clear();

        this.currentTrack = new NowPlayingListData(file.getName(), file.getName(), file.getName(),
                file.getAbsolutePath(), true);
        nowPlaylingList.add(currentTrack);
        new Thread(this).start();
    }

    public void play(URL url) {
        this.stopPlay();
        nowPlaylingList.clear();

        try {
            String urlStr = url.toString();

            if(urlStr.toLowerCase().contains("youtube.com") || urlStr.toLowerCase().contains("youtu.be")) {
                YoutubeLink youtubeLink = YoutubeUrlFetcher.getBest(YoutubeUrlFetcher.fetch(urlStr));

                urlStr = youtubeLink.getUrl();

                this.currentTrack = new NowPlayingListData(youtubeLink.getFileName(), urlStr, urlStr,
                        urlStr, true);
            } else {
                this.currentTrack = new NowPlayingListData(urlStr, urlStr, urlStr,
                        urlStr, true);
            }

            nowPlaylingList.add(currentTrack);
            new Thread(this).start();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Could not recognize URL. Error: " + ex, "Error Occurred!", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void playFrom(float time) {
        stopPlay();
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
        VLCJ_AUDIO_PLAYER.getPlayer().stop();
        VLCJ_VIDEO_PLAYER.getPlayer().stop();
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
        try {
            if (this.currentTrack != null) {
                log.debug("Signal count" + playSignal.getCount());

                // Before playing the requested track, the previous one needs to be stopped; Aiming for single instance media player.
                waitToBeReady();

                resetSeekbar();

                if(currentTrack.isMovie() && JavaVersionUtils.isGreaterThan6()) {

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

                    mediaPlayer.playMedia(this.currentTrack.getLocation());

                    if (startFrom != 0) {
                        mediaPlayer.setPosition(startFrom);
                        startFrom = 0;
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

    public void play(final List<NowPlayingListData> trackLocations) {
        clearNowPlayingList();
        addTracks(trackLocations);
        resetIteratorPos();
        log.debug(listIterator.nextIndex());
        log.debug(listIterator.hasNext());
        if(listIterator.hasNext()){
            currentTrack = listIterator.next();
        }
        play();
    }

    public void previous() {
        if(listIterator.hasPrevious()){
            currentTrack = listIterator.previous();
            play();
        }
    }

    public boolean hasNext() {
        return listIterator.hasNext();
    }

    public void next() {
        if(hasNext()){
            currentTrack = listIterator.next();
            play();
        }
    }

    public void seekTo(float percentage) {
        MediaPlayer player = getCurrentPlayer();
        log.debug("Setting position to: " + percentage);
        player.setPosition(percentage);


        if(!isPlaying() && !isSeekValueAdjusting() && hasPrevious()) {
            // Seeked when nothing is playing.
            listIterator.previous();
            play();

        } else if(!player.isPlaying()) {
            // Seeked when paused.
            updateSeekPositions(player, VLCJ_PLAYERS);
        }
    }

    public boolean hasPrevious() {
        return listIterator.hasPrevious();
    }

    public void addMediaPlayerListener(final MediaPlayerEventListener listener) {
        eventListenerList.add(listener);
    }

    public List<NowPlayingListData> getNowPlaylingList() {
        return nowPlaylingList;
    }

    public boolean isPlaying() {
        return playSignal.getCount() > 0;
    }

    public JPanel getPlayerControlPanel() {
        return playerControlPanel;
    }

    public void clearNowPlayingList() {
        synchronized(nowPlaylingList) {
            listIterator = null;
            nowPlaylingList.clear();
            listIterator = nowPlaylingList.listIterator();
        }
    }

    private void addTracks(final List<NowPlayingListData> trackLocations) {
        synchronized(nowPlaylingList) {
            for(final NowPlayingListData trackLocation : trackLocations) {
                listIterator.add(trackLocation);
            }
        }
    }

    private void resetIteratorPos() {
        synchronized(listIterator) {
            listIterator = nowPlaylingList.listIterator();
        }
    }

    public MediaPlayer getPlayer() {

        synchronized (listIterator) {
            if(currentTrack != null && currentTrack.isMovie()) {
                return VLCJ_VIDEO_PLAYER.getPlayer();
            }

            return VLCJ_AUDIO_PLAYER.getPlayer();
        }

    }

    /**
     * Player that is currently being used.
     *
     * @return
     */
    public MediaPlayer getCurrentPlayer() {
        synchronized (listIterator) {
            if(currentTrack != null && currentTrack.isMovie()) {
                if(JavaVersionUtils.isGreaterThan6()) {
                    return ((VLCJVideoPlayer) VLCJ_VIDEO_PLAYER).getFXPlayer();
                }
                return VLCJ_VIDEO_PLAYER.getPlayer();
            }

            return VLCJ_AUDIO_PLAYER.getPlayer();
        }
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


}

