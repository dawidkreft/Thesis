package com.example.dawid.miernikdrgicieekrowerowych;

public class DataCollect {

    int ID;
    String date;
    String latitude;
    String longitude;
    String altitude;
    String gps_accuracy;
    String quality;

    public DataCollect(int ID, String date, String latitude, String longitude, String altitude, String gps_accuracy, String quality) {
        this.ID = ID;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.gps_accuracy = gps_accuracy;
        this.quality = quality;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getGps_accuracy() {
        return gps_accuracy;
    }

    public void setGps_accuracy(String gps_accuracy) {
        this.gps_accuracy = gps_accuracy;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }


    @Override
    public String toString() {
        String result = "ID: " + ID + ",data: " + date + " ,wspolrz: " + latitude + "x" + longitude +
                " ,gpsAccu: " + gps_accuracy + ",q: " + quality;
        return result;
    }
}




