package com.example.tractoramarilloapp.model;

import android.content.ContentValues;

import com.example.tractoramarilloapp.persistence.FaenaContract;

/**
 * Clase que representa a una faena y sus atributos
 */
public class Faena {

    //atributos de la clase
    private String nameFaena;
    private String codeInternoFaena;

    public Faena(String nameFaena, String codeInternoFaena) {
        this.nameFaena = nameFaena;
        this.codeInternoFaena = codeInternoFaena;
    }

    public void setCodeInternoFaena(String codeInternoFaena) {
        this.codeInternoFaena = codeInternoFaena;
    }

    public String getNameFaena() {
        return nameFaena;
    }

    public void setNameFaena(String nameFaena) {
        this.nameFaena = nameFaena;
    }

    public ContentValues toContentValues(){

        ContentValues values = new ContentValues();
        values.put(FaenaContract.FaenaContractEntry.FAENA_NAME, this.nameFaena);
        values.put(FaenaContract.FaenaContractEntry.CODE_FAENA, this.codeInternoFaena);
        return values;
    }
}
