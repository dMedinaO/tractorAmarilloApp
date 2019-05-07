package com.example.tractoramarilloapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBar;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.LoginFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tractoramarilloapp.handlers.HandlerInforme;
import com.example.tractoramarilloapp.model.Maquinaria;
import com.example.tractoramarilloapp.model.UserSession;
import com.example.tractoramarilloapp.nfc.NFCHandler;
import com.example.tractoramarilloapp.persistence.HandlerDBPersistence;
import com.example.tractoramarilloapp.handlers.SessionHandler;

import com.example.tractoramarilloapp.handlers.SessionHandler;
import com.example.tractoramarilloapp.utils.FA;
import static com.example.tractoramarilloapp.InternetStatus.isOnline;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static ConnectivityManager manager;

    private ImageView imageComentario,imageSync,imageSignal;
    private SharedPreferences.Editor editor;
    private TextView msjMotivacional,textComentario,textDateTime;

    private SimpleDateFormat sdf,sdf_sync_date,sdf_sync_time;

    NFCHandler nfcHandler;
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    Tag myTag;
    Context context;
    TextView tvNFCContent;
    TextView message;
    Button btnWrite;
    private SessionHandler sessionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        new ValuesTempDB().addElements(this);

        new HandlerInforme(getApplicationContext()).showInformeDetail();
        int comment = new HandlerDBPersistence(getApplicationContext()).consultarRegistros("SELECT * FROM comentario").getCount();
        Log.e("TAG-COMENTARIOS", comment+" values");

        HandlerDBPersistence handlerDBPersistence = new HandlerDBPersistence(this);
        ArrayList<Maquinaria> listMaquinaria = handlerDBPersistence.getMaquinariaList();
        for (int i=0; i<listMaquinaria.size(); i++){
            Log.e("MAQUINARIA", listMaquinaria.get(i).getCodeInternoMachine());
        }

        this.context = this;
        this.sessionHandler = new SessionHandler(this.context);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // VARIABLES INIT
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions( ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar);

        View customActionBarView = actionBar.getCustomView();

        imageComentario = (ImageView) findViewById(R.id.imageComentario);
        imageSignal = (ImageView) findViewById(R.id.imageSignal);
        imageSync = (ImageView) findViewById(R.id.imageSync);
        textComentario = (TextView) findViewById(R.id.textComentarioLink);
        textDateTime = (TextView) findViewById(R.id.textDateTime);
        imageComentario.setColorFilter(Color.rgb(206, 206, 206));
        textComentario.setTextColor(Color.rgb(206, 206, 206));

        sdf_sync_date = new SimpleDateFormat("yyyy-MM-dd");
        sdf_sync_time = new SimpleDateFormat("HH:mm:ss");

        //SHARED PREFERENCES
        final SharedPreferences prefs =
                getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
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


        // CHECK INTERNET CONNECTION
        if(isOnline(getApplicationContext())){
            imageSignal.setImageResource(R.mipmap.signal);
        }else{
            imageSignal.setImageResource(R.mipmap.signal_off);
        }

        imageSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            Log.e("TAG SYNC","activada sincronización");

            String currentDate = sdf_sync_date.format(new Date());
            String currentTime = sdf_sync_time.format(new Date());

            editor.putString("last_sync",currentDate+" a las "+currentTime);
            editor.commit();
            Log.e("TAG SYNC","Data: "+prefs.getString("last_sync","0000-00-00 a las 23:59:59"));
            textDateTime.setText(prefs.getString("last_sync","0000-00-00 a las 23:59:59"));


            if(isOnline(getApplicationContext())){
                alertSync("Sincronización exitosa.");
            }else{
                alertSync("Error de sincronización, intente nuevamente.");
            }
            }
        });
        textDateTime.setText(prefs.getString("last_sync","0000-00-00 a las 23:59:59"));

    }

    /**
     * Metodo que permite mostrar los mensajes de error de sesion con respecto al tipo de error existente
     * @param responseSession
     */
    public void showMessageError(int responseSession){

        //ERROR DE PULSERA
        if (responseSession == -1){
            Log.e("TAG-RESPONSE", "TAG NOT AVAILABLE!");
        }

        //ERROR DB
        if (responseSession == -2){

            Log.e("TAG-RESPONSE", "ERROR DB TO INSERT VALUES");
        }

        //ERROR OPERARIO ACTIVO
        if (responseSession == -3){
            Log.e("TAG-RESPONSE", "WORKER IS ACTIVE, DEVICE IS NOT AVAILABLE");
        }

        //ERROR JEFE ACTIVO
        if (responseSession == -4){
            Log.e("TAG-RESPONSE", "BOSS ACTIVE");
        }
    }

    @Override
    public void onBackPressed() { }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        String response = this.nfcHandler.readerTAGNFC(intent);

        if (!response.equalsIgnoreCase("VOID")){

            //SPLIT TO ARRAY THE VALUES OF TAG
            String[] arrayResponse = response.split(":");
            int responseSession = this.sessionHandler.createSession(response);
            Log.e("SESSION_RESPONSE", responseSession+" response Session");

            // BOSS LOGIN
            if (responseSession==0) {
                editor.putString("modalidad","1");
                editor.putString("idUsuarioBoss",arrayResponse[2]);
                editor.commit();
                Intent intent2 = new Intent(MainActivity.this,MainActivity_jefe.class);
                startActivity(intent2);
                finish();
            }
            // WORKER LOGIN
            if (responseSession==1){

                editor.putString("idUsuario",arrayResponse[2]);
                editor.putString("modalidad","2");
                editor.putString("tokenSession", this.sessionHandler.getTokenSession());//agregamos el token de la sesion del usuario
                editor.commit();

                Intent intent2 = new Intent(MainActivity.this,MainActivity_predio.class);
                startActivity(intent2);
                finish();
            }
            if (responseSession== -1){

                Log.e("TAG ERROR:","response INVALID: "+response);
                alertErrorLogin("TAG invalido. Favor acerque su pulcera...");
            }



        }else{
            Log.e("TAG ERROR:","response VOID: "+response);
            alertErrorLogin("Error al leer el TAG. Favor acerque nuevamente el dispositivo al TAG.");
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

    public void alertSync(String message){
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

    public void alertErrorLogin(String message){
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
