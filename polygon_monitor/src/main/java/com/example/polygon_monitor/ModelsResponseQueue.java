package com.example.polygon_monitor;

/**
 * Created by User on 8/15/2017.
 */

 class ModelsResponseQueue {

    private String geoId;
    private int queueId;
    private int action;
    private String JSON = null;


    public ModelsResponseQueue(int queueId, String geoId, int action, String JSON) {
        this.geoId = geoId;
        this.queueId = queueId;
        this.action = action;
        this.JSON = JSON;
    }


    public ModelsResponseQueue(String geoId, int action, String JSON) {
        this.geoId = geoId;
        this.action = action;
        this.JSON = JSON;
    }

    public String getGeoId() {
        return geoId;
    }

    public void setGeoId(String geoId) {
        this.geoId = geoId;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getJSON() {
        return JSON;
    }

    public void setJSON(String JSON) {
        this.JSON = JSON;
    }

    public int getQueueId() {
        return queueId;
    }

    public void setId(int queueId) {
        this.queueId = queueId;
    }
}
