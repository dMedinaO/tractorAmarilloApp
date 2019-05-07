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

import com.example.tractoramarilloapp.handlers.HandlerFaena;
import com.example.tractoramarilloapp.handlers.HandlerInforme;
import com.example.tractoramarilloapp.nfc.NFCHandler;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.tractoramarilloapp.InternetStatus.isOnline;

public class MainActivity_faena extends AppCompatActivity {

    private TextView textComentarioLink;
    private Button buttonFaena;
    private ImageView imageComentario,imageSync,imageSignal;

    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;

    private SimpleDateFormat sdf;
    static final int COMENTARIO_REQUEST = 2;
    HandlerFaena handlerFaena;

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

        // SHARED PREFERENCES
        prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        editor = prefs.edit();

        this.context = getApplicationContext();
        String nombreImplemento = prefs.getString("tagImplemento","null");

        this.handlerFaena = new HandlerFaena("", this.context);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faena);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar);

        View customActionBarView = actionBar.getCustomView();

        //FINDBYID VARIABLES
        textComentarioLink = (TextView) findViewById(R.id.textComentarioLink);
        buttonFaena = (Button) findViewById(R.id.buttonAceptarFaena);
        imageSignal = (ImageView) findViewById(R.id.imageSignal);
        imageSync = (ImageView) findViewById(R.id.imageSync);
        imageComentario = (ImageView) findViewById(R.id.imageComentario);

        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
        //spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, this.handlerFaena.getFaenaList()));

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item,this.handlerFaena.getFaenaList());
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);

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

                editor.putString("nameFaena",spinner.getSelectedItem().toString());
                editor.putString("idFaena",handlerFaena.getFaenaIDList()[spinner.getSelectedItemPosition()]);


                //obtener la informacion necesaria para crear el informe
                SimpleDateFormat sdf;
                sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String tokenSession = prefs.getString("tokenSession", "null");
                String currentDateandTime = sdf.format(new Date());
                String idInforme = prefs.getString("idInforme", "null");
                //String idFaena = spinner.getSelectedItemPosition()+"";
                String idFaena = handlerFaena.getFaenaIDList()[spinner.getSelectedItemPosition()];
                String idUser = prefs.getString("idUsuario", "null");

                int idInformeFaena = new HandlerInforme(getApplicationContext()).addElementToInformeFaena(idFaena, idUser, tokenSession, currentDateandTime, idInforme);
                editor.putString("idInformeFaena", idInformeFaena+"");
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

        imageComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity_faena.this,MainActivity_comentario.class);
                startActivityForResult(intent,COMENTARIO_REQUEST);
            }
        });

        textComentarioLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity_faena.this,MainActivity_comentario.class);
                startActivityForResult(intent,COMENTARIO_REQUEST);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to
        if (requestCode == COMENTARIO_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                String comentariosResult = data.getStringExtra("comentario");
                Toast.makeText(MainActivity_faena.this,"Comentario guardado exitosamente!.",Toast.LENGTH_SHORT).show();
                editor.putString("comentarios",comentariosResult);
                editor.commit();

            }
        }

    }


    @Override
    public void onBackPressed() { }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        this.context = getApplicationContext();
        //NFC CONFIGURATION
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[]{tagDetected};

        this.nfcHandler = new NFCHandler(this, context, nfcAdapter);

        String response = this.nfcHandler.readerTAGNFC(intent);

        if (!response.equalsIgnoreCase("VOID")) {

            //SPLIT TO ARRAY THE VALUES OF TAG
            String[] arrayResponse = response.split(":");
            String tagImplemento = prefs.getString("tagImplemento", "");
            String flagImplemento = prefs.getString("flagImplemento", "");
            String tagMaquina = prefs.getString("tagMaquinaria", "");
            String idUsuario = prefs.getString("idUsuario", "null");

            if (arrayResponse[1].equalsIgnoreCase("3")) {//SI ES MAQUINARIA

                if (tagMaquina.equalsIgnoreCase("" + arrayResponse[0])) {

                    String[] tagRead = response.split(":");
                    String newTag = tagRead[0] + ":" + tagRead[1] + ":" + tagRead[2] + ":0:-";

                    myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    int responseWrite = this.nfcHandler.writeNFC(newTag, myTag, pendingIntent, writeTagFilters); //escribimos que ya se encuentra vacia

                    Log.e("TAG 5: ", "Maquina nuevamente: " + arrayResponse[0] + " maquina: " + tagMaquina);
                    editor.putString("idImplementCierreForzado", tagImplemento);//esto es un parche picante para que se usa para valorar el tag del implemento, si no tiene valores, realmente esta ocupado
                    //si dicho tag tiene valor, significa que fue ocupado pero su sesion fue cerrado forzado.

                    Intent intent2 = new Intent(MainActivity_faena.this, MainActivity_horometro.class);
                    intent2.putExtra("flagHorometro", "3");
                    startActivity(intent2);
                    finish();
                } else{
                    Log.e("TAG ERROR:", "Es maquina pero distinta a la mia.");
                    alertWriteNFC("Esta maquinaria no corresponde a la seleccionada...");
                }
            } else {
                if (flagImplemento.equalsIgnoreCase("0")) {//ESTA TRABAJANDO CON IMPLEMENTO
                    if (arrayResponse[1].equalsIgnoreCase("4")) {//ESTE ES EL CASO A SI CORRESPONDE A IMPLEMENTO

                        if (tagImplemento.equalsIgnoreCase(arrayResponse[0])) {

                            //ACA DEBOD HINCHAR LAS PELOTAS CON EL TAG DEL INFORME ACTUAL

                            String idInforme = prefs.getString("idInformeImplemento", "idInformeImplemento");
                            String currentDateandTime = sdf.format(new Date());
                            new HandlerInforme(getApplicationContext()).closeInformeImplemento(idInforme, currentDateandTime);

                            String[] tagRead = response.split(":");
                            String newTag = tagRead[0] + ":" + tagRead[1] + ":" + tagRead[2] + ":0:-";
                            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                            this.alertEliminarImplemento(newTag, myTag);//le pasamos el new tag para escribir y dejar habilitado el implemento en el caso que corresponda

                        } else {

                            Log.e("TAG-IMPLEMENTO", "IMPLEMENTO NO CORRESPONDE AL ACTUAL");
                            alertWriteNFC("Este implemento no corresponde al seleccionado...");
                        }

                    } else {//ACA ES CUANDO NO CORRESPONDE A NINGUN CASO
                        alertWriteNFC("El TAG no corresponde a un implemento o maquinaria. Favor acercar el dispositivo a un TAG válido.");
                    }
                } else {
                    Log.e("TAG-IMPLEMENTO", "IMPLEMENTO NO HA SIDO SELECCIONADO, NO SE ESTA TRABAJANDO CON IMPLEMENTO");
                }
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

    public void alertNFC(String message){
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


    public void alertEliminarImplemento(final String newTagWrite, final Tag myTag){

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Mensaje")
                .setMessage(R.string.alert_eliminar_implemento)
                .setPositiveButton("ACEPTAR",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        String currentDateandTime = sdf.format(new Date());
                        editor.putString("fin_implemento", currentDateandTime);
                        editor.remove("tagImplemento");
                        editor.remove("nameImplemento");
                        editor.commit();

                        //escribimos la cosa
                        //NFC CONFIGURATION
                        nfcAdapter = NfcAdapter.getDefaultAdapter(MainActivity_faena.this);

                        pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(MainActivity_faena.this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
                        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
                        writeTagFilters = new IntentFilter[]{tagDetected};

                        nfcHandler = new NFCHandler(MainActivity_faena.this, context, nfcAdapter);

                        Log.e("TAG-FAENA", newTagWrite+" TAG a escribir");

                        int responseWrite = nfcHandler.writeNFC(newTagWrite, myTag, pendingIntent, writeTagFilters); //escribimos que ya se encuentra vacia

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