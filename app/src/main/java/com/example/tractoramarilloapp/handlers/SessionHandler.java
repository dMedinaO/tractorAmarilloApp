package com.example.tractoramarilloapp.handlers;

import android.content.Context;
import android.util.Log;

import com.example.tractoramarilloapp.model.UserSession;
import com.example.tractoramarilloapp.persistence.HandlerDBPersistence;
import com.example.tractoramarilloapp.persistence.SessionClass;
import com.example.tractoramarilloapp.persistence.SessionClassContract;
import com.example.tractoramarilloapp.utils.FA;

import java.util.ArrayList;

/**
 * Clase que tiene la responsabilidad de manipular todas las acciones asociadas a una sesion en el dispositivo, permite crear sesiones, buscar y editar sesiones existentes, asi como tambien
 * eliminar sesiones.
 */
public class SessionHandler {

    //atributos de la clase
    private Context context;
    private static String TAG="SESSION";
    private HandlerDBPersistence handlerDBPersistence;
    private String tokenSession;

    /**
     * Constructor de la clase
     * @param context
     */
    public SessionHandler(Context context){

        this.context = context;
        this.handlerDBPersistence = new HandlerDBPersistence(this.context);
    }

    /**
     * Metodo que permite poder evaluar el manejo de sesion, consta con los pasos asociados a la revision de la sesion , si existen procesos, etc.
     * @param TagNFC
     * @return
     */
    public int createSession(String TagNFC){

        int response = 0;

        //obtenemos las sesiones activas en el dispositivo
        ArrayList<SessionClass> active = this.handlerDBPersistence.getSessionActive("ACTIVE");
        ArrayList<SessionClass> pending = this.handlerDBPersistence.getSessionActive("PENDING");
        this.handlerDBPersistence.close();

        //unimos las listas
        ArrayList<SessionClass> activeSession = FA.joinList(active, pending);

        int kindTag = FA.checkPulseraTag(TagNFC);//obtenemos tipo de pulsera
        Log.e(TAG, kindTag+" tipo pulsera");
        Log.e(TAG, activeSession.size()+" active/pending session");
        if (kindTag == -1){//pulsera con error
            Log.e(TAG, "PULSERA ERROR");
            response = -1;
        }else{
            if (activeSession.size() == 0) {//no hay sesiones activas
                if (kindTag == 1){//tipo JEFE
                    String [] valuesTag = TagNFC.split(":");
                    int valueInsert = this.createSessionInSystem(kindTag, valuesTag[2]);//index dos porque en la posicion 3 existe el usuario
                    if (valueInsert == -1){
                        response=-2;
                        Log.e(TAG, "DB ERROR");
                    }else{

                        if (valueInsert == -5){
                            response = -5;
                            Log.e(TAG, "USER NOT REGISTERED");

                        }else {
                            response =0;
                            Log.e(TAG, "OK INSERT SESSION BOSS");
                        }
                    }

                }else{//tipo operador
                    String [] valuesTag = TagNFC.split(":");
                    int valueInsert = this.createSessionInSystem(kindTag, valuesTag[2]);//index dos porque en la posicion 3 existe el usuario
                    if (valueInsert == -1){
                        response=-2;
                        Log.e(TAG, "DB ERROR");
                    }else{

                        if (valueInsert == -5){
                            response = -5;
                            Log.e(TAG, "USER NOT REGISTERED");
                        }else {
                            response = 1;
                            Log.e(TAG, "OK INSERT OPERADOR");
                        }
                    }
                }
            }else{//a lot of active session in device

                if (activeSession.size() == 1){//sesion unica
                    if (activeSession.get(0).getSessionKind().equals("2")){//sesion de operario, no se puede trabajar porque el operario esta trabajando y es la unica sesion activa
                        response = -3;//sesion activa para operador y en estado unico
                        Log.e(TAG, "SSESION UNIQUE AND OPERADOR AVAILABLE");
                    }else{//la sesion activa es la de un jefe

                        //preguntamos quien desea iniciar sesion
                        if (kindTag == 1){//jefe desea inicar sesion... NO SE PUEDE

                            response = -4;//error de jefe iniciando sesion activa en jefeSESSION ACTIVE IS UNIQUE AND BOSS AVAILABLE
                            Log.e(TAG, "SESSION ACTIVE IS UNIQUE AND BOSS AVAILABLE");
                        }else{//desea iniciar sesion el operador
                            String [] valuesTag = TagNFC.split(":");
                            int value = this.createSessionInSystem(kindTag, valuesTag[2]);
                            if (value == -1){
                                Log.e(TAG, "ERROR DB INSERT OPERATOR");
                                response = -2;
                            }else{
                                if (value == -5){

                                    response = -5;
                                    Log.e(TAG, "USER NOT REGISTERED");
                                }else {
                                    Log.e(TAG, "SESSION ACTIVE IS UNIQUE INSERT OPERATOR OK");
                                    response = 1;
                                }
                            }
                        }
                    }
                }else{//sesion no es unica, existen diferentes sesiones...
                    //preguntamos si existe un jefe con sesion activa
                    if (this.isBossActive(activeSession)){

                        //preguntamos si el que quiere iniciar es del tipo boss o worker
                        if (kindTag == 1){
                            response = -4;//error asociado a conflictos de jefes
                            Log.e(TAG, "ALREADY BOSS IN ACTIVE SESSION");
                        }else{
                            String [] valuesTag = TagNFC.split(":");
                            int value = this.createSessionInSystem(kindTag, valuesTag[2]);
                            if (value == -1){
                                response = -3;
                                Log.e(TAG, "SESSION ACTIVE IS NOT UNIQUE AND BOSS AVAILABLE ERROR INSERT OPERATOR");
                            }else{
                                if(value == -5){
                                    response = -5;
                                    Log.e(TAG, "USER NOT REGISTERED IN SYSTEM");
                                }else {
                                    Log.e(TAG, "SESSION ACTIVE IS NOT UNIQUE AND BOSS AVAILABLE OK INSERT OPERATOR");
                                    response = 1;
                                }
                            }
                        }
                    }else{
                        Log.e(TAG, "SESSION ACTIVE OPERATOR AVAILABLE");
                        response = -3;//no se puede iniciar sesion
                    }
                }
            }
        }

        return response;
    }

