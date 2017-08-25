package com.example.polygon_monitor;


import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.SparseArrayCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;


/**
 * Created by User on 8/17/2017.
 */

public class ServicesPolygonMonitorService extends Service {

    private final int INTERVAL = 10000;
    private final int FASTEST_INTERVAL = 5000;


    private FusedLocationProviderClient providerClient;
    private SparseArrayCompat<List<LatLng>> polygons = new SparseArrayCompat<>();
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {


        if (intent != null) {
            if (intent.hasExtra(ReceiversGeofenceEventReceiver.GEO_ID)) {
                createLocationRequest();
                requestingLocationUpdates();
                locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        for (Location location :
                                locationResult.getLocations()) {

                            for (int i = 0; i < polygons.size(); i++) {
                                int key = polygons.keyAt(i);
                                if (UtilsLocationUtil.isPointInPolygon(location, polygons.get(key))) {
                                    Intent polygonIntent = new Intent();
                                    Log.d(PolygonMonitorController.POLYGON_MONITOR_TAG, "key is " + polygons.get(key).toString());
                                    polygonIntent.setAction(ReceiversGeofenceEventReceiver.ENTER_POLYGON);
                                    polygonIntent.putExtra(ReceiversGeofenceEventReceiver.GEO_ID, String.valueOf(key));
                                    sendBroadcast(polygonIntent);
                                    polygons.remove(key);
                                    if (polygons.size() == 0) {
                                        stopSelf();
                                        Log.d(PolygonMonitorController.POLYGON_MONITOR_TAG, "polygon monitor stopped");
                                    }
                                }
                            }


                        }
                    }
                };
                providerClient = LocationServices.getFusedLocationProviderClient(this);


                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    providerClient.requestLocationUpdates(locationRequest, locationCallback, null);
                }

                if (intent.getAction().equals(ReceiversGeofenceEventReceiver.ADD_POLYGON)) {
                    List<LatLng> polyList = UtilsPolyUtil.decode(intent.getStringExtra(ReceiversGeofenceEventReceiver.ENCODED_POLYGON));
                    addPolygon(intent.getStringExtra(ReceiversGeofenceEventReceiver.GEO_ID), polyList);
                } else if (intent.getAction().equals(ReceiversGeofenceEventReceiver.DELETE_POLYGON)) {
                    Log.d(PolygonMonitorController.POLYGON_MONITOR_TAG, "starting deleting" + intent.getStringExtra(ReceiversGeofenceEventReceiver.GEO_ID));
                    removePolygon(intent.getStringExtra(ReceiversGeofenceEventReceiver.GEO_ID));
                }
            }
        }
        return START_STICKY;
    }

    private void createLocationRequest() {

        if (locationRequest == null) {
            locationRequest = new LocationRequest();
            locationRequest.setInterval(INTERVAL);
            locationRequest.setFastestInterval(FASTEST_INTERVAL);
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        }
    }

    private void requestingLocationUpdates() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.d(PolygonMonitorController.POLYGON_MONITOR_TAG, "task Success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(PolygonMonitorController.POLYGON_MONITOR_TAG, "task Failure");
            }
        });
    }

    public void addPolygon(String geoId, List<LatLng> polygon) {

        polygons.put(Integer.valueOf(geoId), polygon);

    }

    public void removePolygon(String geoId) {

        polygons.remove(Integer.valueOf(geoId));
        Log.d(PolygonMonitorController.POLYGON_MONITOR_TAG, "removed " + geoId);

    }


}
