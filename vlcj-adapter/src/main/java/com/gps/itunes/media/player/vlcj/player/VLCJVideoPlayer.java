package com.gps.itunes.media.player.vlcj.player;

import com.gps.imp.utils.JavaVersionUtils;
import com.gps.imp.utils.SingleQueuedThreadExecutor;
import com.gps.itunes.lib.parser.utils.OSInfo;
import com.gps.itunes.media.player.vlcj.player.impl.DummyFXPlayerFrame;
import com.gps.itunes.media.player.vlcj.player.impl.FXPlayerFrameImpl;
import com.gps.itunes.media.player.vlcj.ui.player.FullscreenVideoPlayerFrame;
import com.gps.itunes.media.player.vlcj.ui.player.VideoPlayerFrame;
import com.gps.itunes.media.player.vlcj.ui.player.events.*;
import org.apache.log4j.Logger;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.DefaultFullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.FullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;
import uk.co.caprica.vlcj.player.embedded.windows.Win32FullScreenStrategy;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class VLCJVideoPlayer implements VLCJPlayer {

	protected MediaPlayer player;
    protected VideoPlayerFrame vFrame;
    protected AtomicBoolean ignoreSeekbarChange = new AtomicBoolean(false);
    private AtomicBoolean isFullScreen = new AtomicBoolean(false);
    protected final MediaPlayerFactory mediaPlayerFactory;
    protected final List<SeekEventListener> seekEventListenerList = new ArrayList<SeekEventListener>();
    protected FullscreenVideoPlayerFrame fullscreenFrame = new FullscreenVideoPlayerFrame();
    protected FXPlayerFrame fxPlayerFrame;

    private SingleQueuedThreadExecutor singleQueuedThreadExecutor = new SingleQueuedThreadExecutor();

    private static final Logger LOG = Logger.getLogger(VLCJVideoPlayer.class);

    public VLCJVideoPlayer(MediaPlayerFactory mediaPlayerFactory) {
        this.mediaPlayerFactory = mediaPlayerFactory;
        if(JavaVersionUtils.isGreaterThan6() && OSInfo.isOSMac()) {
            fxPlayerFrame = new FXPlayerFrameImpl();
        } else {
            fxPlayerFrame = DummyFXPlayerFrame.getDummyInstance();
        }
        init();
	}

    protected void init() {

        vFrame = new VideoPlayerFrame();
        FullScreenStrategy fullScreenStrategy;
        if(OSInfo.isOSWin()) {
            fullScreenStrategy = new Win32FullScreenStrategy(fullscreenFrame);
        } else {
            // TODO: Add Linux fullscreenStrategy: XFullScreenStrategy?
            fullScreenStrategy = new DefaultFullScreenStrategy(fullscreenFrame);
        }
        player = mediaPlayerFactory.newEmbeddedMediaPlayer(fullScreenStrategy);

        CanvasVideoSurface videoSurface = mediaPlayerFactory.newVideoSurface(vFrame.getFrameCanvas());
        ((EmbeddedMediaPlayer)(player)).setVideoSurface(videoSurface);

        vFrame.getSeekbar().addChangeListener(
                new SeekChangeListener(ignoreSeekbarChange,
                        vFrame.getSeekbar(),
                        seekEventListenerList));
        fullscreenFrame.getSeekbar().addChangeListener(
                new SeekChangeListener(ignoreSeekbarChange,
                        fullscreenFrame.getSeekbar(),
                        seekEventListenerList));


        fxPlayerFrame.getSeekbar().addChangeListener(
                new SeekChangeListener(ignoreSeekbarChange,
                        fxPlayerFrame.getSeekbar(),
                        seekEventListenerList));

    }

    public MediaPlayer getPlayer() {
		return player;
	}

    public MediaPlayer getFXPlayer() {
        return getFxPlayerFrame().getPlayer();
    }

    public void setVisible(boolean visible) {
        vFrame.setVisible(visible);
        if(visible) {
            vFrame.toFront();
            vFrame.getVideoPanel().requestFocus();
            vFrame.getVideoPanel().requestFocusInWindow();
        }
    }

    public void resetSeekbar() {
        ignoreSeekbarChange.set(true);
        vFrame.getSeekbar().setValue(0);
        fullscreenFrame.getSeekbar().setValue(0);
        fxPlayerFrame.getSeekbar().setValue(0);
        ignoreSeekbarChange.set(false);
    }

    public void registerSeekEventListener(SeekEventListener seekEventListener) {
        synchronized (seekEventListenerList) {
            seekEventListenerList.add(seekEventListener);
        }
    }

    public void unRegisterSeekEventListener(SeekEventListener seekEventListener) {
        synchronized (seekEventListenerList) {
            Iterator iterator = seekEventListenerList.iterator();
            while(iterator.hasNext()) {
                if(seekEventListener == iterator.next()) {
                    iterator.remove();
                }
            }
        }
    }

    public void updateSeekbar(SeekInfo seekInfo) {
        ignoreSeekbarChange.set(true);

        String startValue = SeekInfo.convertIntoReadableTime(seekInfo.getTrackStartValue());
        vFrame.getStartTimeLabel().setText(startValue);
        fullscreenFrame.getStartTimeLabel().setText(startValue);
        fxPlayerFrame.getStartTimeLabel().setText(startValue);

        vFrame.getSeekbar().setValue(seekInfo.getSeekPosition());
        fullscreenFrame.getSeekbar().setValue(seekInfo.getSeekPosition());
        fxPlayerFrame.getSeekbar().setValue(seekInfo.getSeekPosition());

        String endValue = SeekInfo.convertIntoReadableTime(seekInfo.getTrackEndValue());
        vFrame.getEndTimeLabel().setText(endValue);
        fullscreenFrame.getEndTimeLabel().setText(endValue);
        fxPlayerFrame.getEndTimeLabel().setText(endValue);

        if(!vFrame.getSeekbar().getValueIsAdjusting() && !fullscreenFrame.getSeekbar().getValueIsAdjusting() && !fxPlayerFrame.getSeekbar().getValueIsAdjusting()) {
            vFrame.updateSeekbar();
            fullscreenFrame.updateSeekbar();
            fxPlayerFrame.updateSeekbar();
        }
        ignoreSeekbarChange.set(false);
    }

    public boolean isSeekValueAdjusting() {
        return vFrame.getSeekbar().getValueIsAdjusting()
                || fullscreenFrame.getSeekbar().getValueIsAdjusting()
                || fxPlayerFrame.getSeekbar().getValueIsAdjusting();
    }

    public void attachCommandListener(VideoPlayerKeyListener videoPlayerKeyListener) {
        vFrame.requestFocus();
        vFrame.getVideoPanel().addKeyListener(videoPlayerKeyListener);
        vFrame.getSeekbar().addKeyListener(videoPlayerKeyListener);

        fullscreenFrame.requestFocus();
        fullscreenFrame.getVideoPanel().addKeyListener(videoPlayerKeyListener);
        fullscreenFrame.getSeekbar().addKeyListener(videoPlayerKeyListener);

        fxPlayerFrame.requestFocus();
        fxPlayerFrame.getVideoPanel().addKeyListener(videoPlayerKeyListener);
        fxPlayerFrame.getSeekbar().addKeyListener(videoPlayerKeyListener);

    }

    public void attachCommandListener(VideoPlayerMouseAdapter videoPlayerMouseAdapter) {
        vFrame.getFrameCanvas().addMouseListener(videoPlayerMouseAdapter);
        fullscreenFrame.getFrameCanvas().addMouseListener(videoPlayerMouseAdapter);
        fxPlayerFrame.getFrameCanvas().addMouseListener(videoPlayerMouseAdapter);
    }

    public void attachCommandListener(VideoPlayerMouseWheelListener videoPlayerMouseWheelListener) {
        vFrame.getFrameCanvas().addMouseWheelListener(videoPlayerMouseWheelListener);
        fullscreenFrame.getFrameCanvas().addMouseWheelListener(videoPlayerMouseWheelListener);
        fxPlayerFrame.getFrameCanvas().addMouseWheelListener(videoPlayerMouseWheelListener);
    }

    public void setPaused() {
        fullscreenFrame.drawPaused();
        vFrame.getBasicPlayerControlPanel().setPaused();
        fullscreenFrame.getBasicPlayerControlPanel().setPaused();
        fxPlayerFrame.getBasicPlayerControlPanel().setPaused();
    }

    public void setPlaying() {
        vFrame.getBasicPlayerControlPanel().setPlaying();
        fullscreenFrame.getBasicPlayerControlPanel().setPlaying();
        fxPlayerFrame.getBasicPlayerControlPanel().setPlaying();
    }

    public void registerPlayerControlEventListener(PlayerControlEventListener playerControlEventListener) {
        vFrame.getBasicPlayerControlPanel().setPlayerControlEventListener(playerControlEventListener);
        fullscreenFrame.getBasicPlayerControlPanel().setPlayerControlEventListener(playerControlEventListener);
        fxPlayerFrame.getBasicPlayerControlPanel().setPlayerControlEventListener(playerControlEventListener);
    }

    public void exitFullscreen() {
        if(isFullscreen()) {
            toggleFullScreen();
        }
    }

    public void toggleFullScreen() {
        isFullScreen.set(!isFullScreen.get());

        if(isFullScreen.get()) {
            vFrame.disableVideo();
            fullscreenFrame.enableVideo(vFrame.getFrameCanvas());
        } else {
            fullscreenFrame.disableVideo();
            vFrame.enableVideo(vFrame.getFrameCanvas());
        }

        if(player instanceof EmbeddedMediaPlayer) {
            ((EmbeddedMediaPlayer) (player)).setFullScreen(isFullScreen.get());
        }
    }

    public boolean isFullscreen() {
        return isFullScreen.get();
    }


    public void showSlider() {
        fullscreenFrame.showSliderAndHideLater();
    }

    public VideoPlayerFrame getVideoPlayerFrame() {
        return vFrame;
    }

    public FullscreenVideoPlayerFrame getFullscreenFrame() {
        return fullscreenFrame;
    }

    public FXPlayerFrame getFxPlayerFrame() {
        return fxPlayerFrame;
    }

    public void setTitle(String title) {
        vFrame.setTitle(title);
        fxPlayerFrame.setTitle(title);
    }

    public void playInFx(String location) {
        fxPlayerFrame.play(location);
    }

    public void addOverlay(String message, boolean sticky) {
        final EmbeddedMediaPlayer embeddedMediaPlayer = ((EmbeddedMediaPlayer)(player));
        embeddedMediaPlayer.setOverlay(getOverlayWindow(message));
        embeddedMediaPlayer.enableOverlay(true);
        if(!sticky) {
            singleQueuedThreadExecutor.terminateExistingAndScheduleForLater(new Runnable() {
                public void run() {
                    embeddedMediaPlayer.setOverlay(null);
                    embeddedMediaPlayer.enableOverlay(false);
                }
            }, 3000, TimeUnit.SECONDS);
        }
    }

    private static Window getOverlayWindow(String message) {
        final JWindow transparentWindow = new JWindow();

        // Set basic window opacity if required - the window system must support WindowTranslucency (i.e. PERPIXEL_TRANSLUCENT)!
        //transparentWindow.setOpacity(0.8f);
        // White with transparent alpha channel - WindowTranslucency is required for translucency.
        transparentWindow.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.0f));

        final JLabel superImposedLightweigtLabel = new JLabel(message, JLabel.CENTER);
        superImposedLightweigtLabel.setOpaque(true);

        transparentWindow.getContentPane().add(superImposedLightweigtLabel);
        return transparentWindow;
    }
}