    /**
     * Metodo que permite registrar la sesion en el dispositivo con un token asociado
     * @param kindSession
     * @return
     */
    public int createSessionInSystem(int kindSession, String userID){

        try {
            if (this.isUserIsRegistered(userID)) {
                String sessionToken = FA.generateTokenSession(userID);
                this.tokenSession = sessionToken;
                Log.e(TAG, sessionToken + " session");
                String status;
                if (kindSession == 1) {
                    status = "ACTIVE";
                } else {
                    status = "PENDING";
                }

                String startSessionDate = "-";
                String endSessionDate = "-";
                String closeSessionKind = "-";
                String sessionKind;
                String userAssociated = userID;

                if (kindSession == 1) {
                    sessionKind = "BOSS";
                } else {
                    sessionKind = "WORKER";
                }

                long response = this.handlerDBPersistence.saveSessionInDB(new SessionClass(sessionToken, status, startSessionDate, endSessionDate, closeSessionKind, sessionKind, userAssociated));

                return (int) response;
            } else {
                Log.e(TAG, "USER NOT REGISTERED");
                return -5;
            }
        }catch (Exception e){
            return -1;
        }

    }

    //GETTER AND SETTER PARA TOKEN SESSION
    public String getTokenSession() {
        return tokenSession;
    }

    public void setTokenSession(String tokenSession) {
        this.tokenSession = tokenSession;
    }

    /**
     * Metodo que permite evaluar si existe una sesion activa en el dispositivo asociada a un jefe o no
     * @param listSession
     * @return
     */
    public boolean isBossActive(ArrayList<SessionClass> listSession){

        boolean response=false;

        for (int i=0; i< listSession.size(); i++){
            if (listSession.get(i).getSessionKind().equals("BOSS")){
                response=true;
                break;
            }
        }

        return  response;
    }

    /**
     * Metodo que permite poder evaluar si un usuario existe o se encuentra registrado en el sistema o activo para utilizar el dispositivo
     * @param idUser
     * @return
     */
    public boolean isUserIsRegistered(String idUser){

        boolean response = false;
        ArrayList<UserSession> listUser = this.handlerDBPersistence.getUser();//obtenemos la lista de usuarios
        Log.e(TAG, listUser.size()+" tamano");
        //ciclo de busqueda de informacion
        for (int i=0; i<listUser.size(); i++){
            Log.e(TAG, listUser.get(i).getIDUser()+" -- "+ idUser);
            if (listUser.get(i).getIDUser().equalsIgnoreCase(idUser)){
                response=true;
                break;
            }
        }
        return response;
    }

