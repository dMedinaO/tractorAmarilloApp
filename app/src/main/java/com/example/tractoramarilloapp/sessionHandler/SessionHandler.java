package com.example.tractoramarilloapp.sessionHandler;

import java.util.UUID;

/**
 * Clase que tiene la responsabilidad de manipular todas las acciones asociadas a una sesion en el dispositivo, permite crear sesiones, buscar y editar sesiones existentes, asi como tambien
 * eliminar sesiones.
 */
public class SessionHandler {

    public String generateTokenSession(){

        String response;
        String id = UUID.randomUUID().toString();
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        response = id+"_"+tsLong;
        return response;
    }
}
