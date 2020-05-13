package com.pk.city_loudness_meter.http;

import com.pk.city_loudness_meter.services.LocationService;
import com.pk.city_loudness_meter.services.MediaRecorderService;
import com.pk.city_loudness_meter.util.DeviceUtils;

import org.json.JSONArray;
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
    private DeviceUtils deviceUtils;
    private JSONArray dataList = new JSONArray();
    private boolean runTask = true;
    private Timer sendDataTimer, prepareDataTimer;


    private final int SEND_DATA_PERIOD = 5 * 60 * 1000;
    private final int GET_DATA_PERIOD = 3 * 1000;

    public MeasurementRequest(MediaRecorderService mediaRecorderService, OkHttpClient okHttpClient, LocationService locationService, DeviceUtils deviceUtils) {
        this.locationService = locationService;
        this.mediaRecorderService = mediaRecorderService;
        this.okHttpClient = okHttpClient;
        this.deviceUtils = deviceUtils;
    }

    private void getData() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("longitude", locationService.getLocation().getLongitude());
            jsonObject.put("latitude", locationService.getLocation().getLatitude());
            jsonObject.put("magnitude", mediaRecorderService.getDb());
            dataList.put(jsonObject);
        } catch (JSONException e) {
            android.util.Log.e("[MAIN]", "JSON Exception: " + android.util.Log.getStackTraceString(e));
        }
    }

    private Request prepareRequest() {
        return new Request.Builder()
                .url("https://cityloudnessmeter.herokuapp.com/api/location")
                .post(RequestBody.create(dataList.toString(), MediaType.parse("application/json; charset=utf-8")))
                .addHeader("Content-Type", "application/json")
                .build();
    }

    private void send() {
        try {
            Call call = okHttpClient.newCall(prepareRequest());
            call.execute();
        } catch (IOException e) {
            android.util.Log.e("[MAIN]", "IO Exception: " + android.util.Log.getStackTraceString(e));
        }
    }

    public void sendDataTask() {
        sendDataTimer = new Timer();
        sendDataTimer .scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runTask = false;
                if (dataList.length() > 0) {
                    send();
                }
                clearData();
                runTask = true;
            }
        }, SEND_DATA_PERIOD, SEND_DATA_PERIOD);
    }

    public void prepareData() {
        prepareDataTimer = new Timer();
        prepareDataTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (runTask && !deviceUtils.isLocked()) {
                    getData();
                }
            }

        }, 100, GET_DATA_PERIOD);
    }

    private void clearData() {
        dataList = new JSONArray();
    }

    public void stop() {
        sendDataTimer.cancel();
        sendDataTimer.purge();
        prepareDataTimer.cancel();
        prepareDataTimer.purge();
    }
}
