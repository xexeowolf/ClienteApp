package com.example.andresgarher.androiddrawer;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class Ensaladas extends AppCompatActivity {

    private ListView lvBebidas;

    List<String> datosBebidas;
    String[] temporal;
    String[] eleccion;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bebidas);

        lvBebidas = (ListView) findViewById(R.id.listView2);

        Bundle extras =getIntent().getExtras();
        if(extras!=null){
            temporal=(String[])extras.get("lista");
            eleccion=new String[temporal.length];
            for(int g=0;g<temporal.length;g++){
                String[] subtemporal=temporal[g].split("jk");
                eleccion[g]=subtemporal[0]+" "+subtemporal[1];
                String complete="";
                for(int q=0;q<subtemporal.length;q++){
                    complete=complete+subtemporal[q]+"\n";
                }
                temporal[g]=complete;
            }
        }


        datosBebidas= new ArrayList<String>(asList(temporal));

        ArrayAdapter<String> adaptadorBebi = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datosBebidas);
        lvBebidas.setAdapter(adaptadorBebi);

        lvBebidas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int posicion, long id) {
                Intent result = new Intent();
                result.putExtra("parametro", String.valueOf(eleccion[posicion]));
                setResult(Activity.RESULT_OK, result);
                finish();
            }});
    }
}

