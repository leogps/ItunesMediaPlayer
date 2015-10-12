///*
// * This file is part of VLCJ.
// *
// * VLCJ is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * VLCJ is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
// *
// * Copyright 2009, 2010, 2011, 2012, 2013, 2014, 2015 Caprica Software Limited.
// */
//
//package com.gps.itunes.media.player.vlcj.player;
//
//import com.gps.itunes.media.player.vlcj.ui.player.events.SeekEventListener;
//import com.gps.itunes.media.player.vlcj.ui.player.events.VideoPlayerKeyListener;
//import com.gps.itunes.media.player.vlcj.ui.player.events.VideoPlayerMouseAdapter;
//import com.gps.itunes.media.player.vlcj.ui.player.events.VideoPlayerMouseWheelListener;
//import com.sun.jna.Memory;
//import javafx.application.Application;
//import javafx.application.Platform;
//import javafx.beans.property.FloatProperty;
//import javafx.beans.property.SimpleFloatProperty;
//import javafx.beans.value.ChangeListener;
//import javafx.beans.value.ObservableValue;
//import javafx.event.EventHandler;
//import javafx.geometry.Rectangle2D;
//import javafx.scene.CacheHint;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.image.*;
//import javafx.scene.layout.BorderPane;
//import javafx.scene.layout.Pane;
//import javafx.stage.Screen;
//import javafx.stage.Stage;
//import javafx.stage.WindowEvent;
//import org.apache.log4j.Logger;
//import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
//import uk.co.caprica.vlcj.player.MediaPlayer;
//import uk.co.caprica.vlcj.player.direct.BufferFormat;
//import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
//import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
//import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;
//
//import java.nio.ByteBuffer;
//
///**
// * Example showing how to dynamically resize video.
// * <p>
// * Originally contributed by Vladislav Kisel, https://github.com/caprica/vlcj-javafx/pull/9,
// * incorporated with minor changes.
// * <p>
// * The idea is to first determine the maximum size available (i.e. the screen size) and request
// * that LibVLC send video frames in that size. We then scale *down* from the maximum size to fit
// * the current window size, without any change in the native video buffer format.
// * <p>
// * So LibVLC will always be sending video frames in the maximum possible size.
// * <p>
// * This is a reasonable compromise.
// * <p>
// * For comparison, to achieve dynamic resizing by having LibVLC send video frames at a constantly
// * changing size would require a constantly changing buffer format - and this would require you to
// * get the current play-back position, stop the media player, play the media, set the new buffer
// * format for the new size, and then restore the previous play-back position. Such an approach is
// * clearly problematic.
// */
//public class FXPlayer extends Application implements VLCJPlayer {
//
//    private static Logger LOG = Logger.getLogger(FXPlayer.class);
//
//    public static void main(String args[]) {
//        launch();
//    }
//
//    private ImageView imageView;
//
//    private DirectMediaPlayerComponent mediaPlayerComponent;
//
//    private WritableImage writableImage;
//
//    private Pane playerHolder;
//
//    private WritablePixelFormat<ByteBuffer> pixelFormat;
//
//    private FloatProperty videoSourceRatioProperty;
//
//    private Stage primaryStage;
//
//    @Override
//    public void start(Stage primaryStage) throws Exception {
//
//        this.primaryStage = primaryStage;
//
//        mediaPlayerComponent = new CanvasPlayerComponent();
//        playerHolder = new Pane();
//        videoSourceRatioProperty = new SimpleFloatProperty(0.4f);
//        pixelFormat = PixelFormat.getByteBgraPreInstance();
//        initializeImageView();
//
//        BorderPane borderPane = new BorderPane();
//        borderPane.setCenter(playerHolder);
//
//        Scene scene = new Scene(borderPane);
//        primaryStage.setScene(scene);
//
//        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//
//            public void handle(WindowEvent event) {
//                mediaPlayerComponent.getMediaPlayer().stop();
//                //Platform.exit();
////                System.exit(0);
//            }
//        });
//
//        Button button = new Button("Press me");
//        borderPane.setBottom(button);
//
//        primaryStage.show();
//
//    }
//
//    private void initializeImageView() {
//        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
//        writableImage = new WritableImage((int) visualBounds.getWidth(), (int) visualBounds.getHeight());
//
//        imageView = new ImageView(writableImage);
//        imageView.setCache(true);
//        imageView.setCacheHint(CacheHint.SPEED);
//        playerHolder.getChildren().add(imageView);
//
//        playerHolder.widthProperty().addListener(
//                new ChangeListener<Number>() {
//                    public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
//                        fitImageViewSize(newValue.floatValue(), (float) playerHolder.getHeight());
//                    }
//                });
//
//        playerHolder.heightProperty().addListener(new ChangeListener<Number>() {
//            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
//                fitImageViewSize((float) playerHolder.getWidth(), newValue.floatValue());
//            }
//        });
//
//        videoSourceRatioProperty.addListener(new ChangeListener<Number>() {
//            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
//                fitImageViewSize((float) playerHolder.getWidth(), (float) playerHolder.getHeight());
//            }
//        });
//    }
//
//    private void fitImageViewSize(final float width, final float height) {
//        Platform.runLater(new Runnable() {
//            public void run() {
//                float fitHeight = videoSourceRatioProperty.get() * width;
//                if (fitHeight > height) {
//                    imageView.setFitHeight(height);
//                    double fitWidth = height / videoSourceRatioProperty.get();
//                    imageView.setFitWidth(fitWidth);
//                    imageView.setX((width - fitWidth) / 2);
//                    imageView.setY(0);
//                } else {
//                    imageView.setFitWidth(width);
//                    imageView.setFitHeight(fitHeight);
//                    imageView.setY((height - fitHeight) / 2);
//                    imageView.setX(0);
//                }
//            }
//        });
//    }
//
//    public void play(String mediaLocation) {
//        primaryStage.show();
//        primaryStage.setFullScreen(true);
//
//        getPlayer().prepareMedia(mediaLocation);
//        getPlayer().start();
//    }
//
//    private class CanvasPlayerComponent extends DirectMediaPlayerComponent {
//
//        public CanvasPlayerComponent() {
//            super(new CanvasBufferFormatCallback());
//        }
//
//        PixelWriter pixelWriter = null;
//
//        private PixelWriter getPW() {
//            if (pixelWriter == null) {
//                pixelWriter = writableImage.getPixelWriter();
//            }
//            return pixelWriter;
//        }
//
//        @Override
//        public void display(final DirectMediaPlayer mediaPlayer, Memory[] nativeBuffers, final BufferFormat bufferFormat) {
//            if (writableImage == null) {
//                return;
//            }
//            Platform.runLater(new Runnable() {
//                public void run() {
//
//                    if(mediaPlayer.isPlaying()) {
//                        Memory nativeBuffer = mediaPlayer.lock()[0];
//                        try {
//                            ByteBuffer byteBuffer = nativeBuffer.getByteBuffer(0, nativeBuffer.size());
//                            getPW().setPixels(0, 0, bufferFormat.getWidth(), bufferFormat.getHeight(), pixelFormat, byteBuffer, bufferFormat.getPitches()[0]);
//                        } finally {
//                            mediaPlayer.unlock();
//                        }
//                    }
//                }
//            });
//        }
//    }
//
//    private class CanvasBufferFormatCallback implements BufferFormatCallback {
//
//        public BufferFormat getBufferFormat(final int sourceWidth, final int sourceHeight) {
//            Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
//            Platform.runLater(new Runnable() {
//                public void run() {
//                    videoSourceRatioProperty.set((float) sourceHeight / (float) sourceWidth);
//                }
//            });
//            return new RV32BufferFormat((int) visualBounds.getWidth(), (int) visualBounds.getHeight());
//        }
//    }
//
//    public void resetSeekbar() {
//
//    }
//
//    public void registerSeekEventListener(SeekEventListener seekEventListener) {
//
//    }
//
//    public void unRegisterSeekEventListener(SeekEventListener seekEventListener) {
//
//    }
//
//    public void updateSeekbar(SeekInfo seekInfo) {
//
//    }
//
//    public boolean isSeekValueAdjusting() {
//        return false;
//    }
//
//    public void attachCommandListener(VideoPlayerKeyListener videoPlayerKeyListener) {
//
//    }
//
//    public void attachCommandListener(VideoPlayerMouseAdapter videoPlayerMouseAdapter) {
//
//    }
//
//    public void attachCommandListener(VideoPlayerMouseWheelListener videoPlayerMouseWheelListener) {
//
//    }
//
//    public void setPaused() {
//
//    }
//
//    public MediaPlayer getPlayer() {
//        return mediaPlayerComponent.getMediaPlayer();
//    }
//
//}
