package com.example.tractoramarilloapp.syncService;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.tractoramarilloapp.persistence.HandlerDBPersistence;

import java.util.ArrayList;

/**
 * Clase con la responsabilidad de solicitar la informacion al dispositivo, extendera a un AS para enviarla en background
 */
public class SyncDownService {

    //atributos de la clase
    private String host;
    private String service;
    private String url;
    private Context context;
    private HandlerDBPersistence handlerDBPersistence;
    private String dateQuery;
    private String tipoLlamado;
    private Activity activityParent;

    public SyncDownService(String host, String service, String url, Context context, String dateQuery, String tipoLlamado, Activity activityParent){

        this.host = host;
        this.service = service;
        this.url = url;
        this.context = context;
        this.handlerDBPersistence = new HandlerDBPersistence(this.context);
        this.dateQuery = dateQuery;
        this.tipoLlamado = tipoLlamado;
        this.activityParent = activityParent;
    }

    /**
     * Metodo que permite manipular todos los eventos y acciones asociadas al proceso de sincronizacion de datos:
     *
     * 1. Obtener la data del dispositivo.
     * 2. Traer la data desde el servidor.
     * 3. Comparar la informacion.
     * 4. Actualizar la data.
     *
     * NOTE: Solo es agregar registros en el dispositivo
     * NOTE: Los codigos unico
     */
    public void processSyncElement(){

        if (this.isAvailableToDownload()==0) {
            this.changeStatusSharedPreference();

            ArrayList<String> listTableName = new ArrayList<>();
            listTableName.add("faena");
            listTableName.add("predio");
            listTableName.add("usuario");
            listTableName.add("maquinaria");
            listTableName.add("tipoMaquinaria");
            listTableName.add("tipoHabilitado");
            listTableName.add("implementos");
            listTableName.add("mensajeMotivacional");

            //las estructuras de estas tablas se encuentran creadas, solo basta con traer la informacion para poblarla
            for (int i = 0; i < listTableName.size(); i++) {

                //obtenemos la informacion desde servidor
                ConnectToService connectToService = new ConnectToService(this.host, this.url, this.service, this.dateQuery, listTableName.get(i), this.context, this.tipoLlamado, this.activityParent);
                //connectToService.execute();
                connectToService.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        }else{
            Log.e("SYNC_DOWN", "No se puede porque esta trabajando el UP");
            //deberia mostrar un mensaje en caso de que el tipo de llamada sea desde windows
        }
    }

    /**
     * Metodo que permite cambiar los estados asociados en las shared preference que tienen relacion con el inicio del sincronizador
     */
    public void changeStatusSharedPreference(){

        SharedPreferences preferences = activityParent.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        //instanciamos todos los elementos en modo 1 => que estan trabajando
        editor.putString("SYNC_DOWN", "1");
        editor.putString("PREDIO_DOWN", "1");
        editor.putString("FAENA_DOWN", "1");
        editor.putString("MAQUINARIA_DOWN", "1");
        editor.putString("IMPLEMENTO_DOWN", "1");
        editor.putString("USUARIO_DOWN", "1");
        editor.putString("MENSAJE_DOWN", "1");
        editor.putString("TIPO_MAQUINARIA_DOWN", "1");
        editor.putString("TIPO_HABILITADO_DOWN", "1");
        editor.commit();
    }

    /**
     * Metodo que permite determinar si se encuentra el sincronizador de subida trabajando, de ser asi, este sincronizador no
     * puede trabajar y se deja un mensaje.
     * @return
     */
    public int isAvailableToDownload(){

        SharedPreferences preferences = activityParent.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

        String evalSyncUp = preferences.getString("SYNC_UP", "null");

        if (evalSyncUp.equalsIgnoreCase("0"))
            return 0;
        else
            return 1;
    }
}
