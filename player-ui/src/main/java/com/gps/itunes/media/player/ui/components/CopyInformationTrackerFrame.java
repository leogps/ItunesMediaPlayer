package com.gps.itunes.media.player.ui.components;

import com.gps.itunes.lib.tasks.ProgressInformer;
import com.gps.itunes.lib.tasks.progressinfo.CopyTrackFailureInformation;
import com.gps.itunes.lib.tasks.progressinfo.CopyTrackInformation;
import com.gps.itunes.lib.tasks.progressinfo.ProgressInformation;
import com.gps.itunes.lib.tasks.progressinfo.ProgressTracker;
import com.gps.itunes.media.player.ui.exceptions.TaskExecutionException;
import com.gps.itunes.media.player.ui.tasks.PlaylistCopier;
import com.gps.itunes.media.player.ui.tasks.TaskParams;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Created by leogps on 3/27/15.
 */
public class CopyInformationTrackerFrame extends JFrame {
    private JTable copyInformationTable;
    private DefaultTableModel copyInformationTableModel;
    private final PlaylistCopier playlistCopier;
    private final ProgressTracker progressTracker;
    private JScrollPane jScrollPane1;
    private JPanel jPanel;

    private static final String SUCCESS = "success";
    private static final String FAILURE = "failure";

    private static final Logger LOGGER = Logger
            .getLogger(CopyInformationTrackerFrame.class);

    public CopyInformationTrackerFrame(PlaylistCopier playlistCopier) {
        this.progressTracker = new ProgressTracker(new CopyProgressInformer(), new CopyProgressInformation());


        playlistCopier.addProgressTracker(progressTracker);
        this.playlistCopier = playlistCopier;
    }

    public void begin(final TaskParams taskParams) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    playlistCopier.submitTask(taskParams);
                } catch (TaskExecutionException e) {
                    LOGGER.error(e);
                }
            }
        });
    }

    private void createUIComponents() {
        jPanel = new JPanel();
        jScrollPane1 = new JScrollPane();

        this.copyInformationTable = new JTable() {

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component returnComp = super.prepareRenderer(renderer, row, column);

                Object obj = copyInformationTableModel.getValueAt(row, 1);
                boolean failedRow = false;
                if (obj instanceof String) {
                    String status = (String) obj;
                    failedRow = FAILURE.equals(status);
                }

                if (!returnComp.getBackground().equals(getSelectionBackground())) {
                    returnComp.setBackground(failedRow ? Color.RED : Color.WHITE);
                }

                JComponent jcomp = (JComponent) returnComp;
                if (returnComp == jcomp) {
                    String tooltipText = String.valueOf(getModel().getValueAt(row, column));
                    if (tooltipText.equals("null")) {
                        tooltipText = "";
                    }
                    jcomp.setToolTipText(tooltipText);
                }

                return returnComp;
            }
        };
        this.copyInformationTableModel = new CopyInformationTableModel();
        this.copyInformationTable.setModel(copyInformationTableModel);

        jPanel.add(jScrollPane1);
        jScrollPane1.setViewportView(copyInformationTable);

        add(jPanel);

        setTitle("Playlist Copy");
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        pack();
        setVisible(true);
    }

    private class CopyProgressInformer<CopyProgressInformation> implements ProgressInformer<ProgressInformation<CopyTrackInformation>> {

        @Override
        public void informProgress(final ProgressInformation<CopyTrackInformation> t) {
            final CopyTrackInformation info = t.getInformation();

            if (info instanceof CopyTrackFailureInformation) {
                CopyTrackFailureInformation failureInformation = (CopyTrackFailureInformation) info;

                copyInformationTableModel.addRow(new Object[]{
                        failureInformation.getCurrentTrack(),
                        FAILURE,
                        failureInformation.getToDest(),
                        failureInformation.getFailureMessage()
                });

            } else {
                copyInformationTableModel.addRow(new Object[]{
                        info.getCurrentTrack(),
                        SUCCESS,
                        info.getToDest(),
                        String.format("Processed: %s tracks.", info.getCurrentTrackNo() + 1)
                });
            }
        }
    }

    private class CopyProgressInformation implements ProgressInformation<CopyTrackInformation> {

        private CopyTrackInformation info = new CopyTrackInformation(-1, 100, 0, "", "");

        @Override
        public void setInformation(CopyTrackInformation info) {
            this.info = info;
        }

        @Override
        public CopyTrackInformation getInformation() {
            return info;
        }
    }

    private class CopyInformationTableModel extends DefaultTableModel {

        private final String[] COLUMNS = {"Track Name", "Status", "Destination", "Message"};

        CopyInformationTableModel() {
            this.setRowCount(0);
            this.setColumnIdentifiers(COLUMNS);
            this.setColumnCount(COLUMNS.length);
        }

        @Override
        public boolean isCellEditable(int i, int i1) {
            return false;
        }
    }

}
