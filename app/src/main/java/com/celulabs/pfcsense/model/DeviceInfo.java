package com.celulabs.pfcsense.model;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Modelo con la información del terminal
 *
 * @author gonztirado
 */
@ParseClassName("DeviceInfo")
public class DeviceInfo extends ParseObject {

    static final String PROPERTY_DEVICE_ID = "deviceId";
    static final String PROPERTY_DEVICE_MODEL = "deviceModel";
    static final String PROPERTY_NUM_STARTUP = "numStartup";

    /**
     * Crea o actualiza el objeto sensor info tomando como clave única 'deviceId + sensorId'
     */
    public void createOrUpdate() {
        ParseQuery<DeviceInfo> query = ParseQuery.getQuery(DeviceInfo.class);
        query.whereEqualTo(PROPERTY_DEVICE_ID, getDeviceId());
        query.findInBackground(new FindCallback<DeviceInfo>() {
            @Override
            public void done(List<DeviceInfo> results, ParseException e) {
                if (results != null && !results.isEmpty()) {
                    for (DeviceInfo a : results) {
                        /* Incrementamos el nº de conexiones al sensor */
                        a.setNumStartup(a.getNumStartup() + 1);
                        a.saveInBackground();
                    }
                } else {
                    setNumStartup(1);
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

    public int getNumStartup() {
        return getInt(PROPERTY_NUM_STARTUP);
    }

    public void setNumStartup(int numStartup) {
        put(PROPERTY_NUM_STARTUP, numStartup);
    }
}
