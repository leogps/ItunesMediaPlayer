package com.gps.itunes.media.player.vlcj.ui.player;

import java.util.EnumSet;
import java.util.Set;

/**
 * Represents the Column for Now Playing List Table.
 *
 * Created by leogps on 10/18/15.
 */
public enum NowPlayingListTableColumn {
    STATUS("Status", 5),
    NAME("Name", 25),
    ARTIST("Artist", 20),
    ALBUM("Album", 20);

    private final String name;
    private final int width;

    NowPlayingListTableColumn(String name, int width) {
        this.name = name;
        this.width = width;
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    /**
     *
     * Returns the name associated with the column.
     *
     * @return
     */
    @Override
    public String toString(){
        return getName();
    }

    public static NowPlayingListTableColumn[] asArray() {
        Set<NowPlayingListTableColumn> enumSet = EnumSet.allOf(NowPlayingListTableColumn.class);
        NowPlayingListTableColumn[] columns = new NowPlayingListTableColumn[enumSet.size()];
        int index = -1;
        for(NowPlayingListTableColumn column : enumSet) {
            columns[++index] = column;
        }
        return columns;
    }
}
