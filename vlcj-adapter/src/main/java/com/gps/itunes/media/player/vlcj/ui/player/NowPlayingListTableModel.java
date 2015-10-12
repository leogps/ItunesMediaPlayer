/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gps.itunes.media.player.vlcj.ui.player;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author leogps
 */
public class NowPlayingListTableModel extends DefaultTableModel {

    private static final String[] COLUMNS = {"Name", "Artist", "Album"};

    public NowPlayingListTableModel() {
        this.setRowCount(0);
        this.setColumnIdentifiers(COLUMNS);
        this.setColumnCount(COLUMNS.length);
    }

    @Override
    public boolean isCellEditable(int i, int i1) {
        return false;
    }
    
    
}
