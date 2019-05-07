package com.example.tractoramarilloapp.handlers;

import android.content.Context;

import com.example.tractoramarilloapp.persistence.HandlerDBPersistence;
import com.example.tractoramarilloapp.utils.FA;

/**
 * Clase con la responsabilidad de representar las fallas en el dispositivo, ya sea un implemento o una maquinaria
 */
public class HandlerFallas {

    //atributos de la clase
    private HandlerDBPersistence handlerDBPersistence;
    private Context context;

    public HandlerFallas(Context context){

        this.context = context;
        this.handlerDBPersistence = new HandlerDBPersistence(this.context);
    }

    /**
     * Metodo que permite agregar elementos al informe de fallas del dispositivo
     * @param descripcionFalla
     * @param userID
     * @param idElemento
     * @param tipoElemento
     * @return
     */
    public int addFallasInDevice(String descripcionFalla, String userID, String idElemento, String tipoElemento){

        //obtenemos el ID
        String sqlID = "SELECT * FROM fallaHerramienta";
        int lastID = this.handlerDBPersistence.getLastID(sqlID, "idFallaHerramienta");

        if (lastID == -1){
            lastID = 1;
        }else{
            lastID++;
        }

        //obtenemos la fecha
        String date = FA.getCurrentDate();
        String sqlInsert = "INSERT INTO fallaHerramienta VALUES("+lastID+", '"+descripcionFalla+"', '"+date+"', '"+userID+"', '"+tipoElemento+"', '"+idElemento+"')";
        int response = this.handlerDBPersistence.execSQLData(sqlInsert);
        return response;
    }
}
