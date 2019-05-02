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

import com.example.tractoramarilloapp.handlers.HandlerImplemento;
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

    private HandlerImplemento handlerImplemento;

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
        String idUsuario = prefs.getString("idUsuario","");
        editor.putString("flagImplemento","0");
        editor.commit();

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

                        editor.putString("flagImplemento","1");
                        editor.commit();
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

        //tvNFCContent.setText("NFC Content: " + response);
        //Toast.makeText(MainActivity_implemento.this,"MAQUINA: "+response,Toast.LENGTH_SHORT).show();

        //SPLIT TO ARRAY THE VALUES OF TAG

        this.context = getApplicationContext();
        //NFC CONFIGURATION
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[]{tagDetected};

        this.nfcHandler = new NFCHandler(this, context, nfcAdapter);
        String text = this.nfcHandler.readerTAGNFC(intent);

        String[] arrayResponse = text.split(":");
        String tagMaquina = prefs.getString("tagMaquinaria", "null");
        String idUsuario = prefs.getString("idUsuario", "null");
        String modalidad = prefs.getString("modalidad", "null");

        this.handlerImplemento = new HandlerImplemento(tagMaquina, text, this.context);

        int responseHandler = this.handlerImplemento.applyFluxeCheck();

        if (tagMaquina.equalsIgnoreCase("" + arrayResponse[0])) {

            Log.e("TAG 5: ", "Maquina nuevamente: " + arrayResponse[0] + " maquina: " + tagMaquina);

            //editor.clear().commit();
            Intent intent2 = new Intent(MainActivity_implemento.this, MainActivity_horometro.class);
            intent2.putExtra("flagHorometro", "3");
            startActivity(intent2);
            finish();

        }

        if (modalidad.equalsIgnoreCase("2")) {


            if (responseHandler == 0){//todos los procesos fueron OK


                String [] tagRead = text.split(":");
                String newTag = tagRead[0]+":"+tagRead[1]+":"+tagRead[2]+":1:"+idUsuario;
                Log.e("WRITE", newTag+" new text to NFC");
                myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                int responseWrite = this.nfcHandler.writeNFC(newTag, myTag, pendingIntent, writeTagFilters);
                if (responseWrite == 0){

                    editor.putString("nameImplemento",tagRead[2]);
                    editor.putString("tagImplemento",tagRead[0]);
                    editor.commit();
                    Log.e("HANDLER", "OK");
                    Intent intent2 = new Intent(MainActivity_implemento.this,MainActivity_faena.class);
                    startActivity(intent2);
                    finish();

                }else{
                    Log.e("HANDLER", "ERROR");
                    alertWriteNFC("Error al escribir NFC. Favor intente nuevamente.");
                }

            } else if (responseHandler == -2) {
                alertWriteNFC("Implemento no se encuentra registrado.");
            } else if (responseHandler == -3) {
                Log.e("HANDLER", "ERROR MACHINE OCUPADA");
                alertWriteNFC("Implemento no se encuentra habilitado para trabajar");
            } else if (responseHandler == -4) {
                Log.e("HANDLER", "ERROR OPERADOR NO CORRESPONDE");
                alertWriteNFC("El implemento seleccionado no se puede ocupar con la maquinaria actual");
            }



        }





        /*
        if (nombreMaquina.equalsIgnoreCase(""+response)){

                Log.e("TAG implemento: ","inicio implemento date: "+currentDateandTime);

                editor.putString("idImplemento",arrayResponse[0]);
                editor.putString("nameImplemento",arrayResponse[2]);
                editor.commit();

                Log.e("TAG 6","Implemento: "+arrayResponse[0]+" maquina: "+tagMaquina);
                Intent intent2 = new Intent(MainActivity_implemento.this,MainActivity_faena.class);
                startActivity(intent2);
                finish();

            }

        }

        if (modalidad.equalsIgnoreCase("1")){

            if (tagMaquina.equalsIgnoreCase(""+arrayResponse[0])){

                Log.e("TAG 5: ","Maquina nuevamente: "+response+" maquina: "+tagMaquina);

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

                Log.e("TAG 6","Implemento: "+response+" maquina: "+tagMaquina);
                Intent intent2 = new Intent(MainActivity_implemento.this,MainActivity_faena.class);
                startActivity(intent2);
                finish();


            }

        }

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }

        */

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

    public void alertWriteNFC(String message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Mensaje")
                .setMessage(message)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
