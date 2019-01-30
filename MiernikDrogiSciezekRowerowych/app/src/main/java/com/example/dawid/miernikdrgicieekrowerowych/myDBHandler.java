package com.example.dawid.miernikdrgicieekrowerowych;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

 public class myDBHandler extends SQLiteOpenHelper {

     private static final int DATABASE_VERSION = 1;
     private static final String DATABASE_NAME = "MyDatabase.db";
     public static final String TABLE_NAME = "Pomiary";
     public static final String COLUMN_ID = "ID";
     public static final String DATE = "Date";
     public static final String MEASURE_LATITUDE = "Latitude";
     public static final String MEASURE_LONGITUDE = "Longitude";
     public static final String MEASURE_ALTITUDE = "Altitude";
     public static final String GPS_PROVIDER = "GPS_provider";
     public static final String QUALITY_PATH = "Quality";


     public myDBHandler(Context context, String name,
                        SQLiteDatabase.CursorFactory factory, int version) {
         super(context, DATABASE_NAME, factory, DATABASE_VERSION);
     }

     public void onCreate(SQLiteDatabase db) {
         String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID +
                 "  INTEGER PRIMARY KEY AUTOINCREMENT, " + DATE + " TEXT, " +
                 MEASURE_LATITUDE + " TEXT, " + MEASURE_LONGITUDE + " TEXT, " +
                 MEASURE_ALTITUDE + " TEXT, " + GPS_PROVIDER + " TEXT, " +
                 QUALITY_PATH + " TEXT )";
         db.execSQL(CREATE_TABLE);
     }

     @Override
     public void onUpgrade(SQLiteDatabase db, int i, int i1) {
     }



     public DataCollect getdatafromID(int id) {
         String query = "Select *FROM " + TABLE_NAME;
         SQLiteDatabase db = this.getWritableDatabase();
         Cursor cursor = db.rawQuery(query, null);

         while (cursor.moveToNext()) {
             int resultID = Integer.valueOf(cursor.getString(0));
             String result_1 = cursor.getString(1);
             String result_2 = cursor.getString(2);
             String result_3 = cursor.getString(3);
             String result_4 = cursor.getString(4);
             String result_5 = cursor.getString(5);
             String result_6 = cursor.getString(6);

             if (resultID == id)
                 return new DataCollect(resultID,result_1,result_2,result_3,result_4,result_5,result_6);
         }
         cursor.close();
         db.close();
         return null;
     }


     public void addHandler(DataCollect myData) {
         ContentValues values = new ContentValues();
         values.put(COLUMN_ID, getLastID() + 1);
         values.put(DATE, myData.getDate());
         values.put(MEASURE_LATITUDE, myData.getLatitude());
         values.put(MEASURE_LONGITUDE,myData.getLongitude());
         values.put(MEASURE_ALTITUDE, myData.getAltitude());
         values.put(GPS_PROVIDER, myData.getGps_accuracy());
         values.put(QUALITY_PATH, myData.getQuality());
         SQLiteDatabase db = this.getWritableDatabase();
         db.insert(TABLE_NAME, null, values);
         db.close();
     }

     public Integer getLastID() {
         int last = 0;
         String query = "Select *FROM " + TABLE_NAME;
         SQLiteDatabase db = this.getWritableDatabase();
         Cursor cursor = db.rawQuery(query, null);
         while (cursor.moveToNext()) {
             last = cursor.getInt(0);
         }
         cursor.close();
         db.close();
         return last;
     }
 }
