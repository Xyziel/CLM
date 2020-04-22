package com.pk.city_loudness_meter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationServices;
import com.pk.city_loudness_meter.http.MeasurementRequest;
import com.pk.city_loudness_meter.services.MediaRecorderService;
import com.pk.city_loudness_meter.services.LocationService;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    private boolean permissionToRecordAccepted = false;
    private boolean permissionToCourseLocation = false;
    private boolean permissionToInternet = false;
    private boolean permissionToAccessNetwork = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionRequest();

        LocationService locationService = new LocationService(LocationServices.getFusedLocationProviderClient(this));
        MediaRecorderService mediaRecorderService = new MediaRecorderService();
        MeasurementRequest measurementService = new MeasurementRequest(mediaRecorderService, new OkHttpClient(), locationService);

        measurementService.send();
    }

    private void permissionRequest() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO,
           Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE
        }, 200);
    }

    @Override
    public void onRequestPermissionsResult(int code, String[] permissions, int[] grantResults) {
        if (code == 200) {
            permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            permissionToCourseLocation = grantResults[1] == PackageManager.PERMISSION_GRANTED;
            permissionToInternet = grantResults[2] == PackageManager.PERMISSION_GRANTED;
            permissionToAccessNetwork = grantResults[3] == PackageManager.PERMISSION_GRANTED;
        }
        if (!permissionToRecordAccepted && !permissionToCourseLocation && !permissionToAccessNetwork && !permissionToInternet)
            finish();
    }
}
