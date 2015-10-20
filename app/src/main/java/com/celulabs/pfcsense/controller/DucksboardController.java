package com.celulabs.pfcsense.controller;

import com.celulabs.pfcsense.model.DucksboardSettings;
import com.celulabs.pfcsense.model.SensorData;
import com.celulabs.pfcsense.model.SensorInfo;
import com.celulabs.pfcsense.model.TemperatureData;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Controlador de envío de datos al dashboard de Ducksboard
 *
 * @author gonztirado
 */
public class DucksboardController {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String JSON_PARAM_TIMESTAMP = "timestamp";
    private static final String JSON_PARAM_VALUE = "value";
    private static final String API_PUSH_URL = "https://push.ducksboard.com/v/";

    private DucksboardSettings _settings;


    private static DucksboardController ourInstance = new DucksboardController();

    /**
     * @return devuelve la instancia del controlador
     */
    public static DucksboardController getInstance() {
        return ourInstance;
    }

    /**
     * Constructor
     */
    private DucksboardController() {
    }

    public void initDuckboardSettings(final SensorInfo sensorInfo) {
        ParseQuery<DucksboardSettings> query = ParseQuery.getQuery(DucksboardSettings.class);
        query.whereEqualTo(DucksboardSettings.PROPERTY_SENSOR_ID, sensorInfo.getSensorId());
        query.findInBackground(new FindCallback<DucksboardSettings>() {
            @Override
            public void done(List<DucksboardSettings> results, ParseException e) {
                if (results != null && !results.isEmpty()) {
                    _settings = results.get(0);
                } else {
                    _settings = new DucksboardSettings();
                    _settings.setSensorId(sensorInfo.getSensorId());
                    _settings.setSensorName(sensorInfo.getSensorName());
                    _settings.setDefaultValues();
                    _settings.saveInBackground();
                }
            }
        });
    }

    /**
     * Envía al dashboard un dato de temperatura ambiental
     *
     * @param sensorData
     */
    public void pushTemperatureData(TemperatureData sensorData) {
        if (_settings != null) {
            pushData(sensorData, _settings.getValueTemperatureWidgetID());
            pushData(sensorData, _settings.getGraphicTemperatureWidgetID());
        }
    }

    /**
     * Envía al dashboard un dato de temperatura por infrarojos
     *
     * @param sensorData
     */
    public void pushTemperatureIRData(SensorData sensorData) {
        if (_settings != null) {
            pushData(sensorData, _settings.getValueTemperatureIRWidgetID());
            pushData(sensorData, _settings.getGraphicTemperatureIRWidgetID());
        }
    }

    /**
     * Envía al dashboard un dato de humedad
     *
     * @param sensorData
     */
    public void pushHumidityData(SensorData sensorData) {
        if (_settings != null) {
            pushData(sensorData, _settings.getValueHumidityWidgetID());
            pushData(sensorData, _settings.getGraphicHumidityWidgetID());
        }
    }

    /**
     * Envía al dashboard un dato de presión atmosférica
     *
     * @param sensorData
     */
    public void pushBarometerData(SensorData sensorData) {
        if (_settings != null) {
            pushData(sensorData, _settings.getValueBarometerWidgetID());
            pushData(sensorData, _settings.getGraphicBarometerWidgetID());
        }
    }

    /**
     * Envía al dashboard un dato de luminosidad ambiental
     *
     * @param sensorData
     */
    public void pushLuxometerData(SensorData sensorData) {
        if (_settings != null) {
            pushData(sensorData, _settings.getValueLuxometerWidgetID());
            pushData(sensorData, _settings.getGraphicLuxometerWidgetID());
        }
    }


    /**
     * Envía al dashboard un dato de sensor
     *
     * @param sensorData
     */
    private void pushData(SensorData sensorData, String widgetID) {
        if ((sensorData != null) && (widgetID != null) && !widgetID.isEmpty()) {
            JSONObject jsonObject = getJsonObject(sensorData);
            pushDashboardRequest(widgetID, jsonObject.toString());
        }
    }

    private JSONObject getJsonObject(SensorData sensorData) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JSON_PARAM_VALUE, sensorData.getValue());
            jsonObject.put(JSON_PARAM_TIMESTAMP, sensorData.getTimestamp() / 1000);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    /**
     * Realiza una petición de envío de datos al dashboard
     *
     * @param widgetID
     * @param jsonRequest
     */
    private void pushDashboardRequest(final String widgetID, final String jsonRequest) {

        if ((_settings != null) && (_settings.getApiKey() != null) && !_settings.getApiKey().isEmpty()) {
            final String apiKey = _settings.getApiKey();

            Thread sendThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String url = API_PUSH_URL + widgetID;
                        OkHttpClient httpClient = new OkHttpClient();
                        RequestBody body = RequestBody.create(JSON, jsonRequest);
                        Request request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .header("Authorization", "Basic " + apiKey)
                                .build();

                        httpClient.newCall(request).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            sendThread.start();
        }
    }
}
