package com.example.tractoramarilloapp.persistence;

import android.provider.BaseColumns;

/**
 * Clase que representa la informacion asociada al informe de maquinarias
 */
public class InformeMaquinariaContract {

    public static abstract class InformeMaquinariaContractEntry implements BaseColumns {

        public static final String TABLE_NAME = "informeMaquinaria";
        public static final String ID_INFORME = "idinformeMaquinaria";
        public static final String ID_MAQUINARIA = "idMaquinaria";
        public static final String ID_PREDIO = "idPredio";
        public static final String HOROMETRO_INICIO = "horometroInicio";
        public static final String HOROMETRO_TERMINO = "horometroFinal";
        public static final String USER_ID = "userID";
        public static final String SESSION_TOKEN = "sessionTAG";
        public static final String CLOSE_SESSION_KIND = "closeSessionKind";
        public static final String TOKEN_PRE = "tokenPrevio";
        public static final String STATUS_SEND = "statusSend";

    }
}
