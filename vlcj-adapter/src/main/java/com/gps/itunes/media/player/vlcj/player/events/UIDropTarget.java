package com.gps.itunes.media.player.vlcj.player.events;

import org.apache.log4j.Logger;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;

/**
 * Created by leogps on 6/12/17.
 */
public abstract class UIDropTarget extends DropTarget {

    private static Logger LOG = Logger.getLogger(UIDropTarget.class);

    @Override
    public synchronized void drop(DropTargetDropEvent dropTargetDropEvent) {
        try {
            Transferable tr = dropTargetDropEvent.getTransferable();
            DataFlavor[] flavors = tr.getTransferDataFlavors();
            for (int i = 0; i < flavors.length; i++) {
                LOG.debug("Possible flavor: " + flavors[i].getMimeType());
                if (flavors[i].isFlavorJavaFileListType()) {
                    dropTargetDropEvent.acceptDrop(DnDConstants.ACTION_COPY);
                    LOG.debug("Successful file list drop.");

                    java.util.List<File> list = (java.util.List<File>) tr.getTransferData(flavors[i]);
                    for (int j = 0; j < list.size(); j++) {
                        LOG.debug(list.get(j) + "\n");
                    }
                    dropTargetDropEvent.dropComplete(true);
                    onFilesDroppedEvent(list, dropTargetDropEvent);
                    return;
                }
            }
            LOG.debug("Drop failed: " + dropTargetDropEvent);
            dropTargetDropEvent.rejectDrop();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            dropTargetDropEvent.rejectDrop();
        }
    }

    /**
     * On Files DroppedEvent
     */
    public abstract void onFilesDroppedEvent(List<File> fileList, DropTargetDropEvent dropTargetDropEvent);
}
