/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gps.itunes.media.player.vlcj.ui.player;

import com.gps.imp.utils.Constants;
import com.gps.itunes.media.player.vlcj.ui.player.events.NowPlayingListTrackSelectedEventListener;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author leogps
 */
public class NowPlayingListFrame extends javax.swing.JFrame {

    private DefaultTableModel tableModel = new NowPlayingListTableModel();

    private List<NowPlayingListTrackSelectedEventListener> nowPlayingListTrackSelectedEventListenerList = new ArrayList<NowPlayingListTrackSelectedEventListener>();

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
    }

    private void addEventListeners() {
        nowPlayingList.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                if(SwingUtilities.isLeftMouseButton(me)) {
                    Point p = me.getPoint();
                    int row = nowPlayingList.rowAtPoint(p);
                    if (me.getClickCount() == 2) {
                        NowPlayingListData nowPlayingListData = (NowPlayingListData) tableModel.getValueAt(row, 1);
                        for (NowPlayingListTrackSelectedEventListener nowPlayingListTrackSelectedEventListener :
                                nowPlayingListTrackSelectedEventListenerList) {
                            nowPlayingListTrackSelectedEventListener.onNowPlayingListTrackSelectedEvent(nowPlayingListData);
                        }
                    }
                }
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(NowPlayingListFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NowPlayingListFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NowPlayingListFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NowPlayingListFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                new NowPlayingListFrame().setVisible(true);
            }
        });
    }

    private javax.swing.JScrollPane scrollPane;
    private JTable nowPlayingList;


    public JTable getNowPlayingList() {
        return nowPlayingList;
    }
    
    public void add(NowPlayingListData nowPlayingListData) {
        tableModel.addRow(new Object[]{
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
}
