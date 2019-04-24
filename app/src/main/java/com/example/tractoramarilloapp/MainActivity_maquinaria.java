package com.example.tractoramarilloapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tractoramarilloapp.nfc.NFCHandler;

public class MainActivity_maquinaria extends AppCompatActivity {

    private TextView textMensajeAlert,textUsuario,textRut;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    //NFC VARIABLES
    NFCHandler nfcHandler;
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    Tag myTag;
    Context context;
    TextView tvNFCContent;
    TextView message;
    Button btnWrite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maquinaria);

        textMensajeAlert = (TextView) findViewById(R.id.textMensajeAlert);
        //textUsuario = (TextView) findViewById(R.id.textUsuario);
        //textRut = (TextView) findViewById(R.id.textUsuarioRut);


        // SHARED PREFERENCES
        prefs = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
        editor = prefs.edit();
        String nombreUsuario = prefs.getString("usuario","null");
        String rutUsuario = prefs.getString("usuario_rut","null");

        //textUsuario.setText("Bienvenido "+nombreUsuario);
        //textRut.setText("Rut: "+rutUsuario);
        //<\SHARED PREFERENCES


        //NFC CONFIGURATION
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





        textMensajeAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                DialogoAlerta dialogo = new DialogoAlerta("Mensaje","Actualmente no está habilitado para operar la maquinaria seleccionada","ACEPTAR");
                dialogo.show(fragmentManager, "tagAlerta");
            }
        });

    }

    @Override
    public void onBackPressed() { }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        String response = this.nfcHandler.readerTAGNFC(intent);
        //tvNFCContent.setText("NFC Content: " + response);
        //Toast.makeText(MainActivity_maquinaria.this,"MAQUINA: "+response,Toast.LENGTH_SHORT).show();

        editor.putInt("id_maquinaria",1);
        editor.putString("maquinaria_nombre",response);
        editor.putString("maquinaria_modelo","XSRW-R2");
        editor.putString("maquinaria_capacidad","1000kg");
        editor.commit();

        String nombreUsuario = prefs.getString("usuario","null");

        if (nombreUsuario.equalsIgnoreCase(""+response)){

            Log.e("TAG 3","Pulsera nuevamente: "+response+" usuario: "+nombreUsuario);
            editor.clear().commit();
            Intent intent2 = new Intent(MainActivity_maquinaria.this,MainActivity.class);
            startActivity(intent2);
            finish();

        }else{

            Log.e("TAG 4","Maquina: "+response+" usuario: "+nombreUsuario);
            Intent intent2 = new Intent(MainActivity_maquinaria.this,MainActivity_horometro.class);
            intent2.putExtra("flagHorometro","1");
            startActivity(intent2);
            finish();
        }

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


