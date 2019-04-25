package com.example.tractoramarilloapp.persistence;

import android.provider.BaseColumns;

public class UserContract {

    public static abstract  class UserContractEntry implements BaseColumns {

        public static final String TABLE_NAME = "user";
        public static final String ID_USER = "id_user";
        public static final String NAME_USER = "name_user";
        public static final String RUT_USER = "rut_user";
        public static final String ROL = "rol_user";

    }
}
