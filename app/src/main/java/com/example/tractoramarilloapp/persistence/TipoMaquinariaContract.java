package com.example.tractoramarilloapp.persistence;

import android.provider.BaseColumns;

public class TipoMaquinariaContract {

    public static abstract class TipoMaquinariaContractEntry implements BaseColumns {

        public static final String TABLE_NAME = "tipo_maquinaria";
        public static final String CODE_INTERNO = "code_interno";
        public static final String NAME_TIPO = "tipo_maquinaria";

    }
}