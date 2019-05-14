package com.example.tractoramarilloapp;

import android.content.Context;

import com.example.tractoramarilloapp.syncService.SyncDownService;

public class ValuesTempDB {

    public void addElements(Context context){

        SyncDownService syncDownService = new SyncDownService("http://192.168.1.146", "syncDownService.php", "/syncServiceTractorAmarillo/", context, "-", "-");
        syncDownService.processSyncElement();

    }
}
