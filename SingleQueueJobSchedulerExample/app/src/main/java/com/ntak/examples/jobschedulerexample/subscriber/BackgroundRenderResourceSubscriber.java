package com.ntak.examples.jobschedulerexample.subscriber;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import com.ntak.examples.jobschedulerexample.R;
import com.ntak.examples.jobschedulerexample.builder.MapBuilder;
import com.ntak.examples.jobschedulerexample.event.LocalImgManipEvent;
import com.ntak.examples.jobschedulerexample.event.ViewEnableEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import static com.ntak.examples.jobschedulerexample.application.GlobalBus.callbackTasks;
import static com.ntak.examples.jobschedulerexample.application.GlobalBus.backgroundTasks;

/**
 * Created by akakshepati on 21/12/16.
 */
public class BackgroundRenderResourceSubscriber implements Subscriber {

    private static final String TAG = "BgRendResSubscriber";

    @Subscribe(threadMode=ThreadMode.ASYNC)
    public void handleImgManipEvent(LocalImgManipEvent event) {
        Log.i(TAG, "handleImgManipEvent: Consuming and processing LocalImgManipEvent.");
        callbackTasks.post(new MapBuilder<String,String>(HashMap.class).addEntry("ACTION","LOCAL_SYNC_PENDING").build());

        try {
            if (event.getParams() == null || (event.getParams().containsKey("ACTION") && !event.getParams().get("ACTION").equals("TRANS_IMAGE"))) {
                Log.i(TAG, "handleImgManipEvent: ACTION was not the expected image manipulation request: " + event.getParams().get("ACTION").equals("TRANS_IMAGE"));
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

            if (img == null) {
                Log.i(TAG, "handleImgManipEvent: The image could not be decoded properly.");
                return;
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

            callbackTasks.post(img);
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
