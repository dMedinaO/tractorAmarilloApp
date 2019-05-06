package com.example.tractoramarilloapp.model;

import android.content.ContentValues;

import com.example.tractoramarilloapp.persistence.ComentarioContract;

/**
 * Clase que permite representar a un comentario, junto con sus valores y la data necesaria para ser enviada al informe de comentarios
 */
public class Comentarios {

    //atributos
    private int idComentario;
    private String idUser;
    private String horaComentario;
    private String content;

    public Comentarios(int idComentario, String idUser, String horaComentario, String content) {
        this.idComentario = idComentario;
        this.idUser = idUser;
        this.horaComentario = horaComentario;
        this.content = content;
    }

    public int getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(int idComentario) {
        this.idComentario = idComentario;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getHoraComentario() {
        return horaComentario;
    }

    public void setHoraComentario(String horaComentario) {
        this.horaComentario = horaComentario;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ContentValues toConentValues(){

        ContentValues contentValues = new ContentValues();

        contentValues.put(ComentarioContract.ComentarioContractEntry.ID_COMENTARIO, this.idComentario);
        contentValues.put(ComentarioContract.ComentarioContractEntry.CONTENT_COMENTARIO, this.content);
        contentValues.put(ComentarioContract.ComentarioContractEntry.HORA_COMENTARIO, this.horaComentario);
        contentValues.put(ComentarioContract.ComentarioContractEntry.ID_USER, this.idUser);

        return contentValues;
    }
}
