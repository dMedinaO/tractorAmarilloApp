package com.example.tractoramarilloapp.handlers;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.tractoramarilloapp.model.Faena;
import com.example.tractoramarilloapp.model.Implemento;
import com.example.tractoramarilloapp.model.Maquinaria;
import com.example.tractoramarilloapp.model.Predio;
import com.example.tractoramarilloapp.model.UserSession;
import com.example.tractoramarilloapp.persistence.FaenaContract;
import com.example.tractoramarilloapp.persistence.HandlerDBPersistence;
import com.example.tractoramarilloapp.persistence.ImplementContract;
import com.example.tractoramarilloapp.persistence.InformeFaenaContract;
import com.example.tractoramarilloapp.persistence.InformeImplementoContract;
import com.example.tractoramarilloapp.persistence.InformeMaquinariaContract;
import com.example.tractoramarilloapp.persistence.MaquinariaContract;
import com.example.tractoramarilloapp.persistence.PredioContract;
import com.example.tractoramarilloapp.persistence.SessionClassContract;
import com.example.tractoramarilloapp.persistence.UserContract;

/**
 * Clase con la responsabilidad de obtener toda la informacion relevante a la sesion y el informe correspondiente,
 * permite obtener la data del usuario, la data del implemento, faena y maquinaria, contemplando para la busqueda
 * el token de sesion
 */
public class InformationDetailSession {

    //atributos de la clase
    private String tokenSession;
    private String idUser;
    private String informeID;
    private UserSession userSession;
    private Predio predio;
    private Maquinaria maquinaria;
    private Implemento implemento;
    private Faena faena;

    private HandlerInforme handlerInforme;
    private Context context;
    private HandlerDBPersistence handlerDBPersistence;

    /**
     * Constructor de la clase, instancia los handler asociados a la data a procesar y obtiene la informacion de estos
     * @param tokenSession
     * @param context
     */
    public InformationDetailSession(String tokenSession, Context context, String idUser){
        this.tokenSession = tokenSession;
        this.idUser = idUser;
        this.context = context;
        this.handlerDBPersistence = new HandlerDBPersistence(this.context);
        this.handlerInforme = new HandlerInforme(this.context);

        this.getDataUser();//obtenemos la informacion del usuario
        this.getDataInformeMaquinaria();//obtenemos la informacion de la maquinaria y el predio
        this.getDataFaena();//obtenemos la informcion de la faena
        this.getDataImplemento();//obtenemos la informacion del implemento, este valor puede ser nulo, lo que indica sin implemento
    }

    /**
     * Metodo que permite obtener la informacion del usuario en base al token de sesion
     */
    public void getDataUser(){

        //hacemos la consulta con respecto a la informacion de la data en base a la informacion de las tablas
        String sqlInfoUser = "SELECT * FROM "+ UserContract.UserContractEntry.TABLE_NAME
                + " WHERE " + UserContract.UserContractEntry.ID_USER + " = '"+this.idUser+"'";

        Log.e("QUERY-INFORME-USER", sqlInfoUser);

        Cursor cursor = this.handlerDBPersistence.consultarRegistros(sqlInfoUser);

        Log.e("QUERY-INFORME-USER", cursor.getCount()+" cantidad de wns que vienen en la query");
        cursor.moveToFirst();

        int userID = cursor.getColumnIndex(UserContract.UserContractEntry.ID_USER);
        int rutUser = cursor.getColumnIndex(UserContract.UserContractEntry.RUT_USER);
        int userName = cursor.getColumnIndex(UserContract.UserContractEntry.NAME_USER);
        int rolUser = cursor.getColumnIndex(UserContract.UserContractEntry.ROL);

        String userIDV = cursor.getString(userID);
        String rutUserV = cursor.getString(rutUser);
        String userNameV = cursor.getString(userName);
        String rolUserV = cursor.getString(rolUser);
        this.userSession = new UserSession(userIDV, userNameV, rutUserV, rolUserV);
    }

