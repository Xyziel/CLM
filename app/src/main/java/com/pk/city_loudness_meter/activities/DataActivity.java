package com.pk.city_loudness_meter.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

import okhttp3.OkHttpClient;

public class DataActivity extends AppCompatActivity {
    private GoogleSignInClient googleSignInClient;
    private MeasurementRequest measurementService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        final Button logoutButton = findViewById(R.id.buttonLogout);

        logoutButton.setOnClickListener(v -> logout());

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        LocationService locationService = new LocationService(LocationServices.getFusedLocationProviderClient(this));
        MediaRecorderService mediaRecorderService = new MediaRecorderService();
        DeviceUtils deviceUtils = new DeviceUtils(this);
        measurementService = new MeasurementRequest(mediaRecorderService, new OkHttpClient(), locationService, deviceUtils);
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

}
