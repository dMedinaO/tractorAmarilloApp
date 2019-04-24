package com.example.tractoramarilloapp.persistence;

import android.content.ContentValues;

/**
 * clase que representa la sesion a ser mapeada en el dispositivo en formato data base, posee un Contract para poder mapearla y
 * se le asocia la informacion correspondiente a su data, posee los metodos para obtener la informacion y setearla (GETTER AND SETTER)
 */
public class SessionClass {

    //atributos de la clase
    private String sessionToken;
    private String status;
    private String startSessionDate;
    private String endSessionDate;
    private String closeSessionKind;
    private String sessionKind;
    private String userAssociated;

    public SessionClass(String sessionToken, String status, String startSessionDate, String endSessionDate, String closeSessionKind, String sessionKind, String userAssociated){

        this.sessionToken = sessionToken;
        this.status = status;
        this.startSessionDate = startSessionDate;
        this.endSessionDate = endSessionDate;
        this.closeSessionKind = closeSessionKind;
        this.sessionKind = sessionKind;
        this.userAssociated = userAssociated;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartSessionDate() {
        return startSessionDate;
    }

    public void setStartSessionDate(String startSessionDate) {
        this.startSessionDate = startSessionDate;
    }

    public String getEndSessionDate() {
        return endSessionDate;
    }

    public void setEndSessionDate(String endSessionDate) {
        this.endSessionDate = endSessionDate;
    }

    public String getCloseSessionKind() {
        return closeSessionKind;
    }

    public void setCloseSessionKind(String closeSessionKind) {
        this.closeSessionKind = closeSessionKind;
    }

    public String getSessionKind() {
        return sessionKind;
    }

    public void setSessionKind(String sessionKind) {
        this.sessionKind = sessionKind;
    }

    public String getUserAssociated() {
        return userAssociated;
    }

    public void setUserAssociated(String userAssociated) {
        this.userAssociated = userAssociated;
    }

    /**
     * Metodo que permite mapear la data del objeto en forma de tabla para ser consultada o insertada en la DB
     * @return
     */
    public ContentValues toConentValues(){

        ContentValues values = new ContentValues();

        values.put(SessionClassContract.SessionClassContractEntry.TOKEN, this.sessionToken);
        values.put(SessionClassContract.SessionClassContractEntry.USER_SESSION, this.userAssociated);
        values.put(SessionClassContract.SessionClassContractEntry.CLOSE_SESSION_KIND, this.closeSessionKind);
        values.put(SessionClassContract.SessionClassContractEntry.SESSION_KIND, this.sessionKind);
        values.put(SessionClassContract.SessionClassContractEntry.START_SESSION, this.startSessionDate);
        values.put(SessionClassContract.SessionClassContractEntry.END_SESSION, this.endSessionDate);
        values.put(SessionClassContract.SessionClassContractEntry.STATUS, this.status);
        return values;
    }
}
