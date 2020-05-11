package com.pk.city_loudness_meter.services;

import android.media.MediaRecorder;
import android.os.Build;

public class MediaRecorderService {

    MediaRecorder recorder;

    public MediaRecorderService()
    {
        recorder = new MediaRecorder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            recorder.setAudioSource(MediaRecorder.AudioSource.UNPROCESSED);
        }
        else {
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        }

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
            return 1;

    }

    public double getDb()
    {
        return 20 * Math.log10(getAmplitude());
    }

}
