package com.example.tractoramarilloapp.handlers;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.tractoramarilloapp.model.Implemento;
import com.example.tractoramarilloapp.persistence.HandlerDBPersistence;

import java.util.ArrayList;

/**
 * Clase que tiene la responsabilidad de manejar los eventos y acciones asociadas a un implemento,
 * permite evaluar si el tag leido corresponde a un implemento, si este se encuentra registrado o si esta
 * habilidato para trabajar con ciertas maquinarias
 */
public class HandlerImplemento {

    private String codeInternoMachine;
    private String tagNFC;
    private static String TAG="HANDLER-IMPLEMENT";
    private HandlerDBPersistence handlerDBPersistence;
    private Context context;
    private String codeImplement;

    /**
     * Constructor de la clase
     * @param codeInternoMachine
     * @param tagNFC
     * @param context
     */
    public HandlerImplemento(String codeInternoMachine, String tagNFC, Context context){

        this.codeInternoMachine = codeInternoMachine;
        this.tagNFC = tagNFC;
        this.context = context;
        this.handlerDBPersistence = new HandlerDBPersistence(this.context);
    }

    /**
     * Metodo que permite evaluar los flujos correspondientes a los asociados al sistema de evaluacion de implementos
     * junto con las caracteristicas de estos y los check correspondientes
     * @return
     */
    public int applyFluxeCheck(){

        int response = 0;

        if (this.isImplement()){//validacion de tag corresponde a un implemento

            if (this.isImplementRegistered()){//si el implemento se encuentra registrado en el dispositivo

                if (this.isImplementAvailable()){//si el implemento se encuentra habilitado para trabajar

                    if(this.isImplementAvailableForMachine()){
                        Log.e(TAG, "implements OK");
                    }else {
                        Log.e(TAG,"Implemento no se puede ocupar con maquinaria seleccionada");
                        response=-4;
                    }
                }else{
                    response=-3;
                    Log.e(TAG, "Implemento no se encuentra habilitado para trabajar");
                }
            }else{
                response=-2;
                Log.e(TAG, "Implemento no se encuentra registrado");
            }

        }else{
            response = -1;
            Log.e(TAG, "Tag no es implemento");
        }
        return response;
    }
    /**
     * Metodo que tiene la responsabilidad de evaluar si el tag recibido corresponde a un implemento o no
     * @return
     */
    public boolean isImplement(){

        boolean response=false;

        String [] dataTag = this.tagNFC.split(":");
        try {
            if (dataTag[1].equalsIgnoreCase("4")) {//tipo 4 corresponde a un implemento
                this.codeImplement = dataTag[0];
                response = true;
            }
        }catch (Exception e){//manejo de errores en caso de cualquier estupidez, en vola la tarjeta no esta grabada o no tiene algun caracter especifico o que se yo XD
            response = false;
        }
        return  response;
    }

    /**
     * Metodo que permite evaluar si la maquina se encuentra registrada en el dispositivo
     * @return
     */
    public boolean isImplementRegistered(){

        boolean response = false;

        ArrayList<Implemento> listImplement = this.handlerDBPersistence.getImplementos();

        for (int i=0; i< listImplement.size(); i++){
            if (listImplement.get(i).getCodeInternoImplemento().equalsIgnoreCase(this.codeImplement)){
                response = true;
            }
        }

        return response;
    }

    /**
     * Metodo que permite evaluar si el implemento se encuentra disponible para trabajar
     * @return
     */
    public boolean isImplementAvailable(){

        boolean response=false;

        try {
            String[] dataTag = this.tagNFC.split(":");
            if (dataTag[3].equalsIgnoreCase("0")) {//indica que esta desocupada
                response = true;
            }
        }catch (Exception e){//por si pasa alguna wea XD
            response=false;
        }
        return  response;
    }

    /**
     * Metodo que permite evaluar si el implemento esta habilitado para trabajar con la maquinaria
     * @return
     */
    public boolean isImplementAvailableForMachine(){

        boolean response = false;

        String sqlQuery = "SELECT * FROM implementoHabilitado WHERE implementoHabilitado.implementoID = '"+this.codeImplement+"' AND implementoHabilitado.maquinariaID = '"+ this.codeInternoMachine+"'";
        Log.e(TAG, sqlQuery);
        Cursor cursor = this.handlerDBPersistence.consultarRegistros(sqlQuery);
        Log.e(TAG, cursor.getCount()+" tiene datos la puta caga XD ");
        if (cursor.getCount()==0){
            response = true;
        }

        return  response;
    }
}
