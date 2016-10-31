package com.example.andresgarher.androiddrawer;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Clase encargada de obtener las elecciones del usuario y posteriormente enviarlas al servidor para ser registradas.
 */
public class MenuRest extends AppCompatActivity {

    private ListView lv1, lv2;

    private int pos;
    private JSONArray arreglo = new JSONArray();
    private int numCat=3;
    private String mesaN="";
    private int contmenu=1;
    private FloatingActionButton fbS;
    String[] menuspf,menuspg,menuspc,menuspl,menuspv;

    List<String> datosOrdenes = new ArrayList<String>();

    List<String> datos = new ArrayList<String>(asList("CATEGORIAS", "Ensaladas", "Sopas", "Platos Fuertes", "Bebidas", "Postres"));

    List<String> opciones = new ArrayList<String>();

    List<String> datosTotal=new ArrayList<String>();

    ArrayAdapter<String> ad;

    /**
     * Constructor de la activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        lv1 = (ListView) findViewById(R.id.ListView1);
        lv2 = (ListView) findViewById(R.id.ListView2);

        fbS=(FloatingActionButton)findViewById(R.id.fb2);
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datos);
        lv1.setAdapter(adaptador);


        ad = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, opciones);
        lv2.setAdapter(ad);
        Bundle extras =getIntent().getExtras();
        if(extras!=null){
            numCat=extras.getInt("categoria");
            mesaN=extras.getString("numeromesa");
        }

        try {
            new GetMenuInfo().execute(new URL("http://192.168.43.116:9080/Proyecto2/central/chef/menu/ensaladas"));//192.168.1.62
            new GetMenuInfo().execute(new URL("http://192.168.43.116:9080/Proyecto2/central/chef/menu/sopas"));
            new GetMenuInfo().execute(new URL("http://192.168.43.116:9080/Proyecto2/central/chef/menu/platos"));
            new GetMenuInfo().execute(new URL("http://192.168.43.116:9080/Proyecto2/central/chef/menu/bebidas"));
            new GetMenuInfo().execute(new URL("http://192.168.43.116:9080/Proyecto2/central/chef/menu/postres"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        fbS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(v.getContext(),VoiceRecognition.class),720);
            }
        });


        lv1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int posicion, long id) {
                pos = (int) lv1.getItemIdAtPosition(posicion);
                switch (pos){
                    case 1:
                        Intent pasarBebi = new Intent(v.getContext(), Ensaladas.class);
                        pasarBebi.putExtra("lista",menuspf);
                        startActivityForResult(pasarBebi, 808);
                        break;
                    case 2:
                        Intent pasarSopas = new Intent(v.getContext(), Sopas.class);
                        pasarSopas.putExtra("lista",menuspg);
                        startActivityForResult(pasarSopas, 809);
                        break;
                    case 3:
                        Intent pasarPlatos = new Intent(v.getContext(), PlatosFuertes.class);
                        pasarPlatos.putExtra("lista",menuspc);
                        startActivityForResult(pasarPlatos, 810);
                        break;
                    case 4:
                        Intent pasarBebidass = new Intent(v.getContext(), bebibas.class);
                        pasarBebidass.putExtra("lista",menuspl);
                        startActivityForResult(pasarBebidass, 811);
                        break;
                    case 5:
                        Intent pasarPostres = new Intent(v.getContext(), Postres.class);
                        pasarPostres.putExtra("lista",menuspv);
                        startActivityForResult(pasarPostres, 812);
                        break;
                }

            }
        });

        lv2.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int posic=(int)lv2.getItemIdAtPosition(position);
                opciones.remove(posic);
                ad.notifyDataSetChanged();
                datosTotal.remove(posic);
            }
        });

    }

    /**
     * Metodo que envia los nombres de las recetas seleccionadas por el usuario
     * @param v vista donde se encuentra el boton que ejecuta la accion.
     */
    public void ejecutar(View v){
        for(int i=0; i<datosOrdenes.size(); i++){
            JSONObject objeto = new JSONObject();
            try {
                objeto.put("nombre", datosOrdenes.get(i));
                arreglo.put(objeto);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            new EnviarDatos().execute(new URL("http://192.168.43.116:9080/Proyecto2/central/cliente/orden"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Intent result = new Intent();
        String[] bill=new String[datosTotal.size()];
        for(int y=0;y<datosTotal.size();y++){
            bill[y]=datosTotal.get(y);
        }
        result.putExtra("orden",bill);
        setResult(Activity.RESULT_OK, result);
        finish();
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
        String[] tempArr;
        switch(requestCode) {
            case (720) : {
                if (resultCode == Activity.RESULT_OK) {
                    String[] nombreO=data.getStringArrayExtra("order");
                    for(int f=0;f<nombreO.length;f++){
                    boolean val=encontrar(nombreO[f],menuspf,"Ensalada: ");
                    if(val==false){
                        val=encontrar(nombreO[f],menuspg,"Sopa: ");
                    }
                    if(val==false){
                        val=encontrar(nombreO[f],menuspc,"Plato Fuerte: ");
                    }
                    if(val==false){
                        val=encontrar(nombreO[f],menuspl,"Bebida: ");
                    }
                    if(val==false){
                        val=encontrar(nombreO[f],menuspv,"Postre: ");
                    }
                    if(val==false){
                        Toast.makeText(getBaseContext(), "Su eleccion no forma parte del menu", Toast.LENGTH_LONG).show();
                    }
                    }

                }
                break;
            }
            case (808) : {
                if (resultCode == Activity.RESULT_OK) {
                    String newText = data.getStringExtra("parametro");
                    tempArr=newText.split(" ");
                    opciones.add("Ensalada: "+tempArr[0]+" : ₡"+tempArr[2]);
                    datosOrdenes.add(tempArr[0]);
                    datosTotal.add(tempArr[0]+"jk"+tempArr[2]);
                    ad.notifyDataSetChanged();
                }
                break;
            }
            case (809) : {
                if (resultCode == Activity.RESULT_OK) {
                    String newText = data.getStringExtra("parametro");
                    tempArr=newText.split(" ");
                    opciones.add("Sopa: "+tempArr[0]+" : ₡"+tempArr[2]);
                    datosOrdenes.add(tempArr[0]);
                    datosTotal.add(tempArr[0]+"jk"+tempArr[2]);
                    ad.notifyDataSetChanged();                }
                break;
            }
            case (810) : {
                if (resultCode == Activity.RESULT_OK) {
                    String newText = data.getStringExtra("parametro");
                    tempArr=newText.split(" ");
                    opciones.add("Plato Fuerte: "+tempArr[0]+" : ₡"+tempArr[2]);
                    datosOrdenes.add(tempArr[0]);
                    datosTotal.add(tempArr[0]+"jk"+tempArr[2]);
                    ad.notifyDataSetChanged();
                }
                break;
            }
            case (811) : {
                if (resultCode == Activity.RESULT_OK) {
                    String newText = data.getStringExtra("parametro");
                    tempArr=newText.split(" ");
                    opciones.add("Bebida: "+tempArr[0]+" : ₡"+tempArr[2]);
                    datosOrdenes.add(tempArr[0]);
                    datosTotal.add(tempArr[0]+"jk"+tempArr[2]);
                    ad.notifyDataSetChanged();                }
                break;
            }
            case (812) : {
                if (resultCode == Activity.RESULT_OK) {
                    String newText = data.getStringExtra("parametro");
                    tempArr=newText.split(" ");
                    opciones.add("Postre: "+tempArr[0]+" : ₡"+tempArr[2]);
                    datosOrdenes.add(tempArr[0]);
                    datosTotal.add(tempArr[0]+"jk"+tempArr[2]);
                    ad.notifyDataSetChanged();
                }
                break;
            }

        }

    }

    /**
     * Metodo que busca la receta seleccionada por medio de reconocimiento de voz en la base de datos del servidor.
     * @param nombre nombre de la receta a encontrar
     * @param arreglo lista con todas la recetas existentes.
     * @param categoria categoria de la receta.
     * @return true si se encuentra la receta en la base de datos, de lo contrario false.
     */
    public boolean encontrar(String nombre,String[]arreglo,String categoria){
        for(int r=0;r<arreglo.length;r++){
            char[] infoC=arreglo[r].toCharArray();
            char[] comparador=nombre.toCharArray();
            int equidad=0;
            for(int z=0;z<comparador.length;z++){
                if(infoC[z]==comparador[z]){
                    equidad++;
                }
            }
            if(equidad==comparador.length){
                String[]tempo=arreglo[r].split("jk");
                String[] price=tempo[1].split(" ");
                opciones.add(categoria+tempo[0]+" : ₡"+price[1]);
                datosOrdenes.add(tempo[0]);
                datosTotal.add(tempo[0]+"jk"+price[1]);
                ad.notifyDataSetChanged();
                return true;
            }
        }
        return false;
    }


    /**
     * Clase encargada de enviar los datos de la seleccion de platillos del usuario en segundo plano.
     */
    public class EnviarDatos extends AsyncTask<URL, Void, Void> {

        @Override
        protected Void doInBackground(URL... urls) {
// Obtener la conexión
            HttpURLConnection con = null;

            try {

                con = (HttpURLConnection)urls[0].openConnection();

// Activar método POST
                con.setDoOutput(true);

                JSONArray total=new JSONArray();


                    total.put(numCat);
                    total.put(arreglo);
                    total.put(mesaN);
                con.setFixedLengthStreamingMode(total.toString().getBytes().length);
                con.setRequestProperty("Content-Type","application/json");

                OutputStream out = new BufferedOutputStream(con.getOutputStream());

                out.write(total.toString().getBytes());
                out.flush();
                out.close();
                arreglo=new JSONArray();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(con!=null)
                    con.disconnect();
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void s) {
            Toast.makeText(getBaseContext(), "Orden Registrada", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Clase que se ejecuta en segundo plano que consulta al servidor por su base de datos.
     */
    public class GetMenuInfo extends AsyncTask<URL, Void, List<String>> {



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

            String[] algo=new String[coleccion.size()];
            for(int h=0;h<coleccion.size();h++){
                algo[h]=coleccion.get(h);
            }

            if(contmenu==1){
                menuspf=algo;}
            else if(contmenu==2){
                menuspg=algo;
            }else if(contmenu==3){
                menuspc=algo;
            }else if(contmenu==4){
                menuspl=algo;
            }else if(contmenu==5){
                menuspv=algo;
            }
            contmenu++;



        }
    }


}



