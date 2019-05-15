package com.example.tractoramarilloapp.syncService;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase con la responsabilidad de enviar la informacion de los informes al servidor, se envian tanto los informes como la data
 * de la unidad local, esta ultima se elimina del dispositivo a la hora de procesar enviarla al servidor
 */
public class SyncUpService extends AsyncTask {

    //atributos de la clase
    private String host;
    private String service;
    private String url;
    private Context context;
    private HandlerDBPersistence handlerDBPersistence;
    private String tipoLlamado;
    private Activity activity;

    public SyncUpService(String host, String service, String url, Context context, String tipoLlamado, Activity activity){

        this.host = host;
        this.service = service;
        this.url = url;
        this.context = context;
        this.handlerDBPersistence = new HandlerDBPersistence(this.context);
        this.tipoLlamado = tipoLlamado;
        this.activity = activity;
    }

    public void processElementToService() throws JSONException {

        if (this.isAvailableToSync() == 0) {
            //obtenemos la informacion de las unidades locales a enviar, solo aquellas cuyo estatus se encuentra en NOT
            final String sqlQuery = "SELECT * FROM unidadLocal where statusSend = 'NOT'";
            Cursor cursor = this.handlerDBPersistence.consultarRegistros(sqlQuery);

            if (cursor != null && cursor.getCount() > 0) {

                //procesamos la data, formando un JSON ARRAY, utilizamos los metodos existentes en la clase FA
                final JSONObject dataToServer = FA.createJSONUnityLocal(cursor);

                String urlFull = this.host + this.url + this.service;

                StringRequest stringRequest = new StringRequest(Request.Method.POST, urlFull,

                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {

                                Log.e("TAG_JSON_RESPONSE", response);
                                //procesamos el JSON
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    JSONArray jArray = obj.getJSONArray("responseServer");

                                    //hacemos la eliminacion de la data
                                    for (int i = 0; i < jArray.length(); i++) {
                                        try {
                                            JSONObject oneObject = jArray.getJSONObject(i);
                                            // Pulling items from the array
                                            String idUnidad = oneObject.getString("idUnidad");
                                            String responseServer = oneObject.getString("responseInsert");

                                            if (responseServer.equalsIgnoreCase("OK")) {
                                                String sqlRemove = "DELETE FROM unidadLocal where idUnidad = " + idUnidad;
                                                new HandlerDBPersistence(context).execSQLData(sqlRemove);
                                            }
                                        } catch (JSONException e) {
                                            // Oops
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                finishSync();
                            }//onResponse

                        }

                        , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //mensaje_pantalla(2,context);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("data", dataToServer.toString());
                        return params;
                    }
                };

                // Add the request to the queue
                Volley.newRequestQueue(context).add(stringRequest);
            } else {
                Log.e("TAG-SYNC-UP", "No es necesario ejecutar la subida de datos ya que no existen elementos a subir");
            }
        }else{
            Log.e("SYNC_UP", "El descargador esta trabajando!");
        }
    }

    /**
     * Metodo que revisa el estado del sincronizador de subida de datos
     * @return
     */
    public int isAvailableToSync(){

        SharedPreferences preferences = activity.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

        String evalSyncUp = preferences.getString("SYNC_DOWN", "null");

        if (evalSyncUp.equalsIgnoreCase("0"))
            return 0;
        else
            return 1;
    }

    /**
     * Metodo que da termino al sincronizador
     */
    public void finishSync(){

        SharedPreferences preferences = this.activity.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("SYNC_UP", "0");
        editor.commit();

        if (this.tipoLlamado.equalsIgnoreCase("WINDOWS"))
            Log.e("SYNC_UP", "Debo detener el mensaje rql");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (this.tipoLlamado.equalsIgnoreCase("WINDOWS")){
            Log.e("SYNC_UP", "Deberia mostrar mensaje");
        }
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        try {
            this.processElementToService();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


}
