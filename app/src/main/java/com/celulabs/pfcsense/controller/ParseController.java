package com.celulabs.pfcsense.controller;

import android.content.Context;

import com.celulabs.pfcsense.model.BarometerData;
import com.celulabs.pfcsense.model.DeviceInfo;
import com.celulabs.pfcsense.model.DucksboardSettings;
import com.celulabs.pfcsense.model.HumidityData;
import com.celulabs.pfcsense.model.LuxometerData;
import com.celulabs.pfcsense.model.SensorData;
import com.celulabs.pfcsense.model.SensorInfo;
import com.celulabs.pfcsense.model.TemperatureData;
import com.celulabs.pfcsense.model.TemperatureIRData;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Controlador que se encarga de configurar la aplicación para que funcione con parse
 *
 * @author gonztirado
 */
public class ParseController {
    private static final String APPLICATION_ID = "b0qMt6S10hSKTZAkkMFxagZWx4Ncv8foDBNkLvyw";
    private static final String CLIENT_KEY = "HiQrmFNU76iO7HTOfeUSiLMK6kJANOmy5UP6NpTz";

    private static ParseController ourInstance = new ParseController();

    /**
     * @return devuelve la instancia del controlador
     */
    public static ParseController getInstance() {
        return ourInstance;
    }

    /**
     * Constructor
     */
    private ParseController() {
    }

    /**
     * Inicializa la aplicación para que funcione Parse.
     * Este método debe ser invocado en nuestra clase Application en el método 'onCreate()'
     *
     * @param context contexto de la aplicación
     */
    public void initApp(Context context) {
        registerSubclasses();
        Parse.enableLocalDatastore(context);
        Parse.initialize(context, APPLICATION_ID, CLIENT_KEY);
        configureACL();
    }

    /**
     * Registra las subclases de parse que vamos a utilizar en la aplicación.
     * Conforme se vayan agregando clases al modelo será necesario añadirlas en este método
     */
    private void registerSubclasses() {
        ParseObject.registerSubclass(DeviceInfo.class);
        ParseObject.registerSubclass(SensorInfo.class);
        ParseObject.registerSubclass(SensorData.class);
        ParseObject.registerSubclass(TemperatureData.class);
        ParseObject.registerSubclass(TemperatureIRData.class);
        ParseObject.registerSubclass(BarometerData.class);
        ParseObject.registerSubclass(HumidityData.class);
        ParseObject.registerSubclass(LuxometerData.class);
        ParseObject.registerSubclass(DucksboardSettings.class);
    }

    /**
     * Configura los permisos ACL de parse
     */
    private void configureACL() {
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);
    }
}
