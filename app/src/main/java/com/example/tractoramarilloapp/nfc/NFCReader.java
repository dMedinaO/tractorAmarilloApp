package com.example.tractoramarilloapp.nfc;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.util.Log;

import java.io.UnsupportedEncodingException;

/**
 * clase que permite el desarrollo del lector de informacion desde el NFC,
 * tiene los metodos asociados a la deteccion del codigo, asi como tambien la decodificacion
 * de este en base a la data que recibe desde los tag en el formato estandar
 */
public class NFCReader {

    //atributos de la clase
    private Intent intent;
    private String NFCContent;

    public NFCReader(Intent intent){

        this.intent = intent;
        this.NFCContent = "NULL";//indicando que es un string void
    }

    /**
     * Metodo que permite hacer la lectura del NFC por medio de un intent el cual permite la
     * generacion de la instancia, se basa en las diferentes acciones asociadas a la lectura,
     * es decir, puede descubrir diferentes formas de TAG que implican distintas codificaciones
     */
    public void readFromIntent() {

        try {
            String action = intent.getAction();
            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                    || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                    || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
                Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                NdefMessage[] msgs = null;
                if (rawMsgs != null) {
                    msgs = new NdefMessage[rawMsgs.length];
                    for (int i = 0; i < rawMsgs.length; i++) {
                        msgs[i] = (NdefMessage) rawMsgs[i];
                    }
                    buildTagViews(msgs);
                } else {
                    this.NFCContent = "VOID";
                }

            } else {
                this.NFCContent = "UKNOWN";
            }
        }catch (Exception e){
            this.NFCContent = "ERROR";
        }
    }

    /**
     * Metodo que permite hacer la decodificacion de los TAG descubiertos por el lector NFC
     * @param msgs
     */
    private void buildTagViews(NdefMessage[] msgs) {

        if (msgs == null || msgs.length == 0) return;

        String text = "";
        //String tagId = new String(msgs[0].getRecords()[0].getType());
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16"; //Obtenemos codificacion
        int languageCodeLength = payload[0] & 0063; // Get the Language Code, e.g. "en"
        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");

        try {
            // Get the Text
            text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
            Log.e("TAG", text);
        } catch (Exception e) {
            Log.e("UnsupportedEncoding", e.toString());
            text = "ERROR";
        }
        this.NFCContent = text;
    }

    //GETTER
    public String getNFCContent() {
        return NFCContent;
    }
}