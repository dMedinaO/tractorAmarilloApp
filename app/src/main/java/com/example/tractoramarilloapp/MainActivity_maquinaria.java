package com.example.tractoramarilloapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tractoramarilloapp.handlers.HandlerMaquinaria;
import com.example.tractoramarilloapp.nfc.NFCHandler;

import static com.example.tractoramarilloapp.InternetStatus.isOnline;

public class MainActivity_maquinaria extends AppCompatActivity {

    private TextView textMensajeAlert,textUsuario,textRut;
    private ImageView imageComentario,imageSync,imageSignal;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    //se agrega el handler asociado a la maquinaria
    private HandlerMaquinaria handlerMaquinaria;

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
        String nombreUsuario = prefs.getString("usuario","null");//corresponde al ID del usuario


        //NFC CONFIGURATION
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };
        this.nfcHandler = new NFCHandler(this, context, nfcAdapter);
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
        //tvNFCContent.setText("NFC Content: " + response);
        //Toast.makeText(MainActivity_maquinaria.this,"MAQUINA: "+response,Toast.LENGTH_SHORT).show();

        editor.putInt("id_maquinaria",1);
        editor.putString("maquinaria_nombre",response);
        editor.putString("maquinaria_modelo","XSRW-R2");
        editor.putString("maquinaria_capacidad","1000kg");
        editor.commit();

        String nombreUsuario = prefs.getString("usuario","null");
        //NFC CONFIGURATION
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };

        this.nfcHandler = new NFCHandler(this, context, nfcAdapter);
        String text = this.nfcHandler.readerTAGNFC(getIntent());
        this.context = getApplicationContext();
        this.handlerMaquinaria = new HandlerMaquinaria(nombreUsuario, text, this.context);

        int responseHander = this.handlerMaquinaria.applyFluxe();
        Log.e("RESPONSE-HANDLER", responseHander + " response");

        if (responseHander == 0 || responseHander == -3){//todo esta ok!!!

            String [] tagRead = text.split(":");
            String newTag = tagRead[0]+":"+tagRead[1]+":"+tagRead[2]+":1:"+nombreUsuario;
            Log.e("WRITE", newTag+" new text to NFC");
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            int responseWrite = this.nfcHandler.writeNFC(newTag, myTag, pendingIntent, writeTagFilters);
            if (responseWrite == 0){

                editor.putString("nameMaquinaria",tagRead[2]);
                editor.putString("tagMaquinaria",tagRead[0]);
                editor.commit();
                Log.e("HANDLER", "OK");
                Intent intent2 = new Intent(MainActivity_maquinaria.this,MainActivity_horometro.class);
                intent2.putExtra("flagHorometro","1");
                startActivity(intent2);
                finish();

            }else{
                Log.e("HANDLER", "ERROR");
            }

        }

        /* cerrar sesion
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
        */

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


