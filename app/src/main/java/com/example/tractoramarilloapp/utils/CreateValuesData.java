package com.example.tractoramarilloapp.utils;

import java.util.UUID;

/**
 * case que tiene las funcioanlidades asociadas a crear diferentes valores con respecto a la situacion, posee
 * diferentes metodos que te permiten crear token de sesion, numeros aleatorios, etc.
 */
public class CreateValuesData {

    public static String generateTokenSession(){

        String response;
        String id = UUID.randomUUID().toString();
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        response = id+"_"+tsLong;
        return response;
    }
}
