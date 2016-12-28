package com.ntak.examples.jobschedulerexample.service;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.ntak.examples.jobschedulerexample.builder.MapBuilder;
import com.ntak.examples.jobschedulerexample.event.ViewEnableEvent;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import static com.ntak.examples.jobschedulerexample.application.GlobalBus.callbackTasks;

/**
 * Created by akakshepati on 23/12/16.
 */
public class NetworkService extends GcmTaskService {

    private static final String TAG = "NetworkService";

    @Override
    public int onRunTask(TaskParams taskParams) {
        Bundle bundle = taskParams.getExtras();

        if (bundle == null) {
            Log.i(TAG, "doInBackground: No URL supplied. Task ending.");
            return GcmNetworkManager.RESULT_SUCCESS;
        }

        String url = bundle.getString("DOWNLOAD_URL");

        if (url == null) {
            Log.i(TAG, "doInBackground: No URL supplied. Task ending.");
            return GcmNetworkManager.RESULT_SUCCESS;
        }

        String outputFileName = bundle.getString("OUTPUT_FILENAME");

        if (outputFileName == null || outputFileName.trim().isEmpty())
            outputFileName = "tmp.bin";

        Log.i(TAG, "onRunTask: Starting copy of data from: " + url + " to: " + outputFileName);
        
        try {
            URLConnection con = new URL(url).openConnection();
            con.connect();
            BufferedInputStream iStream = new BufferedInputStream(con.getInputStream());
            File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath(),outputFileName);

            if (outputFile.exists())
                outputFile.delete();

            int output;

            //outputFile.createNewFile();
            FileOutputStream oFileStream = new FileOutputStream(outputFile);
            BufferedOutputStream oStream = new BufferedOutputStream(oFileStream);

            int bytesRead = 0;
            while ((output = iStream.read()) != -1) {
                oStream.write(output);
                if (bytesRead % 1000 == 0) {
                    Log.i(TAG, "onRunTask: Processed 1000 Bytes of data." );
                }
                bytesRead++;
            }
            oStream.close();
            iStream.close();
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: Problem copying file to local location.",e);
            return GcmNetworkManager.RESULT_FAILURE;
        } finally {
            // Enable download button in Main Activity on completion
            Log.i(TAG, "onRunTask: Producing ViewEnableEvent message to inform activity of event completion.");
            callbackTasks.post(new MapBuilder<String,String>(HashMap.class).addEntry("ACTION","NET_SYNC_IDLE").build());
            ViewEnableEvent callbackEvent = new ViewEnableEvent();
            callbackEvent.setIdentifier("R.id.btnNet");
            callbackEvent.setEnable(true);
            callbackTasks.post(callbackEvent);
        }
        Log.i(TAG, "onRunTask: File copying process complete.");
        return GcmNetworkManager.RESULT_SUCCESS;
    }
}
