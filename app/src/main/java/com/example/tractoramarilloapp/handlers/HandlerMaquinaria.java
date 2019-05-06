package com.example.tractoramarilloapp.handlers;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.tractoramarilloapp.model.Maquinaria;
import com.example.tractoramarilloapp.persistence.HandlerDBPersistence;

import java.util.ArrayList;

/**
 * Clase con la responsabilidad de manipular las acciones referidas al operador y la maquinaria con respecto a la data
 * que estos pueden operar y el resto de weas correspondientes
 */
public class HandlerMaquinaria {

    private String userValue;
    private String tagNFC;
    private HandlerDBPersistence handlerDBPersistence;
    private Context context;
    private String tipoMaquinaria;
    public static String TAG="HANDLER-MACHINE";
    private ProgressDialog dialog;

    /**
     * Constructor de la clase
     * @param userValue
     * @param tagNFC
     */
    public HandlerMaquinaria(String userValue, String tagNFC, Context context){

        this.tagNFC = tagNFC;
        this.userValue = userValue;
        this.context = context;
        this.handlerDBPersistence = new HandlerDBPersistence(this.context);
        this.tipoMaquinaria = "-";//almacena el tipo de maquina

    }

    public int applyFluxe(){

        int response = 0;

        if (this.checkTagIsMachine()){//si el tag corresponde a una maquina
            Log.e(TAG, "is Machine");
            if (this.isMachineRegistered()){//si la maquina esta registrada en el sistema
                Log.e(TAG, "is registered");

                if(this.isWorkerAvailable()){//si el operario se encuentra habilitado para dicha maquina
                    Log.e(TAG, "User available");
                }else{
                    Log.e(TAG, "worked is not available");
                    response = -4;//operario no habilitado para la maquinaria
                }
            }else{
                Log.e(TAG, "is not registered");
                response = -2;//maquina no registrada.
            }
        }else{
            Log.e(TAG, "is not a machine");
            response = -1;//tag no corresponde a una maquina
        }
        return response;
    }
    /**
     * Metodo que permite evaluar si la maquinaria se encuentra habilitada para trabajar o no
     * @return
     */
    public boolean isMachineAvailable(){

        boolean response = false;

        String statusMachine = this.tagNFC.split(":")[2];
        if (statusMachine.equalsIgnoreCase("0")){
            response = true;
        }
        return  response;
    }

    /**
     * Metodo que permite validar si la tag corresponde a una maquinaria
     * @return
     */
    public boolean checkTagIsMachine(){

        boolean response=false;

        String [] data = this.tagNFC.split(":");
        if (data[1].equalsIgnoreCase("3")){
            response = true;
        }

        return response;
    }

    /**
     * Metodo que permite evaluar si la maquina se encuentra registrada o no
     * @return
     */
    public boolean isMachineRegistered(){

        boolean response=false;

        ArrayList<Maquinaria> listMaquina = this.handlerDBPersistence.getMaquinariaList();

        for (int i=0; i< listMaquina.size(); i++){
            if (listMaquina.get(i).getCodeInternoMachine().equalsIgnoreCase(this.tagNFC.split(":")[0])){
                this.tipoMaquinaria = listMaquina.get(i).getTipoMaquinaria();
                response = true;
                break;
            }
        }

        return response;
    }

    /**
     * Metodo que permite evaluar si el operario se encuentra habilitado para operar la maquinaria
     * @return
     */
    public boolean isWorkerAvailable(){

        boolean response=false;

        //obtenemos el tipo maquinara
        String sqlQuery = "SELECT * FROM tipoHabilitado where tipoHabilitado.operadorID = '"+this.userValue+"' AND tipoHabilitado.tipoMaquinariaID = '"+this.tipoMaquinaria+"'";
        Cursor cursor = this.handlerDBPersistence.consultarRegistros(sqlQuery);
        if (cursor != null){
            response = true;
        }

        return response;
    }
}
