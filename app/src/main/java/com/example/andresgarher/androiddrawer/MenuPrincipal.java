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
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Activity que contiene todas las funcionalidades principales de la aplicacion.
 */

public class MenuPrincipal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActionBarDrawerToggle toggle;
    private TextView tvtot,tvpog,tvmsj;
    private String[] ordenesTotal;

    public ListView lista_progreso;
    public ArrayAdapter<String> ad;
    protected ProgressBar barraprogreso;
    public String[]optativo;
    private final int interval = 3000; // 1 Second
    private Handler handler = new Handler();
    private Runnable runnable;
    private String mesaN="";
    private boolean unaVez=true;
    private int factor=0;

    /**
     *Constructor de la activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        ordenesTotal=new String[0];

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView NavigationView = (NavigationView) findViewById(R.id.NavigationView);
        NavigationView.setNavigationItemSelectedListener(this);
        barraprogreso = (ProgressBar)findViewById(R.id.barraprogreso);
        barraprogreso.setVisibility(View.GONE);
        lista_progreso=(ListView)findViewById(R.id.lista_progreso);;
        lista_progreso.setVisibility(View.GONE);
        tvtot=(TextView)findViewById(R.id.tvtot);
        tvpog=(TextView)findViewById(R.id.tvpog);
        tvmsj=(TextView)findViewById(R.id.tvmsj);
        tvpog.setTextColor(Color.BLACK);
        tvtot.setTextColor(Color.BLACK);
        tvtot.setVisibility(View.GONE);
        tvpog.setVisibility(View.GONE);
        tvmsj.setVisibility(View.GONE);

        Bundle ext=getIntent().getExtras();
        if(ext!=null){
            mesaN=ext.getString("numeromesa");
        }
        runnable= new Runnable(){
            public void run() {
                try {
                    new GetMesaInfo().execute(new URL("http://192.168.43.116:9080/Proyecto2/central/cliente/progreso"));
                    handler.postDelayed(runnable,interval);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }


    /**
     * Metodo que realiza distintas acciones dependiendo del resultado devuelto por una activity.
     * @param requestCode codigo unico de cada activity
     * @param resultCode codigo que indica si el proceso termino correctamente
     * @param data informacion devuelta por una activity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (710): {
                if (resultCode == Activity.RESULT_OK) {

                    if(ordenesTotal.length==0){
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
                    if(barraprogreso.getProgress()>=100){
                        barraprogreso.setProgress(0);
                        //handler.post(runnable);
                    }
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
                    tvmsj.setVisibility(View.GONE);
                }
            break;}
        }
    }

    /**
     *Metodo encargado de unir los elementos de dos arreglos en un arreglo.
     * @param first primer arreglo a unir
     * @param second segundo arreglo a unir
     * @return un arreglo con la informacion de ambos arreglos
     */
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

    /**
     *Metodo que realiza una accion al oprimir una opcion(item) del menu
     * @param item opcion seleccionada
     * @return accion relacionada al item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);

    }

    /**
     * Metodo encargado de realizar acciones dependiendo del item seleccionado en el menu de navegacion
     * @param item opcion seleccionada en el menu
     * @return accion relacionada al item.
     */
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_menu_1) {
            factor=ordenesTotal.length;
            tvmsj.setVisibility(View.GONE);
            Intent intento=new Intent(this, MenuRest.class);
            int i=1;
            Bundle extras =getIntent().getExtras();
            if(extras!=null){
                i=extras.getInt("categoria");
            }
            intento.putExtra("categoria",i);
            intento.putExtra("numeromesa",mesaN);
            startActivityForResult(intento,710);


        }else if (id == R.id.nav_menu_3) {
            //handler.removeCallbacks(runnable);
            handler.post(runnable);
            Toast.makeText(getBaseContext(), "Ver barra de progreso", Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_menu_4) {
            handler.removeCallbacks(runnable);
            barraprogreso.setProgress(0);
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

    /**
     *Clase encargada de obtener la informacion relacionada con el numero de mesa registrado del servidor.
     */

    public class GetMesaInfo extends AsyncTask<URL, Void, List<String>> {

            @Override
            protected List<String> doInBackground(URL... urls) {
                // Obtener la conexión
                HttpURLConnection con = null;

                try {

                    con = (HttpURLConnection) urls[0].openConnection();

                    // Activar método POST
                    con.setDoOutput(true);

                    con.setFixedLengthStreamingMode(mesaN.getBytes().length);
                    con.setRequestProperty("Content-Type", "application/json");

                    OutputStream out = new BufferedOutputStream(con.getOutputStream());

                    out.write(mesaN.getBytes());
                    out.flush();
                    out.close();


                } catch (IOException e) {
                    e.printStackTrace();
                }

                List<String> msj = null;

                try {
                    //con = (HttpURLConnection) urls[0].openConnection();
                    try {
                        InputStream in = new BufferedInputStream(con.getInputStream());
                        InputStreamReader j = new InputStreamReader(in, "UTF-8");
                        LectorJSON parser = new LectorJSON();

                        msj = parser.readJsonStream(in);

                    } finally {
                        con.disconnect();
                    }
                } catch (Exception e) {


                }
                return msj;
            }

        /**
         *  Metodo que muestra el progreso de una orden en tiempo real.
         * @param coleccion lista enlazada con la informacion necesaria para procesar y comparar.
         */
        @Override
        protected void onPostExecute(List<String> coleccion){
            int f=Integer.parseInt(coleccion.get(0));
            if(f!=0){
                f--;
            }else{
                if(factor==ordenesTotal.length){
                    barraprogreso.setProgress(barraprogreso.getProgress()+100/ordenesTotal.length);
                }
            }
            if(barraprogreso.getProgress()==0){
                int tempF=factor;
                while(f<(f+factor)){
                barraprogreso.setProgress(barraprogreso.getProgress()+100/ordenesTotal.length);
                factor--;}
                factor=tempF;
            }
            if(ordenesTotal.length>0 && barraprogreso.getProgress()<100 && (f+factor)<ordenesTotal.length){
                barraprogreso.setProgress(barraprogreso.getProgress()+100/ordenesTotal.length);
                factor++;

            }else if(barraprogreso.getProgress()>=100){
                tvmsj.setVisibility(View.VISIBLE);
                handler.removeCallbacks(runnable);
            }
        }
    }

}

