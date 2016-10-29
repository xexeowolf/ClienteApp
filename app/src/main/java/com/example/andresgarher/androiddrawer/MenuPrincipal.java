package com.example.andresgarher.androiddrawer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MenuPrincipal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActionBarDrawerToggle toggle;
    private TextView tvtot,tvpog;
    private String[] ordenesTotal;

    public ListView lista_progreso;
    public ArrayAdapter<String> ad;
    protected ProgressBar barraprogreso;
    public String[]optativo;
    private final int interval = 3000; // 1 Second
    private Handler handler = new Handler();
    private Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView NavigationView = (NavigationView) findViewById(R.id.NavigationView);
        NavigationView.setNavigationItemSelectedListener(this);
        barraprogreso = (ProgressBar)findViewById(R.id.barraprogreso);
        barraprogreso.setVisibility(View.GONE);
        lista_progreso=(ListView)findViewById(R.id.lista_progreso);;
        lista_progreso.setVisibility(View.GONE);
        tvtot=(TextView)findViewById(R.id.tvtot);
        tvpog=(TextView)findViewById(R.id.tvpog);
        tvpog.setTextColor(Color.BLACK);
        tvtot.setTextColor(Color.BLACK);
        tvtot.setVisibility(View.GONE);
        tvpog.setVisibility(View.GONE);
        runnable= new Runnable(){
            public void run() {
                try {
                    new GetMesaInfo().execute(new URL("http://192.168.1.62:9080/Proyecto2/central/cliente/progreso"));
                    //Toast.makeText(getBaseContext(), String.valueOf(contDS)+" segundos", Toast.LENGTH_LONG).show();
                    handler.postDelayed(runnable,interval);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        //handler.post(runnable);
    }


    public String[] unirArreglos(String[] first,String[] second){
        String[] ambos=new String[first.length+second.length];
        for(int i=0;i<first.length;i++){
            ambos[i]=first[i];
        }
        for(int j=0;j<second.length;j++){
            ambos[j+first.length]=second[j];
        }

        return ambos;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (710): {
                if (resultCode == Activity.RESULT_OK) {

                    if(ordenesTotal==null){
                        optativo=data.getStringArrayExtra("orden");
                        ordenesTotal=optativo.clone();
                        for(int u=0;u<optativo.length;u++){
                            String[] uperd=optativo[u].split("jk");
                            optativo[u]="Orden: "+uperd[0]+" Precio: ₡"+uperd[1];
                        }


                    }else{

                        ordenesTotal=unirArreglos(ordenesTotal,data.getStringArrayExtra("orden"));
                        String[] tempop=optativo;
                        optativo=data.getStringArrayExtra("orden");
                        for(int u=0;u<optativo.length;u++){
                            String[] up=optativo[u].split("jk");
                            optativo[u]="Orden: "+up[0]+" Precio: ₡"+up[1];
                        }
                        optativo=unirArreglos(optativo,tempop);

                    }
                    ad=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,optativo);
                    lista_progreso.setAdapter(ad);
                    lista_progreso.setVisibility(View.VISIBLE);
                    barraprogreso.setVisibility(View.VISIBLE);
                    tvtot.setVisibility(View.VISIBLE);
                    tvpog.setVisibility(View.VISIBLE);

                }
                break;
            }
            case 715:{
                if(resultCode==Activity.RESULT_OK){
                    optativo=new String[0];
                    ordenesTotal=new String[0];
                    ad=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,optativo);
                    lista_progreso.setAdapter(ad);
                    lista_progreso.setVisibility(View.GONE);
                    barraprogreso.setVisibility(View.GONE);
                    tvtot.setVisibility(View.GONE);
                    tvpog.setVisibility(View.GONE);
                }
            break;}
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);

    }

    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_menu_1) {
            Intent intento=new Intent(this, MenuRest.class);
            int i=1;
            Bundle extras =getIntent().getExtras();
            if(extras!=null){
                i=extras.getInt("categoria");
            }
            intento.putExtra("categoria",i);
            startActivityForResult(intento,710);


        }else if (id == R.id.nav_menu_3) {
            handler.removeCallbacks(runnable);
        } else if (id == R.id.nav_menu_4) {
            Intent intentoP=new Intent(this,PagarCuenta.class);
            if(ordenesTotal==null){
                ordenesTotal=new String[0];
            }
            intentoP.putExtra("ordenes",ordenesTotal);
            startActivityForResult(intentoP,715);

        } else if (id == R.id.nav_menu_5){
            startActivity(new Intent(this, Calificar.class));

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class GetMesaInfo extends AsyncTask<URL, Void, List<String>> {



        @Override
        protected List<String> doInBackground(URL... urls) {

            List<String> msj = null;

            try {
                HttpURLConnection urlConnection = (HttpURLConnection) urls[0].openConnection();
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    InputStreamReader j=new InputStreamReader(in,"UTF-8");
                    LectorJSON parser = new LectorJSON();

                    msj = parser.readJsonStream(in);

                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {


            }
            return msj;

        }

        @Override
        protected void onPostExecute(List<String> coleccion){
            //int f=Integer.parseInt(coleccion.get(0));
            Toast.makeText(getBaseContext(), String.valueOf(coleccion.size()), Toast.LENGTH_LONG).show();
            if(barraprogreso.getProgress()<100){
                barraprogreso.setProgress(barraprogreso.getProgress()+100/20);
            }else{
                handler.removeCallbacks(runnable);
            }
        }
    }

}

