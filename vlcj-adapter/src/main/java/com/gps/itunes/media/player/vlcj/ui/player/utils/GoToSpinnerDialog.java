package com.gps.itunes.media.player.vlcj.ui.player.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by leogps on 9/21/15.
 */
public class GoToSpinnerDialog extends JDialog {

    private static final int MAX_MINUTE = 59;
    private static final int MAX_SECOND = 59;

    public GoToSpinnerDialog(TrackTime trackLimitTime, TrackTime initTime,
                              final GotoValueSubmissionEventListener gotoValueSubmissionEventListener) {
        setTitle("Go To");
        setModal(true);
        setModalityType(ModalityType.APPLICATION_MODAL);

        this.setLayout(new BorderLayout());


        final JSpinner hourSpinner = new JSpinner();
        SpinnerModel hourSpinnerModel = new SpinnerNumberModel(initTime.getHour(), 0, trackLimitTime.getHour(), 1);
        hourSpinner.setModel(hourSpinnerModel);
        JSpinner.NumberEditor hourNumberEditor = new JSpinner.NumberEditor(hourSpinner);
        hourSpinner.setEditor(hourNumberEditor);

        final JSpinner minSpinner = new JSpinner();
        SpinnerModel minSpinnerModel;
        if(trackLimitTime.getHour() > 0) {
            minSpinnerModel = new SpinnerNumberModel(initTime.getMin(), 0, MAX_MINUTE, 1);
        } else {
            minSpinnerModel = new SpinnerNumberModel(initTime.getMin(), 0, trackLimitTime.getMin(), 1);
        }
        minSpinner.setModel(minSpinnerModel);
        JSpinner.NumberEditor minNumberEditor = new JSpinner.NumberEditor(minSpinner, "00");
        minSpinner.setEditor(minNumberEditor);

        final JSpinner secSpinner = new JSpinner();
        SpinnerModel secSpinnerModel;
        if(trackLimitTime.getMin() > 0) {
            secSpinnerModel = new SpinnerNumberModel(initTime.getSec(), 0, MAX_SECOND, 1);
        } else {
            secSpinnerModel = new SpinnerNumberModel(initTime.getSec(), 0, trackLimitTime.getSec(), 1);
        }
        secSpinner.setModel(secSpinnerModel);
        JSpinner.NumberEditor secNumberEditor = new JSpinner.NumberEditor(secSpinner, "00");
        secSpinner.setEditor(secNumberEditor);

        JPanel spinnerPanel = new JPanel();
        JLabel colonLabel1 = new JLabel(":");
        JLabel colonLabel2 = new JLabel(":");
        spinnerPanel.setLayout(new FlowLayout());
        spinnerPanel.add(hourSpinner);
        spinnerPanel.add(colonLabel1);
        spinnerPanel.add(minSpinner);
        spinnerPanel.add(colonLabel2);
        spinnerPanel.add(secSpinner);

        add(spinnerPanel, BorderLayout.CENTER);

        JButton button = new JButton("OK");

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {

                int hour = (Integer) hourSpinner.getValue();
                int min = (Integer) minSpinner.getValue();
                int sec = (Integer) secSpinner.getValue();

                TrackTime value = new TrackTime(hour, min, sec);

                gotoValueSubmissionEventListener.onSubmit(value);

                dispose();

            }
        });
        add(button, BorderLayout.SOUTH);
        pack();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public static void main(String[] args) {

        TrackTime trackLimit = new TrackTime(1, 20, 30);
        TrackTime initTrackTime = new TrackTime(0, 05, 01);

        new GoToSpinnerDialog(trackLimit, initTrackTime, new GotoValueSubmissionEventListener() {
            public void onSubmit(TrackTime value) {
                System.out.println(value);
            }
        });
    }
}