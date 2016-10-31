package com.example.andresgarher.androiddrawer;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Activity que muestra la informacion almacenada en el servidor con respecto a sopas.
 */
public class Sopas extends AppCompatActivity {

    private ListView lvSopas;

    List<String> datosSopas;
    String[] temporal;
    String[]eleccion;

    /**
     * Constructor de la clase.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sopas);

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

        datosSopas=new ArrayList<String>(asList(temporal));

        lvSopas = (ListView) findViewById(R.id.listView3);

        ArrayAdapter<String> adaptadorBebi = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datosSopas);
        lvSopas.setAdapter(adaptadorBebi);

        lvSopas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int posicion, long id) {
                //int pos=lvSopas.getCheckedItemPosition();
                Intent result = new Intent();
                result.putExtra("parametro", String.valueOf(eleccion[posicion]));
                setResult(Activity.RESULT_OK, result);
                finish();
            }});

    }
}
