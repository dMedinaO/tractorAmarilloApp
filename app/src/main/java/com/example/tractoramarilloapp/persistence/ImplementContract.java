package com.example.tractoramarilloapp.persistence;

import android.provider.BaseColumns;

public class ImplementContract {

    public static abstract  class ImplementContractEntry implements BaseColumns {

        public static final String TABLE_NAME = "implemento";
        public static final String NAME_IMPLEMENTO = "name_implemento";
        public static final String STATUS_IMPLEMENTO = "status_implemento";
        public static final String ANO_IMPLEMENTO = "ano_implemento";
        public static final String FABRICANTE_IMPLEMENTO = "fabricante_implemento";
        public static final String COLOR_IMPLEMENTO = "color_implemento";
        public static final String CAPACIDAD_IMPLEMENTO = "capacidad_implemento";
        public static final String CODE_INTERNO_IMPLEMENTO = "codeInternoImplemento";
    }
}
