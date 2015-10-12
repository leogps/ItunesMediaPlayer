/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gps.itunes.media.player.ui.controller;

/**
 * Contains the information about whether a Major task is in progress
 *
 * @author leogps
 */
public final class MajorTaskInfo {
    
    private MajorTaskInfo(){
    }
    
    private static boolean majorTaskInProgress = false;
    
    /**
     * 
     * Set the majorTaskInProgress value.
     * 
     * @param majorTaskInProgress 
     */
    public static void setMajorTaskInfo(final boolean majorTaskInProgress){
        MajorTaskInfo.majorTaskInProgress = majorTaskInProgress;
    }
    
    /**
     * 
     * Returns the majorTaskInProgress value.
     * 
     * @return 
     */
    public static boolean isMajorTaskInProgress(){
        return majorTaskInProgress;
    }
    
}
