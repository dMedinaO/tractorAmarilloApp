package com.example.tractoramarilloapp.persistence;

import android.provider.BaseColumns;

public class PredioContract {

    public static abstract  class PredioContractEntry implements BaseColumns {

        public static final String TABLE_NAME = "predio";
        public static final String PREDIO_NAME = "name_predio";
        public static final String CODE_PREDIO = "code_predio";
    }
}
