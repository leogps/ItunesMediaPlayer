//package com.gps.itunes.media.player.vlcj.player.impl;
//
//import javafx.application.Application;
//import javafx.embed.swing.JFXPanel;
//import javafx.scene.Scene;
//import javafx.scene.layout.BorderPane;
//import javafx.stage.Stage;
//
//import javax.swing.*;
//import java.awt.*;
//
///**
// * @author leogps
// * Created on 5/29/21
// */
//public class JFXPlayerWrapper extends Application {
//
//    private static JFXPanel javafxPanel;
//    
//    private final FXPlayerFrameImpl frame;
//
//    public JFXPlayerWrapper(FXPlayerFrameImpl fxPlayerFrame) {
//        this.frame = fxPlayerFrame;
//
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                initAndShowGUI();
//            }
//        });
//    }
//
//    public void initAndShowGUI() {
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//        // Create JavaFX panel.
//        javafxPanel = new JFXPanel();
//        frame.getContentPane().add(javafxPanel, BorderLayout.CENTER);
//
//        // Create JavaFX scene.
////        launch();
//        //Application.launch (JFXPlayerWrapper.class, null);
//
//        // Show frame.
//        frame.pack();
//        frame.setVisible(true);
//    }
//
//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        // Add scene to panel
//        javafxPanel.setScene(createScene());
//    }
//
//    private static Scene createScene() {
//        BorderPane pane = new BorderPane();
//        Scene scene = new Scene(pane);
//        return scene;
//    }
//}
