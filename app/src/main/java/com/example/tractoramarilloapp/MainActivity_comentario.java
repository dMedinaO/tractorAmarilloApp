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

import com.example.tractoramarilloapp.persistence.HandlerDBPersistence;

public class MainActivity_comentario extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentario);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions( ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar);

        View customActionBarView = actionBar.getCustomView();








    }
}
