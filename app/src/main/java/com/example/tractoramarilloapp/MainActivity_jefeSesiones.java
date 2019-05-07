package com.example.tractoramarilloapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tractoramarilloapp.handlers.InformationDetailSession;
import com.example.tractoramarilloapp.handlers.SessionHandler;
import com.example.tractoramarilloapp.model.UserSession;
import com.example.tractoramarilloapp.persistence.SessionClass;
import com.example.tractoramarilloapp.utils.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity_jefeSesiones extends AppCompatActivity {

    private List<String> names;
    private RecyclerView myRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextView nombreJefe, jefeRUT;
    private String idJefe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_jefe_sesiones);

        SharedPreferences prefs =
                getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        this.idJefe = prefs.getString("idUsuarioBoss", "null");

        // ACTION BAR INIT
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions( ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_jefe);

        View customActionBarView = actionBar.getCustomView();

        nombreJefe = (TextView) findViewById(R.id.textUsuarioJefe);
        jefeRUT = (TextView) findViewById(R.id.textRUT);

        this.getValuesUser();

        this.getListInformationDetail();

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

    public void getValuesUser(){

        UserSession userSession = new SessionHandler(getApplicationContext()).getInformationBoss(this.idJefe);
        this.nombreJefe.setText("Bienvenido " + userSession.getNameUser());
        this.jefeRUT.setText("RUT: "+userSession.getRutUser());

    }

    /**
     * Metodo que permite poder obtener toda la informacion asociada a los informes, uuuuuffff esperemos esta wea funcione!!!!
     * @return
     */
    public ArrayList<InformationDetailSession> getListInformationDetail(){

        Log.e("ACTIVE-SESSION", "Entre al metodo ql");
        //obtenemos todas las sesiones activas y en base a los TAG de sesion obtenemos la informacion de los informes que se han generado
        ArrayList<SessionClass> sessionActive = new SessionHandler(getApplicationContext()).getSessionActive();

        ArrayList<InformationDetailSession> listInforme = new ArrayList<>();

        for (int i=0; i<sessionActive.size(); i++){
            Log.e("ACTIVE-SESSION", sessionActive.get(i).getSessionToken());
            Log.e("ACTIVE-SESSION", sessionActive.get(i).getSessionKind());
            Log.e("ACTIVE-SESSION", sessionActive.get(i).getUserAssociated());
            listInforme.add(new InformationDetailSession(sessionActive.get(i).getSessionToken(), getApplicationContext(), sessionActive.get(i).getUserAssociated()));
        }

        for (int i=0; i<listInforme.size(); i++){
            Log.e("ACTIVE-SESSION", listInforme.get(i).getTokenSession());
            Log.e("ACTIVE-SESSION", listInforme.get(i).getUserSession().getIDUser());
            Log.e("ACTIVE-SESSION", listInforme.get(i).getUserSession().getNameUser());
            Log.e("ACTIVE-SESSION", listInforme.get(i).getMaquinaria().getCodeInternoMachine());
            Log.e("ACTIVE-SESSION", listInforme.get(i).getImplemento().getNameImplement());
            Log.e("ACTIVE-SESSION", listInforme.get(i).getFaena().getNameFaena());
        }
        return listInforme;
    }
}
