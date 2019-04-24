package com.example.tractoramarilloapp.sessionHandler;

import com.example.tractoramarilloapp.utils.CreateValuesData;

/**
 * Clase que representa a una sesion, el tipo que posee y la informacion asociada a ella, tal como el token, tipo, fecha de inicio-termino y el estado en el que se encuentra la sesion
 */
public class SessionData {

    //atributos de la clase
    private String sessionToken;
    private String status;
    private String startSessionDate;
    private String endSessionDate;
    private String closeSessionKind;
    private String sessionKind;
    private String userAssociated;

    /**
     * Constructor de la clase que permite disponer la informacion de una session, la cual estara en estado iniciada,
     * @param userAssociated
     * @param sessionKind
     */
    public SessionData(String userAssociated, String sessionKind){

        this.userAssociated = userAssociated;
        this.sessionKind = sessionKind;
    }

    /**
     * Metodo que permite crear una nueva sesion, valida ciertas funcionalidades y permite el acceso con respecto al tipo de acceso solicitado segun el rol correspondiente
     * @return
     */
    public int createSession(){

        String token = CreateValuesData.generateTokenSession();
        int response;

        return 0;
    }
}
