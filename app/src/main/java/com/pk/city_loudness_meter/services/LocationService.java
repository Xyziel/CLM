package com.pk.city_loudness_meter.services;

import android.location.Location;
import android.os.Looper;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;

import java.util.concurrent.ExecutionException;

public class LocationService {

    private FusedLocationProviderClient fusedLocationProviderClient;

    public LocationService(FusedLocationProviderClient fusedLocationProviderClient) {
        this.fusedLocationProviderClient = fusedLocationProviderClient;

    }

    public Location getLocation() {
        Task<Location> geoLocationTask = this.getLastLocationTask();

        try {
            Tasks.await(geoLocationTask);
        } catch (ExecutionException | InterruptedException e) {
            android.util.Log.e("[MAIN]", "Execution exception: " + android.util.Log.getStackTraceString(e));
        }

        return geoLocationTask.getResult();
    }

    private Task<Location> getLastLocationTask() {
        TaskCompletionSource<Location> taskCompletionSource = new TaskCompletionSource<>();

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(
                new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        requestNewLocationData();
                        taskCompletionSource.setResult(task.getResult());
                    }
                }
        );

        return taskCompletionSource.getTask();
    }

    private void requestNewLocationData(){

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(0);
        locationRequest.setFastestInterval(0);
        locationRequest.setNumUpdates(1);

        fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, new LocationCallback(),
                Looper.myLooper()
        );

    }
}
