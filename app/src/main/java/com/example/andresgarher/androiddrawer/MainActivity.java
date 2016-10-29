package com.example.andresgarher.androiddrawer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Movie;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button botonscan;
    private EditText numeromesa;
    private String guardarnumeromesa;
    private TextView EtiquetaEscaneo,textocategoria,textonumero,textoprimir;
    private Button BotonAvanzar;
    private Spinner Categoria;
    private String[] datosCate= {"Oro", "Plata", "Bronce"};
    int cate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_inicio);
        Categoria = (Spinner)findViewById(R.id.spinner3);

        ArrayAdapter<String> adaptadorNatu = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datosCate);
        Categoria.setAdapter(adaptadorNatu);
        Categoria.setBackgroundColor(Color.LTGRAY);


        textocategoria=(TextView)findViewById(R.id.textoCategoria);
        textonumero=(TextView)findViewById(R.id.EtiquetaNumeroMesa);
        textoprimir=(TextView)findViewById(R.id.TituloScan);
        textocategoria.setTextColor(Color.WHITE);
        textonumero.setTextColor(Color.WHITE);
        textoprimir.setTextColor(Color.WHITE);

        ImageView imageView = (ImageView) findViewById(R.id.imageView3);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);
        Glide.with(this).load(R.raw.bienvenido).into(imageViewTarget);

        Categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        cate =3;
                        break;
                    case 1:
                        cate = 2;
                        break;
                    case 2:
                        cate = 1;
                        break;
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        BotonAvanzar = (Button)findViewById(R.id.BotonAvanzarMenu);
        BotonAvanzar.setOnClickListener(this);
        botonscan = (Button) findViewById(R.id.BotonScaneo);
        final Activity activity = this;
        botonscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scanear codigo");//la etiqueta
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);//cuando captura el codigo suena un ruido
                integrator.setBarcodeImageEnabled(false);//ni idea
                integrator.initiateScan();

            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        numeromesa = (EditText) findViewById(R.id.NumeroMesa);
        numeromesa.setInputType(1);//0 nada,1 texto,2 numeros
        guardarnumeromesa = result.getContents().toString();
        numeromesa.setText(guardarnumeromesa);
        numeromesa.setInputType(0);
        EtiquetaEscaneo = (TextView) findViewById(R.id.TituloScan);
        EtiquetaEscaneo.setText("El codigo ha sido leido exitosamente");
        BotonAvanzar = (Button) findViewById(R.id.BotonAvanzarMenu);
        BotonAvanzar.setVisibility(View.VISIBLE);
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onClick(View v) {
        BotonAvanzar = (Button) findViewById(R.id.BotonAvanzarMenu);
        Intent intent = new Intent(this, MenuPrincipal.class);
        intent.putExtra("categoria", cate);
        startActivity(intent);
    }
}