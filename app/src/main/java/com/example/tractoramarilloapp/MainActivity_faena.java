package com.example.tractoramarilloapp;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tractoramarilloapp.nfc.NFCHandler;

import static com.example.tractoramarilloapp.InternetStatus.isOnline;

public class MainActivity_faena extends AppCompatActivity {

    private Button buttonFaena;
    private ImageView imageComentario,imageSync,imageSignal;
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;

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
        setContentView(R.layout.activity_faena);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar);

        View customActionBarView = actionBar.getCustomView();

        //FINDBYID VARIABLES
        buttonFaena = (Button) findViewById(R.id.buttonAceptarFaena);
        imageSignal = (ImageView) findViewById(R.id.imageSignal);
        imageSync = (ImageView) findViewById(R.id.imageSync);

        // SHARED PREFERENCES
        prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        editor = prefs.edit();

        //NFC CONFIGURATION
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);


        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[]{tagDetected};
        this.nfcHandler = new NFCHandler(this, context, nfcAdapter);

        //instanciamos al handler de
        String text = this.nfcHandler.readerTAGNFC(getIntent());


        // SPINNER CREATE
        final Spinner spinner = (Spinner) findViewById(R.id.spinnerFaena);
        String[] letra = {"ARAR", "LIMPIAR CAMPO"};
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //<\ SPINNER CREATE


        buttonFaena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putString("faena_nombre",spinner.getSelectedItem().toString());
                editor.putInt("id_faena",spinner.getSelectedItemPosition());
                editor.commit();

                Intent intent = new Intent(MainActivity_faena.this, MainActivity_detalleSesion.class);
                startActivity(intent);
                finish();

            }
        });

        // CHECK INTERNET CONNECTION
        if(isOnline(getApplicationContext())){
            imageSignal.setImageResource(R.mipmap.signal);
        }else{
            imageSignal.setImageResource(R.mipmap.signal_off);
        }


    }

    public void alertEliminarImplemento(){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Mensaje")
                .setMessage(R.string.alert_eliminar_implemento)
                .setPositiveButton("ACEPTAR",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        editor.remove("id_implemento");
                        editor.remove("implemento_nombre");
                        editor.remove("implemento_modelo");
                        editor.remove("implemento_capacidad");
                        editor.remove("inicio_implemento");
                        editor.commit();
                        Intent intent2 = new Intent(MainActivity_faena.this,MainActivity_implemento.class);
                        startActivity(intent2);
                        finish();
                    }
                })
                .setNegativeButton("CANCELAR",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() { }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        String response = this.nfcHandler.readerTAGNFC(intent);


        String nombreImplemento = prefs.getString("implemento_nombre","null");
        String nombreMaquina = prefs.getString("maquinaria_nombre","null");
        String nombreUsuario = prefs.getString("usuario","null");

        if (nombreImplemento.equalsIgnoreCase(""+response)){

            Log.e("TAG 7: ","Implemento nuevamente: "+response+" maquina: "+nombreMaquina);
            alertEliminarImplemento();


        }else if (nombreUsuario.equalsIgnoreCase(""+response)) {
            Toast.makeText(MainActivity_faena.this,"Para cerrar sesión acerque el dispositivo a la maquinaria...",Toast.LENGTH_SHORT).show();
        }else if(nombreMaquina.equalsIgnoreCase(""+response)){

            Log.e("TAG 5: ","Maquina nuevamente: "+response+" maquina: "+nombreMaquina);

            editor.clear().commit();
            Intent intent2 = new Intent(MainActivity_faena.this,MainActivity_horometro.class);
            intent2.putExtra("flagHorometro","3");
            startActivity(intent2);
            finish();

        }else{

            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
                myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            }

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