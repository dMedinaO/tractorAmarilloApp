package com.example.tractoramarilloapp.syncService;

import android.content.Context;
import android.util.Log;

import com.example.tractoramarilloapp.model.Faena;
import com.example.tractoramarilloapp.model.Implemento;
import com.example.tractoramarilloapp.model.Maquinaria;
import com.example.tractoramarilloapp.model.Predio;
import com.example.tractoramarilloapp.model.TipoMaquinaria;
import com.example.tractoramarilloapp.model.UserSession;
import com.example.tractoramarilloapp.persistence.HandlerDBPersistence;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Clase con la responsabilidad de procesar la informacion asociada a la data desde el servidor, posee diferentes metodos
 * con respecto al procesamiento de la data asociada a los diferentes tipos de clases
 *
 */
public class ProcessDataJSON {

    public boolean processDataFaena(JSONArray jsonArray, Context context) throws JSONException {

        //instanciamos una lista de tablas Sync
        ArrayList<Faena> listDataFaena =  new ArrayList<>();

        for(int i=0; i<jsonArray.length();i++){
            JSONObject object = jsonArray.getJSONObject(i);


            listDataFaena.add(new Faena(object.getString("nombreFaena"), object.getString("idfaena"), "-"));
        }

        new HandlerDBPersistence(context).processUpdateInformationFaena(listDataFaena);
        Log.i("responseData", listDataFaena.toString());
        return true;
    }

    public boolean processDataPredio(JSONArray jsonArray, Context context) throws JSONException {

        //instanciamos una lista de tablas Sync
        ArrayList<Predio> listDataPredio =  new ArrayList<>();

        for(int i=0; i<jsonArray.length();i++){
            JSONObject object = jsonArray.getJSONObject(i);


            listDataPredio.add(new Predio(object.getString("nombrePredio"), object.getString("idpredio")));
        }

        new HandlerDBPersistence(context).processUpdateInformationPredio(listDataPredio);
        Log.i("responseData", listDataPredio.toString());
        return true;
    }

    public boolean processDataUser(JSONArray jsonArray, Context context) throws  JSONException{


        ArrayList<UserSession> listDataUser =  new ArrayList<>();

        for(int i=0; i<jsonArray.length();i++){
            JSONObject object = jsonArray.getJSONObject(i);


            listDataUser.add(new UserSession(object.getString("idusuario"), object.getString("nombreCompleto"), object.getString("rutUsuario"), object.getString("rol")));
        }

        new HandlerDBPersistence(context).processUpdateInformationUser(listDataUser);
        Log.i("responseData", listDataUser.toString());
        return true;
    }

    public boolean processDataMaquinaria(JSONArray jsonArray, Context context) throws  JSONException{


        ArrayList<Maquinaria> listMaquinaria =  new ArrayList<>();

        for(int i=0; i<jsonArray.length();i++){
            JSONObject object = jsonArray.getJSONObject(i);

            String nameMachine = object.getString("nombreMaquinaria");
            String markMachine = object.getString("marcaMaquina");
            String modelMachine = object.getString("modeloMaquina");
            String yeardMachine = object.getString("anhoMaquina");
            String statusMachine = object.getString("estadoMaquina");
            String patentMachine = object.getString("patenteMaquinaria");
            String colorMachine = object.getString("colorMaquinaria");
            String tipoMaquinaria = object.getString("tipoMaquinaria");
            String codeInterno = object.getString("tagIdentificador");
            String categoria = object.getString("categoria");

            listMaquinaria.add(new Maquinaria(nameMachine, markMachine,  modelMachine, yeardMachine, statusMachine, patentMachine, colorMachine, codeInterno, tipoMaquinaria, categoria));
        }

        new HandlerDBPersistence(context).processUpdateInformationMaquinaria(listMaquinaria);
        Log.i("responseData", listMaquinaria.toString());
        return true;
    }

    public boolean processDataTipoMaquinaria(JSONArray jsonArray, Context context) throws  JSONException{


        ArrayList<TipoMaquinaria> listTipoMaquinaria =  new ArrayList<>();

        for(int i=0; i<jsonArray.length();i++){
            JSONObject object = jsonArray.getJSONObject(i);

            String idtipoMaquinaria = object.getString("idtipoMaquinaria");
            String descriptorMaquinaria = object.getString("descriptorMaquinaria");

            listTipoMaquinaria.add(new TipoMaquinaria(idtipoMaquinaria, descriptorMaquinaria));
        }

        new HandlerDBPersistence(context).processUpdateInformationTipoMaquinaria(listTipoMaquinaria);
        Log.i("responseData", listTipoMaquinaria.toString());
        return true;
    }

    public boolean processDataTipoHabilitado(JSONArray jsonArray, Context context) throws  JSONException{


        ArrayList<String> stringQueryData =  new ArrayList<>();

        for(int i=0; i<jsonArray.length();i++){
            JSONObject object = jsonArray.getJSONObject(i);

            String id_operador = object.getString("id_operador");
            String id_tipoMaquinaria = object.getString("id_tipoMaquinaria");

            String query = "INSERT INTO tipoHabilitado values ('"+id_operador+"', '"+id_tipoMaquinaria+"')";

            stringQueryData.add(query);
        }

        new HandlerDBPersistence(context).processUpdateInformationTipoHabilitado(stringQueryData);
        Log.i("responseData", stringQueryData.toString());
        return true;
    }

    public boolean processDataImplemento(JSONArray jsonArray, Context context) throws  JSONException{


        ArrayList<Implemento> listImplemento =  new ArrayList<>();

        for(int i=0; i<jsonArray.length();i++){
            JSONObject object = jsonArray.getJSONObject(i);

            String nameImplement = object.getString("nombreImplemento");
            String statusImplement = object.getString("estadoImplemento");
            String ano = object.getString("anho");
            String fabricante = object.getString("fabricante");
            String color = object.getString("color");
            String capacidad = object.getString("capacidad");
            String codeInternoImplemento = object.getString("tagIdentificador");
            String categoria = object.getString("categoria");

            listImplemento.add(new Implemento(nameImplement, statusImplement, ano, fabricante, color, capacidad, codeInternoImplemento, categoria));
        }

        new HandlerDBPersistence(context).processUpdateInformationImplemento(listImplemento);
        Log.i("responseData", listImplemento.toString());
        return true;
    }

    public  boolean processMensajeMotivacional(JSONArray jsonArray, Context context) throws  JSONException{

        ArrayList<String> stringQueryData =  new ArrayList<>();

        for(int i=0; i<jsonArray.length();i++){
            JSONObject object = jsonArray.getJSONObject(i);

            String idMensaje = object.getString("idMensaje");
            String descripcion = object.getString("descripcion");

            String query = "INSERT INTO mensajeMotivacional values ("+idMensaje+", '"+descripcion+"')";

            stringQueryData.add(query);
        }

        new HandlerDBPersistence(context).processUpdateInformationMensaje(stringQueryData);
        Log.i("responseData", stringQueryData.toString());
        return true;
    }
}
