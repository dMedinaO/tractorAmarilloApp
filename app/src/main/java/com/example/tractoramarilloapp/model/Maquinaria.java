package com.example.tractoramarilloapp.model;

import android.content.ContentValues;

import com.example.tractoramarilloapp.persistence.MaquinariaContract;

import java.util.ArrayList;

/**
 * Clase que tiene la responsabilidad de mapear a una maquinara que se encuentra habilitada en el sistema
 */
public class Maquinaria {

    private String codeInternoMachine;
    private String nameMachine;
    private String markMachine;
    private String modelMachine;
    private String yeardMachine;
    private String statusMachine;
    private String patentMachine;
    private String colorMachine;
    private String tipoMaquinaria;

    private ArrayList<Implemento> listImplements;

    /**
     * Building class for Maquinaria
     * @param nameMachine
     * @param markMachine
     * @param modelMachine
     * @param yeardMachine
     * @param statusMachine
     * @param patentMachine
     * @param colorMachine
     * @param tipoMaquinaria
     */
    public Maquinaria(String nameMachine, String markMachine, String modelMachine, String yeardMachine, String statusMachine, String patentMachine, String colorMachine, String codeInterno, String tipoMaquinaria) {
        this.nameMachine = nameMachine;
        this.markMachine = markMachine;
        this.modelMachine = modelMachine;
        this.yeardMachine = yeardMachine;
        this.statusMachine = statusMachine;
        this.patentMachine = patentMachine;
        this.colorMachine = colorMachine;
        this.codeInternoMachine = codeInterno;
        this.tipoMaquinaria = tipoMaquinaria;
        this.listImplements = new ArrayList<>();
    }

    /**
     * Metodo que permite agregar un elemento asociado a la data de interes
     * @param implemento
     */
    public void addImplement(Implemento implemento){
        this.listImplements.add(implemento);
    }

    /**
     * Metodo que te permite eliminar un implemento
     * @param implemento
     * @return
     */
    public int removeImplement(Implemento implemento){

        return 0;
    }

    /**
     * Metodo que permite buscar un implemento
     * @param implemento
     * @return
     */
    public Implemento searchImplemento(Implemento implemento){

        return null;
    }

    //GETTER AND SETTER

    public String getCodeInternoMachine() {
        return codeInternoMachine;
    }

    public void setCodeInternoMachine(String codeInternoMachine) {
        this.codeInternoMachine = codeInternoMachine;
    }

    public String getNameMachine() {
        return nameMachine;
    }

    public void setNameMachine(String nameMachine) {
        this.nameMachine = nameMachine;
    }

    public String getMarkMachine() {
        return markMachine;
    }

    public void setMarkMachine(String markMachine) {
        this.markMachine = markMachine;
    }

    public String getModelMachine() {
        return modelMachine;
    }

    public void setModelMachine(String modelMachine) {
        this.modelMachine = modelMachine;
    }

    public String getYeardMachine() {
        return yeardMachine;
    }

    public void setYeardMachine(String yeardMachine) {
        this.yeardMachine = yeardMachine;
    }

    public String getStatusMachine() {
        return statusMachine;
    }

    public void setStatusMachine(String statusMachine) {
        this.statusMachine = statusMachine;
    }

    public String getPatentMachine() {
        return patentMachine;
    }

    public void setPatentMachine(String patentMachine) {
        this.patentMachine = patentMachine;
    }

    public String getColorMachine() {
        return colorMachine;
    }

    public void setColorMachine(String colorMachine) {
        this.colorMachine = colorMachine;
    }

    public ArrayList<Implemento> getListImplements() {
        return listImplements;
    }

    public void setListImplements(ArrayList<Implemento> listImplements) {
        this.listImplements = listImplements;
    }

    public String getTipoMaquinaria() {
        return tipoMaquinaria;
    }

    public void setTipoMaquinaria(String tipoMaquinaria) {
        this.tipoMaquinaria = tipoMaquinaria;
    }

    public ContentValues toContentValues(){

        ContentValues contentValues = new ContentValues();

        contentValues.put(MaquinariaContract.MaquinariaContractEntry.COLOR_MACHINE, this.colorMachine);
        contentValues.put(MaquinariaContract.MaquinariaContractEntry.MODEL_MACHINE, this.modelMachine);
        contentValues.put(MaquinariaContract.MaquinariaContractEntry.NAME_MACHINE, this.nameMachine);
        contentValues.put(MaquinariaContract.MaquinariaContractEntry.PATENT_MACHINE, this.patentMachine);
        contentValues.put(MaquinariaContract.MaquinariaContractEntry.STATUS_MACHINE, this.statusMachine);
        contentValues.put(MaquinariaContract.MaquinariaContractEntry.YEARD_MACHINE, this.yeardMachine);
        contentValues.put(MaquinariaContract.MaquinariaContractEntry.CODE_INTERNO_MACHINE, this.codeInternoMachine);
        contentValues.put(MaquinariaContract.MaquinariaContractEntry.MARK_MACHINE, this.markMachine);
        contentValues.put(MaquinariaContract.MaquinariaContractEntry.KIND_MACHINE, this.tipoMaquinaria);

        return contentValues;
    }
}
