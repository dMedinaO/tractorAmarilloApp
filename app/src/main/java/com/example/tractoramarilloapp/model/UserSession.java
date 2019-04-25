package com.example.tractoramarilloapp.model;

import android.content.ContentValues;

import com.example.tractoramarilloapp.persistence.SessionClass;
import com.example.tractoramarilloapp.persistence.UserContract;

import java.util.ArrayList;

/**
 *  Clase que permite representar a un usuario del dispositivo, es en forma general con el fin de poder obtener la informacion, de
 *  esta clase heredaran los atributos las clases Boss y Worker, quienes representan al jefe de sesion y al operador
 */
public class UserSession {

    //atributos de la clase
    private String IDUser;
    private String NameUser;
    private String RutUser;
    private String rol;
    private ArrayList<SessionClass> listSession;

    public UserSession(String IDUser, String NameUser, String RutUser, String rol){

        this.IDUser = IDUser;
        this.NameUser = NameUser;
        this.RutUser = RutUser;
        this.rol = rol;
        this.listSession = new ArrayList<>();//representa la sesiones que tendra un usuario, imaginando que un usuario puede tener varias sesiones en un mismo dispositivo
    }

    //GETTER AND SETTER
    public String getIDUser() {
        return IDUser;
    }

    public void setIDUser(String IDUser) {
        this.IDUser = IDUser;
    }

    public String getNameUser() {
        return NameUser;
    }

    public void setNameUser(String nameUser) {
        NameUser = nameUser;
    }

    public String getRutUser() {
        return RutUser;
    }

    public void setRutUser(String rutUser) {
        RutUser = rutUser;
    }

    public ArrayList<SessionClass> getListSession() {
        return listSession;
    }

    public void setListSession(ArrayList<SessionClass> listSession) {
        this.listSession = listSession;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public ContentValues toConentValues(){

        ContentValues values = new ContentValues();

        values.put(UserContract.UserContractEntry.ID_USER, this.IDUser);
        values.put(UserContract.UserContractEntry.NAME_USER, this.NameUser);
        values.put(UserContract.UserContractEntry.ROL, this.rol);
        values.put(UserContract.UserContractEntry.RUT_USER, this.RutUser);

        return values;
    }
}
