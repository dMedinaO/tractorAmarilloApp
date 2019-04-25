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
import android.support.v4.app.FragmentManager;
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

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.tractoramarilloapp.InternetStatus.isOnline;

public class MainActivity_implemento extends AppCompatActivity {

    private TextView textUsuario,textRut,textPredioNombre,textMensajeAlert;
    private ImageView imageComentario,imageSync,imageSignal;
    private Button buttonImplemento;

    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;

    private SimpleDateFormat sdf;

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
        setContentView(R.layout.activity_implemento);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions( ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar);

        View customActionBarView = actionBar.getCustomView();

        //FINDBYID VARIABLES
        textMensajeAlert = (TextView) findViewById(R.id.textMensajeAlert);
        buttonImplemento = (Button) findViewById(R.id.buttonAceptarImplemento);
        imageSignal = (ImageView) findViewById(R.id.imageSignal);
        imageSync = (ImageView) findViewById(R.id.imageSync);


        // SHARED PREFERENCES
        prefs = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
        editor = prefs.edit();
        String nombreUsuario = prefs.getString("usuario","unknow");
        String rutUsuario = prefs.getString("usuario_rut","9999999-K");

        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        //NFC CONFIGURATION
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };
        this.nfcHandler = new NFCHandler(this, context, nfcAdapter);

        //instanciamos al handler de
        String text = this.nfcHandler.readerTAGNFC(getIntent());


        buttonImplemento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertTrabajarSinImplemento(v);
            }
        });

        textMensajeAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertImplementoError(v);
            }
        });

        // CHECK INTERNET CONNECTION
        if(isOnline(getApplicationContext())){
            imageSignal.setImageResource(R.mipmap.signal);
        }else{
            imageSignal.setImageResource(R.mipmap.signal_off);
        }

    }

    public void alertImplementoError(View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Mensaje")
                .setMessage(R.string.alert_implemento_error)
                .setPositiveButton("ACEPTAR",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void alertTrabajarSinImplemento(View view){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Mensaje")
                .setMessage(R.string.alert_trabajar_implemento)
                .setPositiveButton("ACEPTAR",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent2 = new Intent(MainActivity_implemento.this,MainActivity_faena.class);
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
        //tvNFCContent.setText("NFC Content: " + response);
        //Toast.makeText(MainActivity_implemento.this,"MAQUINA: "+response,Toast.LENGTH_SHORT).show();

        //SPLIT TO ARRAY THE VALUES OF TAG
        String[] arrayResponse = response.split(":");
        String nombreMaquina = prefs.getString("id_maquinaria","null");
        String nombreUsuario = prefs.getString("usuario","null");
        String modalidad = prefs.getString("modalidad","null");

        if (modalidad.equalsIgnoreCase("2")){

            if (nombreMaquina.equalsIgnoreCase(""+arrayResponse[0])){

                Log.e("TAG 5: ","Maquina nuevamente: "+response+" maquina: "+nombreMaquina);

                editor.clear().commit();
                Intent intent2 = new Intent(MainActivity_implemento.this,MainActivity_horometro.class);
                intent2.putExtra("flagHorometro","3");
                startActivity(intent2);
                finish();

            }else if (nombreUsuario.equalsIgnoreCase(""+arrayResponse[0])) {
                Toast.makeText(MainActivity_implemento.this,"Para cerrar sesión acerque el dispositivo a la maquinaria...",Toast.LENGTH_SHORT).show();
            }else {

                String currentDateandTime = sdf.format(new Date());
                editor.putString("inicio_implemento", ""+currentDateandTime);

                Log.e("TAG implemento: ","inicio implemento date: "+currentDateandTime);

                editor.putString("id_implemento",arrayResponse[0]);
                editor.putString("implemento_nombre",arrayResponse[2]);
                editor.putString("implemento_modelo","XXX-RE2");
                editor.putString("implemento_capacidad","5kg");
                editor.commit();

                Log.e("TAG 6","Implemento: "+arrayResponse[0]+" maquina: "+nombreMaquina);
                Intent intent2 = new Intent(MainActivity_implemento.this,MainActivity_faena.class);
                startActivity(intent2);
                finish();
                if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
                    myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                }

            }

        }

        if (modalidad.equalsIgnoreCase("1")){

            if (nombreMaquina.equalsIgnoreCase(""+arrayResponse[0])){

                Log.e("TAG 5: ","Maquina nuevamente: "+response+" maquina: "+nombreMaquina);

                editor.clear().commit();
                Intent intent2 = new Intent(MainActivity_implemento.this,MainActivity_horometro.class);
                intent2.putExtra("flagHorometro","3");
                startActivity(intent2);
                finish();

            }else if (nombreUsuario.equalsIgnoreCase(""+arrayResponse[0])) {
                Toast.makeText(MainActivity_implemento.this,"Para cerrar sesión acerque el dispositivo a la maquinaria...",Toast.LENGTH_SHORT).show();
            }else {

                String currentDateandTime = sdf.format(new Date());
                editor.putString("inicio_implemento", ""+currentDateandTime);

                Log.e("TAG implemento: ","inicio implemento date: "+currentDateandTime);

                editor.putString("id_implemento",arrayResponse[0]);
                editor.putString("implemento_nombre",arrayResponse[2]);
                editor.putString("implemento_modelo","XXX-RE2");
                editor.putString("implemento_capacidad","5kg");
                editor.commit();

                Log.e("TAG 6","Implemento: "+response+" maquina: "+nombreMaquina);
                Intent intent2 = new Intent(MainActivity_implemento.this,MainActivity_faena.class);
                startActivity(intent2);
                finish();
                if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
                    myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                }

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
