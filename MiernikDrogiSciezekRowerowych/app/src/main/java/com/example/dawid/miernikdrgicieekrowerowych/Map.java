package com.example.dawid.miernikdrgicieekrowerowych;


import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;
import java.util.List;

public class Map extends  FragmentActivity implements  OnMapReadyCallback{

    myDBHandler dbHandler;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        double x = 52.3753412272257;  // wspolrzedne miasta Poznań
        double y = 16.9629862846828;

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(x,y)));


        dbHandler = new myDBHandler(this, null, null, 1);
        DataCollect myData;
        List<LatLng> currentSegment = new ArrayList<>();
        int lastparameter = -1;
        int parametr = 0;

        for (int i =1 ; i <dbHandler.getLastID();   i++) {
            myData = dbHandler.getdatafromID(i);
            parametr = Integer.valueOf(myData.getQuality());

                if (parametr == lastparameter) {
                    currentSegment.add(new LatLng(Double.parseDouble(myData.getLatitude()),
                            Double.parseDouble(myData.getLongitude())));
                } else {
                    currentSegment.add(new LatLng(Double.parseDouble(myData.getLatitude()),
                            Double.parseDouble(myData.getLongitude())));

                   mMap.addPolyline(new PolylineOptions()
                            .addAll(currentSegment)
                            .color(getColorfromparameter(parametr))
                            .width(10));
                    lastparameter = parametr;
                    currentSegment.clear();
                    currentSegment.add(new LatLng(Double.parseDouble(myData.getLatitude()),
                            Double.parseDouble(myData.getLongitude())));
                }
            }
            mMap.addPolyline(new PolylineOptions()
                    .addAll(currentSegment)
                    .color(getColorfromparameter(parametr))
                    .width(10));
        }


        public int getColorfromparameter(int parametr) {
        int color = Color.TRANSPARENT;
        if (parametr < 2) {
            color = Color.GREEN;
        } else if (parametr <4) {
            color = Color.rgb(255,255,0);
        } else if (parametr < 5) {
            color = Color.rgb(128,128,0);
        } else {
            color = Color.RED;
        }
        return color;
    }

}


