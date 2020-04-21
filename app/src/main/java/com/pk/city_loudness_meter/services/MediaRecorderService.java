package com.pk.city_loudness_meter.services;

import android.media.MediaRecorder;
import android.net.wifi.hotspot2.pps.Credential;

public class MediaRecorderService {

    MediaRecorder recorder;

    public MediaRecorderService()
    {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.UNPROCESSED);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
        recorder.setOutputFile("/dev/null");
        try
        {
            recorder.prepare();
        }catch (java.io.IOException ioe) {
            android.util.Log.e("[MAIN]", "IOException: " + android.util.Log.getStackTraceString(ioe));

        }catch (java.lang.SecurityException e) {
            android.util.Log.e("[MAIN]", "SecurityException: " + android.util.Log.getStackTraceString(e));
        }
        try
        {
            recorder.start();
        }catch (java.lang.SecurityException e) {
            android.util.Log.e("[MAIN]", "SecurityException: " + android.util.Log.getStackTraceString(e));
        }
    }

    protected double getAmplitude() {
        if (recorder != null)
            return recorder.getMaxAmplitude();
        else
            return 0;

    }

    public double getDb()
    {
        return 20 * Math.log10(getAmplitude());
    }

}
