package com.celulabs.pfcsense.model;

import com.orm.SugarRecord;

/**
 * Modelo con la información de configuración de un sensor
 *
 * @author gonztirado
 */
public class SensorInfo extends SugarRecord<SensorInfo> {
    String sensorId;
    float maxTemperature;
    float minTemperature;

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public float getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(float maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public float getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(float minTemperature) {
        this.minTemperature = minTemperature;
    }
}
