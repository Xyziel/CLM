package com.pk.city_loudness_meter.http;

import com.pk.city_loudness_meter.services.LocationService;
import com.pk.city_loudness_meter.services.MediaRecorderService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MeasurementRequest {

    private MediaRecorderService mediaRecorderService;
    private LocationService locationService;
    private OkHttpClient okHttpClient;

    public MeasurementRequest(MediaRecorderService mediaRecorderService, OkHttpClient okHttpClient, LocationService locationService) {
        this.locationService = locationService;
        this.mediaRecorderService = mediaRecorderService;
        this.okHttpClient = okHttpClient;
    }

    private JSONObject prepareData() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("longitude", locationService.getLocation().getLongitude());
            jsonObject.put("latitude", locationService.getLocation().getLatitude());
            jsonObject.put("magnitude", mediaRecorderService.getDb());
        } catch (JSONException e) {
            android.util.Log.e("[MAIN]", "JSON Exception: " + android.util.Log.getStackTraceString(e));
        }

        return jsonObject;
    }

    private Request prepareRequest() {
        return new Request.Builder()
                .url("http://10.0.2.2:8080/api/location")
                .post(RequestBody.create(prepareData().toString(), MediaType.parse("application/json; charset=utf-8")))
                .addHeader("Content-Type", "application/json")
                .build();
    }

    public void send() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    Call call = okHttpClient.newCall(prepareRequest());
                    call.execute();
                } catch (IOException e) {
                    android.util.Log.e("[MAIN]", "IO Exception: " + android.util.Log.getStackTraceString(e));
                }
            }

        }, 0, 3000);
    }
}
