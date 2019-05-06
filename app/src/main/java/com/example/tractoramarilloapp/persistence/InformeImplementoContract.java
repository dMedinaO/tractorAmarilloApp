package com.example.tractoramarilloapp.persistence;

import android.provider.BaseColumns;

/**
 * Clase que permite representar la informacion en base de datos del informe de implementos usados para faenas
 */
public class InformeImplementoContract {

    public static abstract class InformeImplementoContractEntry implements BaseColumns {

        public static final String TABLE_NAME = "informeUsoImplemento";
        public static final String ID_INFORME = "idinformeImplemento";
        public static final String ID_IMPLEMENTO = "idImplemento";
        public static final String HORA_INICIO = "horaInicio";
        public static final String HORA_TERMINO = "horaFinal";
        public static final String USER_ID = "userID";
        public static final String SESSION_TOKEN = "sessionTAG";
        public static final String INFORME_MAQUINARIA_ID = "idInformeMaquinaria";
        public static final String STATUS_SEND = "statusSend";

    }
}
