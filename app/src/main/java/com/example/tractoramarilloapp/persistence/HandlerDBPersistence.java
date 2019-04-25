package com.example.tractoramarilloapp.persistence;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.tractoramarilloapp.model.Faena;
import com.example.tractoramarilloapp.model.Implemento;
import com.example.tractoramarilloapp.model.Maquinaria;
import com.example.tractoramarilloapp.model.Predio;
import com.example.tractoramarilloapp.model.UserSession;

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

        /*demo tabla usuarios*/
        db.execSQL("CREATE TABLE " + UserContract.UserContractEntry.TABLE_NAME + " ("
                + UserContract.UserContractEntry.ID_USER + " TEXT NOT NULL, "
                + UserContract.UserContractEntry.NAME_USER + " TEXT NOT NULL, "
                + UserContract.UserContractEntry.ROL + " TEXT NOT NULL, "
                + UserContract.UserContractEntry.RUT_USER + " TEXT NOT NULL, "
                + "UNIQUE (" + UserContract.UserContractEntry.ID_USER + "))");

        /*demo tabla predio*/
        db.execSQL("CREATE TABLE " + PredioContract.PredioContractEntry.TABLE_NAME + " ("
                + PredioContract.PredioContractEntry.CODE_PREDIO + " TEXT NOT NULL, "
                + PredioContract.PredioContractEntry.PREDIO_NAME + " TEXT NOT NULL, "
                + "UNIQUE (" + PredioContract.PredioContractEntry.CODE_PREDIO + "))");

        /*demo tabla faena*/
        db.execSQL("CREATE TABLE " + FaenaContract.FaenaContractEntry.TABLE_NAME + " ("
                + FaenaContract.FaenaContractEntry.CODE_FAENA + " TEXT NOT NULL, "
                + FaenaContract.FaenaContractEntry.FAENA_NAME + " TEXT NOT NULL, "
                + "UNIQUE (" + FaenaContract.FaenaContractEntry.CODE_FAENA + "))");

        /*demo tabla maquinaria*/
        db.execSQL("CREATE TABLE " + MaquinariaContract.MaquinariaContractEntry.TABLE_NAME + " ("
                + MaquinariaContract.MaquinariaContractEntry.CODE_INTERNO_MACHINE + " TEXT NOT NULL, "
                + MaquinariaContract.MaquinariaContractEntry.COLOR_MACHINE + " TEXT NOT NULL, "
                + MaquinariaContract.MaquinariaContractEntry.MODEL_MACHINE + " TEXT NOT NULL, "
                + MaquinariaContract.MaquinariaContractEntry.NAME_MACHINE + " TEXT NOT NULL, "
                + MaquinariaContract.MaquinariaContractEntry.PATENT_MACHINE + " TEXT NOT NULL, "
                + MaquinariaContract.MaquinariaContractEntry.YEARD_MACHINE + " TEXT NOT NULL, "
                + MaquinariaContract.MaquinariaContractEntry.STATUS_MACHINE + " TEXT NOT NULL, "
                + "UNIQUE (" + MaquinariaContract.MaquinariaContractEntry.CODE_INTERNO_MACHINE + "))");

        /*demo tabla implementos*/
        db.execSQL("CREATE TABLE " + ImplementContract.ImplementContractEntry.TABLE_NAME + " ("
                + ImplementContract.ImplementContractEntry.ANO_IMPLEMENTO + " TEXT NOT NULL, "
                + ImplementContract.ImplementContractEntry.CAPACIDAD_IMPLEMENTO + " TEXT NOT NULL, "
                + ImplementContract.ImplementContractEntry.CODE_INTERNO_IMPLEMENTO + " TEXT NOT NULL, "
                + ImplementContract.ImplementContractEntry.FABRICANTE_IMPLEMENTO + " TEXT NOT NULL, "
                + ImplementContract.ImplementContractEntry.COLOR_IMPLEMENTO + " TEXT NOT NULL, "
                + ImplementContract.ImplementContractEntry.NAME_IMPLEMENTO + " TEXT NOT NULL, "
                + ImplementContract.ImplementContractEntry.STATUS_IMPLEMENTO + " TEXT NOT NULL, "
                + "UNIQUE (" + ImplementContract.ImplementContractEntry.CODE_INTERNO_IMPLEMENTO + "))");
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
     * Metodo que permite poder obtener todos los usuarios en el dispositivo
     * @return
     */
    public ArrayList<UserSession> getUser(){

        ArrayList<UserSession> listUser = new ArrayList<>();//definicion de la lista de sesiones

        //hacemos la consulta para obtener la informacion en forma de cursor
        Cursor cursor = getReadableDatabase().query(
                UserContract.UserContractEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        //desde el cursor obtenemos las listas activas asociadas al dispositivo
        cursor.moveToFirst();//vamos al primer elemento

        //obtenemos los indices de las columnas

        int IDUser = cursor.getColumnIndex(UserContract.UserContractEntry.ID_USER);
        int NameUser = cursor.getColumnIndex(UserContract.UserContractEntry.NAME_USER);
        int RutUser = cursor.getColumnIndex(UserContract.UserContractEntry.RUT_USER);
        int rol = cursor.getColumnIndex(UserContract.UserContractEntry.ROL);

        //recorremos el cursor para obtener la informacion y formar el objeto de interes
        while (!cursor.isAfterLast()){

            String IDUserV = cursor.getString(IDUser);
            String NameUserV = cursor.getString(NameUser);
            String RutUserV = cursor.getString(RutUser) ;
            String rolV =  cursor.getString(rol);


            //instanciamos un objeto del tipo sesion y lo agregamos a la lista
            listUser.add(new UserSession(IDUserV, NameUserV, RutUserV, rolV));
            cursor.moveToNext();
        }

        return listUser;

    }

    /**
     * Metodo que permite poder obtener todos los predios
     * @return
     */
    public ArrayList<Predio> getPredio(){

        ArrayList<Predio> listPredio = new ArrayList<>();//definicion de la lista de sesiones

        //hacemos la consulta para obtener la informacion en forma de cursor
        Cursor cursor = getReadableDatabase().query(
                PredioContract.PredioContractEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        //desde el cursor obtenemos las listas activas asociadas al dispositivo
        cursor.moveToFirst();//vamos al primer elemento

        //obtenemos los indices de las columnas

        int predioName = cursor.getColumnIndex(PredioContract.PredioContractEntry.PREDIO_NAME);
        int codePredio = cursor.getColumnIndex(PredioContract.PredioContractEntry.CODE_PREDIO);

        //recorremos el cursor para obtener la informacion y formar el objeto de interes
        while (!cursor.isAfterLast()){

            String predioNameV = cursor.getString(predioName);
            String codePredioV = cursor.getString(codePredio);


            //instanciamos un objeto del tipo sesion y lo agregamos a la lista
            listPredio.add(new Predio(predioNameV, codePredioV));
            cursor.moveToNext();
        }

        return listPredio;
    }

    /**
     * Metodo que permite poder obtener todos las faenas
     * @return
     */
    public ArrayList<Faena> getFaena(){

        ArrayList<Faena> listFaena = new ArrayList<>();//definicion de la lista de sesiones

        //hacemos la consulta para obtener la informacion en forma de cursor
        Cursor cursor = getReadableDatabase().query(
                FaenaContract.FaenaContractEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        //desde el cursor obtenemos las listas activas asociadas al dispositivo
        cursor.moveToFirst();//vamos al primer elemento

        //obtenemos los indices de las columnas

        int faneaName = cursor.getColumnIndex(FaenaContract.FaenaContractEntry.FAENA_NAME);
        int codeFaena = cursor.getColumnIndex(FaenaContract.FaenaContractEntry.CODE_FAENA);

        //recorremos el cursor para obtener la informacion y formar el objeto de interes
        while (!cursor.isAfterLast()){

            String faneaNameV = cursor.getString(faneaName);
            String codeFaenaV = cursor.getString(codeFaena);


            //instanciamos un objeto del tipo sesion y lo agregamos a la lista
            listFaena.add(new Faena(faneaNameV, codeFaenaV));
            cursor.moveToNext();
        }

        return listFaena;
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

    public long saveUsuario(UserSession userSession){

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                UserContract.UserContractEntry.TABLE_NAME,
                null,
                userSession.toConentValues()
        );
    }

    public long saveMaquina(Maquinaria maquinaria){

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                MaquinariaContract.MaquinariaContractEntry.TABLE_NAME,
                null,
                maquinaria.toContentValues()
        );
    }

    public long saveImplemento(Implemento implemento){

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                ImplementContract.ImplementContractEntry.TABLE_NAME,
                null,
                implemento.toContentValues()
        );
    }

    public long savePredio(Predio predio){

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                PredioContract.PredioContractEntry.TABLE_NAME,
                null,
                predio.toContentValues()
        );
    }

    public long saveFaena(Faena faena){

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                FaenaContract.FaenaContractEntry.TABLE_NAME,
                null,
                faena.toContentValues()
        );
    }

    //metodo que te permite ejecutar una sql

}
