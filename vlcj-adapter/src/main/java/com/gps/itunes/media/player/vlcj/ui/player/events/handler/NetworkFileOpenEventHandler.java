package com.gps.itunes.media.player.vlcj.ui.player.events.handler;

import com.gps.imp.utils.ui.NetworkFileOpenDialog;
import com.gps.imp.utils.ui.NetworkFileOpenEventListener;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by leogps on 11/27/14.
 */
public class NetworkFileOpenEventHandler implements NetworkFileOpenEventListener {

    private static Logger LOG = Logger.getLogger(NetworkFileOpenEventHandler.class);

    private static Dimension dialogDimensions = new Dimension(630, 240);

    private AtomicBoolean cancelled = new AtomicBoolean(false);
    private AtomicReference<String> inputValueRef = new AtomicReference<String>();

    public String handle() {
        LOG.debug("Handling NetworkFileOpenEvent...");
        NetworkFileOpenDialog dialog = new NetworkFileOpenDialog(this);
        dialog.setPreferredSize(dialogDimensions);
        dialog.setMaximumSize(dialogDimensions);
        dialog.pack();
        dialog.setVisible(true);
        if (cancelled.get()) {
            LOG.debug("Cancelled.");
            return null;
        }
        LOG.debug("NetworkFile: " + inputValueRef.get());
        return inputValueRef.get();
    }

    @Override
    public void onOk(String inputValue) {
        inputValueRef.set(inputValue);
    }

    @Override
    public void onCancel() {
        cancelled.set(true);
    }
}
