package com.example.tractoramarilloapp.syncService;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Clase con la responsabilidad de obtener la informacion desde el servidor, trabaja con Volley
 * para la manipulacion de las conexiones y posee un metodo que procesa la data y la habilita desde
 * el servidor para almacenarla en el registro de datos persistente
 */
public class ConnectToService extends AsyncTask {

    //atributos de la clase
    private String host;
    private String url;
    private String service;
    private String params;
    private String tableName;
    private Context context;
    private String tipoLlamado;
    private Activity activity;

    /**
     * Constructor de la clase
     * @param host
     * @param url
     * @param service
     * @param params
     * @param tipoLlamado
     */
    public ConnectToService(String host, String url, String service, String params, String tableName, Context context, String tipoLlamado, Activity activity){

        this.host = host;
        this.params = params;
        this.url = url;
        this.service = service;
        this.tableName = tableName;
        this.context = context;
        this.tipoLlamado = tipoLlamado;
        this.activity = activity;
    }

    /**
     * Metodo que permite obtener la informacion desde el servidor
     * @return
     */
    public int getValuesServer(){

        int response=0;

        String urlFull = this.host+this.url+this.service+"?fecha="+this.params+"&tabla="+this.tableName;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, urlFull, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            //formamos la lista de las tablas a sincronizar desde la bd en servidor
                            JSONArray responseObject = response.getJSONArray("data");

                            if (tableName.equalsIgnoreCase("faena")){//manipulamos la informacion de la faena
                                new ProcessDataJSON().processDataFaena(responseObject, context);
                                changeStatusToShared("0");
                            }else{

                                if (tableName.equalsIgnoreCase("predio")){//manipulamos la informacion del predio
                                    new ProcessDataJSON().processDataPredio(responseObject, context);
                                    changeStatusToShared("0");
                                }else{

                                    if (tableName.equalsIgnoreCase("usuario")){//manipulamos la informacion del usuario
                                        new ProcessDataJSON().processDataUser(responseObject, context);
                                        changeStatusToShared("0");

                                    }else{
                                        if (tableName.equalsIgnoreCase("maquinaria")){//manipulamos la informacion de la maquinaria
                                            new ProcessDataJSON().processDataMaquinaria(responseObject, context);
                                            changeStatusToShared("0");
                                        }else{
                                            if (tableName.equalsIgnoreCase("tipoMaquinaria")){//manipulamos la informacion del tipo de maquinaria
                                                new ProcessDataJSON().processDataTipoMaquinaria(responseObject, context);
                                                changeStatusToShared("0");
                                            }else{
                                                if (tableName.equalsIgnoreCase("tipoHabilitado")){
                                                    new ProcessDataJSON().processDataTipoHabilitado(responseObject, context);
                                                    changeStatusToShared("0");
                                                }else{
                                                    if (tableName.equalsIgnoreCase("implementos")){
                                                        new ProcessDataJSON().processDataImplemento(responseObject, context);
                                                        changeStatusToShared("0");
                                                    }else{
                                                        if(tableName.equalsIgnoreCase("mensajeMotivacional")){
                                                            new ProcessDataJSON().processMensajeMotivacional(responseObject, context);
                                                            changeStatusToShared("0");
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //preguntamos si terminamos
                        int isFinish = isDownFinish();

                        if (isFinish == 0){
                            finishSync();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                VolleyLog.d("TAG-ERROR", "JSONObj Error: " + error.getMessage());


                            }
                        }
                );

        //AGREGAR REQUEST A LA COLA DE EJECUCION DE VOLLEY
        //Volley.newRequestQueue(context).add(stringRequest);

        //SE AUMENTA LIMITE DE TIEMPO DE ESPERA Y SE LIMITA CANTIDAD DE PETICIONES AL SERVIDOR, PARA EVITAR QUE SE ENVIEN MULTIPLES EMAIL
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        int socketTimeout = 40000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        mRequestQueue.add(jsonObjectRequest);

        return response;
    }

    /**
     * Metodo que permite la actualizacion de los procesos de sincronizacion en las shared asociados a las tablas de descarga de datos
     * @param key
     */
    public void changeStatusToShared(String key){

        SharedPreferences preferences = this.activity.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if (this.tableName.equalsIgnoreCase("faena"))
            editor.putString("FAENA_DOWN", key);
        else if (this.tableName.equalsIgnoreCase("predio"))
            editor.putString("PREDIO_DOWN", key);
        else if (this.tableName.equalsIgnoreCase("usuario"))
            editor.putString("USUARIO_DOWN", key);
        else if (this.tableName.equalsIgnoreCase("maquinaria"))
            editor.putString("MAQUINARIA_DOWN", key);
        else if (this.tableName.equalsIgnoreCase("tipoMaquinaria"))
            editor.putString("TIPO_MAQUINARIA_DOWN", key);
        else if (this.tableName.equalsIgnoreCase("tipoHabilitado"))
            editor.putString("TIPO_HABILITADO_DOWN", key);
        else if (this.tableName.equalsIgnoreCase("implementos"))
            editor.putString("IMPLEMENTO_DOWN", key);
        else
            editor.putString("MENSAJE_DOWN", key);
        editor.commit();

    }

    /**
     * Metodo que permite evaluar si el sincronizador ya se encuentra listo para terminar.
     * @return
     */
    public int isDownFinish(){

        //obtenemos las shared
        SharedPreferences preferences = this.activity.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

        //formamos un arreglo con las keys
        String [] keyShared = {"FAENA_DOWN", "PREDIO_DOWN", "USUARIO_DOWN", "MAQUINARIA_DOWN", "TIPO_MAQUINARIA_DOWN", "TIPO_HABILITADO_DOWN", "IMPLEMENTO_DOWN", "MENSAJE_DOWN"};
        String [] status = new String[keyShared.length];

        for (int i=0; i<keyShared.length; i++){
            status[i] = preferences.getString(keyShared[i], null);
        }

        int cont=0;
        for (int i=0; i<status.length; i++){
            if (status[i].equalsIgnoreCase("0"))
                cont++;
        }

        if (cont == status.length)
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

        editor.putString("SYNC_DOWN", "0");
        editor.commit();

        if (this.tipoLlamado.equalsIgnoreCase("WINDOWS"))
            Log.e("SYNC_DOWN", "Debo detener el mensaje rql");
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (this.tipoLlamado.equalsIgnoreCase("WINDOWS")){
            Log.e("SYNC_DOWN", "Debo mostrar el mensaje rql");
        }
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        this.getValuesServer();
        return null;
    }
}
