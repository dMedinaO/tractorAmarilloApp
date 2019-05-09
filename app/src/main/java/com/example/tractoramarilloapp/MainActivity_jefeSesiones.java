package com.example.tractoramarilloapp;

import android.app.AlertDialog;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tractoramarilloapp.handlers.HandlerImplemento;
import com.example.tractoramarilloapp.handlers.HandlerInforme;
import com.example.tractoramarilloapp.handlers.InformationDetailSession;
import com.example.tractoramarilloapp.handlers.SessionHandler;
import com.example.tractoramarilloapp.model.UserSession;
import com.example.tractoramarilloapp.nfc.NFCHandler;
import com.example.tractoramarilloapp.persistence.HandlerDBPersistence;
import com.example.tractoramarilloapp.persistence.SessionClass;
import com.example.tractoramarilloapp.utils.ConnectivityApplication;
import com.example.tractoramarilloapp.utils.ConnectivityReceiver;
import com.example.tractoramarilloapp.utils.FA;
import com.example.tractoramarilloapp.utils.RecyclerAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity_jefeSesiones extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private List<String> names;
    private RecyclerView myRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImageView imageSignal;

    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    private SessionHandler sessionHandler;
    private InformationDetailSession informationDetailSession;

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

    private TextView nombreJefe, jefeRUT;
    private String idJefe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_jefe_sesiones);
        new HandlerInforme(getApplicationContext()).showInformeDetail();

        SharedPreferences prefs =
                getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        this.idJefe = prefs.getString("idUsuarioBoss", "null");

        // ACTION BAR INIT
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions( ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_jefe);

        View customActionBarView = actionBar.getCustomView();

        nombreJefe = (TextView) findViewById(R.id.textUsuarioJefe);
        jefeRUT = (TextView) findViewById(R.id.textRUT);
        imageSignal = (ImageView) findViewById(R.id.imageSignal);


        // Chequea constantemente si hay internet o no
        checkConnection();

        this.getValuesUser();

        this.getListInformationDetail();

        // SHARED PREFERENCES
        prefs = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
        editor = prefs.edit();

        context = this;
        this.sessionHandler = new SessionHandler(this.context);


        // NFC CONFIGURATION

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };
        this.nfcHandler = new NFCHandler(this, context, nfcAdapter);
        //instanciamos al handler de
        String text = this.nfcHandler.readerTAGNFC(getIntent());

        myRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        myRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(mLayoutManager);

        if (this.getListInformationDetail().size()==0){
            Log.e("TAG ERROR","Lista de sesiones viene vacía "+this.getListInformationDetail().isEmpty());
        }else{
            mAdapter = new RecyclerAdapter(this.getListInformationDetail(), R.layout.listasesiones_recycler, new RecyclerAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(String idInforme,String tokenSession,String idUser, int position) {

                    //Toast.makeText(MainActivity_jefeSesiones.this, idInforme + " - " + tokenSession + " - " + idUser + " - " + position, Toast.LENGTH_SHORT).show();
                    informationDetailSession = new InformationDetailSession(tokenSession, getApplicationContext(), idUser);
                    FA.showInformationInformeSessionTAG(new HandlerDBPersistence(getApplicationContext()),tokenSession);

                    //SET VALUES TO DETALLE SESION
                    editor.putString("tokenSession",tokenSession);
                    editor.putString("tagMaquinaria",informationDetailSession.getMaquinaria().getCodeInternoMachine());
                    editor.putString("tagImplemento",informationDetailSession.getImplemento().getCodeInternoImplemento());
                    editor.putString("idUsuario",informationDetailSession.getUserSession().getIDUser());
                    editor.putString("idPredio",informationDetailSession.getPredio().getCode_internoPredio());
                    editor.putString("idFaena",informationDetailSession.getFaena().getCodeInternoFaena());
                    editor.putString("namePredio",informationDetailSession.getPredio().getNamePredio());
                    editor.putString("nameFaena",informationDetailSession.getFaena().getNameFaena());
                    editor.commit();

                    Intent intent2 = new Intent(MainActivity_jefeSesiones.this, MainActivity_detalleSesion.class);
                    editor.putBoolean("fromSesiones",true);
                    editor.commit();
                    startActivity(intent2);
                    finish();
                }
            });
        }





        myRecyclerView.setAdapter(mAdapter);

    }

    public void alertWriteNFC(String message){
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

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        String response = this.nfcHandler.readerTAGNFC(intent);

        Log.e("TAG RESPONSE","response: "+response);

        if (!response.equalsIgnoreCase("VOID")){

            //SPLIT TO ARRAY THE VALUES OF TAG
            String[] arrayResponse = response.split(":");
            int responseSession = this.sessionHandler.createSession(response);
            Log.e("TAG RESPONSE","response session: "+responseSession);
            //String modalidad = prefs.getString("modalidad", "null");

            if (arrayResponse[1].equalsIgnoreCase("2")) {//operador

                if (checkUserExist(arrayResponse[2])){ //Usuario ya esta activo
                    Log.e("TAG ERROR",arrayResponse[2] + " Este usuario se encuentra activo");
                    alertWriteNFC("Usuario ya se encuentra activo. Favor cierre la sesión primero.");
                }else{
                    Log.e("TAG OK","No existen sesiones activas para el usuario. OK");
                    editor.putString("idUsuario",arrayResponse[2]);
                    editor.putString("modalidad","1");
                    editor.putString("tokenSession", this.sessionHandler.getTokenSession());//agregamos el token de la sesion del usuario
                    editor.commit();

                    Intent intent2 = new Intent(MainActivity_jefeSesiones.this,MainActivity_predio.class);
                    startActivity(intent2);
                    finish();
                }


            }else{
                if (arrayResponse[1].equalsIgnoreCase("1")){//jefe

                    this.alertWriteNFC("No puedes cerrar sesión, verifica que no existan operarios con sesiones activas");

                }else{
                    this.alertWriteNFC("TAG invalido. No corresponde a una pulsera");
                }
            }


        }else{
            Log.e("TAG ERROR:","response VOID: "+response);
            alertWriteNFC("Error al leer el TAG. Favor acerque nuevamente el dispositivo al TAG.");
        }

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }

    }

    public void getValuesUser(){

        UserSession userSession = new SessionHandler(getApplicationContext()).getInformationBoss(this.idJefe);
        this.nombreJefe.setText("Bienvenido " + userSession.getNameUser());
        this.jefeRUT.setText("RUT: "+userSession.getRutUser());

    }

    /**
     * Metodo que permite poder obtener toda la informacion asociada a los informes, uuuuuffff esperemos esta wea funcione!!!!
     * @return
     */
    public ArrayList<InformationDetailSession> getListInformationDetail(){

        Log.e("ACTIVE-SESSION", "Entre al metodo ql");
        //obtenemos todas las sesiones activas y en base a los TAG de sesion obtenemos la informacion de los informes que se han generado
        ArrayList<SessionClass> sessionActive = new SessionHandler(getApplicationContext()).getSessionActive();

        ArrayList<InformationDetailSession> listInforme = new ArrayList<>();


        for (int i=0; i<sessionActive.size(); i++){
            Log.e("ACTIVE-SESSION", sessionActive.get(i).getSessionToken());
            Log.e("ACTIVE-SESSION", sessionActive.get(i).getSessionKind());
            Log.e("ACTIVE-SESSION", sessionActive.get(i).getUserAssociated());
            listInforme.add(new InformationDetailSession(sessionActive.get(i).getSessionToken(), getApplicationContext(), sessionActive.get(i).getUserAssociated()));
        }

        for (int i=0; i<listInforme.size(); i++){
            Log.e("ACTIVE-SESSION", listInforme.get(i).getTokenSession());
            Log.e("ACTIVE-SESSION", listInforme.get(i).getUserSession().getIDUser());
            Log.e("ACTIVE-SESSION", listInforme.get(i).getUserSession().getNameUser());
            Log.e("ACTIVE-SESSION", listInforme.get(i).getMaquinaria().getCodeInternoMachine());
            Log.e("ACTIVE-SESSION", listInforme.get(i).getImplemento().getNameImplement());
            Log.e("ACTIVE-SESSION", listInforme.get(i).getFaena().getNameFaena());
        }
        return listInforme;
    }

    public boolean checkUserExist(String idUser){

        Boolean response=false;
        ArrayList<SessionClass> actives = new SessionHandler(getApplicationContext()).getSessionActive();

        for (int i=0; i< actives.size(); i++){
            if (actives.get(i).getUserAssociated().equalsIgnoreCase(idUser)){
                response=true;
                break;
            }
        }

        return response;
    }
}
