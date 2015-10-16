package com.celulabs.pfcsense.controller;

import com.celulabs.pfcsense.model.SensorData;

/**
 * Controlador de tratamiento de información de sensores de temperatura
 *
 * @author gonztirado
 */
public class SensorDataController {

    private static SensorDataController ourInstance = new SensorDataController();

    public static SensorDataController getInstance() {
        return ourInstance;
    }

    private SensorDataController() {
    }

    public void addSensorValue(double value) {
        SensorData sensorData = new SensorData();
        sensorData.setDeviceId("bq_e4");
        sensorData.setSensorId("bqsense_1");
        sensorData.setValue(value);
        sensorData.setTimestamp(System.currentTimeMillis());
        sensorData.saveInBackground();
    }
}
