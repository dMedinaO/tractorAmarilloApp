package com.example.tractoramarilloapp.model;

import android.content.ContentValues;

import com.example.tractoramarilloapp.persistence.TipoMaquinariaContract;

public class TipoMaquinaria {

    private String codeTipo;
    private String nombreTipo;

    public TipoMaquinaria(String codeTipo, String nombreTipo){

        this.codeTipo = codeTipo;
        this.nombreTipo = nombreTipo;
    }

    public String getCodeTipo() {
        return codeTipo;
    }

    public void setCodeTipo(String codeTipo) {
        this.codeTipo = codeTipo;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    public ContentValues toContentValus(){

        ContentValues contentValues = new ContentValues();

        contentValues.put(TipoMaquinariaContract.TipoMaquinariaContractEntry.CODE_INTERNO, this.codeTipo);
        contentValues.put(TipoMaquinariaContract.TipoMaquinariaContractEntry.NAME_TIPO, this.nombreTipo);
        return  contentValues;
    }
}