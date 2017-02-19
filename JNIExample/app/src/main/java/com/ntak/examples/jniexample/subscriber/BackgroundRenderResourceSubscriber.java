package com.ntak.examples.jniexample.subscriber;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TimingLogger;

import com.ntak.examples.jniexample.R;
import com.ntak.examples.jniexample.builder.MapBuilder;
import com.ntak.examples.jniexample.event.LocalImgManipEvent;
import com.ntak.examples.jniexample.event.ViewEnableEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.ntak.examples.jniexample.application.GlobalBus.callbackTasks;
import static com.ntak.examples.jniexample.application.GlobalBus.backgroundTasks;

/**
 * Created by akakshepati on 21/12/16.
 */
public class BackgroundRenderResourceSubscriber implements Subscriber {

    static {
        System.loadLibrary("imgManipExample");
    }

    private static final String TAG = "BgRendResSubscriber";
    private static final String TAG_TIMER = "TIMER_BitmapTrans";

    public static native void getBitmapJNI(String fileLoc);
    // TODO: 3) Benchmark time taken between C and Java code

    @Subscribe(threadMode=ThreadMode.ASYNC)
    public void handleImgManipEvent(LocalImgManipEvent event) {
        Log.i(TAG, "handleImgManipEvent: Consuming and processing LocalImgManipEvent.");
        callbackTasks.post(new MapBuilder<String,String>(HashMap.class).addEntry("ACTION","LOCAL_SYNC_PENDING").build());

        Log.i(TAG, "handleImgManipEvent: TAG_TIMER Verbose log level enabled = " + Log.isLoggable(TAG_TIMER,Log.VERBOSE));

        Map<String, String> params = event.getParams();
        
        try {
            if (params == null || (params.containsKey("ACTION") && !params.get("ACTION").equals("TRANS_IMAGE"))) {
                Log.i(TAG, "handleImgManipEvent: ACTION was not the expected image manipulation request: " + event.getParams().get("ACTION").equals("TRANS_IMAGE"));
                return;
            }

            if (event.isNATIVE_CALL() && params.get("SRC_FILE") == null) {
                Log.i(TAG, "handleImgManipEvent: SRC_FILE has not been set to an expected value.");
                return;
            }
            
            if (event.getCALLING_CONTEXT() == null) {
                Log.i(TAG, "handleImgManipEvent: The event metadata is malformed. Please check context and resource id.");
                return;
            }

            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inMutable = true;
            opt.inScaled = false;

            Bitmap img = BitmapFactory.decodeResource(event.getCALLING_CONTEXT().getResources(), event.getIMG_RES_ID(), opt);
            TimingLogger tLog = new TimingLogger(TAG_TIMER, "BITMAP_TRANS_CMP");

            if (!event.isNATIVE_CALL()) {
                tLog.addSplit("getBitmap START");
                img = getBitmap(event, img);
                tLog.addSplit("getBitmap END");
                if (img == null) return;
            } else {
                File png = new File(params.get("SRC_FILE"));

                if (!png.getParentFile().exists())
                    png.getParentFile().mkdirs();

                Log.i(TAG, "handleImgManipEvent: File path: " + png.getAbsolutePath());
                FileOutputStream outStream = event.getCALLING_CONTEXT().openFileOutput(png.getName(),event.getCALLING_CONTEXT().MODE_PRIVATE);

                img = BitmapFactory.decodeResource(event.getCALLING_CONTEXT().getResources(), event.getIMG_RES_ID(), opt);
                img.compress(Bitmap.CompressFormat.PNG, 100, outStream);

                tLog.addSplit("getBitmapJNI START");
                getBitmapJNI(png.getAbsolutePath());
                tLog.addSplit("getBitmapJNI END");
            }
            tLog.dumpToLog();
            callbackTasks.post(img);
        }  catch (IOException e) {
            Log.w(TAG, "handleImgManipEvent: Could not create png file.", e);
        }
        finally {
            // Enable local button in Main Activity on completion
            ViewEnableEvent callbackEvent = new ViewEnableEvent();
            callbackEvent.setIdentifier("R.id.btnLocal");
            callbackEvent.setEnable(true);
            callbackTasks.post(callbackEvent);
            callbackTasks.post(new MapBuilder<String, String>(HashMap.class).addEntry("ACTION", "LOCAL_SYNC_IDLE").build());
        }
    }

    @Nullable
    private Bitmap getBitmap(LocalImgManipEvent event, Bitmap img) {

        if (img == null) {
            Log.i(TAG, "handleImgManipEvent: The image could not be decoded properly.");
            return null;
        }

        int logProgressUpdate = img.getWidth() * img.getHeight() / 10;
        int i = 0;
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int pixel = img.getPixel(x, y);

                if (pixel == 0xFF30587C) { // BLUE
                    img.setPixel(x, y, Color.parseColor(event.getCALLING_CONTEXT().getResources().getString(R.color.colorAndrLogoBgAfter)));
                }

                if (pixel == 0xFFA4C639) { // GREEN
                    img.setPixel(x, y, Color.parseColor(event.getCALLING_CONTEXT().getResources().getString(R.color.colorAndrLogoActorAfter)));
                }

                if (((x * img.getHeight() + y) % logProgressUpdate) == 0) {
                    Log.i(TAG, "handleImgManipEvent: Completed analysing " + i * 10 + "% of source image.");
                    i++;
                }
            }
        }
        return img;
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
