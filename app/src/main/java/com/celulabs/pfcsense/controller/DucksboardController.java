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

    private long _lastPushTemperatureValue;
    private long _lastPushTemperatureIRValue;
    private long _lastPushHumidityValue;
    private long _lastPushBarometerValue;
    private long _lastPushLuxometerValue;
    private long _lastPushTemperatureGraphic;
    private long _lastPushTemperatureIRGraphic;
    private long _lastPushHumidityGraphic;
    private long _lastPushBarometerGraphic;
    private long _lastPushLuxometerGraphic;


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
            _lastPushTemperatureValue = pushData(sensorData, _settings.getValueTemperatureWidgetID(), _lastPushTemperatureValue, _settings.getValuePushInterval());
            _lastPushTemperatureGraphic = pushData(sensorData, _settings.getGraphicTemperatureWidgetID(), _lastPushTemperatureGraphic, _settings.getGraphicPushInterval());
        }
    }

    /**
     * Envía al dashboard un dato de temperatura por infrarojos
     *
     * @param sensorData
     */
    public void pushTemperatureIRData(SensorData sensorData) {
        if (_settings != null) {
            _lastPushTemperatureIRValue = pushData(sensorData, _settings.getValueTemperatureIRWidgetID(), _lastPushTemperatureIRValue, _settings.getValuePushInterval());
            _lastPushTemperatureIRGraphic = pushData(sensorData, _settings.getGraphicTemperatureIRWidgetID(), _lastPushTemperatureIRGraphic, _settings.getGraphicPushInterval());
        }
    }

    /**
     * Envía al dashboard un dato de humedad
     *
     * @param sensorData
     */
    public void pushHumidityData(SensorData sensorData) {
        if (_settings != null) {
            _lastPushHumidityValue = pushData(sensorData, _settings.getValueHumidityWidgetID(), _lastPushHumidityValue, _settings.getValuePushInterval());
            _lastPushHumidityGraphic = pushData(sensorData, _settings.getGraphicHumidityWidgetID(), _lastPushHumidityGraphic, _settings.getGraphicPushInterval());
        }
    }

    /**
     * Envía al dashboard un dato de presión atmosférica
     *
     * @param sensorData
     */
    public void pushBarometerData(SensorData sensorData) {
        if (_settings != null) {
            _lastPushBarometerValue = pushData(sensorData, _settings.getValueBarometerWidgetID(), _lastPushBarometerValue, _settings.getValuePushInterval());
            _lastPushBarometerGraphic = pushData(sensorData, _settings.getGraphicBarometerWidgetID(), _lastPushBarometerGraphic, _settings.getGraphicPushInterval());
        }
    }

    /**
     * Envía al dashboard un dato de luminosidad ambiental
     *
     * @param sensorData
     */
    public void pushLuxometerData(SensorData sensorData) {
        if (_settings != null) {
            _lastPushLuxometerValue = pushData(sensorData, _settings.getValueLuxometerWidgetID(), _lastPushLuxometerValue, _settings.getValuePushInterval());
            _lastPushLuxometerGraphic = pushData(sensorData, _settings.getGraphicLuxometerWidgetID(), _lastPushLuxometerGraphic, _settings.getGraphicPushInterval());
        }
    }


    /**
     * Intenta un envío push de datos al dashboard un dato de sensor
     *
     * @param sensorData
     * @param widgetID
     * @param lastPushTimestamp
     * @param pushInterval
     * @return devuelve la fecha en que se ha realizado la ultima petición push para esta gráfica
     */
    private long pushData(SensorData sensorData, String widgetID, long lastPushTimestamp, long pushInterval) {
        if ((sensorData != null) && (widgetID != null) && !widgetID.isEmpty()) {
            JSONObject jsonObject = getJsonObject(sensorData);
            return pushDashboardRequest(widgetID, jsonObject.toString(), lastPushTimestamp, pushInterval);
        } else {
            return lastPushTimestamp;
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
     * @param lastPushTimestamp
     * @param pushInterval
     */
    private long pushDashboardRequest(final String widgetID, final String jsonRequest, long lastPushTimestamp, long pushInterval) {
        long currentTime = System.currentTimeMillis();
        long nextPutAllowed = lastPushTimestamp + pushInterval;

        if ((currentTime >= nextPutAllowed) && (_settings != null) && (_settings.getApiKey() != null) && !_settings.getApiKey().isEmpty()) {
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
            return currentTime;
        } else {
            return lastPushTimestamp;
        }
    }
}
