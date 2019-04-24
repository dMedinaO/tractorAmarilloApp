package com.example.tractoramarilloapp.model;

import com.example.tractoramarilloapp.persistence.SessionClass;

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
    private ArrayList<SessionClass> listSession;

    public UserSession(String IDUser, String NameUser, String RutUser){

        this.IDUser = IDUser;
        this.NameUser = NameUser;
        this.RutUser = RutUser;
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
}
