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
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
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
import java.nio.ByteBuffer;

/**
 * Created by leogps on 10/1/15.
 */
public class FXPlayerFrameImpl extends VideoPlayerFrame implements FXPlayerFrame {

    private static Logger LOG = Logger.getLogger(FXPlayerFrameImpl.class);

    private ImageView imageView;

    private DirectMediaPlayerComponent mediaPlayerComponent;

    private WritableImage writableImage;

    private Pane playerHolder;

    private WritablePixelFormat<ByteBuffer> pixelFormat;

    private FloatProperty videoSourceRatioProperty;

    private JFXPanel fxPanel;

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
    }

    private Scene createScene() {
        mediaPlayerComponent = new CanvasPlayerComponent();
        playerHolder = new Pane();
        videoSourceRatioProperty = new SimpleFloatProperty(0.4f);
        pixelFormat = PixelFormat.getByteBgraPreInstance();
        initializeImageView();

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(playerHolder);

        Scene scene = new Scene(borderPane);
        return scene;
    }

    private void initializeImageView() {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        writableImage = new WritableImage((int) visualBounds.getWidth(), (int) visualBounds.getHeight());
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
        //reinitAutoHideLater();
    }

    private void setFullScreen() {
        //TODO: Use in fullscreenFXPlayerFrame
//        GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
//        graphicsDevice.setFullScreenWindow(this);
    }

    private class CanvasPlayerComponent extends DirectMediaPlayerComponent {

        public CanvasPlayerComponent() {
            super(new CanvasBufferFormatCallback());
        }

        PixelWriter pixelWriter = null;

        private PixelWriter getPW() {
            if (pixelWriter == null) {
                pixelWriter = writableImage.getPixelWriter();
            }
            return pixelWriter;
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
                            getPW().setPixels(0, 0, bufferFormat.getWidth(), bufferFormat.getHeight(), pixelFormat, byteBuffer, bufferFormat.getPitches()[0]);
                        } finally {
                            mediaPlayer.unlock();
                        }
                    }
                }
            });
        }
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

    public MediaPlayer getPlayer() {
        return mediaPlayerComponent.getMediaPlayer();
    }
}
