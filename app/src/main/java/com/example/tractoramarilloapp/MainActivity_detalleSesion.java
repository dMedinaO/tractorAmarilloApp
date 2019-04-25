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
import android.support.v7.app.AlertDialog;
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
import java.util.Date;

import static com.example.tractoramarilloapp.InternetStatus.isOnline;

public class MainActivity_detalleSesion extends AppCompatActivity {

    private ImageView imageCheck,imageSync,imageSignal;
    private Button buttonInicio,buttonVolver;
    private TextView mensajeAlert,nombreUsuario,usuarioRUT,nombrePredio,nombreFaena;
    private TextView nombreMaquina,maquinaModelo,maquinaCapacidad;
    private TextView nombreImplemento,implementoTipo,implementoCapacidad;
    static final int HOROMETRO_REQUEST = 1;
    private int flagInicio;

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
        actionBar.setDisplayOptions( ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar);

        View customActionBarView = actionBar.getCustomView();

        final RelativeLayout relativeInicioSesion = findViewById(R.id.relativeMensajeSesión);
        final RelativeLayout relativeCierreSesion = findViewById(R.id.relativeMensajeSesionOff);
        final RelativeLayout relativeImplemento = findViewById(R.id.relativeImplemento);


        buttonInicio = (Button) findViewById(R.id.buttonIniciarJornada);
        buttonVolver = (Button) findViewById(R.id.buttonVolver);
        imageCheck = (ImageView) findViewById(R.id.imageView5);
        mensajeAlert = (TextView) findViewById(R.id.textMensajeAlert);
        imageSignal = (ImageView) findViewById(R.id.imageSignal);
        imageSync = (ImageView) findViewById(R.id.imageSync);


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


        //NFC
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

        nombrePredio.setText(nombrePredio.getText().toString()+""+prefs.getString("predio_nombre","null"));

        nombreMaquina.setText(nombreMaquina.getText().toString()+""+prefs.getString("maquinaria_nombre","null"));
        maquinaModelo.setText(maquinaModelo.getText().toString()+""+prefs.getString("maquinaria_modelo","null"));
        maquinaCapacidad.setText(maquinaCapacidad.getText().toString()+""+prefs.getString("maquinaria_capacidad","null"));

        nombreImplemento.setText(nombreImplemento.getText().toString()+""+prefs.getString("implemento_nombre","null"));
        implementoTipo.setText(implementoTipo.getText().toString()+""+prefs.getString("implemento_modelo","null"));
        implementoCapacidad.setText(implementoCapacidad.getText().toString()+""+prefs.getString("implemento_capacidad","null"));
        nombreFaena.setText(nombreFaena.getText().toString()+""+prefs.getString("faena_nombre","null"));

        nombreUsuario.setText(nombreUsuario.getText().toString()+""+prefs.getString("usuario","null"));
        usuarioRUT.setText(usuarioRUT.getText().toString()+""+prefs.getString("usuario_rut","null"));



        Log.e("HOLA HOLA",prefs.getAll().toString());

        // BOTON INICIO DE SESION
        buttonInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagInicio = 1;
                buttonInicio.setVisibility(View.GONE);
                buttonVolver.setVisibility(View.GONE);
                relativeInicioSesion.setVisibility(View.VISIBLE);


            }
        });

        // BOTON VOLVER AL ACTIVITY DE FAENA
        buttonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //limipia los datos de faena obtenidos anteriormente
                editor.remove("id_faena");
                editor.remove("faena_nombre");
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
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                String data2 = data.getStringExtra("horometro");

                // Do something with the contact here (bigger example below)
                //Toast.makeText(MainActivity_detalleSesion.this,"Hola horometro " + data2,Toast.LENGTH_SHORT).show();
                //mensajeAlert.setText("Acerque el dispositivo al implemento.");
                relativeInicioSesion.setVisibility(View.GONE);
                relativeCierreSesion.setVisibility(View.VISIBLE);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        String currentDateandTime = sdf.format(new Date());
                        //editor.putString("fin_implemento", currentDateandTime);
                        editor.clear().commit();

                        //editor.clear().commit();
                        Intent intent = new Intent(MainActivity_detalleSesion.this,MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }, 3000);
            }
        }
    }

    public void open(View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure,You wanted to make decision");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Toast.makeText(MainActivity_detalleSesion.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                            }
                        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
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

        // IF SI EL TAG ES EL IMPLEMENTO
        if (nombreImplemento.equalsIgnoreCase(""+response)){

            Toast.makeText(MainActivity_detalleSesion.this,"Para cerrar sesión acerque el dispositivo a la maquinaria...",Toast.LENGTH_SHORT).show();

        }
        // IF SI EL TAG ES LA PULSERA
        else if (nombreUsuario.equalsIgnoreCase(""+response)) {

            Toast.makeText(MainActivity_detalleSesion.this,"Para cerrar sesión acerque el dispositivo a la maquinaria...",Toast.LENGTH_SHORT).show();
        }
        // UF SI EL TAG ES LA MAQUINARIA Y CIERRA LA SESION
        else if(nombreMaquina.equalsIgnoreCase(""+response)){

            if (flagInicio!=1){
                Toast.makeText(MainActivity_detalleSesion.this,"Debe iniciar sesión antes de realizar esta operación...",Toast.LENGTH_SHORT).show();
            }else{
                Log.e("TAG 8: ","Maquinaria cierre sesión: "+response+" maquina: "+nombreMaquina);

                //editor.clear().commit();
                Intent intent2 = new Intent(MainActivity_detalleSesion.this,MainActivity_horometro.class);
                intent2.putExtra("flagHorometro","2");
                startActivityForResult(intent2,HOROMETRO_REQUEST);
                //finish();

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
