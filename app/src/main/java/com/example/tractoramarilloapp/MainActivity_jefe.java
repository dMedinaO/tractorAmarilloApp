package com.example.tractoramarilloapp;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
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
import com.example.tractoramarilloapp.model.UserSession;
import com.example.tractoramarilloapp.nfc.NFCHandler;
import com.example.tractoramarilloapp.utils.ConnectivityApplication;
import com.example.tractoramarilloapp.utils.ConnectivityReceiver;

import org.w3c.dom.Text;

import java.util.Date;

import static com.example.tractoramarilloapp.InternetStatus.isOnline;

public class MainActivity_jefe extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private TextView nombreJefe, jefeRUT;
    private TextView unidadLocal;
    private ImageView imageComentario, imageSync, imageSignal;

    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    private static ConnectivityManager manager;
    private SessionHandler sessionHandler;
    private String idJefe;

    private RelativeLayout relativeInicioJefe;
    private RelativeLayout relativeFinJefe;

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
        setContentView(R.layout.activity_main_jefe);

        // ACTION BAR INIT
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions( ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_jefe);

        this.sessionHandler = new SessionHandler(getApplicationContext());

        View customActionBarView = actionBar.getCustomView();

        //SHARED PREFERENCES
        prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        editor = prefs.edit();
        this.idJefe = prefs.getString("idUsuarioBoss", "null");

        //VARIABLES INIT
        nombreJefe = (TextView) findViewById(R.id.textUsuarioJefe);
        jefeRUT = (TextView) findViewById(R.id.textRUT);
        imageSignal = (ImageView) findViewById(R.id.imageSignal);
        imageSync = (ImageView) findViewById(R.id.imageSync);
        unidadLocal = (TextView) findViewById(R.id.textUnidadLocal);

        relativeInicioJefe = (RelativeLayout) findViewById(R.id.relativeInicioJefe);
        relativeFinJefe = (RelativeLayout) findViewById(R.id.relativeFinJefe);

        // Chequea constantemente si hay internet o no
        checkConnection();

        //nombreJefe.setText(nombreJefe.getText().toString()+""+prefs.getString("usuario_jefe",""));
        //jefeRUT.setText(jefeRUT.getText().toString()+""+prefs.getString("usuario_jefe_rut",""));

        this.getValuesUser();

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

        imageSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(isOnline(getApplicationContext())){
                alertSync("Sincronización exitosa.");
            }else{
                alertSync("Error de sincronización, intente nuevamente.");
            }
            }
        });

        //Actualiza la cantidad de unidades locales
        syncUnityLocal();


    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        if (isConnected) {
            //Toast.makeText(MainActivity_jefey.this,"HAY INTERNET",Toast.LENGTH_SHORT).show();
            imageSignal.setImageResource(R.mipmap.signal);
        } else {
            //Toast.makeText(MainActivity.this, "NO HAY INTERNET", Toast.LENGTH_SHORT).show();
            imageSignal.setImageResource(R.mipmap.signal_off);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        String response = this.nfcHandler.readerTAGNFC(intent);

        Log.e("TAG READ:", "response: "+response);

        if (!response.equalsIgnoreCase("VOID")){

            String[] arrayResponse = response.split(":");
            String tagUsuarioBoss = prefs.getString("idUsuarioBoss","null");
            String tokenUsuario = prefs.getString("tokenSessionB","null");

            int responseSession = this.sessionHandler.createSession(response);

            Log.e("TAG-RESPONSE-SESSION", "RETURN HANDLER SESSION "+ responseSession);
            Log.e("TAG-RESPONSE-SESSION", "RETURN HANDLER SESSION "+ this.sessionHandler.getTokenSession());
            Log.e("TAG 1","Pulsera: "+arrayResponse[0]);

            if (arrayResponse[1].equalsIgnoreCase("1")){
                if (tagUsuarioBoss.equalsIgnoreCase(arrayResponse[2])){
                    Log.e("TAG TAG","TAG es un jefe de taller");
                    if (this.sessionHandler.closeSessionBoss(tokenUsuario)) {

                        relativeInicioJefe.setVisibility(View.GONE);
                        relativeFinJefe.setVisibility(View.VISIBLE);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {

                                Log.e("HANDLER", "OK");
                                editor.remove("idUsuarioBoss");
                                editor.remove("modalidad");
                                editor.commit();
                                Intent intent2 = new Intent(MainActivity_jefe.this, MainActivity.class);
                                startActivity(intent2);
                                finish();
                            }
                        }, 2000);
                    }else{
                        alertErrorLogin("No puedes cerrar sesión, verifica que no existan operarios con sesión Activa");
                    }
                }else{
                    Log.e("TAG TAG","TAG es un jefe de taller pero no el mismo");
                    alertErrorLogin("Ya existe una sesión activa en este dispositivo...");
                }

            }
            if (arrayResponse[1].equalsIgnoreCase("2")) {

                Log.e("TAG TAG","TAG es un operador");
                editor.putString("idUsuario",arrayResponse[2]);
                Log.e("TAG-RESPONSE-SESSION","Token activo " + this.sessionHandler.getTokenSession());
                editor.putString("tokenSession", this.sessionHandler.getTokenSession());//agregamos el token de la sesion del usuario
                editor.commit();

                Intent intent2 = new Intent(MainActivity_jefe.this,MainActivity_predio.class);
                startActivity(intent2);
                finish();
            }
            if (arrayResponse[1].equalsIgnoreCase("3")){
                Log.e("TAG TAG","TAG es una maquinaria");
                editor.putString("idMaquina_comentario",arrayResponse[0]);
                editor.commit();
                Intent intent2 = new Intent(MainActivity_jefe.this,MainActivity_jefeComentarios.class);
                intent2.putExtra("comentario_mode","1"); //modo maquinaria
                startActivity(intent2);
                finish();

            }
            if (arrayResponse[1].equalsIgnoreCase("4")){
                Log.e("TAG TAG","TAG es un implemento");
                editor.putString("idImplemento_comentario",arrayResponse[0]);
                editor.commit();
                Intent intent2 = new Intent(MainActivity_jefe.this,MainActivity_jefeComentarios.class);
                intent2.putExtra("comentario_mode","2"); //modo maquinaria
                startActivity(intent2);
                finish();
            }

        }else{
            Log.e("TAG ERROR:","response VOID: "+response);
            alertErrorLogin("Error al leer el TAG. Favor acerque nuevamente el dispositivo al TAG.");
        }



        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
    }

    public void syncUnityLocal(){
        int unidadlocalcount = new HandlerInforme(getApplicationContext()).getUnidadesLocalesNumber();
        unidadLocal.setText("U. Local "+unidadlocalcount);
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

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
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

    public void getValuesUser(){

        UserSession userSession = new SessionHandler(getApplicationContext()).getInformationBoss(this.idJefe);
        this.nombreJefe.setText("Bienvenido " + userSession.getNameUser());
        this.jefeRUT.setText("RUT: "+userSession.getRutUser());

    }
}
