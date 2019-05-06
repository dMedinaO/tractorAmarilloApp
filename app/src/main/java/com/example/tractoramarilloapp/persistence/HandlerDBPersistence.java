package com.example.tractoramarilloapp.persistence;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.tractoramarilloapp.model.Comentarios;
import com.example.tractoramarilloapp.model.Faena;
import com.example.tractoramarilloapp.model.Implemento;
import com.example.tractoramarilloapp.model.InformeFaena;
import com.example.tractoramarilloapp.model.InformeImplemento;
import com.example.tractoramarilloapp.model.InformeMaquinaria;
import com.example.tractoramarilloapp.model.Maquinaria;
import com.example.tractoramarilloapp.model.Predio;
import com.example.tractoramarilloapp.model.TipoMaquinaria;
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
        db.execSQL("CREATE TABLE IF NOT EXISTS " + SessionClassContract.SessionClassContractEntry.TABLE_NAME + " ("
                + SessionClassContract.SessionClassContractEntry.TOKEN + " TEXT NOT NULL, "
                + SessionClassContract.SessionClassContractEntry.STATUS + " TEXT NOT NULL, "
                + SessionClassContract.SessionClassContractEntry.START_SESSION + " TEXT NOT NULL, "
                + SessionClassContract.SessionClassContractEntry.END_SESSION + " TEXT NOT NULL, "
                + SessionClassContract.SessionClassContractEntry.CLOSE_SESSION_KIND + " TEXT NOT NULL, "
                + SessionClassContract.SessionClassContractEntry.SESSION_KIND + " TEXT NOT NULL, "
                + SessionClassContract.SessionClassContractEntry.USER_SESSION + " TEXT NOT NULL, "
                + "UNIQUE (" + SessionClassContract.SessionClassContractEntry.TOKEN + "))");

        /*demo tabla usuarios*/
        db.execSQL("CREATE TABLE IF NOT EXISTS " + UserContract.UserContractEntry.TABLE_NAME + " ("
                + UserContract.UserContractEntry.ID_USER + " TEXT NOT NULL, "
                + UserContract.UserContractEntry.NAME_USER + " TEXT NOT NULL, "
                + UserContract.UserContractEntry.ROL + " TEXT NOT NULL, "
                + UserContract.UserContractEntry.RUT_USER + " TEXT NOT NULL, "
                + "UNIQUE (" + UserContract.UserContractEntry.ID_USER + "))");

        /*demo tabla predio*/
        db.execSQL("CREATE TABLE IF NOT EXISTS " + PredioContract.PredioContractEntry.TABLE_NAME + " ("
                + PredioContract.PredioContractEntry.CODE_PREDIO + " TEXT NOT NULL, "
                + PredioContract.PredioContractEntry.PREDIO_NAME + " TEXT NOT NULL, "
                + "UNIQUE (" + PredioContract.PredioContractEntry.CODE_PREDIO + "))");

        /*demo tabla faena*/
        db.execSQL("CREATE TABLE IF NOT EXISTS " + FaenaContract.FaenaContractEntry.TABLE_NAME + " ("
                + FaenaContract.FaenaContractEntry.CODE_FAENA + " TEXT NOT NULL, "
                + FaenaContract.FaenaContractEntry.FAENA_NAME + " TEXT NOT NULL, "
                + FaenaContract.FaenaContractEntry.CODE_IMPLEMENTO + " TEXT NOT NULL, "
                + "UNIQUE (" + FaenaContract.FaenaContractEntry.CODE_FAENA + "))");

        /*demo tabla maquinaria*/
        db.execSQL("CREATE TABLE IF NOT EXISTS " + MaquinariaContract.MaquinariaContractEntry.TABLE_NAME + " ("
                + MaquinariaContract.MaquinariaContractEntry.CODE_INTERNO_MACHINE + " TEXT NOT NULL, "
                + MaquinariaContract.MaquinariaContractEntry.COLOR_MACHINE + " TEXT NOT NULL, "
                + MaquinariaContract.MaquinariaContractEntry.MODEL_MACHINE + " TEXT NOT NULL, "
                + MaquinariaContract.MaquinariaContractEntry.NAME_MACHINE + " TEXT NOT NULL, "
                + MaquinariaContract.MaquinariaContractEntry.PATENT_MACHINE + " TEXT NOT NULL, "
                + MaquinariaContract.MaquinariaContractEntry.YEARD_MACHINE + " TEXT NOT NULL, "
                + MaquinariaContract.MaquinariaContractEntry.STATUS_MACHINE + " TEXT NOT NULL, "
                + MaquinariaContract.MaquinariaContractEntry.KIND_MACHINE + " TEXT NOT NULL, "
                + MaquinariaContract.MaquinariaContractEntry.MARK_MACHINE + " TEXT NOT NULL, "
                + "UNIQUE (" + MaquinariaContract.MaquinariaContractEntry.CODE_INTERNO_MACHINE + "))");

        /*demo tabla implementos*/
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ImplementContract.ImplementContractEntry.TABLE_NAME + " ("
                + ImplementContract.ImplementContractEntry.ANO_IMPLEMENTO + " TEXT NOT NULL, "
                + ImplementContract.ImplementContractEntry.CAPACIDAD_IMPLEMENTO + " TEXT NOT NULL, "
                + ImplementContract.ImplementContractEntry.CODE_INTERNO_IMPLEMENTO + " TEXT NOT NULL, "
                + ImplementContract.ImplementContractEntry.FABRICANTE_IMPLEMENTO + " TEXT NOT NULL, "
                + ImplementContract.ImplementContractEntry.COLOR_IMPLEMENTO + " TEXT NOT NULL, "
                + ImplementContract.ImplementContractEntry.NAME_IMPLEMENTO + " TEXT NOT NULL, "
                + ImplementContract.ImplementContractEntry.STATUS_IMPLEMENTO + " TEXT NOT NULL, "
                + "UNIQUE (" + ImplementContract.ImplementContractEntry.CODE_INTERNO_IMPLEMENTO + "))");

        /*demo tabla tipoMaquinaria*/
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TipoMaquinariaContract.TipoMaquinariaContractEntry.TABLE_NAME + " ("
                + TipoMaquinariaContract.TipoMaquinariaContractEntry.CODE_INTERNO + " TEXT NOT NULL, "
                + TipoMaquinariaContract.TipoMaquinariaContractEntry.NAME_TIPO + " TEXT NOT NULL )");

        /*demo tabla tipoHabilitado*/
        db.execSQL("CREATE TABLE IF NOT EXISTS  tipoHabilitado ("
                + "operadorID TEXT NOT NULL, "
                + "tipoMaquinariaID TEXT NOT NULL )");

        /*demo tabla implementoHabilitado*/
        db.execSQL("CREATE TABLE IF NOT EXISTS  implementoHabilitado ("
                + "implementoID TEXT NOT NULL, "
                + "maquinariaID TEXT NOT NULL )");

        /*demo tabla informe maquinaria, se asocia a la informacion del uso de maquinarias*/
        db.execSQL("CREATE TABLE IF NOT EXISTS informeMaquinaria ("
                + "idinformeMaquinaria INTEGER  NOT NULL, "
                + "idMaquinaria TEXT NOT NULL, "
                + "idPredio TEXT NOT NULL, "
                + "horometroInicio TEXT NOT NULL, "
                + "horometroFinal TEXT NOT NULL, "
                + "userID TEXT NOT NULL, "
                + "sessionTAG TEXT NOT NULL, "
                + "closeSessionKind TEXT NOT NULL, "
                + "statusSend TEXT NOT NULL, "
                + "UNIQUE (idinformeMaquinaria))");

        /*demo tabla informe implementos, se asocia a la informacion del uso de implementos en un implemento con respecto a una maquinaria
        * para el desarrollo de una faena*/
        db.execSQL("CREATE TABLE IF NOT EXISTS informeUsoImplemento ("
                + "idinformeImplemento INTEGER  NOT NULL, "
                + "idImplemento TEXT NOT NULL, "
                + "horaInicio TEXT NOT NULL, "
                + "horaFinal TEXT NOT NULL, "
                + "userID TEXT NOT NULL, "
                + "sessionTAG TEXT NOT NULL, "
                + "idInformeMaquinaria TEXT NOT NULL, "
                + "statusSend TEXT NOT NULL, "
                + "UNIQUE (idinformeImplemento))");

        /*demo tabla informe faena, se asocia principalmente a los trabajos realizados con la maquinaria*/
        db.execSQL("CREATE TABLE IF NOT EXISTS informeFaena ("
                + "idinformeFaena INTEGER  NOT NULL, "
                + "idFaena TEXT NOT NULL, "
                + "horaInicio TEXT NOT NULL, "
                + "horaFinal TEXT NOT NULL, "
                + "userID TEXT NOT NULL, "
                + "sessionTAG TEXT NOT NULL, "
                + "idInformeMaquinaria TEXT NOT NULL, "
                + "statusSend TEXT NOT NULL, "
                + "UNIQUE (idinformeFaena))");

        /*demo tabla comentario general*/
        db.execSQL("CREATE TABLE IF NOT EXISTS comentario ("
                + "idComentario INTEGER  NOT NULL, "
                + "descripcion TEXT NOT NULL, "
                + "horaComentario TEXT NOT NULL, "
                + "userID TEXT NOT NULL, "
                + "UNIQUE (idComentario))");

        /*demo tabla comentario falla general, puede ser implemento o maquinaria*/
        db.execSQL("CREATE TABLE IF NOT EXISTS fallaHerramienta ("
                + "idFallaHerramienta INTEGER  NOT NULL, "
                + "descripcionFalla TEXT NOT NULL, "
                + "horaNotificacion TEXT NOT NULL, "
                + "userID TEXT NOT NULL, "
                + "implementoID TEXT, "
                + "maquinariaID TEXT, "
                + "UNIQUE (idFallaHerramienta))");

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

        cursor.close();
        return listSession;

    }

    /**
     * Metodo que permite poder obtener todos los usuarios en el dispositivo
     * @return
     */
    public ArrayList<UserSession> getUser(){

        ArrayList<UserSession> listUser = new ArrayList<>();//definicion de la lista de sesiones

        //hacemos la consulta para obtener la informacion en forma de cursor
        Cursor cursor = this.getReadableDatabase().query(
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

        cursor.close();
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

        cursor.close();
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
        int codeImplemento = cursor.getColumnIndex(FaenaContract.FaenaContractEntry.CODE_IMPLEMENTO);

        //recorremos el cursor para obtener la informacion y formar el objeto de interes
        while (!cursor.isAfterLast()){

            String faneaNameV = cursor.getString(faneaName);
            String codeFaenaV = cursor.getString(codeFaena);
            String codeImplementoV = cursor.getString(codeImplemento);


            //instanciamos un objeto del tipo sesion y lo agregamos a la lista
            listFaena.add(new Faena(faneaNameV, codeFaenaV, codeImplementoV));
            cursor.moveToNext();
        }

        cursor.close();
        return listFaena;
    }

    /**
     * Metodo que permite poder obtener todos las maquinarias
     * @return
     */
    public ArrayList<Maquinaria> getMaquinariaList(){

        ArrayList<Maquinaria> listMaquina = new ArrayList<>();//definicion de la lista de sesiones

        //hacemos la consulta para obtener la informacion en forma de cursor
        Cursor cursor = getReadableDatabase().query(
                MaquinariaContract.MaquinariaContractEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        //desde el cursor obtenemos las listas activas asociadas al dispositivo
        cursor.moveToFirst();//vamos al primer elemento

        //obtenemos los indices de las columnas

        int codeInternoMachine = cursor.getColumnIndex(MaquinariaContract.MaquinariaContractEntry.CODE_INTERNO_MACHINE);
        int nameMachine = cursor.getColumnIndex(MaquinariaContract.MaquinariaContractEntry.NAME_MACHINE);
        int markMachine = cursor.getColumnIndex(MaquinariaContract.MaquinariaContractEntry.MARK_MACHINE);
        int modelMachine = cursor.getColumnIndex(MaquinariaContract.MaquinariaContractEntry.MODEL_MACHINE);
        int yeardMachine = cursor.getColumnIndex(MaquinariaContract.MaquinariaContractEntry.YEARD_MACHINE);
        int statusMachine = cursor.getColumnIndex(MaquinariaContract.MaquinariaContractEntry.STATUS_MACHINE);
        int patentMachine = cursor.getColumnIndex(MaquinariaContract.MaquinariaContractEntry.PATENT_MACHINE);
        int colorMachine = cursor.getColumnIndex(MaquinariaContract.MaquinariaContractEntry.COLOR_MACHINE);
        int tipoMachine = cursor.getColumnIndex(MaquinariaContract.MaquinariaContractEntry.KIND_MACHINE);

        //recorremos el cursor para obtener la informacion y formar el objeto de interes
        while (!cursor.isAfterLast()){

            String codeInternoMachineV = cursor.getString(codeInternoMachine);
            String nameMachineV = cursor.getString(nameMachine);
            String markMachineV = cursor.getString(markMachine);
            String modelMachineV = cursor.getString(modelMachine);
            String yeardMachineV = cursor.getString(yeardMachine);
            String statusMachineV = cursor.getString(statusMachine);
            String patentMachineV = cursor.getString(patentMachine);
            String colorMachineV = cursor.getString(colorMachine);
            String tipoMachineV = cursor.getString(tipoMachine);

            //instanciamos un objeto del tipo sesion y lo agregamos a la lista
            listMaquina.add(new Maquinaria(nameMachineV, markMachineV, modelMachineV, yeardMachineV, statusMachineV, patentMachineV, colorMachineV, codeInternoMachineV, tipoMachineV));
            cursor.moveToNext();
        }

        cursor.close();
        return listMaquina;
    }

    public ArrayList<Implemento> getImplementos(){

        ArrayList<Implemento> listImplemento = new ArrayList<>();

        //hacemos la consulta para obtener la informacion en forma de cursor
        Cursor cursor = getReadableDatabase().query(
                ImplementContract.ImplementContractEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        //desde el cursor obtenemos las listas activas asociadas al dispositivo
        cursor.moveToFirst();//vamos al primer elemento

        int codeInterno = cursor.getColumnIndex(ImplementContract.ImplementContractEntry.CODE_INTERNO_IMPLEMENTO);
        int nameImplement = cursor.getColumnIndex(ImplementContract.ImplementContractEntry.NAME_IMPLEMENTO);
        int anoImplement = cursor.getColumnIndex(ImplementContract.ImplementContractEntry.ANO_IMPLEMENTO);
        int capacidadImplement = cursor.getColumnIndex(ImplementContract.ImplementContractEntry.CAPACIDAD_IMPLEMENTO);
        int colorImplement = cursor.getColumnIndex(ImplementContract.ImplementContractEntry.COLOR_IMPLEMENTO);
        int statusImplement = cursor.getColumnIndex(ImplementContract.ImplementContractEntry.STATUS_IMPLEMENTO);
        int fabricanteImplement = cursor.getColumnIndex(ImplementContract.ImplementContractEntry.FABRICANTE_IMPLEMENTO);

        //recorremos el cursor para obtener la informacion y formar el objeto de interes
        while (!cursor.isAfterLast()){

            String codeInternoV = cursor.getString(codeInterno);
            String nameImplementV = cursor.getString(nameImplement);
            String anoImplementV = cursor.getString(anoImplement);
            String capacidadImplementV = cursor.getString(capacidadImplement);
            String colorImplementV = cursor.getString(colorImplement);
            String statusImplementV = cursor.getString(statusImplement);
            String fabricanteImplementV = cursor.getString(fabricanteImplement);

            //instanciamos un objeto del tipo sesion y lo agregamos a la lista
            listImplemento.add(new Implemento(nameImplementV, statusImplementV, anoImplementV, fabricanteImplementV, colorImplementV, capacidadImplementV, codeInternoV));
            cursor.moveToNext();
        }

        cursor.close();
        return  listImplemento;
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

    public long saveTipoMaquina(TipoMaquinaria tipoMaquinaria){

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                TipoMaquinariaContract.TipoMaquinariaContractEntry.TABLE_NAME,
                null,
                tipoMaquinaria.toContentValus()
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

    public long saveInformeMaquinaria(InformeMaquinaria informeMaquinaria){

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                InformeMaquinariaContract.InformeMaquinariaContractEntry.TABLE_NAME,
                null,
                informeMaquinaria.toContentValues()
        );
    }

    public long saveInformeImplemento(InformeImplemento informeImplemento){

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                InformeImplementoContract.InformeImplementoContractEntry.TABLE_NAME,
                null,
                informeImplemento.toContentValues()
        );
    }

    public long saveInformeFaena(InformeFaena informeFaena){

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                InformeFaenaContract.InformeFaenaContractEntry.TABLE_NAME,
                null,
                informeFaena.toContentValues()
        );
    }

    public long saveComentario(Comentarios comentarios){

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                ComentarioContract.ComentarioContractEntry.TABLE_NAME,
                null,
                comentarios.toConentValues()
        );
    }

    //<SQL SELECT GENERICO>
    public Cursor consultarRegistros(String sql_select) {

        Cursor cursor = null;

        try {

            SQLiteDatabase db = getReadableDatabase();
            cursor = db.rawQuery(sql_select, null);

            if (cursor != null) {
                cursor.moveToFirst();
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return cursor;

    }
    //</SQL SELECT GENERICO>

    //<METODO QUE PERMITE GRABAR DE MANERA GENERICA EN LA BD
    public int execSQLData(String sqlValues){

        int response = 0;
        try{
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL(sqlValues);
        }catch (Exception e){
            response = 1;
            Log.e("ERROR DB", "ERROR SQL");
        }


        return response;

    }

    /**
     * Metodo que permite obtener el ultimo ID de un informe con el fin de poder agregar un nuevo elemento a partir de este
     * @param sqlQuery
     * @
     * @return
     */
    public int getLastID(String sqlQuery, String identificador){

        int lastID=-1;

        Cursor cursor = null;

        try {

            SQLiteDatabase db = getReadableDatabase();
            cursor = db.rawQuery(sqlQuery, null);

            if (cursor != null) {
                cursor.moveToLast();
                int idValue = cursor.getColumnIndex(identificador);
                lastID = cursor.getInt(idValue);

            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return lastID;
    }


}
