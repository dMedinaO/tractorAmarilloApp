package com.example.tractoramarilloapp.persistence;

import android.provider.BaseColumns;

public class UnityLocalContract {

    public static abstract class UnityLocalContractEntry implements BaseColumns {

        public static final String TABLE_NAME = "unidadLocal";
        public static final String ID_UNIDAD = "idUnidad";
        public static final String ID_USUARIO = "idUsuario";
        public static final String TOKEN_SESSION = "tokenSession";
        public static final String TOKEN_SESSION_PREV = "tokenSessionPre";
        public static final String START_SESSION = "startSession";
        public static final String END_SESSION = "closeSession";
        public static final String CLOSE_SESSION_KIND = "clseSessionKind";
        public static final String ID_MAQUINARIA =   "idMaquinaria";

        public static final String HOROMETRO_INICIAL = "horometroInicial";
        public static final String HOROMETRO_FINAL = "horometroFinal";
        public static final String ID_PREDIO = "idPredio";
        public static final String IS_AVAILABLE_IMPLEMENT = "isAvailableImplement";
        public static final String ID_IMPLEMENT =   "idImplemento";

        public static final String INICIO_IMPLEMENTO = "inicioImplemento";
        public static final String FIN_IMPLEMENTO = "finImplemento";
        public static final String ID_FAENA = "idFaena";
        public static final String STATUS_SEND = "statusSend";
    }
}