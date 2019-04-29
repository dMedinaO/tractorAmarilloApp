package com.example.tractoramarilloapp.persistence;

import android.provider.BaseColumns;

public class MaquinariaContract {

    public static abstract  class MaquinariaContractEntry implements BaseColumns {

        public static final String TABLE_NAME = "maquinaria";
        public static final String NAME_MACHINE = "name_machine";
        public static final String MODEL_MACHINE = "model_machine";
        public static final String YEARD_MACHINE = "yeard_machine";
        public static final String STATUS_MACHINE = "status_machine";
        public static final String PATENT_MACHINE = "patent_machine";
        public static final String COLOR_MACHINE = "color_machine";
        public static final String CODE_INTERNO_MACHINE = "codeInternoMachine";
        public static final String KIND_MACHINE = "tipoMaquinaria";
        public static final String MARK_MACHINE = "marca_maquina";
    }
}
