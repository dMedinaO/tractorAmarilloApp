package com.example.tractoramarilloapp;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tractoramarilloapp.handlers.HandlerInforme;
import com.example.tractoramarilloapp.handlers.HandlerMaquinaria;
import com.example.tractoramarilloapp.handlers.SessionHandler;
import com.example.tractoramarilloapp.nfc.NFCHandler;

import static com.example.tractoramarilloapp.InternetStatus.isOnline;

public class MainActivity_maquinaria extends AppCompatActivity {

    private TextView textMensajeAlert,textUsuario,textRut,textComentarioLink;
    private ImageView imageComentario,imageSync,imageSignal;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private ProgressDialog dialog;

    static final int COMENTARIO_REQUEST = 2;

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
        textComentarioLink = (TextView) findViewById(R.id.textComentarioLink);
        imageSignal = (ImageView) findViewById(R.id.imageSignal);
        imageSync = (ImageView) findViewById(R.id.imageSync);
        imageComentario = (ImageView) findViewById(R.id.imageComentario);

        // SHARED PREFERENCES
        prefs = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
        editor = prefs.edit();
        //String nombreUsuario = prefs.getString("usuario","null");//corresponde al ID del usuario


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

        imageComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity_maquinaria.this,MainActivity_comentario.class);
                startActivityForResult(intent,COMENTARIO_REQUEST);
            }
        });

        textComentarioLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity_maquinaria.this,MainActivity_comentario.class);
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
                Toast.makeText(MainActivity_maquinaria.this,"Comentario guardado exitosamente!.",Toast.LENGTH_SHORT).show();
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
        String response = this.nfcHandler.readerTAGNFC(intent);


        //CONDICION SI EL TAG VIENE VACIO
        if (!response.equalsIgnoreCase("VOID")){

            //SPLIT TO ARRAY THE VALUES OF TAG
            String[] arrayResponse = response.split(":");
            String idUsuario = prefs.getString("idUsuario","null");
            String modalidad = prefs.getString("modalidad","null");
            Log.e("RESPONSE-TAG",  "response: "+response);


            this.context = getApplicationContext();
            this.handlerMaquinaria = new HandlerMaquinaria(idUsuario, response, this.context);
            final int responseHandler = this.handlerMaquinaria.applyFluxe();
            Log.e("RESPONSE-HANDLER", responseHandler + " response");

            //WRITE NFC OK
            if (responseHandler == 0){

                levantarDialog(MainActivity_maquinaria.this,"Este proceso puede tardar un momento. Favor espere...");

                HandlerInforme handlerInforme = new HandlerInforme(this.context);
                String predio = prefs.getString("idPredio", "0");
                String tokenSession = prefs.getString("tokenSession", "null");
                final int idInforme = handlerInforme.addElementToInforme(response.split(":")[0], idUsuario, predio, tokenSession, arrayResponse[4]);

                final String [] tagRead = response.split(":");

                Log.e("TAG TOKEN SESION",  "token: "+tokenSession);

                //FORMAMOS EL STRING DEL NEW TAG CON RESPECTO A SI LA MAQUINARIA SE ENCUENTRA HABILITADA O NO
                String newTag = "";
                if (tagRead[2].equalsIgnoreCase("0")){//Maquina desocupada
                    newTag = tagRead[0] + ":"+tagRead[1]+":1:"+tokenSession.split("_")[1]+":-";
                }else{

                    newTag = tagRead[0] + ":"+tagRead[1]+":1:"+tokenSession.split("_")[1]+":"+tagRead[4];
                    Log.e("TOKEN-ERROR", newTag);
                }

                Log.e("WRITE", newTag+" new text to NFC");
                myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);


                    int responseWrite = this.nfcHandler.writeNFC(newTag, myTag, pendingIntent, writeTagFilters);
                    if (responseWrite == 0){

                        Handler handler = new Handler();

                        if (dialog.isShowing()) {
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                dialog.dismiss();
                                editor.putString("nameMaquinaria",tagRead[2]);
                                editor.putString("tagMaquinaria",tagRead[0]);
                                editor.putString("idInforme",idInforme+"");//se adiciona el ID del informe generado

                                editor.commit();
                                Log.e("HANDLER", "OK");
                                Intent intent2 = new Intent(MainActivity_maquinaria.this, MainActivity_horometro.class);
                                intent2.putExtra("flagHorometro", "1");
                                startActivity(intent2);
                                finish();
                                }
                            }, 2000);

                    }

                }else{
                    Log.e("HANDLER", "ERROR");
                    alertWriteNFC("Error al escribir NFC. Favor intente nuevamente...");
                }


            }else if(responseHandler == -1){// Pueden ser varios tipos de TAG

                Log.e("HANDLER", "ERROR IS NOT MACHINE");
                if (arrayResponse[1].equalsIgnoreCase("1")){
                    alertWriteNFC("El TAG no corresponde a una maquina. Favor acercar el dispositivo a una maquinaria");
                }
                if (arrayResponse[1].equalsIgnoreCase("2")){

                    if (idUsuario.equalsIgnoreCase(arrayResponse[2])) {

                        //modalidad BOSS
                        if (modalidad.equalsIgnoreCase("1")){
                            Log.e("HANDLER", "El tag es un operador, se cierra la sesión");
                            String tokenSession = prefs.getString("tokenSession", "null");
                            if (new SessionHandler(this.context).closeSession(tokenSession)) {
                                editor.remove("idUsuario");
                                editor.remove("tokenSession");
                                editor.commit();
                                Intent intent2 = new Intent(MainActivity_maquinaria.this, MainActivity_jefe.class);
                                startActivity(intent2);
                                finish();
                            }else{
                                Log.e("TAG:ERROR", "No se que paso aquí!!!");
                            }
                        }
                        //modalidad WORKER
                        else{
                            Log.e("HANDLER", "El tag es un operador, se cierra la sesión");
                            String tokenSession = prefs.getString("tokenSession", "null");
                            if (new SessionHandler(this.context).closeSession(tokenSession)) {
                                editor.remove("idUsuario");
                                editor.remove("tokenSession");
                                editor.remove("modalidad");
                                editor.commit();
                                Intent intent2 = new Intent(MainActivity_maquinaria.this, MainActivity.class);
                                startActivity(intent2);
                                finish();
                            }else{
                                Log.e("TAG:ERROR", "No se que paso aquí!!!");
                            }
                        }


                    }else{
                        Log.e("TAG ERROR:", "ingresa otro usuario. Sesión esta tomada por otro usuario");
                        Toast.makeText(MainActivity_maquinaria.this, "Existe una sesión activa en este equipo...", Toast.LENGTH_SHORT).show();
                    }



                }
                if (arrayResponse[1].equalsIgnoreCase("4")){
                    alertWriteNFC("El TAG no corresponde a una maquina. Favor acercar el dispositivo a una maquina");
                }

            }else if(responseHandler == -2){
                Log.e("HANDLER", "ERROR MACHINE OCUPADA");
                alertWriteNFC("La maquinaria no se encuentra registrada...");
            }else if(responseHandler == -4){
                Log.e("HANDLER", "ERROR OPERADOR NO CORRESPONDE");
                alertWriteNFC("Actualmente no está habilitado para operar la maquinaria seleccionada...");
            }


            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
                myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            }

        }else{
            Log.e("TAG ERROR:","response VOID: "+response);
            alertWriteNFC("Error al leer el TAG. Favor acerque nuevamente el dispositivo a la maquinaria.");
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

    public void levantarDialog(Context context, String msg) {

        dialog = new ProgressDialog(context);
        dialog.setTitle("Escribiendo");
        dialog.setMessage(msg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();

    }
}


