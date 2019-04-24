package com.example.tractoramarilloapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tractoramarilloapp.nfc.NFCHandler;
import com.kofigyan.stateprogressbar.StateProgressBar;

public class MainActivity_predio extends AppCompatActivity {

    private TextView textUsuario,textRut,textPredioNombre,textMensajeAlert;
    private Button buttonPredio;
    private int flagLogin;

    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    private TextView msjMotivacional;
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
        setContentView(R.layout.activity_predio);

        //FINDBYID VARIABLES
        buttonPredio = (Button) findViewById(R.id.buttonAceptarPredio);
        //textUsuario = (TextView) findViewById(R.id.textUsuario);
        //textRut = (TextView) findViewById(R.id.textUsuarioRut);
        textPredioNombre = (TextView) findViewById(R.id.textPredioNombre);
        textMensajeAlert = (TextView) findViewById(R.id.textMensajeAlert);

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
        writeTagFilters = new IntentFilter[] { tagDetected };
        this.nfcHandler = new NFCHandler(this, context, nfcAdapter);


        //instanciamos al handler de
        String text = this.nfcHandler.readerTAGNFC(getIntent());
        //.setText("NFC Content: " + text);
        //Toast.makeText(MainActivity.this,"HOLA "+text,Toast.LENGTH_SHORT).show();


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
        String[] letra = {"MARIA","CARLOS","RICARDO","LUIS","FRANCISCO"};
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(MainActivity_predio.this,"HOLA spinner "+position,Toast.LENGTH_SHORT).show();

                if (position != 0){



                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //<\ SPINNER CREATE



        buttonPredio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putString("predio_nombre",spinner.getSelectedItem().toString());
                editor.putInt("id_predio",spinner.getSelectedItemPosition());
                editor.commit();

                Intent intent = new Intent(MainActivity_predio.this,MainActivity_maquinaria.class);
                startActivity(intent);
                finish();

            }
        });

        /*
            HACIA VENTANA MAQUINARIA
         */
//        textMensajeAlert.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(MainActivity_predio.this,MainActivity_horometro.class);
//                intent.putExtra("flagHorometro","1");
//                startActivity(intent);
//
//
//            }
//        });

    }

    @Override
    public void onBackPressed() { }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        String response = this.nfcHandler.readerTAGNFC(intent);
        //tvNFCContent.setText("NFC Content: " + response);
        //Toast.makeText(MainActivity_predio.this,"USUARIO: "+response,Toast.LENGTH_SHORT).show();

        String nombreUsuario = prefs.getString("usuario","null");

        if (nombreUsuario.equalsIgnoreCase(""+response)){

            if (flagLogin == 1){

                Log.e("TAG 2","Pulsera nuevamente: "+response+" usuario: "+nombreUsuario);
                editor.clear().commit();
                Intent intent2 = new Intent(MainActivity_predio.this,MainActivity.class);
                startActivity(intent2);
                finish();

            }else{
                Toast.makeText(MainActivity_predio.this,"Debes esperar al menos 5 segundos para cerrar la sesión...",Toast.LENGTH_SHORT).show();
            }


        }else{
            Toast.makeText(MainActivity_predio.this,"Existe una sesión activa en este equipo...",Toast.LENGTH_SHORT).show();
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

}