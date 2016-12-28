package com.ntak.examples.jobschedulerexample.application;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.ntak.examples.jobschedulerexample.R;
import com.ntak.examples.jobschedulerexample.event.LocalImgManipEvent;
import com.ntak.examples.jobschedulerexample.event.NetworkDownloadEvent;
import com.ntak.examples.jobschedulerexample.event.ViewEnableEvent;
import com.ntak.examples.jobschedulerexample.builder.MapBuilder;
import com.ntak.examples.jobschedulerexample.service.NetworkService;
import com.ntak.examples.jobschedulerexample.subscriber.BackgroundRenderResourceSubscriber;
import com.ntak.examples.jobschedulerexample.subscriber.NetworkSubscriber;

import static com.ntak.examples.jobschedulerexample.application.GlobalBus.*;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Button btnNetwork;
    private Button btnLocal;
    private ImageView andrLogo;
    private TextView bottomText;

    private NetworkSubscriber netService = new NetworkSubscriber();
    private BackgroundRenderResourceSubscriber locService = new BackgroundRenderResourceSubscriber();
    private GcmNetworkManager mGcmNetMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnNetwork = (Button)findViewById(R.id.btnNet);
        btnLocal = (Button)findViewById(R.id.btnLocal);
        bottomText = (TextView)findViewById(R.id.txtView);
        andrLogo = (ImageView)findViewById(R.id.imgView);
        mGcmNetMan = GcmNetworkManager.getInstance(getApplicationContext());

        if (!callbackTasks.isRegistered(this))
            callbackTasks.register(this);

        try {
            final URL url = new URL("http://wallpapersonthe.net/wallpapers/b/5120x4096/5120x4096-gundam_anime-18776.jpg");

            btnNetwork.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomText.setText(getResources().getString(R.string.pendNet_text));
                    backgroundTasks.post(new NetworkDownloadEvent(url,"output.jpg",getApplicationContext()));
                    btnNetwork.setEnabled(false);
                }
            });

            btnLocal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomText.setText(getResources().getString(R.string.pendLocal_text));
                    backgroundTasks.post(new LocalImgManipEvent(new MapBuilder<String,String>(HashMap.class).addEntry("ACTION","TRANS_IMAGE").addEntry("IMAGE_RES","spriteColourChange").build(),getApplicationContext(),R.drawable.andr_green));
                    btnLocal.setEnabled(false);
                }
            });
            
        } catch (MalformedURLException e) {
            Log.e(TAG, "onCreate: Unable to set up Network Task as URL is invalid.",e);
        }



    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!callbackTasks.isRegistered(this))
            callbackTasks.register(this);
        netService.open();
        locService.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (callbackTasks.isRegistered(this))
            callbackTasks.unregister(this);
        netService.close();
        locService.close();
    }

    /*
     * Checks on Subscribe annotation
     * 1) Checking specific to general @Subscribe methods.
     * 2) Checking instance creation upon message available for subscriber.
     *
     * 1) DOES NOT WORK LIKE THIS -> RAN OBJECT PARAMETER METHOD.
     * 2) RUNS SAME INSTANCE OF ACTIVITY - DOES NOT CREATE A NEW INSTANCE FOR EACH CONSUMPTION.
     */

    @Subscribe(threadMode=ThreadMode.MAIN)
    public void handleCallbackQueue(ViewEnableEvent item) {
        final ViewEnableEvent localItem = item;
        Log.i(TAG, "handleCallbackQueue(ViewEnableEvent): enabled");
        if (item.getIdentifier().equals("R.id.btnNet")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    btnNetwork.setEnabled(localItem.isEnable());
                    bottomText.setText(getResources().getString(R.string.def_text));
                }
            },1000);
        }

        if (item.getIdentifier().equals("R.id.btnLocal")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    btnLocal.setEnabled(localItem.isEnable());
                    bottomText.setText(getResources().getString(R.string.def_text));
                }
            },1000);
        }

    }

    /*
b    @Subscribe(threadMode=ThreadMode.MAIN)
    public void handleCallbackQueue(Object item) {
        Log.i(TAG, "handleCallbackQueue(Object): executed");
        return;
    }*/

    @Subscribe(threadMode=ThreadMode.MAIN)
    public void handleCallbackQueue(Bitmap item) {
        Log.i(TAG, "handleCallbackQueue: Bitmap image is being set.");
        andrLogo.setImageResource(android.R.color.transparent);
        andrLogo.setImageBitmap(item);
        andrLogo.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    @Subscribe(threadMode=ThreadMode.MAIN)
    public void handleCallbackQueue(Map<String,String> item) {
        Log.i(TAG, "handleCallbackQueue: Processing Callback ACTION.");
        if (item != null && item.containsKey("ACTION")) {
            switch (item.get("ACTION")) {
                case "NET_SYNC_PENDING":        Log.i(TAG, "handleCallbackQueue: ACTION: NET_SYNC_PENDING");
                                                new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {bottomText.setBackgroundColor(0xFFFF9C43);}}, 100);
                                                break;

                case "LOCAL_SYNC_PENDING" :     Log.i(TAG, "handleCallbackQueue: ACTION: LOCAL_SYNC_PENDING");
                                                new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {bottomText.setBackgroundColor(0xFF57FF3B);}}, 100);
                                                break;

                case "NET_SYNC_IDLE" :          Log.i(TAG, "handleCallbackQueue: ACTION: NET_SYNC_IDLE");
                                                new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {bottomText.setBackgroundColor(Color.parseColor(getResources().getString(R.color.colorBgTextBottom)));}}, 1000);
                                                break;

                case "LOCAL_SYNC_IDLE" :        Log.i(TAG, "handleCallbackQueue: ACTION: LOCAL_SYNC_IDLE");
                                                new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {bottomText.setBackgroundColor(Color.parseColor(getResources().getString(R.color.colorBgTextBottom)));}}, 1000);
                                                break;

                default:
                    Log.w(TAG, "handleCallbackQueue: Could not handle ACTION: " + item.get("ACTION"));
            }
        } else {
            Log.w(TAG, "handleCallbackQueue: Invalid Map passed in");
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGcmNetMan.cancelAllTasks(NetworkService.class);
    }
}
