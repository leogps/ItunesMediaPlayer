package com.gps.youtube.dl.process;

/**
 * Created by leogps on 12/18/15.
 */
public interface AsyncProcessListener {

    void onSuccess(AsyncProcess asyncProcess);

    void onFailure(AsyncProcess asyncProcess);

}
