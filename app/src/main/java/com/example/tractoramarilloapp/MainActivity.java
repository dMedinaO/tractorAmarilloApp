package com.example.tractoramarilloapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tractoramarilloapp.nfc.NFCHandler;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences.Editor editor;
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
        setContentView(R.layout.activity_main);
        context = this;

        SharedPreferences prefs =
                getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);

        editor = prefs.edit();

        String usuario = "Ricardo Etcheverry";
        String usuarioRut = "24.858.868-3";

        //tvNFCContent = findViewById(R.id.nfc_contents);
       // message = findViewById(R.id.edit_message);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };
        this.nfcHandler = new NFCHandler(this, context, nfcAdapter);

        /*btnWrite = findViewById(R.id.button);
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Click", "Try Write");
                nfcHandler.writeNFC(message.getText().toString(), myTag, pendingIntent, writeTagFilters);
            }
        });*/

        //instanciamos al handler de
        String text = this.nfcHandler.readerTAGNFC(getIntent());
        //.setText("NFC Content: " + text);
        //Toast.makeText(MainActivity.this,"HOLA "+text,Toast.LENGTH_SHORT).show();



        msjMotivacional = (TextView) findViewById(R.id.textMotivacional);

        msjMotivacional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this,"Hola mensaje",Toast.LENGTH_LONG).show();

                Intent intent = new Intent(MainActivity.this,MainActivity_predio.class);
                //intent.putExtra("usuario","Ricardo Etcheverry");
                //intent.putExtra("usuario_rut","24.858.868-3");
                startActivity(intent);


            }
        });



    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        String response = this.nfcHandler.readerTAGNFC(intent);
        //tvNFCContent.setText("NFC Content: " + response);
        //Toast.makeText(MainActivity.this,"USUARIO: "+response,Toast.LENGTH_SHORT).show();

        editor.putString("usuario",response);
        editor.putString("usuario_rut","24858868-3");
        editor.commit();

        Log.e("TAG 1","Pulsera: "+response);

        Intent intent2 = new Intent(MainActivity.this,MainActivity_predio.class);
        startActivity(intent2);
        finish();
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
