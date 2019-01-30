package com.example.dawid.miernikdrgicieekrowerowych;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button btnStart, btnStop, btnShowMap, btnShowData;
    TextView txtBluetoothStatus, txtGPSStatus;
    ListView listOfDevice;

    // ----------Elementy Bluetooth ----
    BluetoothAdapter myBluetoothAdapter;
    BluetoothDevice btArray[];
    Receive myReceive;
    static final int STATE_CONNECTING = 2;
    static final int CONNECTED = 3;
    static final int CONNECTION_FAILED = 4;
    static final int STATE_MESSEGE_RECEIVED = 5;
    static final int REQUEST_ENABLE_BT = 1;

    //Database
    myDBHandler dbHandler;

    // GPS elements
    private LocationListener locationListener;
    private LocationManager locationManager;
    Location actuallocation;
    Criteria criteria;
    String thebestProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnShowData = (Button) findViewById(R.id.btnShowData);
        btnShowMap = (Button) findViewById(R.id.btnShowMap);
        listOfDevice = (ListView) findViewById(R.id.deviceList);
        txtBluetoothStatus = (TextView) findViewById(R.id.txtVstatusBlu);
        txtGPSStatus = (TextView) findViewById(R.id.txtVstatusGPS);

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        dbHandler = new myDBHandler(this, null, null, 1);
        criteria = new Criteria();

        btnStartMethod();
        btnStopMethod();
        btnShowDataMethod();
        ListDeviceClicMethod();
        if (isServiceMapsOK())
            btnMapMethod();

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                actuallocation = location;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, locationListener);
        }
    }

    private void btnShowDataMethod() {
        btnShowData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Baza.class);
                startActivity(intent);
            }
        });
    }

    private void ListDeviceClicMethod() {
        listOfDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClientClass clientClass = new ClientClass(btArray[i]);
                clientClass.start();
                txtBluetoothStatus.setText("Łączenie z urządzeniem");
                txtBluetoothStatus.setText("Połączono z: " + btArray[i].getName() + "  " + btArray[i].getAddress());
            }
        });
    }


    private void btnStopMethod() {

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myBluetoothAdapter.isEnabled()) {
                    myBluetoothAdapter.disable();
                    txtBluetoothStatus.setText("Bluetooth wylaczone");
                }

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {

            if (resultCode == RESULT_OK) {
                txtBluetoothStatus.setText("Bluetooth wlączone");

            } else if (resultCode == RESULT_CANCELED) {
                txtBluetoothStatus.setText("Bluetooth nie wlączone");
            }
        }
    }


    private void btnStartMethod() {
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myBluetoothAdapter == null) {
                        txtBluetoothStatus.setText("Urządzenie nie wspiera Bluetooth");
                    } else {
                        if (!myBluetoothAdapter.isEnabled()) {
                            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); // intent wlaczajacy Bluetooth
                            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);

                        }
                }
                showBluetoothMethod();

                thebestProvider = locationManager.getBestProvider(criteria, true);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
                actuallocation = locationManager.getLastKnownLocation(thebestProvider);

                if (actuallocation != null) {
                    txtGPSStatus.setText("GPS działa i jest widoczny");

                } else {
                    txtGPSStatus.setText(" GPS nie działa");
                }
            }

        });
    }

    private void showBluetoothMethod() {
        Set<BluetoothDevice> bt = myBluetoothAdapter.getBondedDevices();
        btArray = new BluetoothDevice[bt.size()];
        String[] strings = new String[bt.size()];
        int index = 0;
        if (bt.size() > 0) {
            for (BluetoothDevice device : bt) {
                btArray[index] = device;
                strings[index] = device.getName();
                index++;
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, strings);
            listOfDevice.setAdapter(adapter);
        }
    }


    Handler handler;
    {
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case STATE_CONNECTING:
                        txtBluetoothStatus.setText("Łączenie");
                        break;
                    case CONNECTED:
                        txtBluetoothStatus.setText("Połączony poprawnie");
                        break;
                    case CONNECTION_FAILED:
                        txtBluetoothStatus.setText("Błąd połaczenia");
                        break;
                    case STATE_MESSEGE_RECEIVED:

                        byte[] readbuf = (byte[]) message.obj;
                        try {
                            String tempMag = new String(readbuf, 0, message.arg1); // konwersja symboli pobranych z bluetooth
                            int myCollectData = Integer.valueOf(tempMag);
                            txtBluetoothStatus.setText("Nasłuchiwanie Bluetooth: " + myCollectData); // zmiana statusu Bluetooth
                            int tempID = 0;  //  zmienna zapamietujaca ostatnie ID z bazy danych

                            if (tempID == -2) {  // jesli tempID jest wieksze od -1 to nie powtarzaj metody getlastID()
                                tempID = 0;
                            } else if (tempID < 0) {
                                tempID = dbHandler.getLastID();  // sprawdzanie liczby danych w bazie i pobranie ostatniego ID do zapisu
                            }
                            if (ActivityCompat.checkSelfPermission(MainActivity.this,
                                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                    ActivityCompat.checkSelfPermission(MainActivity.this,
                                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            }
                            actuallocation = locationManager.getLastKnownLocation(thebestProvider);  // pobranie ostatniej znanej lokalizacji
                            tempID++;
                            Date dateNow = new Date(); // pobranie aktualnej daty
                            DataCollect myData = new DataCollect(tempID, dateNow.toString(), String.valueOf(actuallocation.getLatitude()), // utworzenie obiektu klasy DataCollect
                                    String.valueOf(actuallocation.getLongitude()), String.valueOf(actuallocation.getAltitude()),
                                    String.valueOf(actuallocation.getAccuracy()), String.valueOf(myCollectData));  //zapisanie danych w postaci lańcuchów danych

                            dbHandler.addHandler(myData);              // Dodawanie do bazy


                        } catch (Exception e) {
                        }
                }
                return false;
            }
        });
    }

    private  void btnMapMethod() {
            btnShowMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, Map.class);
                    startActivity(intent);
                }
            });
    }

    public  boolean isServiceMapsOK(){
        int availble = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(availble == ConnectionResult.SUCCESS){
            // wszystko jest ok , mapa dziala
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(availble)){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this , availble , 9001);
            dialog.show();
        }else{
            Toast.makeText(MainActivity.this, "Mapa nie zadziala", Toast.LENGTH_SHORT).show();
        }
        return false ;
    }


 //    Klasy obsługujące Bluetooth
    class ClientClass extends Thread {

        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass(BluetoothDevice device1) {   // Wywołanie konstruktora
            UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // nadanie unikalnego identyfikatora
            BluetoothSocket tmp = null;
            device = device1;
            try {
                tmp = device.createRfcommSocketToServiceRecord(myUUID); // próba utworzenia połączenia
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = tmp;
        }

        public void run() {
           myBluetoothAdapter.cancelDiscovery(); // zatrzymanie wyszukiwania urządzień

            try {
                socket.connect();
                Message message = Message.obtain();
                message.what = CONNECTED;   // połaczenie z urządzeń i nadanie informacji do chwytaka
                handler.sendMessage(message);
                myReceive = new Receive(socket);   // utworenie obiektu odbiornika, ktory nasluchuje dane
                myReceive.start();
            } catch (IOException e) {
                e.printStackTrace();   // wysłanie informacji do chwytaka o niepowodzeniu
                Message message = Message.obtain();
                message.what = CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    // -------- Klasa odbiornika -----------------
    private class Receive extends Thread    {

        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;

        public Receive(BluetoothSocket socket){
            bluetoothSocket = socket;
            InputStream tempIn = null;

            try{
                tempIn = bluetoothSocket.getInputStream(); // Utworzenie strumienia wejścia danych
            }catch (IOException e){
                e.printStackTrace();

            }
            inputStream = tempIn;
        }

        public void run(){          // funkcja odczytająca dane
            byte[] buffer = new byte[1024];
            int bytes;
            while(true){
                try{
                    bytes= inputStream.read(buffer);  // funkcja przekazuje dane do buffer a zwraca rozmiar danych
                    handler.obtainMessage(STATE_MESSEGE_RECEIVED,bytes, -1,buffer).sendToTarget(); // wyslanie danych
                }catch (IOException e){                                                                 //  i komunikatu do chwytaka
                    e.printStackTrace();
                }
            }
        }
    }
}
