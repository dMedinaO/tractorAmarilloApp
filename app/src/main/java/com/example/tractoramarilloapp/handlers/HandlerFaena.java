package com.example.tractoramarilloapp.handlers;

import android.content.Context;

import com.example.tractoramarilloapp.model.Faena;
import com.example.tractoramarilloapp.persistence.HandlerDBPersistence;

import java.util.ArrayList;

/**
 * Clase con la responsabilidad de obtener las faneas habilitadas para el implemento seleccionado, en caso de que el implemento no tenga valor se trae toda la informacion
 * asociada a las tablas
 */
public class HandlerFaena {

    private String [] faenaList;
    private String [] faenaIDList;
    private String codeImplemento;
    private Context context;
    private HandlerDBPersistence handlerDBPersistence;

    public HandlerFaena(String codeImplemento, Context context){

        this.context = context;
        this.codeImplemento = codeImplemento;
        this.handlerDBPersistence = new HandlerDBPersistence(this.context);

        //evaluamos en el contstructor que agregamos...
        this.searchElementFaena();
    }

    public String[] getFaenaList() {
        return faenaList;
    }

    public void setFaenaList(String[] faenaList) {
        this.faenaList = faenaList;
    }

    public String[] getFaenaIDList() {
        return faenaIDList;
    }

    public void setFaenaIDList(String[] faenaIDList) {
        this.faenaIDList = faenaIDList;
    }

    public String getCodeImplemento() {
        return codeImplemento;
    }

    public void setCodeImplemento(String codeImplemento) {
        this.codeImplemento = codeImplemento;
    }

    /**
     * Metodo que permite buscar y completar los elementos de arreglos asociadas a la data
     */
    public void searchElementFaena(){

        ArrayList<Faena> listFaena = this.handlerDBPersistence.getFaena();

        if (this.codeImplemento.equalsIgnoreCase("NO")){
            this.addElementRestrict(listFaena);
        }else{
            this.addElementsFull(listFaena);
        }
    }

    /**
     * Metodo que permite agregar elementos de manera no restrictiva
     * @param listFaena
     */
    public void addElementsFull(ArrayList<Faena> listFaena){

        this.faenaIDList = new String[listFaena.size()];
        this.faenaList = new String[listFaena.size()];

        for (int i=0; i< listFaena.size(); i++){

            this.faenaList[i] = listFaena.get(i).getNameFaena();
            this.faenaIDList[i] = listFaena.get(i).getCodeInternoFaena();
        }
    }
    /**
     * Metodo privado que permite agregar elementos a la lista con respecto a la informacion que este posea,
     * @param listFaena
     */
    private void addElementRestrict(ArrayList<Faena> listFaena){
        ArrayList<Faena> listTemp = new ArrayList<>();

        for (int i=0; i<listFaena.size(); i++){
            if (listFaena.get(i).getCodeImplemento().equalsIgnoreCase(this.codeImplemento)){
                listTemp.add(listFaena.get(i));
            }
        }

        //agregamos los elementos a los array
        this.faenaIDList = new String[listTemp.size()];
        this.faenaList = new String[listTemp.size()];

        for (int i=0; i<listTemp.size(); i++){

            this.faenaList[i] = listTemp.get(i).getNameFaena();
            this.faenaIDList[i] = listTemp.get(i).getCodeInternoFaena();
        }
    }
}
