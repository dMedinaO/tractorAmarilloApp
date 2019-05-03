package com.example.tractoramarilloapp;


import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Handler;
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

import com.example.tractoramarilloapp.nfc.NFCHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.tractoramarilloapp.InternetStatus.isOnline;

public class MainActivity_detalleSesion extends AppCompatActivity {

    private ImageView imageCheck,imageSync,imageSignal,imageComentario;
    private Button buttonInicio,buttonVolver,buttonVolverLista;
    private TextView mensajeAlert,nombreUsuario,usuarioRUT,nombrePredio,nombreFaena;
    private TextView nombreMaquina,maquinaModelo,maquinaCapacidad;
    private TextView nombreImplemento,implementoTipo,implementoCapacidad;
    private TextView textComentarioLink;
    private int flagInicio;
    private String modalidad;
    private ArrayList<String> result;

    static final int HOROMETRO_REQUEST = 1;
    static final int COMENTARIO_REQUEST = 2;

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
        setContentView(R.layout.activity_detalle_sesion);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar);

        View customActionBarView = actionBar.getCustomView();

        final RelativeLayout relativeInicioSesion = findViewById(R.id.relativeMensajeSesión);
        final RelativeLayout relativeCierreSesion = findViewById(R.id.relativeMensajeSesionOff);
        final RelativeLayout relativeImplemento = findViewById(R.id.relativeImplemento);

        //VARIABLES INIT
        buttonInicio = (Button) findViewById(R.id.buttonIniciarJornada);
        buttonVolver = (Button) findViewById(R.id.buttonVolver);
        buttonVolverLista = (Button) findViewById(R.id.buttonVolverLista);
        imageCheck = (ImageView) findViewById(R.id.imageView5);
        mensajeAlert = (TextView) findViewById(R.id.textMensajeAlert);
        imageSignal = (ImageView) findViewById(R.id.imageSignal);
        imageSync = (ImageView) findViewById(R.id.imageSync);
        imageComentario = (ImageView) findViewById(R.id.imageComentario);


        nombreUsuario = (TextView) findViewById(R.id.textNombreUsuario);
        usuarioRUT = (TextView) findViewById(R.id.textRUT);
        nombrePredio = (TextView) findViewById(R.id.textPredioNombre);
        nombreFaena = (TextView) findViewById(R.id.textFaena);
        nombreMaquina = (TextView) findViewById(R.id.textMaquinariaDescripcion);
        maquinaModelo = (TextView) findViewById(R.id.textMaquinariaModelo);
        maquinaCapacidad = (TextView) findViewById(R.id.textMaquinariaCapacidad);
        nombreImplemento = (TextView) findViewById(R.id.textImplementoDescripcion);
        implementoTipo = (TextView) findViewById(R.id.textImplementoTipo);
        implementoCapacidad = (TextView) findViewById(R.id.textImplementoCapacidad);
        textComentarioLink = (TextView) findViewById(R.id.textComentarioLink);

        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        flagInicio=0;

        //NFC CONFIGURATION
        context = this;
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };
        this.nfcHandler = new NFCHandler(this, context, nfcAdapter);

        //instanciamos al handler de
        String text = this.nfcHandler.readerTAGNFC(getIntent());

        // SHARED PREFERENCES
        prefs = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
        editor = prefs.edit();
        modalidad = prefs.getString("modalidad","0");


        //SET VALUES TO LAYOUT
        nombrePredio.setText(nombrePredio.getText().toString()+""+prefs.getString("namePredio",""));
        nombreFaena.setText(nombreFaena.getText().toString()+""+prefs.getString("nameFaena",""));

        nombreMaquina.setText(nombreMaquina.getText().toString()+""+prefs.getString("nameMaquinaria",""));
        maquinaModelo.setText(maquinaModelo.getText().toString()+""+prefs.getString("model_machine",""));

        nombreImplemento.setText(nombreImplemento.getText().toString()+""+prefs.getString("nameImplemento","Sin implemento"));
        implementoTipo.setText(implementoTipo.getText().toString()+""+prefs.getString("modeL_implemento",""));
        implementoCapacidad.setText(implementoCapacidad.getText().toString()+""+prefs.getString("capacidad_implemento",""));

        nombreUsuario.setText(nombreUsuario.getText().toString()+""+prefs.getString("nameUsuario",""));
        usuarioRUT.setText(usuarioRUT.getText().toString()+""+prefs.getString("usuario_rut",""));

        Log.e("TAG RESULT:",prefs.getAll().toString());

        // BOTON INICIO DE SESION
        buttonInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagInicio = 1;
                buttonInicio.setVisibility(View.GONE);
                buttonVolver.setVisibility(View.GONE);

                //Modalidad inicio sesión JEFE
                if (modalidad.equalsIgnoreCase("1")){

                    Intent intent = new Intent(MainActivity_detalleSesion.this,MainActivity_jefeSesiones.class);
                    startActivity(intent);
                    finish();

                }else{
                    relativeInicioSesion.setVisibility(View.VISIBLE);
                }

            }
        });

        // BOTON VOLVER AL ACTIVITY DE FAENA
        buttonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //limipia los datos de faena obtenidos anteriormente
                editor.remove("nameFaena");
                editor.remove("idFaena");
                editor.commit();

                Intent intent = new Intent(MainActivity_detalleSesion.this,MainActivity_faena.class);
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

        //go to comment window
        imageComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity_detalleSesion.this,MainActivity_comentario.class);
                startActivityForResult(intent,COMENTARIO_REQUEST);
            }
        });

        textComentarioLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity_detalleSesion.this,MainActivity_comentario.class);
                startActivityForResult(intent,COMENTARIO_REQUEST);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        final RelativeLayout relativeInicioSesion = findViewById(R.id.relativeMensajeSesión);
        final RelativeLayout relativeCierreSesion = findViewById(R.id.relativeMensajeSesionOff);
        final RelativeLayout relativeImplemento = findViewById(R.id.relativeImplemento);

        // Check which request we're responding to
        if (requestCode == HOROMETRO_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                String data2 = data.getStringExtra("horometro");


                relativeInicioSesion.setVisibility(View.GONE);
                relativeCierreSesion.setVisibility(View.VISIBLE);



                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        String currentDateandTime = sdf.format(new Date());

                        editor.putString("fin_implemento", currentDateandTime);
                        editor.clear().commit();

                        //editor.clear().commit();
                        if (modalidad.equalsIgnoreCase("1")){
                            Intent intent = new Intent(MainActivity_detalleSesion.this,MainActivity_jefe.class);
                            startActivity(intent);
                            finish();
                        }
                        if (modalidad.equalsIgnoreCase("2")){
                            Intent intent = new Intent(MainActivity_detalleSesion.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }


                    }
                }, 3000);
            }
            if (resultCode == RESULT_CANCELED){

            }
        }

        if (requestCode == COMENTARIO_REQUEST){

            if (resultCode == RESULT_OK){
                String comentariosResult = data.getStringExtra("comentario");
                Toast.makeText(MainActivity_detalleSesion.this,"Comentario guardado exitosamente!.",Toast.LENGTH_SHORT).show();
                editor.putString("comentarios",comentariosResult);
                editor.commit();
            }

            if (resultCode == RESULT_CANCELED) {

            }

        }
    }

    @Override
    public void onBackPressed() { }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        String response = this.nfcHandler.readerTAGNFC(intent);
        Log.e("TAG TAG","response: "+response);

        if (!response.equalsIgnoreCase("VOID")){

            Log.e("TAG TAG TAG","response: "+response);
            //SPLIT TO ARRAY THE VALUES OF TAG
            String[] arrayResponse = response.split(":");
            String tagImplemento = prefs.getString("tagImplemento","null");
            String tagMaquinaria = prefs.getString("tagMaquinaria","null");
            String idUsuario = prefs.getString("idUsuario","null");

            // IF SI EL TAG ES EL IMPLEMENTO
            if (arrayResponse[1].equalsIgnoreCase("4")){


                Log.e("TAG TAG TAG","TAG es un implemento");

                //Validacion para que marque primero el inicio de sesión para luego poder cambiar el implemento
                if (flagInicio!=1){
                    Log.e("TAG TAG TAG","FLAG - No ha iniciado sesión");
                    //Toast.makeText(MainActivity_detalleSesion.this,"Debe iniciar sesión antes de realizar esta operación...",Toast.LENGTH_SHORT).show();
                    alertWriteNFC("Debe iniciar sesión antes de realizar esta operación...");
                }else{
                    String currentDateandTime = sdf.format(new Date());

                    editor.putString("fin_implemento", currentDateandTime);
                    editor.remove("tagImplemento");
                    editor.remove("nameImplemento");
                    editor.commit();
                    Intent intent2 = new Intent(MainActivity_detalleSesion.this,MainActivity_implemento.class);
                    startActivity(intent2);
                    finish();
                }

            }
            // IF SI EL TAG ES LA PULSERA
            if (idUsuario.equalsIgnoreCase(""+arrayResponse[0])) {
                Log.e("TAG TAG TAG","TAG usuario "+response);
                //Toast.makeText(MainActivity_detalleSesion.this,"Para cerrar sesión acerque el dispositivo a la maquinaria...",Toast.LENGTH_SHORT).show();
                alertWriteNFC("Debe iniciar sesión antes de realizar esta operación...");
            }
            // UF SI EL TAG ES LA MAQUINARIA Y CIERRA LA SESION
            if(tagMaquinaria.equalsIgnoreCase(""+arrayResponse[0])){

                if (flagInicio!=1){

                    alertWriteNFC("Debe iniciar sesión antes de realizar esta operación...");
                    //Toast.makeText(MainActivity_detalleSesion.this,"Debe iniciar sesión antes de realizar esta operación...",Toast.LENGTH_SHORT).show();
                }else{
                    Log.e("TAG 8: ","Maquinaria cierre sesión: "+arrayResponse[0]+" maquina: "+nombreMaquina);

                    //editor.clear().commit();
                    Intent intent2 = new Intent(MainActivity_detalleSesion.this,MainActivity_horometro.class);
                    intent2.putExtra("flagHorometro","2");
                    startActivityForResult(intent2,HOROMETRO_REQUEST);
                    //finish();



                }
            }

        }else{
            Log.e("TAG ERROR:","response VOID: "+response);
            alertWriteNFC("Error al leer el TAG. Favor acerque nuevamente el dispositivo al TAG.");
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

    public void alertWriteNFC(String message){
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Mensaje")
                .setMessage(message)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.cancel();
                    }
                });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
