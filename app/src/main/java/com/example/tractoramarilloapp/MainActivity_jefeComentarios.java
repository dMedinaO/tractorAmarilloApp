package com.example.tractoramarilloapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.Image;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.tractoramarilloapp.handlers.HandlerFallas;
import com.example.tractoramarilloapp.handlers.HandlerInforme;
import com.example.tractoramarilloapp.handlers.SessionHandler;
import com.example.tractoramarilloapp.model.Implemento;
import com.example.tractoramarilloapp.model.Maquinaria;
import com.example.tractoramarilloapp.model.UserSession;
import com.example.tractoramarilloapp.nfc.NFCHandler;
import com.example.tractoramarilloapp.persistence.HandlerDBPersistence;
import com.example.tractoramarilloapp.utils.ConnectivityApplication;
import com.example.tractoramarilloapp.utils.ConnectivityReceiver;

import java.util.ArrayList;

public class MainActivity_jefeComentarios extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private EditText editComentarios;
    private Button cancelButton,acceptButton;
    private TextView rutUser, nameUser;
    private ImageView imageSignal;

    private TextView text1, text2, text3, text4;//informacion para la maquinaria
    private TextView textI1, textI2, textI3, textI4;//informacion para el implemento
    private TextView unidadLocal;

    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    private HandlerDBPersistence handlerDB;

    private ArrayList<Maquinaria> arrayMaquinarias;
    private String idJefe;

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
        setContentView(R.layout.activity_main_jefe_comentarios);

        // ACTION BAR INIT
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_jefe);

        View customActionBarView = actionBar.getCustomView();


        final RelativeLayout relativeComentarioImplemento = findViewById(R.id.relativeImplemento);
        final RelativeLayout relativeComentarioMaquinaria = findViewById(R.id.relativeInfoMaquinaria);

        //Contenido del usuario
        this.rutUser = findViewById(R.id.textUsuarioJefe);
        this.nameUser = findViewById(R.id.textRUT);

        //contenido para la maquinaria
        this.text1 = findViewById(R.id.text1);
        this.text2 = findViewById(R.id.text2);
        this.text3 = findViewById(R.id.text3);
        this.text4 = findViewById(R.id.text4);

        //contenido para el implemento
        this.textI1 = findViewById(R.id.textImplemento);
        this.textI2 = findViewById(R.id.textImplementoDescripcion);
        this.textI3 = findViewById(R.id.textImplementoTipo);
        this.textI4 = findViewById(R.id.textImplementoCapacidad);

        editComentarios = (EditText) findViewById(R.id.editTextComentarios);
        cancelButton = (Button) findViewById(R.id.buttonCancel);
        acceptButton = (Button) findViewById(R.id.buttonAccept);
        nameUser = (TextView) findViewById(R.id.textUsuarioJefe);
        rutUser = (TextView) findViewById(R.id.textRUT);
        imageSignal = (ImageView) findViewById(R.id.imageSignal);
        unidadLocal = (TextView) findViewById(R.id.textUnidadLocal);

        // Chequea constantemente si hay internet o no
        checkConnection();

        handlerDB = new HandlerDBPersistence(MainActivity_jefeComentarios.this);

        arrayMaquinarias = handlerDB.getMaquinariaList();
        Log.e("TAG MACHINES","valor: "+arrayMaquinarias);

        // SHARED PREFERENCES
        prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        this.idJefe = prefs.getString("idUsuarioBoss", "null");
        editor = prefs.edit();

        // NFC CONFIGURATION
        context = this;
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };
        this.nfcHandler = new NFCHandler(this, context, nfcAdapter);

        //instanciamos al handler de
        String text = this.nfcHandler.readerTAGNFC(getIntent());

        //Completa los datos del jefe de taller
        this.getValuesUser();

        final Bundle bundle = getIntent().getExtras();

        if (bundle != null){

            Log.e("TAG COMMENT MODE","valor: "+bundle.getString("comentario_mode"));

            //Comentarios modo maquinaria
            if (bundle.getString("comentario_mode").equalsIgnoreCase("1")){

                String codeMaquina = prefs.getString("idMaquina_comentario", "null");
                this.getValuesForShowInView(0, codeMaquina);
                relativeComentarioMaquinaria.setVisibility(View.VISIBLE);
                String descripcionFalla = editComentarios.getText().toString();
                String userID = prefs.getString("idUsuarioBoss", "null");
                String tipoElemento="MAQUINARIA";

                new HandlerFallas(getApplicationContext()).addFallasInDevice(descripcionFalla, userID, codeMaquina, tipoElemento);


            }else
                //Comentarios modo implemento
                if(bundle.getString("comentario_mode").equalsIgnoreCase("2")) {

                    String codeMaquina = prefs.getString("idImplemento_comentario", "null");
                    this.getValuesForShowInView(1, codeMaquina);

                    relativeComentarioImplemento.setVisibility(View.VISIBLE);
                    String descripcionFalla = editComentarios.getText().toString();
                    String userID = prefs.getString("idUser", "null");
                    String tipoElemento = "IMPLEMENTO";

                    new HandlerFallas(getApplicationContext()).addFallasInDevice(descripcionFalla, userID, codeMaquina, tipoElemento);

                }

        }

            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                //Comentarios modo maquinaria
                if (bundle.getString("comentario_mode").equalsIgnoreCase("1")){

                    editor.putString("comentarios_maquinaria_jefe",editComentarios.getText().toString());
                    Intent intent = new Intent(MainActivity_jefeComentarios.this, MainActivity_jefe.class);
                    startActivity(intent);
                    finish();

                }else
                    //Comentarios modo implemento
                    if(bundle.getString("comentario_mode").equalsIgnoreCase("2")){

                        editor.putString("comentarios_implemento_jefe",editComentarios.getText().toString());
                        Intent intent = new Intent(MainActivity_jefeComentarios.this, MainActivity_jefe.class);
                        startActivity(intent);
                        finish();
                    }

                }
            });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent2 = new Intent(MainActivity_jefeComentarios.this,MainActivity_jefe.class);
                startActivity(intent2);
                finish();

            }
        });

        //Actualiza la cantidad de unidades locales
        syncUnityLocal();

    }

    public void syncUnityLocal(){
        int unidadlocalcount = new HandlerInforme(getApplicationContext()).getUnidadesLocalesNumber();
        unidadLocal.setText("U. Local "+unidadlocalcount);
    }

    /**
     * Metodo para obtener la informacion a mostrar en la vista
     */
    public void getValuesForShowInView(int values, String codeInterno){
        Log.e("CODE-VALUES", codeInterno);
        if (values == 0){//modalidad maquinaria

            ArrayList<Maquinaria> maquinariaArrayList = new HandlerDBPersistence(getApplicationContext()).getMaquinariaList();
            for (int i=0; i<maquinariaArrayList.size(); i++) {
                if (maquinariaArrayList.get(i).getCodeInternoMachine().equalsIgnoreCase(codeInterno)) {
                    //this.text1.setText("Información de la Maquinaria: " +maquinariaArrayList.get(i).getNameMachine());
                    this.text2.setText("Descripción: " +maquinariaArrayList.get(i).getNameMachine());
                    this.text3.setText("Modelo: " +maquinariaArrayList.get(i).getModelMachine());
                    this.text4.setText("Patente: " +maquinariaArrayList.get(i).getPatentMachine());

                }
            }
        }else{//modalidad implemento

            ArrayList<Implemento> implementoArrayList = new HandlerDBPersistence(getApplicationContext()).getImplementos();
            for (int i=0; i< implementoArrayList.size(); i++){
                if (implementoArrayList.get(i).getCodeInternoImplemento().equalsIgnoreCase(codeInterno)){
                    this.textI2.setText("Descripción: " +implementoArrayList.get(i).getNameImplement());
                    this.textI3.setText("Fabricante: " +implementoArrayList.get(i).getFabricante());
                    this.textI4.setText("Capacidad: " +implementoArrayList.get(i).getCapacidad());
                }
            }
        }
    }

    public void getValuesUser(){

        UserSession userSession = new SessionHandler(getApplicationContext()).getInformationBoss(this.idJefe);
        this.nameUser.setText("Bienvenido " + userSession.getNameUser());
        this.rutUser.setText("RUT: "+userSession.getRutUser());

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
}
