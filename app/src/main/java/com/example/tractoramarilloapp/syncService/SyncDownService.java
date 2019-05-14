package com.example.tractoramarilloapp.syncService;


import android.content.Context;
import android.os.AsyncTask;

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

    public SyncDownService(String host, String service, String url, Context context, String dateQuery, String tipoLlamado){

        this.host = host;
        this.service = service;
        this.url = url;
        this.context = context;
        this.handlerDBPersistence = new HandlerDBPersistence(this.context);
        this.dateQuery = dateQuery;
        this.tipoLlamado = tipoLlamado;
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
        for (int i=0; i<listTableName.size(); i++){

            //obtenemos la informacion desde servidor
            ConnectToService connectToService = new ConnectToService(this.host, this.url, this.service, this.dateQuery, listTableName.get(i), this.context, this.tipoLlamado);
            //connectToService.execute();
            connectToService.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }

    }
}
