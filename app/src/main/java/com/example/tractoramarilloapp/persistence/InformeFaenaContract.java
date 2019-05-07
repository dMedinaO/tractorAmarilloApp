package com.example.tractoramarilloapp.persistence;

import android.provider.BaseColumns;

/**
 * Clase que permite representar al informe de faena mapeado en la base de datos
 */
public class InformeFaenaContract {

    public static abstract class InformeFaenaContractEntry implements BaseColumns {

        public static final String TABLE_NAME = "informeFaena";
        public static final String ID_INFORME = "idinformeFaena";
        public static final String ID_FAENA = "idFaena";
        public static final String HORA_INICIO = "horaInicio";
        public static final String HORA_TERMINO = "horaFinal";
        public static final String USER_ID = "userID";
        public static final String SESSION_TOKEN = "sessionTAG";
        public static final String INFORME_MAQUINARIA_ID = "idInformeMaquinaria";
        public static final String STATUS_SEND = "statusSend";

    }
}
