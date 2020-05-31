package com.pk.city_loudness_meter.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.pk.city_loudness_meter.R;
import com.pk.city_loudness_meter.http.MeasurementRequest;
import com.pk.city_loudness_meter.services.LocationService;
import com.pk.city_loudness_meter.services.MediaRecorderService;
import com.pk.city_loudness_meter.util.DeviceUtils;

import org.json.JSONArray;

import okhttp3.OkHttpClient;

public class DataActivity extends AppCompatActivity {
    private GoogleSignInClient googleSignInClient;
    private MeasurementRequest measurementService;
    private MediaRecorderService mediaRecorderService;
    private TextView longitude, latitude, locError;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        final Button logoutButton = findViewById(R.id.buttonLogout);
        longitude = findViewById(R.id.textView5);
        latitude = findViewById(R.id.textView6);
        locError = findViewById(R.id.textView7);
        logoutButton.setOnClickListener(v -> logout());

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        LocationService locationService = new LocationService(LocationServices.getFusedLocationProviderClient(this));
        mediaRecorderService = new MediaRecorderService();
        DeviceUtils deviceUtils = new DeviceUtils(this);
        measurementService = new MeasurementRequest(mediaRecorderService, new OkHttpClient(), locationService, deviceUtils, this);
        startCollectingData();
    }

    private void logout() {
        googleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(DataActivity.this, LoginActivity.class));
                        finish();
                    }
                });

        SharedPreferences loginData = getSharedPreferences("loginData", MODE_PRIVATE);
        SharedPreferences.Editor loginDataEditor = loginData.edit();
        loginDataEditor.putBoolean("loginDataSaved", false);
        loginDataEditor.apply();

        measurementService.stop();
        mediaRecorderService.stopRecorder();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void startCollectingData() {
        measurementService.prepareData();
        measurementService.sendDataTask();
    }

//    public void displayListLength(JSONArray list) {
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(getApplicationContext(), list.length() +  " measurements were sent.", Toast.LENGTH_LONG).show();
//            }
//        });
//    }

    public void displayLocation(double lat, double lon) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                latitude.setText("Latitude: " + lat);
                longitude.setText("Longitude: " + lon);
                locError.setText("");
            }
        });
    }

    public void displayLocalizationError() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                locError.setText("Can't find your location");
            }
        });
    }

}
