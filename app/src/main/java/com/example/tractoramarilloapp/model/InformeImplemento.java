package com.example.tractoramarilloapp.model;

import android.content.ContentValues;

import com.example.tractoramarilloapp.persistence.InformeImplementoContract;

/**
 * Clase que permite representar al informe de implementos bajo el cual se expone el uso de estos y quien lo opero, asi como tambien
 * se asocia a un informe de maquinaria y bajo la cual se opero.
 */
public class InformeImplemento {

    //atributos de la clase
    private int idInformeImplemento;
    private String idImplemento;
    private String horaInicio;
    private String horaFinal;
    private String userID;
    private String sessionTAG;
    private String statusSend;
    private String informeMaquinariaID;

    public InformeImplemento(int idInformeImplemento, String idImplemento, String horaInicio, String horaFinal, String userID, String sessionTAG, String statusSend, String informeMaquinariaID) {
        this.idInformeImplemento = idInformeImplemento;
        this.idImplemento = idImplemento;
        this.horaInicio = horaInicio;
        this.horaFinal = horaFinal;
        this.userID = userID;
        this.sessionTAG = sessionTAG;
        this.statusSend = statusSend;
        this.informeMaquinariaID = informeMaquinariaID;
    }

    public int getIdInformeImplemento() {
        return idInformeImplemento;
    }

    public void setIdInformeImplemento(int idInformeImplemento) {
        this.idInformeImplemento = idInformeImplemento;
    }

    public String getIdImplemento() {
        return idImplemento;
    }

    public void setIdImplemento(String idImplemento) {
        this.idImplemento = idImplemento;
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

        contentValues.put(InformeImplementoContract.InformeImplementoContractEntry.ID_IMPLEMENTO, this.idImplemento);
        contentValues.put(InformeImplementoContract.InformeImplementoContractEntry.HORA_INICIO, this.horaInicio);
        contentValues.put(InformeImplementoContract.InformeImplementoContractEntry.HORA_TERMINO, this.horaFinal);
        contentValues.put(InformeImplementoContract.InformeImplementoContractEntry.USER_ID, this.userID);
        contentValues.put(InformeImplementoContract.InformeImplementoContractEntry.ID_INFORME, this.idInformeImplemento);
        contentValues.put(InformeImplementoContract.InformeImplementoContractEntry.SESSION_TOKEN, this.sessionTAG);
        contentValues.put(InformeImplementoContract.InformeImplementoContractEntry.INFORME_MAQUINARIA_ID, this.informeMaquinariaID);
        contentValues.put(InformeImplementoContract.InformeImplementoContractEntry.STATUS_SEND, this.statusSend);

        return  contentValues;
    }
}
