package com.gps.itunes.media.player.ui.splash;

import java.awt.*;
import java.util.concurrent.CountDownLatch;

/**
 * Created by leogps on 10/11/15.
 */
public class SplashAnimator {

    // TODO: Move to properties file?
    private static final int MAX_PROGRESS_WIDTH = 580;
    private static final int PROGRESS_HEIGHT = 7;
    private static final int PROGRESS_X_COORDINATE = 40;
    private static final int PROGRESS_Y_COORDINATE = 304;

    private final SplashScreen splash;
    private final Graphics2D graphics2D;

    /**
     *
     * @param progress: from 1 to 100.
     * @param message: Message to be shown on the splash screen.
     */
    public synchronized void renderSplashFrame(int progress, String message) {
        if(isSplashAvailable()) {
            Rectangle splashImageBounds = splash.getBounds();
            // Clearing previous text...
            graphics2D.setComposite(AlphaComposite.Clear);
            graphics2D.fillRect(0, 0, splashImageBounds.width, splashImageBounds.height);

            graphics2D.setPaintMode();
            graphics2D.setColor(Color.BLACK);

            int progressWidth = ((MAX_PROGRESS_WIDTH) * (progress + 1)) / 100;
            System.out.println("Progressbar width: " + progressWidth);

            graphics2D.fillRect(PROGRESS_X_COORDINATE, PROGRESS_Y_COORDINATE, progressWidth, PROGRESS_HEIGHT);
            graphics2D.drawString(message, 120, 150);
            update();
        }
    }

    public SplashAnimator() {
        splash = SplashScreen.getSplashScreen();
        if (splash == null) {
            System.out.println("SplashScreen.getSplashScreen() returned null");
            graphics2D = null;
            return;
        }
        graphics2D = splash.createGraphics();
        if (graphics2D == null) {
            System.out.println("graphics2D is null");
            return;
        }
    }

    public boolean isSplashAvailable() {
        return splash != null && splash.isVisible() && graphics2D != null;
    }

    public synchronized void update() {
        if(splash != null && splash.isVisible()) {
            synchronized (splash) {
                if(splash.isVisible()) {
                    splash.update();
                }
            }
        }
    }

    public synchronized void close() {
        if(splash != null && splash.isVisible()) {
            splash.close();
        }
    }

    public static void main (String args[]) throws InterruptedException {
        SplashAnimator splashAnimator = new SplashAnimator();

        String[] messages = new String[]{"Checking VLC Engine...", "Loading Properties...", "Registering Event Listeners", "Loading iTunes library file...",
                "Parsing iTunes library file", "Loading UI..."};

        if(splashAnimator.isSplashAvailable()) {
            for (int i = 0; i < 100; i++) {
                splashAnimator.renderSplashFrame(i, messages[(i / 5) % messages.length]);
                try {
                    Thread.sleep(90);
                } catch (InterruptedException e) {
                }
            }
        }

        new CountDownLatch(1).await();
    }
}
