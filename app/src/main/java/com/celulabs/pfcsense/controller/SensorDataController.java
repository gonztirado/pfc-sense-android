package com.celulabs.pfcsense.controller;

import com.celulabs.pfcsense.model.SensorData;

/**
 * Controlador de tratamiento de información de sensores de temperatura
 *
 * @author gonztirado
 */
public class SensorDataController {

    private final static long TEMPERATURE_DATA_ELAPSE = 1000 * 30; // 30"
    private long _lastTemperatureTimestamp;

    private static SensorDataController ourInstance = new SensorDataController();

    public static SensorDataController getInstance() {
        return ourInstance;
    }

    private SensorDataController() {
    }

    public void addSensorValue(double temperatureValue) {
        long currentTime = System.currentTimeMillis();

        if((currentTime - _lastTemperatureTimestamp) > TEMPERATURE_DATA_ELAPSE) {
            SensorData sensorData = new SensorData();
            sensorData.setSensorId("bqsense_1");
            sensorData.setTimestamp((int) (currentTime / 1000));
            sensorData.setTemperature(temperatureValue);
            sensorData.setLatitude(36.694726);
            sensorData.setLongitude(-4.453308);
            sensorData.save();

            _lastTemperatureTimestamp = currentTime;
        }
    }
}
