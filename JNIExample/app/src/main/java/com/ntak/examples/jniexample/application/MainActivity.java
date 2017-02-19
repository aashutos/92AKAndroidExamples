package com.ntak.examples.jniexample.application;

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

import com.ntak.examples.jniexample.R;
import com.ntak.examples.jniexample.event.LocalImgManipEvent;
import com.ntak.examples.jniexample.event.ViewEnableEvent;
import com.ntak.examples.jniexample.builder.MapBuilder;
import com.ntak.examples.jniexample.subscriber.BackgroundRenderResourceSubscriber;

import static com.ntak.examples.jniexample.application.GlobalBus.*;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Button btnLocal;
    private Button btnLocalC;
    private ImageView andrLogo;
    private TextView bottomText;

    private BackgroundRenderResourceSubscriber locService = new BackgroundRenderResourceSubscriber();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLocal = (Button)findViewById(R.id.btnLocal);
        btnLocalC = (Button)findViewById(R.id.btnLocalC);
        bottomText = (TextView)findViewById(R.id.txtView);
        andrLogo = (ImageView)findViewById(R.id.imgView);

        if (!callbackTasks.isRegistered(this))
            callbackTasks.register(this);

        try {
            final URL url = new URL("http://www.mediafire.com/convkey/69e2/zp6wgh8yv5f5r5xzg.jpg");

            btnLocal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postLocalTask(false);
                }
            });

            btnLocalC.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    postLocalTask(true);
                }
            });

        } catch (MalformedURLException e) {
            Log.e(TAG, "onCreate: Unable to set up Network Task as URL is invalid.",e);
        }



    }

    private void postLocalTask(boolean isNative) {
        bottomText.setText(getResources().getString(R.string.pendLocal_text));
        backgroundTasks.post(new LocalImgManipEvent(new MapBuilder<String,String>(HashMap.class).addEntry("ACTION","TRANS_IMAGE").addEntry("IMAGE_RES","spriteColourChange").addEntry("SRC_FILE",getBaseContext().getFilesDir()+File.separator+"andrLogo.png").build(),getApplicationContext(),R.drawable.andr_green,isNative));
        btnLocal.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!callbackTasks.isRegistered(this))
            callbackTasks.register(this);
        locService.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (callbackTasks.isRegistered(this))
            callbackTasks.unregister(this);
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


    @Subscribe(threadMode=ThreadMode.MAIN)
    public void handleCallbackQueue(Object item) {
        Log.i(TAG, "handleCallbackQueue(Object): executed");
        return;
    }

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
    }
}
