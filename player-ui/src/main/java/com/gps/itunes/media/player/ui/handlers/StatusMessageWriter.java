/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gps.itunes.media.player.ui.handlers;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import org.apache.log4j.spi.LoggingEvent;

/**
 *
 * Writes the status messages to the status JTextArea.
 * This is configured in such a way that only the log.info(message)'s messages are written.
 * 
 * @author leogps
 */
public class StatusMessageWriter extends org.apache.log4j.WriterAppender{
    
    private static JTextArea statusLabel;
    
    /**
     * 
     * Set the statusLabel to which the status messages needs to be written to.
     * 
     * @param statusLabel 
     */
    public static void setStatusLabel(final JTextArea statusLabel){
        StatusMessageWriter.statusLabel = statusLabel;
    }
    
    @Override
    public void append(final LoggingEvent loggingEvent){
        
        final String message;
        if(loggingEvent.getMessage() == null) {
            message = "(null)";
        } else {
            message = loggingEvent.getMessage().toString();
        }
        
        SwingUtilities.invokeLater(new Runnable(){
            
            @Override
            public void run(){
                if(statusLabel != null && 
                        loggingEvent.getLevel() == org.apache.log4j.Level.INFO){
                    statusLabel.setText(message);
                }
            }
            
        });
        
        
        
    }
    
}
