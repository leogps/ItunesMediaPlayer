/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gps.itunes.media.player.ui.tablehelpers.comparators;

import java.util.Comparator;

/**
 *
 * Provides methods that return {@link #java.util.Comparator Comparator} which
 * can be used when sorting the table columns.
 *
 * @author leogps
 */
public class RowComparators {

    /**
     *
     * Returns a comparator that provides default {@link Integer Integer}
     * compare functionality.
     *
     * @return
     */
    public static Comparator getIntegerComparator() {

        return new Comparator() {

            @Override
            public int compare(final Object obj1, final Object obj2) {

                return ((Integer) obj1).compareTo((Integer) obj2);

            }
        };

    }
    
    /**
     * 
     * Return Comparator to compare integers. If object is not a number,
     * the object is given a value of -1 for comparison.
     * 
     * @return 
     */
    public static Comparator getIntegerComparatorNullAllowed() {
        
        return new Comparator() {

            @Override
            public int compare(final Object obj1, final Object obj2) {
                final Integer int1 = getIntValue(obj1);
                final Integer int2 = getIntValue(obj2);
                
                return (int1).compareTo(int2);
            }

            private int getIntValue(Object obj) {
                int num = -1;
                try{
                    num = Integer.parseInt((String)obj);
                } catch(final Exception e) {
                    //Eat error.
                }
                return num;
            }
        };
        
    }

    /**
     * 
     * Returns a comparator that provides comparison logic for time
     * in the format hh:mm:ss
     * 
     * @return 
     */
    public static Comparator getTimeComparator() {
        return new Comparator() {

            @Override
            public int compare(Object obj1, Object obj2) {
                final TrackTime time1 = TrackTime.parseTime((String) obj1);
                final TrackTime time2 = TrackTime.parseTime((String) obj2);

                final int hrComparison;
                final int minComparison;

                if ((hrComparison = getIntegerComparator().compare(time1.getHrs(), time2.getHrs())) != 0) {
                    return hrComparison;
                } else if ((minComparison = getIntegerComparator().compare(time1.getMins(), time2.getMins())) != 0) {
                    return minComparison;
                } else {
                    return getIntegerComparator().compare(time1.getSecs(), time2.getSecs());
                }
            }
        };
    }
}
