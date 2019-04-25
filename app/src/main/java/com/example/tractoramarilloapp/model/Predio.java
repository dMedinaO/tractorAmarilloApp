package com.example.tractoramarilloapp.model;

import android.content.ContentValues;

import com.example.tractoramarilloapp.persistence.PredioContract;

import java.util.ArrayList;

/**
 * Clase que permite representar a un predio y la informacion asociada a dicho predio
 */
public class Predio {

    private String namePredio;
    private String code_internoPredio;

    private ArrayList<Faena> faenasAvailable;

    /**
     * constructor de la clase
     * @param namePredio
     */
    public Predio (String namePredio, String code_internoPredio){

        this.namePredio= namePredio;
        this.code_internoPredio = code_internoPredio;
        this.faenasAvailable = new ArrayList<>();
    }

    public String getCode_internoPredio() {
        return code_internoPredio;
    }

    public void setCode_internoPredio(String code_internoPredio) {
        this.code_internoPredio = code_internoPredio;
    }

    public String getNamePredio() {
        return namePredio;
    }

    public void setNamePredio(String namePredio) {
        this.namePredio = namePredio;
    }

    public ArrayList<Faena> getFaenasAvailable() {
        return faenasAvailable;
    }


    public void setFaenasAvailable(ArrayList<Faena> faenasAvailable) {
        this.faenasAvailable = faenasAvailable;
    }

    /**
     * Metodo que permite agregar una faena a la lista de faenas habilitadas al predio
     * @param faena
     */
    public void addFaenaToList(Faena faena){

        this.faenasAvailable.add(faena);
    }

    /**
     * Metodo que permite remover una faena al predio seleccionado
     * @param faena
     * @return
     */
    public int removeFaene(Faena faena){

        return 0;
    }

    /**
     * Metodo que permite buscar una faena en la lista del predio
     * @param faena
     * @return
     */
    public Faena searchFaena(Faena faena){

        return null;
    }

    public ContentValues toContentValues(){

        ContentValues contentValues = new ContentValues();

        contentValues.put(PredioContract.PredioContractEntry.PREDIO_NAME, this.namePredio);
        contentValues.put(PredioContract.PredioContractEntry.CODE_PREDIO, this.code_internoPredio);
        return  contentValues;
    }
}
