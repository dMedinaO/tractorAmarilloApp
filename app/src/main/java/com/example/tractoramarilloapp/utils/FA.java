package com.example.tractoramarilloapp.utils;

import com.example.tractoramarilloapp.persistence.SessionClass;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Clase asociada al manejo de distintas funcionalidades y operaciones en procesos independientes
 * que permiten la manipulacion de la data y otras caracteristicas
 */
public class FA {

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
}
