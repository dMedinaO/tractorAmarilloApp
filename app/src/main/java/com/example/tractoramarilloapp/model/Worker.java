package com.example.tractoramarilloapp.model;

import java.util.ArrayList;

/**
 * Este es el trabajador obrero mano de obra barata que hace la pega a la gente XD por lo tanto, tiene funcionalidades
 * diferentes asociadas a sus labores, principalmente, la manipulacion de maquinaria y el status asociado a en que proceso
 * se encuentra sujeto el compadre para determinar que carajos se hace con su sesion... flujo de mierda XD
 */
public class Worker extends UserSession {

    private ArrayList<Maquinaria> listMaquinaria;

    public Worker(String IDUser, String NameUser, String RutUser, String rol) {
        super(IDUser, NameUser, RutUser, rol);
        this.listMaquinaria = new ArrayList<>();
    }


    /**
     * Metodo que permite agregar una maquinaria
     * @param maquinaria
     */
    public void addMaquinaria (Maquinaria maquinaria){

        this.listMaquinaria.add(maquinaria);
    }

    /**
     * Metodo que permite eliminar una maquinaria
     * @param maquinaria
     * @return
     */
    public int removeMaquinaria(Maquinaria maquinaria){
        return 0;
    }

    /**
     * Metodo que permite buscar una maquinaria
     * @param maquinaria
     * @return
     */
    public Maquinaria searchMaquinaria(Maquinaria maquinaria){
        return  null;
    }

    public ArrayList<Maquinaria> getListMaquinaria() {
        return listMaquinaria;
    }

    public void setListMaquinaria(ArrayList<Maquinaria> listMaquinaria) {
        this.listMaquinaria = listMaquinaria;
    }
}
