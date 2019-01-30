package com.example.dawid.miernikdrgicieekrowerowych;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Baza extends Activity {

    TextView myText;
    myDBHandler dbHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baza);
        myText = (TextView) findViewById(R.id.myTextViewu);
        dbHandler = new myDBHandler(this, null, null, 1);
        int size = dbHandler.getLastID();

        if(size < 1){
            myText.setText("\n \n \n \n            Baza danych jest pusta ");
        }else {
            DataCollect[] myData = new DataCollect[size];
            String texxt = "";
            for (int i = 1; i < dbHandler.getLastID(); i++) {
                myData[i] = dbHandler.getdatafromID(i);
               texxt = texxt+ "\n" +  myData[i].toString();
            }
            myText.setText(texxt);
        }
    }
}
