package com.example.tractoramarilloapp;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.tractoramarilloapp.utils.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity_jefeSesiones extends AppCompatActivity {

    private List<String> names;
    private RecyclerView myRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_jefe_sesiones);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions( ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar);

        View customActionBarView = actionBar.getCustomView();

        names = this.getAllNames();

        myRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        myRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerAdapter(names, R.layout.listasesiones_recycler, new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String name, int position) {
                Toast.makeText(MainActivity_jefeSesiones.this,name + " - "+ position,Toast.LENGTH_SHORT).show();
            }
        });


        myRecyclerView.setAdapter(mAdapter);

    }


    private List<String> getAllNames(){
        return new ArrayList<String>(){{
            add("Jose");
            add("Alejandro");
            add("Ricardo");
            add("Carlos");
            add("Jose");
            add("Jose");
        }};
    }

}
