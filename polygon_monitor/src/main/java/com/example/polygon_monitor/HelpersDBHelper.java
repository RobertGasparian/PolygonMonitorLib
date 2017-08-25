package com.example.polygon_monitor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 8/11/2017.
 */

 class HelpersDBHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "geofenceManager";
    private static final String TABLE_GEOFENCES = "geofences";
    private static final String TABLE_QUEUE = "queue";
    private static final String TABLE_POLYGON = "polygon";

    private static final String _ID = "id";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String RADIUS = "radius";
    private static final String ACTION = "action";
    private static final String GEOFENCE_ID = "geofence_id";
    private static final String JSON_STRING = "JSON";
    private static final String CLASSIFY = "classify";
    private static final String QUERY_MARK = "=?";


    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String CREATE_TABLE = "CREATE TABLE ";
    private static final String PRIMARY_KEY_INT = " INTEGER PRIMARY KEY AUTOINCREMENT,";
    private static final String DROP_TABLE_EXISTS = "DROP TABLE IF EXISTS ";
    private static final String SELECT_FROM_ALL = "SELECT  * FROM ";


    public HelpersDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_GEOFENCES_TABLE = CREATE_TABLE + TABLE_GEOFENCES +
                "(" + _ID + TEXT_TYPE + ","
                + LATITUDE + TEXT_TYPE + ","
                + LONGITUDE + TEXT_TYPE + ","
                + RADIUS + TEXT_TYPE + ")";
        String CREATE_QUEUE_TABLE = CREATE_TABLE + TABLE_QUEUE +
                "(" + _ID + PRIMARY_KEY_INT
                + ACTION + TEXT_TYPE + ","
                + GEOFENCE_ID + TEXT_TYPE + ","
                + JSON_STRING + TEXT_TYPE + ")";
        String CREATE_POLYGON_TABLE = CREATE_TABLE + TABLE_POLYGON +
                "(" + GEOFENCE_ID + TEXT_TYPE + ","
                + LATITUDE + TEXT_TYPE + ","
                + LONGITUDE + TEXT_TYPE + ","
                + CLASSIFY + INTEGER_TYPE + ")";
        Log.d(PolygonMonitorController.POLYGON_MONITOR_TAG,CREATE_POLYGON_TABLE);


        db.execSQL(CREATE_GEOFENCES_TABLE);
        db.execSQL(CREATE_QUEUE_TABLE);
        db.execSQL(CREATE_POLYGON_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        db.execSQL(DROP_TABLE_EXISTS + TABLE_GEOFENCES);
        db.execSQL(DROP_TABLE_EXISTS + TABLE_QUEUE);
        db.execSQL(DROP_TABLE_EXISTS + TABLE_POLYGON);
        onCreate(db);
    }

    public void addGeofenceInfo(ModelsGeofenceInfo geofenceInfo) {

        SQLiteDatabase db = this.getWritableDatabase();
        if (!checkIdExistence(geofenceInfo.getId())) {
            ContentValues values = new ContentValues();

            values.put(_ID, geofenceInfo.getId());
            values.put(LATITUDE, geofenceInfo.getLatitude());
            values.put(LONGITUDE, geofenceInfo.getLongitude());
            values.put(RADIUS, geofenceInfo.getRadius());
            long inserted = db.insert(TABLE_GEOFENCES, null, values);
            Log.d(PolygonMonitorController.POLYGON_MONITOR_TAG, String.valueOf(inserted));
            db.close();
        } else {
            updateGeofence(geofenceInfo);
        }
    }

    public void updateGeofence(ModelsGeofenceInfo geofenceInfo) {


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(_ID, geofenceInfo.getId());
        values.put(LATITUDE, geofenceInfo.getLatitude());
        values.put(LONGITUDE, geofenceInfo.getLongitude());
        values.put(RADIUS, geofenceInfo.getRadius());

        db.update(TABLE_GEOFENCES, values, _ID + QUERY_MARK, new String[]{String.valueOf(geofenceInfo.getId())});

    }

    private boolean checkIdExistence(String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_QUEUE, new String[]{_ID}, _ID + QUERY_MARK, new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToNext()) {
            cursor.close();

            return true;
        } else {
            cursor.close();

            return false;
        }


    }

    public void deleteGeofenceInfo(String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GEOFENCES, _ID + QUERY_MARK, new String[]{id});
        db.close();

    }

    public List<ModelsGeofenceInfo> getAllGeofences() {

        List<ModelsGeofenceInfo> geofenceInfos = new ArrayList<>();
        String selectQuery = SELECT_FROM_ALL + TABLE_GEOFENCES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            ModelsGeofenceInfo geofenceInfo = new ModelsGeofenceInfo(cursor.getString(cursor.getColumnIndex(HelpersDBHelper._ID)),
                    cursor.getString(cursor.getColumnIndex(HelpersDBHelper.LATITUDE)),
                    cursor.getString(cursor.getColumnIndex(HelpersDBHelper.LONGITUDE)),
                    cursor.getString(cursor.getColumnIndex(HelpersDBHelper.RADIUS)));
            geofenceInfos.add(geofenceInfo);

        }
        cursor.close();

        return geofenceInfos;

    }

    public void addQueue(ModelsResponseQueue responseQueue) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(GEOFENCE_ID, responseQueue.getGeoId());
        values.put(ACTION, responseQueue.getAction());
        values.put(JSON_STRING, responseQueue.getJSON());
        db.insert(TABLE_QUEUE, null, values);
        db.close();

    }

    public List<ModelsResponseQueue> getAllQueues() {

        List<ModelsResponseQueue> responseQueues = new ArrayList<>();
        String selectQuery = SELECT_FROM_ALL + TABLE_QUEUE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            ModelsResponseQueue responseQueue = new ModelsResponseQueue(cursor.getInt(cursor.getColumnIndex(HelpersDBHelper._ID)),
                    cursor.getString(cursor.getColumnIndex(HelpersDBHelper.GEOFENCE_ID)),
                    cursor.getInt(cursor.getColumnIndex(HelpersDBHelper.ACTION)),
                    cursor.getString(cursor.getColumnIndex(HelpersDBHelper.JSON_STRING)));
            responseQueues.add(responseQueue);

        }
        cursor.close();

        return responseQueues;

    }

    public void deleteQueue(String id) {


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_QUEUE, new String[]{_ID, GEOFENCE_ID, ACTION, JSON_STRING}, GEOFENCE_ID + QUERY_MARK, new String[]{String.valueOf(id)}, null, null, null);
        cursor.moveToFirst();
        int deleting_id = cursor.getInt(cursor.getColumnIndex(HelpersDBHelper._ID));
        cursor.close();
        db.delete(TABLE_QUEUE, _ID + QUERY_MARK, new String[]{String.valueOf(deleting_id)});
        db.close();
    }

    public void addPolygonVertex(LatLng latLng, String geoId, int order) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(GEOFENCE_ID, geoId);
        values.put(LATITUDE, String.valueOf(latLng.latitude));
        values.put(LONGITUDE, String.valueOf(latLng.longitude));
        values.put(CLASSIFY, order);
        db.insert(TABLE_POLYGON, null, values);
        db.close();
    }

    public void deletePolygon(String geoId){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_POLYGON, GEOFENCE_ID + QUERY_MARK, new String[]{geoId});
        db.close();
    }

    public List<LatLng> getPolygon(String geoId){

        List<LatLng> polygons = new ArrayList<>();
        List<ModelsPolygonVertex> withOrders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_POLYGON, new String[]{LATITUDE, LONGITUDE, CLASSIFY}, GEOFENCE_ID + QUERY_MARK, new String[]{geoId}, null, null, null);
        while (cursor.moveToNext()){
            ModelsPolygonVertex polygonVertex = new ModelsPolygonVertex(
                    cursor.getString(cursor.getColumnIndex(HelpersDBHelper.LATITUDE)),
                    cursor.getString(cursor.getColumnIndex(HelpersDBHelper.LONGITUDE)),
                    cursor.getInt(cursor.getColumnIndex(HelpersDBHelper.CLASSIFY)));
            withOrders.add(polygonVertex);
        }

        for (int i = 0; i < withOrders.size(); i++) {
            for (int j = 0; j < withOrders.size(); j++) {
                if(withOrders.get(j).getOrder()==i){
                    LatLng latLng = new LatLng(Double.valueOf(withOrders.get(j).getLatitude()),Double.valueOf(withOrders.get(j).getLongitude()));
                    polygons.add(latLng);
                }
            }
        }

        return  polygons;
    }

}