    public void getDataInformeMaquinaria(){

        //consultamos al informe maquinaria y obtenemos la data de esta
        String sqlData = "SELECT * FROM "+ InformeMaquinariaContract.InformeMaquinariaContractEntry.TABLE_NAME +
                        " join " + PredioContract.PredioContractEntry.TABLE_NAME + " on "+ InformeMaquinariaContract.InformeMaquinariaContractEntry.ID_PREDIO+
                        " = " + PredioContract.PredioContractEntry.CODE_PREDIO + " join " + MaquinariaContract.MaquinariaContractEntry.TABLE_NAME + " on " +
                        MaquinariaContract.MaquinariaContractEntry.CODE_INTERNO_MACHINE + " = " + InformeMaquinariaContract.InformeMaquinariaContractEntry.ID_MAQUINARIA +
                        " where " + InformeMaquinariaContract.InformeMaquinariaContractEntry.SESSION_TOKEN + " = '"+this.tokenSession+"'";

        Cursor cursor = this.handlerDBPersistence.consultarRegistros(sqlData); //hacemos la consulta y obtenemos el registro, dado a que el tag es unico, solo obtenemos un dato

        cursor.moveToFirst();

        //debemos instanciar un predio y una maquinaria
        int idPredio = cursor.getColumnIndex(PredioContract.PredioContractEntry.CODE_PREDIO);
        int namePredio = cursor.getColumnIndex(PredioContract.PredioContractEntry.PREDIO_NAME);
        String predioName = cursor.getString(namePredio);
        String predioID = cursor.getString(idPredio);
        this.predio = new Predio(predioName, predioID);

        //debemos instanciar una maquinaria, por lo que obtenemos la informacion necesaria
        int idMaquinaria = cursor.getColumnIndex(MaquinariaContract.MaquinariaContractEntry.CODE_INTERNO_MACHINE);
        int nameMaquinaria = cursor.getColumnIndex(MaquinariaContract.MaquinariaContractEntry.NAME_MACHINE);

        int markMachine = cursor.getColumnIndex(MaquinariaContract.MaquinariaContractEntry.MARK_MACHINE);
        int modelMachine = cursor.getColumnIndex(MaquinariaContract.MaquinariaContractEntry.MODEL_MACHINE);
        int yeardMachine = cursor.getColumnIndex(MaquinariaContract.MaquinariaContractEntry.YEARD_MACHINE);
        int statusMachine = cursor.getColumnIndex(MaquinariaContract.MaquinariaContractEntry.STATUS_MACHINE);
        int patentMachine = cursor.getColumnIndex(MaquinariaContract.MaquinariaContractEntry.PATENT_MACHINE);
        int colorMachine = cursor.getColumnIndex(MaquinariaContract.MaquinariaContractEntry.COLOR_MACHINE);
        int tipoMachine = cursor.getColumnIndex(MaquinariaContract.MaquinariaContractEntry.KIND_MACHINE);
        int informeMaquinariaID = cursor.getColumnIndex(InformeMaquinariaContract.InformeMaquinariaContractEntry.ID_INFORME);
        int categoriaData = cursor.getColumnIndex(MaquinariaContract.MaquinariaContractEntry.CATEGORIA_MACHINE);

        String nameMaquina = cursor.getString(nameMaquinaria);
        String idMaquina = cursor.getString(idMaquinaria);

        String markMachineV = cursor.getString(markMachine);
        String  modelMachineV = cursor.getString(modelMachine);
        String yeardMachineV = cursor.getString(yeardMachine);
        String statusMachineV = cursor.getString(statusMachine);
        String patentMachineV = cursor.getString(patentMachine);
        String colorMachineV = cursor.getString(colorMachine);
        String tipoMachineV = cursor.getString(tipoMachine);
        String categoriaV = cursor.getString(categoriaData);

        this.informeID = cursor.getString(informeMaquinariaID);//esto es para obtener la informacion del informe

        this.maquinaria = new Maquinaria(nameMaquina, markMachineV, modelMachineV, yeardMachineV, statusMachineV, patentMachineV, colorMachineV, idMaquina, tipoMachineV, categoriaV);
    }

    /**
     * Metodo que permite obtener la informacion de la faena asociada al informe de maquinaria
     */
    public void getDataFaena(){

        //hacemos la consulta y obtenemos la data correspondiente

        String sqlData = "SELECT * FROM "+ InformeFaenaContract.InformeFaenaContractEntry.TABLE_NAME +
                        " join " + FaenaContract.FaenaContractEntry.TABLE_NAME + " on "+
                        FaenaContract.FaenaContractEntry.CODE_FAENA + " = " + InformeFaenaContract.InformeFaenaContractEntry.ID_FAENA + " where " + InformeFaenaContract.InformeFaenaContractEntry.INFORME_MAQUINARIA_ID +
                        " = '"+this.informeID+"'";

        Log.e("QUERY-FAENA-INFORME", sqlData);

        Cursor cursor = this.handlerDBPersistence.consultarRegistros(sqlData);

        cursor.moveToFirst();

        int faenaName = cursor.getColumnIndex(FaenaContract.FaenaContractEntry.FAENA_NAME);
        int faenaID = cursor.getColumnIndex(FaenaContract.FaenaContractEntry.CODE_FAENA);
        int codeImplemento = cursor.getColumnIndex(FaenaContract.FaenaContractEntry.CODE_IMPLEMENTO);
        String nameFaena = cursor.getString(faenaName);
        String idFaena = cursor.getString(faenaID);
        String codeID = cursor.getString(codeImplemento);

        this.faena = new Faena(nameFaena, idFaena, codeID);

    }

