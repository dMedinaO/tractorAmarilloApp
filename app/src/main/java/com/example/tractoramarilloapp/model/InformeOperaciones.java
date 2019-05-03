package com.example.tractoramarilloapp.model;

import android.content.ContentValues;

import com.example.tractoramarilloapp.persistence.InformeOperacionesContract;

/**
 * Clase con la responsabilidad de representar un informe de operaciones, la cual permite ser mapeada y registrad en el dispositivo
 */
public class InformeOperaciones {

    //atributos de la clase
    private int idinformeOperaciones;
    private String idMaquinaria;
    private String horometroInicio;
    private String horometroFinal;
    private String userID;
    private String sessionTAG;
    private String isImplementActive;
    private String idImplemento;
    private String horarioInicio;
    private String horarioFinal;
    private String idFaena;
    private String statusSend;
    private String idPredio;

    /**
     * Constructor de la clase
     * @param idinformeOperaciones
     * @param idMaquinaria
     * @param horometroInicio
     * @param horometroFinal
     * @param userID
     * @param sessionTAG
     * @param isImplementActive
     * @param idImplemento
     * @param horarioInicio
     * @param horarioFinal
     * @param idFaena
     * @param statusSend
     * @param predio
     */
    public InformeOperaciones(int idinformeOperaciones, String idMaquinaria, String horometroInicio, String horometroFinal, String userID, String sessionTAG, String isImplementActive, String idImplemento, String horarioInicio, String horarioFinal, String idFaena, String statusSend, String predio) {
        this.idinformeOperaciones = idinformeOperaciones;
        this.idMaquinaria = idMaquinaria;
        this.horometroInicio = horometroInicio;
        this.horometroFinal = horometroFinal;
        this.userID = userID;
        this.sessionTAG = sessionTAG;
        this.isImplementActive = isImplementActive;
        this.idImplemento = idImplemento;
        this.horarioInicio = horarioInicio;
        this.horarioFinal = horarioFinal;
        this.idFaena = idFaena;
        this.statusSend = statusSend;
        this.idPredio = predio;
    }

    //GETTER AND SETTERS

    public String getIdPredio() {
        return idPredio;
    }

    public void setIdPredio(String idPredio) {
        this.idPredio = idPredio;
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

    public String getIsImplementActive() {
        return isImplementActive;
    }

    public void setIsImplementActive(String isImplementActive) {
        this.isImplementActive = isImplementActive;
    }

    public String getIdImplemento() {
        return idImplemento;
    }

    public void setIdImplemento(String idImplemento) {
        this.idImplemento = idImplemento;
    }

    public String getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(String horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public String getHorarioFinal() {
        return horarioFinal;
    }

    public void setHorarioFinal(String horarioFinal) {
        this.horarioFinal = horarioFinal;
    }

    public String getIdFaena() {
        return idFaena;
    }

    public void setIdFaena(String idFaena) {
        this.idFaena = idFaena;
    }

    public String getStatusSend() {
        return statusSend;
    }

    public void setStatusSend(String statusSend) {
        this.statusSend = statusSend;
    }

    public ContentValues toContentValues(){

        ContentValues contentValues = new ContentValues();

        contentValues.put(InformeOperacionesContract.InformeOperacionesContractEntry.ID_INFORME, this.idinformeOperaciones);
        contentValues.put(InformeOperacionesContract.InformeOperacionesContractEntry.ID_MAQUINARIA, this.idMaquinaria);
        contentValues.put(InformeOperacionesContract.InformeOperacionesContractEntry.HOROMETRO_INICIO, this.horometroInicio);
        contentValues.put(InformeOperacionesContract.InformeOperacionesContractEntry.HOROMETRO_TERMINO, this.horometroFinal);
        contentValues.put(InformeOperacionesContract.InformeOperacionesContractEntry.USER_ID, this.userID);
        contentValues.put(InformeOperacionesContract.InformeOperacionesContractEntry.SESSION_TOKEN, this.sessionTAG);
        contentValues.put(InformeOperacionesContract.InformeOperacionesContractEntry.IS_IMPLEMENT_ACTIVE, this.isImplementActive);
        contentValues.put(InformeOperacionesContract.InformeOperacionesContractEntry.ID_IMPLEMENTO, this.idImplemento);
        contentValues.put(InformeOperacionesContract.InformeOperacionesContractEntry.HORA_INICIO, this.horarioInicio);
        contentValues.put(InformeOperacionesContract.InformeOperacionesContractEntry.HORA_TERMINO, this.horarioFinal);
        contentValues.put(InformeOperacionesContract.InformeOperacionesContractEntry.ID_FAENA, this.idFaena);
        contentValues.put(InformeOperacionesContract.InformeOperacionesContractEntry.ID_PREDIO, this.idPredio);
        contentValues.put(InformeOperacionesContract.InformeOperacionesContractEntry.STATUS_INFORME, this.statusSend);

        return  contentValues;
    }
}
