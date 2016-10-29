package com.example.andresgarher.androiddrawer;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Calificar extends AppCompatActivity {

    private static Button button_sbm;
    private static TextView text_v;
    private static RatingBar rating_b;
    private static EditText textoEdit;

    private JSONArray arreglo = new JSONArray();
    List<String> datosCalificar = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificar);

        textoEdit = (EditText)findViewById(R.id.editText);

        listenerForRatingBar();
        onButtonClickListener();



    }

    public void listenerForRatingBar(){
        rating_b = (RatingBar)findViewById(R.id.ratingBar);
        text_v = (TextView)findViewById(R.id.textView8);

        rating_b.setOnRatingBarChangeListener(
                new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        text_v.setText(String.valueOf(rating));
                    }
                }


        );

    }


    public void onButtonClickListener(){
        rating_b = (RatingBar)findViewById(R.id.ratingBar);
        button_sbm = (Button)findViewById(R.id.buttonEnviar);

        button_sbm.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(Calificar.this, "Calificacion: "+String.valueOf(rating_b.getRating())+"\nComentarios: "+textoEdit.getText(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
        );
    }

    public void ejecutarEnviar(View v){
        for(int i=0; i<datosCalificar.size(); i++){
            JSONObject objeto = new JSONObject();
            try {
                objeto.put("comentario",textoEdit.getText());
                objeto.put("nota", String.valueOf(rating_b.getRating()));
                arreglo.put(objeto);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            new EnviarDatos().execute(new URL("http://192.168.1.62:9080/Proyecto2/central/cliente/nota"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }



    public class EnviarDatos extends AsyncTask<URL, Void, Void> {

        @Override
        protected Void doInBackground(URL... urls) {
// Obtener la conexión
            HttpURLConnection con = null;

            try {

                con = (HttpURLConnection)urls[0].openConnection();

// Activar método POST
                con.setDoOutput(true);

                con.setFixedLengthStreamingMode(arreglo.toString().getBytes().length);
                con.setRequestProperty("Content-Type","application/json");

                OutputStream out = new BufferedOutputStream(con.getOutputStream());

                out.write(arreglo.toString().getBytes());
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


}
