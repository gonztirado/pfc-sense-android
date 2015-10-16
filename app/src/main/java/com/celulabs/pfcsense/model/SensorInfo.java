package com.celulabs.pfcsense.model;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

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
    static final String PROPERTY_NUM_CONNECTIONS = "numConnections";

    /**
     * Crea o actualiza el objeto sensor info tomando como clave única 'deviceId + sensorId'
     */
    public void createOrUpdate() {
        ParseQuery<SensorInfo> query = ParseQuery.getQuery(SensorInfo.class);
        query.whereEqualTo(PROPERTY_DEVICE_ID, getDeviceId());
        query.whereEqualTo(PROPERTY_SENSOR_ID, getSensorId());
        query.findInBackground(new FindCallback<SensorInfo>() {
            @Override
            public void done(List<SensorInfo> results, ParseException e) {
                if (results != null && !results.isEmpty()) {
                    for (SensorInfo a : results) {
                        /* Incrementamos el nº de conexiones al sensor */
                        a.setNumConnections(a.getNumConnections() + 1);
                        a.saveInBackground();
                    }
                } else {
                    setNumConnections(1);
                    saveInBackground();
                }
            }
        });
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

    public int getNumConnections() {
        return getInt(PROPERTY_NUM_CONNECTIONS);
    }

    public void setNumConnections(int numConnections) {
        put(PROPERTY_NUM_CONNECTIONS, numConnections);
    }
}
