package com.gps.itunes.media.player.vlcj.player.impl;

import com.gps.itunes.media.player.vlcj.player.FXPlayerFrame;
import com.gps.itunes.media.player.vlcj.ui.player.VideoPlayerFrame;
import com.sun.jna.Memory;
import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import org.apache.log4j.Logger;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by leogps on 10/1/15.
 */
public class FXPlayerFrameImpl extends VideoPlayerFrame implements FXPlayerFrame {

    private static Logger LOG = Logger.getLogger(FXPlayerFrameImpl.class);

    private ImageView imageView;

    private DirectMediaPlayerComponent mediaPlayerComponent = new CanvasPlayerComponent();

    private WritableImage writableImage;
    
    private AtomicReference<WritableImage> writableImageReference = new AtomicReference<>();
    private AtomicReference<PixelWriter> pixelWriterReference = new AtomicReference<>();

    private Pane playerHolder;

    private WritablePixelFormat<ByteBuffer> pixelFormat;

    private FloatProperty videoSourceRatioProperty;

    private JFXPanel fxPanel;

    private static Future future;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private static final int AUTO_HIDE_HEADER_FOOTER_TIME = 5;

    public FXPlayerFrameImpl() {
        super();

        fxPanel = new JFXPanel();
        fxPanel.setBackground(Color.BLACK);
        getVideoPanel().add(fxPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setVisible(false);

        Platform.runLater(new Runnable() {
            public void run() {
                initFX(fxPanel);
            }
        });
    }

    private void initFX(JFXPanel fxPanel) {
        // This method is invoked on JavaFX thread
        Scene scene = createScene();
        fxPanel.setScene(scene);
        fxPanel.setBackground(Color.BLACK);
    }

    private Scene createScene() {
        playerHolder = new Pane();
        videoSourceRatioProperty = new SimpleFloatProperty(0.4f);
        pixelFormat = PixelFormat.getByteBgraPreInstance();
        initializeImageView();

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(playerHolder);
        Scene scene = new Scene(borderPane, -1, -1);
        scene.setFill(javafx.scene.paint.Color.BLACK);

        EventHandler handler = new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                handleUserAttentionRequest();
            }
        };
        borderPane.addEventFilter(MouseEvent.MOUSE_MOVED, handler);
        borderPane.addEventFilter(MouseEvent.MOUSE_CLICKED, handler);
        borderPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, handler);
        borderPane.addEventFilter(MouseEvent.MOUSE_PRESSED, handler);
        borderPane.addEventFilter(javafx.scene.input.KeyEvent.ANY, handler);
        
        scene.addEventFilter(MouseEvent.MOUSE_MOVED, handler);
        scene.addEventFilter(MouseEvent.MOUSE_CLICKED, handler);
        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, handler);
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, handler);
        scene.addEventFilter(javafx.scene.input.KeyEvent.ANY, handler);
        return scene;
    }

    private void initializeImageView() {
        initWritableImage();
        imageView = new ImageView(writableImage);
        playerHolder.getChildren().add(imageView);

        playerHolder.widthProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                        fitImageViewSize(newValue.floatValue(), (float) playerHolder.getHeight());
                    }
                });

        playerHolder.heightProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                fitImageViewSize((float) playerHolder.getWidth(), newValue.floatValue());
            }
        });

        videoSourceRatioProperty.addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                fitImageViewSize((float) playerHolder.getWidth(), (float) playerHolder.getHeight());
            }
        });
    }

    private synchronized void initWritableImage() {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        writableImage = new WritableImage((int) visualBounds.getWidth(), (int) visualBounds.getHeight());
        writableImageReference.set(writableImage);
        pixelWriterReference.set(writableImageReference.get().getPixelWriter());
    }

    private void fitImageViewSize(final float width, final float height) {
        Platform.runLater(new Runnable() {
            public void run() {
                float fitHeight = videoSourceRatioProperty.get() * width;
                if (fitHeight > height) {
                    imageView.setFitHeight(height);
                    double fitWidth = height / videoSourceRatioProperty.get();
                    imageView.setFitWidth(fitWidth);
                    imageView.setX((width - fitWidth) / 2);
                    imageView.setY(0);
                } else {
                    imageView.setFitWidth(width);
                    imageView.setFitHeight(fitHeight);
                    imageView.setY((height - fitHeight) / 2);
                    imageView.setX(0);
                }
            }
        });
    }

    public void play(String mediaLocation) {
        this.setVisible(true);
        getPlayer().prepareMedia(mediaLocation);
        getPlayer().start();

        bringToFront();


//        final JFrame frame = this;
//        //FIXME: Remove this.
//        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
//
//            public void run() {
//
//                frame.setVisible(false);
//
//            }
//        }, 60, 60, TimeUnit.SECONDS);
    }

    public void setBufferingValue(float bufferringValue) {
        super.setBufferingValue(bufferringValue);
    }

    private void bringToFront() {
//        getVideoPanel().removeAll();
//        getVideoPanel().add(fxPanel, BorderLayout.CENTER);
        getVideoPanel().requestFocus();
        setFullScreen();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        getFooterPanel().requestFocus();
        reinitAutoHideLater();
    }

    private void setFullScreen() {
        Window window;
        if (this instanceof RootPaneContainer) {
            window = this;
        } else {
            window = getOwner();
            while(window != null && !(window instanceof RootPaneContainer)) {
                window = window.getOwner();
            }
        }
        requestOSXFullscreen(window);
    }

    public static void enableOSXFullscreen(Window window) {
        try {
            Class util = Class.forName("com.apple.eawt.FullScreenUtilities");
            Class params[] = new Class[]{Window.class, Boolean.TYPE};
            Method method = util.getMethod("setWindowCanFullScreen", params);
            method.invoke(util, window, true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void requestOSXFullscreen(Window window) {
        try {
            enableOSXFullscreen(window);
            Class appClass = Class.forName("com.apple.eawt.Application");
            Class params[] = new Class[]{};

            Method getApplication = appClass.getMethod("getApplication", params);
            Object application = getApplication.invoke(appClass);
            Method requestToggleFulLScreen = application.getClass().getMethod("requestToggleFullScreen", Window.class);

            requestToggleFulLScreen.invoke(application, window);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private class CanvasPlayerComponent extends DirectMediaPlayerComponent {

        public CanvasPlayerComponent() {
            super(new CanvasBufferFormatCallback());
        }

        private PixelWriter getPW() {
            if (pixelWriterReference.get() == null) {
                pixelWriterReference.set(writableImageReference.get().getPixelWriter());
            }
            return pixelWriterReference.get();
        }

        @Override
        public void display(final DirectMediaPlayer mediaPlayer, Memory[] nativeBuffers, final BufferFormat bufferFormat) {
            if (writableImage == null) {
                return;
            }
            Platform.runLater(new Runnable() {
                public void run() {
                    if(mediaPlayer.isPlaying()) {
                        Memory nativeBuffer = mediaPlayer.lock()[0];
                        try {
                            ByteBuffer byteBuffer = nativeBuffer.getByteBuffer(0, nativeBuffer.size());
                            try {
                                getPW().setPixels(0, 0, bufferFormat.getWidth(), bufferFormat.getHeight(), pixelFormat, byteBuffer, bufferFormat.getPitches()[0]);
                            } catch (BufferOverflowException e) {
                                LOG.error(e.getMessage(), e);
                                reInitWritableImage();
                            }
                        } finally {
                            mediaPlayer.unlock();
                        }
                    }
                }
            });
        }
    }

    private synchronized void reInitWritableImage() {
        initWritableImage();
    }

    private class CanvasBufferFormatCallback implements BufferFormatCallback {

        public BufferFormat getBufferFormat(final int sourceWidth, final int sourceHeight) {
            Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
            Platform.runLater(new Runnable() {
                public void run() {
                    videoSourceRatioProperty.set((float) sourceHeight / (float) sourceWidth);
                }
            });
            return new RV32BufferFormat((int) visualBounds.getWidth(), (int) visualBounds.getHeight());
        }
    }

    private void handleUserAttentionRequest() {

        LOG.debug("User attention requested.");
        if (!headerPanel.isVisible()) {
            LOG.debug("Showing header panel");
            headerPanel.setVisible(true);
        }
        if(!getFooterPanel().isVisible()) {
            LOG.debug("Showing footer panel");
            getFooterPanel().setVisible(true);
        }
        reinitAutoHideLater();
    }

    protected void reinitAutoHideLater() {
        cancelAutoHideLaterTask();
        autoHideLater();
    }

    private void cancelAutoHideLaterTask() {
        if(future != null && !future.isCancelled()) {
            synchronized (future) {
                future.cancel(true);
            }
        }
    }

    private void autoHideLater() {
        future = scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

            public void run() {
                if(!getSeekbar().getValueIsAdjusting()
                        && !basicPlayerControlPanel.getVolumeSlider().getValueIsAdjusting()) {
                    LOG.debug("Autohiding... at " + Calendar.getInstance().getTime());
                    headerPanel.setVisible(false);
                    getFooterPanel().setVisible(false);
                }

            }
        }, AUTO_HIDE_HEADER_FOOTER_TIME, AUTO_HIDE_HEADER_FOOTER_TIME, TimeUnit.SECONDS);

    }

    public void showSliderAndHideLater() {
        headerPanel.setVisible(true);
        getFooterPanel().setVisible(true);
        reinitAutoHideLater();
    }

    public MediaPlayer getPlayer() {
        return mediaPlayerComponent.getMediaPlayer();
    }
}
