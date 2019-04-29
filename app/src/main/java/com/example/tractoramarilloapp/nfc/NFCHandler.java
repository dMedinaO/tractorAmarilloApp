package com.example.tractoramarilloapp.nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.util.Log;


/**
 * Clase con la responsabilidad de evaluar las acciones asociadas al NFC, presenta los modulos de escritura y lectura de datos
 * los cuales se asocian a la informacion obtenida desde el dispositivo.
 */
public class NFCHandler {

    //atributos de la clase
    private Activity activity;
    private Context context;
    private NfcAdapter nfcAdapter;
    private NFCWrite nfcWrite;
    private NFCReader nfcReader;

    private int statusCode;//hace referencia a la data existente

    public NFCHandler(Activity activity, Context context, NfcAdapter nfcAdapter){

        this.activity = activity;
        this.context = context;
        this.nfcAdapter = nfcAdapter;

    }

    /**
     * Metodo que permite relizar la lectura del codigo desde el NFC
     * @return
     */
    public String readerTAGNFC(Intent intent){

        //instancia al lector
        this.nfcReader = new NFCReader(intent);
        nfcReader.readFromIntent();

        return nfcReader.getNFCContent();
    }

    /**
     * Metodo que permite hacer la escritura de datos en el NFC
     * @return
     */
    public int writeNFC(String dataWrite, Tag myTag,  PendingIntent pendingIntent, IntentFilter writeTagFilters[]){

        int response;
        this.nfcWrite = new NFCWrite(nfcAdapter, pendingIntent, writeTagFilters, this.activity);

        //preguntamos el modo
        //if (nfcWrite.isWriteMode() == false){

        //    nfcWrite.WriteModeOn();
        //}

        try {
            if (myTag == null) {
                Log.e("WRITE-NFC", "ERROR TAG NULL");
                //Toast.makeText(context, ERROR_DETECTED, Toast.LENGTH_LONG).show();
                response = -1;//ERROR
            } else {
                Log.e("WRITE-NFC", "TRY WRITE HANDLER");
                nfcWrite.WriteNFC(dataWrite, myTag);
                response = 0;//OK
            }
        } catch (Exception e) {
            Log.e("WRITE-NFC", "ERROR HANDLER");
            e.printStackTrace();
            response = -1;//ERROR
        }

        //nfcWrite.WriteModeOff();//cambiamos los estados a mode of

        return  response;
    }

    public void changeModeWrite(int option, PendingIntent pendingIntent, IntentFilter writeTagFilters[]){

        this.nfcWrite = new NFCWrite(nfcAdapter, pendingIntent, writeTagFilters, this.activity);
        if (option == 1){//ON
            this.nfcWrite.WriteModeOn();
        }else{//OFF
            this.nfcWrite.WriteModeOff();
        }
    }
}
