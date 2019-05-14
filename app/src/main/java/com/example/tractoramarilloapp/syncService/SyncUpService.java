package com.example.tractoramarilloapp.syncService;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tractoramarilloapp.persistence.HandlerDBPersistence;
import com.example.tractoramarilloapp.utils.FA;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Clase con la responsabilidad de enviar la informacion de los informes al servidor, se envian tanto los informes como la data
 * de la unidad local, esta ultima se elimina del dispositivo a la hora de procesar enviarla al servidor
 */
public class SyncUpService {

    //atributos de la clase
    private String host;
    private String service;
    private String url;
    private Context context;
    private HandlerDBPersistence handlerDBPersistence;
    private String dateQuery;

    public SyncUpService(String host, String service, String url, Context context, String dateQuery){

        this.host = host;
        this.service = service;
        this.url = url;
        this.context = context;
        this.handlerDBPersistence = new HandlerDBPersistence(this.context);
        this.dateQuery = dateQuery;
    }

    public void processElementToService() throws JSONException {

        //obtenemos la informacion de las unidades locales a enviar, solo aquellas cuyo estatus se encuentra en NOT
        String sqlQuery = "SELECT * FROM unidadLocal where statusSend = 'NOT'";
        Cursor cursor = this.handlerDBPersistence.consultarRegistros(sqlQuery);

        if (cursor!= null && cursor.getCount()>0){

            //procesamos la data, formando un JSON ARRAY, utilizamos los metodos existentes en la clase FA
            final JSONObject dataToServer = FA.createJSONUnityLocal(cursor);

            RequestQueue queue = Volley.newRequestQueue(this.context);
            String urlFull = this.host+this.url+this.service;

            // Request a string response from the provided URL.{
            StringRequest stringRequest = new StringRequest(Request.Method.POST, urlFull,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("TAG-SYNC-UP", response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // error
                }
            }){
                @Override
                public byte[] getBody() throws AuthFailureError {
                    String your_string_json = dataToServer.toString(); // put your json
                    return your_string_json.getBytes();
                }
            };
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
            queue.start();
        }else {
            Log.e("TAG-SYNC-UP", "No es necesario ejecutar la subida de datos ya que no existen elementos a subir");
        }

    }
}
