package com.example.tractoramarilloapp.sessionHandler;

import com.example.tractoramarilloapp.model.UserSession;

import java.util.ArrayList;

/**
 * Clase con las responsabilidad de generar una lista de objetos y sus estados asociados a los usuarios del sistema, en conjunto con los metodos
 * de este, con el fin de poder asociar de manera mas simple el mapeo de informacion, las maquinarias seleccionadas, implementos, faneas, etc. Posee
 * metodos que permiten crear las instancias y tiene la facultad de tener la informacion necesaria para las operaciones que realiza el dispositivo (actividades)
 */
public class PersonalHandler {

    private ArrayList<UserSession> listUserProcessDevice;

    public PersonalHandler(){
        this.listUserProcessDevice = new ArrayList<>();
    }

    public ArrayList<UserSession> getListUserProcessDevice() {
        return listUserProcessDevice;
    }

    public void setListUserProcessDevice(ArrayList<UserSession> listUserProcessDevice) {
        this.listUserProcessDevice = listUserProcessDevice;
    }
}
