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
import com.example.tractoramarilloapp.syncService.SyncDownService;

public class ValuesTempDB {

    public void addElements(Context context){

        SyncDownService syncDownService = new SyncDownService("http://192.168.1.146", "syncDownService.php", "/syncServiceTractorAmarillo/", context, "-");
        syncDownService.processSyncElement();

    }
}
