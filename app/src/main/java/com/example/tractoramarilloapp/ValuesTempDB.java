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
        handlerDBPersistence.saveFaena(new Faena("FAENA 1", "1", "124pq"));
        handlerDBPersistence.saveFaena(new Faena("FAENA 2", "2", "124pq"));
        handlerDBPersistence.saveFaena(new Faena("FAENA 3", "3", "aqse"));
        handlerDBPersistence.saveFaena(new Faena("FAENA 4", "4", "aqse"));

        //agregar predio
        handlerDBPersistence.savePredio(new Predio("PREDIO 1", "1"));
        handlerDBPersistence.savePredio(new Predio("PREDIO 2", "2"));
        handlerDBPersistence.savePredio(new Predio("PREDIO 3", "3"));
        handlerDBPersistence.savePredio(new Predio("PREDIO 4", "4"));

        //AGREGAR USUARIO 24858868
        handlerDBPersistence.saveUsuario(new UserSession("15993323", "JMedina", "15993323-7", "2"));
        handlerDBPersistence.saveUsuario(new UserSession("24858868", "REtcheverry", "24858868-3", "2"));
        handlerDBPersistence.saveUsuario(new UserSession("17609672", "LRojas", "17609672-1", "1"));
        handlerDBPersistence.saveUsuario(new UserSession("18228843", "DMedina", "18228843-8", "2"));

        //MAQUINARIA
        long response1 = handlerDBPersistence.saveMaquina(new Maquinaria("M2", "patito", "patito", "2019", "0", "--", "BLACK", "124ra", "1"));//
        long response2 = handlerDBPersistence.saveMaquina(new Maquinaria("M1", "patito2", "patito2", "2018", "0", "--", "BLACK", "aqse", "2"));
        long response3 = handlerDBPersistence.saveMaquina(new Maquinaria("M3", "patito", "patito", "2019", "0", "--", "BLACK", "124qa", "1"));

        //implementos
        handlerDBPersistence.saveImplemento(new Implemento("I3", "0", "2018", "f1", "color1", "12345", "aqws"));
        handlerDBPersistence.saveImplemento(new Implemento("I1", "0", "2018", "f1", "color3", "12345", "aqws2"));
        handlerDBPersistence.saveImplemento(new Implemento("I2", "0", "2018", "f1", "color2", "12345", "aqws3"));

        //tipo maquinaria
        handlerDBPersistence.saveTipoMaquina(new TipoMaquinaria("1", "Tipo1"));
        handlerDBPersistence.saveTipoMaquina(new TipoMaquinaria("2", "Tipo2"));

        //SQL para insertar elementos en la tabla de tipo habilitado, en la relacion de operarios que pueden manejar cierta wea de maquina
        handlerDBPersistence.execSQLData("INSERT INTO tipoHabilitado VALUES ('15993323', '1')");
        handlerDBPersistence.execSQLData("INSERT INTO tipoHabilitado VALUES ('15993323', '2')");
        handlerDBPersistence.execSQLData("INSERT INTO tipoHabilitado VALUES ('24858868', '2')");
        handlerDBPersistence.execSQLData("INSERT INTO tipoHabilitado VALUES ('18228843', '1')");

        //SQL para insertar elementos en la tabla implemento habilitado esto en la relación de implementos que estan habilitados para cierta wea de maquina
        handlerDBPersistence.execSQLData("INSERT INTO implementoHabilitado VALUES ('aqws', '124qa')");//I3-M3
        handlerDBPersistence.execSQLData("INSERT INTO implementoHabilitado VALUES ('aqws', 'aqse')");//I3-M1
        handlerDBPersistence.execSQLData("INSERT INTO implementoHabilitado VALUES ('aqws2', '124ra')");//I1-M2
        handlerDBPersistence.execSQLData("INSERT INTO implementoHabilitado VALUES ('aqsw3', '124qa')");//I2-M3

        handlerDBPersistence.close();

    }
}
