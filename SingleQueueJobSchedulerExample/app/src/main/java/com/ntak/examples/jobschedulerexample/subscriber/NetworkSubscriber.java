package com.ntak.examples.jobschedulerexample.subscriber;

import android.net.ConnectivityManager;
import android.util.Log;

import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.Task;
import com.ntak.examples.jobschedulerexample.builder.MapBuilder;
import com.ntak.examples.jobschedulerexample.event.NetworkDownloadEvent;
import com.ntak.examples.jobschedulerexample.event.ViewEnableEvent;

import static com.ntak.examples.jobschedulerexample.application.GlobalBus.*;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.ntak.examples.jobschedulerexample.service.NetworkService;

import java.util.HashMap;

public class NetworkSubscriber implements Subscriber{

    private static final String TAG = "NetworkSubscriber";

    private GcmNetworkManager mGcmNetMan;

    public NetworkSubscriber() {
        backgroundTasks.register(this);
    }

    @Subscribe(threadMode=ThreadMode.ASYNC)
    public void handleNetworkDownloadEvent(NetworkDownloadEvent event) {
        Log.i(TAG, "handleNetworkDownloadEvent: Consuming and processing NetworkDownloadEvent.");
        Log.d(TAG, "handleNetworkDownloadEvent: HashCode: " + this.hashCode() + " ToString: " + this.toString());

        callbackTasks.post(new MapBuilder<String,String>(HashMap.class).addEntry("ACTION","NET_SYNC_PENDING").build());

        ConnectivityManager conMgr = (ConnectivityManager) event.getCALLING_CONTEXT().getSystemService (event.getCALLING_CONTEXT().CONNECTIVITY_SERVICE);
        if (conMgr.getActiveNetworkInfo() == null
                || !conMgr.getActiveNetworkInfo().isAvailable()
                || !conMgr.getActiveNetworkInfo().isConnected()) {
            Log.i(TAG, "handleNetworkDownloadEvent: The app is not connected to the internet.");

            // Enable download button in Main Activity on completion
            Log.i(TAG, "onRunTask: Producing ViewEnableEvent message to inform activity of event completion.");
            ViewEnableEvent callbackEvent = new ViewEnableEvent();
            callbackEvent.setIdentifier("R.id.btnNet");
            callbackEvent.setEnable(true);
            callbackTasks.post(callbackEvent);
            callbackTasks.post(new MapBuilder<String,String>(HashMap.class).addEntry("ACTION","NET_SYNC_IDLE").build());
            return;
        }
        
        Task task = new OneoffTask.Builder()
                .setService(NetworkService.class)
                .setExecutionWindow(0, 30)
                .setTag(TAG)
                .setUpdateCurrent(false)
                .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                .setRequiresCharging(false)
                .setExtras(event.getBundle())
                .build();

        mGcmNetMan = GcmNetworkManager.getInstance(event.getCALLING_CONTEXT());
        mGcmNetMan.schedule(task);

        Log.i(TAG, "handleNetworkDownloadEvent: Network task scheduled.");
    }

    @Override
    public void open() {
        if (!backgroundTasks.isRegistered(this))
            backgroundTasks.register(this);
    }

    @Override
    public void close() {
        if (backgroundTasks.isRegistered(this))
            backgroundTasks.unregister(this);
    }
}
