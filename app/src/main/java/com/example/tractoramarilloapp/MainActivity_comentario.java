package com.example.tractoramarilloapp;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
