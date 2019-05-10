package com.example.tractoramarilloapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tractoramarilloapp.handlers.HandlerInforme;
import com.example.tractoramarilloapp.model.Comentarios;
import com.example.tractoramarilloapp.nfc.NFCHandler;
import com.example.tractoramarilloapp.persistence.HandlerDBPersistence;
import com.example.tractoramarilloapp.utils.ConnectivityApplication;
import com.example.tractoramarilloapp.utils.ConnectivityReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity_comentario extends AppCompatActivity  implements ConnectivityReceiver.ConnectivityReceiverListener {

    private Button acceptButton,cancelButton;
    private EditText comentarioField;
    private ImageView imageSignal;
    private TextView unidadLocal;

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
        setContentView(R.layout.activity_comentario);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions( ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_jefe);

        View customActionBarView = actionBar.getCustomView();

        acceptButton = (Button) findViewById(R.id.buttonAccept);
        cancelButton = (Button) findViewById(R.id.buttonCancel);
        comentarioField = (EditText) findViewById(R.id.editTextComentarios);
        imageSignal = (ImageView) findViewById(R.id.imageSignal);
        unidadLocal = (TextView) findViewById(R.id.textUnidadLocal);

        // Chequea constantemente si hay internet o no
        checkConnection();

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

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            //obtenemos la informacion para almacenar el comentario en la BD
            SharedPreferences prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
            String idUser = prefs.getString("idUsuario", "null");

            HandlerDBPersistence handlerDBPersistence = new HandlerDBPersistence(getApplicationContext());

            String sqlQueryInforme = "SELECT * FROM comentario";
            int lastID = handlerDBPersistence.getLastID(sqlQueryInforme, "idComentario");

            if (lastID == -1){
                lastID = 1;
            }else{
                lastID++;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateandTime = sdf.format(new Date());
            handlerDBPersistence.saveComentario(new Comentarios(lastID, idUser,  currentDateandTime, comentarioField.getText().toString()));
            Intent output = new Intent();
            output.putExtra("comentario", comentarioField.getText().toString());
            setResult(RESULT_OK, output);
            finish();
            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent output = new Intent();
                setResult(RESULT_CANCELED, output);
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
    public void onBackPressed() {

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
