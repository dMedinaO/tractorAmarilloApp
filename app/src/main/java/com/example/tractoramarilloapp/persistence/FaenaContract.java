package com.example.tractoramarilloapp.persistence;

import android.provider.BaseColumns;

public class FaenaContract {

    public static abstract  class FaenaContractEntry implements BaseColumns {

        public static final String TABLE_NAME = "faena";
        public static final String FAENA_NAME = "name_faena";
        public static final String CODE_FAENA = "code_faena";
    }
}
