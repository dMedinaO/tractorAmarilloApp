package com.example.tractoramarilloapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.tractoramarilloapp.handlers.HandlerInforme;
import com.example.tractoramarilloapp.handlers.SessionHandler;
import com.example.tractoramarilloapp.nfc.NFCHandler;
import com.example.tractoramarilloapp.utils.ConnectivityApplication;
import com.example.tractoramarilloapp.utils.ConnectivityReceiver;
import com.example.tractoramarilloapp.utils.FA;

import static com.example.tractoramarilloapp.InternetStatus.isOnline;

public class MainActivity_horometro extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private ImageView imageComentario;
    private ImageView imageSync,imageSignal;
    private TextView tituloHorometro,textComentario;
    private TextView unidadLocal;
    private EditText inputHorometro;
    private Button buttonHorometro;

    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    private FA fa;

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
        setContentView(R.layout.activity_horometro);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions( ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar);

        View customActionBarView = actionBar.getCustomView();

        inputHorometro = (EditText) findViewById(R.id.inputHorometro);
        buttonHorometro = (Button) findViewById(R.id.buttonHorometro);
        tituloHorometro = (TextView) findViewById(R.id.textHorometro);
        imageSignal = (ImageView) findViewById(R.id.imageSignal);
        imageSync = (ImageView) findViewById(R.id.imageSync);

        // botones action bar
        imageComentario = (ImageView) findViewById(R.id.imageComentario);
        textComentario = (TextView) findViewById(R.id.textComentarioLink);
        unidadLocal = (TextView) findViewById(R.id.textUnidadLocal);

        imageComentario.setColorFilter(Color.rgb(206, 206, 206));
        textComentario.setTextColor(Color.rgb(206, 206, 206));

        // Chequea constantemente si hay internet o no
        checkConnection();

        // SHARED PREFERENCES
        prefs = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
        editor = prefs.edit();
        final String modalidad = prefs.getString("modalidad","null");

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

        final Bundle bundle = getIntent().getExtras();

        if (bundle != null){

            Log.e("TAG HOROMETRO","valor: "+bundle.getString("flagHorometro"));

            if (bundle.getString("flagHorometro").equalsIgnoreCase("1")){
                tituloHorometro.setText(R.string.horometro_inicio);
            }else if(bundle.getString("flagHorometro").equalsIgnoreCase("2") || bundle.getString("flagHorometro").equalsIgnoreCase("3")){
                tituloHorometro.setText(R.string.horometro_fin);
            }

        }


        buttonHorometro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("TAG MODALIDAD:","modalidad: "+modalidad+"  horometro: "+inputHorometro.getText().toString());

                if (inputHorometro.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(MainActivity_horometro.this,"Debe ingresar horómetro para continuar.", Toast.LENGTH_LONG).show();
                }else{
                    // INICIO DE HOROMETRO
                    if (bundle.getString("flagHorometro").equalsIgnoreCase("1")){
                        editor.putString("inicio_horometro",inputHorometro.getText().toString());
                        editor.commit();

                        //modificar el informe con el ID actual
                        String idInforme = prefs.getString("idInforme", "null");
                        new HandlerInforme(getApplicationContext()).changeValuesHorometro(inputHorometro.getText().toString(), "-", idInforme);//se cambia el horario al inform
                        Intent intent = new Intent(MainActivity_horometro.this,MainActivity_implemento.class);
                        startActivity(intent);
                        finish();

                    }
                    // FIN DE HOROMETRO
                    if (bundle.getString("flagHorometro").equalsIgnoreCase("2")){
                        editor.putString("fin_horometro",inputHorometro.getText().toString());

                        //modificar el informe con el ID actual
                        String idInforme = prefs.getString("idInforme", "null");

                        new HandlerInforme(getApplicationContext()).closeInformeMaquinaria(idInforme,inputHorometro.getText().toString(),  "CLOSE_NORMAL", prefs.getString("tokenSession", "null"));

                        Intent output = new Intent();
                        setResult(RESULT_OK, output);
                        finish();
                    }
                    // FIN DE HOROMETRO FORZADO
                    if (bundle.getString("flagHorometro").equalsIgnoreCase("3")){

                        //cerramos el informe de maquinaria
                        String idInforme = prefs.getString("idInforme", "null");
                        new HandlerInforme(getApplicationContext()).closeInformeMaquinaria(idInforme,inputHorometro.getText().toString(),  "CLOSE_FORZADO", prefs.getString("tokenSession", "null"));
                        editor.putString("fin_horometro",inputHorometro.getText().toString());
                        editor.commit();

                        if (modalidad.equalsIgnoreCase("1")) {
                            String tokenSession = prefs.getString("tokenSession", "null");
                            if (new SessionHandler(getApplicationContext()).closeSession(tokenSession)) {

                                if (new SessionHandler(getApplicationContext()).getSessionActive().size()>0){
                                    Log.e("TAG OK","se cierra la sesión... CON SESIONES ACTIVAS");
                                    editor.remove("idUsuario");
                                    editor.remove("tagMaquinaria");
                                    editor.remove("nameMaquinaria");
                                    editor.remove("tokenSession");
                                    editor.commit();
                                    Intent intent = new Intent(MainActivity_horometro.this,MainActivity_jefeSesiones.class);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Log.e("TAG OK","se cierra la sesión... SIN SESIONES ACTIVAS");
                                    editor.remove("idUsuario");
                                    editor.remove("tagMaquinaria");
                                    editor.remove("nameMaquinaria");
                                    editor.remove("tokenSession");
                                    editor.commit();
                                    Intent intent = new Intent(MainActivity_horometro.this,MainActivity_jefe.class);
                                    startActivity(intent);
                                    finish();
                                }

                            }else{
                                Log.e("TAG:ERROR", "No se que carajos paso aqui :(");

                            }

                        }
                        if (modalidad.equalsIgnoreCase("2")){

                            //modificar el informe con el ID actual
                            String idInformeV = prefs.getString("idInforme", "null");

                            new HandlerInforme(getApplicationContext()).closeInformeMaquinaria(idInformeV,inputHorometro.getText().toString(),  "CLOSE_NORMAL", prefs.getString("tokenSession", "null"));

                            String tokenSession = prefs.getString("tokenSession", "null");
                            if (new SessionHandler(getApplicationContext()).closeSession(tokenSession)) {
                                editor.remove("idUsuario");
                                editor.remove("tagMaquinaria");
                                editor.remove("nameMaquinaria");
                                editor.remove("tokenSession");
                                editor.commit();
                                Intent intent = new Intent(MainActivity_horometro.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Log.e("TAG:ERROR", "No se que carajos paso aqui :(");

                            }
                        }

                    }

                }

            }
        });

        //Actualiza la cantidad de unidades locales
        syncUnityLocal();

    }

    public void syncUnityLocal(){
        int unidadlocalcount = new HandlerInforme(getApplicationContext()).getUnidadesLocalesNumber();
        unidadLocal.setText("U. Local "+unidadlocalcount);
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
    public void onBackPressed() { }

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
