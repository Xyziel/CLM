package com.pk.city_loudness_meter.services;

import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
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

        fusedLocationProviderClient
                .getLastLocation()
                .addOnCompleteListener(task -> taskCompletionSource.setResult(task.getResult()));

        return taskCompletionSource.getTask();
    }

}
