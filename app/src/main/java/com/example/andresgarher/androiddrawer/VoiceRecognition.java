package com.example.andresgarher.androiddrawer;


import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.andresgarher.androiddrawer.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada de realizar el reconocimiento de voz.
 */
public class VoiceRecognition extends AppCompatActivity {
    private final static int VOICE_RECOGNITION_REQUEST_CODE = 1001;
    private EditText metTextHint;
    private ListView mlvTextMachines,lista_ordenes;
    private Spinner msTextMatches;
    private Button mbtSpeak;
    private FloatingActionButton sp;
    List<String> ordenes=new ArrayList<String>();
    ArrayAdapter<String> or_ad;

    /**
     * Constructor de la clase.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recognition);

        metTextHint = (EditText)findViewById(R.id.etTextHint);
        mlvTextMachines = (ListView)findViewById(R.id.lvTextMatches);
        lista_ordenes = (ListView)findViewById(R.id.order_lista);
        or_ad=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,ordenes);
        lista_ordenes.setAdapter(or_ad);
        msTextMatches = (Spinner)findViewById(R.id.sNoOfMatches);
        sp=(FloatingActionButton)findViewById(R.id.fb);
        sp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                speak(sp.getRootView());
            }
        });
        mlvTextMachines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                or_ad.add((String)mlvTextMachines.getItemAtPosition(position));
                or_ad.notifyDataSetChanged();
                lista_ordenes.setVisibility(View.VISIBLE);
            }
        });

        lista_ordenes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int posic=(int)lista_ordenes.getItemIdAtPosition(position);
                ordenes.remove(posic);
                or_ad.notifyDataSetChanged();
            }
        });



    }

    /**
     * Metodo que transforma el texto obtenido por reconocimiento de voz en un formato comparable.
     * @param text texto obtenido por reconocimiento de voz
     * @return texto en formato comparable.
     */
    public String fisMayu(String text){

        String tex = "";
        char[] charss = text.toCharArray();
        charss[0] = Character.toUpperCase(charss[0]);
        for (int i = 0; i < text.length(); i++){
            if (charss[i] == ' '){
                charss[i + 1] = Character.toUpperCase(charss[i + 1]);

            }
            tex += charss[i];
        }
        String[]compacto=tex.split(" ");
        tex=compacto[0];
        for(int e=1;e<compacto.length;e++){
            tex=tex+compacto[e];
        }
        return tex;
    }

    public void devolver(View view){
        Intent result = new Intent();

        String[] paquete=new String[ordenes.size()];
        for(int y=0;y<paquete.length;y++){
            paquete[y]=fisMayu(ordenes.get(y));
        }
        result.putExtra("order",paquete);
        setResult(Activity.RESULT_OK, result);
        finish();

    }

    public void CheckVoiceRecognition(){
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH),0);
        if(activities.size()==0){
            //mbtSpeak.setEnabled(false);
            Toast.makeText(this,"voice recognizer no present",Toast.LENGTH_LONG).show();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void speak(View view){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getClass().getPackage().getName());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,metTextHint.getText().toString());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        if(msTextMatches.getSelectedItemPosition()== AdapterView.INVALID_POSITION){
            Toast.makeText(this,"Please select No. of Matches from Spinner",Toast.LENGTH_LONG).show();
            return ;
        }
        int noOfMatches = Integer.parseInt(msTextMatches.getSelectedItem().toString());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,noOfMatches);
        startActivityForResult(intent,VOICE_RECOGNITION_REQUEST_CODE);
    }

    /**
     * Metodo que realiza distintas acciones dependiendo del resultado devuelto por una activity.
     * @param requestCode codigo unico de cada activity
     * @param resultCode codigo que indica si el proceso termino correctamente
     * @param data informacion devuelta por una activity
     */

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(resultCode == RESULT_OK){
            ArrayList<String> textMatchlist = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if(!textMatchlist.isEmpty()){
                if(textMatchlist.get(0).contains("search")){
                    String searchQuery = textMatchlist.get(0).replace("search"," ");
                    Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                    search.putExtra(SearchManager.QUERY,searchQuery);
                    startActivity(search);
                }
                else{
                    mlvTextMachines.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,textMatchlist));
                }
            }
        }
        else if(resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){
            showToastMessage("Audio Error");
        }
        else if(resultCode == RecognizerIntent.RESULT_CLIENT_ERROR){
            showToastMessage("Client Error");
        }
        else if(resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){
            showToastMessage("Network Error");
        }
        else if(resultCode == RecognizerIntent.RESULT_NO_MATCH){
            showToastMessage("No Match");
        }
        else if(resultCode == RecognizerIntent.RESULT_SERVER_ERROR){
            showToastMessage("Server Error");
        }
        super.onActivityResult(requestCode,resultCode,data);
    }
    void showToastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();

    }
}