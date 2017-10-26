/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gps.itunes.media.player.vlcj.ui.player;

import com.gps.imp.utils.Constants;
import com.gps.imp.utils.JavaVersionUtils;
import com.gps.imp.utils.ui.LabelCell;
import com.gps.itunes.media.player.vlcj.player.impl.ItunesMediaPlayerImpl;
import com.gps.itunes.media.player.vlcj.ui.player.events.NowPlayingListTrackSelectedEventListener;

import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author leogps
 */
public class NowPlayingListFrame extends javax.swing.JFrame {

    private DefaultTableModel tableModel = new NowPlayingListTableModel();

    private List<NowPlayingListTrackSelectedEventListener> nowPlayingListTrackSelectedEventListenerList = new ArrayList<NowPlayingListTrackSelectedEventListener>();

    private static final int NOW_PLAYING_LIST_STATUS_CELL_INDEX = 1;
    private static final int NOW_PLAYING_LIST_DATA_CELL_INDEX = 2;

    private final ImageIcon currentlyPlayingIcon = new ImageIcon(ItunesMediaPlayerImpl.class.getClassLoader().getResource("images/play_20x20.png")) {
        @Override
        public String toString() {
            return "Playing";
        }
    };

    private final ImageIcon currentlyPausedIcon = new ImageIcon(ItunesMediaPlayerImpl.class.getClassLoader().getResource("images/pause_20x20.png")) {
        @Override
        public String toString() {
            return "Paused";
        }
    };

    /**
     * Creates new form NowPlayingListForm
     */
    public NowPlayingListFrame() {
        initComponents();
        setTitle("Now Playing List");
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);

        ((NowPlayingListTableModel) tableModel).setWidthsForTable(getNowPlayingList());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    private void initComponents() {

        setResizable(true);

        nowPlayingList = new JTable();
        nowPlayingList.setModel(tableModel);
        nowPlayingList.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        nowPlayingList.setGridColor(new java.awt.Color(153, 153, 153));

        scrollPane = new JScrollPane(nowPlayingList);

        Container contentPane = getContentPane();
        contentPane.setLayout(new GridLayout(1,1));
        contentPane.add(scrollPane, BorderLayout.CENTER);

        addEventListeners();
        pack();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TableColumn statusColumn = nowPlayingList.getColumnModel().getColumn(NOW_PLAYING_LIST_STATUS_CELL_INDEX);
                statusColumn.setCellEditor(new LabelCell());
                statusColumn.setCellRenderer(new LabelCell());
            }
        });
    }

    private void addEventListeners() {
        nowPlayingList.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                if(SwingUtilities.isLeftMouseButton(me)) {
                    Point p = me.getPoint();
                    int row = nowPlayingList.rowAtPoint(p);
                    if (me.getClickCount() == 2) {
                        NowPlayingListData nowPlayingListData = (NowPlayingListData) tableModel.getValueAt(row, NOW_PLAYING_LIST_DATA_CELL_INDEX);
                        for (NowPlayingListTrackSelectedEventListener nowPlayingListTrackSelectedEventListener :
                                nowPlayingListTrackSelectedEventListenerList) {
                            nowPlayingListTrackSelectedEventListener.onNowPlayingListTrackSelectedEvent(nowPlayingListData);
                        }
                    }
                }
            }
        });
        if (!JavaVersionUtils.isGreaterThan6()) {
            // In Java 6 for Mac on > El Capitan, the scroll event does not repaint the table contents correctly.
            scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
                @Override
                public void adjustmentValueChanged(AdjustmentEvent adjustmentEvent) {
                    if (!adjustmentEvent.getValueIsAdjusting()) {
                        nowPlayingList.repaint();
                    }
                }
            });
        }
    }

    private javax.swing.JScrollPane scrollPane;
    private JTable nowPlayingList;


    public JTable getNowPlayingList() {
        return nowPlayingList;
    }
    
    public void add(NowPlayingListData nowPlayingListData) {
        tableModel.addRow(new Object[]{
                Integer.toString(tableModel.getRowCount() + 1),
                Constants.EMPTY,
                nowPlayingListData,
                nowPlayingListData.getArtist(),
                nowPlayingListData.getAlbum()});
    }

    private void clearTable() {
        tableModel.setRowCount(0);
    }

    public void clear() {
        clearTable();
    }

    public void addNowPlayingListTrackSelectedEventListener(NowPlayingListTrackSelectedEventListener nowPlayingListTrackSelectedEventListener) {
        this.nowPlayingListTrackSelectedEventListenerList.add(nowPlayingListTrackSelectedEventListener);
    }

    public void updateCellStatus(final boolean isPlaying, final long trackId) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                for (int i = 0; i < tableModel.getDataVector().size(); i++) {
                    Vector objectVector = (Vector) tableModel.getDataVector().get(i);
                    NowPlayingListData nowPlayingListData = (NowPlayingListData) objectVector.get(NOW_PLAYING_LIST_DATA_CELL_INDEX);
                    if (nowPlayingListData.getTrackId() == trackId) {
                        objectVector.set(NOW_PLAYING_LIST_STATUS_CELL_INDEX, (isPlaying) ? currentlyPlayingIcon : currentlyPausedIcon);
                    } else {
                        objectVector.set(NOW_PLAYING_LIST_STATUS_CELL_INDEX, Constants.EMPTY);
                    }
                    tableModel.fireTableCellUpdated(i, NOW_PLAYING_LIST_STATUS_CELL_INDEX); // first column, every row
                }
            }
        });
    }
}
