package com.example.tractoramarilloapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.tractoramarilloapp.model.Maquinaria;
import com.example.tractoramarilloapp.persistence.HandlerDBPersistence;

import java.util.ArrayList;

public class MainActivity_jefeComentarios extends AppCompatActivity {

    private EditText editComentarios;

    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    private HandlerDBPersistence handlerDB;

    private ArrayList<Maquinaria> arrayMaquinarias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_jefe_comentarios);

        // ACTION BAR INIT
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_jefe);

        View customActionBarView = actionBar.getCustomView();


        final RelativeLayout relativeComentarioImplemento = findViewById(R.id.relativeImplemento);
        final RelativeLayout relativeComentarioMaquinaria = findViewById(R.id.relativeInfoMaquinaria);

        editComentarios = (EditText) findViewById(R.id.editTextComentarios);
        handlerDB = new HandlerDBPersistence(MainActivity_jefeComentarios.this);

        arrayMaquinarias = handlerDB.getMaquinariaList();
        Log.e("TAG MACHINES","valor: "+arrayMaquinarias);

        // SHARED PREFERENCES
        prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        editor = prefs.edit();


        final Bundle bundle = getIntent().getExtras();

        if (bundle != null){

            Log.e("TAG COMMENT MODE","valor: "+bundle.getString("comentario_mode"));

            //Comentarios modo maquinaria
            if (bundle.getString("comentario_mode").equalsIgnoreCase("1")){
                relativeComentarioMaquinaria.setVisibility(View.VISIBLE);


                /*editor.putString("comentarios_maquinaria_jefe",editComentarios.getText().toString());
                Intent output = new Intent();
                setResult(RESULT_OK, output);
                finish();*/

            }else
                //Comentarios modo implemento
                if(bundle.getString("comentario_mode").equalsIgnoreCase("2")){
                    relativeComentarioImplemento.setVisibility(View.VISIBLE);
                    editor.putString("comentarios_implemento_jefe",editComentarios.getText().toString());
                    Intent output = new Intent();
                    setResult(RESULT_OK, output);
                    finish();
                }

        }


    }
}