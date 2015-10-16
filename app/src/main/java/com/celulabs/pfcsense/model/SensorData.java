package com.celulabs.pfcsense.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

/**
 * Modelo de los datos recopilados por los diferentes sensores de temperatura
 *
 * @author gonztirado
 */
@ParseClassName("SensorData")
public class SensorData extends ParseObject {

    private static final String PROPERTY_DEVICE_ID = "deviceId";
    private static final String PROPERTY_SENSOR_ID = "sensorId";
    private static final String PROPERTY_VALUE = "value";
    private static final String PROPERTY_TIMESTAMP = "timestamp";
    private static final String PROPERTY_TIMESTAMP_DATE = "timestampDate";

    public String getDeviceId() {
        return getString(PROPERTY_DEVICE_ID);
    }

    public void setDeviceId(String deviceId) {
        put(PROPERTY_DEVICE_ID, deviceId);
    }

    public String getSensorId() {
        return getString(PROPERTY_SENSOR_ID);
    }

    public void setSensorId(String sensorId) {
        put(PROPERTY_SENSOR_ID, sensorId);
    }

    public double getValue() {
        return getDouble(PROPERTY_VALUE);
    }

    public void setValue(double temperature) {
        put(PROPERTY_VALUE, temperature);
    }

    public long getTimestamp() {
        return getLong(PROPERTY_TIMESTAMP);
    }

    public Date getDatetime() {
        return getDate(PROPERTY_TIMESTAMP_DATE);
    }

    public void setTimestamp(long timestamp) {
        put(PROPERTY_TIMESTAMP, timestamp);
        put(PROPERTY_TIMESTAMP_DATE, new Date(timestamp));
    }

    public void setTimestampDate(Date timestampDate) {
        put(PROPERTY_TIMESTAMP_DATE, timestampDate);
        put(PROPERTY_TIMESTAMP, timestampDate != null ? timestampDate.getTime() : 0);
    }
}
