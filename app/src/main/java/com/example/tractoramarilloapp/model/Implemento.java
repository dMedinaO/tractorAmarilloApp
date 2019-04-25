package com.example.tractoramarilloapp.model;

import android.content.ContentValues;

import com.example.tractoramarilloapp.persistence.ImplementContract;

import java.util.ArrayList;

/**
 * Clase que permite representar a un implemento en base a las caracteristicas que este posea
 */
public class Implemento {

    private String nameImplement;
    private String statusImplement;
    private String ano;
    private String fabricante;
    private String color;
    private String capacidad;
    private String codeInternoImplemento;

    private ArrayList<Faena> listFaena;

    public Implemento(String nameImplement, String statusImplement, String ano, String fabricante, String color, String capacidad, String codeInternoImplemento) {

        this.codeInternoImplemento = codeInternoImplemento;
        this.nameImplement = nameImplement;
        this.statusImplement = statusImplement;
        this.ano = ano;
        this.fabricante = fabricante;
        this.color = color;
        this.capacidad = capacidad;
        this.listFaena = new ArrayList<>();
    }

    public void addFaena(Faena faena){
        this.listFaena.add(faena);
    }

    public int removeFaena(Faena faena){

        return 0;
    }

    public Faena searchFaena(Faena faena){

        return null;
    }

    public String getNameImplement() {
        return nameImplement;
    }

    public void setNameImplement(String nameImplement) {
        this.nameImplement = nameImplement;
    }

    public String getStatusImplement() {
        return statusImplement;
    }

    public void setStatusImplement(String statusImplement) {
        this.statusImplement = statusImplement;
    }

    public String getCodeInternoImplemento() {
        return codeInternoImplemento;
    }

    public void setCodeInternoImplemento(String codeInternoImplemento) {
        this.codeInternoImplemento = codeInternoImplemento;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public String getColorImplemento() {
        return color;
    }

    public void setColorImplemento(String color) {
        this.color = color;
    }

    public String getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(String capacidad) {
        this.capacidad = capacidad;
    }

    public ArrayList<Faena> getListFaena() {
        return listFaena;
    }

    public void setListFaena(ArrayList<Faena> listFaena) {
        this.listFaena = listFaena;
    }

    public ContentValues toContentValues(){

        ContentValues values = new ContentValues();

        values.put(ImplementContract.ImplementContractEntry.NAME_IMPLEMENTO, this.nameImplement);
        values.put(ImplementContract.ImplementContractEntry.ANO_IMPLEMENTO, this.ano);
        values.put(ImplementContract.ImplementContractEntry.CAPACIDAD_IMPLEMENTO, this.capacidad);
        values.put(ImplementContract.ImplementContractEntry.COLOR_IMPLEMENTO, this.color);
        values.put(ImplementContract.ImplementContractEntry.FABRICANTE_IMPLEMENTO, this.fabricante);
        values.put(ImplementContract.ImplementContractEntry.STATUS_IMPLEMENTO, this.statusImplement);
        values.put(ImplementContract.ImplementContractEntry.CODE_INTERNO_IMPLEMENTO, this.codeInternoImplemento);
        
        return  values;
    }
}
