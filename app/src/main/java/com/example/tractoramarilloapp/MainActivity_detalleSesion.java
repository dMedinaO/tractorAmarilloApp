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

import com.example.tractoramarilloapp.handlers.HandlerInforme;
import com.example.tractoramarilloapp.handlers.SessionHandler;
import com.example.tractoramarilloapp.model.Faena;
import com.example.tractoramarilloapp.model.Implemento;
import com.example.tractoramarilloapp.model.Maquinaria;
import com.example.tractoramarilloapp.model.UserSession;
import com.example.tractoramarilloapp.nfc.NFCHandler;
import com.example.tractoramarilloapp.persistence.HandlerDBPersistence;
import com.example.tractoramarilloapp.utils.FA;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.tractoramarilloapp.InternetStatus.isOnline;

public class MainActivity_detalleSesion extends AppCompatActivity {

    private ImageView imageCheck,imageSync,imageSignal,imageComentario;
    private Button buttonInicio,buttonVolver,buttonVolverLista,buttonVolverListaSesiones;
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
    private FA fa;

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

    RelativeLayout relativeInicioSesion;
    RelativeLayout relativeCierreSesion;
    RelativeLayout relativeImplemento;
    RelativeLayout relativeSesiones;
    RelativeLayout relativeInicio;
    RelativeLayout relativeExpirada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_sesion);
        relativeInicioSesion = findViewById(R.id.relativeMensajeSesión);
        relativeCierreSesion = findViewById(R.id.relativeMensajeSesionOff);
        relativeImplemento = findViewById(R.id.relativeImplemento);
        relativeSesiones = findViewById(R.id.relativeMensajeSesiones);
        relativeInicio = findViewById(R.id.relativeInicio);
        relativeExpirada = findViewById(R.id.relativeMensajeSesionesExpirada);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar);

        View customActionBarView = actionBar.getCustomView();

        //VARIABLES INIT
        buttonInicio = (Button) findViewById(R.id.buttonIniciarJornada);
        buttonVolver = (Button) findViewById(R.id.buttonVolver);
        buttonVolverLista = (Button) findViewById(R.id.buttonVolverLista);
        buttonVolverListaSesiones = (Button) findViewById(R.id.buttonVolverListaSesiones);
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

        if (prefs.getBoolean("fromSesiones",false)){//true
            flagInicio=1;
            relativeSesiones.setVisibility(View.VISIBLE);
            relativeInicio.setVisibility(View.GONE);

        }else{
            relativeSesiones.setVisibility(View.GONE);
            relativeInicio.setVisibility(View.VISIBLE);
        }

        nombrePredio.setText(nombrePredio.getText().toString()+""+prefs.getString("namePredio",""));
        nombreFaena.setText(nombreFaena.getText().toString()+""+prefs.getString("nameFaena",""));

        this.completeInformationdataMaquinaria();
        this.completeUsersInformation();
        this.completeImplementsInformation();


        Log.e("TAG RESULT:",prefs.getAll().toString());

        // BOTON INICIO DE SESION
        buttonInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            new HandlerInforme(getApplicationContext()).showInformeDetail();

            String idInforme = prefs.getString("idInforme", "");
            String idInformeImplemento = prefs.getString("idInformeImplemento", "");
            String idInformeFaena = prefs.getString("idInformeFaena", "null");

            flagInicio = 1;
            buttonInicio.setVisibility(View.GONE);
            buttonVolver.setVisibility(View.GONE);

            //Modalidad inicio sesión JEFE
            if (modalidad.equalsIgnoreCase("1")) {

                //fa.clearShared("MisPreferencias");//Elimina los shared preferences

                //1. modificar el estado de la sesion en caso de que la modalidad sea operador y se cambie a ACTIVE
                SessionHandler sessionHandler = new SessionHandler(getApplicationContext());
                sessionHandler.ChangeStatusSession("ACTIVE");


                Intent intent = new Intent(MainActivity_detalleSesion.this,MainActivity_jefeSesiones.class);
                startActivity(intent);
                finish();

            }else{

                relativeInicioSesion.setVisibility(View.VISIBLE);

                //cambios necesarios

                //1. modificar el estado de la sesion en caso de que la modalidad sea operador y se cambie a ACTIVE
                SessionHandler sessionHandler = new SessionHandler(getApplicationContext());
                sessionHandler.ChangeStatusSession("ACTIVE");

                //2. modificamos los valores del informe a realizar, obteniendo la data de las shared preference y updateando el dispositivo
                String idImplemento = prefs.getString("tagImplemento","0");

                String isImplementActive = "";

                if (idImplemento.equalsIgnoreCase("-")){
                    isImplementActive="NO";
                }else{
                    isImplementActive="TES";
                }

                String statusSend = "NOT_YET";

                //hacemos la instancia a la adicion de informacion al informe generado previamente
                //new HandlerInforme(getApplicationContext()).changeValuesHorometro(horometroInicio,horometroFinal,  idInforme, idImplemento, horarioInicio, horarioFinal, idFaena, statusSend, isImplementActive);
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

        // BOTON VOLVER A LA LISTA SESIONES
        buttonVolverListaSesiones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            //limipia los datos de faena obtenidos anteriormente
            editor.remove("nameFaena");
            editor.remove("idFaena");
            editor.commit();

            Intent intent = new Intent(MainActivity_detalleSesion.this,MainActivity_jefeSesiones.class);
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
        final RelativeLayout relativeMensajeSesiones = findViewById(R.id.relativeMensajeSesiones);

        // Check which request we're responding to
        if (requestCode == HOROMETRO_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                String data2 = data.getStringExtra("horometro");

                relativeInicioSesion.setVisibility(View.GONE);
                relativeMensajeSesiones.setVisibility(View.GONE);
                relativeCierreSesion.setVisibility(View.VISIBLE);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        String currentDateandTime = sdf.format(new Date());
                        editor.putString("fin_implemento", currentDateandTime);

                        if (modalidad.equalsIgnoreCase("1")){
                            String tokenSession = prefs.getString("tokenSession", "null");
                            if (new SessionHandler(getApplicationContext()).closeSession(tokenSession)) {
                                if (new SessionHandler(getApplicationContext()).getSessionActive().size()>0){
                                    Log.e("TAG OK","se cierra la sesión... CON SESIONES ACTIVAS");
                                    clearShared("MisPreferencias");//Elimina los shared preferences
                                    Intent intent = new Intent(MainActivity_detalleSesion.this,MainActivity_jefeSesiones.class);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Log.e("TAG OK","se cierra la sesión... SIN SESIONES ACTIVAS");
                                    clearShared("MisPreferencias");//Elimina los shared preferences
                                    Intent intent = new Intent(MainActivity_detalleSesion.this,MainActivity_jefe.class);
                                    startActivity(intent);
                                    finish();
                                }

                            }else{
                                Log.e("TAG-ERROR", "NO SE QUE MIERDA PASO :(");
                            }


                        }
                        if (modalidad.equalsIgnoreCase("2")){
                            String tokenSession = prefs.getString("tokenSession", "null");
                            if (new SessionHandler(getApplicationContext()).closeSession(tokenSession)) {
                                clearShared("MisPreferencias");//Elimina los shared preferences
                                Intent intent = new Intent(MainActivity_detalleSesion.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Log.e("TAG-ERROR", "NO SE QUE MIERDA PASO :(");
                            }
                        }

                    }
                }, 2000);
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

        //SPLIT TO ARRAY THE VALUES OF TAG
        String[] arrayResponse = response.split(":");
        String tagImplemento = prefs.getString("tagImplemento","null");
        String nombreMaquina = prefs.getString("tagMaquinaria","null");
        String nombreUsuario = prefs.getString("idUsuario","null");
        String tokenSession = prefs.getString("tokenSession", "null");

        if (flagInicio == 1) {//boton ya se encuentra pulsado


            if (arrayResponse[1].equalsIgnoreCase("4")){//corresponde a implemento

                if (tagImplemento.equalsIgnoreCase("null")){//el loco trabaja sin implemento
                    Intent intent2 = new Intent(MainActivity_detalleSesion.this,MainActivity_implemento.class);
                    startActivity(intent2);
                    finish();
                }else{//el loco esta trabajando con implemento
                    if (tagImplemento.equalsIgnoreCase(arrayResponse[0])){

                        String [] tagRead = response.split(":");
                        String newTag = tagRead[0]+":"+tagRead[1]+":"+tagRead[2]+":0:-";
                        myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                        Log.e("TAG-ERROR", "ESTE ES MI IMPLEMENTO");

                        nfcAdapter = NfcAdapter.getDefaultAdapter(MainActivity_detalleSesion.this);

                        pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(MainActivity_detalleSesion.this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
                        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
                        writeTagFilters = new IntentFilter[]{tagDetected};

                        nfcHandler = new NFCHandler(MainActivity_detalleSesion.this, context, nfcAdapter);

                        Log.e("TAG-FAENA", newTag+" TAG a escribir");

                        int responseWrite = nfcHandler.writeNFC(newTag, myTag, pendingIntent, writeTagFilters); //escribimos que ya se encuentra vacia
                        Intent intent2 = new Intent(MainActivity_detalleSesion.this,MainActivity_implemento.class);
                        startActivity(intent2);
                        finish();
                    }else{
                        Log.e("TAG-ERROR", "ESTE NO ES MI IMPLEMENTO");
                        alertWriteNFC("El TAG no corresponde a un implemento. Favor acercar el dispositivo a un implemento");
                    }
                }

            }else{
                if (arrayResponse[1].equalsIgnoreCase("3")){//corresponde a una maquinaria
                    if (arrayResponse[0].equalsIgnoreCase(nombreMaquina)) {// la misma maquinaria

                        if (arrayResponse[2].equalsIgnoreCase(tokenSession)) {//si esta ocupada la maquina por mi, revisando el token

                            //new SessionHandler(getApplicationContext()).closeSession(tokenSession);
                            Log.e("TAG-ERROR", "SESSION NORMAL CLOSED");
                            Intent intent2 = new Intent(MainActivity_detalleSesion.this, MainActivity_horometro.class);
                            intent2.putExtra("flagHorometro", "2");
                            startActivityForResult(intent2, HOROMETRO_REQUEST);

                        }else{//UN WN ME CAGO, CIERRE DE SESIÓN EXPIRADA
                            Log.e("TAG-EXPIRED", "UN WN ME QUITO LA MAQUINA, CIERRE SESION EXPIRADA");

                            //modificar el informe con el ID actual
                            String idInformeV = prefs.getString("idInforme", "null");
                            new HandlerInforme(getApplicationContext()).closeInformeMaquinaria(idInformeV,"--",  "CLOSE_EXPIRED", arrayResponse[2]);

                            tokenSession = prefs.getString("tokenSession", "null");
                            if (new SessionHandler(getApplicationContext()).closeSession(tokenSession)) {
                                Log.e("TAG:EXPIRED", "Se le expiró toooodaaaaa");
                            }else{
                                Log.e("TAG:EXPIRED", "No se que carajos paso aqui :(");

                            }

                            //Manipulamos la visualizacion de las vistas... REVISAR!!!!
                            relativeInicioSesion.setVisibility(View.GONE);
                            relativeExpirada.setVisibility(View.VISIBLE);
                        }
                    }else{
                        Log.e("TAG-ERROR", "NO ES MI MAQUINA");
                        alertWriteNFC("Esta maquinaria no corresponde a la seleccionada...");
                    }
                }else{//corresponde a cualquier otra wea
                    Log.e("TAG-ERROR", "ES CUALQUIER OTRA WEA QUE NO SEA MAQUINA NI IMPLEMENTO");
                    alertWriteNFC("El TAG invalido. Favor acerque el dispositivo a una maquinaria o implemento.");
                }
            }
        }else if (flagInicio==0){ // AUN NO HA INICIADO SESION
            Log.e("TAG-ERROR", "NO HA APRETADO BOTON DE INICIO SESIÓN");
            alertWriteNFC("Debe iniciar sesión primero para poder realizar esta operación.");
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

    /**
     * Metodo que permite completar la informacion de la mquinaria
     */
    public void completeInformationdataMaquinaria(){

        ArrayList<Maquinaria> maquinarias = new HandlerDBPersistence(getApplicationContext()).getMaquinariaList();

        String codeMaquinaria = prefs.getString("tagMaquinaria", "null");

        for(int i=0; i<maquinarias.size(); i++){
            if (maquinarias.get(i).getCodeInternoMachine().equalsIgnoreCase(codeMaquinaria)){
                TextView textMaquinariaDescripcion = findViewById(R.id.textMaquinariaDescripcion);
                textMaquinariaDescripcion.setText("Descripción: "+maquinarias.get(i).getNameMachine());

                TextView textMaquinariaModelo = findViewById(R.id.textMaquinariaModelo);
                textMaquinariaModelo.setText("Modelo: "+maquinarias.get(i).getModelMachine());

                TextView textMaquinariaCapacidad = findViewById(R.id.textMaquinariaCapacidad);
                textMaquinariaCapacidad.setText("Capacidad: "+maquinarias.get(i).getPatentMachine());
                break;
            }
        }

    }

    /**
     * Metodo que permite completar la informacion del usuario
     */
    public void completeUsersInformation(){

        ArrayList<UserSession> userSessions = new HandlerDBPersistence(getApplicationContext()).getUser();

        for (int i =0; i< userSessions.size(); i++){
            if (userSessions.get(i).getIDUser().equalsIgnoreCase(prefs.getString("idUsuario", ""))){

                TextView textNombreUsuario = findViewById(R.id.textNombreUsuario);
                textNombreUsuario.setText("Bienvenido: "+userSessions.get(i).getNameUser());

                TextView textRUT = findViewById(R.id.textRUT);
                textRUT.setText("RUT: "+userSessions.get(i).getRutUser());
            }
        }
    }

    public void completeImplementsInformation(){

        ArrayList<Implemento> implementos = new HandlerDBPersistence(getApplicationContext()).getImplementos();

        String codeImplemento = prefs.getString("tagImplemento", "0");

        if (codeImplemento.equalsIgnoreCase("0")){//tag nulo
            TextView textImplementoDescripcion = findViewById(R.id.textImplementoDescripcion);
            textImplementoDescripcion.setText("Descripción: Sin Implemento");

            TextView textImplementoTipo = findViewById(R.id.textImplementoTipo);
            textImplementoTipo.setText("Tipo: --");

            TextView textImplementoCapacidad = findViewById(R.id.textImplementoCapacidad);
            textImplementoCapacidad.setText("Capacidad: --");

        }else {
            for (int i = 0; i < implementos.size(); i++) {
                if (implementos.get(i).getCodeInternoImplemento().equalsIgnoreCase(codeImplemento)){

                    TextView textImplementoDescripcion = findViewById(R.id.textImplementoDescripcion);
                    textImplementoDescripcion.setText("Descripción: "+implementos.get(i).getNameImplement());

                    TextView textImplementoTipo = findViewById(R.id.textImplementoTipo);
                    textImplementoTipo.setText("Tipo: "+implementos.get(i).getFabricante());

                    TextView textImplementoCapacidad = findViewById(R.id.textImplementoCapacidad);
                    textImplementoCapacidad.setText("Capacidad: "+implementos.get(i).getCapacidad());
                    break;
                }
            }
        }

    }

    /**
     * Metodo que permite limpiar el Shared Preferences de la aplicación
     * @return
     */
    public void clearShared(String namePreferences){
        SharedPreferences prefs = getSharedPreferences(namePreferences,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.remove("idUsuario");
        editor.remove("idPredio");
        editor.remove("namePredio");
        editor.remove("tagMaquinaria");
        editor.remove("nameMaquinaria");
        editor.remove("tagImplemento");
        editor.remove("idFaena");
        editor.remove("nameFaena");
        editor.remove("inicio_horometro");
        editor.remove("fin_horometro");
        editor.remove("inicio_implemento");
        editor.remove("fin_implemento");
        editor.remove("flagImplemento");
        editor.remove("comentarios");
        editor.remove("idMaquina_comentario");
        editor.remove("idImplemento_comentario");
        editor.commit();

    }
}
