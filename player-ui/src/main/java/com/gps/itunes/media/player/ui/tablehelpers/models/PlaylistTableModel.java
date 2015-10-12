/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gps.itunes.media.player.ui.tablehelpers.models;

import javax.swing.table.DefaultTableModel;


/**
 * TableModel for PlaylistTable.
 *
 * @author leogps
 */
public class PlaylistTableModel extends DefaultTableModel {
    
    private static org.apache.log4j.Logger log = 
            org.apache.log4j.Logger.getLogger(PlaylistTableModel.class);
    

    public PlaylistTableModel() {
        this.setColumnCount(columns.length);
        this.setRowCount(0);
        this.setColumnIdentifiers(columns);    
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit[columnIndex];
    }
    
    
    private final boolean[] canEdit = new boolean[]{
        false
    };
    
    private final String[] columns = {"Playlists"};


    public void clear() {
        while(getRowCount() > 0) {
            removeRow(0);
        }
    }
}
