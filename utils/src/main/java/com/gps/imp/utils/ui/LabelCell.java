package com.gps.imp.utils.ui;

import com.gps.imp.utils.Constants;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * This displays label inside the table cell to display an icons.
 */
public class LabelCell extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {

    private final JLabel label;

    public LabelCell(){
        label = new JLabel(Constants.EMPTY);
    }


    public Object getCellEditorValue() {
        return null;
    }


    public Component getTableCellRendererComponent(JTable table,
                                                   Object value, boolean isSelected, boolean hasFocus, int row,
                                                   int column) {
        renderLabel(value);
        return label;
    }

    private void renderLabel(Object value) {
        if(value instanceof Icon){
            label.setIcon((Icon) value);
        } else {
            label.setIcon(null);
            label.setVisible(false);
        }
    }


    public Component getTableCellEditorComponent(JTable table,
                                                 Object value, boolean isSelected, int row, int column) {
        renderLabel(value);
        return label;
    }

}
