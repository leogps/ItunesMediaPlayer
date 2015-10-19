/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gps.itunes.media.player.vlcj.ui.player;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author leogps
 */
public class NowPlayingListTableModel extends DefaultTableModel {

    public NowPlayingListTableModel() {
        this.setRowCount(0);

        NowPlayingListTableColumn[] columns = NowPlayingListTableColumn.asArray();
        this.setColumnIdentifiers(columns);
        this.setColumnCount(columns.length);
    }

    public void setWidthsForTable(JTable table) {
        NowPlayingListTableColumn[] columns = NowPlayingListTableColumn.asArray();
        for(NowPlayingListTableColumn column : columns) {
            TableColumn tableColumn = table.getColumn(column.getName());
            tableColumn.setPreferredWidth(column.getWidth());
        }
    }

    @Override
    public boolean isCellEditable(int i, int i1) {
        return false;
    }
}
