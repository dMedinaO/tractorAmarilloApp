package com.example.tractoramarilloapp.persistence;

import android.provider.BaseColumns;

public class ComentarioContract {

    public static abstract  class ComentarioContractEntry implements BaseColumns {

        public static final String TABLE_NAME = "comentario";
        public static final String ID_COMENTARIO = "idComentario";
        public static final String ID_USER = "userID";
        public static final String HORA_COMENTARIO = "horaComentario";
        public static final String CONTENT_COMENTARIO = "descripcion";

    }
}
