package com.gps.itunes.media.player.vlcj.player;

import com.gps.imp.utils.JavaVersionUtils;
import com.gps.imp.utils.SingleQueuedThreadExecutor;
import com.gps.itunes.lib.parser.utils.OSInfo;
import com.gps.itunes.media.player.vlcj.player.impl.DummyFXPlayerFrame;
import com.gps.itunes.media.player.vlcj.player.impl.FXPlayerFrameImpl;
import com.gps.itunes.media.player.vlcj.ui.player.FullscreenVideoPlayerFrame;
import com.gps.itunes.media.player.vlcj.ui.player.VideoPlayerFrame;
import com.gps.itunes.media.player.vlcj.ui.player.events.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.ComponentVideoSurface;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
    protected final boolean isFXPlayer;

    private final SingleQueuedThreadExecutor singleQueuedThreadExecutor = new SingleQueuedThreadExecutor();

    private static final Logger LOG = LogManager.getLogger(VLCJVideoPlayer.class);

    public VLCJVideoPlayer(MediaPlayerFactory mediaPlayerFactory) {
        this.mediaPlayerFactory = mediaPlayerFactory;
        if(JavaVersionUtils.isGreaterThan6() && OSInfo.isOSMac()) {
            fxPlayerFrame = new FXPlayerFrameImpl();
            isFXPlayer = true;
        } else {
            fxPlayerFrame = DummyFXPlayerFrame.getDummyInstance();
            isFXPlayer = false;
        }
        init();
	}

    protected void init() {

        vFrame = new VideoPlayerFrame();
        ComponentVideoSurface videoSurface = mediaPlayerFactory.videoSurfaces().newVideoSurface(vFrame.getFrameCanvas());
        if(JavaVersionUtils.isGreaterThan6() && OSInfo.isOSMac()) {
            player = fxPlayerFrame.getPlayer();
        } else {
            player = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
            ((EmbeddedMediaPlayer)(player)).videoSurface().set(videoSurface);
        }

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

    public void registerDragAndDropEvent(DropTarget dropTarget) {
        if(vFrame == null) {
            LOG.error("Video Frame not initialized, cannot register drag and drop event.");
            return;
        }
        vFrame.setDropTarget(dropTarget);
        fxPlayerFrame.getFrameCanvas().setDropTarget(dropTarget);
        fxPlayerFrame.getJFXPanel().setDropTarget(dropTarget);
    }

    public MediaPlayer getPlayer() {
		return player;
	}

    public MediaPlayer getFXPlayer() {
        return getFxPlayerFrame().getPlayer();
    }

    public void setVisible(boolean visible) {
        VideoPlayerFrame playerFrame;
        if(isFullscreen()) {
            playerFrame = getFullscreenFrame();
        } else {
            playerFrame = getVideoPlayerFrame();
        }
        playerFrame.setVisible(visible);
        if(visible) {
            playerFrame.toFront();
            playerFrame.getVideoPanel().requestFocus();
            playerFrame.getVideoPanel().requestFocusInWindow();
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
        fxPlayerFrame.getJFXPanel().addKeyListener(videoPlayerKeyListener);
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

    public void setBufferingValue(float bufferingValue) {
        vFrame.setBufferingValue(bufferingValue);
        fullscreenFrame.setBufferingValue(bufferingValue);
        fxPlayerFrame.setBufferingValue(bufferingValue);
    }

    public void exitFullscreen() {
        if(isFullscreen()) {
            toggleFullScreen();
        }
    }

    public void toggleFullScreen() {
        isFullScreen.set(!isFullScreen.get());

        if (!isFXPlayer) {
            if(isFullScreen.get()) {
                vFrame.disableVideo();
                fullscreenFrame.enableVideo(vFrame.getFrameCanvas());
                fullscreenFrame.setTitle(vFrame.getTitle());
            } else {
                fullscreenFrame.disableVideo();
                vFrame.enableVideo(vFrame.getFrameCanvas());
            }
        }

        if(player instanceof EmbeddedMediaPlayer) {
            ((EmbeddedMediaPlayer) (player)).fullScreen().set(isFullScreen.get());
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
//        final EmbeddedMediaPlayer embeddedMediaPlayer = ((EmbeddedMediaPlayer)(player));
//        embeddedMediaPlayer.setOverlay(getOverlayWindow(message));
//        embeddedMediaPlayer.enableOverlay(true);
//        if(!sticky) {
//            singleQueuedThreadExecutor.terminateExistingAndScheduleForLater(new Runnable() {
//                public void run() {
//                    Window window = embeddedMediaPlayer.getOverlay();
//                    if(window != null) {
//                        window.removeAll();
//                        window.setVisible(false);
//                        window.dispose();
//                    }
//                    embeddedMediaPlayer.setOverlay(null);
//                }
//            }, 500, TimeUnit.SECONDS);
//        }

        final EmbeddedMediaPlayer embeddedMediaPlayer = ((EmbeddedMediaPlayer)(player));
        final Window overlay = getOverlayWindow(message);
        embeddedMediaPlayer.overlay().set(overlay);
        embeddedMediaPlayer.overlay().enable(true);
        if(!sticky) {
            singleQueuedThreadExecutor.terminateExistingAndScheduleForLater(new Runnable() {
                public void run() {
                    overlay.setVisible(false);
                    overlay.dispose();
                    embeddedMediaPlayer.overlay().set(null);
                    embeddedMediaPlayer.overlay().enable(false);
                }
            }, 3000, TimeUnit.SECONDS);
        }
    }

    private static Window getOverlayWindow(String message) {
        final JWindow transparentWindow = new JWindow();

        // Set basic window opacity if required - the window system must support WindowTranslucency (i.e. PERPIXEL_TRANSLUCENT)!
        //transparentWindow.setOpacity(0.8f);
        // White with transparent alpha channel - WindowTranslucency is required for translucency.
        transparentWindow.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.55f));

        final JLabel superImposedLightweightLabel = new JLabel(message, JLabel.CENTER);
        superImposedLightweightLabel.setOpaque(true);

        transparentWindow.getContentPane().add(superImposedLightweightLabel);
        // Determine what the default GraphicsDevice can support.
//        GraphicsEnvironment ge =
//                GraphicsEnvironment.getLocalGraphicsEnvironment();
//        GraphicsDevice gd = ge.getDefaultScreenDevice();
//        if(gd.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT)) {
//            transparentWindow.setOpacity(0.55f);
//        }
        transparentWindow.setSize(200, 30);
        transparentWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                LOG.debug("Overlay window activated...");
                transparentWindow.removeAll();
                transparentWindow.setVisible(true);
                transparentWindow.dispose();
            }
        });
        return transparentWindow;
    }
}
