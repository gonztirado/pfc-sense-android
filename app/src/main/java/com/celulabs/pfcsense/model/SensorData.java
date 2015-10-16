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
    private static final String PROPERTY_DEVICE_MODEL = "deviceModel";
    private static final String PROPERTY_SENSOR_ID = "sensorId";
    private static final String PROPERTY_SENSOR_NAME = "sensorName";
    private static final String PROPERTY_VALUE = "value";
    private static final String PROPERTY_TIMESTAMP = "timestamp";
    private static final String PROPERTY_TIMESTAMP_DATE = "timestampDate";

    public void setSensorInfo(SensorInfo sensorInfo) {
        if (sensorInfo != null) {
            put(PROPERTY_DEVICE_ID, sensorInfo.getDeviceId());
            put(PROPERTY_DEVICE_MODEL, sensorInfo.getDeviceModel());
            put(PROPERTY_SENSOR_ID, sensorInfo.getSensorId());
            put(PROPERTY_SENSOR_NAME, sensorInfo.getSensorName());
        }
    }

    public SensorInfo getSensorInfo() {
        SensorInfo sensorInfo = new SensorInfo();
        sensorInfo.setDeviceId(getString(PROPERTY_DEVICE_ID));
        sensorInfo.setDeviceModel(getString(PROPERTY_DEVICE_MODEL));
        sensorInfo.setSensorId(getString(PROPERTY_SENSOR_ID));
        sensorInfo.setSensorName(getString(PROPERTY_SENSOR_NAME));

        return sensorInfo;
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
