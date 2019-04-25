package com.example.tractoramarilloapp.model;

import java.util.ArrayList;

public class Boss extends UserSession {

    private ArrayList<Predio> listPredios;

    public Boss(String IDUser, String NameUser, String RutUser, String rol) {
        super(IDUser, NameUser, RutUser, rol);
        this.listPredios = new ArrayList<>();
    }


    /**
     * Metodo que permite agregar un predio a la lista de predios
     * @param predio
     */
    public void addPredio(Predio predio){

        this.listPredios.add(predio);
    }

    /**
     * Metodo que permite remover un predio de la lista
     * @param predio
     * @return
     */
    public int removePredio(Predio predio){
        return 0;
    }

    /**
     * Metodo que permite buscar un predio en la lista
     * @param predio
     * @return
     */
    public Predio searchPredio(Predio predio){

        return null;
    }
}

