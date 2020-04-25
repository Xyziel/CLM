package com.pk.city_loudness_meter.util;

import android.app.Activity;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.view.Display;

public class DeviceUtils {

    private Activity activity;

    public DeviceUtils(Activity activity) {
        this.activity = activity;
    }

    public boolean isLocked() {
        DisplayManager dm = (DisplayManager) activity.getSystemService(Context.DISPLAY_SERVICE);
        for (Display display : dm.getDisplays()) {
            if (display.getState() == Display.STATE_OFF) {
                return true;
            }
        }
        return false;
    }
}
