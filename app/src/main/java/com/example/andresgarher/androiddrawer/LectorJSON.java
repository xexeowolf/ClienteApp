package com.example.andresgarher.androiddrawer;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Clase encargada de recibir informacion en formato JSON y extraer los elementos.
 * Created by alfredo on 22/10/16.
 */

public class LectorJSON {

    /*
    Método encargado de coordinar la conversión final de un flujo
    con formato JSON
    */

    /**
     *
     * @param in flujo de datos entrantes
     * @return lista enlazada con la informacion recibida
     * @throws IOException
     */
    public List<String> readJsonStream(InputStream in) throws IOException {

// Nueva instancia de un lector JSON
        JsonReader reader = new JsonReader(
                new InputStreamReader(in, "UTF-8"));

        try {
            return readCommentsArray(reader);
        }
        finally{
            reader.close();
        }
    }

    /*
    Este método lee cada elemento al interior de un array JSON
    */

    /**
     * Metodo encargado de leer la informacion almacenada en el flujo de datos entrante
     * @param reader lector de tipo JSON
     * @return lista enlazada simple con los elementos recolectados por el lector JSON
     * @throws IOException
     */
    public List<String> readCommentsArray(JsonReader reader) throws IOException {
        List<String> comments = new ArrayList<>();

// Se dirige al corchete de apertura del arreglo
        reader.beginArray();
        while (reader.hasNext()) {
            comments.add(readMessage(reader,"atributo"));
        }

// Se dirige al corchete de cierre
        reader.endArray();
        return comments;
    }

    /*
    Lee los atributos de cada objeto
    */

    /**
     * Metodo encargado de leer la informacion almacenada en el flujo de datos entrante
     * @param reader lector de tipo JSON
     * @param llave nombre de la llave sobre la cual esta almacenada la informacion en el JSONObject
     * @return cadenas de texto con la informacion recibida.
     * @throws IOException
     */
    public String readMessage(JsonReader reader,String llave) throws IOException {

// Cuerpo del comentario
        String body = null;

// Se dirige a la llave de apertura del objeto
        reader.beginObject();

        while (reader.hasNext()) {

// Se obtiene el nombre del atributo
            String name = reader.nextName();

            if (name.equals(llave)) {
                body= reader.nextString();
            }else {
                reader.skipValue();
            }
        }

// Se dirige a la llave de cierre del objeto
        reader.endObject();
        return body;
    }
}