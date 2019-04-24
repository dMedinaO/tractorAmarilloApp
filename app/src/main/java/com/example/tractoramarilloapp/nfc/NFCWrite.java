package com.example.tractoramarilloapp.nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Clase que tiene la responsabilidad de hacer la escritura en el NFC, presenta distintos
 * modulos con respecto a la escritura asociada, la cual corresponde a:
 * 1. Borrar y escribir, recibe un unico string y genera la escritura correspondiente al texto enviado
 * 2. Escribir al final, recibe dos string que requiere concatenar al final el nuevo elemento
 * 3. Escribir al inicio, recibe dos string que reuquiere concatenar al inicio el nuevo
 *
 * Ademas posee los metodos asociados a habilitar y deshabilitar la escritura de datos
 */
public class NFCWrite {

    //atributos de la clase
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter writeTagFilters[];
    private boolean writeMode;
    private Activity activity;

    //constructor de la clase
    public NFCWrite(NfcAdapter nfcAdapter, PendingIntent pendingIntent, IntentFilter writeTagFilters[], Activity activity){

        this.nfcAdapter = nfcAdapter;
        this.pendingIntent = pendingIntent;
        this.writeTagFilters = writeTagFilters;
        this.activity = activity;
        this.writeMode = false;//instanciamos en modo falso, lo que implica que no se encuentra habilitado para generar la escritura
    }

    /**
     * metodo que permite habilitar el modo de escritura del NFC
     */
    public void WriteModeOn(){
        writeMode = true;
        nfcAdapter.enableForegroundDispatch(activity, pendingIntent, writeTagFilters, null);
    }

    /**
     * Metodo que permite deshabilitar el modo de escritura del NFC
     */
    public void WriteModeOff(){
        writeMode = false;
        nfcAdapter.disableForegroundDispatch(activity);
    }

    /**
     * Metodo que permite hacer la escritura de elementos en el NFC, recibe el texto y el tag a quien le grabara la informacion
     * @param text
     * @param tag
     * @throws IOException
     * @throws FormatException
     */
    public void WriteNFC(String text, Tag tag) throws IOException, FormatException {
        NdefRecord[] records = { createRecord(text) };
        NdefMessage message = new NdefMessage(records);
        // Get an instance of Ndef for the tag.
        Ndef ndef = Ndef.get(tag);
        // Enable I/O
        ndef.connect();
        // Write the message
        ndef.writeNdefMessage(message);
        // Close the connection
        ndef.close();
    }

    /**
     * Metodo que permite crear los record para ser escritos en el tag NFC
     * @param text
     * @return
     * @throws UnsupportedEncodingException
     */
    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        String lang       = "en";
        byte[] textBytes  = text.getBytes();
        byte[] langBytes  = lang.getBytes("US-ASCII");
        int    langLength = langBytes.length;
        int    textLength = textBytes.length;
        byte[] payload    = new byte[1 + langLength + textLength];

        // set status byte (see NDEF spec for actual bits)
        payload[0] = (byte) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1,              langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,  NdefRecord.RTD_TEXT,  new byte[0], payload);

        return recordNFC;
    }

    public boolean isWriteMode() {
        return writeMode;
    }
}
