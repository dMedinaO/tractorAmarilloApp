package com.example.tractoramarilloapp.persistence;

import android.provider.BaseColumns;

/**
 * Clase asociada al mapeo de las sesiones en el dispositivo para el correspondiente registro en la base de datos
 */
public class SessionClassContract {

    public static abstract  class SessionClassContractEntry implements BaseColumns{

        public static final String TABLE_NAME = "session_table";
        public static final String TOKEN = "session_token";
        public static final String STATUS = "status";
        public static final String START_SESSION = "session_start";
        public static final String END_SESSION = "session_end";
        public static final String CLOSE_SESSION_KIND = "close_session_kind";
        public static final String SESSION_KIND = "session_kind";
        public static final String USER_SESSION =   "user_session";
    }
}
