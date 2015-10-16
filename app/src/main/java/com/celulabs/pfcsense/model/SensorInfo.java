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

    String deviceId;
    String deviceModel;
    String sensorId;
    String sensorName;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }
}
