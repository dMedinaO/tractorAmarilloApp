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
    private String codeImplemento;

    public Faena(String nameFaena, String codeInternoFaena, String codeImplemento) {
        this.nameFaena = nameFaena;
        this.codeInternoFaena = codeInternoFaena;
        this.codeImplemento = codeImplemento;
    }

    public String getCodeInternoFaena() {
        return codeInternoFaena;
    }

    public String getCodeImplemento() {
        return codeImplemento;
    }

    public void setCodeImplemento(String codeImplemento) {
        this.codeImplemento = codeImplemento;
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
        values.put(FaenaContract.FaenaContractEntry.CODE_IMPLEMENTO, this.codeImplemento);
        return values;
    }
}
