package com.example.tractoramarilloapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.tractoramarilloapp.model.Comentarios;
import com.example.tractoramarilloapp.persistence.HandlerDBPersistence;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity_comentario extends AppCompatActivity {

    private Button acceptButton,cancelButton;
    private EditText comentarioField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentario);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions( ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_jefe);

        View customActionBarView = actionBar.getCustomView();

        acceptButton = (Button) findViewById(R.id.buttonAccept);
        cancelButton = (Button) findViewById(R.id.buttonCancel);
        comentarioField = (EditText) findViewById(R.id.editTextComentarios);


        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //obtenemos la informacion para almacenar el comentario en la BD
                SharedPreferences prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                String idUser = prefs.getString("idUsuario", "null");

                HandlerDBPersistence handlerDBPersistence = new HandlerDBPersistence(getApplicationContext());

                String sqlQueryInforme = "SELECT * FROM comentario";
                int lastID = handlerDBPersistence.getLastID(sqlQueryInforme, "idComentario");

                if (lastID == -1){
                    lastID = 1;
                }else{
                    lastID++;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime = sdf.format(new Date());
                handlerDBPersistence.saveComentario(new Comentarios(lastID, idUser,  currentDateandTime, comentarioField.getText().toString()));
                Intent output = new Intent();
                output.putExtra("comentario", comentarioField.getText().toString());
                setResult(RESULT_OK, output);
                finish();
            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent output = new Intent();
                setResult(RESULT_CANCELED, output);
                finish();
            }
        });








    }

    @Override
    public void onBackPressed() {

    }
}
