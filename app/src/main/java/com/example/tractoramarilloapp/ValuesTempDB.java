package com.example.tractoramarilloapp;

import android.content.Context;
import android.util.Log;

import com.example.tractoramarilloapp.model.Faena;
import com.example.tractoramarilloapp.model.Implemento;
import com.example.tractoramarilloapp.model.Maquinaria;
import com.example.tractoramarilloapp.model.Predio;
import com.example.tractoramarilloapp.model.TipoMaquinaria;
import com.example.tractoramarilloapp.model.UserSession;
import com.example.tractoramarilloapp.persistence.HandlerDBPersistence;

public class ValuesTempDB {

    public void addElements(Context context){

        HandlerDBPersistence handlerDBPersistence = new HandlerDBPersistence(context);

        //agregamos faenas
        handlerDBPersistence.saveFaena(new Faena("FAENA 1", "1"));
        handlerDBPersistence.saveFaena(new Faena("FAENA 2", "2"));
        handlerDBPersistence.saveFaena(new Faena("FAENA 3", "3"));
        handlerDBPersistence.saveFaena(new Faena("FAENA 4", "4"));

        //agregar predio
        handlerDBPersistence.savePredio(new Predio("PREDIO 1", "1"));
        handlerDBPersistence.savePredio(new Predio("PREDIO 2", "2"));
        handlerDBPersistence.savePredio(new Predio("PREDIO 3", "3"));
        handlerDBPersistence.savePredio(new Predio("PREDIO 4", "4"));

        //AGREGAR USUARIO 24858868
        handlerDBPersistence.saveUsuario(new UserSession("15993323", "JMedina", "15993323-7", "2"));
        handlerDBPersistence.saveUsuario(new UserSession("24858868", "REtcheverry", "24858868-3", "2"));
        handlerDBPersistence.saveUsuario(new UserSession("17609672", "LRojas", "17609672-1", "1"));
        handlerDBPersistence.saveUsuario(new UserSession("18228843", "DMedina", "18228843-8", "1"));

        //MAQUINARIA
        long response1 = handlerDBPersistence.saveMaquina(new Maquinaria("maquina2", "patito", "patito", "2019", "0", "---", "BLACK", "124ra", "1"));
        long response2 = handlerDBPersistence.saveMaquina(new Maquinaria("m123", "patito2", "patito2", "2018", "0", "---", "BLACK", "aqse", "2"));

        Log.e("INSERT", response1+" -- values DB");
        Log.e("INSERT", response2+" -- values DB");

        //implementos
        handlerDBPersistence.saveImplemento(new Implemento("implement1", "0", "2018", "f1", "color1", "12345", "124pq"));
        handlerDBPersistence.saveImplemento(new Implemento("i123", "0", "2018", "f1", "color1", "12345", "aqse"));

        //tipo maquinaria
        handlerDBPersistence.saveTipoMaquina(new TipoMaquinaria("1", "Tipo1"));
        handlerDBPersistence.saveTipoMaquina(new TipoMaquinaria("2", "Tipo2"));

        //SQL para crear la tabla relacional de asociacion entre maquinaria y operador
        handlerDBPersistence.execSQLData("INSERT INTO tipoHabilitado VALUES ('15993323', '2')");
        handlerDBPersistence.execSQLData("INSERT INTO tipoHabilitado VALUES ('15993323', '1')");
        handlerDBPersistence.execSQLData("INSERT INTO tipoHabilitado VALUES ('24858868', '2')");

        handlerDBPersistence.close();

    }
}
