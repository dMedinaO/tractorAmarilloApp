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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tractoramarilloapp.handlers.HandlerImplemento;
import com.example.tractoramarilloapp.handlers.HandlerInforme;
import com.example.tractoramarilloapp.nfc.NFCHandler;
import com.example.tractoramarilloapp.utils.ConnectivityApplication;
import com.example.tractoramarilloapp.utils.ConnectivityReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.tractoramarilloapp.InternetStatus.isOnline;

public class MainActivity_implemento extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private TextView textUsuario,textRut,textPredioNombre,textMensajeAlert,textComentarioLink;
    private TextView unidadLocal;
    private ImageView imageComentario,imageSync,imageSignal;
    private Button buttonImplemento;
    private ProgressDialog dialog;

    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;

    private SimpleDateFormat sdf;
    static final int COMENTARIO_REQUEST = 2;

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
        textComentarioLink = (TextView) findViewById(R.id.textComentarioLink);
        unidadLocal = (TextView) findViewById(R.id.textUnidadLocal);
        buttonImplemento = (Button) findViewById(R.id.buttonAceptarImplemento);
        imageSignal = (ImageView) findViewById(R.id.imageSignal);
        imageSync = (ImageView) findViewById(R.id.imageSync);
        imageComentario = (ImageView) findViewById(R.id.imageComentario);


        // Chequea constantemente si hay internet o no
        checkConnection();


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

        imageComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity_implemento.this,MainActivity_comentario.class);
                startActivityForResult(intent,COMENTARIO_REQUEST);
            }
        });

        textComentarioLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity_implemento.this,MainActivity_comentario.class);
                startActivityForResult(intent,COMENTARIO_REQUEST);
            }
        });

        //Actualiza la cantidad de unidades locales
        syncUnityLocal();

    }

    public void syncUnityLocal(){
        int unidadlocalcount = new HandlerInforme(getApplicationContext()).getUnidadesLocalesNumber();
        unidadLocal.setText("U. Local "+unidadlocalcount);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to
        if (requestCode == COMENTARIO_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                String comentariosResult = data.getStringExtra("comentario");
                Toast.makeText(MainActivity_implemento.this,"Comentario guardado exitosamente!.",Toast.LENGTH_SHORT).show();
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
            String tagMaquina = prefs.getString("tagMaquinaria", "null");
            final String idUsuario = prefs.getString("idUsuario", "null");
            String modalidad = prefs.getString("modalidad", "null");

            this.handlerImplemento = new HandlerImplemento(tagMaquina, response, this.context);

            int responseHandler = this.handlerImplemento.applyFluxeCheck();

            if (responseHandler == 0) {//todos los procesos fueron OK

                levantarDialog(MainActivity_implemento.this, "Este proceso puede tardar un momento. Favor espere...");

                final String tokenSession = prefs.getString("tokenSession", "null");

                final String[] tagRead = response.split(":");
                String newTag = tagRead[0] + ":" + tagRead[1] + ":1:" + idUsuario + ":" + tokenSession.split("_")[1];

                Log.e("WRITE", newTag + " new text to NFC");
                myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                int responseWrite = this.nfcHandler.writeNFC(newTag, myTag, pendingIntent, writeTagFilters);
                if (responseWrite == 0) {

                    Handler handler = new Handler();

                    if (dialog.isShowing()) {
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                dialog.dismiss();

                                String currentDateandTime = sdf.format(new Date());
                                editor.putString("inicio_implemento", currentDateandTime);
                                editor.putString("tagImplemento", tagRead[0]);

                                String idInforme = prefs.getString("idInforme", "null");
                                //creamos un informe del tipo implemento
                                int informeImplemento = new HandlerInforme(getApplicationContext()).addElementToInformeImplemento(tagRead[0], idUsuario, tokenSession, currentDateandTime, idInforme);
                                editor.putString("idInformeImplemento", informeImplemento + "");
                                editor.commit();

                                Log.e("HANDLER", "OK");
                                Intent intent2 = new Intent(MainActivity_implemento.this, MainActivity_faena.class);
                                startActivity(intent2);
                                finish();
                            }
                        }, 2000);

                    }

                } else {
                    Log.e("HANDLER", "ERROR");
                    alertWriteNFC("Error al escribir NFC. Favor intente nuevamente.");
                    if (dialog.isShowing()) {
                        dialog.cancel();
                    }
                }

            } else if (responseHandler == -1) {
                Log.e("HANDLER", "ERROR TAG INVALIDO. NO ES IMPLEMENTO");

                if (arrayResponse[1].equalsIgnoreCase("3")) {

                    if (tagMaquina.equalsIgnoreCase("" + arrayResponse[0])) {//CIERRE POR MAQUINARIA: CERRAR SESION Y TERMINAR REGISTRO DE INFORME, NOTA: ESTO SE REALIZA EN LA VENTANA DE HOROMETRO

                        Log.e("TAG 5: ", "Maquina nuevamente: " + arrayResponse[0] + " maquina: " + tagMaquina);

                        String[] tagRead = response.split(":");
                        String newTag;
                        String tokenSession = prefs.getString("tokenSession", "null");
                        //id maquina, tipo maquinaria, estado uso, token actual, token previo
                        newTag = tagRead[0] + ":" + tagRead[1] + ":0:-:" + tagRead[3];
                        Log.e("TOKEN-ERROR", newTag);


                        myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                        int responseWrite = this.nfcHandler.writeNFC(newTag, myTag, pendingIntent, writeTagFilters); //escribimos que ya se encuentra vacia
                        //editor.clear().commit();
                        Intent intent2 = new Intent(MainActivity_implemento.this, MainActivity_horometro.class);
                        intent2.putExtra("flagHorometro", "3");
                        startActivity(intent2);
                        finish();

                    } else {
                        Log.e("TAG ERROR:", "Es maquina pero distinta a la mia.");
                        alertWriteNFC("Esta maquinaria no corresponde a la seleccionada...");
                    }


                } else {
                    alertWriteNFC("El TAG no corresponde a un implemento. Favor acercar el dispositivo a un implemento");
                }

            } else if (responseHandler == -2) {
                Log.e("HANDLER", "ERROR IMPLEMENTO NO REGISTRADO");
                alertWriteNFC("Implemento no se encuentra registrado.");
            } else if (responseHandler == -4) {
                Log.e("HANDLER", "ERROR OPERADOR NO CORRESPONDE");
                alertWriteNFC("El implemento seleccionado no se puede ocupar con la maquinaria actual");
            }

        } else {
            Log.e("TAG ERROR:", "response VOID: " + response);
            alertWriteNFC("Error al leer el TAG. Favor acerque nuevamente el dispositivo al TAG.");
        }


    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        if (isConnected) {
            //Toast.makeText(MainActivity.this,"HAY INTERNET",Toast.LENGTH_SHORT).show();
            imageSignal.setImageResource(R.mipmap.signal);
        } else {
            //Toast.makeText(MainActivity.this, "NO HAY INTERNET", Toast.LENGTH_SHORT).show();
            imageSignal.setImageResource(R.mipmap.signal_off);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
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
        ConnectivityApplication.getInstance().setConnectivityListener(this);
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
