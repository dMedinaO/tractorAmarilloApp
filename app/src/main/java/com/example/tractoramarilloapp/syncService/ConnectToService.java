package com.example.tractoramarilloapp.syncService;

import android.content.Context;
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

import java.util.ArrayList;

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

    /**
     * Constructor de la clase
     * @param host
     * @param url
     * @param service
     * @param params
     * @param tipoLlamado
     */
    public ConnectToService(String host, String url, String service, String params, String tableName, Context context, String tipoLlamado){

        this.host = host;
        this.params = params;
        this.url = url;
        this.service = service;
        this.tableName = tableName;
        this.context = context;
        this.tipoLlamado = tipoLlamado;
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
                            }else{

                                if (tableName.equalsIgnoreCase("predio")){//manipulamos la informacion del predio
                                    new ProcessDataJSON().processDataPredio(responseObject, context);
                                }else{

                                    if (tableName.equalsIgnoreCase("usuario")){//manipulamos la informacion del usuario
                                        new ProcessDataJSON().processDataUser(responseObject, context);

                                    }else{
                                        if (tableName.equalsIgnoreCase("maquinaria")){//manipulamos la informacion de la maquinaria
                                            new ProcessDataJSON().processDataMaquinaria(responseObject, context);
                                        }else{
                                            if (tableName.equalsIgnoreCase("tipoMaquinaria")){//manipulamos la informacion del tipo de maquinaria

                                                new ProcessDataJSON().processDataTipoMaquinaria(responseObject, context);
                                            }else{
                                                if (tableName.equalsIgnoreCase("tipoHabilitado")){
                                                    new ProcessDataJSON().processDataTipoHabilitado(responseObject, context);
                                                }else{
                                                    if (tableName.equalsIgnoreCase("implementos")){
                                                        new ProcessDataJSON().processDataImplemento(responseObject, context);
                                                    }else{
                                                        if(tableName.equalsIgnoreCase("mensajeMotivacional")){
                                                            new ProcessDataJSON().processMensajeMotivacional(responseObject, context);
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

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        this.getValuesServer();
        return null;
    }
}
