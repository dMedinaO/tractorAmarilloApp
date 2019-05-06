package com.example.tractoramarilloapp.model;

import android.content.ContentValues;

import com.example.tractoramarilloapp.persistence.InformeFaenaContract;

/**
 * Clase que permite representar la informacion asociada a un informe del tipo faena, junto con la asociacion correspondiente
 * al informe de maquinaria.
 */
public class InformeFaena {

    //atributos de la clase
    private int idInformeFaena;
    private String idFaena;
    private String horaInicio;
    private String horaFinal;
    private String userID;
    private String sessionTAG;
    private String statusSend;
    private String informeMaquinariaID;

    public InformeFaena(int idInformeFaena, String idFaena, String horaInicio, String horaFinal, String userID, String sessionTAG, String statusSend, String informeMaquinariaID) {
        this.idInformeFaena = idInformeFaena;
        this.idFaena = idFaena;
        this.horaInicio = horaInicio;
        this.horaFinal = horaFinal;
        this.userID = userID;
        this.sessionTAG = sessionTAG;
        this.statusSend = statusSend;
        this.informeMaquinariaID = informeMaquinariaID;
    }

    public int getIdInformeFaena() {
        return idInformeFaena;
    }

    public void setIdInformeFaena(int idInformeFaena) {
        this.idInformeFaena = idInformeFaena;
    }

    public String getIdFaena() {
        return idFaena;
    }

    public void setIdFaena(String idFaena) {
        this.idFaena = idFaena;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFinal() {
        return horaFinal;
    }

    public void setHoraFinal(String horaFinal) {
        this.horaFinal = horaFinal;
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

    public String getInformeMaquinariaID() {
        return informeMaquinariaID;
    }

    public void setInformeMaquinariaID(String informeMaquinariaID) {
        this.informeMaquinariaID = informeMaquinariaID;
    }

    public ContentValues toContentValues(){

        ContentValues contentValues = new ContentValues();

        contentValues.put(InformeFaenaContract.InformeFaenaContractEntry.ID_INFORME, this.idInformeFaena);
        contentValues.put(InformeFaenaContract.InformeFaenaContractEntry.ID_FAENA, this.idFaena);
        contentValues.put(InformeFaenaContract.InformeFaenaContractEntry.HORA_INICIO, this.horaInicio);
        contentValues.put(InformeFaenaContract.InformeFaenaContractEntry.HORA_TERMINO, this.horaFinal);
        contentValues.put(InformeFaenaContract.InformeFaenaContractEntry.USER_ID, this.userID);
        contentValues.put(InformeFaenaContract.InformeFaenaContractEntry.SESSION_TOKEN, this.sessionTAG);
        contentValues.put(InformeFaenaContract.InformeFaenaContractEntry.STATUS_SEND, this.statusSend);
        contentValues.put(InformeFaenaContract.InformeFaenaContractEntry.INFORME_MAQUINARIA_ID, this.informeMaquinariaID);

        return contentValues;
    }
}
