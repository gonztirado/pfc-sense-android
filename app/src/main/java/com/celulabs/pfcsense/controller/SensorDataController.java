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
        currentSensorInfo = new SensorInfo();
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
     * @param deviceId    identificador del terminal
     * @param deviceModel nombre del modelo del terminal
     */
    public void setCurrenDeviceInfo(String deviceId, String deviceModel) {
        currentSensorInfo.setDeviceId(deviceId);
        currentSensorInfo.setDeviceModel(deviceModel);
    }

    /**
     * Setea la información del sensor seleccionado
     *
     * @param sensorId   identificador del sensor
     * @param sensorName nombre descriptivo del sensor
     */
    public void setCurrentSensorInfo(String sensorId, String sensorName) {
        if (currentSensorInfo.getSensorId() != sensorId) {
            SensorInfo sensorInfo = new SensorInfo();
            sensorInfo.setDeviceId(currentSensorInfo.getDeviceId());
            sensorInfo.setDeviceModel(currentSensorInfo.getDeviceModel());
            sensorInfo.setSensorId(sensorId);
            sensorInfo.setSensorName(sensorName);

            /* Guardamos el sensor contectado */
            currentSensorInfo = sensorInfo;
            currentSensorInfo.saveInBackground();
        }
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
