package com.ntak.examples.jobschedulerexample.builder;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by akakshepati on 23/12/16.
 */
public class BundleBuilder {

    private static final String TAG = "BundleBuilder";
    private final Bundle bundle;

    public BundleBuilder() {
        bundle = new Bundle();
    }

    public <T> BundleBuilder addValue(String key, T value) {
        if (value == null || key == null)
            return this;

        switch (value.getClass().getCanonicalName()) {

            case "android.os.Bundle":           bundle.putBundle(key,(Bundle) value);
                                                break;

            case "java.lang.Byte":              bundle.putByte(key,(Byte)value);
                                                break;

            case "java.lang.Character":         bundle.putChar(key,(Character)value);
                                                break;

            case "java.lang.Float":             bundle.putFloat(key,(Float)value);
                                                break;

            case "java.lang.CharSequence":      bundle.putCharSequence(key,(CharSequence)value);
                                                break;

            case "android.os.Parcelable":       bundle.putParcelable(key,(Parcelable)value);
                                                break;

            case "java.lang.Integer":           bundle.putInt(key,(Integer)value);
                                                break;

            case "java.lang.Short":             bundle.putShort(key,(Short)value);
                                                break;

            default:
                Log.w(TAG, "addItem: Type was not an expected type for the Bundle. The String representation was saved down in bundle.");
                Log.i(TAG, "addItem: Key: " + key + " Value: " + value);
                bundle.putString(key,value.toString());
        }
        return this;
    }

    public Bundle build() {
        return bundle;
    }

}
