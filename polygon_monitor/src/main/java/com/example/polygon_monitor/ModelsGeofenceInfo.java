package com.example.polygon_monitor;

/**
 * Created by Rob on 8/11/2017.
 */
 public class ModelsGeofenceInfo {


    private String latitude;
    private String longitude;
    private String radius;
    private String id;

    public ModelsGeofenceInfo() {
    }

    public ModelsGeofenceInfo(String id, String latitude, String longitude, String radius) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.id = id;
    }


    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLatitudeDouble() {
        return Double.valueOf(latitude);
    }

    public double getLongitudeDouble() {
        return Double.valueOf(longitude);
    }

    public float getRadiusFloat() {
        return Float.valueOf(radius);
    }
}
