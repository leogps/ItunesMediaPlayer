/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gps.itunes.media.player.ui.handlers;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import java.io.Serializable;

import static org.apache.logging.log4j.Level.INFO;

/**
 *
 * Writes the status messages to the status JTextArea.
 * This is configured in such a way that only the log.info(message)'s messages are written.
 * 
 * @author leogps
 */
@Plugin(
        name = "StatusMessageAppender",
        category = Core.CATEGORY_NAME,
        elementType = Appender.ELEMENT_TYPE)
public class StatusMessageAppender extends AbstractAppender {
    
    private static JTextArea statusLabel;

    protected StatusMessageAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties) {
        super(name, filter, layout, ignoreExceptions, properties);
    }

    @SuppressWarnings("unused")
    @PluginFactory
    public static StatusMessageAppender createAppender(@PluginAttribute("name") String name,
                                                   @PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
                                                   @PluginElement("Layout") Layout<?> layout,
                                                   @PluginElement("Filters") Filter filter)
    {
        if (name == null)
        {
            LOGGER.error("No name provided for StatusMessageAppender");
            return null;
        }

        return new StatusMessageAppender(name, filter, layout, ignoreExceptions, null);
    }

    /**
     * 
     * Set the statusLabel to which the status messages needs to be written to.
     * 
     * @param statusLabel 
     */
    public static void setStatusLabel(final JTextArea statusLabel){
        StatusMessageAppender.statusLabel = statusLabel;
    }
    
    @Override
    public void append(final LogEvent logEvent){
        
        final String message;
        if(logEvent.getMessage() == null) {
            message = "(null)";
        } else {
            message = logEvent.getMessage().getFormattedMessage();
        }

        SwingUtilities.invokeLater(new Runnable(){
            
            @Override
            public void run(){
                if (statusLabel != null) {
                    statusLabel.setText(message);
                }
            }
        });
    }
    
}
