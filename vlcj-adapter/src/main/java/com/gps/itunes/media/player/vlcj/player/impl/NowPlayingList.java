/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gps.itunes.media.player.vlcj.player.impl;

import com.gps.itunes.media.player.vlcj.ui.player.NowPlayingListData;
import com.gps.itunes.media.player.vlcj.ui.player.NowPlayingListFrame;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author leogps
 */
public class NowPlayingList extends ArrayList<NowPlayingListData> {
    
    private final NowPlayingListFrame frame;
    
    public NowPlayingList() {
        frame = new NowPlayingListFrame();
    }
    
    public void setCurrentPlayingTrack(final int index) {
        frame.setCurrentPlayingTrack(index);
    }
    
    public void showNowPlayingList() {
        frame.setVisible(true);
    }
    
    
    public ListIterator<NowPlayingListData> listIterator() {
        final ListIterator it = super.listIterator();
        final List<NowPlayingListData> list = this;
        final ListIterator retIterator = new ListIterator() {
            
            
            public boolean hasNext() {
                return it.hasNext();
            }
            
            
            public Object next() {
                return it.next();
            }
            
            
            public boolean hasPrevious() {
                return it.hasPrevious();
            }
            
            
            public Object previous() {
                return it.previous();
            }
            
            
            public int nextIndex() {
                return it.nextIndex();
            }
            
            
            public int previousIndex() {
                return it.previousIndex();
            }
            
            
            public void remove() {
                frame.populate(list);
                it.remove();
            }
            
            
            public void set(Object e) {
                it.set(e);
            }
            
            public void add(Object e) {
                it.add(e);
                frame.populate(list);
            }
        };
        return retIterator;
    }

    public boolean isNowPlaylingListFrameVisible() {
        return frame.isVisible();
    }
}
