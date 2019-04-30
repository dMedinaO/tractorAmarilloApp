package com.example.tractoramarilloapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tractoramarilloapp.model.Maquinaria;
import com.example.tractoramarilloapp.nfc.NFCHandler;
import com.example.tractoramarilloapp.persistence.HandlerDBPersistence;
import com.example.tractoramarilloapp.handlers.SessionHandler;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences.Editor editor;
    private TextView msjMotivacional;
    NFCHandler nfcHandler;
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    Tag myTag;
    Context context;
    TextView tvNFCContent;
    TextView message;
    Button btnWrite;
    private SessionHandler sessionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        new ValuesTempDB().addElements(this);

        HandlerDBPersistence handlerDBPersistence = new HandlerDBPersistence(this);
        ArrayList<Maquinaria> listMaquinaria = handlerDBPersistence.getMaquinariaList();
        for (int i=0; i<listMaquinaria.size(); i++){
            Log.e("MAQUINARIA", listMaquinaria.get(i).getCodeInternoMachine());
        }

        this.context = this;
        this.sessionHandler = new SessionHandler(this.context);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs =  getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);

        editor = prefs.edit();

        String usuario = "Ricardo Etcheverry";
        String usuarioRut = "24.858.868-3";

        //tvNFCContent = findViewById(R.id.nfc_contents);
       // message = findViewById(R.id.edit_message);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };
        this.nfcHandler = new NFCHandler(this, context, nfcAdapter);

        /*btnWrite = findViewById(R.id.button);
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Click", "Try Write");
                nfcHandler.writeNFC(message.getText().toString(), myTag, pendingIntent, writeTagFilters);
            }
        });*/

        //instanciamos al handler de
        String text = this.nfcHandler.readerTAGNFC(getIntent());
        //.setText("NFC Content: " + text);
        //Toast.makeText(MainActivity.this,"HOLA "+text,Toast.LENGTH_SHORT).show();



        msjMotivacional = (TextView) findViewById(R.id.textMotivacional);

        msjMotivacional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this,"Hola mensaje",Toast.LENGTH_LONG).show();

                Intent intent = new Intent(MainActivity.this,MainActivity_predio.class);
                //intent.putExtra("usuario","Ricardo Etcheverry");
                //intent.putExtra("usuario_rut","24.858.868-3");
                startActivity(intent);


            }
        });

    }

    /**
     * Metodo que permite mostrar los mensajes de error de sesion con respecto al tipo de error existente
     * @param responseSession
     */
    public void showMessageError(int responseSession){

        //ERROR DE PULSERA
        if (responseSession == -1){
            Log.e("TAG-RESPONSE", "TAG NOT AVAILABLE!");
        }

        //ERROR DB
        if (responseSession == -2){

            Log.e("TAG-RESPONSE", "ERROR DB TO INSERT VALUES");
        }

        //ERROR OPERARIO ACTIVO
        if (responseSession == -3){
            Log.e("TAG-RESPONSE", "WORKER IS ACTIVE, DEVICE IS NOT AVAILABLE");
        }

        //ERROR JEFE ACTIVO
        if (responseSession == -4){
            Log.e("TAG-RESPONSE", "BOSS ACTIVE");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        String response = this.nfcHandler.readerTAGNFC(intent);
        //tvNFCContent.setText("NFC Content: " + response);
        //Toast.makeText(MainActivity.this,"USUARIO: "+response,Toast.LENGTH_SHORT).show();

        //editor.putString("usuario",response);
        //editor.putString("usuario_rut","24858868-3");
        //editor.commit();

        Log.e("TAG 1","Pulsera: "+response);

        int responseSession = this.sessionHandler.createSession(response);
        Log.e("SESSION_RESPONSE", responseSession+" response Session");

        //SESSION OK OPERATOR
        if (responseSession == 1) {
            editor.putString("usuario",response.split(":")[2]);
            //UserSession userSession = FA.getUserInformationByCode(response.split(":")[2], this);
            //editor.putString("usuario_rut",userSession.getRutUser());//obtener desde la base de datos!!!
            editor.commit();

            Intent intent2 = new Intent(MainActivity.this,MainActivity_predio.class);
            startActivity(intent2);
            Log.e("TAG-RESPONSE", "SESSION START!");
            //finish();
        }else{
            this.showMessageError(responseSession);
        }


        //finish();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        this.nfcHandler.changeModeWrite(0, pendingIntent, writeTagFilters);//desactivamos

    }

    @Override
    public void onResume(){
        super.onResume();
        this.nfcHandler.changeModeWrite(1, pendingIntent, writeTagFilters);//activamos
    }
}
