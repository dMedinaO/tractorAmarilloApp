package com.example.tractoramarilloapp.persistence;

import android.provider.BaseColumns;

/**
 * Clase con la responsabilidad de mapear a la clase informeOperaciones
 */
public class InformeOperacionesContract {

    public static abstract  class InformeOperacionesContractEntry implements BaseColumns {

        public static final String TABLE_NAME = "informeOperaciones";
        public static final String ID_INFORME = "idinformeOperaciones";
        public static final String ID_MAQUINARIA = "idMaquinaria";
        public static final String HOROMETRO_INICIO = "horometroInicio";
        public static final String HOROMETRO_TERMINO = "horometroFinal";
        public static final String USER_ID = "userID";
        public static final String SESSION_TOKEN = "sessionTAG";
        public static final String IS_IMPLEMENT_ACTIVE = "isImplementActive";
        public static final String ID_IMPLEMENTO = "idImplemento";
        public static final String HORA_INICIO = "horarioInicio";
        public static final String HORA_TERMINO = "horarioFinal";
        public static final String ID_FAENA = "idFaena";
        public static final String ID_PREDIO = "idPredio";
        public static final String STATUS_INFORME = "statusSend";
    }
}
