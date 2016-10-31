package com.example.andresgarher.androiddrawer;

/**
 * Created by andres on 27/10/16.
 * Clase que muestra los tramites para cancelar una cuenta pendiente.
 */


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;



public class PagarCuenta extends AppCompatActivity {

    String[] entrada;

    List<Integer> precio=new ArrayList<Integer>();

    List<String> pedidos= new ArrayList<String>();

    List<String> cuentas = new ArrayList<String>();

    List<String> tmp = new ArrayList<String>();

    private String cuenta = "";
    private TextView tv1;
    private ListView lv1, lv2, lv3;
    private int remove;
    private int cuent = 1;
    private int montoCuenta = 0;

    /**
     * Constructor de la activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagar_cuenta);

        Bundle extras=getIntent().getExtras();
        if(extras!=null) {
            entrada = (String[])extras.get("ordenes");
            if(entrada.length>0){

            for(int gh=0;gh<entrada.length;gh++){
                String ordenEn=entrada[gh];
                String[] caract=ordenEn.split("jk");
                pedidos.add((caract[0] + "\n                       ₡"+caract[1]));
                precio.add(Integer.parseInt(caract[1]));
            }}
            else{
                Toast.makeText(getBaseContext(), "No tiene ordenes pendientes por cobrar", Toast.LENGTH_LONG).show();
                finish();
            }

        }

        tv1=(TextView)findViewById(R.id.textView);

        lv1 =(ListView)findViewById(R.id.lv1);
        final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, pedidos);
        lv1.setAdapter(adapter1);

        lv2 = (ListView)findViewById(R.id.lv2);
        final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, tmp);
        lv2.setAdapter(adapter2);

        lv3 = (ListView)findViewById(R.id.lv3);
        final ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, cuentas);
        lv3.setAdapter(adapter3);
        lv3.setVisibility(View.GONE);

        final Button generar = (Button) findViewById(R.id.generar);
        final Button nueva = (Button) findViewById(R.id.nueva);
        nueva.setVisibility(View.GONE);
        final Button terminar = (Button) findViewById(R.id.terminar);

        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int posicion, long id) {
                remove = (int) lv1.getItemIdAtPosition(posicion);

                tmp.add(pedidos.get(remove));
                cuenta += "\n" + pedidos.get(remove);
                montoCuenta += precio.get(remove);
                precio.remove(remove);
                pedidos.remove(remove);


                adapter1.notifyDataSetChanged();
                adapter2.notifyDataSetChanged();
            }
        });


        generar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (tmp.size() != 0){
                    cuenta = "Cuenta" + cuent + ":" + "                  ₡" + montoCuenta + cuenta;
                    cuentas.add(cuenta);
                    cuent += 1;
                    montoCuenta = 0;
                    cuenta = "";
                    tmp.clear();
                    adapter2.notifyDataSetChanged();
                }


                lv2.setVisibility(View.GONE);
                generar.setVisibility(View.GONE);

                lv3.setVisibility(View.VISIBLE);
                nueva.setVisibility(View.VISIBLE);

                adapter3.notifyDataSetChanged();
                if(pedidos.size()==0){
                    generar.setVisibility(View.GONE);
                }
            }
        });

        nueva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(pedidos.size()>0){
                    lv1.setVisibility(View.VISIBLE);

                    lv3.setVisibility(View.GONE);
                    nueva.setVisibility(View.GONE);

                    lv2.setVisibility(View.VISIBLE);
                    generar.setVisibility(View.VISIBLE);
                    if(pedidos.size()==0){
                        generar.setVisibility(View.GONE);
                    }

                }else{
                    Toast.makeText(getBaseContext(), "No tiene mas ordenes pendientes", Toast.LENGTH_LONG).show();
                }
            }
        });

        terminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Cuenta Cancelada", Toast.LENGTH_LONG).show();
                Intent alfin=new Intent();
                setResult(Activity.RESULT_OK, alfin);
                finish();
            }
        });
    }


}