    /**
     * Metodo que permite obtener la informacion del implemento, este valor puede ser nulo debido a trabajos asociados a faena sin implemento
     */
    public void getDataImplemento(){

        String sqldata = "SELECT * FROM "+ InformeImplementoContract.InformeImplementoContractEntry.TABLE_NAME
                        + " join "+ ImplementContract.ImplementContractEntry.TABLE_NAME + " on " + ImplementContract.ImplementContractEntry.CODE_INTERNO_IMPLEMENTO + " = " + InformeImplementoContract.InformeImplementoContractEntry.ID_IMPLEMENTO
                        + " where " + InformeImplementoContract.InformeImplementoContractEntry.INFORME_MAQUINARIA_ID + " = '"+this.informeID+"'";

        Cursor cursor = this.handlerDBPersistence.consultarRegistros(sqldata);

        if (cursor == null || cursor.getCount()==0){//trabajo sin implemento el mono ql
            this.implemento = new Implemento("SIN IMPLEMENTO", "--", "--", "--", "--", "--", "0", "--");
        }else{//trabajo con implemento

            cursor.moveToFirst();
            //obtenemos la informacion del implemento
            int nameImplemento = cursor.getColumnIndex(ImplementContract.ImplementContractEntry.NAME_IMPLEMENTO);
            int statusImplemento = cursor.getColumnIndex(ImplementContract.ImplementContractEntry.STATUS_IMPLEMENTO);
            int anoImplemento = cursor.getColumnIndex(ImplementContract.ImplementContractEntry.ANO_IMPLEMENTO);
            int fabricante = cursor.getColumnIndex(ImplementContract.ImplementContractEntry.FABRICANTE_IMPLEMENTO);
            int color = cursor.getColumnIndex(ImplementContract.ImplementContractEntry.COLOR_IMPLEMENTO);
            int capacidad = cursor.getColumnIndex(ImplementContract.ImplementContractEntry.CAPACIDAD_IMPLEMENTO);
            int codeInternoImp = cursor.getColumnIndex(ImplementContract.ImplementContractEntry.CODE_INTERNO_IMPLEMENTO);
            int categoriaImplemento = cursor.getColumnIndex(ImplementContract.ImplementContractEntry.CATEGORIA_IMPLEMENTO);

            String nameImplementoV = cursor.getString(nameImplemento);
            String statusImplementoV = cursor.getString(statusImplemento);
            String anoImplementoV = cursor.getString(anoImplemento);
            String fabricanteV = cursor.getString(fabricante);
            String colorV = cursor.getString(color);
            String capacidadV = cursor.getString(capacidad);
            String codeInternoImpV = cursor.getString(codeInternoImp);
            String categoriaImplementoV = cursor.getString(categoriaImplemento);
            this.implemento = new Implemento(nameImplementoV, statusImplementoV, anoImplementoV, fabricanteV, colorV, capacidadV, codeInternoImpV, categoriaImplementoV);

        }
    }

    public String getTokenSession() {
        return tokenSession;
    }

    public void setTokenSession(String tokenSession) {
        this.tokenSession = tokenSession;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public Predio getPredio() {
        return predio;
    }

    public void setPredio(Predio predio) {
        this.predio = predio;
    }

    public Maquinaria getMaquinaria() {
        return maquinaria;
    }

    public void setMaquinaria(Maquinaria maquinaria) {
        this.maquinaria = maquinaria;
    }

    public Implemento getImplemento() {
        return implemento;
    }

    public void setImplemento(Implemento implemento) {
        this.implemento = implemento;
    }

    public Faena getFaena() {
        return faena;
    }

    public void setFaena(Faena faena) {
        this.faena = faena;
    }

    public HandlerInforme getHandlerInforme() {
        return handlerInforme;
    }

    public void setHandlerInforme(HandlerInforme handlerInforme) {
        this.handlerInforme = handlerInforme;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public HandlerDBPersistence getHandlerDBPersistence() {
        return handlerDBPersistence;
    }

    public void setHandlerDBPersistence(HandlerDBPersistence handlerDBPersistence) {
        this.handlerDBPersistence = handlerDBPersistence;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getInformeID() {
        return informeID;
    }

    public void setInformeID(String informeID) {
        this.informeID = informeID;
    }
}
