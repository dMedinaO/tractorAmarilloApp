package com.example.tractoramarilloapp.persistence;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Clase asociada a la manipulacion de datos desde el sistema de almacenamienot persistente enfocado en el dispositivo, tiene las acciones necesarias a la
 * creacion de la base de datos, ejecucion de procesos sql, insercion de elementos y consultas de procesos especificos
 */
public class HandlerDBPersistence extends SQLiteOpenHelper {

    /**
     * Constructor de la clase, debido a que extiende a SQLiteOpenHelper, se hace llamada al super constructor asociado a la data perteneciente a la herencia obtenida
     * @param context
     */
    public HandlerDBPersistence(Context context){

        super(context, "tractor_amarillo_lite", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //instanciamos la tabla de sesion para que quede habilitada a la hora de crear una sesion de usuario
        db.execSQL("CREATE TABLE " + SessionClassContract.SessionClassContractEntry.TABLE_NAME + " ("
                + SessionClassContract.SessionClassContractEntry.TOKEN + " TEXT NOT NULL, "
                + SessionClassContract.SessionClassContractEntry.STATUS + " TEXT NOT NULL, "
                + SessionClassContract.SessionClassContractEntry.START_SESSION + " TEXT NOT NULL, "
                + SessionClassContract.SessionClassContractEntry.END_SESSION + " TEXT NOT NULL, "
                + SessionClassContract.SessionClassContractEntry.CLOSE_SESSION_KIND + " TEXT NOT NULL, "
                + SessionClassContract.SessionClassContractEntry.SESSION_KIND + " TEXT NOT NULL, "
                + SessionClassContract.SessionClassContractEntry.USER_SESSION + " TEXT NOT NULL, "
                + "UNIQUE (" + SessionClassContract.SessionClassContractEntry.TOKEN + "))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Metodo que permite poder obtener todas las sesiones activas en el dispositivo
     * @return
     */
    public ArrayList<SessionClass> getSessionActive(String params){

        ArrayList<SessionClass> listSession = new ArrayList<>();//definicion de la lista de sesiones

        //hacemos la consulta para obtener la informacion en forma de cursor
        Cursor cursor = getReadableDatabase().query(
                SessionClassContract.SessionClassContractEntry.TABLE_NAME,
                null,
                SessionClassContract.SessionClassContractEntry.STATUS + " LIKE ?",
                new String[]{params},
                null,
                null,
                null);

        //desde el cursor obtenemos las listas activas asociadas al dispositivo
        cursor.moveToFirst();//vamos al primer elemento

        //obtenemos los indices de las columnas

        int sessionToken = cursor.getColumnIndex(SessionClassContract.SessionClassContractEntry.TOKEN);
        int status = cursor.getColumnIndex(SessionClassContract.SessionClassContractEntry.STATUS);
        int startSessionDate = cursor.getColumnIndex(SessionClassContract.SessionClassContractEntry.START_SESSION);
        int endSessionDate = cursor.getColumnIndex(SessionClassContract.SessionClassContractEntry.END_SESSION);
        int closeSessionKind = cursor.getColumnIndex(SessionClassContract.SessionClassContractEntry.CLOSE_SESSION_KIND);
        int sessionKind = cursor.getColumnIndex(SessionClassContract.SessionClassContractEntry.SESSION_KIND);
        int userAssociated = cursor.getColumnIndex(SessionClassContract.SessionClassContractEntry.USER_SESSION);

        //recorremos el cursor para obtener la informacion y formar el objeto de interes
        while (!cursor.isAfterLast()){

            String sessionTokenV = cursor.getString(sessionToken);
            String statusV = cursor.getString(status);
            String startSessionDateV = cursor.getString(startSessionDate) ;
            String endSessionDateV =  cursor.getString(endSessionDate);
            String closeSessionKindV = cursor.getString(closeSessionKind);
            String sessionKindV = cursor.getString(sessionKind);
            String userAssociatedV = cursor.getString(userAssociated);

            //instanciamos un objeto del tipo sesion y lo agregamos a la lista
            listSession.add(new SessionClass(sessionTokenV, statusV, startSessionDateV, endSessionDateV, closeSessionKindV, sessionKindV, userAssociatedV));
            cursor.moveToNext();
        }

        return listSession;
    }

    /**
     * Metodo que permite registrar una sesion en la base de datos
     * @param sessionClass
     * @return
     */
    public long saveSessionInDB(SessionClass sessionClass){

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                SessionClassContract.SessionClassContractEntry.TABLE_NAME,
                null,
                sessionClass.toConentValues()
            );
    }
}
