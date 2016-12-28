package com.ntak.examples.jobschedulerexample.event;

import android.content.Context;
import android.os.Bundle;

import com.ntak.examples.jobschedulerexample.builder.BundleBuilder;

import java.net.URL;

/**
 * Specifies a file to be downloaded. This is for use in conjunction with the NetworkService.
 *
 * Created by akakshepati on 21/12/16.
 */
public class NetworkDownloadEvent {
    private final URL DOWNLOAD_URL;
    private final String OUTPUT_FILENAME;
    private final Context CALLING_CONTEXT;
    private final Bundle bundle;

    public NetworkDownloadEvent(URL DOWNLOAD_URL, String OUTPUT_FILENAME, Context context) {
        this.DOWNLOAD_URL = DOWNLOAD_URL;
        this.OUTPUT_FILENAME = OUTPUT_FILENAME;
        this.CALLING_CONTEXT = context;
        this.bundle = new BundleBuilder()   .addValue("DOWNLOAD_URL",DOWNLOAD_URL)
                                            .addValue("OUTPUT_FILENAME",OUTPUT_FILENAME)
                                            .build();
    }

    public URL getDOWNLOAD_URL() {
        return DOWNLOAD_URL;
    }

    public String getOUTPUT_FILENAME() {
        return OUTPUT_FILENAME;
    }

    public Context getCALLING_CONTEXT() {
        return CALLING_CONTEXT;
    }

    public Bundle getBundle() {
        return bundle;
    }
}
