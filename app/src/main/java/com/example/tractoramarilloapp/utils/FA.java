package com.example.tractoramarilloapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.tractoramarilloapp.model.UserSession;
import com.example.tractoramarilloapp.persistence.HandlerDBPersistence;
import com.example.tractoramarilloapp.persistence.InformeFaenaContract;
import com.example.tractoramarilloapp.persistence.InformeImplementoContract;
import com.example.tractoramarilloapp.persistence.InformeMaquinariaContract;
import com.example.tractoramarilloapp.persistence.SessionClass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Clase asociada al manejo de distintas funcionalidades y operaciones en procesos independientes
 * que permiten la manipulacion de la data y otras caracteristicas
 */
public class FA extends AppCompatActivity {

    /**
     * Metodo que permite generar el token de sesion en base al UUID del dispositivo y a la fecha actual
     * @return
     */
    public static String generateTokenSession(String userID){

        String response;
        String id = userID;
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        response = id+"_"+tsLong;
        Log.e("FA-Values", response);
        return response;
    }

    /**
     * Metodo que permite revisar si la pulsera es del tipo jefe u operador o desconocida
     * @param tagNFC
     * @return
     */
    public static int checkPulseraTag(String tagNFC){

        int response;

        try {
            String[] splitter = tagNFC.split(":");//dividimos por :

            //la posicion indica el tipo de pulsera
            if (splitter[1].equals("1")) {//pulsera del tipo JEFE
                response = 1;
            } else {
                if (splitter[1].equals("2")) {//pulsera del tipo OPERADOR
                    response = 2;
                } else {
                    response = -1;//pulsera no identificada
                }
            }
        }catch (Exception e){
            response = -1;//error en lectura de pulsera, no se puede hacer split

        }
        return response;
    }

    public static ArrayList<SessionClass> joinList(ArrayList<SessionClass> active, ArrayList<SessionClass> pending){

        ArrayList<SessionClass> joinValues = new ArrayList<>();
        for (int i=0; i< active.size(); i++){
            joinValues.add(active.get(i));
        }

        for (int j=0; j<pending.size(); j++){
            joinValues.add(pending.get(j));
        }

        return joinValues;
    }

    /**
     * Metodo que permite buscar la informacion del usuario
     * @param idUser
     * @param context
     * @return
     */
    public static UserSession getUserInformationByCode (String idUser, Context context){

        HandlerDBPersistence handlerDBPersistence = new HandlerDBPersistence(context);
        ArrayList<UserSession> userSessions = handlerDBPersistence.getUser();

        UserSession userSessionO = null;

        for (int i=0; i<userSessions.size(); i++){
            if (userSessions.get(i).getIDUser().equalsIgnoreCase(idUser)){
                userSessionO = userSessions.get(i);
                break;
            }
        }

        return userSessionO;
    }

    /**
     * Metodo que permite obtener la fecha actual del dispositivo
     * @return
     */
    public static String getCurrentDate(){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    /**
     * Metodo que permite limpiar el Shared Preferences de la aplicación
     * @return
     */
    public void clearShared(String namePreferences){
        SharedPreferences prefs = getSharedPreferences(namePreferences,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.remove("idUsuario");
        editor.remove("idPredio");
        editor.remove("namePredio");
        editor.remove("tagMaquinaria");
        editor.remove("nameMaquinaria");
        editor.remove("tagImplemento");
        editor.remove("idFaena");
        editor.remove("nameFaena");
        editor.remove("inicio_horometro");
        editor.remove("fin_horometro");
        editor.remove("inicio_implemento");
        editor.remove("fin_implemento");
        editor.remove("flagImplemento");
        editor.remove("comentarios");
        editor.remove("idMaquina_comentario");
        editor.remove("idImplemento_comentario");
        editor.commit();

    }

    public static void showInformationInforme(HandlerDBPersistence handlerDBPersistence){

        String sqlMaquina = "SELECT * FROM "+ InformeMaquinariaContract.InformeMaquinariaContractEntry.TABLE_NAME;
        String sqlImplemento = "SELECT * FROM "+ InformeImplementoContract.InformeImplementoContractEntry.TABLE_NAME;
        String sqlFaena = "SELECT * FROM "+ InformeFaenaContract.InformeFaenaContractEntry.TABLE_NAME;

        Cursor c1 = handlerDBPersistence.consultarRegistros(sqlMaquina);
        Cursor c2 = handlerDBPersistence.consultarRegistros(sqlImplemento);
        Cursor c3 = handlerDBPersistence.consultarRegistros(sqlFaena);

        showCursor(c1);
        showCursor(c2);
        showCursor(c3);

    }

    public static void showInformationInformeSessionTAG(HandlerDBPersistence handlerDBPersistence,String sessionTAG){

        String sqlMaquina = "SELECT * FROM "+ InformeMaquinariaContract.InformeMaquinariaContractEntry.TABLE_NAME + " WHERE sessionTAG='"+sessionTAG+"'";
        String sqlImplemento = "SELECT * FROM "+ InformeImplementoContract.InformeImplementoContractEntry.TABLE_NAME + " WHERE sessionTAG='"+sessionTAG+"'";
        String sqlFaena = "SELECT * FROM "+ InformeFaenaContract.InformeFaenaContractEntry.TABLE_NAME + " WHERE sessionTAG='"+sessionTAG+"'";

        Cursor c1 = handlerDBPersistence.consultarRegistros(sqlMaquina);
        Cursor c2 = handlerDBPersistence.consultarRegistros(sqlImplemento);
        Cursor c3 = handlerDBPersistence.consultarRegistros(sqlFaena);

        showCursor(c1);
        showCursor(c2);
        showCursor(c3);

    }

    public static void showCursor(Cursor c1){

        if (c1!= null && c1.getCount()>0){
            c1.moveToFirst();

            while (!c1.isAfterLast()) {
                String[] colNames = c1.getColumnNames();
                String response = "";

                for (int i=0; i<colNames.length; i++){
                    int index = c1.getColumnIndex(colNames[i]);
                    String value = c1.getString(index);
                    response = response+"Name:"+colNames[i]+"-Value:"+value+" ";
                }
                Log.e("TAG-QUERY-VIEW", response);
                c1.moveToNext();
            }
        }
    }
}
