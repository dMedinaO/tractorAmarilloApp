package com.example.tractoramarilloapp.model;

import android.content.ContentValues;

import com.example.tractoramarilloapp.persistence.InformeMaquinariaContract;

/**
 * clase que permite representar los elementos asociados al informe de maquinaria,
 * se instancia cuando el usuario accede a una maquinaria y se encuentra habilitado para operarla
 */
public class InformeMaquinaria {

    //atributos de la clase
    private int idinformeOperaciones;
    private String idMaquinaria;
    private String horometroInicio;
    private String horometroFinal;
    private String userID;
    private String sessionTAG;
    private String statusSend;
    private String closeSessionKind;
    private String predio;

    public InformeMaquinaria(int idinformeOperaciones, String idMaquinaria, String horometroInicio, String horometroFinal, String userID, String sessionTAG, String statusSend, String closeSessionKind, String predio) {
        this.idinformeOperaciones = idinformeOperaciones;
        this.idMaquinaria = idMaquinaria;
        this.horometroInicio = horometroInicio;
        this.horometroFinal = horometroFinal;
        this.userID = userID;
        this.sessionTAG = sessionTAG;
        this.statusSend = statusSend;
        this.closeSessionKind = closeSessionKind;
        this.predio = predio;
    }

    public String getPredio() {
        return predio;
    }

    public void setPredio(String predio) {
        this.predio = predio;
    }

    public int getIdinformeOperaciones() {
        return idinformeOperaciones;
    }

    public void setIdinformeOperaciones(int idinformeOperaciones) {
        this.idinformeOperaciones = idinformeOperaciones;
    }

    public String getIdMaquinaria() {
        return idMaquinaria;
    }

    public void setIdMaquinaria(String idMaquinaria) {
        this.idMaquinaria = idMaquinaria;
    }

    public String getHorometroInicio() {
        return horometroInicio;
    }

    public void setHorometroInicio(String horometroInicio) {
        this.horometroInicio = horometroInicio;
    }

    public String getHorometroFinal() {
        return horometroFinal;
    }

    public void setHorometroFinal(String horometroFinal) {
        this.horometroFinal = horometroFinal;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getSessionTAG() {
        return sessionTAG;
    }

    public void setSessionTAG(String sessionTAG) {
        this.sessionTAG = sessionTAG;
    }

    public String getStatusSend() {
        return statusSend;
    }

    public void setStatusSend(String statusSend) {
        this.statusSend = statusSend;
    }

    public String getCloseSessionKind() {
        return closeSessionKind;
    }

    public void setCloseSessionKind(String closeSessionKind) {
        this.closeSessionKind = closeSessionKind;
    }

    public ContentValues toContentValues(){

        ContentValues contentValues = new ContentValues();

        contentValues.put(InformeMaquinariaContract.InformeMaquinariaContractEntry.ID_INFORME, this.idinformeOperaciones);
        contentValues.put(InformeMaquinariaContract.InformeMaquinariaContractEntry.ID_MAQUINARIA, this.idMaquinaria);
        contentValues.put(InformeMaquinariaContract.InformeMaquinariaContractEntry.ID_PREDIO, this.predio);
        contentValues.put(InformeMaquinariaContract.InformeMaquinariaContractEntry.HOROMETRO_INICIO, this.horometroInicio);
        contentValues.put(InformeMaquinariaContract.InformeMaquinariaContractEntry.HOROMETRO_TERMINO, this.horometroFinal);
        contentValues.put(InformeMaquinariaContract.InformeMaquinariaContractEntry.USER_ID, this.userID);
        contentValues.put(InformeMaquinariaContract.InformeMaquinariaContractEntry.SESSION_TOKEN, this.sessionTAG);
        contentValues.put(InformeMaquinariaContract.InformeMaquinariaContractEntry.CLOSE_SESSION_KIND, this.closeSessionKind);
        contentValues.put(InformeMaquinariaContract.InformeMaquinariaContractEntry.STATUS_SEND, this.statusSend);

        return  contentValues;
    }
}
