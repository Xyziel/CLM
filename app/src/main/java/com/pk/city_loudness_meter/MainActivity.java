package com.pk.city_loudness_meter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationServices;
import com.pk.city_loudness_meter.activities.LoginActivity;
import com.pk.city_loudness_meter.http.MeasurementRequest;
import com.pk.city_loudness_meter.services.MediaRecorderService;
import com.pk.city_loudness_meter.services.LocationService;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    private boolean permissionToRecordAccepted = false;
    private boolean permissionToInternet = false;
    private boolean permissionToAccessNetwork = false;
    private boolean permissionToFineLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionRequest();

    }

    private void permissionRequest() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO,
           Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE,
           Manifest.permission.ACCESS_COARSE_LOCATION
        }, 200);
    }

    @Override
    public void onRequestPermissionsResult(int code, String[] permissions, int[] grantResults) {
        if (code == 200) {
            permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            permissionToFineLocation = grantResults[1] == PackageManager.PERMISSION_GRANTED;
            permissionToInternet = grantResults[2] == PackageManager.PERMISSION_GRANTED;
            permissionToAccessNetwork = grantResults[3] == PackageManager.PERMISSION_GRANTED;
        }
        if (!permissionToRecordAccepted && !permissionToFineLocation && !permissionToAccessNetwork && !permissionToInternet) {
            finish();
        }
        else {
            login();
        }
    }

    private void login() {
        Intent intent = new Intent(this, LoginActivity.class);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }, 2000);
    }
}
