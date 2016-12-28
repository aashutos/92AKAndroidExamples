package com.ntak.examples.jobschedulerexample.event;

import android.content.Context;

import java.util.Map;

/**
 * Created by akakshepati on 24/12/16.
 */

public class LocalImgManipEvent {

    private final Map<String,String> params;
    private final Context CALLING_CONTEXT;
    private final int IMG_RES_ID;

    public LocalImgManipEvent(Map<String, String> params, Context CALLING_CONTEXT, int IMG_RES_ID) {
        this.params = params;
        this.CALLING_CONTEXT = CALLING_CONTEXT;
        this.IMG_RES_ID = IMG_RES_ID;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public Context getCALLING_CONTEXT() {
        return CALLING_CONTEXT;
    }

    public int getIMG_RES_ID() {
        return IMG_RES_ID;
    }
}
