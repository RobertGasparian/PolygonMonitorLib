package com.example.polygon_monitor;


import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import static java.util.Collections.sort;

/**
 * Created by polbins on 14/05/2016.
 */
 class UtilsLocationUtil {



    static LatLng convertLocationToLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }


    public static boolean isPointInPolygon(Location location, List<LatLng> locations) {
        return UtilsPolyUtil.containsLocation(
                UtilsLocationUtil.convertLocationToLatLng(location), locations, true);
    }


}
