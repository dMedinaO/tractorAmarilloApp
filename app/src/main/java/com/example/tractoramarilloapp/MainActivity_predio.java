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
import android.os.Handler;
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

import com.example.tractoramarilloapp.handlers.SessionHandler;
import com.example.tractoramarilloapp.model.Predio;
import com.example.tractoramarilloapp.nfc.NFCHandler;
import com.example.tractoramarilloapp.persistence.HandlerDBPersistence;
import com.kofigyan.stateprogressbar.StateProgressBar;

import java.util.ArrayList;
import java.util.Date;

import static com.example.tractoramarilloapp.InternetStatus.isOnline;

public class MainActivity_predio extends AppCompatActivity {

    //atributo para representar el valor obtenido desde la activity previa
    private String [] predioString;//para almacenar los predios a mostrar en la lista
    private String [] predioCodeInterno; // para almacenar la informacion de los codigo interno del predio, esto se hace por si los locos son wns y ponen un predio  repetido

    private TextView textUsuario,textRut,textPredioNombre,textMensajeAlert,textComentarioLink,msjMotivacional;
    private ImageView imageComentario,imageSync,imageSignal;
    private Button buttonPredio;
    private int flagLogin;

    static final int COMENTARIO_REQUEST = 2;

    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;

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

        //metodo que permite completar los arreglos de string de predios con la informacion de la base de datos
        this.getNamePredios();
        this.context = getApplicationContext();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predio);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions( ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar);

        View customActionBarView = actionBar.getCustomView();

        //FINDBYID VARIABLES
        buttonPredio = (Button) findViewById(R.id.buttonAceptarPredio);

        textPredioNombre = (TextView) findViewById(R.id.textPredioNombre);
        textMensajeAlert = (TextView) findViewById(R.id.textMensajeAlert);
        textComentarioLink = (TextView) findViewById(R.id.textComentarioLink);
        imageSignal = (ImageView) findViewById(R.id.imageSignal);
        imageSync = (ImageView) findViewById(R.id.imageSync);
        imageComentario = (ImageView) findViewById(R.id.imageComentario);

        final RelativeLayout relativePredioSelect = findViewById(R.id.relativePredioSelect);
        final RelativeLayout relativePredioInfo = findViewById(R.id.relativePredioInfo);


        // SHARED PREFERENCES
        prefs = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
        editor = prefs.edit();
        String nombreUsuario = prefs.getString("usuario","null");
        String rutUsuario = prefs.getString("usuario_rut","null");


        //NFC
        context = this;
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[]{tagDetected};
        this.nfcHandler = new NFCHandler(this, context, nfcAdapter);


        //instanciamos al handler de
        String text = this.nfcHandler.readerTAGNFC(getIntent());


        //Espera los 5 segundos para cerrar la sesión luego de haberla iniciado
        flagLogin = 0;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                flagLogin = 1;
            }
        }, 5000);


        // STATE PROGRESS BAR CREATE
        String[] descriptionData = {" "," ", " ", " "};
        final StateProgressBar stateProgressBar = (StateProgressBar) findViewById(R.id.stateProgressBar);
        stateProgressBar.setMaxStateNumber(StateProgressBar.StateNumber.FOUR);
        stateProgressBar.setStateDescriptionData(descriptionData);

        stateProgressBar.setStateDescriptionTypeface("fonts/RobotoSlab-Light.ttf");
        stateProgressBar.setStateNumberTypeface("fonts/Questrial-Regular.ttf");
        //<\ STATE PROGRESS BAR CREATE


        // SPINNER CREATE
        final Spinner spinner = (Spinner) findViewById(R.id.spinnerPredio);
        //String[] letra = {"MARIA","CARLOS","RICARDO","LUIS","FRANCISCO"};
        //spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, this.predioString));

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item,this.predioString);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);
        //spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //<\ SPINNER CREATE



        buttonPredio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putString("namePredio",spinner.getSelectedItem().toString());
                editor.putInt("idPredio",spinner.getSelectedItemPosition());
                editor.commit();

                Intent intent = new Intent(MainActivity_predio.this,MainActivity_maquinaria.class);
                startActivity(intent);
                finish();

            }
        });

        imageComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity_predio.this,MainActivity_comentario.class);
                startActivityForResult(intent,COMENTARIO_REQUEST);
            }
        });

        textComentarioLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity_predio.this,MainActivity_comentario.class);
                startActivityForResult(intent,COMENTARIO_REQUEST);
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

        // Check which request we're responding to
        if (requestCode == COMENTARIO_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                String comentariosResult = data.getStringExtra("comentario");
                Toast.makeText(MainActivity_predio.this,"Comentario guardado exitosamente!.",Toast.LENGTH_SHORT).show();
                editor.putString("comentarios",comentariosResult);
                editor.commit();

            }
        }

    }

    @Override
    public void onBackPressed() { }

    @Override
    protected void onNewIntent(Intent intent) {
        this.context = getApplicationContext();
        setIntent(intent);
        String response = this.nfcHandler.readerTAGNFC(intent);

        if (!response.equalsIgnoreCase("VOID")){

            String[] arrayResponse = response.split(":");
            String idUsuario = prefs.getString("idUsuario","null");
            String modalidad = prefs.getString("modalidad","null");


            if (modalidad.equalsIgnoreCase("1") || modalidad.equalsIgnoreCase("2")){

            if (idUsuario.equalsIgnoreCase(""+arrayResponse[0])){

                if (flagLogin == 1){

                    Log.e("TAG 2","Pulsera nuevamente: "+response+" usuario: "+idUsuario);
                    editor.clear().commit();
                    Intent intent2 = new Intent(MainActivity_predio.this,MainActivity.class);
                    startActivity(intent2);
                    finish();

                }else{
                    Toast.makeText(MainActivity_predio.this,"Debes esperar al menos 5 segundos para cerrar la sesión...",Toast.LENGTH_SHORT).show();
                }


            }


        }else if (modalidad.equalsIgnoreCase("2")){//MODALIDAD TRABAJADOR PERRO ESCLAVO


            if (idUsuario.equalsIgnoreCase(""+arrayResponse[2])){//PARA COMPARAR CON EL ID DEL USUARIO

                if (flagLogin == 1){

                    //CERRAMOS SESION
                    String tokenSession = prefs.getString("tokenSession", "null");
                    if (new SessionHandler(this.context).closeSession(tokenSession)){
                        Log.e("TAG 2","Pulsera nuevamente: "+response+" usuario: "+idUsuario);
                        editor.clear().commit();//SE BORRA TODA LA DATA DEL SHARED QUE HA ESTADO ALMACENADA
                        Intent intent2 = new Intent(MainActivity_predio.this,MainActivity.class);
                        startActivity(intent2);
                        finish();
                    }else{
                        Log.e("TAG:ERROR", "No se que paso :(");
                    }


                }else{
                    Toast.makeText(MainActivity_predio.this,"Debes esperar al menos 5 segundos para cerrar la sesión...",Toast.LENGTH_SHORT).show();
                }

                if (arrayResponse[1].equalsIgnoreCase("3") || arrayResponse[1].equalsIgnoreCase("4")){
                    Log.e("TAG INVALIDO ","Pulsera: "+arrayResponse[0]+" Usuario: "+idUsuario);
                    alertWriteNFC("TAG invalido. No esta permitido est");
                }


            }

            }else{
                Log.e("VALIDACION: ","Pulsera: "+arrayResponse[0]+" Usuario: "+idUsuario);
                Toast.makeText(MainActivity_predio.this,"Existe una sesión activa en este equipo...",Toast.LENGTH_SHORT).show();
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

    /**
     * Metodo que permite obtener los predios existentes en el dispositivo
     */
    public void getNamePredios(){

        HandlerDBPersistence handlerDBPersistence = new HandlerDBPersistence(this);
        ArrayList<Predio> listPredio = handlerDBPersistence.getPredio();

        this.predioCodeInterno = new String[listPredio.size()];
        this.predioString = new String[listPredio.size()];

        for (int i=0; i<listPredio.size(); i++){

            this.predioString[i] = listPredio.get(i).getNamePredio();
            this.predioCodeInterno[i] = listPredio.get(i).getCode_internoPredio();
        }

        handlerDBPersistence.close();
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
