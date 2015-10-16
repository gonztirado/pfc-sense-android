package com.celulabs.pfcsense.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Modelo con la información del sensor
 *
 * @author gonztirado
 */
@ParseClassName("SensorInfo")
public class SensorInfo extends ParseObject {

    static final String PROPERTY_DEVICE_ID = "deviceId";
    static final String PROPERTY_DEVICE_MODEL = "deviceModel";
    static final String PROPERTY_SENSOR_ID = "sensorId";
    static final String PROPERTY_SENSOR_NAME = "sensorName";

    public SensorInfo() {
        setDeviceId("");
        setDeviceModel("");
        setSensorId("");
        setSensorName("");
    }

    public String getDeviceId() {
        return getString(PROPERTY_DEVICE_ID);
    }

    public void setDeviceId(String deviceId) {
        put(PROPERTY_DEVICE_ID, deviceId);
    }

    public String getDeviceModel() {
        return getString(PROPERTY_DEVICE_MODEL);
    }

    public void setDeviceModel(String deviceModel) {
        put(PROPERTY_DEVICE_MODEL, deviceModel);
    }

    public String getSensorId() {
        return getString(PROPERTY_SENSOR_ID);
    }

    public void setSensorId(String sensorId) {
        put(PROPERTY_SENSOR_ID, sensorId);
    }

    public String getSensorName() {
        return getString(PROPERTY_SENSOR_NAME);
    }

    public void setSensorName(String sensorName) {
        put(PROPERTY_SENSOR_NAME, sensorName);
    }
}