    /**
     * Metodo que permite cambiar la informacion de la sesion con respecto al id usuario
     * @param status
     * @return
     */
    public boolean ChangeStatusSession(String status){

        boolean response=false;

        //primero obtenermos el token de la sesion
        ArrayList<SessionClass> listSession = this.handlerDBPersistence.getSessionActive("PENDING");//solo las que estan pendiente
        if (listSession.size()>0) {
            String tokenSession = listSession.get(listSession.size() - 1).getSessionToken();//obtenemos el token del ultimo registro del compadre

            String sqlExec = "UPDATE " + SessionClassContract.SessionClassContractEntry.TABLE_NAME + " SET " + SessionClassContract.SessionClassContractEntry.STATUS + "= '" + status + "' WHERE " + SessionClassContract.SessionClassContractEntry.TOKEN + "= '" + tokenSession + "'";
            Log.e("CHANGE-SESSION", sqlExec);
            int responseDB = this.handlerDBPersistence.execSQLData(sqlExec);
            if (responseDB == 0) {
                response = true;
            }
        }else{
            response=true;
        }
        return response;
    }

    /**
     * Metodo que permite cerrar la sesion del usuario activo, recibe el token de la sesion activo, esto implica que se elimina la informacion del usuario en la base de datos
     * @param tokenSessionV
     * @return
     */
    public boolean closeSession(String tokenSessionV){

        boolean response = false;
        String sqlExec = "DELETE FROM "+ SessionClassContract.SessionClassContractEntry.TABLE_NAME + " WHERE "+SessionClassContract.SessionClassContractEntry.TOKEN +"= '"+tokenSessionV+"'";
        Log.e("CLOSE-BOSS", sqlExec);
        int responseDB = this.handlerDBPersistence.execSQLData(sqlExec);
        if (responseDB ==0 ){
            Log.e(TAG, "SESSION REMOVE OK");
            response=true;
        }
        return response;
    }

    /**
     * Metodo que permite poder obtener todas las sesiones activas del tipo
     * @return
     */
    public ArrayList<SessionClass> getSessionActive(){

        ArrayList<SessionClass> listSession = this.handlerDBPersistence.getSessionActive("ACTIVE");//obtenemos solo las sesiones activas
        ArrayList<SessionClass> listWorker = new ArrayList<>();

        //hacemos la busqueda de la data asociada a los datos del tipo Worker
        for (int i=0; i< listSession.size(); i++){

            if (listSession.get(i).getSessionKind().equalsIgnoreCase("WORKER")){
                listWorker.add(listSession.get(i));
            }
        }

        return listWorker;
    }

    /**
     * Metodo que permite consultar la informacion de un usuario con el ID de este, retorna un objeto del tipo usuario
     * @return
     */
    public UserSession getInformationBoss(String idData){

        ArrayList<UserSession> listUser = this.handlerDBPersistence.getUser();

        UserSession userSession = null;
        for (int i=0; i< listUser.size(); i++){

            if (listUser.get(i).getIDUser().equalsIgnoreCase(idData)){
                userSession = listUser.get(i);
                break;
            }
        }

        return userSession;
    }

    /**
     * Metodo que permite cerrar la sesion del usuario en caso de que sea factible, retorna true si es factible y en caso contrario retorna un false
     * @return
     */
    public boolean closeSessionBoss(String tokenSessionValues){

        Log.e("CLOSE-BOSS", tokenSessionValues);
        boolean response=false;
        ArrayList<SessionClass> sessionActive = this.getSessionActive();

        if (sessionActive.size()>0){
            Log.e("CLOSE-BOSS", "NO PUEDE!!! ");
            response = false;
        }else{
            Log.e("CLOSE-BOSS", "TE LA CIERRO TODA!!! ");

            //le cerramos la sesion
            this.closeSession(tokenSessionValues);//cierre de sesión
            response = true;
        }

        return response;
    }
}
