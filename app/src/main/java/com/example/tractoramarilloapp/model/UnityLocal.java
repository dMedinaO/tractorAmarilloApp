package com.example.tractoramarilloapp.model;

import android.content.ContentValues;

import com.example.tractoramarilloapp.persistence.UnityLocalContract;

/**
 * Clase que permite representar a una unidad local, la cual se asocia a la informacion disponible en el sistema y registrada para ser enviada al servidor
 */
public class UnityLocal {

    //atributos de la clase
    private String idUnidad;

    //informacion sobre el usuario
    private String idUsuario;
    private String tokenSession;
    private String tokenSessionPre;
    private String startSession;
    private String closeSession;
    private String clseSessionKind;

    //informacion sobre la maquinaria
    private String idMaquinaria;
    private String horometroInicial;
    private String horometroFinal;
    private String idPredio;

    //informacion sobre el implemento
    private String isAvailableImplement;
    private String idImplemento;
    private String inicioImplemento;
    private String finImplemento;

    //informacion sobre la faena
    private String idFaena;

    private String statusSend;


    public UnityLocal() {

    }

    //GETTER AND SETTER
    public String getIdUnidad() {
        return idUnidad;
    }

    public void setIdUnidad(String idUnidad) {
        this.idUnidad = idUnidad;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTokenSession() {
        return tokenSession;
    }

    public void setTokenSession(String tokenSession) {
        this.tokenSession = tokenSession;
    }

    public String getTokenSessionPre() {
        return tokenSessionPre;
    }

    public void setTokenSessionPre(String tokenSessionPre) {
        this.tokenSessionPre = tokenSessionPre;
    }

    public String getStartSession() {
        return startSession;
    }

    public void setStartSession(String startSession) {
        this.startSession = startSession;
    }

    public String getCloseSession() {
        return closeSession;
    }

    public void setCloseSession(String closeSession) {
        this.closeSession = closeSession;
    }

    public String getClseSessionKind() {
        return clseSessionKind;
    }

    public void setClseSessionKind(String clseSessionKind) {
        this.clseSessionKind = clseSessionKind;
    }

    public String getIdMaquinaria() {
        return idMaquinaria;
    }

    public void setIdMaquinaria(String idMaquinaria) {
        this.idMaquinaria = idMaquinaria;
    }

    public String getHorometroInicial() {
        return horometroInicial;
    }

    public void setHorometroInicial(String horometroInicial) {
        this.horometroInicial = horometroInicial;
    }

    public String getHorometroFinal() {
        return horometroFinal;
    }

    public void setHorometroFinal(String horometroFinal) {
        this.horometroFinal = horometroFinal;
    }

    public String getIdPredio() {
        return idPredio;
    }

    public void setIdPredio(String idPredio) {
        this.idPredio = idPredio;
    }

    public String getIsAvailableImplement() {
        return isAvailableImplement;
    }

    public void setIsAvailableImplement(String isAvailableImplement) {
        this.isAvailableImplement = isAvailableImplement;
    }

    public String getIdImplemento() {
        return idImplemento;
    }

    public void setIdImplemento(String idImplemento) {
        this.idImplemento = idImplemento;
    }

    public String getInicioImplemento() {
        return inicioImplemento;
    }

    public void setInicioImplemento(String inicioImplemento) {
        this.inicioImplemento = inicioImplemento;
    }

    public String getFinImplemento() {
        return finImplemento;
    }

    public void setFinImplemento(String finImplemento) {
        this.finImplemento = finImplemento;
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

        contentValues.put(UnityLocalContract.UnityLocalContractEntry.CLOSE_SESSION_KIND, this.clseSessionKind);
        contentValues.put(UnityLocalContract.UnityLocalContractEntry.END_SESSION, this.closeSession);
        contentValues.put(UnityLocalContract.UnityLocalContractEntry.FIN_IMPLEMENTO, this.finImplemento);
        contentValues.put(UnityLocalContract.UnityLocalContractEntry.HOROMETRO_FINAL, this.horometroFinal);
        contentValues.put(UnityLocalContract.UnityLocalContractEntry.HOROMETRO_INICIAL, this.horometroInicial);
        contentValues.put(UnityLocalContract.UnityLocalContractEntry.ID_FAENA, this.idFaena);
        contentValues.put(UnityLocalContract.UnityLocalContractEntry.ID_IMPLEMENT, this.idImplemento);
        contentValues.put(UnityLocalContract.UnityLocalContractEntry.ID_MAQUINARIA, this.idMaquinaria);
        contentValues.put(UnityLocalContract.UnityLocalContractEntry.ID_UNIDAD, this.idUnidad);
        contentValues.put(UnityLocalContract.UnityLocalContractEntry.ID_USUARIO, this.idUsuario);
        contentValues.put(UnityLocalContract.UnityLocalContractEntry.ID_PREDIO, this.idPredio);
        contentValues.put(UnityLocalContract.UnityLocalContractEntry.TOKEN_SESSION, this.tokenSession);
        contentValues.put(UnityLocalContract.UnityLocalContractEntry.TOKEN_SESSION_PREV, this.tokenSessionPre);
        contentValues.put(UnityLocalContract.UnityLocalContractEntry.START_SESSION, this.startSession);
        contentValues.put(UnityLocalContract.UnityLocalContractEntry.IS_AVAILABLE_IMPLEMENT, this.isAvailableImplement);
        contentValues.put(UnityLocalContract.UnityLocalContractEntry.INICIO_IMPLEMENTO, this.inicioImplemento);
        contentValues.put(UnityLocalContract.UnityLocalContractEntry.STATUS_SEND, this.statusSend);

        return contentValues;
    }
}
