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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tractoramarilloapp.nfc.NFCHandler;

import static com.example.tractoramarilloapp.InternetStatus.isOnline;

public class MainActivity_maquinaria extends AppCompatActivity {

    private TextView textMensajeAlert,textUsuario,textRut;
    private ImageView imageComentario,imageSync,imageSignal;
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

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions( ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar);

        View customActionBarView = actionBar.getCustomView();

        textMensajeAlert = (TextView) findViewById(R.id.textMensajeAlert);
        imageSignal = (ImageView) findViewById(R.id.imageSignal);
        imageSync = (ImageView) findViewById(R.id.imageSync);


        // SHARED PREFERENCES
        prefs = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
        editor = prefs.edit();
        String nombreUsuario = prefs.getString("usuario","null");
        String rutUsuario = prefs.getString("usuario_rut","null");


        //NFC CONFIGURATION
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };
        this.nfcHandler = new NFCHandler(this, context, nfcAdapter);

        //instanciamos al handler de
        String text = this.nfcHandler.readerTAGNFC(getIntent());


        textMensajeAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                DialogoAlerta dialogo = new DialogoAlerta("Mensaje","Actualmente no está habilitado para operar la maquinaria seleccionada",1);
                dialogo.show(fragmentManager, "tagAlerta");
            }
        });

        // CHECK INTERNET CONNECTION
        if(isOnline(getApplicationContext())){
            imageSignal.setImageResource(R.mipmap.signal);
        }else{
            imageSignal.setImageResource(R.mipmap.signal_off);
        }

    }

    @Override
    public void onBackPressed() { }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        String response = this.nfcHandler.readerTAGNFC(intent);

        //SPLIT TO ARRAY THE VALUES OF TAG
        String[] arrayResponse = response.split(":");
        String nombreUsuario = prefs.getString("id_usuario","null");
        String modalidad = prefs.getString("modalidad","null");

        editor.putString("nameMaquinaria",arrayResponse[2]);
        editor.putString("tagMaquinaria",arrayResponse[0]);
        editor.commit();

        Log.e("TAG 23323", "Pulsera nuevamente: " + arrayResponse[2] + " usuario: " + nombreUsuario);

        //Modalidad jefe de taller
        if (modalidad.equalsIgnoreCase("1")) {
            if (nombreUsuario.equalsIgnoreCase("" + arrayResponse[2])) {

                Log.e("TAG 3", "Pulsera nuevamente: " + arrayResponse[2] + " usuario: " + nombreUsuario);
                editor.clear().commit();
                Intent intent2 = new Intent(MainActivity_maquinaria.this, MainActivity_jefe.class);
                startActivity(intent2);
                finish();

            } else {

                Log.e("TAG 4", "Maquina: " + arrayResponse[0] + " usuario: " + nombreUsuario);
                Intent intent2 = new Intent(MainActivity_maquinaria.this, MainActivity_horometro.class);
                intent2.putExtra("flagHorometro", "1");
                startActivity(intent2);
                finish();
            }
        }
        //Modalidad gestor
        if (modalidad.equalsIgnoreCase("2")){
            if (nombreUsuario.equalsIgnoreCase(""+arrayResponse[2])){

                Log.e("TAG 3","Pulsera nuevamente: "+arrayResponse[2]+" usuario: "+nombreUsuario);
                editor.clear().commit();
                Intent intent2 = new Intent(MainActivity_maquinaria.this,MainActivity.class);
                startActivity(intent2);
                finish();

            }else{

                Log.e("TAG 4","Maquina: "+arrayResponse[0]+" usuario: "+nombreUsuario);
                Intent intent2 = new Intent(MainActivity_maquinaria.this,MainActivity_horometro.class);
                intent2.putExtra("flagHorometro","1");
                startActivity(intent2);
                finish();
            }
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


