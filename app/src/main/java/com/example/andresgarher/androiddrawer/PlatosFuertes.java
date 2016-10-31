package com.example.andresgarher.androiddrawer;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Activity que muestra la informacion almacenada en el servidor con respecto a platos fuertes.
 */
public class PlatosFuertes extends AppCompatActivity {


    private ListView lvPlatos;

    List<String> datosPlatos;
    String[] temporal;
    String[]eleccion;

    /**
     * Constructor de la activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platos_fuertes);

        lvPlatos = (ListView) findViewById(R.id.listView4);

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


        datosPlatos=new ArrayList<String>(asList(temporal));


        ArrayAdapter<String> adaptadorBebi = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datosPlatos);
        lvPlatos.setAdapter(adaptadorBebi);

        lvPlatos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int posicion, long id) {
                Intent result = new Intent();
                result.putExtra("parametro", String.valueOf(eleccion[posicion]));
                setResult(Activity.RESULT_OK, result);
                finish();
            }});



    }
}
