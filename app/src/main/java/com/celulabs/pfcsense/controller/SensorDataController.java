package com.celulabs.pfcsense.controller;

import com.celulabs.pfcsense.model.SensorData;
import com.celulabs.pfcsense.model.SensorInfo;
import com.celulabs.pfcsense.model.TemperatureData;

/**
 * Controlador de tratamiento de información de sensores de temperatura
 *
 * @author gonztirado
 */
public class SensorDataController {

    private static SensorDataController ourInstance = new SensorDataController();

    private SensorInfo currentSensorInfo;

    /**
     * @return devuelve la instancia del controlador
     */
    public static SensorDataController getInstance() {
        return ourInstance;
    }

    /**
     * Construtor
     */
    private SensorDataController() {
        setCurrentSensorInfo("", "");
    }

    /**
     * @return devuelve la información del sensor seleccionado
     */
    public SensorInfo getCurrentSensorInfo() {
        return currentSensorInfo;
    }

    /**
     * Setea la información del sensor seleccionado
     *
     * @param sensorId   identificador del sensor
     * @param sensorName nombre descriptivo del sensor
     */
    public void setCurrentSensorInfo(String sensorId, String sensorName) {
        SensorInfo sensorInfo = new SensorInfo();
        sensorInfo.setDeviceId("1234");
        sensorInfo.setDeviceModel("bq_test");
        sensorInfo.setSensorId(sensorId);
        sensorInfo.setSensorName(sensorName);
        this.currentSensorInfo = sensorInfo;
    }

    public void addTemperature(double value) {
        addSensorValue(new TemperatureData(), value);
    }

    /**
     * Añade un dato de sensor
     *
     * @param sensorData objeto con el tipo de dato del sensor
     * @param value      valor del sensor
     */
    private void addSensorValue(SensorData sensorData, double value) {
        sensorData.setSensorInfo(getCurrentSensorInfo());
        sensorData.setValue(value);
        sensorData.setTimestamp(System.currentTimeMillis());
        sensorData.saveInBackground();
    }
}
