package com.example.tractoramarilloapp.handlers;

import android.content.Context;
import android.util.Log;

import com.example.tractoramarilloapp.model.InformeOperaciones;
import com.example.tractoramarilloapp.persistence.HandlerDBPersistence;
import com.example.tractoramarilloapp.persistence.SessionClass;

import java.util.ArrayList;

/**
 * Clase con la responsabilidad de manejar todas las acciones asociadas al informe de datos a sincronizar
 */
public class HandlerInforme {

    private Context context;
    private HandlerDBPersistence handlerDBPersistence;

    /**
     * Constructor de la clase
     * @param context
     */
    public  HandlerInforme (Context context){
        this.context = context;
        this.handlerDBPersistence = new HandlerDBPersistence(this.context);
    }

    public int addElementToInforme(String idMaquinaria, String idUser, String predio){

        int response = 0;

        //obtenemos la informacion de la sesion con respecto al usuario
        ArrayList<SessionClass> listSession = this.handlerDBPersistence.getSessionActive("PENDING");//solo las que estan pendiente
        String tokenSession = listSession.get(listSession.size()-1).getSessionToken();//obtenemos el token del ultimo registro del compadre

        String sqlQueryInforme = "SELECT * FROM informeOperaciones";
        int lastID = this.handlerDBPersistence.getLastID(sqlQueryInforme, "idinformeOperaciones");

        if (lastID == -1){
            lastID = 1;
        }else{
            lastID++;
        }
        InformeOperaciones informeOperaciones = new InformeOperaciones(lastID, idMaquinaria, "-", "-", idUser, tokenSession, "-", "-", "-", "-", "-", "-", predio);

        if (this.handlerDBPersistence.saveInformeOperaciones(informeOperaciones) != -1){
            Log.e("TAG-INFORME", "INSERT INFORME OK");
            response = lastID;
        }else{
            Log.e("TAG-INFORME", "INSERT INFORME ERROR");
            response = -1;
        }

        return response;
    }

    /**
     * Metodo que permite poder editar informacion asociada a los valores de horometro en el informe
     * @param horoInicial
     * @param horoFinal
     * @param idInforme
     */
    public void changeValuesHorometro(String horoInicial, String horoFinal, String idInforme, String idImplemento, String horarioInicio, String horarioFinal, String idFaena, String statusSend, String isActive){

        String sqlUpdate = "UPDATE informeOperaciones set"
                + "horometroInicio = '"+horoInicial+"', "
                + "horometroFinal ='"+ horoFinal+"', "
                + "isImplementActive = '"+isActive+"', "
                + "idImplemento = '"+idImplemento+"', "
                + "horarioInicio = '"+horarioInicio+"', "
                + "horarioFinal = '"+horarioFinal+"', "
                + "idFaena ='"+idFaena+"', "
                + "statusSend = '"+statusSend+"' "
                + "WHERE idinformeOperaciones= "+idInforme;

        this.handlerDBPersistence.execSQLData(sqlUpdate);
    }
}
