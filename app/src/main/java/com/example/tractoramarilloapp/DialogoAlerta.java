package com.example.tractoramarilloapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DialogoAlerta extends DialogFragment {

    private String titulo;
    private String mensaje;
    private String mensajeButton;

    public DialogoAlerta(String titulo, String mensaje, String mensajeButton){
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.mensajeButton = mensajeButton;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());

        builder.setMessage(mensaje)
                .setTitle(titulo)
                .setPositiveButton(mensajeButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }
}
