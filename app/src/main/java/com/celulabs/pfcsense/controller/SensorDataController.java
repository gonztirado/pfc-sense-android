package com.celulabs.pfcsense.controller;

import com.celulabs.pfcsense.model.BarometerData;
import com.celulabs.pfcsense.model.DeviceInfo;
import com.celulabs.pfcsense.model.HumidityData;
import com.celulabs.pfcsense.model.LuxometerData;
import com.celulabs.pfcsense.model.SensorData;
import com.celulabs.pfcsense.model.SensorInfo;
import com.celulabs.pfcsense.model.TemperatureData;
import com.celulabs.pfcsense.model.TemperatureIRData;

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
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceId(deviceId);
        deviceInfo.setDeviceModel(deviceModel);
        deviceInfo.createOrUpdate();

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
        SensorInfo sensorInfo = new SensorInfo();
        sensorInfo.setDeviceId(currentSensorInfo.getDeviceId());
        sensorInfo.setDeviceModel(currentSensorInfo.getDeviceModel());
        sensorInfo.setSensorId(sensorId);
        sensorInfo.setSensorName(sensorName);

        /* Guardamos el sensor contectado */
        currentSensorInfo = sensorInfo;
        currentSensorInfo.createOrUpdate();

        /* Inicializamos configuración de sensor en Ducksboar */
        DucksboardController.getInstance().initDuckboardSettings(currentSensorInfo);
    }

    /**
     * Añade un nuevo valor de temperatura
     *
     * @param value
     */
    public void addTemperatureValue(double value) {
        TemperatureData data = new TemperatureData();
        addSensorValue(data, value);
        DucksboardController.getInstance().pushTemperatureData(data);
    }

    /**
     * Añade un nuevo valor de temperatura por infrarojos
     *
     * @param value
     */
    public void addTemperatureIRValue(double value) {
        TemperatureIRData data = new TemperatureIRData();
        addSensorValue(data, value);
        DucksboardController.getInstance().pushTemperatureIRData(data);
    }

    /**
     * Añade un nuevo valor de presión barométrica
     *
     * @param value
     */
    public void addBarometerValue(double value) {
        BarometerData data = new BarometerData();
        addSensorValue(data, value);
        DucksboardController.getInstance().pushBarometerData(data);
    }

    /**
     * Añade un nuevo valor de humedad ambiental
     *
     * @param value
     */
    public void addHumidityValue(double value) {
        HumidityData data = new HumidityData();
        addSensorValue(data, value);
        DucksboardController.getInstance().pushHumidityData(data);
    }

    /**
     * Añade un nuevo valor de presión barométrica
     *
     * @param value
     */
    public void addLuxometerValue(double value) {
        LuxometerData data = new LuxometerData();
        addSensorValue(data, value);
        DucksboardController.getInstance().pushLuxometerData(data);
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
