package com.example.tractoramarilloapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

public class DialogoAlerta extends DialogFragment {

    private String title;
    private String message;
    private int option;
    private String value;


    public DialogoAlerta(String title, String message, int option){
        this.title = title;
        this.message = message;
        this.option = option;
        this.value="";
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());

        if (option==1){
            builder.setMessage(message)
                    .setTitle(title)
                    .setCancelable(true)
                    .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

        }else{
            builder.setMessage(message)
                    .setTitle(title)
                    .setCancelable(true)
                    .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            setValue("Aceptado");
                            //dialog.cancel();
                        }
                    })
                    .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setValue("Cancelado");
                            //dialog.cancel();
                        }
                    });
        }


        return builder.create();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getOption() {
        return option;
    }

    public void setOption(int option) {
        this.option = option;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
