package com.gps.ilp.utils;

/**
 * Created by leogps on 11/27/14.
 */
public class URLFetchException extends Exception {

    public URLFetchException(String msg) {
        super(msg);
    }

    public URLFetchException(Exception ex) {
        super(ex);
    }
}
