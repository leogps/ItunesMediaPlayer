/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gps.itunes.media.player.ui.tablehelpers.models;

import com.gps.itunes.media.player.ui.tablehelpers.columns.TrackTableColumns;
import com.gps.itunes.media.player.ui.tablehelpers.comparators.RowComparators;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.util.EnumSet;

/**
 *
 * TableModel for TracksTable.
 * 
 * @author leogps
 */
public class TracksTableModel extends DefaultTableModel {
    
    private static final int HOLDER_INDEX = 1;

    public TracksTableModel() {
        this.setRowCount(0);
        this.setColumnIdentifiers(TrackTableColumns.COLUMNS);
        this.setColumnCount(TrackTableColumns.COLUMNS.length);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public static int getHOLDER_INDEX() {
        return HOLDER_INDEX;
    }

    /**
     * Clears Table's contents and re-attaches Row Sorters to fix sorting issue.
     * 
     * @param tracksTable 
     */
    public void clearTable(final JTable tracksTable) {
        setRowCount(0);
        fireTableStructureChanged();
        setTracksTableRowSorter(tracksTable);
        for(TrackTableColumns column : EnumSet.allOf(TrackTableColumns.class)) {
            TableColumn tableColumn = tracksTable.getColumn(column.getName());
            tableColumn.setPreferredWidth(column.getWidth());
        }
    }
    
    
    /**
     * 
     * Sets Row sorter for tracks table.
     * 
     * @param tracksTable 
     */
    public void setTracksTableRowSorter(final JTable tracksTable){
        final TableRowSorter tracksTableRowSorter = new TableRowSorter(this);

        // S_NO integer sorting.
        tracksTableRowSorter.setComparator(tracksTable.getColumn(TrackTableColumns.S_NO.getName()).getModelIndex(),
                RowComparators.getIntegerComparator());
        
        // Custom sorting for time column.
        tracksTableRowSorter.setComparator(tracksTable.getColumn(TrackTableColumns.Time.getName()).getModelIndex(),
                RowComparators.getTimeComparator());
        
        // Integer sorting for Rating.
        tracksTableRowSorter.setComparator(tracksTable.getColumn(TrackTableColumns.Rating.getName()).getModelIndex(),
                RowComparators.getIntegerComparatorNullAllowed());
        
        //Integer sorting for Plays column.
        tracksTableRowSorter.setComparator(tracksTable.getColumn(TrackTableColumns.Plays.getName()).getModelIndex(),
                RowComparators.getIntegerComparatorNullAllowed());
        
        tracksTable.setRowSorter(tracksTableRowSorter);
        
    }
}
