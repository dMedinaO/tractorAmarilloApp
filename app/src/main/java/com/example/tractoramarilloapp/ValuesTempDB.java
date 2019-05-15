package com.example.tractoramarilloapp;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.example.tractoramarilloapp.model.Faena;
import com.example.tractoramarilloapp.persistence.HandlerDBPersistence;
import com.example.tractoramarilloapp.syncService.ConnectToService;
import com.example.tractoramarilloapp.syncService.SyncDownService;

import java.util.ArrayList;

public class ValuesTempDB {

    public void addElements(Context context, Activity activity){


        SyncDownService syncDownService = new SyncDownService("http://45.7.228.219", "syncDownService.php", "/syncServiceTractorAmarillo/", context, "-", "-", activity);
        syncDownService.processSyncElement();

    }
}
