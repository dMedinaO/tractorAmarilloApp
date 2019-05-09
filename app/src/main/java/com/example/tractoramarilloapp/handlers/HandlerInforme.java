package com.example.tractoramarilloapp.handlers;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.tractoramarilloapp.model.InformeFaena;
import com.example.tractoramarilloapp.model.InformeImplemento;
import com.example.tractoramarilloapp.model.InformeMaquinaria;
import com.example.tractoramarilloapp.persistence.HandlerDBPersistence;
import com.example.tractoramarilloapp.persistence.InformeFaenaContract;
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

    public int addElementToInforme(String idMaquinaria, String idUser, String predio, String tokenSession, String tokenPreview){

        int response = 0;

        String sqlQueryInforme = "SELECT * FROM informeMaquinaria";
        int lastID = this.handlerDBPersistence.getLastID(sqlQueryInforme, "idinformeMaquinaria");

        if (lastID == -1){
            lastID = 1;
        }else{
            lastID++;
        }

        InformeMaquinaria informeMaquinaria = new InformeMaquinaria(lastID, idMaquinaria, "-", "-", idUser, tokenSession, "NOT_YET", "-", predio, tokenPreview);


        if (this.handlerDBPersistence.saveInformeMaquinaria(informeMaquinaria) != -1){
            Log.e("TAG-INFORME", "INSERT INFORME OK "+lastID);
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
    public void changeValuesHorometro(String horoInicial, String horoFinal, String idInforme){

        String sqlUpdate = "UPDATE informeMaquinaria set "
                + "horometroInicio = '"+horoInicial+"', "
                + "horometroFinal = '"+ horoFinal+"' "
                + "WHERE idinformeMaquinaria= "+idInforme;

        this.handlerDBPersistence.execSQLData(sqlUpdate);
    }


    /**
     * Metodo que permite poder editar informacion asociada a los valores de inicio del implemento
     * @param horoInicial
     * @param horoFinal
     * @param idInforme
     */
    public void changeValuesHoraImplemento(String horoInicial, String horoFinal, String idInforme){

        String sqlUpdate = "UPDATE informeUsoImplemento set "
                + "horaInicio = '"+horoInicial+"', "
                + "horaFinal = '"+ horoFinal+"' "
                + "WHERE idinformeImplemento= "+idInforme;

        this.handlerDBPersistence.execSQLData(sqlUpdate);
    }

    public int addElementToInformeImplemento(String idImplemento, String idUser, String tokenSession, String horarioInicial, String idInformeMaquinaria){

        int response = 0;

        String sqlQueryInforme = "SELECT * FROM informeUsoImplemento";
        int lastID = this.handlerDBPersistence.getLastID(sqlQueryInforme, "idinformeImplemento");

        if (lastID == -1){
            lastID = 1;
        }else{
            lastID++;
        }

        InformeImplemento informeImplemento = new InformeImplemento(lastID, idImplemento, horarioInicial, "-", idUser, tokenSession, "NOT_YET", idInformeMaquinaria);

        if (this.handlerDBPersistence.saveInformeImplemento(informeImplemento) != -1){
            Log.e("TAG-INFORME", "INSERT INFORME OK "+ lastID);
            response = lastID;
        }else{
            Log.e("TAG-INFORME", "INSERT INFORME ERROR");
            response = -1;
        }

        return response;

    }

    public int addElementToInformeFaena(String idFaena, String idUser, String tokenSession, String horarioInicial, String idInformeMaquinaria){

        int response = 0;

        String sqlQueryInforme = "SELECT * FROM "+ InformeFaenaContract.InformeFaenaContractEntry.TABLE_NAME;
        int lastID = this.handlerDBPersistence.getLastID(sqlQueryInforme, InformeFaenaContract.InformeFaenaContractEntry.ID_INFORME);

        if (lastID == -1){
            lastID = 1;
        }else{
            lastID++;
        }

        InformeFaena informeFaena = new InformeFaena(lastID, idFaena, horarioInicial, "-", idUser, tokenSession, "NOT_YET", idInformeMaquinaria);

        if (this.handlerDBPersistence.saveInformeFaena(informeFaena) != -1){
            Log.e("TAG-INFORME", "INSERT INFORME OK "+ lastID);
            response = lastID;
        }else{
            Log.e("TAG-INFORME", "INSERT INFORME ERROR");
            response = -1;
        }

        return response;

    }

    public void showInformeDetail(){

        String query1 = "SELECT * FROM informeMaquinaria";
        String query2 = "SELECT * FROM informeUsoImplemento";
        String query3 = "SELECT * FROM informeFaena";

        Cursor cursor1 = this.handlerDBPersistence.consultarRegistros(query1);
        Cursor cursor2 = this.handlerDBPersistence.consultarRegistros(query2);
        Cursor cursor3 = this.handlerDBPersistence.consultarRegistros(query3);


        String values = "Informe Maquinaria:"+cursor1.getCount()+" - Informe Implemento: "+cursor2.getCount()+" - Informe Faena: "+cursor3.getCount();
        Log.e("TAG-HANDLER", values);

    }

    public void showAllInformeDetail(){

        String query1 = "SELECT * FROM informeMaquinaria";
        String query2 = "SELECT * FROM informeUsoImplemento";
        String query3 = "SELECT * FROM informeFaena";

        Cursor cursor1 = this.handlerDBPersistence.consultarRegistros(query1);
        Cursor cursor2 = this.handlerDBPersistence.consultarRegistros(query2);
        Cursor cursor3 = this.handlerDBPersistence.consultarRegistros(query3);


        String values = "Informe Maquinaria: "+cursor1+"\n"+
                        "Informe Implemento: "+cursor2+"\n"+
                        "Informe Faena: "+cursor3;
        Log.e("TAG-HANDLER", values);

    }

    /**
     * Metodo que permite cerrar un informe del tipo Maquinaria
     * @param idInforme
     * @param horometroFinal
     * @param closeSessionKind
     * @return
     */
    public int closeInformeMaquinaria(String idInforme, String horometroFinal, String closeSessionKind, String tokenPreview){

        //creamos el SQL y exportamos la data
        String sqlQuery = "UPDATE informeMaquinaria SET horometroFinal= '"+horometroFinal+"', closeSessionKind = '"+ closeSessionKind+"', tokenPrevio ='"+tokenPreview+"' where idinformeMaquinaria = "+ idInforme;
        int response = this.handlerDBPersistence.execSQLData(sqlQuery);

        return response;
    }

    /**
     * Metodo que permite cerrar el informe de implemento
     * @param idInforme
     * @return
     */
    public int closeInformeImplemento(String idInforme, String horaFinal){

        //creamos el SQL y exportamos la data
        String sqlQuery = "UPDATE informeUsoImplemento SET horaFinal= '"+horaFinal+"' where idinformeImplemento = "+ idInforme;
        int response = this.handlerDBPersistence.execSQLData(sqlQuery);

        return response;
    }

}
